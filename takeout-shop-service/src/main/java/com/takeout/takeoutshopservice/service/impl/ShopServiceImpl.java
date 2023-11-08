package com.takeout.takeoutshopservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.takeoutcommon.common.ErrorCode;
import com.takeout.takeoutcommon.common.PageRequest;
import com.takeout.takeoutcommon.constant.UserConstant;
import com.takeout.takeoutcommon.exception.BusinessException;
import com.takeout.takeoutmodel.dto.UserDto;
import com.takeout.takeoutmodel.entity.AddressInfo;
import com.takeout.takeoutmodel.entity.Shop;
import com.takeout.takeoutmodel.entity.User;
import com.takeout.takeoutmodel.enums.UserRoleEnum;
import com.takeout.takeoutmodel.request.AddShopRequest;
import com.takeout.takeoutmodel.vo.ShopVO;
import com.takeout.takeoutserviceclient.service.UserFeignClient;
import com.takeout.takeoutshopservice.mapper.ShopMapper;
import com.takeout.takeoutshopservice.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 13123
 * @description 针对表【shop(店铺表)】的数据库操作Service实现
 * @createDate 2023-11-06 11:32:22
 */
@Service
@Slf4j
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop>
        implements ShopService {

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private ShopMapper shopMapper;

    @Transactional
    @Override
    public Shop addShop(AddShopRequest addShopRequest, HttpServletRequest request) {
        if (addShopRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String shopName = addShopRequest.getShopName();
        String tag = addShopRequest.getTag();
        String picture = addShopRequest.getPicture();

        if (StringUtils.isAnyBlank(shopName, tag)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userFeignClient.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        // 添加店铺，并且获取到所添加的店铺的ID
        QueryWrapper<Shop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("shopName", shopName);
        Shop shop = shopMapper.selectOne(queryWrapper);
        if (shop != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "店铺名已被注册");
        }
        shop = new Shop();
        shop.setTag(tag);
        shop.setShopName(shopName);
        shop.setPicture(picture);
        shop.setUserId(loginUser.getId());
        shopMapper.insert(shop);
        System.out.println(shop.getId());

        int userRole = loginUser.getUserRole();
        // 如果用户是普通用户，那么需要更改用户角色，发起远程调用，如果是店主，则拒绝访问，这个接口设计成成为店主的时候就发一个添加 shop 的请求
        if (userRole == UserRoleEnum.USER.getValue()) {
            UserDto userDto = new UserDto();
            loginUser.setShopId(shop.getId());
            userDto.setUser(loginUser);
            userDto.setUserRoleEnum(UserRoleEnum.SHOPKEEPER);
            System.out.println(userDto);
            userFeignClient.changeRole(userDto);
        }

        // 更改 session 中的用户信息
        User newUser = userFeignClient.getUserById(loginUser.getId());
        User safetyUser = userFeignClient.getSafetyUser(newUser);
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, safetyUser);

        return shopMapper.selectById(shop.getId());
    }

    @Override
    public void test() {
        log.info("测试");
        userFeignClient.test();
    }

    @Override
    public Page<Shop> searchShopByName(String searchShopName, PageRequest pageRequest) {
        if (StrUtil.isBlank(searchShopName) || pageRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "搜索参数不能为空");
        }
        QueryWrapper<Shop> queryWrapper = new QueryWrapper<>();
        // 只能查询已经审核完毕的店铺
        queryWrapper.eq("shopStatus", 0);
        queryWrapper.like("shopName", searchShopName);
        long current = pageRequest.getCurrent();
        long pageSize = pageRequest.getPageSize();
        return shopMapper.selectPage(new Page<>(current, pageSize), queryWrapper);
    }

    @Override
    public Page<Shop> searchShopByTag(Integer tag, PageRequest pageRequest) {
        if (Objects.isNull(tag) || pageRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签不能为空");
        }
        QueryWrapper<Shop> queryWrapper = new QueryWrapper<>();
        // 只能查询已经审核完毕的店铺
        queryWrapper.eq("shopStatus", 0);
        List<Shop> shopList = shopMapper.selectList(queryWrapper);
        // 这里判断 tag 所对应的 index 位置的值是否为 1，如果为 1，则说明该店铺符合该标签
        shopList = shopList.stream().filter(shop -> shop.getTag().charAt(tag) == '1').collect(Collectors.toList());

        long current = pageRequest.getCurrent();
        long pageSize = pageRequest.getPageSize();

        // 如果一页的大小已经大于 shopList 的大小了
        if(pageSize > shopList.size()){
            pageSize = shopList.size();
        }

        List<Shop> pageList = shopList.subList((int) ((current - 1) * pageSize), (int) (Math.min((int) current * pageSize, shopList.size())));
        if(pageList.isEmpty()){
            return new Page<>(current, pageSize, shopList.size());
        }
        Page<Shop> shopPage = new Page<>(current, pageSize, shopList.size());
        shopPage.setRecords(pageList);
        return shopPage;
    }

    @Override
    public AddressInfo getAddress(Long userId) {
        if (Objects.isNull(userId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AddressInfo addressInfo = userFeignClient.getAddressByUserId(userId);
        if (addressInfo == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return addressInfo;
    }

    @Override
    public Page<Shop> getShopToBeAudited(PageRequest pageRequest) {
        if(pageRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Shop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("shopStatus", 1);
        long current = pageRequest.getCurrent();
        long pageSize = pageRequest.getPageSize();
        return shopMapper.selectPage(new Page<>(current, pageSize), queryWrapper);
    }

    @Override
    public int auditShop(Long id, Boolean isPass) {
        if(Objects.isNull(id) || Objects.isNull(isPass)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Shop shop = shopMapper.selectById(id);
        if(shop == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "该店铺不存在");
        }
        // 如果审核通过，则更改状态，否则逻辑删除该店铺
        if(isPass){
            if(shop.getShopStatus() == 0){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "该店铺已审核通过");
            } else {
                // 设置店铺状态为可用
                shop.setShopStatus(0);
                return shopMapper.updateById(shop);
            }
        } else {
            // 查询店主
            Long shopUserId = shop.getUserId();
            User shopUser = userFeignClient.getUserById(shopUserId);
            // 审核没通过，将用户的角色改为普通用户
            UserDto userDto = new UserDto();
            userDto.setUser(shopUser);
            userDto.setUserRoleEnum(UserRoleEnum.USER);
            userFeignClient.changeRole(userDto);
            return shopMapper.deleteById(shop);
        }
    }

    @Override
    public Page<Shop> getHistoryShops(PageRequest pageRequest) {
        if(pageRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = pageRequest.getCurrent();
        long pageSize = pageRequest.getPageSize();
        List<Shop> shopList = shopMapper.getHistoryShops();
        if(pageSize > shopList.size()){
            pageSize = shopList.size();
        }

        List<Shop> pageList = shopList.subList((int) ((current - 1) * pageSize), (int) (Math.min((int) current * pageSize, shopList.size())));
        if(pageList.isEmpty()){
            return new Page<>(current, pageSize, shopList.size());
        }
        Page<Shop> shopPage = new Page<>(current, pageSize, shopList.size());
        shopPage.setRecords(pageList);
        return shopPage;
    }

    @Override
    public Page<ShopVO> getShopPageVO(Page<Shop> shopPage) {
        List<Shop> shopList = shopPage.getRecords();
        Page<ShopVO> shopVOPage = new Page<>(shopPage.getCurrent(), shopPage.getSize(), shopPage.getTotal());
        List<ShopVO> shopVOList = shopList.stream().map(this::getShopVO).collect(Collectors.toList());
        shopVOPage.setRecords(shopVOList);
        return shopVOPage;
    }

    private ShopVO getShopVO(Shop originShop) {
        if (originShop == null) {
            return null;
        }
        ShopVO shopVO = new ShopVO();
        BeanUtil.copyProperties(originShop, shopVO);
        return shopVO;
    }

}





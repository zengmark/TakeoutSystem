package com.takeout.takeoutshopservice.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.takeoutcommon.common.ErrorCode;
import com.takeout.takeoutcommon.constant.UserConstant;
import com.takeout.takeoutcommon.exception.BusinessException;
import com.takeout.takeoutmodel.dto.UserDto;
import com.takeout.takeoutmodel.entity.AddressInfo;
import com.takeout.takeoutmodel.entity.Shop;
import com.takeout.takeoutmodel.entity.User;
import com.takeout.takeoutmodel.enums.UserRoleEnum;
import com.takeout.takeoutmodel.request.AddShopRequest;
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
        if(shop != null){
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
        if(userRole == UserRoleEnum.USER.getValue()){
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
    public List<Shop> searchShopByName(String searchShopName) {
        if(StrUtil.isBlank(searchShopName)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "搜索参数不能为空");
        }
        QueryWrapper<Shop> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("shopName", searchShopName);
        List<Shop> shopList = shopMapper.selectList(queryWrapper);
        if(shopList == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return shopList;
    }

    @Override
    public List<Shop> searchShopByTag(Integer tag) {
        if(Objects.isNull(tag)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签不能为空");
        }
        QueryWrapper<Shop> queryWrapper = new QueryWrapper<>();
        List<Shop> shopList = shopMapper.selectList(queryWrapper);
        // 这里判断 tag 所对应的 index 位置的值是否为 1，如果为 1，则说明该店铺符合该标签
        return shopList.stream().filter(shop -> shop.getTag().charAt(tag) == '1').collect(Collectors.toList());
    }

    @Override
    public AddressInfo getAddress(Long userId) {
        if(Objects.isNull(userId)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AddressInfo addressInfo = userFeignClient.getAddressByUserId(userId);
        if(addressInfo == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return addressInfo;
    }
}





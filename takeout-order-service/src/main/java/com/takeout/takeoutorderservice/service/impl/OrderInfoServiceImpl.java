package com.takeout.takeoutorderservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.takeoutcommon.common.ErrorCode;
import com.takeout.takeoutcommon.exception.BusinessException;
import com.takeout.takeoutmodel.entity.*;
import com.takeout.takeoutmodel.request.AddOrderRequest;
import com.takeout.takeoutmodel.vo.AddressInfoVO;
import com.takeout.takeoutmodel.vo.MenuVO;
import com.takeout.takeoutmodel.vo.OrderInfoVO;
import com.takeout.takeoutorderservice.mapper.OrderInfoMapper;
import com.takeout.takeoutorderservice.service.OrderInfoService;
import com.takeout.takeoutserviceclient.service.MenuFeignClient;
import com.takeout.takeoutserviceclient.service.ShopFeignClient;
import com.takeout.takeoutserviceclient.service.UserFeignClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author 13123
 * @description 针对表【order_info(订单表)】的数据库操作Service实现
 * @createDate 2023-11-07 12:36:08
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo>
        implements OrderInfoService {

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private MenuFeignClient menuFeignClient;

    @Resource
    private ShopFeignClient shopFeignClient;

    @Resource
    private OrderInfoMapper orderInfoMapper;

    @Transactional
    @Override
    public int createOrder(AddOrderRequest addOrderRequest, HttpServletRequest request) {
        if (addOrderRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);

        Long userId = addOrderRequest.getUserId();
        Long shopUserId = addOrderRequest.getShopUserId();
        Integer price = addOrderRequest.getPrice();

        if (!Objects.equals(userId, loginUser.getId())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 判断用户是否拥有足够的余额去下单
        if (loginUser.getBalance() < price) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "抱歉，您的余额不足");
        }

        // 生成订单对象并将其添加到数据库中
        OrderInfo orderInfo = new OrderInfo();
        BeanUtil.copyProperties(addOrderRequest, orderInfo);
        int insert = orderInfoMapper.insert(orderInfo);

        // 扣减用户余额，并且添加店主余额
        return userFeignClient.updateBalance(userId, shopUserId, price);
    }

    @Override
    public List<OrderInfoVO> listUserOrders(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userFeignClient.getLoginUser(request);
        // 先将该用户下过的所有订单拿到
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        List<OrderInfo> orderInfoList = orderInfoMapper.selectList(queryWrapper);

        // 返回空数组
        if (orderInfoList == null || orderInfoList.isEmpty()) {
            return new ArrayList<>();
        }

        return getOrderInfoVOList(orderInfoList);
    }

    @Override
    public List<OrderInfoVO> listShopOrders(Long shopId) {
        if(Objects.isNull(shopId)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<OrderInfo> orderInfoList = orderInfoMapper.listShopOrders(shopId);
        return getOrderInfoVOList(orderInfoList);
    }

    @Override
    public int finishOrder(Long id) {
        if(Objects.isNull(id)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断该订单的状态是否已完成
        OrderInfo orderInfo = orderInfoMapper.selectById(id);
        if(orderInfo.getStatus() == 1){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "该订单已完成");
        }
        orderInfo.setStatus(1);
        return orderInfoMapper.updateById(orderInfo);
    }

    @Override
    public List<OrderInfoVO> getShoppingCart(HttpServletRequest request) {
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId()).eq("status", 2);
        List<OrderInfo> orderInfoList = orderInfoMapper.selectList(queryWrapper);
        return getOrderInfoVOList(orderInfoList);
    }

    @Transactional
    @Override
    public int cancelOrder(Long id, HttpServletRequest request) {
        if(Objects.isNull(id)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断订单状态是否为已完成，已完成的订单不可取消
        OrderInfo orderInfo = orderInfoMapper.selectById(id);
        if(orderInfo.getStatus() == 1){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "该订单已完成");
        }

        User loginUser = userFeignClient.getLoginUser(request);
        Long userId = loginUser.getId();
        Long shopId = orderInfo.getShopId();
        Shop shop = shopFeignClient.getShopById(shopId);
        Long shopUserId = shop.getUserId();

        // 获取该订单的菜品信息
        List<Menu> menuList = getMenuList(orderInfo);

        int price = 0;
        for (Menu menu : menuList) {
            price += menu.getPrice();
        }

        orderInfo.setStatus(1);
        orderInfoMapper.updateById(orderInfo);

        // 扣减店主余额，增加用户余额
        return userFeignClient.updateBalance(shopUserId, userId, price);
    }

    private List<Menu> getMenuList(OrderInfo orderInfo) {
        // 获取该订单的 Menu 信息
        String menuStr = orderInfo.getMenuId();
        String[] menus = menuStr.split(",");
        List<Long> menuIdList = new ArrayList<>();
        for (String menuS : menus) {
            Long menuId = Long.parseLong(menuS);
            menuIdList.add(menuId);
        }
        // 直接通过 menuFeignClient 发送请求获取到该订单的所有的 Menu 对象
        List<Menu> menuList = menuFeignClient.getOrderMenus(menuIdList);
        return menuList;
    }

    /**
     * 获取 OrderInfoVOList 对象
     * @param orderInfoList
     * @return
     */
    @NotNull
    private List<OrderInfoVO> getOrderInfoVOList(List<OrderInfo> orderInfoList) {
        List<OrderInfoVO> orderInfoVOList = new ArrayList<>();
        // 遍历所有的订单
        for (OrderInfo orderInfo : orderInfoList) {
            OrderInfoVO orderInfoVO = new OrderInfoVO();
            BeanUtil.copyProperties(orderInfo, orderInfoVO);

            // 获取该订单的 Menu 信息
            List<Menu> menuList = getMenuList(orderInfo);
            List<MenuVO> menuVOList = menuFeignClient.getMenuVOList(menuList);
            orderInfoVO.setMenuVOList(menuVOList);

            // 计算总价
            int price = 0;
            for (MenuVO menuVO : menuVOList) {
                price += menuVO.getPrice();
            }

            orderInfoVO.setPrice(price);

            // 查询该订单的用户地址
            AddressInfo userAddressInfo = userFeignClient.getAddressByAddressId(orderInfo.getAddressInfoId());
            AddressInfoVO userAddressInfoVO = userFeignClient.getAddressInfoVO(userAddressInfo);
            // 查询该订单的店铺地址信息
            Shop shop = shopFeignClient.getShopById(orderInfo.getShopId());
            Long shopUserId = shop.getUserId();
            AddressInfo shopUserAddressInfo = userFeignClient.getAddressByUserId(shopUserId);
            AddressInfoVO shopUserAddressInfoVO = userFeignClient.getAddressInfoVO(shopUserAddressInfo);

            orderInfoVO.setUserAddressInfo(userAddressInfoVO);
            orderInfoVO.setShopUserAddressInfo(shopUserAddressInfoVO);

            orderInfoVOList.add(orderInfoVO);
        }
        return orderInfoVOList;
    }

}

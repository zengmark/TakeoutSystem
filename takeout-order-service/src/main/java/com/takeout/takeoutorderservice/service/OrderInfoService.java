package com.takeout.takeoutorderservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.takeoutcommon.common.PageRequest;
import com.takeout.takeoutmodel.entity.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.takeoutmodel.request.AddOrderRequest;
import com.takeout.takeoutmodel.vo.OrderInfoVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 13123
* @description 针对表【order_info(订单表)】的数据库操作Service
* @createDate 2023-11-07 12:36:08
*/
public interface OrderInfoService extends IService<OrderInfo> {

    int createOrder(AddOrderRequest addOrderRequest, HttpServletRequest request);

    Page<OrderInfoVO> listUserOrders(HttpServletRequest request, PageRequest pageRequest);

    Page<OrderInfoVO> listShopOrders(Long shopId, PageRequest pageRequest);

    int finishOrder(Long id);

    Page<OrderInfoVO> getShoppingCart(PageRequest pageRequest, HttpServletRequest request);

    int cancelOrder(Long id, HttpServletRequest request);

    int deleteShoppingCart(HttpServletRequest request);

    Page<OrderInfoVO> getOrderInfoVOPage(Page<OrderInfo> orderInfoPage);
}

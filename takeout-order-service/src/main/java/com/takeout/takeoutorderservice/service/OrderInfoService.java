package com.takeout.takeoutorderservice.service;

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

    List<OrderInfoVO> listUserOrders(HttpServletRequest request);

    List<OrderInfoVO> listShopOrders(Long shopId);

    int finishOrder(Long id);

    List<OrderInfoVO> getShoppingCart(HttpServletRequest request);

    int cancelOrder(Long id, HttpServletRequest request);
}

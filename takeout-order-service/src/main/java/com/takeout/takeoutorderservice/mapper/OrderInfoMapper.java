package com.takeout.takeoutorderservice.mapper;

import com.takeout.takeoutmodel.entity.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 13123
* @description 针对表【order_info(订单表)】的数据库操作Mapper
* @createDate 2023-11-07 12:36:08
* @Entity com.takeout.takeoutmodel.entity.OrderInfo
*/
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    List<OrderInfo> listShopOrders(@Param("shopId") Long shopId);
}





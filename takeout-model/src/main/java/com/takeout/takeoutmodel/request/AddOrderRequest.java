package com.takeout.takeoutmodel.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class AddOrderRequest implements Serializable {

    private static final long serialVersionUID = 238358547919863401L;

    /**
     * 下了该订单的用户ID
     */
    private Long userId;

    /**
     * 该店铺的店主的用户ID，其实不用这个也能通过店铺ID拿到店主的ID，这里是为了方便操作避免多次回表
     */
    private Long shopUserId;

    /**
     * 商家ID
     */
    private Long shopId;

    /**
     * 该订单包含的菜品ID数组，以 , 分割
     */
    private String menuId;

    /**
     * 下单用户的地址信息表ID
     */
    private Long addressInfoId;

    /**
     * 订单总金额，用于扣减用户的余额，这个前端进行计算即可
     */
    private Integer price;

}

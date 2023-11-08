package com.takeout.takeoutmodel.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class OrderInfoVO implements Serializable {

    private static final long serialVersionUID = -2910872731629951131L;

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 下了该订单的用户ID
     */
    private Long userId;

    /**
     * 商家ID，这个是店铺的ID，而不是店铺的店主的用户ID
     */
    private Long shopId;

    /**
     * 该订单包含的菜品数组
     */
    private List<MenuVO> menuVOList;

    /**
     * 这个价格需要通过菜品数组去计算
     */
    private Integer price;

    /**
     * 该订单用户的地址
     */
    private AddressInfoVO userAddressInfo;

    /**
     * 该订单店主的地址信息
     */
    private AddressInfoVO shopUserAddressInfo;

    /**
     * 订单状态，0 未完成，1 已完成
     */
    private Integer orderStatus;

    /**
     * 创建时间
     */
    private Date createTime;

}

package com.takeout.takeoutmodel.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 订单表
 * @TableName order_info
 */
@TableName(value ="order_info")
@Data
public class OrderInfo implements Serializable {
    /**
     * 订单ID
     */
    @TableId(type = IdType.AUTO)
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
     * 该订单包含的菜品ID数组，以 , 分割
     */
    private String menuId;

    /**
     * 地址信息表ID
     */
    private Long addressInfoId;

    /**
     * 订单状态，0 未完成，1 已完成
     */
    private Integer orderStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除，0 未删除，1 已删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 该订单的价格
     */
    private Integer price;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
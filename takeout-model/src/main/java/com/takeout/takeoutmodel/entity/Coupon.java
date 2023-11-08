package com.takeout.takeoutmodel.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 优惠券表
 * @TableName coupon
 */
@TableName(value ="coupon")
@Data
public class Coupon implements Serializable {
    /**
     * 优惠券ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建者ID
     */
    private Long adminId;

    /**
     * 持有优惠券的用户ID
     */
    private Long userId;

    /**
     * 绑定所使用了该优惠券的订单
     */
    private Long orderId;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 优惠券状态，0 可用，1 不可用
     */
    private Integer couponStatus;

    /**
     * 优惠券金额
     */
    private Integer amount;

    /**
     * 到期时间
     */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expirationTime;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
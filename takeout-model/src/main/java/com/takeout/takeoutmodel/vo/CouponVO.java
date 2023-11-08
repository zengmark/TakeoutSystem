package com.takeout.takeoutmodel.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class CouponVO implements Serializable {

    private static final long serialVersionUID = -4060354086401834684L;

    /**
     * 优惠券ID
     */
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

}

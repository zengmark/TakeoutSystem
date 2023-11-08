package com.takeout.takeoutmodel.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class AddCouponRequest implements Serializable {

    private static final long serialVersionUID = 4171257187603538138L;

    /**
     * 创建者ID
     */
    private Long adminId;

    /**
     * 优惠券名称
     */
    private String couponName;

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
     * 创建的优惠券数量
     */
    private Integer num;

}

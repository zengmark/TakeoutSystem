package com.takeout.takeoutmodel.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AddressInfoVO implements Serializable {
    private static final long serialVersionUID = -5593383251649475942L;

    /**
     * 地址信息表ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     * 收货人名称
     */
    private String consigneeName;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 标签，0 家，1 公司，2 学校
     */
    private Integer tag;

}

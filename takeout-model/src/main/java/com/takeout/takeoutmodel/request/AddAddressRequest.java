package com.takeout.takeoutmodel.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class AddAddressRequest implements Serializable {

    private static final long serialVersionUID = -2716865864759250322L;

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

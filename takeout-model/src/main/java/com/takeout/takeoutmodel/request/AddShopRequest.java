package com.takeout.takeoutmodel.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class AddShopRequest implements Serializable {
    private static final long serialVersionUID = 4167870371336516664L;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 店铺标签
     */
    private String tag;

    /**
     * 店铺图片
     */
    private String picture;

    /**
     * 店铺简介
     */
    private String shopDescription;
}

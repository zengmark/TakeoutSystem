package com.takeout.takeoutmodel.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class AddShopRequest implements Serializable {
    private static final long serialVersionUID = 4167870371336516664L;

    private String shopName;

    private String tag;

}

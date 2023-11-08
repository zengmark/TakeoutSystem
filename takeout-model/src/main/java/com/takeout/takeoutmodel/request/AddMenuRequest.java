package com.takeout.takeoutmodel.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class AddMenuRequest implements Serializable {
    private static final long serialVersionUID = 7382169086821570172L;

    private Long shopId;

    private String category;

    private String menuName;

    private String menuDescription;

    private Integer price;

    private String picture;
}

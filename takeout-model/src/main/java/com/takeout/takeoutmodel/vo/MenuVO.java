package com.takeout.takeoutmodel.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MenuVO implements Serializable {
    private static final long serialVersionUID = 2460566863349774311L;

    /**
     * 菜品ID
     */
    private Long id;

    /**
     * 菜品归属店铺ID
     */
    private Long shopId;

    /**
     * 菜品分类
     */
    private String category;

    /**
     * 菜品名称
     */
    private String menuName;

    /**
     * 菜品详细信息
     */
    private String menuDescription;

    /**
     * 菜品价格
     */
    private Integer price;

    /**
     * 菜品图片
     */
    private String picture;

}

package com.takeout.takeoutmodel.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 店铺表
 * @TableName shop
 */
@TableName(value ="shop")
@Data
public class Shop implements Serializable {
    /**
     * 店铺ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 店铺标签
     */
    private String tag;

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
     * 店铺名称，这里是通过 bitmap 来判断标签
     */
    private String shopName;

    /**
     * 店铺图片
     */
    private String picture;

    /**
     * 店主ID
     */
    private Long userId;

    /**
     * 店铺的状态
     */
    private Integer shopStatus;

    /**
     * 店铺简介
     */
    private String shopDescription;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
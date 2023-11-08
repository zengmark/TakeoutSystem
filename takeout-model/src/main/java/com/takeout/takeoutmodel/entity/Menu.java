package com.takeout.takeoutmodel.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 菜品表
 * @TableName menu
 */
@TableName(value ="menu")
@Data
public class Menu implements Serializable {
    /**
     * 菜品ID
     */
    @TableId(type = IdType.AUTO)
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
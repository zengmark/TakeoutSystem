package com.takeout.takeoutmodel.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ShopVO implements Serializable {
    private static final long serialVersionUID = 807888771653600686L;

    /**
     * 店铺ID
     */
    private Long id;

    /**
     * 店铺标签
     */
    private String tag;

    /**
     * 店铺名称
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
}

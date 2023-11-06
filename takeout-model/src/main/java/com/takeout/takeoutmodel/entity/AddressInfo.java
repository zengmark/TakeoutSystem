package com.takeout.takeoutmodel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 地址信息表
 * @TableName address_info
 */
@TableName(value ="address_info")
@Data
public class AddressInfo implements Serializable {
    /**
     * 地址信息表ID
     */
    @TableId(type = IdType.AUTO)
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
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
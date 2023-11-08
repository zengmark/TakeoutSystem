package com.takeout.takeoutmodel.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户表
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户角色：0 用户，1 店长，2 管理员
     */
    private Integer userRole;

    /**
     * 用户账户的余额
     */
    private Integer balance;

    /**
     * 当角色为 1（店长）时的店铺ID
     */
    private Long shopId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户的默认地址的ID
     */
    private Long defaultAddressId;

    /**
     * 是否删除，0 未删除，1 已删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
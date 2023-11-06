package com.takeout.takeoutmodel.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = -4164779258845419653L;
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户账号
     */
    private String userAccount;

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

}

package com.takeout.takeoutcommon.constant;

/**
 * 用户常量
 */
public interface UserConstant {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    /**
     * 用户登录后存储的密码需要加盐，然后通过 MD5 加密
     */
    String SALT = "salt";

    //  region 权限

    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "user";

    /**
     * 店铺权限
     */
    String SHOP_ROLE = "shop";

    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";

    // endregion
}
package com.takeout.takeoutuserservice.service;

import com.takeout.takeoutmodel.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.takeoutmodel.enums.UserRoleEnum;
import com.takeout.takeoutmodel.request.UserLoginRequest;
import com.takeout.takeoutmodel.request.UserRegisterRequest;

import javax.servlet.http.HttpServletRequest;

/**
* @author 13123
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2023-11-02 14:41:36
*/
public interface UserService extends IService<User> {
    User userLogin(UserLoginRequest loginRequest, HttpServletRequest request);

    User getSafetyUser(User originUser);

    Boolean userRegister(UserRegisterRequest registerRequest);

    Boolean userLogout(HttpServletRequest request);

    Integer updateUser(User user, User originUser, HttpServletRequest request);

    int changeRole(User loginUser, UserRoleEnum userRoleEnum);

    int updateBalance(Long userId, Long shopUserId, Integer price);
}

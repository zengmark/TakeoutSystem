package com.takeout.takeoutuserservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.takeoutcommon.common.ErrorCode;
import com.takeout.takeoutcommon.constant.UserConstant;
import com.takeout.takeoutcommon.exception.BusinessException;
import com.takeout.takeoutmodel.entity.User;
import com.takeout.takeoutmodel.request.UserLoginRequest;
import com.takeout.takeoutmodel.request.UserRegisterRequest;
import com.takeout.takeoutuserservice.mapper.UserMapper;
import com.takeout.takeoutuserservice.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author 13123
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2023-11-02 14:41:36
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User userLogin(UserLoginRequest loginRequest, HttpServletRequest request) {
        if (loginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "登录参数为空");
        }

        String userAccount = loginRequest.getUserAccount();
        String userPassword = loginRequest.getUserPassword();

        // todo 对于参数进行更严格的校验

        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码为空");
        }

        String encryptByMd5Password = DigestUtils.md5DigestAsHex((userPassword + UserConstant.SALT).getBytes());

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount).eq("userPassword", encryptByMd5Password);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.error("用户不存在");
            return null;
        }

        User safetyUser = getSafetyUser(user);
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, safetyUser);

        return user;
    }

    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User user = new User();
        user.setId(originUser.getId());
        user.setUserAccount(originUser.getUserAccount());
        user.setUserName(originUser.getUserName());
        user.setUserAvatar(originUser.getUserAvatar());
        user.setUserRole(originUser.getUserRole());
        user.setBalance(originUser.getBalance());
        user.setShopId(originUser.getShopId());
        user.setCreateTime(originUser.getCreateTime());
        user.setUpdateTime(originUser.getUpdateTime());
        user.setIsDelete(originUser.getIsDelete());

        return user;
    }

    @Override
    public Boolean userRegister(UserRegisterRequest registerRequest) {
        if (registerRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册参数为空");
        }

        String userAccount = registerRequest.getUserAccount();
        String userPassword = registerRequest.getUserPassword();

        if(StringUtils.isAnyBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码为空");
        }

        // todo 对于参数进行更严格的校验

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        Long count = userMapper.selectCount(queryWrapper);

        // 如果该账号已经存在了
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在，请勿重复添加");
        }

        User user = new User();

        String encryptByMd5Password = DigestUtils.md5DigestAsHex((userPassword + UserConstant.SALT).getBytes());

        user.setUserAccount(userAccount);
        user.setUserPassword(encryptByMd5Password);
        user.setUserRole(0);

        int insert = userMapper.insert(user);
        if(insert > 0) {
            return true;
        }

        return false;
    }

    @Override
    public Boolean userLogout(HttpServletRequest request) {
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        request.removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    @Override
    public Integer updateUser(User user, User originUser, HttpServletRequest request) {
        // 判断用户是否登录
        if(originUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if(user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 还得查询一下该用户是否存在账号？
        Long id = user.getId();
        User oldUser = userMapper.selectById(id);
        if(oldUser == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        int result = userMapper.updateById(user);
        User newUser = userMapper.selectById(id);
        User safetyUser = getSafetyUser(newUser);
        // 更新 session 的内容
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, safetyUser);

        return result;
    }

}





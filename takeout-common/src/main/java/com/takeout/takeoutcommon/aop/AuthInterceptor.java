package com.takeout.takeoutcommon.aop;

import com.takeout.takeoutcommon.annotation.AuthCheck;
import com.takeout.takeoutcommon.common.ErrorCode;
import com.takeout.takeoutcommon.constant.UserConstant;
import com.takeout.takeoutcommon.exception.BusinessException;
import com.takeout.takeoutmodel.entity.User;
import com.takeout.takeoutmodel.enums.UserRoleEnum;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
@Aspect
public class AuthInterceptor {

    /**
     * 判断用户是否拥有该权限
     * @param joinPoint
     * @param authCheck
     * @return
     * @throws Throwable
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        /*String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 必须有该权限才通过
        if (StringUtils.isNotBlank(mustRole)) {
            UserRoleEnum mustUserRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
            if (mustUserRoleEnum == null) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            String userRole = loginUser.getUserRole();
            // 如果被封号，直接拒绝
            if (UserRoleEnum.BAN.equals(mustUserRoleEnum)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            // 必须有管理员权限
            if (UserRoleEnum.ADMIN.equals(mustUserRoleEnum)) {
                if (!mustRole.equals(userRole)) {
                    throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
                }
            }
        }
        // 通过权限校验，放行
        return joinPoint.proceed();*/

        String role = authCheck.haveRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 获取当前登录用户
        User loginUser = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Integer userRole = loginUser.getUserRole();

        // 必须有权限才能放行
        if(StringUtils.isNotBlank(role)){
            UserRoleEnum haveRoleEnum = null;
            UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(userRole);
            for (UserRoleEnum value : UserRoleEnum.values()) {
                if(value.getText().equals(role)){
                    haveRoleEnum = value;
                }
            }
            // 如果为空，说明没有权限
            if(haveRoleEnum == null){
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            // 判断注解上添加的是什么权限
            if(haveRoleEnum != userRoleEnum){
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
        }
        return joinPoint.proceed();
    }
}

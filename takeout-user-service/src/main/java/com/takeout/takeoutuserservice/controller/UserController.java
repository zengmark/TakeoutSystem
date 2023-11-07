package com.takeout.takeoutuserservice.controller;

import cn.hutool.core.bean.BeanUtil;
import com.takeout.takeoutcommon.common.BaseResponse;
import com.takeout.takeoutcommon.common.ErrorCode;
import com.takeout.takeoutcommon.common.ResultUtils;
import com.takeout.takeoutcommon.constant.UserConstant;
import com.takeout.takeoutcommon.exception.BusinessException;
import com.takeout.takeoutmodel.entity.AddressInfo;
import com.takeout.takeoutmodel.entity.User;
import com.takeout.takeoutmodel.request.UserLoginRequest;
import com.takeout.takeoutmodel.request.UserRegisterRequest;
import com.takeout.takeoutmodel.vo.AddressInfoVO;
import com.takeout.takeoutmodel.vo.UserVO;
import com.takeout.takeoutuserservice.service.AddressInfoService;
import com.takeout.takeoutuserservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private AddressInfoService addressInfoService;


    @GetMapping("/test")
    public BaseResponse<User> test(HttpServletRequest request){
        log.debug("测试成功");
        User safetyUser = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        return ResultUtils.success(safetyUser);
    }

    @PostMapping("/login")
    public BaseResponse<UserVO> userLogin(@RequestBody UserLoginRequest loginRequest, HttpServletRequest request){
        if(loginRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "登录参数为空");
        }
        User user = userService.userLogin(loginRequest, request);
        if(user == null){
            return ResultUtils.success(null);
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    @PostMapping("/register")
    public BaseResponse<Boolean> userRegister(@RequestBody UserRegisterRequest registerRequest){
        // todo 前端进行初始密码和检查密码的校验
        if(registerRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册参数为空");
        }
        Boolean isSuccess = userService.userRegister(registerRequest);
        return ResultUtils.success(isSuccess);
    }

    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request){
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean isSuccess = userService.userLogout(request);
        return ResultUtils.success(isSuccess);
    }

    @GetMapping("/getLoginUser")
    public BaseResponse<UserVO> getLoginUser(HttpServletRequest request){
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(loginUser, userVO);
        return ResultUtils.success(userVO);
    }

    /**
     * todo 上传用户头像
     * @return
     */
    @PostMapping("/upload")
    public BaseResponse<String> uploadUserAvatar(){
        return null;
    }

    /**
     * 更新用户信息
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Integer> updateUserInfo(@RequestBody User user, HttpServletRequest request){
        if(user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User originUser = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        // todo 下面的是为了模拟测试环境
        user.setId(originUser.getId());
        user.setUserAccount(originUser.getUserAccount());
        user.setUserPassword(originUser.getUserPassword());
        user.setUserName("测试用户名");
        user.setUserAvatar(originUser.getUserAvatar());
        user.setUserRole(originUser.getUserRole());
        user.setBalance(100000);
        user.setShopId(originUser.getShopId());
        user.setCreateTime(originUser.getCreateTime());
        user.setUpdateTime(originUser.getUpdateTime());
        user.setIsDelete(originUser.getIsDelete());

        return ResultUtils.success(userService.updateUser(user, originUser, request));
    }

    /**
     * 获取用户地址
     * @param request
     * @return
     */
    @GetMapping("/getUserAddress")
    public BaseResponse<List<AddressInfoVO>> getUserAddress(HttpServletRequest request){
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 判断用户是否登录
        User loginUser = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        List<AddressInfo> addressInfoList = addressInfoService.getUserAddress(loginUser);
        List<AddressInfoVO> addressInfoVOList = addressInfoList.stream().map(this::getAddressVo).collect(Collectors.toList());
        return ResultUtils.success(addressInfoVOList);
    }

    private AddressInfoVO getAddressVo(AddressInfo addressInfo){
        if(addressInfo == null){
            return null;
        }

        AddressInfoVO addressInfoVO = new AddressInfoVO();
        BeanUtil.copyProperties(addressInfo, addressInfoVO);
        return addressInfoVO;
    }

}

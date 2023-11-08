package com.takeout.takeoutuserservice.controller.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.takeout.takeoutcommon.common.ErrorCode;
import com.takeout.takeoutcommon.exception.BusinessException;
import com.takeout.takeoutmodel.dto.UserDto;
import com.takeout.takeoutmodel.entity.AddressInfo;
import com.takeout.takeoutmodel.entity.User;
import com.takeout.takeoutmodel.enums.UserRoleEnum;
import com.takeout.takeoutserviceclient.service.UserFeignClient;
import com.takeout.takeoutuserservice.service.AddressInfoService;
import com.takeout.takeoutuserservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

@RestController
@RequestMapping("/inner")
@Slf4j
public class UserInnerController implements UserFeignClient {

    @Resource
    private UserService userService;

    @Resource
    private AddressInfoService addressInfoService;

    @PostMapping("/changeRole")
    @Override
    public int changeRole(@RequestBody UserDto userDto) {
        User loginUser = userDto.getUser();
        UserRoleEnum userRoleEnum = userDto.getUserRoleEnum();
        if (loginUser == null || userRoleEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userService.changeRole(loginUser, userRoleEnum);
    }

    @GetMapping("/get/id")
    @Override
    public User getUserById(Long userId) {
        return userService.getById(userId);
    }

    /**
     * 这个是提供给店家使用的，因为通常来说一家店也就一个地址，普通用户想要获取地址信息在 UserController 中已经写好了接口了
     *
     * @param userId
     * @return
     */
    @GetMapping("/get/address")
    @Override
    public AddressInfo getAddressByUserId(Long userId) {
        QueryWrapper<AddressInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        return addressInfoService.getOne(queryWrapper);
    }

    /**
     * 根据地址信息 ID 查询地址信息，这个是给查询订单的用户地址所使用的
     * @param addressInfoId
     * @return
     */
    @GetMapping("/get/addressByAddressId")
    @Override
    public AddressInfo getAddressByAddressId(Long addressInfoId) {
        if (Objects.isNull(addressInfoId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return addressInfoService.getById(addressInfoId);
    }

    @PutMapping("/updateBalance")
    @Override
    public int updateBalance(Long userId, Long shopUserId, Integer price) {
        if (Objects.isNull(userId) || Objects.isNull(shopUserId) || Objects.isNull(price)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return userService.updateBalance(userId, shopUserId, price);
    }

    @GetMapping("/test")
    @Override
    public int test() {
        log.info("测试数据");
        return 0;
    }
}

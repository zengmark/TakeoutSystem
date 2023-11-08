package com.takeout.takeoutuserservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.takeoutcommon.common.ErrorCode;
import com.takeout.takeoutcommon.constant.UserConstant;
import com.takeout.takeoutcommon.exception.BusinessException;
import com.takeout.takeoutmodel.entity.AddressInfo;
import com.takeout.takeoutmodel.entity.User;
import com.takeout.takeoutmodel.request.AddAddressRequest;
import com.takeout.takeoutuserservice.mapper.AddressInfoMapper;
import com.takeout.takeoutuserservice.service.AddressInfoService;
import com.takeout.takeoutuserservice.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * @author 13123
 * @description 针对表【address_info(地址信息表)】的数据库操作Service实现
 * @createDate 2023-11-06 19:06:03
 */
@Service
public class AddressInfoServiceImpl extends ServiceImpl<AddressInfoMapper, AddressInfo>
        implements AddressInfoService {

    @Resource
    private AddressInfoMapper addressInfoMapper;

    @Resource
    private UserService userService;

    @Override
    public List<AddressInfo> getUserAddress(User loginUser) {
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        QueryWrapper<AddressInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        return addressInfoMapper.selectList(queryWrapper);
    }

    @Transactional
    @Override
    public List<AddressInfo> addUserAddress(AddAddressRequest addAddressRequest, HttpServletRequest request) {
        if (addAddressRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        // 插入新地址到数据库中
        AddressInfo addressInfo = new AddressInfo();
        BeanUtil.copyProperties(addAddressRequest, addressInfo);
        addressInfo.setUserId(loginUser.getId());
        addressInfoMapper.insert(addressInfo);

        List<AddressInfo> userAddressList = getUserAddress(loginUser);
        if(userAddressList == null || userAddressList.isEmpty()){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 判断用户的地址列表是否只有一个地址，如果只有一个，那么设置为默认地址
        if(userAddressList.size() == 1){
            User user = userService.getById(loginUser.getId());
            user.setDefaultAddressId(userAddressList.get(0).getId());
            userService.updateById(user);
        }
        return userAddressList;
    }

    @Transactional
    @Override
    public AddressInfo addShopAddress(AddAddressRequest addAddressRequest, HttpServletRequest request) {
        if(addAddressRequest == null || request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);

        // 先判断当前店铺是否已经有了地址了，如果有了那么就不允许添加了
        QueryWrapper<AddressInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        List<AddressInfo> addressInfoList = addressInfoMapper.selectList(queryWrapper);
        if(addressInfoList != null && !addressInfoList.isEmpty()){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "店铺已存在地址，请勿重复添加");
        }

        // 插入新地址到数据库中
        AddressInfo addressInfo = new AddressInfo();
        BeanUtil.copyProperties(addAddressRequest, addressInfo);
        addressInfo.setUserId(loginUser.getId());
        addressInfoMapper.insert(addressInfo);

        User user = userService.getById(loginUser.getId());
        user.setDefaultAddressId(addressInfo.getId());
        userService.updateById(user);

        return addressInfo;
    }

    @Override
    public AddressInfo getDefaultAddress(HttpServletRequest request) {
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        return addressInfoMapper.selectById(loginUser.getDefaultAddressId());
    }

    @Transactional
    @Override
    public List<AddressInfo> deleteUserAddress(Long id, HttpServletRequest request) {
        if(Objects.isNull(id) || request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        // 判断所要删除的地址 ID 是否为用户的默认地址的 ID，如果是的话，更新用户的默认地址为 -1
        Long defaultAddressId = loginUser.getDefaultAddressId();
        if(Objects.equals(defaultAddressId, id)){
            User user = userService.getById(loginUser.getId());
            user.setDefaultAddressId(-1L);
            userService.updateById(user);
        }

        // 删除地址
        addressInfoMapper.deleteById(id);

        return getUserAddress(loginUser);
    }

    @Override
    public int setDefaultAddress(Long id, HttpServletRequest request) {
        if(Objects.isNull(id) || request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        User user = userService.getById(loginUser.getId());
        user.setDefaultAddressId(id);
        userService.updateById(user);

        // 需要更新 session 的用户信息
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, userService.getSafetyUser(user));
        return 1;
    }
}





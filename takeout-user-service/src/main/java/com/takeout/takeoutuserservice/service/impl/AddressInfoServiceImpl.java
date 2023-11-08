package com.takeout.takeoutuserservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.takeoutcommon.common.ErrorCode;
import com.takeout.takeoutcommon.exception.BusinessException;
import com.takeout.takeoutmodel.entity.AddressInfo;
import com.takeout.takeoutmodel.entity.User;
import com.takeout.takeoutuserservice.mapper.AddressInfoMapper;
import com.takeout.takeoutuserservice.service.AddressInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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

    @Override
    public List<AddressInfo> getUserAddress(User loginUser) {
        if(loginUser == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<AddressInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        return addressInfoMapper.selectList(queryWrapper);
    }
}





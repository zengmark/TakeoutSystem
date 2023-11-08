package com.takeout.takeoutuserservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.takeoutmodel.entity.AddressInfo;
import com.takeout.takeoutmodel.entity.User;

import java.util.List;

/**
* @author 13123
* @description 针对表【address_info(地址信息表)】的数据库操作Service
* @createDate 2023-11-06 19:06:03
*/
public interface AddressInfoService extends IService<AddressInfo> {

    List<AddressInfo> getUserAddress(User loginUser);
}

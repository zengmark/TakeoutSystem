package com.takeout.takeoutshopservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.takeoutmodel.entity.Shop;
import com.takeout.takeoutshopservice.mapper.ShopMapper;
import com.takeout.takeoutshopservice.service.ShopService;
import org.springframework.stereotype.Service;

/**
* @author 13123
* @description 针对表【shop(店铺表)】的数据库操作Service实现
* @createDate 2023-11-06 11:32:22
*/
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop>
    implements ShopService {

}





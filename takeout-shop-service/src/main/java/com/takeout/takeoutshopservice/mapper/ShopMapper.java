package com.takeout.takeoutshopservice.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.takeoutmodel.entity.Shop;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 13123
 * @description 针对表【shop(店铺表)】的数据库操作Mapper
 * @createDate 2023-11-06 11:32:22
 * @Entity com.takeout.takeoutmodel.entity.Shop
 */
public interface ShopMapper extends BaseMapper<Shop> {
    List<Shop> getHistoryShops();
}





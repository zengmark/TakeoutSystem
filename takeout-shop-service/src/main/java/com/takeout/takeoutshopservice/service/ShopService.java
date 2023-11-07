package com.takeout.takeoutshopservice.service;

import com.takeout.takeoutmodel.entity.AddressInfo;
import com.takeout.takeoutmodel.entity.Shop;
import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.takeoutmodel.request.AddShopRequest;
import com.takeout.takeoutmodel.vo.ShopVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 13123
* @description 针对表【shop(店铺表)】的数据库操作Service
* @createDate 2023-11-06 11:32:22
*/
public interface ShopService extends IService<Shop> {
    Shop addShop(AddShopRequest addShopRequest, HttpServletRequest request);

    void test();

    List<Shop> searchShopByName(String searchShopName);

    List<Shop> searchShopByTag(Integer tag);

    AddressInfo getAddress(Long userId);

    List<Shop> getShopToBeAudited();

    int auditShop(Long id, Boolean isPass);

    List<Shop> getHistoryShops();
}

package com.takeout.takeoutshopservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.takeoutcommon.common.PageRequest;
import com.takeout.takeoutmodel.entity.AddressInfo;
import com.takeout.takeoutmodel.entity.Shop;
import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.takeoutmodel.request.AddShopRequest;
import com.takeout.takeoutmodel.vo.ShopVO;
import org.springframework.web.bind.annotation.RequestBody;

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

    Page<Shop> searchShopByName(String searchShopName, PageRequest pageRequest);

    Page<Shop> searchShopByTag(Integer tag, PageRequest pageRequest);

    AddressInfo getAddress(Long userId);

    Page<Shop> getShopToBeAudited(PageRequest pageRequest);

    int auditShop(Long id, Boolean isPass);

    Page<Shop> getHistoryShops(PageRequest pageRequest);

    Page<ShopVO> getShopPageVO(Page<Shop> shopPage);

//    Page<ShopVO> getAllShopsByPage(PageRequest pageRequest);
}

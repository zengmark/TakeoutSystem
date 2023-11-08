package com.takeout.takeoutserviceclient.service;

import com.takeout.takeoutmodel.entity.Shop;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "takeout-shop-service", path = "/api/shop/inner")
public interface ShopFeignClient {
    @GetMapping("/getShopById")
    Shop getShopById(@RequestParam("shopUserId") Long shopId);
}

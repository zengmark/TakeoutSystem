package com.takeout.takeoutserviceclient.service;

import com.takeout.takeoutmodel.entity.Coupon;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "takeout-coupon-service", path = "/api/coupon/inner")
public interface CouponFeignClient {
    @GetMapping("/getCouponById")
    Coupon getCouponById(@RequestParam("couponId") Long couponId);

    @PutMapping("/setUsed")
    int setUsed(@RequestParam("couponId") Long couponId, @RequestParam("orderId") Long orderId);

    @PutMapping("/setUnUsed")
    int setUnUsed(@RequestParam("id") Long id);
}

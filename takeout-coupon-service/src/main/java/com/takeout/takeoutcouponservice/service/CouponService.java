package com.takeout.takeoutcouponservice.service;

import com.takeout.takeoutmodel.entity.Coupon;
import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.takeoutmodel.request.AddCouponRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 13123
* @description 针对表【coupon(优惠券表)】的数据库操作Service
* @createDate 2023-11-07 19:32:26
*/
public interface CouponService extends IService<Coupon> {

    int createCoupon(AddCouponRequest addCouponRequest, HttpServletRequest request);

    int grabCoupon(String couponName, HttpServletRequest request);

    List<Coupon> getUserCoupons(HttpServletRequest request);
}

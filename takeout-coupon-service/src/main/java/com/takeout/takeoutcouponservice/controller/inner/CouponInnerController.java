package com.takeout.takeoutcouponservice.controller.inner;

import com.takeout.takeoutcommon.common.ErrorCode;
import com.takeout.takeoutcommon.exception.BusinessException;
import com.takeout.takeoutcouponservice.service.CouponService;
import com.takeout.takeoutmodel.entity.Coupon;
import com.takeout.takeoutserviceclient.service.CouponFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

@RestController
@RequestMapping("/inner")
@Slf4j
public class CouponInnerController implements CouponFeignClient {

    @Resource
    private CouponService couponService;

    @GetMapping("/getCouponById")
    @Override
    public Coupon getCouponById(@RequestParam("couponId") Long couponId) {
        if(Objects.isNull(couponId)){
            return null;
        }
        return couponService.getCouponById(couponId);
    }

    @PutMapping("/setUsed")
    @Override
    public int setUsed(@RequestParam("couponId") Long couponId, @RequestParam("orderId") Long orderId) {
        if(Objects.isNull(couponId)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return couponService.setUsed(couponId, orderId);
    }

    @PutMapping("/setUnUsed")
    @Override
    public int setUnUsed(Long id) {
        if(Objects.isNull(id)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return couponService.setUnUsed(id);
    }
}

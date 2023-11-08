package com.takeout.takeoutcouponservice.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.takeout.takeoutcommon.annotation.AuthCheck;
import com.takeout.takeoutcommon.common.BaseResponse;
import com.takeout.takeoutcommon.common.ErrorCode;
import com.takeout.takeoutcommon.common.ResultUtils;
import com.takeout.takeoutcommon.exception.BusinessException;
import com.takeout.takeoutcouponservice.service.CouponService;
import com.takeout.takeoutmodel.entity.Coupon;
import com.takeout.takeoutmodel.request.AddCouponRequest;
import com.takeout.takeoutmodel.vo.CouponVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class CouponController {

    @Resource
    private CouponService couponService;

    /**
     * 管理员创建优惠券
     * @param addCouponRequest
     * @param request
     * @return
     */
    @PostMapping("/createCoupon")
    @AuthCheck(haveRole = "admin")
    public BaseResponse<Integer> createCoupon(@RequestBody AddCouponRequest addCouponRequest, HttpServletRequest request){
        if(addCouponRequest == null || request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int insert = couponService.createCoupon(addCouponRequest, request);
        return ResultUtils.success(insert);
    }

    /**
     * 用户抢优惠券（一个人只能抢一张优惠券）
     * @param couponName
     * @param request
     * @return
     */
    @PutMapping("/grabCoupon")
    public BaseResponse<Integer> grabCoupon(@RequestParam("couponName") String couponName, HttpServletRequest request){
        if(StrUtil.isBlank(couponName) || request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int grab = couponService.grabCoupon(couponName, request);
        return ResultUtils.success(grab);
    }

    /**
     * 获取用户的所有的可用优惠券
     * @param request
     * @return
     */
    @GetMapping("/getUserCoupons")
    public BaseResponse<List<CouponVO>> getUserCoupons(HttpServletRequest request){
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Coupon> userCouponList = couponService.getUserCoupons(request);
        List<CouponVO> userCouponVOList = userCouponList.stream().map(this::getCouponVO).collect(Collectors.toList());
        return ResultUtils.success(userCouponVOList);
    }

    @GetMapping("/getAllAvailableCoupons")
    @AuthCheck(haveRole = "admin")
    public BaseResponse<List<CouponVO>> getAllAvailableCoupons(){

        return ResultUtils.success(null);
    }

    private CouponVO getCouponVO(Coupon coupon){
        if(coupon == null){
            return null;
        }
        CouponVO couponVO = new CouponVO();
        BeanUtil.copyProperties(coupon, couponVO);
        return couponVO;
    }
}

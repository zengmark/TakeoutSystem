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
import java.util.Map;
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

    /**
     * 展示所有可用的优惠券（这里的可用的定义是，用户ID为-1且优惠券状态为0），返回一个 Map，key：优惠券名称，value：优惠券的数组
     * @return
     */
    @GetMapping("/getAllAvailableCoupons")
    public BaseResponse<Map<String, List<CouponVO>>> getAllAvailableCoupons(HttpServletRequest request){
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Coupon> couponList = couponService.getAllAvailableCoupons(request);
        Map<String, List<CouponVO>> availableCouponMap = couponList.stream().map(this::getCouponVO).collect(Collectors.groupingBy(CouponVO::getCouponName));
        return ResultUtils.success(availableCouponMap);
    }

    /**
     * 批量删除优惠券，管理员的权限
     * @param ids
     * @return
     */
    @DeleteMapping("/deleteBatch")
    @AuthCheck(haveRole = "admin")
    public BaseResponse<Boolean> deleteBatch(@RequestBody List<Long> ids){
        if(ids == null || ids.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isSuccess = couponService.removeBatchByIds(ids);
        return ResultUtils.success(isSuccess);
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

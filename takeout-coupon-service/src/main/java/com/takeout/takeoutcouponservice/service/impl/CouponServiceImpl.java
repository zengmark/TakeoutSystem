package com.takeout.takeoutcouponservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.takeoutcommon.common.ErrorCode;
import com.takeout.takeoutcommon.constant.UserConstant;
import com.takeout.takeoutcommon.exception.BusinessException;
import com.takeout.takeoutcouponservice.mapper.CouponMapper;
import com.takeout.takeoutcouponservice.service.CouponService;
import com.takeout.takeoutmodel.entity.Coupon;
import com.takeout.takeoutmodel.entity.User;
import com.takeout.takeoutmodel.request.AddCouponRequest;
import com.takeout.takeoutserviceclient.service.UserFeignClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author 13123
 * @description 针对表【coupon(优惠券表)】的数据库操作Service实现
 * @createDate 2023-11-07 19:32:26
 */
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon>
        implements CouponService {

    @Resource
    private CouponMapper couponMapper;

    @Resource
    private UserFeignClient userFeignClient;

    @Override
    public int createCoupon(AddCouponRequest addCouponRequest, HttpServletRequest request) {
        if (addCouponRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long adminId = addCouponRequest.getAdminId();
        Integer num = addCouponRequest.getNum();

        User loginUser = userFeignClient.getLoginUser(request);
        if (!Objects.equals(loginUser.getId(), adminId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        for (int i = 0; i < num; i++) {
            Coupon coupon = new Coupon();
            BeanUtil.copyProperties(addCouponRequest, coupon);
            couponMapper.insert(coupon);
        }
        return 1;
    }

    // todo 先简单的上个锁，避免数据不一致的情况导致多个用户抢到了同一个优惠券
    @Override
    public synchronized int grabCoupon(String couponName, HttpServletRequest request) {
        if(StrUtil.isBlank(couponName) || request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
        // 判断用户是否抢过该优惠券了（判断依据是判断该优惠券名称的优惠券中是否有某个优惠券有该用户的 ID， 且该优惠券是可用的，因为如果不可用那么是可以允许用户抢新的可用的优惠券的）
        queryWrapper.eq("couponName", couponName).eq("userId", loginUser.getId()).eq("couponStatus", 0);
        List<Coupon> userCouponList = couponMapper.selectList(queryWrapper);
        if(userCouponList != null && !userCouponList.isEmpty()){
            throw new BusinessException(ErrorCode.FORBIDDEN, "您已持有该优惠券");
        }

        queryWrapper = new QueryWrapper<>();
        // 这里有几个个筛选条件，分别是：优惠券名称要一致，优惠券可用，优惠券未过期（这里直接定时任务然后定期去判断优惠券是否过期，如果过期就是不可用，所以这个不参与判断条件）
        queryWrapper.eq("couponName", couponName).eq("couponStatus", 0).eq("userId", -1);
        List<Coupon> couponList = couponMapper.selectList(queryWrapper);

        Coupon coupon = couponList.get(0);
        coupon.setUserId(loginUser.getId());

        return couponMapper.updateById(coupon);
    }

    @Override
    public List<Coupon> getUserCoupons(HttpServletRequest request) {
        if(request == null){
            // 这里不要抛异常，直接返回一个空数组即可
            return new ArrayList<>();
        }
        User loginUser = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
        // 查询用户优惠券（只查可用的）
        queryWrapper.eq("userId", loginUser.getId()).eq("couponStatus", 0);
        return couponMapper.selectList(queryWrapper);
    }

    @Override
    public Coupon getCouponById(Long couponId) {
        if(Objects.isNull(couponId)){
            // 这里不抛异常了，直接返回空即可
            return null;
        }
        return couponMapper.selectById(couponId);
    }

    @Override
    public int setUsed(Long couponId, Long orderId) {
        if(Objects.isNull(couponId)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        Coupon coupon = couponMapper.selectById(couponId);
        if(coupon.getCouponStatus() == 1){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 绑定优惠券到用户以及订单
        coupon.setOrderId(orderId);
        coupon.setCouponStatus(1);
        return couponMapper.updateById(coupon);
    }

    @Override
    public int setUnUsed(Long id) {
        if(Objects.isNull(id)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("orderId", id);
        List<Coupon> couponList = couponMapper.selectList(queryWrapper);
        // 如果真的存在这个订单
        if(couponList != null && !couponList.isEmpty()){
            Coupon coupon = couponList.get(0);
            coupon.setOrderId(-1L);
            coupon.setCouponStatus(0);
        }
        return 1;
    }

    @Override
    public List<Coupon> getAllAvailableCoupons(HttpServletRequest request) {
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("couponStatus", 0).eq("userId", -1L);
        return couponMapper.selectList(queryWrapper);
    }
}





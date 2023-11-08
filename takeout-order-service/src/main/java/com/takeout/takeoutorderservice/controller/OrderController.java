package com.takeout.takeoutorderservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.takeoutcommon.annotation.AuthCheck;
import com.takeout.takeoutcommon.common.BaseResponse;
import com.takeout.takeoutcommon.common.ErrorCode;
import com.takeout.takeoutcommon.common.PageRequest;
import com.takeout.takeoutcommon.common.ResultUtils;
import com.takeout.takeoutcommon.exception.BusinessException;
import com.takeout.takeoutmodel.entity.OrderInfo;
import com.takeout.takeoutmodel.request.AddOrderRequest;
import com.takeout.takeoutmodel.vo.OrderInfoVO;
import com.takeout.takeoutorderservice.service.OrderInfoService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
public class OrderController {

    @Resource
    private OrderInfoService orderInfoService;

    @GetMapping("/test")
    public BaseResponse<String> test() {
        return ResultUtils.success("test");
    }

    /**
     * 创建订单
     * todo 判断用户当前是否拥有默认地址，如果没有，那么将这个地址作为默认地址
     *
     * @param addOrderRequest
     * @param request
     * @return
     */
    @PostMapping("/create")
    public BaseResponse<Integer> createOrder(@RequestBody AddOrderRequest addOrderRequest, HttpServletRequest request) {
        if (addOrderRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 这里是需要登录才能有添加订单的操作
        int order = orderInfoService.createOrder(addOrderRequest, request);

        return ResultUtils.success(order);
    }

    /**
     * 删除订单
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete")
    public BaseResponse<Boolean> deleteOrder(@RequestParam("id") Long id) {
        if (Objects.isNull(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isSuccess = orderInfoService.removeById(id);
        return ResultUtils.success(isSuccess);
    }

    /**
     * 获取用户所有订单，暂时先将所有数据返回，这里不需要返回优惠券的信息，但是好像需要返回一个历史订单的价格
     * todo 按照创建时间排序
     *
     * @param request
     * @return
     */
    @PostMapping("/listUserOrders")
    public BaseResponse<Page<OrderInfoVO>> listUserOrders(HttpServletRequest request, @RequestBody PageRequest pageRequest) {
        if (request == null || pageRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<OrderInfoVO> orderInfoVOPage = orderInfoService.listUserOrders(request, pageRequest);
        return ResultUtils.success(orderInfoVOPage);
    }

    /**
     * 获取店铺历史订单，暂时先将所有数据返回，这里跟上面有点不同的是，可能用户会删除订单，但是店家依旧能看到订单
     * todo 应当按照创建时间排序
     *
     * @param shopId
     * @return
     */
    @PostMapping("/listShopOrders")
    @AuthCheck(haveRole = "shop")
    public BaseResponse<Page<OrderInfoVO>> listShopOrders(@RequestParam("shopId") Long shopId, @RequestBody PageRequest pageRequest) {
        if (Objects.isNull(shopId) || pageRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<OrderInfoVO> orderInfoVOPage = orderInfoService.listShopOrders(shopId, pageRequest);
        return ResultUtils.success(orderInfoVOPage);
    }

    /**
     * 完成订单，更改订单状态
     *
     * @param id
     * @return
     */
    @PutMapping("/finishOrder")
    public BaseResponse<Integer> finishOrder(@RequestParam("id") Long id) {
        if (Objects.isNull(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = orderInfoService.finishOrder(id);
        return ResultUtils.success(result);
    }

    /**
     * 取消订单，这里还需要返还优惠券
     *
     * @param id
     * @param request
     * @return
     */
    @PutMapping("/cancelOrder")
    public BaseResponse<Integer> cancelOrder(@RequestParam("id") Long id, HttpServletRequest request) {
        if (Objects.isNull(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int cancel = orderInfoService.cancelOrder(id, request);
        return ResultUtils.success(cancel);
    }


    /**
     * 获取用户的购物车，状态为 2 的订单即为购物车的订单
     * todo 应当指定排序规则
     *
     * @param request
     * @return
     */
    @PostMapping("/shoppingCart")
    public BaseResponse<Page<OrderInfoVO>> getShoppingCart(@RequestBody PageRequest pageRequest, HttpServletRequest request) {
        if(request == null || pageRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<OrderInfoVO> orderInfoVOPage = orderInfoService.getShoppingCart(pageRequest, request);
        return ResultUtils.success(orderInfoVOPage);
    }

    /**
     * 删除用户的购物车
     * @param request
     * @return
     */
    @DeleteMapping("deleteShoppingCart")
    public BaseResponse<Integer> deleteShoppingCart(HttpServletRequest request){
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int delete = orderInfoService.deleteShoppingCart(request);
        return ResultUtils.success(delete);
    }
}

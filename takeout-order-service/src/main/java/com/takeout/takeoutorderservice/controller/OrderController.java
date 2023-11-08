package com.takeout.takeoutorderservice.controller;

import com.takeout.takeoutcommon.common.BaseResponse;
import com.takeout.takeoutcommon.common.ErrorCode;
import com.takeout.takeoutcommon.common.ResultUtils;
import com.takeout.takeoutcommon.exception.BusinessException;
import com.takeout.takeoutmodel.entity.OrderInfo;
import com.takeout.takeoutmodel.request.AddOrderRequest;
import com.takeout.takeoutmodel.vo.OrderInfoVO;
import com.takeout.takeoutorderservice.service.OrderInfoService;
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
     * 获取用户所有订单，暂时先将所有数据返回
     * todo 分页返回数据，同时应当按照创建时间排序
     *
     * @param request
     * @return
     */
    @GetMapping("/listUserOrders")
    public BaseResponse<List<OrderInfoVO>> listUserOrders(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<OrderInfoVO> orderInfoVOList = orderInfoService.listUserOrders(request);
        return ResultUtils.success(orderInfoVOList);
    }

    /**
     * 获取店铺历史订单，暂时先将所有数据返回，这里跟上面有点不同的是，可能用户会删除订单，但是店家依旧能看到订单
     * todo 分页返回数据，同时应当按照创建时间排序
     *
     * @param shopId
     * @return
     */
    @GetMapping("/listShopOrders")
    public BaseResponse<List<OrderInfoVO>> listShopOrders(@RequestParam("shopId") Long shopId) {
        if (Objects.isNull(shopId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        List<OrderInfoVO> orderInfoVOList = orderInfoService.listShopOrders(shopId);
        return ResultUtils.success(orderInfoVOList);
    }

    /**
     * 完成订单，更改订单状态
     * @param id
     * @return
     */
    @PutMapping("/finishOrder")
    public BaseResponse<Integer> finishOrder(@RequestParam("id") Long id){
        if(Objects.isNull(id)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = orderInfoService.finishOrder(id);
        return ResultUtils.success(result);
    }

    @PutMapping("/cancelOrder")
    public BaseResponse<Integer> cancelOrder(@RequestParam("id") Long id, HttpServletRequest request){
        if(Objects.isNull(id)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int cancel = orderInfoService.cancelOrder(id, request);
        return ResultUtils.success(cancel);
    }


    /**
     * 获取用户的购物车，状态为 2 的订单即为购物车的订单
     * @param request
     * @return
     */
    @GetMapping("/shoppingCart")
    public BaseResponse<List<OrderInfoVO>> getShoppingCart(HttpServletRequest request){
        List<OrderInfoVO> orderInfoVOList = orderInfoService.getShoppingCart(request);
        return ResultUtils.success(orderInfoVOList);
    }
}

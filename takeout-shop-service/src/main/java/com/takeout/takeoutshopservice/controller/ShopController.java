package com.takeout.takeoutshopservice.controller;

import com.takeout.takeoutcommon.common.BaseResponse;
import com.takeout.takeoutcommon.common.ResultUtils;
import com.takeout.takeoutmodel.entity.Shop;
import com.takeout.takeoutmodel.vo.ShopVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ShopController {

    @GetMapping("/test")
    public BaseResponse<Shop> test(){
        log.info("测试成功");
        return ResultUtils.success(null);
    }

    @PostMapping("/addshop")
    public BaseResponse<ShopVO> addShop(){
        return null;
    }
}

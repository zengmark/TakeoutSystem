package com.takeout.takeoutshopservice.controller.inner;

import com.takeout.takeoutcommon.common.ErrorCode;
import com.takeout.takeoutcommon.exception.BusinessException;
import com.takeout.takeoutmodel.entity.Shop;
import com.takeout.takeoutserviceclient.service.ShopFeignClient;
import com.takeout.takeoutshopservice.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

@RestController
@RequestMapping("/inner")
@Slf4j
public class ShopInnerController implements ShopFeignClient {

    @Resource
    private ShopService shopService;

    @GetMapping("/getShopById")
    @Override
    public Shop getShopById(@RequestParam("shopUserId") Long shopId) {
        if(Objects.isNull(shopId)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return shopService.getById(shopId);
    }

}

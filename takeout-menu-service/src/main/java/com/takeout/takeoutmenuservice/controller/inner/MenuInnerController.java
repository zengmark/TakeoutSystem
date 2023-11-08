package com.takeout.takeoutmenuservice.controller.inner;

import com.takeout.takeoutmenuservice.service.MenuService;
import com.takeout.takeoutmodel.entity.Menu;
import com.takeout.takeoutserviceclient.service.MenuFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/inner")
@Slf4j
public class MenuInnerController implements MenuFeignClient {

    @Resource
    private MenuService menuService;

    @PostMapping("/getOrderMenus")
    @Override
    public List<Menu> getOrderMenus(@RequestBody List<Long> menuIdList) {
        if(menuIdList == null || menuIdList.isEmpty()){
            return new ArrayList<>();
        }
        return menuService.getHistoryMenus(menuIdList);
    }
}

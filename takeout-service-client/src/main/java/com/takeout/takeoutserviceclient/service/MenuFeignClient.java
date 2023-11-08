package com.takeout.takeoutserviceclient.service;

import cn.hutool.core.bean.BeanUtil;
import com.takeout.takeoutmodel.entity.Menu;
import com.takeout.takeoutmodel.vo.MenuVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@FeignClient(name = "takeout-menu-service", path = "/api/menu/inner")
public interface MenuFeignClient {

    @PostMapping("/getOrderMenus")
    List<Menu> getOrderMenus(@RequestBody List<Long> menuIdList);

    default List<MenuVO> getMenuVOList(List<Menu> menuList) {
        return menuList.stream().map(this::getMenuVO).collect(Collectors.toList());
    }

    default MenuVO getMenuVO(Menu menu) {
        MenuVO menuVO = new MenuVO();
        BeanUtil.copyProperties(menu, menuVO);
        return menuVO;
    }
}

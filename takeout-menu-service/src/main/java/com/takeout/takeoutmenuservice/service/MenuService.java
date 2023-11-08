package com.takeout.takeoutmenuservice.service;

import com.takeout.takeoutmodel.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.takeoutmodel.request.AddMenuRequest;
import com.takeout.takeoutmodel.request.UpdateMenuRequest;

import java.util.List;

/**
* @author 13123
* @description 针对表【menu(菜品表)】的数据库操作Service
* @createDate 2023-11-06 20:43:13
*/
public interface MenuService extends IService<Menu> {

    List<Menu> getAllMenus(Long shopId);

    int addMenu(AddMenuRequest request);

    int deleteMenu(Long menuId);

    int deleteCategory(String category, Long shopId);

    int updateMenu(UpdateMenuRequest request);

    List<Menu> getHistoryMenus(List<Long> historyIds);

}

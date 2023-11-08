package com.takeout.takeoutmenuservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.takeoutcommon.common.ErrorCode;
import com.takeout.takeoutcommon.exception.BusinessException;
import com.takeout.takeoutmenuservice.mapper.MenuMapper;
import com.takeout.takeoutmenuservice.service.MenuService;
import com.takeout.takeoutmodel.entity.Menu;
import com.takeout.takeoutmodel.request.AddMenuRequest;
import com.takeout.takeoutmodel.request.UpdateMenuRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author 13123
 * @description 针对表【menu(菜品表)】的数据库操作Service实现
 * @createDate 2023-11-06 20:43:13
 */
@Service
@Slf4j
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu>
        implements MenuService {

    @Resource
    private MenuMapper menuMapper;

    @Override
    public List<Menu> getAllMenus(Long shopId) {
        if (Objects.isNull(shopId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("shopId", shopId);
        return menuMapper.selectList(queryWrapper);
    }

    @Override
    public int addMenu(AddMenuRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        // 判断该店铺是否有同名的菜
        queryWrapper.eq("shopId", request.getShopId()).eq("menuName", request.getMenuName());
        List<Menu> menuList = menuMapper.selectList(queryWrapper);
        if (!menuList.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "存在同名菜品");
        }

        Menu menu = new Menu();
        BeanUtil.copyProperties(request, menu);
        return menuMapper.insert(menu);
    }

    @Override
    public int deleteMenu(Long menuId) {
        if (Objects.isNull(menuId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", menuId);
        List<Menu> menuList = menuMapper.selectList(queryWrapper);
        if (menuList.isEmpty()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除菜品不存在");
        }

        return menuMapper.deleteById(menuId);
    }

    @Override
    public int deleteCategory(String category, Long shopId) {
        if (StrUtil.isBlank(category) || Objects.isNull(shopId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("shopId", shopId).eq("category", category);
        List<Menu> menuList = menuMapper.selectList(queryWrapper);
        if (menuList.isEmpty()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "该分类不存在");
        }

        return menuMapper.delete(queryWrapper);
    }

    @Transactional
    @Override
    public int updateMenu(UpdateMenuRequest request) {
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long id = request.getId();
        Menu menu = menuMapper.selectById(id);
        if(menu == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新菜品不存在");
        }

        menu = new Menu();
        menu.setShopId(request.getShopId());
        menu.setCategory(request.getCategory());
        menu.setMenuName(request.getMenuName());
        menu.setMenuDescription(request.getMenuDescription());
        menu.setPrice(request.getPrice());
        menu.setPicture(request.getPicture());

        // 这里需要逻辑删除掉菜品，但是不能真正删掉，因为日后需要查历史订单，查询历史订单的时候需要自己编写 sql
        int delete = menuMapper.deleteById(id);
        return menuMapper.insert(menu);
    }

    /**
     * 获取所有历史菜品，这里包括被删除后的菜品，菜品被更新时候是需要逻辑删除然后创建新的菜品的，因为查看历史菜品的时候会需要查看当时的菜品而不是更新后的菜品
     * @param historyIds
     * @return
     */
    @Override
    public List<Menu> getHistoryMenus(List<Long> historyIds) {
        if(historyIds == null || historyIds.isEmpty()){
            return new ArrayList<>();
        }
        return menuMapper.getHistoryMenus(historyIds);
    }

}





package com.takeout.takeoutmenuservice.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.takeout.takeoutcommon.common.BaseResponse;
import com.takeout.takeoutcommon.common.ErrorCode;
import com.takeout.takeoutcommon.common.ResultUtils;
import com.takeout.takeoutcommon.exception.BusinessException;
import com.takeout.takeoutmenuservice.mapper.MenuMapper;
import com.takeout.takeoutmenuservice.service.MenuService;
import com.takeout.takeoutmodel.entity.Menu;
import com.takeout.takeoutmodel.request.AddMenuRequest;
import com.takeout.takeoutmodel.request.UpdateMenuRequest;
import com.takeout.takeoutmodel.vo.MenuVO;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class MenuController {

    @Resource
    private MenuService menuService;

    @Resource
    private MenuMapper menuMapper;

    @GetMapping("/test")
    public BaseResponse<Menu> test(){
        return ResultUtils.success(menuMapper.test());
    }

    /**
     * 给某一家店铺添加菜品，其实添加的过程就默认了指定了菜品的种类了，那么也不用关心具体这个种类是否存在了，存在那么返回的时候就多一个种类，不存在就无所谓，一样的
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Map<String, List<MenuVO>>> addMenu(@RequestBody AddMenuRequest request){
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int insert = menuService.addMenu(request);
        if(insert == 0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        Map<String, List<MenuVO>> menuVOMap = getMenuVOMap(request.getShopId());
        return ResultUtils.success(menuVOMap);
    }

    /**
     * 删除菜品
     * @param menuId
     * @return
     */
    @DeleteMapping("/delete")
    public BaseResponse<Map<String, List<MenuVO>>> deleteMenu(@RequestParam("menuId") Long menuId, @RequestParam("shopId") Long shopId){
        if(Objects.isNull(menuId) || Objects.isNull(shopId)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int delete = menuService.deleteMenu(menuId);
        Map<String, List<MenuVO>> menuVOMap = getMenuVOMap(shopId);
        return ResultUtils.success(menuVOMap);
    }

    /**
     * 删除一整个种类的菜品，这个操作是有必要的，直接将所有的该分类下的菜品都删除掉
     * @param category
     * @param shopId
     * @return
     */
    @DeleteMapping("/deleteCategory")
    public BaseResponse<Map<String, List<MenuVO>>> deleteCategory(@RequestParam("category") String category, @RequestParam("shopId") Long shopId){
        if(StrUtil.isBlank(category) || Objects.isNull(shopId)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int delete = menuService.deleteCategory(category, shopId);
        Map<String, List<MenuVO>> menuVOMap = getMenuVOMap(shopId);
        return ResultUtils.success(menuVOMap);
    }

    @PutMapping("/update")
    public BaseResponse<Map<String, List<MenuVO>>> updateMenu(@RequestBody UpdateMenuRequest request){
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int update = menuService.updateMenu(request);
        Map<String, List<MenuVO>> menuVOMap = getMenuVOMap(request.getShopId());
        return ResultUtils.success(menuVOMap);
    }

    /**
     * 获取某一家商铺的所有菜品，返回值是按菜品分类的 Map
     * @param shopId
     * @return
     */
    @GetMapping("/getAllMenus")
    public BaseResponse<Map<String, List<MenuVO>>> getAllMenus(@RequestParam Long shopId){
        if(Objects.isNull(shopId)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Map<String, List<MenuVO>> menuVOMap = getMenuVOMap(shopId);
        return ResultUtils.success(menuVOMap);
    }

    @PostMapping("/getHistoryMenus")
    public BaseResponse<List<MenuVO>> getHistoryMenus(@RequestBody List<Long> historyIds){
        if(historyIds == null || historyIds.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Menu> menuList = menuService.getHistoryMenus(historyIds);
        List<MenuVO> menuVOList = menuList.stream().map(this::getMenuVO).collect(Collectors.toList());
        return ResultUtils.success(menuVOList);
    }

    // todo 可以用缓存优化这一步（毕竟菜单一般来说也不会经常变，可以提前全部加载出来）
    @NotNull
    private Map<String, List<MenuVO>> getMenuVOMap(Long shopId) {
        List<Menu> menuList = menuService.getAllMenus(shopId);
        Map<String, List<MenuVO>> menuVOMap = menuList.stream().map(this::getMenuVO).collect(Collectors.groupingBy(MenuVO::getCategory));
        return menuVOMap;
    }

    private MenuVO getMenuVO(Menu originMenu){
        if (originMenu == null) {
            return null;
        }
        MenuVO menuVO = new MenuVO();
        BeanUtil.copyProperties(originMenu, menuVO);
        return menuVO;
    }
}

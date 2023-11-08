package com.takeout.takeoutshopservice.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.takeoutcommon.annotation.AuthCheck;
import com.takeout.takeoutcommon.common.BaseResponse;
import com.takeout.takeoutcommon.common.ErrorCode;
import com.takeout.takeoutcommon.common.PageRequest;
import com.takeout.takeoutcommon.common.ResultUtils;
import com.takeout.takeoutcommon.constant.UserConstant;
import com.takeout.takeoutcommon.exception.BusinessException;
import com.takeout.takeoutmodel.entity.AddressInfo;
import com.takeout.takeoutmodel.entity.Shop;
import com.takeout.takeoutmodel.entity.User;
import com.takeout.takeoutmodel.enums.UserRoleEnum;
import com.takeout.takeoutmodel.request.AddShopRequest;
import com.takeout.takeoutmodel.vo.AddressInfoVO;
import com.takeout.takeoutmodel.vo.ShopVO;
import com.takeout.takeoutshopservice.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class ShopController {

    @Resource
    private ShopService shopService;

    @GetMapping("/test")
    public BaseResponse<Shop> test() {
        log.info("测试成功");
        shopService.test();
        return ResultUtils.success(null);
    }

    /**
     * todo 上传店铺图片
     * @return
     */
    @PostMapping("/upload")
    public BaseResponse<String> uploadShopAvatar() {
        return null;
    }

    /**
     * 添加店铺
     * @param addShopRequest
     * @param request
     * @return
     */
    @PostMapping("/addShop")
    public BaseResponse<ShopVO> addShop(@RequestBody AddShopRequest addShopRequest, HttpServletRequest request) {
        if (addShopRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Shop shop = shopService.addShop(addShopRequest, request);
        return ResultUtils.success(getShopVO(shop));
    }

    /**
     * 这里是获取店主的 shop 信息
     *
     * @param request
     * @return
     */
    @GetMapping("/getShop")
    @AuthCheck(haveRole = "shop")
    public BaseResponse<ShopVO> getShop(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if (!Objects.equals(loginUser.getUserRole(), UserRoleEnum.SHOPKEEPER.getValue())) {
            throw new BusinessException(ErrorCode.NO_AUTH, "您未开通店铺");
        }
        Shop shop = shopService.getById(loginUser.getShopId());
        return ResultUtils.success(getShopVO(shop));
    }

//    /**
//     * 主页获取店铺信息 todo 待删除
//     * todo 需要进行分页返回分页的数据
//     *
//     * @return
//     */
//    @GetMapping("/getAllShops")
//    public BaseResponse<List<ShopVO>> getAllShops() {
//        List<Shop> shopList = shopService.list();
//        List<ShopVO> shopVOList = shopList.stream().filter(shop -> shop.getShopStatus() == 0).map(this::getShopVO).collect(Collectors.toList());
//        return ResultUtils.success(shopVOList);
//    }

    /**
     * 主页获取店铺信息
     * todo 按条件排序
     * @return
     */
    @PostMapping("/getAllShops")
    public BaseResponse<Page<ShopVO>> getAllShopsByPage(@RequestBody PageRequest pageRequest) {
        if(pageRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = pageRequest.getCurrent();
        long pageSize = pageRequest.getPageSize();
        QueryWrapper<Shop> queryWrapper = new QueryWrapper<>();
        // 只能查询已经审核完毕的店铺
        queryWrapper.eq("shopStatus", 0);
        Page<Shop> shopPage = shopService.page(new Page<>(current, pageSize), queryWrapper);
        return ResultUtils.success(shopService.getShopPageVO(shopPage));
    }

    /**
     * 按名称搜索店铺
     * todo 按条件排序
     * @param searchShopName
     * @return
     */
    @PostMapping("/searchByName")
    public BaseResponse<Page<ShopVO>> searchShopByName(@RequestParam("searchShopName") String searchShopName, @RequestBody PageRequest pageRequest) {
        if (StrUtil.isBlank(searchShopName) || pageRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<Shop> shopPage = shopService.searchShopByName(searchShopName, pageRequest);
        return ResultUtils.success(shopService.getShopPageVO(shopPage));
    }

    /**
     * 按标签搜索店铺
     * todo 按条件排序
     * @param tag
     * @return
     */
    @PostMapping("/searchByTag")
    public BaseResponse<Page<ShopVO>> searchShopByTag(@RequestParam("tag") Integer tag, @RequestBody PageRequest pageRequest) {
        if (Objects.isNull(tag) || pageRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<Shop> shopPage = shopService.searchShopByTag(tag, pageRequest);
        return ResultUtils.success(shopService.getShopPageVO(shopPage));
    }

    /**
     * 根据店主的用户ID获取到店铺的地址信息
     * @param userId
     * @return
     */
    @GetMapping("/getShopAddress")
    public BaseResponse<AddressInfoVO> getShopAddress(@RequestParam("userId") Long userId){
        if(Objects.isNull(userId)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AddressInfo addressInfo = shopService.getAddress(userId);
        AddressInfoVO addressInfoVO = new AddressInfoVO();
        BeanUtil.copyProperties(addressInfo, addressInfoVO);
        return ResultUtils.success(addressInfoVO);
    }

    /**
     * 获取所有待审核的店铺
     * todo 按时间排序
     * @return
     */
    @PostMapping("/getShopToBeAudited")
    @AuthCheck(haveRole = "admin")
    public BaseResponse<Page<ShopVO>> getShopToBeAudited(@RequestBody PageRequest pageRequest){
        if(pageRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<Shop> shopPage = shopService.getShopToBeAudited(pageRequest);
        return ResultUtils.success(shopService.getShopPageVO(shopPage));
    }

    /**
     * 返回所有店铺，包括被删除掉的
     * todo 按条件排序
     * @return
     */
    @PostMapping("/getHistoryShops")
    @AuthCheck(haveRole = "admin")
    public BaseResponse<Page<ShopVO>> getHistoryShops(@RequestBody PageRequest pageRequest){
        if(pageRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<Shop> shopList = shopService.getHistoryShops(pageRequest);
        return ResultUtils.success(shopService.getShopPageVO(shopList));
    }

    /**
     * 审核店铺
     * @param id
     * @return
     */
    @PutMapping("auditShop")
    @AuthCheck(haveRole = "admin")
    public BaseResponse<Integer> auditedShop(@RequestParam("id") Long id, @RequestParam("isPass") Boolean isPass){
        if(Objects.isNull(id)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int audit = shopService.auditShop(id, isPass);
        return ResultUtils.success(audit);
    }

    private ShopVO getShopVO(Shop originShop) {
        if (originShop == null) {
            return null;
        }
        ShopVO shopVO = new ShopVO();
        BeanUtil.copyProperties(originShop, shopVO);
        return shopVO;
    }
}

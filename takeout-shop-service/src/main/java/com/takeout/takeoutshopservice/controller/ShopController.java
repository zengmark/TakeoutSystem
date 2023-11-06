package com.takeout.takeoutshopservice.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.takeout.takeoutcommon.annotation.AuthCheck;
import com.takeout.takeoutcommon.common.BaseResponse;
import com.takeout.takeoutcommon.common.ErrorCode;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/addshop")
    public BaseResponse<ShopVO> addShop(AddShopRequest addShopRequest, HttpServletRequest request) {
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
    @AuthCheck(haveRole = "shop")
    @GetMapping("/getShop")
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

    /**
     * 主页获取店铺信息
     * todo 需要进行分页返回分页的数据
     *
     * @return
     */
    @GetMapping("/getAllShops")
    public BaseResponse<List<ShopVO>> getAllShops() {
        List<Shop> shopList = shopService.list();
        List<ShopVO> shopVOList = shopList.stream().map(this::getShopVO).collect(Collectors.toList());
        return ResultUtils.success(shopVOList);
    }

    /**
     * 按名称搜索店铺
     * @param searchShopName
     * @return
     */
    @GetMapping("/searchByName")
    public BaseResponse<List<ShopVO>> searchShopByName(@RequestParam("searchShopName") String searchShopName) {
        if (StrUtil.isBlank(searchShopName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Shop> shopList = shopService.searchShopByName(searchShopName);
        List<ShopVO> shopVOList = shopList.stream().map(this::getShopVO).collect(Collectors.toList());
        return ResultUtils.success(shopVOList);
    }

    /**
     * 按标签搜索店铺
     * @param tag
     * @return
     */
    @GetMapping("/searchByTag")
    public BaseResponse<List<ShopVO>> searchShopByTag(@RequestParam("tag") Integer tag) {
        if (Objects.isNull(tag)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Shop> shopList = shopService.searchShopByTag(tag);
        List<ShopVO> shopVOList = shopList.stream().map(this::getShopVO).collect(Collectors.toList());
        return ResultUtils.success(shopVOList);
    }

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

    private ShopVO getShopVO(Shop originShop) {
        if (originShop == null) {
            return null;
        }
        ShopVO shopVO = new ShopVO();
        BeanUtil.copyProperties(originShop, shopVO);
        return shopVO;
    }
}

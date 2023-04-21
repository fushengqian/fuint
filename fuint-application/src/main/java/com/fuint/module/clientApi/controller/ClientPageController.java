package com.fuint.module.clientApi.controller;

import com.fuint.common.dto.UserInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.BannerService;
import com.fuint.common.service.CouponService;
import com.fuint.common.service.GoodsService;
import com.fuint.common.service.SettingService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtBanner;
import com.fuint.repository.model.MtGoods;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 页面接口controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-页面相关接口")
@RestController
@RequestMapping(value = "/clientApi/page")
public class ClientPageController extends BaseController {

    /**
     * 商品服务接口
     * */
    @Autowired
    private GoodsService goodsService;

    /**
     * 卡券服务接口
     */
    @Autowired
    private CouponService couponService;

    /**
     * Banner服务接口
     * */
    @Autowired
    private BannerService bannerService;

    @Autowired
    private SettingService settingService;

    /**
     * 获取页面数据
     */
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getPageData(HttpServletRequest request, @RequestParam Map<String, Object> param) throws BusinessCheckException {
        Integer storeId = request.getHeader("storeId") == null ? 0 : Integer.parseInt(request.getHeader("storeId"));

        String token = request.getHeader("Access-Token");
        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);
        if (null != mtUser) {
            param.put("userId", mtUser.getId());
        }

        param.put("status", StatusEnum.ENABLED.getKey());
        ResponseObject couponData = couponService.findCouponList(param);

        Map<String, Object> bannerParam = new HashMap<>();
        bannerParam.put("status", StatusEnum.ENABLED.getKey());
        if (storeId > 0) {
            bannerParam.put("storeId", storeId);
        }
        List<MtBanner> bannerData = bannerService.queryBannerListByParams(bannerParam);

        List<MtGoods> goodsData = goodsService.getStoreGoodsList(storeId, "");
        String baseImage = settingService.getUploadBasePath();
        if (goodsData.size() > 0) {
            for (MtGoods goods : goodsData) {
                 goods.setLogo(baseImage + goods.getLogo());
            }
        }

        Map<String, Object> outParams = new HashMap();
        outParams.put("banner", bannerData);
        outParams.put("coupon", couponData.getData());
        outParams.put("goods", goodsData);

        ResponseObject responseObject = getSuccessResult(outParams);

        return getSuccessResult(responseObject.getData());
    }
}

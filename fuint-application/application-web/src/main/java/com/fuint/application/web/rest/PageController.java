package com.fuint.application.web.rest;

import com.fuint.application.dao.entities.MtBanner;
import com.fuint.application.dao.entities.MtGoods;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.goods.GoodsService;
import com.fuint.application.service.token.TokenService;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.ResponseObject;
import com.fuint.application.BaseController;
import com.fuint.application.service.banner.BannerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 页面接口controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/rest/page")
public class PageController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(PageController.class);

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

    /**
     * Token服务接口
     */
    @Autowired
    private TokenService tokenService;

    @Autowired
    private Environment env;

    /**
     * 获取页面数据
     */
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getPageData(HttpServletRequest request, @RequestParam Map<String, Object> param) throws BusinessCheckException {
        Integer storeId = request.getHeader("storeId") == null ? 0 : Integer.parseInt(request.getHeader("storeId"));

        String token = request.getHeader("Access-Token");
        MtUser mtUser = tokenService.getUserInfoByToken(token);
        if (null != mtUser) {
            param.put("userId", mtUser.getId());
        }

        param.put("EQ_status", StatusEnum.ENABLED.getKey());
        ResponseObject couponData = couponService.findCouponList(param);
        List<MtBanner> bannerData = bannerService.queryBannerListByParams(param);

        List<MtGoods> goodsData = goodsService.getStoreGoodsList(storeId);
        String baseImage = env.getProperty("images.upload.url");
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

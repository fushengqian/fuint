package com.fuint.application.web.rest;

import com.fuint.application.dao.entities.MtBanner;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.token.TokenService;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.ResponseObject;
import com.fuint.application.BaseController;
import com.fuint.application.service.banner.BannerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 页面接口controller
 * Created by zach on 2021/4/12.
 */
@RestController
@RequestMapping(value = "/rest/page")
public class PageController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(PageController.class);

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

    /**
     * 获取页面数据
     */
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getPageData(HttpServletRequest request, @RequestParam Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        MtUser mtUser = tokenService.getUserInfoByToken(token);
        if (null != mtUser) {
            param.put("userId", mtUser.getId());
        }

        Map<String, Object> outParams = new HashMap();

        ResponseObject couponData = couponService.findCouponList(param);
        List<MtBanner> bannerData = bannerService.queryBannerListByParams(param);

        outParams.put("banner", bannerData);
        outParams.put("coupon", couponData.getData());

        ResponseObject responseObject = getSuccessResult(outParams);

        return getSuccessResult(responseObject.getData());
    }
}

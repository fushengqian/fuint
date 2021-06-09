package com.fuint.application.web.rest;

import com.fuint.application.dao.entities.MtCoupon;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.dto.CouponDto;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.token.TokenService;
import com.fuint.application.service.usercoupon.UserCouponService;
import com.fuint.application.util.DateUtil;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.ResponseObject;
import com.fuint.application.BaseController;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * 卡券接口controller
 * Created by zach on 2021/4/22.
 * updated by zach on 2021/5/1.
 */
@RestController
@RequestMapping(value = "/rest/coupon")
public class CouponApiController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(CouponApiController.class);

    /**
     * 卡券服务接口
     */
    @Autowired
    private CouponService couponService;

    /**
     * 会员卡券服务接口
     * */
    @Autowired
    private UserCouponService userCouponService;

    /**
     * Token服务接口
     */
    @Autowired
    private TokenService tokenService;

    /**
     * 获取列表数据
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getListData(HttpServletRequest request, @RequestParam Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        MtUser mtUser = tokenService.getUserInfoByToken(token);
        if (null != mtUser) {
            param.put("userId", mtUser.getId());
        }

        Map<String, Object> outParams = new HashMap();

        ResponseObject couponData = couponService.findCouponList(param);
        outParams.put("coupon", couponData.getData());

        ResponseObject responseObject = getSuccessResult(outParams);

        return getSuccessResult(responseObject.getData());
    }

    /**
     * 领取卡券
     * */
    @RequestMapping(value = "/receive", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject receive(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        MtUser mtUser = tokenService.getUserInfoByToken(token);
        if (null != mtUser) {
            param.put("userId", mtUser.getId());
        } else {
            return getFailureResult(1001);
        }

        try {
            userCouponService.receiveCoupon(param);
        } catch (BusinessCheckException e) {
            return getFailureResult(1006, e.getMessage());
        }

        // 组织返回参数
        Map<String, Object> result = new HashMap<>();
        result.put("result", true);

        return getSuccessResult(result);
    }

    /**
     * 查询卡券详情
     *
     * @param param  Request对象
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request, @RequestParam Map<String, Object> param) throws BusinessCheckException, InvocationTargetException, IllegalAccessException {
        String token = request.getHeader("Access-Token");
        Integer couponId = param.get("couponId") == null ? 0 : Integer.parseInt(param.get("couponId").toString());

        MtCoupon couponInfo = couponService.queryCouponById(couponId.longValue());

        CouponDto dto = new CouponDto();

        BeanUtils.copyProperties(dto, couponInfo);

        String effectiveDate = DateUtil.formatDate(couponInfo.getBeginTime(), "yyyy.MM.dd") + " - " + DateUtil.formatDate(couponInfo.getEndTime(), "yyyy.MM.dd");
        dto.setEffectiveDate(effectiveDate);
        dto.setGotNum(10093);
        dto.setLimitNum(1092);

        return getSuccessResult(dto);
    }
}

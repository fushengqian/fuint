package com.fuint.coupon.web.rest;

import com.fuint.exception.BusinessCheckException;
import com.fuint.coupon.dao.entities.MtUserCoupon;
import com.fuint.coupon.service.coupon.CouponService;
import com.fuint.coupon.service.token.TokenService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import com.fuint.coupon.BaseController;
import com.fuint.coupon.ResponseObject;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fuint.coupon.dao.entities.MtUser;
import com.fuint.coupon.enums.UserCouponStatusEnum;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**-
 * 我的卡券controller
 * Created by zach on 2019/8/26.
 */
@RestController
@RequestMapping(value = "/rest/myCoupon")
public class MyCouponController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(MyCouponController.class);

    /**
     * 优惠券服务接口
     */
    @Autowired
    private CouponService couponService;

    /**
     * Token服务接口
     */
    @Autowired
    private TokenService tokenService;

    /**
     * 查询我的优惠券
     *
     * @param param  Request对象
     */
    @RequestMapping(value = "/doQuery", method = RequestMethod.GET)
    public ResponseObject doQuery(HttpServletRequest request, @RequestParam Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("token");

        if (StringUtils.isEmpty(token)) {
            return getFailureResult(1001);
        }

        MtUser mtUser = tokenService.getUserInfoByToken(token);

        if (null == mtUser) {
            return getFailureResult(1001);
        }

        param.put("userId", mtUser.getId());

        ResponseObject result = couponService.findMyCouponList(param);

        return getSuccessResult(result.getData());
    }

    /**
     * 查询我的优惠券是否已使用
     *
     * @param param  Request对象
     */
    @RequestMapping(value = "/isUsed", method = RequestMethod.GET)
    public ResponseObject isUsed(HttpServletRequest request, @RequestParam Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("token");
        Integer userCouponId = param.get("id") == null ? 0 : Integer.parseInt(param.get("id").toString());

        if (StringUtils.isEmpty(token)) {
            return getFailureResult(1001);
        }

        MtUser mtUser = tokenService.getUserInfoByToken(token);

        if (null == mtUser) {
            return getFailureResult(1001);
        }

        MtUserCoupon userCoupon = couponService.queryUserCouponById(userCouponId);
        if (userCoupon.getStatus().equals(UserCouponStatusEnum.USED.getKey()) && mtUser.getId() .equals(userCoupon.getUserId())) {
            return getSuccessResult(true);
        } else {
            return getSuccessResult(false);
        }
    }
}

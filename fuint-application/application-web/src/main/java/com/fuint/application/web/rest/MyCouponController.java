package com.fuint.application.web.rest;

import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.MtUserCoupon;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.token.TokenService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.enums.UserCouponStatusEnum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**-
 * 我的卡券controller
 * Created by zach on 2019/08/26.
 * Updated by zach on 2021/04/23.
 */
@RestController
@RequestMapping(value = "/rest/myCoupon")
public class MyCouponController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(MyCouponController.class);

    /**
     * 卡券服务接口
     */
    @Autowired
    private CouponService couponService;

    /**
     * Token服务接口
     */
    @Autowired
    private TokenService tokenService;

    /**
     * 查询我的卡券
     *
     * @param request  Request对象
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request, HttpServletResponse response) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String status = request.getParameter("status") == null ? "" : request.getParameter("status").toString();
        String type = request.getParameter("type") == null ? "" : request.getParameter("type").toString();

        if (StringUtils.isEmpty(token)) {
            return getFailureResult(1001);
        }

        MtUser mtUser = tokenService.getUserInfoByToken(token);
        if (null == mtUser) {
            return getFailureResult(1001);
        }

        Map<String, Object> param = new HashMap<>();

        param.put("userId", mtUser.getId());
        param.put("status", status);
        param.put("type", type);

        ResponseObject result = couponService.findMyCouponList(param);

        return getSuccessResult(result.getData());
    }

    /**
     * 查询我的卡券是否已使用
     *
     * @param param  Request对象
     */
    @RequestMapping(value = "/isUsed", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject isUsed(HttpServletRequest request, @RequestParam Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
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

package com.fuint.module.clientApi.controller;

import com.fuint.common.dto.CouponDto;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.param.CouponInfoParam;
import com.fuint.common.param.CouponListParam;
import com.fuint.common.param.CouponReceiveParam;
import com.fuint.common.service.CouponService;
import com.fuint.common.service.SettingService;
import com.fuint.common.service.UserCouponService;
import com.fuint.common.util.DateUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.MtUserCouponMapper;
import com.fuint.repository.model.MtCoupon;
import com.fuint.repository.model.MtUserCoupon;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * 卡券接口controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-卡券相关接口")
@RestController
@RequestMapping(value = "/clientApi/coupon")
public class ClientCouponController extends BaseController {

    @Resource
    private MtUserCouponMapper mtUserCouponMapper;

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
     * 系统设置服务接口
     * */
    @Autowired
    private SettingService settingService;

    /**
     * 获取卡券列表数据
     */
    @ApiOperation(value = "获取卡券列表数据")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request, @RequestBody CouponListParam couponListParam) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);
        if (null != mtUser) {
            couponListParam.setUserId(mtUser.getId());
        }

        Map<String, Object> outParams = new HashMap();

        ResponseObject couponData = couponService.findCouponList(couponListParam);
        outParams.put("coupon", couponData.getData());

        ResponseObject responseObject = getSuccessResult(outParams);

        return getSuccessResult(responseObject.getData());
    }

    /**
     * 领取卡券
     * */
    @ApiOperation(value = "领取卡券")
    @RequestMapping(value = "/receive", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject receive(HttpServletRequest request, @RequestBody CouponReceiveParam couponReceiveParam) {
        String token = request.getHeader("Access-Token");
        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);

        if (null != mtUser) {
            couponReceiveParam.setUserId(mtUser.getId());
        } else {
            return getFailureResult(1001);
        }

        try {
            userCouponService.receiveCoupon(couponReceiveParam);
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
     * @param couponInfoParam Request对象
     */
    @ApiOperation(value = "查询卡券详情")
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request, @RequestBody CouponInfoParam couponInfoParam) throws BusinessCheckException, InvocationTargetException, IllegalAccessException {
        String token = request.getHeader("Access-Token");
        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);

        Integer couponId = couponInfoParam.getCouponId() == null ? 0 : couponInfoParam.getCouponId();
        String userCouponCode = couponInfoParam.getUserCouponCode() == null ? "" : couponInfoParam.getUserCouponCode();

        MtCoupon couponInfo = new MtCoupon();
        if (StringUtil.isNotEmpty(userCouponCode)) {
            MtUserCoupon userCouponInfo = mtUserCouponMapper.findByCode(userCouponCode);
            if (userCouponInfo != null) {
                couponInfo = couponService.queryCouponById(userCouponInfo.getCouponId());
            }
        } else {
            couponInfo = couponService.queryCouponById(couponId);
        }

        if (couponInfo == null) {
            return getFailureResult(201);
        }

        CouponDto couponDto = new CouponDto();
        BeanUtils.copyProperties(couponInfo, couponDto);
        couponDto.setIsReceive(false);

        // 是否需要领取码
        if (couponInfo.getReceiveCode() != null && StringUtil.isNotEmpty(couponInfo.getReceiveCode())) {
            couponDto.setNeedReceiveCode(true);
        } else {
            couponDto.setNeedReceiveCode(false);
        }

        if (null != mtUser) {
            List<MtUserCoupon> userCoupon = userCouponService.getUserCouponDetail(mtUser.getId(), couponId);
            if (userCoupon.size() >= couponInfo.getLimitNum() && couponInfo.getLimitNum() > 0) {
                couponDto.setIsReceive(true);
                couponDto.setUserCouponId(userCoupon.get(0).getId());
            }
        }

        String baseImage = settingService.getUploadBasePath();
        couponDto.setImage(baseImage + couponInfo.getImage());
        String effectiveDate = DateUtil.formatDate(couponInfo.getBeginTime(), "yyyy.MM.dd") + " - " + DateUtil.formatDate(couponInfo.getEndTime(), "yyyy.MM.dd");
        couponDto.setEffectiveDate(effectiveDate);
        couponDto.setGotNum(0);
        couponDto.setLimitNum(0);

        return getSuccessResult(couponDto);
    }
}

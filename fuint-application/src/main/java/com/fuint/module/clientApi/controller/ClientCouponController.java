package com.fuint.module.clientApi.controller;

import com.fuint.common.dto.CouponDto;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.enums.CouponExpireTypeEnum;
import com.fuint.common.param.CouponInfoParam;
import com.fuint.common.param.CouponListParam;
import com.fuint.common.param.CouponReceiveParam;
import com.fuint.common.service.*;
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
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 卡券接口controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-卡券相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/coupon")
public class ClientCouponController extends BaseController {

    private MtUserCouponMapper mtUserCouponMapper;

    /**
     * 卡券服务接口
     */
    private CouponService couponService;

    /**
     * 会员卡券服务接口
     * */
    private UserCouponService userCouponService;

    /**
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 商户服务接口
     */
    private MerchantService merchantService;

    /**
     * 店铺接口
     */
    private StoreService storeService;

    @ApiOperation(value = "获取卡券列表数据")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request, @RequestBody CouponListParam params) throws BusinessCheckException {
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        UserInfo mtUser = TokenUtil.getUserInfo();
        if (null != mtUser) {
            params.setUserId(mtUser.getId());
        }

        Map<String, Object> outParams = new HashMap();
        Integer merchantId = merchantService.getMerchantId(merchantNo);
        params.setMerchantId(merchantId);
        ResponseObject couponData = couponService.findCouponList(params);
        outParams.put("coupon", couponData.getData());

        ResponseObject responseObject = getSuccessResult(outParams);
        return getSuccessResult(responseObject.getData());
    }

    @ApiOperation(value = "领取卡券")
    @RequestMapping(value = "/receive", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject receive(@RequestBody CouponReceiveParam couponReceiveParam) throws BusinessCheckException {
        UserInfo mtUser = TokenUtil.getUserInfo();
        if (null != mtUser) {
            couponReceiveParam.setUserId(mtUser.getId());
        } else {
            return getFailureResult(1001);
        }

        userCouponService.receiveCoupon(couponReceiveParam);

        // 组织返回参数
        Map<String, Object> result = new HashMap<>();
        result.put("result", true);

        return getSuccessResult(result);
    }

    @ApiOperation(value = "查询卡券详情")
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject detail(@RequestBody CouponInfoParam params) throws BusinessCheckException, InvocationTargetException, IllegalAccessException {
        UserInfo mtUser = TokenUtil.getUserInfo();

        Integer couponId = params.getCouponId() == null ? 0 : params.getCouponId();
        String userCouponCode = params.getUserCouponCode() == null ? "" : params.getUserCouponCode();

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

        // 适用店铺
        String storeNames = storeService.getStoreNames(couponInfo.getStoreIds());
        couponDto.setStoreNames(storeNames);

        String baseImage = settingService.getUploadBasePath();
        couponDto.setImage(baseImage + couponInfo.getImage());
        String effectiveDate = "";
        if (couponInfo.getExpireType().equals(CouponExpireTypeEnum.FIX.getKey())) {
            effectiveDate = DateUtil.formatDate(couponInfo.getBeginTime(), "yyyy.MM.dd HH:mm") + " - " + DateUtil.formatDate(couponInfo.getEndTime(), "yyyy.MM.dd");
        } else if(couponInfo.getExpireType().equals(CouponExpireTypeEnum.FLEX.getKey())) {
            effectiveDate = "领取后" + couponInfo.getExpireTime() + "天内有效";
        } else {
            effectiveDate = DateUtil.formatDate(couponInfo.getCreateTime(), "yyyy.MM.dd HH:mm") + " - 永久";
        }
        couponDto.setEffectiveDate(effectiveDate);
        couponDto.setGotNum(0);
        couponDto.setLimitNum(0);

        return getSuccessResult(couponDto);
    }
}

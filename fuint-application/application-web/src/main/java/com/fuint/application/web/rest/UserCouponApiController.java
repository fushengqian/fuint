package com.fuint.application.web.rest;

import com.fuint.application.dto.UserCouponDto;
import com.fuint.application.util.DateUtil;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.MtCoupon;
import com.fuint.application.dao.entities.MtUserCoupon;
import com.fuint.application.dao.repositories.MtUserCouponRepository;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.token.TokenService;
import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.util.QRCodeUtil;
import com.fuint.application.util.Base64Util;
import com.fuint.application.util.SeqUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 会员卡券controller
 * Created by zach on 2020/08/18.
 * Updated by zach on 2021/04/29.
 */
@RestController
@RequestMapping(value = "/rest/userCouponApi")
public class UserCouponApiController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(UserCouponApiController.class);

    @Autowired
    private MtUserCouponRepository userCouponRepository;

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

    @Autowired
    private Environment env;

    /**
     * 查询会员卡券详情
     *
     * @param param  Request对象
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request, @RequestParam Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer userCouponId = param.get("userCouponId") == null ? 0 : Integer.parseInt(param.get("userCouponId").toString());
        int width = param.get("width") == null ? 800 : Integer.parseInt(param.get("width").toString());
        int height = param.get("height") == null ? 800 : Integer.parseInt(param.get("height").toString());

        // 参数有误
        if (userCouponId <= 0) {
            return getFailureResult(1002);
        }

        if (StringUtils.isEmpty(token)) {
            return getFailureResult(1001);
        }

        MtUser mtUser = tokenService.getUserInfoByToken(token);
        if (null == mtUser) {
            return getFailureResult(1001);
        }

        MtUserCoupon userCoupon = userCouponRepository.findOne(userCouponId);
        if (!mtUser.getId().equals(userCoupon.getUserId())) {
            return getFailureResult(1004);
        }

        MtCoupon couponInfo = couponService.queryCouponById(userCoupon.getCouponId().longValue());
        if (null == couponInfo) {
            return getFailureResult(1002);
        }

        ByteArrayOutputStream out = null;
        ResponseObject responseObject;
        try {
            // 如果超过两小时，重新生成code
            String rCode = userCoupon.getCode();
            if (couponService.codeExpired(rCode) && userCoupon.getStatus().equals("A")) {
                StringBuffer code = new StringBuffer();
                code.append(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                code.append(SeqUtil.getRandomNumber(15));

                userCoupon.setCode(code.toString());
                userCoupon.setUpdateTime(new Date());
                userCouponRepository.save(userCoupon);

                rCode = code.toString();
            }

            String website = env.getProperty("website.url");
            String content  = website + "/index.html#/result?code=" + rCode +"&time=" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

            // 生成并输出二维码
            out = new ByteArrayOutputStream();
            QRCodeUtil.createQrCode(out, content, width, height, "png", "");

            // 对数据进行Base64编码，返回的码需加上：data:image/jpg;base64
            String qrCode = new String(Base64Util.baseEncode(out.toByteArray()), "UTF-8");
            qrCode = "data:image/jpg;base64," + qrCode;

            UserCouponDto result = new UserCouponDto();
            result.setName(couponInfo.getName());
            result.setQrCode(qrCode);
            result.setId(userCouponId);
            result.setDescription(couponInfo.getDescription());
            result.setCouponId(couponInfo.getId());

            String effectiveDate = DateUtil.formatDate(couponInfo.getBeginTime(), "yyyy.MM.dd") + " - " + DateUtil.formatDate(couponInfo.getEndTime(), "yyyy.MM.dd");

            result.setEffectiveDate(effectiveDate);
            result.setCode(userCoupon.getCode());
            result.setAmount(userCoupon.getAmount());
            result.setBalance(userCoupon.getBalance());

            responseObject = getSuccessResult(result);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            responseObject = getCustomrResult(500, "生成二维码异常", "");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        return getSuccessResult(responseObject.getData());
    }
}

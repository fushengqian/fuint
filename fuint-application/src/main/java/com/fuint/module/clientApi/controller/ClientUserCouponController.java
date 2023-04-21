package com.fuint.module.clientApi.controller;

import com.fuint.common.dto.UserCouponDto;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.enums.CouponTypeEnum;
import com.fuint.common.enums.UserCouponStatusEnum;
import com.fuint.common.service.*;
import com.fuint.common.util.DateUtil;
import com.fuint.common.util.QRCodeUtil;
import com.fuint.common.util.SeqUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.common.util.Base64Util;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.MtUserCouponMapper;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 会员卡券controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-会员卡券相关接口")
@RestController
@RequestMapping(value = "/clientApi/userCouponApi")
public class ClientUserCouponController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ClientUserCouponController.class);

    @Resource
    private MtUserCouponMapper mtUserCouponMapper;

    @Autowired
    private CouponService couponService;

    @Autowired
    private ConfirmLogService confirmLogService;

    @Autowired
    private StaffService staffService;

    @Autowired
    private SettingService settingService;

    /**
     * 会员服务接口
     */
    @Autowired
    private MemberService memberService;

    /**
     * 查询会员卡券详情
     *
     * @param param  Request对象
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request, @RequestParam Map<String, Object> param) throws BusinessCheckException, InvocationTargetException, IllegalAccessException {
        String token = request.getHeader("Access-Token");
        Integer userCouponId = param.get("userCouponId") == null ? 0 : Integer.parseInt(param.get("userCouponId").toString());
        String userCouponCode = param.get("userCouponCode") == null ? "" : param.get("userCouponCode").toString();

        int width = param.get("width") == null ? 800 : Integer.parseInt(param.get("width").toString());
        int height = param.get("height") == null ? 800 : Integer.parseInt(param.get("height").toString());

        // 参数有误
        if (userCouponId <= 0 && StringUtil.isEmpty(userCouponCode)) {
            return getFailureResult(1004);
        }

        if (StringUtil.isEmpty(token)) {
            return getFailureResult(1001);
        }

        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);
        if (null == mtUser) {
            return getFailureResult(1001);
        }

        MtUser loginInfo = memberService.queryMemberById(mtUser.getId());
        MtUserCoupon userCoupon;
        if (userCouponId > 0) {
            userCoupon = mtUserCouponMapper.selectById(userCouponId);
        } else {
            userCoupon = mtUserCouponMapper.findByCode(userCouponCode);
        }

        if (!mtUser.getId().equals(userCoupon.getUserId())) {
            MtStaff confirmInfo = staffService.queryStaffByMobile(loginInfo.getMobile());
            if (null == confirmInfo) {
                return getFailureResult(1004);
            }
        }

        MtCoupon couponInfo = couponService.queryCouponById(userCoupon.getCouponId());
        if (null == couponInfo) {
            return getFailureResult(1002);
        }

        ByteArrayOutputStream out = null;
        ResponseObject responseObject;
        try {
            // 如果超时，重新生成code
            String rCode = userCoupon.getCode();
            if (couponService.codeExpired(rCode) && userCoupon.getStatus().equals(UserCouponStatusEnum.UNUSED.getKey())) {
                StringBuffer code = new StringBuffer();
                code.append(new SimpleDateFormat("yyMMddHHmmss").format(new Date()));
                code.append(SeqUtil.getRandomNumber(6));
                userCoupon.setCode(code.toString());
                userCoupon.setUpdateTime(new Date());
                mtUserCouponMapper.updateById(userCoupon);
            }

            String content = userCoupon.getCode();

            // 生成并输出二维码
            out = new ByteArrayOutputStream();
            QRCodeUtil.createQrCode(out, content, width, height, "png", "");

            // 对数据进行Base64编码
            String qrCode = new String(Base64Util.baseEncode(out.toByteArray()), "UTF-8");
            qrCode = "data:image/jpg;base64," + qrCode;

            UserCouponDto result = new UserCouponDto();
            result.setName(couponInfo.getName());
            result.setQrCode(qrCode);

            String baseImage = settingService.getUploadBasePath();
            result.setImage(baseImage + couponInfo.getImage());

            result.setId(userCouponId);
            result.setDescription(couponInfo.getDescription());
            result.setCouponId(couponInfo.getId());
            result.setType(couponInfo.getType());
            result.setUseRule(couponInfo.getOutRule());
            String effectiveDate = DateUtil.formatDate(couponInfo.getBeginTime(), "yyyy.MM.dd") + " - " + DateUtil.formatDate(couponInfo.getEndTime(), "yyyy.MM.dd");
            result.setEffectiveDate(effectiveDate);
            result.setCode(userCoupon.getCode());
            result.setAmount(userCoupon.getAmount());
            result.setBalance(userCoupon.getBalance());
            result.setStatus(userCoupon.getStatus());
            result.setIsGive(couponInfo.getIsGive());

            // 如果是计次卡，获取核销列表
            if (couponInfo.getType().equals(CouponTypeEnum.TIMER.getKey())) {
                if (userCouponId <= 0 && StringUtil.isNotEmpty(userCouponCode)) {
                    userCouponId = userCoupon.getId();
                }
                List<MtConfirmLog> confirmLogs = confirmLogService.getConfirmList(userCouponId);
                Long confirmCount = confirmLogService.getConfirmNum(userCouponId);
                result.setConfirmCount(confirmCount.intValue());
                result.setConfirmLogs(confirmLogs);
            }

            responseObject = getSuccessResult(result);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            responseObject = getSuccessResult(201, "生成二维码异常", "");
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

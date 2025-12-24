package com.fuint.module.clientApi.controller;

import com.alibaba.fastjson.JSONObject;
import com.fuint.common.dto.AssetDto;
import com.fuint.common.dto.UserDto;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.enums.*;
import com.fuint.common.service.*;
import com.fuint.common.util.Base64Util;
import com.fuint.common.util.DateUtil;
import com.fuint.common.util.QRCodeUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.module.clientApi.request.MemberInfoRequest;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-会员相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/user")
public class ClientUserController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ClientUserController.class);

    /**
     * 会员服务接口
     * */
    private MemberService memberService;

    /**
     * 会员卡券服务接口
     * */
    private UserCouponService userCouponService;

    /**
     * 员工服务接口
     * */
    private StaffService staffService;

    /**
     * 卡券服务接口
     * */
    private CouponService couponService;

    /**
     * 会员等级服务接口
     **/
    private UserGradeService userGradeService;

    /**
     * 系统配置服务接口
     * */
    private SettingService settingService;

    /**
     * 微信服务接口
     * */
    private WeixinService weixinService;

    /**
     * 商户服务接口
     */
    private MerchantService merchantService;

    /**
     * 验证码接口
     */
    private VerifyCodeService verifyCodeService;

    /**
     * 获取会员信息
     */
    @ApiOperation(value = "获取会员信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request) throws BusinessCheckException {
        String isWechat = request.getHeader("isWechat") == null ? YesOrNoEnum.NO.getKey() : request.getHeader("isWechat");
        String platform = request.getHeader("platform") == null ? "" : request.getHeader("platform");
        String userNo = request.getParameter("code") == null ? "" : request.getParameter("code");
        UserInfo loginInfo = TokenUtil.getUserInfo();
        Integer merchantId = merchantService.getMerchantId(request.getHeader("merchantNo"));
        MtUser mtUser = null;
        if (loginInfo != null) {
            mtUser = memberService.queryMemberById(loginInfo.getId());
        }
        if (StringUtil.isNotEmpty(userNo)) {
            mtUser = memberService.queryMemberByUserNo(merchantId, userNo);
        }
        MtUserGrade gradeInfo = null;
        if (mtUser != null) {
            gradeInfo = memberService.queryMemberGradeByGradeId(mtUser.getGradeId());
        }

        List<MtUserGrade> memberGrade = userGradeService.getPayUserGradeList(merchantId, mtUser);
        Map<String, Object> outParams = new HashMap<>();

        UserDto userInfo = null;
        if (mtUser != null) {
            userInfo = new UserDto();
            BeanUtils.copyProperties(mtUser, userInfo);
            if (StringUtil.isNotEmpty(mtUser.getPassword())) {
                userInfo.setHasPassword(YesOrNoEnum.YES.getKey());
            } else {
                userInfo.setHasPassword(YesOrNoEnum.NO.getKey());
            }
        }

        outParams.put("userInfo", userInfo);
        outParams.put("gradeInfo", gradeInfo);
        outParams.put("memberGrade", memberGrade);

        // 会员到期时间
        String gradeEndTime = "";
        if (mtUser != null) {
            if (mtUser.getEndTime() != null) {
                gradeEndTime = DateUtil.formatDate(mtUser.getEndTime(), "yyyy.MM.dd HH:mm");
            }
        }
        outParams.put("gradeEndTime", gradeEndTime);

        // 是否店铺员工
        boolean isMerchant = false;
        if (mtUser != null) {
            if (mtUser.getMobile() != null && StringUtil.isNotEmpty(mtUser.getMobile())) {
                MtStaff staffInfo = staffService.queryStaffByMobile(mtUser.getMobile());
                if (staffInfo != null && staffInfo.getAuditedStatus().equals(StatusEnum.ENABLED.getKey())) {
                    isMerchant = true;
                }
            }
        }

        // 是否开通微信会员卡
        boolean openWxCard = false;
        if (platform.equals(PlatformTypeEnum.H5.getCode()) && isWechat.equals(YesOrNoEnum.YES.getKey()) && mtUser != null && StringUtil.isNotEmpty(mtUser.getOpenId())) {
            MtSetting cardSetting = settingService.querySettingByName(mtUser.getMerchantId(), SettingTypeEnum.USER.getKey(), UserSettingEnum.OPEN_WX_CARD.getKey());
            if (cardSetting != null && cardSetting.getValue().equals(YesOrNoEnum.TRUE.getKey())) {
                MtSetting cardIdSetting = settingService.querySettingByName(mtUser.getMerchantId(), SettingTypeEnum.USER.getKey(), UserSettingEnum.WX_MEMBER_CARD_ID.getKey());
                if (cardIdSetting != null) {
                    Boolean isOpen = weixinService.isOpenCard(mtUser.getMerchantId(), cardIdSetting.getValue(), mtUser.getOpenId());
                    logger.info("weixinService.isOpenCard userId = {}，isOpen = {}", mtUser.getId(), isOpen);
                    if (!isOpen) {
                        openWxCard = true;
                    }
                }
            }
        }

        outParams.put("isMerchant", isMerchant);
        outParams.put("openWxCard", openWxCard);

        return getSuccessResult(outParams);
    }

    /**
     * 获取会员资产数据
     */
    @ApiOperation(value = "获取会员资产数据")
    @RequestMapping(value = "/asset", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject asset(HttpServletRequest request) throws BusinessCheckException {
        String userId = request.getParameter("userId");
        UserInfo mtUser = TokenUtil.getUserInfo();
        if (StringUtil.isNotEmpty(userId)) {
            MtUser userInfo = memberService.queryMemberById(Integer.parseInt(userId));
            if (userInfo != null) {
                mtUser.setId(userInfo.getId());
            }
        }
        Integer couponNum = 0;
        Integer preStoreNum = 0;
        Integer timerNum = 0;

        if (mtUser != null) {
            List<String> statusList = Arrays.asList(UserCouponStatusEnum.UNUSED.getKey());
            List<MtUserCoupon> dataList = userCouponService.getUserCouponList(mtUser.getId(), statusList);
            for (int i = 0; i < dataList.size(); i++) {
                MtCoupon couponInfo = couponService.queryCouponById(dataList.get(i).getCouponId());
                if (couponInfo == null) {
                    continue;
                }
                if (dataList.get(i).getType().equals(CouponTypeEnum.COUPON.getKey())) {
                    couponNum++;
                }
                if (dataList.get(i).getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
                    preStoreNum++;
                }
                if (dataList.get(i).getType().equals(CouponTypeEnum.TIMER.getKey())) {
                    timerNum++;
                }
            }
        }

        AssetDto asset = new AssetDto();
        asset.setCoupon(couponNum);
        asset.setPrestore(preStoreNum);
        asset.setTimer(timerNum);

        Map<String, Object> outParams = new HashMap<>();
        outParams.put("asset", asset);

        return getSuccessResult(outParams);
    }

    /**
     * 获取会员设置
     */
    @ApiOperation(value = "获取会员设置")
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject setting(HttpServletRequest request) throws BusinessCheckException {
        String merchantNo = request.getHeader("merchantNo");
        Map<String, Object> outParams = new HashMap<>();

        Integer merchantId = merchantService.getMerchantId(merchantNo);
        List<MtSetting> settingList = settingService.getSettingList(merchantId, SettingTypeEnum.USER.getKey());

        for (MtSetting setting : settingList) {
            if (setting.getName().equals(UserSettingEnum.GET_COUPON_NEED_PHONE.getKey())) {
                outParams.put(UserSettingEnum.GET_COUPON_NEED_PHONE.getKey(), setting.getValue());
            } else if (setting.getName().equals(UserSettingEnum.SUBMIT_ORDER_NEED_PHONE.getKey())) {
                outParams.put(UserSettingEnum.SUBMIT_ORDER_NEED_PHONE.getKey(), setting.getValue());
            } else if (setting.getName().equals(UserSettingEnum.LOGIN_NEED_PHONE.getKey())) {
                outParams.put(UserSettingEnum.LOGIN_NEED_PHONE.getKey(), setting.getValue());
            }
        }

        return getSuccessResult(outParams);
    }

    /**
     * 保存会员信息
     */
    @ApiOperation(value = "保存会员信息")
    @RequestMapping(value = "/saveInfo", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject saveInfo(HttpServletRequest request, @RequestBody MemberInfoRequest memberInfo) throws BusinessCheckException {
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        String name = memberInfo.getName();
        String birthday = memberInfo.getBirthday();
        String avatar = memberInfo.getAvatar();
        Integer sex = memberInfo.getSex();
        String code = memberInfo.getCode();
        String password = memberInfo.getPassword();
        String passwordOld = memberInfo.getPasswordOld();
        String phone = memberInfo.getMobile();
        String verifyCode = memberInfo.getVerifyCode();

        String mobile = "";
        Integer merchantId = merchantService.getMerchantId(merchantNo);
        UserInfo userInfo = TokenUtil.getUserInfo();
        boolean modifyPassword = false;
        if (userInfo == null) {
            return getFailureResult(1001);
        }

        // 通过短信验证码修改手机号
        if (StringUtil.isNotEmpty(phone) && StringUtil.isNotEmpty(verifyCode)) {
            MtVerifyCode mtVerifyCode = verifyCodeService.checkVerifyCode(phone, verifyCode);
            if (mtVerifyCode != null) {
                mobile = phone;
            } else {
                return getFailureResult(3002);
            }
        }

        // 小程序获取手机号
        if (StringUtil.isNotEmpty(code)) {
            JSONObject loginInfo = weixinService.getWxProfile(merchantId, code);
            if (loginInfo != null) {
                mobile = weixinService.getPhoneNumber(memberInfo.getEncryptedData(), loginInfo.get("session_key").toString(), memberInfo.getIv());
            }
        }

        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        if (StringUtil.isNotEmpty(name)) {
            mtUser.setName(name);
        }
        if (StringUtil.isNotEmpty(password)) {
            if (StringUtil.isNotEmpty(passwordOld) && StringUtil.isNotEmpty(mtUser.getSalt())) {
                String pass = memberService.deCodePassword(passwordOld, mtUser.getSalt());
                if (!pass.equals(mtUser.getPassword())) {
                    return getFailureResult(201, "旧密码输入有误");
                }
            }
            mtUser.setPassword(password);
            modifyPassword = true;
        }
        if (sex != null && (sex == 0 || sex == 1 || sex == 2)) {
            mtUser.setSex(sex);
        }
        if (StringUtil.isNotEmpty(birthday)) {
            mtUser.setBirthday(birthday);
        }
        if (StringUtil.isNotEmpty(mobile)) {
            mtUser.setMobile(mobile);
        }
        if (StringUtil.isNotEmpty(avatar)) {
            mtUser.setAvatar(avatar);
        }

        MtUser result = memberService.updateMember(mtUser, modifyPassword);
        return getSuccessResult(result);
    }

    /**
     * 设置会员的默认店铺
     */
    @ApiOperation(value = "设置会员的默认店铺")
    @RequestMapping(value = "/defaultStore", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject defaultStore(HttpServletRequest request) throws BusinessCheckException {
        Integer storeId = request.getParameter("storeId") == null ? 0 : Integer.parseInt(request.getParameter("storeId"));
        UserInfo userInfo = TokenUtil.getUserInfo();
        if (userInfo != null && storeId > 0) {
            MtUser mtUser = memberService.queryMemberById(userInfo.getId());
            memberService.updateMember(mtUser, false);
        }
        return getSuccessResult(true);
    }

    /**
     * 获取会员二维码
     * */
    @ApiOperation(value = "获取会员二维码")
    @RequestMapping(value = "/qrCode", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject qrCode() throws BusinessCheckException {
        UserInfo loginInfo = TokenUtil.getUserInfo();
        if (loginInfo == null) {
            return getFailureResult(1001);
        }
        MtUser mtUser = memberService.queryMemberById(loginInfo.getId());
        String qrCode = "";
        try {
            // 生成并输出二维码
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            String content = mtUser.getUserNo();

            QRCodeUtil.createQrCode(out, content, 800, 800, "png", "");

            // 对数据进行Base64编码
            qrCode = new String(Base64Util.baseEncode(out.toByteArray()), "UTF-8");

            qrCode = "data:image/jpg;base64," + qrCode;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        // 微信会员卡领取二维码
        String wxCardQrCode = "";
        MtSetting cardIdSetting = settingService.querySettingByName(mtUser.getMerchantId(), SettingTypeEnum.USER.getKey(), UserSettingEnum.WX_MEMBER_CARD_ID.getKey());
        if (cardIdSetting != null) {
            wxCardQrCode = weixinService.createCardQrCode(mtUser.getMerchantId(), cardIdSetting.getValue(), mtUser.getUserNo());
        }

        Map<String, Object> outParams = new HashMap<>();
        outParams.put("qrCode", qrCode);
        outParams.put("userInfo", mtUser);
        outParams.put("wxCardQrCode", wxCardQrCode);

        return getSuccessResult(outParams);
    }
}

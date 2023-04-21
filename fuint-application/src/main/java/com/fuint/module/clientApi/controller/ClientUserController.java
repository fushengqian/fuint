package com.fuint.module.clientApi.controller;

import com.alibaba.fastjson.JSONObject;
import com.fuint.common.dto.AssetDto;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.enums.CouponTypeEnum;
import com.fuint.common.enums.SettingTypeEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.UserCouponStatusEnum;
import com.fuint.common.service.*;
import com.fuint.common.util.Base64Util;
import com.fuint.common.util.DateUtil;
import com.fuint.common.util.QRCodeUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(value = "/clientApi/user")
public class ClientUserController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ClientUserController.class);

    @Autowired
    private MemberService memberService;

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private StaffService staffService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserGradeService userGradeService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private WeixinService weixinService;

    /**
     * 获取会员信息
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String userNo = request.getParameter("code") == null ? "" : request.getParameter("code");
        UserInfo loginInfo = TokenUtil.getUserInfoByToken(token);
        MtUser userInfo = null;
        if (loginInfo != null) {
            userInfo = memberService.queryMemberById(loginInfo.getId());
        }
        if (StringUtil.isNotEmpty(userNo)) {
            userInfo = memberService.queryMemberByUserNo(userNo);
        }
        MtUserGrade gradeInfo = null;
        if (userInfo != null) {
            gradeInfo = memberService.queryMemberGradeByGradeId(Integer.parseInt(userInfo.getGradeId()));
        }

        List<MtUserGrade> memberGrade = userGradeService.getPayUserGradeList(userInfo);
        Map<String, Object> outParams = new HashMap<>();
        outParams.put("userInfo", userInfo);
        outParams.put("gradeInfo", gradeInfo);
        outParams.put("memberGrade", memberGrade);

        // 会员到期时间
        String gradeEndTime = "";
        if (userInfo != null) {
            if (userInfo.getEndTime() != null) {
                gradeEndTime = DateUtil.formatDate(userInfo.getEndTime(), "yyyy.MM.dd HH:mm");
            }
        }
        outParams.put("gradeEndTime", gradeEndTime);

        // 是否店铺员工
        boolean isMerchant = false;
        if (userInfo != null) {
            if (userInfo.getMobile() != null && StringUtil.isNotEmpty(userInfo.getMobile())) {
                MtStaff staffInfo = staffService.queryStaffByMobile(userInfo.getMobile());
                if (staffInfo != null && staffInfo.getAuditedStatus().equals(StatusEnum.ENABLED.getKey())) {
                    isMerchant = true;
                }
            }
        }
        outParams.put("isMerchant", isMerchant);

        return getSuccessResult(outParams);
    }

    /**
     * 获取会员数据信息
     */
    @RequestMapping(value = "/asset", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject asset(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);

        Integer couponNum = 0;
        Integer preStoreNum = 0;
        Integer timerNum = 0;

        if (mtUser != null) {
            List<String> statusList = Arrays.asList(UserCouponStatusEnum.UNUSED.getKey());
            List<MtUserCoupon> dataList = userCouponService.getUserCouponList(mtUser.getId(), statusList);
            for (int i = 0; i < dataList.size(); i++) {
                MtCoupon couponInfo = couponService.queryCouponById(dataList.get(i).getCouponId());
                boolean isEffective = couponService.isCouponEffective(couponInfo);
                if (!isEffective) {
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
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject setting() throws BusinessCheckException {
        Map<String, Object> outParams = new HashMap<>();

        List<MtSetting> settingList = settingService.getSettingList(SettingTypeEnum.USER.getKey());

        for (MtSetting setting : settingList) {
            if (setting.getName().equals("getCouponNeedPhone")) {
                outParams.put("getCouponNeedPhone", setting.getValue());
            } else if (setting.getName().equals("submitOrderNeedPhone")) {
                outParams.put("submitOrderNeedPhone", setting.getValue());
            } else if (setting.getName().equals("loginNeedPhone")) {
                outParams.put("loginNeedPhone", setting.getValue());
            }
        }

        return getSuccessResult(outParams);
    }

    /**
     * 保存会员信息
     */
    @RequestMapping(value = "/saveInfo", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject saveInfo(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String name = param.get("name") == null ? "" : param.get("name").toString();
        String birthday = param.get("birthday") == null ? "" : param.get("birthday").toString();
        String avatar = param.get("avatar") == null ? "" : param.get("avatar").toString();
        Integer sex = param.get("sex") == null ? 1 : Integer.parseInt(param.get("sex").toString());
        String code = param.get("code") == null ? "" : param.get("code").toString();
        String mobile = "";
        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);
        if (userInfo == null) {
            return getFailureResult(1001);
        }

        if (StringUtil.isNotEmpty(code)) {
            JSONObject loginInfo = weixinService.getWxProfile(code);
            if (loginInfo != null) {
                mobile = weixinService.getPhoneNumber(param.get("encryptedData").toString(), loginInfo.get("session_key").toString(), param.get("iv").toString());
            }
        }

        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        if (StringUtil.isNotEmpty(name)) {
            mtUser.setName(name);
        }
        if (sex.equals(1) || sex.equals(0) || sex.equals(2)) {
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

        MtUser result = memberService.updateMember(mtUser);
        return getSuccessResult(result);
    }

    /**
     * 设置会员的默认店铺
     */
    @RequestMapping(value = "/defaultStore", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject defaultStore(HttpServletRequest request) throws BusinessCheckException {
        Integer storeId = request.getParameter("storeId") == null ? 0 : Integer.parseInt(request.getParameter("storeId"));

        String token = request.getHeader("Access-Token");
        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);
        if (userInfo != null && storeId > 0) {
            MtUser mtUser = memberService.queryMemberById(userInfo.getId());
            mtUser.setStoreId(storeId);
            memberService.updateMember(mtUser);
        }

        Map<String, Object> outParams = new HashMap<>();
        return getSuccessResult(outParams);
    }

    /**
     * 获取会员码
     * */
    @RequestMapping(value = "/qrCode", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject qrCode(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        UserInfo loginInfo = TokenUtil.getUserInfoByToken(token);

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

        Map<String, Object> outParams = new HashMap<>();
        outParams.put("qrCode", qrCode);
        outParams.put("userInfo", mtUser);

        return getSuccessResult(outParams);
    }
}

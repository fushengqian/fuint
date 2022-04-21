package com.fuint.application.web.rest;

import com.fuint.application.dao.entities.*;
import com.fuint.application.dto.AssetDto;
import com.fuint.application.enums.*;
import com.fuint.application.service.staff.StaffService;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.setting.SettingService;
import com.fuint.application.service.usercoupon.UserCouponService;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.usergrade.UserGradeService;
import com.fuint.application.util.DateUtil;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.ResponseObject;
import com.fuint.application.BaseController;
import com.fuint.application.service.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员类controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/rest/user")
public class UserController extends BaseController {

    @Autowired
    private TokenService tokenService;

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

    /**
     * 获取会员信息
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request) throws BusinessCheckException {
        String userToken = request.getHeader("Access-Token");
        MtUser userInfo = tokenService.getUserInfoByToken(userToken);

        MtUserGrade gradeInfo = null;
        if (userInfo != null) {
            gradeInfo = memberService.queryMemberGradeByGradeId(Integer.parseInt(userInfo.getGradeId()));
        }

        // 如果已购买，则返回空
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

        // 是否商户员工
        boolean isMerchant = false;
        if (userInfo != null) {
            MtStaff confirmInfo = staffService.queryStaffByUserId(userInfo.getId());
            if (null != confirmInfo) {
                if (confirmInfo.getAuditedStatus().equals(StatusEnum.ENABLED.getKey())) {
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
    public ResponseObject asset(HttpServletRequest request, Model model) throws BusinessCheckException {
        String userToken = request.getHeader("Access-Token");
        MtUser mtUser = tokenService.getUserInfoByToken(userToken);

        Integer couponNum = 0;
        Integer preStoreNum = 0;
        Integer timerNum = 0;

        if (mtUser != null) {
            List<String> statusList = Arrays.asList(UserCouponStatusEnum.UNUSED.getKey());
            List<MtUserCoupon> dataList = userCouponService.getUserCouponList(mtUser.getId(), statusList);
            PaginationRequest requestName = RequestHandler.buildPaginationRequest(request, model);
            requestName.getSearchParams().put("EQ_status", StatusEnum.ENABLED.getKey());
            requestName.setCurrentPage(1);
            requestName.setPageSize(10000);
            PaginationResponse<MtCoupon> couponData = couponService.queryCouponListByPagination(requestName);
            List<MtCoupon> couponList = couponData.getContent();

            for (int i = 0; i < dataList.size(); i++) {
                MtCoupon couponInfo = new MtCoupon();
                for (int j = 0; j < couponList.size(); j++) {
                    if (dataList.get(i).getCouponId() == couponList.get(j).getId()) {
                        couponInfo = couponList.get(j);
                        break;
                    }
                }

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
     * 设置会员默认店铺
     */
    @RequestMapping(value = "/defaultStore", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject defaultStore(HttpServletRequest request) throws BusinessCheckException {
        Integer storeId = request.getParameter("storeId") == null ? 0 : Integer.parseInt(request.getParameter("storeId"));

        String userToken = request.getHeader("Access-Token");
        MtUser mtUser = tokenService.getUserInfoByToken(userToken);
        if (mtUser != null && storeId > 0) {
            mtUser.setStoreId(storeId);
            memberService.updateMember(mtUser);
        }

        Map<String, Object> outParams = new HashMap<>();
        return getSuccessResult(outParams);
    }
}

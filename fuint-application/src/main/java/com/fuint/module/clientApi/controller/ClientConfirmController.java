package com.fuint.module.clientApi.controller;

import com.fuint.common.dto.UserInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.CouponService;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.StaffService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.MtUserCouponMapper;
import com.fuint.repository.model.MtCoupon;
import com.fuint.repository.model.MtStaff;
import com.fuint.repository.model.MtUser;
import com.fuint.repository.model.MtUserCoupon;
import com.fuint.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * 核销卡券controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/clientApi/confirm")
public class ClientConfirmController extends BaseController {

    @Resource
    private MtUserCouponMapper mtUserCouponMapper;

    /**
     * 卡券服务接口
     */
    @Autowired
    private CouponService couponService;

    /**
     * 员工服务接口
     * */
    @Autowired
    private StaffService staffService;

    /**
     * 会员服务接口
     */
    @Autowired
    private MemberService memberService;

    /**
     * 核销卡券
     *
     * @param param  Request对象
     */
    @RequestMapping(value = "/doConfirm", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doConfirm(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String code = param.get("code") == null ? "" : param.get("code").toString();
        String amount = (param.get("amount") == null || param.get("amount") == "") ? "0" : param.get("amount").toString();
        String remark = param.get("remark") == null ? "" : param.get("remark").toString();

        if (StringUtil.isEmpty(token)) {
            return getFailureResult(1001);
        }

        UserInfo loginInfo = TokenUtil.getUserInfoByToken(token);
        if (loginInfo == null) {
            return getFailureResult(1001);
        }

        MtUserCoupon userCoupon = mtUserCouponMapper.findByCode(code);
        if (null == userCoupon) {
            return getFailureResult(1003, "该券不存在！");
        }

        // 券码已过期
        if (couponService.codeExpired(code)) {
            return getFailureResult(1003, "二维码已过期，请重新获取！");
        }
        MtUser mtUser = memberService.queryMemberById(loginInfo.getId());
        MtCoupon couponInfo = couponService.queryCouponById(userCoupon.getCouponId());

        // 员工是否已经被审核
        HashMap params = new HashMap<>();
        params.put("MOBILE", mtUser.getMobile());
        params.put("AUDITED_STATUS", StatusEnum.ENABLED.getKey());
        List<MtStaff> staffList = staffService.queryStaffByParams(params);
        Integer storeId = 0;
        if (staffList.size() > 0) {
            for (MtStaff staff : staffList) {
                if (staff.getStoreId() > 0) {
                    storeId = staff.getStoreId();
                }
                String storeIdsStr = couponInfo.getStoreIds();
                if (StringUtil.isNotEmpty(storeIdsStr)) {
                    String[] storeIds = couponInfo.getStoreIds().split(",");
                    Boolean isSameStore = false;
                    for (String hid : storeIds) {
                        if (staff.getStoreId().toString().equals(hid)) {
                            isSameStore = true;
                            break;
                        }
                    }
                    if (!isSameStore) {
                        return getFailureResult(1003, "员工对该卡券没有权限");
                    }
                }
            }
        } else {
            return getFailureResult(1003, "员工状态异常！");
        }

        Integer userCouponId = userCoupon.getId();
        String confirmCode = "";

        try {
            confirmCode = couponService.useCoupon(userCouponId, mtUser.getId(), storeId, 0, new BigDecimal(amount), remark);
        } catch (BusinessCheckException e) {
            return getFailureResult(1003, e.getMessage());
        }

        // 获取最新余额
        MtUserCoupon userCouponNew = mtUserCouponMapper.selectById(userCoupon.getId());

        // 组织返回参数
        Map<String, Object> result = new HashMap<>();
        result.put("result", true);
        result.put("money", couponInfo.getAmount());
        result.put("balance", userCouponNew.getBalance());
        result.put("tips", "");
        result.put("name", couponInfo.getName());
        result.put("code", confirmCode);
        result.put("status", userCouponNew.getStatus());

        return getSuccessResult(result);
    }
}

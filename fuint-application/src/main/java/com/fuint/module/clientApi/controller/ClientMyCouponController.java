package com.fuint.module.clientApi.controller;

import com.fuint.common.dto.member.UserInfo;
import com.fuint.common.enums.UserCouponStatusEnum;
import com.fuint.common.service.CouponService;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.StaffService;
import com.fuint.common.service.UserCouponService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.module.clientApi.request.MyCouponRequest;
import com.fuint.repository.model.MtStaff;
import com.fuint.repository.model.MtUser;
import com.fuint.repository.model.MtUserCoupon;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 我的卡券controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-我的卡券相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/myCoupon")
public class ClientMyCouponController extends BaseController {

    /**
     * 卡券服务接口
     */
    private CouponService couponService;

    /**
     * 会员卡券服务接口
     * */
    private UserCouponService userCouponService;

    /**
     * 会员服务接口
     * */
    private MemberService memberService;

    /**
     * 员工服务接口
     * */
    private StaffService staffService;

    /**
     * 查询我的卡券
     */
    @ApiOperation(value = "查询我的卡券")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String status = request.getParameter("status") == null ? "" : request.getParameter("status");
        String type = request.getParameter("type") == null ? "" : request.getParameter("type");
        String userId = request.getParameter("userId") == null ? "" : request.getParameter("userId");

        UserInfo mtUser = TokenUtil.getUserInfo();
        if (null == mtUser) {
            return getFailureResult(1001);
        }

        // 目标用户ID，默认当前登录用户
        Integer targetUserId = mtUser.getId();

        // 传入 userId 参数时，仅允许登录用户本人，或同商户的店员（员工）代查，防止越权查看/核销他人卡券
        if (StringUtil.isNotEmpty(userId) && !String.valueOf(mtUser.getId()).equals(userId)) {
            MtUser loginInfo = memberService.queryMemberById(mtUser.getId());
            if (null == loginInfo) {
                return getFailureResult(1001);
            }
            MtStaff staffInfo = staffService.queryStaffByMobile(loginInfo.getMobile());
            if (null == staffInfo) {
                return getFailureResult(1004);
            }
            // 校验店员与目标会员是否同商户
            MtUser targetUser = memberService.queryMemberById(Integer.parseInt(userId));
            if (null == targetUser || !targetUser.getMerchantId().equals(staffInfo.getMerchantId())) {
                return getFailureResult(1004);
            }
            targetUserId = targetUser.getId();
        }

        Map<String, Object> param = new HashMap<>();
        param.put("userId", targetUserId);
        param.put("status", status);
        param.put("type", type);

        ResponseObject result = userCouponService.getUserCouponList(param);
        return getSuccessResult(result.getData());
    }

    /**
     * 查询我的卡券是否已使用
     */
    @ApiOperation(value = "查询我的卡券是否已使用")
    @RequestMapping(value = "/isUsed", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject isUsed(@RequestBody MyCouponRequest requestParam) {
        Integer userCouponId = requestParam.getId() == null ? 0 : requestParam.getId();

        UserInfo mtUser = TokenUtil.getUserInfo();
        MtUserCoupon userCoupon = couponService.queryUserCouponById(userCouponId);
        if (userCoupon.getStatus().equals(UserCouponStatusEnum.USED.getKey()) && mtUser.getId().equals(userCoupon.getUserId())) {
            return getSuccessResult(true);
        } else {
            return getSuccessResult(false);
        }
    }

    /**
     * 删除我的卡券
     */
    @ApiOperation(value = "删除我的卡券")
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject remove(@RequestBody MyCouponRequest requestParam) throws BusinessCheckException {
        Integer userCouponId = requestParam.getUserCouponId() == null ? 0 : requestParam.getUserCouponId();
        UserInfo mtUser = TokenUtil.getUserInfo();

        Boolean result = couponService.removeCoupon(userCouponId, mtUser.getId());
        if (result) {
            return getSuccessResult(true);
        } else {
            return getSuccessResult(false);
        }
    }
}

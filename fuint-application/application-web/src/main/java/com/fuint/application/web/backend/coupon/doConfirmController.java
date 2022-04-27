package com.fuint.application.web.backend.coupon;

import com.fuint.application.dao.entities.MtCoupon;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.dao.entities.MtUserCoupon;
import com.fuint.application.dao.repositories.MtUserCouponRepository;
import com.fuint.application.dto.ReqResult;
import com.fuint.application.dto.UserCouponDto;
import com.fuint.application.enums.CouponTypeEnum;
import com.fuint.application.enums.UserCouponStatusEnum;
import com.fuint.application.service.confirmlog.ConfirmLogService;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.util.DateUtil;
import com.fuint.base.service.account.TAccountService;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.exception.BusinessCheckException;
import com.fuint.exception.BusinessRuntimeException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.fuint.base.shiro.ShiroUser;
import com.fuint.base.dao.entities.TAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.fuint.application.web.backend.base.BaseController;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.commons.lang.StringUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

/**
 * 卡券核销类controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Controller
@RequestMapping(value = "/backend/doConfirm")
public class doConfirmController extends BaseController {

    @Autowired
    private MtUserCouponRepository userCouponRepository;

    /**
     * 卡券服务接口
     */
    @Autowired
    private CouponService couponService;

    /**
     * 账户服务接口
     */
    @Autowired
    private TAccountService accountService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ConfirmLogService confirmLogService;

    /**
     * 核销页面
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return
     */
    @RequiresPermissions("backend/doConfirm/index")
    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        Integer userCouponId = request.getParameter("id") == null ? 0 : Integer.parseInt(request.getParameter("id"));
        String userCouponCode = request.getParameter("code") == null ? "" : request.getParameter("code");
        if (StringUtils.isEmpty(userCouponCode) && userCouponId < 1) {
            throw new BusinessRuntimeException("核销券码不能为空");
        }

        // 通过券码或ID获取
        MtUserCoupon userCoupon;
        if (!StringUtils.isEmpty(userCouponCode)) {
            userCoupon = userCouponRepository.findByCode(userCouponCode);
        } else {
            userCoupon = userCouponRepository.findOne(userCouponId);
        }

        if (userCoupon == null) {
            throw new BusinessRuntimeException("未查询到该卡券信息，请刷新后再试");
        }

        if (!userCoupon.getStatus().equals(UserCouponStatusEnum.UNUSED.getKey())) {
            throw new BusinessRuntimeException("该卡券可能已经核销或过期，请确认");
        }

        MtCoupon couponInfo = couponService.queryCouponById(userCoupon.getCouponId());
        MtUser userInfo = memberService.queryMemberById(userCoupon.getUserId());

        String effectiveDate = DateUtil.formatDate(couponInfo.getBeginTime(), "yyyy.MM.dd") + " - " + DateUtil.formatDate(couponInfo.getEndTime(), "yyyy.MM.dd");

        UserCouponDto userCouponInfo = new UserCouponDto();
        userCouponInfo.setName(couponInfo.getName());
        userCouponInfo.setEffectiveDate(effectiveDate);
        userCouponInfo.setDescription(couponInfo.getDescription());
        userCouponInfo.setId(userCoupon.getId());
        userCouponInfo.setType(couponInfo.getType());
        userCouponInfo.setStatus(userCoupon.getStatus());
        userCouponInfo.setBalance(userCoupon.getBalance());
        userCouponInfo.setAmount(userCoupon.getAmount());
        userCouponInfo.setCode(userCoupon.getCode());
        userCouponInfo.setUseRule(couponInfo.getOutRule());
        Long confirmCount = confirmLogService.getConfirmNum(userCoupon.getId());
        userCouponInfo.setConfirmCount(confirmCount.intValue());

        model.addAttribute("couponInfo", userCouponInfo);
        model.addAttribute("userInfo", userInfo);

        return "coupon/confirm";
    }

    /**
     * 确认核销
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return
     */
    @RequiresPermissions("backend/doConfirm/doConfirm")
    @RequestMapping(value = "/doConfirm")
    @ResponseBody
    public ReqResult doConfirm(HttpServletRequest request, HttpServletResponse response, Model model) {
        Integer userCouponId = request.getParameter("userCouponId") == null ? 0 : Integer.parseInt(request.getParameter("userCouponId"));
        String amount = request.getParameter("amount") == null ? "0" : request.getParameter("amount");
        String remark = StringUtils.isEmpty(request.getParameter("remark")) ? "后台核销" : request.getParameter("remark");

        ShiroUser user = ShiroUserHelper.getCurrentShiroUser();
        TAccount account = accountService.findAccountById(user.getId());
        Integer storeId = account.getStoreId();

        MtUserCoupon mtUserCoupon = userCouponRepository.findOne(userCouponId);
        if (mtUserCoupon.getType().equals(CouponTypeEnum.PRESTORE.getKey()) && StringUtils.isEmpty(amount)) {
            ReqResult reqResult = new ReqResult();
            reqResult.setResult(false);
            reqResult.setMsg("错误，预存卡核销金额不能为空！");
            return reqResult;
        }

        try {
            couponService.useCoupon(userCouponId, user.getId().intValue(), storeId, 0, new BigDecimal(amount), remark);
        } catch (BusinessCheckException e) {
            ReqResult reqResult = new ReqResult();
            reqResult.setCode("0");
            reqResult.setResult(false);
            reqResult.setMsg("核销失败：" + e.getMessage());
            return reqResult;
        }

        ReqResult reqResult = new ReqResult();
        reqResult.setResult(true);

        return reqResult;
    }
}

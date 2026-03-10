package com.fuint.module.merchantApi.controller;

import com.fuint.common.dto.member.UserInfo;
import com.fuint.common.param.StatisticParam;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.MerchantService;
import com.fuint.common.service.ReportService;
import com.fuint.common.service.StaffService;
import com.fuint.common.util.DateUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtStaff;
import com.fuint.repository.model.MtUser;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

/**
 * 商户报表管理接口controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="商户端-报表相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/merchantApi/report")
public class MerchantReportController extends BaseController {

    /**
     * 会员服务接口
     */
    private MemberService memberService;

    /**
     * 店铺员工服务接口
     * */
    private StaffService staffService;

    /**
     * 商户服务接口
     * */
    private MerchantService merchantService;

    /**
     * 报表服务接口
     * */
    private ReportService reportService;

    @ApiOperation(value = "报表概述")
    @RequestMapping(value = "/overview", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject overview(HttpServletRequest request, @RequestBody StatisticParam param) throws ParseException {
        UserInfo userInfo = TokenUtil.getUserInfo();
        MtUser mtUser = memberService.queryMemberById(userInfo.getId());

        if (mtUser == null || StringUtil.isBlank(mtUser.getMobile())) {
            return getFailureResult(201, "您的帐号不是商户，没有操作权限");
        }

        MtStaff staff = staffService.queryStaffByMobile(mtUser.getMobile());
        Integer merchantId = merchantService.getMerchantId(request.getHeader("merchantNo"));
        if (staff == null || !merchantId.equals(staff.getMerchantId())) {
            return getFailureResult(201, "您没有操作权限");
        }

        String startTimeStr = param.getStartTime();
        String endTimeStr = param.getEndTime();
        Integer storeId = param.getStoreId();

        Date startTime = StringUtil.isNotEmpty(startTimeStr) ? DateUtil.parseDate(startTimeStr) : null;
        Date endTime = StringUtil.isNotEmpty(endTimeStr) ? DateUtil.parseDate(endTimeStr) : null;

        Map<String, Object> result = reportService.getReportOverview(merchantId, storeId, startTime, endTime);
        return getSuccessResult(result);
    }
}

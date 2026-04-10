package com.fuint.module.merchantApi.controller;

import com.fuint.common.dto.member.UserInfo;
import com.fuint.common.dto.order.RefundDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.enums.RefundStatusEnum;
import com.fuint.common.param.RefundDetailParam;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.RefundService;
import com.fuint.common.service.StaffService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.module.clientApi.request.RefundListRequest;
import com.fuint.repository.model.MtRefund;
import com.fuint.repository.model.MtStaff;
import com.fuint.repository.model.MtUser;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 售后类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="商户端-售后管理相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/merchantApi/refund")
public class MerchantRefundController extends BaseController {

    /**
     * 会员服务接口
     */
    private MemberService memberService;

    /**
     * 店铺员工服务接口
     * */
    private StaffService staffService;

    /**
     * 售后服务接口
     * */
    private RefundService refundService;

    /**
     * 获取售后订单列表
     */
    @ApiOperation(value = "获取售订单后列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(@RequestBody RefundListRequest param) throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfo();
        MtStaff staffInfo = staffService.queryStaffByMobile(userInfo.getMobile());

        if (staffInfo == null) {
            return getFailureResult(1001);
        } else {
            param.setMerchantId(staffInfo.getMerchantId());
            param.setStoreId(staffInfo.getStoreId());
        }

        String status = param.getStatus() != null ? param.getStatus() : "";
        if (status.equals("1")) {
            status = RefundStatusEnum.CREATED.getKey();
        } else {
            status = "";
        }
        param.setStatus(status);

        ResponseObject refund = refundService.getUserRefundList(param);
        return getSuccessResult(refund.getData());
    }

    /**
     * 获取售后订单详情
     */
    @ApiOperation(value = "获取售后订单详情")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request) throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfo();

        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        MtStaff mtStaff = staffService.queryStaffByMobile(mtUser.getMobile());
        if (mtStaff == null) {
            return getFailureResult(1004);
        }

        String refundId = request.getParameter("refundId");
        if (StringUtil.isEmpty(refundId)) {
            return getFailureResult(2000, "售后订单ID不能为空");
        }
        RefundDto refundInfo = refundService.getRefundById(Integer.parseInt(refundId));
        return getSuccessResult(refundInfo);
    }

    /**
     * 更新售后订单
     */
    @ApiOperation(value = "更新售后订单")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject update(@RequestBody RefundDetailParam param) throws BusinessCheckException {
        UserInfo mtUser = TokenUtil.getUserInfo();

        Integer refundId = param.getRefundId();
        if (refundId == null || refundId <= 0) {
            return getFailureResult(201, "售后订单不能为空");
        }

        RefundDto refundInfo = refundService.getRefundById(refundId);
        if (refundInfo == null) {
            return getFailureResult(201, "售后订单不存在");
        }

        MtUser userInfo = memberService.queryMemberById(mtUser.getId());
        MtStaff staffInfo = staffService.queryStaffByMobile(userInfo.getMobile());

        if (staffInfo == null || (staffInfo.getStoreId() != null && staffInfo.getStoreId() > 0 && !staffInfo.getStoreId().equals(refundInfo.getStoreInfo().getId()))) {
            return getFailureResult(1004);
        }

        RefundDto refundDto = new RefundDto();
        refundDto.setId(refundId);
        refundDto.setOperator(staffInfo.getRealName());
        refundDto.setStatus(param.getStatus());
        refundDto.setRemark(param.getRemark());

        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountName(staffInfo.getRealName());
        accountInfo.setMerchantId(staffInfo.getMerchantId());
        MtRefund mtRefund = refundService.updateRefund(refundDto, accountInfo);
        return getSuccessResult(mtRefund);
    }
}

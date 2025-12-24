package com.fuint.module.merchantApi.controller;

import com.fuint.common.dto.RefundDto;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.param.RefundDetailParam;
import com.fuint.common.param.RefundListParam;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.RefundService;
import com.fuint.common.service.StaffService;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtRefund;
import com.fuint.repository.model.MtStaff;
import com.fuint.repository.model.MtUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
    public ResponseObject list(@RequestBody RefundListParam params) throws BusinessCheckException, IllegalAccessException {
        UserInfo userInfo = TokenUtil.getUserInfo();
        MtStaff staffInfo = staffService.queryStaffByMobile(userInfo.getMobile());

        if (staffInfo == null) {
            return getFailureResult(1001);
        } else {
            params.setMerchantId(staffInfo.getMerchantId());
            params.setStoreId(staffInfo.getStoreId());
        }
        PaginationResponse paginationResponse = refundService.getRefundListByPagination(new PaginationRequest(params.getPage(), params.getPageSize(), CommonUtil.convert(params)));

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        return getSuccessResult(result);
    }

    /**
     * 获取售后订单详情
     */
    @ApiOperation(value = "获取售后订单详情")
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject detail(@RequestBody RefundDetailParam param) throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfo();

        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        MtStaff mtStaff = staffService.queryStaffByMobile(mtUser.getMobile());
        if (mtStaff == null) {
            return getFailureResult(1004);
        }

        Integer refundId = param.getRefundId();
        if (refundId == null || refundId <= 0) {
            return getFailureResult(201, "售后订单不能为空");
        }

        RefundDto refundInfo = refundService.getRefundById(refundId);
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
        MtRefund mtRefund = refundService.updateRefund(refundDto);
        return getSuccessResult(mtRefund);
    }
}

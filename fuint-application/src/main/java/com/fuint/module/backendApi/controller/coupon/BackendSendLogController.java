package com.fuint.module.backendApi.controller.coupon;

import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.param.SendLogPage;
import com.fuint.common.service.CouponService;
import com.fuint.common.service.SendLogService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtSendLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 卡券发放记录管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-卡券发放相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/sendLog")
public class BackendSendLogController extends BaseController {

    /**
     * 发送记录服务接口
     */
    private SendLogService sendLogService;

    /**
     * 卡券服务接口
     * */
    private CouponService couponService;

    /**
     * 查询发券记录列表
     */
    @ApiOperation(value = "查询发券记录列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(@ModelAttribute SendLogPage sendLogPage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            sendLogPage.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            sendLogPage.setStoreId(accountInfo.getStoreId());
        }
        PaginationResponse<MtSendLog> paginationResponse = sendLogService.querySendLogListByPagination(sendLogPage);

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }

    /**
     * 废除用户卡券
     */
    @ApiOperation(value = "废除用户卡券")
    @RequestMapping(value = "/removeUserCoupon/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject removeUserCoupon(@PathVariable("id") Long id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        if (id == null) {
            return getFailureResult(201, "系统参数有误");
        }

        MtSendLog sendLog = sendLogService.querySendLogById(id);
        if (sendLog == null) {
            return getFailureResult(201, "系统参数有误");
        }

        couponService.removeUserCoupon(id, sendLog.getUuid(), accountInfo);
        return getSuccessResult(true);
    }
}

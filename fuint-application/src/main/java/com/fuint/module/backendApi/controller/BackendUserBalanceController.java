package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.StatusParam;
import com.fuint.common.param.UserBalancePage;
import com.fuint.common.service.UserBalanceService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtUserBalance;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 会员余额管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-会员余额相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/userBalance")
public class BackendUserBalanceController extends BaseController {

    /**
     * 会员余额服务接口
     */
    private UserBalanceService userBalanceService;

    /**
     * 会员余额列表查询
     */
    @ApiOperation(value = "会员余额列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('userBalance:list')")
    public ResponseObject list(@ModelAttribute UserBalancePage userBalancePage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Map<String, Object> params = new HashMap<>();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            params.put("merchantId", accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            params.put("storeId", accountInfo.getStoreId());
        }

        PaginationResponse<MtUserBalance> paginationResponse = userBalanceService.queryUserBalanceListByPagination(userBalancePage);
        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }

    /**
     * 更新会员余额状态
     *
     * @return
     */
    @ApiOperation(value = "更新会员余额状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('userBalance:edit')")
    public ResponseObject updateStatus(@RequestBody StatusParam params) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        String status = params.getStatus() == null ? params.getStatus() : StatusEnum.ENABLED.getKey();
        MtUserBalance mtUserBalance = userBalanceService.queryUserBalanceById(params.getId());
        if (mtUserBalance == null) {
            return getFailureResult(201, "该数据不存在");
        }

        mtUserBalance.setOperator(accountInfo.getAccountName());
        mtUserBalance.setStatus(status);
        userBalanceService.updateUserBalance(mtUserBalance);

        return getSuccessResult(true);
    }

    /**
     * 保存会员余额
     */
    @ApiOperation(value = "保存会员余额")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('userBalance:add')")
    public ResponseObject saveHandler(@RequestBody Map<String, Object> params) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        String id = params.get("id") == null ? "" : params.get("id").toString();
        String status = params.get("status") == null ? StatusEnum.ENABLED.getKey() : params.get("status").toString();
        String storeId = params.get("storeId") == null ? "0" : params.get("storeId").toString();

        MtUserBalance mtUserBalance = new MtUserBalance();
        mtUserBalance.setOperator(accountInfo.getAccountName());
        mtUserBalance.setStatus(status);
        mtUserBalance.setStoreId(Integer.parseInt(storeId));
        mtUserBalance.setMerchantId(accountInfo.getMerchantId());
        if (StringUtil.isNotEmpty(id)) {
            mtUserBalance.setId(Integer.parseInt(id));
            userBalanceService.updateUserBalance(mtUserBalance);
        } else {
            userBalanceService.addUserBalance(mtUserBalance);
        }

        return getSuccessResult(true);
    }

    /**
     * 获取会员余额详情
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "获取会员余额详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('userBalance:list')")
    public ResponseObject info(@PathVariable("id") Integer id) throws BusinessCheckException {
        MtUserBalance userBalanceInfo = userBalanceService.queryUserBalanceById(id);

        Map<String, Object> result = new HashMap<>();
        result.put("userBalanceInfo", userBalanceInfo);

        return getSuccessResult(result);
    }
}

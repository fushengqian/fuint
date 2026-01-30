package com.fuint.module.backendApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.MerchantDto;
import com.fuint.common.dto.ParamDto;
import com.fuint.common.enums.MerchantTypeEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.MerchantService;
import com.fuint.common.service.SettingService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.module.backendApi.request.MerchantSubmitRequest;
import com.fuint.repository.model.MtMerchant;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商户管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-商户管理相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/merchant")
public class BackendMerchantController extends BaseController {

    /**
     * 商户服务接口
     */
    private MerchantService merchantService;

    /**
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 分页查询商户列表
     */
    @ApiOperation(value = "分页查询商户列表")
    @RequestMapping(value = "/list")
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('merchant:index')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));

        String merchantId = request.getParameter("id");
        String merchantName = request.getParameter("name");
        String status = request.getParameter("status");

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            merchantId = accountInfo.getMerchantId().toString();
        }

        Map<String, Object> params = new HashMap<>();
        if (StringUtil.isNotEmpty(merchantId)) {
            params.put("id", merchantId);
        }
        if (StringUtil.isNotEmpty(merchantName)) {
            params.put("name", merchantName);
        }
        if (StringUtil.isNotEmpty(status)) {
            params.put("status", status);
        }
        PaginationResponse<MerchantDto> paginationResponse = merchantService.queryMerchantListByPagination(new PaginationRequest(page, pageSize, params));

        // 商户类型列表
        List<ParamDto> typeList = MerchantTypeEnum.getMerchantTypeList();

        Map<String, Object> result = new HashMap<>();
        result.put("dataList", paginationResponse);
        result.put("imagePath", settingService.getUploadBasePath());
        result.put("typeList", typeList);

        return getSuccessResult(result);
    }

    /**
     * 查询商户列表
     * */
    @ApiOperation(value = "查询商户列表")
    @RequestMapping(value = "/searchMerchant",  method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject searchMerchant(HttpServletRequest request) {
        String merchantId = request.getParameter("id") == null ? "" : request.getParameter("id");
        String name = request.getParameter("name") == null ? "" : request.getParameter("name");

        Map<String, Object> params = new HashMap<>();
        if (StringUtil.isNotEmpty(merchantId)) {
            params.put("merchantId", merchantId);
        }
        if (StringUtil.isNotEmpty(name)) {
            params.put("name", name);
        }
        params.put("status", StatusEnum.ENABLED.getKey());
        List<MtMerchant> merchantList = merchantService.queryMerchantByParams(params);
        Map<String, Object> result = new HashMap<>();
        result.put("merchantList", merchantList);

        return getSuccessResult(result);
    }

    /**
     * 更新商户状态
     */
    @ApiOperation(value = "更新商户状态")
    @RequestMapping(value = "/updateStatus")
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('merchant:index')")
    public ResponseObject updateStatus(@RequestBody Map<String, Object> params) throws BusinessCheckException {
        String status = params.get("status") != null ? params.get("status").toString() : StatusEnum.ENABLED.getKey();
        Integer merchantId = params.get("merchantId") == null ? 0 : Integer.parseInt(params.get("merchantId").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            merchantId = accountInfo.getMerchantId();
        }
        merchantService.updateStatus(merchantId, accountInfo.getAccountName(), status);
        return getSuccessResult(true);
    }

    /**
     * 保存商户信息
     */
    @ApiOperation(value = "保存商户信息")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('merchant:index')")
    public ResponseObject saveHandler(@RequestBody MerchantSubmitRequest merchantInfo) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Integer merchantId = accountInfo.getMerchantId();

        MtMerchant mtMerchant = new MtMerchant();
        BeanUtils.copyProperties(merchantInfo, mtMerchant);
        if (merchantId != null && merchantId > 0) {
            mtMerchant.setId(merchantId);
        }

        if (StringUtil.isEmpty(mtMerchant.getName())) {
            return getFailureResult(201, "商户名称不能为空");
        }

        if (mtMerchant.getId() == null && merchantId != null && merchantId > 0) {
            return getFailureResult(201, "抱歉，您没有添加商户的权限");
        }

        mtMerchant.setOperator(accountInfo.getAccountName());
        merchantService.saveMerchant(mtMerchant);

        return getSuccessResult(true);
    }

    /**
     * 获取商户详情
     */
    @ApiOperation(value = "获取商户详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('merchant:index')")
    public ResponseObject getMerchantInfo(@PathVariable("id") Integer merchantId) {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            merchantId = accountInfo.getMerchantId();
        }

        MtMerchant merchantInfo = merchantService.queryMerchantById(merchantId);
        Map<String, Object> result = new HashMap<>();
        result.put("merchantInfo", merchantInfo);

        return getSuccessResult(result);
    }
}

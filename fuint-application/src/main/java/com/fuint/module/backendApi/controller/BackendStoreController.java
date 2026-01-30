package com.fuint.module.backendApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.StoreDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.MerchantService;
import com.fuint.common.service.SettingService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.module.backendApi.request.StoreSubmitRequest;
import com.fuint.repository.model.MtMerchant;
import com.fuint.repository.model.MtStore;
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
 * 店铺管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-店铺相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/store")
public class BackendStoreController extends BaseController {

    /**
     * 店铺服务接口
     */
    private StoreService storeService;

    /**
     * 商户接口
     */
    private MerchantService merchantService;

    /**
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 获取店铺列表
     */
    @ApiOperation(value = "获取店铺列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('store:list')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String storeName = request.getParameter("name");
        String storeStatus = request.getParameter("status");
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        Map<String, Object> params = new HashMap<>();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            params.put("merchantId", accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            params.put("storeId", accountInfo.getStoreId());
        }
        if (StringUtil.isNotEmpty(storeName)) {
            params.put("name", storeName);
        }
        if (StringUtil.isNotEmpty(storeStatus)) {
            params.put("status", storeStatus);
        }
        PaginationResponse<StoreDto> paginationResponse = storeService.queryStoreListByPagination(new PaginationRequest(page, pageSize, params));

        List<MtMerchant> merchantList = merchantService.getMyMerchantList(accountInfo.getMerchantId(), accountInfo.getStoreId(), StatusEnum.ENABLED.getKey());

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        result.put("merchantList", merchantList);
        result.put("imagePath", settingService.getUploadBasePath());

        return getSuccessResult(result);
    }

    /**
     * 搜索店铺
     */
    @ApiOperation(value = "搜索店铺")
    @RequestMapping(value = "/searchStore",  method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject search(HttpServletRequest request) {
        String merchantId = request.getParameter("merchantId") == null ? "" : request.getParameter("merchantId");
        String storeId = request.getParameter("id") == null ? "" : request.getParameter("id");
        String storeName = request.getParameter("name") == null ? "" : request.getParameter("name");

        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            storeId = accountInfo.getStoreId().toString();
        }

        Map<String, Object> paramsStore = new HashMap<>();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            merchantId = accountInfo.getMerchantId().toString();
        }
        if (StringUtil.isNotEmpty(merchantId)) {
            paramsStore.put("merchantId", merchantId);
        }
        if (StringUtil.isNotEmpty(storeId)) {
            paramsStore.put("storeId", storeId);
        }
        if (StringUtil.isNotEmpty(storeName)) {
            paramsStore.put("name", storeName);
        }

        paramsStore.put("status", StatusEnum.ENABLED.getKey());
        List<MtStore> storeList = storeService.queryStoresByParams(paramsStore);
        Map<String, Object> result = new HashMap<>();
        result.put("storeList", storeList);

        return getSuccessResult(result);
    }

    /**
     * 更新店铺状态
     */
    @ApiOperation(value = "更新店铺状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('store:add')")
    public ResponseObject updateStatus(@RequestBody Map<String, Object> params) throws BusinessCheckException {
        String status = params.get("status") != null ? params.get("status").toString() : StatusEnum.ENABLED.getKey();
        Integer storeId = params.get("storeId") == null ? 0 : Integer.parseInt(params.get("storeId").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        storeService.updateStatus(storeId, accountInfo.getAccountName(), status);

        return getSuccessResult(true);
    }

    /**
     * 保存店铺
     */
    @ApiOperation(value = "保存店铺")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('store:add')")
    public ResponseObject saveHandler(@RequestBody StoreSubmitRequest storeParam) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        StoreDto storeDto = new StoreDto();

        if ((StringUtil.isEmpty(storeParam.getLatitude()) || StringUtil.isEmpty(storeParam.getLongitude())) && StringUtil.isNotEmpty(storeParam.getAddress())) {
            Map<String, Object> latAndLng = storeService.getLatAndLngByAddress(storeParam.getAddress());
            storeParam.setLatitude(latAndLng.get("lat").toString());
            storeParam.setLongitude(latAndLng.get("lng").toString());
        }

        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            if (storeParam.getId() == null) {
                return getFailureResult(201, "店铺帐号不能新增店铺，请使用商户帐号添加！");
            }
            storeParam.setId(accountInfo.getStoreId());
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            storeParam.setMerchantId(accountInfo.getMerchantId());
        }

        BeanUtils.copyProperties(storeParam, storeDto);
        if (StringUtil.isEmpty(storeParam.getName())) {
            return getFailureResult(201, "店铺名称不能为空");
        } else {
            if (!StringUtil.isNotEmpty(storeParam.getName())) {
                StoreDto store = storeService.queryStoreByName(storeParam.getName());
                if (store != null && store.getName().equals(storeParam.getName()) && !store.getId().equals(storeParam.getId())) {
                    return getFailureResult(201, "该店铺名称已经存在");
                }
            }
        }

        storeDto.setOperator(accountInfo.getAccountName());
        storeService.saveStore(storeDto);

        return getSuccessResult(true);
    }

    /**
     * 获取店铺详情
     */
    @ApiOperation(value = "获取店铺详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('store:list')")
    public ResponseObject getStoreInfo(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        StoreDto storeInfo = storeService.queryStoreDtoById(id);
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            if (!accountInfo.getMerchantId().equals(storeInfo.getMerchantId())) {
                return getFailureResult(1004);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("storeInfo", storeInfo);

        return getSuccessResult(result);
    }
}

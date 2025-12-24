package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.param.BannerPage;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.common.dto.BannerDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.SettingService;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.common.service.BannerService;
import com.fuint.repository.model.MtBanner;
import com.fuint.repository.model.MtStore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 焦点图管理类controller
 * <p>
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags = "管理端-焦点图相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/banner")
public class BackendBannerController extends BaseController {

    /**
     * 焦点图服务接口
     */
    private BannerService bannerService;

    /**
     * 系统设置服务接口
     */
    private SettingService settingService;

    /**
     * 店铺服务接口
     */
    private StoreService storeService;

    /**
     * 焦点图列表查询
     */
    @ApiOperation(value = "焦点图列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('content:banner:list')")
    public ResponseObject list(@ModelAttribute BannerPage bannerPage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            bannerPage.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            bannerPage.setStoreId(accountInfo.getStoreId());
        }
        PaginationResponse<MtBanner> paginationResponse = bannerService.queryBannerListByPagination(bannerPage);
        List<MtStore> storeList = storeService.getMyStoreList(accountInfo.getMerchantId(), accountInfo.getStoreId(), StatusEnum.ENABLED.getKey());

        Map<String, Object> result = new HashMap<>();
        result.put("dataList", paginationResponse);
        result.put("imagePath", settingService.getUploadBasePath());
        result.put("storeList", storeList);

        return getSuccessResult(result);
    }

    /**
     * 更新焦点图状态
     */
    @ApiOperation(value = "更新焦点图状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('content:banner:edit')")
    public ResponseObject updateStatus(@RequestBody Map<String, Object> params) throws BusinessCheckException {
        String status = params.get("status") != null ? params.get("status").toString() : StatusEnum.ENABLED.getKey();
        Integer id = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        MtBanner mtBanner = bannerService.queryBannerById(id);
        if (mtBanner == null) {
            return getFailureResult(201);
        }

        BannerDto bannerDto = new BannerDto();
        bannerDto.setOperator(accountInfo.getAccountName());
        bannerDto.setId(id);
        bannerDto.setStatus(status);
        bannerService.updateBanner(bannerDto);

        return getSuccessResult(true);
    }

    /**
     * 保存焦点图
     */
    @ApiOperation(value = "保存焦点图")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('content:banner:add')")
    public ResponseObject saveHandler(@RequestBody BannerDto bannerDto) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        bannerDto.setOperator(accountInfo.getAccountName());
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            bannerDto.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            bannerDto.setStoreId(accountInfo.getStoreId());
        }
        if (bannerDto.getId() != null && bannerDto.getId() > 0) {
            bannerService.updateBanner(bannerDto);
        } else {
            bannerService.addBanner(bannerDto);
        }
        return getSuccessResult(true);
    }

    /**
     * 获取焦点图详情
     */
    @ApiOperation(value = "获取焦点图详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('content:banner:list')")
    public ResponseObject info(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        MtBanner bannerInfo = bannerService.queryBannerById(id);
        String imagePath = settingService.getUploadBasePath();

        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            if (!bannerInfo.getMerchantId().equals(accountInfo.getMerchantId())) {
                return getFailureResult(1004);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("bannerInfo", bannerInfo);
        result.put("imagePath", imagePath);

        return getSuccessResult(result);
    }
}

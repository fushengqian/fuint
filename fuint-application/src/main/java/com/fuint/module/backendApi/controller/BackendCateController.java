package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.GoodsCateDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.GoodsCatePage;
import com.fuint.common.param.StatusParam;
import com.fuint.common.service.CateService;
import com.fuint.common.service.SettingService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtGoodsCate;
import com.fuint.repository.model.MtStore;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品分类管理controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-商品分类相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/goods/cate")
public class BackendCateController extends BaseController {

    /**
     * 商品分类服务接口
     */
    private CateService cateService;

    /**
     * 配置服务接口
     * */
    private SettingService settingService;

    /**
     * 店铺服务接口
     */
    private StoreService storeService;

    /**
     * 获取商品分类列表
     */
    @ApiOperation(value = "获取商品分类列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('goods:cate:index')")
    public ResponseObject list(@ModelAttribute GoodsCatePage goodsCatePage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            goodsCatePage.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            goodsCatePage.setStoreId(accountInfo.getStoreId());
        }

        PaginationResponse<GoodsCateDto> paginationResponse = cateService.queryCateListByPagination(goodsCatePage);
        List<MtStore> storeList = storeService.getMyStoreList(accountInfo.getMerchantId(), accountInfo.getStoreId(), StatusEnum.ENABLED.getKey());

        Map<String, Object> result = new HashMap<>();
        result.put("imagePath", settingService.getUploadBasePath());
        result.put("storeList", storeList);
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }

    /**
     * 更新商品分类状态
     */
    @ApiOperation(value = "更新商品分类状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('goods:cate:index')")
    public ResponseObject updateStatus(@RequestBody StatusParam params) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        MtGoodsCate mtCate = cateService.queryCateById(params.getId());
        if (mtCate == null) {
            return getFailureResult(201, "该类别不存在");
        }

        MtGoodsCate cate = new MtGoodsCate();
        cate.setOperator(accountInfo.getAccountName());
        cate.setId(params.getId());
        cate.setStatus(params.getStatus());
        cateService.updateCate(cate);

        return getSuccessResult(true);
    }

    /**
     * 保存商品分类
     */
    @ApiOperation(value = "保存商品分类")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @PreAuthorize("@pms.hasPermission('goods:cate:index')")
    public ResponseObject save(@RequestBody Map<String, Object> params) throws BusinessCheckException {
        String id = params.get("id") == null ? "" : params.get("id").toString();
        String name = params.get("name") == null ? "" : CommonUtil.replaceXSS(params.get("name").toString());
        String description = params.get("description") == null ? "" : CommonUtil.replaceXSS(params.get("description").toString());
        String logo = params.get("logo") == null ? "" : CommonUtil.replaceXSS(params.get("logo").toString());
        String sort = params.get("sort") == null ? "0" : params.get("sort").toString();
        String status = params.get("status") == null ? StatusEnum.ENABLED.getKey() : params.get("status").toString();
        Integer storeId = (params.get("storeId") == null || StringUtil.isEmpty(params.get("storeId").toString())) ? 0 : Integer.parseInt(params.get("storeId").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Integer myStoreId = accountInfo.getStoreId();
        if (myStoreId > 0) {
            storeId = myStoreId;
        }

        MtGoodsCate info = new MtGoodsCate();
        info.setName(name);
        info.setDescription(description);
        info.setLogo(logo);
        info.setSort(Integer.parseInt(sort));
        info.setStatus(status);
        info.setMerchantId(accountInfo.getMerchantId());
        info.setStoreId(storeId);
        info.setOperator(accountInfo.getAccountName());

        if (StringUtil.isNotEmpty(id)) {
            info.setId(Integer.parseInt(id));
            cateService.updateCate(info);
        } else {
            cateService.addCate(info);
        }

        return getSuccessResult(true);
    }

    /**
     * 商品分类详情
     */
    @ApiOperation(value = "商品分类详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('goods:cate:index')")
    public ResponseObject info(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        MtGoodsCate mtCate = cateService.queryCateById(id);
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0 && !accountInfo.getMerchantId().equals(mtCate.getMerchantId())) {
            return getFailureResult(1004);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("cateInfo", mtCate);
        result.put("imagePath", settingService.getUploadBasePath());

        return getSuccessResult(result);
    }
}

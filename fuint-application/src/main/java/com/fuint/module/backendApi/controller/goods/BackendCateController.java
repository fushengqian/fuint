package com.fuint.module.backendApi.controller.goods;

import com.fuint.common.dto.goods.GoodsCateDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.GoodsCateInfo;
import com.fuint.common.param.GoodsCatePage;
import com.fuint.common.param.StatusParam;
import com.fuint.common.service.CateService;
import com.fuint.common.service.SettingService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtGoodsCate;
import com.fuint.repository.model.MtStore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
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
        cateService.updateCate(cate, accountInfo);

        return getSuccessResult(true);
    }

    /**
     * 保存商品分类
     */
    @ApiOperation(value = "保存商品分类")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @PreAuthorize("@pms.hasPermission('goods:cate:index')")
    public ResponseObject save(@RequestBody GoodsCateInfo cateInfo) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        MtGoodsCate mtGoodsCate = new MtGoodsCate();
        BeanUtils.copyProperties(cateInfo, mtGoodsCate);
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            mtGoodsCate.setStoreId(accountInfo.getStoreId());
        }
        mtGoodsCate.setMerchantId(accountInfo.getMerchantId());
        mtGoodsCate.setOperator(accountInfo.getAccountName());

        if (cateInfo.getId() != null && cateInfo.getId() > 0) {
            cateService.updateCate(mtGoodsCate, accountInfo);
        } else {
            cateService.addCate(mtGoodsCate);
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

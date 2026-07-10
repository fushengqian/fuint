package com.fuint.module.backendApi.controller.goods;

import com.fuint.common.dto.goods.GoodsDto;
import com.fuint.common.dto.goods.StockCheckGoodsDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.StockCheckPage;
import com.fuint.common.service.SettingService;
import com.fuint.common.service.StockCheckService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.ListUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.MtGoodsMapper;
import com.fuint.repository.mapper.MtGoodsSkuMapper;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 库存盘点管理controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-库存盘点管理相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/stockCheck")
public class BackendStockCheckController extends BaseController {

    private MtGoodsMapper mtGoodsMapper;

    private MtGoodsSkuMapper mtGoodsSkuMapper;

    /**
     * 库存盘点服务接口
     */
    private StockCheckService stockCheckService;

    /**
     * 配置服务接口
     * */
    private SettingService settingService;

    /**
     * 店铺服务接口
     */
    private StoreService storeService;

    /**
     * 获取盘点记录列表
     */
    @ApiOperation(value = "获取盘点记录列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('stockCheck:index')")
    public ResponseObject list(@ModelAttribute StockCheckPage stockCheckPage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            stockCheckPage.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            stockCheckPage.setStoreId(accountInfo.getStoreId());
        }
        PaginationResponse<MtStockCheck> paginationResponse = stockCheckService.queryCheckListByPagination(stockCheckPage);

        // 店铺列表
        List<MtStore> storeList = storeService.getMyStoreList(accountInfo.getMerchantId(), accountInfo.getStoreId(), StatusEnum.ENABLED.getKey());

        Map<String, Object> result = new HashMap<>();
        result.put("imagePath", settingService.getUploadBasePath());
        result.put("storeList", storeList);
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }

    /**
     * 创建盘点任务
     */
    @ApiOperation(value = "创建盘点任务")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('stockCheck:index')")
    public ResponseObject create(@RequestBody Map<String, Object> params) throws BusinessCheckException {
        String description = params.get("description") == null ? "" : CommonUtil.replaceXSS(params.get("description").toString());
        Integer storeId = (params.get("storeId") == null || StringUtil.isEmpty(params.get("storeId").toString())) ? 0 : Integer.parseInt(params.get("storeId").toString());

        List<LinkedHashMap<String, Object>> originalMapList = (List<LinkedHashMap<String, Object>>) params.get("goodsList");
        List<StockCheckGoodsDto> goodsList = ListUtil.convertMapListToDtoList(originalMapList, StockCheckGoodsDto.class);

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Integer myStoreId = accountInfo.getStoreId();
        if (myStoreId != null && myStoreId > 0) {
            storeId = myStoreId;
        }

        MtStockCheck mtStockCheck = new MtStockCheck();
        mtStockCheck.setMerchantId(accountInfo.getMerchantId());
        mtStockCheck.setDescription(description);
        mtStockCheck.setStoreId(storeId);
        mtStockCheck.setCheckTime(new Date());
        mtStockCheck.setOperator(accountInfo.getAccountName());
        stockCheckService.createCheck(mtStockCheck, goodsList, accountInfo);

        return getSuccessResult(true);
    }

    /**
     * 获取盘点详情
     */
    @ApiOperation(value = "获取盘点详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('stockCheck:index')")
    public ResponseObject info(@PathVariable("id") Integer id) throws BusinessCheckException {
        MtStockCheck mtStockCheck = stockCheckService.queryCheckById(id);
        if (mtStockCheck == null) {
            return getFailureResult(201, "该盘点记录不存在");
        }

        Map<String, Object> param = new HashMap<>();
        param.put("CHECK_ID", mtStockCheck.getId());
        param.put("STATUS", StatusEnum.ENABLED.getKey());
        List<MtStockCheckItem> checkItems = stockCheckService.queryCheckItemsByParams(param);

        List<GoodsDto> goodsList = new ArrayList<>();
        if (checkItems != null && !checkItems.isEmpty()) {
            for (MtStockCheckItem checkItem : checkItems) {
                 MtGoods mtGoods = mtGoodsMapper.selectById(checkItem.getGoodsId());
                 if (mtGoods == null) {
                     continue;
                 }
                 GoodsDto goodsDto = new GoodsDto();
                 goodsDto.setId(mtGoods.getId());
                 goodsDto.setGoodsNo(mtGoods.getGoodsNo());
                 goodsDto.setName(mtGoods.getName());
                 goodsDto.setLogo(mtGoods.getLogo());
                 goodsDto.setStock(checkItem.getSystemStock());
                 goodsDto.setNum(checkItem.getActualStock());
                 goodsDto.setDescription(checkItem.getDescription());
                 goodsDto.setStatus(mtGoods.getStatus());
                 goodsDto.setIsSingleSpec(mtGoods.getIsSingleSpec());

                 if (checkItem.getSkuId() != null && checkItem.getSkuId() > 0) {
                     MtGoodsSku mtGoodsSku = mtGoodsSkuMapper.selectById(checkItem.getSkuId());
                     if (mtGoodsSku != null) {
                         goodsDto.setSkuId(checkItem.getSkuId());
                         goodsDto.setGoodsNo(mtGoodsSku.getSkuNo());
                         goodsDto.setStock(checkItem.getSystemStock());
                         goodsDto.setNum(checkItem.getActualStock());
                         if (StringUtil.isNotEmpty(mtGoodsSku.getLogo())) {
                             goodsDto.setLogo(mtGoodsSku.getLogo());
                         }
                     }
                 }
                 goodsList.add(goodsDto);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("imagePath", settingService.getUploadBasePath());
        result.put("checkInfo", mtStockCheck);
        result.put("goodsList", goodsList);

        return getSuccessResult(result);
    }

    /**
     * 提交盘点结果
     */
    @ApiOperation(value = "提交盘点结果")
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('stockCheck:index')")
    public ResponseObject submit(@RequestBody Map<String, Object> params) throws BusinessCheckException {
        Integer checkId = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());

        if (checkId <= 0) {
            return getFailureResult(201, "盘点ID不能为空");
        }

        List<LinkedHashMap<String, Object>> originalMapList = (List<LinkedHashMap<String, Object>>) params.get("goodsList");
        List<StockCheckGoodsDto> goodsList = ListUtil.convertMapListToDtoList(originalMapList, StockCheckGoodsDto.class);

        if (goodsList == null || goodsList.isEmpty()) {
            return getFailureResult(201, "盘点商品列表不能为空");
        }

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        stockCheckService.submitCheck(checkId, goodsList, accountInfo);

        return getSuccessResult(true);
    }

    /**
     * 删除盘点记录
     */
    @ApiOperation(value = "删除盘点记录")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('stockCheck:index')")
    public ResponseObject delete(@RequestBody Map<String, Object> params) throws BusinessCheckException {
        Integer id = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        MtStockCheck mtStockCheck = stockCheckService.queryCheckById(id);
        if (mtStockCheck == null) {
            return getFailureResult(201, "该盘点记录不存在");
        }

        stockCheckService.deleteCheck(id, accountInfo);
        return getSuccessResult(true);
    }
}

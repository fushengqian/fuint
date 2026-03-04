package com.fuint.module.backendApi.controller.goods;

import com.fuint.common.dto.goods.GoodsDto;
import com.fuint.common.dto.goods.StockGoodsDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.StockPage;
import com.fuint.common.service.SettingService;
import com.fuint.common.service.StockService;
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
 * 商品库存管理管理controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-商品库存管理相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/stock")
public class BackendStockController extends BaseController {

    private MtGoodsMapper mtGoodsMapper;

    private MtGoodsSkuMapper mtGoodsSkuMapper;

    /**
     * 商品分类服务接口
     */
    private StockService stockService;

    /**
     * 配置服务接口
     * */
    private SettingService settingService;

    /**
     * 店铺服务接口
     */
    private StoreService storeService;

    /**
     * 获取库存管理记录列表
     */
    @ApiOperation(value = "获取库存管理记录列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('stock:index')")
    public ResponseObject list(@ModelAttribute StockPage stockPage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            stockPage.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            stockPage.setStoreId(accountInfo.getStoreId());
        }
        PaginationResponse<MtStock> paginationResponse = stockService.queryStockListByPagination(stockPage);

        // 店铺列表
        List<MtStore> storeList = storeService.getMyStoreList(accountInfo.getMerchantId(), accountInfo.getStoreId(), StatusEnum.ENABLED.getKey());

        Map<String, Object> result = new HashMap<>();
        result.put("imagePath", settingService.getUploadBasePath());
        result.put("storeList", storeList);
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }

    /**
     * 删除库存管理记录状态
     */
    @ApiOperation(value = "删除库存管理记录状态")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('stock:index')")
    public ResponseObject delete(@RequestBody Map<String, Object> params) throws BusinessCheckException {
        Integer id = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        MtStock mtStock = stockService.queryStockById(id.longValue());
        if (mtStock == null) {
            return getFailureResult(201, "该数据不存在");
        }

        stockService.delete(id, accountInfo.getAccountName());
        return getSuccessResult(true);
    }

    /**
     * 保存库存管理记录
     */
    @ApiOperation(value = "保存库存管理记录")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('stock:index')")
    public ResponseObject save(@RequestBody Map<String, Object> params) throws BusinessCheckException {
        String type = params.get("type") == null ? "" : CommonUtil.replaceXSS(params.get("type").toString());
        String description = params.get("description") == null ? "" : CommonUtil.replaceXSS(params.get("description").toString());
        String status = params.get("status") == null ? StatusEnum.ENABLED.getKey() : params.get("status").toString();
        Integer storeId = (params.get("storeId") == null || StringUtil.isEmpty(params.get("storeId").toString())) ? 0 : Integer.parseInt(params.get("storeId").toString());
        List<LinkedHashMap<String, Object>> originalMapList = (List<LinkedHashMap<String, Object>>) params.get("goodsList");
        List<StockGoodsDto> goodsList = ListUtil.convertMapListToDtoList(originalMapList, StockGoodsDto.class);

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Integer myStoreId = accountInfo.getStoreId();
        if (myStoreId != null && myStoreId > 0) {
            storeId = myStoreId;
        }

        MtStock mtStock = new MtStock();
        mtStock.setMerchantId(accountInfo.getMerchantId());
        mtStock.setDescription(description);
        mtStock.setStatus(status);
        mtStock.setStoreId(storeId);
        mtStock.setType(type);
        String operator = accountInfo.getAccountName();
        mtStock.setOperator(operator);
        stockService.addStock(mtStock, goodsList);

        return getSuccessResult(true);
    }

    /**
     * 获取库存管理记录详情
     */
    @ApiOperation(value = "获取库存管理记录详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('stock:index')")
    public ResponseObject info(@PathVariable("id") Integer id) throws BusinessCheckException {
        MtStock mtStock = stockService.queryStockById(id.longValue());
        Map<String, Object> param = new HashMap<>();
        param.put("STOCK_ID", mtStock.getId());
        List<MtStockItem> stockItems = stockService.queryItemByParams(param);
        List<GoodsDto> goodsList = new ArrayList<>();
        if (stockItems != null && stockItems.size() > 0) {
            for (MtStockItem stockItem : stockItems) {
                 MtGoods mtGoods = mtGoodsMapper.selectById(stockItem.getGoodsId());
                 GoodsDto goodsDto = new GoodsDto();
                 goodsDto.setGoodsNo(mtGoods.getGoodsNo());
                 goodsDto.setName(mtGoods.getName());
                 goodsDto.setLogo(mtGoods.getLogo());
                 goodsDto.setNum(stockItem.getNum());
                 goodsDto.setStatus(mtGoods.getStatus());
                 if (stockItem.getSkuId() != null && stockItem.getSkuId() > 0) {
                     MtGoodsSku mtGoodsSku = mtGoodsSkuMapper.selectById(stockItem.getSkuId());
                     if (mtGoodsSku != null) {
                         goodsDto.setGoodsNo(mtGoodsSku.getSkuNo());
                         if (StringUtil.isNotEmpty(mtGoodsSku.getLogo())) {
                             goodsDto.setLogo(mtGoodsSku.getLogo());
                         }
                     }
                 }
                 goodsList.add(goodsDto);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("stockInfo", mtStock);
        result.put("goodsList", goodsList);

        return getSuccessResult(result);
    }
}

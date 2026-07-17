package com.fuint.module.merchantApi.controller;

import com.fuint.common.dto.goods.StockCheckGoodsDto;
import com.fuint.common.dto.member.UserInfo;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.StockCheckPage;
import com.fuint.common.service.*;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.MtGoodsSkuMapper;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 商户端-库存盘点controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="商户端-库存盘点相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/merchantApi/stockCheck")
public class MerchantStockCheckController extends BaseController {

    /**
     * 库存盘点服务接口
     */
    private StockCheckService stockCheckService;

    /**
     * 商品服务接口
     */
    private GoodsService goodsService;

    /**
     * 会员服务接口
     */
    private MemberService memberService;

    /**
     * 店铺员工服务接口
     */
    private StaffService staffService;

    /**
     * 系统设置服务接口
     */
    private SettingService settingService;

    /**
     * 商品SKU Mapper
     */
    private MtGoodsSkuMapper mtGoodsSkuMapper;

    /**
     * 获取盘点记录列表
     */
    @ApiOperation(value = "获取盘点记录列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(@RequestBody StockCheckPage param) throws BusinessCheckException {
        MtStaff staffInfo = getStaffInfo();
        param.setMerchantId(staffInfo.getMerchantId());
        if (staffInfo.getStoreId() != null && staffInfo.getStoreId() > 0) {
            param.setStoreId(staffInfo.getStoreId());
        }

        PaginationResponse<MtStockCheck> paginationResponse = stockCheckService.queryCheckListByPagination(param);

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }

    /**
     * 获取盘点详情（含商品明细）
     */
    @ApiOperation(value = "获取盘点详情")
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject detail(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        MtStaff staffInfo = getStaffInfo();

        String checkId = param.get("checkId") != null ? param.get("checkId").toString() : "";
        if (StringUtil.isEmpty(checkId)) {
            return getFailureResult(201, "盘点ID不能为空");
        }

        MtStockCheck stockCheck = stockCheckService.queryCheckById(Integer.parseInt(checkId));
        if (stockCheck == null) {
            return getFailureResult(201, "盘点记录不存在");
        }
        if (!staffInfo.getMerchantId().equals(stockCheck.getMerchantId())) {
            return getFailureResult(201, "您没有操作权限");
        }

        // 查询盘点明细
        Map<String, Object> itemParams = new HashMap<>();
        itemParams.put("CHECK_ID", Integer.parseInt(checkId));
        itemParams.put("STATUS", StatusEnum.ENABLED.getKey());
        List<MtStockCheckItem> items = stockCheckService.queryCheckItemsByParams(itemParams);

        // 构建包含商品信息的明细列表
        List<Map<String, Object>> itemList = new ArrayList<>();
        if (items != null) {
            for (MtStockCheckItem item : items) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("id", item.getId());
                itemMap.put("checkId", item.getCheckId());
                itemMap.put("goodsId", item.getGoodsId());
                itemMap.put("skuId", item.getSkuId());
                itemMap.put("systemStock", item.getSystemStock());
                itemMap.put("actualStock", item.getActualStock());
                itemMap.put("diffStock", item.getDiffStock());
                itemMap.put("description", item.getDescription());

                // 查询商品信息
                MtGoods goodsInfo = goodsService.queryGoodsById(item.getGoodsId());
                if (goodsInfo != null) {
                    itemMap.put("goodsName", goodsInfo.getName());
                    itemMap.put("goodsLogo", goodsInfo.getLogo());
                    itemMap.put("goodsPrice", goodsInfo.getPrice());
                    itemMap.put("isSingleSpec", goodsInfo.getIsSingleSpec());

                    // 如果是多规格，查询SKU信息
                    if (item.getSkuId() != null && item.getSkuId() > 0) {
                        MtGoodsSku skuInfo = mtGoodsSkuMapper.selectById(item.getSkuId());
                        if (skuInfo != null) {
                            itemMap.put("skuName", skuInfo.getSkuNo());
                        }
                    }
                }

                itemList.add(itemMap);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("stockCheck", stockCheck);
        result.put("itemList", itemList);
        result.put("imagePath", settingService.getUploadBasePath());

        return getSuccessResult(result);
    }

    /**
     * 创建盘点任务
     */
    @ApiOperation(value = "创建盘点任务")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject create(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        MtStaff staffInfo = getStaffInfo();

        String description = param.get("description") == null ? "" : param.get("description").toString();
        List<Map<String, Object>> goodsListParam = param.get("goodsList") == null ? new ArrayList<>()
                : (List<Map<String, Object>>) param.get("goodsList");

        if (goodsListParam.isEmpty()) {
            return getFailureResult(201, "盘点商品列表不能为空");
        }

        // 构建盘点商品DTO列表
        List<StockCheckGoodsDto> goodsList = new ArrayList<>();
        for (Map<String, Object> item : goodsListParam) {
            StockCheckGoodsDto dto = new StockCheckGoodsDto();
            Object goodsIdObj = item.get("goodsId");
            if (goodsIdObj != null) {
                dto.setGoodsId(Integer.parseInt(goodsIdObj.toString()));
                dto.setId(Integer.parseInt(goodsIdObj.toString()));
            }
            Object skuIdObj = item.get("skuId");
            if (skuIdObj != null && StringUtil.isNotEmpty(skuIdObj.toString())) {
                dto.setSkuId(Integer.parseInt(skuIdObj.toString()));
            }
            Object descObj = item.get("description");
            if (descObj != null) {
                dto.setDescription(descObj.toString());
            }
            goodsList.add(dto);
        }

        MtStockCheck mtStockCheck = new MtStockCheck();
        mtStockCheck.setStoreId(staffInfo.getStoreId());
        mtStockCheck.setDescription(description);
        mtStockCheck.setCheckTime(new Date());

        AccountInfo accountInfo = buildAccountInfo(staffInfo);
        MtStockCheck result = stockCheckService.createCheck(mtStockCheck, goodsList, accountInfo);

        return getSuccessResult(result);
    }

    /**
     * 提交盘点结果
     */
    @ApiOperation(value = "提交盘点结果")
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject submit(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        MtStaff staffInfo = getStaffInfo();

        String checkId = param.get("checkId") == null ? "" : param.get("checkId").toString();
        if (StringUtil.isEmpty(checkId)) {
            return getFailureResult(201, "盘点ID不能为空");
        }

        List<Map<String, Object>> goodsListParam = param.get("goodsList") == null ? new ArrayList<>()
                : (List<Map<String, Object>>) param.get("goodsList");

        if (goodsListParam.isEmpty()) {
            return getFailureResult(201, "盘点商品列表不能为空");
        }

        // 构建盘点商品DTO列表
        List<StockCheckGoodsDto> goodsList = new ArrayList<>();
        for (Map<String, Object> item : goodsListParam) {
            StockCheckGoodsDto dto = new StockCheckGoodsDto();
            Object goodsIdObj = item.get("goodsId");
            if (goodsIdObj != null) {
                dto.setGoodsId(Integer.parseInt(goodsIdObj.toString()));
                dto.setId(Integer.parseInt(goodsIdObj.toString()));
            }
            Object skuIdObj = item.get("skuId");
            if (skuIdObj != null && StringUtil.isNotEmpty(skuIdObj.toString())) {
                dto.setSkuId(Integer.parseInt(skuIdObj.toString()));
            }
            Object actualStockObj = item.get("actualStock");
            if (actualStockObj != null) {
                dto.setActualStock(Double.parseDouble(actualStockObj.toString()));
            }
            Object descObj = item.get("description");
            if (descObj != null) {
                dto.setDescription(descObj.toString());
            }
            goodsList.add(dto);
        }

        AccountInfo accountInfo = buildAccountInfo(staffInfo);
        MtStockCheck result = stockCheckService.submitCheck(Integer.parseInt(checkId), goodsList, accountInfo);

        return getSuccessResult(result);
    }

    /**
     * 删除盘点记录
     */
    @ApiOperation(value = "删除盘点记录")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject delete(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        MtStaff staffInfo = getStaffInfo();

        String checkId = param.get("checkId") == null ? "" : param.get("checkId").toString();
        if (StringUtil.isEmpty(checkId)) {
            return getFailureResult(201, "盘点ID不能为空");
        }

        AccountInfo accountInfo = buildAccountInfo(staffInfo);
        stockCheckService.deleteCheck(Integer.parseInt(checkId), accountInfo);

        return getSuccessResult(true);
    }

    /**
     * 获取当前员工信息
     */
    private MtStaff getStaffInfo() throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfo();
        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        MtStaff staffInfo = staffService.queryStaffByMobile(mtUser.getMobile());
        if (staffInfo == null) {
            throw new BusinessCheckException("您不是商户员工，没有操作权限");
        }
        return staffInfo;
    }

    /**
     * 构造操作人信息
     */
    private AccountInfo buildAccountInfo(MtStaff staffInfo) {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountName(staffInfo.getRealName());
        accountInfo.setMerchantId(staffInfo.getMerchantId());
        accountInfo.setStoreId(staffInfo.getStoreId());
        return accountInfo;
    }
}

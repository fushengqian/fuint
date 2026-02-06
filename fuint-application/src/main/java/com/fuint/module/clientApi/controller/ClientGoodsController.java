package com.fuint.module.clientApi.controller;

import com.alibaba.fastjson.JSONObject;
import com.fuint.common.dto.*;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.YesOrNoEnum;
import com.fuint.common.param.GoodsInfoParam;
import com.fuint.common.param.GoodsListParam;
import com.fuint.common.service.CateService;
import com.fuint.common.service.GoodsService;
import com.fuint.common.service.MerchantService;
import com.fuint.common.service.SettingService;
import com.fuint.common.util.CommonUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtGoods;
import com.fuint.repository.model.MtGoodsCate;
import com.fuint.repository.model.MtGoodsSku;
import com.fuint.repository.model.MtGoodsSpec;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 商品类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-商品相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/goodsApi")
public class ClientGoodsController extends BaseController {

    /**
     * 商品服务接口
     * */
    private GoodsService goodsService;

    /**
     * 商品类别服务接口
     * */
    private CateService cateService;

    /**
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 商户服务接口
     */
    private MerchantService merchantService;

    /**
     * 获取商品分类列表
     */
    @ApiOperation(value = "获取商品分类列表")
    @RequestMapping(value = "/cateList", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject cateList(HttpServletRequest request) {
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        Integer storeId = StringUtil.isEmpty(request.getHeader("storeId")) ? 0 : Integer.parseInt(request.getHeader("storeId"));
        String platform = request.getHeader("platform") == null ? "" : request.getHeader("platform");
        Integer merchantId = merchantService.getMerchantId(merchantNo);
        String baseImage = settingService.getUploadBasePath();
        List<MtGoodsCate> cateList = cateService.getCateList(merchantId, storeId, null, StatusEnum.ENABLED.getKey());
        List<ResCateDto> result = new ArrayList<>();
        for (MtGoodsCate cate : cateList) {
             ResCateDto dto = new ResCateDto();
             dto.setCateId(cate.getId());
             dto.setName(cate.getName());
             if (StringUtil.isNotEmpty(cate.getLogo())) {
                 dto.setLogo(baseImage + cate.getLogo());
             }
             Map<String, Object> goodsData = goodsService.getStoreGoodsList(storeId, "", platform, cate.getId(), 1, 100);
             List<MtGoods> goodsList = (ArrayList)goodsData.get("goodsList");
             if (goodsList.size() > 0) {
                 for (MtGoods goods : goodsList) {
                      goods.setLogo(baseImage + goods.getLogo());
                 }
             }
             List<MtGoods> goodsArr = new ArrayList<>();
             for (MtGoods goods : goodsList) {
                  if (goods.getCateId().compareTo(cate.getId()) == 0) {
                      goodsArr.add(goods);
                  }
             }
             dto.setGoodsList(goodsArr);
             dto.setSort((goodsArr.size() > 0) ? 1 : 0);
             result.add(dto);
        }
        // 商品数量为0就排在后面
        Collections.sort(result, (p1, p2) -> Integer.compare(p2.getSort(), p1.getSort()));
        return getSuccessResult(result);
    }

    /**
     * 获取商品列表
     */
    @ApiOperation(value = "获取商品列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        Integer storeId = StringUtil.isEmpty(request.getHeader("storeId")) ? 0 : Integer.parseInt(request.getHeader("storeId"));
        String platform = request.getHeader("platform") == null ? "" : request.getHeader("platform");
        Map<String, Object> goodsData = goodsService.getStoreGoodsList(storeId, "", platform, 0,1, 200);
        return getSuccessResult(goodsData.get("goodsList"));
    }

    /**
     * 搜索商品
     */
    @ApiOperation(value = "搜索商品")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject search(HttpServletRequest request, @RequestBody GoodsListParam params) {
        Integer storeId = StringUtil.isEmpty(request.getHeader("storeId")) ? 0 : Integer.parseInt(request.getHeader("storeId"));
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        String platform = request.getHeader("platform") == null ? "" : request.getHeader("platform");
        Integer merchantId = merchantService.getMerchantId(merchantNo);
        if (merchantId > 0 ) {
            params.setMerchantId(merchantId);
        }
        if (storeId > 0) {
            params.setStoreId(storeId);
        }
        if (StringUtil.isNotBlank(platform)) {
            params.setPlatform(platform);
        }
        params.setStatus(StatusEnum.ENABLED.getKey());
        params.setHasPrice(YesOrNoEnum.YES.getKey());
        PaginationResponse<GoodsDto> paginationResponse = goodsService.queryGoodsListByPagination(params);
        return getSuccessResult(paginationResponse);
    }

    /**
     * 获取商品详情
     */
    @ApiOperation(value = "获取商品详情")
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject detail(@RequestBody GoodsInfoParam goodsInfoParam) throws BusinessCheckException {
        String goodsId = goodsInfoParam.getGoodsId() == null ? "0" : goodsInfoParam.getGoodsId();
        if (StringUtil.isEmpty(goodsId)) {
            return getFailureResult(2000, "商品ID不能为空");
        }

        GoodsDto goodsDto = goodsService.getGoodsDetail(Integer.parseInt(goodsId), false);

        GoodsDetailDto goodsDetailDto = new GoodsDetailDto();
        goodsDetailDto.setGoodsNo(goodsDto.getGoodsNo());
        goodsDetailDto.setGoodsId(goodsDto.getId());
        goodsDetailDto.setName(goodsDto.getName());
        goodsDetailDto.setCateId(goodsDto.getCateId());
        goodsDetailDto.setPrice(goodsDto.getPrice());
        goodsDetailDto.setLinePrice(goodsDto.getLinePrice());
        goodsDetailDto.setSalePoint(goodsDto.getSalePoint());
        goodsDetailDto.setSort(goodsDto.getSort());
        goodsDetailDto.setCanUsePoint(goodsDto.getCanUsePoint());
        goodsDetailDto.setIsMemberDiscount(goodsDto.getIsMemberDiscount());

        List<String> images = JSONObject.parseArray(goodsDto.getImages(), String.class);
        List<String> imageList = new ArrayList<>();
        String baseImage = settingService.getUploadBasePath();
        for (String image : images) {
            imageList.add((baseImage + image));
        }
        goodsDetailDto.setImages(imageList);

        goodsDetailDto.setIsSingleSpec(goodsDto.getIsSingleSpec());
        goodsDetailDto.setLogo(goodsDto.getLogo());
        goodsDetailDto.setStock(goodsDto.getStock());
        goodsDetailDto.setWeight(goodsDto.getWeight());
        goodsDetailDto.setDescription(goodsDto.getDescription());
        if (StringUtil.isNotEmpty(goodsDetailDto.getDescription())) {
            goodsDetailDto.setDescription(CommonUtil.fixVideo(goodsDetailDto.getDescription()));
        }
        goodsDetailDto.setInitSale(goodsDto.getInitSale());
        goodsDetailDto.setStatus(goodsDto.getStatus());

        // 商品规格列表
        List<MtGoodsSpec> goodsSpecList = goodsDto.getSpecList();
        List<String> specNameArr = new ArrayList<>();
        List<MtGoodsSpec> specArr = new ArrayList<>();
        for (MtGoodsSpec mtGoodsSpec : goodsSpecList) {
            if (!specNameArr.contains(mtGoodsSpec.getName())) {
                MtGoodsSpec spec = new MtGoodsSpec();
                spec.setId(mtGoodsSpec.getId());
                spec.setName(mtGoodsSpec.getName());
                specArr.add(spec);
                specNameArr.add(mtGoodsSpec.getName());
            }
        }
        List<GoodsSpecDto> specDtoList = new ArrayList<>();
        for (MtGoodsSpec mtSpec : specArr) {
            GoodsSpecDto dto = new GoodsSpecDto();
            dto.setSpecId(mtSpec.getId());
            dto.setName(mtSpec.getName());
            List<GoodsSpecValueDto> valueList = new ArrayList<>();
            for (MtGoodsSpec spec : goodsSpecList) {
                 if (spec.getName().equals(mtSpec.getName())) {
                     GoodsSpecValueDto valueDto = new GoodsSpecValueDto();
                     valueDto.setSpecValue(spec.getValue());
                     valueDto.setSpecValueId(spec.getId());
                     valueList.add(valueDto);
                 }
            }
            dto.setValueList(valueList);
            if (!goodsDetailDto.getIsSingleSpec().equals(YesOrNoEnum.YES.getKey())) {
                specDtoList.add(dto);
            }
        }

        // sku列表
        List<MtGoodsSku> goodsSkuList = goodsDto.getSkuList();
        List<GoodsSkuDto> skuDtoList = new ArrayList<>();
        String basePath = settingService.getUploadBasePath();
        for (MtGoodsSku sku : goodsSkuList) {
             GoodsSkuDto dto = new GoodsSkuDto();
             dto.setId(sku.getId());
             if (sku.getLogo() != null && StringUtil.isNotEmpty(sku.getLogo())) {
                 dto.setLogo(basePath + sku.getLogo());
             } else {
                 dto.setLogo(goodsDetailDto.getLogo());
             }
             dto.setGoodsId(sku.getGoodsId());
             dto.setSkuNo(sku.getSkuNo());
             dto.setPrice(sku.getPrice());
             dto.setLinePrice(sku.getLinePrice());
             dto.setStock(sku.getStock());
             dto.setWeight(sku.getWeight());
             dto.setSpecIds(sku.getSpecIds());
             skuDtoList.add(dto);
        }

        if (goodsDetailDto.getIsSingleSpec().equals(YesOrNoEnum.YES.getKey())) {
            GoodsSkuDto dto = new GoodsSkuDto();
            dto.setId(0);
            dto.setLogo(goodsDetailDto.getLogo());
            dto.setGoodsId(goodsDetailDto.getGoodsId());
            dto.setSkuNo("");
            dto.setPrice(goodsDetailDto.getPrice());
            dto.setLinePrice(goodsDetailDto.getLinePrice());
            dto.setStock(goodsDetailDto.getStock());
            dto.setWeight(goodsDetailDto.getWeight());
            dto.setSpecIds("");
            skuDtoList.add(dto);
        }

        goodsDetailDto.setSpecList(specDtoList);
        goodsDetailDto.setSkuList(skuDtoList);

        return getSuccessResult(goodsDetailDto);
    }

    /**
     * 通过sku编码获取商品信息
     * */
    @ApiOperation(value = "通过sku编码获取商品信息")
    @RequestMapping(value = "/getGoodsInfoBySkuNo", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject getGoodsInfoBySkuNo(HttpServletRequest request, @RequestBody GoodsInfoParam goodsInfoParam) throws BusinessCheckException {
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        String skuNo = goodsInfoParam.getSkuNo() == null ? "" : goodsInfoParam.getSkuNo();
        if (StringUtil.isEmpty(skuNo)) {
            return getFailureResult(201, "商品编码不能为空");
        }
        Integer merchantId = merchantService.getMerchantId(merchantNo);
        Integer goodsId = 0;
        Integer skuId = 0;
        MtGoodsSku mtGoodsSku = goodsService.getSkuInfoBySkuNo(skuNo);
        if (mtGoodsSku == null) {
            MtGoods mtGoods = goodsService.queryGoodsByGoodsNo(merchantId, skuNo);
            if (mtGoods != null) {
                goodsId = mtGoods.getId();
            }
        } else {
            goodsId = mtGoodsSku.getGoodsId();
            skuId = mtGoodsSku.getId();
        }

        if (goodsId > 0) {
            GoodsDto goodsDto = goodsService.getGoodsDetail(goodsId, false);
            Map<String, Object> data = new HashMap();
            data.put("skuId", skuId);
            data.put("goodsInfo", goodsDto);
            return getSuccessResult(data);
        } else {
            return getFailureResult(201, "未查询到商品信息");
        }
    }
}

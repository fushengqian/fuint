package com.fuint.module.clientApi.controller;

import com.alibaba.fastjson.JSONObject;
import com.fuint.common.Constants;
import com.fuint.common.dto.*;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.YesOrNoEnum;
import com.fuint.common.service.CateService;
import com.fuint.common.service.GoodsService;
import com.fuint.common.service.SettingService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtGoods;
import com.fuint.repository.model.MtGoodsCate;
import com.fuint.repository.model.MtGoodsSku;
import com.fuint.repository.model.MtGoodsSpec;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-商品相关接口")
@RestController
@RequestMapping(value = "/clientApi/goodsApi")
public class ClientGoodsController extends BaseController {

    /**
     * 商品服务接口
     * */
    @Autowired
    private GoodsService goodsService;

    /**
     * 商品类别服务接口
     * */
    @Autowired
    private CateService cateService;

    @Autowired
    private SettingService settingService;

    /**
     * 获取商品分类列表
     */
    @RequestMapping(value = "/cateList", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject cateList(HttpServletRequest request) throws BusinessCheckException {
        Integer storeId = request.getHeader("storeId") == null ? 0 : Integer.parseInt(request.getHeader("storeId"));

        Map<String, Object> param = new HashMap<>();
        param.put("status", StatusEnum.ENABLED.getKey());
        List<MtGoodsCate> cateList = cateService.queryCateListByParams(param);

        List<MtGoods> goodsList = goodsService.getStoreGoodsList(storeId, "");

        String baseImage = settingService.getUploadBasePath();
        if (goodsList.size() > 0) {
            for (MtGoods goods : goodsList) {
                goods.setLogo(baseImage + goods.getLogo());
            }
        }

        List<ResCateDto> result = new ArrayList<>();
        for (MtGoodsCate cate : cateList) {
            ResCateDto dto = new ResCateDto();
            dto.setCateId(cate.getId());
            dto.setName(cate.getName());
            dto.setLogo(cate.getLogo());
            List<MtGoods> goodsArr = new ArrayList<>();
            for (MtGoods goods : goodsList) {
                if (goods.getCateId() == cate.getId()) {
                    goodsArr.add(goods);
                }
            }
            dto.setGoodsList(goodsArr);
            result.add(dto);
        }

        return getSuccessResult(result);
    }

    /**
     * 获取商品列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        Integer storeId = request.getHeader("storeId") == null ? 0 : Integer.parseInt(request.getHeader("storeId"));
        List<MtGoods> goodsList = goodsService.getStoreGoodsList(storeId, "");
        return getSuccessResult(goodsList);
    }

    /**
     * 搜索商品
     * */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject search(@RequestBody Map<String, Object> params) throws BusinessCheckException {
        Integer page = params.get("page") == null ? 1 : Integer.parseInt(params.get("page").toString());
        Integer pageSize = params.get("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(params.get("pageSize").toString());
        String name = params.get("name") == null ? "" : params.get("name").toString();
        Integer cateId = params.get("cateId") == null ? 0 : Integer.parseInt(params.get("cateId").toString());

        PaginationRequest paginationRequest = new PaginationRequest();

        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("status", StatusEnum.ENABLED.getKey());
        if (cateId > 0) {
            searchParams.put("cateId", cateId);
        }
        if (StringUtil.isNotEmpty(name)) {
            searchParams.put("name", name);
        }

        paginationRequest.setSearchParams(searchParams);
        paginationRequest.setSortColumn(new String[]{"sort asc", "id desc"});
        PaginationResponse<GoodsDto> paginationResponse = goodsService.queryGoodsListByPagination(paginationRequest);

        return getSuccessResult(paginationResponse);
    }

    /**
     * 获取商品详情
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request) throws BusinessCheckException, InvocationTargetException, IllegalAccessException {
        String goodsId = request.getParameter("goodsId") == null ? "0" : request.getParameter("goodsId");
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
            specDtoList.add(dto);
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
    @RequestMapping(value = "/getGoodsInfoBySkuNo", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getGoodsInfoBySkuNo(HttpServletRequest request) throws BusinessCheckException, InvocationTargetException, IllegalAccessException {
        String skuNo = request.getParameter("skuNo") == null ? "" : request.getParameter("skuNo");
        if (StringUtil.isEmpty(skuNo)) {
            return getFailureResult(201, "商品编码不能为空");
        }

        Integer goodsId = 0;
        Integer skuId = 0;
        MtGoodsSku mtGoodsSku = goodsService.getSkuInfoBySkuNo(skuNo);
        if (mtGoodsSku == null) {
            MtGoods mtGoods = goodsService.queryGoodsByGoodsNo(skuNo);
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

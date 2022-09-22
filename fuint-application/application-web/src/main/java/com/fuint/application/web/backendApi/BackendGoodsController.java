package com.fuint.application.web.backendApi;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;
import com.fuint.application.config.Constants;
import com.fuint.application.dao.repositories.MtGoodsSpecRepository;
import com.fuint.application.dao.repositories.MtGoodsSkuRepository;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.goods.CateService;
import com.fuint.application.service.setting.SettingService;
import com.fuint.application.service.store.StoreService;
import com.fuint.application.service.token.TokenService;
import com.fuint.application.util.CommonUtil;
import com.fuint.base.dao.entities.TAccount;
import com.fuint.base.service.account.TAccountService;
import com.fuint.exception.BusinessCheckException;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.application.dao.entities.*;
import com.fuint.application.dto.*;
import com.fuint.application.dto.GoodsSpecItemDto;
import com.fuint.application.service.goods.GoodsService;
import com.fuint.util.StringUtil;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.util.RequestHandler;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 商品管理controller
 *
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/backendApi/goods/goods")
public class BackendGoodsController extends BaseController {

    /**
     * 商品服务接口
     */
    @Autowired
    private GoodsService goodsService;

    /**
     * 商品分类服务接口
     */
    @Autowired
    private CateService cateService;

    /**
     * 店铺服务接口
     */
    @Autowired
    private StoreService storeService;

    /**
     * 后台账户服务接口
     */
    @Autowired
    private TAccountService accountService;

    /**
     * 登录令牌接口
     * */
    @Autowired
    private TokenService tokenService;

    @Autowired
    private MtGoodsSpecRepository specRepository;

    @Autowired
    private MtGoodsSkuRepository goodSkuRepository;

    @Autowired
    private SettingService settingService;

    /**
     * 查询列表
     *
     * @param request
     * @return
     * @throws BusinessCheckException
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String name = request.getParameter("name");
        String goodsNo = request.getParameter("goodsNo");
        String status = request.getParameter("status");

        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
        }

        TAccount account = accountService.findAccountById(accountInfo.getId());
        Integer storeId = account.getStoreId();

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        if (storeId > 0) {
            params.put("EQ_storeId", storeId.toString());
        }
        if (StringUtil.isNotEmpty(name)) {
            params.put("LIKE_name", name);
        }
        if (StringUtil.isNotEmpty(goodsNo)) {
            params.put("EQ_goodsNo", goodsNo);
        }
        if (StringUtil.isNotEmpty(status)) {
            params.put("EQ_status", status);
        }

        params.put("NQ_status", StatusEnum.DISABLE.getKey());
        paginationRequest.setSearchParams(params);
        paginationRequest.setSortColumn(new String[]{"status asc", "updateTime desc"});
        PaginationResponse<GoodsDto> paginationResponse = goodsService.queryGoodsListByPagination(paginationRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }

    /**
     * 删除商品
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject delete(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
        }

        String operator = accountInfo.getAccountName();
        goodsService.deleteGoods(id, operator);

        return getSuccessResult(true);
    }

    /**
     * 商品详情
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request) throws BusinessCheckException, InvocationTargetException, IllegalAccessException {
        String token = request.getHeader("Access-Token");
        Integer goodsId = request.getParameter("goodsId") == null ? 0 : Integer.parseInt(request.getParameter("goodsId"));

        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
        }

        TAccount account = accountService.findAccountById(accountInfo.getId());
        GoodsDto goods = goodsService.getGoodsDetail(goodsId, false);

        Map<String, Object> result = new HashMap<>();
        result.put("goods", goods);

        List<String> images = new ArrayList<>();
        if (goods != null) {
            images = JSONArray.parseArray(goods.getImages(), String.class);
        }
        result.put("images", images);

        // 商品规格列表
        List<String> specKeyArr = new ArrayList<>();
        List<GoodsSpecItemDto> specArr = new ArrayList<>();
        if (goods != null) {
            for (MtGoodsSpec mtGoodsSpec : goods.getSpecList()) {
                if (!specKeyArr.contains(mtGoodsSpec.getName())) {
                    specKeyArr.add(mtGoodsSpec.getName());
                }
            }

            int id = 1;
            for (String name : specKeyArr) {
                GoodsSpecItemDto item = new GoodsSpecItemDto();
                List<GoodsSpecChildDto> child = new ArrayList<>();
                for (MtGoodsSpec mtGoodsSpec : goods.getSpecList()) {
                    if (mtGoodsSpec.getName().equals(name)) {
                        GoodsSpecChildDto e = new GoodsSpecChildDto();
                        e.setId(mtGoodsSpec.getId());
                        e.setName(mtGoodsSpec.getValue());
                        e.setChecked(true);
                        child.add(e);
                    }
                }
                item.setId(id);
                item.setName(name);
                item.setChild(child);
                specArr.add(item);
                id++;
            }
        }

        Map<String, Object> param = new HashMap<>();
        param.put("EQ_status", StatusEnum.ENABLED.getKey());
        List<MtGoodsCate> cateList = cateService.queryCateListByParams(param);
        result.put("cateList", cateList);

        String imagePath = settingService.getUploadBasePath();
        result.put("imagePath", imagePath);

        String specData = JSONObject.toJSONString(specArr);
        result.put("specData", specData);

        Map<String, Object> skuData = new HashMap<>();
        if (goods != null) {
            for (MtGoodsSku sku : goods.getSkuList()) {
                skuData.put("skus[" + sku.getSpecIds() + "][skuNo]", sku.getSkuNo());
                skuData.put("skus[" + sku.getSpecIds() + "][logo]", (sku.getLogo().length() > 1 ? (imagePath + sku.getLogo()) : ""));
                skuData.put("skus[" + sku.getSpecIds() + "][goodsId]", sku.getGoodsId());
                skuData.put("skus[" + sku.getSpecIds() + "][stock]", sku.getStock());
                skuData.put("skus[" + sku.getSpecIds() + "][price]", sku.getPrice());
                skuData.put("skus[" + sku.getSpecIds() + "][linePrice]", sku.getLinePrice());
                skuData.put("skus[" + sku.getSpecIds() + "][weight]", sku.getWeight());
            }
        }
        result.put("skuData", JSONObject.toJSONString(skuData));

        Map<String, Object> paramsStore = new HashMap<>();
        paramsStore.put("EQ_status", StatusEnum.ENABLED.getKey());
        List<MtStore> storeList = storeService.queryStoresByParams(paramsStore);
        result.put("storeList", storeList);

        Integer storeId = account.getStoreId();
        result.put("storeId", storeId);

        return getSuccessResult(result);
    }

    /**
     * 保存商品信息
     *
     * @param request  HttpServletRequest对象
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject saveHandler(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
        }

        String goodsId = param.get("goodsId") == null ? "0" : param.get("goodsId").toString();
        if (StringUtil.isEmpty(goodsId)) {
            goodsId = "0";
        }

        String name = CommonUtil.replaceXSS(param.get("name").toString());
        String description = param.get("editorValue") == null ? "" : param.get("editorValue").toString();
        String images[] = param.get("image") == null ? new String[0] : request.getParameterValues("image");
        String sort = param.get("sort") == null ? "" : param.get("sort").toString();
        String stock = param.get("stock") == null ? "" : param.get("stock").toString();
        String status = param.get("status") == null ? "" : param.get("status").toString();
        String goodsNo = param.get("goodsNo") == null ? "" : param.get("goodsNo").toString();
        String price = param.get("price") == null ? "" : param.get("price").toString();
        String linePrice = param.get("linePrice") == null ? "" : param.get("linePrice").toString();
        String weight = param.get("weight") == null ? "" : param.get("weight").toString();
        Integer initSale = param.get("initSale") == null ? 0 : Integer.parseInt(param.get("initSale").toString());
        String salePoint = param.get("salePoint") == null ? "" : param.get("salePoint").toString();
        String canUsePoint = param.get("canUsePoint") == null ? "" : param.get("canUsePoint").toString();
        String isMemberDiscount = param.get("isMemberDiscount") == null ? "" : param.get("isMemberDiscount").toString();
        String isSingleSpec = param.get("isSingleSpec") == null ? "" : param.get("isSingleSpec").toString();
        Integer cateId = param.get("cateId") == null ? 0 : Integer.parseInt(param.get("cateId").toString());
        Integer storeId = param.get("storeId") == null ? 0 : Integer.parseInt(param.get("storeId").toString());

        // 去除空值
        if (images.length > 0) {
            List<String> tempArr = new ArrayList<>();
            for (String img : images) {
                 if (StringUtil.isNotEmpty(img)) {
                     tempArr.add(img);
                 }
            }
            images = tempArr.toArray(new String[0]);
        }

        Enumeration skuMap = request.getParameterNames();
        List<String> dataArr = new ArrayList<>();
        List<String> item = new ArrayList<>();
        String imagePath = settingService.getUploadBasePath();

        while (skuMap.hasMoreElements()) {
            String paramName = (String)skuMap.nextElement();
            if (paramName.contains("skus")) {
                String paramValue = request.getParameter(paramName);
                paramName = paramName.replace("[", "_");
                paramName = paramName.replace("]", "");
                String[] s1 = paramName.split("_"); // skus[5-7][image]  skus_5-7_image
                dataArr.add(s1[1] + '_' + s1[2] + '_' + paramValue);
                if (!item.contains(s1[1])) {
                    item.add(s1[1]);
                }
            }
        }

        BigDecimal minPrice = new BigDecimal("0");
        BigDecimal minLinePrice = new BigDecimal("0");

        // 先删除旧的sku数据
        if (item.size() > 0) {
            Map<String, Object> param0 = new HashMap<>();
            param0.put("EQ_goodsId", goodsId);
            Specification<MtGoodsSku> specification = goodSkuRepository.buildSpecification(param0);
            Sort sort0 = new Sort(Sort.Direction.ASC, "id");
            List<MtGoodsSku> goodsSkuList = goodSkuRepository.findAll(specification, sort0);
            if (goodsSkuList.size() > 0) {
                for (MtGoodsSku mtGoodsSku : goodsSkuList) {
                     if (!item.contains(mtGoodsSku.getSpecIds())) {
                         goodSkuRepository.delete(mtGoodsSku.getId());
                     }
                }
            }
        }

        for (String key : item) {
            Map<String, Object> params = new HashMap<>();
            params.put("EQ_goodsId", goodsId);
            params.put("EQ_specIds", key);

            // 是否已存在
            Specification<MtGoodsSku> specification2 = goodSkuRepository.buildSpecification(params);
            Sort sort2 = new Sort(Sort.Direction.ASC, "id");
            List<MtGoodsSku> goodsSkuList = goodSkuRepository.findAll(specification2, sort2);
            MtGoodsSku sku = new MtGoodsSku();
            if (goodsSkuList.size() > 0) {
                sku = goodsSkuList.get(0);
            }

            sku.setGoodsId(Integer.parseInt(goodsId));
            sku.setSpecIds(key);
            sku.setStatus(StatusEnum.ENABLED.getKey());
            for (String str :dataArr) {
                String[] ss = str.split("_");
                if (ss[0].equals(key)) {
                   if (ss[1].equals("skuNo")) {
                       String skuNo = ss.length > 2 ? ss[2] : "";
                       sku.setSkuNo(skuNo);
                   } else if (ss[1].equals("logo")) {
                       String logo = ss.length > 2 ? ss[2] : "";
                       logo = logo.replace(imagePath, "");
                       sku.setLogo(logo);
                   } else if (ss[1].equals("stock")) {
                       String skuStock = ss.length > 2 ? ss[2] : "0";
                       sku.setStock(Integer.parseInt(skuStock));
                   } else if (ss[1].equals("price")) {
                       String skuPrice = ss.length > 2 ? ss[2] : "0";
                       sku.setPrice(new BigDecimal(skuPrice));
                       // 商品价格取sku中最低价
                       if ((new BigDecimal("0").equals(minPrice)) && (minPrice.compareTo(new BigDecimal(skuPrice)) < 0)) {
                           minPrice = new BigDecimal(skuPrice);
                       }
                   } else if (ss[1].equals("linePrice")) {
                       String skuLinePrice = ss.length > 2 ? ss[2] : "0";
                       sku.setLinePrice(new BigDecimal(skuLinePrice));
                       if ((new BigDecimal("0").equals(minLinePrice)) && (minPrice.compareTo(new BigDecimal(skuLinePrice)) < 0)) {
                           minLinePrice = new BigDecimal(skuLinePrice);
                       }
                   } else if (ss[1].equals("weight")) {
                       String skuWeight = ss.length > 2 ? ss[2] : "0";
                       sku.setWeight(new BigDecimal(skuWeight));
                   }
                }
            }
            goodSkuRepository.save(sku);
        }

        TAccount account = accountService.findAccountById(accountInfo.getId());
        Integer myStoreId = account.getStoreId();
        if (myStoreId > 0) {
            storeId = myStoreId;
        }

        MtGoods info = new MtGoods();
        info.setId(Integer.parseInt(goodsId));
        info.setCateId(cateId);
        info.setName(name);
        info.setGoodsNo(goodsNo);
        info.setIsSingleSpec(isSingleSpec);
        if (StringUtil.isNotEmpty(stock)) {
            info.setStock(Integer.parseInt(stock));
        }
        if (StringUtil.isNotEmpty(description)) {
            info.setDescription(description);
        }
        if (storeId != null) {
            info.setStoreId(storeId);
        }
        if (images.length > 0) {
            info.setLogo(images[0]);
        }
        if (StringUtil.isNotEmpty(sort)) {
            info.setSort(Integer.parseInt(sort));
        }
        if (StringUtil.isNotEmpty(status)) {
            info.setStatus(status);
        }
        if (StringUtil.isNotEmpty(price)) {
            info.setPrice(new BigDecimal(price));
        }
        if(minPrice.compareTo(new BigDecimal("0")) > 0) {
            info.setPrice(minPrice);
        }
        if (StringUtil.isNotEmpty(linePrice)) {
            info.setLinePrice(new BigDecimal(linePrice));
        }
        if(minLinePrice.compareTo(new BigDecimal("0")) > 0) {
            info.setLinePrice(minLinePrice);
        }
        if (StringUtil.isNotEmpty(weight)) {
            info.setWeight(new BigDecimal(weight));
        }
        if (initSale > 0) {
            info.setInitSale(initSale);
        }
        if (StringUtil.isNotEmpty(salePoint)) {
            info.setSalePoint(salePoint);
        }
        if (StringUtil.isNotEmpty(canUsePoint)) {
            info.setCanUsePoint(canUsePoint);
        }
        if (StringUtil.isNotEmpty(isMemberDiscount)) {
            info.setIsMemberDiscount(isMemberDiscount);
        }
        if (images.length > 0) {
            String imagesJson = JSONObject.toJSONString(images);
            info.setImages(imagesJson);
        }

        String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
        info.setOperator(operator);

        MtGoods goodsInfo = goodsService.saveGoods(info);

        Map<String, Object> result = new HashMap();
        result.put("goodsInfo", goodsInfo);

        return getSuccessResult(result);
    }

    /**
     * 保存商品规格
     *
     * @param request  HttpServletRequest对象
     */
    @RequestMapping(value = "/saveSpecName", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject saveSpecName(HttpServletRequest request) throws BusinessCheckException {
        String goodsId = request.getParameter("goodsId") == null ? "0" : request.getParameter("goodsId");
        String name = request.getParameter("name") == null ? "" : request.getParameter("name");

        if (StringUtil.isEmpty(goodsId)) {
            return null;
        }

        Map<String, Object> param = new HashMap<>();
        param.put("EQ_goodsId", goodsId);
        param.put("EQ_name", name);
        Specification<MtGoodsSpec> specification = specRepository.buildSpecification(param);
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        List<MtGoodsSpec> dataList = specRepository.findAll(specification, sort);

        Integer targetId = 0;
        if (dataList.size() < 1) {
            MtGoodsSpec mtGoodsSpec = new MtGoodsSpec();
            mtGoodsSpec.setGoodsId(Integer.parseInt(goodsId));
            mtGoodsSpec.setName(name);
            mtGoodsSpec.setValue("");
            mtGoodsSpec.setStatus(StatusEnum.ENABLED.getKey());
            MtGoodsSpec data = specRepository.save(mtGoodsSpec);
            targetId = data.getId();
        } else {
            targetId = dataList.get(0).getId();
        }

        Map<String, Object> outParams = new HashMap();
        outParams.put("id", targetId);

        return getSuccessResult(outParams);
    }

    /**
     * 保存商品规格值
     *
     * @param request  HttpServletRequest对象
     * @return
     */
    @RequestMapping(value = "/saveSpecValue", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject saveSpecValue(HttpServletRequest request) {
        String specName = request.getParameter("specName") == null ? "" : request.getParameter("specName");
        String goodsId = request.getParameter("goodsId") == null ? "" : request.getParameter("goodsId");
        String value = request.getParameter("value") == null ? "" : request.getParameter("value");

        if (StringUtil.isEmpty(specName) || StringUtil.isEmpty(goodsId) || StringUtil.isEmpty(value)) {
            return getFailureResult(201, "请求参数错误");
        }

        Map<String, Object> param = new HashMap<>();
        param.put("EQ_goodsId", goodsId);
        param.put("EQ_name", specName);
        param.put("EQ_status", StatusEnum.ENABLED.getKey());
        Specification<MtGoodsSpec> specification = specRepository.buildSpecification(param);
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        List<MtGoodsSpec> dataList = specRepository.findAll(specification, sort);

        // 1.先修改空值
        Integer id = 0;
        if (dataList.size() > 0) {
            for (MtGoodsSpec mtGoodsSpec : dataList) {
                if (StringUtil.isEmpty(mtGoodsSpec.getValue())) {
                    mtGoodsSpec.setValue(value);
                    id = mtGoodsSpec.getId();
                    specRepository.save(mtGoodsSpec);
                    break;
                }
            }
        }

        // 2.没有空值再新增
        if (id < 1) {
            MtGoodsSpec mtGoodsSpec = new MtGoodsSpec();
            mtGoodsSpec.setGoodsId(Integer.parseInt(goodsId));
            mtGoodsSpec.setName(specName);
            mtGoodsSpec.setValue(value);
            mtGoodsSpec.setStatus(StatusEnum.ENABLED.getKey());
            MtGoodsSpec info = specRepository.save(mtGoodsSpec);
            id = info.getId();
        }

        ReqResult reqResult = new ReqResult();
        reqResult.setResult(true);
        reqResult.setCode("200");
        reqResult.setMsg("请求成功");

        Map<String, Object> outParams = new HashMap();
        outParams.put("id", id);

        return getSuccessResult(outParams);
    }

    /**
     * 删除商品规格
     *
     * @param request  HttpServletRequest对象
     * @return
     */
    @RequestMapping(value = "/deleteSpec", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject deleteSpec(HttpServletRequest request) {
        String specName = request.getParameter("specName") == null ? "" : request.getParameter("specName");
        String goodsId = request.getParameter("goodsId") == null ? "0" : request.getParameter("goodsId");

        if (StringUtil.isEmpty(specName) || StringUtil.isEmpty(goodsId)) {
            return getFailureResult(201, "请求参数错误");
        }

        Map<String, Object> param = new HashMap<>();
        param.put("EQ_goodsId", goodsId);
        param.put("EQ_name", specName);
        Specification<MtGoodsSpec> specification = specRepository.buildSpecification(param);
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        List<MtGoodsSpec> dataList = specRepository.findAll(specification, sort);
        if (dataList.size() > 0) {
            for (MtGoodsSpec mtGoodsSpec : dataList) {
                MtGoodsSpec info = specRepository.findOne(mtGoodsSpec.getId());
                info.setStatus(StatusEnum.DISABLE.getKey());
                specRepository.save(mtGoodsSpec);
            }
        }

        return getSuccessResult(true);
    }

    /**
     * 删除商品规格值
     *
     * @param request  HttpServletRequest对象
     */
    @RequestMapping(value = "/deleteSpecValue", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject deleteSpecValue(HttpServletRequest request) {
        Integer specId = request.getParameter("id") == null ? 0 : Integer.parseInt(request.getParameter("id"));

        if (specId < 1) {
            return getFailureResult(201, "请求参数错误");
        }

        MtGoodsSpec mtGoodsSpec = specRepository.findOne(specId);
        if (mtGoodsSpec == null) {
            return getFailureResult(201, "该规格值不存在");
        }

        mtGoodsSpec.setStatus(StatusEnum.DISABLE.getKey());
        specRepository.save(mtGoodsSpec);

        // 把对应的sku删掉
        Map<String, Object> param = new HashMap<>();
        param.put("EQ_goodsId", mtGoodsSpec.getGoodsId().toString());
        Specification<MtGoodsSku> specification = goodSkuRepository.buildSpecification(param);
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        List<MtGoodsSku> goodsSkuList = goodSkuRepository.findAll(specification, sort);
        for(MtGoodsSku mtGoodsSku : goodsSkuList) {
            String[] ss = mtGoodsSku.getSpecIds().split("-");
            for (int i = 0; i < ss.length; i++) {
                 if (ss[i].equals(specId+"")) {
                     mtGoodsSku.setStatus(StatusEnum.DISABLE.getKey());
                     goodSkuRepository.save(mtGoodsSku);
                 }
            }
        }

        return getSuccessResult(true);
    }

    /**
     * 快速查询商品
     * */
    @RequestMapping(value = "/quickSearchGoods")
    @CrossOrigin
    public ResponseObject quickSearchGoods(HttpServletRequest request) throws BusinessCheckException {
        String selectGoodsIds = request.getParameter("selectGoodsIds");

        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, null);
        PaginationResponse<GoodsDto> paginationResponse = goodsService.queryGoodsListByPagination(paginationRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }
}

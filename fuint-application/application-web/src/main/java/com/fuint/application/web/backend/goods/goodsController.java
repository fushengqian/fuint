package com.fuint.application.web.backend.goods;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.fuint.application.dao.repositories.MtGoodsSpecRepository;
import com.fuint.application.dao.repositories.MtGoodsSkuRepository;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.goods.CateService;
import com.fuint.application.service.store.StoreService;
import com.fuint.application.util.CommonUtil;
import com.fuint.base.dao.entities.TAccount;
import com.fuint.base.service.account.TAccountService;
import com.fuint.base.shiro.ShiroUser;
import com.fuint.exception.BusinessCheckException;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.application.dao.entities.*;
import com.fuint.application.dto.*;
import com.fuint.application.dto.GoodsSpecItemDto;
import com.fuint.application.service.goods.GoodsService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.util.RequestHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 商品管理controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Controller
@RequestMapping(value = "/backend/goods/goods")
public class goodsController {

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

    @Autowired
    private MtGoodsSpecRepository specRepository;

    @Autowired
    private MtGoodsSkuRepository goodskuRepository;

    @Autowired
    private Environment env;

    /**
     * 查询列表
     *
     * @param request
     * @param response
     * @param model
     * @return
     * @throws BusinessCheckException
     */
    @RequestMapping(value = "/list")
    @RequiresPermissions("/backend/goods/goods/list")
    public String queryList(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        TAccount account = accountService.findAccountById(shiroUser.getId());
        Integer storeId = account.getStoreId();

        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);

        if (storeId > 0) {
            paginationRequest.getSearchParams().put("EQ_storeId", storeId.toString());
        }

        PaginationResponse<GoodsDto> paginationResponse = goodsService.queryGoodsListByPagination(paginationRequest);

        String imagePath = env.getProperty("images.upload.url");

        model.addAttribute("paginationResponse", paginationResponse);
        model.addAttribute("imagePath", imagePath);
        model.addAttribute("storeId", storeId);

        return "goods/goods/list";
    }

    /**
     * 删除商品
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/goods/goods/delete")
    @RequestMapping(value = "/delete/{id}")
    @ResponseBody
    public ReqResult delete(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Integer id) throws BusinessCheckException {
        List<Integer> ids = new ArrayList<>();
        ids.add(id);

        String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
        goodsService.deleteGoods(id, operator);

        ReqResult reqResult = new ReqResult();
        reqResult.setResult(true);

        return reqResult;
    }

    /**
     * 添加商品页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/goods/goods/add")
    @RequestMapping(value = "/add")
    public String add(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException,InvocationTargetException,IllegalAccessException {
        Integer goodsId = request.getParameter("goodsId") == null ? 0 : Integer.parseInt(request.getParameter("goodsId"));

        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        if (shiroUser == null) {
            return "redirect:/login";
        }
        TAccount account = accountService.findAccountById(shiroUser.getId());

        GoodsDto goods = goodsService.getGoodsDetail(goodsId, false);
        model.addAttribute("goods", goods);

        List<String> images = new ArrayList<>();
        if (goods != null) {
            images = JSONArray.parseArray(goods.getImages(), String.class);
        }
        model.addAttribute("images", images);

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
        model.addAttribute("cateList", cateList);

        String imagePath = env.getProperty("images.upload.url");
        model.addAttribute("imagePath", imagePath);

        String specData = JSONObject.toJSONString(specArr);
        model.addAttribute("specData", specData);

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
        model.addAttribute("skuData", JSONObject.toJSONString(skuData));

        Map<String, Object> paramsStore = new HashMap<>();
        paramsStore.put("EQ_status", StatusEnum.ENABLED.getKey());
        List<MtStore> storeList = storeService.queryStoresByParams(paramsStore);
        model.addAttribute("storeList", storeList);

        Integer storeId = account.getStoreId();
        model.addAttribute("storeId", storeId);

        return "goods/goods/add";
    }

    /**
     * 保存商品
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     */
    @RequiresPermissions("backend/goods/goods/save")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public ReqResult saveHandler(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String goodsId = request.getParameter("goodsId") == null ? "0" : request.getParameter("goodsId");
        if (StringUtils.isEmpty(goodsId)) {
            goodsId = "0";
        }

        String name = CommonUtil.replaceXSS(request.getParameter("name"));
        String description = request.getParameter("editorValue") == null ? "" : request.getParameter("editorValue");
        String images[] = request.getParameterValues("image") == null ? new String[0] : request.getParameterValues("image");
        String sort = request.getParameter("sort") == null ? "0" : request.getParameter("sort");
        String stock = request.getParameter("stock") == null ? "0" : request.getParameter("stock");
        String status = request.getParameter("status") == null ? StatusEnum.ENABLED.getKey() : request.getParameter("status");
        String goodsNo = request.getParameter("goodsNo") == null ? "" : request.getParameter("goodsNo");
        String price = request.getParameter("price") == null ? "0" : request.getParameter("price");
        String linePrice = request.getParameter("linePrice") == null ? "0" : request.getParameter("linePrice");
        String weight = request.getParameter("weight") == null ? "0" : request.getParameter("weight");
        Integer initSale = request.getParameter("initSale") == null ? 0 : Integer.parseInt(request.getParameter("initSale"));
        String salePoint = request.getParameter("salePoint") == null ? "" : request.getParameter("salePoint");
        String canUsePoint = request.getParameter("canUsePoint") == null ? "N" : request.getParameter("canUsePoint");
        String isMemberDiscount = request.getParameter("isMemberDiscount") == null ? "N" : request.getParameter("isMemberDiscount");
        String isSingleSpec = request.getParameter("isSingleSpec") == null ? "Y" : request.getParameter("isSingleSpec");
        Integer cateId = request.getParameter("cateId") == null ? 0 : Integer.parseInt(request.getParameter("cateId"));
        Integer storeId = request.getParameter("storeId") == null ? 0 : Integer.parseInt(request.getParameter("storeId"));

        if (StringUtils.isEmpty(sort)) {
            sort = "0";
        }

        // 去除空值
        if (images.length > 0) {
            List<String> tempArr = new ArrayList<>();
            for (String img : images) {
                 if (StringUtils.isNotEmpty(img)) {
                     tempArr.add(img);
                 }
            }
            images = tempArr.toArray(new String[0]);
        }

        Enumeration skuMap = request.getParameterNames();
        List<String> dataArr = new ArrayList<>();
        List<String> item = new ArrayList<>();
        String imagePath = env.getProperty("images.upload.url");

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
            Specification<MtGoodsSku> specification = goodskuRepository.buildSpecification(param0);
            Sort sort0 = new Sort(Sort.Direction.ASC, "id");
            List<MtGoodsSku> goodsSkuList = goodskuRepository.findAll(specification, sort0);
            if (goodsSkuList.size() > 0) {
                for (MtGoodsSku mtGoodsSku : goodsSkuList) {
                     if (!item.contains(mtGoodsSku.getSpecIds())) {
                         goodskuRepository.delete(mtGoodsSku.getId());
                     }
                }
            }
        }

        for (String key : item) {
            Map<String, Object> param = new HashMap<>();
            param.put("EQ_goodsId", goodsId);
            param.put("EQ_specIds", key);

            // 是否已存在
            Specification<MtGoodsSku> specification2 = goodskuRepository.buildSpecification(param);
            Sort sort2 = new Sort(Sort.Direction.ASC, "id");
            List<MtGoodsSku> goodsSkuList = goodskuRepository.findAll(specification2, sort2);
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
            goodskuRepository.save(sku);
        }

        ShiroUser shirouser = ShiroUserHelper.getCurrentShiroUser();
        TAccount account = accountService.findAccountById(shirouser.getId());
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
        info.setStock(Integer.parseInt(stock));
        info.setDescription(description);
        info.setStoreId(storeId);
        if (images.length > 0) {
            info.setLogo(images[0]);
        }
        if (StringUtils.isNotEmpty(sort)) {
            info.setSort(Integer.parseInt(sort));
        }
        info.setStatus(status);
        if (new BigDecimal(price).compareTo(new BigDecimal("0")) > 0) {
            info.setPrice(new BigDecimal(price));
        }
        if(minPrice.compareTo(new BigDecimal("0")) > 0) {
            info.setPrice(minPrice);
        }
        if (new BigDecimal(linePrice).compareTo(new BigDecimal("0")) > 0) {
            info.setLinePrice(new BigDecimal(linePrice));
        }
        if(minLinePrice.compareTo(new BigDecimal("0")) > 0) {
            info.setLinePrice(minLinePrice);
        }
        if (StringUtils.isNotEmpty(weight)) {
            info.setWeight(new BigDecimal(weight));
        }
        info.setInitSale(initSale);
        info.setSalePoint(salePoint);
        info.setCanUsePoint(canUsePoint);
        info.setIsMemberDiscount(isMemberDiscount);

        if (images.length > 0) {
            String imagesJson = JSONObject.toJSONString(images);
            info.setImages(imagesJson);
        }

        String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
        info.setOperator(operator);

        MtGoods goods = goodsService.saveGoods(info);

        ReqResult reqResult = new ReqResult();
        reqResult.setResult(true);

        Map<String, Object> outParams = new HashMap();
        outParams.put("goods", goods);

        reqResult.setData(outParams);

        return reqResult;
    }

    /**
     * 保存商品规格
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     */
    @RequiresPermissions("backend/goods/goods/saveSpecName")
    @RequestMapping(value = "/saveSpecName", method = RequestMethod.POST)
    @ResponseBody
    public ReqResult saveSpecName(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String goodsId = request.getParameter("goodsId") == null ? "0" : request.getParameter("goodsId");
        String name = request.getParameter("name") == null ? "" : request.getParameter("name");

        if (StringUtils.isEmpty(goodsId)) {
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

        ReqResult reqResult = new ReqResult();
        reqResult.setResult(true);
        reqResult.setCode("200");
        reqResult.setMsg("请求成功");

        Map<String, Object> outParams = new HashMap();
        outParams.put("id", targetId);

        reqResult.setData(outParams);

        return reqResult;
    }

    /**
     * 保存商品规格值
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     */
    @RequiresPermissions("backend/goods/goods/saveSpecValue")
    @RequestMapping(value = "/saveSpecValue", method = RequestMethod.POST)
    @ResponseBody
    public ReqResult saveSpecValue(HttpServletRequest request, HttpServletResponse response, Model model) {
        String specName = request.getParameter("specName") == null ? "" : request.getParameter("specName");
        String goodsId = request.getParameter("goodsId") == null ? "" : request.getParameter("goodsId");
        String value = request.getParameter("value") == null ? "" : request.getParameter("value");

        if (StringUtils.isEmpty(specName) || StringUtils.isEmpty(goodsId) || StringUtils.isEmpty(value)) {
            ReqResult reqResult = new ReqResult();
            reqResult.setCode("201");
            reqResult.setResult(false);
            reqResult.setMsg("请求参数错误");
            return reqResult;
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
                if (StringUtils.isEmpty(mtGoodsSpec.getValue())) {
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

        reqResult.setData(outParams);

        return reqResult;
    }

    /**
     * 删除商品规格
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     */
    @RequiresPermissions("backend/goods/goods/deleteSpec")
    @RequestMapping(value = "/deleteSpec", method = RequestMethod.POST)
    @ResponseBody
    public ReqResult deleteSpec(HttpServletRequest request, HttpServletResponse response, Model model) {
        String specName = request.getParameter("specName") == null ? "" : request.getParameter("specName");
        String goodsId = request.getParameter("goodsId") == null ? "0" : request.getParameter("goodsId");

        ReqResult reqResult = new ReqResult();
        if (StringUtils.isEmpty(specName) || StringUtils.isEmpty(goodsId)) {
            reqResult.setCode("201");
            reqResult.setResult(false);
            reqResult.setMsg("请求参数错误");
            return reqResult;
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

        reqResult.setResult(true);
        reqResult.setCode("200");
        reqResult.setMsg("请求成功");

        return reqResult;
    }

    /**
     * 删除商品规格值
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     */
    @RequiresPermissions("backend/goods/goods/deleteSpecValue")
    @RequestMapping(value = "/deleteSpecValue", method = RequestMethod.POST)
    @ResponseBody
    public ReqResult deleteSpecValue(HttpServletRequest request, HttpServletResponse response, Model model) {
        Integer specId = request.getParameter("id") == null ? 0 : Integer.parseInt(request.getParameter("id"));

        ReqResult reqResult = new ReqResult();
        if (specId < 1) {
            reqResult.setCode("201");
            reqResult.setResult(false);
            reqResult.setMsg("请求参数错误");
            return reqResult;
        }

        MtGoodsSpec mtGoodsSpec = specRepository.findOne(specId);
        if (mtGoodsSpec == null) {
            reqResult.setCode("201");
            reqResult.setResult(false);
            reqResult.setMsg("该规格值不存在");
            return reqResult;
        }

        mtGoodsSpec.setStatus(StatusEnum.DISABLE.getKey());
        specRepository.save(mtGoodsSpec);

        // 把对应的sku删掉
        Map<String, Object> param = new HashMap<>();
        param.put("EQ_goodsId", mtGoodsSpec.getGoodsId().toString());
        Specification<MtGoodsSku> specification = goodskuRepository.buildSpecification(param);
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        List<MtGoodsSku> goodsSkuList = goodskuRepository.findAll(specification, sort);
        for(MtGoodsSku mtGoodsSku : goodsSkuList) {
            String[] ss = mtGoodsSku.getSpecIds().split("-");
            for (int i = 0; i < ss.length; i++) {
                 if (ss[i].equals(specId+"")) {
                     mtGoodsSku.setStatus(StatusEnum.DISABLE.getKey());
                     goodskuRepository.save(mtGoodsSku);
                 }
            }
        }

        reqResult.setResult(true);
        reqResult.setCode("200");
        reqResult.setMsg("请求成功");

        return reqResult;
    }

    /**
     * 查询商品页面
     * */
    @RequiresPermissions("backend/goods/goods/searchGoods")
    @RequestMapping(value = "/searchGoods")
    public String searchGoods(HttpServletRequest request, Model model) {
        String selectGoodsIds = request.getParameter("selectGoodsIds");

        model.addAttribute("selectGoodsIds", selectGoodsIds);
        return "goods/goods/searchGoods";
    }

    /**
     * 快速查询商品
     * */
    @RequiresPermissions("backend/goods/goods/quickSearchGoods")
    @RequestMapping(value = "/quickSearchGoods")
    public String quickSearchGoods(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String selectGoodsIdStr = request.getParameter("selectGoodsIds");

        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        PaginationResponse<GoodsDto> paginationResponse = goodsService.queryGoodsListByPagination(paginationRequest);

        String[] selectGoodsIds = selectGoodsIdStr.split(",");

        model.addAttribute("paginationResponse", paginationResponse);
        model.addAttribute("selectGoodsIds", selectGoodsIds);

        return "goods/goods/searchGoodsList";
    }
}

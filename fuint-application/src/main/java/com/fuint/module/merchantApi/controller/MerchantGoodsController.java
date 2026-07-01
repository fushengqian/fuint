package com.fuint.module.merchantApi.controller;

import com.alibaba.fastjson.JSONObject;
import com.fuint.common.dto.goods.GoodsDto;
import com.fuint.common.dto.member.UserInfo;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.YesOrNoEnum;
import com.fuint.common.param.GoodsListParam;
import com.fuint.common.param.StatusParam;
import com.fuint.common.service.*;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * 商户端-商品管理controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="商户端-商品管理相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/merchantApi/goods")
public class MerchantGoodsController extends BaseController {

    /**
     * 商品服务接口
     */
    private GoodsService goodsService;

    /**
     * 商品分类服务接口
     */
    private CateService cateService;

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
     * 获取商品列表
     */
    @ApiOperation(value = "获取商品列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(@RequestBody GoodsListParam param) throws BusinessCheckException {
        MtStaff staffInfo = getStaffInfo();

        param.setMerchantId(staffInfo.getMerchantId());
        param.setStoreId(staffInfo.getStoreId());
        if (StringUtil.isEmpty(param.getStatus())) {
            param.setStatus(StatusEnum.ENABLED.getKey());
        }

        PaginationResponse<GoodsDto> paginationResponse = goodsService.queryGoodsListByPagination(param);

        // 分类列表
        List<MtGoodsCate> cateList = cateService.getCateList(
                staffInfo.getMerchantId(),
                staffInfo.getStoreId(),
                null,
                StatusEnum.ENABLED.getKey()
        );

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        result.put("cateList", cateList);
        result.put("imagePath", settingService.getUploadBasePath());

        return getSuccessResult(result);
    }

    /**
     * 获取商品详情
     */
    @ApiOperation(value = "获取商品详情")
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject detail(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        MtStaff staffInfo = getStaffInfo();

        String goodsId = param.get("goodsId") != null ? param.get("goodsId").toString() : "";
        if (StringUtil.isEmpty(goodsId)) {
            return getFailureResult(201, "商品ID不能为空");
        }

        MtGoods mtGoods = goodsService.queryGoodsById(Integer.parseInt(goodsId));
        if (mtGoods == null) {
            return getFailureResult(201, "商品不存在");
        }

        if (!staffInfo.getMerchantId().equals(mtGoods.getMerchantId())) {
            return getFailureResult(201, "您没有操作权限");
        }

        GoodsDto goods = goodsService.getGoodsDetail(Integer.parseInt(goodsId), false);

        List<String> imageList = new ArrayList<>();
        if (goods.getImages() != null && !goods.getImages().isEmpty()) {
            imageList = com.alibaba.fastjson.JSONArray.parseArray(goods.getImages(), String.class);
        }

        // 分类列表
        List<MtGoodsCate> cateList = cateService.getCateList(
                staffInfo.getMerchantId(),
                staffInfo.getStoreId(),
                null,
                StatusEnum.ENABLED.getKey()
        );

        Map<String, Object> result = new HashMap<>();
        result.put("goods", goods);
        result.put("imageList", imageList);
        result.put("cateList", cateList);
        result.put("imagePath", settingService.getUploadBasePath());

        return getSuccessResult(result);
    }

    /**
     * 保存商品信息（新增/编辑）
     */
    @ApiOperation(value = "保存商品信息")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject save(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        MtStaff staffInfo = getStaffInfo();

        String goodsId = param.get("goodsId") == null ? "0" : param.get("goodsId").toString();
        if (StringUtil.isEmpty(goodsId)) {
            goodsId = "0";
        }

        String name = param.get("name") == null ? null : CommonUtil.replaceXSS(param.get("name").toString());
        String description = param.get("description") == null ? "" : param.get("description").toString();
        List<String> images = param.get("images") == null ? new ArrayList<>() : (List) param.get("images");
        String sort = param.get("sort") == null ? "0" : param.get("sort").toString();
        String stock = param.get("stock") == null ? "0" : param.get("stock").toString();
        String status = param.get("status") == null ? StatusEnum.ENABLED.getKey() : param.get("status").toString();
        String price = param.get("price") == null ? "0" : param.get("price").toString();
        String linePrice = param.get("linePrice") == null ? "0" : param.get("linePrice").toString();
        String costPrice = param.get("costPrice") == null ? "0" : param.get("costPrice").toString();
        String salePoint = param.get("salePoint") == null ? "" : param.get("salePoint").toString();
        Integer cateId = (param.get("cateId") == null || StringUtil.isEmpty(param.get("cateId").toString())) ? 0 : Integer.parseInt(param.get("cateId").toString());

        if (StringUtil.isEmpty(name)) {
            return getFailureResult(201, "商品名称不能为空");
        }

        MtGoods mtGoods = new MtGoods();
        mtGoods.setId(Integer.parseInt(goodsId));
        mtGoods.setMerchantId(staffInfo.getMerchantId());
        mtGoods.setName(name);
        mtGoods.setCateId(cateId);
        mtGoods.setDescription(description);
        mtGoods.setIsSingleSpec(YesOrNoEnum.YES.getKey());
        mtGoods.setStatus(status);
        mtGoods.setPrice(new BigDecimal(price));
        mtGoods.setLinePrice(new BigDecimal(linePrice));
        mtGoods.setCostPrice(new BigDecimal(costPrice));
        mtGoods.setStock(Double.parseDouble(stock));
        mtGoods.setSort(Integer.parseInt(sort));
        mtGoods.setSalePoint(salePoint);
        mtGoods.setPlatform(0); // 不限平台
        mtGoods.setOperator(staffInfo.getRealName());

        // 去掉图片中的域名前缀，数据库只存储相对路径
        String basePath = settingService.getUploadBasePath();
        List<String> relativeImages = new ArrayList<>();
        for (String img : images) {
            if (img != null && basePath != null && img.startsWith(basePath)) {
                relativeImages.add(img.substring(basePath.length()));
            } else {
                relativeImages.add(img);
            }
        }

        if (relativeImages.size() > 0) {
            mtGoods.setLogo(relativeImages.get(0));
            mtGoods.setImages(JSONObject.toJSONString(relativeImages));
        }

        if (mtGoods.getId() != null && mtGoods.getId() > 0) {
            mtGoods.setStoreId(staffInfo.getStoreId());
        }

        goodsService.saveGoods(mtGoods, null);

        return getSuccessResult(true);
    }

    /**
     * 更新商品状态（启用/禁用/删除）
     */
    @ApiOperation(value = "更新商品状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject updateStatus(@RequestBody StatusParam params) throws BusinessCheckException {
        MtStaff staffInfo = getStaffInfo();

        MtGoods mtGoods = goodsService.queryGoodsById(params.getId());
        if (mtGoods == null) {
            return getFailureResult(201, "该商品不存在");
        }

        if (!staffInfo.getMerchantId().equals(mtGoods.getMerchantId())) {
            return getFailureResult(201, "您没有操作权限");
        }

        AccountInfo accountInfo = buildAccountInfo(staffInfo);
        goodsService.updateStatus(params.getId(), params.getStatus(), accountInfo);

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

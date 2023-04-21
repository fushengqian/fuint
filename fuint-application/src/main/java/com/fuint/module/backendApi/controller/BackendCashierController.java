package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.*;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.YesOrNoEnum;
import com.fuint.common.service.*;
import com.fuint.common.util.DateUtil;
import com.fuint.common.util.PhoneFormatCheckUtils;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收银管理controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-收银台相关接口")
@RestController
@RequestMapping(value = "/backendApi/cashier")
public class BackendCashierController extends BaseController {

    /**
     * 后台账户服务接口
     */
    @Autowired
    private AccountService accountService;

    /**
     * 商品类别服务接口
     */
    @Autowired
    private CateService cateService;

    /**
     * 购物车服务接口
     * */
    @Autowired
    private CartService cartService;

    /**
     * 订单服务接口
     * */
    @Autowired
    private OrderService orderService;

    /**
     * 商品服务接口
     */
    @Autowired
    private GoodsService goodsService;

    /**
     * 店铺服务接口
     */
    @Autowired
    private StoreService storeService;

    /**
     * 系统设置服务接口
     */
    @Autowired
    private SettingService settingService;

    /**
     * 会员服务接口
     */
    @Autowired
    private MemberService memberService;

    /**
     * 收银台初始化
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @RequestMapping(value = "/init/{userId}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject init(HttpServletRequest request, @PathVariable("userId") Integer userId) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        AccountInfo accountDto = TokenUtil.getAccountInfoByToken(token);
        if (accountDto == null) {
            return getFailureResult(1001, "请先登录");
        }

        TAccount accountInfo = accountService.getAccountInfoById(accountDto.getId());
        Integer storeId = accountInfo.getStoreId();
        MtStore storeInfo;
        if (storeId == null || storeId < 1) {
            storeInfo = storeService.getDefaultStore();
        } else {
            storeInfo = storeService.queryStoreById(storeId);
        }

        MtUser memberInfo = null;
        if (userId != null && userId > 0) {
            memberInfo = memberService.queryMemberById(userId);
        }

        Map<String, Object> param = new HashMap<>();
        param.put("status", StatusEnum.ENABLED.getKey());
        List<MtGoodsCate> cateList = cateService.queryCateListByParams(param);

        param.put("status", StatusEnum.ENABLED.getKey());
        List<MtGoods> goodsList = goodsService.getStoreGoodsList(storeId, "");

        String imagePath = settingService.getUploadBasePath();

        Map<String, Object> result = new HashMap<>();
        result.put("imagePath", imagePath);
        result.put("storeInfo", storeInfo);
        result.put("memberInfo", memberInfo);
        result.put("accountInfo", accountDto);
        result.put("goodsList", goodsList);
        result.put("cateList", cateList);

        return getSuccessResult(result);
    }

    /**
     * 查询商品
     *
     * @param request
     * @param param
     * @return
     */
    @RequestMapping(value = "/searchGoods", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject searchGoods(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String keyword =  param.get("keyword") == null ? "" : param.get("keyword").toString();

        AccountInfo accountDto = TokenUtil.getAccountInfoByToken(token);
        if (accountDto == null) {
            return getFailureResult(1001, "请先登录");
        }

        TAccount accountInfo = accountService.getAccountInfoById(accountDto.getId());
        Integer storeId = accountInfo.getStoreId();

        List<MtGoods> goodsList = goodsService.getStoreGoodsList(storeId, keyword);
        return getSuccessResult(goodsList);
    }

    /**
     * 商品详情
     *
     * @param request
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/getGoodsInfo/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getGoodsInfo(HttpServletRequest request, @PathVariable("id") Integer goodsId) throws BusinessCheckException, InvocationTargetException, IllegalAccessException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        GoodsDto goodsInfo = goodsService.getGoodsDetail(goodsId, false);

        Map<String, Object> result = new HashMap<>();
        result.put("goodsInfo", goodsInfo);

        // 商品规格列表
        List<String> specNameArr = new ArrayList<>();
        List<Integer> specIdArr = new ArrayList<>();
        List<GoodsSpecItemDto> specArr = new ArrayList<>();

        // sku列表
        List<GoodsSkuDto> skuArr = new ArrayList<>();
        if (goodsInfo != null) {
            // 处理规格列表
            for (MtGoodsSpec mtGoodsSpec : goodsInfo.getSpecList()) {
                if (!specNameArr.contains(mtGoodsSpec.getName())) {
                    specNameArr.add(mtGoodsSpec.getName());
                    specIdArr.add(mtGoodsSpec.getId());
                }
            }

            for (int i = 0; i < specNameArr.size(); i++) {
                GoodsSpecItemDto item = new GoodsSpecItemDto();
                List<GoodsSpecChildDto> child = new ArrayList<>();
                Integer specId = specIdArr.get(i) == null ? (i + 1) : specIdArr.get(i);
                String name = specNameArr.get(i);
                for (MtGoodsSpec mtGoodsSpec : goodsInfo.getSpecList()) {
                    if (mtGoodsSpec.getName().equals(name)) {
                        GoodsSpecChildDto e = new GoodsSpecChildDto();
                        e.setId(mtGoodsSpec.getId());
                        e.setName(mtGoodsSpec.getValue());
                        e.setChecked(true);
                        child.add(e);
                    }
                }
                item.setId(specId);
                item.setName(name);
                item.setChild(child);
                specArr.add(item);
            }

            // 处理sku列表
            for (MtGoodsSku mtGoodsSku : goodsInfo.getSkuList()) {
                GoodsSkuDto skuDto = new GoodsSkuDto();
                BeanUtils.copyProperties(mtGoodsSku, skuDto);
                List<MtGoodsSpec> specList = new ArrayList<>();
                String[] specIds = skuDto.getSpecIds().split("-");
                for (String specId : specIds) {
                    MtGoodsSpec spec = goodsService.getSpecDetail(Integer.parseInt(specId));
                    if (spec != null) {
                        specList.add(spec);
                    }
                }
                skuDto.setSpecList(specList);
                skuArr.add(skuDto);
            }
        }

        result.put("specList", specArr);
        result.put("skuList", skuArr);

        return getSuccessResult(result);
    }

    /**
     * 获取会员信息
     */
    @RequestMapping(value = "/getMemberInfo", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject getMemberInfo(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String keyword =  param.get("keyword") == null ? "" : param.get("keyword").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        if (StringUtil.isEmpty(keyword)) {
            return getFailureResult(201);
        }

        MtUser userInfo;
        if (PhoneFormatCheckUtils.isChinaPhoneLegal(keyword)) {
            userInfo = memberService.queryMemberByMobile(keyword);
        } else {
            userInfo = memberService.queryMemberByName(keyword);
            if (userInfo == null) {
                userInfo = memberService.queryMemberByUserNo(keyword);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("memberInfo", userInfo);
        return getSuccessResult(result);
    }

    /**
     * 获取会员信息
     */
    @RequestMapping(value = "/getMemberInfoById/{userId}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getMemberInfoById(HttpServletRequest request, @PathVariable("userId") String userId) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        if (StringUtil.isEmpty(userId)) {
            return getFailureResult(201);
        }

        MtUser userInfo = memberService.queryMemberById(Integer.parseInt(userId));

        Map<String, Object> result = new HashMap<>();
        result.put("memberInfo", userInfo);
        return getSuccessResult(result);
    }

    /**
     * 执行挂单
     */
    @RequestMapping(value = "/doHangUp", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doHangUp(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        String token = request.getHeader("Access-Token");
        String cartIds = param.get("cartIds") == null ? "" : param.get("cartIds").toString();
        String hangNo = param.get("hangNo") == null ? "" : param.get("hangNo").toString();
        String userId = param.get("userId") == null ? "" : param.get("userId").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        String isVisitor = YesOrNoEnum.NO.getKey();
        if (StringUtil.isEmpty(userId)) {
            isVisitor = YesOrNoEnum.YES.getKey();
        }

        try {
            if (StringUtil.isNotEmpty(cartIds)) {
                String[] ids = cartIds.split(",");
                if (ids.length > 0) {
                    for (int i = 0; i < ids.length; i++) {
                         cartService.setHangNo(Integer.parseInt(ids[i]), hangNo, isVisitor);
                    }
                }
            }
        } catch (BusinessCheckException e) {
            return getFailureResult(201, "挂单失败");
        }

        return getSuccessResult(true);
    }

    /**
     * 获取挂单列表
     */
    @RequestMapping(value = "/getHangUpList", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getHangUpList(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        List<HangUpDto> dataList = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
             String hangNo = "#0" + (i+1);
             Map<String, Object> param = new HashMap<>();
             param.put("hangNo", hangNo);
             List<MtCart> cartList = cartService.queryCartListByParams(param);
             HangUpDto dto = new HangUpDto();
             dto.setIsEmpty(true);
             if (cartList.size() > 0) {
                 Integer userId = cartList.get(0).getUserId();
                 String isVisitor = cartList.get(0).getIsVisitor();
                 Map<String, Object> cartInfo = orderService.calculateCartGoods(userId, cartList, 0, false);
                 dto.setNum(Integer.parseInt(cartInfo.get("totalNum").toString()));
                 dto.setAmount(new BigDecimal(cartInfo.get("totalPrice").toString()));
                 if (isVisitor.equals(YesOrNoEnum.NO.getKey())) {
                     MtUser userInfo = memberService.queryMemberById(userId);
                     dto.setMemberInfo(userInfo);
                 }
                 String dateTime = DateUtil.formatDate(cartList.get(0).getUpdateTime(), "yyyy-MM-dd HH:mm:ss");
                 dto.setDateTime(dateTime);
                 dto.setIsEmpty(false);
             }
             dto.setHangNo(hangNo);
             dataList.add(dto);
        }

        return getSuccessResult(dataList);
    }
}

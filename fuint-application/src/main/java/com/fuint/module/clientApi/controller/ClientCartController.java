package com.fuint.module.clientApi.controller;

import com.fuint.common.dto.UserInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.YesOrNoEnum;
import com.fuint.common.service.CartService;
import com.fuint.common.service.GoodsService;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.OrderService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.MtGoodsSkuMapper;
import com.fuint.repository.model.MtCart;
import com.fuint.repository.model.MtGoodsSku;
import com.fuint.repository.model.MtUser;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-购物车相关接口")
@RestController
@RequestMapping(value = "/clientApi/cart")
public class ClientCartController extends BaseController {

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
     * */
    @Autowired
    private GoodsService goodsService;

    /**
     * 会员接口
     * */
    @Autowired
    private MemberService memberService;

    @Resource
    private MtGoodsSkuMapper mtGoodsSkuMapper;

    /**
     * 保存购物车
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject save(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer storeId = request.getHeader("storeId") == null ? 0 : Integer.parseInt(request.getHeader("storeId"));
        Integer cartId = param.get("cartId") == null ? 0 : Integer.parseInt(param.get("cartId").toString());
        Integer goodsId = param.get("goodsId") == null ? 0 : Integer.parseInt(param.get("goodsId").toString());
        Integer skuId = param.get("skuId") == null ? 0 : Integer.parseInt(param.get("skuId").toString());
        String skuNo = param.get("skuNo") == null ? "" : param.get("skuNo").toString();
        Integer buyNum = param.get("buyNum") == null ? 1 : Integer.parseInt(param.get("buyNum").toString());
        String action = param.get("action") == null ? "+" : param.get("action").toString();
        String hangNo = param.get("hangNo") == null ? "" : param.get("hangNo").toString();
        Integer userId = param.get("userId") == null ? 0 : Integer.parseInt(param.get("userId").toString()); // 指定会员ID

        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);
        MtUser mtUser;
        if (userInfo == null) {
            mtUser = memberService.getCurrentUserInfo(request, userId, token);
        } else {
            mtUser = memberService.queryMemberById(userInfo.getId());
        }

        if (mtUser == null) {
            return getFailureResult(1001);
        }

        // 通过商品条码操作
        if (StringUtil.isNotEmpty(skuNo)) {
            MtGoodsSku mtGoodsSku = goodsService.getSkuInfoBySkuNo(skuNo);
            if (mtGoodsSku != null) {
                goodsId = mtGoodsSku.getGoodsId();
                skuId = mtGoodsSku.getId();
            }
        }

        MtCart mtCart = new MtCart();
        mtCart.setGoodsId(goodsId);
        mtCart.setUserId(mtUser.getId());
        mtCart.setStoreId(storeId);
        mtCart.setNum(buyNum);
        mtCart.setSkuId(skuId);
        mtCart.setId(cartId);
        mtCart.setHangNo(hangNo);
        mtCart.setIsVisitor(YesOrNoEnum.NO.getKey());

        try {
            Integer id = cartService.saveCart(mtCart, action);
            Map<String, Object> data = new HashMap();
            data.put("cartId", id);
            return getSuccessResult(data);
        } catch (BusinessCheckException e) {
            return getFailureResult(201, e.getMessage());
        }
    }

    /**
     * 删除购物车
     */
    @RequestMapping(value = "/clear", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject clear(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String cartIds = param.get("cartId") == null ? "" : param.get("cartId").toString().replace("[", "").replace("]", "");
        Integer userId = param.get("userId") == null ? 0 : Integer.parseInt(param.get("userId").toString());
        String hangNo = param.get("hangNo") == null ? "" : param.get("hangNo").toString();

        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);
        MtUser mtUser;
        if (userInfo == null) {
            mtUser = memberService.getCurrentUserInfo(request, userId, token);
        } else {
            mtUser = memberService.queryMemberById(userInfo.getId());
        }

        if (mtUser == null) {
            return getFailureResult(1001);
        }

        if (StringUtil.isEmpty(cartIds)) {
            if (StringUtil.isNotEmpty(hangNo)) {
                cartService.removeCartByHangNo(hangNo);
            } else {
                cartService.clearCart(mtUser.getId());
            }
        } else {
            cartService.removeCart(cartIds);
        }

        return getSuccessResult(true);
    }

    /**
     * 获取购物车列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer storeId = request.getHeader("storeId") == null ? 0 : Integer.parseInt(request.getHeader("storeId"));
        Integer goodsId = params.get("goodsId") == null ? 0 : Integer.parseInt(params.get("goodsId").toString());
        Integer skuId = params.get("skuId") == null ? 0 : Integer.parseInt(params.get("skuId").toString());
        Integer buyNum = params.get("buyNum") == null ? 1 : Integer.parseInt(params.get("buyNum").toString());
        String cartIds = params.get("cartIds") == null ? "" : params.get("cartIds").toString();
        Integer userCouponId = params.get("couponId") == null ? 0 : Integer.parseInt(params.get("couponId").toString());// 会员卡券ID
        Integer userId = params.get("userId") == null ? 0 : Integer.parseInt(params.get("userId").toString()); // 会员ID
        String point = params.get("point") == null ? "" : params.get("point").toString();
        String hangNo = params.get("hangNo") == null ? "" : params.get("hangNo").toString();
        boolean isUsePoint = false;
        if (point.equals("true")) {
            isUsePoint = true;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", new ArrayList<>());
        result.put("totalNum", 0);
        result.put("totalPrice", 0);
        result.put("couponList", new ArrayList<>());
        result.put("useCouponInfo", null);

        Map<String, Object> param = new HashMap<>();
        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);
        MtUser mtUser;
        // 没有会员信息，则查询是否是后台收银员下单
        if (userInfo == null) {
            mtUser = memberService.getCurrentUserInfo(request, userId, token);
        } else {
            mtUser = memberService.queryMemberById(userInfo.getId());
        }

        if (null == mtUser) {
            return getSuccessResult(result);
        } else {
            param.put("userId", mtUser.getId());
        }

        if (StringUtil.isNotEmpty(cartIds)) {
            param.put("ids", cartIds);
        }

        param.put("status", StatusEnum.ENABLED.getKey());
        if (StringUtil.isNotEmpty(hangNo)) {
            param = new HashMap<>();
            param.put("hangNo", hangNo);
        } else {
            param.put("hangNo", "");
        }
        if (storeId > 0) {
            param.put("storeId", storeId);
        }
        List<MtCart> cartList = new ArrayList<>();

        if (goodsId < 1) {
            cartList = cartService.queryCartListByParams(param);
        } else {
            // 直接购买
            MtCart mtCart = new MtCart();
            mtCart.setGoodsId(goodsId);

            // 校验skuId是否正确
            if (skuId > 0) {
                Map<String, Object> skuParam = new HashMap<>();
                skuParam.put("goods_id", goodsId);
                skuParam.put("id", skuId);
                List<MtGoodsSku> skuList = mtGoodsSkuMapper.selectByMap(skuParam);
                // 该skuId不正常
                if (skuList.size() < 1) {
                    skuId = 0;
                }
            }

            mtCart.setSkuId(skuId);
            mtCart.setNum(buyNum);
            mtCart.setId(0);

            if (mtUser != null) {
                mtCart.setUserId(mtUser.getId());
            }

            mtCart.setStatus(StatusEnum.ENABLED.getKey());
            cartList.add(mtCart);
        }

        result = orderService.calculateCartGoods(mtUser.getId(), cartList, userCouponId, isUsePoint);

        return getSuccessResult(result);
    }
}

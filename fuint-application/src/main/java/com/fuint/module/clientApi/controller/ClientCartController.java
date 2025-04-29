package com.fuint.module.clientApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.enums.OrderModeEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.YesOrNoEnum;
import com.fuint.common.param.CartClearParam;
import com.fuint.common.param.CartListParam;
import com.fuint.common.param.CartSaveParam;
import com.fuint.common.service.*;
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
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
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
@AllArgsConstructor
@RequestMapping(value = "/clientApi/cart")
public class ClientCartController extends BaseController {

    private MtGoodsSkuMapper mtGoodsSkuMapper;

    /**
     * 购物车服务接口
     * */
    private CartService cartService;

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 商品服务接口
     * */
    private GoodsService goodsService;

    /**
     * 会员接口
     * */
    private MemberService memberService;

    /**
     * 商户服务接口
     */
    private MerchantService merchantService;

    /**
     * 保存购物车
     */
    @ApiOperation(value = "保存购物车")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject save(HttpServletRequest request, @RequestBody CartSaveParam saveParam) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        Integer storeId = request.getHeader("storeId") == null ? 0 : Integer.parseInt(request.getHeader("storeId"));
        Integer cartId = saveParam.getCartId() == null ? 0 : saveParam.getCartId();
        Integer goodsId = saveParam.getGoodsId() == null ? 0 : saveParam.getGoodsId();
        Integer skuId = saveParam.getSkuId() == null ? 0 : saveParam.getSkuId();
        String skuNo = saveParam.getSkuNo() == null ? "" : saveParam.getSkuNo();
        Double buyNum = saveParam.getBuyNum() == null ? 1 : saveParam.getBuyNum();
        String action = saveParam.getAction() == null ? "+" : saveParam.getAction();
        String hangNo = saveParam.getHangNo() == null ? "" : saveParam.getHangNo();
        Integer userId = saveParam.getUserId() == null ? 0 : saveParam.getUserId(); // 指定会员ID

        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);
        MtUser mtUser;
        if (userInfo == null) {
            mtUser = memberService.getCurrentUserInfo(request, userId, token);
        } else {
            mtUser = memberService.queryMemberById(userInfo.getId());
        }

        if (mtUser == null && StringUtil.isNotEmpty(token)) {
            AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
            if (accountInfo != null) {
                return getFailureResult(201, "该管理员还未关联店铺员工");
            }
            return getFailureResult(1001);
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
            } else {
                return getFailureResult(201, "该商品条码异常，可能已删除");
            }
        }

        // 商品ID不能为空
        if (goodsId == null || goodsId <= 0) {
            return getFailureResult(201, "该商品ID异常");
        }

        Integer merchantId = merchantService.getMerchantId(merchantNo);
        if (merchantId <= 0) {
            merchantId = mtUser.getMerchantId();
        }
        if (merchantId <= 0) {
            AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
            if (accountInfo != null) {
                merchantId = accountInfo.getMerchantId();
                if (merchantId == null || merchantId <= 0) {
                    return getFailureResult(201, "平台方账户无操作权限");
                }
            }
        }
        if (storeId <= 0 && mtUser.getStoreId() != null) {
            storeId = mtUser.getStoreId();
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
        mtCart.setMerchantId(merchantId);

        Integer id = cartService.saveCart(mtCart, action);
        Map<String, Object> data = new HashMap();
        data.put("cartId", id);

        return getSuccessResult(data);
    }

    /**
     * 删除购物车
     */
    @ApiOperation(value = "删除/清空购物车")
    @RequestMapping(value = "/clear", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject clear(HttpServletRequest request, @RequestBody CartClearParam clearParam) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String cartIds = clearParam.getCartId() == null ? "" : String.join(",", clearParam.getCartId());
        Integer userId = clearParam.getUserId() == null ? 0 : clearParam.getUserId();
        String hangNo = clearParam.getHangNo() == null ? "" : clearParam.getHangNo();

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
    @ApiOperation(value = "获取购物车列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request, @RequestBody CartListParam params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        Integer storeId = request.getHeader("storeId") == null ? 0 : Integer.parseInt(request.getHeader("storeId"));
        String platform = request.getHeader("platform") == null ? "" : request.getHeader("platform");
        Integer goodsId = params.getGoodsId() == null ? 0 : params.getGoodsId();
        Integer skuId = params.getSkuId() == null ? 0 : params.getSkuId();
        Double buyNum = params.getBuyNum() == null ? 1 : params.getBuyNum();
        String cartIds = params.getCartIds() == null ? "" : params.getCartIds();
        Integer userCouponId = params.getCouponId() == null ? 0 : params.getCouponId();// 会员卡券ID
        Integer userId = params.getUserId() == null ? 0 : params.getUserId(); // 会员ID
        String point = params.getPoint() == null ? "" : params.getPoint();
        String hangNo = params.getHangNo() == null ? "" : params.getHangNo();
        String orderMode = params.getOrderMode() == null ? OrderModeEnum.ONESELF.getKey() : params.getOrderMode();
        Integer merchantId = merchantService.getMerchantId(merchantNo);
        boolean isUsePoint = false;
        if (point.equals(YesOrNoEnum.TRUE.getKey())) {
            isUsePoint = true;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", new ArrayList<>());
        result.put("totalNum", 0);
        result.put("totalPrice", 0);
        result.put("couponList", new ArrayList<>());
        result.put("useCouponInfo", null);
        result.put("deliveryFee", 0);
        result.put("payPrice", 0);
        result.put("discount", 0);
        result.put("memberDiscount", 0);

        Map<String, Object> param = new HashMap<>();
        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);
        MtUser mtUser;
        // 没有会员信息，则查询是否是后台收银员下单
        if (userInfo == null) {
            mtUser = memberService.getCurrentUserInfo(request, userId, token);
            // 把收银员的购物信息切换给会员
            if (mtUser != null && StringUtil.isNotEmpty(cartIds)) {
                cartService.switchCartIds(userId, cartIds);
            }
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

        if (merchantId <= 0) {
            merchantId = mtUser.getMerchantId();
        }
        if (merchantId > 0) {
            param.put("merchantId", merchantId);
        }
        param.put("status", StatusEnum.ENABLED.getKey());
        if (StringUtil.isNotEmpty(hangNo)) {
            param.remove("userId");
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
        if (merchantId <= 0) {
            merchantId = mtUser.getMerchantId();
        }
        result = orderService.calculateCartGoods(merchantId, mtUser.getId(), cartList, userCouponId, isUsePoint, platform, orderMode);

        return getSuccessResult(result);
    }
}

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

    @Resource
    private MtGoodsSkuMapper mtGoodsSkuMapper;

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

    /**
     * 保存购物车
     */
    @ApiOperation(value = "添加、保存购物车")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject save(HttpServletRequest request, @RequestBody CartSaveParam cartSaveParam) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer storeId = request.getHeader("storeId") == null ? 0 : Integer.parseInt(request.getHeader("storeId"));
        Integer cartId = cartSaveParam.getCartId() == null ? 0 : cartSaveParam.getCartId();
        Integer goodsId = cartSaveParam.getGoodsId() == null ? 0 : cartSaveParam.getGoodsId();
        Integer skuId = cartSaveParam.getSkuId() == null ? 0 : cartSaveParam.getSkuId();
        String skuNo = cartSaveParam.getSkuNo() == null ? "" : cartSaveParam.getSkuNo();
        Integer buyNum = cartSaveParam.getBuyNum() == null ? 1 : cartSaveParam.getBuyNum();
        String action = cartSaveParam.getAction() == null ? "+" : cartSaveParam.getAction();
        String hangNo = cartSaveParam.getHangNo() == null ? "" : cartSaveParam.getHangNo();
        Integer userId = cartSaveParam.getUserId() == null ? 0 : cartSaveParam.getUserId(); // 指定会员ID

        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);
        MtUser mtUser;
        if (userInfo == null) {
            mtUser = memberService.getCurrentUserInfo(request, userId, token);
        } else {
            mtUser = memberService.queryMemberById(userInfo.getId());
        }

        if (mtUser == null) {
            AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
            if (accountInfo != null) {
                return getFailureResult(201, "请先将该账号关联店铺员工");
            }
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
    @ApiOperation(value = "删除/清空购物车")
    @RequestMapping(value = "/clear", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject clear(HttpServletRequest request, @RequestBody CartClearParam cartClearParam) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String cartIds = cartClearParam.getCartId() == null ? "" : String.join(",", cartClearParam.getCartId());
        Integer userId = cartClearParam.getUserId() == null ? 0 : cartClearParam.getUserId();
        String hangNo = cartClearParam.getHangNo() == null ? "" : cartClearParam.getHangNo();

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
    public ResponseObject list(HttpServletRequest request, @RequestBody CartListParam cartListParam) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer storeId = request.getHeader("storeId") == null ? 0 : Integer.parseInt(request.getHeader("storeId"));
        String platform = request.getHeader("platform") == null ? "" : request.getHeader("platform");
        Integer goodsId = cartListParam.getGoodsId() == null ? 0 : cartListParam.getGoodsId();
        Integer skuId = cartListParam.getSkuId() == null ? 0 : cartListParam.getSkuId();
        Integer buyNum = cartListParam.getBuyNum() == null ? 1 : cartListParam.getBuyNum();
        String cartIds = cartListParam.getCartIds() == null ? "" : cartListParam.getCartIds();
        Integer userCouponId = cartListParam.getCouponId() == null ? 0 : cartListParam.getCouponId();// 会员卡券ID
        Integer userId = cartListParam.getUserId() == null ? 0 : cartListParam.getUserId(); // 会员ID
        String point = cartListParam.getPoint() == null ? "" : cartListParam.getPoint();
        String hangNo = cartListParam.getHangNo() == null ? "" : cartListParam.getHangNo();
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
        result.put("deliveryFee", 0);

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

        result = orderService.calculateCartGoods(mtUser.getId(), cartList, userCouponId, isUsePoint, platform, OrderModeEnum.EXPRESS.getKey());

        return getSuccessResult(result);
    }
}

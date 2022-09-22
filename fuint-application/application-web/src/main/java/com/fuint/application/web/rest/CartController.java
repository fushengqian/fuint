package com.fuint.application.web.rest;

import com.fuint.application.dao.entities.MtCart;
import com.fuint.application.dao.entities.MtGoodsSku;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.dao.repositories.MtGoodsSkuRepository;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.cart.CartService;
import com.fuint.application.service.goods.GoodsService;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.order.OrderService;
import com.fuint.application.service.token.TokenService;
import com.fuint.base.shiro.ShiroUser;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.ResponseObject;
import com.fuint.application.BaseController;
import com.fuint.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车类controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/rest/cart")
public class CartController extends BaseController {

    /**
     * Token服务接口
     */
    @Autowired
    private TokenService tokenService;

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

    @Autowired
    private MtGoodsSkuRepository goodsSkuRepository;

    /**
     * 保存购物车
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject save(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer cartId = param.get("cartId") == null ? 0 : Integer.parseInt(param.get("cartId").toString());
        Integer goodsId = param.get("goodsId") == null ? 0 : Integer.parseInt(param.get("goodsId").toString());
        Integer skuId = param.get("skuId") == null ? 0 : Integer.parseInt(param.get("skuId").toString());
        String skuNo = param.get("skuNo") == null ? "" : param.get("skuNo").toString();
        Integer buyNum = param.get("buyNum") == null ? 1 : Integer.parseInt(param.get("buyNum").toString());
        String action = param.get("action") == null ? "+" : param.get("action").toString();
        Integer userId = param.get("userId") == null ? 0 : Integer.parseInt(param.get("userId").toString());

        MtUser mtUser = tokenService.getUserInfoByToken(token);

        if (mtUser == null) {
            mtUser = memberService.getCurrentUserInfo(userId);
        }

        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        if (shiroUser != null && mtUser == null) {
            return getFailureResult(4000);
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
        mtCart.setNum(buyNum);
        mtCart.setSkuId(skuId);
        mtCart.setId(cartId);

        Integer id = cartService.saveCart(mtCart, action);

        Map<String, Object> data = new HashMap();
        data.put("cartId", id);

        return getSuccessResult(data);
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

        MtUser mtUser = tokenService.getUserInfoByToken(token);
        if (mtUser == null) {
            mtUser = memberService.getCurrentUserInfo(userId);
        }

        if (null == mtUser) {
            return getFailureResult(1001);
        }
        if (StringUtil.isEmpty(cartIds)) {
            cartService.clearCart(mtUser.getId());
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
        Integer goodsId = params.get("goodsId") == null ? 0 : Integer.parseInt(params.get("goodsId").toString());
        Integer skuId = params.get("skuId") == null ? 0 : Integer.parseInt(params.get("skuId").toString());
        Integer buyNum = params.get("buyNum") == null ? 1 : Integer.parseInt(params.get("buyNum").toString());
        String cartIds = params.get("cartIds") == null ? "" : params.get("cartIds").toString();
        Integer userCouponId = params.get("couponId") == null ? 0 : Integer.parseInt(params.get("couponId").toString());// 会员卡券ID
        Integer userId = params.get("userId") == null ? 0 : Integer.parseInt(params.get("userId").toString()); // 会员ID
        String point = params.get("point") == null ? "" : params.get("point").toString();
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
        MtUser mtUser = tokenService.getUserInfoByToken(token);

        // 没有会员信息，则查询是否是后台收银员下单
        if (mtUser == null) {
            mtUser = memberService.getCurrentUserInfo(userId);
        }

        if (null == mtUser) {
            return getSuccessResult(result);
        } else {
            param.put("EQ_userId", mtUser.getId().toString());
        }

        if (StringUtil.isNotEmpty(cartIds)) {
            param.put("IN_id", cartIds);
        }

        param.put("EQ_status", StatusEnum.ENABLED.getKey());
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
                skuParam.put("EQ_goodsId", goodsId.toString());
                skuParam.put("EQ_id", skuId.toString());
                Specification<MtGoodsSku> specification = goodsSkuRepository.buildSpecification(skuParam);
                Sort sort = new Sort(Sort.Direction.ASC, "id");
                List<MtGoodsSku> skuList = goodsSkuRepository.findAll(specification, sort);
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

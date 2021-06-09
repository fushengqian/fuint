package com.fuint.application.web.rest;

import com.fuint.application.dao.entities.MtCoupon;
import com.fuint.application.dao.entities.MtOrder;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.dto.OrderDto;
import com.fuint.application.enums.OrderTypeEnum;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.order.OrderService;
import com.fuint.application.service.token.TokenService;
import com.fuint.application.service.usercoupon.UserCouponService;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.ResponseObject;
import com.fuint.application.BaseController;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 结算接口controller
 * Created by zach on 2021/5/2.
 */
@RestController
@RequestMapping(value = "/rest/settlement")
public class SettlementController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(PageController.class);

    /**
     * 会员卡券服务接口
     * */
    @Autowired
    private UserCouponService userCouponService;

    /**
     * Token服务接口
     */
    @Autowired
    private TokenService tokenService;

    /**
     * 订单服务接口
     * */
    @Autowired
    private OrderService orderService;

    /**
     * 卡券服务接口
     */
    @Autowired
    private CouponService couponService;

    /**
     * 结算提交
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject submit(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        MtUser mtUser = tokenService.getUserInfoByToken(token);
        if (null == mtUser) {
            return getFailureResult(1001);
        }
        param.put("userId", mtUser.getId());

        Integer couponId = param.get("couponId") == null ? 0 : Integer.parseInt(param.get("couponId").toString());
        String selectNum = param.get("selectNum") == null ? "" : param.get("selectNum").toString();
        String remark = param.get("remark") == null ? "" : param.get("remark").toString();
        String type = param.get("type") == null ? "" : param.get("type").toString();

        // 生成订单数据
        OrderDto orderDto = new OrderDto();
        orderDto.setCouponId(couponId);
        orderDto.setRemark(remark);
        orderDto.setUserId(mtUser.getId());
        orderDto.setType(type);

        // 预存卡的订单
        String orderParam = "";
        BigDecimal totalAmount = new BigDecimal(0);
        if (orderDto.getType().equals(OrderTypeEnum.PRESTORE.getKey())) {
            MtCoupon couponInfo = couponService.queryCouponById(couponId.longValue());
            String inRule = couponInfo.getInRule();
            String[] selectNumArr = selectNum.split(",");
            String[] ruleArr = inRule.split(",");
            for (int i = 0; i < ruleArr.length; i++) {
                String item = ruleArr[i] + "_" + (StringUtils.isNotEmpty(selectNumArr[i]) ? selectNumArr[i] : 0);
                String[] itemArr = item.split("_");
                // 预存金额
                BigDecimal price = new BigDecimal(itemArr[0]);
                // 预存数量
                BigDecimal num = new BigDecimal(selectNumArr[i]);
                BigDecimal amount = price.multiply(num);
                totalAmount = totalAmount.add(amount);
                orderParam = StringUtils.isEmpty(orderParam) ?  item : orderParam + ","+item;
            }
        }
        orderDto.setParam(orderParam);
        orderDto.setAmount(totalAmount);

        MtOrder orderInfo = orderService.createOrder(orderDto);
        param.put("orderId", orderInfo.getId());

        // @TODO 给前台生成支付信息，支付成功回调
        userCouponService.preStore(param);

        Map<String, Object> outParams = new HashMap();

        outParams.put("isCreated", true);
        outParams.put("payType", "WECHAT");

        ResponseObject responseObject = getSuccessResult(outParams);

        return getSuccessResult(responseObject.getData());
    }
}

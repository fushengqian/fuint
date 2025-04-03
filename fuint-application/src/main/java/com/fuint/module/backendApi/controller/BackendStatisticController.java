package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.GoodsTopDto;
import com.fuint.common.dto.MemberTopDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.StatisticParam;
import com.fuint.common.service.GoodsService;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.OrderService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.DateUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtStore;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * 数据统计控制器
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-数据统计相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/statistic")
public class BackendStatisticController extends BaseController {

    /**
     * 会员服务接口
     * */
    private MemberService memberService;

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 商品服务接口
     * */
    private GoodsService goodsService;

    /**
     * 店铺服务接口
     */
    private StoreService storeService;

    /**
     * 数据概况
     *
     * @return
     */
    @ApiOperation(value = "数据概况")
    @RequestMapping(value = "/main", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject main(HttpServletRequest request, @RequestBody StatisticParam param) throws BusinessCheckException, ParseException {
        String token = request.getHeader("Access-Token");
        String startTimeStr = param.getStartTime();
        String endTimeStr = param.getEndTime();
        Integer storeId = param.getStoreId();

        Date startTime = StringUtil.isNotEmpty(startTimeStr) ? DateUtil.parseDate(startTimeStr) : null;
        Date endTime = StringUtil.isNotEmpty(endTimeStr) ? DateUtil.parseDate(endTimeStr) : null;

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        Integer merchantId = accountInfo.getMerchantId();
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            storeId = accountInfo.getStoreId();
        }

        // 总会员数
        Long totalUserCount = memberService.getUserCount(merchantId, storeId);
        // 新增会员数量
        Long userCount = memberService.getUserCount(merchantId, storeId, startTime, endTime);

        // 总订单数
        BigDecimal totalOrderCount = orderService.getOrderCount(merchantId, storeId);
        // 订单数
        BigDecimal orderCount = orderService.getOrderCount(merchantId, storeId, startTime, endTime);

        // 交易金额
        BigDecimal payAmount = orderService.getPayMoney(merchantId, storeId, startTime, endTime);
        // 总交易金额
        BigDecimal totalPayAmount = orderService.getPayMoney(merchantId, storeId);

        // 活跃会员数
        Long activeUserCount = memberService.getActiveUserCount(merchantId, storeId, startTime, endTime);

        // 总支付人数
        Integer totalPayUserCount = orderService.getPayUserCount(merchantId, storeId);

        // 店铺列表
        Map<String, Object> params = new HashMap<>();
        params.put("status", StatusEnum.ENABLED.getKey());
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            params.put("storeId", accountInfo.getStoreId().toString());
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            params.put("merchantId", accountInfo.getMerchantId());
        }
        List<MtStore> storeList = storeService.queryStoresByParams(params);

        Map<String, Object> result = new HashMap<>();
        result.put("userCount", userCount);
        result.put("totalUserCount", totalUserCount);
        result.put("orderCount", orderCount);
        result.put("totalOrderCount", totalOrderCount);
        result.put("payAmount", payAmount);
        result.put("totalPayAmount", totalPayAmount);
        result.put("activeUserCount", activeUserCount);
        result.put("totalPayUserCount", totalPayUserCount);
        result.put("storeList", storeList);

        return getSuccessResult(result);
    }

    /**
     * 排行榜数据
     *
     * @return
     */
    @ApiOperation(value = "排行榜数据")
    @RequestMapping(value = "/top", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject top(HttpServletRequest request, @RequestBody StatisticParam param) throws ParseException {
        String token = request.getHeader("Access-Token");
        String startTimeStr = param.getStartTime();
        String endTimeStr = param.getEndTime();
        Integer storeId = param.getStoreId();

        Date startTime = StringUtil.isNotEmpty(startTimeStr) ? DateUtil.parseDate(startTimeStr) : null;
        Date endTime = StringUtil.isNotEmpty(endTimeStr) ? DateUtil.parseDate(endTimeStr) : null;

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        Integer merchantId = accountInfo.getMerchantId();
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            storeId = accountInfo.getStoreId();
        }

        List<GoodsTopDto> goodsList = goodsService.getGoodsSaleTopList(merchantId, storeId, startTime, endTime);
        List<MemberTopDto> memberList = memberService.getMemberConsumeTopList(merchantId, storeId, startTime, endTime);

        Map<String, Object> result = new HashMap<>();
        result.put("goodsList", goodsList);
        result.put("memberList", memberList);

        return getSuccessResult(result);
    }

    /**
     * 获取会员数量
     *
     * @return
     */
    @ApiOperation(value = "获取会员数量")
    @RequestMapping(value = "/totalMember", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject totalMember(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);

        Integer merchantId = accountInfo.getMerchantId();
        Integer storeId = accountInfo.getStoreId();

        Long totalMember = memberService.getUserCount(merchantId, storeId);
        Map<String, Object> result = new HashMap<>();
        result.put("totalMember", totalMember);

        return getSuccessResult(result);
    }
}

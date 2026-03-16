package com.fuint.module.backendApi.controller.common;

import com.fuint.common.dto.goods.GoodsTopDto;
import com.fuint.common.dto.member.MemberTopDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.param.StatisticParam;
import com.fuint.common.service.GoodsService;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.ReportService;
import com.fuint.common.util.DateUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 商品服务接口
     * */
    private GoodsService goodsService;

    /**
     * 报表服务接口
     * */
    private ReportService reportService;

    /**
     * 数据概况
     */
    @ApiOperation(value = "数据概况")
    @RequestMapping(value = "/main", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject main(@RequestBody StatisticParam param) throws ParseException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Integer merchantId = accountInfo.getMerchantId();
        Integer storeId = param.getStoreId();
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            storeId = accountInfo.getStoreId();
        }
        Map<String, Object> result = reportService.getReportOverview(merchantId, storeId, param.getStartTime(), param.getEndTime());
        return getSuccessResult(result);
    }

    /**
     * 排行榜数据
     */
    @ApiOperation(value = "排行榜数据")
    @RequestMapping(value = "/top", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject top(@RequestBody StatisticParam param) throws ParseException {
        String startTimeStr = param.getStartTime();
        String endTimeStr = param.getEndTime();
        Integer storeId = param.getStoreId();

        Date startTime = StringUtil.isNotEmpty(startTimeStr) ? DateUtil.parseDate(startTimeStr) : null;
        Date endTime = StringUtil.isNotEmpty(endTimeStr) ? DateUtil.parseDate(endTimeStr) : null;

        AccountInfo accountInfo = TokenUtil.getAccountInfo();

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
     */
    @ApiOperation(value = "获取会员数量")
    @RequestMapping(value = "/totalMember", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject totalMember() {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        Integer merchantId = accountInfo.getMerchantId();
        Integer storeId = accountInfo.getStoreId();

        Long totalMember = memberService.getUserCount(merchantId, storeId);
        Map<String, Object> result = new HashMap<>();
        result.put("totalMember", totalMember);

        return getSuccessResult(result);
    }
}

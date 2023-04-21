package com.fuint.module.backendApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.ParamDto;
import com.fuint.common.enums.CouponTypeEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.UserCouponStatusEnum;
import com.fuint.common.service.*;
import com.fuint.common.util.DateUtil;
import com.fuint.common.util.ExcelUtil;
import com.fuint.common.util.TokenUtil;
import static com.fuint.common.util.XlsUtil.objectConvertToString;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.MtSendLogMapper;
import com.fuint.repository.mapper.MtUserCouponMapper;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

/**
 * 会员卡券统计管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-会员卡券统计相关接口")
@RestController
@RequestMapping(value = "/backendApi/userCoupon")
public class BackendUserCouponController extends BaseController {

    @Resource
    private MtUserCouponMapper mtUserCouponMapper;

    @Resource
    private MtSendLogMapper mtSendLogMapper;

    /**
     * 卡券分组服务接口
     */
    @Autowired
    private UserCouponService userCouponService;

    /**
     * 卡券服务接口
     */
    @Autowired
    private CouponService couponService;

    /**
     * 店铺接口
     */
    @Autowired
    private StoreService storeService;

    /**
     * 后台用户接口
     * */
    @Autowired
    private AccountService accountService;

    @Autowired
    private SendLogService sendLogService;

    /**
     * 查询会员卡券列表
     *
     * @param request
     * @return
     * @throws BusinessCheckException
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        Map<String, Object> param = new HashMap<>();
        param.put("pageNumber", page);
        param.put("pageSize", pageSize);
        param.put("status", request.getParameter("status"));
        param.put("userId", request.getParameter("userId"));
        param.put("mobile", request.getParameter("mobile"));
        param.put("storeId", request.getParameter("storeId"));
        param.put("couponId", request.getParameter("couponId"));
        param.put("type", request.getParameter("type"));
        param.put("code", request.getParameter("code"));

        ResponseObject result = userCouponService.getUserCouponList(param);

        Map<String, Object> storeParams = new HashMap<>();
        storeParams.put("status", StatusEnum.ENABLED.getKey());
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            storeParams.put("storeId", accountInfo.getStoreId().toString());
        }
        List<MtStore> storeList = storeService.queryStoresByParams(storeParams);

        // 卡券类型列表
        CouponTypeEnum[] typeListEnum = CouponTypeEnum.values();
        List<ParamDto> typeList = new ArrayList<>();
        for (CouponTypeEnum enumItem : typeListEnum) {
            ParamDto paramDto = new ParamDto();
            paramDto.setKey(enumItem.getKey());
            paramDto.setName(enumItem.getValue());
            paramDto.setValue(enumItem.getKey());
            typeList.add(paramDto);
        }

        // 卡券状态列表
        UserCouponStatusEnum[] statusListEnum = UserCouponStatusEnum.values();
        List<ParamDto> statusList = new ArrayList<>();
        for (UserCouponStatusEnum enumItem : statusListEnum) {
            ParamDto paramDto = new ParamDto();
            paramDto.setKey(enumItem.getKey());
            paramDto.setName(enumItem.getValue());
            paramDto.setValue(enumItem.getKey());
            statusList.add(paramDto);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("paginationResponse", result.getData());
        data.put("storeList", storeList);
        data.put("typeList", typeList);
        data.put("statusList", statusList);

        return getSuccessResult(data);
    }

    /**
     * 核销用户卡券
     * */
    @RequestMapping(value = "/doConfirm", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject doConfirm(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String userCouponId = request.getParameter("userCouponId");
        MtUserCoupon mtUserCoupon = couponService.queryUserCouponById(Integer.parseInt(userCouponId));

        if (mtUserCoupon == null || StringUtil.isEmpty(userCouponId)) {
            throw new BusinessCheckException("错误，用户卡券不存在！");
        }

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        TAccount account = accountService.getAccountInfoById(accountInfo.getId());
        Integer storeId = account.getStoreId();

        BigDecimal confirmAmount = mtUserCoupon.getAmount();
        if (mtUserCoupon.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
            confirmAmount = mtUserCoupon.getBalance();
        }

        try {
            couponService.useCoupon(Integer.parseInt(userCouponId), accountInfo.getId(), storeId, 0, confirmAmount, "后台核销");
        } catch (BusinessCheckException e) {
            return getFailureResult(201, e.getMessage());
        }

        return getSuccessResult(true);
    }

    /**
     * 作废会员卡券
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject delete(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        try {
            couponService.deleteUserCoupon(id, accountInfo.getAccountName());
        } catch (BusinessCheckException e) {
            return getFailureResult(201, e.getMessage());
        }

        // 发券记录，部分作废
        MtUserCoupon userCoupon = mtUserCouponMapper.selectById(id);
        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(Constants.PAGE_NUMBER);
        paginationRequest.setPageSize(Constants.MAX_ROWS);
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("uuid", userCoupon.getUuid());
        paginationRequest.setSearchParams(requestParams);
        PaginationResponse<MtSendLog> list = sendLogService.querySendLogListByPagination(paginationRequest);
        if (list.getContent().size() > 0) {
            MtSendLog sendLog = list.getContent().get(0);
            if (sendLog.getStatus().equals(UserCouponStatusEnum.UNUSED.getKey())) {
                Integer total = sendLog.getRemoveSuccessNum();
                if (null == total) {
                    total = 0;
                }
                sendLog.setRemoveSuccessNum((total + 1));
                sendLog.setStatus(UserCouponStatusEnum.USED.getKey());
                mtSendLogMapper.updateById(sendLog);
            }
        }

        return getSuccessResult(true);
    }

    /**
     * 导出会员卡券
     *
     * @return
     */
    @RequestMapping(value = "/exportList", method = RequestMethod.GET)
    @CrossOrigin
    public void exportList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String token = request.getParameter("token");
        String mobile = request.getParameter("mobile") == null ? "" : request.getParameter("mobile");
        String userId = request.getParameter("userId") == null ? "" : request.getParameter("userId");
        String couponId = request.getParameter("couponId") == null ? "" : request.getParameter("couponId");
        String status = request.getParameter("status") == null ? "" : request.getParameter("status");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return;
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(1);
        paginationRequest.setPageSize(50000);

        Map<String, Object> searchParams = new HashMap<>();
        if (StringUtil.isNotEmpty(mobile)) {
            searchParams.put("mobile", mobile);
        }
        if (StringUtil.isNotEmpty(userId)) {
            searchParams.put("userId", userId);
        }
        if (StringUtil.isNotEmpty(couponId)) {
            searchParams.put("couponId", couponId);
        }
        if (StringUtil.isNotEmpty(status)) {
            searchParams.put("status", status);
        }

        paginationRequest.setSearchParams(searchParams);
        PaginationResponse<MtUserCoupon> result = userCouponService.queryUserCouponListByPagination(paginationRequest);

        // excel标题
        String[] title = { "核销二维码", "卡券ID", "卡券名称", "会员手机号", "状态", "面额", "余额" };

        // excel文件名
        String fileName = "会员卡券"+ DateUtil.formatDate(new Date(), "yyyy.MM.dd_HHmm") +".xls";

        // sheet名
        String sheetName = "数据列表";

        String[][] content = null;

        List<MtUserCoupon> list = result.getContent();

        if (list.size() > 0) {
            content= new String[list.size()][title.length];
        }

        for (int i = 0; i < list.size(); i++) {
             MtUserCoupon obj = list.get(i);
             MtCoupon mtCoupon = couponService.queryCouponById(obj.getCouponId());
             if (mtCoupon != null) {
                 content[i][0] = objectConvertToString(obj.getCode());
                 content[i][1] = objectConvertToString(obj.getCouponId());
                 content[i][2] = objectConvertToString(mtCoupon.getName());
                 content[i][3] = objectConvertToString(obj.getMobile());
                 content[i][4] = UserCouponStatusEnum.getValue(obj.getStatus());
                 content[i][5] = objectConvertToString(obj.getAmount() != null ? obj.getAmount().toString() : "0.00");
                 content[i][6] = objectConvertToString(obj.getBalance() != null ? obj.getBalance().toString() : "0.00");
             }
        }

        // 创建HSSFWorkbook
        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook(sheetName, title, content, null);
        ExcelUtil.setResponseHeader(response, fileName, wb);
    }
}

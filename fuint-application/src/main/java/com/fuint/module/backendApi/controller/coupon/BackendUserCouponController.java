package com.fuint.module.backendApi.controller.coupon;




import java.util.ArrayList;
import java.io.IOException;
import com.fuint.common.vo.UserCouponExportVo;
import com.fuint.common.Constants;
import com.fuint.common.dto.common.ParamDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.enums.CouponTypeEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.UserCouponStatusEnum;
import com.fuint.common.param.SendLogPage;
import com.fuint.common.param.UserCouponPage;
import com.fuint.common.service.*;
import com.fuint.common.util.DateUtil;
import com.fuint.common.util.ExcelUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.MtSendLogMapper;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fuint.common.util.XlsUtil.objectConvertToString;

/**
 * 会员卡券统计管理controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name="管理端-会员卡券统计相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/userCoupon")
public class BackendUserCouponController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BackendUserCouponController.class);

    private MtSendLogMapper mtSendLogMapper;

    /**
     * 卡券分组服务接口
     */
    private UserCouponService userCouponService;

    /**
     * 卡券服务接口
     */
    private CouponService couponService;

    /**
     * 店铺接口
     */
    private StoreService storeService;

    /**
     * 卡券发放记录接口
     * */
    private SendLogService sendLogService;

    /**
     * 店铺员工服务接口
     * */
    private StaffService staffService;

    /**
     * 查询会员卡券列表
     */
    @Operation(summary="查询会员卡券列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('coupon:userCoupon:index')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Map<String, Object> param = new HashMap<>();
        param.put("pageNumber", page);
        param.put("pageSize", pageSize);
        param.put("status", request.getParameter("status"));
        param.put("userId", request.getParameter("userId"));
        param.put("userNo", request.getParameter("userNo"));
        param.put("mobile", request.getParameter("mobile"));
        param.put("storeId", request.getParameter("storeId"));
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            param.put("merchantId", accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            param.put("storeId", accountInfo.getStoreId());
        }
        param.put("couponId", request.getParameter("couponId"));
        param.put("id", request.getParameter("id"));
        param.put("type", request.getParameter("type"));
        param.put("code", request.getParameter("code"));

        ResponseObject result = userCouponService.getUserCouponList(param);

        // 店铺列表
        List<MtStore> storeList = storeService.getMyStoreList(accountInfo.getMerchantId(), accountInfo.getStoreId(), StatusEnum.ENABLED.getKey());

        // 卡券类型列表
        List<ParamDto> typeList = CouponTypeEnum.getCouponTypeList();

        // 卡券状态列表
        List<ParamDto> statusList = UserCouponStatusEnum.getUserCouponStatusList();

        Map<String, Object> data = new HashMap<>();
        data.put("paginationResponse", result.getData());
        data.put("storeList", storeList);
        data.put("typeList", typeList);
        data.put("statusList", statusList);

        return getSuccessResult(data);
    }

    /**
     * 核销用户卡券
     */
    @Operation(summary="核销用户卡券")
    @RequestMapping(value = "/doConfirm", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('coupon:userCoupon:index')")
    public ResponseObject doConfirm(HttpServletRequest request) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        String userCouponId = request.getParameter("userCouponId");
        MtUserCoupon mtUserCoupon = couponService.queryUserCouponById(Integer.parseInt(userCouponId));

        if (mtUserCoupon == null || StringUtil.isEmpty(userCouponId)) {
            throw new BusinessCheckException("错误，用户卡券不存在！");
        }
        if (!mtUserCoupon.getMerchantId().equals(accountInfo.getMerchantId())) {
            return getFailureResult(1004);
        }
        Integer storeId = accountInfo.getStoreId();
        BigDecimal confirmAmount = mtUserCoupon.getAmount();
        if (mtUserCoupon.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
            confirmAmount = mtUserCoupon.getBalance();
        }
        Integer userId = 0;
        if (accountInfo.getStaffId() != null && accountInfo.getStaffId() > 0) {
            MtStaff staff = staffService.queryStaffById(accountInfo.getStaffId());
            if (staff != null) {
                userId = staff.getUserId();
            }
        }
        couponService.useCoupon(Integer.parseInt(userCouponId), userId, storeId, 0, confirmAmount, "后台核销", accountInfo.getAccountName());
        return getSuccessResult(true);
    }

    /**
     * 删除会员卡券
     */
    @Operation(summary="删除会员卡券")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('coupon:userCoupon:delete')")
    public ResponseObject delete(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        // 删除会员的卡券
        couponService.deleteUserCoupon(id, accountInfo);

        // 发券记录，部分作废
        SendLogPage sendLogPage = new SendLogPage();
        sendLogPage.setPage(Constants.PAGE_NUMBER);
        sendLogPage.setPageSize(Constants.MAX_ROWS);
        // 注意：这里需要通过uuid查询，但SendLogPage没有uuid字段，需要后续补充
        PaginationResponse<MtSendLog> list = sendLogService.querySendLogListByPagination(sendLogPage);
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
     */
    @Operation(summary="导出会员卡券")
    @RequestMapping(value = "/exportList", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('coupon:userCoupon:index')")
    public void exportList(HttpServletRequest request, HttpServletResponse response, @ModelAttribute UserCouponPage userCouponPage) throws IOException {
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(request.getParameter("token"));
        if (accountInfo == null) {
            logger.error("导出会员卡券失败：登录信息失效");
            return;
        }

        userCouponPage.setPage(1);
        userCouponPage.setPageSize(Constants.MAX_ROWS);
        PaginationResponse<MtUserCoupon> result = userCouponService.queryUserCouponListByPagination(userCouponPage);

        // 组装导出数据（表头由 UserCouponExportVo 的 @ExcelProperty 声明）
        List<UserCouponExportVo> data = new ArrayList<>();
        for (MtUserCoupon obj : result.getContent()) {
            if (obj == null) {
                continue;
            }
            MtCoupon mtCoupon = couponService.queryCouponById(obj.getCouponId());
            if (mtCoupon == null) {
                continue;
            }
            UserCouponExportVo vo = new UserCouponExportVo();
            vo.setCode(objectConvertToString(obj.getCode()));
            vo.setCouponId(objectConvertToString(obj.getCouponId()));
            vo.setCouponName(objectConvertToString(mtCoupon.getName()));
            vo.setMobile(objectConvertToString(obj.getMobile()));
            vo.setStatus(UserCouponStatusEnum.getValue(obj.getStatus()));
            vo.setAmount(objectConvertToString(obj.getAmount() != null ? obj.getAmount().toString() : "0.00"));
            vo.setBalance(objectConvertToString(obj.getBalance() != null ? obj.getBalance().toString() : "0.00"));
            data.add(vo);
        }

        ExcelUtil.exportXlsx(response, "会员卡券" + DateUtil.formatDate(new Date(), "yyyy.MM.dd_HHmm"), "数据列表", UserCouponExportVo.class, data);

        logger.info("导出会员卡券成功：accountInfo = {}", accountInfo.getAccountName());
    }
}

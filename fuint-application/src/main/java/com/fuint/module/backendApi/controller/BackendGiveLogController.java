package com.fuint.module.backendApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.GiveDto;
import com.fuint.common.dto.GiveItemDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.AccountService;
import com.fuint.common.service.GiveService;
import com.fuint.common.util.ExcelUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.common.util.XlsUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.MtCouponGroupMapper;
import com.fuint.repository.mapper.MtCouponMapper;
import com.fuint.repository.mapper.MtUserCouponMapper;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import static com.fuint.common.util.XlsUtil.objectConvertToString;

import io.swagger.annotations.Api;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 转赠管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-转赠相关接口")
@RestController
@RequestMapping(value = "/backendApi/give")
public class BackendGiveLogController extends BaseController {

    /**
     * 转赠服务接口
     */
    @Autowired
    private GiveService giveService;

    /**
     * 后台账户服务接口
     */
    @Autowired
    private AccountService accountService;

    @Resource
    private MtUserCouponMapper mtUserCouponMapper;

    @Resource
    private MtCouponGroupMapper mtCouponGroupMapper;

    @Resource
    private MtCouponMapper mtCouponMapper;

    /**
     * 查询列表
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
        String mobile = request.getParameter("mobile") == null ? "" : request.getParameter("mobile");
        String userId = request.getParameter("userId") == null ? "" : request.getParameter("userId");
        String couponId = request.getParameter("couponId") == null ? "" : request.getParameter("couponId");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        TAccount account = accountService.getAccountInfoById(accountInfo.getId());
        Integer storeId = account.getStoreId();
        Map<String, Object> params = new HashMap<>();
        if (StringUtil.isNotEmpty(mobile)) {
            params.put("mobile", mobile);
        }
        if (StringUtil.isNotEmpty(userId)) {
            params.put("userId", userId);
        }
        if (StringUtil.isNotEmpty(couponId)) {
            params.put("couponId", couponId);
        }
        if (storeId > 0) {
            params.put("storeId", storeId);
        }
        paginationRequest.setSearchParams(params);
        PaginationResponse<GiveDto> paginationResponse = giveService.queryGiveListByPagination(paginationRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }

    /**
     * 快速查询详情
     * */
    @RequestMapping(value = "/giveItem", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject giveItem(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String giveId = request.getParameter("giveId");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        if (StringUtil.isEmpty(giveId)) {
            return getFailureResult(201, "参数有误");
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(1);
        paginationRequest.setPageSize(5000);

        Map<String, Object> params = new HashMap<>();
        params.put("status", StatusEnum.ENABLED.getKey());
        params.put("GIVE_ID", giveId);

        List<MtGiveItem> itemList = giveService.queryItemByParams(params);

        List<GiveItemDto> dataList = new ArrayList<>();
        for (MtGiveItem item : itemList) {
            MtGive giveInfo = giveService.queryGiveById(Long.parseLong(giveId));
            MtUserCoupon userCouponInfo = mtUserCouponMapper.selectById(item.getUserCouponId());
            if (userCouponInfo != null) {
                MtCouponGroup groupInfo = mtCouponGroupMapper.selectById(userCouponInfo.getGroupId());
                MtCoupon couponInfo = mtCouponMapper.selectById(userCouponInfo.getCouponId());
                if (groupInfo != null && couponInfo != null) {
                    GiveItemDto dto = new GiveItemDto();
                    dto.setId(item.getId());
                    dto.setMobile(giveInfo.getUserMobile());
                    dto.setUserMobile(giveInfo.getMobile());
                    dto.setGroupId(userCouponInfo.getGroupId());
                    dto.setGroupName(groupInfo.getName());
                    dto.setCouponId(userCouponInfo.getCouponId());
                    dto.setCouponName(couponInfo.getName());
                    dto.setMoney(userCouponInfo.getAmount());
                    dto.setCreateTime(item.getCreateTime());
                    dataList.add(dto);
                }
            }
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("itemList", dataList);

        return getSuccessResult(resultMap);
    }

    /**
     * 导出数据
     *
     * @return
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ResponseBody
    public void export(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(1);
        paginationRequest.setPageSize(50000);

        PaginationResponse<GiveDto> paginationResponse = giveService.queryGiveListByPagination(paginationRequest);
        List<GiveDto> list = paginationResponse.getContent();

        // excel标题
        String[] title = {"记录ID", "用户手机号", "转赠数量", "转赠总金额", "赠予对象手机号", "赠予时间"};
        String fileName;
        fileName = "转赠记录" + System.currentTimeMillis() + ".xls";

        String[][] content = null;
        if (list.size() > 0) {
            content= new String[list.size()][title.length];
        }

        for (int i = 0; i < list.size(); i++) {
             GiveDto obj = list.get(i);
             content[i][0] = objectConvertToString(obj.getId());
             content[i][1] = objectConvertToString(obj.getUserMobile());
             content[i][2] = objectConvertToString(obj.getNum());
             content[i][3] = objectConvertToString(obj.getMoney());
             content[i][4] = objectConvertToString(obj.getMobile());
             content[i][5] = objectConvertToString(obj.getCreateTime());
        }

        // 创建HSSFWorkbook
        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook("转赠记录", title, content, null);

        // 响应到客户端
        try {
            XlsUtil.setXlsHeader(request, response, fileName);
            OutputStream os = response.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

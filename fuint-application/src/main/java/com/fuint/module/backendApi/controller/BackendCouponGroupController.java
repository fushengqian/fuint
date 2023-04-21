package com.fuint.module.backendApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.*;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.CouponGroupService;
import com.fuint.common.service.CouponService;
import com.fuint.common.util.TokenUtil;
import com.fuint.common.util.XlsUtil;
import com.fuint.framework.dto.ExcelExportDto;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.service.ExportService;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.MtUserCouponMapper;
import com.fuint.repository.model.MtCouponGroup;
import com.fuint.repository.model.MtUserCoupon;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 卡券分组管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-卡券分组相关接口")
@RestController
@RequestMapping(value = "/backendApi/couponGroup")
public class BackendCouponGroupController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BackendCouponGroupController.class);

    @Resource
    private MtUserCouponMapper mtUserCouponMapper;

    /**
     * 卡券分组服务接口
     */
    @Autowired
    private CouponGroupService couponGroupService;

    /**
     * 卡券服务接口
     * */
    @Autowired
    CouponService couponService;

    @Autowired
    private ExportService exportService;

    /**
     * 查询卡券分组列表
     *
     * @param request
     * @return
     * @throws BusinessCheckException
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        Integer page = request.getParameter("page") == null ? 1 : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String name = request.getParameter("name") == null ? "" : request.getParameter("name");
        String id = request.getParameter("id") == null ? "" : request.getParameter("id");
        String status = request.getParameter("status") == null ? StatusEnum.ENABLED.getKey() : request.getParameter("status");

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> searchParams = new HashMap<>();
        if (StringUtil.isNotEmpty(name)) {
            searchParams.put("name", name);
        }
        if (StringUtil.isNotEmpty(id)) {
            searchParams.put("id", id);
        }
        if (StringUtil.isNotEmpty(status)) {
            searchParams.put("status", status);
        }

        paginationRequest.setSearchParams(searchParams);
        PaginationResponse<MtCouponGroup> paginationResponse = couponGroupService.queryCouponGroupListByPagination(paginationRequest);

        // 计算券种类、总价值
        if (paginationResponse.getContent().size() > 0) {
            for (int i = 0; i < paginationResponse.getContent().size(); i++) {
                MtCouponGroup object = paginationResponse.getContent().get(i);
                object.setMoney(couponGroupService.getCouponMoney(object.getId().intValue()));
                object.setNum(couponGroupService.getCouponNum(object.getId()));
            }
        }

        // 统计数据
        List<GroupDataListDto> groupData = new ArrayList<>();

        if (paginationResponse.getContent().size() > 0) {
            for (int i = 0; i < paginationResponse.getContent().size(); i++) {
                 Integer groupId = paginationResponse.getContent().get(i).getId();
                 GroupDataDto data = new GroupDataDto();
                 data.setCancelNum(0);
                 data.setExpireNum(0);
                 data.setUseNum(0);
                 data.setSendNum(0);
                 data.setUnSendNum(0);
                 GroupDataListDto item = new GroupDataListDto();
                 if (null != data) {
                     item.setKey(groupId.toString());
                     item.setData(data);
                     groupData.add(item);
                 }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        result.put("groupData", groupData);

        return getSuccessResult(result);
    }

    /**
     * 保存卡券分组信息
     *
     * @param request
     * @param reqCouponGroupDto
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject save(HttpServletRequest request, @RequestBody ReqCouponGroupDto reqCouponGroupDto) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        reqCouponGroupDto.setOperator(accountInfo.getAccountName());

        if (reqCouponGroupDto.getId() != null && reqCouponGroupDto.getId() > 0) {
            couponGroupService.updateCouponGroup(reqCouponGroupDto);
        } else {
            couponGroupService.addCouponGroup(reqCouponGroupDto);
        }

        return getSuccessResult(true);
    }

    /**
     * 删除卡券分组
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

        // 该分组已有数据，不允许删除
        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("GROUP_ID", id.toString());
        List<MtUserCoupon> dataList = mtUserCouponMapper.selectByMap(searchParams);
        if (dataList.size() > 0) {
            return getFailureResult(201, "已发放卡券，不允许删除");
        }

        couponGroupService.deleteCouponGroup(id, accountInfo.getAccountName());

        return getSuccessResult(true);
    }

    /**
     * 更新状态
     *
     * @return
     */
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject updateStatus(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String status = params.get("status") != null ? params.get("status").toString() : StatusEnum.ENABLED.getKey();
        Integer id = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        try {
            String operator = accountInfo.getAccountName();
            ReqCouponGroupDto groupDto = new ReqCouponGroupDto();
            groupDto.setOperator(operator);
            groupDto.setId(id);
            groupDto.setStatus(status);
            couponGroupService.updateCouponGroup(groupDto);
        } catch (BusinessCheckException e) {
            return getFailureResult(201, e.getMessage());
        }

        return getSuccessResult(true);
    }

    /**
     * 分组详情
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        MtCouponGroup mtCouponGroup = couponGroupService.queryCouponGroupById(id);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("groupInfo", mtCouponGroup);

        return getSuccessResult(resultMap);
    }

    /**
     * 导出模板文件
     *
     * @param request
     * @param response
     * @throws BusinessCheckException
     */
    @RequestMapping(value = "/exportTemplate", method = RequestMethod.GET)
    @CrossOrigin
    public void exportTemplate(HttpServletRequest request, HttpServletResponse response) throws BusinessCheckException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL url = classLoader.getResource("");
        String srcTemplateFilePath = url.getPath();

        ExcelExportDto excelExportDto = new ExcelExportDto();
        excelExportDto.setSrcPath(srcTemplateFilePath);
        excelExportDto.setSrcTemplateFileName("template" + File.separator + "importTemplate.xlsx");

        String filename = "批量发券模板" + ".xlsx";
        try {
            OutputStream out = response.getOutputStream();
            XlsUtil.setXlsHeader(request, response, filename);
            excelExportDto.setOut(out);
            exportService.exportLocalFile(excelExportDto);
        } catch (Exception e) {
            logger.error("导出出错", e);
            throw new BusinessCheckException("导出出错");
        }
    }

    /**
     * 上传文件
     *
     * @param request
     * @throws
     */
    @RequestMapping(value = "/upload/", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @CrossOrigin
    public ResponseObject uploadFile(HttpServletRequest request, @RequestParam("fileInput") MultipartFile file) {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        try {
            String filePath = couponGroupService.saveExcelFile(file, request);
            String uuid = couponGroupService.importSendCoupon(file, accountInfo.getAccountName(), filePath);
            return getSuccessResult(uuid);
        } catch (BusinessCheckException e) {
            return getFailureResult(201, e.getMessage());
        } catch (Exception e) {
            return getFailureResult(201, e.getMessage());
        }
    }

    /**
     * 查询分组列表
     * */
    @RequestMapping(value = "/quickSearch", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject quickSearch(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(Constants.PAGE_NUMBER);
        paginationRequest.setPageSize(Constants.MAX_ROWS);
        paginationRequest.setSearchParams(new HashMap<>());
        PaginationResponse<MtCouponGroup> paginationResponse = couponGroupService.queryCouponGroupListByPagination(paginationRequest);

        List<MtCouponGroup> groupList = paginationResponse.getContent();

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("groupList", groupList);

        return getSuccessResult(resultMap);
    }
}

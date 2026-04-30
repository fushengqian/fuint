package com.fuint.module.backendApi.controller.coupon;

import com.fuint.common.Constants;
import com.fuint.common.dto.coupon.GroupDataDto;
import com.fuint.common.dto.coupon.GroupDataListDto;
import com.fuint.common.dto.coupon.ReqCouponGroupDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.CouponGroupPage;
import com.fuint.common.param.StatusParam;
import com.fuint.common.service.CouponGroupService;
import com.fuint.common.service.UploadService;
import com.fuint.common.util.TokenUtil;
import com.fuint.common.util.XlsUtil;
import com.fuint.framework.dto.ExcelExportDto;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.service.ExportService;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.MtCouponMapper;
import com.fuint.repository.model.MtCoupon;
import com.fuint.repository.model.MtCouponGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
@AllArgsConstructor
@RequestMapping(value = "/backendApi/couponGroup")
public class BackendCouponGroupController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BackendCouponGroupController.class);

    private MtCouponMapper mtCouponMapper;

    /**
     * 卡券分组服务接口
     */
    private CouponGroupService couponGroupService;

    /**
     * 导出服务接口
     * */
    private ExportService exportService;

    /**
     * 上传文件服务接口
     * */
    private UploadService uploadService;

    /**
     * 查询卡券分组列表
     */
    @ApiOperation(value = "查询卡券分组列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('coupon:group:index')")
    public ResponseObject list(@ModelAttribute CouponGroupPage couponGroupPage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            couponGroupPage.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            couponGroupPage.setStoreId(accountInfo.getStoreId());
        }

        PaginationResponse<MtCouponGroup> paginationResponse = couponGroupService.queryCouponGroupListByPagination(couponGroupPage);

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
     */
    @ApiOperation(value = "保存卡券分组信息")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('coupon:group:edit')")
    public ResponseObject save(@RequestBody ReqCouponGroupDto reqCouponGroupDto) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        reqCouponGroupDto.setMerchantId(accountInfo.getMerchantId());
        reqCouponGroupDto.setStoreId(accountInfo.getStoreId());
        reqCouponGroupDto.setOperator(accountInfo.getAccountName());
        if (reqCouponGroupDto.getId() != null && reqCouponGroupDto.getId() > 0) {
            couponGroupService.updateCouponGroup(reqCouponGroupDto, accountInfo);
        } else {
            couponGroupService.addCouponGroup(reqCouponGroupDto);
        }
        return getSuccessResult(true);
    }

    /**
     * 删除卡券分组
     */
    @ApiOperation(value = "删除卡券分组")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('coupon:group:edit')")
    public ResponseObject delete(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        // 该分组已有数据，不允许删除
        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("GROUP_ID", id.toString());
        searchParams.put("status", StatusEnum.ENABLED.getKey());
        List<MtCoupon> dataList = mtCouponMapper.selectByMap(searchParams);
        if (dataList.size() > 0) {
            return getFailureResult(201, "该分组下有卡券，不能删除");
        }

        couponGroupService.deleteCouponGroup(id, accountInfo);
        return getSuccessResult(true);
    }

    /**
     * 更新分组状态
     */
    @ApiOperation(value = "更新分组状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('coupon:group:edit')")
    public ResponseObject updateStatus(@RequestBody StatusParam params) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() == null || accountInfo.getMerchantId() <= 0) {
            getFailureResult(5002);
        }
        ReqCouponGroupDto groupDto = new ReqCouponGroupDto();
        groupDto.setOperator(accountInfo.getAccountName());
        groupDto.setId(params.getId());
        groupDto.setStatus(params.getStatus());
        couponGroupService.updateCouponGroup(groupDto, accountInfo);
        return getSuccessResult(true);
    }

    /**
     * 获取分组详情
     */
    @ApiOperation(value = "获取分组详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('coupon:group:index')")
    public ResponseObject info(@PathVariable("id") Integer groupId) throws BusinessCheckException {
        MtCouponGroup mtCouponGroup = couponGroupService.queryCouponGroupById(groupId);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("groupInfo", mtCouponGroup);

        return getSuccessResult(resultMap);
    }

    /**
     * 导出模板文件
     */
    @ApiOperation(value = "导出模板文件")
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
     */
    @ApiOperation(value = "上传文件")
    @RequestMapping(value = "/upload/", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @CrossOrigin
    public ResponseObject uploadFile(HttpServletRequest request, @RequestParam("fileInput") MultipartFile file) throws Exception {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        String filePath = uploadService.saveUploadFile(request, file);
        String uuid = couponGroupService.importSendCoupon(file, accountInfo, filePath);
        return getSuccessResult(uuid);
    }

    /**
     * 查询分组列表
     * */
    @ApiOperation(value = "查询分组列表")
    @RequestMapping(value = "/quickSearch", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject quickSearch() {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        CouponGroupPage couponGroupPage = new CouponGroupPage();
        couponGroupPage.setPage(Constants.PAGE_NUMBER);
        couponGroupPage.setPageSize(Constants.ALL_ROWS);
        couponGroupPage.setStatus(StatusEnum.ENABLED.getKey());
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            couponGroupPage.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            couponGroupPage.setStoreId(accountInfo.getStoreId());
        }
        PaginationResponse<MtCouponGroup> paginationResponse = couponGroupService.queryCouponGroupListByPagination(couponGroupPage);

        List<MtCouponGroup> groupList = paginationResponse.getContent();

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("groupList", groupList);

        return getSuccessResult(resultMap);
    }
}

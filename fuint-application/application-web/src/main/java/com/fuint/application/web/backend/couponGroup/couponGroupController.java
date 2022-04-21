package com.fuint.application.web.backend.couponGroup;

import com.fuint.exception.BusinessCheckException;
import com.fuint.util.DateUtil;
import com.fuint.excel.export.dto.ExcelExportDto;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.application.dao.entities.*;
import com.fuint.application.dao.repositories.MtUserCouponRepository;
import com.fuint.application.dto.GroupDataListDto;
import com.fuint.application.dto.GroupDataDto;
import com.fuint.application.dto.ReqResult;
import com.fuint.application.dto.ReqCouponGroupDto;
import com.fuint.application.service.coupongroup.CouponGroupService;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.sendlog.SendLogService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fuint.application.web.backend.util.UploadResult;
import com.fuint.application.web.backend.util.JSONUtil;
import com.fuint.application.util.XlsUtil;
import com.fuint.excel.export.service.ExportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.util.RequestHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.*;

/**
 * 卡券分组管理类controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Controller
@RequestMapping(value = "/backend/couponGroup")
public class couponGroupController {

    private static final Logger logger = LoggerFactory.getLogger(couponGroupController.class);

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

    @Autowired
    private SendLogService sendLogService;

    @Autowired
    private MtUserCouponRepository userCouponRepository;

    /**
     * 上次执行搜索全量索引的时间
     */
    private Date lastIndexTime = null;

    /**
     * 卡券分组列表查询
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 列表展现页面
     */
    @RequiresPermissions("backend/couponGroup/index")
    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String EQ_code = request.getParameter("EQ_code");
        model.addAttribute("EQ_code", EQ_code);

        if (lastIndexTime != null) {
            long diff = DateUtil.getDiffSeconds(new Date(), lastIndexTime);
            model.addAttribute("isDisable", diff < 30 ? "disable" : "");
        }

        return "couponGroup/index";
    }

    /**
     * 查询卡券分组列表
     *
     * @param request
     * @param response
     * @param model
     * @return
     * @throws BusinessCheckException
     */
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    @RequiresPermissions("/backend/couponGroup/queryList")
    public String queryList(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        PaginationResponse<MtCouponGroup> paginationResponse = couponGroupService.queryCouponGroupListByPagination(paginationRequest);

        // 计算券种类，总价值
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
            for (int i=0; i < paginationResponse.getContent().size(); i++) {
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

        model.addAttribute("groupData", groupData);
        model.addAttribute("paginationResponse", paginationResponse);

        return "couponGroup/list";
    }

    /**
     * 查询卡券分组列表
     *
     * @param request
     * @param response
     * @param model
     * @return
     * @throws BusinessCheckException
     */
    @RequestMapping(value = "/getGroupData", method = RequestMethod.POST)
    @RequiresPermissions("/backend/couponGroup/getGroupData")
    @ResponseBody
    public String getGroupData(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {

        String groupId = request.getParameter("groupId");
        GroupDataDto data = couponGroupService.getGroupData(Integer.parseInt(groupId), request, model);

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("cancelNum", data.getCancelNum().toString());
        resultMap.put("expireNum", data.getExpireNum().toString());
        resultMap.put("sendNum", data.getSendNum().toString());
        resultMap.put("unSendNum", data.getUnSendNum().toString());
        resultMap.put("useNum", data.getUseNum().toString());

        return JSONUtil.toJSonString(resultMap);
    }

    /**
     * 删除卡券分组
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/couponGroup/delete")
    @RequestMapping(value = "/delete/{id}")
    @ResponseBody
    public ReqResult delete(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Integer id) throws BusinessCheckException {
        List<Integer> ids = new ArrayList<>();
        ids.add(id);

        // 分组已发放，不允许删除
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        paginationRequest.setCurrentPage(1);
        paginationRequest.setPageSize(1);
        paginationRequest.getSearchParams().put("EQ_groupId", id.toString());
        PaginationResponse<MtUserCoupon> paginationResponse = userCouponRepository.findResultsByPagination(paginationRequest);
        if (paginationResponse.getContent().size() > 0) {
            ReqResult reqResult = new ReqResult();
            reqResult.setResult(false);
            reqResult.setMsg("已发放卡券，不允许删除！");
            return reqResult;
        }

        String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
        couponGroupService.deleteCouponGroup(id, operator);

        ReqResult reqResult = new ReqResult();
        reqResult.setResult(true);
        return reqResult;
    }

    /**
     * 添加卡券分组初始化页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/couponGroup/add")
    @RequestMapping(value = "/add")
    public String add(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        return "couponGroup/add";
    }

    /**
     * 新增卡券分组页面
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     */
    @RequiresPermissions("backend/couponGroup/create")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createHandler(HttpServletRequest request, HttpServletResponse response, Model model, ReqCouponGroupDto reqCouponGroupDto) throws BusinessCheckException {
        PaginationRequest requestName = RequestHandler.buildPaginationRequest(request, model);
        requestName.getSearchParams().put("EQ_name", reqCouponGroupDto.getName());
        PaginationResponse<MtCouponGroup> dataName = couponGroupService.queryCouponGroupListByPagination(requestName);
        if (dataName.getContent().size() > 0) {
            throw new BusinessCheckException("该分组名称已存在，请修改后提交");
        }

        String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
        reqCouponGroupDto.setOperator(operator);
        couponGroupService.addCouponGroup(reqCouponGroupDto);

        return "redirect:/backend/couponGroup/index";
    }

    /**
     * 编辑卡券分组初始化页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/couponGroupEditInit")
    @RequestMapping(value = "/couponGroupEditInit/{id}")
    public String couponGroupEditInit(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Integer id) throws BusinessCheckException {
        MtCouponGroup mtCouponGroup = couponGroupService.queryCouponGroupById(id);
        model.addAttribute("couponGroup", mtCouponGroup);
        return "couponGroup/edit";
    }

    /**
     * 编辑卡券分组
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/couponGroup/update")
    @RequestMapping(value = "/update")
    public String couponGroupUpdate(HttpServletRequest request, HttpServletResponse response, Model model, ReqCouponGroupDto reqCouponGroupDto) throws BusinessCheckException {
        String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
        reqCouponGroupDto.setOperator(operator);
        couponGroupService.updateCouponGroup(reqCouponGroupDto);

        return "redirect:/backend/couponGroup/index";
    }

    /**
     * 导出模板文件
     *
     * @param request
     * @param response
     * @param model
     * @throws BusinessCheckException
     */
    @RequestMapping(value = "/exportTemplate", method = RequestMethod.GET)
    @RequiresPermissions("backend/couponGroup/exportTemplate")
    public void exportTemplate(HttpServletRequest request, HttpServletResponse response,
                               Model model) throws BusinessCheckException {
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
     * @throws IOException
     */
    @RequiresPermissions("backend/couponGroup/upload")
    @RequestMapping(value = "/upload/", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String uploadFile(HttpServletRequest request, @RequestParam("fileInput") MultipartFile file) {
        UploadResult uploadResult = new UploadResult();

        try {
            String filePath = couponGroupService.saveExcelFile(file, request);

            String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
            String uuid = couponGroupService.importSendCoupon(file, operator, filePath);

            uploadResult.setStatus("true");
            uploadResult.setBatchCode(uuid);
            uploadResult.setMessage("导入成功");
        } catch (BusinessCheckException e) {
            uploadResult.setStatus("false");
            uploadResult.setMessage(e.getMessage());
            logger.error("importCouponController->uploadFile：{}", e);
        } catch (Exception e) {
            uploadResult.setStatus("false");
            uploadResult.setMessage("导入失败");
            logger.error("importCouponController->uploadFile：{}", e);
        }

        return JSONUtil.toJSonString(uploadResult);
    }

    /**
     * 废除用户卡券
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/couponGroup/removeUserCoupon")
    @RequestMapping(value = "/removeUserCoupon/{id}")
    @ResponseBody
    public ReqResult removeUserCoupon(HttpServletRequest request, HttpServletResponse response, Model model,  @PathVariable("id") Long id) throws BusinessCheckException {
        ReqResult reqResult = new ReqResult();

        if (id == null) {
            reqResult.setResult(false);
            reqResult.setMsg("系统参数有误！");
            return reqResult;
        }

        MtSendLog sendLog = sendLogService.querySendLogById(id);
        if (null == sendLog) {
            reqResult.setResult(false);
            reqResult.setMsg("系统参数有误！");
            return reqResult;
        }

        try {
            String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
            couponService.removeUserCoupon(id, sendLog.getUuid(), operator);
        } catch (BusinessCheckException e) {
            reqResult.setResult(false);
            reqResult.setMsg(e.getMessage());
            return reqResult;
        }

        reqResult.setResult(true);
        return reqResult;
    }

    /**
     * 快速查询分组
     * */
    @RequiresPermissions("backend/couponGroup/quickSearchInit")
    @RequestMapping(value = "/quickSearchInit")
    public String quickSearchInit(HttpServletRequest request, Model model) throws BusinessCheckException {
        return "components/groupQuickSearch";
    }

    /**
     * 快速查询分组
     * */
    @RequiresPermissions("backend/couponGroup/quickSearch")
    @RequestMapping(value = "/quickSearch")
    public String quickSearch(HttpServletRequest request, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        PaginationResponse<MtCouponGroup> paginationResponse = couponGroupService.queryCouponGroupListByPagination(paginationRequest);

        List<MtCouponGroup> groupList = paginationResponse.getContent();
        model.addAttribute("groupList", groupList);

        return "components/groupList";
    }
}

package com.fuint.application.web.backend.couponGroup;

import com.fuint.exception.BusinessCheckException;
import com.fuint.util.DateUtil;
import com.fuint.excel.export.dto.ExcelExportDto;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.application.dao.entities.*;
import com.fuint.application.dao.repositories.MtUserCouponRepository;
import com.fuint.application.dto.*;
import com.fuint.application.service.coupongroup.CouponGroupService;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.sendlog.SendLogService;
import com.fuint.application.service.sms.SendSmsInterface;
import com.fuint.util.StringUtil;
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
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 优惠分组管理类controller
 * Created by zach on 2019/07/18.
 */
@Controller
@RequestMapping(value = "/backend/couponGroup")
public class couponGroupController {

    private static final Logger logger = LoggerFactory.getLogger(couponGroupController.class);

    /**
     * 优惠分组服务接口
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
    private MemberService memberService;

    /**
     * 短信发送接口
     */
    @Autowired
    private SendSmsInterface sendSmsService;

    @Autowired
    private MtUserCouponRepository userCouponRepository;

    /**
     * 上次执行搜索全量索引的时间
     */
    private Date lastIndexTime = null;

    /**
     * 优惠分组列表查询
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
     * 查询优惠分组列表
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
                object.setMoney(couponGroupService.getCouponMoney(object.getId().longValue()));
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
     * 查询优惠分组列表
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
     * 删除优惠分组
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/couponGroup/delete")
    @RequestMapping(value = "/delete/{id}")
    @ResponseBody
    public ReqResult delete(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Long id) throws BusinessCheckException {
        List<Long> ids = new ArrayList<Long>();
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
     * 添加优惠分组初始化页面
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
     * 新增优惠分组页面
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     */
    @RequiresPermissions("backend/couponGroup/create")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createHandler(HttpServletRequest request, HttpServletResponse response, Model model, ReqCouponGroupDto reqCouponGroupDto) throws BusinessCheckException {
        if (reqCouponGroupDto.getTotal() < 1) {
            throw new BusinessCheckException("发行量不能小于1，请修改");
        }

        String total = reqCouponGroupDto.getTotal().toString();
        Pattern pattern = Pattern.compile("[0-9]*");
        if (total == null || (!pattern.matcher(total).matches())) {
            throw new BusinessCheckException("发行量须是正整数，请修改");
        }

        PaginationRequest requestName = RequestHandler.buildPaginationRequest(request, model);
        requestName.getSearchParams().put("EQ_name", reqCouponGroupDto.getName());
        PaginationResponse<MtCouponGroup> dataName = couponGroupService.queryCouponGroupListByPagination(requestName);
        if (dataName.getContent().size() > 0) {
            throw new BusinessCheckException("券名称已存在，请修改");
        }

        if (reqCouponGroupDto.getTotal() > 50000) {
            throw new BusinessCheckException("一次生成不能超过50000套");
        }

        String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
        reqCouponGroupDto.setOperator(operator);
        couponGroupService.addCouponGroup(reqCouponGroupDto);

        return "redirect:/backend/couponGroup/index";
    }

    /**
     * 编辑优惠分组初始化页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/couponGroupEditInit")
    @RequestMapping(value = "/couponGroupEditInit/{id}")
    public String couponGroupEditInit(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Long id) throws BusinessCheckException {
        MtCouponGroup mtCouponGroup = couponGroupService.queryCouponGroupById(id);
        model.addAttribute("couponGroup", mtCouponGroup);
        return "couponGroup/edit";
    }

    /**
     * 编辑优惠分组初始化页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/couponGroup/update")
    @RequestMapping(value = "/update")
    public String couponGroupUpdate(HttpServletRequest request, HttpServletResponse response, Model model, ReqCouponGroupDto reqCouponGroupDto) throws BusinessCheckException {
        String total = reqCouponGroupDto.getTotal().toString();
        Pattern pattern = Pattern.compile("[0-9]*");
        if (total == null || (!pattern.matcher(total).matches()) || reqCouponGroupDto.getTotal() < 1) {
            throw new BusinessCheckException("发行量须是正整数，请修改");
        }

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
     * 发放卡券
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/couponGroup/sendCoupon")
    @RequestMapping(value = "/sendCoupon")
    @ResponseBody
    public ReqResult sendCoupon(HttpServletRequest request, HttpServletResponse response, Model model, ReqSendCouponDto reqSendCouponDto) throws BusinessCheckException {

        String mobile = request.getParameter("mobile");
        String num = request.getParameter("num");
        String groupId = request.getParameter("groupId");

        ReqResult reqResult = new ReqResult();

        if (groupId == null) {
            reqResult.setResult(false);
            reqResult.setMsg("系统参数有误！");
            return reqResult;
        }

        try {
            String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
            if (null == operator) {
                reqResult.setResult(false);
                reqResult.setMsg("请重新登录！");
                return reqResult;
            }
        } catch (Exception e) {
            reqResult.setResult(false);
            reqResult.setMsg("请重新登录！");
            return reqResult;
        }

        if (mobile.length() < 11 || mobile.length() > 11) {
            reqResult.setResult(false);
            reqResult.setMsg("手机号格式有误！");
            return reqResult;
        }

        Pattern pattern = Pattern.compile("[0-9]*");
        if (num == null || (!pattern.matcher(num).matches())) {
            reqResult.setResult(false);
            reqResult.setMsg("发放套数必须为正整数！");
            return reqResult;
        }

        // 导入批次
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");

        try {
            couponService.sendCoupon(Long.parseLong(groupId), mobile, Integer.parseInt(num), uuid);
        } catch (BusinessCheckException e) {
            reqResult.setResult(false);
            reqResult.setMsg(e.getMessage());
            return reqResult;
        }

        MtUser mtUser = memberService.queryMemberByMobile(mobile);
        MtCouponGroup mtCouponGroup = couponGroupService.queryCouponGroupById(Long.parseLong(groupId));

        // 发放记录
        ReqSendLogDto dto = new ReqSendLogDto();
        dto.setType(1);
        dto.setMobile(mobile);
        dto.setUserId(mtUser.getId());
        dto.setFileName("");
        dto.setGroupId(Integer.parseInt(groupId));
        dto.setGroupName(mtCouponGroup.getName());
        dto.setSendNum(Integer.parseInt(num));

        String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
        dto.setOperator(operator);

        dto.setUuid(uuid);
        sendLogService.addSendLog(dto);

        // 发送短信
        try {
            List<String> mobileList = new ArrayList<>();
            mobileList.add(mobile);

            Integer totalNum = 0;
            BigDecimal totalMoney = new BigDecimal("0.0");

            List<MtCoupon> couponList = couponService.queryCouponListByGroupId(Long.parseLong(groupId));
            for (MtCoupon coupon : couponList) {
                totalNum = totalNum + (coupon.getSendNum()*Integer.parseInt(num));
                totalMoney = totalMoney.add((coupon.getAmount().multiply(new BigDecimal(num).multiply(new BigDecimal(coupon.getSendNum())))));
            }

            Map<String, String> params = new HashMap<>();
            params.put("totalNum", totalNum+"");
            params.put("totalMoney", totalMoney+"");
            sendSmsService.sendSms("received-coupon", mobileList, params);
        } catch (Exception e) {
            //empty
        }

        reqResult.setResult(true);
        return reqResult;
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

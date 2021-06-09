package com.fuint.application.web.backend.sendLog;

import com.fuint.exception.BusinessCheckException;
import com.fuint.util.DateUtil;
import com.fuint.application.dao.entities.MtSendLog;
import com.fuint.application.dao.entities.MtUserCoupon;
import com.fuint.application.dao.repositories.MtSendLogRepository;
import com.fuint.application.enums.UserCouponStatusEnum;
import com.fuint.application.service.sendlog.SendLogService;
import com.fuint.application.service.usercoupon.UserCouponService;
import com.fuint.application.web.backend.util.ExcelUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
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
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import static com.fuint.application.util.XlsUtil.objectConvertToString;

/**
 * 发券记录管理类controller
 * Created by zach on 2019/09/16.
 */
@Controller
@RequestMapping(value = "/backend/sendLog")
public class sendLogController {

    private static final Logger logger = LoggerFactory.getLogger(sendLogController.class);

    /**
     * 发送记录服务接口
     */
    @Autowired
    private SendLogService sendLogService;

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private MtSendLogRepository sendLogRepository;

    /**
     * 上次执行搜索全量索引的时间
     */
    private Date lastIndexTime = null;

    /**
     * 发券记录列表查询
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 列表展现页面
     */
    @RequiresPermissions("backend/sendLog/index")
    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String EQ_type = request.getParameter("EQ_type");
        model.addAttribute("EQ_type", EQ_type);
        if (lastIndexTime != null) {
            long diff = DateUtil.getDiffSeconds(new Date(), lastIndexTime);
            model.addAttribute("isDisable", diff < 30 ? "disable" : "");
        }
        return "sendLog/index";
    }

    /**
     * 查询发券记录列表
     *
     * @param request
     * @param response
     * @param model
     * @return
     * @throws BusinessCheckException
     */
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    @RequiresPermissions("/backend/sendLog/queryList")
    public String queryList(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        PaginationResponse<MtSendLog> paginationResponse = sendLogService.querySendLogListByPagination(paginationRequest);

        if (paginationResponse.getContent().size() > 0) {
            for (MtSendLog log : paginationResponse.getContent()) {
                if (log.getStatus().equals("B")) {
                    PaginationRequest requestUserCouponUse = RequestHandler.buildPaginationRequest(request, model);
                    requestUserCouponUse.getSearchParams().put("EQ_uuid", log.getUuid());
                    requestUserCouponUse.getSearchParams().put("EQ_userId", log.getUserId().toString());
                    requestUserCouponUse.getSearchParams().put("EQ_status", UserCouponStatusEnum.DISABLE.getKey());
                    PaginationResponse<MtUserCoupon> dataUserCoupon = userCouponService.queryUserCouponListByPagination(requestUserCouponUse);
                    Long successNum = dataUserCoupon.getTotalElements();

                    if (null != log.getRemoveSuccessNum()) {
                        if (successNum > log.getRemoveSuccessNum()) {
                            log.setRemoveSuccessNum(successNum.intValue());
                            sendLogRepository.save(log);
                        }
                    }
                }
            }
        }

        model.addAttribute("paginationResponse", paginationResponse);

        if (request.getParameter("EQ_type").equals("2")) {
            return "sendLog/list_batch";
        } else {
            return "sendLog/list";
        }
    }

    /**
     * 导出数据
     *
     * @return
     */
    @RequiresPermissions("backend/sendLog/export")
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ResponseBody
    public void export(HttpServletRequest request, HttpServletResponse response,Model model) throws Exception {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        paginationRequest.setPageSize(50000);
        paginationRequest.setCurrentPage(1);

        PaginationResponse<MtSendLog> paginationResponse = sendLogService.querySendLogListByPagination(paginationRequest);

        if (paginationResponse.getContent().size() > 0) {
            for (MtSendLog log : paginationResponse.getContent()) {
                if (log.getStatus().equals("B")) {
                    PaginationRequest requestUserCouponUse = RequestHandler.buildPaginationRequest(request, model);
                    requestUserCouponUse.getSearchParams().put("EQ_uuid", log.getUuid());
                    String b = "";
                    requestUserCouponUse.getSearchParams().put("EQ_status", UserCouponStatusEnum.DISABLE.getKey());
                    PaginationResponse<MtUserCoupon> dataUserCoupon = userCouponService.queryUserCouponListByPagination(requestUserCouponUse);
                    Long successNum = dataUserCoupon.getTotalElements();

                    if (null != log.getRemoveSuccessNum()) {
                        if (successNum > log.getRemoveSuccessNum()) {
                            log.setRemoveSuccessNum(successNum.intValue());
                            sendLogRepository.save(log);
                        }
                    }
                }
            }
        }

        List<MtSendLog> list = paginationResponse.getContent();

        //excel标题
        String tab = request.getParameter("EQ_type");
        String[] title_tab1 = {"记录ID", "操作人", "用户手机", "发放分组ID", "发放分组名称", "发放套数", "操作时间", "状态"};
        String[] title_tab2 = {"记录ID", "操作人", "用户手机", "发放详情", "操作时间", "状态"};
        String[] title;
        String fileName;
        if (tab.equals("1")) {
            title = title_tab1;
            fileName = "单用户发券记录" + System.currentTimeMillis() + ".xls";
        } else {
            title = title_tab2;
            fileName = "批量发券记录" + System.currentTimeMillis() + ".xls";
        }

        String[][] content = null;
        if (list.size() > 0) {
            content= new String[list.size()][title.length];
        }

        if (tab.equals("1")) {
            for (int i = 0; i < list.size(); i++) {
                MtSendLog obj = list.get(i);
                content[i][0] = objectConvertToString(obj.getId());
                content[i][1] = objectConvertToString(obj.getOperator());
                content[i][2] = objectConvertToString(obj.getMobile());
                content[i][3] = objectConvertToString(obj.getGroupId());
                content[i][4] = objectConvertToString(obj.getGroupName());
                content[i][5] = objectConvertToString(obj.getSendNum());
                content[i][6] = objectConvertToString(obj.getCreateTime());
                if (obj.getStatus().equals("D")) {
                    content[i][7] = objectConvertToString("全部作废");
                } else if(obj.getStatus().equals("B")) {
                    content[i][7] = objectConvertToString("部分作废");
                } else {
                    content[i][7] = objectConvertToString("正常");
                }
            }
        } else {
            for (int i = 0; i < list.size(); i++) {
                MtSendLog obj = list.get(i);
                content[i][0] = objectConvertToString(obj.getId());
                content[i][1] = objectConvertToString(obj.getOperator());
                content[i][2] = objectConvertToString(obj.getMobile());
                content[i][3] = objectConvertToString(obj.getFileName());
                content[i][4] = objectConvertToString(obj.getCreateTime());
                if (obj.getStatus().equals("D")) {
                    content[i][5] = objectConvertToString("全部作废");
                } else if(obj.getStatus().equals("B")) {
                    content[i][5] = objectConvertToString("部分作废");
                } else {
                    content[i][5] = objectConvertToString("正常");
                }
            }
        }

        // 创建HSSFWorkbook
        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook("发券记录", title, content, null);

        // 响应到客户端
        try {
            this.setExportResponseHeader(response, fileName);
            OutputStream os = response.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setExportResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(), "ISO8859-1");
            } catch (UnsupportedEncodingException ee) {
                ee.printStackTrace();
            }
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            String fileNameString = "";
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

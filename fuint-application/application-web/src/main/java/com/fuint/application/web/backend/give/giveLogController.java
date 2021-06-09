package com.fuint.application.web.backend.give;

import com.fuint.exception.BusinessCheckException;
import com.fuint.util.DateUtil;
import com.fuint.application.dao.entities.*;
import com.fuint.application.dao.repositories.MtCouponGroupRepository;
import com.fuint.application.dao.repositories.MtCouponRepository;
import com.fuint.application.dao.repositories.MtUserCouponRepository;
import com.fuint.application.dto.GiveDto;
import com.fuint.application.dto.GiveItemDto;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.give.GiveService;
import com.fuint.application.web.backend.util.ExcelUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.util.RequestHandler;
import com.fuint.application.web.backend.base.BaseController;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import static com.fuint.application.util.XlsUtil.objectConvertToString;

/**
 * 转赠管理类controller
 * Created by zach on 2019/10/14.
 */
@Controller
@RequestMapping(value = "/backend/give")
public class giveLogController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(giveLogController.class);

    /**
     * 转赠服务接口
     */
    @Autowired
    private GiveService giveService;

    @Autowired
    private MtUserCouponRepository userCouponRepository;

    @Autowired
    private MtCouponGroupRepository couponGroupRepository;

    @Autowired
    private MtCouponRepository couponRepository;

    /**
     * 上次执行搜索全量索引的时间
     */
    private Date lastIndexTime = null;

    /**
     * 转赠列表查询
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 转赠列表展现页面
     */
    @RequiresPermissions("backend/give/index")
    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {

        if (lastIndexTime != null) {
            long diff = DateUtil.getDiffSeconds(new Date(), lastIndexTime);
            model.addAttribute("isDisable", diff < 30 ? "disable" : "");
        }

        return "give/index";
    }

    /**
     * 查询列表
     *
     * @param request
     * @param response
     * @param model
     * @return
     * @throws BusinessCheckException
     */
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    @RequiresPermissions("/backend/give/queryList")
    public String queryList(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);

        PaginationResponse<GiveDto> paginationResponse = giveService.queryGiveListByPagination(paginationRequest);
        model.addAttribute("paginationResponse", paginationResponse);

        return "give/list";
    }

    /**
     * 转赠详情页面
     * */
    @RequiresPermissions("backend/give/viewItem")
    @RequestMapping(value = "/viewItem")
    public String activityQuickSearchInit(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {

        model.addAttribute("giveId", request.getParameter("giveId"));

        return "give/item";
    }

    /**
     * 快速查询详情
     * */
    @RequiresPermissions("backend/give/viewItem")
    @RequestMapping(value = "/giveItem")
    public String giveItem(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        Map<String, Object> params = paginationRequest.getSearchParams();

        Integer giveId = Integer.parseInt(request.getParameter("giveId"));

        if (null == params) {
            params = new HashMap<>();
        }
        params.put("EQ_status", StatusEnum.ENABLED.getKey());
        params.put("EQ_giveId", giveId.toString());

        List<MtGiveItem> itemList = giveService.queryItemByParams(params);

        List<GiveItemDto> dataList = new ArrayList<>();
        for (MtGiveItem item : itemList) {
            MtGive giveInfo = giveService.queryGiveById(giveId.longValue());
            MtUserCoupon userCouponInfo = userCouponRepository.findOne(item.getUserCouponId());
            MtCouponGroup groupInfo = couponGroupRepository.findOne(userCouponInfo.getGroupId());
            MtCoupon couponInfo = couponRepository.findOne(userCouponInfo.getCouponId());

            GiveItemDto dto = new GiveItemDto();
            dto.setId(item.getId());
            dto.setMobile(giveInfo.getUserMobile());
            dto.setUserMobile(giveInfo.getMobile());
            dto.setGroupId(userCouponInfo.getGroupId());
            dto.setGroupName(groupInfo.getName());
            dto.setCouponId(userCouponInfo.getCouponId());
            dto.setCouponName(couponInfo.getName());
            dto.setMoney(couponInfo.getAmount());
            dto.setCreateTime(item.getCreateTime());

            dataList.add(dto);
        }

        model.addAttribute("itemList", dataList);

        return "give/itemList";
    }

    /**
     * 导出数据
     *
     * @return
     */
    @RequiresPermissions("backend/give/export")
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ResponseBody
    public void export(HttpServletRequest request, HttpServletResponse response,Model model) throws Exception {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        paginationRequest.setPageSize(50000);
        paginationRequest.setCurrentPage(1);

        PaginationResponse<GiveDto> paginationResponse = giveService.queryGiveListByPagination(paginationRequest);
        List<GiveDto> list = paginationResponse.getContent();

        //excel标题
        String[] title = {"记录ID", "用户手机号", "FuInt账号", "转赠数量", "转赠总金额", "赠予对象手机号", "赠予对象FuInt账号", "赠予时间"};
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
            content[i][2] = objectConvertToString(obj.getUserHnaAccount());
            content[i][3] = objectConvertToString(obj.getNum());
            content[i][4] = objectConvertToString(obj.getMoney());
            content[i][5] = objectConvertToString(obj.getMobile());
            content[i][6] = objectConvertToString(obj.getHnaAccount());
            content[i][7] = objectConvertToString(obj.getCreateTime());
        }

        // 创建HSSFWorkbook
        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook("转赠记录", title, content, null);

        // 响应到客户端
        try {
            this.setExportResponseHeaders(response, fileName);
            OutputStream os = response.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setExportResponseHeaders(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(), "ISO8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");

            String file = "";

            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

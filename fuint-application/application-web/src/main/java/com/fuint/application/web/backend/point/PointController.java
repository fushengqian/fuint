package com.fuint.application.web.backend.point;

import com.fuint.application.dao.entities.MtPoint;
import com.fuint.application.dao.entities.MtSetting;
import com.fuint.application.dto.ReqResult;
import com.fuint.application.enums.PointSettingEnum;
import com.fuint.application.enums.SettingTypeEnum;
import com.fuint.application.service.point.PointService;
import com.fuint.application.service.setting.SettingService;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 积分管理controller
 * Created by zach on 2021/05/16
 */
@Controller
@RequestMapping(value = "/backend/point")
public class PointController {

    private static final Logger logger = LoggerFactory.getLogger(PointController.class);

    /**
     * 配置服务接口
     * */
    @Autowired
    private SettingService settingService;

    /**
     * 积分服务接口
     * */
    @Autowired
    private PointService pointService;

    /**
     * 积分明细列表查询
     *
     * @param request  HttpServletRequest对象
     * @param model    SpringFramework Model对象
     * @return 列表展现页面
     */
    @RequiresPermissions("backend/point/index")
    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);

        PaginationResponse<MtPoint> paginationResponse = pointService.queryPointListByPagination(paginationRequest);

        model.addAttribute("paginationResponse", paginationResponse);

        return "point/list";
    }

    /**
     * 编辑初始化页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/point/setting")
    @RequestMapping(value = "/setting")
    public String editInit(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        List<MtSetting> settingList = settingService.getSettingList(SettingTypeEnum.POINT.getKey());

        for (MtSetting setting : settingList) {
            if (setting.getName().equals("pointNeedConsume")) {
                model.addAttribute("pointNeedConsume", setting.getValue());
            } else if (setting.getName().equals("canUsedAsMoney")) {
                model.addAttribute("canUsedAsMoney", setting.getValue());
            }else if (setting.getName().equals("exchangeNeedPoint")) {
                model.addAttribute("exchangeNeedPoint", setting.getValue());
            } else if (setting.getName().equals("rechargePointSpeed")) {
                model.addAttribute("rechargePointSpeed", setting.getValue());
            }
            model.addAttribute("status", setting.getStatus());
        }

        return "point/setting";
    }

    /**
     * 提交保存
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     */
    @RequiresPermissions("backend/point/save")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public ReqResult saveHandler(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String status = request.getParameter("status");

        String pointNeedConsume = request.getParameter("pointNeedConsume") != null ? request.getParameter("pointNeedConsume") : "1";
        String canUsedAsMoney = request.getParameter("canUsedAsMoney") != null ? request.getParameter("canUsedAsMoney") : "true";
        String exchangeNeedPoint = request.getParameter("exchangeNeedPoint") != null ? request.getParameter("exchangeNeedPoint") : "0";
        String rechargePointSpeed = request.getParameter("rechargePointSpeed") != null ? request.getParameter("rechargePointSpeed") : "1";

        String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();

        PointSettingEnum[] settingList = PointSettingEnum.values();
        for (PointSettingEnum setting : settingList) {
            MtSetting info = new MtSetting();
            info.setType(SettingTypeEnum.POINT.getKey());
            info.setName(setting.getKey());

            if (setting.getKey().equals("pointNeedConsume")) {
                info.setValue(pointNeedConsume);
            } else if (setting.getKey().equals("canUsedAsMoney")) {
                info.setValue(canUsedAsMoney);
            } else if (setting.getKey().equals("exchangeNeedPoint")) {
                info.setValue(exchangeNeedPoint);
            } else if (setting.getKey().equals("rechargePointSpeed")) {
                info.setValue(rechargePointSpeed);
            }

            info.setDescription(setting.getValue());
            info.setStatus(status);
            info.setOperator(operator);
            info.setUpdateTime(new Date());

            settingService.saveSetting(info);
        }

        ReqResult reqResult = new ReqResult();
        reqResult.setResult(true);

        return reqResult;
    }
}

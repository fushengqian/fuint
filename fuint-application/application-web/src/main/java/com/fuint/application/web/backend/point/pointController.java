package com.fuint.application.web.backend.point;

import com.fuint.application.dao.entities.MtPoint;
import com.fuint.application.dao.entities.MtSetting;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.dto.PointDto;
import com.fuint.application.dto.ReqResult;
import com.fuint.application.enums.PointSettingEnum;
import com.fuint.application.enums.SettingTypeEnum;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.point.PointService;
import com.fuint.application.service.setting.SettingService;
import com.fuint.application.util.CommonUtil;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.shiro.ShiroUser;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
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
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Controller
@RequestMapping(value = "/backend/point")
public class pointController {

    private static final Logger logger = LoggerFactory.getLogger(pointController.class);

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
     * 会员服务接口
     * */
    @Autowired
    private MemberService memberService;

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
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        if (shiroUser == null) {
            return "redirect:/login";
        }

        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);

        String mobile = request.getParameter("mobile") == null ? "" : request.getParameter("mobile");
        if (StringUtils.isNotEmpty(mobile)) {
            MtUser userInfo = memberService.queryMemberByMobile(mobile);
            if (userInfo != null) {
                Map<String, Object> searchParams = new HashedMap();
                searchParams.put("EQ_userId", userInfo.getId()+"");
                paginationRequest.setSearchParams(searchParams);
            }
        }

        PaginationResponse<PointDto> paginationResponse = pointService.queryPointListByPagination(paginationRequest);

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
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        if (shiroUser == null) {
            return "redirect:/login";
        }

        List<MtSetting> settingList = settingService.getSettingList(SettingTypeEnum.POINT.getKey());

        for (MtSetting setting : settingList) {
            if (setting.getName().equals("pointNeedConsume")) {
                model.addAttribute("pointNeedConsume", setting.getValue());
            } else if (setting.getName().equals("canUsedAsMoney")) {
                model.addAttribute("canUsedAsMoney", setting.getValue());
            } else if (setting.getName().equals("exchangeNeedPoint")) {
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

        ReqResult reqResult = new ReqResult();
        if (!CommonUtil.isNumeric(pointNeedConsume) || !CommonUtil.isNumeric(exchangeNeedPoint) || !CommonUtil.isNumeric(rechargePointSpeed)) {
            reqResult.setResult(false);
            reqResult.setMsg("输入参数有误！");
            return reqResult;
        }

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

        reqResult.setResult(true);
        return reqResult;
    }

    /**
     * 充值页面
     *
     * @param request  HttpServletRequest对象
     * @param model    SpringFramework Model对象
     * @return 充值页面
     */
    @RequiresPermissions("backend/point/recharge")
    @RequestMapping(value = "/recharge")
    public String recharge(HttpServletRequest request, Model model) throws BusinessCheckException {
        Integer userId = request.getParameter("userId") == null ? 0 : Integer.parseInt(request.getParameter("userId"));

        MtUser userInfo = memberService.queryMemberById(userId);

        model.addAttribute("userInfo", userInfo);

        return "point/recharge";
    }

    /**
     * 提交充值
     *
     * @param request  HttpServletRequest对象
     */
    @RequiresPermissions("backend/point/doRecharge")
    @RequestMapping(value = "/doRecharge", method = RequestMethod.POST)
    @ResponseBody
    public ReqResult doRecharge(HttpServletRequest request) throws BusinessCheckException {
        String amount = request.getParameter("amount") == null ? "0" : request.getParameter("amount");
        String remark = request.getParameter("remark") == null ? "后台充值" : request.getParameter("remark");
        Integer userId = request.getParameter("userId") == null ? 0 : Integer.parseInt(request.getParameter("userId"));
        Integer type = request.getParameter("type") == null ? 1 : Integer.parseInt(request.getParameter("type"));
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();

        ReqResult reqResult = new ReqResult();

        if (!CommonUtil.isNumeric(amount)) {
            reqResult.setCode("201");
            reqResult.setResult(false);
            reqResult.setMsg("充值积分必须是数字！");
        }

        if (shiroUser == null) {
            reqResult.setCode("201");
            reqResult.setResult(false);
            reqResult.setMsg("请重新登录！");
        }

        if (userId < 1) {
            reqResult.setCode("201");
            reqResult.setResult(false);
            reqResult.setMsg("充值会员信息不能为空！");
        }

        String operator = shiroUser.getAcctName();

        MtPoint mtPoint = new MtPoint();
        if (type == 2) {
            // 扣减
            mtPoint.setAmount(Integer.parseInt(amount) - (Integer.parseInt(amount)) * 2);
        } else {
            mtPoint.setAmount(Integer.parseInt(amount));
        }
        mtPoint.setDescription(remark);
        mtPoint.setUserId(userId);
        mtPoint.setOperator(operator);
        mtPoint.setOrderSn("");

        pointService.addPoint(mtPoint);
        MtUser userInfo = memberService.queryMemberById(userId);

        reqResult.setResult(true);
        Map<String, Object> data = new HashMap();
        data.put("userInfo", userInfo);
        reqResult.setData(data);

        return reqResult;
    }
}

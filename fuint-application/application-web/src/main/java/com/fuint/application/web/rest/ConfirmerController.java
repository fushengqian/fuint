package com.fuint.application.web.rest;

import com.fuint.exception.BusinessCheckException;
import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;
import com.fuint.application.dao.entities.MtStore;
import com.fuint.application.dao.entities.MtStaff;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.store.StoreService;
import com.fuint.application.service.staff.StaffService;
import com.fuint.application.util.CommonUtil;
import com.fuint.application.util.PhoneFormatCheckUtils;
import com.fuint.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 核销人员controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/rest/confirmer")
public class ConfirmerController extends BaseController{

    @Autowired
    private StaffService staffService;

    @Autowired
    private StoreService storeService;

    /**
     * 员工信息录入
     */
    @RequestMapping(value = "/doAdd", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doAdd(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException{
        String mobile = CommonUtil.replaceXSS(request.getParameter("mobile"));
        String storeID = request.getParameter("storeID");

        response.setCharacterEncoding("UTF-8");

        String realName;
        try {
            realName = URLDecoder.decode(request.getParameter("realName"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            try {
                realName = new String(request.getParameter("realName").getBytes("ISO-8859-1"), "utf-8").toString();
            } catch (UnsupportedEncodingException ee) {
                return getFailureResult(1002,"抱歉，系统编码出错了");
            }
        }

        realName = CommonUtil.replaceXSS(realName);

        if (StringUtil.isEmpty(mobile)) {
            return getFailureResult(1002,"手机号码不能为空");
        } else if(!PhoneFormatCheckUtils.isChinaPhoneLegal(mobile)) {
            return getFailureResult(1002, "手机号码格式不正确");
        }

        MtStaff mtStaff = new MtStaff();
        mtStaff.setMobile(mobile);
        mtStaff.setRealName(realName);
        try {
            mtStaff.setStoreId(Integer.parseInt(storeID));
            MtStore mtStore=storeService.queryStoreById(Integer.parseInt(storeID));
            if (mtStore == null) {
                return getFailureResult(1002, "店铺ID不正确");
            }
        } catch (Exception e) {
            return getFailureResult(1002, "店铺ID不正确");
        }

        MtStaff mtStaff2 = staffService.queryStaffByMobile(mobile);
        if (mtStaff2 != null) {
            if (mtStaff2.getAuditedStatus().equals(StatusEnum.ENABLED.getKey()) || mtStaff2.getAuditedStatus().equals(StatusEnum.FORBIDDEN.getKey())) {
                return getFailureResult(1002, "手机号不能重复提交！");
            } else {
                mtStaff2.setAuditedStatus(StatusEnum.UnAudited.getKey());
                mtStaff2.setUserId(null);
                mtStaff2.setStoreId(Integer.parseInt(storeID));
                mtStaff2.setRealName(realName);
                mtStaff = staffService.saveStaff(mtStaff2);
            }
        } else {
            mtStaff = staffService.saveStaff(mtStaff);
        }

        return getSuccessResult(mtStaff);
    }

    /**
     * 获取店铺列表
     */
    @RequestMapping(value = "/getStoreList", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject getStoreList(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        Map<String, Object> params = new HashMap<>();
        params.put("EQ_status", StatusEnum.ENABLED.getKey());

        List<MtStore> storeList = storeService.queryStoresByParams(params);

        return getSuccessResult(storeList);
    }
}

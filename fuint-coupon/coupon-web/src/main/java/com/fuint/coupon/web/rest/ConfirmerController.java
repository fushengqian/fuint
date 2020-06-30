package com.fuint.coupon.web.rest;

import com.fuint.exception.BusinessCheckException;
import com.fuint.coupon.BaseController;
import com.fuint.coupon.ResponseObject;
import com.fuint.coupon.dao.entities.MtStore;
import com.fuint.coupon.dao.entities.MtConfirmer;
import com.fuint.coupon.enums.StatusEnum;
import com.fuint.coupon.service.store.StoreService;
import com.fuint.coupon.service.confirmer.ConfirmerService;
import com.fuint.coupon.util.CommonUtil;
import com.fuint.coupon.util.PhoneFormatCheckUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
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
 * 登录类controller
 * Created by zach on 2019/7/16.
 */
@RestController
@RequestMapping(value = "/rest/confirmer")
public class ConfirmerController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(ConfirmerController.class);

    @Autowired
    private ConfirmerService confirmerService;

    @Autowired
    private StoreService storeService;


    /**
     * 核销人员信息录入
     */
    @RequestMapping(value = "/doAdd", method = RequestMethod.POST)
    public ResponseObject doAddConfirmer(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException{
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

        if (StringUtils.isEmpty(mobile)) {
            return getFailureResult(1002,"手机号码不能为空");
        } else if(!PhoneFormatCheckUtils.isChinaPhoneLegal(mobile)) {
            return getFailureResult(1002, "手机号码格式不正确");
        }

        MtConfirmer mtConfirmer = new MtConfirmer();
        mtConfirmer.setMobile(mobile);
        mtConfirmer.setRealName(realName);
        try {
            mtConfirmer.setStoreId(Integer.parseInt(storeID));
            MtStore mtStore=storeService.queryStoreById(Integer.parseInt(storeID));
            if(mtStore==null) {
                return getFailureResult(1002, "店铺ID不正确");
            }

        }catch (Exception e) {
            return getFailureResult(1002, "店铺ID不正确");
        }

        MtConfirmer mtConfirmer2=confirmerService.queryConfirmerByMobile(mobile);
        if(mtConfirmer2 != null) {
            if(mtConfirmer2.getAuditedStatus().equals(StatusEnum.ENABLED.getKey())||mtConfirmer2.getAuditedStatus().equals(StatusEnum.FORBIDDEN.getKey())) {
                return getFailureResult(1002, "手机号不能重复提交！");
            }else {
                mtConfirmer2.setAuditedStatus(StatusEnum.UnAudited.getKey());
                mtConfirmer2.setUserId(null);
                mtConfirmer2.setStoreId(Integer.parseInt(storeID));
                mtConfirmer2.setRealName(realName);
                mtConfirmer = confirmerService.addConfirmer(mtConfirmer2);
            }
        } else {
            mtConfirmer = confirmerService.addConfirmer(mtConfirmer);
        }

        return getSuccessResult(mtConfirmer);
    }

    /**
     * 获取店铺列表
     */
    @RequestMapping(value = "/getStoreList", method = RequestMethod.POST)
    public ResponseObject getStoreList(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException{
        String usertoken = request.getHeader("token");
        Map<String, Object> params = new HashMap<>();
        params.put("EQ_status", StatusEnum.ENABLED.getKey());
        List<MtStore> storeList = storeService.queryStoresByParams(params);
        return getSuccessResult(storeList);
    }
}

package com.fuint.application.web.backend.store;

import com.fuint.base.dao.entities.TAccount;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.service.account.TAccountService;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import com.fuint.exception.BusinessRuntimeException;
import com.fuint.util.StringUtil;
import com.fuint.application.dao.entities.*;
import com.fuint.application.dto.MtStoreDto;
import com.fuint.application.dto.ReqResult;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.store.StoreService;
import com.fuint.application.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 店铺信息管理类controller
 * Created by zach on 2019-07-19
 */
@Controller
@RequestMapping(value = "/backend/store")
public class StoreController {

    private static final Logger logger = LoggerFactory.getLogger(StoreController.class);

    /**
     * 店铺信息管理服务接口
     */
    @Autowired
    private StoreService storeService;

    /**
     * 后台员工账户服务接口
     */
    @Autowired
    private TAccountService tAccountService;


    /**
     * 店铺信息列表查询
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 店铺信息列表展现页面
     */
    @RequiresPermissions("backend/store/queryList")
    @RequestMapping(value = "/queryList")
    public String queryList(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        String storeId = request.getParameter("EQ_id");
        String store_name = request.getParameter("LIKE_name");
        String storeStatus = request.getParameter("EQ_status");
        Map<String, Object> params = paginationRequest.getSearchParams();
        if (params == null) {
            params = new HashMap<String, Object>();

            if (StringUtils.isNotEmpty(storeId)) {
                params.put("EQ_id", storeId);
            }
            if (StringUtils.isNotEmpty(store_name)) {
                params.put("LIKE_name", store_name);
            }

            if (StringUtils.isNotEmpty(storeStatus)) {
                params.put("EQ_status", storeStatus);
            }
        }

       // params.put("EQ_status", StatusEnum.ENABLED.getKey());
        paginationRequest.setSearchParams(params);
        PaginationResponse<MtStore> paginationResponse = storeService.queryStoreListByPagination(paginationRequest);

        model.addAttribute("paginationResponse", paginationResponse);
        model.addAttribute("params", params);
        return "store/store_list";
    }

    /**
     * 查询店铺页面
     * */
    @RequiresPermissions("backend/store/simple_searchStore")
    @RequestMapping(value = "/simple_searchStore")
    public String activityQuickSearchInit(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String name = request.getParameter("name");
        model.addAttribute("name", name);
        return "components/simple_storeQuickSearch";
    }

    /**
     * 快速查询店铺
     * */
    @RequiresPermissions("backend/store/simple-quick-search-store")
    @RequestMapping(value = "/simple-quick-search-store")
    public String activityQuickSearchList(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        Map<String, Object> params = paginationRequest.getSearchParams();
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("EQ_status", StatusEnum.ENABLED.getKey());
        //限制选择店铺，只能选择自己有权限的店铺 20191024
        //登录员工所属店铺处理 20191024
        Long accID = ShiroUserHelper.getCurrentShiroUser().getId();
        TAccount tAccount=tAccountService.findAccountById(accID);
        if(tAccount.getStoreId()==null||tAccount.getStoreId().equals(-1))
        {   //处理历史异常数据：没有选择店铺的情况
            if(tAccount.getStoreId()==null){
                params.put("EQ_id","0");
            }
        } else {
            params.put("EQ_id",tAccount.getStoreId().toString());  //查询消费列表
        }

        List<MtStore> storeList = storeService.queryStoresByParams(params);
        model.addAttribute("storeList", storeList);

        return "components/simple_storeList";
    }

    /**
     * 激活店铺
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/store/active/{id}")
    @RequestMapping(value = "/active/{id}")
    public String activeStore(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Integer id) throws BusinessCheckException {
        List<Integer> ids = new ArrayList<Integer>();
        ids.add(id);
        Integer i = storeService.updateStatus(ids, StatusEnum.ENABLED.getKey());
        ReqResult reqResult = new ReqResult();

        reqResult.setResult(true);
        //刷新缓存
        return "redirect:/backend/store/queryList";
    }
    /**
     * 删除店铺
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/store/delete/{id}")
    @RequestMapping(value = "/delete/{id}")
    public String delete(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Integer id) throws BusinessCheckException {
        /*List<Integer> ids = new ArrayList<Integer>();
        ids.add(id);
        Integer i = storeService.updateStatus(ids, StatusEnum.DISABLE.getKey());
        */
        String operator;
        try {
            operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
        }catch (Exception e)
        {
            operator="sysadmin";
        }
        storeService.deleteStore(id,operator);
        ReqResult reqResult = new ReqResult();

        reqResult.setResult(true);
        //return reqResult;
        //刷新缓存
        return "redirect:/backend/store/queryList";
    }

    /**
     * 批量删除店铺
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/store/batchDelete")
    @RequestMapping(value = "/batchDelete")
    @ResponseBody
    public ReqResult batchDelete(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String paramIds = request.getParameter("ids");
        if (StringUtil.isNotBlank(paramIds)) {
            String[] ids = paramIds.split(",");
            List<Integer> idList = new ArrayList<Integer>();
            if (ids.length > 0) {
                for (String id : ids) {
                    idList.add(Integer.parseInt(id));
                }
            }
            Integer i = storeService.updateStatus(idList, StatusEnum.DISABLE.getKey());
        }
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
    @RequiresPermissions("backend/store/add")
    @RequestMapping(value = "/add")
    public String add(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        return "store/store_add";
    }

    /**
     * 新增店铺页面
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     */
    @RequiresPermissions("backend/store/create")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String addstoreHandler(HttpServletRequest request, HttpServletResponse response,
                                     Model model) throws BusinessCheckException {
        MtStoreDto tstoreInfo = new MtStoreDto();

        //表单直接赋值Dto
        //tstoreInfo = (MtStoreDto) RequestHandler.createBean(request, tstoreInfo);
        String storeId = request.getParameter("storeId");
        String storeName = CommonUtil.replaceXSS(request.getParameter("storeName"));
        String ContactName = CommonUtil.replaceXSS(request.getParameter("ContactName"));
        String ContactPhone = CommonUtil.replaceXSS(request.getParameter("ContactPhone"));
        String storeDesc = CommonUtil.replaceXSS(request.getParameter("storeDesc"));

        tstoreInfo.setName(storeName);
        tstoreInfo.setContact(ContactName);
        tstoreInfo.setPhone(ContactPhone);
        tstoreInfo.setDescription(storeDesc);

        if (StringUtils.isEmpty(storeName)) {
            throw new BusinessRuntimeException("店铺名称不能为空");
        } else {
            MtStoreDto tempDto = null;
            try {
                //不是edit
                if(!StringUtil.isNotEmpty(storeId))
                {
                    tempDto = storeService.queryStoreByName(storeName);
                }
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (null != tempDto) {
                throw new BusinessCheckException("该店铺名称已经存在!");
            }

        }

        try {
            //如果是 edit
            if(StringUtil.isNotEmpty(storeId))
            {
                tstoreInfo.setId(Integer.parseInt(storeId));
            }

        } catch (Exception e) {
            throw new BusinessRuntimeException("整型转化异常" + e.getMessage());
        }

        storeService.addStore(tstoreInfo);
        //刷新缓存
        return "redirect:/backend/store/queryList";
    }

    /**
     * 编辑活动初始化页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/store/storeEditInit/{id}")
    @RequestMapping(value = "/storeEditInit/{id}")
    public String storeRuleEditInit(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Integer id) throws BusinessCheckException {

        MtStoreDto mtstoreInfo = null;
        try {
            mtstoreInfo = storeService.queryStoreDtoById(id);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        model.addAttribute("store", mtstoreInfo);
        return "store/store_edit";
    }


}

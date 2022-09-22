package com.fuint.application.web.backend.store;

import com.fuint.application.dao.repositories.MtStoreRepository;
import com.fuint.base.dao.entities.TAccount;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.service.account.TAccountService;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.base.util.RequestHandler;
import com.fuint.base.shiro.ShiroUser;
import com.fuint.exception.BusinessCheckException;
import com.fuint.exception.BusinessRuntimeException;
import com.fuint.util.StringUtil;
import com.fuint.application.dao.entities.*;
import com.fuint.application.dto.MtStoreDto;
import com.fuint.application.dto.ReqResult;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.store.StoreService;
import com.fuint.application.util.CommonUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 店铺管理类controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Controller
@RequestMapping(value = "/backend/store")
public class storeController {

    /**
     * 店铺服务接口
     */
    @Autowired
    private StoreService storeService;

    /**
     * 后台账户服务接口
     */
    @Autowired
    private TAccountService tAccountService;

    @Autowired
    private MtStoreRepository storeRepository;

    /**
     * 查询店铺列表
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 店铺列表
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
            params = new HashMap<>();
            if (StringUtil.isNotEmpty(storeId)) {
                params.put("EQ_id", storeId);
            }
            if (StringUtil.isNotEmpty(store_name)) {
                params.put("LIKE_name", store_name);
            }
            if (StringUtil.isNotEmpty(storeStatus)) {
                params.put("EQ_status", storeStatus);
            }
        }

        params.put("NQ_status", StatusEnum.DISABLE.getKey());
        paginationRequest.setSearchParams(params);
        paginationRequest.setSortColumn(new String[]{"status asc", "isDefault desc"});
        PaginationResponse<MtStore> paginationResponse = storeService.queryStoreListByPagination(paginationRequest);

        model.addAttribute("paginationResponse", paginationResponse);
        model.addAttribute("params", params);

        return "store/store_list";
    }

    /**
     * 查询店铺页面（多选）
     * */
    @RequiresPermissions("backend/store/searchStore")
    @RequestMapping(value = "/searchStore")
    public String searchStore(HttpServletRequest request) throws BusinessCheckException {
        return "components/storeQuickSearch";
    }

    /**
     * 快速查询店铺
     * */
    @RequiresPermissions("backend/store/quickSearchStore")
    @RequestMapping(value = "/quickSearchStore")
    public String quickSearchStore(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        Map<String, Object> params = paginationRequest.getSearchParams();

        if (null == params) {
            params = new HashMap<>();
        }

        params.put("EQ_status", StatusEnum.ENABLED.getKey());

        List<MtStore> storeList = storeService.queryStoresByParams(params);
        model.addAttribute("storeList", storeList);

        return "components/storeList";
    }

    /**
     * 查询店铺页面（单选）
     * */
    @RequiresPermissions("backend/store/quickSearchInit")
    @RequestMapping(value = "/quickSearchInit")
    public String quickSearchInit(HttpServletRequest request, HttpServletResponse response, Model model) {
        String name = request.getParameter("name");
        model.addAttribute("name", name);
        return "components/simpleStoreQuickSearch";
    }

    /**
     * 快速查询店铺
     * */
    @RequiresPermissions("backend/store/quickSearchList")
    @RequestMapping(value = "/quickSearchList")
    public String quickSearchList(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        Map<String, Object> params = paginationRequest.getSearchParams();
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("EQ_status", StatusEnum.ENABLED.getKey());

        Long accId = ShiroUserHelper.getCurrentShiroUser().getId();
        TAccount tAccount = tAccountService.findAccountById(accId);
        if (tAccount.getStoreId() > 0) {
            params.put("EQ_id", tAccount.getStoreId().toString());
        }

        List<MtStore> storeList = storeService.queryStoresByParams(params);
        model.addAttribute("storeList", storeList);

        return "components/simpleStoreList";
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
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        if (shiroUser == null) {
            return "redirect:/login";
        }

        String operator = shiroUser.getAcctName();

        MtStore mtStore = storeRepository.findOne(id);

        if (mtStore == null) {
            throw new BusinessCheckException("该店铺状态异常");
        }

        mtStore.setStatus(StatusEnum.ENABLED.getKey());
        mtStore.setUpdateTime(new Date());
        mtStore.setOperator(operator);

        storeRepository.save(mtStore);

        ReqResult reqResult = new ReqResult();
        reqResult.setResult(true);

        return "redirect:/backend/store/queryList";
    }
    /**
     * 禁用、删除店铺
     *
     * @param request
     * @return
     */
    @RequiresPermissions("backend/store/delete/{id}")
    @RequestMapping(value = "/delete/{id}")
    public String delete(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String status = request.getParameter("status") != null ? request.getParameter("status"): StatusEnum.ENABLED.getKey();

        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        if (shiroUser == null) {
            return "redirect:/login";
        }

        String operator = shiroUser.getAcctName();
        storeService.updateStatus(id, operator, status);
        ReqResult reqResult = new ReqResult();

        reqResult.setResult(true);
        return "redirect:/backend/store/queryList";
    }

    /**
     * 添加店铺初始化页面
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
     * 保存店铺
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     */
    @RequiresPermissions("backend/store/save")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveHandler(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        if (shiroUser == null) {
            return "redirect:/login";
        }

        MtStoreDto storeInfo = new MtStoreDto();

        String storeId = request.getParameter("storeId");
        String storeName = CommonUtil.replaceXSS(request.getParameter("storeName"));
        String ContactName = CommonUtil.replaceXSS(request.getParameter("ContactName"));
        String ContactPhone = CommonUtil.replaceXSS(request.getParameter("ContactPhone"));
        String storeDesc = CommonUtil.replaceXSS(request.getParameter("storeDesc"));
        String isDefault = CommonUtil.replaceXSS(request.getParameter("isDefault"));
        String address = CommonUtil.replaceXSS(request.getParameter("address"));
        String hours = CommonUtil.replaceXSS(request.getParameter("hours"));
        String latitude = CommonUtil.replaceXSS(request.getParameter("latitude"));
        String longitude = CommonUtil.replaceXSS(request.getParameter("longitude"));

        if ((StringUtil.isEmpty(latitude) || StringUtil.isEmpty(longitude)) && StringUtil.isNotEmpty(address)) {
            Map<String, Object> latAndLng = CommonUtil.getLatAndLngByAddress(address);
            latitude = latAndLng.get("lat").toString();
            longitude = latAndLng.get("lng").toString();
        }

        storeInfo.setName(storeName);
        storeInfo.setContact(ContactName);
        storeInfo.setPhone(ContactPhone);
        storeInfo.setDescription(storeDesc);
        storeInfo.setIsDefault(isDefault);
        storeInfo.setAddress(address);
        storeInfo.setHours(hours);
        storeInfo.setLatitude(latitude);
        storeInfo.setLongitude(longitude);

        if (StringUtil.isEmpty(storeName)) {
            throw new BusinessRuntimeException("店铺名称不能为空");
        } else {
            MtStoreDto tempDto = null;
            try {
                if (!StringUtil.isNotEmpty(storeId)) {
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

        // 如果是编辑
        if (StringUtil.isNotEmpty(storeId)) {
            storeInfo.setId(Integer.parseInt(storeId));
        }

        String operator = shiroUser.getAcctName();
        storeInfo.setOperator(operator);

        storeService.saveStore(storeInfo);

        return "redirect:/backend/store/queryList";
    }

    /**
     * 编辑店铺初始化页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/store/storeEditInit/{id}")
    @RequestMapping(value = "/storeEditInit/{id}")
    public String storeRuleEditInit(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Integer id) throws BusinessCheckException {
        MtStoreDto storeInfo = null;
        try {
            storeInfo = storeService.queryStoreDtoById(id);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        model.addAttribute("store", storeInfo);
        return "store/store_edit";
    }
}

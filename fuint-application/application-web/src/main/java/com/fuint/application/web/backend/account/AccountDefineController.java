/**
 * fuint.com Inc.
 * Copyright (c) 2019-2020 All Rights Reserved.
 */
package com.fuint.application.web.backend.account;

import com.fuint.base.dao.entities.TAccount;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.service.account.TAccountService;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.base.util.RequestHandler;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 用户管理 - Controller
 *
 * Created by zach on 2020/04/16
 */
@Controller
@RequestMapping(value = "/user")
public class AccountDefineController {

    /**
     * 账户服务接口
     */
    @Autowired
    private TAccountService htAccountServiceImpl;


    /**
     * 账户信息列表展现
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 账户信息列表展现页面
     */
    @RequiresPermissions("user/queryListByStore")
    @RequestMapping(value = "/queryListByStore")
    public String accountList(HttpServletRequest request, HttpServletResponse response, Model model) {
        Long accID = ShiroUserHelper.getCurrentShiroUser().getId();
        TAccount tAccount=htAccountServiceImpl.findAccountById(accID);

        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        Map<String, Object> params = paginationRequest.getSearchParams();

        if (tAccount.getStoreId()!=null && tAccount.getStoreId()!=-1) {
            params.put("EQ_storeId", tAccount.getStoreId().toString());
        }

        paginationRequest.setSearchParams(params);

        PaginationResponse<TAccount> paginationResponse = htAccountServiceImpl.findAccountsByPagination(paginationRequest);
        model.addAttribute("paginationResponse", paginationResponse);
        model.addAttribute("tAccount", tAccount);
        return "account/account_list";
    }
}

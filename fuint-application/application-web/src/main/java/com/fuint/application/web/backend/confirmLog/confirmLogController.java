package com.fuint.application.web.backend.confirmLog;

import com.fuint.base.dao.entities.TAccount;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.service.account.TAccountService;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.MtStore;
import com.fuint.application.service.confirmlog.ConfirmLogService;
import com.fuint.application.service.store.StoreService;
import com.fuint.application.dto.ConfirmLogDto;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 会员卡券核销流水
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Controller
@RequestMapping(value = "/backend/confirmLog")
public class confirmLogController {

    private static final Logger logger = LoggerFactory.getLogger(confirmLogController.class);

    /**
     * 卡券核销流水接口
     */
    @Autowired
    private ConfirmLogService ConfirmLogService;

    /**
     * 店铺服务接口
     */
    @Autowired
    private StoreService storeService;

    /**
     * 后台员工账户服务接口
     */
    @Autowired
    private TAccountService tAccountService;

    /**
     * 会员卡券核销记录列表
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return
     */
    @RequiresPermissions("backend/confirmLog/confirmLogList")
    @RequestMapping(value = "/confirmLogList")
    public String confirmLogList(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        Map<String, Object> params = paginationRequest.getSearchParams();

        paginationRequest.setSortColumn(new String[]{"updateTime desc", "id desc"});
        paginationRequest.setSearchParams(params);

        List<MtStore> storeList;
        // 登录员工所属店铺处理
        Long accID = ShiroUserHelper.getCurrentShiroUser().getId();
        TAccount tAccount = tAccountService.findAccountById(accID);
        HashMap<String, Object> paramsStore = new HashMap<String, Object>();
        if (tAccount.getStoreId() == null || tAccount.getStoreId().equals(-1)) {
            if (tAccount.getStoreId() == null) {
                paramsStore.put("EQ_id", "0");
                params.put("EQ_storeId", "0");
            }
            storeList = storeService.queryStoresByParams(paramsStore);
        } else {
            paramsStore.put("EQ_id",tAccount.getStoreId().toString());
            params.put("EQ_storeId",tAccount.getStoreId().toString());
            List<Integer> ids = new ArrayList<Integer>();
            ids.add(tAccount.getStoreId().intValue());
            storeList = storeService.queryStoresByIds(ids);
        }

        PaginationResponse<ConfirmLogDto> paginationResponse = ConfirmLogService.queryConfirmLogListByPagination(paginationRequest);
        model.addAttribute("paginationResponse", paginationResponse);
        model.addAttribute("storeList", storeList);
        model.addAttribute("params", params);

        return "confirmLog/confirmLogList";
    }
}

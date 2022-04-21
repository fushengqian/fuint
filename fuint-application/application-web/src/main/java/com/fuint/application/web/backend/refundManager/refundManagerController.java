package com.fuint.application.web.backend.refundManager;

import com.fuint.application.dao.entities.MtRefund;
import com.fuint.application.dto.RefundDto;
import com.fuint.application.enums.RefundStatusEnum;
import com.fuint.application.service.refund.RefundService;
import com.fuint.base.dao.entities.TAccount;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.service.account.TAccountService;
import com.fuint.base.shiro.ShiroUser;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 售后管理controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Controller
@RequestMapping(value = "/backend/refund")
public class refundManagerController {

    /**
     * 订单服务接口
     * */
    @Autowired
    private RefundService refundService;

    /**
     * 后台账户服务接口
     */
    @Autowired
    private TAccountService accountService;

    /**
     * 退款列表查询
     *
     * @param request  HttpServletRequest对象
     * @param model    SpringFramework Model对象
     * @return
     */
    @RequiresPermissions("backend/refund/index")
    @RequestMapping(value = "/index")
    public String list(HttpServletRequest request, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        Map<String, Object> params = paginationRequest.getSearchParams();

        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        TAccount account = accountService.findAccountById(shiroUser.getId());
        Integer storeId = account.getStoreId();

        PaginationResponse<MtRefund> paginationResponse = refundService.getRefundListByPagination(paginationRequest);

        model.addAttribute("paginationResponse", paginationResponse);
        model.addAttribute("params", params);
        model.addAttribute("storeId", storeId);

        return "refund/list";
    }

    /**
     * 退款详情
     * @param request  HttpServletRequest对象
     * @param model    SpringFramework Model对象
     * @return
     * */
    @RequiresPermissions("backend/refund/detail/{refundId}")
    @RequestMapping(value = "/detail/{refundId}")
    public String detail(HttpServletRequest request, Model model, @PathVariable("refundId") Integer refundId) throws BusinessCheckException {
        MtRefund refundInfo = refundService.getRefundById(refundId);
        model.addAttribute("orderInfo", refundInfo);

        return "refund/detail";
    }

    /**
     * 审核通过
     * @return
     */
    @RequiresPermissions("backend/refund/agree/{id}")
    @RequestMapping(value = "/agree/{id}")
    public String agree(@PathVariable("id") Integer id) throws BusinessCheckException {
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        if (shiroUser == null) {
            return "redirect:/login";
        }

        String operator = shiroUser.getAcctName();

        RefundDto dto = new RefundDto();
        dto.setId(id);
        dto.setOperator(operator);
        dto.setStatus(RefundStatusEnum.APPROVED.getKey());

        refundService.agreeRefund(dto);

        return "redirect:/backend/refund/index";
    }

    /**
     * 拒绝售后
     * @return
     */
    @RequiresPermissions("backend/refund/disagree/{id}")
    @RequestMapping(value = "/disagree/{id}")
    public String disagree(@PathVariable("id") Integer id) throws BusinessCheckException {
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        if (shiroUser == null) {
            return "redirect:/login";
        }

        String operator = shiroUser.getAcctName();

        RefundDto dto = new RefundDto();
        dto.setId(id);
        dto.setOperator(operator);
        dto.setStatus(RefundStatusEnum.REJECT.getKey());

        refundService.updateRefund(dto);

        return "redirect:/backend/refund/index";
    }
}

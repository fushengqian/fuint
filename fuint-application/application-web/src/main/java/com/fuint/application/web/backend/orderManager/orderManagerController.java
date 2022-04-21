package com.fuint.application.web.backend.orderManager;

import com.fuint.application.ResponseObject;
import com.fuint.application.dto.ExpressDto;
import com.fuint.application.dto.OrderDto;
import com.fuint.application.dto.ReqResult;
import com.fuint.application.dto.UserOrderDto;
import com.fuint.application.enums.OrderStatusEnum;
import com.fuint.application.enums.OrderTypeEnum;
import com.fuint.application.enums.PayStatusEnum;
import com.fuint.application.service.order.OrderService;
import com.fuint.application.util.TimeUtils;
import com.fuint.base.dao.entities.TAccount;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.service.account.TAccountService;
import com.fuint.base.shiro.ShiroUser;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单管理controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Controller
@RequestMapping(value = "/backend/order")
public class orderManagerController {
    /**
     * 订单服务接口
     * */
    @Autowired
    private OrderService orderService;

    /**
     * 后台账户服务接口
     */
    @Autowired
    private TAccountService accountService;

    /**
     * 订单列表查询
     *
     * @param request  HttpServletRequest对象
     * @param model    SpringFramework Model对象
     * @return
     */
    @RequiresPermissions("backend/order/list")
    @RequestMapping(value = "/list")
    public String list(HttpServletRequest request, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        Map<String, Object> params = paginationRequest.getSearchParams();

        Map<String, Object> param = new HashMap<>();
        param.put("type", params.get("EQ_type"));
        param.put("orderSn", params.get("EQ_orderSn"));
        param.put("status", params.get("EQ_status"));
        param.put("payStatus", params.get("EQ_payStatus"));
        param.put("pageNumber", paginationRequest.getCurrentPage());
        param.put("pageSize", paginationRequest.getPageSize());
        param.put("userId", params.get("EQ_userId"));
        param.put("mobile", params.get("EQ_mobile"));
        param.put("storeId", params.get("EQ_storeId"));

        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        TAccount account = accountService.findAccountById(shiroUser.getId());
        Integer storeId = account.getStoreId();
        if (storeId > 0) {
            param.put("storeId", storeId.toString());
        }

        ResponseObject response = orderService.getUserOrderList(param);
        OrderTypeEnum[] typeList = OrderTypeEnum.values();
        OrderStatusEnum[] statusList = OrderStatusEnum.values();
        PayStatusEnum[] payStatusList = PayStatusEnum.values();

        model.addAttribute("paginationResponse", response.getData());
        model.addAttribute("typeList", typeList);
        model.addAttribute("statusList", statusList);
        model.addAttribute("payStatusList", payStatusList);
        model.addAttribute("params", params);
        model.addAttribute("storeId", storeId);

        return "order/list";
    }

    /**
     * 订单详情
     * @param request  HttpServletRequest对象
     * @param model    SpringFramework Model对象
     * @return
     * */
    @RequiresPermissions("backend/order/detail/{orderId}")
    @RequestMapping(value = "/detail/{orderId}")
    public String detail(HttpServletRequest request, Model model, @PathVariable("orderId") Integer orderId) throws BusinessCheckException {
        UserOrderDto orderInfo = orderService.getOrderById(orderId);
        model.addAttribute("orderInfo", orderInfo);

        return "order/detail";
    }

    /**
     * 确认发货
     * @param request  HttpServletRequest对象
     * @return
     * */
    @RequiresPermissions("backend/order/delivered")
    @RequestMapping(value = "/delivered")
    @ResponseBody
    public ReqResult delivered(HttpServletRequest request) throws BusinessCheckException {
        Integer orderId = request.getParameter("orderId") == null ? 0 : Integer.parseInt(request.getParameter("orderId"));
        String expressCompany = request.getParameter("expressCompany") == null ? "" : request.getParameter("expressCompany");
        String expressNo = request.getParameter("expressNo") == null ? "" : request.getParameter("expressNo");

        if (orderId < 0) {
            ReqResult reqResult = new ReqResult();
            reqResult.setCode("0");
            reqResult.setResult(false);
            return reqResult;
        }

        OrderDto dto = new OrderDto();
        dto.setId(orderId);
        dto.setStatus(OrderStatusEnum.DELIVERED.getKey());

        if (StringUtils.isNotEmpty(expressCompany) || StringUtils.isNotEmpty(expressNo)) {
            ExpressDto expressInfo = new ExpressDto();
            String time = TimeUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm");
            expressInfo.setExpressTime(time);
            expressInfo.setExpressNo(expressNo);
            expressInfo.setExpressCompany(expressCompany);
            dto.setExpressInfo(expressInfo);
        }

        orderService.updateOrder(dto);

        ReqResult reqResult = new ReqResult();
        reqResult.setCode("1");
        reqResult.setResult(true);
        reqResult.setMsg("执行成功");

        return reqResult;
    }
}

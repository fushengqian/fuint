package com.fuint.application.web.backend.orderManager;

import com.fuint.application.dao.entities.MtOrder;
import com.fuint.application.service.order.OrderService;
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
import javax.servlet.http.HttpServletRequest;

/**
 * 订单管理controller
 * Created by zach on 2021/05/18
 */
@Controller
@RequestMapping(value = "/backend/order")
public class OrderManagerController {

    private static final Logger logger = LoggerFactory.getLogger(OrderManagerController.class);

    /**
     * 积分服务接口
     * */
    @Autowired
    private OrderService orderService;

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

        PaginationResponse<MtOrder> paginationResponse = orderService.getOrderListByPagination(paginationRequest);

        model.addAttribute("paginationResponse", paginationResponse);

        return "order/list";
    }
}

package com.fuint.application.web.backend.orderManager;

import com.fuint.application.ResponseObject;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.dto.ExpressDto;
import com.fuint.application.dto.OrderDto;
import com.fuint.application.dto.ReqResult;
import com.fuint.application.dto.UserOrderDto;
import com.fuint.application.enums.OrderStatusEnum;
import com.fuint.application.enums.OrderTypeEnum;
import com.fuint.application.enums.PayStatusEnum;
import com.fuint.application.enums.WxMessageEnum;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.order.OrderService;
import com.fuint.application.service.weixin.WeixinService;
import com.fuint.application.util.TimeUtils;
import com.fuint.base.dao.entities.TAccount;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.service.account.TAccountService;
import com.fuint.base.shiro.ShiroUser;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import com.fuint.util.StringUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

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
     * 会员服务接口
     * */
    @Autowired
    private MemberService memberService;

    /**
     * 微信服务接口
     * */
    @Autowired
    private WeixinService weixinService;

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
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        if (shiroUser == null) {
            return "redirect:/login";
        }

        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        paginationRequest.getSearchParams().put("NQ_status", OrderStatusEnum.DELETED.getKey());
        paginationRequest.setSortColumn(new String[]{"status asc", "createTime desc"});
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
        param.put("orderMode", params.get("EQ_orderMode"));

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
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        if (shiroUser == null) {
            return "redirect:/login";
        }

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
            reqResult.setMsg("系统出错啦，订单ID不能为空");
            reqResult.setResult(false);
            return reqResult;
        }

        UserOrderDto orderInfo = orderService.getOrderById(orderId);
        MtUser userInfo = memberService.queryMemberById(orderInfo.getUserId());

        OrderDto dto = new OrderDto();
        dto.setId(orderId);
        dto.setStatus(OrderStatusEnum.DELIVERED.getKey());

        if (StringUtil.isNotEmpty(expressCompany) || StringUtil.isNotEmpty(expressNo)) {
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

        // 发送小程序订阅消息
        if (orderInfo != null && userInfo != null) {
            Date nowTime = new Date();
            Date sendTime = new Date(nowTime.getTime() - 60000);
            Map<String, Object> params = new HashMap<>();
            params.put("receiver", orderInfo.getAddress().getName());
            params.put("orderSn", orderInfo.getOrderSn());
            params.put("expressCompany", expressCompany);
            params.put("expressNo", expressNo);
            weixinService.sendSubscribeMessage(userInfo.getId(), userInfo.getOpenId(), WxMessageEnum.DELIVER_GOODS.getKey(), "pages/order/index", params, sendTime);
        }

        return reqResult;
    }

    /**
     * 修改订单
     * @param request  HttpServletRequest对象
     * @return
     * */
    @RequiresPermissions("backend/order/modify")
    @RequestMapping(value = "/modify")
    @ResponseBody
    public ReqResult modify(HttpServletRequest request) throws BusinessCheckException {
        Integer orderId = request.getParameter("orderId") == null ? 0 : Integer.parseInt(request.getParameter("orderId"));
        String status = request.getParameter("status") == null ? "" : request.getParameter("status");
        String amount = request.getParameter("amount") == null ? "" : request.getParameter("amount");
        String discount = request.getParameter("discount") == null ? "" : request.getParameter("discount");
        String remark = request.getParameter("remark") == null ? "" : request.getParameter("remark");

        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        if (shiroUser == null) {
            ReqResult reqResult = new ReqResult();
            reqResult.setCode("0");
            reqResult.setMsg("登录信息已失效，请重新登录");
            reqResult.setResult(false);
            return reqResult;
        }

        if (orderId < 0) {
            ReqResult reqResult = new ReqResult();
            reqResult.setCode("0");
            reqResult.setMsg("系统出错啦，订单ID不能为空");
            reqResult.setResult(false);
            return reqResult;
        }

        OrderDto orderDto = new OrderDto();
        orderDto.setId(orderId);
        orderDto.setOperator(shiroUser.getAcctName());
        if (StringUtil.isNotEmpty(status)) {
            orderDto.setStatus(status);
        }

        if (StringUtil.isNotEmpty(amount)) {
            orderDto.setAmount(new BigDecimal(amount));
        }

        if (StringUtil.isNotEmpty(discount)) {
            orderDto.setDiscount(new BigDecimal(discount));
        }

        if (StringUtil.isNotEmpty(remark)) {
            orderDto.setRemark(remark);
        }

        try {
            orderService.updateOrder(orderDto);
        } catch (BusinessCheckException e) {

        }

        ReqResult reqResult = new ReqResult();
        reqResult.setCode("1");
        reqResult.setResult(true);
        reqResult.setMsg("订单修改成功");

        return reqResult;
    }

    /**
     * 最新订单列表查询
     * @param request  HttpServletRequest对象
     * @return
     */
    @RequiresPermissions("backend/order/latest")
    @RequestMapping(value = "/latest")
    @ResponseBody
    public ReqResult latest(HttpServletRequest request) throws BusinessCheckException {
        Integer pageSize = request.getParameter("pageSize") == null ? 10 : Integer.parseInt(request.getParameter("pageSize"));
        Integer page = request.getParameter("page") == null ? 1 : Integer.parseInt(request.getParameter("page"));
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();

        ReqResult reqResult = new ReqResult();
        reqResult.setCode("200");
        if (shiroUser == null) {
            Map<String, Object> data = new HashMap();
            data.put("goodsList", new ArrayList<>());
            reqResult.setData(data);
            return reqResult;
        }

        Map<String, Object> param = new HashMap<>();
        param.put("pageNumber", page);
        param.put("pageSize", pageSize);

        TAccount account = accountService.findAccountById(shiroUser.getId());
        Integer storeId = account.getStoreId();
        if (storeId > 0) {
            param.put("storeId", storeId.toString());
        }

        ResponseObject response = orderService.getUserOrderList(param);

        Map<String, Object> data = new HashMap();
        data.put("goodsList", response.getData());

        reqResult.setData(data);
        return reqResult;
    }

    /**
     * 订单信息
     * @return
     * */
    @RequiresPermissions("backend/order/info/{orderId}")
    @RequestMapping(value = "/info/{orderId}")
    @ResponseBody
    public ReqResult info(@PathVariable("orderId") Integer orderId) throws BusinessCheckException {
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();

        ReqResult reqResult = new ReqResult();
        reqResult.setCode("200");
        if (shiroUser == null) {
            Map<String, Object> data = new HashMap();
            data.put("orderInfo", null);
            reqResult.setData(data);
            return reqResult;
        }

        UserOrderDto orderInfo = orderService.getOrderById(orderId);
        Map<String, Object> data = new HashMap();
        data.put("orderInfo", orderInfo);
        reqResult.setData(data);

        return reqResult;
    }

    /**
     * 删除订单
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/order/delete")
    @RequestMapping(value = "/delete/{id}")
    public String delete(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Integer id) throws BusinessCheckException {
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();

        if (shiroUser == null) {
            return "redirect:/login";
        }

        String operator = shiroUser.getAcctName();

        orderService.deleteOrder(id, operator);

        ReqResult reqResult = new ReqResult();
        reqResult.setResult(true);

        return "redirect:/backend/order/list";
    }
}

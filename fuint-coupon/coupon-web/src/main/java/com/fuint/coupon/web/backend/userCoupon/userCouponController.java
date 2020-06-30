package com.fuint.coupon.web.backend.userCoupon;

import com.fuint.exception.BusinessCheckException;
import com.fuint.util.DateUtil;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.coupon.dao.entities.MtCouponGroup;
import com.fuint.coupon.dto.ReqResult;
import com.fuint.coupon.service.coupongroup.CouponGroupService;
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
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.coupon.dto.ReqCouponGroupDto;
import com.fuint.coupon.dto.ReqSendCouponDto;
import com.fuint.base.util.RequestHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 优惠券统计管理类controller
 * Created by zach on 2019/08/28.
 */
@Controller
@RequestMapping(value = "/backend/userCoupon")
public class userCouponController {

    private static final Logger logger = LoggerFactory.getLogger(userCouponController.class);

    /**
     * 优惠分组服务接口
     */
    @Autowired
    private CouponGroupService couponGroupService;

    /**
     * 上次执行搜索全量索引的时间
     */
    private Date lastIndexTime = null;

    /**
     * 优惠分组列表查询
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 促销活动列表展现页面
     */
    @RequiresPermissions("backend/couponGroup/index")
    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String EQ_code = request.getParameter("EQ_code");
        model.addAttribute("EQ_code", EQ_code);
        if (lastIndexTime != null) {
            long diff = DateUtil.getDiffSeconds(new Date(), lastIndexTime);
            model.addAttribute("isDisable", diff < 30 ? "disable" : "");
        }
        return "couponGroup/index";
    }

    /**
     * 查询促销活动组列表
     *
     * @param request
     * @param response
     * @param model
     * @return
     * @throws BusinessCheckException
     */
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    @RequiresPermissions("/backend/couponGroup/queryList")
    public String queryList(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        PaginationResponse<MtCouponGroup> paginationResponse = couponGroupService.queryCouponGroupListByPagination(paginationRequest);

        // 计算券种类，总价值
        if (paginationResponse.getContent().size() > 0) {
            for (int i=0; i < paginationResponse.getContent().size(); i++) {
                MtCouponGroup object = paginationResponse.getContent().get(i);
                object.setMoney(couponGroupService.getCouponMoney(object.getId().longValue()));
                object.setNum(couponGroupService.getCouponNum(object.getId()));
            }
        }

        model.addAttribute("paginationResponse", paginationResponse);
        return "couponGroup/list";
    }

    /**
     * 删除电子群组
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/couponGroup/delete")
    @RequestMapping(value = "/delete/{id}")
    @ResponseBody
    public ReqResult delete(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Long id) throws BusinessCheckException {
        List<Long> ids = new ArrayList<Long>();
        ids.add(id);

        String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
        couponGroupService.deleteCouponGroup(id, operator);

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
    @RequiresPermissions("backend/couponGroup/add")
    @RequestMapping(value = "/add")
    public String add(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        return "couponGroup/add";
    }

    /**
     * 新增优惠分组页面
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     */
    @RequiresPermissions("backend/couponGroup/create")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String addActivityHandler(HttpServletRequest request, HttpServletResponse response,
                                     Model model, ReqCouponGroupDto reqCouponGroupDto) throws BusinessCheckException {

        String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
        reqCouponGroupDto.setOperator(operator);
        couponGroupService.addCouponGroup(reqCouponGroupDto);

        return "redirect:/backend/couponGroup/index";
    }

    /**
     * 编辑优惠分组初始化页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/couponGroupEditInit")
    @RequestMapping(value = "/couponGroupEditInit/{id}")
    public String couponGroupEditInit(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Long id) throws BusinessCheckException {
        MtCouponGroup mtCouponGroup = couponGroupService.queryCouponGroupById(id);
        model.addAttribute("couponGroup", mtCouponGroup);
        return "couponGroup/edit";
    }

    /**
     * 编辑优惠分组初始化页面
     *
     * @return
     */
    @RequiresPermissions("backend/couponGroup/update")
    @RequestMapping(value = "/update")
    public String couponGroupUpdate( ReqCouponGroupDto reqCouponGroupDto) throws BusinessCheckException {

        String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
        reqCouponGroupDto.setOperator(operator);
        couponGroupService.updateCouponGroup(reqCouponGroupDto);

        return "redirect:/backend/couponGroup/index";
    }

    /**
     * 发放优惠券
     *
     * @return
     */
    @RequiresPermissions("backend/couponGroup/sendCoupon")
    @RequestMapping(value = "/sendCoupon")
    @ResponseBody
    public ReqResult sendCoupon() throws BusinessCheckException {
        ReqResult reqResult = new ReqResult();
        reqResult.setResult(true);
        return reqResult;
    }
}

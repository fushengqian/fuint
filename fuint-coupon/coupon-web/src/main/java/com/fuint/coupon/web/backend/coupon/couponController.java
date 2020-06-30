package com.fuint.coupon.web.backend.coupon;

import com.fuint.base.shiro.ShiroUser;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.coupon.dao.repositories.MtCouponGroupRepository;
import com.fuint.coupon.enums.GroupTypeEnum;
import com.fuint.exception.BusinessCheckException;
import com.fuint.util.DateUtil;
import com.fuint.coupon.dao.entities.*;
import com.fuint.coupon.dao.repositories.MtSendLogRepository;
import com.fuint.coupon.dao.repositories.MtUserCouponRepository;
import com.fuint.coupon.dto.ContentDto;
import com.fuint.coupon.dto.ReqResult;
import com.fuint.coupon.enums.StatusEnum;
import com.fuint.coupon.service.coupon.CouponService;
import com.fuint.coupon.service.coupongroup.CouponGroupService;
import com.fuint.coupon.service.store.StoreService;
import com.fuint.coupon.service.sendlog.SendLogService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.coupon.dto.ReqCouponDto;
import com.fuint.coupon.dto.DateDto;
import com.fuint.base.util.RequestHandler;
import com.fuint.coupon.web.backend.base.BaseController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * 优惠券管理类controller
 * Created by zach on 2019/08/05.
 */
@Controller
@RequestMapping(value = "/backend/coupon")
public class couponController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(couponController.class);

    /**
     * 优惠券服务接口
     */
    @Autowired
    private CouponService couponService;

    /**
     * 优惠分组服务接口
     * */
    @Autowired
    private CouponGroupService couponGroupService;

    /**
     * 店铺服务接口
     */
    @Autowired
    private StoreService storeService;

    @Autowired
    private MtUserCouponRepository userCouponRepository;

    @Autowired
    private MtSendLogRepository sendLogRepository;

    @Autowired
    private SendLogService sendLogService;

    @Autowired
    private MtCouponGroupRepository couponGroupRepository;

    @Autowired
    private Environment env;

    /**
     * 上次执行搜索全量索引的时间
     */
    private Date lastIndexTime = null;

    /**
     * 优惠券列表查询
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 优惠券列表展现页面
     */
    @RequiresPermissions("backend/coupon/index")
    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String EQ_groupId = request.getParameter("EQ_groupId");

        model.addAttribute("EQ_groupId", EQ_groupId);

        if (StringUtils.isNotEmpty(EQ_groupId)) {
            MtCouponGroup group = couponGroupService.queryCouponGroupById(Long.parseLong(EQ_groupId));
            model.addAttribute("groupName", group.getName());
        }

        if (lastIndexTime != null) {
            long diff = DateUtil.getDiffSeconds(new Date(), lastIndexTime);
            model.addAttribute("isDisable", diff < 30 ? "disable" : "");
        }

        // 已有用户使用，不允许再修改
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        paginationRequest.setCurrentPage(1);
        paginationRequest.setPageSize(1);
        paginationRequest.getSearchParams().put("EQ_groupId", EQ_groupId);
        PaginationResponse<MtUserCoupon> paginationResponse = userCouponRepository.findResultsByPagination(paginationRequest);
        if (paginationResponse.getContent().size() > 0) {
            model.addAttribute("canCreate", "N");
        } else {
            model.addAttribute("canCreate", "Y");
        }

        return "coupon/index";
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
    @RequiresPermissions("/backend/coupon/queryList")
    public String queryList(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        PaginationResponse<MtCoupon> paginationResponse = couponService.queryCouponListByPagination(paginationRequest);

        List<MtCoupon> dataList = paginationResponse.getContent();
        List<ContentDto> storeMap = new ArrayList<>();
        List<MtCouponGroup> groupMap = new ArrayList<>();

        if (dataList.size() > 0) {
            for (MtCoupon coupon : dataList) {
                MtCouponGroup groupInfo = couponGroupRepository.findOne(coupon.getGroupId());
                MtCouponGroup g = new MtCouponGroup();
                g.setId(groupInfo.getId());
                g.setName(groupInfo.getName());
                g.setTotal(groupInfo.getTotal());

                // 取券类型
                if (GroupTypeEnum.PRESTORE.getKey().equals(groupInfo.getType())) {
                    g.setType(GroupTypeEnum.PRESTORE.getValue());
                } else {
                    g.setType(GroupTypeEnum.COUPON.getValue());
                }

                Boolean isInGroup = false;
                for (MtCouponGroup gg : groupMap) {
                    if (gg.getId().equals(groupInfo.getId())) {
                        isInGroup = true;
                    }
                }
                if (!isInGroup) {
                    groupMap.add(g);
                }

                String storeName = "";
                String storeIds = coupon.getStoreIds();
                if (StringUtils.isNotEmpty(storeIds)) {
                    String[] list = storeIds.split(",");
                    if (list.length > 0) {
                        for (String id : list) {
                            MtStore store = storeService.queryStoreById(Integer.parseInt(id));
                            storeName = storeName.length() > 0 ? storeName + ','+ store.getName() : store.getName();
                        }
                    }
                }

                Boolean isIn = false;
                if (storeMap.size() > 0) {
                    for (ContentDto oo : storeMap) {
                         if (oo.getKey().equals(storeIds)) {
                             isIn = true;
                         }
                    }
                }

                if (false == isIn) {
                    ContentDto h = new ContentDto();
                    h.setKey(storeIds);
                    if (storeName.length() > 60) {
                        storeName = storeName.substring(0, 60) + "...";
                    }
                    h.setValue(storeName);
                    if (StringUtils.isNotEmpty(h.getKey())) {
                        storeMap.add(h);
                    }
                }
            }
        }

        model.addAttribute("paginationResponse", paginationResponse);

        String groupId = request.getParameter("EQ_groupId");

        // 分组已发放，不允许再增加
        PaginationRequest paginationRequestFor = RequestHandler.buildPaginationRequest(request, model);
        paginationRequestFor.setCurrentPage(1);
        paginationRequestFor.setPageSize(1);
        paginationRequestFor.getSearchParams().put("EQ_groupId", groupId);
        PaginationResponse<MtUserCoupon> paginationResponseFor = userCouponRepository.findResultsByPagination(paginationRequestFor);
        if (paginationResponseFor.getContent().size() > 0) {
            model.addAttribute("canCreate", "N");
        } else {
            model.addAttribute("canCreate", "Y");
        }

        Integer groupTotal = 0;
        if (StringUtils.isNotEmpty(groupId)) {
            MtCouponGroup groupInfo = couponGroupService.queryCouponGroupById(Long.parseLong(groupId));
            groupTotal = groupInfo.getTotal();
        }

        model.addAttribute("groupTotal", groupTotal);
        model.addAttribute("storeMap", storeMap);
        model.addAttribute("groupMap", groupMap);

        return "coupon/list";
    }

    /**
     * 删除
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/coupon/delete")
    @RequestMapping(value = "/delete/{id}")
    @ResponseBody
    public ReqResult delete(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Long id) throws BusinessCheckException {
        List<Long> ids = new ArrayList<Long>();
        ids.add(id);

        String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
        couponService.deleteCoupon(id, operator);

        ReqResult reqResult = new ReqResult();
        reqResult.setResult(true);
        return reqResult;
    }

    /**
     * 添加卡券初始化页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/coupon/add")
    @RequestMapping(value = "/add")
    public String add(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String groupId = request.getParameter("groupId");
        model.addAttribute("groupId", groupId);

        if (StringUtils.isNotEmpty(groupId)) {
            MtCouponGroup mtCouponInfo = couponGroupService.queryCouponGroupById(Long.parseLong(groupId));
            model.addAttribute("groupInfo", mtCouponInfo);
        }

        return "coupon/add";
    }

    /**
     * 新增优惠券
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     */
    @RequiresPermissions("backend/coupon/create")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String addCouponHandler(HttpServletRequest request, HttpServletResponse response,
                                     Model model, ReqCouponDto reqCouponDto) throws BusinessCheckException {

        String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
        reqCouponDto.setOperator(operator);

        PaginationRequest requestName = RequestHandler.buildPaginationRequest(request, model);
        requestName.getSearchParams().put("EQ_name", reqCouponDto.getName());
        requestName.getSearchParams().put("EQ_groupId", reqCouponDto.getGroupId().toString());
        PaginationResponse<MtCoupon> dataName = couponService.queryCouponListByPagination(requestName);
        if (dataName.getContent().size() > 0) {
            throw new BusinessCheckException("券名称已存在，请修改");
        }

        // 分组已发放，不允许再增加
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        paginationRequest.setCurrentPage(1);
        paginationRequest.setPageSize(1);
        paginationRequest.getSearchParams().put("EQ_groupId", reqCouponDto.getGroupId().toString());
        PaginationResponse<MtUserCoupon> paginationResponse = userCouponRepository.findResultsByPagination(paginationRequest);
        if (paginationResponse.getContent().size() > 0) {
            throw new BusinessCheckException("该分组已开始使用，不允许再新增卡券");
        }

        MtCoupon coupon = couponService.addCoupon(reqCouponDto);

        return "redirect:/backend/coupon/index?EQ_groupId="+coupon.getGroupId().toString();
    }

    /**
     * 编辑优惠券
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     */
    @RequiresPermissions("backend/coupon/update")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String updateCouponHandler(HttpServletRequest request, HttpServletResponse response,
                                     Model model, ReqCouponDto reqCouponDto) throws BusinessCheckException {

        String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
        reqCouponDto.setOperator(operator);

        // 分组已发放，不允许再增加
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        paginationRequest.setCurrentPage(1);
        paginationRequest.setPageSize(1);
        paginationRequest.getSearchParams().put("EQ_groupId", reqCouponDto.getGroupId().toString());

        PaginationResponse<MtUserCoupon> paginationResponse = userCouponRepository.findResultsByPagination(paginationRequest);
        if (paginationResponse.getContent().size() > 0) {
            throw new BusinessCheckException("该分组已发放，不允许再修改优惠券");
        }

        MtCoupon coupon = couponService.updateCoupon(reqCouponDto);

        return "redirect:/backend/coupon/index?EQ_groupId="+coupon.getGroupId().toString();
    }

    /**
     * 编辑优惠券初始化页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/coupon/couponEditInit")
    @RequestMapping(value = "/couponEditInit/{id}")
    public String couponGroupEditInit(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Long id) throws BusinessCheckException {
        MtCoupon mtCouponInfo = couponService.queryCouponById(id);

        String baseImage = env.getProperty("website.url");

        model.addAttribute("baseImage", baseImage);
        model.addAttribute("couponInfo", mtCouponInfo);

        MtCouponGroup mtGroupInfo = couponGroupService.queryCouponGroupById(mtCouponInfo.getGroupId().longValue());
        model.addAttribute("groupInfo", mtGroupInfo);

        List<MtStore> storeList = new ArrayList<>();

        if (StringUtils.isNotEmpty(mtCouponInfo.getStoreIds())) {
            String[] ids = mtCouponInfo.getStoreIds().split(",");
            for (String storeId : ids) {
                MtStore info = storeService.queryStoreById(Integer.parseInt(storeId));
                storeList.add(info);
            }
        }

        model.addAttribute("storeList", storeList);

        List<DateDto> exceptTimeList = new ArrayList<>();
        if (StringUtils.isNotEmpty(mtCouponInfo.getExceptTime())) {
            String[] exceptTimeArr = mtCouponInfo.getExceptTime().split(",");
            if (exceptTimeArr.length > 0) {
                for (int i=0; i<exceptTimeArr.length; i++) {
                    if (!exceptTimeArr[i].equals("weekend")) {
                        String[] date = exceptTimeArr[i].split("_");
                        DateDto dto = new DateDto();
                        dto.setStartDate(date[0]);
                        dto.setEndDate(date[1]);

                        exceptTimeList.add(dto);
                    }
                }
            }
        }
        model.addAttribute("exceptTimeList", exceptTimeList);

        // 分组已发放，不允许再修改
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        paginationRequest.setCurrentPage(1);
        paginationRequest.setPageSize(1);
        paginationRequest.getSearchParams().put("EQ_groupId", mtCouponInfo.getGroupId().toString());
        PaginationResponse<MtUserCoupon> paginationResponse = userCouponRepository.findResultsByPagination(paginationRequest);

        if (paginationResponse.getContent().size() > 0) {
            model.addAttribute("canUpdate", "N");
        } else {
            model.addAttribute("canUpdate", "Y");
        }

        return "coupon/edit";
    }

    /**
     * 查询店铺页面
     * */
    @RequiresPermissions("backend/coupon/searchStore")
    @RequestMapping(value = "/searchStore")
    public String searchStore(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        return "components/storeQuickSearch";
    }

    /**
     * 快速查询店铺
     * */
    @RequiresPermissions("backend/coupon/quickSearchStore")
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
     * 根据券ID 删除个人优惠券
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/coupon/deleteUserCoupon/{id}")
    @RequestMapping(value = "/deleteUserCoupon/{id}")
    public String deleteUserCoupon(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Integer id) throws BusinessCheckException {
        List<Integer> ids = new ArrayList<Integer>();
        ids.add(id);

        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        if (null==shiroUser) {
            return "redirect:/login";
        }

        couponService.deleteUserCoupon(id, shiroUser.getAcctName());

        ReqResult reqResult = new ReqResult();
        reqResult.setResult(true);

        // 发券记录，部分作废
        MtUserCoupon userCoupon = userCouponRepository.findOne(id);
        PaginationRequest requestParams = RequestHandler.buildPaginationRequest(request, model);
        requestParams.getSearchParams().put("EQ_uuid", userCoupon.getUuid());
        PaginationResponse<MtSendLog> list = sendLogService.querySendLogListByPagination(requestParams);
        if (list.getContent().size() > 0) {
            MtSendLog sendLog = list.getContent().get(0);
            if (sendLog.getStatus().equals("A")) {

                Integer total = sendLog.getRemoveSuccessNum();
                if (total==null) {
                    total=0;
                }
                sendLog.setRemoveSuccessNum((total+1));

                sendLog.setStatus("B");
                sendLogRepository.save(sendLog);
            }
        }

        return "redirect:/backend/member/CouponinfoList";
    }


    /**
     * 根据券ID 撤销个人已使用的优惠券 zach 20190912 add
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/coupon/rollbackUserCoupon/{id}")
    @RequestMapping(value = "/rollbackUserCoupon/{id}")
    public String rollbackUserCoupon(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Integer id) throws BusinessCheckException {

        String tempuserCouponId = request.getParameter("userCouponId");
        Integer userCouponId = 0;
        if (tempuserCouponId!=null) {
            userCouponId=Integer.parseInt(tempuserCouponId);
        }

        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        if (null == shiroUser) {
            return "redirect:/login";
        }

        couponService.rollbackUserCoupon(id,userCouponId, shiroUser.getAcctName());

        ReqResult reqResult = new ReqResult();
        reqResult.setResult(true);

        return "redirect:/backend/confirmLog/ConfirmLogQueryList";
    }
}

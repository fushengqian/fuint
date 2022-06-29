package com.fuint.application.web.backend.coupon;

import com.fuint.application.dao.repositories.MtCouponGoodsRepository;
import com.fuint.application.dto.*;
import com.fuint.application.enums.CouponTypeEnum;
import com.fuint.application.service.goods.GoodsService;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.setting.SettingService;
import com.fuint.application.service.sms.SendSmsInterface;
import com.fuint.base.dao.entities.TAccount;
import com.fuint.base.service.account.TAccountService;
import com.fuint.base.shiro.ShiroUser;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.application.dao.repositories.MtCouponGroupRepository;
import com.fuint.exception.BusinessCheckException;
import com.fuint.util.DateUtil;
import com.fuint.application.dao.entities.*;
import com.fuint.application.dao.repositories.MtSendLogRepository;
import com.fuint.application.dao.repositories.MtUserCouponRepository;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.coupongroup.CouponGroupService;
import com.fuint.application.service.store.StoreService;
import com.fuint.application.service.sendlog.SendLogService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.util.RequestHandler;
import com.fuint.application.web.backend.base.BaseController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 卡券管理类controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Controller
@RequestMapping(value = "/backend/coupon")
public class couponController extends BaseController {

    /**
     * 卡券服务接口
     */
    @Autowired
    private CouponService couponService;

    /**
     * 卡券分组服务接口
     * */
    @Autowired
    private CouponGroupService couponGroupService;

    /**
     * 店铺服务接口
     */
    @Autowired
    private StoreService storeService;

    /**
     * 商品服务接口
     * */
    @Autowired
    private GoodsService goodsService;

    @Autowired
    private MtUserCouponRepository userCouponRepository;

    @Autowired
    private MtSendLogRepository sendLogRepository;

    @Autowired
    private SendLogService sendLogService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private SendSmsInterface sendSmsService;

    @Autowired
    private TAccountService accountService;

    @Autowired
    private MtCouponGroupRepository couponGroupRepository;

    @Autowired
    private MtCouponGoodsRepository mtCouponGoodsRepository;

    @Autowired
    private SettingService settingService;

    /**
     * 上次执行搜索全量索引的时间
     */
    private Date lastIndexTime = null;

    /**
     * 卡券列表查询
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     * @return 卡券列表展现页面
     */
    @RequiresPermissions("backend/coupon/index")
    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String EQ_groupId = request.getParameter("EQ_groupId");

        model.addAttribute("EQ_groupId", EQ_groupId);

        if (StringUtils.isNotEmpty(EQ_groupId)) {
            MtCouponGroup group = couponGroupService.queryCouponGroupById(Integer.parseInt(EQ_groupId));
            model.addAttribute("groupName", group.getName());
        }

        if (lastIndexTime != null) {
            long diff = DateUtil.getDiffSeconds(new Date(), lastIndexTime);
            model.addAttribute("isDisable", diff < 30 ? "disable" : "");
        }

        CouponTypeEnum[] typeList = CouponTypeEnum.values();
        model.addAttribute("typeList", typeList);

        return "coupon/index";
    }

    /**
     * 查询卡券列表
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

        paginationRequest.getSearchParams().put("NQ_status", StatusEnum.DISABLE.getKey());
        paginationRequest.setSortColumn(new String[]{"status asc", "createTime asc"});
        PaginationResponse<MtCoupon> paginationResponse = couponService.queryCouponListByPagination(paginationRequest);
        List<MtCoupon> dataList = paginationResponse.getContent();
        List<ContentDto> storeMap = new ArrayList<>();
        List<MtCouponGroup> groupMap = new ArrayList<>();

        if (dataList.size() > 0) {
            for (MtCoupon coupon : dataList) {
                MtCouponGroup groupInfo = couponGroupRepository.findOne(coupon.getGroupId());
                if (groupInfo == null) {
                    continue;
                }

                MtCouponGroup g = new MtCouponGroup();
                g.setId(groupInfo.getId());
                g.setName(groupInfo.getName());
                g.setTotal(groupInfo.getTotal());

                Boolean isInGroup = false;
                for (MtCouponGroup gg : groupMap) {
                    if (gg.getId().equals(groupInfo.getId())) {
                        isInGroup = true;
                    }
                }
                if (!isInGroup) {
                    groupMap.add(g);
                }

                // 可用店铺
                String storeName = "";
                String storeIds = coupon.getStoreIds();
                if (StringUtils.isNotEmpty(storeIds)) {
                    String[] list = storeIds.split(",");
                    if (list.length > 0) {
                        for (String id : list) {
                            MtStore store = storeService.queryStoreById(Integer.parseInt(id));
                            if (store != null) {
                                storeName = storeName.length() > 0 ? storeName + ',' + store.getName() : store.getName();
                            }
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
        Integer groupTotal = 0;
        if (StringUtils.isNotEmpty(groupId)) {
            MtCouponGroup groupInfo = couponGroupService.queryCouponGroupById(Integer.parseInt(groupId));
            groupTotal = groupInfo.getTotal();
        }

        String baseImage = settingService.getUploadBasePath();

        model.addAttribute("baseImage", baseImage);
        model.addAttribute("groupTotal", groupTotal);
        model.addAttribute("storeMap", storeMap);
        model.addAttribute("groupMap", groupMap);

        return "coupon/list";
    }

    /**
     * 删除卡券
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

        // 卡券分组
        if (StringUtils.isNotEmpty(groupId)) {
            MtCouponGroup mtCouponInfo = couponGroupService.queryCouponGroupById(Integer.parseInt(groupId));
            model.addAttribute("groupInfo", mtCouponInfo);
        }

        model.addAttribute("isEdit", false);

        return "coupon/form";
    }

    /**
     * 保存卡券
     *
     * @param request  HttpServletRequest对象
     * @param model    SpringFramework Model对象
     */
    @RequiresPermissions("backend/coupon/save")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveCouponHandler(HttpServletRequest request, Model model, ReqCouponDto reqCouponDto) throws BusinessCheckException {
        String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
        reqCouponDto.setOperator(operator);

        PaginationRequest requestName = RequestHandler.buildPaginationRequest(request, model);
        requestName.getSearchParams().put("EQ_name", reqCouponDto.getName());
        requestName.getSearchParams().put("EQ_groupId", reqCouponDto.getGroupId().toString());
        PaginationResponse<MtCoupon> dataName = couponService.queryCouponListByPagination(requestName);

        if (dataName.getContent().size() > 0 && reqCouponDto.getId() == null) {
            throw new BusinessCheckException("券名称已存在，请修改");
        }

        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        TAccount account = accountService.findAccountById(shiroUser.getId());
        Integer storeId = account.getStoreId();
        if (storeId > 0) {
            reqCouponDto.setStoreIds(storeId.toString());
        }

        couponService.saveCoupon(reqCouponDto);

        return "redirect:/backend/coupon/index";
    }

    /**
     * 编辑卡券初始化页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/coupon/couponEditInit")
    @RequestMapping(value = "/couponEditInit/{id}")
    public String couponEditInit(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Integer id) throws BusinessCheckException {
        MtCoupon mtCouponInfo = couponService.queryCouponById(id);

        String baseImage = settingService.getUploadBasePath();
        model.addAttribute("baseImage", baseImage);
        model.addAttribute("couponInfo", mtCouponInfo);

        MtCouponGroup mtGroupInfo = couponGroupService.queryCouponGroupById(mtCouponInfo.getGroupId());
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

        // 卡券适用商品
        List<MtCouponGoods> couponGoodsList = mtCouponGoodsRepository.getCouponGoods(id);
        String goodsIds = "";
        List<MtGoods> goodsList = new ArrayList<>();
        if (couponGoodsList.size() > 0) {
            for (MtCouponGoods cg : couponGoodsList) {
                if (goodsIds.length() > 0) {
                    goodsIds = goodsIds + "," + cg.getGoodsId();
                } else {
                    goodsIds = cg.getGoodsId().toString();
                }
                MtGoods goodsInfo = goodsService.queryGoodsById(cg.getGoodsId());
                goodsList.add(goodsInfo);
            }
        }
        model.addAttribute("goodsIds", goodsIds);
        model.addAttribute("goodsList", goodsList);

        // 不可用日期
        List<DateDto> exceptTimeList = new ArrayList<>();
        if (StringUtils.isNotEmpty(mtCouponInfo.getExceptTime())) {
            String[] exceptTimeArr = mtCouponInfo.getExceptTime().split(",");
            if (exceptTimeArr.length > 0) {
                for (int i = 0; i < exceptTimeArr.length; i++) {
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

        // 预存卡的预存规则
        List<PreStoreRuleDto> preStoreList = new ArrayList<>();
        if (StringUtils.isNotEmpty(mtCouponInfo.getInRule()) && mtCouponInfo.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
            String[] ruleArr = mtCouponInfo.getInRule().split(",");
            if (ruleArr.length > 0) {
                for (int i = 0; i < ruleArr.length; i++) {
                     if (StringUtils.isNotEmpty(ruleArr[i])) {
                         String[] ruleItem = ruleArr[i].split("_");
                         if (ruleItem.length == 2) {
                             PreStoreRuleDto dto = new PreStoreRuleDto();
                             dto.setPreStoreAmount(ruleItem[0]);
                             dto.setTargetAmount(ruleItem[1]);
                             preStoreList.add(dto);
                         }
                     }
                }
            }
        }

        model.addAttribute("exceptTimeList", exceptTimeList);
        model.addAttribute("preStoreList", preStoreList);
        model.addAttribute("isEdit", true);

        return "coupon/form";
    }

    /**
     * 查询店铺页面
     * */
    @RequiresPermissions("backend/coupon/searchStore")
    @RequestMapping(value = "/searchStore")
    public String searchStore(HttpServletRequest request) throws BusinessCheckException {
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
     * 根据券ID 删除个人卡券
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
        if (null == shiroUser) {
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
                if (null == total) {
                    total = 0;
                }
                sendLog.setRemoveSuccessNum((total+1));
                sendLog.setStatus("B");
                sendLogRepository.save(sendLog);
            }
        }

        return "redirect:/backend/userCoupon/list";
    }

    /**
     * 根据券ID 撤销个人已使用的卡券
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
        if (tempuserCouponId != null) {
            userCouponId = Integer.parseInt(tempuserCouponId);
        }

        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        if (null == shiroUser) {
            return "redirect:/login";
        }

        couponService.rollbackUserCoupon(id,userCouponId, shiroUser.getAcctName());

        ReqResult reqResult = new ReqResult();
        reqResult.setResult(true);

        return "redirect:/backend/confirmLog/confirmLogList";
    }

    /**
     * 发放卡券
     *
     * @param request
     * @return
     */
    @RequiresPermissions("backend/coupon/sendCoupon")
    @RequestMapping(value = "/sendCoupon")
    @ResponseBody
    public ReqResult sendCoupon(HttpServletRequest request) throws BusinessCheckException {
        String mobile = request.getParameter("mobile");
        String num = request.getParameter("num");
        String couponId = request.getParameter("couponId");

        ReqResult reqResult = new ReqResult();
        if (couponId == null) {
            reqResult.setResult(false);
            reqResult.setMsg("系统参数有误！");
            return reqResult;
        }

        if (mobile.length() < 11 || mobile.length() > 11) {
            reqResult.setResult(false);
            reqResult.setMsg("手机号格式有误！");
            return reqResult;
        }

        Pattern pattern = Pattern.compile("[0-9]*");
        if (num == null || (!pattern.matcher(num).matches())) {
            reqResult.setResult(false);
            reqResult.setMsg("发放套数必须为正整数！");
            return reqResult;
        }

        // 导入批次
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        try {
            couponService.sendCoupon(Integer.parseInt(couponId), mobile, Integer.parseInt(num), uuid);
        } catch (BusinessCheckException e) {
            reqResult.setResult(false);
            reqResult.setMsg(e.getMessage());
            return reqResult;
        }

        MtCoupon couponInfo = couponService.queryCouponById(Integer.parseInt(couponId));
        MtUser mtUser = memberService.queryMemberByMobile(mobile);
        MtCouponGroup mtCouponGroup = couponGroupService.queryCouponGroupById(couponInfo.getGroupId());

        // 发放记录
        ReqSendLogDto dto = new ReqSendLogDto();
        dto.setType(1);
        dto.setMobile(mobile);
        dto.setUserId(mtUser.getId());
        dto.setFileName("");
        dto.setGroupId(couponInfo.getGroupId());
        dto.setGroupName(mtCouponGroup.getName());
        dto.setCouponId(couponInfo.getId());
        dto.setSendNum(Integer.parseInt(num));
        String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
        dto.setOperator(operator);
        dto.setUuid(uuid);
        sendLogService.addSendLog(dto);

        // 发送短信
        try {
            List<String> mobileList = new ArrayList<>();
            mobileList.add(mobile);

            Integer totalNum = 0;
            BigDecimal totalMoney = new BigDecimal("0.0");

            List<MtCoupon> couponList = couponService.queryCouponListByGroupId(couponInfo.getGroupId());
            for (MtCoupon coupon : couponList) {
                totalNum = totalNum + (coupon.getSendNum()*Integer.parseInt(num));
                totalMoney = totalMoney.add((coupon.getAmount().multiply(new BigDecimal(num).multiply(new BigDecimal(coupon.getSendNum())))));
            }

            Map<String, String> params = new HashMap<>();
            params.put("totalNum", totalNum+"");
            params.put("totalMoney", totalMoney+"");
            sendSmsService.sendSms("received-coupon", mobileList, params);
        } catch (Exception e) {
            //empty
        }

        reqResult.setResult(true);
        return reqResult;
    }
}

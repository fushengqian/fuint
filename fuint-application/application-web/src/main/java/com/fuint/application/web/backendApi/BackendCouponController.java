package com.fuint.application.web.backendApi;

import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;
import com.fuint.application.config.Constants;
import com.fuint.application.dao.repositories.MtCouponGoodsRepository;
import com.fuint.application.dto.*;
import com.fuint.application.enums.CouponTypeEnum;
import com.fuint.application.service.goods.GoodsService;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.setting.SettingService;
import com.fuint.application.service.sms.SendSmsInterface;
import com.fuint.application.service.token.TokenService;
import com.fuint.base.dao.entities.TAccount;
import com.fuint.base.service.account.TAccountService;
import com.fuint.application.dao.repositories.MtCouponGroupRepository;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.*;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.coupongroup.CouponGroupService;
import com.fuint.application.service.store.StoreService;
import com.fuint.application.service.sendlog.SendLogService;
import com.fuint.util.StringUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 卡券管理类controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/backendApi/coupon")
public class BackendCouponController extends BaseController {

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
    private SendLogService sendLogService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private SendSmsInterface sendSmsService;

    @Autowired
    private TAccountService accountService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private MtCouponGroupRepository couponGroupRepository;

    @Autowired
    private MtCouponGoodsRepository mtCouponGoodsRepository;

    @Autowired
    private SettingService settingService;

    /**
     * 查询卡券列表
     *
     * @param request
     * @return
     * @throws BusinessCheckException
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        Integer groupId = request.getParameter("groupId") == null ? 0 : Integer.parseInt(request.getParameter("groupId"));

        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        params.put("NQ_status", StatusEnum.DISABLE.getKey());
        paginationRequest.setSearchParams(params);
        paginationRequest.setSortColumn(new String[]{"status asc", "createTime desc"});
        PaginationResponse<MtCoupon> paginationResponse = couponService.queryCouponListByPagination(paginationRequest);
        List<MtCoupon> dataList = paginationResponse.getContent();
        List<ContentDto> storeList = new ArrayList<>();
        List<MtCouponGroup> groupList = new ArrayList<>();

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
                for (MtCouponGroup gg : groupList) {
                    if (gg.getId().equals(groupInfo.getId())) {
                        isInGroup = true;
                    }
                }
                if (!isInGroup) {
                    groupList.add(g);
                }

                // 可用店铺
                String storeName = "";
                String storeIds = coupon.getStoreIds();
                if (StringUtil.isNotEmpty(storeIds)) {
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
                if (storeList.size() > 0) {
                    for (ContentDto oo : storeList) {
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
                    if (StringUtil.isNotEmpty(h.getKey())) {
                        storeList.add(h);
                    }
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);

        Integer groupTotal = 0;
        if (groupId > 0) {
            MtCouponGroup groupInfo = couponGroupService.queryCouponGroupById(groupId);
            groupTotal = groupInfo.getTotal();
        }

        // 卡券类型列表
        CouponTypeEnum[] typeListEnum = CouponTypeEnum.values();
        List<ParamDto> typeList = new ArrayList<>();
        for (CouponTypeEnum enumItem : typeListEnum) {
            ParamDto paramDto = new ParamDto();
            paramDto.setKey(enumItem.getKey());
            paramDto.setName(enumItem.getValue());
            paramDto.setValue(enumItem.getKey());
            typeList.add(paramDto);
        }

        // 状态列表
        StatusEnum[] statusListEnum = StatusEnum.values();
        List<ParamDto> statusList = new ArrayList<>();
        for (StatusEnum enumItem : statusListEnum) {
            ParamDto paramDto = new ParamDto();
            paramDto.setKey(enumItem.getKey());
            paramDto.setName(enumItem.getValue());
            paramDto.setValue(enumItem.getKey());
            statusList.add(paramDto);
        }

        String imagePath = settingService.getUploadBasePath();

        result.put("imagePath", imagePath);
        result.put("groupTotal", groupTotal);
        result.put("storeList", storeList);
        result.put("groupList", groupList);
        result.put("typeList", typeList);
        result.put("statusList", statusList);
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }

    /**
     * 删除卡券
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject delete(HttpServletRequest request, @PathVariable("id") Long id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
        }

        List<Long> ids = new ArrayList<>();
        ids.add(id);

        String operator = accountInfo.getAccountName();
        couponService.deleteCoupon(id, operator);

        return getSuccessResult(true);
    }

    /**
     * 保存卡券
     *
     * @param request  HttpServletRequest对象
     * @return
     */
    @RequiresPermissions("backend/coupon/save")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseObject saveCouponHandler(HttpServletRequest request, @RequestBody ReqCouponDto reqCouponDto) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
        }

        reqCouponDto.setOperator(accountInfo.getAccountName());


        PaginationRequest requestName = new PaginationRequest();
        Map<String, Object> requestParams = new HashMap<>();

        requestParams.put("EQ_name", reqCouponDto.getName());
        requestParams.put("EQ_groupId", reqCouponDto.getGroupId().toString());
        requestName.setSearchParams(requestParams);
        PaginationResponse<MtCoupon> dataName = couponService.queryCouponListByPagination(requestName);

        if (dataName.getContent().size() > 0 && reqCouponDto.getId() == null) {
            return getFailureResult(201, "卡券名称已存在，请修改");
        }

        TAccount account = accountService.findAccountById(accountInfo.getId());
        Integer storeId = account.getStoreId();
        if (storeId > 0) {
            reqCouponDto.setStoreIds(storeId.toString());
        }

        try {
            couponService.saveCoupon(reqCouponDto);
        } catch (BusinessCheckException e) {
            return getFailureResult(201, e.getMessage());
        }

        return getSuccessResult(true);
    }

    /**
     * 卡券详情
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
        }

        MtCoupon mtCouponInfo = couponService.queryCouponById(id);

        String baseImage = settingService.getUploadBasePath();

        Map<String, Object> result = new HashMap<>();
        result.put("baseImage", baseImage);
        result.put("couponInfo", mtCouponInfo);

        MtCouponGroup mtGroupInfo = couponGroupService.queryCouponGroupById(mtCouponInfo.getGroupId());
        result.put("groupInfo", mtGroupInfo);

        List<MtStore> storeList = new ArrayList<>();

        if (StringUtil.isNotEmpty(mtCouponInfo.getStoreIds())) {
            String[] ids = mtCouponInfo.getStoreIds().split(",");
            for (String storeId : ids) {
                MtStore info = storeService.queryStoreById(Integer.parseInt(storeId));
                storeList.add(info);
            }
        }

        result.put("storeList", storeList);

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
        result.put("goodsIds", goodsIds);
        result.put("goodsList", goodsList);

        // 不可用日期
        List<DateDto> exceptTimeList = new ArrayList<>();
        if (StringUtil.isNotEmpty(mtCouponInfo.getExceptTime())) {
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
        if (StringUtil.isNotEmpty(mtCouponInfo.getInRule()) && mtCouponInfo.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
            String[] ruleArr = mtCouponInfo.getInRule().split(",");
            if (ruleArr.length > 0) {
                for (int i = 0; i < ruleArr.length; i++) {
                     if (StringUtil.isNotEmpty(ruleArr[i])) {
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

        result.put("exceptTimeList", exceptTimeList);
        result.put("preStoreList", preStoreList);
        result.put("isEdit", true);

        return getSuccessResult(result);
    }

    /**
     * 发放卡券
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/sendCoupon", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject sendCoupon(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String mobile = request.getParameter("mobile");
        String num = request.getParameter("num");
        String couponId = request.getParameter("couponId");

        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
        }

        if (couponId == null) {
            return getFailureResult(201, "系统参数有误");
        }

        if (mobile.length() < 11 || mobile.length() > 11) {
            return getFailureResult(401, "手机号格式有误");
        }

        Pattern pattern = Pattern.compile("[0-9]*");
        if (num == null || (!pattern.matcher(num).matches())) {
            return getFailureResult(401, "发放套数必须为正整数");
        }

        // 导入批次
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        try {
            couponService.sendCoupon(Integer.parseInt(couponId), mobile, Integer.parseInt(num), uuid, accountInfo.getAccountName());
        } catch (BusinessCheckException e) {
            return getFailureResult(201, e.getMessage());
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
        String operator = accountInfo.getAccountName();
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

        return getSuccessResult(true);
    }
}

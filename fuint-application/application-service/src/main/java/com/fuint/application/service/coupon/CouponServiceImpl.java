package com.fuint.application.service.coupon;

import com.fuint.application.dto.CouponDto;
import com.fuint.application.service.confirmlog.ConfirmLogService;
import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.shiro.ShiroUser;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.application.enums.CouponTypeEnum;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.*;
import com.fuint.application.dao.repositories.*;
import com.fuint.application.dto.MyCouponDto;
import com.fuint.application.dto.ReqCouponDto;
import com.fuint.application.config.Constants;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.enums.UserCouponStatusEnum;
import com.fuint.application.enums.SendWayEnum;
import com.fuint.application.service.coupongroup.CouponGroupService;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.ResponseObject;
import com.fuint.application.service.sendlog.SendLogService;
import com.fuint.application.service.sms.SendSmsInterface;
import com.fuint.application.util.CommonUtil;
import com.fuint.application.util.DateUtil;
import com.fuint.application.util.SeqUtil;
import org.apache.commons.collections.map.HashedMap;
import com.fuint.application.BaseService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fuint.application.dto.ResMyCouponDto;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 卡券业务实现类
 * Created by zach on 2020/08/06.
 * Updated by zach on 2021/04/23.
 */
@Service
public class CouponServiceImpl extends BaseService implements CouponService {
    private static final Logger log = LoggerFactory.getLogger(CouponServiceImpl.class);

    @Autowired
    private MtCouponRepository couponRepository;

    @Autowired
    private CouponGroupService couponGroupService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private SendLogService sendLogService;

    @Autowired
    private ConfirmLogService confirmLogService;

    @Autowired
    private SendSmsInterface sendSmsService;

    @Autowired
    private MtUserCouponRepository userCouponRepository;

    @Autowired
    private MtConfirmLogRepository confirmLogRepository;

    @Autowired
    private MtSendLogRepository sendLogRepository;

    @Autowired
    private MtStoreRepository mtStoreRepository;

    /**
     * 分页查询券列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtCoupon> queryCouponListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException {
        paginationRequest.setSortColumn(new String[]{"status asc", "id desc"});
        PaginationResponse<MtCoupon> paginationResponse = couponRepository.findResultsByPagination(paginationRequest);
        return paginationResponse;
    }

    /**
     * 新增卡券
     *
     * @param reqCouponDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "新增卡券")
    public MtCoupon addCoupon(ReqCouponDto reqCouponDto) throws BusinessCheckException {
        MtCoupon coupon = new MtCoupon();

        Date startTime = reqCouponDto.getBeginTime();
        Date endTime = reqCouponDto.getEndTime();
        if (endTime.before(startTime)) {
            throw new BusinessCheckException("生效期结束时间不能早于开始时间");
        }

        coupon.setGroupId(reqCouponDto.getGroupId());
        coupon.setType(reqCouponDto.getType());
        coupon.setName(CommonUtil.replaceXSS(reqCouponDto.getName()));
        coupon.setStoreIds(CommonUtil.replaceXSS(reqCouponDto.getStoreIds()));
        if (null == reqCouponDto.getSendNum()) {
            reqCouponDto.setSendNum(1);
        }
        coupon.setSendWay(reqCouponDto.getSendWay());
        coupon.setSendNum(reqCouponDto.getSendNum());
        coupon.setBeginTime(reqCouponDto.getBeginTime());
        coupon.setEndTime(reqCouponDto.getEndTime());
        coupon.setExceptTime(CommonUtil.replaceXSS(reqCouponDto.getExceptTime()));
        coupon.setDescription(CommonUtil.replaceXSS(reqCouponDto.getDescription()));
        coupon.setRemarks(CommonUtil.replaceXSS(reqCouponDto.getRemarks()));
        coupon.setInRule(CommonUtil.replaceXSS(reqCouponDto.getInRule()));
        coupon.setOutRule(CommonUtil.replaceXSS(reqCouponDto.getOutRule()));
        if (null == reqCouponDto.getAmount()) {
            reqCouponDto.setAmount(new BigDecimal(0));
        }
        coupon.setAmount(reqCouponDto.getAmount());
        String image = reqCouponDto.getImage();
        if (null == image || image.equals("")) {
            image = "";
        }

        coupon.setImage(image);
        coupon.setRemarks(CommonUtil.replaceXSS(reqCouponDto.getRemarks()));

        coupon.setStatus("A");
        //创建时间
        coupon.setCreateTime(new Date());

        //更新时间
        coupon.setUpdateTime(new Date());

        //操作人
        coupon.setOperator(reqCouponDto.getOperator());

        MtCoupon couponInfo = couponRepository.save(coupon);

        // 如果是优惠券，并且是线下发放，生成卡券
        if (coupon.getType().equals(CouponTypeEnum.COUPON.getKey()) && coupon.getSendWay().equals(SendWayEnum.OFFLINE.getKey())) {
            MtCouponGroup groupInfo = couponGroupService.queryCouponGroupById(coupon.getGroupId().longValue());

            Integer total = groupInfo.getTotal() * coupon.getSendNum();
            if (total > 0) {
                String uuid = UUID.randomUUID().toString().replaceAll("-", "");

                for (int i = 1; i <= total; i++) {
                    MtUserCoupon userCoupon = new MtUserCoupon();
                    userCoupon.setCouponId(couponInfo.getId());
                    userCoupon.setGroupId(coupon.getGroupId());
                    userCoupon.setMobile("");
                    userCoupon.setUserId(0);
                    userCoupon.setStatus("E");
                    userCoupon.setCreateTime(new Date());
                    userCoupon.setUpdateTime(new Date());
                    userCoupon.setUuid(uuid);

                    // 12位随机数
                    StringBuffer code = new StringBuffer();
                    code.append(SeqUtil.getRandomNumber(4));
                    code.append(SeqUtil.getRandomNumber(4));
                    code.append(SeqUtil.getRandomNumber(4));
                    code.append(SeqUtil.getRandomNumber(4));
                    userCoupon.setCode(code.toString());

                    userCouponRepository.save(userCoupon);
                }
            }
        }

        return coupon;
    }

    /**
     * 根据ID获取券信息
     *
     * @param id 券ID
     * @throws BusinessCheckException
     */
    @Override
    public MtCoupon queryCouponById(Long id) throws BusinessCheckException {
        return couponRepository.findOne(id.intValue());
    }

    /**
     * 根据活动组ID 删除活动组信息
     *
     * @param id       券ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除卡券")
    public void deleteCoupon(Long id, String operator) throws BusinessCheckException {
        MtCoupon coupon = this.queryCouponById(id);
        if (null == coupon) {
            return;
        }
        coupon.setStatus("D");
        //修改时间
        coupon.setUpdateTime(new Date());
        //操作人
        coupon.setOperator(operator);
        couponRepository.save(coupon);
    }

    /**
     * 修改卡券
     *
     * @param reqCouponDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "修改卡券")
    public MtCoupon updateCoupon(ReqCouponDto reqCouponDto) throws BusinessCheckException {
        MtCoupon coupon = this.queryCouponById(reqCouponDto.getId());

        if (null == coupon) {
            throw new BusinessCheckException("该卡券不存在！");
        }

        coupon.setGroupId(reqCouponDto.getGroupId());
        coupon.setName(CommonUtil.replaceXSS(reqCouponDto.getName()));
        coupon.setAmount(reqCouponDto.getAmount());
        coupon.setSendWay(reqCouponDto.getSendWay());
        coupon.setSendNum(reqCouponDto.getSendNum());
        coupon.setDescription(CommonUtil.replaceXSS(reqCouponDto.getDescription()));
        coupon.setImage(reqCouponDto.getImage());
        coupon.setRemarks(CommonUtil.replaceXSS(reqCouponDto.getRemarks()));

        coupon.setStatus("A");

        //更新时间
        coupon.setUpdateTime(new Date());

        //操作人
        coupon.setOperator(reqCouponDto.getOperator());

        couponRepository.save(coupon);

        return coupon;
    }

    /**
     * 获取我的卡券列表
     * @param paramMap
     * @throws BusinessCheckException
     * */
    @Override
    @Transactional
    public ResponseObject findMyCouponList(Map<String, Object> paramMap) throws BusinessCheckException {
        Integer pageNumber = paramMap.get("pageNumber") == null ? Constants.PAGE_NUMBER : Integer.parseInt(paramMap.get("pageNumber").toString());
        Integer pageSize = paramMap.get("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(paramMap.get("pageSize").toString());
        String userId = paramMap.get("userId") == null ? "0" : paramMap.get("userId").toString();
        String status =  paramMap.get("status") == null ? "A": paramMap.get("status").toString();
        String type =  paramMap.get("type") == null ? "": paramMap.get("type").toString();

        // 处理已过期，置为过期
        if (pageNumber <= 1) {
            List<String> statusList = Arrays.asList("A");
            List<MtUserCoupon> data = userCouponRepository.getUserCouponList(Integer.parseInt(userId), statusList);
            for (MtUserCoupon uc : data) {
                MtCoupon coupon = this.queryCouponById(uc.getCouponId().longValue());
                if (coupon.getEndTime().before(new Date())) {
                    uc.setStatus(StatusEnum.EXPIRED.getKey());
                    uc.setUpdateTime(new Date());
                    userCouponRepository.save(uc);
                }
            }
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(pageNumber);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> searchParams = new HashedMap();
        searchParams.put("EQ_status", status);
        searchParams.put("EQ_userId", userId);
        if (StringUtils.isNotEmpty(type)) {
            searchParams.put("EQ_type", type);
        }

        paginationRequest.setSearchParams(searchParams);
        paginationRequest.setSortColumn(new String[]{"id desc", "groupId desc"});

        PaginationResponse<MtUserCoupon> paginationResponse = userCouponRepository.findResultsByPagination(paginationRequest);

        List<MyCouponDto> dataList = new ArrayList<>();

        if (paginationResponse.getContent().size() > 0) {
            for (MtUserCoupon userCouponDto : paginationResponse.getContent()) {
                 MtCoupon couponInfo = this.queryCouponById(userCouponDto.getCouponId().longValue());

                 MyCouponDto dto = new MyCouponDto();
                 dto.setId(userCouponDto.getId());
                 dto.setName(couponInfo.getName());
                 dto.setCouponId(couponInfo.getId());
                 dto.setUseRule(couponInfo.getDescription());

                 String image = couponInfo.getImage();
                 if (null == image || image.equals("")) {
                    image = "/static/default-coupon.jpg";
                 }

                 dto.setImage(image);
                 dto.setStatus(userCouponDto.getStatus());
                 dto.setAmount(couponInfo.getAmount());
                 dto.setType(couponInfo.getType());

                 boolean canUse = this.isCouponEffective(couponInfo);
                 dto.setCanUse(canUse);

                 String effectiveDate = DateUtil.formatDate(couponInfo.getBeginTime(), "yyyy.MM.dd") + "-" + DateUtil.formatDate(couponInfo.getEndTime(), "yyyy.MM.dd");
                 dto.setEffectiveDate(effectiveDate);

                 String tips = "";

                 // 优惠券tips
                 if (couponInfo.getType().equals(CouponTypeEnum.COUPON.getKey())) {
                    if (Integer.parseInt(couponInfo.getOutRule()) > 0) {
                        tips = "满" + couponInfo.getOutRule() + "可用";
                    } else {
                        tips = "无门槛券";
                    }
                 }

                 // 预存券tips
                 if (couponInfo.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
                     tips = "￥" + userCouponDto .getAmount() + "，余额￥" + userCouponDto.getBalance();
                 }

                  // 集次卡tips
                  if (couponInfo.getType().equals(CouponTypeEnum.TIMER.getKey())) {
                      Integer confirmNum = confirmLogService.getConfirmNum(userCouponDto.getId());
                      tips = "已集"+ confirmNum +"次，需集满" + couponInfo.getOutRule() + "次";
                  }

                  dto.setTips(tips);

                  dataList.add(dto);
            }
        }

        ResMyCouponDto myCouponResponse = new ResMyCouponDto();
        myCouponResponse.setPageNumber(pageNumber);
        myCouponResponse.setPageSize(pageSize);
        myCouponResponse.getTotalRow(paginationResponse.getTotalElements());
        myCouponResponse.setTotalPage(paginationResponse.getTotalPages());
        myCouponResponse.setContent(dataList);

        return getSuccessResult(myCouponResponse);
    }

    /**
     * 获取卡券列表
     * @param paramMap
     * */
    @Override
    @Transactional
    public ResponseObject findCouponList(Map<String, Object> paramMap) {
        Integer pageNumber = paramMap.get("pageNumber") == null ? Constants.PAGE_NUMBER : Integer.parseInt(paramMap.get("pageNumber").toString());
        Integer pageSize = paramMap.get("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(paramMap.get("pageSize").toString());
        String status =  paramMap.get("status") == null ? StatusEnum.ENABLED.getKey(): paramMap.get("status").toString();
        String type =  paramMap.get("type") == null ? "" : paramMap.get("type").toString();
        Integer userId =  paramMap.get("userId") == null ? 0 : Integer.parseInt(paramMap.get("userId").toString());

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(pageNumber);
        paginationRequest.setPageSize(pageSize);
        Map<String, Object> searchParams = new HashedMap();

        searchParams.put("EQ_status", status);
        if (StringUtils.isNotEmpty(type)) {
            searchParams.put("EQ_type", type);
        }
        paginationRequest.setSearchParams(searchParams);
        paginationRequest.setSortColumn(new String[]{"id desc", "groupId desc"});

        PaginationResponse<MtCoupon> paginationResponse = couponRepository.findResultsByPagination(paginationRequest);

        List<MtCoupon> dataList = paginationResponse.getContent();
        List<CouponDto> content = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            CouponDto item = new CouponDto();
            BeanUtils.copyProperties(dataList.get(i), item);

            // 是否领取，且领取量大于限制数
            List<String> statusList = Arrays.asList("A", "B", "C");
            List<MtUserCoupon> userCoupon = userCouponRepository.getUserCouponListByCouponId(userId, item.getId(), statusList);
            if (userCoupon.size() >= dataList.get(i).getLimitNum()) {
                item.setIsReceive(true);
            }

            List<Object[]> numData = userCouponRepository.getPeopleNumByCouponId(item.getId());
            Long num;
            if (null == numData || numData.size() < 1) {
                num = 0l;
            } else {
                Object[] obj = numData.get(0);
                num = (Long) obj[1];
            }
            item.setGotNum(num.intValue());

            String sellingPoint = "";

            // 优惠券卖点
            if (item.getType().equals(CouponTypeEnum.COUPON.getKey())) {
                if (Integer.parseInt(item.getOutRule()) > 0) {
                    sellingPoint = "满" + item.getOutRule() + "可用";
                } else {
                    sellingPoint = "无门槛券";
                }
            }

            // 预存券卖点
            if (item.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
                String inRuleArr[] = item.getInRule().split(",");
                if (inRuleArr.length > 0) {
                    for (int n = 0; n < inRuleArr.length; n++) {
                        String store[] = inRuleArr[n].split("_");
                        sellingPoint = "预存" + store[0] + "赠" + store[1];
                    }
                }
            }

            // 集次卡卖点
            if (item.getType().equals(CouponTypeEnum.TIMER.getKey())) {
                sellingPoint = "集满" + item.getOutRule() + "次即可";
            }

            item.setSellingPoint(sellingPoint);

            content.add(item);
        }

        PageRequest pageRequest = new PageRequest(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        Page page = new PageImpl(content, pageRequest, paginationResponse.getTotalElements());
        PaginationResponse<CouponDto> result = new PaginationResponse(page, CouponDto.class);
        result.setContent(content);

        return getSuccessResult(result);
    }

    /**
     * 根据分组获取卡券列表
     * @param groupId 查询参数
     * @throws BusinessCheckException
     * */
    public List<MtCoupon> queryCouponListByGroupId(Long groupId) throws BusinessCheckException {
        List<MtCoupon> couponList = couponRepository.queryByGroupId(groupId.intValue());
        return couponList;
    }

    /**
     * 发放卡券
     *
     * @param groupId 券组ID
     * @param mobile  操作人
     * @param num     发放套数
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "发放卡券")
    public void sendCoupon(Long groupId, String mobile, Integer num, String uuid) throws BusinessCheckException {
        MtCouponGroup groupInfo = couponGroupService.queryCouponGroupById(groupId);

        MtUser mtUser = memberService.queryMemberByMobile(mobile);

        if (null == groupInfo) {
            throw new BusinessCheckException("该分组为空，请增加卡券");
        }

        if (null == mtUser || !mtUser.getStatus().equals("A")) {
            throw new BusinessCheckException("该手机号码不存在或已禁用，请先注册会员");
        }

        Integer sendedNum = couponGroupService.getSendedNum(groupId.intValue());
        if (sendedNum >= groupInfo.getTotal() || (num - (groupInfo.getTotal() - sendedNum) > 0)) {
            throw new BusinessCheckException("分组ID"+groupId+"数量不足，请增加发行量");
        }

        // 发放num套
        for (int k = 1; k <= num; k++) {
            List<MtCoupon> couponList = this.queryCouponListByGroupId(groupId);
            if (couponList != null && couponList.size() > 0) {
                for (int i = 0; i < couponList.size(); i++) {
                    MtCoupon coupon = couponList.get(i);

                    // 券是否有效
                    if (!coupon.getStatus().equals("A")) {
                        continue;
                    }

                    for (int j = 1; j <= coupon.getSendNum(); j++) {
                        MtUserCoupon userCoupon = new MtUserCoupon();
                        userCoupon.setCouponId(coupon.getId());
                        userCoupon.setGroupId(groupInfo.getId());
                        userCoupon.setMobile(mobile);
                        userCoupon.setUserId(mtUser.getId());
                        userCoupon.setStatus("A");
                        userCoupon.setCreateTime(new Date());
                        userCoupon.setUpdateTime(new Date());

                        // 12位随机数
                        StringBuffer code = new StringBuffer();
                        code.append(SeqUtil.getRandomNumber(4));
                        code.append(SeqUtil.getRandomNumber(4));
                        code.append(SeqUtil.getRandomNumber(4));
                        code.append(SeqUtil.getRandomNumber(4));
                        userCoupon.setCode(code.toString());
                        userCoupon.setUuid(uuid);

                        userCouponRepository.save(userCoupon);
                    }
                }
            } else {
                throw new BusinessCheckException("分组ID"+groupId+"数量不足，请增加发行量");
            }
        }

        return;
    }

    /**
     * 发放卡券
     *
     * @param userCouponId 用户券ID
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "核销卡券")
    public String useCoupon(Long userCouponId, Integer userId, Integer storeId) throws BusinessCheckException {
        MtUserCoupon userCoupon = userCouponRepository.findOne(userCouponId.intValue());

        if (null == userCoupon) {
            throw new BusinessCheckException("该卡券不存在！");
        } else if (!userCoupon.getStatus().equals("A")) {
            throw new BusinessCheckException("该卡券状态有误，可能已使用或过期");
        }

        MtStore mtStore = mtStoreRepository.findOne(storeId);
        if (null == mtStore) {
            throw new BusinessCheckException("该店铺不存在！");
        } else if (!mtStore.getStatus().equals(StatusEnum.ENABLED.getKey())) {
            throw new BusinessCheckException("该店铺状态有误，可能已禁用");
        }

        // 是否处于有效期
        MtCoupon couponInfo = this.queryCouponById(userCoupon.getCouponId().longValue());
        Date begin = couponInfo.getBeginTime();
        Date end = couponInfo.getEndTime();
        Date now = new Date();
        if (now.before(begin)) {
            throw new BusinessCheckException("该卡券还没到使用日期");
        }
        if (end.before(now)) {
            throw new BusinessCheckException("该卡券已过期");
        }

        // 是否在例外日期
        Calendar cal = Calendar.getInstance();
        Boolean isWeekend = false;
        if (cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) {
            isWeekend = true;
        }

        String exceptTime = couponInfo.getExceptTime();
        if (null != exceptTime && !exceptTime.equals("")) {
            String[] exceptTimeList = exceptTime.split(",");
            if (exceptTimeList.length > 0) {
                for (String timeStr : exceptTimeList) {
                     if (timeStr.equals("weekend")) {
                         if (isWeekend) {
                             throw new BusinessCheckException("该卡券在当前日期不可用");
                         }
                     } else {
                         String[] timeItem = exceptTime.split("_");
                         if (timeItem.length == 2) {
                             try {
                                 Date startTime = DateUtil.parseDate(timeItem[0].toString(), "yyyy-MM-dd HH:mm");
                                 Date endTime = DateUtil.parseDate(timeItem[1].toString(), "yyyy-MM-dd HH:mm");
                                 // 2019-09-18 17:00_2019-09-19 04:00
                                 if (now.before(endTime) && now.after(startTime)) {
                                     throw new BusinessCheckException("该卡券在当前日期不可用");
                                 }
                             } catch (ParseException pe) {
                                 throw new BusinessCheckException("该卡券在当前日期不可用.");
                             }
                         }
                     }
                }
            }
        }

        userCoupon.setStatus("B");
        userCoupon.setUpdateTime(new Date());
        userCoupon.setUsedTime(new Date());
        userCoupon.setStoreId(storeId);
        userCouponRepository.save(userCoupon);

        // 生成核销流水
        MtConfirmLog confirmLog = new MtConfirmLog();
        StringBuilder code = new StringBuilder();
        String sstoreId="00000"+storeId.toString();
        code.append(new SimpleDateFormat("yyMMddHHmmss").format(new Date()));
        code.append(sstoreId.substring(sstoreId.length()-4, sstoreId.length()));
        code.append(SeqUtil.getRandomNumber(6));
        confirmLog.setCode(code.toString());

        confirmLog.setUserCouponId(userCouponId.intValue());
        confirmLog.setCreateTime(new Date());
        confirmLog.setUpdateTime(new Date());
        confirmLog.setUserId(userCoupon.getUserId());
        confirmLog.setOperatorUserId(userId);
        confirmLog.setStoreId(storeId);
        confirmLog.setStatus(StatusEnum.ENABLED.getKey());

        //判断是否是后台更新 20191012 add
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser(); //当前登录账号
        if (shiroUser != null) {
            confirmLog.setOperatorFrom("tAccount");
        }
        confirmLogRepository.save(confirmLog);

        try {
            List<String> mobileList = new ArrayList<>();
            mobileList.add(userCoupon.getMobile());
            Map<String, String> params = new HashMap<>();
            params.put("couponName", couponInfo.getName());
            params.put("storeName", mtStore.getName());
            params.put("sn", code.toString());
            sendSmsService.sendSms("confirm-coupon", mobileList, params);
        } catch (Exception e) {
            //empty
        }

        return confirmLog.getCode();
    }

    /**
     * 根据券ID 删除个人卡券
     *
     * @param id       券ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除卡券")
    public void deleteUserCoupon(Integer id, String operator) throws BusinessCheckException {
        MtUserCoupon usercoupon = this.userCouponRepository.findOne(id);
        if (null == usercoupon) {
            return;
        }

        // 未使用状态才能作废删除
        if(!usercoupon.getStatus().equals(UserCouponStatusEnum.UNUSED.getKey())) {
            throw new BusinessCheckException("不能作废，该劵状态异常");
        }
        usercoupon.setStatus("D");

        //修改时间
        usercoupon.setUpdateTime(new Date());

        //操作人
        usercoupon.setOperator(operator);

        //更新发券日志为部分作废状态
        this.sendLogRepository.updateSingleForRemove(usercoupon.getUuid(),"B");

        userCouponRepository.save(usercoupon);
    }

    /**
     * 根据券ID 撤销个人卡券消费流水 zach 20191012 add
     *
     * @param id       消费流水ID
     * @param userCouponId       用户卡券ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "撤销个人已使用的卡券")
    @Transactional
    public void rollbackUserCoupon(Integer id, Integer userCouponId,String operator) throws BusinessCheckException {

        MtConfirmLog mtConfirmLog = this.confirmLogRepository.findOne(id);

        MtUserCoupon usercoupon = this.userCouponRepository.findOne(userCouponId);

        if (null == mtConfirmLog || !mtConfirmLog.getUserCouponId().equals(userCouponId)) {
            throw new BusinessCheckException("卡券核销流水不存在！");
        }

        if (null == usercoupon) {
            throw new BusinessCheckException("用户卡券不存在！");
        }
        //卡券未过期才能撤销,当前时间小于过期日期才能删除,48小时
        Calendar endTimecal = Calendar.getInstance();
        endTimecal.setTime(mtConfirmLog.getCreateTime());
        endTimecal.add(Calendar.DAY_OF_MONTH, 2);

        if (endTimecal.getTime().before(new Date())) {
            throw new BusinessCheckException("卡券核销已经超过48小时，无法撤销！");
        }

        MtCoupon mtCoupon=couponRepository.findOne(usercoupon.getCouponId());

        // 卡券未过期才能撤销,当前时间小于过期日期才能删除
        if (mtCoupon.getEndTime().before(new Date())) {
            throw new BusinessCheckException("卡券未过期才能撤销");
        }
        // 卡券只有是使用状态且核销流水正常状态才能撤销
        if(!usercoupon.getStatus().equals(UserCouponStatusEnum.USED.getKey())||
                !mtConfirmLog.getStatus().equals(StatusEnum.ENABLED.getKey())) {
            throw new BusinessCheckException("该劵状态异常，请稍后重试！");
        }
        usercoupon.setStatus(UserCouponStatusEnum.UNUSED.getKey());  //回退至可用状态
        usercoupon.setStoreId(null);
        usercoupon.setUsedTime(null);

        // 修改时间
        usercoupon.setUpdateTime(new Date());

        //更新用户卡券
        userCouponRepository.save(usercoupon);

        //更新流水
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser(); //当前登录账号
        mtConfirmLog.setOperator(shiroUser.getAcctName());

        mtConfirmLog.setStatus(StatusEnum.DISABLE.getKey());
        mtConfirmLog.setUpdateTime(new Date());
        mtConfirmLog.setCancelTime(new Date());
        confirmLogRepository.save(mtConfirmLog);
    }

    /**
     * 根据ID获取用户卡券信息
     * @param userCouponId 查询参数
     * @throws BusinessCheckException
     * */
    @Override
    public MtUserCoupon queryUserCouponById(Integer userCouponId) throws BusinessCheckException {
        MtUserCoupon usercoupon = this.userCouponRepository.findOne(userCouponId);
        return usercoupon;
    }

    /**
     * 根据批次撤销卡券
     *
     * @param uuid       批次ID
     * @param operator   操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "作废卡券")
    @Transactional
    public void removeUserCoupon(Long id, String uuid, String operator) throws BusinessCheckException {
        MtSendLog sendLog = this.sendLogService.querySendLogById(id);

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(1);
        paginationRequest.setPageSize(1);
        Map<String, Object> searchParams = new HashedMap();
        searchParams.put("EQ_uuid", uuid);
        paginationRequest.setSearchParams(searchParams);
        PaginationResponse<MtUserCoupon> paginationResponse = userCouponRepository.findResultsByPagination(paginationRequest);

        Long total = paginationResponse.getTotalElements();

        List<Integer> coupondIdList = userCouponRepository.getCouponIdsByUuid(uuid);
        List<Integer> couponIds = new ArrayList<>();
        couponIds.add(0);

        Date nowDate = new Date();

        for (int i = 0; i < coupondIdList.size(); i++) {
            Integer couponId = coupondIdList.get(i);
            MtCoupon couponInfo = this.queryCouponById(couponId.longValue());
            if (couponInfo.getStatus().equals("A") && couponInfo.getEndTime().after(nowDate)) {
                couponIds.add(couponId);
            }
        }

        Integer row = this.userCouponRepository.removeUserCoupon(uuid, couponIds, operator);
        if (row.compareTo( total.intValue()) != -1) {
            this.sendLogRepository.updateForRemove(uuid, "D", total.intValue(), 0);
        } else {
            this.sendLogRepository.updateForRemove(uuid, "B", row, (total.intValue()-row));
        }

        return;
    }

    /**
     * 判断卡券码是否过期
     * @param code 12位券码
     * @throws BusinessCheckException
     * */
    @Override
    @OperationServiceLog(description = "卡券码是否过期")
    public boolean codeExpired(String code) {
        if (StringUtils.isEmpty(code)) {
            return true;
        }
        try {
            Date dateTime = DateUtil.parseDate(code.substring(0, 14), "yyyyMMddHHmmss");

            Long time = dateTime.getTime();
            Long nowTime = System.currentTimeMillis();

            Long seconds = (nowTime - time) / 1000;
            // 超过1小时
            if (seconds > 3600) {
                return true;
            }
        } catch (Exception e) {
            return true;
        }

        return false;
    }

    /**
     * 判断卡券是否过期
     * @param coupon
     * @return
     * */
    @Override
    @OperationServiceLog(description = "判断卡券是否有效")
    public boolean isCouponEffective(MtCoupon coupon) {
        Date begin = coupon.getBeginTime();
        Date end = coupon.getEndTime();
        Date now = new Date();

        // 已过期
        if (now.before(begin)) {
            return false;
        }

        // 未生效
        if (end.before(now)) {
            return false;
        }

        // 状态异常
        if (!coupon.getStatus().equals(StatusEnum.ENABLED.getKey())) {
            return false;
        }

        return true;
    }
}

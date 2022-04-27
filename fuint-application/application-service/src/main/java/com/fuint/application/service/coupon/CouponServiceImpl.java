package com.fuint.application.service.coupon;

import com.fuint.application.dto.CouponDto;
import com.fuint.application.service.usercoupon.UserCouponService;
import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.shiro.ShiroUser;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.application.enums.CouponTypeEnum;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.*;
import com.fuint.application.dao.repositories.*;
import com.fuint.application.dto.ReqCouponDto;
import com.fuint.application.config.Constants;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.enums.UserCouponStatusEnum;
import com.fuint.application.enums.SendWayEnum;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.confirmlog.ConfirmLogService;
import com.fuint.application.ResponseObject;
import com.fuint.application.service.sms.SendSmsInterface;
import com.fuint.application.util.CommonUtil;
import com.fuint.application.util.DateUtil;
import com.fuint.application.util.SeqUtil;
import org.apache.commons.collections.map.HashedMap;
import com.fuint.application.BaseService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 卡券业务实现类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Service
public class CouponServiceImpl extends BaseService implements CouponService {

    @Autowired
    private MtCouponRepository couponRepository;

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private SendSmsInterface sendSmsService;

    @Autowired
    private ConfirmLogService confirmLogService;

    @Autowired
    private MtUserCouponRepository userCouponRepository;

    @Autowired
    private MtConfirmLogRepository confirmLogRepository;

    @Autowired
    private MtSendLogRepository sendLogRepository;

    @Autowired
    private MtStoreRepository mtStoreRepository;

    @Autowired
    private MtCouponGoodsRepository mtCouponGoodsRepository;

    @Autowired
    private Environment env;

    /**
     * 分页查询券列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtCoupon> queryCouponListByPagination(PaginationRequest paginationRequest) {
        paginationRequest.setSortColumn(new String[]{"status asc", "id desc"});
        return couponRepository.findResultsByPagination(paginationRequest);
    }

    /**
     * 保存卡券信息
     *
     * @param reqCouponDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "保存卡券信息")
    public MtCoupon saveCoupon(ReqCouponDto reqCouponDto) throws BusinessCheckException {
        MtCoupon coupon;

        if (reqCouponDto.getId() != null) {
            coupon = couponRepository.findOne(reqCouponDto.getId());
        } else {
           coupon = new MtCoupon();
        }

        Date startTime = reqCouponDto.getBeginTime();
        Date endTime = reqCouponDto.getEndTime();
        if (endTime.before(startTime)) {
            throw new BusinessCheckException("生效期结束时间不能早于开始时间");
        }

        coupon.setGroupId(reqCouponDto.getGroupId());

        if (reqCouponDto.getType() != null) {
            coupon.setType(reqCouponDto.getType());
        }
        if (reqCouponDto.getName() != null) {
            coupon.setName(CommonUtil.replaceXSS(reqCouponDto.getName()));
        }
        if (reqCouponDto.getIsGive() != null) {
            coupon.setIsGive(reqCouponDto.getIsGive());
        }
        if (reqCouponDto.getPoint() != null) {
            coupon.setPoint(reqCouponDto.getPoint());
        }
        if (coupon.getPoint() == null) {
            coupon.setPoint(0);
        }
        if (reqCouponDto.getLimitNum() != null) {
            coupon.setLimitNum(reqCouponDto.getLimitNum());
        }
        if (coupon.getLimitNum() == null) {
            coupon.setLimitNum(0);
        }
        if (reqCouponDto.getReceiveCode() != null) {
            coupon.setReceiveCode(reqCouponDto.getReceiveCode());
        }
        if (coupon.getReceiveCode() == null) {
            coupon.setReceiveCode("");
        }

        if (coupon.getType().equals(CouponTypeEnum.TIMER.getKey())) {
            coupon.setPoint(reqCouponDto.getTimerPoint());
            coupon.setReceiveCode(reqCouponDto.getTimerReceiveCode());
        }

        coupon.setStoreIds(CommonUtil.replaceXSS(reqCouponDto.getStoreIds()));

        if (null == reqCouponDto.getSendNum()) {
            reqCouponDto.setSendNum(1);
        }

        if (coupon.getType().equals(CouponTypeEnum.PRESTORE.getKey()) || coupon.getType().equals(CouponTypeEnum.TIMER.getKey())) {
            coupon.setSendWay(SendWayEnum.FRONT.getKey());
        } else {
            coupon.setSendWay(reqCouponDto.getSendWay());
        }

        coupon.setSendNum(reqCouponDto.getSendNum());

        if (null == reqCouponDto.getTotal()) {
            reqCouponDto.setTotal(0);
        }
        coupon.setTotal(reqCouponDto.getTotal());

        coupon.setBeginTime(reqCouponDto.getBeginTime());
        coupon.setEndTime(reqCouponDto.getEndTime());
        coupon.setExceptTime(CommonUtil.replaceXSS(reqCouponDto.getExceptTime()));
        coupon.setDescription(CommonUtil.replaceXSS(reqCouponDto.getDescription()));
        coupon.setRemarks(CommonUtil.replaceXSS(reqCouponDto.getRemarks()));
        coupon.setInRule(CommonUtil.replaceXSS(reqCouponDto.getInRule()));
        coupon.setOutRule(CommonUtil.replaceXSS(reqCouponDto.getOutRule()));
        coupon.setApplyGoods(reqCouponDto.getApplyGoods());

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

        coupon.setStatus(StatusEnum.ENABLED.getKey());

        // 创建时间
        if (reqCouponDto.getId() == null) {
            coupon.setCreateTime(new Date());
        }

        // 更新时间
        coupon.setUpdateTime(new Date());

        // 操作人
        coupon.setOperator(reqCouponDto.getOperator());

        MtCoupon couponInfo = couponRepository.save(coupon);

        // 适用商品
        if (reqCouponDto.getGoodsIds() != null) {
           String[] goodsIds = reqCouponDto.getGoodsIds().split(",");
           if (goodsIds.length > 0) {
               // 1.先删除
               List<MtCouponGoods> couponGoodsList = mtCouponGoodsRepository.getCouponGoods(couponInfo.getId());
               for (MtCouponGoods cg : couponGoodsList) {
                   mtCouponGoodsRepository.delete(cg.getId());
               }
               // 2.再添加
               for (int n = 0; n < goodsIds.length; n++) {
                   if (StringUtils.isNotEmpty(goodsIds[n])) {
                       MtCouponGoods mtCouponGoods = new MtCouponGoods();
                       mtCouponGoods.setCouponId(couponInfo.getId());
                       mtCouponGoods.setGoodsId(Integer.parseInt(goodsIds[n]));
                       mtCouponGoods.setStatus(StatusEnum.ENABLED.getKey());
                       mtCouponGoods.setCreateTime(new Date());
                       mtCouponGoods.setUpdateTime(new Date());
                       mtCouponGoodsRepository.save(mtCouponGoods);
                   }
               }
           }
        }

        // 如果是优惠券，并且是线下发放，生成会员卡券
        if (coupon.getType().equals(CouponTypeEnum.COUPON.getKey()) && coupon.getSendWay().equals(SendWayEnum.OFFLINE.getKey())) {
            Integer total = coupon.getTotal() * coupon.getSendNum();
            if (total > 0) {
                String uuid = UUID.randomUUID().toString().replaceAll("-", "");

                for (int i = 1; i <= total; i++) {
                    MtUserCoupon userCoupon = new MtUserCoupon();
                    userCoupon.setCouponId(couponInfo.getId());
                    userCoupon.setGroupId(coupon.getGroupId());
                    userCoupon.setMobile("");
                    userCoupon.setUserId(0);
                    userCoupon.setStatus(UserCouponStatusEnum.UNSEND.getKey());
                    userCoupon.setCreateTime(new Date());
                    userCoupon.setUpdateTime(new Date());
                    userCoupon.setUuid(uuid);
                    userCoupon.setType(CouponTypeEnum.COUPON.getKey());
                    userCoupon.setAmount(couponInfo.getAmount());
                    userCoupon.setBalance(couponInfo.getAmount());
                    userCoupon.setStoreId(0);
                    userCoupon.setOperator(reqCouponDto.getOperator());
                    userCoupon.setImage(couponInfo.getImage());

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
    public MtCoupon queryCouponById(Integer id) throws BusinessCheckException {
        return couponRepository.findOne(id);
    }

    /**
     * 删除卡券
     *
     * @param id       券ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除卡券")
    public void deleteCoupon(Long id, String operator) throws BusinessCheckException {
        MtCoupon couponInfo = this.queryCouponById(id.intValue());
        if (null == couponInfo) {
            return;
        }

        couponInfo.setStatus(StatusEnum.DISABLE.getKey());

        // 修改时间
        couponInfo.setUpdateTime(new Date());

        // 操作人
        couponInfo.setOperator(operator);

        couponRepository.save(couponInfo);

        return;
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
        String needPoint =  paramMap.get("needPoint") == null ? "0" : paramMap.get("needPoint").toString();
        String sendWay =  paramMap.get("sendWay") == null ? "front" : paramMap.get("sendWay").toString();

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(pageNumber);
        paginationRequest.setPageSize(pageSize);
        Map<String, Object> searchParams = new HashedMap();

        searchParams.put("EQ_status", status);
        if (StringUtils.isNotEmpty(sendWay)) {
            searchParams.put("EQ_sendWay", sendWay);
        }
        if (StringUtils.isNotEmpty(type)) {
            searchParams.put("EQ_type", type);
        }
        if (Integer.parseInt(needPoint) > 0) {
            searchParams.put("GT_point", needPoint);
        }

        paginationRequest.setSearchParams(searchParams);
        paginationRequest.setSortColumn(new String[]{"id desc", "groupId desc"});

        PaginationResponse<MtCoupon> paginationResponse = couponRepository.findResultsByPagination(paginationRequest);

        List<MtCoupon> dataList = paginationResponse.getContent();
        List<CouponDto> content = new ArrayList<>();
        String baseImage = env.getProperty("images.upload.url");
        for (int i = 0; i < dataList.size(); i++) {
            CouponDto item = new CouponDto();
            BeanUtils.copyProperties(dataList.get(i), item);

            item.setImage(baseImage + item.getImage());

            // 是否领取，且领取量大于限制数
            List<String> statusList = Arrays.asList(UserCouponStatusEnum.UNUSED.getKey(), UserCouponStatusEnum.USED.getKey(), UserCouponStatusEnum.EXPIRE.getKey());
            List<MtUserCoupon> userCoupon = userCouponRepository.getUserCouponListByCouponId(userId, item.getId(), statusList);
            if ((userCoupon.size() >= dataList.get(i).getLimitNum()) && (dataList.get(i).getLimitNum() > 0)) {
                item.setIsReceive(true);
                item.setUserCouponId(userCoupon.get(0).getId());
            }

            // 领取或预存数量
            List<Object[]> numData = userCouponRepository.getPeopleNumByCouponId(item.getId());
            Long num;
            if (null == numData || numData.size() < 1) {
                num = 0l;
            } else {
                Object[] obj = numData.get(0);
                num = (Long) obj[1];
            }
            item.setGotNum(num.intValue());

            // 剩余数量
            Integer leftNum = dataList.get(i).getTotal() - item.getGotNum();
            item.setLeftNum(leftNum >= 0 ? leftNum : 0);

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
        result.setTotalPages(paginationResponse.getTotalPages());

        return getSuccessResult(result);
    }

    /**
     * 根据分组获取卡券列表
     * @param groupId 查询参数
     * @throws BusinessCheckException
     * */
    public List<MtCoupon> queryCouponListByGroupId(Integer groupId) throws BusinessCheckException {
        List<MtCoupon> couponList = couponRepository.queryByGroupId(groupId.intValue());
        return couponList;
    }

    /**
     * 发放卡券
     *
     * @param couponId 卡券ID
     * @param mobile   手机号
     * @param num      发放套数
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "发放卡券")
    public void sendCoupon(Integer couponId, String mobile, Integer num, String uuid) throws BusinessCheckException {
        MtUser mtUser = memberService.queryMemberByMobile(mobile);

        if (null == mtUser || !mtUser.getStatus().equals(StatusEnum.ENABLED.getKey())) {
            throw new BusinessCheckException("该手机号码不存在或已禁用，请先注册会员");
        }

        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        MtCoupon couponInfo = this.queryCouponById(couponId);

        // 判断券是否有效
        if (!couponInfo.getStatus().equals(StatusEnum.ENABLED.getKey())) {
            throw new BusinessCheckException("该卡券已停用，不能发放");
        }

        // 发放的是预存卡
        if (couponInfo.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
            if (StringUtils.isNotEmpty(couponInfo.getInRule())) {
                String storeParams = "";
                String[] paramArr = couponInfo.getInRule().split(",");
                for (int i = 0; i < paramArr.length; i++) {
                     if (StringUtils.isNotEmpty(storeParams)) {
                         storeParams = storeParams + "," + paramArr[i] + "_" + num;
                     } else {
                         storeParams = paramArr[i] + "_" + num;
                     }
                }

                Map<String, Object> param = new HashMap<>();
                param.put("couponId", couponInfo.getId());
                param.put("userId", mtUser.getId());
                param.put("param", storeParams);
                param.put("orderId", 0);
                userCouponService.preStore(param);
            }
            return;
        }

        // 优惠券或集次卡，发放num套
        for (int k = 1; k <= num; k++) {
            for (int j = 1; j <= couponInfo.getSendNum(); j++) {
                MtUserCoupon userCoupon = new MtUserCoupon();
                userCoupon.setCouponId(couponInfo.getId());
                userCoupon.setType(couponInfo.getType());
                userCoupon.setImage(couponInfo.getImage());
                userCoupon.setStoreId(mtUser.getStoreId());
                userCoupon.setAmount(couponInfo.getAmount());
                userCoupon.setBalance(couponInfo.getAmount());
                if (shiroUser != null) {
                    userCoupon.setOperator(shiroUser.getAcctName());
                }
                userCoupon.setGroupId(couponInfo.getGroupId());
                userCoupon.setMobile(mobile);
                userCoupon.setUserId(mtUser.getId());
                userCoupon.setStatus(UserCouponStatusEnum.UNUSED.getKey());
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
    }

    /**
     * 核销卡券
     *
     * @param userCouponId 用户卡券ID
     * @param userId  员工会员ID
     * @param storeId 店铺ID
     * @param orderId 订单ID
     * @param amount 核销金额
     * @param remark 核销备注
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "核销卡券")
    public String useCoupon(Integer userCouponId, Integer userId, Integer storeId, Integer orderId, BigDecimal amount, String remark) throws BusinessCheckException {
        MtUserCoupon userCoupon = userCouponRepository.findOne(userCouponId.intValue());

        if (userCoupon == null) {
            throw new BusinessCheckException("该卡券不存在！");
        } else if (!userCoupon.getStatus().equals(UserCouponStatusEnum.UNUSED.getKey()) && !userCoupon.getStatus().equals(UserCouponStatusEnum.UNSEND.getKey())) {
            throw new BusinessCheckException("该卡券状态有误，可能已使用或已过期！");
        }

        MtStore mtStore = null;
        if (storeId > 0) {
            mtStore = mtStoreRepository.findOne(storeId);
            if (null == mtStore) {
                throw new BusinessCheckException("该店铺不存在！");
            } else if (!mtStore.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                throw new BusinessCheckException("该店铺状态有误，可能已禁用");
            }
        }

        // 是否处于有效期
        MtCoupon couponInfo = this.queryCouponById(userCoupon.getCouponId());
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
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
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
                                 Date startTime = DateUtil.parseDate(timeItem[0], "yyyy-MM-dd HH:mm");
                                 Date endTime = DateUtil.parseDate(timeItem[1], "yyyy-MM-dd HH:mm");
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

        if (couponInfo.getType().equals(CouponTypeEnum.COUPON.getKey())) {
            // 优惠券核销直接修改状态
            userCoupon.setStatus(UserCouponStatusEnum.USED.getKey());
        } else if (couponInfo.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
            // 预存卡核销，修改余额
            BigDecimal balance = userCoupon.getBalance();
            BigDecimal newBalance = balance.subtract(amount);

            if (newBalance.compareTo(new BigDecimal("0")) == -1) {
                throw new BusinessCheckException("余额不足，无法核销");
            }

            if (newBalance.compareTo(new BigDecimal("0")) == 0) {
                userCoupon.setStatus(UserCouponStatusEnum.USED.getKey());
            }

            userCoupon.setBalance(newBalance);
        } else if (couponInfo.getType().equals(CouponTypeEnum.TIMER.getKey())) {
            // 集次卡核销，增加核销次数至满
            Long confirmCount = confirmLogService.getConfirmNum(userCouponId);
            if ((confirmCount.intValue() + 1) >= Integer.parseInt(couponInfo.getOutRule())) {
                userCoupon.setStatus(UserCouponStatusEnum.USED.getKey());
            }
        }

        userCoupon.setUpdateTime(new Date());
        userCoupon.setUsedTime(new Date());
        userCoupon.setStoreId(storeId);
        userCouponRepository.save(userCoupon);

        // 生成核销流水
        MtConfirmLog confirmLog = new MtConfirmLog();
        StringBuilder code = new StringBuilder();
        String sStoreId="00000"+storeId.toString();
        code.append(new SimpleDateFormat("yyMMddHHmmss").format(new Date()));
        code.append(sStoreId.substring(sStoreId.length()-4));
        code.append(SeqUtil.getRandomNumber(6));
        confirmLog.setCode(code.toString());

        confirmLog.setCouponId(couponInfo.getId());
        confirmLog.setUserCouponId(userCouponId.intValue());
        confirmLog.setOrderId(orderId);
        confirmLog.setCreateTime(new Date());
        confirmLog.setUpdateTime(new Date());
        confirmLog.setUserId(userCoupon.getUserId());
        confirmLog.setOperatorUserId(userId);
        if (userId > 0) {
            MtUser userInfo = memberService.queryMemberById(userId);
            if (userInfo != null) {
                confirmLog.setOperator(userInfo.getName());
            }
        }
        confirmLog.setStoreId(storeId);
        confirmLog.setStatus(StatusEnum.ENABLED.getKey());

        // 优惠券核销金额
        if (couponInfo.getType().equals(CouponTypeEnum.COUPON.getKey())) {
            amount = userCoupon.getAmount();
        }

        confirmLog.setAmount(amount);
        confirmLog.setRemark(remark);

        // 判断是否是后台更新
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        if (shiroUser != null) {
            confirmLog.setOperatorFrom("tAccount");
            confirmLog.setOperator(shiroUser.getAcctName());
        }
        confirmLogRepository.save(confirmLog);

        try {
            List<String> mobileList = new ArrayList<>();
            mobileList.add(userCoupon.getMobile());
            Map<String, String> params = new HashMap<>();
            params.put("couponName", couponInfo.getName());
            if (mtStore != null){
                params.put("storeName", mtStore.getName());
            }
            params.put("sn", code.toString());
            sendSmsService.sendSms("confirm-coupon", mobileList, params);
        } catch (Exception e) {
            //empty
        }

        return confirmLog.getCode();
    }

    /**
     * 根据券ID删除会员卡券
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
        usercoupon.setStatus(UserCouponStatusEnum.DISABLE.getKey());

        // 修改时间
        usercoupon.setUpdateTime(new Date());

        // 操作人
        usercoupon.setOperator(operator);

        // 更新发券日志为部分作废状态
        this.sendLogRepository.updateSingleForRemove(usercoupon.getUuid(),UserCouponStatusEnum.USED.getKey());

        userCouponRepository.save(usercoupon);
    }

    /**
     * 根据券ID 撤销个人卡券核销
     *
     * @param id             核销流水ID
     * @param userCouponId   用户卡券ID
     * @param operator       操作人
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
            throw new BusinessCheckException("用户卡券不存在");
        }

        // 卡券未过期才能撤销,当前时间小于过期日期才能删除,48小时
        Calendar endTimecal = Calendar.getInstance();
        endTimecal.setTime(mtConfirmLog.getCreateTime());
        endTimecal.add(Calendar.DAY_OF_MONTH, 2);

        if (endTimecal.getTime().before(new Date())) {
            throw new BusinessCheckException("卡券核销已经超过48小时，无法撤销");
        }

        MtCoupon mtCoupon=couponRepository.findOne(usercoupon.getCouponId());

        // 卡券未过期才能撤销,当前时间小于过期日期才能删除
        if (mtCoupon.getEndTime().before(new Date())) {
            throw new BusinessCheckException("卡券未过期才能撤销");
        }

        // 优惠券只有是使用状态且核销流水正常状态才能撤销
        if(usercoupon.getType().equals(CouponTypeEnum.COUPON.getKey())) {
            if ((!usercoupon.getStatus().equals(UserCouponStatusEnum.USED.getKey())) || (!mtConfirmLog.getStatus().equals(StatusEnum.ENABLED.getKey()))) {
                throw new BusinessCheckException("该劵状态异常，请稍后重试");
            }
        }

        // 回退至可用状态
        usercoupon.setStatus(UserCouponStatusEnum.UNUSED.getKey());
        usercoupon.setStoreId(null);
        usercoupon.setUsedTime(null);
        usercoupon.setUpdateTime(new Date());

        // 如果是预存卡则返回余额
        if (usercoupon.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
            BigDecimal balance = usercoupon.getBalance();
            BigDecimal amount = mtConfirmLog.getAmount();
            if (amount.compareTo(new BigDecimal("0")) > 0) {
                BigDecimal newBalance = balance.add(amount);
                usercoupon.setBalance(newBalance);
            }
        }

        // 更新用户卡券
        userCouponRepository.save(usercoupon);

        // 更新流水
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
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
            MtCoupon couponInfo = this.queryCouponById(couponId);
            if (couponInfo.getStatus().equals("A") && couponInfo.getEndTime().after(nowDate)) {
                couponIds.add(couponId);
            }
        }

        Integer row = this.userCouponRepository.removeUserCoupon(uuid, couponIds, operator);
        if (row.compareTo( total.intValue()) != -1) {
            this.sendLogRepository.updateForRemove(uuid, UserCouponStatusEnum.DISABLE.getKey(), total.intValue(), 0);
        } else {
            this.sendLogRepository.updateForRemove(uuid, UserCouponStatusEnum.USED.getKey(), row, (total.intValue()-row));
        }

        return;
    }

    /**
     * 判断卡券码是否过期
     * @param code 12位券码
     * @throws BusinessCheckException
     * */
    @Override
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
    public boolean isCouponEffective(MtCoupon coupon) {
        Date begin = coupon.getBeginTime();
        Date end = coupon.getEndTime();
        Date now = new Date();

        // 未生效
        if (begin != null) {
            if (now.before(begin)) {
                return false;
            }
        }

        // 已过期
        if (end != null) {
            if (now.after(end)) {
                return false;
            }
        }

        if (coupon.getStatus() == null) {
            return false;
        }

        // 状态异常
        if (!coupon.getStatus().equals(StatusEnum.ENABLED.getKey())) {
            return false;
        }

        return true;
    }
}

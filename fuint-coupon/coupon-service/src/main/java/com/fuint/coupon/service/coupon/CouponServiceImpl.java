package com.fuint.coupon.service.coupon;

import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.shiro.ShiroUser;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.coupon.enums.GroupTypeEnum;
import com.fuint.exception.BusinessCheckException;
import com.fuint.coupon.dao.entities.*;
import com.fuint.coupon.dao.repositories.*;
import com.fuint.coupon.dto.MyCouponDto;
import com.fuint.coupon.dto.ReqCouponDto;
import com.fuint.coupon.config.Constants;
import com.fuint.coupon.enums.CouponContentEnum;
import com.fuint.coupon.enums.StatusEnum;
import com.fuint.coupon.enums.UserCouponStatusEnum;
import com.fuint.coupon.service.coupongroup.CouponGroupService;
import com.fuint.coupon.service.member.MemberService;
import com.fuint.coupon.ResponseObject;
import com.fuint.coupon.service.sendlog.SendLogService;
import com.fuint.coupon.service.sms.SendSmsInterface;
import com.fuint.coupon.util.CommonUtil;
import com.fuint.coupon.util.DateUtil;
import com.fuint.coupon.util.SeqUtil;
import org.apache.commons.collections.map.HashedMap;
import com.fuint.coupon.BaseService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fuint.coupon.dto.ResMyCouponDto;
import org.springframework.core.env.Environment;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 优惠券业务实现类
 * Created by zach on 2019/08/06.
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
    private MtUserCouponRepository userCouponRepository;

    @Autowired
    private MtConfirmLogRepository confirmLogRepository;

    @Autowired
    private MtSendLogRepository sendLogRepository;
    @Autowired
    private MtStoreRepository mtStoreRepository;

    /**
     * 短信发送接口
     */
    @Autowired
    private SendSmsInterface sendSmsService;

    @Autowired
    private Environment env;

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
    @OperationServiceLog(description = "新增卡券")
    public MtCoupon addCoupon(ReqCouponDto reqCouponDto) throws BusinessCheckException {
        MtCoupon coupon = new MtCoupon();

        Date startTime = reqCouponDto.getBeginTime();
        Date endTime = reqCouponDto.getEndTime();
        if (endTime.before(startTime)) {
            throw new BusinessCheckException("生效期结束时间不能早于开始时间");
        }

        MtCouponGroup groupInfo = couponGroupService.queryCouponGroupById(reqCouponDto.getGroupId().longValue());

        // 预存卡
        if (groupInfo.getType().equals(GroupTypeEnum.PRESTORE.getKey())) {
            coupon.setMoney(new BigDecimal(0));
        }

        // 优惠券
        if (groupInfo.getType().equals(GroupTypeEnum.COUPON.getKey())) {
            coupon.setMoney(reqCouponDto.getMoney());
        }

        coupon.setGroupId(reqCouponDto.getGroupId());

        coupon.setName(CommonUtil.replaceXSS(reqCouponDto.getName()));
        coupon.setStoreIds(CommonUtil.replaceXSS(reqCouponDto.getStoreIds()));
        coupon.setTotal(reqCouponDto.getTotal());
        coupon.setBeginTime(reqCouponDto.getBeginTime());
        coupon.setEndTime(reqCouponDto.getEndTime());
        coupon.setExceptTime(CommonUtil.replaceXSS(reqCouponDto.getExceptTime()));
        coupon.setDescription(CommonUtil.replaceXSS(reqCouponDto.getDescription()));
        coupon.setRemarks(CommonUtil.replaceXSS(reqCouponDto.getRemarks()));
        coupon.setInRule(CommonUtil.replaceXSS(reqCouponDto.getInRule()));
        coupon.setOutRule(CommonUtil.replaceXSS(reqCouponDto.getOutRule()));

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

        couponRepository.save(coupon);

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
    @OperationServiceLog(description = "删除优惠券")
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
     * 修改优惠券
     *
     * @param reqCouponDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "修改优惠券")
    public MtCoupon updateCoupon(ReqCouponDto reqCouponDto) throws BusinessCheckException {
        MtCoupon coupon = this.queryCouponById(reqCouponDto.getId());

        if (null == coupon) {
            throw new BusinessCheckException("该优惠券不存在！");
        }

        coupon.setGroupId(reqCouponDto.getGroupId());
        coupon.setName(CommonUtil.replaceXSS(reqCouponDto.getName()));
        coupon.setMoney(reqCouponDto.getMoney());
        coupon.setTotal(reqCouponDto.getTotal());
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
     * 获取我的优惠券列表
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

        // 处理已过期，置为过期
        if (pageNumber <= 1) {
            List<MtUserCoupon> ulist = userCouponRepository.getUserCouponList(Integer.parseInt(userId));
            for (MtUserCoupon uc : ulist) {
                MtCoupon coupon = this.queryCouponById(uc.getCouponId().longValue());
                if (coupon.getEndTime().before(new Date())) {
                    uc.setStatus("C");
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
        paginationRequest.setSearchParams(searchParams);
        paginationRequest.setSortColumn(new String[]{"id desc", "groupId desc"});

        PaginationResponse<MtUserCoupon> paginationResponse = userCouponRepository.findResultsByPagination(paginationRequest);

        List<MyCouponDto> dataList = new ArrayList<>();
        String baseImage = env.getProperty("images.website");

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

                 dto.setImage(baseImage + image);
                 dto.setStatus(userCouponDto.getStatus());
                 dto.setMoney(couponInfo.getMoney());

                 dataList.add(dto);
            }
        }

        ResMyCouponDto myCouponResponse = new ResMyCouponDto();
        myCouponResponse.setPageNumber(pageNumber);
        myCouponResponse.setPageSize(pageSize);
        myCouponResponse.getTotalRow(paginationResponse.getTotalElements());
        myCouponResponse.setTotalPage(paginationResponse.getTotalPages());
        myCouponResponse.setDataList(dataList);

        return getSuccessResult(myCouponResponse);
    }

    /**
     * 根据分组获取优惠券列表
     * @param groupId 查询参数
     * @throws BusinessCheckException
     * */
    public List<MtCoupon> queryCouponListByGroupId(Long groupId) throws BusinessCheckException {
        List<MtCoupon> couponList = couponRepository.queryByGroupId(groupId.intValue());
        return couponList;
    }

    /**
     * 发放优惠券
     *
     * @param groupId 券ID
     * @param mobile  操作人
     * @param num     发放套数
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "发放优惠券")
    public void sendCoupon(Long groupId, String mobile, Integer num, String uuid) throws BusinessCheckException {
        MtCouponGroup groupInfo = couponGroupService.queryCouponGroupById(groupId);

        MtUser mtUser = memberService.queryMemberByMobile(mobile);

        if (null == groupInfo) {
            throw new BusinessCheckException("该分组为空，请增加优惠券");
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

                    for (int j = 1; j <= coupon.getTotal(); j++) {
                        MtUserCoupon userCoupon = new MtUserCoupon();
                        userCoupon.setCouponId(coupon.getId());
                        userCoupon.setGroupId(groupInfo.getId());
                        userCoupon.setMobile(mobile);
                        userCoupon.setUserId(mtUser.getId());
                        userCoupon.setStatus("A");
                        userCoupon.setCreateTime(new Date());
                        userCoupon.setUpdateTime(new Date());

                        // 32位随机数
                        StringBuffer code = new StringBuffer();
                        code.append(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                        code.append(SeqUtil.getRandomNumber(15));
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
     * 发放优惠券
     *
     * @param userCouponId 用户券ID
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "核销优惠券")
    public String useCoupon(Long userCouponId, Integer userId, Integer storeId) throws BusinessCheckException {
        MtUserCoupon userCoupon = userCouponRepository.findOne(userCouponId.intValue());

        if (null == userCoupon) {
            throw new BusinessCheckException("该优惠券不存在！");
        } else if (!userCoupon.getStatus().equals("A")) {
            throw new BusinessCheckException("该优惠券状态有误，可能已使用或过期");
        }

        //20191012 wangshude add check store status
        MtStore mtStore =mtStoreRepository.findOne(storeId);
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
            throw new BusinessCheckException("该优惠券还没到使用日期");
        }
        if (end.before(now)) {
            throw new BusinessCheckException("该优惠券已过期");
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
                             throw new BusinessCheckException("该优惠券在当前日期不可用");
                         }
                     } else {
                         String[] timeItem = exceptTime.split("_");
                         if (timeItem.length == 2) {
                             try {
                                 Date startTime = DateUtil.parseDate(timeItem[0].toString(), "yyyy-MM-dd HH:mm");
                                 Date endTime = DateUtil.parseDate(timeItem[1].toString(), "yyyy-MM-dd HH:mm");
                                 // 2019-09-18 17:00_2019-09-19 04:00
                                 if (now.before(endTime) && now.after(startTime)) {
                                     throw new BusinessCheckException("该优惠券在当前日期不可用");
                                 }
                             } catch (ParseException pe) {
                                 throw new BusinessCheckException("该优惠券在当前日期不可用.");
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
        confirmLog.setStatus("A");

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
     * 发放优惠券
     *
     * @param contentIds 用户券ID
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    public String getConetntByIds(String contentIds) throws BusinessCheckException {
        if (StringUtils.isEmpty(contentIds)) {
            return "";
        }

        String[] list = contentIds.split(",");

        String text = "";

        if (list.length > 0) {
            for (String id : list) {
                 if (id.equals(CouponContentEnum.ROOM.getKey())) {
                     if (StringUtils.isNotEmpty(text)) {
                         text = text + "," + CouponContentEnum.ROOM.getValue();
                     } else {
                         text = text + CouponContentEnum.ROOM.getValue();
                     }
                 } else if (id.equals(CouponContentEnum.ROOM_BTEAKFAST.getKey())) {
                    if (StringUtils.isNotEmpty(text)) {
                        text = text + "," + CouponContentEnum.ROOM_BTEAKFAST.getValue();
                    } else {
                        text = text + CouponContentEnum.ROOM_BTEAKFAST.getValue();
                    }
                 } else if (id.equals(CouponContentEnum.MEALS.getKey())) {
                     if (StringUtils.isNotEmpty(text)) {
                         text = text + "," + CouponContentEnum.MEALS.getValue();
                     } else {
                         text = text + CouponContentEnum.MEALS.getValue();
                     }
                 } else if (id.equals(CouponContentEnum.WASH.getKey())) {
                     if (StringUtils.isNotEmpty(text)) {
                         text = text + "," + CouponContentEnum.WASH.getValue();
                     } else {
                         text = text + CouponContentEnum.WASH.getValue();
                     }
                 } else if (id.equals(CouponContentEnum.HEALTH.getKey())) {
                     if (StringUtils.isNotEmpty(text)) {
                         text = text + "," + CouponContentEnum.HEALTH.getValue();
                     } else {
                         text = text + CouponContentEnum.HEALTH.getValue();
                     }
                 }
            }
        }

        return text;
    }

    /**
     * 根据券ID 删除个人优惠券zach 20190912 add
     *
     * @param id       券ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除优惠券")
    public void deleteUserCoupon(Integer id, String operator) throws BusinessCheckException {
        MtUserCoupon usercoupon = this.userCouponRepository.findOne(id);
        if (null == usercoupon) {
            return;
        }
        //未使用状态才能作废删除
        if(!usercoupon.getStatus().equals(UserCouponStatusEnum.UNUSED.getKey()))
        {
            throw new BusinessCheckException("不能作废，该劵状态异常");
        }
        usercoupon.setStatus("D");
        //修改时间
        usercoupon.setUpdateTime(new Date());
        //操作人
        //usercoupon.setOperator(operator);

        //更新发券日志为部分作废状态
        this.sendLogRepository.updateSingleForRemove(usercoupon.getUuid(),"B");
        userCouponRepository.save(usercoupon);
    }


    /**
     * 根据券ID 撤销个人已使用的优惠券 zach 20190912 add  :::已不用，用其他函数代替
     *
     * @param id       券ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "撤销个人已使用的优惠券")
    public void rollbackUserCoupon(Integer id, String operator) throws BusinessCheckException {
        MtUserCoupon usercoupon = this.userCouponRepository.findOne(id);
        if (null == usercoupon) {
            return;
        }
        MtCoupon mtCoupon=couponRepository.findOne(usercoupon.getCouponId());
        //优惠券未过期才能撤销,当前时间小于过期日期才能删除
        if(mtCoupon.getEndTime().before(new Date()))
        {
            throw new BusinessCheckException("优惠券未过期才能撤销");
        }
        //优惠券只有是使用状态才能撤销
        if(!usercoupon.getStatus().equals(UserCouponStatusEnum.USED.getKey()))
        {
            throw new BusinessCheckException("该劵状态异常，请稍后重试！");
        }
        usercoupon.setStatus("A");
        usercoupon.setStoreId(null);
        usercoupon.setUsedTime(null);
        //修改时间
        usercoupon.setUpdateTime(new Date());
        //操作人
        //usercoupon.setOperator(operator);
        userCouponRepository.save(usercoupon);
    }


    /**
     * 根据券ID 撤销个人优惠券消费流水 zach 20191012 add
     *
     * @param id       消费流水ID
     * @param userCouponId       用户优惠券ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "撤销个人已使用的优惠券")
    @Transactional
    public void rollbackUserCoupon(Integer id, Integer userCouponId,String operator) throws BusinessCheckException {

        MtConfirmLog mtConfirmLog = this.confirmLogRepository.findOne(id);

        MtUserCoupon usercoupon = this.userCouponRepository.findOne(userCouponId);

        if (null == mtConfirmLog || !mtConfirmLog.getUserCouponId().equals(userCouponId)) {
            throw new BusinessCheckException("优惠券核销流水不存在！");
        }

        if (null == usercoupon) {
            throw new BusinessCheckException("用户优惠券不存在！");
        }
        //优惠券未过期才能撤销,当前时间小于过期日期才能删除,48小时
        Calendar endTimecal = Calendar.getInstance();
        endTimecal.setTime(mtConfirmLog.getCreateTime());
        endTimecal.add(Calendar.DAY_OF_MONTH, 2);

        if (endTimecal.getTime().before(new Date())) {
            throw new BusinessCheckException("优惠券核销已经超过48小时，无法撤销！");
        }

        MtCoupon mtCoupon=couponRepository.findOne(usercoupon.getCouponId());

        // 优惠券未过期才能撤销,当前时间小于过期日期才能删除
        if (mtCoupon.getEndTime().before(new Date())) {
            throw new BusinessCheckException("优惠券未过期才能撤销");
        }
        // 优惠券只有是使用状态且核销流水正常状态才能撤销
        if(!usercoupon.getStatus().equals(UserCouponStatusEnum.USED.getKey())||
                !mtConfirmLog.getStatus().equals(StatusEnum.ENABLED.getKey())) {
            throw new BusinessCheckException("该劵状态异常，请稍后重试！");
        }
        usercoupon.setStatus(UserCouponStatusEnum.UNUSED.getKey());  //回退至可用状态
        usercoupon.setStoreId(null);
        usercoupon.setUsedTime(null);

        // 修改时间
        usercoupon.setUpdateTime(new Date());

        //更新用户优惠券
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
     * 根据ID获取用户优惠券信息
     * @param userCouponId 查询参数
     * @throws BusinessCheckException
     * */
    @Override
    public MtUserCoupon queryUserCouponById(Integer userCouponId) throws BusinessCheckException {
        MtUserCoupon usercoupon = this.userCouponRepository.findOne(userCouponId);
        return usercoupon;
    }

    /**
     * 根据批次撤销优惠券
     *
     * @param uuid       批次ID
     * @param operator   操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "作废优惠券")
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
     * 判断优惠券码是否过期
     * @param code 券码
     * @throws BusinessCheckException
     * */
    @Override
    @OperationServiceLog(description = "优惠券码是否过期")
    public boolean codeExpired(String code) {
        if (StringUtils.isEmpty(code)) {
            return true;
        }
        try {
            Date dateTime = DateUtil.parseDate(code.substring(0, 14), "yyyyMMddHHmmss");

            Long time = dateTime.getTime();
            Long nowTime = System.currentTimeMillis();

            Long seconds = (nowTime - time) / 1000;
            // 超过2小时
            if (seconds > 7200) {
                return true;
            }
        } catch (Exception e) {
            return true;
        }

        return false;
    }
}

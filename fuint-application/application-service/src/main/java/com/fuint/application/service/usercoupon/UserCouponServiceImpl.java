package com.fuint.application.service.usercoupon;

import com.fuint.application.ResponseObject;
import com.fuint.application.config.Constants;
import com.fuint.application.config.Message;
import com.fuint.application.dto.CouponDto;
import com.fuint.application.dto.MyCouponDto;
import com.fuint.application.enums.CouponTypeEnum;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.enums.UserCouponStatusEnum;
import com.fuint.application.service.confirmlog.ConfirmLogService;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.coupongroup.CouponGroupService;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.point.PointService;
import com.fuint.application.service.setting.SettingService;
import com.fuint.application.service.store.StoreService;
import com.fuint.application.util.DateUtil;
import com.fuint.application.util.SeqUtil;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.*;
import com.fuint.application.dao.repositories.MtUserCouponRepository;
import com.fuint.application.BaseService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;

/**
 * 会员卡券业务实现类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Service
public class UserCouponServiceImpl extends BaseService implements UserCouponService {

    @Autowired
    private MtUserCouponRepository userCouponRepository;

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponGroupService couponGroupService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PointService pointService;

    @Autowired
    private ConfirmLogService confirmLogService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private SettingService settingService;

    /**
     * 分页查询券列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtUserCoupon> queryUserCouponListByPagination(PaginationRequest paginationRequest) {
        paginationRequest.setSortColumn(new String[]{"status asc", "id desc"});
        PaginationResponse<MtUserCoupon> paginationResponse = userCouponRepository.findResultsByPagination(paginationRequest);
        return paginationResponse;
    }

    /**
     * 领取卡券(优惠券、集次卡)
     * @param paramMap
     * @return
     * */
    @Override
    @Transactional
    public boolean receiveCoupon(Map<String, Object> paramMap) throws BusinessCheckException {
        Integer couponId = paramMap.get("couponId") == null ? 0 : Integer.parseInt(paramMap.get("couponId").toString());
        Integer userId = paramMap.get("userId") == null ? 0 : Integer.parseInt(paramMap.get("userId").toString());
        Integer num = paramMap.get("num") == null ? 1 : Integer.parseInt(paramMap.get("num").toString());

        MtCoupon couponInfo = couponService.queryCouponById(couponId);
        if (null == couponInfo) {
            throw new BusinessCheckException(Message.COUPON_NOT_EXIST);
        }

        // 卡券类型检查
        if (couponInfo.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
            throw new BusinessCheckException(Message.COUPON_TYPE_ERROR);
        }

        // 判断卡券是否有效
        boolean isCouponEffective = couponService.isCouponEffective(couponInfo);
        if (!isCouponEffective) {
            throw new BusinessCheckException(Message.COUPON_IS_EXPIRE);
        }

        MtCouponGroup groupInfo = couponGroupService.queryCouponGroupById(couponInfo.getGroupId());
        MtUser userInfo = memberService.queryMemberById(userId);
        if (null == userInfo) {
            throw new BusinessCheckException(Message.USER_NOT_EXIST);
        }

        // 是否已经领取
        List<String> statusList = Arrays.asList(UserCouponStatusEnum.UNUSED.getKey(), UserCouponStatusEnum.USED.getKey(), UserCouponStatusEnum.EXPIRE.getKey());
        List<MtUserCoupon> userCouponData = userCouponRepository.getUserCouponListByCouponId(userId, couponId, statusList);
        if ((userCouponData.size() >= couponInfo.getLimitNum()) && (couponInfo.getLimitNum() > 0)) {
            throw new BusinessCheckException(Message.MAX_COUPON_LIMIT);
        }

        // 积分不足以领取
        if (couponInfo.getPoint() != null && couponInfo.getPoint() > 0) {
            if (userInfo.getPoint() < couponInfo.getPoint()) {
                throw new BusinessCheckException(Message.POINT_LIMIT);
            }
        }

        // 可领取多张，领取序列号
        StringBuffer uuid = new StringBuffer();
        uuid.append(SeqUtil.getRandomNumber(4));
        uuid.append(SeqUtil.getRandomNumber(4));
        uuid.append(SeqUtil.getRandomNumber(4));
        uuid.append(SeqUtil.getRandomNumber(4));

        for (int i = 1; i <= num; i++) {
            MtUserCoupon userCoupon = new MtUserCoupon();

            userCoupon.setCouponId(couponInfo.getId());
            userCoupon.setType(couponInfo.getType());
            userCoupon.setAmount(couponInfo.getAmount());
            userCoupon.setGroupId(groupInfo.getId());
            userCoupon.setMobile(userInfo.getMobile());
            userCoupon.setUserId(userInfo.getId());
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
            userCoupon.setUuid(uuid.toString());

            userCouponRepository.save(userCoupon);
        }

        // 是否需要扣除相应积分
        if (couponInfo.getPoint() != null && couponInfo.getPoint() > 0) {
            MtPoint reqPointDto = new MtPoint();
            reqPointDto.setUserId(userId);
            reqPointDto.setAmount(-couponInfo.getPoint());
            reqPointDto.setDescription("领取"+ couponInfo.getName() + "扣除" +couponInfo.getPoint() +"积分");
            reqPointDto.setOperator("");
            pointService.addPoint(reqPointDto);
        }

        return true;
    }

    /**
     * 预存
     * @param paramMap
     * @return
     * */
    public boolean preStore(Map<String, Object> paramMap) throws BusinessCheckException {
        Integer couponId = paramMap.get("couponId") == null ? 0 : Integer.parseInt(paramMap.get("couponId").toString());
        Integer userId = paramMap.get("userId") == null ? 0 : Integer.parseInt(paramMap.get("userId").toString());
        String param = paramMap.get("param") == null ? "" : paramMap.get("param").toString();
        Integer orderId = paramMap.get("orderId") == null ? 0 : Integer.parseInt(paramMap.get("orderId").toString());

        if (StringUtils.isEmpty(param) || couponId <= 0 || userId <= 0) {
            throw new BusinessCheckException(Message.PARAM_ERROR);
        }

        MtCoupon couponInfo = couponService.queryCouponById(couponId);
        if (couponInfo == null) {
            throw new BusinessCheckException(Message.COUPON_NOT_EXIST);
        }

        MtUser userInfo = memberService.queryMemberById(userId);
        if (userInfo == null) {
            throw new BusinessCheckException(Message.USER_NOT_EXIST);
        }

        String[] paramArr = param.split(",");

        for (int i = 0; i < paramArr.length; i++) {
            String item = paramArr[i];
            if (StringUtils.isNotEmpty(item)) {
                String buyItem = paramArr[i]; // 100_200_1
                String[] buyItemArr = buyItem.split("_");
                if (StringUtils.isNotEmpty(buyItemArr[2])) {
                    Integer numInt = Integer.parseInt(buyItemArr[2]);
                    for (int j = 1; j <= numInt; j++) {
                        if (StringUtils.isNotEmpty(buyItemArr[1])) {
                            this.preStoreItem(couponInfo, userInfo, orderId, new BigDecimal(buyItemArr[1]));
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * 获取会员卡券列表
     * @param userId
     * @param status
     * @return
     * */
    @Override
    public List<MtUserCoupon> getUserCouponList(Integer userId, List<String> status) {
        return userCouponRepository.getUserCouponList(userId, status);
    }

    /**
     * 获取会员卡券列表
     * @param paramMap
     * @throws BusinessCheckException
     * */
    @Override
    @Transactional
    public ResponseObject getUserCouponList(Map<String, Object> paramMap) throws BusinessCheckException {
        Integer pageNumber = paramMap.get("pageNumber") == null ? Constants.PAGE_NUMBER : Integer.parseInt(paramMap.get("pageNumber").toString());
        Integer pageSize = paramMap.get("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(paramMap.get("pageSize").toString());
        String userId = paramMap.get("userId") == null ? "" : paramMap.get("userId").toString();
        String status =  paramMap.get("status") == null ? "" : paramMap.get("status").toString();
        String type =  paramMap.get("type") == null ? "": paramMap.get("type").toString();
        String mobile = paramMap.get("mobile") == null ? "" : paramMap.get("mobile").toString();
        String storeId = paramMap.get("storeId") == null ? "" : paramMap.get("storeId").toString();
        String couponId = paramMap.get("couponId") == null ? "" : paramMap.get("couponId").toString();
        String code = paramMap.get("code") == null ? "" : paramMap.get("code").toString();

        // 处理已失效
        if (pageNumber <= 1 && StringUtils.isNotEmpty(userId)) {
            List<String> statusList = Arrays.asList(UserCouponStatusEnum.UNUSED.getKey());
            List<MtUserCoupon> data = userCouponRepository.getUserCouponList(Integer.parseInt(userId), statusList);
            for (MtUserCoupon uc : data) {
                MtCoupon coupon = couponService.queryCouponById(uc.getCouponId());
                // 已过期
                if (coupon.getEndTime().before(new Date())) {
                    uc.setStatus(StatusEnum.EXPIRED.getKey());
                    uc.setUpdateTime(new Date());
                    userCouponRepository.save(uc);
                }
                // 已删除
                if (coupon.getStatus().equals(StatusEnum.DISABLE.getKey())) {
                    uc.setStatus(UserCouponStatusEnum.DISABLE.getKey());
                    uc.setUpdateTime(new Date());
                    userCouponRepository.save(uc);
                }
            }
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(pageNumber);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> searchParams = new HashedMap();
        if (StringUtils.isNotEmpty(status)) {
            searchParams.put("EQ_status", status);
        }
        if (StringUtils.isNotEmpty(userId)) {
            searchParams.put("EQ_userId", userId);
        }
        if (StringUtils.isNotEmpty(mobile)) {
            searchParams.put("EQ_mobile", mobile);
        }
        if (StringUtils.isNotEmpty(type)) {
            searchParams.put("EQ_type", type);
        }
        if (StringUtils.isNotEmpty(storeId)) {
            searchParams.put("EQ_storeId", storeId);
        }
        if (StringUtils.isNotEmpty(couponId)) {
            searchParams.put("EQ_couponId", couponId);
        }
        if (StringUtils.isNotEmpty(code)) {
            searchParams.put("EQ_code", code);
        }

        paginationRequest.setSearchParams(searchParams);
        paginationRequest.setSortColumn(new String[]{"id desc", "createTime desc"});
        PaginationResponse<MtUserCoupon> paginationResponse = userCouponRepository.findResultsByPagination(paginationRequest);

        List<MyCouponDto> dataList = new ArrayList<>();

        if (paginationResponse.getContent().size() > 0) {
            for (MtUserCoupon userCouponDto : paginationResponse.getContent()) {
                MtCoupon couponInfo = couponService.queryCouponById(userCouponDto.getCouponId());
                MtUser userInfo = memberService.queryMemberById(userCouponDto.getUserId());
                MtStore storeInfo = storeService.queryStoreById(userCouponDto.getStoreId());

                MyCouponDto dto = new MyCouponDto();
                dto.setId(userCouponDto.getId());
                dto.setName(couponInfo.getName());
                dto.setCode(userCouponDto.getCode());
                dto.setCouponId(couponInfo.getId());
                dto.setUseRule(couponInfo.getDescription());

                String image = couponInfo.getImage();
                String baseImage = settingService.getUploadBasePath();
                dto.setImage(baseImage + image);
                dto.setStatus(userCouponDto.getStatus());
                dto.setAmount(userCouponDto.getAmount());
                dto.setBalance(userCouponDto.getBalance());
                dto.setType(couponInfo.getType());
                dto.setUsedTime(userCouponDto.getUsedTime());
                dto.setCreateTime(userCouponDto.getCreateTime());
                dto.setUserInfo(userInfo);
                dto.setStoreInfo(storeInfo);

                boolean canUse = couponService.isCouponEffective(couponInfo);
                if (!userCouponDto.getStatus().equals(UserCouponStatusEnum.UNUSED.getKey())) {
                    canUse = false;
                }
                dto.setCanUse(canUse);

                String effectiveDate = DateUtil.formatDate(couponInfo.getBeginTime(), "yyyy.MM.dd") + "-" + DateUtil.formatDate(couponInfo.getEndTime(), "yyyy.MM.dd");
                dto.setEffectiveDate(effectiveDate);

                String tips = "";

                // 优惠券tips
                if (couponInfo.getType().equals(CouponTypeEnum.COUPON.getKey())) {
                    if (StringUtils.isNotEmpty(couponInfo.getOutRule()) && Integer.parseInt(couponInfo.getOutRule()) > 0) {
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
                    Long confirmNum = confirmLogService.getConfirmNum(userCouponDto.getId());
                    tips = "已集"+ confirmNum +"次，需集满" + couponInfo.getOutRule() + "次";
                }

                dto.setTips(tips);
                dataList.add(dto);
            }
        }

        Long total = paginationResponse.getTotalElements();
        PageRequest pageRequest = new PageRequest(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        Page page = new PageImpl(dataList, pageRequest, total.longValue());
        PaginationResponse<MyCouponDto> pageResponse = new PaginationResponse(page, MyCouponDto.class);
        pageResponse.setContent(page.getContent());
        pageResponse.setCurrentPage(pageResponse.getCurrentPage() + 1);
        pageResponse.setTotalPages(paginationResponse.getTotalPages());

        return getSuccessResult(pageResponse);
    }

    /**
     * 获取会员可支付使用的卡券
     * @param userId
     * @return
     * */
    @Override
    public List<CouponDto> getPayAbleCouponList(Integer userId) throws BusinessCheckException {
        List<String> statusList = Arrays.asList(UserCouponStatusEnum.UNUSED.getKey());
        List<MtUserCoupon> userCouponList = userCouponRepository.getUserCouponList(userId, statusList);
        List<CouponDto> dataList = new ArrayList<>();

        if (userCouponList.size() > 0) {
            for (MtUserCoupon userCoupon : userCouponList) {
                 MtCoupon couponInfo = couponService.queryCouponById(userCoupon.getCouponId());
                 CouponDto couponDto = new CouponDto();
                 couponDto.setId(couponInfo.getId());
                 couponDto.setUserCouponId(userCoupon.getId());
                 couponDto.setName(couponInfo.getName());
                 couponDto.setAmount(userCoupon.getAmount());
                 couponDto.setStatus(UserCouponStatusEnum.UNUSED.getKey());
                 boolean isEffective = couponService.isCouponEffective(couponInfo);
                 // 1.预存卡可用
                 if (isEffective && couponInfo.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
                     if (userCoupon.getBalance().compareTo(new BigDecimal("0")) > 0) {
                         couponDto.setType(CouponTypeEnum.PRESTORE.getValue());
                         couponDto.setAmount(userCoupon.getBalance());
                         dataList.add(couponDto);
                     }
                 } else if(isEffective && couponInfo.getType().equals(CouponTypeEnum.COUPON.getKey())) {
                     // 2.无门槛的优惠券可用
                     if (StringUtils.isEmpty(couponInfo.getOutRule())) {
                         couponDto.setType(CouponTypeEnum.COUPON.getValue());
                         dataList.add(couponDto);
                     }
                 }
            }
        }

        return dataList;
    }

    /**
     * 获取会员卡券详情
     * @param userId
     * @param couponId
     * @return
     * */
    @Override
    public  List<MtUserCoupon> getUserCouponDetail(Integer userId, Integer couponId) {
        return userCouponRepository.findUserCouponDetail(couponId, userId);
    }

    /**
     * 获取会员卡券详情
     * @param userCouponId
     * @return
     * */
    @Override
    public  MtUserCoupon getUserCouponDetail(Integer userCouponId) {
        return userCouponRepository.findOne(userCouponId);
    }

    /**
     * 预存单张
     * @param couponInfo
     * @param userInfo
     * @return
     * */
    private boolean preStoreItem(MtCoupon couponInfo, MtUser userInfo, Integer orderId, BigDecimal amount) {
        MtUserCoupon userCoupon = new MtUserCoupon();
        userCoupon.setCouponId(couponInfo.getId());
        userCoupon.setType(couponInfo.getType());
        userCoupon.setGroupId(couponInfo.getGroupId());
        userCoupon.setMobile(userInfo.getMobile());
        userCoupon.setUserId(userInfo.getId());
        userCoupon.setStatus(UserCouponStatusEnum.UNUSED.getKey());
        userCoupon.setCreateTime(new Date());
        userCoupon.setUpdateTime(new Date());

        userCoupon.setOrderId(orderId);
        userCoupon.setAmount(amount);
        userCoupon.setBalance(amount);

        // 12位随机数
        StringBuffer code = new StringBuffer();
        code.append(SeqUtil.getRandomNumber(4));
        code.append(SeqUtil.getRandomNumber(4));
        code.append(SeqUtil.getRandomNumber(4));
        code.append(SeqUtil.getRandomNumber(4));
        userCoupon.setCode(code.toString());
        userCoupon.setUuid(code.toString());

        userCouponRepository.save(userCoupon);
        return true;
    }
}

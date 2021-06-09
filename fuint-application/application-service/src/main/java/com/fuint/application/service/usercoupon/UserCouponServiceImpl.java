package com.fuint.application.service.usercoupon;

import com.fuint.application.config.Message;
import com.fuint.application.enums.CouponTypeEnum;
import com.fuint.application.enums.UserCouponStatusEnum;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.coupongroup.CouponGroupService;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.util.SeqUtil;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.*;
import com.fuint.application.dao.repositories.MtUserCouponRepository;
import com.fuint.application.BaseService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 会员卡券业务实现类
 * Created by zach on 2020/09/06.
 * Updated by zach on 2021/05/04
 */
@Service
public class UserCouponServiceImpl extends BaseService implements UserCouponService {

    private static final Logger log = LoggerFactory.getLogger(UserCouponServiceImpl.class);

    @Autowired
    private MtUserCouponRepository userCouponRepository;

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponGroupService couponGroupService;

    @Autowired
    private MemberService memberService;

    /**
     * 分页查询券列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtUserCoupon> queryUserCouponListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException {
        paginationRequest.setSortColumn(new String[]{"status asc", "id desc"});
        PaginationResponse<MtUserCoupon> paginationResponse = userCouponRepository.findResultsByPagination(paginationRequest);
        return paginationResponse;
    }

    /**
     * 会员领券(优惠券、集次卡)
     * @param paramMap
     * @return
     * */
    @Override
    public boolean receiveCoupon(Map<String, Object> paramMap) throws BusinessCheckException {
        Integer couponId = paramMap.get("couponId") == null ? 0 : Integer.parseInt(paramMap.get("couponId").toString());
        Integer userId = paramMap.get("userId") == null ? 0 : Integer.parseInt(paramMap.get("userId").toString());

        MtCoupon couponInfo = couponService.queryCouponById(couponId.longValue());
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

        MtCouponGroup groupInfo = couponGroupService.queryCouponGroupById(couponInfo.getGroupId().longValue());
        MtUser userInfo = memberService.queryMemberById(userId);
        if (null == userInfo) {
            throw new BusinessCheckException(Message.USER_NOT_EXIST);
        }

        // 是否已经领取
        List<String> statusList = Arrays.asList("A", "B", "C");
        List<MtUserCoupon> userCouponData = userCouponRepository.getUserCouponListByCouponId(userId, couponId, statusList);
        if (userCouponData.size() >= couponInfo.getLimitNum()) {
            throw new BusinessCheckException(Message.MAX_COUPON_LIMIT);
        }

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
        userCoupon.setUuid(code.toString());

        userCouponRepository.save(userCoupon);

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
        String selectNum = paramMap.get("selectNum") == null ? "" : paramMap.get("selectNum").toString();
        Integer orderId = paramMap.get("orderId") == null ? 0 : Integer.parseInt(paramMap.get("orderId").toString());

        if (StringUtils.isEmpty(selectNum) || couponId <= 0 || userId <= 0) {
            throw new BusinessCheckException(Message.PARAM_ERROR);
        }

        MtCoupon couponInfo = couponService.queryCouponById(couponId.longValue());
        if (null == couponInfo) {
            throw new BusinessCheckException(Message.COUPON_NOT_EXIST);
        }

        MtUser userInfo = memberService.queryMemberById(userId);
        if (null == userInfo) {
            throw new BusinessCheckException(Message.USER_NOT_EXIST);
        }

        String[] numArr = selectNum.split(",");
        String[] ruleArr = couponInfo.getInRule().split(",");

        if (numArr.length != ruleArr.length) {
            throw new BusinessCheckException(Message.PARAM_ERROR);
        }

        for (int i = 0; i < ruleArr.length; i++) {
            String num = numArr[i];
            if (StringUtils.isNotEmpty(num)) {
                Integer numInt = Integer.parseInt(num);
                for (int j = 1; j <= numInt; j++) {
                    String ruleItem = ruleArr[i]; // 100_200
                    String[] ruleItemArr = ruleItem.split("_");
                    if (StringUtils.isNotEmpty(ruleItemArr[1])) {
                        this.preStoreItem(couponInfo, userInfo, orderId, new BigDecimal(ruleItemArr[1]));
                    }
                }
            }
        }

        return true;
    }

    /**
     * 获取会员卡券列表
     * @param userId
     * @return
     * */
    @Override
    public List<MtUserCoupon> getUserCouponList(Integer userId, List<String> status) throws BusinessCheckException {
        return userCouponRepository.getUserCouponList(userId, status);
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

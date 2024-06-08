package com.fuint.module.schedule;

import com.fuint.common.enums.UserCouponStatusEnum;
import com.fuint.common.enums.WxMessageEnum;
import com.fuint.common.service.CouponService;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.UserCouponService;
import com.fuint.common.service.WeixinService;
import com.fuint.common.util.DateUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.mapper.MtUserCouponMapper;
import com.fuint.repository.model.MtCoupon;
import com.fuint.repository.model.MtUser;
import com.fuint.repository.model.MtUserCoupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.core.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import java.text.ParseException;
import java.util.*;

/**
 * 卡券到期处理定时任务
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@EnableScheduling
@Component("couponExpireJob")
public class CouponExpireJob {

    private Logger logger = LoggerFactory.getLogger(CouponExpireJob.class);

    /**
     * 会员卡券服务接口
     */
    @Autowired(required = false)
    private UserCouponService userCouponService;

    /**
     * 微信服务接口
     * */
    @Autowired(required = false)
    private WeixinService weixinService;

    /**
     * 卡券服务接口
     * */
    @Autowired(required = false)
    private CouponService couponService;

    /**
     * 会员服务接口
     * */
    @Autowired(required = false)
    private MemberService memberService;

    @Autowired(required = false)
    private MtUserCouponMapper mtUserCouponMapper;

    @Autowired
    private Environment environment;

    /**
     * 一次最多发送消息数量
     **/
    private int MAX_SEND_NUM = 50;

    @Scheduled(cron = "${couponExpire.job.time}")
    @Transactional(rollbackFor = Exception.class)
    public void dealCoupon() throws BusinessCheckException, ParseException {
        String theSwitch = environment.getProperty("couponExpire.job.switch");
        if (theSwitch != null && theSwitch.equals("1")) {
            logger.info("CouponExpireJobJobStart!!!");
            // 处理已过期消息
            Calendar calendarExpire = Calendar.getInstance();
            calendarExpire.add(Calendar.DATE, -365);
            Date dateTimeExpire = calendarExpire.getTime();
            String endTimeExpire = DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
            String startTimeExpire = DateUtil.formatDate(dateTimeExpire, "yyyy-MM-dd HH:mm:ss");
            List<MtUserCoupon> userCoupons = userCouponService.getUserCouponListByExpireTime(0, UserCouponStatusEnum.UNUSED.getKey(), startTimeExpire, endTimeExpire);
            if (userCoupons != null && userCoupons.size() > 0) {
                int dealNum = 0;
                for (MtUserCoupon mtUserCoupon : userCoupons) {
                    if (dealNum <= MAX_SEND_NUM) {
                        mtUserCoupon.setStatus(UserCouponStatusEnum.EXPIRE.getKey());
                        mtUserCoupon.setUpdateTime(new Date());
                        mtUserCouponMapper.updateById(mtUserCoupon);
                        dealNum++;
                    }
                }
            }

            // 获取3天内到期的会员卡券，发送通知消息
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 3);
            Date dateTime = calendar.getTime();
            String endTime = DateUtil.formatDate(dateTime, "yyyy-MM-dd HH:mm:ss");
            String startTime = DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
            List<MtUserCoupon> dataList = userCouponService.getUserCouponListByExpireTime(0, UserCouponStatusEnum.UNUSED.getKey(), startTime, endTime);
            if (dataList != null && dataList.size() > 0) {
                int dealNum = 0;
                for (MtUserCoupon mtUserCoupon : dataList) {
                    if (dealNum <= MAX_SEND_NUM) {
                        // 发送小程序订阅消息
                        MtCoupon couponInfo = couponService.queryCouponById(mtUserCoupon.getCouponId());
                        MtUser userInfo = null;
                        if (mtUserCoupon.getUserId() != null && mtUserCoupon.getUserId() > 0) {
                            userInfo = memberService.queryMemberById(mtUserCoupon.getUserId());
                        }

                        if (couponInfo != null && userInfo != null) {
                            mtUserCoupon.getUpdateTime();
                            Integer days = DateUtil.daysBetween(DateUtil.formatDate(mtUserCoupon.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"), endTime);
                            if (days > 1) {
                                Date now = new Date();
                                Date sendTime = new Date(now.getTime());
                                Map<String, Object> params = new HashMap<>();
                                String couponExpireTime = DateUtil.formatDate(mtUserCoupon.getExpireTime(), "yyyy-MM-dd HH:mm");
                                params.put("expireTime", couponExpireTime);
                                params.put("name", couponInfo.getName());
                                params.put("tips", "您的卡券即将到期，请留意~");
                                weixinService.sendSubscribeMessage(userInfo.getMerchantId(), userInfo.getId(), userInfo.getOpenId(), WxMessageEnum.COUPON_EXPIRE.getKey(), "pages/user/index", params, sendTime);
                                mtUserCoupon.setUpdateTime(new Date());
                                mtUserCouponMapper.updateById(mtUserCoupon);
                            }
                        }

                        dealNum++;
                    }
                }
            }

            logger.info("CouponExpireJobJobEnd!!!");
        }
    }
}

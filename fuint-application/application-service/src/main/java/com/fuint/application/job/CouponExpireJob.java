package com.fuint.application.job;

import com.fuint.application.dao.entities.MtCoupon;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.dao.entities.MtUserCoupon;
import com.fuint.application.dao.repositories.MtUserCouponRepository;
import com.fuint.application.enums.UserCouponStatusEnum;
import com.fuint.application.enums.WxMessageEnum;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.weixin.WeixinService;
import com.fuint.application.util.DateUtil;
import com.fuint.exception.BusinessCheckException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import com.fuint.application.service.usercoupon.UserCouponService;
import java.util.*;


/**
 * 卡券到期处理定时任务
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Configuration
@EnableScheduling
public class CouponExpireJob {

    private static final Logger logger = LoggerFactory.getLogger(CouponExpireJob.class);

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
    private MtUserCouponRepository userCouponRepository;

    @Autowired
    private Environment environment;

    /**
     * 一次最多发送消息数量
     **/
    private int MAX_SEND_NUM = 50;

    @Scheduled(cron = "${couponExpire.job.time}")
    public void scheduled() throws BusinessCheckException {
        String theSwitch = environment.getProperty("couponExpire.job.switch");
        if (theSwitch.equals("1")) {
            logger.debug("CouponExpireJobJobStart!!!");
            Map<String, Object> param = new HashMap<>();
            param.put("EQ_status", UserCouponStatusEnum.UNUSED.getKey());

            // 3天内到期的发送消息提醒
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 3);
            Date dateTime = calendar.getTime();
            String expireTime = DateUtil.formatDate(dateTime, "yyyy-MM-dd HH:mm:ss");
            param.put("LT_expireTime", expireTime);
            param.put("GT_expireTime", "2020-01-01 00:00:00");

            List<MtUserCoupon> dataList = userCouponService.getUserCouponListByParams(param);
            if (dataList.size() > 0) {
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
                            Date now = new Date();
                            Date sendTime = new Date(now.getTime());
                            Map<String, Object> params = new HashMap<>();
                            String couponExpireTime = DateUtil.formatDate(mtUserCoupon.getExpireime(), "yyyy-MM-dd HH:mm");
                            params.put("expireTime", couponExpireTime);
                            params.put("name", couponInfo.getName());
                            params.put("tips", "您的卡券即将到期，请留意~");
                            weixinService.sendSubscribeMessage(userInfo.getId(), userInfo.getOpenId(), WxMessageEnum.COUPON_EXPIRE.getKey(), "pages/user/index", params, sendTime);
                            mtUserCoupon.setExpireime(null);
                            userCouponRepository.save(mtUserCoupon);
                        }

                        dealNum++;
                    }
                }
            }

            logger.debug("CouponExpireJobJobEnd!!!");
        }
    }
}

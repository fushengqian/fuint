package com.fuint.application.service.token;

import com.fuint.application.dao.repositories.MtUserRepository;
import com.fuint.application.dto.AccountDto;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.util.MD5Util;
import com.fuint.application.util.TimeUtils;
import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.cache.redis.RedisTemplate;
import com.fuint.exception.BusinessCheckException;
import com.fuint.util.StringUtil;
import nl.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import com.fuint.application.dao.entities.MtUser;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Service
public class TokenService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MtUserRepository userRepository;

    /**
     * 生成token
     * */
    public String generateToken(String userAgent, String userId) {
        StringBuilder token = new StringBuilder();
        UserAgent userAgent1 = UserAgent.parseUserAgentString(userAgent);
        if (userAgent1.getOperatingSystem().isMobileDevice()) {
            token.append("MOBILE_");
        } else {
            token.append("PC_");
        }

        token.append(userId);

        // 加时间
        token.append(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + "_");

        // 加六位随机数
        token.append(new Random().nextInt((999999 - 111111 + 1)) + 111111);

        String result = MD5Util.getMD5(token.toString());

        return result;
    }

    /**
     * 保存token
     * */
    public void saveToken(String token, MtUser mtUser) {
        if (token == null || mtUser == null) {
            return;
        }
        // 如果是PC端，那么token保存24个小时
        if (token.startsWith("PC")) {
            redisTemplate.set(token, mtUser, 24 * 3600);
        } else {
            // 如果是移动端，那么token保存30天
            redisTemplate.set(token, mtUser, 24 * 30 * 3600);
        }
    }

    /**
     * 保存后台登录token
     * */
    @OperationServiceLog(description = "登录后台系统")
    public void saveAccountToken(String token, AccountDto tAccount) {
        if (token == null || StringUtil.isEmpty(token) || tAccount == null) {
            return;
        }
        redisTemplate.set(token, tAccount, 24 * 3600);
        return;
    }

    /**
     * 检查token是否存在 ，及登录状态
     * @param token
     * @return
     */
    public Boolean checkTokenLogin(String token) {
        if (token == null || StringUtil.isEmpty(token)) {
            return Boolean.FALSE;
        }

        if (this.redisTemplate.exists(token)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * 通过登录token获取用户登录信息
     * @param token
     * @return
     * */
    public MtUser getUserInfoByToken(String token) throws BusinessCheckException {
        if (token == null || StringUtil.isEmpty(token)) {
            return null;
        }

        MtUser mtUser = null;
        try {
            if (this.redisTemplate.exists(token)) {
                mtUser = this.redisTemplate.get(token, MtUser.class);
                if (mtUser != null) {
                    mtUser = memberService.queryMemberById(mtUser.getId());
                }
            }
        } catch (Exception e) {
            throw new BusinessCheckException("连接redis出错");
        }

        // 更新会员活跃时间
        if (mtUser != null) {
           if (!mtUser.getStatus().equals(StatusEnum.ENABLED.getKey())) {
               return null;
           }

           Date lastUpdateTime = mtUser.getUpdateTime();
           Long timestampLast = Long.valueOf(TimeUtils.date2timeStamp(lastUpdateTime));
           Long timestampNow = System.currentTimeMillis()/1000;
           Long minute = timestampNow - timestampLast;

           // 5分钟更新一次
           if (minute >= 300) {
               userRepository.updateActiveTime(mtUser.getId(), new Date());
           }
        }

        return mtUser;
    }

    /**
     * 通过登录token获取后台登录信息
     * @param token
     * @return
     * */
    public AccountDto getAccountInfoByToken(String token) throws BusinessCheckException {
        if (token == null || StringUtil.isEmpty(token)) {
            return null;
        }

        AccountDto accountDto = null;
        try {
            if (this.redisTemplate.exists(token)) {
                accountDto = this.redisTemplate.get(token, AccountDto.class);
            }
        } catch (Exception e) {
            throw new BusinessCheckException("连接redis出错");
        }

        return accountDto;
    }

    /**
     * 清除所有设备token缓存
     * */
    public Boolean removeToken(String token) {
        redisTemplate.removeLike(token);
        return Boolean.TRUE;
    }
}
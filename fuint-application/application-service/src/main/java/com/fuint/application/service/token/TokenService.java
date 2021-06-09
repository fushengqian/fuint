package com.fuint.application.service.token;

import com.fuint.cache.redis.RedisTemplate;
import com.fuint.exception.BusinessCheckException;
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

    // 生成token(格式为token:设备-加密的手机号-时间-六位随机数)
    public String generateToken(String userAgent, String mobile) {
        StringBuilder token = new StringBuilder();
        UserAgent userAgent1 = UserAgent.parseUserAgentString(userAgent);
        if (userAgent1.getOperatingSystem().isMobileDevice()) {
            token.append("MOBILE_");
        } else {
            token.append("PC_");
        }
        //加加密的手机号
        //token.append(DigestUtils.md5Hex(mobile) + "-");

        //不加密手机号，用于后台变更用户信息的时候，删除缓存(格式："MOBILE_13511111111" or "PC_13511111111"打头) 20190904
        token.append(mobile);
        //加时间
        token.append(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + "_");
        //加六位随机数111111-999999
        token.append(new Random().nextInt((999999 - 111111 + 1)) + 111111);
        System.out.println("token=>" + token.toString());
        return token.toString();
    }

    //保存token
    public void saveToken(String token, MtUser mtUser) {
        //如果是PC，那么token保存两个小时；
        if (token.startsWith("PC")) {
            //先删除redis key，再保存新key
            redisTemplate.removeLike("PC_"+mtUser.getMobile());
            redisTemplate.set(token, mtUser, 2 * 60 * 60);
        } else {
            //redisUtil.set(token, JSONObject.toJSONString(user));
            //先删除redis key，再保存新key
            redisTemplate.removeLike("MOBILE_"+mtUser.getMobile());
           // 如果是MOBILE,那么token保存48个小时；
            redisTemplate.set(token, mtUser, 48 * 60 * 60);
        }
    }

    //检查token是否存在 ，及登录状态
    public Boolean checkTokenLogin(String token) {
        if (this.redisTemplate.exists(token)) {
            //token超时检查?? 后续再加
            return Boolean.TRUE;
        }
        else
        {
            return Boolean.FALSE;
        }

    }

    // 检查token存在,则获取用户登录信息
    public MtUser getUserInfoByToken(String token) throws BusinessCheckException {
        MtUser mtUser = null;
        try {
            if (this.redisTemplate.exists(token)) {
                mtUser = this.redisTemplate.get(token, MtUser.class);
            }
        } catch (Exception e) {
            throw new BusinessCheckException("连接redis出错");
        }

        return mtUser;
    }


    //清除所有设备token缓存
    public Boolean removeTokenLikeMobile(String mobile) {
        //清缓存
        redisTemplate.removeLike("MOBILE_"+mobile);
        redisTemplate.removeLike("PC_"+mobile);
        return Boolean.TRUE;
    }

    //清除单独设备token缓存
    public Boolean removeTokenLikeMobile(String mobile,String direct) {
        redisTemplate.removeLike(direct+"_"+mobile);
        return Boolean.TRUE;
    }
}
package com.fuint.captcha.service;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.fuint.cache.redis.RedisTemplate;
import com.fuint.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;

/**
 * 图形验证码插件服务类
 *
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Service
public class CaptchaService {

    private static final Logger logger = LoggerFactory.getLogger(CaptchaService.class);

    @Resource
    private Producer captchaProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 生成图形验证码,并保存至Session
     * @param session Session
     * @return BufferedImage
     */
    public BufferedImage getCode(HttpSession session){
        //生成验证码
        String codeText = captchaProducer.createText();
        BufferedImage codeImage = captchaProducer.createImage(codeText);
        logger.info("生成验证码{}", codeText);

        //设置Session信息
        if(session != null){
            session.setAttribute(Constants.KAPTCHA_SESSION_KEY, codeText);
        }

        return codeImage;
    }

    /**
     * 生成图形验证码,并保存至Redis
     * @return BufferedImage
     */
    public BufferedImage getCodeByUuid(String uuid) {
        //生成验证码
        String codeText = captchaProducer.createText();
        BufferedImage codeImage = captchaProducer.createImage(codeText);
        logger.info("生成验证码{}", codeText);

        redisTemplate.set(uuid, codeText, 1800);

        return codeImage;
    }

    /**
     * 图形验证码校验
     * @param code 输入的验证码
     * @param session Session
     * @return Boolean
     */
    public Boolean checkCode(String code, HttpSession session){
        String sessionCode = (String) session.getAttribute(Constants.KAPTCHA_SESSION_KEY);
        if (StringUtil.isEmpty(code) || StringUtil.isEmpty(sessionCode)) {
            return false;
        } else {
            if (code.equalsIgnoreCase(sessionCode)) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 图形验证码校验
     * @param code 输入的验证码
     * @param uuid uuid
     * @return Boolean
     */
    public Boolean checkCodeByUuid(String code, String uuid){
        String vCode = redisTemplate.get(uuid, String.class);
        if (StringUtil.isEmpty(code) || StringUtil.isEmpty(vCode)) {
            return false;
        } else {
            if (code.equalsIgnoreCase(vCode)) {
                return true;
            } else {
                return false;
            }
        }
    }
}
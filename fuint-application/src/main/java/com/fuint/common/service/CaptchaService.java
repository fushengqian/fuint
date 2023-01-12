package com.fuint.common.service;

import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;

/**
 * 图形验证码插件服务类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface CaptchaService {

    /**
     * 生成图形验证码,并保存至Session
     * @param session Session
     * @return BufferedImage
     */
    BufferedImage getCode(HttpSession session);

    /**
     * 图形验证码校验
     * @param code 输入的验证码
     * @param session Session
     * @return Boolean
     */
    Boolean checkCode(String code, HttpSession session);

    /**
     * 生成图形验证码
     * @return BufferedImage
     */
    BufferedImage getCodeByUuid(String uuid);

    /**
     * 图形验证码校验
     * @param code 输入的验证码
     * @param uuid uuid
     * @return Boolean
     */
    Boolean checkCodeByUuid(String code, String uuid);

}
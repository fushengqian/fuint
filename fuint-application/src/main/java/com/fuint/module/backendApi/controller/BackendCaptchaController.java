package com.fuint.module.backendApi.controller;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fuint.common.service.CaptchaService;

/**
 * 图形验证码接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-图形验证码相关接口")
@Controller
@RequestMapping("/backendApi/captcha")
public class BackendCaptchaController {

    private static final Logger logger = LoggerFactory.getLogger(BackendCaptchaController.class);

    @Resource
    private CaptchaService captchaService;

    @RequestMapping(value="/getCode", method = RequestMethod.GET)
    public void getCode(HttpServletResponse response) {
        // 生成验证码
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        BufferedImage codeImage = captchaService.getCodeByUuid(uuid);

        // 输出验证码图像
        response.setDateHeader("Expires", 0);
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        response.setContentType("image/jpeg");
        ServletOutputStream out = null;

        try {
            out = response.getOutputStream();
            ImageIO.write(codeImage, "jpg", out);
            out.flush();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    @RequestMapping(value="/checkCode", method = RequestMethod.POST)
    @ResponseBody
    public String checkCode(@RequestParam String code, HttpServletRequest request) {
        String uuid = request.getParameter("uuid") == null ? "" : request.getParameter("uuid");
        Boolean flag = captchaService.checkCodeByUuid(code, uuid);
        if (flag) {
            return "success";
        } else {
            return "failed";
        }
    }
}

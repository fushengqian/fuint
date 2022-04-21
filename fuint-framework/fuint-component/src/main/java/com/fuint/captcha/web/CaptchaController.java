package com.fuint.captcha.web;

import com.fuint.captcha.service.CaptchaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 图形验证码插件控制类
 * Created by FSQ
 * Contact wx fsq_better
 */
@Controller
@RequestMapping("/captcha")
public class CaptchaController {
    private static final Logger logger = LoggerFactory.getLogger(CaptchaController.class);

    @Resource
    private CaptchaService captchaService;

    @RequestMapping("/getCode")
    public void getCode(HttpServletRequest request, HttpServletResponse response) {
        //生成验证码
        HttpSession session = request.getSession();
        BufferedImage codeImage = captchaService.getCode(session);

        //输出验证码图像
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

    @RequestMapping("/checkCode")
    @ResponseBody
    public String checkCode(@RequestParam String code, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Boolean flag = captchaService.checkCode(code, session);
        if (flag) {
            return "sucess";
        } else {
            return "faild";
        }
    }
}

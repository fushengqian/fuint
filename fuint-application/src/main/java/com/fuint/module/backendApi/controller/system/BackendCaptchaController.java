package com.fuint.module.backendApi.controller.system;

import com.fuint.common.service.CaptchaService;
import com.fuint.common.util.SeqUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 图形验证码接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name="管理端-图形验证码相关接口")
@Controller
@AllArgsConstructor
@RequestMapping("/backendApi/captcha")
public class BackendCaptchaController {

    private static final Logger logger = LoggerFactory.getLogger(BackendCaptchaController.class);

    private CaptchaService captchaService;

    @Operation(summary="获取图形验证码")
    @RequestMapping(value="/getCode", method = RequestMethod.GET)
    public void getCode(HttpServletResponse response) throws Exception {
        // 生成验证码
        String uuid = SeqUtil.getUUID();
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

    @Operation(summary="验证图形验证码")
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

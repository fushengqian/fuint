package com.fuint.application.web.rest;

import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;
import com.fuint.application.util.Base64Util;
import com.fuint.captcha.service.CaptchaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 图形验证码控制类
 * Created by fsq on 2021/04/27.
 */
@RestController
@RequestMapping("/rest/captcha")
public class CaptchaApiController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(CaptchaApiController.class);

    @Resource
    private CaptchaService captchaService;

    @RequestMapping("/getCode")
    @CrossOrigin
    public ResponseObject getCode(HttpServletRequest request, HttpServletResponse response) {
        String captcha = "";
        try {
            HttpSession session = request.getSession();
            BufferedImage image = captchaService.getCode(session);
            // 输出流
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", stream);
            captcha = new String(Base64Util.baseEncode(stream.toByteArray()), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Object> outParams = new HashMap<String, Object>();
        outParams.put("captcha", "data:image/jpg;base64,"+captcha);

        return getSuccessResult(outParams);
    }

    @RequestMapping("/checkCode")
    @CrossOrigin
    public ResponseObject checkCode(@RequestParam String code, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Boolean result = captchaService.checkCode(code, session);
        Map<String, Object> outParams = new HashMap<String, Object>();
        outParams.put("result", result);

        return getSuccessResult(outParams);
    }
}

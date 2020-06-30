package com.fuint.coupon.web.rest;

import com.fuint.exception.BusinessCheckException;
import com.fuint.coupon.dao.entities.MtCoupon;
import com.fuint.coupon.dao.entities.MtUserCoupon;
import com.fuint.coupon.dao.repositories.MtUserCouponRepository;
import com.fuint.coupon.service.coupon.CouponService;
import com.fuint.coupon.service.token.TokenService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import com.fuint.coupon.BaseController;
import com.fuint.coupon.ResponseObject;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fuint.coupon.dao.entities.MtUser;
import com.fuint.coupon.util.QRCodeUtil;
import com.fuint.coupon.util.Base64Util;
import com.fuint.coupon.util.SeqUtil;
import com.fuint.coupon.util.AESUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**-
 * 优惠券二维码controller
 * Created by zach on 2019/9/04.
 */
@RestController
@RequestMapping(value = "/rest/qrCode")
public class QrCodeController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(QrCodeController.class);

    @Autowired
    private MtUserCouponRepository userCouponRepository;

    /**
     * 优惠券服务接口
     */
    @Autowired
    private CouponService couponService;

    /**
     * Token服务接口
     */
    @Autowired
    private TokenService tokenService;

    @Autowired
    private Environment env;

    /**
     * 查询优惠券二维码
     *
     * @param param  Request对象
     */
    @RequestMapping(value = "/doGet", method = RequestMethod.GET)
    public ResponseObject doGet(HttpServletRequest request, @RequestParam Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("token");
        Integer id = param.get("id") == null ? 1 : Integer.parseInt(param.get("id").toString());
        int width = param.get("width") == null ? 800 : Integer.parseInt(param.get("width").toString());
        int height = param.get("height") == null ? 800 : Integer.parseInt(param.get("height").toString());

        // 参数有误
        if (id <= 0) {
            return getFailureResult(1002);
        }

        if (StringUtils.isEmpty(token)) {
            return getFailureResult(1001);
        }

        MtUser mtUser = tokenService.getUserInfoByToken(token);

        if (null == mtUser) {
            return getFailureResult(1001);
        }

        MtUserCoupon userCoupon = userCouponRepository.findOne(id);
        if (!mtUser.getId().equals(userCoupon.getUserId())) {
            return getFailureResult(1004);
        }

        MtCoupon couponInfo = couponService.queryCouponById(userCoupon.getCouponId().longValue());
        if (null == couponInfo) {
            return getFailureResult(1002);
        }

        ByteArrayOutputStream out = null;
        ResponseObject responseObject = null;
        try {
            // 如果超过两小时，重新生成code
            String rcode = userCoupon.getCode();
            if (couponService.codeExpired(rcode) && userCoupon.getStatus().equals("A")) {
                StringBuffer code = new StringBuffer();
                code.append(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                code.append(SeqUtil.getRandomNumber(15));

                userCoupon.setCode(code.toString());
                userCoupon.setUpdateTime(new Date());
                userCouponRepository.save(userCoupon);

                rcode = code.toString();
            }

            String website = env.getProperty("website.url");
            String content  = website + "/index.html#/result?code=" + rcode +"&time=" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

            // 生成并输出二维码
            out = new ByteArrayOutputStream();
            QRCodeUtil.createQrCode(out, content, width, height, "JPEG");

            // 对数据进行Base64编码，返回的码需加上：data:image/jpg;base64
            String img = new String(Base64Util.baseEncode(out.toByteArray()), "UTF-8");

            //组织返回参数
            Map<String, Object> outparams = new HashMap<String, Object>();
            outparams.put("img", img);
            outparams.put("money", couponInfo.getMoney());
            String tips = "";
            outparams.put("tips", tips);
            outparams.put("name", couponInfo.getName());

            responseObject = getSuccessResult(outparams);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            responseObject = getCustomrResult(500, "生成二维码异常", "");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        return getSuccessResult(responseObject.getData());
    }
}

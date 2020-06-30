package com.fuint.coupon.web.backend.home;

import com.fuint.coupon.FrameworkConstants;
import com.fuint.coupon.ResponseObject;
import com.fuint.exception.BusinessCheckException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 首页控制器
 * Created by zach on 2020-04-27
 */
@Controller
@RequestMapping(value = "/backend/home")
public class homeController {

    private static final Logger logger = LoggerFactory.getLogger(homeController.class);

    /**
     * 首页
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        return "home/index";
    }

    /**
     * 首页图表统计数据
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "/statistic")
    @ResponseBody
    public ResponseObject statistic(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {

        // prestore_in,prestore_spend,coupon_get,coupon_spend
        String tag = request.getParameter("tag");

        Map<String, Object> resultMap = new HashMap<>();

        String label[] = {"4月6日", "4月7日", "4月8日", "4月9日", "4月10日", "昨天", "今天"};

        int data[][]={{1000, 2900, 3080, 4101, 6800, 7900, 9002}, {1000, 3000, 4000, 7000, 8800, 9000, 10000}};

        resultMap.put("labels", label);
        resultMap.put("data", data);

        return new ResponseObject(FrameworkConstants.HTTP_RESPONSE_CODE_SUCCESS, "请求成功", resultMap);
    }
}

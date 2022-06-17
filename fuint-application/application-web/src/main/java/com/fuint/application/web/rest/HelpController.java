package com.fuint.application.web.rest;

import com.fuint.exception.BusinessCheckException;
import com.fuint.application.service.token.TokenService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;
import com.fuint.application.dao.entities.MtUser;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/**
 * 帮助相关controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/rest/help")
public class HelpController extends BaseController {

    /**
     * Token服务接口
     */
    @Autowired
    private TokenService tokenService;

    /**
     * 查询帮助内容列表
     *
     * @param request  Request对象
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        if (StringUtils.isEmpty(token)) {
            return getFailureResult(1001);
        }

        MtUser mtUser = tokenService.getUserInfoByToken(token);
        if (null == mtUser) {
            return getFailureResult(1001);
        }

        ArrayList<String> data = new ArrayList<>();

        return getSuccessResult(data);
    }
}

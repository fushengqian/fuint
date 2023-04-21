package com.fuint.module.clientApi.controller;

import com.fuint.common.dto.UserInfo;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * 帮助相关controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-帮助相关接口")
@RestController
@RequestMapping(value = "/clientApi/help")
public class ClientHelpController extends BaseController {

    /**
     * 查询帮助内容列表
     *
     * @param request  Request对象
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        if (StringUtil.isEmpty(token)) {
            return getFailureResult(1001);
        }

        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);
        if (null == mtUser) {
            return getFailureResult(1001);
        }

        ArrayList<String> data = new ArrayList<>();

        return getSuccessResult(data);
    }
}

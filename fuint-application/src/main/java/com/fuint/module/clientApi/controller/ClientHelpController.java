package com.fuint.module.clientApi.controller;

import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * 帮助相关controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name="会员端-帮助相关接口")
@RestController
@RequestMapping(value = "/clientApi/help")
public class ClientHelpController extends BaseController {

    /**
     * 查询帮助内容列表
     */
    @Operation(summary="查询帮助列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list() throws BusinessCheckException {
        ArrayList<String> data = new ArrayList<>();
        return getSuccessResult(data);
    }
}

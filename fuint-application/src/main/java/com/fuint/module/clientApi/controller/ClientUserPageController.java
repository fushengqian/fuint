package com.fuint.module.clientApi.controller;

import com.fuint.common.dto.decorate.UserPageDto;
import com.fuint.common.service.MerchantService;
import com.fuint.common.service.PageDecorateService;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.utils.StringUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 会员端个人中心接口controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name="会员端-个人中心相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/userPage")
public class ClientUserPageController extends BaseController {

    /**
     * 页面装修服务接口
     */
    private PageDecorateService pageDecorateService;

    /**
     * 商户服务接口
     */
    private MerchantService merchantService;

    /**
     * 获取个人中心配置
     */
    @Operation(summary="获取个人中心配置")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request) {
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        Integer storeId = StringUtil.isEmpty(request.getHeader("storeId")) ? 0 : Integer.parseInt(request.getHeader("storeId"));
        Integer merchantId = merchantService.getMerchantId(merchantNo);

        UserPageDto userPageDto = pageDecorateService.getUserPage(merchantId, storeId);
        if (userPageDto == null) {
            userPageDto = new UserPageDto();
        }
        return getSuccessResult(userPageDto);
    }
}

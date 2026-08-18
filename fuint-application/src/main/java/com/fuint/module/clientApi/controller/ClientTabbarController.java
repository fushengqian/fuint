package com.fuint.module.clientApi.controller;

import com.fuint.common.dto.decorate.TabbarDto;
import com.fuint.common.service.MerchantService;
import com.fuint.common.service.PageDecorateService;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 会员端底部导航接口controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-底部导航相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/tabbar")
public class ClientTabbarController extends BaseController {

    /**
     * 页面装修服务接口
     */
    private PageDecorateService pageDecorateService;

    /**
     * 商户服务接口
     */
    private MerchantService merchantService;

    /**
     * 获取底部导航配置
     */
    @ApiOperation(value = "获取底部导航配置")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request) {
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        Integer storeId = StringUtil.isEmpty(request.getHeader("storeId")) ? 0 : Integer.parseInt(request.getHeader("storeId"));
        Integer merchantId = merchantService.getMerchantId(merchantNo);

        TabbarDto tabbarDto = pageDecorateService.getTabbar(merchantId, storeId);
        if (tabbarDto == null) {
            tabbarDto = new TabbarDto();
        }
        return getSuccessResult(tabbarDto);
    }
}

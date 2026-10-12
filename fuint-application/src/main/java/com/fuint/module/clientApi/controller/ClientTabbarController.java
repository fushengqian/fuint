package com.fuint.module.clientApi.controller;

import com.fuint.common.dto.decorate.TabbarDto;
import com.fuint.common.service.MerchantService;
import com.fuint.common.service.PageDecorateService;
import com.fuint.common.service.SettingService;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.utils.StringUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 会员端底部导航接口controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name="会员端-底部导航相关接口")
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
     * 系统设置服务接口
     */
    private SettingService settingService;

    /**
     * 获取底部导航配置
     */
    @Operation(summary="获取底部导航配置")
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
        // 与其它图片接口保持一致：返回图片上传根路径，客户端用它补全图标相对路径
        Map<String, Object> result = new HashMap<>();
        result.put("tabbar", tabbarDto);
        result.put("imagePath", settingService.getUploadBasePath());
        return getSuccessResult(result);
    }
}

package com.fuint.module.backendApi.controller.system;

import com.fuint.common.dto.decorate.TabbarDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.service.PageDecorateService;
import com.fuint.common.service.SettingService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 底部导航设置管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags = "管理端-底部导航设置相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/tabbar")
public class BackendTabbarController extends BaseController {

    /**
     * 页面装修服务接口
     */
    private PageDecorateService pageDecorateService;

    /**
     * 系统设置服务接口
     */
    private SettingService settingService;

    /**
     * 获取底部导航配置
     */
    @ApiOperation(value = "获取底部导航配置")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('decorate:tabbar')")
    public ResponseObject info() throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Integer merchantId = accountInfo.getMerchantId() == null ? 0 : accountInfo.getMerchantId();
        Integer storeId = accountInfo.getStoreId() == null ? 0 : accountInfo.getStoreId();
        TabbarDto tabbarDto = pageDecorateService.getTabbar(merchantId, storeId);
        if (tabbarDto == null) {
            tabbarDto = new TabbarDto();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("tabbar", tabbarDto);
        result.put("imagePath", settingService.getUploadBasePath());
        return getSuccessResult(result);
    }

    /**
     * 保存底部导航配置
     */
    @ApiOperation(value = "保存底部导航配置")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('decorate:tabbar')")
    public ResponseObject save(@RequestBody TabbarDto tabbarDto) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        pageDecorateService.saveTabbar(tabbarDto, accountInfo);
        return getSuccessResult(true);
    }
}

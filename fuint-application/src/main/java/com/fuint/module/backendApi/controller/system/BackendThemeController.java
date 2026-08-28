package com.fuint.module.backendApi.controller.system;

import com.fuint.common.dto.decorate.ThemeDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.service.PageDecorateService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 主题设置管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags = "管理端-主题设置相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/theme")
public class BackendThemeController extends BaseController {

    /**
     * 页面装修服务接口
     */
    private PageDecorateService pageDecorateService;

    /**
     * 获取主题配置
     */
    @ApiOperation(value = "获取主题配置")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('decorate:theme')")
    public ResponseObject info() throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Integer merchantId = accountInfo.getMerchantId() == null ? 0 : accountInfo.getMerchantId();
        Integer storeId = accountInfo.getStoreId() == null ? 0 : accountInfo.getStoreId();
        ThemeDto themeDto = pageDecorateService.getTheme(merchantId, storeId);
        if (themeDto == null) {
            themeDto = new ThemeDto();
        }
        return getSuccessResult(themeDto);
    }

    /**
     * 保存主题配置
     */
    @ApiOperation(value = "保存主题配置")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('decorate:theme')")
    public ResponseObject save(@RequestBody ThemeDto themeDto) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        pageDecorateService.saveTheme(themeDto, accountInfo);
        return getSuccessResult(true);
    }
}

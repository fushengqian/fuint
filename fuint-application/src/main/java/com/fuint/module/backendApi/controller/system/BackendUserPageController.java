package com.fuint.module.backendApi.controller.system;

import com.fuint.common.dto.decorate.UserPageDto;
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
 * 个人中心设置管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags = "管理端-个人中心设置相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/userPage")
public class BackendUserPageController extends BaseController {

    /**
     * 页面装修服务接口
     */
    private PageDecorateService pageDecorateService;

    /**
     * 系统设置服务接口
     */
    private SettingService settingService;

    /**
     * 获取个人中心配置
     */
    @ApiOperation(value = "获取个人中心配置")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('decorate:userPage')")
    public ResponseObject info() throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Integer merchantId = accountInfo.getMerchantId() == null ? 0 : accountInfo.getMerchantId();
        Integer storeId = accountInfo.getStoreId() == null ? 0 : accountInfo.getStoreId();
        UserPageDto userPageDto = pageDecorateService.getUserPage(merchantId, storeId);
        if (userPageDto == null) {
            userPageDto = new UserPageDto();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("userPage", userPageDto);
        result.put("imagePath", settingService.getUploadBasePath());
        return getSuccessResult(result);
    }

    /**
     * 保存个人中心配置
     */
    @ApiOperation(value = "保存个人中心配置")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('decorate:userPage')")
    public ResponseObject save(@RequestBody UserPageDto userPageDto) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        pageDecorateService.saveUserPage(userPageDto, accountInfo);
        return getSuccessResult(true);
    }
}

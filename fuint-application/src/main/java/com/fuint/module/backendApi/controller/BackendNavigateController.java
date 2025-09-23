package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.NavigationDto;
import com.fuint.common.enums.SettingTypeEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.SettingService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtSetting;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 积分管理controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-积分相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/navigate")
public class BackendNavigateController extends BaseController {

    /**
     * 配置服务接口
     * */
    private SettingService settingService;

    /**
     * 导航设置详情
     */
    @ApiOperation(value = "导航设置详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(request.getHeader("Access-Token"));
        List<NavigationDto> navigation = settingService.getNavigation(accountInfo.getMerchantId(), accountInfo.getStoreId());
        Map<String, Object> result = new HashMap();
        result.put("navigation", navigation);
        return getSuccessResult(result);
    }

    /**
     * 提交导航设置
     */
    @ApiOperation(value = "提交导航设置")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject save(HttpServletRequest request, @RequestBody List<NavigationDto> navigation) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(request.getHeader("Access-Token"));
        if (accountInfo.getMerchantId() == null || accountInfo.getMerchantId() <= 0) {
            return getFailureResult(5002);
        }

        MtSetting mtSetting = new MtSetting();
        mtSetting.setMerchantId(accountInfo.getMerchantId());
        mtSetting.setStoreId(accountInfo.getStoreId());
        mtSetting.setType(SettingTypeEnum.NAVIGATION.getKey());
        mtSetting.setName(SettingTypeEnum.NAVIGATION.getKey());
        mtSetting.setValue("");
        mtSetting.setDescription(SettingTypeEnum.NAVIGATION.getValue());
        mtSetting.setStatus(StatusEnum.ENABLED.getKey());
        mtSetting.setOperator(accountInfo.getAccountName());
        Date dateTime = new Date();
        mtSetting.setUpdateTime(dateTime);
        mtSetting.setCreateTime(dateTime);

        settingService.saveSetting(mtSetting);
        return getSuccessResult(true);
    }
}

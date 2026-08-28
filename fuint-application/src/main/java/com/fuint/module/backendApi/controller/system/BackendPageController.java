package com.fuint.module.backendApi.controller.system;

import com.fuint.common.dto.decorate.PageDecorationDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.param.PagePage;
import com.fuint.common.service.PageDecorateService;
import com.fuint.common.service.SettingService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 页面装修管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags = "管理端-页面装修相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/page")
public class BackendPageController extends BaseController {

    /**
     * 页面装修服务接口
     */
    private PageDecorateService pageDecorateService;

    /**
     * 配置服务
     */
    private SettingService settingService;

    /**
     * 装修页面列表查询
     */
    @ApiOperation(value = "装修页面列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('decorate:page')")
    public ResponseObject list(@ModelAttribute PagePage pagePage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            pagePage.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            pagePage.setStoreId(accountInfo.getStoreId());
        }
        PaginationResponse<MtPage> paginationResponse = pageDecorateService.queryPageListByPagination(pagePage);

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        result.put("imagePath", settingService.getUploadBasePath());

        return getSuccessResult(result);
    }

    /**
     * 保存装修页面
     */
    @ApiOperation(value = "保存装修页面")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('decorate:page')")
    public ResponseObject saveHandler(@RequestBody PageDecorationDto pageDto) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        pageDecorateService.savePage(pageDto, accountInfo);
        return getSuccessResult(true);
    }

    /**
     * 获取装修页面详情
     */
    @ApiOperation(value = "获取装修页面详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('decorate:page')")
    public ResponseObject detail(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        PageDecorationDto pageDto = pageDecorateService.getPageDetail(id);
        if (pageDto == null) {
            return getFailureResult(1001);
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0
                && !pageDto.getMerchantId().equals(accountInfo.getMerchantId())) {
            return getFailureResult(1004);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("pageInfo", pageDto);
        result.put("imagePath", settingService.getUploadBasePath());

        return getSuccessResult(result);
    }

    /**
     * 设为默认页面
     */
    @ApiOperation(value = "设为默认页面")
    @RequestMapping(value = "/setDefault/{id}", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('decorate:page')")
    public ResponseObject setDefault(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        pageDecorateService.setDefaultPage(id, accountInfo);
        return getSuccessResult(true);
    }

    /**
     * 删除装修页面
     */
    @ApiOperation(value = "删除装修页面")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('decorate:page')")
    public ResponseObject delete(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        pageDecorateService.deletePage(id, accountInfo);
        return getSuccessResult(true);
    }
}

package com.fuint.module.clientApi.controller;

import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.BannerService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtBanner;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 页面接口controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-页面相关接口")
@RestController
@RequestMapping(value = "/clientApi/page")
public class ClientPageController extends BaseController {

    /**
     * Banner服务接口
     * */
    @Autowired
    private BannerService bannerService;

    /**
     * 获取页面数据
     */
    @ApiOperation(value = "获取首页页面数据")
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getPageData(HttpServletRequest request, @RequestParam Map<String, Object> param) throws BusinessCheckException {
        Integer storeId = request.getHeader("storeId") == null ? 0 : Integer.parseInt(request.getHeader("storeId"));

        Map<String, Object> params = new HashMap<>();
        params.put("status", StatusEnum.ENABLED.getKey());
        if (storeId > 0) {
            params.put("storeId", storeId);
        }
        List<MtBanner> bannerData = bannerService.queryBannerListByParams(params);

        Map<String, Object> outParams = new HashMap();
        outParams.put("banner", bannerData);
        return getSuccessResult(outParams);
    }
}

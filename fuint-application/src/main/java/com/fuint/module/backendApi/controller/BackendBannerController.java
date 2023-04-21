package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.common.Constants;
import com.fuint.common.dto.BannerDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.SettingService;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.common.service.BannerService;
import com.fuint.repository.model.MtBanner;
import com.fuint.repository.model.MtStore;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Banner管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-Banner相关接口")
@RestController
@RequestMapping(value = "/backendApi/banner")
public class BackendBannerController extends BaseController {

    /**
     * Banner服务接口
     */
    @Autowired
    private BannerService bannerService;

    /**
     * 系统设置服务接口
     * */
    @Autowired
    private SettingService settingService;

    /**
     * 店铺服务接口
     */
    @Autowired
    private StoreService storeService;

    /**
     * banner列表查询
     *
     * @param request  HttpServletRequest对象
     * @return banner列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String title = request.getParameter("title");
        String status = request.getParameter("status");
        String searchStoreId = request.getParameter("storeId");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        Integer storeId;
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        } else {
            storeId = accountInfo.getStoreId();
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        if (StringUtil.isNotEmpty(title)) {
            params.put("title", title);
        }
        if (StringUtil.isNotEmpty(status)) {
            params.put("status", status);
        }
        if (StringUtil.isNotEmpty(searchStoreId)) {
            params.put("storeId", searchStoreId);
        }
        if (storeId != null && storeId > 0) {
            params.put("storeId", storeId);
        }
        paginationRequest.setSearchParams(params);
        PaginationResponse<MtBanner> paginationResponse = bannerService.queryBannerListByPagination(paginationRequest);

        String imagePath = settingService.getUploadBasePath();
        Map<String, Object> paramsStore = new HashMap<>();
        paramsStore.put("status", StatusEnum.ENABLED.getKey());
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            paramsStore.put("storeId", accountInfo.getStoreId().toString());
        }
        List<MtStore> storeList = storeService.queryStoresByParams(paramsStore);

        Map<String, Object> result = new HashMap<>();
        result.put("dataList", paginationResponse);
        result.put("imagePath", imagePath);
        result.put("storeList", storeList);

        return getSuccessResult(result);
    }

    /**
     * 更新状态
     *
     * @return
     */
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject updateStatus(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String status = params.get("status") != null ? params.get("status").toString() : StatusEnum.ENABLED.getKey();
        Integer id = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        MtBanner mtBanner = bannerService.queryBannerById(id);
        if (mtBanner == null) {
            return getFailureResult(201);
        }

        String operator = accountInfo.getAccountName();

        BannerDto bannerDto = new BannerDto();
        bannerDto.setOperator(operator);
        bannerDto.setId(id);
        bannerDto.setStatus(status);
        bannerService.updateBanner(bannerDto);

        return getSuccessResult(true);
    }

    /**
     * 保存banner
     *
     * @param request  HttpServletRequest对象
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject saveHandler(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String id = params.get("id") == null ? "" : params.get("id").toString();
        String title = params.get("title") == null ? "" : params.get("title").toString();
        String description = params.get("description") == null ? "" : params.get("description").toString();
        String image = params.get("image") == null ? "" : params.get("image").toString();
        String url = params.get("url") == null ? "" : params.get("url").toString();
        String status = params.get("status") == null ? "" : params.get("status").toString();
        String storeId = params.get("storeId") == null ? "0" : params.get("storeId").toString();
        String sort = params.get("sort") == null ? "0" : params.get("sort").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        BannerDto info = new BannerDto();
        info.setTitle(title);
        info.setDescription(description);
        info.setImage(image);
        info.setUrl(url);
        info.setOperator(accountInfo.getAccountName());
        info.setStatus(status);
        info.setStoreId(Integer.parseInt(storeId));
        info.setSort(Integer.parseInt(sort));

        if (StringUtil.isNotEmpty(id)) {
            info.setId(Integer.parseInt(id));
            bannerService.updateBanner(info);
        } else {
            bannerService.addBanner(info);
        }

        return getSuccessResult(true);
    }

    /**
     * banner详情
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        MtBanner bannerInfo = bannerService.queryBannerById(id);
        String imagePath = settingService.getUploadBasePath();

        Map<String, Object> result = new HashMap<>();
        result.put("bannerInfo", bannerInfo);
        result.put("imagePath", imagePath);

        return getSuccessResult(result);
    }
}

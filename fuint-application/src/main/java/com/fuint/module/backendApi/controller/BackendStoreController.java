package com.fuint.module.backendApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.StoreDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.YesOrNoEnum;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
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
 * 店铺管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-店铺相关接口")
@RestController
@RequestMapping(value = "/backendApi/store")
public class BackendStoreController extends BaseController {

    /**
     * 店铺服务接口
     */
    @Autowired
    private StoreService storeService;

    /**
     * 分页查询店铺列表
     *
     * @param request  HttpServletRequest对象
     * @return 店铺列表
     */
    @RequestMapping(value = "/list")
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));

        String storeId = request.getParameter("id");
        String storeName = request.getParameter("name");
        String storeStatus = request.getParameter("status");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            storeId = accountInfo.getStoreId().toString();
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        if (StringUtil.isNotEmpty(storeId)) {
            params.put("storeId", storeId);
        }
        if (StringUtil.isNotEmpty(storeName)) {
            params.put("name", storeName);
        }
        if (StringUtil.isNotEmpty(storeStatus)) {
            params.put("status", storeStatus);
        }
        paginationRequest.setSearchParams(params);
        PaginationResponse<MtStore> paginationResponse = storeService.queryStoreListByPagination(paginationRequest);

        return getSuccessResult(paginationResponse);
    }

    /**
     * 查询店铺列表
     * */
    @RequestMapping(value = "/searchStore",  method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject searchStore(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String storeId = request.getParameter("id") == null ? "" : request.getParameter("id");
        String storeName = request.getParameter("name") == null ? "" : request.getParameter("name");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            storeId = accountInfo.getStoreId().toString();
        }

        Map<String, Object> params = new HashMap<>();
        if (StringUtil.isNotEmpty(storeId)) {
            params.put("storeId", storeId);
        }
        if (StringUtil.isNotEmpty(storeName)) {
            params.put("name", storeName);
        }

        params.put("status", StatusEnum.ENABLED.getKey());
        List<MtStore> storeList = storeService.queryStoresByParams(params);
        Map<String, Object> result = new HashMap<>();
        result.put("storeList", storeList);

        return getSuccessResult(result);
    }

    /**
     * 更新店铺状态
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/updateStatus")
    @CrossOrigin
    public ResponseObject updateStatus(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String status = params.get("status") != null ? params.get("status").toString() : StatusEnum.ENABLED.getKey();
        Integer storeId = params.get("storeId") == null ? 0 : Integer.parseInt(params.get("storeId").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        String operator = accountInfo.getAccountName();
        storeService.updateStatus(storeId, operator, status);

        return getSuccessResult(true);
    }

    /**
     * 保存店铺
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject saveHandler(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        StoreDto storeInfo = new StoreDto();
        String storeId = params.get("id").toString();
        String storeName = CommonUtil.replaceXSS(params.get("name").toString());
        String contact = CommonUtil.replaceXSS(params.get("contact").toString());
        String phone = CommonUtil.replaceXSS(params.get("phone").toString());
        String description = params.get("description") == null ? "" : CommonUtil.replaceXSS(params.get("description").toString());
        String isDefault = params.get("isDefault") == null ? YesOrNoEnum.NO.getKey() : CommonUtil.replaceXSS(params.get("isDefault").toString());
        String address = params.get("address") == null ? "" : CommonUtil.replaceXSS(params.get("address").toString());
        String hours = params.get("hours") == null ? "" : CommonUtil.replaceXSS(params.get("hours").toString());
        String latitude = params.get("latitude") == null ? "" : CommonUtil.replaceXSS(params.get("latitude").toString());
        String longitude = params.get("longitude") == null ? "" : CommonUtil.replaceXSS(params.get("longitude").toString());
        String wxMchId = params.get("wxMchId") == null ? "" : CommonUtil.replaceXSS(params.get("wxMchId").toString());
        String wxApiV2 = params.get("wxApiV2") == null ? "" : CommonUtil.replaceXSS(params.get("wxApiV2").toString());
        String status = params.get("status") != null ? params.get("status").toString() : StatusEnum.ENABLED.getKey();

        if ((StringUtil.isEmpty(latitude) || StringUtil.isEmpty(longitude)) && StringUtil.isNotEmpty(address)) {
            Map<String, Object> latAndLng = CommonUtil.getLatAndLngByAddress(address);
            latitude = latAndLng.get("lat").toString();
            longitude = latAndLng.get("lng").toString();
        }

        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            storeId = accountInfo.getStoreId().toString();
        }

        storeInfo.setName(storeName);
        storeInfo.setContact(contact);
        storeInfo.setPhone(phone);
        storeInfo.setDescription(description);
        storeInfo.setIsDefault(isDefault);
        storeInfo.setAddress(address);
        storeInfo.setHours(hours);
        storeInfo.setLatitude(latitude);
        storeInfo.setLongitude(longitude);
        storeInfo.setStatus(status);
        storeInfo.setWxMchId(wxMchId);
        storeInfo.setWxApiV2(wxApiV2);

        if (StringUtil.isEmpty(storeName)) {
            return getFailureResult(201, "店铺名称不能为空");
        } else {
            if (!StringUtil.isNotEmpty(storeName)) {
                StoreDto tempDto = storeService.queryStoreByName(storeName);
                if (null != tempDto && tempDto.getName().equals(storeName) && !tempDto.getId().equals(storeId)) {
                    return getFailureResult(201, "该店铺名称已经存在");
                }
            }
        }

        // 修改店铺
        if (StringUtil.isNotEmpty(storeId)) {
            storeInfo.setId(Integer.parseInt(storeId));
        }

        String operator = accountInfo.getAccountName();
        storeInfo.setOperator(operator);
        storeService.saveStore(storeInfo);

        return getSuccessResult(true);
    }

    /**
     * 店铺信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getStoreInfo(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        StoreDto storeInfo = storeService.queryStoreDtoById(id);

        Map<String, Object> result = new HashMap<>();
        result.put("storeInfo", storeInfo);

        return getSuccessResult(result);
    }
}

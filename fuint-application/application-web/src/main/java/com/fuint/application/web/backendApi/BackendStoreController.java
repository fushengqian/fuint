package com.fuint.application.web.backendApi;

import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;
import com.fuint.application.config.Constants;
import com.fuint.application.dto.AccountDto;
import com.fuint.application.service.token.TokenService;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.util.StringUtil;
import com.fuint.application.dao.entities.*;
import com.fuint.application.dto.MtStoreDto;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.store.StoreService;
import com.fuint.application.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 店铺管理类controller
 *
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/backendApi/store")
public class BackendStoreController extends BaseController {

    /**
     * 店铺服务接口
     */
    @Autowired
    private StoreService storeService;

    /**
     * 登录令牌接口
     * */
    @Autowired
    private TokenService tokenService;

    /**
     * 分页查询店铺列表
     *
     * @param request  HttpServletRequest对象
     * @return 店铺列表
     */
    @RequestMapping(value = "/list")
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));

        String storeId = request.getParameter("id");
        String storeName = request.getParameter("name");
        String storeStatus = request.getParameter("status");

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        if (StringUtil.isNotEmpty(storeId)) {
            params.put("EQ_id", storeId);
        }
        if (StringUtil.isNotEmpty(storeName)) {
            params.put("LIKE_name", storeName);
        }
        if (StringUtil.isNotEmpty(storeStatus)) {
            params.put("EQ_status", storeStatus);
        }

        params.put("NQ_status", StatusEnum.DISABLE.getKey());
        paginationRequest.setSearchParams(params);
        paginationRequest.setSortColumn(new String[]{"status asc", "isDefault desc"});
        PaginationResponse<MtStore> paginationResponse = storeService.queryStoreListByPagination(paginationRequest);

        return getSuccessResult(paginationResponse);
    }

    /**
     * 查询店铺列表
     * */
    @RequestMapping(value = "/searchStore",  method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject searchStore(HttpServletRequest request) throws BusinessCheckException {
        String storeId = request.getParameter("id") == null ? "" : request.getParameter("id");
        String storeName = request.getParameter("name") == null ? "" : request.getParameter("name");

        Map<String, Object> params = new HashMap<>();
        if (StringUtil.isNotEmpty(storeId)) {
            params.put("EQ_id", storeId);
        }
        if (StringUtil.isNotEmpty(storeName)) {
            params.put("LIKE_name", storeName);
        }

        params.put("EQ_status", StatusEnum.ENABLED.getKey());

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

        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
        }

        String operator = accountInfo.getAccountName();
        storeService.updateStatus(storeId, operator, status);

        return getSuccessResult(true);
    }

    /**
     * 保存店铺
     *
     * @param request  HttpServletRequest对象
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject saveHandler(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountDto accountInfo = tokenService.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "请先登录");
        }

        MtStoreDto storeInfo = new MtStoreDto();
        String storeId = params.get("id").toString();
        String storeName = CommonUtil.replaceXSS(params.get("name").toString());
        String contact = CommonUtil.replaceXSS(params.get("contact").toString());
        String phone = CommonUtil.replaceXSS(params.get("phone").toString());
        String description = params.get("description") == null ? "" : CommonUtil.replaceXSS(params.get("description").toString());
        String isDefault = params.get("isDefault") == null ? "N" : CommonUtil.replaceXSS(params.get("isDefault").toString());
        String address = params.get("address") == null ? "" : CommonUtil.replaceXSS(params.get("address").toString());
        String hours = params.get("hours") == null ? "" : CommonUtil.replaceXSS(params.get("hours").toString());
        String latitude = params.get("latitude") == null ? "" : CommonUtil.replaceXSS(params.get("latitude").toString());
        String longitude = params.get("longitude") == null ? "" : CommonUtil.replaceXSS(params.get("longitude").toString());
        String status = params.get("status") == null ? "" : CommonUtil.replaceXSS(params.get("status").toString());

        if ((StringUtil.isEmpty(latitude) || StringUtil.isEmpty(longitude)) && StringUtil.isNotEmpty(address)) {
            Map<String, Object> latAndLng = CommonUtil.getLatAndLngByAddress(address);
            latitude = latAndLng.get("lat").toString();
            longitude = latAndLng.get("lng").toString();
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

        if (StringUtil.isEmpty(storeName)) {
            return getFailureResult(201, "店铺名称不能为空");
        } else {
            MtStoreDto tempDto = null;
            try {
                if (!StringUtil.isNotEmpty(storeId)) {
                    tempDto = storeService.queryStoreByName(storeName);
                }
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (null != tempDto) {
                return getFailureResult(201, "该店铺名称已经存在");
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
    @RequestMapping(value = "/info/{id}")
    @CrossOrigin
    public ResponseObject getStoreInfo(@PathVariable("id") Integer id) throws BusinessCheckException {
        MtStoreDto storeInfo = null;

        try {
            storeInfo = storeService.queryStoreDtoById(id);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("storeInfo", storeInfo);

        return getSuccessResult(result);
    }
}

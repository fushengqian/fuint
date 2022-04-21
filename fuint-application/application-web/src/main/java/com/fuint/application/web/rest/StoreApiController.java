package com.fuint.application.web.rest;

import com.fuint.application.dao.entities.MtStore;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.store.StoreService;
import com.fuint.exception.BusinessCheckException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 店铺接口相关controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/rest/store")
public class StoreApiController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(MyCouponController.class);

    @Autowired
    private StoreService storeService;

    /**
     * 店铺列表
     *
     * @param request  Request对象
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String keyword = param.get("keyword") == null ? "" : param.get("keyword").toString();
        String latitude = request.getHeader("latitude") == null ? "" : request.getHeader("latitude");
        String longitude = request.getHeader("longitude") == null ? "" : request.getHeader("longitude");

        List<MtStore> storeList;
        if (StringUtils.isEmpty(latitude) || StringUtils.isEmpty(longitude)) {
            Map<String, Object> params = new HashMap<>();
            if (StringUtils.isNotEmpty(keyword)) {
                params.put("LIKE_name", keyword);
            }
            params.put("EQ_status", StatusEnum.ENABLED.getKey());
            storeList = storeService.queryStoresByParams(params);
        } else {
            storeList = storeService.queryByDistance(latitude, longitude);
        }

        Map<String, Object> outParams = new HashMap<>();
        outParams.put("data", storeList);

        return getSuccessResult(outParams);
    }

    /**
     * 店铺详情
     *
     * @param request  Request对象
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request) throws BusinessCheckException {
        Integer storeId = request.getHeader("storeId") == null ? 0 : Integer.parseInt(request.getHeader("storeId"));

        MtStore storeInfo = storeService.queryStoreById(storeId);

        Map<String, Object> outParams = new HashMap<>();
        outParams.put("storeInfo", storeInfo);

        return getSuccessResult(outParams);
    }
}

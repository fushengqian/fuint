package com.fuint.application.web.rest;

import com.fuint.application.dao.entities.MtStore;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.store.StoreService;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.service.token.TokenService;
import com.fuint.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;
import com.fuint.application.dao.entities.MtUser;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统接口相关controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/rest/system")
public class SystemController extends BaseController {

    @Autowired
    private StoreService storeService;

    @Autowired
    private TokenService tokenService;

    /**
     * 系统配置
     *
     * @param request  Request对象
     */
    @RequestMapping(value = "/config", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject config(HttpServletRequest request) throws BusinessCheckException {
        String userToken = request.getHeader("Access-Token");
        String storeId = request.getHeader("storeId") == null ? "" : request.getHeader("storeId");
        String latitude = request.getHeader("latitude") == null ? "" : request.getHeader("latitude");
        String longitude = request.getHeader("longitude") == null ? "" : request.getHeader("longitude");

        MtUser userInfo = tokenService.getUserInfoByToken(userToken);

        // 默认店铺，取会员之前选择的店铺
        MtStore storeInfo = null;
        if (userInfo != null) {
            if (userInfo.getStoreId() != null) {
                storeInfo = storeService.queryStoreById(userInfo.getStoreId());
            }
        }

        // 未登录先切换的店铺
        if (userInfo == null && StringUtil.isNotEmpty(storeId)) {
            storeInfo = storeService.queryStoreById(Integer.parseInt(storeId));
        }

        // 检查店铺是否已关闭
        if (storeInfo != null) {
            if (!storeInfo.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                storeInfo = null;
            }
        }

        // 取距离最近的
        if (storeInfo == null && StringUtil.isNotEmpty(latitude) && StringUtil.isNotEmpty(longitude)) {
            List<MtStore> storeList = storeService.queryByDistance(latitude, longitude);
            if (storeList.size() > 0) {
                storeInfo = storeList.get(0);
            }
        }

        // 最后取系统默认的
        if (storeInfo == null) {
            Map<String, Object> params = new HashMap<>();
            params.put("EQ_status", StatusEnum.ENABLED.getKey());
            params.put("EQ_isDefault", "Y");
            List<MtStore> storeList = storeService.queryStoresByParams(params);
            if (storeList.size() > 0) {
                storeInfo = storeList.get(0);
            } else {
                params.put("EQ_status", StatusEnum.ENABLED.getKey());
                List<MtStore> dataList = storeService.queryStoresByParams(params);
                if (dataList.size() > 0) {
                    storeInfo = dataList.get(0);
                }
            }
        }

        Map<String, Object> outParams = new HashMap<>();
        outParams.put("storeInfo", storeInfo);

        return getSuccessResult(outParams);
    }
}

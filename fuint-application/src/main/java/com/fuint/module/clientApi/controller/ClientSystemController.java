package com.fuint.module.clientApi.controller;

import com.fuint.common.dto.ParamDto;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.SettingService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtStore;
import com.fuint.repository.model.MtUser;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统接口相关controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-系统配置相关接口")
@RestController
@RequestMapping(value = "/clientApi/system")
public class ClientSystemController extends BaseController {

    @Autowired
    private StoreService storeService;

    /**
     * 会员服务接口
     */
    @Autowired
    private MemberService memberService;

    /**
     * 配置服务接口
     * */
    @Autowired
    private SettingService settingService;

    /**
     * 系统配置
     *
     * @param request Request对象
     */
    @RequestMapping(value = "/config", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject config(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String platform = request.getHeader("platform");
        String storeId = request.getHeader("storeId") == null ? "" : request.getHeader("storeId");
        String latitude = request.getHeader("latitude") == null ? "" : request.getHeader("latitude");
        String longitude = request.getHeader("longitude") == null ? "" : request.getHeader("longitude");

        UserInfo loginInfo = TokenUtil.getUserInfoByToken(token);

        // 默认店铺，取会员之前选择的店铺
        MtStore storeInfo = null;
        if (loginInfo != null) {
            MtUser mtUser = memberService.queryMemberById(loginInfo.getId());
            if (mtUser != null) {
                // 会员已禁用
                if (!mtUser.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                    return getFailureResult(1001);
                }
                if (mtUser.getStoreId() != null) {
                    storeInfo = storeService.queryStoreById(mtUser.getStoreId());
                }
            }
        }

        // 未登录先切换的店铺
        if (loginInfo == null && StringUtil.isNotEmpty(storeId)) {
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
            List<MtStore> storeList = storeService.queryByDistance("", latitude, longitude);
            if (storeList.size() > 0) {
                storeInfo = storeList.get(0);
            }
        }

        // 最后取系统默认的店铺
        if (storeInfo == null) {
            storeInfo = storeService.getDefaultStore();
        }

        // 支付方式列表
        List<ParamDto> payTypeList = settingService.getPayTypeList(platform);

        Map<String, Object> result = new HashMap<>();
        result.put("storeInfo", storeInfo);
        result.put("payTypeList", payTypeList);

        return getSuccessResult(result);
    }
}

package com.fuint.module.clientApi.controller;

import com.fuint.common.dto.ParamDto;
import com.fuint.common.dto.StoreInfo;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.MerchantService;
import com.fuint.common.service.SettingService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtMerchant;
import com.fuint.repository.model.MtStore;
import com.fuint.repository.model.MtUser;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
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
@AllArgsConstructor
@RequestMapping(value = "/clientApi/system")
public class ClientSystemController extends BaseController {

    /**
     * 店铺服务接口
     * */
    private StoreService storeService;

    /**
     * 会员服务接口
     */
    private MemberService memberService;

    /**
     * 配置服务接口
     * */
    private SettingService settingService;

    /**
     * 商户接口
     */
    private MerchantService merchantService;

    /**
     * 获取系统配置
     *
     * @param request Request对象
     */
    @ApiOperation(value = "获取系统配置")
    @RequestMapping(value = "/config", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject config(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String platform = request.getHeader("platform");
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        String storeId = request.getHeader("storeId") == null ? "" : request.getHeader("storeId");
        String latitude = request.getHeader("latitude") == null ? "" : request.getHeader("latitude");
        String longitude = request.getHeader("longitude") == null ? "" : request.getHeader("longitude");

        UserInfo loginInfo = TokenUtil.getUserInfoByToken(token);
        Integer merchantId = merchantService.getMerchantId(merchantNo);

        // 默认店铺，取会员之前选择的店铺
        MtStore mtStore = null;
        MtUser mtUser = null;
        if (loginInfo != null) {
            mtUser = memberService.queryMemberById(loginInfo.getId());
            if (mtUser != null) {
                // 会员已禁用
                if (!mtUser.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                    return getFailureResult(1001);
                }
                // 商户不同
                if (!mtUser.getMerchantId().equals(merchantId)) {
                    return getFailureResult(1001);
                }
            }
        }

        // 之前选择的店铺
        if (StringUtil.isNotEmpty(storeId)) {
            mtStore = storeService.queryStoreById(Integer.parseInt(storeId));
            // 店铺是否已关闭
            if (mtStore != null) {
                if (!mtStore.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                    mtStore = null;
                }
            }
        }

        // 取距离最近的
        if (mtStore == null && StringUtil.isNotEmpty(latitude) && StringUtil.isNotEmpty(longitude)) {
            List<StoreInfo> storeList = storeService.queryByDistance(merchantNo, "", latitude, longitude);
            if (storeList.size() > 0) {
                MtStore store = new MtStore();
                BeanUtils.copyProperties(storeList.get(0), store);
                mtStore = store;
            }
        }

        // 最后取系统默认的店铺
        if (mtStore == null) {
            mtStore = storeService.getDefaultStore(merchantNo);
        }

        // 完善会员的店铺信息
        if (mtUser != null && (mtUser.getStoreId() == null || mtUser.getStoreId() < 1)) {
            mtUser.setStoreId(mtStore.getId());
            mtUser.setUpdateTime(new Date());
            memberService.updateMember(mtUser, false);
        }

        StoreInfo storeInfo = new StoreInfo();
        if (mtStore != null) {
            BeanUtils.copyProperties(mtStore, storeInfo);
            MtMerchant mtMerchant = merchantService.queryMerchantById(mtStore.getMerchantId());
            if (mtMerchant != null) {
                storeInfo.setMerchantNo(mtMerchant.getNo());
            }
        } else {
            storeInfo = null;
        }

        // 支付方式列表
        List<ParamDto> payTypeList = settingService.getPayTypeList(merchantId, storeInfo.getId(), platform);

        Map<String, Object> result = new HashMap<>();
        result.put("storeInfo", storeInfo);
        result.put("payTypeList", payTypeList);

        return getSuccessResult(result);
    }
}

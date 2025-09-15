package com.fuint.module.merchantApi.controller;

import com.fuint.common.dto.MerchantSettingDto;
import com.fuint.common.dto.StaffDto;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.MerchantService;
import com.fuint.common.service.StaffService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 商户相关controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="商户端-商户设置相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/merchantApi/merchantSetting")
public class MerchantSettingController extends BaseController {

    /**
     * 会员服务接口
     * */
    private MemberService memberService;

    /**
     * 店铺员工服务接口
     * */
    private StaffService staffService;

    /**
     * 商户服务接口
     * */
    private MerchantService merchantService;

    /**
     * 查询商户设置信息
     */
    @ApiOperation(value = "查询商户设置信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request) throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfoByToken(request.getHeader("Access-Token"));
        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        StaffDto staffInfo = staffService.getStaffInfoByMobile(mtUser.getMobile());
        if (null == staffInfo) {
            return getFailureResult(1002, "该账号不是商户");
        }
        Map<String, Object> outParams = new HashMap<>();
        MerchantSettingDto merchantInfo = merchantService.getMerchantInfo(staffInfo.getMerchantId(), staffInfo.getStoreId());
        outParams.put("merchantInfo", merchantInfo);
        return getSuccessResult(outParams);
    }
}

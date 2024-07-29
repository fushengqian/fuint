package com.fuint.module.clientApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.CommissionRelationDto;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.ShareListParam;
import com.fuint.common.service.CommissionRelationService;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.MerchantService;
import com.fuint.common.service.WeixinService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtUser;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import java.lang.reflect.InvocationTargetException;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 邀请controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-邀请相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/share")
public class ClientShareController extends BaseController {

    private Environment env;

    /**
     * 分佣提成关系服务接口
     * */
    private CommissionRelationService commissionRelationService;

    /**
     * 微信相关服务接口
     * */
    private WeixinService weixinService;

    /**
     * 商户服务接口
     */
    private MerchantService merchantService;

    /**
     * 会员服务接口
     * */
    private MemberService memberService;

    /**
     * 获取邀请列表
     */
    @ApiOperation(value="获取邀请列表", notes="获取邀请列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request,  @RequestBody ShareListParam param) throws BusinessCheckException, InvocationTargetException, IllegalAccessException {
        Integer page = param.getPage() == null ? Constants.PAGE_NUMBER : param.getPage();
        Integer pageSize = param.getPageSize() == null ? Constants.PAGE_SIZE : param.getPageSize();
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");

        String token = request.getHeader("Access-Token");
        if (StringUtil.isEmpty(token)) {
            return getFailureResult(1001);
        }

        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);
        if (userInfo == null) {
            return getFailureResult(1001);
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        params.put("status", StatusEnum.ENABLED.getKey());
        params.put("userId", userInfo.getId());
        if (StringUtil.isNotEmpty(merchantNo)) {
            params.put("merchantNo", merchantNo);
        }
        paginationRequest.setSearchParams(params);
        PaginationResponse<CommissionRelationDto> paginationResponse = commissionRelationService.queryRelationByPagination(paginationRequest);

        Map<String, Object> outParams = new HashMap();
        String url = env.getProperty("website.url");
        outParams.put("url", url);
        outParams.put("paginationResponse", paginationResponse);

        return getSuccessResult(outParams);
    }

    /**
     * 生成小程序链接
     */
    @ApiOperation(value = "生成小程序链接")
    @RequestMapping(value = "/getMiniAppLink", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject getMiniAppLink(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);
        if (null == mtUser) {
            return getFailureResult(1001);
        }

        String path = param.get("path") == null ? "" : param.get("path").toString();
        String query = param.get("query") == null ? "" : param.get("query").toString();
        Integer merchantId = merchantService.getMerchantId(merchantNo);

        if (merchantId == null || merchantId <= 0) {
            MtUser userInfo = memberService.queryMemberById(mtUser.getId());
            if (userInfo != null) {
                merchantId = userInfo.getMerchantId();
            }
        }

        String link = weixinService.createMiniAppLink(merchantId, path + query);

        Map<String, Object> outParams = new HashMap();
        outParams.put("link", link);

        ResponseObject responseObject = getSuccessResult(outParams);
        return getSuccessResult(responseObject.getData());
    }
}

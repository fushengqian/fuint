package com.fuint.module.clientApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.CommissionRelationDto;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.ShareListParam;
import com.fuint.common.service.CommissionRelationService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import java.lang.reflect.InvocationTargetException;
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

    /**
     * 分佣提成关系服务接口
     * */
    private CommissionRelationService commissionRelationService;

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
        outParams.put("content", paginationResponse.getContent());
        outParams.put("pageSize", paginationResponse.getPageSize());
        outParams.put("pageNumber", paginationResponse.getCurrentPage());
        outParams.put("totalRow", paginationResponse.getTotalElements());
        outParams.put("totalPage", paginationResponse.getTotalPages());

        return getSuccessResult(outParams);
    }
}

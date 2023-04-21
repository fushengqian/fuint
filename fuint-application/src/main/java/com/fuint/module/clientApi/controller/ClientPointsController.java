package com.fuint.module.clientApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.PointDto;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.PointService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 积分相关controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-积分相关接口")
@RestController
@RequestMapping(value = "/clientApi/points")
public class ClientPointsController extends BaseController {

    /**
     * 积分服务接口
     */
    @Autowired
    private PointService pointService;

    /**
     * 查询我的积分明细
     *
     * @param request Request对象
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));

        if (StringUtil.isEmpty(token)) {
            return getFailureResult(1001);
        }

        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);
        if (null == mtUser) {
            return getFailureResult(1001);
        }

        Map<String, Object> param = new HashMap<>();

        param.put("userId", mtUser.getId());
        param.put("status", StatusEnum.ENABLED.getKey());

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setSearchParams(param);
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);
        PaginationResponse<PointDto> paginationResponse = pointService.queryPointListByPagination(paginationRequest);

        return getSuccessResult(paginationResponse);
    }

    /**
     * 转赠积分
     *
     * @param param Request对象
     */
    @RequestMapping(value = "/doGive", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doGive(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        String token = request.getHeader("Access-Token");

        if (StringUtil.isEmpty(token)) {
            return getFailureResult(1001);
        }

        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);

        if (null == mtUser) {
            return getFailureResult(1001);
        }

        String mobile = param.get("mobile") == null ? "" : param.get("mobile").toString();
        String remark = param.get("remark") == null ? "" : param.get("remark").toString();
        Integer amount = param.get("remark") == null ? 0 : Integer.parseInt(param.get("amount").toString());

        try {
            boolean result = pointService.doGift(mtUser.getId(), mobile, amount, remark);
            if (result) {
                return getSuccessResult(true);
            } else {
                return getFailureResult(3008, "转赠积分失败");
            }
        } catch (BusinessCheckException e) {
            return getFailureResult(3008, e.getMessage());
        }
    }
}

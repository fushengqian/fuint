package com.fuint.application.web.rest;

import com.fuint.application.dto.PointDto;
import com.fuint.application.enums.StatusEnum;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.service.point.PointService;
import com.fuint.application.service.token.TokenService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;
import com.fuint.application.dao.entities.MtUser;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 积分相关controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/rest/points")
public class PointsController extends BaseController {

    /**
     * 积分服务接口
     */
    @Autowired
    private PointService pointService;

    /**
     * Token服务接口
     */
    @Autowired
    private TokenService tokenService;

    /**
     * 查询我的积分明细
     *
     * @param request  Request对象
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        if (StringUtils.isEmpty(token)) {
            return getFailureResult(1001);
        }

        MtUser mtUser = tokenService.getUserInfoByToken(token);
        if (null == mtUser) {
            return getFailureResult(1001);
        }

        Map<String, Object> param = new HashMap<>();

        param.put("EQ_userId", mtUser.getId()+"");
        param.put("EQ_status", StatusEnum.ENABLED.getKey());

        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        paginationRequest.setSearchParams(param);
        PaginationResponse<PointDto> paginationResponse = pointService.queryPointListByPagination(paginationRequest);

        return getSuccessResult(paginationResponse);
    }

    /**
     * 转赠积分
     *
     * @param param  Request对象
     */
    @RequestMapping(value = "/doGive", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doGive(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        if (StringUtils.isEmpty(token)) {
            return getFailureResult(1001);
        }

        MtUser mtUser = tokenService.getUserInfoByToken(token);

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

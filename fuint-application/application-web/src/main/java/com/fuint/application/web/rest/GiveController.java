package com.fuint.application.web.rest;

import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.MtGive;
import com.fuint.application.dao.entities.MtVerifyCode;
import com.fuint.application.dto.GiveDto;
import com.fuint.application.service.give.GiveService;
import com.fuint.application.service.token.TokenService;
import com.fuint.application.service.verifycode.VerifyCodeService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * 转赠功能controller
 * Created by zach on 2019/10/09.
 */
@RestController
@RequestMapping(value = "/rest/give")
public class GiveController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(GiveController.class);

    /**
     * 转赠服务接口
     */
    @Autowired
    private GiveService giveService;

    /**
     * Token服务接口
     */
    @Autowired
    private TokenService tokenService;

    /**
     * 验证码信息管理接口
     */
    @Autowired
    private VerifyCodeService verifyCodeService;

    /**
     * 转赠卡券
     *
     * @param param  Request对象
     */
    @RequestMapping(value = "/doGive", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doGive(HttpServletRequest request, @RequestParam Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        if (StringUtils.isEmpty(token)) {
            return getFailureResult(1001);
        }

        MtUser mtUser = tokenService.getUserInfoByToken(token);

        if (null == mtUser) {
            return getFailureResult(1001);
        }
        param.put("userId", mtUser.getId());

        try {
            String vcode = param.get("vcode") == null ? "" : param.get("vcode").toString();
            if (StringUtils.isEmpty(vcode)) {
                return getFailureResult(3001, "验证码不能为空");
            }

            MtVerifyCode mtVerifyCode = verifyCodeService.checkVerifyCode(mtUser.getMobile(), vcode);
            if (null != mtVerifyCode) {
                verifyCodeService.updateValidFlag(mtVerifyCode.getId(),"1");
            } else {
                return getFailureResult(3002, "验证码有误");
            }

            ResponseObject result = giveService.addGive(param);

            return getSuccessResult(result.getData());
        } catch (BusinessCheckException e) {
            return getFailureResult(3008, e.getMessage());
        }
    }

    /**
     * 查询转赠记录
     *
     * @param request  Request对象
     */
    @RequestMapping(value = "/giveLog", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject giveLog(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        if (StringUtils.isEmpty(token)) {
            return getFailureResult(1001);
        }

        MtUser mtUser = tokenService.getUserInfoByToken(token);

        if (null == mtUser) {
            return getFailureResult(1001);
        }

        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);

        String mobile = request.getParameter("mobile") == null ? "" : request.getParameter("mobile").toString();
        String type = request.getParameter("type") == null ? "give" : request.getParameter("type").toString();
        String pageNumber = request.getParameter("pageNumber") == null ? "1" : request.getParameter("pageNumber").toString();
        String pageSize = request.getParameter("pageSize") == null ? "20" : request.getParameter("pageSize").toString();
        paginationRequest.setCurrentPage(Integer.parseInt(pageNumber));
        paginationRequest.setPageSize(Integer.parseInt(pageSize));

        if (type.equals("gived")) {
            paginationRequest.getSearchParams().put("EQ_userId", mtUser.getId().toString());
        } else {
            paginationRequest.getSearchParams().put("EQ_giveUserId", mtUser.getId().toString());
        }

        if (StringUtils.isNotEmpty(mobile) && type.equals("give")) {
            paginationRequest.getSearchParams().put("EQ_mobile", mobile);
        } else if(StringUtils.isNotEmpty(mobile) && type.equals("gived")) {
            paginationRequest.getSearchParams().put("EQ_userMobile", mobile);
        }

        PaginationResponse<GiveDto> paginationResponse = giveService.queryGiveListByPagination(paginationRequest);

        ResponseObject responseObject;
        Map<String, Object> outParams = new HashMap();
        outParams.put("dataList", paginationResponse.getContent());
        outParams.put("pageSize", paginationResponse.getPageSize());
        outParams.put("pageNumber", paginationResponse.getCurrentPage());
        outParams.put("totalRow", paginationResponse.getTotalElements());
        outParams.put("totalPage", paginationResponse.getTotalPages());

        responseObject = getSuccessResult(outParams);

        return getSuccessResult(responseObject.getData());
    }
}


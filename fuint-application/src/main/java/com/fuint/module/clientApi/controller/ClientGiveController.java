package com.fuint.module.clientApi.controller;

import com.fuint.common.dto.GiveDto;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.service.GiveService;
import com.fuint.common.service.MemberService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtUser;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 卡券转赠controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-卡券转赠相关接口")
@RestController
@RequestMapping(value = "/clientApi/give")
public class ClientGiveController extends BaseController {

    /**
     * 转赠服务接口
     */
    @Autowired
    private GiveService giveService;

    /**
     * 会员服务接口
     * */
    @Autowired
    private MemberService memberService;

    /**
     * 转赠卡券
     *
     * @param param  Request对象
     */
    @RequestMapping(value = "/doGive", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doGive(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        if (StringUtil.isEmpty(token)) {
            return getFailureResult(1001);
        }

        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);
        if (userInfo == null) {
            return getFailureResult(1001);
        }
        MtUser mtUser = memberService.queryMemberById(userInfo.getId());

        param.put("userId", mtUser.getId());
        param.put("storeId", mtUser.getStoreId());

        try {
            /*
            String vcode = param.get("vcode") == null ? "" : param.get("vcode").toString();
            if (StringUtil.isEmpty(vcode)) {
                return getFailureResult(3001, "验证码不能为空");
            }
            MtVerifyCode mtVerifyCode = verifyCodeService.checkVerifyCode(mtUser.getMobile(), vcode);
            if (null != mtVerifyCode) {
                verifyCodeService.updateValidFlag(mtVerifyCode.getId(),"1");
            } else {
                return getFailureResult(3002, "验证码有误");
            }*/
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
    public ResponseObject giveLog(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        if (StringUtil.isEmpty(token)) {
            return getFailureResult(1001);
        }

        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);

        if (null == mtUser) {
            return getFailureResult(1001);
        }

        String mobile = request.getParameter("mobile") == null ? "" : request.getParameter("mobile");
        String type = request.getParameter("type") == null ? "give" : request.getParameter("type");
        String pageNumber = request.getParameter("pageNumber") == null ? "1" : request.getParameter("pageNumber");
        String pageSize = request.getParameter("pageSize") == null ? "20" : request.getParameter("pageSize");

        PaginationRequest paginationRequest = new PaginationRequest();
        Map<String, Object> searchParams = new HashMap<>();
        paginationRequest.setCurrentPage(Integer.parseInt(pageNumber));
        paginationRequest.setPageSize(Integer.parseInt(pageSize));

        if (type.equals("gived")) {
            searchParams.put("userId", mtUser.getId());
        } else {
            searchParams.put("giveUserId", mtUser.getId());
        }

        if (StringUtil.isNotEmpty(mobile) && type.equals("give")) {
            searchParams.put("mobile", mobile);
        } else if(StringUtil.isNotEmpty(mobile) && type.equals("gived")) {
            searchParams.put("userMobile", mobile);
        }
        paginationRequest.setSearchParams(searchParams);
        PaginationResponse<GiveDto> paginationResponse = giveService.queryGiveListByPagination(paginationRequest);

        ResponseObject responseObject;
        Map<String, Object> outParams = new HashMap();
        outParams.put("content", paginationResponse.getContent());
        outParams.put("pageSize", paginationResponse.getPageSize());
        outParams.put("pageNumber", paginationResponse.getCurrentPage());
        outParams.put("totalRow", paginationResponse.getTotalElements());
        outParams.put("totalPage", paginationResponse.getTotalPages());

        responseObject = getSuccessResult(outParams);

        return getSuccessResult(responseObject.getData());
    }
}


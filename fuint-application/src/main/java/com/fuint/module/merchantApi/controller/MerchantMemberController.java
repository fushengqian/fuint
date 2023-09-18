package com.fuint.module.merchantApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.UserDto;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.MemberListParam;
import com.fuint.common.service.*;
import com.fuint.common.util.DateUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="商户端-会员管理相关接口")
@RestController
@RequestMapping(value = "/merchantApi/member")
public class MerchantMemberController extends BaseController {

    /**
     * 会员服务接口
     */
    @Autowired
    private MemberService memberService;

    /**
     * 会员等级服务接口
     * */
    @Autowired
    private UserGradeService userGradeService;

    /**
     * 店铺员工服务接口
     * */
    @Autowired
    private StaffService staffService;

    /**
     * 会员列表查询
     *
     * @param  request HttpServletRequest对象
     * @return 会员列表
     */
    @ApiOperation(value = "查询会员列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request, @RequestBody MemberListParam memberListParam) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String mobile = memberListParam.getMobile();
        String userId = memberListParam.getId();
        String name = memberListParam.getName();
        String birthday = memberListParam.getBirthday();
        String userNo = memberListParam.getUserNo();
        String gradeId = memberListParam.getGradeId();
        String orderBy = memberListParam.getOrderBy() == null ? "" : memberListParam.getOrderBy();
        String regTime = memberListParam.getRegTime() == null ? "" : memberListParam.getRegTime();
        String activeTime = memberListParam.getActiveTime() == null ? "" : memberListParam.getActiveTime();
        String memberTime = memberListParam.getMemberTime() == null ? "" : memberListParam.getMemberTime();
        String status = memberListParam.getStatus();
        String dataType = memberListParam.getDataType();
        Integer page = memberListParam.getPage() == null ? Constants.PAGE_NUMBER : memberListParam.getPage();
        Integer pageSize = memberListParam.getPageSize() == null ? Constants.PAGE_SIZE : memberListParam.getPageSize();

        // 今日注册、今日活跃
        if (dataType.equals("todayRegister")) {
            regTime = DateUtil.formatDate(new Date(), "yyyy-MM-dd") + " 00:00:00~" + DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
        } else if (dataType.equals("todayActive")) {
            activeTime = DateUtil.formatDate(new Date(), "yyyy-MM-dd") + " 00:00:00~" + DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
        }

        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);
        if (userInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        MtStaff staffInfo = null;
        if (mtUser != null && mtUser.getMobile() != null) {
            staffInfo = staffService.queryStaffByMobile(mtUser.getMobile());
        }
        if (staffInfo == null) {
            return getFailureResult(1002, "该账号不是商户");
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        if (staffInfo.getStoreId() != null && staffInfo.getStoreId() > 0) {
            params.put("storeId", staffInfo.getStoreId());
        }
        if (StringUtil.isNotEmpty(userId)) {
            params.put("id", userId);
        }
        if (StringUtil.isNotEmpty(name)) {
            params.put("name", name);
        }
        if (StringUtil.isNotEmpty(mobile)) {
            params.put("mobile", mobile);
        }
        if (StringUtil.isNotEmpty(birthday)) {
            params.put("birthday", birthday);
        }
        if (StringUtil.isNotEmpty(userNo)) {
            params.put("userNo", userNo);
        }
        if (StringUtil.isNotEmpty(gradeId)) {
            params.put("gradeId", gradeId);
        }
        if (StringUtil.isNotEmpty(status)) {
            params.put("status", status);
        }

        // 注册时间比对
        if (StringUtil.isNotEmpty(regTime)) {
            params.put("regTime", regTime);
        }

        // 活跃时间比对
        if (StringUtil.isNotEmpty(activeTime)) {
            params.put("activeTime", activeTime);
        }

        // 会员有效期比对
        if (StringUtil.isNotEmpty(memberTime)) {
            params.put("memberTime", memberTime);
        }

        // 会员排序方式
        if (StringUtil.isNotEmpty(orderBy)) {
            if (orderBy.equals("balance")) {
                paginationRequest.setSortColumn(new String[]{"balance desc"});
            } else if (orderBy.equals("point")) {
                paginationRequest.setSortColumn(new String[]{"point desc"});
            } else if (orderBy.equals("memberGrade")) {
                paginationRequest.setSortColumn(new String[]{"gradeId desc"});
            } else if (orderBy.equals("payAmount")) {
                paginationRequest.setSortColumn(new String[]{"balance desc"});
            } else if (orderBy.equals("memberTime")) {
                paginationRequest.setSortColumn(new String[]{"endTime desc", "gradeId desc"});
                MtUserGrade defaultGrade = userGradeService.getInitUserGrade(mtUser.getMerchantId());
                if (defaultGrade != null) {
                    params.put("gradeId", defaultGrade.getId().toString());
                }
            }
        }
        paginationRequest.setSearchParams(params);
        PaginationResponse<UserDto> paginationResponse = memberService.queryMemberListByPagination(paginationRequest);

        // 会员等级列表
        Map<String, Object> param = new HashMap<>();
        param.put("status", StatusEnum.ENABLED.getKey());
        List<MtUserGrade> userGradeList = memberService.queryMemberGradeByParams(param);

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        result.put("userGradeList", userGradeList);

        return getSuccessResult(result);
    }

    /**
     * 会员详情
     *
     * @param request
     * @param id 会员ID
     * @return
     */
    @ApiOperation(value = "查询会员详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        MtUser mtUserInfo = memberService.queryMemberById(id);

        Map<String, Object> param = new HashMap<>();
        List<MtUserGrade> userGradeList = memberService.queryMemberGradeByParams(param);

        Map<String, Object> result = new HashMap<>();
        result.put("userGradeList", userGradeList);
        result.put("memberInfo", mtUserInfo);

        return getSuccessResult(result);
    }
}

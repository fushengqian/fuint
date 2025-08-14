package com.fuint.module.merchantApi.controller;

import com.fuint.common.dto.UserInfo;
import com.fuint.common.param.StaffParam;
import com.fuint.common.service.MerchantService;
import com.fuint.common.service.StaffService;
import com.fuint.common.service.MemberService;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.module.merchantApi.request.StaffListRequest;
import com.fuint.repository.model.MtStaff;
import com.fuint.repository.model.MtUser;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 商户员工管理接口controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="商户端-员工相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/merchantApi/staff")
public class MerchantStaffController extends BaseController {

    /**
     * 会员服务接口
     */
    private MemberService memberService;

    /**
     * 店铺员工服务接口
     * */
    private StaffService staffService;

    /**
     * 商户服务接口
     * */
    private MerchantService merchantService;

    @ApiOperation(value = "员工列表")
    @RequestMapping(value = "/staffList", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject staffList(HttpServletRequest request, @RequestBody StaffListRequest requestParams) throws BusinessCheckException {
        Integer merchantId = merchantService.getMerchantId(request.getHeader("merchantNo"));

        UserInfo userInfo = TokenUtil.getUserInfoByToken(request.getHeader("Access-Token"));
        MtUser mtUser = memberService.queryMemberById(userInfo.getId());

        if (mtUser == null || StringUtil.isBlank(mtUser.getMobile())) {
            return getFailureResult(201, "该账号不是商户");
        }

        MtStaff staff = staffService.queryStaffByMobile(mtUser.getMobile());
        if (staff == null || !merchantId.equals(staff.getMerchantId())) {
            return getFailureResult(201, "您没有操作权限");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("merchantId", staff.getMerchantId());
        if (staff.getStoreId() != null && staff.getStoreId() > 0) {
            params.put("storeId", staff.getStoreId());
        }
        if (StringUtil.isNotEmpty(requestParams.getName())) {
            params.put("name", requestParams.getName());
        }
        if (StringUtil.isNotEmpty(requestParams.getMobile())) {
            params.put("mobile", requestParams.getMobile());
        }

        PaginationResponse paginationResponse = staffService.queryStaffListByPagination(new PaginationRequest(requestParams.getPage(), requestParams.getPageSize(), params));
        Map<String, Object> result = new HashMap<>();
        result.put("staffList", paginationResponse.getContent());
        result.put("pageSize", paginationResponse.getPageSize());
        result.put("pageNumber", paginationResponse.getCurrentPage());
        result.put("totalRow", paginationResponse.getTotalElements());
        result.put("totalPage", paginationResponse.getTotalPages());

        return getSuccessResult(result);
    }

    @ApiOperation(value = "保存员工信息")
    @RequestMapping(value = "/saveStaff", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject saveStaff(HttpServletRequest request, @RequestBody StaffParam staffInfo) throws BusinessCheckException {
        Integer merchantId = merchantService.getMerchantId(request.getHeader("merchantNo"));

        UserInfo userInfo = TokenUtil.getUserInfoByToken(request.getHeader("Access-Token"));
        MtUser mtUser = memberService.queryMemberById(userInfo.getId());

        if (mtUser == null || StringUtil.isBlank(mtUser.getMobile())) {
            return getFailureResult(201, "该账号不是商户");
        }

        MtStaff staff = staffService.queryStaffByMobile(mtUser.getMobile());
        if (staff == null || !merchantId.equals(staff.getMerchantId())) {
            return getFailureResult(201, "您没有操作权限");
        }

        MtStaff mtStaff = new MtStaff();
        if (staffInfo.getId() != null && staffInfo.getId() > 0) {
            mtStaff = staffService.queryStaffById(staffInfo.getId());
        }
        BeanUtils.copyProperties(staffInfo, mtStaff);
        staffService.saveStaff(mtStaff, staff.getRealName());

        return getSuccessResult(true);
    }
}

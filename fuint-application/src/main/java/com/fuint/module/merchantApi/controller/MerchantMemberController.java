package com.fuint.module.merchantApi.controller;

import com.fuint.common.dto.UserDto;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.MemberDetailParam;
import com.fuint.common.param.MemberInfoParam;
import com.fuint.common.param.MemberListParam;
import com.fuint.common.param.MemberPage;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.StaffService;
import com.fuint.common.service.UserGradeService;
import com.fuint.common.util.DateUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtStaff;
import com.fuint.repository.model.MtUser;
import com.fuint.repository.model.MtUserGrade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

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
@AllArgsConstructor
@RequestMapping(value = "/merchantApi/member")
public class MerchantMemberController extends BaseController {

    /**
     * 会员服务接口
     */
    private MemberService memberService;

    /**
     * 店铺员工服务接口
     * */
    private StaffService staffService;

    /**
     * 会员等级服务接口
     **/
    private UserGradeService userGradeService;

    /**
     * 会员列表查询
     */
    @ApiOperation(value = "查询会员列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(@RequestBody MemberListParam memberListParam) throws BusinessCheckException, IllegalAccessException {
        String dataType = memberListParam.getDataType();
        // 今日注册、今日活跃
        if (dataType.equals("todayRegister")) {
            String regTime = DateUtil.formatDate(new Date(), "yyyy-MM-dd") + " 00:00:00~" + DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
            memberListParam.setRegTime(regTime);
        } else if (dataType.equals("todayActive")) {
            String activeTime = DateUtil.formatDate(new Date(), "yyyy-MM-dd") + " 00:00:00~" + DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
            memberListParam.setActiveTime(activeTime);
        }
        UserInfo userInfo = TokenUtil.getUserInfo();
        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        MtStaff staffInfo = null;
        if (mtUser != null && mtUser.getMobile() != null) {
            staffInfo = staffService.queryStaffByMobile(mtUser.getMobile());
        }
        if (staffInfo == null) {
            return getFailureResult(201, "您的帐号不是商户，没有操作权限");
        }

        if (staffInfo.getMerchantId() != null && staffInfo.getMerchantId() > 0) {
            memberListParam.setMerchantId(staffInfo.getMerchantId());
        }
        if (staffInfo.getStoreId() != null && staffInfo.getStoreId() > 0) {
            memberListParam.setStoreId(staffInfo.getStoreId());
        }

        MemberPage memberPage = new MemberPage();
        BeanUtils.copyProperties(memberListParam, memberPage);
        PaginationResponse<UserDto> paginationResponse = memberService.queryMemberListByPagination(memberPage);

        // 会员等级列表
        List<MtUserGrade> userGradeList = userGradeService.getMerchantGradeList(staffInfo.getMerchantId(), StatusEnum.ENABLED.getKey());

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        result.put("userGradeList", userGradeList);

        return getSuccessResult(result);
    }

    /**
     * 会员详情
     */
    @ApiOperation(value = "查询会员详情")
    @RequestMapping(value = "/info", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject info(@RequestBody MemberDetailParam memberParam) throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfo();

        MtStaff staffInfo = null;
        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        if (mtUser != null && mtUser.getMobile() != null) {
            staffInfo = staffService.queryStaffByMobile(mtUser.getMobile());
        }
        if (staffInfo == null) {
            return getFailureResult(201, "您的帐号不是商户，没有操作权限");
        }
        MtUser memberInfo = memberService.queryMemberById(memberParam.getMemberId());
        MtUserGrade gradeInfo = memberService.queryMemberGradeByGradeId(memberInfo.getGradeId());

        Map<String, Object> result = new HashMap<>();
        result.put("userInfo", memberInfo);
        result.put("gradeInfo", gradeInfo);

        return getSuccessResult(result);
    }

    /**
     * 保存会员信息
     */
    @ApiOperation(value = "保存会员信息")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject save(@RequestBody MemberInfoParam memberInfoParam) throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfo();

        MtStaff staffInfo = null;
        MtUser myUserInfo = memberService.queryMemberById(userInfo.getId());
        if (myUserInfo != null && myUserInfo.getMobile() != null) {
            staffInfo = staffService.queryStaffByMobile(myUserInfo.getMobile());
        }
        if (staffInfo == null) {
            return getFailureResult(201, "您的帐号不是商户，没有操作权限");
        }
        MtUser mtUser = new MtUser();
        if (memberInfoParam.getId() != null) {
            mtUser = memberService.queryMemberById(memberInfoParam.getId());
        }
        mtUser.setMerchantId(staffInfo.getMerchantId());
        mtUser.setStoreId(staffInfo.getStoreId());
        mtUser.setMobile(memberInfoParam.getMobile());
        mtUser.setName(memberInfoParam.getName());
        mtUser.setAvatar(memberInfoParam.getAvatar());
        mtUser.setSex(memberInfoParam.getSex());
        mtUser.setBirthday(memberInfoParam.getBirthday());
        mtUser.setUserNo(memberInfoParam.getUserNo());
        MtUser memberInfo;
        if (memberInfoParam.getId() == null) {
            mtUser.setDescription("商户登记添加");
            memberInfo = memberService.addMember(mtUser, null);
        } else {
            memberInfo = memberService.updateMember(mtUser, false);
        }
        return getSuccessResult(memberInfo);
    }
}

package com.fuint.module.backendApi.controller;

import com.fuint.common.Constants;
import com.fuint.common.dto.*;
import com.fuint.common.enums.SettingTypeEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.UserSettingEnum;
import com.fuint.common.enums.YesOrNoEnum;
import com.fuint.common.param.MemberPage;
import com.fuint.common.service.*;
import com.fuint.common.util.*;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.module.backendApi.request.MemberSubmitRequest;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import weixin.popular.util.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * 会员管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-会员相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/member")
public class BackendMemberController extends BaseController {

    /**
     * 会员服务接口
     */
    private MemberService memberService;

    /**
     * 配置服务接口
     * */
    private SettingService settingService;

    /**
     * 店铺服务接口
     */
    private StoreService storeService;

    /**
     * 会员分组服务接口
     */
    private MemberGroupService memberGroupService;

    /**
     * 微信相关接口
     * */
    private WeixinService weixinService;

    /**
     * 上传文件服务接口
     * */
    private UploadService uploadService;

    /**
     * 会员等级服务接口
     **/
    private UserGradeService userGradeService;

    /**
     * 查询会员列表
     */
    @ApiOperation(value = "查询会员列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:index')")
    public ResponseObject list(@ModelAttribute MemberPage memberPage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            memberPage.setMerchantId(accountInfo.getMerchantId());
        }
        PaginationResponse<UserDto> paginationResponse = memberService.queryMemberListByPagination(memberPage);

        // 会员等级列表
        List<MtUserGrade> userGradeList = userGradeService.getMerchantGradeList(accountInfo.getMerchantId(), null);

        // 店铺列表
        List<MtStore> storeList = storeService.getMyStoreList(accountInfo.getMerchantId(), 0, StatusEnum.ENABLED.getKey());

        // 会员分组
        List<UserGroupDto> groupList = new ArrayList<>();
        Map<String, Object> searchParams = new HashMap<>();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            searchParams.put("merchantId", accountInfo.getMerchantId());
        }
        searchParams.put("status", StatusEnum.ENABLED.getKey());
        PaginationRequest groupRequest = new PaginationRequest();
        groupRequest.setCurrentPage(1);
        groupRequest.setPageSize(Constants.MAX_ROWS);
        groupRequest.setSearchParams(searchParams);
        PaginationResponse<UserGroupDto> groupResponse = memberGroupService.queryMemberGroupListByPagination(groupRequest);
        if (groupResponse != null && groupResponse.getContent() != null) {
            groupList = groupResponse.getContent();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        result.put("userGradeList", userGradeList);
        result.put("storeList", storeList);
        result.put("groupList", groupList);

        return getSuccessResult(result);
    }

    /**
     * 更新会员状态
     */
    @ApiOperation(value = "更新会员状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:index')")
    public ResponseObject updateStatus(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        Integer userId = param.get("userId") == null ? 0 : Integer.parseInt(param.get("userId").toString());
        String status = param.get("status") == null ? StatusEnum.ENABLED.getKey() : param.get("status").toString();
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        MtUser userInfo = memberService.queryMemberById(userId);
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            if (!accountInfo.getMerchantId().equals(userInfo.getMerchantId())) {
                return getFailureResult(1004);
            }
        }

        if (userInfo == null) {
            return getFailureResult(201, "会员不存在");
        }

        userInfo.setStatus(status);
        memberService.updateMember(userInfo, false);

        return getSuccessResult(true);
    }

    /**
     * 删除会员
     */
    @ApiOperation(value = "删除会员")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:index')")
    public ResponseObject delete(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        MtUser mtUser = memberService.queryMemberById(id);
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            if (!mtUser.getMerchantId().equals(accountInfo.getMerchantId())) {
                return getFailureResult(1004);
            }
        }
        memberService.deleteMember(id, accountInfo.getAccountName());
        return getSuccessResult(true);
    }

    /**
     * 保存会员信息
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:add')")
    public ResponseObject save(@RequestBody MemberSubmitRequest memberInfo) throws BusinessCheckException, ParseException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (PhoneFormatCheckUtils.isChinaPhoneLegal(memberInfo.getMobile())) {
            // 重置该手机号
            memberService.resetMobile(memberInfo.getMobile(), memberInfo.getId());
        }

        MtUser mtUser;
        if (memberInfo.getId() == null || memberInfo.getId() <= 0) {
            mtUser = new MtUser();
        } else {
            mtUser = memberService.queryMemberById(memberInfo.getId());
        }

        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            mtUser.setMerchantId(accountInfo.getMerchantId());
        }
        BeanUtils.copyProperties(memberInfo, mtUser);
        mtUser.setStartTime(DateUtil.parseDate(memberInfo.getStartTime()));
        mtUser.setEndTime(DateUtil.parseDate(memberInfo.getEndTime()));
        mtUser.setIsStaff(YesOrNoEnum.NO.getKey());
        Integer myStoreId = accountInfo.getStoreId();
        if (myStoreId != null && myStoreId > 0) {
            mtUser.setStoreId(myStoreId);
        }
        if (mtUser.getId() == null) {
            memberService.addMember(mtUser, null);
        } else {
            memberService.updateMember(mtUser, false);
        }
        return getSuccessResult(true);
    }

    /**
     * 获取会员详情
     */
    @ApiOperation(value = "获取会员详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:index')")
    public ResponseObject info(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        MtUser mtUser = memberService.queryMemberById(id);
        if (mtUser == null) {
            return getFailureResult(201, "会员信息有误");
        }

        UserDto memberInfo = new UserDto();
        BeanUtils.copyProperties(mtUser, memberInfo);

        MtUserGroup mtUserGroup = memberGroupService.queryMemberGroupById(memberInfo.getGroupId());
        if (mtUserGroup != null) {
            UserGroupDto userGroupDto = new UserGroupDto();
            BeanUtils.copyProperties(mtUserGroup, userGroupDto);
            memberInfo.setGroupInfo(userGroupDto);
        }
        memberInfo.setMobile(CommonUtil.hidePhone(memberInfo.getMobile()));
        List<MtUserGrade> userGradeList = userGradeService.getMerchantGradeList(accountInfo.getMerchantId(), null);
        Map<String, Object> result = new HashMap<>();
        result.put("userGradeList", userGradeList);
        result.put("memberInfo", memberInfo);

        return getSuccessResult(result);
    }

    /**
     * 获取会员设置
     */
    @ApiOperation(value = "获取会员设置")
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:setting')")
    public ResponseObject setting() throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        List<MtSetting> settingList = settingService.getSettingList(accountInfo.getMerchantId(), SettingTypeEnum.USER.getKey());

        String getCouponNeedPhone = YesOrNoEnum.FALSE.getKey();
        String submitOrderNeedPhone = YesOrNoEnum.FALSE.getKey();
        String loginNeedPhone = YesOrNoEnum.FALSE.getKey();
        String openWxCard = YesOrNoEnum.FALSE.getKey();
        WxCardDto wxMemberCard = null;
        for (MtSetting setting : settingList) {
            if (StringUtil.isNotEmpty(setting.getValue())) {
                if (setting.getName().equals(UserSettingEnum.GET_COUPON_NEED_PHONE.getKey())) {
                    getCouponNeedPhone = setting.getValue();
                } else if (setting.getName().equals(UserSettingEnum.GET_COUPON_NEED_PHONE.getKey())) {
                    submitOrderNeedPhone = setting.getValue();
                } else if (setting.getName().equals(UserSettingEnum.LOGIN_NEED_PHONE.getKey())) {
                    loginNeedPhone = setting.getValue();
                } else if (setting.getName().equals(UserSettingEnum.OPEN_WX_CARD.getKey())) {
                    openWxCard = setting.getValue();
                } else if (setting.getName().equals(UserSettingEnum.WX_MEMBER_CARD.getKey())) {
                    wxMemberCard = JsonUtil.parseObject(setting.getValue(), WxCardDto.class);
                }
            }
        }

        String imagePath = settingService.getUploadBasePath();
        Map<String, Object> result = new HashMap<>();
        result.put("getCouponNeedPhone", getCouponNeedPhone);
        result.put("submitOrderNeedPhone", submitOrderNeedPhone);
        result.put("loginNeedPhone", loginNeedPhone);
        result.put("openWxCard", openWxCard);
        result.put("wxMemberCard", wxMemberCard);
        result.put("imagePath", imagePath);

        return getSuccessResult(result);
    }

    /**
     * 保存会员设置
     */
    @ApiOperation(value = "保存会员设置")
    @RequestMapping(value = "/saveSetting", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:setting')")
    public ResponseObject saveSetting(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        String getCouponNeedPhone = param.get("getCouponNeedPhone") != null ? param.get("getCouponNeedPhone").toString() : null;
        String submitOrderNeedPhone = param.get("submitOrderNeedPhone") != null ? param.get("submitOrderNeedPhone").toString() : null;
        String loginNeedPhone = param.get("loginNeedPhone") != null ? param.get("loginNeedPhone").toString() : null;
        String openWxCard = param.get("openWxCard") != null ? param.get("openWxCard").toString() : null;
        String wxMemberCard = param.get("wxMemberCard") != null ? param.get("wxMemberCard").toString() : null;

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() == null || accountInfo.getMerchantId() <= 0) {
            throw new BusinessCheckException("平台方帐号无法执行该操作，请使用商户帐号操作");
        }

        UserSettingEnum[] settingList = UserSettingEnum.values();
        for (UserSettingEnum setting : settingList) {
            MtSetting mtSetting = new MtSetting();
            mtSetting.setType(SettingTypeEnum.USER.getKey());
            mtSetting.setName(setting.getKey());
            if (setting.getKey().equals(UserSettingEnum.GET_COUPON_NEED_PHONE.getKey())) {
                mtSetting.setValue(getCouponNeedPhone);
            } else if (setting.getKey().equals(UserSettingEnum.SUBMIT_ORDER_NEED_PHONE.getKey())) {
                mtSetting.setValue(submitOrderNeedPhone);
            } else if (setting.getKey().equals(UserSettingEnum.LOGIN_NEED_PHONE.getKey())) {
                mtSetting.setValue(loginNeedPhone);
            } else if (setting.getKey().equals(UserSettingEnum.OPEN_WX_CARD.getKey())) {
                mtSetting.setValue(openWxCard);
            } else if (setting.getKey().equals(UserSettingEnum.WX_MEMBER_CARD.getKey())) {
                mtSetting.setValue(wxMemberCard);
            }
            mtSetting.setDescription(setting.getValue());
            mtSetting.setOperator(accountInfo.getAccountName());
            mtSetting.setUpdateTime(new Date());
            mtSetting.setMerchantId(accountInfo.getMerchantId());
            mtSetting.setStoreId(0);
            settingService.saveSetting(mtSetting);
        }

        MtSetting openCardSetting = settingService.querySettingByName(accountInfo.getMerchantId(), SettingTypeEnum.USER.getKey(), UserSettingEnum.OPEN_WX_CARD.getKey());
        MtSetting cardSetting = settingService.querySettingByName(accountInfo.getMerchantId(), SettingTypeEnum.USER.getKey(), UserSettingEnum.WX_MEMBER_CARD.getKey());
        MtSetting cardIdSetting = settingService.querySettingByName(accountInfo.getMerchantId(), SettingTypeEnum.USER.getKey(), UserSettingEnum.WX_MEMBER_CARD_ID.getKey());
        if (openCardSetting != null && openCardSetting.getValue().equals(YesOrNoEnum.TRUE.getKey()) && cardSetting != null && accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            String wxCardId = "";
            if (cardIdSetting != null) {
                wxCardId = cardIdSetting.getValue();
            }
            String cardId = weixinService.createWxCard(accountInfo.getMerchantId(), wxCardId);
            if (StringUtil.isNotEmpty(cardId)) {
                MtSetting mtSetting = new MtSetting();
                mtSetting.setType(SettingTypeEnum.USER.getKey());
                mtSetting.setName(UserSettingEnum.WX_MEMBER_CARD_ID.getKey());
                mtSetting.setValue(cardId);
                mtSetting.setOperator(accountInfo.getAccountName());
                mtSetting.setUpdateTime(new Date());
                mtSetting.setMerchantId(accountInfo.getMerchantId());
                mtSetting.setStoreId(0);
                settingService.saveSetting(mtSetting);
            }
        }

        return getSuccessResult(true);
    }

    /**
     * 重置会员密码
     */
    @ApiOperation(value = "重置会员密码")
    @RequestMapping(value = "/resetPwd", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('member:add')")
    public ResponseObject resetPwd(@RequestBody Map<String, Object> param) throws BusinessCheckException {
        Integer userId = param.get("userId") == null ? 0 : Integer.parseInt(param.get("userId").toString());
        String password = param.get("password") == null ? "" : param.get("password").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (StringUtil.isEmpty(password)) {
            return getFailureResult(1001, "密码格式有误");
        }

        MtUser userInfo = memberService.queryMemberById(userId);
        if (userInfo == null) {
            return getFailureResult(201, "会员不存在");
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0 && !accountInfo.getMerchantId().equals(userInfo.getMerchantId())) {
            return getFailureResult(201, "您没有操作权限");
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0 && !accountInfo.getStoreId().equals(userInfo.getStoreId())) {
            return getFailureResult(201, "您没有操作权限");
        }

        userInfo.setPassword(password);
        memberService.updateMember(userInfo, true);

        return getSuccessResult(true);
    }

    /**
     * 获取会员分组
     */
    @ApiOperation(value = "获取会员分组")
    @RequestMapping(value = "/groupList", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject groupList() throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        // 会员分组
        List<UserGroupDto> groupList = new ArrayList<>();
        Map<String, Object> searchParams = new HashMap<>();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            searchParams.put("merchantId", accountInfo.getMerchantId());
        }
        PaginationRequest groupRequest = new PaginationRequest();
        groupRequest.setCurrentPage(1);
        groupRequest.setPageSize(Constants.ALL_ROWS);
        groupRequest.setSearchParams(searchParams);
        PaginationResponse<UserGroupDto> groupResponse = memberGroupService.queryMemberGroupListByPagination(groupRequest);
        if (groupResponse != null && groupResponse.getContent() != null) {
            groupList = groupResponse.getContent();
        }

        return getSuccessResult(groupList);
    }

    /**
     * 查找会员列表
     */
    @ApiOperation(value = "查找会员列表")
    @RequestMapping(value = "/searchMembers", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject searchMembers(HttpServletRequest request) {
        String groupIds = request.getParameter("groupIds") != null ? request.getParameter("groupIds") : "";
        String keyword = request.getParameter("keyword") != null ? request.getParameter("keyword") : "";
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        List<GroupMemberDto> memberList = memberService.searchMembers(accountInfo.getMerchantId(), keyword, groupIds,1, Constants.MAX_ROWS);
        return getSuccessResult(memberList);
    }

    /**
     * 下载会员导入模板
     */
    @ApiOperation(value = "下载会员导入模板")
    @RequestMapping(value = "/downloadTemplate", method = RequestMethod.GET)
    @CrossOrigin
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil.downLoadTemplate(response, "MemberTemplate.xlsx");
    }

    /**
     * 上传会员导入文件
     */
    @ApiOperation(value = "上传会员导入文件")
    @RequestMapping(value = "/uploadMemberFile", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject uploadMemberFile(HttpServletRequest request) throws Exception {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");
        String filePath = uploadService.saveUploadFile(request, file);
        Boolean result = memberService.importMember(file, accountInfo, filePath);

        return getSuccessResult(result);
    }
}

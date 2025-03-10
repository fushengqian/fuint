package com.fuint.common.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.GroupMemberDto;
import com.fuint.common.dto.MemberTopDto;
import com.fuint.common.dto.UserDto;
import com.fuint.common.enums.*;
import com.fuint.common.service.*;
import com.fuint.common.util.*;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.bean.MemberTopBean;
import com.fuint.repository.mapper.MtUserActionMapper;
import com.fuint.repository.mapper.MtUserGradeMapper;
import com.fuint.repository.mapper.MtUserMapper;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * 会员业务接口实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor(onConstructor_= {@Lazy})
public class MemberServiceImpl extends ServiceImpl<MtUserMapper, MtUser> implements MemberService {

    private static final Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

    private MtUserMapper mtUserMapper;

    private MtUserGradeMapper mtUserGradeMapper;

    private MtUserActionMapper mtUserActionMapper;

    /**
     * 短信发送接口
     */
    private SendSmsService sendSmsService;

    /**
     * 会员等级接口
     * */
    private UserGradeService userGradeService;

    /**
     * 会员等级接口
     * */
    private OpenGiftService openGiftService;

    /**
     * 后台账户服务接口
     */
    private AccountService accountService;

    /**
     * 员工接口
     */
    private StaffService staffService;

    /**
     * 店铺接口
     */
    private StoreService storeService;

    /**
     * 会员行为接口
     */
    private UserActionService userActionService;

    /**
     * 系统配置服务接口
     * */
    private SettingService settingService;

    /**
     * 分佣提成关系服务接口
     * */
    private CommissionRelationService commissionRelationService;

    /**
     * 更新活跃时间
     * @param userId 会员ID
     * @return
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateActiveTime(Integer userId) throws BusinessCheckException {
        MtUser mtUser = queryMemberById(userId);
        if (mtUser != null) {
            if (!mtUser.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                return false;
            }
            Date lastUpdateTime = mtUser.getUpdateTime();
            Date registerTime = mtUser.getCreateTime();
            if (lastUpdateTime != null) {
                Long timestampLast = Long.valueOf(TimeUtils.date2timeStamp(lastUpdateTime));
                Long timestampNow = System.currentTimeMillis() / 1000;
                Long minute = timestampNow - timestampLast;

                // 5分钟更新一次
                if (minute >= 300 || registerTime.equals(lastUpdateTime)) {
                    synchronized(MemberServiceImpl.class) {
                        Date activeTime = new Date();
                        mtUserMapper.updateActiveTime(mtUser.getId(), activeTime);
                        // 记录会员行为
                        MtUserAction mtUserAction = new MtUserAction();
                        mtUserAction.setUserId(mtUser.getId());
                        mtUserAction.setStoreId(mtUser.getStoreId());
                        mtUserAction.setMerchantId(mtUser.getMerchantId());
                        mtUserAction.setParam(TimeUtils.formatDate(activeTime, "yyyy-MM-dd HH:mm:ss"));
                        mtUserAction.setAction(UserActionEnum.LOGIN.getKey());
                        mtUserAction.setDescription(UserActionEnum.LOGIN.getValue());
                        userActionService.addUserAction(mtUserAction);
                    }
                }
            }
        }

        return true;
    }

    /**
     * 获取当前操作会员信息
     *
     * @param userId 会员ID
     * @param token 登录token
     * @return
     * */
    @Override
    public MtUser getCurrentUserInfo(HttpServletRequest request, Integer userId, String token) throws BusinessCheckException {
        MtUser mtUser = null;

        // 没有会员信息，则查询是否是后台收银员下单
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo != null) {
            // 输入了会员ID就用会员的账号下单，否则用员工账号下单
            if (userId != null && userId > 0) {
                mtUser = queryMemberById(userId);
            } else {
                Integer accountId = accountInfo.getId();
                TAccount account = accountService.getAccountInfoById(accountId);
                if (account != null) {
                    if (account.getStaffId() > 0) {
                        MtStaff staff = staffService.queryStaffById(account.getStaffId());
                        if (staff != null) {
                            mtUser = queryMemberById(staff.getUserId());
                            if (mtUser != null) {
                                if (staff.getStoreId() != null && staff.getStoreId() > 0) {
                                    mtUser.setStoreId(staff.getStoreId());
                                }
                                if (account.getMerchantId() != null && account.getMerchantId() > 0 && !account.getMerchantId().equals(mtUser.getMerchantId())) {
                                    mtUser.setMerchantId(account.getMerchantId());
                                }
                                mtUser.setUpdateTime(new Date());
                                updateById(mtUser);
                            }
                        }
                    }
                }
            }
        }
        return mtUser;
    }

    /**
     * 分页查询会员列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<UserDto> queryMemberListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException {
        Page<MtUser> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtUser> wrapper = Wrappers.lambdaQuery();
        wrapper.ne(MtUser::getStatus, StatusEnum.DISABLE.getKey());
        wrapper.eq(MtUser::getIsStaff, YesOrNoEnum.NO.getKey());
        String name = paginationRequest.getSearchParams().get("name") == null ? "" : paginationRequest.getSearchParams().get("name").toString();
        if (StringUtils.isNotBlank(name)) {
            wrapper.like(MtUser::getName, name);
        }
        String id = paginationRequest.getSearchParams().get("id") == null ? "" : paginationRequest.getSearchParams().get("id").toString();
        if (StringUtils.isNotBlank(id)) {
            wrapper.eq(MtUser::getId, id);
        }
        String keyword = paginationRequest.getSearchParams().get("keyword") == null ? "" : paginationRequest.getSearchParams().get("keyword").toString();
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(wq -> wq
                    .eq(MtUser::getMobile, keyword)
                    .or()
                    .eq(MtUser::getUserNo, keyword)
                    .or()
                    .eq(MtUser::getName, keyword));
        }
        String mobile = paginationRequest.getSearchParams().get("mobile") == null ? "" : paginationRequest.getSearchParams().get("mobile").toString();
        if (StringUtils.isNotBlank(mobile)) {
            wrapper.like(MtUser::getMobile, mobile);
        }
        String birthday = paginationRequest.getSearchParams().get("birthday") == null ? "" : paginationRequest.getSearchParams().get("birthday").toString();
        if (StringUtils.isNotBlank(birthday)) {
            wrapper.like(MtUser::getBirthday, birthday);
        }
        String userNo = paginationRequest.getSearchParams().get("userNo") == null ? "" : paginationRequest.getSearchParams().get("userNo").toString();
        if (StringUtils.isNotBlank(userNo)) {
            wrapper.eq(MtUser::getUserNo, userNo);
        }
        String gradeId = paginationRequest.getSearchParams().get("gradeId") == null ? "" : paginationRequest.getSearchParams().get("gradeId").toString();
        if (StringUtils.isNotBlank(gradeId)) {
            wrapper.eq(MtUser::getGradeId, gradeId);
        }
        String merchantId = paginationRequest.getSearchParams().get("merchantId") == null ? "" : paginationRequest.getSearchParams().get("merchantId").toString();
        if (StringUtils.isNotBlank(merchantId)) {
            wrapper.eq(MtUser::getMerchantId, merchantId);
        }
        String storeId = paginationRequest.getSearchParams().get("storeId") == null ? "" : paginationRequest.getSearchParams().get("storeId").toString();
        if (StringUtils.isNotBlank(storeId)) {
            wrapper.eq(MtUser::getStoreId, storeId);
        }
        String storeIds = paginationRequest.getSearchParams().get("storeIds") == null ? "" : paginationRequest.getSearchParams().get("storeIds").toString();
        if (StringUtils.isNotBlank(storeIds)) {
            List<String> idList = Arrays.asList(storeIds.split(","));
            if (idList.size() > 0) {
                wrapper.in(MtUser::getStoreId, idList);
            }
        }
        String groupIds = paginationRequest.getSearchParams().get("groupIds") == null ? "" : paginationRequest.getSearchParams().get("groupIds").toString();
        if (StringUtils.isNotBlank(groupIds)) {
            List<String> idList = Arrays.asList(groupIds.split(","));
            if (idList.size() > 0) {
                wrapper.in(MtUser::getGroupId, idList);
            }
        }
        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            wrapper.eq(MtUser::getStatus, status);
        }
        // 注册开始、结束时间
        String startTime = paginationRequest.getSearchParams().get("startTime") == null ? "" : paginationRequest.getSearchParams().get("startTime").toString();
        String endTime = paginationRequest.getSearchParams().get("endTime") == null ? "" : paginationRequest.getSearchParams().get("endTime").toString();
        if (StringUtil.isNotEmpty(startTime)) {
            wrapper.ge(MtUser::getCreateTime, startTime);
        }
        if (StringUtil.isNotEmpty(endTime)) {
            wrapper.le(MtUser::getCreateTime, endTime);
        }
        // 注册时间
        String regTime = paginationRequest.getSearchParams().get("regTime") == null ? "" : paginationRequest.getSearchParams().get("regTime").toString();
        if (StringUtil.isNotEmpty(regTime)) {
            String[] dateTime = regTime.split("~");
            if (dateTime.length == 2) {
                wrapper.ge(MtUser::getCreateTime, dateTime[0]);
                wrapper.le(MtUser::getCreateTime, dateTime[1]);
            }
        }
        // 活跃时间
        String activeTime = paginationRequest.getSearchParams().get("activeTime") == null ? "" : paginationRequest.getSearchParams().get("activeTime").toString();
        if (StringUtil.isNotEmpty(activeTime)) {
            String[] dateTime = activeTime.split("~");
            if (dateTime.length == 2) {
                wrapper.ge(MtUser::getUpdateTime, dateTime[0]);
                wrapper.le(MtUser::getUpdateTime, dateTime[1]);
            }
        }
        // 会员有效期
        String memberTime = paginationRequest.getSearchParams().get("memberTime") == null ? "" : paginationRequest.getSearchParams().get("memberTime").toString();
        if (StringUtil.isNotEmpty(memberTime)) {
            String[] dateTime = memberTime.split("~");
            if (dateTime.length == 2) {
                wrapper.ge(MtUser::getStartTime, dateTime[0]);
                wrapper.le(MtUser::getEndTime, dateTime[1]);
            }
        }
        wrapper.orderByDesc(MtUser::getUpdateTime);
        List<MtUser> userList = mtUserMapper.selectList(wrapper);
        List<UserDto> dataList = new ArrayList<>();
        for (MtUser mtUser : userList) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(mtUser, userDto);
            userDto.setMobile(CommonUtil.hidePhone(mtUser.getMobile()));
            if (userDto.getStoreId() != null && userDto.getStoreId() > 0) {
                MtStore mtStore = storeService.queryStoreById(userDto.getStoreId());
                if (mtStore != null) {
                    userDto.setStoreName(mtStore.getName());
                }
            }
            if (userDto.getGradeId() != null) {
                Integer mchId = StringUtil.isNotEmpty(merchantId) ? Integer.parseInt(merchantId) : 0;
                MtUserGrade mtGrade = userGradeService.queryUserGradeById(mchId, Integer.parseInt(userDto.getGradeId()), mtUser.getId());
                if (mtGrade != null) {
                    userDto.setGradeName(mtGrade.getName());
                }
            }
            if (mtUser.getUserNo() == null || StringUtil.isEmpty(mtUser.getUserNo())) {
                mtUser.setUserNo(CommonUtil.createUserNo());
                updateById(mtUser);
            }
            userDto.setLastLoginTime(TimeUtil.showTime(new Date(), mtUser.getUpdateTime()));
            dataList.add(userDto);
        }

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<UserDto> paginationResponse = new PaginationResponse(pageImpl, UserDto.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 添加会员
     *
     * @param  mtUser 会员信息
     * @param  shareId 分享用户ID
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @OperationServiceLog(description = "新增会员信息")
    public MtUser addMember(MtUser mtUser, String shareId) throws BusinessCheckException {
        // 用户名就是手机号
        if (StringUtil.isNotEmpty(mtUser.getName()) && StringUtil.isEmpty(mtUser.getMobile()) && PhoneFormatCheckUtils.isChinaPhoneLegal(mtUser.getName())) {
            mtUser.setMobile(mtUser.getName());
            String name = mtUser.getName().replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
            mtUser.setName(name);
        }

        // 手机号已存在
        if (StringUtil.isNotEmpty(mtUser.getMobile())) {
            MtUser userInfo = queryMemberByMobile(mtUser.getMerchantId(), mtUser.getMobile());
            if (userInfo != null) {
                return userInfo;
            }
        }

        String userNo = CommonUtil.createUserNo();
        if (StringUtil.isNotEmpty(mtUser.getUserNo())) {
            userNo = mtUser.getUserNo();
        }
        // 会员名称已存在
        List<MtUser> userList = mtUserMapper.queryMemberByName(mtUser.getMerchantId(), mtUser.getName());
        if (userList.size() > 0) {
            mtUser.setName(userNo);
        }
        // 默认会员等级
        if (StringUtil.isEmpty(mtUser.getGradeId())) {
            MtUserGrade grade = userGradeService.getInitUserGrade(mtUser.getMerchantId());
            if (grade != null) {
                mtUser.setGradeId(grade.getId().toString());
            }
        }
        mtUser.setUserNo(userNo);
        mtUser.setBalance(new BigDecimal(0));
        if (mtUser.getPoint() == null || mtUser.getPoint() < 1) {
            mtUser.setPoint(0);
        }
        if (StringUtil.isEmpty(mtUser.getIdcard())) {
            mtUser.setIdcard("");
        }
        mtUser.setSex(mtUser.getSex());
        mtUser.setStatus(StatusEnum.ENABLED.getKey());
        Date time = new Date();
        mtUser.setCreateTime(time);
        mtUser.setUpdateTime(time);
        mtUser.setStartTime(mtUser.getStartTime());
        mtUser.setEndTime(mtUser.getEndTime());
        if (mtUser.getIsStaff() == null) {
            mtUser.setIsStaff(YesOrNoEnum.NO.getKey());
        }
        if (mtUser.getStoreId() != null) {
            mtUser.setStoreId(mtUser.getStoreId());
        } else {
            mtUser.setStoreId(0);
        }
        // 密码加密
        if (mtUser.getPassword() != null && StringUtil.isNotEmpty(mtUser.getPassword())) {
            String salt = SeqUtil.getRandomLetter(4);
            mtUser.setSalt(salt);
            String password = enCodePassword(mtUser.getPassword(), salt);
            mtUser.setPassword(password);
            mtUser.setSource(MemberSourceEnum.REGISTER_BY_ACCOUNT.getKey());
        }
        if (mtUser.getSource() == null || StringUtil.isEmpty(mtUser.getSource())) {
            mtUser.setSource(MemberSourceEnum.BACKEND_ADD.getKey());
        }

        boolean result = save(mtUser);
        if (!result) {
           return null;
        }

        mtUser = queryMemberById(mtUser.getId());

        // 开卡赠礼
        openGiftService.openGift(mtUser.getId(), Integer.parseInt(mtUser.getGradeId()), true);

        // 分佣关系
        commissionRelationService.setCommissionRelation(mtUser, shareId);

        // 新增用户发短信通知
        if (mtUser.getId() > 0 && mtUser.getStatus().equals(StatusEnum.ENABLED.getKey())) {
            // 发送短信
            List<String> mobileList = new ArrayList<>();
            mobileList.add(mtUser.getMobile());
            // 短信模板
            try {
                Map<String, String> params = new HashMap<>();
                sendSmsService.sendSms(mtUser.getMerchantId(), "register-sms", mobileList, params);
            } catch (BusinessCheckException e) {
                logger.error(e.getMessage());
            }
        }

        return mtUser;
    }

    /**
     * 更新会员信息
     *
     * @param  mtUser 会员信息
     * @param  modifyPassword 修改密码
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "修改会员信息")
    public MtUser updateMember(MtUser mtUser, boolean modifyPassword) throws BusinessCheckException {
        mtUser.setUpdateTime(new Date());

        MtUser oldUserInfo = mtUserMapper.selectById(mtUser.getId());
        if (mtUser.getGradeId() != null && StringUtil.isNotEmpty(mtUser.getGradeId())) {
            if (!CommonUtil.isNumeric(mtUser.getGradeId())) {
                throw new BusinessCheckException("该会员等级有误");
            }
        }
        String mobile = mtUser.getMobile();
        if (PhoneFormatCheckUtils.isChinaPhoneLegal(mobile)) {
            mtUserMapper.resetMobile(mobile, mtUser.getId());
            mtUser.setMobile(mobile);
        }

        // 检查会员号是否重复
        if (StringUtil.isNotEmpty(mtUser.getUserNo())) {
            List<MtUser> userList = mtUserMapper.findMembersByUserNo(mtUser.getMerchantId(), mtUser.getUserNo());
            if (userList.size() > 0) {
                for(MtUser user: userList) {
                    MtUser userInfo = user;
                    if (userInfo.getId().intValue() != mtUser.getId().intValue()) {
                        throw new BusinessCheckException("该会员号与会员ID等于" + userInfo.getId() + "重复啦");
                    }
                }
            }
        }
        if (mtUser.getPassword() != null && modifyPassword) {
            String salt = SeqUtil.getRandomLetter(4);
            mtUser.setSalt(salt);
            mtUser.setPassword(enCodePassword(mtUser.getPassword(), salt));
        }
        String gradeId = mtUser.getGradeId();
        mtUser.setGradeId(oldUserInfo.getGradeId());
        mtUser.setMerchantId(oldUserInfo.getMerchantId());
        if (mtUser.getStoreId() == null || mtUser.getStoreId() <= 0) {
            mtUser.setStoreId(oldUserInfo.getStoreId());
        }
        Boolean result = updateById(mtUser);
        if (result && mtUser.getGradeId() != null) {
            // 修改了会员等级，开卡赠礼
            if (!gradeId.equals(oldUserInfo.getGradeId())) {
                openGiftService.openGift(mtUser.getId(), Integer.parseInt(gradeId), false);
            }
        }
        return mtUser;
    }

    /**
     * 通过手机号新增会员
     *
     * @param merchantId 商户ID
     * @param  mobile 手机号
     * @param  shareId 分享用户ID
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "通过手机号新增会员")
    public MtUser addMemberByMobile(Integer merchantId, String mobile, String shareId) throws BusinessCheckException {
        MtUser mtUser = new MtUser();
        mtUser.setUserNo(CommonUtil.createUserNo());
        String nickName = mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
        mtUser.setName(nickName);
        mtUser.setMobile(mobile);
        MtUserGrade grade = userGradeService.getInitUserGrade(merchantId);
        if (grade != null) {
            mtUser.setGradeId(grade.getId() + "");
        }
        Date time = new Date();
        mtUser.setCreateTime(time);
        mtUser.setUpdateTime(time);
        mtUser.setBalance(new BigDecimal(0));
        mtUser.setPoint(0);
        mtUser.setDescription("手机号登录自动注册");
        mtUser.setIdcard("");
        mtUser.setStatus(StatusEnum.ENABLED.getKey());
        mtUser.setMerchantId(merchantId);
        mtUser.setStoreId(0);
        mtUser.setSource(MemberSourceEnum.MOBILE_LOGIN.getKey());
        mtUserMapper.insert(mtUser);
        mtUser = queryMemberByMobile(merchantId, mobile);

        // 开卡赠礼
        openGiftService.openGift(mtUser.getId(), Integer.parseInt(mtUser.getGradeId()), true);

        // 分佣关系
        commissionRelationService.setCommissionRelation(mtUser, shareId);

        return mtUser;
    }

    /**
     * 根据手机号获取会员信息
     *
     * @param  merchantId 商户ID
     * @param  mobile 手机号
     * @throws BusinessCheckException
     * @return
     */
    @Override
    public MtUser queryMemberByMobile(Integer merchantId, String mobile) {
        if (mobile == null || StringUtil.isEmpty(mobile)) {
            return null;
        }
        List<MtUser> mtUser = mtUserMapper.queryMemberByMobile(merchantId, mobile);
        if (mtUser.size() > 0) {
            return mtUser.get(0);
        } else {
            return null;
        }
    }

    /**
     * 根据会员号号获取会员信息
     *
     * @param  merchantId 商户ID
     * @param  userNo     会员号
     * @return
     */
    @Override
    public MtUser queryMemberByUserNo(Integer merchantId, String userNo) {
        if (userNo == null || StringUtil.isEmpty(userNo)) {
            return null;
        }
        List<MtUser> mtUser = mtUserMapper.findMembersByUserNo(merchantId, userNo);
        if (mtUser.size() > 0) {
            return mtUser.get(0);
        } else {
            return null;
        }
    }

    /**
     * 根据会员ID获取会员信息
     *
     * @param  id 会员ID
     * @throws BusinessCheckException
     * @return
     */
    @Override
    public MtUser queryMemberById(Integer id) throws BusinessCheckException {
        MtUser mtUser = mtUserMapper.selectById(id);

        if (mtUser != null) {
            // 检查会员是否过期，过期就把会员等级置为初始等级
            MtUserGrade initGrade = userGradeService.getInitUserGrade(mtUser.getMerchantId());
            if (initGrade != null) {
                Date endTime = mtUser.getEndTime();
                if (endTime != null) {
                    Date now = new Date();
                    if (endTime.before(now)) {
                        if (!mtUser.getGradeId().equals(initGrade.getId())) {
                            mtUser.setGradeId(initGrade.getId().toString());
                            updateById(mtUser);
                        }
                    }
                }
                // 会员等级为空，就把会员等级置为初始等级
                String userGradeId = mtUser.getGradeId();
                if (userGradeId == null && initGrade != null) {
                    mtUser.setGradeId(initGrade.getId().toString());
                    updateById(mtUser);
                    openGiftService.openGift(mtUser.getId(), initGrade.getId(), false);
                } else {
                    // 会员等级不存在或已禁用、删除，就把会员等级置为初始等级
                    MtUserGrade myGrade = userGradeService.queryUserGradeById(mtUser.getMerchantId(), Integer.parseInt(userGradeId), id);
                    if (myGrade == null || !myGrade.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                        mtUser.setGradeId(initGrade.getId().toString());
                        updateById(mtUser);
                    }
                }
            }
        }
        return mtUser;
    }

    /**
     * 根据会员名称获取会员信息
     *
     * @param  merchantId 商户ID
     * @param  name 会员名称
     * @throws BusinessCheckException
     * @return
     */
    @Override
    public MtUser queryMemberByName(Integer merchantId, String name) {
        if (StringUtil.isNotEmpty(name)) {
            List<MtUser> userList = mtUserMapper.queryMemberByName(merchantId, name);
            if (userList.size() == 1) {
                return userList.get(0);
            }
        }
        return null;
    }

    /**
     * 根据openId获取会员信息(为空就注册)
     *
     * @param  merchantId 商户ID
     * @param  openId 微信openId
     * @throws BusinessCheckException
     * @return
     */
    @Override
    public MtUser queryMemberByOpenId(Integer merchantId, String openId, JSONObject userInfo) throws BusinessCheckException {
        MtUser user = mtUserMapper.queryMemberByOpenId(merchantId, openId);
        if (user != null && !user.getStatus().equals(StatusEnum.ENABLED.getKey())) {
            return null;
        }

        String avatar = StringUtil.isNotEmpty(userInfo.getString("avatarUrl")) ? userInfo.getString("avatarUrl") : "";
        String gender = StringUtil.isNotEmpty(userInfo.getString("gender")) ? userInfo.getString("gender") : GenderEnum.MAN.getKey().toString();
        String country = StringUtil.isNotEmpty(userInfo.getString("country")) ? userInfo.getString("country") : "";
        String province = StringUtil.isNotEmpty(userInfo.getString("province")) ? userInfo.getString("province") : "";
        String city = StringUtil.isNotEmpty(userInfo.getString("city")) ? userInfo.getString("city") : "";
        String storeId = StringUtil.isNotEmpty(userInfo.getString("storeId")) ? userInfo.getString("storeId") : "0";
        String nickName = StringUtil.isNotEmpty(userInfo.getString("nickName")) ? userInfo.getString("nickName") : "";
        String mobile = StringUtil.isNotEmpty(userInfo.getString("phone")) ? userInfo.getString("phone") : "";
        String shareId = StringUtil.isNotEmpty(userInfo.getString("shareId")) ? userInfo.getString("shareId") : "0";
        String source = StringUtil.isNotEmpty(userInfo.getString("source")) ? userInfo.getString("source") : MemberSourceEnum.WECHAT_LOGIN.getKey();
        String platform = StringUtil.isNotEmpty(userInfo.getString("platform")) ? userInfo.getString("platform") : "";

        // 需要手机号登录
        if (StringUtil.isEmpty(mobile) && user == null && !platform.equals(PlatformTypeEnum.H5.getCode())) {
            MtSetting mtSetting = settingService.querySettingByName(merchantId, SettingTypeEnum.USER.getKey(), UserSettingEnum.LOGIN_NEED_PHONE.getKey());
            if (mtSetting != null) {
                if (mtSetting.getValue().equals(YesOrNoEnum.TRUE.getKey())) {
                    MtUser tempUser = new MtUser();
                    tempUser.setOpenId(openId);
                    tempUser.setId(0);
                    return tempUser;
                }
            }
        }

        // 手机号已经存在
        if (StringUtil.isNotEmpty(mobile) && user == null) {
            user = queryMemberByMobile(merchantId, mobile);
            if (user != null) {
                user.setOpenId(openId);
            }
        }

        if (user == null) {
            MtUser mtUser = new MtUser();
            if (StringUtil.isNotEmpty(mobile)) {
                MtUser mtUserMobile = queryMemberByMobile(merchantId, mobile);
                if (mtUserMobile != null) {
                    mtUser = mtUserMobile;
                }
            }

            // 昵称为空，用手机号
            if (StringUtil.isEmpty(nickName) && StringUtil.isNotEmpty(mobile)) {
                nickName = mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
            }
            mtUser.setMerchantId(merchantId);
            String userNo = CommonUtil.createUserNo();
            mobile = CommonUtil.replaceXSS(mobile);
            avatar = CommonUtil.replaceXSS(avatar);
            nickName = CommonUtil.replaceXSS(nickName);
            mtUser.setUserNo(userNo);
            mtUser.setMobile(mobile);
            mtUser.setAvatar(avatar);
            if (StringUtil.isNotEmpty(nickName)) {
                mtUser.setName(nickName);
            } else {
                mtUser.setName(userNo);
            }
            mtUser.setOpenId(openId);
            MtUserGrade grade = userGradeService.getInitUserGrade(merchantId);
            if (grade != null) {
                mtUser.setGradeId(grade.getId() + "");
            }
            Date time = new Date();
            mtUser.setCreateTime(time);
            mtUser.setUpdateTime(time);
            mtUser.setBalance(new BigDecimal(0));
            mtUser.setPoint(0);
            mtUser.setDescription("微信登录自动注册");
            mtUser.setIdcard("");
            mtUser.setStatus(StatusEnum.ENABLED.getKey());
            mtUser.setAddress(country + province + city);
            // 微信用户 1：男；2：女 0：未知
            if (gender.equals(GenderEnum.FEMALE.getKey().toString())) {
                gender = GenderEnum.UNKNOWN.getKey().toString();
            } else if (gender.equals(GenderEnum.UNKNOWN.getKey().toString())) {
                gender = GenderEnum.FEMALE.getKey().toString();
            }
            mtUser.setSex(Integer.parseInt(gender));
            if (StringUtil.isNotEmpty(storeId)) {
                mtUser.setStoreId(Integer.parseInt(storeId));
            } else {
                mtUser.setStoreId(0);
            }
            mtUser.setSource(source);
            if (mtUser.getId() == null || mtUser.getId() <= 0) {
                save(mtUser);
            } else {
                updateById(mtUser);
            }
            user = mtUserMapper.queryMemberByOpenId(merchantId, openId);

            // 开卡赠礼
            openGiftService.openGift(user.getId(), Integer.parseInt(user.getGradeId()), true);

            // 分佣关系
            commissionRelationService.setCommissionRelation(mtUser, shareId);
        } else {
            // 已被禁用
            if (user.getStatus().equals(StatusEnum.DISABLE.getKey())) {
               return null;
            }
            // 补充手机号
            if (StringUtil.isNotEmpty(mobile) && PhoneFormatCheckUtils.isChinaPhoneLegal(mobile)) {
                user.setMobile(mobile);
                updateById(user);
            }
            // 补充会员号
            if (StringUtil.isEmpty(user.getUserNo())) {
                user.setUserNo(CommonUtil.createUserNo());
                updateById(user);
            }
        }

        return user;
    }

    /**
     * 根据等级ID获取会员等级信息
     *
     * @param  id 等级ID
     * @throws BusinessCheckException
     * @return
     */
    @Override
    public MtUserGrade queryMemberGradeByGradeId(Integer id) {
        return mtUserGradeMapper.selectById(id);
    }

    /**
     * 删除会员
     *
     * @param  id 会员ID
     * @param  operator 操作人
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @OperationServiceLog(description = "删除会员信息")
    public Integer deleteMember(Integer id, String operator) throws BusinessCheckException {
        MtUser mtUser = mtUserMapper.selectById(id);
        if (null == mtUser) {
            throw new BusinessCheckException("该会员不存在，请确认");
        }
        // 是否是店铺员工
        MtStaff mtStaff = staffService.queryStaffByUserId(id);
        if (mtStaff != null && mtStaff.getAuditedStatus().equals(StatusEnum.ENABLED.getKey())) {
            throw new BusinessCheckException("该会员已关联店铺员工”"+ mtStaff.getRealName()+"“，若要删除请先删除该员工信息");
        }
        mtUser.setStatus(StatusEnum.DISABLE.getKey());
        mtUser.setUpdateTime(new Date());
        mtUser.setOperator(operator);
        updateById(mtUser);
        return mtUser.getId();
    }

    /**
     * 根据条件搜索会员分组
     *
     * @param params 查询参数
     * @return
     * */
    @Override
    public List<MtUserGrade> queryMemberGradeByParams(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        return mtUserGradeMapper.selectByMap(params);
    }

    /**
     * 获取会员数量
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @return
     * */
    @Override
    public Long getUserCount(Integer merchantId, Integer storeId) {
        if (storeId != null && storeId > 0) {
            return mtUserMapper.getStoreUserCount(storeId);
        } else {
            return mtUserMapper.getUserCount(merchantId);
        }
    }

    /**
     * 获取会员数量
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return
     * */
    @Override
    public Long getUserCount(Integer merchantId, Integer storeId, Date beginTime, Date endTime) {
        if (storeId != null && storeId > 0) {
            return mtUserMapper.getStoreUserCountByTime(storeId, beginTime, endTime);
        } else {
            return mtUserMapper.getUserCountByTime(merchantId, beginTime, endTime);
        }
    }

    /**
     * 获取会员数量
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return
     * */
    @Override
    public Long getActiveUserCount(Integer merchantId, Integer storeId, Date beginTime, Date endTime) {
        if (storeId != null && storeId > 0) {
            return mtUserActionMapper.getStoreActiveUserCount(storeId, beginTime, endTime);
        } else {
            return mtUserActionMapper.getActiveUserCount(merchantId, beginTime, endTime);
        }
    }

    /**
     * 重置手机号
     *
     * @param mobile 手机号码
     * @param userId 会员ID
     * @return
     */
    @Override
    public void resetMobile(String mobile, Integer userId) {
        if (mobile == null || StringUtil.isEmpty(mobile)) {
            return;
        }
        mtUserMapper.resetMobile(mobile, userId);
    }

    /**
     * 获取会员消费排行榜
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     * */
    @Override
    public List<MemberTopDto> getMemberConsumeTopList(Integer merchantId, Integer storeId, Date startTime, Date endTime) {
       List<MemberTopBean> memberList = mtUserMapper.getMemberConsumeTopList(merchantId, storeId, startTime, endTime);
       List<MemberTopDto> dataList = new ArrayList<>();
       if (memberList != null && memberList.size() > 0) {
           for (MemberTopBean bean : memberList) {
                MemberTopDto dto = new MemberTopDto();
                BeanUtils.copyProperties(bean, dto);
                dataList.add(dto);
           }
       }
       return dataList;
    }

    /**
     * 查找会员列表
     *
     * @param merchantId 商户ID
     * @param keyword 关键字
     * @param groupIds 分组ID
     * @param page 当前页码
     * @param pageSize 每页数量
     * @return
     * */
    @Override
    public List<GroupMemberDto> searchMembers(Integer merchantId, String keyword, String groupIds, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        LambdaQueryWrapper<MtUser> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtUser::getStatus, StatusEnum.DISABLE.getKey());
        if (merchantId != null && merchantId > 0) {
            lambdaQueryWrapper.eq(MtUser::getMerchantId, merchantId);
        }
        if (StringUtils.isNotBlank(groupIds)) {
            List<String> idList = Arrays.asList(groupIds.split(","));
            if (idList.size() > 0) {
                lambdaQueryWrapper.in(MtUser::getGroupId, idList);
            }
        }
        if (StringUtil.isNotEmpty(keyword)) {
            List<String> itemList = Arrays.asList(keyword.split(","));
            lambdaQueryWrapper.and(wq -> wq
                    .in(MtUser::getUserNo, itemList)
                    .or()
                    .in(MtUser::getMobile, itemList));
        }
        lambdaQueryWrapper.orderByDesc(MtUser::getUpdateTime);
        List<MtUser> userList = mtUserMapper.selectList(lambdaQueryWrapper);
        List<GroupMemberDto> dataList = new ArrayList<>();
        if (userList != null && userList.size() > 0) {
            for (MtUser mtUser : userList) {
                 GroupMemberDto memberDto = new GroupMemberDto();
                 memberDto.setId(mtUser.getId());
                 memberDto.setName(mtUser.getName());
                 memberDto.setUserNo(mtUser.getUserNo());
                 memberDto.setMobile(CommonUtil.hidePhone(mtUser.getMobile()));
                 dataList.add(memberDto);
            }
        }
        return dataList;
    }

    /**
     * 查找会员列表
     *
     * @param merchantId 商户ID
     * @param keyword 关键字
     * @return
     * */
    @Override
    public List<MtUser> searchMembers(Integer merchantId, String keyword) {
       return mtUserMapper.searchMembers(merchantId, keyword);
    }

    /**
     * 设定安全的密码
     *
     * @param password 密码明文
     * @param salt 加密因子
     * @return
     */
    @Override
    public String enCodePassword(String password, String salt) {
        return MD5Util.getMD5(password + salt);
    }

    /**
     * 获取加密密码
     *
     * @param password 密码密文
     * @param salt 加密因子
     * @return
     * */
    @Override
    public String deCodePassword(String password, String salt) {
        return MD5Util.getMD5(password + salt);
    }

    /**
     * 获取会员ID列表
     *
     * @param merchantId 商户号
     * @param storeId 店铺ID
     * @return
     * */
    @Override
    public List<Integer> getUserIdList(Integer merchantId, Integer storeId) {
        return mtUserMapper.getUserIdList(merchantId, storeId);
    }
}

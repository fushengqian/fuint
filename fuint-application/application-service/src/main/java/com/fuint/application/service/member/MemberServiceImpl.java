package com.fuint.application.service.member;

import com.fuint.application.dao.entities.MtStaff;
import com.fuint.application.dao.entities.MtUserGrade;
import com.fuint.application.dao.repositories.MtUserGradeRepository;
import com.fuint.application.service.opengift.OpenGiftService;
import com.fuint.application.service.staff.StaffService;
import com.fuint.application.service.usergrade.UserGradeService;
import com.fuint.application.util.CommonUtil;
import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.base.dao.entities.TAccount;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.service.account.TAccountService;
import com.fuint.base.shiro.ShiroUser;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.dao.repositories.MtUserRepository;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.sms.SendSmsInterface;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson.JSONObject;
import java.math.BigDecimal;
import java.util.*;

/**
 * 会员业务接口实现类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MtUserRepository userRepository;

    @Autowired
    private MtUserGradeRepository userGradeRepository;

    /**
     * 短信发送接口
     */
    @Autowired
    private SendSmsInterface sendSmsService;

    /**
     * 会员等级接口
     * */
    @Autowired
    private UserGradeService userGradeService;

    /**
     * 会员等级接口
     * */
    @Autowired
    private OpenGiftService openGiftService;

    /**
     * 后台账户服务接口
     */
    @Autowired
    private TAccountService accountService;

    /**
     * 员工接口
     */
    @Autowired
    private StaffService staffService;

    /**
     * 获取当前操作会员信息
     * @param userId
     * @return
     * */
    @Override
    public MtUser getCurrentUserInfo(Integer userId) throws BusinessCheckException {
        MtUser mtUser = null;

        // 没有会员信息，则查询是否是后台收银员下单
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        if (shiroUser != null) {
            // 输入了会员ID就用会员的账号下单，否则用员工账号下单
            if (userId > 0) {
                mtUser = this.queryMemberById(userId);
            } else {
                TAccount account = accountService.findAccountById(shiroUser.getId());
                if (account != null) {
                    if (account.getStaffId() > 0) {
                        MtStaff staff = staffService.queryStaffById(account.getStaffId());
                        mtUser = this.queryMemberById(staff.getUserId());
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
    public PaginationResponse<MtUser> queryMemberListByPagination(PaginationRequest paginationRequest) {
        paginationRequest.setSortColumn(new String[]{"updateTime desc", "createTime desc"});
        PaginationResponse<MtUser> paginationResponse = userRepository.findResultsByPagination(paginationRequest);
        return paginationResponse;
    }

    /**
     * 添加会员
     *
     * @param mtUser
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "添加会员")
    public MtUser addMember(MtUser mtUser) throws BusinessCheckException {
        MtUser userInfo = this.queryMemberByMobile(mtUser.getMobile());
        if (userInfo != null) {
            return userInfo;
        }

        if (StringUtils.isEmpty(mtUser.getGradeId())) {
            MtUserGrade grade = userGradeService.getInitUserGrade();
            mtUser.setGradeId(grade.getId()+"");
        }

        mtUser.setUserNo(CommonUtil.createUserNo());
        mtUser.setBalance(new BigDecimal("0.00"));
        if (mtUser.getPoint() == null || mtUser.getPoint() < 1) {
            mtUser.setPoint(0);
        }
        if (StringUtils.isEmpty(mtUser.getIdcard())) {
            mtUser.setIdcard("");
        }
        mtUser.setSex(mtUser.getSex());
        mtUser.setStatus(StatusEnum.ENABLED.getKey());
        mtUser.setCreateTime(new Date());
        mtUser.setUpdateTime(new Date());
        if (mtUser.getStoreId() != null) {
            mtUser.setStoreId(mtUser.getStoreId());
        } else {
            mtUser.setStoreId(0);
        }

        userRepository.save(mtUser);

        mtUser = this.queryMemberByMobile(mtUser.getMobile());

        // 开卡赠礼
        openGiftService.openGift(mtUser.getId(), Integer.parseInt(mtUser.getGradeId()));

        // 新增用户发短信通知
        if (mtUser.getId() > 0 && mtUser.getStatus().equals(StatusEnum.ENABLED.getKey())) {
            // 发送短信
            List<String> mobileList = new ArrayList<String>();
            mobileList.add(mtUser.getMobile());
            // 短信模板
            try {
                Map<String, String> params = new HashMap<>();
                sendSmsService.sendSms("register-sms", mobileList, params);
            } catch (Exception e) {
                throw new BusinessCheckException("注册短信发送失败.");
            }
        }

        return mtUser;
    }

    /**
     * 更新会员信息
     *
     * @param mtUser
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "编辑会员")
    @Transactional
    public MtUser updateMember(MtUser mtUser) throws BusinessCheckException {
        mtUser.setUpdateTime(new Date());

        // 检查会员号是否重复
        if (StringUtils.isNotEmpty(mtUser.getUserNo())) {
            List<MtUser> userList = userRepository.findMembersByUserNo(mtUser.getUserNo());
            if (userList.size() > 0) {
                for(MtUser user: userList) {
                    MtUser userInfo = user;
                    if (userInfo.getId().intValue() != mtUser.getId().intValue()) {
                        throw new BusinessCheckException("该会员号与会员ID等于" + userInfo.getId() + "重复啦");
                    }
                }
            }
        }

        return userRepository.save(mtUser);
    }

    /**
     * 通过手机号添加会员
     *
     * @param mobile
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "自动注册会员信息")
    @Transactional
    public MtUser addMemberByMobile(String mobile) throws BusinessCheckException {
        MtUser mtUser = new MtUser();
        mtUser.setUserNo(CommonUtil.createUserNo());
        String nickName = mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
        mtUser.setName(nickName);
        mtUser.setMobile(mobile);
        MtUserGrade grade = userGradeService.getInitUserGrade();
        mtUser.setGradeId(grade.getId()+"");
        mtUser.setCreateTime(new Date());
        mtUser.setUpdateTime(new Date());
        mtUser.setBalance(new BigDecimal("0.00"));
        mtUser.setPoint(0);
        mtUser.setDescription("手机号登录自动注册");
        mtUser.setIdcard("");
        mtUser.setStatus(StatusEnum.ENABLED.getKey());
        mtUser.setStoreId(0);

        userRepository.save(mtUser);

        mtUser = this.queryMemberByMobile(mobile);

        // 开卡赠礼
        openGiftService.openGift(mtUser.getId(), Integer.parseInt(mtUser.getGradeId()));

        return mtUser;
    }

    /**
     * 根据手机号获取会员信息
     *
     * @param mobile 手机号
     * @throws BusinessCheckException
     */
    @Override
    public MtUser queryMemberByMobile(String mobile) {
        List<MtUser> mtUser = userRepository.queryMemberByMobile(mobile);
        if (mtUser.size() > 0) {
            return mtUser.get(0);
        } else {
            return null;
        }
    }

    /**
     * 根据会员ID获取会员信息
     *
     * @param id 会员ID
     * @return
     * @throws BusinessCheckException
     */
    @Override
    public MtUser queryMemberById(Integer id) throws BusinessCheckException {
        MtUser mtUser = userRepository.findMembersById(id);

        if (mtUser != null) {
            // 检查会员是否过期，过期就把会员等级置为初始等级
            Date endTime = mtUser.getEndTime();
            if (endTime != null) {
                Date now = new Date();
                if (endTime.before(now)) {
                    MtUserGrade grade = userGradeService.getInitUserGrade();
                    if (!mtUser.getGradeId().equals(grade.getId())) {
                        mtUser.setGradeId(grade.getId().toString());
                        userRepository.save(mtUser);
                    }
                }
            }
        }

        return mtUser;
    }

    /**
     * 根据openId获取会员信息(为空就注册)
     *
     * @param openId
     * @throws BusinessCheckException
     */
    @Override
    public MtUser queryMemberByOpenId(String openId, JSONObject userInfo) throws BusinessCheckException {
        MtUser user = userRepository.queryMemberByOpenId(openId);

        String avatar = StringUtils.isNotEmpty(userInfo.getString("avatarUrl")) ? userInfo.getString("avatarUrl") : "";
        String gender = StringUtils.isNotEmpty(userInfo.getString("gender")) ? userInfo.getString("gender") : "1";
        String country = StringUtils.isNotEmpty(userInfo.getString("country")) ? userInfo.getString("country") : "";
        String province = StringUtils.isNotEmpty(userInfo.getString("province")) ? userInfo.getString("province") : "";
        String city = StringUtils.isNotEmpty(userInfo.getString("city")) ? userInfo.getString("city") : "";

        if (user == null) {
            String nickName = userInfo.getString("nickName");
            String mobile = StringUtils.isNotEmpty(userInfo.getString("phone")) ? userInfo.getString("phone") : "";

            MtUser mtUser = new MtUser();
            if (StringUtils.isNotEmpty(mobile)) {
                MtUser mtUserMobile = this.queryMemberByMobile(mobile);
                if (mtUserMobile != null) {
                    mtUser = mtUserMobile;
                }
            }

            // 昵称为空，用手机号
            if (StringUtils.isEmpty(nickName) && StringUtils.isNotEmpty(mobile)) {
                nickName = mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
            }

            mobile = CommonUtil.replaceXSS(mobile);
            avatar = CommonUtil.replaceXSS(avatar);
            nickName = CommonUtil.replaceXSS(nickName);
            mtUser.setUserNo(CommonUtil.createUserNo());
            mtUser.setMobile(mobile);
            mtUser.setAvatar(avatar);
            mtUser.setName(nickName);
            mtUser.setOpenId(openId);
            MtUserGrade grade = userGradeService.getInitUserGrade();
            mtUser.setGradeId(grade.getId()+"");
            mtUser.setCreateTime(new Date());
            mtUser.setUpdateTime(new Date());
            mtUser.setBalance(new BigDecimal("0.00"));
            mtUser.setPoint(0);
            mtUser.setDescription("微信登录自动注册");
            mtUser.setIdcard("");
            mtUser.setStatus(StatusEnum.ENABLED.getKey());
            mtUser.setAddress(country + province + city);
            mtUser.setSex(Integer.parseInt(gender));
            mtUser.setStoreId(0);
            userRepository.save(mtUser);
            user = userRepository.queryMemberByOpenId(openId);

            // 开卡赠礼
            openGiftService.openGift(user.getId(), Integer.parseInt(user.getGradeId()));
        } else {
            // 已被禁用
            if (user.getStatus().equals(StatusEnum.DISABLE.getKey())) {
               return null;
            }

            // 更新昵称和手机号码
            String nickName = StringUtils.isNotEmpty(userInfo.getString("nickName")) ? CommonUtil.replaceXSS(userInfo.getString("nickName")) : "";
            String name = user.getName();
            String mobile = StringUtils.isNotEmpty(userInfo.getString("phone")) ? CommonUtil.replaceXSS(userInfo.getString("phone")) : "";
            if (StringUtils.isEmpty(name)) {
                nickName = CommonUtil.replaceXSS(userInfo.getString("nickName"));
                if (StringUtils.isEmpty(nickName) && StringUtils.isNotEmpty(mobile)) {
                    nickName = mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
                }
            }

            if (StringUtils.isEmpty(user.getUserNo())) {
                user.setUserNo(CommonUtil.createUserNo());
            }
            if (StringUtils.isNotEmpty(nickName)) {
                user.setName(nickName);
            }
            if (StringUtils.isNotEmpty(mobile)) {
                user.setMobile(mobile);
            }
            if (StringUtils.isNotEmpty(avatar)) {
                user.setAvatar(avatar);
            }
            if (StringUtils.isEmpty(user.getAddress())) {
                user.setAddress(country + province + city);
            }
            user.setSex(Integer.parseInt(gender));

            user.setUpdateTime(new Date());
            userRepository.save(user);
        }

        return user;
    }

    /**
     * 根据等级ID获取会员等级信息
     *
     * @param id 等级ID
     * @throws BusinessCheckException
     */
    @Override
    public MtUserGrade queryMemberGradeByGradeId(Integer id) {
        MtUserGrade gradeInfo = userGradeRepository.findOne(id);
        return gradeInfo;
    }

    /**
     * 禁用会员
     *
     * @param id       会员ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除会员")
    public Integer deleteMember(Integer id, String operator) throws BusinessCheckException {
        MtUser mtUser = this.queryMemberById(id);
        if (null == mtUser) {
            return 0;
        }

        mtUser.setStatus(StatusEnum.DISABLE.getKey());
        mtUser.setUpdateTime(new Date());
        mtUser.setOperator(operator);

        userRepository.save(mtUser);

        return mtUser.getId();
    }

    /**
     * 根据条件搜索会员分组
     * */
    @Override
    public List<MtUserGrade> queryMemberGradeByParams(Map<String, Object> params) {
        if (MapUtils.isEmpty(params)) {
            params = new HashMap<>();
        }

        Specification<MtUserGrade> specification = userGradeRepository.buildSpecification(params);
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        List<MtUserGrade> result = userGradeRepository.findAll(specification, sort);

        return result;
    }

    /**
     * 获取会员数量
     * */
    @Override
    public Long getUserCount(Integer storeId) {
        if (storeId > 0) {
            return userRepository.getUserCount(storeId);
        } else {
            return userRepository.getUserCount();
        }
    }

    /**
     * 获取会员数量
     * */
    @Override
    public Long getUserCount(Integer storeId, Date beginTime, Date endTime) {
        if (storeId > 0) {
            return userRepository.getUserCount(storeId, beginTime, endTime);
        } else {
            return userRepository.getUserCount(beginTime, endTime);
        }
    }

    /**
     * 获取会员数量
     * */
    @Override
    public Long getActiveUserCount(Integer storeId, Date beginTime, Date endTime) {
        if (storeId > 0) {
            return userRepository.getActiveUserCount(storeId, beginTime, endTime);
        } else {
            return userRepository.getActiveUserCount(beginTime, endTime);
        }
    }
}

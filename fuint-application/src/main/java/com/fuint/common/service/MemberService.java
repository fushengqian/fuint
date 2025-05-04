package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.alibaba.fastjson.JSONObject;
import com.fuint.common.dto.GroupMemberDto;
import com.fuint.common.dto.MemberTopDto;
import com.fuint.common.dto.UserDto;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtUser;
import com.fuint.repository.model.MtUserGrade;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 会员服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MemberService extends IService<MtUser> {

    /**
     * 更新活跃时间
     *
     * @param userId 会员ID
     * @param ip IP地址
     * @return
     * */
    Boolean updateActiveTime(Integer userId, String ip) throws BusinessCheckException;

    /**
     * 获取当前操作会员信息
     *
     * @param userId 会员ID
     * @param accessToken
     * @return
     * */
    MtUser getCurrentUserInfo(HttpServletRequest request, Integer userId, String accessToken) throws BusinessCheckException;

    /**
     * 分页查询会员列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<UserDto> queryMemberListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加会员
     *
     * @param  memberInfo 会员信息
     * @param  shareId 分享用户ID
     * @throws BusinessCheckException
     * @return
     */
    MtUser addMember(MtUser memberInfo, String shareId) throws BusinessCheckException;

    /**
     * 编辑会员
     *
     * @param  reqUserDto 会员信息
     * @param  modifyPassword 修改密码
     * @throws BusinessCheckException
     * @return
     */
    MtUser updateMember(MtUser reqUserDto, boolean modifyPassword) throws BusinessCheckException;

    /**
     * 通过手机号添加会员
     *
     * @param  merchantId 商户ID
     * @param  mobile 手机号
     * @param  shareId 分享用户ID
     * @param ip IP地址
     * @throws BusinessCheckException
     * @return
     */
    MtUser addMemberByMobile(Integer merchantId, String mobile, String shareId, String ip) throws BusinessCheckException;

    /**
     * 根据会员ID获取会员信息
     *
     * @param  id 会员ID
     * @throws BusinessCheckException
     * @return
     */
    MtUser queryMemberById(Integer id) throws BusinessCheckException;

    /**
     * 根据会员名称获取会员信息
     *
     * @param  merchantId 商户ID
     * @param  name 会员名称
     * @throws BusinessCheckException
     * @return
     */
    MtUser queryMemberByName(Integer merchantId, String name) throws BusinessCheckException;

    /**
     * 根据会员ID获取会员信息
     *
     * @param  merchantId 商户ID
     * @param  openId 微信openId
     * @throws BusinessCheckException
     * @return
     */
    MtUser queryMemberByOpenId(Integer merchantId, String openId, JSONObject userInfo) throws BusinessCheckException;

    /**
     * 根据会员组ID获取会员组信息
     *
     * @param  id 会员组ID
     * @throws BusinessCheckException
     * @return
     */
    MtUserGrade queryMemberGradeByGradeId(Integer id) throws BusinessCheckException;

    /**
     * 根据会员手机获取会员信息
     *
     * @param merchantId 商户ID
     * @param  mobile 会员手机
     * @throws BusinessCheckException
     * @return
     */
    MtUser queryMemberByMobile(Integer merchantId, String mobile) throws BusinessCheckException;

    /**
     * 根据会员号获取会员信息
     *
     * @param  merchantId 商户ID
     * @param  userNo 会员号
     * @throws BusinessCheckException
     * @return
     */
    MtUser queryMemberByUserNo(Integer merchantId, String userNo) throws BusinessCheckException;

    /**
     * 根据会员ID删除会员信息
     *
     * @param  id 会员ID
     * @param  operator 操作人
     * @throws BusinessCheckException
     * @return
     */
    Integer deleteMember(Integer id, String operator) throws BusinessCheckException;

    /**
     * 根据条件搜索会员分组
     *
     * @param params 查询参数
     * @throws BusinessCheckException
     * @return
     * */
    List<MtUserGrade> queryMemberGradeByParams(Map<String, Object> params) throws BusinessCheckException;

    /**
     * 获取会员数量
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @throws BusinessCheckException
     * @return
     * */
    Long getUserCount(Integer merchantId, Integer storeId) throws BusinessCheckException;

    /**
     * 获取会员数量
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @throws BusinessCheckException
     * @return
     * */
    Long getUserCount(Integer merchantId, Integer storeId, Date beginTime, Date endTime) throws BusinessCheckException;

    /**
     * 获取活跃会员数量
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @throws BusinessCheckException
     * @return
     * */
    Long getActiveUserCount(Integer merchantId, Integer storeId, Date beginTime, Date endTime) throws BusinessCheckException;

    /**
     * 重置手机号
     *
     * @param  mobile 手机号码
     * @param  userId 会员ID
     * @throws BusinessCheckException
     * @return
     */
    void resetMobile(String mobile, Integer userId) throws BusinessCheckException;

    /**
     * 获取会员消费排行榜
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     * */
    List<MemberTopDto> getMemberConsumeTopList(Integer merchantId, Integer storeId, Date startTime, Date endTime);

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
    List<GroupMemberDto> searchMembers(Integer merchantId, String keyword, String groupIds, Integer page, Integer pageSize);

    /**
     * 查找会员列表
     *
     * @param merchantId 商户ID
     * @param keyword 关键字
     * @return
     * */
    List<MtUser> searchMembers(Integer merchantId, String keyword);

    /**
     * 设定安全的密码
     *
     * @param password 密码（明文）
     * @param salt 随机因子
     * @return
     */
    String enCodePassword(String password, String salt);

    /**
     * 获取加密密码
     *
     * @param password 密码（密文）
     * @param salt 随机因子
     * @return
     * */
    String deCodePassword(String password, String salt);

    /**
     * 获取会员ID列表
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @return
     * */
    List<Integer> getUserIdList(Integer merchantId, Integer storeId);
}

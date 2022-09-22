package com.fuint.application.service.member;

import com.fuint.application.dao.entities.MtUserGrade;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.MtUser;
import com.alibaba.fastjson.JSONObject;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 会员业务接口
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
public interface MemberService {

    /**
     * 获取当前操作会员信息
     * @param userId
     * @return
     * */
    MtUser getCurrentUserInfo(Integer userId) throws BusinessCheckException;

    /**
     * 分页查询会员列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtUser> queryMemberListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加会员
     *
     * @param reqUserDto
     * @throws BusinessCheckException
     */
    MtUser addMember(MtUser reqUserDto) throws BusinessCheckException;

    /**
     * 编辑会员
     *
     * @param reqUserDto
     * @throws BusinessCheckException
     */
    MtUser updateMember(MtUser reqUserDto) throws BusinessCheckException;

    /**
     * 通过手机号添加会员
     *
     * @param mobile
     * @throws BusinessCheckException
     */
    MtUser addMemberByMobile(String mobile) throws BusinessCheckException;

    /**
     * 根据会员ID获取会员信息
     *
     * @param id 会员ID
     * @throws BusinessCheckException
     */
    MtUser queryMemberById(Integer id) throws BusinessCheckException;

    /**
     * 根据会员名称获取会员信息
     *
     * @param name 会员名称
     * @throws BusinessCheckException
     */
    MtUser queryMemberByName(String name) throws BusinessCheckException;

    /**
     * 根据会员ID获取会员信息
     *
     * @param openId 微信openId
     * @throws BusinessCheckException
     */
    MtUser queryMemberByOpenId(String openId, JSONObject userInfo) throws BusinessCheckException;

    /**
     * 根据会员组ID获取会员组信息
     *
     * @param id 会员组ID
     * @throws BusinessCheckException
     */
    MtUserGrade queryMemberGradeByGradeId(Integer id) throws BusinessCheckException;

    /**
     * 根据会员手机获取会员信息
     *
     * @param mobile 会员手机
     * @throws BusinessCheckException
     */
    MtUser queryMemberByMobile(String mobile) throws BusinessCheckException;

    /**
     * 根据会员ID 删除店铺信息
     *
     * @param id      会员ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    Integer deleteMember(Integer id, String operator) throws BusinessCheckException;

    /**
     * 根据条件搜索会员分组
     * */
    List<MtUserGrade> queryMemberGradeByParams(Map<String, Object> params) throws BusinessCheckException;

    /**
     * 获取会员数量
     * */
    Long getUserCount(Integer storeId) throws BusinessCheckException;

    /**
     * 获取会员数量
     * */
    Long getUserCount(Integer storeId, Date beginTime, Date endTime) throws BusinessCheckException;

    /**
     * 获取活跃会员数量
     * */
    Long getActiveUserCount(Integer storeId, Date beginTime, Date endTime) throws BusinessCheckException;
}

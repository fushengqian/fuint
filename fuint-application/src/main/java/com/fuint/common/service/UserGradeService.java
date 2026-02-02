package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtUser;
import com.fuint.repository.model.MtUserGrade;

import java.util.List;

/**
 * 会员等级业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface UserGradeService extends IService<MtUserGrade> {

    /**
     * 分页查询会员等级列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtUserGrade> queryUserGradeListByPagination(PaginationRequest paginationRequest);

    /**
     * 添加会员等级
     *
     * @param  reqDto
     * @throws BusinessCheckException
     * @return
     */
    MtUserGrade addUserGrade(MtUserGrade reqDto) throws BusinessCheckException;

    /**
     * 修改会员等级
     *
     * @param  reqDto
     * @throws BusinessCheckException
     * @return
     */
    MtUserGrade updateUserGrade(MtUserGrade reqDto) throws BusinessCheckException;

    /**
     * 根据ID获取会员等级信息
     *
     * @param merchantId
     * @param gradeId ID
     * @param userId
     * @return
     */
    MtUserGrade queryUserGradeById(Integer merchantId, Integer gradeId, Integer userId);

    /**
     * 根据ID删除会员等级
     *
     * @param  id      ID
     * @param  operator 操作人
     * @return
     */
    Integer deleteUserGrade(Integer id, String operator);

    /**
     * 获取默认的会员等级
     *
     * @param merchantId
     * @return
     */
    MtUserGrade getInitUserGrade(Integer merchantId);

    /**
     * 获取付费会员等级列表
     *
     * @param  merchantId
     * @param  userInfo
     * @return
     * */
    List<MtUserGrade> getPayUserGradeList(Integer merchantId, MtUser userInfo);

    /**
     * 获取商户会员等级列表
     *
     * @param  merchantId 商户ID
     * @param status 状态
     * @return
     * */
    List<MtUserGrade> getMerchantGradeList(Integer merchantId, String status);

}

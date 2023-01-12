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
    PaginationResponse<MtUserGrade> queryUserGradeListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加会员等级
     *
     * @param reqDto
     * @throws BusinessCheckException
     */
    MtUserGrade addUserGrade(MtUserGrade reqDto) throws BusinessCheckException;

    /**
     * 修改会员等级
     *
     * @param reqDto
     * @throws BusinessCheckException
     */
    MtUserGrade updateUserGrade(MtUserGrade reqDto) throws BusinessCheckException;

    /**
     * 根据ID获取会员等级信息
     *
     * @param id ID
     * @throws BusinessCheckException
     */
    MtUserGrade queryUserGradeById(Integer id) throws BusinessCheckException;

    /**
     * 根据ID删除会员等级
     *
     * @param id      ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    Integer deleteUserGrade(Integer id, String operator) throws BusinessCheckException;

    /**
     * 获取默认的会员等级
     *
     * @throws BusinessCheckException
     */
    MtUserGrade getInitUserGrade() throws BusinessCheckException;

    /**
     * 获取付费会员等级列表
     * @param userInfo
     * @throws BusinessCheckException
     * */
    List<MtUserGrade> getPayUserGradeList(MtUser userInfo) throws BusinessCheckException;
}

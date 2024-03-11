package com.fuint.common.service;

import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.TGenCode;

/**
 * 代码生成服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface GenCodeService {

    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<TGenCode> queryGenCodeListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加生成代码
     *
     * @param  tGenCode 代码参数
     * @throws BusinessCheckException
     * @return
     */
    TGenCode addGenCode(TGenCode tGenCode) throws BusinessCheckException;

    /**
     * 根据ID获取信息
     *
     * @param  id
     * @throws BusinessCheckException
     * @return
     */
    TGenCode queryGenCodeById(Integer id) throws BusinessCheckException;

    /**
     * 更新生成代码
     * @param  tGenCode
     * @throws BusinessCheckException
     * @return
     * */
    TGenCode updateGenCode(TGenCode tGenCode) throws BusinessCheckException;

    /**
     * 生成代码（自定义路径）
     * 
     * @param tableName 表名称
     * @return
     */
    void generatorCode(String tableName);

}

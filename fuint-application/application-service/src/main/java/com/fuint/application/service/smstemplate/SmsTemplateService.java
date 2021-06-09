package com.fuint.application.service.smstemplate;

import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.application.dao.entities.MtSmsTemplate;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dto.MtSmsTemplateDto;

import java.util.List;
import java.util.Map;

/**
 * 短信模板业务接口
 * Created by zach 20190820
 */
public interface SmsTemplateService {

    /**
     * 分页查询店铺列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtSmsTemplate> querySmsTemplateListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加模板
     *
     * @param reqSmsTemplateDto
     * @throws BusinessCheckException
     */
    MtSmsTemplate saveSmsTemplate(MtSmsTemplateDto reqSmsTemplateDto) throws BusinessCheckException;

    /**
     * 根据模板ID获取模板信息
     *
     * @param id ID
     * @throws BusinessCheckException
     */
    MtSmsTemplate querySmsTemplateById(Integer id) throws BusinessCheckException;

    /**
     * 根据条件搜索模板
     * */
    List<MtSmsTemplate> querySmsTemplateByParams(Map<String, Object> params) throws BusinessCheckException;
}

package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.message.SmsTemplateDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.param.SmsTemplatePage;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtSmsTemplate;

import java.util.List;
import java.util.Map;

/**
 * 短信模板业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface SmsTemplateService extends IService<MtSmsTemplate> {

    /**
     * 分页查询模板列表
     *
     * @param smsTemplatePage
     * @return
     */
    PaginationResponse<MtSmsTemplate> querySmsTemplateListByPagination(SmsTemplatePage smsTemplatePage);

    /**
     * 添加模板
     *
     * @param smsTemplateDto 短信模板
     * @param accountInfo   登录账号信息
     * @throws BusinessCheckException
     * @return
     */
    MtSmsTemplate saveSmsTemplate(SmsTemplateDto smsTemplateDto, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 删除短信模板
     * @param id
     * @param accountInfo
     * @return
     * */
    void deleteTemplate(Integer id, AccountInfo accountInfo);

    /**
     * 根据模板ID获取模板信息
     *
     * @param id ID
     * @return
     */
    MtSmsTemplate querySmsTemplateById(Integer id);

    /**
     * 根据条件搜索模板
     *
     * @param params 搜索条件
     * @return
     * */
    List<MtSmsTemplate> querySmsTemplateByParams(Map<String, Object> params);

}

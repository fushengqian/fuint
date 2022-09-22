package com.fuint.application.service.smstemplate;

import com.fuint.application.enums.StatusEnum;
import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.application.dao.entities.MtSmsTemplate;
import com.fuint.application.dao.repositories.MtSmsTemplateRepository;
import com.fuint.application.dto.MtSmsTemplateDto;
import com.fuint.exception.BusinessCheckException;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * 短信模板业务实现类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Service
public class SmsTemplateServiceImpl implements SmsTemplateService {

    @Autowired
    private MtSmsTemplateRepository smsTemplateRepository;

    /**
     * 分页查询模板列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtSmsTemplate> querySmsTemplateListByPagination(PaginationRequest paginationRequest) {
        paginationRequest.setSortColumn(new String[]{"id asc", "status asc"});
        PaginationResponse<MtSmsTemplate> paginationResponse = smsTemplateRepository.findResultsByPagination(paginationRequest);
        return paginationResponse;
    }

    /**
     * 保存模板信息
     *
     * @param mtSmsTemplateDto
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "保存短信模板")
    public MtSmsTemplate saveSmsTemplate(MtSmsTemplateDto mtSmsTemplateDto) {
        MtSmsTemplate mtSmsTemplate = new MtSmsTemplate();

        if (mtSmsTemplateDto.getId() == null) {
            mtSmsTemplate.setCreateTime(new Date());
            mtSmsTemplate.setUpdateTime(new Date());
        } else {
            mtSmsTemplate.setId(mtSmsTemplateDto.getId());
            mtSmsTemplate.setUpdateTime(new Date());
        }

        mtSmsTemplate.setCode(mtSmsTemplateDto.getCode());
        mtSmsTemplate.setName(mtSmsTemplateDto.getName());
        mtSmsTemplate.setUname(mtSmsTemplateDto.getUname());
        mtSmsTemplate.setContent(mtSmsTemplateDto.getContent());
        mtSmsTemplate.setStatus(mtSmsTemplateDto.getStatus());
        mtSmsTemplate.setOperator(mtSmsTemplate.getOperator());

        mtSmsTemplate = smsTemplateRepository.save(mtSmsTemplate);

        return mtSmsTemplate;
    }

    /**
     * 根据ID删除数据
     *
     * @param id       模板ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除短信模板")
    public void deleteTemplate(Integer id, String operator) {
        MtSmsTemplate MtTemplate = smsTemplateRepository.findOne(id);
        if (null == MtTemplate) {
            return;
        }

        MtTemplate.setStatus(StatusEnum.DISABLE.getKey());
        MtTemplate.setUpdateTime(new Date());

        smsTemplateRepository.save(MtTemplate);
    }

    /**
     * 根据D获取信息
     *
     * @param  id 模板ID
     * @throws BusinessCheckException
     */
    @Override
    public MtSmsTemplate querySmsTemplateById(Integer id) {
        return smsTemplateRepository.findOne(id);
    }

    @Override
    public List<MtSmsTemplate> querySmsTemplateByParams(Map<String, Object> params) {
        if (MapUtils.isEmpty(params)) {
            params = new HashMap<>();
        }

        Specification<MtSmsTemplate> specification = smsTemplateRepository.buildSpecification(params);
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        List<MtSmsTemplate> result = smsTemplateRepository.findAll(specification, sort);

        return result;
    }
}

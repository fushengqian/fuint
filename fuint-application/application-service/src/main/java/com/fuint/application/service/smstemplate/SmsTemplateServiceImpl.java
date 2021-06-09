package com.fuint.application.service.smstemplate;

import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.application.dao.entities.MtSmsTemplate;
import com.fuint.application.dao.repositories.MtSmsTemplateRepository;
import com.fuint.application.dto.MtSmsTemplateDto;
import com.fuint.exception.BusinessCheckException;
import com.fuint.exception.BusinessRuntimeException;
import com.fuint.application.enums.StatusEnum;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 短信模板业务实现类
 * Created by zach 20190820
 */
@Service
public class SmsTemplateServiceImpl implements SmsTemplateService {

    private static final Logger log = LoggerFactory.getLogger(SmsTemplateServiceImpl.class);

    @Autowired
    private MtSmsTemplateRepository smsTemplateRepository;

    /**
     * 分页查询模板列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtSmsTemplate> querySmsTemplateListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException {
        paginationRequest.setSortColumn(new String[]{"id asc", "status asc"});
        PaginationResponse<MtSmsTemplate> paginationResponse = smsTemplateRepository.findResultsByPagination(paginationRequest);
        return paginationResponse;
    }

    /**
     * 添加模板信息
     *
     * @param mtSmsTemplateDto
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "编辑短信模板")
    public MtSmsTemplate saveSmsTemplate(MtSmsTemplateDto mtSmsTemplateDto) throws BusinessCheckException {
        MtSmsTemplate mtSmsTemplate = new MtSmsTemplate();

        if (null != mtSmsTemplateDto.getId()) {
            mtSmsTemplate.setId(mtSmsTemplateDto.getId());
        }

        mtSmsTemplate.setCode(mtSmsTemplateDto.getCode());
        mtSmsTemplate.setName(mtSmsTemplateDto.getName());
        mtSmsTemplate.setUname(mtSmsTemplateDto.getUname());
        mtSmsTemplate.setContent(mtSmsTemplateDto.getContent());
        mtSmsTemplate.setStatus(StatusEnum.ENABLED.getKey());
        String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
        mtSmsTemplate.setOperator(operator);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dt = sdf.format(new Date());
            Date addtime = sdf.parse(dt);
            mtSmsTemplate.setUpdateTime(addtime);
            mtSmsTemplate.setCreateTime(addtime);
        } catch (ParseException e) {
            throw new BusinessRuntimeException("日期转换异常" + e.getMessage());
        }

        mtSmsTemplate = smsTemplateRepository.save(mtSmsTemplate);

        return mtSmsTemplate;
    }

    /**
     * 根据D获取信息
     *
     * @param id 模板ID
     * @throws BusinessCheckException
     */
    @Override
    public MtSmsTemplate querySmsTemplateById(Integer id) throws BusinessCheckException {
        return smsTemplateRepository.findOne(id);
    }

    @Override
    public List<MtSmsTemplate> querySmsTemplateByParams(Map<String, Object> params) throws BusinessCheckException {
        if (MapUtils.isEmpty(params)) {
            params = new HashMap<>();
        }

        Specification<MtSmsTemplate> specification = smsTemplateRepository.buildSpecification(params);
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        List<MtSmsTemplate> result = smsTemplateRepository.findAll(specification, sort);

        return result;
    }
}

package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.message.SmsTemplateDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.SmsTemplatePage;
import com.fuint.common.service.SmsTemplateService;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtSmsTemplateMapper;
import com.fuint.repository.model.MtSmsTemplate;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短信模板业务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor(onConstructor_= {@Lazy})
public class SmsTemplateServiceImpl extends ServiceImpl<MtSmsTemplateMapper, MtSmsTemplate> implements SmsTemplateService {

    private MtSmsTemplateMapper mtSmsTemplateMapper;

    /**
     * 分页查询模板列表
     *
     * @param smsTemplatePage
     * @return
     */
    @Override
    public PaginationResponse<MtSmsTemplate> querySmsTemplateListByPagination(SmsTemplatePage smsTemplatePage) {
        Page<MtSmsTemplate> pageHelper = PageHelper.startPage(smsTemplatePage.getPage(), smsTemplatePage.getPageSize());
        LambdaQueryWrapper<MtSmsTemplate> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtSmsTemplate::getStatus, StatusEnum.DISABLE.getKey());

        Integer merchantId = smsTemplatePage.getMerchantId();
        if (merchantId != null) {
            lambdaQueryWrapper.eq(MtSmsTemplate::getMerchantId, merchantId);
        }
        String name = smsTemplatePage.getName();
        if (StringUtils.isNotBlank(name)) {
            lambdaQueryWrapper.like(MtSmsTemplate::getName, name);
        }
        String uname = smsTemplatePage.getUname();
        if (StringUtils.isNotBlank(uname)) {
            lambdaQueryWrapper.eq(MtSmsTemplate::getUname, uname);
        }
        String code = smsTemplatePage.getCode();
        if (StringUtils.isNotBlank(code)) {
            lambdaQueryWrapper.eq(MtSmsTemplate::getCode, code);
        }
        String status = smsTemplatePage.getStatus();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtSmsTemplate::getStatus, status);
        }

        lambdaQueryWrapper.orderByDesc(MtSmsTemplate::getId);
        List<MtSmsTemplate> dataList = mtSmsTemplateMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(smsTemplatePage.getPage(), smsTemplatePage.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtSmsTemplate> paginationResponse = new PaginationResponse(pageImpl, MtSmsTemplate.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 保存模板信息
     *
     * @param mtSmsTemplateDto 短信模板
     * @param accountInfo 登录账号信息
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "保存短信模板")
    public MtSmsTemplate saveSmsTemplate(SmsTemplateDto mtSmsTemplateDto,  AccountInfo accountInfo) throws BusinessCheckException {
        MtSmsTemplate mtSmsTemplate = new MtSmsTemplate();
        mtSmsTemplate.setMerchantId(mtSmsTemplateDto.getMerchantId());
        mtSmsTemplate.setCode(mtSmsTemplateDto.getCode());
        mtSmsTemplate.setName(mtSmsTemplateDto.getName());
        mtSmsTemplate.setUname(mtSmsTemplateDto.getUname());
        mtSmsTemplate.setContent(mtSmsTemplateDto.getContent());
        mtSmsTemplate.setStatus(mtSmsTemplateDto.getStatus());
        mtSmsTemplate.setOperator(accountInfo.getAccountName());

        if (mtSmsTemplateDto.getId() == null) {
            mtSmsTemplate.setCreateTime(new Date());
            mtSmsTemplate.setUpdateTime(new Date());
            mtSmsTemplateMapper.insert(mtSmsTemplate);
        } else {
            MtSmsTemplate oldSmsTemplate = getById(mtSmsTemplateDto.getId());
            if (oldSmsTemplate == null) {
                throw new BusinessCheckException("该短信模板不存在");
            }
            if (!oldSmsTemplate.getMerchantId().equals(accountInfo.getMerchantId())) {
                throw new BusinessCheckException("无操作权限");
            }
            mtSmsTemplate.setMerchantId(oldSmsTemplate.getMerchantId());
            mtSmsTemplate.setId(mtSmsTemplateDto.getId());
            mtSmsTemplate.setUpdateTime(new Date());
            this.updateById(mtSmsTemplate);
        }

        return mtSmsTemplate;
    }

    /**
     * 根据ID删除数据
     *
     * @param id 模板ID
     * @param accountInfo 操作人
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "删除短信模板")
    public void deleteTemplate(Integer id, AccountInfo accountInfo) {
        MtSmsTemplate mtTemplate = mtSmsTemplateMapper.selectById(id);
        if (null == mtTemplate) {
            return;
        }
        if (!mtTemplate.getMerchantId().equals(accountInfo.getMerchantId())) {
            return;
        }

        mtTemplate.setStatus(StatusEnum.DISABLE.getKey());
        mtTemplate.setUpdateTime(new Date());

        mtSmsTemplateMapper.updateById(mtTemplate);
    }

    /**
     * 根据D获取信息
     *
     * @param  id 模板ID
     * @return
     */
    @Override
    public MtSmsTemplate querySmsTemplateById(Integer id) {
        return mtSmsTemplateMapper.selectById(id);
    }

    /**
     * 根据参数查询短信模板
     *
     * @param params 查询参数
     * @return
     * */
    @Override
    public List<MtSmsTemplate> querySmsTemplateByParams(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        return mtSmsTemplateMapper.selectByMap(params);
    }
}

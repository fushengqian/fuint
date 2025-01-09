package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.GenCodeService;
import com.fuint.common.util.VelocityInitializer;
import com.fuint.common.util.VelocityUtils;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.exception.BusinessRuntimeException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.bean.ColumnBean;
import com.fuint.repository.mapper.TGenCodeMapper;
import com.fuint.repository.model.TGenCode;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

/**
 * 代码生成服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor
public class GenCodeServiceImpl implements GenCodeService {

    private static final Logger logger = LoggerFactory.getLogger(GenCodeServiceImpl.class);

    private TGenCodeMapper tGenCodeMapper;

    /**
     * 分页查询生成代码列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<TGenCode> queryGenCodeListByPagination(PaginationRequest paginationRequest) {
        Page<TGenCode> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<TGenCode> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(TGenCode::getStatus, StatusEnum.DISABLE.getKey());

        String tableName = paginationRequest.getSearchParams().get("tableName") == null ? "" : paginationRequest.getSearchParams().get("tableName").toString();
        if (org.apache.commons.lang.StringUtils.isNotBlank(tableName)) {
            lambdaQueryWrapper.like(TGenCode::getTableName, tableName);
        }
        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (org.apache.commons.lang.StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(TGenCode::getStatus, status);
        }

        lambdaQueryWrapper.orderByAsc(TGenCode::getId);
        List<TGenCode> dataList = tGenCodeMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<TGenCode> paginationResponse = new PaginationResponse(pageImpl, TGenCode.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 添加生成代码
     *
     * @param tGenCode 生成代码
     * @return
     */
    @Override
    @OperationServiceLog(description = "新增生成代码")
    public TGenCode addGenCode(TGenCode tGenCode) throws BusinessCheckException {
        tGenCode.setStatus(StatusEnum.ENABLED.getKey());
        Date dateTime = new Date();
        tGenCode.setCreateTime(dateTime);
        tGenCode.setUpdateTime(dateTime);
        Integer id = tGenCodeMapper.insert(tGenCode);
        if (id > 0) {
            return tGenCode;
        } else {
            logger.error("新增生成代码失败.");
            throw new BusinessCheckException("新增生成代码失败.");
        }
    }

    /**
     * 根据ID获取生成代码
     *
     * @param id 生成代码ID
     */
    @Override
    public TGenCode queryGenCodeById(Integer id) {
        return tGenCodeMapper.selectById(id);
    }

    /**
     * 修改生成代码
     *
     * @param tGenCode
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "修改生成代码")
    public TGenCode updateGenCode(TGenCode tGenCode) {
        Date dateTime = new Date();
        tGenCode.setUpdateTime(dateTime);
        tGenCodeMapper.updateById(tGenCode);
        return tGenCode;
    }

    /**
     * 生成代码（自定义路径）
     *
     * @param tableName 表名称
     * @return
     */
    @Override
    public void generatorCode(String tableName) throws BusinessRuntimeException {
        // 查询表信息
        TGenCode table = tGenCodeMapper.findGenCodeByTableName(tableName);
        if (table == null) {
            throw new BusinessRuntimeException("渲染模板失败，该表不存在.");
        }

        List<ColumnBean> columns = tGenCodeMapper.getTableColumnList(table.getTablePrefix() + table.getTableName());

        VelocityInitializer.initVelocity();
        VelocityContext context = VelocityUtils.prepareContext(table, columns);

        // 获取模板列表
        List<String> templates = VelocityUtils.getTemplateList();
        for (String template : templates) {
            if (!StringUtils.containsAny(template, "sql.vm", "api.js.vm", "index.vue.vm")) {
                // 渲染模板
                StringWriter sw = new StringWriter();
                Template tpl = Velocity.getTemplate(template, "UTF-8");
                tpl.merge(context, sw);
                try {
                    String path = getGenPath(table, template);
                    logger.info("path ====== {}", path);
                    FileUtils.writeStringToFile(new File(path), sw.toString(), "UTF-8");
                } catch (IOException e) {
                    throw new BusinessRuntimeException("渲染模板失败，表名：" + table.getTableName());
                }
            }
        }
    }

    /**
     * 获取代码生成地址
     *
     * @param table 业务表信息
     * @param template 模板文件路径
     * @return 生成地址
     */
    public static String getGenPath(TGenCode table, String template) {
        String genPath = table.getBackendPath();
        if (StringUtils.equals(genPath, "/")) {
            return System.getProperty("user.dir") + File.separator + "src" + File.separator + VelocityUtils.getFileName(template, table);
        }
        return genPath + File.separator + VelocityUtils.getFileName(template, table);
    }
}

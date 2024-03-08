package com.fuint.common.service.impl;

import com.fuint.common.service.GenCodeService;
import com.fuint.common.util.VelocityInitializer;
import com.fuint.common.util.VelocityUtils;
import com.fuint.framework.exception.BusinessRuntimeException;
import com.fuint.repository.mapper.TGenCodeMapper;
import com.fuint.repository.model.TGenCode;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
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
     * 生成代码（自定义路径）
     * 
     * @param tableName 表名称
     */
    @Override
    public void generatorCode(String tableName) throws BusinessRuntimeException {
        // 查询表信息
        TGenCode table = tGenCodeMapper.findGenCodeByTableName(tableName);

        VelocityInitializer.initVelocity();

        VelocityContext context = VelocityUtils.prepareContext(table);

        // 获取模板列表
        List<String> templates = VelocityUtils.getTemplateList();
        for (String template : templates) {
            if (!StringUtils.containsAny(template, "sql.vm", "api.js.vm", "index.vue.vm", "index-tree.vue.vm")) {
                // 渲染模板
                StringWriter sw = new StringWriter();
                Template tpl = Velocity.getTemplate(template, "UTF-8");
                tpl.merge(context, sw);
                try {
                    String path = getGenPath(table, template);
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
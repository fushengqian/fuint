package com.fuint.common.util;

import java.util.ArrayList;
import java.util.List;
import com.fuint.repository.bean.ColumnBean;
import org.apache.commons.lang3.StringUtils;
import com.fuint.repository.model.TGenCode;
import com.fuint.utils.StringUtil;
import org.apache.velocity.VelocityContext;

/**
 * 模板处理工具类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class VelocityUtils {

    /** 数据库项目路径 */
    private static final String REPOSITORY_PATH = "/fuint-repository/src/main";

    /** mapper路径 */
    private static final String MAPPER_PATH = "/java/com/fuint/repository/mapper";

    /** model路径 */
    private static final String MODEL_PATH = "/java/com/fuint/repository/model";

    /** mybatis xml路径 */
    private static final String MAPPER_XML_PATH = "/resources/mapper";

    /** 接口服务路径 */
    private static final String SERVICE_PATH = "/fuint-application/src/main/java/com/fuint/common/service";

    /** 控制器路径 */
    private static final String CONTROLLER_PATH = "/fuint-application/src/main/java/com/fuint/module/backendApi/controller";

    /**
     * 设置模板变量信息
     *
     * @return 模板列表
     */
    public static VelocityContext prepareContext(TGenCode genTable, List<ColumnBean> columns) {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("tablePrefix", genTable.getTablePrefix().replaceAll("_", ""));
        velocityContext.put("tableName", genTable.getTableName());
        velocityContext.put("moduleName", genTable.getModuleName());
        String modelName = CommonUtil.firstLetterToUpperCase(genTable.getTablePrefix()).replaceAll("_", "") + CommonUtil.firstLetterToUpperCase(CommonUtil.toCamelCase(genTable.getTableName()));
        velocityContext.put("modelName", modelName);
        velocityContext.put("basePackage", getPackagePrefix(genTable.getPackageName()));
        velocityContext.put("packageName", genTable.getPackageName());
        velocityContext.put("pkColumn", genTable.getPkName());
        velocityContext.put("author", genTable.getAuthor());
        velocityContext.put("table", genTable);
        String tableClass = CommonUtil.toCamelCase(genTable.getTableName());
        velocityContext.put("serviceName", tableClass);
        tableClass = CommonUtil.firstLetterToUpperCase(tableClass);
        String tablePrefix = CommonUtil.firstLetterToUpperCase(genTable.getTablePrefix()).replaceAll("_", "");
        velocityContext.put("className", tablePrefix + tableClass);
        velocityContext.put("tableClass", tableClass);

        if (columns != null && columns.size() > 0) {
            for (ColumnBean columnBean : columns) {
                 columnBean.setField(CommonUtil.toCamelCase(columnBean.getField()));
                 if (columnBean.getType().equals("char") || columnBean.getType().equals("varchar") || columnBean.getType().equals("text")) {
                     columnBean.setType("String");
                 } else if(columnBean.getType().equals("int") || columnBean.getType().equals("tinyint")) {
                     columnBean.setType("Integer");
                 } else if(columnBean.getType().equals("datetime")) {
                     columnBean.setType("Date");
                }
            }
        }
        velocityContext.put("columns", columns);
        return velocityContext;
    }

    /**
     * 获取模板信息
     *
     * @return 模板列表
     */
    public static List<String> getTemplateList() {
        String useWebType = "vm/vue";
        List<String> templates = new ArrayList<>();
        templates.add("vm/java/model.java.vm");
        templates.add("vm/java/mapper.java.vm");
        templates.add("vm/java/service.java.vm");
        templates.add("vm/java/serviceImpl.java.vm");
        templates.add("vm/java/BackendController.java.vm");
        templates.add("vm/xml/mapper.xml.vm");
        templates.add("vm/sql/sql.vm");
        templates.add("vm/js/api.js.vm");
        templates.add(useWebType + "/index.vue.vm");
        return templates;
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, TGenCode genTable) {
        // 文件名称
        String fileName = "";
        // 模块名
        String moduleName = genTable.getModuleName();
        // 表名称
        String tableName = CommonUtil.firstLetterToUpperCase(CommonUtil.toCamelCase(genTable.getTableName()));
        // 表前缀
        String tablePrefix = CommonUtil.firstLetterToUpperCase(genTable.getTablePrefix()).replaceAll("_", "");
        String vuePath = "/src";
        if (template.contains("model.java.vm")) {
            fileName = StringUtil.format("{}/{}.java", REPOSITORY_PATH + MODEL_PATH, tablePrefix + tableName);
        } else if (template.contains("mapper.java.vm")) {
            fileName = StringUtil.format("{}/{}Mapper.java", REPOSITORY_PATH + MAPPER_PATH, tablePrefix + tableName);
        } else if (template.contains("service.java.vm")) {
            fileName = StringUtil.format("{}/{}Service.java", SERVICE_PATH, tableName);
        } else if (template.contains("serviceImpl.java.vm")) {
            fileName = StringUtil.format("{}/impl/{}ServiceImpl.java", SERVICE_PATH, tableName);
        } else if (template.contains("BackendController.java.vm")) {
            fileName = StringUtil.format("{}/{}Controller.java", CONTROLLER_PATH, "Backend" + tableName);
        } else if (template.contains("mapper.xml.vm")) {
            fileName = StringUtil.format("{}/{}Mapper.xml", REPOSITORY_PATH + MAPPER_XML_PATH, tablePrefix + tableName);
        } else if (template.contains("api.js.vm")) {
            fileName = StringUtil.format("{}/api/{}/{}.js", vuePath, moduleName, tablePrefix + tableName);
        } else if (template.contains("index.vue.vm")) {
            fileName = StringUtil.format("{}/views/{}/{}/index.vue", vuePath, moduleName, tablePrefix + tableName);
        }

        return fileName;
    }

    /**
     * 获取包前缀
     *
     * @param packageName 包名称
     * @return 包前缀名称
     */
    public static String getPackagePrefix(String packageName) {
        int lastIndex = packageName.lastIndexOf(".");
        return StringUtils.substring(packageName, 0, lastIndex);
    }
}

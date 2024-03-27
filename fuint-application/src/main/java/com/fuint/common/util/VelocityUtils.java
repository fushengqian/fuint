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

    /** 项目空间路径 */
    private static final String PROJECT_PATH = "main/java";

    /** mybatis空间路径 */
    private static final String MYBATIS_PATH = "main/resources/mapper";

    /**
     * 设置模板变量信息
     *
     * @return 模板列表
     */
    public static VelocityContext prepareContext(TGenCode genTable, List<ColumnBean> columns) {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("tablePrefix", genTable.getTablePrefix());
        velocityContext.put("tableName", genTable.getTableName());
        velocityContext.put("moduleName", genTable.getModuleName());
        String modelName = CommonUtil.firstLetterToUpperCase(genTable.getTablePrefix()).replaceAll("_", "") + CommonUtil.firstLetterToUpperCase(genTable.getTableName());
        velocityContext.put("modelName", modelName);
        velocityContext.put("basePackage", getPackagePrefix(genTable.getPackageName()));
        velocityContext.put("packageName", genTable.getPackageName());
        velocityContext.put("pkColumn", genTable.getPkName());
        velocityContext.put("author", genTable.getAuthor());
        velocityContext.put("table", genTable);
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
        templates.add("vm/java/controller.java.vm");
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
        // 包路径
        String packageName = genTable.getPackageName();
        // 模块名
        String moduleName = genTable.getModuleName();
        // 大写类名
        String className = genTable.getTableName();
        // 业务名称
        String businessName = genTable.getModuleName();
        // 模型名称
        String modelName = CommonUtil.firstLetterToUpperCase(genTable.getTablePrefix()).replaceAll("_", "") + CommonUtil.firstLetterToUpperCase(genTable.getTableName());

        String javaPath = PROJECT_PATH + "/" + StringUtils.replace(packageName, ".", "/");
        String mybatisPath = MYBATIS_PATH + "/" + moduleName;
        String vuePath = "vue";

        if (template.contains("model.java.vm")) {
            fileName = StringUtil.format("{}/model/{}.java", javaPath, modelName);
        } else if (template.contains("mapper.java.vm")) {
            fileName = StringUtil.format("{}/mapper/{}Mapper.java", javaPath, className);
        } else if (template.contains("service.java.vm")) {
            fileName = StringUtil.format("{}/service/I{}Service.java", javaPath, className);
        } else if (template.contains("serviceImpl.java.vm")) {
            fileName = StringUtil.format("{}/service/impl/{}ServiceImpl.java", javaPath, className);
        } else if (template.contains("controller.java.vm")) {
            fileName = StringUtil.format("{}/controller/{}Controller.java", javaPath, className);
        } else if (template.contains("mapper.xml.vm")) {
            fileName = StringUtil.format("{}/{}Mapper.xml", mybatisPath, className);
        } else if (template.contains("sql.vm")) {
            fileName = businessName + "Menu.sql";
        } else if (template.contains("api.js.vm")) {
            fileName = StringUtil.format("{}/api/{}/{}.js", vuePath, moduleName, businessName);
        } else if (template.contains("index.vue.vm")) {
            fileName = StringUtil.format("{}/views/{}/{}/index.vue", vuePath, moduleName, businessName);
        } else if (template.contains("index-tree.vue.vm")) {
            fileName = StringUtil.format("{}/views/{}/{}/index.vue", vuePath, moduleName, businessName);
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

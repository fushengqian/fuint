package com.fuint.common.service;

/**
 * 代码生成服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface GenCodeService {

    /**
     * 生成代码（自定义路径）
     * 
     * @param tableName 表名称
     * @return 数据
     */
    void generatorCode(String tableName);

}

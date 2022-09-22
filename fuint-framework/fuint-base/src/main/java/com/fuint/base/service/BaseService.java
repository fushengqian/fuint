package com.fuint.base.service;

import java.util.Set;

/**
 * 基础接口服务类
 *
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
public interface BaseService {

    /**
     * 根据用户账户获取角色
     * 
     * @param accountId
     * @return
     */
    Set<String> findRoles(Long accountId);
}

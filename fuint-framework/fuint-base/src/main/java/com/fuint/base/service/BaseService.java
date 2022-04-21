/**
 * fuint.cn Inc.
 * Copyright (c) 2019-2022 All Rights Reserved.
 */
package com.fuint.base.service;

import java.util.Set;

/**
 * 基础接口服务类
 * 
 * @author fsq
 * @version $Id: BaseService.java
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

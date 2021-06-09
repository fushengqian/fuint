/**
 * rainbow.com Inc.
 * Copyright (c) 2014-2015 All Rights Reserved.
 */
package com.fuint.base.service;


import java.util.Set;

/**
 * 基础接口服务类
 * 
 * @author fsq
 * @version $Id: BaseService.java, v 0.1 2015年11月20日 上午10:29:49 fsq Exp $
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

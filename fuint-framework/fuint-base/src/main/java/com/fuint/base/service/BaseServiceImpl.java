/**
 * rainbow.com Inc.
 * Copyright (c) 2014-2015 All Rights Reserved.
 */
package com.fuint.base.service;

import java.util.*;


import com.fuint.base.dao.entities.TAccount;
import com.fuint.base.dao.entities.TAccountDuty;
import com.fuint.base.dao.entities.TSource;
import com.fuint.base.service.account.TAccountService;
import com.fuint.base.service.source.TSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 基础接口服务类
 *
 * @author fsq
 * @version $Id: BaseServiceImpl.java, v 0.1 2015年11月20日 上午10:31:07 fsq Exp $
 */
@Service
public class BaseServiceImpl implements BaseService {

    /**
     * 账户接口服务
     */
    @Autowired
    private TAccountService tAccountService;
    /**
     * 资源接口服务
     */
    @Autowired
    private TSourceService tResourceService;


    public Set<String> findRoles(Long accountId) {
        TAccount user = this.tAccountService.findAccountById(accountId);
        Set<String> roles = new HashSet<String>();
        if (user != null) {
            Iterator<TAccountDuty> it = user.getHtAccountDuties().iterator();
            TAccountDuty htAccountDuty = null;
            while (it.hasNext()) {
                htAccountDuty = it.next();
                roles.add(htAccountDuty.gettDuty().getName());
            }
        }
        return roles;
    }

}

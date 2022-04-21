/**
 * fuint.cn Inc.
 * Copyright (c) 2019-2022 All Rights Reserved.
 */
package com.fuint.base.service;

import java.util.*;
import com.fuint.base.dao.entities.TAccount;
import com.fuint.base.dao.entities.TAccountDuty;
import com.fuint.base.service.account.TAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 基础接口服务类
 *
 * @author fsq
 * @version $Id: BaseServiceImpl.java
 */
@Service
public class BaseServiceImpl implements BaseService {

    /**
     * 账户接口服务
     */
    @Autowired
    private TAccountService tAccountService;

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

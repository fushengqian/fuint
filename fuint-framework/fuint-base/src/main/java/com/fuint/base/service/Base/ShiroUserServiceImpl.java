package com.fuint.base.service.Base;

import com.fuint.base.dao.entities.*;
import com.fuint.base.service.account.TAccountService;
import com.fuint.base.service.duty.TDutyService;
import com.fuint.base.service.platform.TPlatformService;
import com.fuint.base.service.source.TSourceService;
import com.fuint.base.shiro.ShiroUser;
import com.fuint.base.shiro.exception.AccountInvalidException;
import com.fuint.util.Encodes;
import org.apache.commons.collections.map.HashedMap;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * shiro user 接口
 * Created by hanxiaoqiang on 16/8/4.
 */
@Service
public class ShiroUserServiceImpl implements ShiroUserService {

    private static final Logger logger = LoggerFactory.getLogger(ShiroUserServiceImpl.class);

    @Autowired
    private TAccountService tAccountService;

    @Autowired
    private TDutyService tDutyService;

    @Autowired
    private TSourceService tSourceService;

    @Autowired
    private TPlatformService tPlatformService;

    /**
     * 根据登录账户名称创建shiro user
     *
     * @param accountName
     * @return
     */
    @Override
    public SimpleAuthenticationInfo createShiroUserByAccountName(String accountName) {
        TAccount user = tAccountService.findByAccountName(accountName);
        if (user != null) {
            if (user.getLocked() == 1) {
                logger.info("账户{}被锁定!", accountName);
                throw new LockedAccountException(); //帐号锁定
            }
            if (user.getAccountStatus() == 0 || user.getIsActive() == 0) {
                logger.info("账户{}无效!status={},activeStatus={}", accountName, user.getAccountStatus(), user.getIsActive());
                throw new AccountInvalidException();
            }
            byte[] salt = Encodes.decodeHex(user.getSalt());
            ShiroUser shiroUser = new ShiroUser(user);
            if (user.gettPlatform() != null) {
                TPlatform tPlatform = tPlatformService.getPlatformById(user.gettPlatform().getId());
                if (tPlatform != null) {
                    user.settPlatform(tPlatform);
                } else {
                    user.settPlatform(null);
                }
            }
            shiroUser.settPlatform(user.gettPlatform());
            List<TDuty> duties = tDutyService.findDutiesByAccountId(user.getId());
            if (duties != null && duties.size() > 0) {
                shiroUser.setDuties(duties);
            }

            List<TSource> sources = tSourceService.findSourcesByAccountId(user.getId());
            if (sources != null && sources.size() > 0) {
                shiroUser.setSources(delRepeated(sources));
            }
            return new SimpleAuthenticationInfo(
                    shiroUser,
                    user.getPassword(), ByteSource.Util.bytes(salt), accountName);
        } else {
            logger.error("用户{}不存在", accountName);
            return null;
        }
    }

    /**
     * 去重
     *
     * @param sources
     * @return
     */
    private List<TSource> delRepeated(List<TSource> sources) {
        List<TSource> distinct = new ArrayList<TSource>();
        if (sources != null) {
            Map<Long, Boolean> sourceMap = new HashedMap();
            for (TSource tSource : sources) {
                if (sourceMap.get(tSource.getId()) == null) {
                    sourceMap.put(tSource.getId(), true);
                    distinct.add(tSource);
                }
            }
        }
        return distinct;
    }

}

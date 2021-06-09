/**
 * cyw.com Inc.
 * Copyright (c) 2014-2015 All Rights Reserved.
 */
package com.fuint.base.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import com.fuint.base.dao.entities.TAccount;
import org.springframework.stereotype.Repository;


/**
 * 账户Repository
 * 
 * @author fsq
 * @version $Id: TAccountRepository.java, v 0.1 2015年10月26日 上午10:04:16 fsq Exp $
 */
@Repository("tAccountRepository")
public interface TAccountRepository extends BaseRepository<TAccount, Long> {

    /**
     * SPRING DATA JPA 可以通过根据方法名以及关键字进行查询操作。
     * (仅需要进行方法的定义，不需要具体的实现)
     * 
     * 关键字：
     * And --- 等价于 SQL 中的 and 关键字，比如 findByUsernameAndPassword(String user, Striang pwd)；
        Or --- 等价于 SQL 中的 or 关键字，比如 findByUsernameOrAddress(String user, String addr)；
        Between --- 等价于 SQL 中的 between 关键字，比如 findBySalaryBetween(int max, int min)；
        LessThan --- 等价于 SQL 中的 "<"，比如 findBySalaryLessThan(int max)；
        GreaterThan --- 等价于 SQL 中的">"，比如 findBySalaryGreaterThan(int min)；
        IsNull --- 等价于 SQL 中的 "is null"，比如 findByUsernameIsNull()；
        IsNotNull --- 等价于 SQL 中的 "is not null"，比如 findByUsernameIsNotNull()；
        NotNull --- 与 IsNotNull 等价；
        Like --- 等价于 SQL 中的 "like"，比如 findByUsernameLike(String user)；
        NotLike --- 等价于 SQL 中的 "not like"，比如 findByUsernameNotLike(String user)；
        OrderBy --- 等价于 SQL 中的 "order by"，比如 findByUsernameOrderBySalaryAsc(String user)；
        Not --- 等价于 SQL 中的 "！ ="，比如 findByUsernameNot(String user)；
        In --- 等价于 SQL 中的 "in"，比如 findByUsernameIn(Collection<String> userList) ，方法的参数可以是 Collection 类型，也可以是数组或者不定长参数；
        NotIn --- 等价于 SQL 中的 "not in"，比如 findByUsernameNotIn(Collection<String> userList) ，方法的参数可以是 Collection 类型，也可以是数组或者不定长参数；
     */

    /**
     * 根据账户编码查询账户信息实体
     * 
     * @param key 账户编码
     * @return 账户信息实体
     */
    public TAccount findByAccountKey(String key);

    /**
     * 根据账户名称查询账户信息实体
     * 
     * @param accountName 账户名称
     * @return 账户信息实体
     */
    public TAccount findByAccountName(String accountName);
}

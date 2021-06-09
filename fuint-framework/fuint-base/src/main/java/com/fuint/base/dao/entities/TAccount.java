/**
 * cyw.com Inc.
 * Copyright (c) 2014-2015 All Rights Reserved.
 */
package com.fuint.base.dao.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import com.fuint.util.StringUtil;
import org.springframework.util.CollectionUtils;


/**
 * 账户信息实体类
 *
 * @author fsq
 * @version $Id: TAccount.java, v 0.1 2015年10月26日 上午9:51:52 fsq Exp $
 */
@Entity
@Table(name = "t_account")
@NamedQuery(name = "TAccount.findAll", query = "SELECT c FROM TAccount c")
public class TAccount implements Serializable {
    /**  */
    private static final long serialVersionUID = -6612153566082678820L;
    /**
     * 账户主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false, name = "acct_id")
    private Long id;
    /**
     * 账户编码
     */
    @Column(name = "account_key", nullable = false, length = 23)
    private String accountKey;
    /**
     * 账户名称
     */
    @Column(name = "account_name", nullable = false, length = 20)
    private String accountName;
    /**
     * 密码
     */
    @Column(name = "password", nullable = false, length = 100)
    private String password;
    /**
     * 账户状态
     * 状态 : 0 无效 1 有效
     */
    @Column(name = "account_status", nullable = false)
    private int accountStatus;
    /**
     * 是否激活
     * 状态 : 0 未激活 1已激活
     */
    @Column(name = "is_active", nullable = false)
    private int isActive;

    /**
     * 创建时间
     */
    @Column(name = "create_date", nullable = false)
    private Date createDate;
    /**
     * 修改时间
     */
    @Column(name = "modify_date", nullable = false)
    private Date modifyDate;

    /**
     * 随机码（公盐）
     */
    @Column(name = "salt", nullable = false)
    private String salt;

    /**
     * shiro认证密码 不持久化到数据库，也不作为显示
     */
    @Transient
    private String plainPassword;

    /**
     * 是否被锁定
     */
    @Column(name = "locked",nullable = true)
    private int locked;

    @OneToOne
    @JoinColumn(name = "owner_id", nullable = true)
    private TPlatform tPlatform;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tAccount", fetch = FetchType.LAZY)
    //@JoinColumn(name = "acct_id")
    private Set<TAccountDuty> htAccountDuties;

       /**
     * 管理员真实姓名
     */
    @Column(name = "real_name",nullable = true)
    private String realName;

    /**
     * 管辖酒店id
     */
    @Column(name = "store_id")
    private int storeId;

    /**
     * 管辖酒店名称
     */
    @Column(name = "store_name")
    private String storeName;

    public Set<TAccountDuty> getHtAccountDuties() {
        return htAccountDuties;
    }

    public void setHtAccountDuties(Set<TAccountDuty> htAccountDuties) {
        this.htAccountDuties = htAccountDuties;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountKey() {
        return accountKey;
    }

    public void setAccountKey(String accountKey) {
        this.accountKey = accountKey;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(int accountStatus) {
        this.accountStatus = accountStatus;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }


    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }

    public int getLocked() {
        return locked;
    }

    public void setLocked(int locked) {
        this.locked = locked;
    }

    public TPlatform gettPlatform() {
        return tPlatform;
    }

    public void settPlatform(TPlatform tPlatform) {
        this.tPlatform = tPlatform;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}

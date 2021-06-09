package com.fuint.base.shiro;

import com.fuint.base.dao.entities.*;
import java.io.Serializable;
import java.util.*;

/**
 * 自定义Authentication对象，使得Subject除了携带用户的登录名外还可以携带更多信息.
 * <p/>
 * Created by hanxiaoqiang on 16/7/7.
 */
public class ShiroUser implements Serializable {

    /**
     * UUID
     */
    private static final long serialVersionUID = -1373760761780840081L;
    /**
     * ID
     */
    private Long id;
    /**
     * 账户编码
     */
    private String acctKey;
    /**
     * 用户账户
     */
    private String acctName;
    /**
     * 用户全称
     */
    private String userName;
    /**
     * 客户端端口
     */
    private Integer clientPort;
    /**
     * 请上一次求方式 get  post
     */
    private String method;
    /**
     * 客户端操作系统以及浏览器信息
     */
    private String userAgent;
    /**
     * 客户端IP
     */
    private String clientIp;
    /**
     * 请求路径
     */
    private String requestURL;


    /**
     * 用户所拥有的角色
     */
    private List<TDuty> duties;

    /**
     * 用户所拥有访问权限的菜单集合 sourceCode:Tsouce
     */
    private List<TSource> sources;
    /**
     * 用户所属平台
     */
    private TPlatform tPlatform;


    public ShiroUser(Long id, String acctName) {
        this.id = id;
        this.acctName = acctName;
    }

    public ShiroUser(TAccount tAccount) {
        this.id = tAccount.getId();
        this.acctName = tAccount.getAccountName();
        this.acctKey = tAccount.getAccountKey();
    }

    public Long getId() {
        return id;
    }

    public String getAcctName() {
        return acctName;
    }

    public void setAcctName(String acctName) {
        this.acctName = acctName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getClientPort() {
        return clientPort;
    }

    public void setClientPort(Integer clientPort) {
        this.clientPort = clientPort;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }


    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }


    /**
     * 本函数输出将作为默认的<shiro:principal/>输出.
     */
    @Override
    public String toString() {
        return acctName;
    }

    /**
     * 重载hashCode,只计算acctName;
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(acctName);
    }

    /**
     * 重载equals,只计算acctName;
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ShiroUser other = (ShiroUser) obj;
        if (acctName == null) {
            if (other.acctName != null) {
                return false;
            }
        } else if (!acctName.equals(other.acctName)) {
            return false;
        }
        return true;
    }

    public TPlatform gettPlatform() {
        return tPlatform;
    }

    public void settPlatform(TPlatform tPlatform) {
        this.tPlatform = tPlatform;
    }

    public String getAcctKey() {
        return acctKey;
    }

    public void setAcctKey(String acctKey) {
        this.acctKey = acctKey;
    }


    public List<TDuty> getDuties() {
        return duties;
    }

    public void setDuties(List<TDuty> duties) {
        this.duties = duties;
    }


    public List<TSource> getSources() {
        return sources;
    }

    public void setSources(List<TSource> sources) {
        this.sources = sources;
    }
}

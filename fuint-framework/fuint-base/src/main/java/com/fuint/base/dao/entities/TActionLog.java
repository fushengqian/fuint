package com.fuint.base.dao.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * 操作日志实体类
 *
 * @author fsq
 * @version $Id: TActionLog.java, v 0.1 2015年11月24日 下午4:43:40 fsq Exp $
 */
@Entity
@Table(name = "t_action_log")
@NamedQuery(name = "TActionLog.findAll", query = "SELECT c FROM TActionLog c")
public class TActionLog implements java.io.Serializable {

    /**
     * UUID
     **/
    private static final long serialVersionUID = -4825794629554867760L;
    /**
     * 账户主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    private Long id;

    /**
     * 方法调用时间
     */
    @Column(name = "action_time")
    private Date actionTime;

    /**
     * 方法耗时
     */
    @Column(name = "time_consuming")
    private Long timeConsuming;

    /**
     * 客户端IP
     */
    @Column(name = "client_ip", length = 50)
    private String clientIp;

    /**
     * 端口
     */
    @Column(name = "client_port")
    private Integer clientPort;

    /**
     * 模块
     */
    @Column(name = "module", length = 255)
    private String module;

    /**
     * url
     */
    @Column(name = "url", length = 255)
    private String url;

    /**
     * 操作用户账户
     */
    @Column(name = "acct_name", length = 255)
    private String acctName;
    /**
     * 用户系统以及浏览器信息
     */
    @Column(name = "user_agent", length = 255)
    private String userAgent;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getActionTime() {
        return actionTime;
    }

    public void setActionTime(Date actionTime) {
        this.actionTime = actionTime;
    }

    public Long getTimeConsuming() {
        return timeConsuming;
    }

    public void setTimeConsuming(Long timeConsuming) {
        this.timeConsuming = timeConsuming;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public Integer getClientPort() {
        return clientPort;
    }

    public void setClientPort(Integer clientPort) {
        this.clientPort = clientPort;
    }

    public String getAcctName() {
        return acctName;
    }

    public void setAcctName(String acctName) {
        this.acctName = acctName;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }


    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}

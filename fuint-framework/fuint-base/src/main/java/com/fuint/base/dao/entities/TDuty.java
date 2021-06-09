package com.fuint.base.dao.entities;

import com.fuint.util.StringUtil;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

/**
 * 角色实体类
 *
 * @author harrisonhan
 * @version $Id: TDuty.java, v 0.1 2015年11月19日 下午5:28:44 harrisonhan Exp $
 */
@Entity
@Table(name = "t_duty")
@NamedQuery(name = "TDuty.findAll", query = "SELECT c FROM TDuty c")
public class TDuty implements java.io.Serializable {

    /**
     * UUID
     */
    private static final long serialVersionUID = -8474025844522178714L;
    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false, name = "duty_id")
    private Long id;
    /**
     * 状态 A  D
     */
    @Column
    private String status;

    /**
     * 角色名称
     */
    @Column(name = "duty_name")
    private String name;

    /**
     * 角色描述
     */
    @Column(name = "description")
    private String describe;

    /**
     * 角色类型
     */
    @Column(name = "duty_type")
    private String dutyType;

    /**
     * 关联权限中间表
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tDuty", fetch = FetchType.LAZY)
    //@JoinColumn(name = "duty_id")
    private Set<TDutySource> tDutySources;

    /**
     * 关联用户中间表
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tDuty", fetch = FetchType.LAZY)
    //@JoinColumn(name = "duty_id")
    private Set<TAccountDuty> accountDuties;

    public TDuty() {

    }

    @Transient
    public List<String> getPermissionsName(){
        List<String> list=new ArrayList<String>();
        Set<TDutySource> sources=gettDutySources();

        for (TDutySource source : sources) {
            if(StringUtil.isNotBlank(source.gettSource().getSourceCode())
                    && !StringUtil.equals(source.gettSource().getSourceCode(),"#")){
                list.add(source.gettSource().getSourceCode());
            }
        }
        return list;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescribe() {
        return this.describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public Set<TDutySource> gettDutySources() {
        return tDutySources;
    }

    public void settDutySources(Set<TDutySource> tDutySources) {
        this.tDutySources = tDutySources;
    }

    public Set<TAccountDuty> getAccountDuties() {
        return this.accountDuties;
    }

    public void setAccountDuties(Set<TAccountDuty> fwAccountDuties) {
        this.accountDuties = fwAccountDuties;
    }

    public String getDutyType() {
        return dutyType;
    }

    public void setDutyType(String dutyType) {
        this.dutyType = dutyType;
    }

}

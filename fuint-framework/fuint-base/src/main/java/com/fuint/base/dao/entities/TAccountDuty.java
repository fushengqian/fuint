package com.fuint.base.dao.entities;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

/**
 * 用户角色中间表实体类
 *
 * @author harrisonhan
 * @version $Id: TAccountDuty.java, v 0.1 2015年11月19日 下午5:30:10 harrisonhan Exp $
 */
@Entity
@Table(name = "t_account_duty")
@NamedQuery(name = "TAccountDuty.findAll", query = "SELECT c FROM TAccountDuty c")
public class TAccountDuty implements java.io.Serializable {

    /**
     * UUID
     */
    private static final long serialVersionUID = 56432278631030039L;

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false, name = "acc_duty_id")
    private Long id;
    /**
     * 关联角色
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "duty_id")
    private TDuty tDuty;

    /**
     * 关联用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acct_id")
    private TAccount tAccount;

    public TAccountDuty() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TDuty gettDuty() {
        return tDuty;
    }

    public void settDuty(TDuty tDuty) {
        this.tDuty = tDuty;
    }


    public TAccount gettAccount() {
        return tAccount;
    }

    public void settAccount(TAccount tAccount) {
        this.tAccount = tAccount;
    }
}

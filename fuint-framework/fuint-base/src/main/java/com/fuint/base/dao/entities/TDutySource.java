package com.fuint.base.dao.entities;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

/**
 * 角色权限中间表实体类
 * 
 * @author harrisonhan
 * @version $Id: TDutySource.java, v 0.1 2015年11月19日 下午5:31:00 harrisonhan Exp $
 */
@Entity
@Table(name = "t_duty_source")
@NamedQuery(name = "TDutySource.findAll", query = "SELECT c FROM TDutySource c")
public class TDutySource implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false, name = "duty_source_id")
    private Long        id;
    /**
     * 关联权限
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id")
    private TSource tSource;

    /**
     * 关联角色
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "duty_id")
    private TDuty tDuty;

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

    public TSource gettSource() {
        return tSource;
    }

    public void settSource(TSource tSource) {
        this.tSource = tSource;
    }
}

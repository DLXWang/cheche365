package com.cheche365.cheche.core.repository;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by chenxiaozhe on 15-7-24.
 * 注解都加载getter上，子类也都把注解加在getter上，否则同时存在getter上和属性上会有jpa初始化问题
 */
@MappedSuperclass
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = -899634793952874659L;
    private Long id;
    private Date createTime;
    private Date updateTime;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @PrePersist
    public void onCreate() {
        this.setCreateTime((new Date()));
    }

    @PreUpdate
    public void onUpdate() {
        this.setUpdateTime(new Date());
    }

    @Column(columnDefinition = "DATETIME")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getUpdateTime() {
        return updateTime;
    }

    public BaseEntity setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }
}

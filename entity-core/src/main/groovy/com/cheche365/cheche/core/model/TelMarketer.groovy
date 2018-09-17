package com.cheche365.cheche.core.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * Created by chenxiangyin on 2017/9/26.
 */
@Entity
class TelMarketer implements Serializable{
    private static final long serialVersionUID = 1L

    private Long id
    private Long  user
    private String bindTel//街道地址
    private String cno//街道地址
    private Date createTime
    private Date updateTime
    private Long  operator

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }


    @Column(columnDefinition = "VARCHAR(50)")
    String getBindTel() {
        return bindTel
    }

    void setBindTel(String bindTel) {
        this.bindTel = bindTel
    }

    @Column(columnDefinition = "VARCHAR(50)")
    String getCno() {
        return cno
    }

    void setCno(String cno) {
        this.cno = cno
    }

    @Column(columnDefinition = "DATETIME")
    Date getCreateTime() {
        return createTime
    }

    void setCreateTime(Date createTime) {
        this.createTime = createTime
    }

    @Column(columnDefinition = "DATETIME")
    Date getUpdateTime() {
        return updateTime
    }

    void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime
    }

    @Column(columnDefinition = "BIGINT(20)")
    Long getUser() {
        return user
    }

    void setUser(Long user) {
        this.user = user
    }

    @Column(columnDefinition = "BIGINT(20)")
    Long getOperator() {
        return operator
    }

    void setOperator(Long operator) {
        this.operator = operator
    }
}

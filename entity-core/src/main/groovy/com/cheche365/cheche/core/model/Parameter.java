package com.cheche365.cheche.core.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * 参数表
 * Created by sunhuazhong on 2015/2/10.
 */
@Entity
public class Parameter {
    private Long id;
    private String paramName;
    private String paramValue;
    private AccessLog accessLog;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    @Column(columnDefinition = "TEXT")
    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    @ManyToOne
    @JoinColumn(name="accessLog", foreignKey=@ForeignKey(name="FK_PARAMETER_REF_ACCESS_LOG", foreignKeyDefinition="FOREIGN KEY (access_log) REFERENCES access_log(id)"))
    @JsonManagedReference
    public AccessLog getAccessLog() {
        return accessLog;
    }

    public void setAccessLog(AccessLog accessLog) {
        this.accessLog = accessLog;
    }
}

package com.cheche365.cheche.core.model;

import javax.persistence.*;

/**
 * Created by sunhuazhong on 2015/10/15.
 */

@Entity
public class MessageVariable {

    private Long id;
    private String code;
    private String name;
    private String type;
    private String placeholder;
    private Integer length;
    private String parameter;//该变量使用到的参数
    private String modelClass;//获取值关联的Model类
    private String modelMethod;//获取值关联的Model类方法

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    @Column(columnDefinition = "SMALLINT(4)")
    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getModelClass() {
        return modelClass;
    }

    public void setModelClass(String modelClass) {
        this.modelClass = modelClass;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getModelMethod() {
        return modelMethod;
    }

    public void setModelMethod(String modelMethod) {
        this.modelMethod = modelMethod;
    }
}

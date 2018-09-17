package com.cheche365.cheche.core.model;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Created by zhengwei on 3/13/17. <br/>
 * 注意：这种Entity父类只能写成java，groovy会报错 <br/>
 * 根据命名规则初始化Enum常量，命名规则：<br/>
 * 以 _ID结尾，其中ID是数据库中纪录的ID,如：<br/>
 * NOT_USE_1, 表示数据库中ID为1纪录对应的常量 <br/>
 */

@MappedSuperclass
public abstract class AutoLoadEnum implements Serializable {

    private static final long serialVersionUID = -616932245671650906L;
    private Long id;
    private String name;
    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public AutoLoadEnum setId(Long id) {
        this.id = id;
        return this;
    }

    @Column
    public String getName() {
        return name;
    }

    public AutoLoadEnum setName(String name) {
        this.name = name;
        return this;
    }

    @Column
    public String getDescription() {
        return description;
    }

    public AutoLoadEnum setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return o!=null &&
            getClass().equals(o.getClass()) &&
            new EqualsBuilder().append(getId(), ((AutoLoadEnum)o).getId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getId()).toHashCode();
    }

    public AutoLoadEnum deepClone(){
        return SerializationUtils.clone(this);
    }
}

package com.cheche365.cheche.core.model.abao;

import javax.persistence.*;

@Entity
@Table(name = "INSURANCE_FIELD_TYPE")
public class InsuranceFieldType {

    private Long id; // 主键
    private String type; // 类型
    private String description; // 描述

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(columnDefinition = "VARCHAR(2000)")
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

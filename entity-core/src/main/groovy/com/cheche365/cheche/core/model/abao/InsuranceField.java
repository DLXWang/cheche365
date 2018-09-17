package com.cheche365.cheche.core.model.abao;

import javax.persistence.*;

@Entity
@Table(name = "INSURANCE_FIELD")
public class InsuranceField {

    private Long id; // 主键
    private String code; // 险种代码
    private String name; // 险种名称
    private String shortName; // 险种简称
    private InsuranceFieldType fieldType; // 险种字段类型
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
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @ManyToOne
    @JoinColumn(name = "FIELD_TYPE", foreignKey = @ForeignKey(name = "FK_INSURANCE_FIELD_REF_FIELD_TYPE", foreignKeyDefinition = "FOREIGN KEY (`field_type`) REFERENCES `insurance_field_type` (`id`)"))
    public InsuranceFieldType getFieldType() {
        return this.fieldType;
    }

    public void setFieldType(InsuranceFieldType fieldType) {
        this.fieldType = fieldType;
    }

    @Column(columnDefinition = "VARCHAR(2000)")
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

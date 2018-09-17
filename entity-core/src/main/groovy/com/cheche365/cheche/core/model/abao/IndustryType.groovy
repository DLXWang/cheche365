package com.cheche365.cheche.core.model.abao

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

import javax.persistence.*

@Entity
@JsonIgnoreProperties(["parent"])
@Table(name = "INDUSTRY_TYPE")
public class IndustryType {

    private Long id; // 主键
    private String name; // 行业分类名称
    private String description;// 行业分类描述
    private IndustryType parent;// 上级行业分类

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(columnDefinition = "VARCHAR(2000)")
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne
    public IndustryType getParent() {
        return parent;
    }

    public void setParent(IndustryType parent) {
        this.parent = parent;
    }
}

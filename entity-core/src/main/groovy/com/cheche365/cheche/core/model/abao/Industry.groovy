package com.cheche365.cheche.core.model.abao

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

import javax.persistence.*

@Entity
@JsonIgnoreProperties(["industryType"])
@Table(name = "INDUSTRY")
public class Industry {

    private Long id; // 主键
    private IndustryType industryType; // 行业二级分类id
    private String code; // 职业代码
    private String name; // 职业名称
    private Integer type; // 职业分类，99为拒保

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "INDUSTRY_TYPE", foreignKey = @ForeignKey(name = "FK_INDUSTRY_REF_INDUSTRY_TYPE", foreignKeyDefinition = "FOREIGN KEY (`industry_type`) REFERENCES `industry_type` (`id`)"))
    public IndustryType getIndustryType() {
        return this.industryType;
    }

    public void setIndustryType(IndustryType industryType) {
        this.industryType = industryType;
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

    @Column(columnDefinition = "TINYINT(3)")
    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}

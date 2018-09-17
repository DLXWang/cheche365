package com.cheche365.cheche.core.model.abao;

import com.cheche365.cheche.core.model.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "INSURANCE_PERSON")
@JsonIgnoreProperties(ignoreUnknown = true)
public class InsurancePerson {

    private Long id; // 主键
    private String name; // 姓名
    private IdentityType identityType; // 证件类型
    private String identity; // 证件号码
    private Date birthday; // 生日
    private String mobile; // 手机号
    private String email; // 邮箱
    private Industry industry; // 行业id
    private Boolean socialSecurity;// 有无社保
    private Gender gender;//性别
    private User user; // 创建人id
    private Relationship relationship;// 与创建人关系
    private Date createTime; // 创建时间
    private Date updateTime; // 更新时间

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne
    @JoinColumn(name = "IDENTITY_TYPE", foreignKey = @ForeignKey(name = "FK_INSURANCE_PERSON_REF_IDENTITY_TYPE", foreignKeyDefinition = "FOREIGN KEY (`identity_type`) REFERENCES `identity_type` (`id`)"))
    public IdentityType getIdentityType() {
        return this.identityType;
    }

    public void setIdentityType(IdentityType identityType) {
        this.identityType = identityType;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getIdentity() {
        return this.identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @ManyToOne
    @JoinColumn(name = "USER", foreignKey = @ForeignKey(name = "FK_INSURANCE_PERSON_REF_USER", foreignKeyDefinition = "FOREIGN KEY (`user`) REFERENCES `user` (`id`)"))
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne
    @JoinColumn(name = "INDUSTRY", foreignKey = @ForeignKey(name = "FK_INSURANCE_PERSON_REF_INDUSTRY", foreignKeyDefinition = "FOREIGN KEY (`industry`) REFERENCES `industry` (`id`)"))
    public Industry getIndustry() {
        return this.industry;
    }

    public void setIndustry(Industry industry) {
        this.industry = industry;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Column(columnDefinition = "DATE")
    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Boolean getSocialSecurity() {
        return socialSecurity;
    }

    public void setSocialSecurity(Boolean socialSecurity) {
        this.socialSecurity = socialSecurity;
    }

    @ManyToOne
    @JoinColumn(name = "gender", foreignKey = @ForeignKey(name = "FK_INSURANCE_PERSON_REF_GENDER", foreignKeyDefinition = "FOREIGN KEY (`gender`) REFERENCES `gender` (`id`)"))
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @ManyToOne
    @JoinColumn(name = "relationship", foreignKey = @ForeignKey(name = "FK_INSURANCE_PERSON_REF_RELATIONSHIP", foreignKeyDefinition = "FOREIGN KEY (`relationship`) REFERENCES `relationship` (`id`)"))
    public Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }
}

package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by sunhuazhong on 2015/11/13.
 */
@Entity
public class InstitutionTemp {
    private Long id;
    private String name;//出单机构名称
    private String comment;//备注
    private String contactName;//机构联系人姓名
    private String contactMobile;//机构联系人手机号
    private String contactEmail;//机构联系人邮箱
    private String contactQq;//机构联系人QQ
    private String checheName;//车车责任人姓名
    private String checheMobile;//车车责任人手机号
    private String checheEmail;//车车责任人邮箱
    private String checheQq;//车车责任人QQ
    private Boolean enable = true;//启用禁用，0-禁用，1-启用
    private Date createTime;//创建时间
    private Date updateTime;//修改时间
    private InternalUser operator;//操作人

    private List<InstitutionBankAccountTemp> bankAccountTempList;
    private List<InstitutionRebateTemp> rebateTempList;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "varchar(20)")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(columnDefinition = "varchar(200)")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Column(columnDefinition = "varchar(20)")
    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    @Column(columnDefinition = "varchar(20)")
    public String getContactMobile() {
        return contactMobile;
    }

    public void setContactMobile(String contactMobile) {
        this.contactMobile = contactMobile;
    }

    @Column(columnDefinition = "varchar(45)")
    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    @Column(columnDefinition = "varchar(20)")
    public String getContactQq() {
        return contactQq;
    }

    public void setContactQq(String contactQq) {
        this.contactQq = contactQq;
    }

    @Column(columnDefinition = "varchar(20)")
    public String getChecheName() {
        return checheName;
    }

    public void setChecheName(String checheName) {
        this.checheName = checheName;
    }

    @Column(columnDefinition = "varchar(20)")
    public String getChecheMobile() {
        return checheMobile;
    }

    public void setChecheMobile(String checheMobile) {
        this.checheMobile = checheMobile;
    }

    @Column(columnDefinition = "varchar(45)")
    public String getChecheEmail() {
        return checheEmail;
    }

    public void setChecheEmail(String checheEmail) {
        this.checheEmail = checheEmail;
    }

    @Column(columnDefinition = "varchar(20)")
    public String getChecheQq() {
        return checheQq;
    }

    public void setChecheQq(String checheQq) {
        this.checheQq = checheQq;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    @Column(columnDefinition = "datetime")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(columnDefinition = "datetime")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey = @ForeignKey(name = "FK_INSTITUTION_TEMP_REF_OPERATOR", foreignKeyDefinition = "FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    @Transient
    public List<InstitutionBankAccountTemp> getbankAccountTempList() {
        return bankAccountTempList;
    }

    public void setbankAccountTempList(List<InstitutionBankAccountTemp> bankAccountTempList) {
        this.bankAccountTempList = bankAccountTempList;
    }

    @Transient
    public List<InstitutionRebateTemp> getRebateListTemp() {
        return rebateTempList;
    }

    public void setRebateListTemp(List<InstitutionRebateTemp> rebateTempList) {
        this.rebateTempList = rebateTempList;
    }
}

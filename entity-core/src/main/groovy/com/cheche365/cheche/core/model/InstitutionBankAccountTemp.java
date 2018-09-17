package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.Date;

/**
 * 出单机构银行账户表
 * Created by sunhuazhong on 2015/11/13.
 */
@Entity
public class InstitutionBankAccountTemp {
    private Long id;
    private InstitutionTemp institutionTemp;//出单机构
    private String bank;//开户行
    private String accountName;//开户名
    private String accountNo;//帐号
    private Date createTime;//创建时间
    private Date updateTime;//修改时间
    private InternalUser operator;//操作人

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "institutionTemp", foreignKey = @ForeignKey(name = "FK_INSTITUTION_BANK_ACCOUNT_TEMP_REF_INSTITUTION_TEMP", foreignKeyDefinition = "FOREIGN KEY (institution_temp) REFERENCES institution_temp(id)"))
    public InstitutionTemp getInstitutionTemp() {
        return institutionTemp;
    }

    public void setInstitutionTemp(InstitutionTemp institutionTemp) {
        this.institutionTemp = institutionTemp;
    }

    @Column(columnDefinition = "varchar(45)")
    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    @Column(columnDefinition = "varchar(45)")
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Column(columnDefinition = "varchar(45)")
    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
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
    @JoinColumn(name = "operator", foreignKey = @ForeignKey(name = "FK_INSTITUTION_BANK_ACCOUNT_TEMP_REF_OPERATOR", foreignKeyDefinition = "FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }
}

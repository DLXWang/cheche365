package com.cheche365.cheche.core.model.tide

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.InternalUser

import javax.persistence.*

/**
 * 潮汐系统: 合约
 * Created by yinJianBin on 2018/4/19.
 */
@Entity
class TideContract extends TideBaseEntity {

    TideBranch tideBranch   //营业部
    TideInstitution tideInstitution//分支机构级别保险公司(出单机构)
    InsuranceCompany insuranceCompany   //保险公司
    String contractName //合约名称：
    Date effectiveDate  //开始日期
    Date expireDate //过期时间
    String loginUrl //登录地址(保险公司承保系统登录使用)
    String partnerUserName  //用户名(保险公司承保系统登录使用)
    String partnerPassword  //密码(保险公司承保系统登录使用)
    String orderCode    //出单代码
    Integer status  //状态
    InternalUser operator //操作人
    Area orderArea  //出单所在城市
    String contractCode //合约编码
    Boolean disable = false // 禁用/启用


    @ManyToOne
    @JoinColumn(name = "tide_branch", foreignKey = @ForeignKey(name = "FK_TIDE_BRANCH_REF_TIDE_BRANCH_ID", foreignKeyDefinition = "FOREIGN KEY (tide_branch) REFERENCES tide_branch(id)"))
    TideBranch getTideBranch() {
        return tideBranch
    }

    @ManyToOne
    @JoinColumn(name = "tide_institution", foreignKey = @ForeignKey(name = "FK_tide_institution_REF_tide_institution_ID", foreignKeyDefinition = "FOREIGN KEY (tide_institution) REFERENCES tide_institution(id)"))
    TideInstitution getTideInstitution() {
        return tideInstitution
    }

    @ManyToOne
    @JoinColumn(name = "insurance_company", foreignKey = @ForeignKey(name = "FK_INSURANCE_COMPANY_REF_INSURANCE_COMPANY_ID", foreignKeyDefinition = "FOREIGN KEY (insurance_company) REFERENCES insurance_company(id)"))
    InsuranceCompany getInsuranceCompany() {
        return insuranceCompany
    }

    @Column(columnDefinition = "varchar(60)")
    String getContractName() {
        return contractName
    }

    @Column(columnDefinition = "datetime")
    Date getEffectiveDate() {
        return effectiveDate
    }

    @Column(columnDefinition = "datetime")
    Date getExpireDate() {
        return expireDate
    }

    @Column(columnDefinition = "varchar(200)")
    String getLoginUrl() {
        return loginUrl
    }

    @Column(columnDefinition = "varchar(60)")
    String getPartnerUserName() {
        return partnerUserName
    }

    @Column(columnDefinition = "varchar(60)")
    String getPartnerPassword() {
        return partnerPassword
    }

    @Column(columnDefinition = "varchar(60)")
    String getOrderCode() {
        return orderCode
    }

    @Column(columnDefinition = "TINYINT(1)")
    Integer getStatus() {
        return status
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey = @ForeignKey(name = "TIDE_CONTRACT_OPERATOR", foreignKeyDefinition = "FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    InternalUser getOperator() {
        return operator
    }

    @ManyToOne
    @JoinColumn(name = "order_area", foreignKey = @ForeignKey(name = "FK_ORDER_AREA_REF_AREA_ID", foreignKeyDefinition = "FOREIGN KEY (order_area) REFERENCES area(id)"))
    Area getOrderArea() {
        return orderArea
    }

    @Column(columnDefinition = "varchar(40)")
    String getContractCode() {
        return contractCode
    }

    @Column(columnDefinition = 'tinyint(1)')
    Boolean getDisable() {
        return disable
    }
}

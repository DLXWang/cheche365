package com.cheche365.cheche.core.model.tide

import com.cheche365.cheche.core.model.InternalUser

import javax.persistence.*

/**
 * 潮汐系统: 合约历史
 * Created by yinJianBin on 2018/5/4.
 */
@Entity
class TideContractHistory extends TideBaseEntity {

    TideContract tideContract // 合约
    String contractName //合约名称
    Date effectiveDate  //开始日期
    Date expireDate //过期时间
    String loginUrl //登录地址(保险公司承保系统登录使用)
    String partnerUserName  //用户名(保险公司承保系统登录使用)
    String partnerPassword  //密码(保险公司承保系统登录使用)
    String orderCode    //出单代码
    Integer status  //状态
    InternalUser operator //操作人
    Integer operationType //操作类型 修改 续约

    static TideContractHistory copyFromContract(TideContract contract) {
        TideContractHistory history = new TideContractHistory(
                tideContract: contract,
                contractName: contract.contractName,
                effectiveDate: contract.effectiveDate,
                expireDate: contract.expireDate,
                loginUrl: contract.loginUrl,
                partnerUserName: contract.partnerUserName,
                partnerPassword: contract.partnerPassword,
                orderCode: contract.orderCode
        )
        return history
    }


    @ManyToOne
    @JoinColumn(name = "tide_contract", foreignKey = @ForeignKey(name = "TIDE_CONTRACT_HISTORY_REF_CONTRACT", foreignKeyDefinition = "FOREIGN KEY (tide_contract) REFERENCES tide_contract(id)"))
    TideContract getTideContract() {
        return tideContract
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
    @JoinColumn(name = "operator", foreignKey = @ForeignKey(name = "TIDE_CONTRACT_HISTORY_OPERATOR", foreignKeyDefinition = "FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    InternalUser getOperator() {
        return operator
    }

    @Column(columnDefinition = "TINYINT(1)")
    Integer getOperationType() {
        return operationType
    }

}

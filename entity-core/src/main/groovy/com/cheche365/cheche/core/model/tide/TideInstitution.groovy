package com.cheche365.cheche.core.model.tide

import com.cheche365.cheche.core.model.InternalUser

import javax.persistence.*

/**
 * 潮汐系统: 合约
 * Created by yinJianBin on 2018/4/19.
 */
@Entity
class TideInstitution extends TideBaseEntity {

    TideBranch tideBranch   //营业部
    String institutionName //合约名称：
    Integer status  //状态
    InternalUser operator //操作人


    @ManyToOne
    @JoinColumn(name = "tide_branch", foreignKey = @ForeignKey(name = "FK_TIDE_INSTUTITION_REF_TIDE_BRANCH_ID", foreignKeyDefinition = "FOREIGN KEY (tide_branch) REFERENCES tide_branch(id)"))
    TideBranch getTideBranch() {
        return tideBranch
    }

    @Column(columnDefinition = "varchar(60)")
    String getInstitutionName() {
        return institutionName
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
}

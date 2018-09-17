package com.cheche365.cheche.core.model.tide

import com.cheche365.cheche.core.model.InternalUser

import javax.persistence.*

/**
 * 潮汐系统: 营业部(牌照)
 * Created by yinJianBin on 2018/4/19.
 */
@Entity
class TideBranch extends TideBaseEntity {

    TidePlatform tidePlatform   //平台机构
    String platformName    //平台机构名称
    String branchName   //营业部名称
    String branchType   //机构类型
    String branchNo //机构编码
    Date sourceCreateTime   //成立时间
    Integer status  //状态
    InternalUser operator //操作人


    @ManyToOne
    @JoinColumn(name = "tide_platform", foreignKey = @ForeignKey(name = "TIDE_BRANCH_REF_TIDE_PLATEFORM", foreignKeyDefinition = "FOREIGN KEY (tide_platform) REFERENCES tide_platform(id)"))
    TidePlatform getTidePlatform() {
        return tidePlatform
    }


    @Column(columnDefinition = "varchar(60)")
    String getPlatformName() {
        return platformName
    }


    @Column(columnDefinition = "varchar(60)")
    String getBranchName() {
        return branchName
    }


    @Column(columnDefinition = "varchar(60)")
    String getBranchType() {
        return branchType
    }


    @Column(columnDefinition = "varchar(60)")
    String getBranchNo() {
        return branchNo
    }


    @Column(columnDefinition = "datetime")
    Date getSourceCreateTime() {
        return sourceCreateTime
    }


    @Column(columnDefinition = "tinyint(1)")
    Integer getStatus() {
        return status
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey = @ForeignKey(name = "TIDE_PLATEFORM_OPERATOR", foreignKeyDefinition = "FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    InternalUser getOperator() {
        return operator
    }
}

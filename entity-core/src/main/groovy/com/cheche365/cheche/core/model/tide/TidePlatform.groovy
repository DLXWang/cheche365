package com.cheche365.cheche.core.model.tide

import com.cheche365.cheche.core.model.InternalUser

import javax.persistence.*

/**
 * 潮汐系统: 平台机构
 * Created by yinJianBin on 2018/4/19.
 */
@Entity
class TidePlatform extends TideBaseEntity {

    String userName //操作员姓名
    String mobile   //操作员手机号
    String name    //平台机构名称
    Integer status  //状态
    InternalUser operator   //操作员关联的用户


    @Column(columnDefinition = "varchar(60)")
    String getUserName() {
        return userName
    }


    @Column(columnDefinition = "varchar(20)")
    String getMobile() {
        return mobile
    }


    @Column(columnDefinition = "varchar(60)")
    String getName() {
        return name
    }


    @Column(columnDefinition = "tinyint(1)")
    Integer getStatus() {
        return status
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey = @ForeignKey(name = "TIDE_PLATFORM_OPERATOR", foreignKeyDefinition = "FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    InternalUser getOperator() {
        return operator
    }
}

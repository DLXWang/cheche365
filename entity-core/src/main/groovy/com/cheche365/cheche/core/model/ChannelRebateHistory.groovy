package com.cheche365.cheche.core.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ForeignKey
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

/**
 * Created by yinJianBin on 2017/6/12.
 */
@Entity
class ChannelRebateHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @ManyToOne
    @JoinColumn(name = "channelRebate", foreignKey = @ForeignKey(name = "FK_CHANNEL_REBATE_HISTORY_REF_CHANNEL_REBATE", foreignKeyDefinition = "FOREIGN KEY(channelRebate)REFERENCES channel_rebate(id)"))
    ChannelRebate channelRebate

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double onlyCommercialRebate

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double onlyCompulsoryRebate

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double commercialRebate

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double compulsoryRebate

    @Column(columnDefinition = "DATETIME")
    Date effectiveDate

    @Column(columnDefinition = "DATETIME")
    Date expireDate

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey = @ForeignKey(name = "operator", foreignKeyDefinition = "FOREIGN KEY(operator) REFERENCES internal_user(id)"))
    InternalUser operator

    @Column(columnDefinition = "DATETIME")
    Date createTime

}

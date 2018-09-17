package com.cheche365.cheche.core.model.agent

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.InsuranceCompany
import groovy.transform.Canonical

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
@Canonical(excludes = ['id'])
class ChannelAgentRebateHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @ManyToOne
    Area area

    @ManyToOne
    InsuranceCompany insuranceCompany

    @ManyToOne
    ChannelAgent channelAgent

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double commercialRebate

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double compulsoryRebate

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double userCommercialRebate

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double userCompulsoryRebate

    @Column(columnDefinition = "DATETIME")
    Date createTime
}

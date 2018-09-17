package com.cheche365.cheche.core.model.agent

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.InsuranceCompany
import groovy.transform.Canonical

import javax.persistence.*

@Entity
@Canonical(excludes = ['id'])
class ChannelAgentRebate {

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
    Double parentDetainCommercialRebate

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double parentDetainCompulsoryRebate

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double userCommercialRebate

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double userCompulsoryRebate

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double onlyCommercialRebate

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double onlyCompulsoryRebate
}

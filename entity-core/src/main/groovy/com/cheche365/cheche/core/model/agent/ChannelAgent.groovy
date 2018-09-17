package com.cheche365.cheche.core.model.agent

import com.cheche365.cheche.core.constants.RebateCalculateConstants
import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.ChannelRebate
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.service.IResourceService
import com.cheche365.cheche.core.service.listener.EntityChangeListener
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.Canonical
import org.apache.commons.lang3.StringUtils

import javax.persistence.*
import static com.cheche365.cheche.core.constants.RebateCalculateConstants.CalculateType

@Entity
@EntityListeners(EntityChangeListener.class)
@JsonIgnoreProperties(ignoreUnknown = true, value = ["parent", "createTime", "updateTime", "previous"], allowSetters = true)
@Canonical(excludes = ['id'])
class ChannelAgent implements Serializable {
    private static final long serialVersionUID = 1L

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @ManyToOne
    User user

    @ManyToOne
    ChannelAgent parent

    @ManyToOne
    Channel channel

    @ManyToOne
    AgentLevel agentLevel

    @Column(columnDefinition = "VARCHAR(100)")
    String agentCode

    @Column(columnDefinition = "VARCHAR(100)")
    String inviteQrCode

    @Column(columnDefinition = "VARCHAR(8)")
    String inviteCode

    @Column(columnDefinition = "DATETIME")
    Date createTime

    @Column(columnDefinition = "DATETIME")
    Date updateTime

    @ManyToOne
    ShopType shopType // 代理人所在渠道，1:个人、2:洗车店、3:维修中心

    @Column(columnDefinition = "VARCHAR(100)")
    String shop //门店名称，当用户选择渠道为“个人”时，此字段隐藏，无需填写，当用户选择洗车店或汽车维修中心时，此为必填字段

    @ManyToOne
    Ethnic ethnic

    @Column(columnDefinition = "VARCHAR(100)")
    String areaName //所属城市

    @Column(columnDefinition = "VARCHAR(100)")
    String defaultArea //默认城市

    @Column(columnDefinition = "TINYINT(1)")
    Boolean disable //是否禁用 默认是0  不禁用

    @Transient
    String inviteQrUrl

    @Transient
    ChannelAgent previous

    @Transient
    Map shareInfo

    @PrePersist
    void generateAgentCode() {
        String agentCode = this.parent ? (this.parent.agentCode ? "${this.parent.agentCode}.${this.parent.id}" : "${this.parent.id}") : null
        this.setAgentCode(agentCode)
    }

    String shareLink() {
        if (this.inviteQrCode) {
            IResourceService resourceService = ApplicationContextHolder.applicationContext.getBean(IResourceService)
            resourceService.absoluteUrl(resourceService.getResourceUrl(resourceService.getResourceAbsolutePath(this.inviteQrCode))) + "?version=1"
        }
    }

    String absoluteAgentCode() {
        return this.parent != null ? this.agentCode + "." + id.toString() : this.agentCode
    }

    Double commercialRebate(ChannelRebate channelRebate, CalculateType calculateType) {
        def channelAgentInfoService = ApplicationContextHolder.applicationContext.getBean('channelAgentInfoService')
        channelAgentInfoService."${calculateType}"(channelRebate, this, RebateCalculateConstants.REBATE_CALCULATE.commercialRebate)
    }

    Double compulsoryRebate(ChannelRebate channelRebate, CalculateType calculateType) {
        def channelAgentInfoService = ApplicationContextHolder.applicationContext.getBean('channelAgentInfoService')
        channelAgentInfoService."${calculateType}"(channelRebate, this, RebateCalculateConstants.REBATE_CALCULATE.compulsoryRebate)
    }

    Double onlyCommercialRebate(ChannelRebate channelRebate, CalculateType calculateType) {
        def channelAgentInfoService = ApplicationContextHolder.applicationContext.getBean('channelAgentInfoService')
        channelAgentInfoService."${calculateType}"(channelRebate, this, RebateCalculateConstants.REBATE_CALCULATE.onlyCommercialRebate)
    }

    Double onlyCompulsoryRebate(ChannelRebate channelRebate, CalculateType calculateType) {
        def channelAgentInfoService = ApplicationContextHolder.applicationContext.getBean('channelAgentInfoService')
        channelAgentInfoService."${calculateType}"(channelRebate, this, RebateCalculateConstants.REBATE_CALCULATE.onlyCompulsoryRebate)
    }

    boolean needCompletionUser() {
        if (this.getEthnic() == null ||
            this.getShopType() == null ||
            StringUtils.isBlank(this.getUser().getIdentity()) ||
            StringUtils.isBlank(this.getUser().getName())) {
            return true
        }
        return false
    }
}

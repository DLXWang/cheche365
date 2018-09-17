package com.cheche365.cheche.operationcenter.web.model.channelRebate

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.model.ChannelRebate
import com.cheche365.cheche.core.model.InternalUser
import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.format.annotation.DateTimeFormat

/**
 * Created by yinJianBin on 2017/6/13.
 */
class ChannelRebateViewModel {
    def id
    def clientType
    def channelType
    def channelId
    def channelName
    def areaId
    def areaName
    def areaIds
    def insuranceCompanyId
    def insuranceCompanyName
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    Date effectiveDate
    String effectiveDateStr

    def onlyCommercialRebate
    def onlyCompulsoryRebate
    def commercialRebate
    def compulsoryRebate

    def status
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    Date readyEffectiveDate
    String readyEffectiveDateStr

    def onlyReadyCommercialRebate
    def onlyReadyCompulsoryRebate
    def readyCommercialRebate
    def readyCompulsoryRebate

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date createTime
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date updateTime
    def operator
    def description

    def provinceId
    def readyFlag
    def excelErr


    Integer currentPage
    Integer pageSize
    Integer draw

    void setEffectiveDateStr(String effectiveDateStr) {
        this.effectiveDateStr = effectiveDateStr
        this.effectiveDate = DateUtils.getDate(effectiveDateStr, DateUtils.DATE_LONGTIME24_PATTERN)
    }

    void setReadyEffectiveDateStr(String readyEffectiveDateStr) {
        this.readyEffectiveDateStr = readyEffectiveDateStr
        this.readyEffectiveDate = DateUtils.getDate(readyEffectiveDateStr, DateUtils.DATE_LONGTIME24_PATTERN)
    }

    static ChannelRebateViewModel buildViewData(ChannelRebate channelRebate, InternalUser operator) {
        ChannelRebateViewModel channelRebateViewModel = new ChannelRebateViewModel()
        channelRebateViewModel.setId(channelRebate.getId())
        channelRebateViewModel.setAreaId(channelRebate.getArea().getId())
        channelRebateViewModel.setAreaName(channelRebate.getArea().getName())
        channelRebateViewModel.setChannelId(channelRebate.getChannel().getId())
        channelRebateViewModel.setChannelName(channelRebate.getChannel().getDescription())
        channelRebateViewModel.setChannelType(channelRebate.getChannel().isThirdPartnerChannel() ? '第三方渠道' : '自有渠道')
        channelRebateViewModel.setClientType(channelRebate.getChannel().isAgentChannel() ? 'toA' : 'toC')

        channelRebateViewModel.setOnlyCommercialRebate(channelRebate.getOnlyCommercialRebate() != null ? channelRebate.getOnlyCommercialRebate() : '')
        channelRebateViewModel.setOnlyCompulsoryRebate(channelRebate.getOnlyCompulsoryRebate() != null ? channelRebate.getOnlyCompulsoryRebate() : '')
        channelRebateViewModel.setCommercialRebate(channelRebate.getCommercialRebate() != null ? channelRebate.getCommercialRebate() : '')
        channelRebateViewModel.setCompulsoryRebate(channelRebate.getCompulsoryRebate() != null ? channelRebate.getCompulsoryRebate() : '')
        channelRebateViewModel.setEffectiveDate(channelRebate.getEffectiveDate())

        channelRebateViewModel.setOnlyReadyCommercialRebate(channelRebate.getOnlyReadyCommercialRebate() != null ? channelRebate.getOnlyReadyCommercialRebate() : '')
        channelRebateViewModel.setOnlyReadyCompulsoryRebate(channelRebate.getOnlyReadyCompulsoryRebate() != null ? channelRebate.getOnlyReadyCompulsoryRebate() : '')
        channelRebateViewModel.setReadyCommercialRebate(channelRebate.getReadyCommercialRebate() != null ? channelRebate.getReadyCommercialRebate() : '')
        channelRebateViewModel.setReadyCompulsoryRebate(channelRebate.getReadyCompulsoryRebate() != null ? channelRebate.getReadyCompulsoryRebate() : '')
        channelRebateViewModel.setReadyEffectiveDate(channelRebate.getReadyEffectiveDate())

        channelRebateViewModel.setCreateTime(channelRebate.getCreateTime())
        channelRebateViewModel.setInsuranceCompanyId(channelRebate.getInsuranceCompany().getId())
        channelRebateViewModel.setInsuranceCompanyName(channelRebate.getInsuranceCompany().getName())
        channelRebateViewModel.setOperator(operator.getName())
        channelRebateViewModel.setStatus(ChannelRebate.Enum.STATUS_MAPPING.get(channelRebate.getStatus()))
        channelRebateViewModel.setDescription(channelRebate.description ?: "数据库为空,默认值!")

        channelRebateViewModel
    }

}

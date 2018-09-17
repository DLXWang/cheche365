package com.cheche365.cheche.operationcenter.web.model.channelRebate

import com.cheche365.cheche.core.model.ChannelRebateHistory
import com.cheche365.cheche.core.model.InternalUser

/**
 * Created by yinJianBin on 2017/6/16.
 */
class ChannelRebateHistoryViewData {
    def id
    def effectiveDate
    def expireDate
    def channelType
    def channelName
    def areaName
    def insuranceCompanyName
    def onlyCommercialRebate
    def onlyCompulsoryRebate
    def commercialRebate
    def compulsoryRebate
    def status
    Long channelRebateId

    def operator
    def createTime

    Integer currentPage
    Integer pageSize
    Integer draw


    static ChannelRebateHistoryViewData buildViewData(ChannelRebateHistory channelRebateHistory, InternalUser operator) {
        ChannelRebateHistoryViewData channelRebateHistoryViewData = new ChannelRebateHistoryViewData()
        channelRebateHistoryViewData.setId(channelRebateHistory.getId())
        channelRebateHistoryViewData.setStatus('已失效')
        channelRebateHistoryViewData.setAreaName(channelRebateHistory.getChannelRebate().getArea().getName())
        channelRebateHistoryViewData.setChannelName(channelRebateHistory.getChannelRebate().getChannel().getDescription())
        channelRebateHistoryViewData.setChannelType(channelRebateHistory.channelRebate.channel.isThirdPartnerChannel() ? '第三方渠道' : '自有渠道')
        channelRebateHistoryViewData.setOnlyCommercialRebate(channelRebateHistory.getOnlyCommercialRebate() != null ? channelRebateHistory.getOnlyCommercialRebate() : '')
        channelRebateHistoryViewData.setOnlyCompulsoryRebate(channelRebateHistory.getOnlyCompulsoryRebate() != null ? channelRebateHistory.getOnlyCompulsoryRebate() : '')
        channelRebateHistoryViewData.setCommercialRebate(channelRebateHistory.getCommercialRebate() != null ? channelRebateHistory.getCommercialRebate() : '')
        channelRebateHistoryViewData.setCompulsoryRebate(channelRebateHistory.getCompulsoryRebate() != null ? channelRebateHistory.getCompulsoryRebate() : '')
        channelRebateHistoryViewData.setEffectiveDate(channelRebateHistory.getEffectiveDate())
        channelRebateHistoryViewData.setExpireDate(channelRebateHistory.getExpireDate())
        channelRebateHistoryViewData.setInsuranceCompanyName(channelRebateHistory.getChannelRebate().getInsuranceCompany().getName())

        channelRebateHistoryViewData
    }

}

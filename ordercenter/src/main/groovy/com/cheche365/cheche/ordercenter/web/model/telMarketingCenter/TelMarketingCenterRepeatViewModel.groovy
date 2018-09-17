package com.cheche365.cheche.ordercenter.web.model.telMarketingCenter

import com.alibaba.druid.util.StringUtils
import com.cheche365.cheche.core.service.ResourceService
import com.cheche365.cheche.core.model.TelMarketingCenterRepeat
import com.cheche365.cheche.core.model.TelMarketingCenterSource
import com.fasterxml.jackson.annotation.JsonFormat

public class TelMarketingCenterRepeatViewModel {

    Long id//主键
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date createTime//创建时间
    String sourceName//来源
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date sourceCreateTime//来源创建时间
    TelMarketingCenterSource source//来源
    Long orderId
    String sourceTable
    Long sourceId
    Long channelId
    String channelIcon
    String channelName
    Long activeUrlId   //如果来源是活动的话，如果有广告来源，则记录该广告来源

    /**
     * telMarketingCenterRepeat实体类转展示类
     *
     * @param repeat
     * @return
     */
    static TelMarketingCenterRepeatViewModel createViewModel(TelMarketingCenterRepeat repeat, Long orderId, ResourceService resourceService) {
        if (repeat == null)
            return null
        TelMarketingCenterRepeatViewModel viewModel = new TelMarketingCenterRepeatViewModel()
        viewModel.setId(repeat.getId())
        viewModel.setCreateTime(repeat.getCreateTime())
        viewModel.setSourceCreateTime(repeat.getSourceCreateTime())
        viewModel.setSourceName(repeat.getSource().getDescription())
        viewModel.setSource(repeat.getSource())
        viewModel.setOrderId(orderId)
        viewModel.setSourceTable(repeat.getSourceTable())
        viewModel.setSourceId(repeat.getSourceId())
        if (repeat.getChannel() != null) {
            viewModel.setChannelId(repeat.getChannel().getId())
            if (!StringUtils.isEmpty(repeat.getChannel().getIcon())) {
                viewModel.setChannelIcon(resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(resourceService.getProperties().getChannelPath()), repeat.getChannel().getIcon()))
            }
            viewModel.setChannelName("$repeat.channel.description${repeat.activeUrlSource ? '-' + repeat.activeUrlSource : ''}")
        }
        viewModel.setActiveUrlId(repeat.getActiveUrlId())
        return viewModel
    }
}

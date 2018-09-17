package com.cheche365.cheche.web.model

import com.cheche365.cheche.core.model.ChannelRebate
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.agent.ChannelAgent

/**
 * Author:   shanxf
 * Date:     2018/5/17 10:44
 */
class ChannelAgentRebateInfo {

    ChannelAgent  channelAgent
    ChannelRebate channelRebate
    PurchaseOrder purchaseOrder
    QuoteRecord   quoteRecord
    Double        commercialRebate
    Double        childCommercialRebate
    Double        compulsoryRebate
    Double        childCompulsoryRebate
}

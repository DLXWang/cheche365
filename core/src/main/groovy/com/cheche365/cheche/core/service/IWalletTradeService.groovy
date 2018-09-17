package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord

/**
 * Created by yellow on 2017/9/7.
 */
interface IWalletTradeService {
    def createAgentWalletTrade(QuoteRecord quoteRecord, PurchaseOrder order)
}

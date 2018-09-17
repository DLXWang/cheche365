package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.QuoteRecord;

/**
 * Created by tongsong on 2017/3/28 0028.
 */
public interface ThirdPartyPaymentTemplate {

    boolean acceptable(QuoteRecord quoteRecord);

    Object prePay(PurchaseOrder purchaseOrder, Channel channel,QuoteRecord quoteRecord);

}

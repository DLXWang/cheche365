package com.cheche365.cheche.web.service.order.discount.strategy

import com.cheche365.cheche.core.model.QuoteRecord


/**
 * Created by zhengwei on 6/1/15.
 */
abstract class DiscountStrategy {


    public static final int NON_USER_INVOLVED = 1 << 1;  //无需用户参与的优惠类型，如直减
    public static final int USER_INVOLVED = 1 << 2;  //需要用户参与，如领红包

    def abstract applyDiscountStrategy(QuoteRecord quoteRecord, order, Long giftId)

    abstract boolean support(QuoteRecord quoteRecord, order, Long giftId)

    int belongsToGroup() {
        return USER_INVOLVED;
    }

}

package com.cheche365.cheche.rest.model;

import com.cheche365.cheche.core.model.Gift;
import com.cheche365.cheche.core.serializer.FormattedDoubleSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by mahong on 2016/4/6.
 */
public class QuoteDiscountResult {
    private Double paidAmount;
    private Gift gift;

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    public Double getPaidAmount() {
        return paidAmount;
    }

    public QuoteDiscountResult setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
        return this;
    }

    public Gift getGift() {
        return gift;
    }

    public QuoteDiscountResult setGift(Gift gift) {
        this.gift = gift;
        return this;
    }
}

package com.cheche365.cheche.ordercenter.model;

import com.cheche365.cheche.core.model.QuoteRecord;
import com.cheche365.cheche.core.model.QuoteSource;
import com.cheche365.cheche.core.serializer.converter.ArrayQuoteRecord;

/**
 * Created by wangfei on 2016/3/24.
 */
public class QuoteRecordViewExtend extends ArrayQuoteRecord {
    private QuoteSource type;//报价类型

    public QuoteSource getType() {
        return type;
    }

    public void setType(QuoteSource type) {
        this.type = type;
    }

    @Override
    public QuoteRecordViewExtend convert(QuoteRecord source) {
        type = source.getType();
        super.convert(source);
        return this;
    }
}

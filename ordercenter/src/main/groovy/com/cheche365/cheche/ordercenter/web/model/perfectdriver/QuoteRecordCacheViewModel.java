package com.cheche365.cheche.ordercenter.web.model.perfectdriver;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.QuoteRecord;
import com.cheche365.cheche.core.model.QuoteRecordCache;

/**
 * Created by xu.yelong on 2016-03-22.
 */
public class QuoteRecordCacheViewModel {
    private Long id;
    private Long perfectDriverId;// 好车主
    private Integer type;// 报价类型，1-电销报价，2-传统报价
    private QuoteRecord quoteRecord;//报价记录
    private Long insuranceCompanyId;//保险公司

    public QuoteRecordCacheViewModel(){}

    public QuoteRecordCacheViewModel(QuoteRecordCache quoteRecordCache){
        this.id=quoteRecordCache.getId();
        this.type=quoteRecordCache.getType();
        this.quoteRecord=quoteRecordCache.getQuoteRecord();
        this.insuranceCompanyId=quoteRecordCache.getInsuranceCompany().getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPerfectDriverId() {
        return perfectDriverId;
    }

    public void setPerfectDriverId(Long perfectDriverId) {
        this.perfectDriverId = perfectDriverId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public QuoteRecord getQuoteRecord() {
        return quoteRecord;
    }

    public void setQuoteRecord(QuoteRecord quoteRecord) {
        this.quoteRecord = quoteRecord;
    }

    public Long getInsuranceCompanyId() {
        return insuranceCompanyId;
    }

    public void setInsuranceCompanyId(Long insuranceCompanyId) {
        this.insuranceCompanyId = insuranceCompanyId;
    }

}

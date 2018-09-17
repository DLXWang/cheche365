package com.cheche365.cheche.ordercenter.web.model.telMarketingCenter;

import java.util.List;

/**
 * Created by yinJianBin on 2016/11/11.
 */
public class QuoteCompanyData {
    private String companyName;         //保险公司名称
    private int quoteNum;               //报价次数
    private List<QuoteRecordExt> quoteRecordExtList;      //报价数据

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getQuoteNum() {
        return quoteNum;
    }

    public void setQuoteNum(int quoteNum) {
        this.quoteNum = quoteNum;
    }

    public List<QuoteRecordExt> getQuoteRecordExtList() {
        return quoteRecordExtList;
    }

    public void setQuoteRecordExtList(List<QuoteRecordExt> quoteRecordExtList) {
        this.quoteRecordExtList = quoteRecordExtList;
    }
}

package com.cheche365.cheche.ordercenter.web.model.order;

import com.cheche365.cheche.common.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangshaobin on 2016/9/23.
 */
public class AmendQuoteRecordViewModel {
    private Long recordId;              //quoteRecordId
    private String commercialAmount;    //商业险金额
    private String compulsoryAmount;    //交强险金额
    private String autoTax;             //车船税
    private String totalAmount;         //总金额
    private String createTime;          //报价时间
    private Long orderHistoryId;        //purchaseOrderHistoryId

    public void setOrderHistoryId(Long orderHistoryId) {
        this.orderHistoryId = orderHistoryId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public void setCommercialAmount(String commercialAmount) {
        this.commercialAmount = commercialAmount;
    }

    public void setCompulsoryAmount(String compulsoryAmount) {
        this.compulsoryAmount = compulsoryAmount;
    }

    public void setAutoTax(String autoTax) {
        this.autoTax = autoTax;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Long getRecordId() {
        return recordId;
    }

    public String getCommercialAmount() {
        return commercialAmount;
    }

    public String getCompulsoryAmount() {
        return compulsoryAmount;
    }

    public String getAutoTax() {
        return autoTax;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getCreateTime() {
        return createTime;
    }

    public Long getOrderHistoryId() {
        return orderHistoryId;
    }

    public static List<AmendQuoteRecordViewModel> changeObjArrToAmendQuoteRecordViewModel(List<Object[]> objects){
        List<AmendQuoteRecordViewModel> list = new ArrayList<AmendQuoteRecordViewModel>();
        for(Object[] obj : objects){
            AmendQuoteRecordViewModel model = new AmendQuoteRecordViewModel();
            model.setRecordId(StringUtil.toLong(String.valueOf(obj[0])));
            model.setCommercialAmount(String.valueOf(obj[1]));
            model.setCompulsoryAmount(String.valueOf(obj[2]));
            model.setAutoTax(String.valueOf(obj[3]));
            model.setCreateTime(objFormatTime(obj[4]));
            model.setTotalAmount(getTotalAmount(obj));
            model.setOrderHistoryId(StringUtil.toLong(String.valueOf(obj[5])));
            list.add(model);
        }
        return list;
    }

    private static String objFormatTime(Object obj){
        return obj == null ? "" : String.valueOf(obj).substring(0,19);
    }

    private static String getTotalAmount(Object[] obj){
        Double sum = StringUtil.toDouble(obj[1]) + StringUtil.toDouble(obj[2]) + StringUtil.toDouble(obj[3]);
        return String.valueOf(sum);
    }
}

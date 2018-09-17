package com.cheche365.cheche.scheduletask.model;

import com.cheche365.cheche.core.model.ActivityMonitorData;

/**
 * Created by sunhuazhong on 2015/12/4.
 */
public class ActivityMonitorDataInfo {
    private String monitorTime;//监控时间
    private Integer pv = 0;//截止到最新更新时间对应的PV汇总值
    private Integer uv = 0;//最新更新时间对应的UV汇总值
    private Integer register = 0;//最新更新时间对应的注册汇总值
    private Integer quote = 0;//最新更新时间对应的试算汇总值
    private Integer submitCount = 0;//最新更新时间对应的提交订单数汇总值
    private Double submitAmount = 0.00;//最新更新时间对应的提交订单总额汇总值
    private Integer paymentCount = 0;//最新更新时间对应的支付订单数汇总值
    private Double paymentAmount = 0.00;//最新更新时间对应的支付订单总额汇总值
    private Double noAutoTaxAmount = 0.00;//最新更新时间对应的不包含车船税总额汇总值
    private Double specialMonitor = 0.00;//最新更新时间对应的特殊监控汇总值
    private Double customerField1 = 0.00;//最新更新时间对应的自定义字段1汇总值
    private Double customerField2 = 0.00;//最新更新时间对应的自定义字段2汇总值
    private Double customerField3 = 0.00;//最新更新时间对应的自定义字段3汇总值
    private Double customerField4 = 0.00;//最新更新时间对应的自定义字段4汇总值
    private Double customerField5 = 0.00;//最新更新时间对应的自定义字段5汇总值

    public String getMonitorTime() {
        return monitorTime;
    }

    public void setMonitorTime(String monitorTime) {
        this.monitorTime = monitorTime;
    }

    public Integer getPv() {
        return pv;
    }

    public void setPv(Integer pv) {
        this.pv = pv;
    }

    public Integer getUv() {
        return uv;
    }

    public void setUv(Integer uv) {
        this.uv = uv;
    }

    public Integer getRegister() {
        return register;
    }

    public void setRegister(Integer register) {
        this.register = register;
    }

    public Integer getQuote() {
        return quote;
    }

    public void setQuote(Integer quote) {
        this.quote = quote;
    }

    public Integer getSubmitCount() {
        return submitCount;
    }

    public void setSubmitCount(Integer submitCount) {
        this.submitCount = submitCount;
    }

    public Double getSubmitAmount() {
        return submitAmount;
    }

    public void setSubmitAmount(Double submitAmount) {
        this.submitAmount = submitAmount;
    }

    public Integer getPaymentCount() {
        return paymentCount;
    }

    public void setPaymentCount(Integer paymentCount) {
        this.paymentCount = paymentCount;
    }

    public Double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(Double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public Double getNoAutoTaxAmount() {
        return noAutoTaxAmount;
    }

    public void setNoAutoTaxAmount(Double noAutoTaxAmount) {
        this.noAutoTaxAmount = noAutoTaxAmount;
    }

    public Double getSpecialMonitor() {
        return specialMonitor;
    }

    public void setSpecialMonitor(Double specialMonitor) {
        this.specialMonitor = specialMonitor;
    }

    public Double getCustomerField1() {
        return customerField1;
    }

    public void setCustomerField1(Double customerField1) {
        this.customerField1 = customerField1;
    }

    public Double getCustomerField2() {
        return customerField2;
    }

    public void setCustomerField2(Double customerField2) {
        this.customerField2 = customerField2;
    }

    public Double getCustomerField3() {
        return customerField3;
    }

    public void setCustomerField3(Double customerField3) {
        this.customerField3 = customerField3;
    }

    public Double getCustomerField4() {
        return customerField4;
    }

    public void setCustomerField4(Double customerField4) {
        this.customerField4 = customerField4;
    }

    public Double getCustomerField5() {
        return customerField5;
    }

    public void setCustomerField5(Double customerField5) {
        this.customerField5 = customerField5;
    }

    private static Integer getValue(Integer integerValue) {
        return integerValue == null? new Integer(0) : integerValue;
    }

    private static Double getValue(Double doubleValue) {
        return doubleValue == null? new Double(0.0) : doubleValue;
    }

    public static void setMonitorData(ActivityMonitorDataInfo monitorData, ActivityMonitorData data) {
        monitorData.setPv(getValue(monitorData.getPv()) + getValue(data.getPv()));
        monitorData.setUv(getValue(monitorData.getUv()) + getValue(data.getUv()));
        monitorData.setRegister(getValue(monitorData.getRegister()) + getValue(data.getRegister()));
        monitorData.setQuote(getValue(monitorData.getQuote()) + getValue(data.getQuote()));
        monitorData.setSubmitCount(getValue(monitorData.getSubmitCount()) + getValue(data.getSubmitCount()));
        monitorData.setSubmitAmount(getValue(monitorData.getSubmitAmount()) + getValue(data.getSubmitAmount()));
        monitorData.setPaymentCount(getValue(monitorData.getPaymentCount()) + getValue(data.getPaymentCount()));
        monitorData.setPaymentAmount(getValue(monitorData.getPaymentAmount()) + getValue(data.getPaymentAmount()));
        monitorData.setNoAutoTaxAmount(getValue(monitorData.getNoAutoTaxAmount()) + getValue(data.getNoAutoTaxAmount()));
        monitorData.setSpecialMonitor(getValue(monitorData.getSpecialMonitor()) + getValue(data.getSpecialMonitor()));
    }
    public static void setSumMonitorData(ActivityMonitorDataInfo sumMonitorData, ActivityMonitorDataInfo monitorDataInfo) {
        sumMonitorData.setPv(getValue(sumMonitorData.getPv()) + getValue(monitorDataInfo.getPv()));
        sumMonitorData.setUv(getValue(sumMonitorData.getUv()) + getValue(monitorDataInfo.getUv()));
        sumMonitorData.setRegister(getValue(sumMonitorData.getRegister()) + getValue(monitorDataInfo.getRegister()));
        sumMonitorData.setQuote(getValue(sumMonitorData.getQuote()) + getValue(monitorDataInfo.getQuote()));
        sumMonitorData.setSubmitCount(getValue(sumMonitorData.getSubmitCount()) + getValue(monitorDataInfo.getSubmitCount()));
        sumMonitorData.setSubmitAmount(getValue(sumMonitorData.getSubmitAmount()) + getValue(monitorDataInfo.getSubmitAmount()));
        sumMonitorData.setPaymentCount(getValue(sumMonitorData.getPaymentCount()) + getValue(monitorDataInfo.getPaymentCount()));
        sumMonitorData.setPaymentAmount(getValue(sumMonitorData.getPaymentAmount()) + getValue(monitorDataInfo.getPaymentAmount()));
        sumMonitorData.setNoAutoTaxAmount(getValue(sumMonitorData.getNoAutoTaxAmount()) + getValue(monitorDataInfo.getNoAutoTaxAmount()));
        sumMonitorData.setSpecialMonitor(getValue(sumMonitorData.getSpecialMonitor()) + getValue(monitorDataInfo.getSpecialMonitor()));
    }
}

package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.Date;

/**
 * 监控数据
 * Created by sunhuazhong on 2015/8/25.
 */
@Entity
public class ActivityMonitorData {
    private Long id;
    private BusinessActivity businessActivity;//商务活动
    private Date monitorTime;//监控时间
    private Area area;//城市
    private Integer pv;//PV
    private Integer	uv;//UV
    private Integer register;//注册
    private Integer quote;//试算
    private Integer submitCount;//提交订单数
    private Double submitAmount;//提交订单总额
    private Integer paymentCount;//支付订单数
    private Double paymentAmount;//支付订单总额
    private Double noAutoTaxAmount;//不含车船税金额
    private Integer specialMonitor;//特殊监控，有可能是参与活动人数，领取红包数等

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "businessActivity", foreignKey=@ForeignKey(name="FK_ACTIVITY_MONITOR_DATA_REF_BUSINESS_ACTIVITY", foreignKeyDefinition="FOREIGN KEY (business_activity) REFERENCES business_activity(id)"))
    public BusinessActivity getBusinessActivity() {
        return businessActivity;
    }

    public void setBusinessActivity(BusinessActivity businessActivity) {
        this.businessActivity = businessActivity;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getMonitorTime() {
        return monitorTime;
    }

    public void setMonitorTime(Date monitorTime) {
        this.monitorTime = monitorTime;
    }

    @ManyToOne
    @JoinColumn(name = "area", foreignKey=@ForeignKey(name="FK_ACTIVITY_MONITOR_DATA_REF_AREA", foreignKeyDefinition="FOREIGN KEY (area) REFERENCES area(id)"))
    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    @Column(columnDefinition = "int(8)")
    public Integer getPv() {
        return pv;
    }

    public void setPv(Integer pv) {
        this.pv = pv;
    }

    @Column(columnDefinition = "int(8)")
    public Integer getUv() {
        return uv;
    }

    public void setUv(Integer uv) {
        this.uv = uv;
    }

    @Column(columnDefinition = "int(8)")
    public Integer getRegister() {
        return register;
    }

    public void setRegister(Integer register) {
        this.register = register;
    }

    @Column(columnDefinition = "int(8)")
    public Integer getQuote() {
        return quote;
    }

    public void setQuote(Integer quote) {
        this.quote = quote;
    }

    @Column(columnDefinition = "int(8)")
    public Integer getSubmitCount() {
        return submitCount;
    }

    public void setSubmitCount(Integer submitCount) {
        this.submitCount = submitCount;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getSubmitAmount() {
        return submitAmount;
    }

    public void setSubmitAmount(Double submitAmount) {
        this.submitAmount = submitAmount;
    }

    @Column(columnDefinition = "int(8)")
    public Integer getPaymentCount() {
        return paymentCount;
    }

    public void setPaymentCount(Integer paymentCount) {
        this.paymentCount = paymentCount;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
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

    @Column(columnDefinition = "int(8)")
    public Integer getSpecialMonitor() {
        return specialMonitor;
    }

    public void setSpecialMonitor(Integer specialMonitor) {
        this.specialMonitor = specialMonitor;
    }

}

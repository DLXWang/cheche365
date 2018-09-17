package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.serializer.FormattedDoubleSerializer
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode

import javax.persistence.*

/**
 * Created by mahong on 2016/11/29.
 * 按天买车险-停驶主表
 */
@Entity
@JsonIgnoreProperties(["purchaseOrder", "createTime", "updateTime", "description"])
class DailyInsurance extends DescribableEntity {
    private PurchaseOrder purchaseOrder;   // 订单id
    private String policyNo;               // 保单号
    private InsurancePackage insurancePackage; //停驶险种套餐
    private Date beginDate;                // 保单停驶开始日期
    private Date endDate;                  // 保单停驶结束日期
    private Double totalRefundAmount;      // 应退保费总额
    private BankCard bankCard;             // 银行卡信息
    private Date restartDate;              // 保单实际复驶日期
    private DailyInsuranceStatus status;   // 状态
    private String orderNo;                // Transient 订单号
    private Long days;                     // Transient 停驶总天数
    private Double discountAmount;         // Transient 累计节省金额（停驶退费金额减去复驶支付金额）
    private int isSync;                    //是否同步到安心标志   1-同步,  0-未同步

    @ManyToOne
    @JoinColumn(name = "purchase_order", foreignKey = @ForeignKey(name = "FK_DAILY_INSURANCE_REF_ORDER", foreignKeyDefinition = "FOREIGN KEY (`purchase_order`) REFERENCES `purchase_order` (`id`)"))
    PurchaseOrder getPurchaseOrder() {
        return purchaseOrder
    }

    DailyInsurance setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder
        return this
    }

    @Column(columnDefinition = "VARCHAR(45)")
    String getPolicyNo() {
        return policyNo
    }

    void setPolicyNo(String policyNo) {
        this.policyNo = policyNo
    }

    @ManyToOne
    @JoinColumn(name = "insurancePackage", foreignKey = @ForeignKey(name = "FK_DAILY_INSURANCE_REF_PKG", foreignKeyDefinition = "FOREIGN KEY (`insurance_package`) REFERENCES `insurance_package` (`id`)"))
    InsurancePackage getInsurancePackage() {
        return insurancePackage
    }

    void setInsurancePackage(InsurancePackage insurancePackage) {
        this.insurancePackage = insurancePackage
    }

    @Column(columnDefinition = "DATE")
    Date getBeginDate() {
        return beginDate
    }

    void setBeginDate(Date beginDate) {
        this.beginDate = beginDate
    }

    @Column(columnDefinition = "DATE")
    Date getEndDate() {
        return endDate
    }

    void setEndDate(Date endDate) {
        this.endDate = endDate
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    Double getTotalRefundAmount() {
        return totalRefundAmount
    }

    void setTotalRefundAmount(Double totalRefundAmount) {
        this.totalRefundAmount = totalRefundAmount
    }

    @ManyToOne
    @JoinColumn(name = "bankCard", foreignKey = @ForeignKey(name = "FK_DAILY_INSURANCE_REF_BANK_CARD", foreignKeyDefinition = "FOREIGN KEY (bank_card) REFERENCES bank_card(id)"))
    BankCard getBankCard() {
        return bankCard
    }

    void setBankCard(BankCard bankCard) {
        this.bankCard = bankCard
    }

    @Column(columnDefinition = "DATE")
    Date getRestartDate() {
        return restartDate
    }

    void setRestartDate(Date restartDate) {
        this.restartDate = restartDate
    }

    @ManyToOne
    @JoinColumn(name = "status", foreignKey = @ForeignKey(name = "FK_DAILY_INSURANCE_REF_STATUS", foreignKeyDefinition = "FOREIGN KEY (`status`) REFERENCES `daily_insurance_status` (`id`)"))
    DailyInsuranceStatus getStatus() {
        return this.status
    }

    void setStatus(DailyInsuranceStatus status) {
        this.status = status
    }

    int getIsSync() {
        return isSync
    }

    void setIsSync(int isSync) {
        this.isSync = isSync
    }

    private List<DailyInsuranceDetail> dailyInsuranceDetails = new ArrayList<DailyInsuranceDetail>();

    @Fetch(FetchMode.SELECT)
    @OneToMany(mappedBy = "dailyInsurance", fetch = FetchType.EAGER)
    List<DailyInsuranceDetail> getDailyInsuranceDetails() {
        return dailyInsuranceDetails
    }

    void setDailyInsuranceDetails(List<DailyInsuranceDetail> dailyInsuranceDetails) {
        this.dailyInsuranceDetails = dailyInsuranceDetails
    }

    @Transient
    String getOrderNo() {
        return (StringUtils.isBlank(orderNo) && purchaseOrder != null) ? purchaseOrder.orderNo : orderNo
    }

    void setOrderNo(String orderNo) {
        this.orderNo = orderNo
    }

    @Transient
    Long getDays() {
        return days
    }

    void setDays(Long days) {
        this.days = days
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Transient
    Double getDiscountAmount() {
        return discountAmount
    }

    void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount
    }

    @Transient
    Date getActualEndDate() {
        return this.getRestartDate() == null ? this.getEndDate() : DateUtils.addDays(this.getRestartDate(), -1)
    }

    //计算净停驶天数和返钱数
    void toNetData(List<DailyRestartInsurance> restartInsurances) {
        this.days = com.cheche365.cheche.common.util.DateUtils.getDaysBetween(this.getEndDate(), this.getBeginDate()) + 1
        this.discountAmount = this.getTotalRefundAmount()

        for (DailyRestartInsurance restartInsurance : restartInsurances) {
            this.days -= (com.cheche365.cheche.common.util.DateUtils.getDaysBetween(restartInsurance.getEndDate(), restartInsurance.getBeginDate()) + 1)
            this.discountAmount -= restartInsurance.premium
            this.dailyInsuranceDetails.each { dailyInsuranceDetail ->
                def restartInsuranceDetail = restartInsurance.restartInsuranceDetails.find {
                    it.code == dailyInsuranceDetail.code
                }
                if (restartInsuranceDetail) {
                    dailyInsuranceDetail.refundPremium -= (restartInsuranceDetail.premium + restartInsuranceDetail.iopPremium)
                }
            }
        }
    }


}

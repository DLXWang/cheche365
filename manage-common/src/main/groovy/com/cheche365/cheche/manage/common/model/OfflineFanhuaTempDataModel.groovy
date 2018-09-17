package com.cheche365.cheche.manage.common.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Transient

/**
 * Created by yinJianBin on 2018/02/11.
 */
@Entity
class OfflineFanhuaTempDataModel implements Comparable<OfflineFanhuaTempDataModel> {
    Long id;                //ID
    Long rowNo       //行号
    String orderNo      //序号
    String institution      //出单网点（系统）
    String ownerOrganizationName        //归属机构
    String groupName        //团队名称（系统）
    String agentName        //代理人（系统）
    String agentNo      //代理人编码
    String orderCreateTime      //出单日期
    String effectiveDate        //起保日期
    String reviewDate       //审单日期
    String policyNo         //保单号
    String policyBatchNo        //批单号
    String warrantyType         //保单类型
    String businessType         //业务类型
    String carType      //车辆种类
    String sellType         //销售方式
    String licensePlateNo       //车牌号
    String insuredName      //被保险人
    String premium      //保费
    String premiumAfterTax      //税后保费
    String insuranceCompanyName         //保险公司
    String insuranceType        //险种
    String rebateLevel      //费率等级
    String sumIncomeRebate      //总收入费率
    String sumIncomeAmount      //总收入金额
    String incomeAfterTax       //税后收入
    String commissionChargeRebate1      //手续费率1
    String commissionChargeAmount1      //手续费1
    String commissionChargeRebateAdded1         //手续费1（增）
    String billingDate1         //开票日期1
    String invoiceNo1       //发票号
    String paymentTime1         //收款日期1
    String balanceBatchNo1      //结算批次号1
    String commissionChargeRebate2      //手续费率2
    String commissionChargeAmount2      //手续费2
    String commissionChargeRebateAdded2         //手续费2(增）
    String billingDate2         //开票日期2
    String invoiceNo2       //发票号
    String paymentTime2         //收款日期2
    String balanceBatchNo2      //结算批次号2
    String commissionChargeRebate3      //手续费率3
    String commissionChargeAmount3      //手续费3
    String commissionChargeRebateAdded3         //手续费3(增）
    String billingDate3         //开票日期3
    String invoiceNo3       //发票号
    String paymentTime3         //收款日期3
    String balanceBatchNo3      //结算批次号3
    String commissionChargeRebate4      //手续费率4（调整）
    String commissionChargeAmount4      //手续费4
    String commissionChargeRebateAdded4         //手续费4（增）
    String billingDate4         //开票日期4
    String invoiceNo4       //发票号
    String paymentTime4         //收款日期4
    String balanceBatchNo4      //结算批次号4
    String noCommissionChargeAmount         //未收手续费
    String sumRebate        //总佣金率
    String sumRebateAmount      //总佣金
    String rebate       //佣金率
    String rebateAmount         //佣金
    String payRebateDate1       //付佣日期1
    String costDate1        //成本属期1
    String rebateChangedRebate      //佣金率调整率
    String rebateChangedAmount      //佣金调整
    String payRebateChangedDate         //调整付佣日期
    String costDateChangedDate1         //调整成本属期1
    String promoteRebate        //推广费率
    String promoteAmount        //推广费
    String payTime2         //支付日期2
    String costDate2        //成本归属期2
    String rebateAddedPoint         //佣金2补发率
    String rebateAddedAmount        //佣金2补发
    String payTime      //支付日期
    String deliveryFee      //快递费
    String trueRebateAmount         //实发佣金
    String recommendReward      //推荐奖
    String rebatePoint4         //佣金4比例
    String rebateAmount4        //佣金4金额
    String payTime3         //支付日期
    String afterTaxMargin       //税后毛利率
    String afterMaxIncome       //税后毛利
    String comment1        //备注1(真实代理人)
    String comment2        //备注2（长）

    Long historyId
    Date createTime
    Integer status

    @Transient
    transient String errorMessage

    //===========================getter===========================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long getId() {
        return id
    }

    @Column(columnDefinition = "bigint(20)")
    Long getRowNo() {
        return rowNo
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getOrderNo() { return orderNo }

    String getInstitution() {
        return institution
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getOwnerOrganizationName() {
        return ownerOrganizationName
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getGroupName() {
        return groupName
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getAgentName() {
        return agentName
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getAgentNo() {
        return agentNo
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getOrderCreateTime() {
        return orderCreateTime
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getEffectiveDate() {
        return effectiveDate
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getReviewDate() {
        return reviewDate
    }

    @Column(columnDefinition = "VARCHAR(30)")
    String getPolicyNo() {
        return policyNo
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getPolicyBatchNo() {
        return policyBatchNo
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getWarrantyType() {
        return warrantyType
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getBusinessType() {
        return businessType
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getCarType() {
        return carType
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getSellType() {
        return sellType
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getLicensePlateNo() {
        return licensePlateNo
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getInsuredName() {
        return insuredName
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getPremium() {
        return premium
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getPremiumAfterTax() {
        return premiumAfterTax
    }

    @Column(columnDefinition = "VARCHAR(30)")
    String getInsuranceCompanyName() {
        return insuranceCompanyName
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getInsuranceType() {
        return insuranceType
    }

    @Column(columnDefinition = "VARCHAR(40)")
    String getRebateLevel() {
        return rebateLevel
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getSumIncomeRebate() {
        return sumIncomeRebate
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getSumIncomeAmount() {
        return sumIncomeAmount
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getIncomeAfterTax() {
        return incomeAfterTax
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getCommissionChargeRebate1() {
        return commissionChargeRebate1
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getCommissionChargeAmount1() {
        return commissionChargeAmount1
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getCommissionChargeRebateAdded1() {
        return commissionChargeRebateAdded1
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getBillingDate1() {
        return billingDate1
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getInvoiceNo1() {
        return invoiceNo1
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getPaymentTime1() {
        return paymentTime1
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getBalanceBatchNo1() {
        return balanceBatchNo1
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getCommissionChargeRebate2() {
        return commissionChargeRebate2
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getCommissionChargeAmount2() {
        return commissionChargeAmount2
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getCommissionChargeRebateAdded2() {
        return commissionChargeRebateAdded2
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getBillingDate2() {
        return billingDate2
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getInvoiceNo2() {
        return invoiceNo2
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getPaymentTime2() {
        return paymentTime2
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getBalanceBatchNo2() {
        return balanceBatchNo2
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getCommissionChargeRebate3() {
        return commissionChargeRebate3
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getCommissionChargeAmount3() {
        return commissionChargeAmount3
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getCommissionChargeRebateAdded3() {
        return commissionChargeRebateAdded3
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getBillingDate3() {
        return billingDate3
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getInvoiceNo3() {
        return invoiceNo3
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getPaymentTime3() {
        return paymentTime3
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getBalanceBatchNo3() {
        return balanceBatchNo3
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getCommissionChargeRebate4() {
        return commissionChargeRebate4
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getCommissionChargeAmount4() {
        return commissionChargeAmount4
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getCommissionChargeRebateAdded4() {
        return commissionChargeRebateAdded4
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getBillingDate4() {
        return billingDate4
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getInvoiceNo4() {
        return invoiceNo4
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getPaymentTime4() {
        return paymentTime4
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getBalanceBatchNo4() {
        return balanceBatchNo4
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getNoCommissionChargeAmount() {
        return noCommissionChargeAmount
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getSumRebate() {
        return sumRebate
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getSumRebateAmount() {
        return sumRebateAmount
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getRebate() {
        return rebate
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getRebateAmount() {
        return rebateAmount
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getPayRebateDate1() {
        return payRebateDate1
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getCostDate1() {
        return costDate1
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getRebateChangedRebate() {
        return rebateChangedRebate
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getRebateChangedAmount() {
        return rebateChangedAmount
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getPayRebateChangedDate() {
        return payRebateChangedDate
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getCostDateChangedDate1() {
        return costDateChangedDate1
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getPromoteRebate() {
        return promoteRebate
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getPromoteAmount() {
        return promoteAmount
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getPayTime2() {
        return payTime2
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getCostDate2() {
        return costDate2
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getRebateAddedPoint() {
        return rebateAddedPoint
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getRebateAddedAmount() {
        return rebateAddedAmount
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getPayTime() {
        return payTime
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getDeliveryFee() {
        return deliveryFee
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getTrueRebateAmount() {
        return trueRebateAmount
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getRecommendReward() {
        return recommendReward
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getRebatePoint4() {
        return rebatePoint4
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getRebateAmount4() {
        return rebateAmount4
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getPayTime3() {
        return payTime3
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getAfterTaxMargin() {
        return afterTaxMargin
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getAfterMaxIncome() {
        return afterMaxIncome
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getComment1() {
        return comment1
    }

    @Column(columnDefinition = "VARCHAR(20)")
    String getComment2() {
        return comment2
    }

    @Column(columnDefinition = "VARCHAR(20)")
    Long getHistoryId() {
        return historyId
    }

    @Column(columnDefinition = "datetime")
    Date getCreateTime() {
        return createTime
    }

    @Column(columnDefinition = "tinyint(1)")
    Integer getStatus() {
        return status
    }

    @Transient
    String getErrorMessage() {
        return errorMessage
    }
//===========================setter===========================
    void setRowNo(Long rowNo) {
        this.rowNo = rowNo
    }

    void setOrderNo(String orderNo) {
        this.orderNo = orderNo
    }

    void setInstitution(String institution) {
        this.institution = institution
    }

    void setOwnerOrganizationName(String ownerOrganizationName) {
        this.ownerOrganizationName = ownerOrganizationName
    }

    void setGroupName(String groupName) {
        this.groupName = groupName
    }

    void setAgentName(String agentName) {
        this.agentName = agentName
    }

    void setAgentNo(String agentNo) {
        this.agentNo = agentNo
    }

    void setOrderCreateTime(String orderCreateTime) {
        this.orderCreateTime = orderCreateTime
    }

    void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate
    }

    void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate
    }

    void setPolicyNo(String policyNo) {
        this.policyNo = policyNo
    }

    void setPolicyBatchNo(String policyBatchNo) {
        this.policyBatchNo = policyBatchNo
    }

    void setWarrantyType(String warrantyType) {
        this.warrantyType = warrantyType
    }

    void setBusinessType(String businessType) {
        this.businessType = businessType
    }

    void setCarType(String carType) {
        this.carType = carType
    }

    void setSellType(String sellType) {
        this.sellType = sellType
    }

    void setLicensePlateNo(String licensePlateNo) {
        this.licensePlateNo = licensePlateNo
    }

    void setInsuredName(String insuredName) {
        this.insuredName = insuredName
    }

    void setPremium(String premium) {
        this.premium = premium
    }

    void setPremiumAfterTax(String premiumAfterTax) {
        this.premiumAfterTax = premiumAfterTax
    }

    void setInsuranceCompanyName(String insuranceCompanyName) {
        this.insuranceCompanyName = insuranceCompanyName
    }

    void setInsuranceType(String insuranceType) {
        this.insuranceType = insuranceType
    }

    void setRebateLevel(String rebateLevel) {
        this.rebateLevel = rebateLevel
    }

    void setSumIncomeRebate(String sumIncomeRebate) {
        this.sumIncomeRebate = sumIncomeRebate
    }

    void setSumIncomeAmount(String sumIncomeAmount) {
        this.sumIncomeAmount = sumIncomeAmount
    }

    void setIncomeAfterTax(String incomeAfterTax) {
        this.incomeAfterTax = incomeAfterTax
    }

    void setCommissionChargeRebate1(String commissionChargeRebate1) {
        this.commissionChargeRebate1 = commissionChargeRebate1
    }

    void setCommissionChargeAmount1(String commissionChargeAmount1) {
        this.commissionChargeAmount1 = commissionChargeAmount1
    }

    void setCommissionChargeRebateAdded1(String commissionChargeRebateAdded1) {
        this.commissionChargeRebateAdded1 = commissionChargeRebateAdded1
    }

    void setBillingDate1(String billingDate1) {
        this.billingDate1 = billingDate1
    }

    void setInvoiceNo1(String invoiceNo1) {
        this.invoiceNo1 = invoiceNo1
    }

    void setPaymentTime1(String paymentTime1) {
        this.paymentTime1 = paymentTime1
    }

    void setBalanceBatchNo1(String balanceBatchNo1) {
        this.balanceBatchNo1 = balanceBatchNo1
    }

    void setCommissionChargeRebate2(String commissionChargeRebate2) {
        this.commissionChargeRebate2 = commissionChargeRebate2
    }

    void setCommissionChargeAmount2(String commissionChargeAmount2) {
        this.commissionChargeAmount2 = commissionChargeAmount2
    }

    void setCommissionChargeRebateAdded2(String commissionChargeRebateAdded2) {
        this.commissionChargeRebateAdded2 = commissionChargeRebateAdded2
    }

    void setBillingDate2(String billingDate2) {
        this.billingDate2 = billingDate2
    }

    void setInvoiceNo2(String invoiceNo2) {
        this.invoiceNo2 = invoiceNo2
    }

    void setPaymentTime2(String paymentTime2) {
        this.paymentTime2 = paymentTime2
    }

    void setBalanceBatchNo2(String balanceBatchNo2) {
        this.balanceBatchNo2 = balanceBatchNo2
    }

    void setCommissionChargeRebate3(String commissionChargeRebate3) {
        this.commissionChargeRebate3 = commissionChargeRebate3
    }

    void setCommissionChargeAmount3(String commissionChargeAmount3) {
        this.commissionChargeAmount3 = commissionChargeAmount3
    }

    void setCommissionChargeRebateAdded3(String commissionChargeRebateAdded3) {
        this.commissionChargeRebateAdded3 = commissionChargeRebateAdded3
    }

    void setBillingDate3(String billingDate3) {
        this.billingDate3 = billingDate3
    }

    void setInvoiceNo3(String invoiceNo3) {
        this.invoiceNo3 = invoiceNo3
    }

    void setPaymentTime3(String paymentTime3) {
        this.paymentTime3 = paymentTime3
    }

    void setBalanceBatchNo3(String balanceBatchNo3) {
        this.balanceBatchNo3 = balanceBatchNo3
    }

    void setCommissionChargeRebate4(String commissionChargeRebate4) {
        this.commissionChargeRebate4 = commissionChargeRebate4
    }

    void setCommissionChargeAmount4(String commissionChargeAmount4) {
        this.commissionChargeAmount4 = commissionChargeAmount4
    }

    void setCommissionChargeRebateAdded4(String commissionChargeRebateAdded4) {
        this.commissionChargeRebateAdded4 = commissionChargeRebateAdded4
    }

    void setBillingDate4(String billingDate4) {
        this.billingDate4 = billingDate4
    }

    void setInvoiceNo4(String invoiceNo4) {
        this.invoiceNo4 = invoiceNo4
    }

    void setPaymentTime4(String paymentTime4) {
        this.paymentTime4 = paymentTime4
    }

    void setBalanceBatchNo4(String balanceBatchNo4) {
        this.balanceBatchNo4 = balanceBatchNo4
    }

    void setNoCommissionChargeAmount(String noCommissionChargeAmount) {
        this.noCommissionChargeAmount = noCommissionChargeAmount
    }

    void setSumRebate(String sumRebate) {
        this.sumRebate = sumRebate
    }

    void setSumRebateAmount(String sumRebateAmount) {
        this.sumRebateAmount = sumRebateAmount
    }

    void setRebate(String rebate) {
        this.rebate = rebate
    }

    void setRebateAmount(String rebateAmount) {
        this.rebateAmount = rebateAmount
    }

    void setPayRebateDate1(String payRebateDate1) {
        this.payRebateDate1 = payRebateDate1
    }

    void setCostDate1(String costDate1) {
        this.costDate1 = costDate1
    }

    void setRebateChangedRebate(String rebateChangedRebate) {
        this.rebateChangedRebate = rebateChangedRebate
    }

    void setRebateChangedAmount(String rebateChangedAmount) {
        this.rebateChangedAmount = rebateChangedAmount
    }

    void setPayRebateChangedDate(String payRebateChangedDate) {
        this.payRebateChangedDate = payRebateChangedDate
    }

    void setCostDateChangedDate1(String costDateChangedDate1) {
        this.costDateChangedDate1 = costDateChangedDate1
    }

    void setPromoteRebate(String promoteRebate) {
        this.promoteRebate = promoteRebate
    }

    void setPromoteAmount(String promoteAmount) {
        this.promoteAmount = promoteAmount
    }

    void setPayTime2(String payTime2) {
        this.payTime2 = payTime2
    }

    void setCostDate2(String costDate2) {
        this.costDate2 = costDate2
    }

    void setRebateAddedPoint(String rebateAddedPoint) {
        this.rebateAddedPoint = rebateAddedPoint
    }

    void setRebateAddedAmount(String rebateAddedAmount) {
        this.rebateAddedAmount = rebateAddedAmount
    }

    void setPayTime(String payTime) {
        this.payTime = payTime
    }

    void setDeliveryFee(String deliveryFee) {
        this.deliveryFee = deliveryFee
    }

    void setTrueRebateAmount(String trueRebateAmount) {
        this.trueRebateAmount = trueRebateAmount
    }

    void setRecommendReward(String recommendReward) {
        this.recommendReward = recommendReward
    }

    void setRebatePoint4(String rebatePoint4) {
        this.rebatePoint4 = rebatePoint4
    }

    void setRebateAmount4(String rebateAmount4) {
        this.rebateAmount4 = rebateAmount4
    }

    void setPayTime3(String payTime3) {
        this.payTime3 = payTime3
    }

    void setAfterTaxMargin(String afterTaxMargin) {
        this.afterTaxMargin = afterTaxMargin
    }

    void setAfterMaxIncome(String afterMaxIncome) {
        this.afterMaxIncome = afterMaxIncome
    }

    void setComment1(String comment1) {
        this.comment1 = comment1
    }

    void setComment2(String comment2) {
        this.comment2 = comment2
    }

    void setHistoryId(Long historyId) {
        this.historyId = historyId
    }

    void setCreateTime(Date createTime) {
        this.createTime = createTime
    }

    void setStatus(Integer status) {
        this.status = status
    }

    @Override
    int compareTo(OfflineFanhuaTempDataModel o) {
        return this.rowNo - o.rowNo
    }
}

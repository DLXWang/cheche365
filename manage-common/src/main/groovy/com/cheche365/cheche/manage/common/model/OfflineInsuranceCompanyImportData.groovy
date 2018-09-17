package com.cheche365.cheche.manage.common.model

import com.cheche365.cheche.core.model.PurchaseOrder

import javax.persistence.*

/**
 * 线下数据导入,保险公司数据
 * Created by yinJianBin on 2017/11/5.
 */
@Entity
class OfflineInsuranceCompanyImportData implements Comparable<OfflineInsuranceCompanyImportData> {
    private Long id;                //ID
    String policyNo            //	保险单号码/批单号码
    String insuredName         //	被保险人
    Double paidAmount          //	实收保费
    Double rebate          //	费用比例%
    Double rebateAmount            //	已结收付费
    Date balanceTime         //	结算时间
    String licensePlateNo          //	车牌号码
    String brandModel          //	车型名称
    String engineNo            //	发动机号
    String vinNo           //	车架号
    Date issueTime           //	出单日期

    PurchaseOrder purchaseOrder       //订单
    Integer status              //对账状态
    Integer matchNum            //匹配次数
    Date createTime
    Date updateTime
    String comment             //备注
    String description         //描述
    OfflineOrderImportHistory history   //上传历史
    Integer rebateAddTimes      //补点次数


    transient String errorMessage        //校验错误原因
    transient Integer order        //行号

    static class Enum {
        static Integer STATUS_NOT_MATCH = 0 //未匹配
        static Integer STATUS_MATCH_FAILD = 5  //匹配失败
        static Integer STATUS_CHECK_FAILD = 10  //校验失败
        static Integer STATUS_MATCH_SUCCESS = 15 //匹配成功
        static Integer STATUS_MATCH_FINISHED = 20    //匹配结束
    }

    @ManyToOne
    @JoinColumn(name = "history", foreignKey = @ForeignKey(name = "FK_OFFLINE_INSURANCE_COMPANY_IMPORT_DATA_REF_IMPORT_HISTORY", foreignKeyDefinition = "FOREIGN KEY (history) REFERENCES offline_order_import_history(id)"))
    OfflineOrderImportHistory getHistory() {
        return history
    }

    void setHistory(OfflineOrderImportHistory history) {
        this.history = history
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    @Column(columnDefinition = "VARCHAR(45)")
    String getPolicyNo() {
        return policyNo
    }

    void setPolicyNo(String policyNo) {
        this.policyNo = policyNo
    }

    @Column(columnDefinition = 'VARCHAR (20)')
    String getInsuredName() {
        return insuredName
    }

    void setInsuredName(String insuredName) {
        this.insuredName = insuredName
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double getPaidAmount() {
        return paidAmount
    }

    void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double getRebate() {
        return rebate
    }

    void setRebate(Double rebate) {
        this.rebate = rebate
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double getRebateAmount() {
        return rebateAmount
    }

    void setRebateAmount(Double rebateAmount) {
        this.rebateAmount = rebateAmount
    }

    @Column(columnDefinition = "DATETIME")
    Date getBalanceTime() {
        return balanceTime
    }

    void setBalanceTime(Date balanceTime) {
        this.balanceTime = balanceTime
    }

    @Column(columnDefinition = 'varchar(45)')
    String getLicensePlateNo() {
        return licensePlateNo
    }

    void setLicensePlateNo(String licensePlateNo) {
        this.licensePlateNo = licensePlateNo
    }

    @Column(columnDefinition = 'varchar(150)')
    String getBrandModel() {
        return brandModel
    }

    void setBrandModel(String brandModel) {
        this.brandModel = brandModel
    }

    @Column(columnDefinition = 'varchar(45)')
    String getEngineNo() {
        return engineNo
    }

    void setEngineNo(String engineNo) {
        this.engineNo = engineNo
    }

    @Column(columnDefinition = 'varchar(45)')
    String getVinNo() {
        return vinNo
    }

    void setVinNo(String vinNo) {
        this.vinNo = vinNo
    }

    @Column(columnDefinition = "DATETIME")
    Date getIssueTime() {
        return issueTime
    }

    void setIssueTime(Date issueTime) {
        this.issueTime = issueTime
    }

    @ManyToOne
    @JoinColumn(name = "purchase_order", foreignKey = @ForeignKey(name = "FK_OFFLINE_INSURANCE_COMPANY_IMPORT_DATA_REF_PURCHASE_ORDER", foreignKeyDefinition = "FOREIGN KEY (purchase_order) REFERENCES purchase_order(id)"))
    PurchaseOrder getPurchaseOrder() {
        return purchaseOrder
    }

    OfflineInsuranceCompanyImportData setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder
        return this
    }

    @Column(columnDefinition = 'TINYINT(1)')
    Integer getStatus() {
        return status
    }

    OfflineInsuranceCompanyImportData setStatus(Integer status) {
        this.status = status
        return this
    }

    @Column(columnDefinition = 'TINYINT(2)')
    Integer getMatchNum() {
        return matchNum
    }

    OfflineInsuranceCompanyImportData setMatchNum(Integer matchNum) {
        this.matchNum = matchNum
        return this
    }

    @Column(columnDefinition = "DATETIME")
    Date getCreateTime() {
        return createTime
    }

    OfflineInsuranceCompanyImportData setCreateTime(Date createTime) {
        this.createTime = createTime
        return this
    }

    @Column(columnDefinition = "DATETIME")
    Date getUpdateTime() {
        return updateTime
    }

    OfflineInsuranceCompanyImportData setUpdateTime(Date updateTime) {
        this.updateTime = updateTime
        return this
    }

    @Column(columnDefinition = "varchar(200)")
    String getComment() {
        return comment
    }

    void setComment(String comment) {
        this.comment = comment
    }

    @Column(columnDefinition = "varchar(150)")
    String getDescription() {
        return description
    }

    void setDescription(String description) {
        this.description = description
    }

    @Transient
    String getErrorMessage() {
        return errorMessage
    }

    OfflineInsuranceCompanyImportData setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage
        return this
    }

    @Transient
    Integer getOrder() {
        return order
    }

    void setOrder(Integer order) {
        this.order = order
    }

    @Override
    int compareTo(OfflineInsuranceCompanyImportData o) {
        return this.order - o.order
    }

    @Column(columnDefinition = "TINYINT(2)")
    Integer getRebateAddTimes() {
        return rebateAddTimes
    }

    void setRebateAddTimes(Integer rebateAddTimes) {
        this.rebateAddTimes = rebateAddTimes
    }

    boolean equalsTo(OfflineInsuranceCompanyImportData that) {

        if (!policyNo.equals(that.policyNo)) return false
        if (rebateAddTimes != that.rebateAddTimes) return false

        return true
    }

}

package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.Date;

/**
 * 合作出单信息表
 * Created by sunhuazhong on 2015/11/13.
 */
@Entity
public class OrderCooperationInfo {
    private Long id;
    private PurchaseOrder purchaseOrder;//订单
    private Area area;//城市
    private InsuranceCompany insuranceCompany;//保险公司
    private AreaContactInfo areaContactInfo;//车车分站
    private Institution institution;//出单机构
    private Date appointTime;//指定出单机构时间
    private OrderCooperationStatus status;//合作出单状态
    private String reason;//订单异常原因
    private Boolean rebateStatus;//确认佣金到账状态
    private Boolean auditStatus;//审核状态
    private Boolean incomeStatus;//收益状态
    private Boolean matchStatus;//险种保额匹配状态
    private Date createTime;//创建时间
    private Date updateTime;//修改时间
    private InternalUser operator;//最后操作人
    private InternalUser assigner;//指定操作人
    private Boolean quoteStatus;//报价状态

    @Transient
    private String refundObject;//退款对象字符串

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToOne
    @JoinColumn(name = "purchaseOrder", foreignKey=@ForeignKey(name="FK_ORDER_COOPERATION_INFO_REF_PURCHASE_ORDER", foreignKeyDefinition="FOREIGN KEY (purchase_order) REFERENCES purchase_order(id)"))
    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    @ManyToOne
    @JoinColumn(name = "area", foreignKey=@ForeignKey(name="FK_ORDER_COOPERATION_INFO_REF_AREA", foreignKeyDefinition="FOREIGN KEY (area) REFERENCES area(id)"))
    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    @ManyToOne
    @JoinColumn(name = "insuranceCompany", foreignKey=@ForeignKey(name="FK_ORDER_COOPERATION_INFO_REF_INSURANCE_COMPANY", foreignKeyDefinition="FOREIGN KEY (insurance_company) REFERENCES insurance_company(id)"))
    public InsuranceCompany getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(InsuranceCompany insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    @ManyToOne
    @JoinColumn(name = "areaContactInfo", foreignKey=@ForeignKey(name="FK_ORDER_COOPERATION_INFO_REF_AREA_CONTACT_INFO", foreignKeyDefinition="FOREIGN KEY (area_contact_info) REFERENCES area_contact_info(id)"))
    public AreaContactInfo getAreaContactInfo() {
        return areaContactInfo;
    }

    public void setAreaContactInfo(AreaContactInfo areaContactInfo) {
        this.areaContactInfo = areaContactInfo;
    }

    @ManyToOne
    @JoinColumn(name = "institution", foreignKey=@ForeignKey(name="FK_ORDER_COOPERATION_INFO_REF_INSTITUTION", foreignKeyDefinition="FOREIGN KEY (institution) REFERENCES institution(id)"))
    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getAppointTime() {
        return appointTime;
    }

    public void setAppointTime(Date appointTime) {
        this.appointTime = appointTime;
    }

    @ManyToOne
    @JoinColumn(name = "status", foreignKey=@ForeignKey(name="FK_ORDER_COOPERATION_INFO_REF_STATUS", foreignKeyDefinition="FOREIGN KEY (status) REFERENCES order_cooperation_status(id)"))
    public OrderCooperationStatus getStatus() {
        return status;
    }

    public void setStatus(OrderCooperationStatus status) {
        this.status = status;
    }

    @Column(columnDefinition = "varchar(45)")
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Boolean getRebateStatus() {
        return rebateStatus;
    }

    public void setRebateStatus(Boolean rebateStatus) {
        this.rebateStatus = rebateStatus;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Boolean getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Boolean auditStatus) {
        this.auditStatus = auditStatus;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Boolean getIncomeStatus() {
        return incomeStatus;
    }

    public void setIncomeStatus(Boolean incomeStatus) {
        this.incomeStatus = incomeStatus;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Boolean getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(Boolean matchStatus) {
        this.matchStatus = matchStatus;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey=@ForeignKey(name="FK_ORDER_COOPERATION_INFO_REF_OPERATOR", foreignKeyDefinition="FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    @ManyToOne
    @JoinColumn(name = "assigner", foreignKey=@ForeignKey(name="FK_ORDER_COOPERATION_INFO_REF_ASSIGNER", foreignKeyDefinition="FOREIGN KEY (assigner) REFERENCES internal_user(id)"))
    public InternalUser getAssigner() {
        return assigner;
    }

    public void setAssigner(InternalUser assigner) {
        this.assigner = assigner;
    }

    public Boolean getQuoteStatus() {
        return quoteStatus;
    }

    public void setQuoteStatus(Boolean quoteStatus) {
        this.quoteStatus = quoteStatus;
    }

    @Transient
    public String getRefundObject() {
        return refundObject;
    }

    public void setRefundObject(String refundObject) {
        this.refundObject = refundObject;
    }
}

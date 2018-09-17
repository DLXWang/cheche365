package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.serializer.FormattedDoubleSerializer
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.apache.commons.lang.math.NumberUtils
import org.apache.commons.lang3.time.DateUtils

import javax.persistence.*

import static com.cheche365.cheche.core.model.GiftTypeUseType.Enum.REDUCE_1

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
class Gift extends DescribableEntity implements Comparable<Gift> {
    private static final long serialVersionUID = 1L

    private User applicant;//用户

    private SourceType sourceType;//福利来源：订单，微信红包

    private Long source;//保单id

    private GiftType giftType;//福利种类：加油卡，微信红包

    private Double giftAmount;//福利金额

    private Date effectiveDate;//有效日期

    private Date expireDate;//失效日期

    private GiftStatus status;

    private Integer quantity;//礼品数量

    private String unit;//单位

    private Double fullLimitAmount;

    private String usageRuleDescription;

    private String usageRuleClass;

    @JsonIgnore
    private String usageRuleParam;

    private String giftDetailLink;

    private String reason;//福利描述

    private String giftDisplay;

    private String giftContent;

    private Double compulsoryPercent //页面输入12,数据库存0.12
    private Double commercialPercent //页面输入12,数据库存0.12


    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,6)")
    Double getCompulsoryPercent() {
        return compulsoryPercent
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,6)")
    Double getCommercialPercent() {
        return commercialPercent
    }

    void setCompulsoryPercent(Double compulsoryPercent) {
        this.compulsoryPercent = compulsoryPercent
    }

    void setCommercialPercent(Double commercialPercent) {
        this.commercialPercent = commercialPercent
    }

    @ManyToOne
    @JoinColumn(name = "applicant", foreignKey = @ForeignKey(name = "FK_GIFT_REF_USER", foreignKeyDefinition = "FOREIGN KEY (applicant) REFERENCES user(id)"))
    User getApplicant() {
        return applicant;
    }

    void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    @Column(columnDefinition = "VARCHAR(2000)")
    String getReason() {
        return reason;
    }

    void setReason(String reason) {
        this.reason = reason;
    }

    @ManyToOne
    @JoinColumn(name = "sourceType", foreignKey = @ForeignKey(name = "FK_GIFT_REF_SOURCE_TYPE", foreignKeyDefinition = "FOREIGN KEY (source_type) REFERENCES source_type(id)"))
    SourceType getSourceType() {
        return sourceType;
    }

    void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    @Column(columnDefinition = "BIGINT(20)")
    Long getSource() {
        return source;
    }

    void setSource(Long source) {
        this.source = source;
    }

    @ManyToOne
    @JoinColumn(name = "giftType", foreignKey = @ForeignKey(name = "FK_GIFT_REF_GIFT_TYPE", foreignKeyDefinition = "FOREIGN KEY (gift_type) REFERENCES gift_type(id)"))
    GiftType getGiftType() {
        return giftType;
    }

    void setGiftType(GiftType giftType) {
        this.giftType = giftType;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    Double getGiftAmount() {
        return giftAmount;
    }

    void setGiftAmount(Double giftAmount) {
        this.giftAmount = giftAmount;
    }

    @Column(columnDefinition = "DATE")
    Date getEffectiveDate() {
        return effectiveDate;
    }

    void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @Column(columnDefinition = "DATE")
    Date getExpireDate() {
        return expireDate;
    }

    void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }


    @ManyToOne
    @JoinColumn(name = "status", foreignKey = @ForeignKey(name = "FK_GIFT_REF_GIFT_STATUS", foreignKeyDefinition = "FOREIGN KEY (status) REFERENCES gift_status(id)"))
    GiftStatus getStatus() {
        return status;
    }

    void setStatus(GiftStatus status) {
        this.status = status;
    }

    @Column(columnDefinition = "tinyint(3)")
    Integer getQuantity() {
        return quantity;
    }


    void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Column(columnDefinition = "VARCHAR(10)")
    String getUnit() {
        return unit;
    }

    void setUnit(String unit) {
        this.unit = unit;
    }


    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    Double getFullLimitAmount() {
        return fullLimitAmount;
    }

    void setFullLimitAmount(Double fullLimitAmount) {
        this.fullLimitAmount = fullLimitAmount;
    }

    @Column(columnDefinition = "VARCHAR(500)")
    String getUsageRuleDescription() {
        return usageRuleDescription;
    }

    void setUsageRuleDescription(String usageRuleDescription) {
        this.usageRuleDescription = usageRuleDescription;
    }


    @Column(columnDefinition = "varchar(50)")
    String getGiftDisplay() {
        return giftDisplay;
    }

    void setGiftDisplay(String giftDisplay) {
        this.giftDisplay = giftDisplay;
    }

    @Column(columnDefinition = "VARCHAR(150)")
    String getUsageRuleClass() {
        return usageRuleClass;
    }

    void setUsageRuleClass(String usageRuleClass) {
        this.usageRuleClass = usageRuleClass;
    }

    @Column(columnDefinition = "VARCHAR(200)")
    String getUsageRuleParam() {
        return usageRuleParam;
    }

    public void setUsageRuleParam(String usageRuleParam) {
        this.usageRuleParam = usageRuleParam;
    }

    @Column(name = "gift_detail_link", columnDefinition = "VARCHAR(200)")
    String getGiftDetailLink() {
        return giftDetailLink;
    }

    void setGiftDetailLink(String giftDetailLink) {
        this.giftDetailLink = giftDetailLink;
    }


    @Transient
    boolean isExpired() {
        return this.expireDate != null && DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH).after(this.expireDate);
    }

    @Override
    int compareTo(Gift comparedGift) {
        return (int) (comparedGift.getGiftAmount() - this.getGiftAmount());
    }

    Double comparableAmount() {
        REDUCE_1 == giftType.useType ? giftAmount : (NumberUtils.isNumber(giftDisplay) ? NumberUtils.toDouble(giftDisplay) : -1)
    }


    @Column
    String getGiftContent() {
        return giftContent;
    }

    void setGiftContent(String giftContent) {
        this.giftContent = giftContent;
    }

    @Transient
    Boolean commercialOnly() {
        ("commercial" == this.usageRuleParam)
    }

    @Transient
    String usageRuleDescPrefix() {
        (this.commercialOnly() ? "商业险" : "")
    }

    static Gift genGiftTemplate(User applicant, GiftType giftType) {
        Gift newGift = new Gift();
        newGift.setGiftType(giftType);
        newGift.setReason("");
        newGift.setGiftAmount(0.0);
        newGift.setSourceType(SourceType.Enum.WECHATRED_2);
        newGift.setSource(null);
        newGift.setApplicant(applicant);
        newGift.setCreateTime(Calendar.getInstance().getTime());
        newGift.setEffectiveDate(DateUtils.truncate(newGift.getCreateTime(), Calendar.DAY_OF_MONTH));
        newGift.setExpireDate(DateUtils.truncate(newGift.getCreateTime(), Calendar.DAY_OF_MONTH));
        newGift.setGiftDisplay("");
        newGift.setStatus(GiftStatus.Enum.CREATED_1);
        return newGift;
    }
}

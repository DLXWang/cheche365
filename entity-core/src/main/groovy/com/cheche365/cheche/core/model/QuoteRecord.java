package com.cheche365.cheche.core.model;

import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.core.serializer.FormattedDoubleSerializer;
import com.cheche365.cheche.core.service.listener.EntityChangeListener;
import com.cheche365.cheche.core.util.BeanUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.BeanUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.cheche365.cheche.core.model.Channel.Enum.WAP_8;
import static com.cheche365.cheche.core.model.QuoteSource.Enum.API_QUOTE_SOURCES;

@Entity
@EntityListeners(EntityChangeListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuoteRecord implements Serializable {

    private static final long serialVersionUID = -8303387323794069347L;
    private Long id;

    @JsonIgnore
    private Quote quote;                        // 报价id
    private User applicant;                     // 申请人id
    private Auto auto;                          // 汽车
    private InsuranceCompany insuranceCompany;  // 保险公司
    private Area area;                          // 地区
    private InsurancePackage insurancePackage;  // 套餐类型

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    private Double premium = 0.0;               // 商业险保费
    private Double discount = 1.0;              // 折扣率
    private Date effectiveDate;                 // 生效日期
    private Date compulsoryEffectiveDate;       // 生效日期
    private Date expireDate;                    // 失效日期
    private Date compulsoryExpireDate;          // 失效日期
    private String originalPolicyNo;            // 商业险原保单号（续保时用）
    private String compulsoryOriginalPolicyNo;  // 交强险原保单号（续保时用）

    private Double compulsoryPremium = 0.0;     // 交通强制险
    private Double autoTax = 0.0;               // 车船使用税

    private Double thirdPartyPremium = 0.0;     // 三者险保费
    private Double thirdPartyAmount = 0.0;      // 三者险保额
    private Double thirdPartyIop = 0.0;         // 三者不计免赔

    private Double damagePremium = 0.0;         // 车损险保费
    private Double damageAmount = 0.0;          // 车损险保额
    private Double damageIop = 0.0;             // 车损不计免赔

    private Double theftPremium = 0.0;          // 盗抢险保费
    private Double theftAmount = 0.0;           // 盗抢险保额
    private Double theftIop = 0.0;              // 盗抢不计免赔

    private Double enginePremium = 0.0;         // 发动机特别险保费
    private Double engineIop = 0.0;             // 发动机特别险不计免赔

    private Double driverPremium = 0.0;         // 车上人员（司机）保费
    private Double driverAmount = 0.0;          // 车上人员（司机）保额
    private Double driverIop = 0.0;             // 车上人员（司机）不计免赔

    private Double passengerPremium = 0.0;      // 车上人员（乘客）保费
    private Double passengerAmount = 0.0;       // 车上人员（乘客）保额
    private Double passengerIop = 0.0;          // 车上人员（乘客）不计免赔
    private Integer passengerCount = 0;         // 车上人员（乘客）数量

    private Double spontaneousLossPremium = 0.0; // 自燃损失险保费
    private Double spontaneousLossAmount = 0.0; // 自燃损失险保额

    private Double glassPremium = 0.0;          // 玻璃单独破碎险保费

    private Double scratchPremium = 0.0;        // 划痕险保费
    private Double scratchAmount = 0.0;         //划痕险保额
    private Double scratchIop = 0.0;            // 划痕险不计免赔保费

    private Double iopTotal = 0.0;              // 不计免赔总额

    private Double spontaneousLossIop = 0.0;   //自燃险不计免赔

    private Double unableFindThirdPartyPremium = 0.0;       //无法找到第三方特约险保费
    private Double designatedRepairShopPremium = 0.0;       //指定专修厂险保费


    private QuoteSource type;                   // 报价类型（web parser，ruleengine）
    private Date createTime;
    private Date updateTime;
    private Date quoteValidTime;                // 报价有效时间
    private QuoteFlowType quoteFlowType;
    private boolean changedCar; //是否留牌换车
    private Channel channel; //渠道
    private String ownerMobile;//代理人填写的车主手机号
    private String quoteSourceId;

    // Transient Fields
    @JsonSerialize(using = FormattedDoubleSerializer.class)
    private Double paidAmount;
    private List<QuoteFieldStatus> quoteFieldStatus;
    private String quoteRecordKey;//报价cache key

    /**
     * 张华彬：zhanghb@cheche365.com
     * <p>
     * QR注解，此需求来自于下述背景：
     * 用户报价成功，但是由于未到续保窗口，
     * 所以QuoteFieldStatus中会提示续保窗口日期。
     * 前端有需求，在看到上述情况时直接在UI上就禁止客户提交订单。
     * <p>
     * 但是这个需求如果用errorCode来实现会早成API版本兼容方面的负担，
     * 而且貌似WebSocket对于版本控制比较棘手。
     * 最后的方案就是在QR中添加一个注解列表，用于描述那些无法以errorCode提现，
     * 但是却又是需要前端关注的一些元信息。
     * <p>
     * 格式为：
     * [
     * (Annotation Enum 1) : Payload1,
     * (Annotation Enum 2) : Payload2
     * ]
     * <p>
     * 注意：注解应该仅仅用于正常报价的情况（因为只有正常报价才能推回QR），只是这些正常情况中有一些特殊的元信息
     */
    private Map annotations;
    @JsonIgnore
    private List<Marketing> marketingList;
    private List<Map<String,Object>> discounts;//A端折扣、分享信息
    private String mobile;//m站报价用户输入的手机号用于记录日志

    @Transient
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Transient
    public List<Map<String, Object>> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<Map<String, Object>> discounts) {
        this.discounts = discounts;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "quote", foreignKey = @ForeignKey(name = "FK_QUOTE_RECORD_REF_QUOTE", foreignKeyDefinition = "FOREIGN KEY (quote) REFERENCES quote(id)"))
    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    @ManyToOne
    @JoinColumn(name = "applicant", foreignKey = @ForeignKey(name = "FK_QUOTE_RECORD_REF_USER", foreignKeyDefinition = "FOREIGN KEY (applicant) REFERENCES user(id)"))
    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    @ManyToOne
    @JoinColumn(name = "auto", foreignKey = @ForeignKey(name = "FK_QUOTE_RECORD_REF_AUTO", foreignKeyDefinition = "FOREIGN KEY (auto) REFERENCES auto(id)"))
    public Auto getAuto() {
        return auto;
    }

    public void setAuto(Auto auto) {
        this.auto = auto;
    }

    @ManyToOne
    @JoinColumn(name = "insuranceCompany", foreignKey = @ForeignKey(name = "FK_QUOTE_RECORD_REF_INSURANCE_COMPANY", foreignKeyDefinition = "FOREIGN KEY (insurance_company) REFERENCES insurance_company(id)"))
    public InsuranceCompany getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(InsuranceCompany insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    @ManyToOne
    @JoinColumn(name = "area", foreignKey = @ForeignKey(name = "FK_QUOTE_RECORD_REF_AREA", foreignKeyDefinition = "FOREIGN KEY (area) REFERENCES arae(id)"))
    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    @ManyToOne
    @JoinColumn(name = "insurancePackage", foreignKey = @ForeignKey(name = "FK_QUOTE_RECORD_REF_INSURANCE_PACKAGE", foreignKeyDefinition = "FOREIGN KEY (insurance_package) REFERENCES insurance_package(id)"))
    public InsurancePackage getInsurancePackage() {
        return insurancePackage;
    }

    public void setInsurancePackage(InsurancePackage insurancePackage) {
        this.insurancePackage = insurancePackage;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getPremium() {
        return premium;
    }

    public void setPremium(Double premium) {
        this.premium = premium;
    }

    @Column(columnDefinition = "DECIMAL(18,6)")
    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    @Column(columnDefinition = "DATE")
    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @Column(columnDefinition = "DATE")
    public Date getCompulsoryEffectiveDate() {
        return compulsoryEffectiveDate;
    }

    public void setCompulsoryEffectiveDate(Date compulsoryEffectiveDate) {
        this.compulsoryEffectiveDate = compulsoryEffectiveDate;
    }

    @Column(columnDefinition = "DATE")
    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    @Column(columnDefinition = "DATE")
    public Date getCompulsoryExpireDate() {
        return compulsoryExpireDate;
    }

    public void setCompulsoryExpireDate(Date compulsoryExpireDate) {
        this.compulsoryExpireDate = compulsoryExpireDate;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getOriginalPolicyNo() {
        return originalPolicyNo;
    }

    public void setOriginalPolicyNo(String originalPolicyNo) {
        this.originalPolicyNo = originalPolicyNo;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getCompulsoryOriginalPolicyNo() {
        return compulsoryOriginalPolicyNo;
    }

    public void setCompulsoryOriginalPolicyNo(String compulsoryOriginalPolicyNo) {
        this.compulsoryOriginalPolicyNo = compulsoryOriginalPolicyNo;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getCompulsoryPremium() {
        return compulsoryPremium;
    }

    public void setCompulsoryPremium(Double compulsoryPremium) {
        this.compulsoryPremium = compulsoryPremium;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getAutoTax() {
        return autoTax;
    }

    public void setAutoTax(Double autoTax) {
        this.autoTax = autoTax;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getThirdPartyPremium() {
        return thirdPartyPremium;
    }

    public void setThirdPartyPremium(Double thirdPartyPremium) {
        this.thirdPartyPremium = thirdPartyPremium;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getThirdPartyAmount() {
        return thirdPartyAmount;
    }

    public void setThirdPartyAmount(Double thirdPartyAmount) {
        this.thirdPartyAmount = thirdPartyAmount;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getDamagePremium() {
        return damagePremium;
    }

    public void setDamagePremium(Double damagePremium) {
        this.damagePremium = damagePremium;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getDamageAmount() {
        return damageAmount;
    }

    public void setDamageAmount(Double damageAmount) {
        this.damageAmount = damageAmount;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getTheftPremium() {
        return theftPremium;
    }

    public void setTheftPremium(Double theftPremium) {
        this.theftPremium = theftPremium;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getTheftAmount() {
        return theftAmount;
    }

    public void setTheftAmount(Double theftAmount) {
        this.theftAmount = theftAmount;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getEnginePremium() {
        return enginePremium;
    }

    public void setEnginePremium(Double enginePremium) {
        this.enginePremium = enginePremium;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getDriverPremium() {
        return driverPremium;
    }

    public void setDriverPremium(Double driverPremium) {
        this.driverPremium = driverPremium;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getDriverAmount() {
        return driverAmount;
    }

    public void setDriverAmount(Double driverAmount) {
        this.driverAmount = driverAmount;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getPassengerPremium() {
        return passengerPremium;
    }

    public void setPassengerPremium(Double passengerPremium) {
        this.passengerPremium = passengerPremium;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getPassengerAmount() {
        return passengerAmount;
    }

    public void setPassengerAmount(Double passengerAmount) {
        this.passengerAmount = passengerAmount;
    }

    @Column(columnDefinition = "TINYINT(3)")
    public Integer getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(Integer passengerCount) {
        this.passengerCount = passengerCount;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getSpontaneousLossPremium() {
        return spontaneousLossPremium;
    }

    public void setSpontaneousLossPremium(Double spontaneousLossPremium) {
        this.spontaneousLossPremium = spontaneousLossPremium;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getSpontaneousLossAmount() {
        return spontaneousLossAmount;
    }

    public void setSpontaneousLossAmount(Double spontaneousLossAmount) {
        this.spontaneousLossAmount = spontaneousLossAmount;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getGlassPremium() {
        return glassPremium;
    }

    public void setGlassPremium(Double glassPremium) {
        this.glassPremium = glassPremium;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getDamageIop() {
        return damageIop;
    }

    public void setDamageIop(Double damageIop) {
        this.damageIop = damageIop;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getThirdPartyIop() {
        return thirdPartyIop;
    }

    public void setThirdPartyIop(Double thirdPartyIop) {
        this.thirdPartyIop = thirdPartyIop;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getTheftIop() {
        return theftIop;
    }

    public void setTheftIop(Double theftIop) {
        this.theftIop = theftIop;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getEngineIop() {
        return engineIop;
    }

    public void setEngineIop(Double engineIop) {
        this.engineIop = engineIop;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getDriverIop() {
        return driverIop;
    }

    public void setDriverIop(Double driverIop) {
        this.driverIop = driverIop;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getPassengerIop() {
        return passengerIop;
    }

    public void setPassengerIop(Double passengerIop) {
        this.passengerIop = passengerIop;
    }

    @ManyToOne
    @JoinColumn(name = "type", foreignKey = @ForeignKey(name = "FK_QUOTE_RECORD_REF_QUOTE_SOURCE", foreignKeyDefinition = "FOREIGN KEY (type) REFERENCES quote_source(id)"))
    public QuoteSource getType() {
        return type;
    }

    public void setType(QuoteSource type) {
        this.type = type;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getScratchAmount() {
        return scratchAmount;
    }

    public void setScratchAmount(Double scratchAmount) {
        this.scratchAmount = scratchAmount;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getScratchPremium() {
        return scratchPremium;
    }

    public void setScratchPremium(Double scratchPremium) {
        this.scratchPremium = scratchPremium;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getScratchIop() {
        return scratchIop;
    }

    public void setScratchIop(Double scratchIop) {
        this.scratchIop = scratchIop;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }


    @Column(columnDefinition = "DATETIME")
    public Date getQuoteValidTime() {
        return quoteValidTime;
    }

    public void setQuoteValidTime(Date quoteValidTime) {
        this.quoteValidTime = quoteValidTime;
    }

    @ManyToOne
    @JoinColumn(name = "quoteFlowType", foreignKey = @ForeignKey(name = "FK_QUOTE_RECORD_REF_QUOTE_FLOW_TYPE", foreignKeyDefinition = "FOREIGN KEY (quote_flow_type) REFERENCES quote_flow_type(id)"))
    public QuoteFlowType getQuoteFlowType() {
        return quoteFlowType;
    }

    public void setQuoteFlowType(QuoteFlowType quoteFlowType) {
        this.quoteFlowType = quoteFlowType;
    }

    @Column(columnDefinition = "tinyint(1)")
    public boolean isChangedCar() {
        return changedCar;
    }

    public void setChangedCar(boolean changedCar) {
        this.changedCar = changedCar;
    }

    @ManyToOne
    @JoinColumn(name = "channel", foreignKey = @ForeignKey(name = "FK_QUOTE_RECORD_REF_CHANNEL", foreignKeyDefinition = "FOREIGN KEY (channel) REFERENCES channel(id)"))
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Transient
    public List<QuoteFieldStatus> getQuoteFieldStatus() {
        return quoteFieldStatus;
    }

    public void setQuoteFieldStatus(List<QuoteFieldStatus> quoteFieldStatus) {
        this.quoteFieldStatus = quoteFieldStatus;
    }


    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Transient
    public Double getTotalPremium() {
        return DoubleUtils.displayDoubleValue(doubleValue(this.premium) + doubleValue(this.compulsoryPremium) + doubleValue(this.autoTax));
    }

    public void setTotalPremium(Double totalPremium) {
    }

    @Transient
    public Double getPaidAmount() {
        return this.paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getIopTotal() {
        return iopTotal;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public void setIopTotal(Double iopTotal) {
        this.iopTotal = iopTotal;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getOwnerMobile() {
        return ownerMobile;
    }

    public void setOwnerMobile(String ownerMobile) {
        this.ownerMobile = ownerMobile;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getSpontaneousLossIop() {
        return spontaneousLossIop;
    }

    public void setSpontaneousLossIop(Double spontaneousLossIop) {
        this.spontaneousLossIop = spontaneousLossIop;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getUnableFindThirdPartyPremium() {
        return unableFindThirdPartyPremium;
    }

    public void setUnableFindThirdPartyPremium(Double unableFindThirdPartyPremium) {
        this.unableFindThirdPartyPremium = unableFindThirdPartyPremium;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getDesignatedRepairShopPremium() {
        return designatedRepairShopPremium;
    }

    public void setDesignatedRepairShopPremium(Double designatedRepairShopPremium) {
        this.designatedRepairShopPremium = designatedRepairShopPremium;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getQuoteSourceId() {
        return quoteSourceId;
    }

    public void setQuoteSourceId(String quoteSourceId) {
        this.quoteSourceId = quoteSourceId;
    }


    /**
     * 不计免赔分项求和
     *
     * @return
     */
    @Transient
    public Double sumIopItems() {
        return DoubleUtils.displayDoubleValue(
            doubleValue(this.getDamageIop())
                + doubleValue(this.getDriverIop())
                + doubleValue(this.getPassengerIop())
                + doubleValue(this.getThirdPartyIop())
                + doubleValue(this.getTheftIop())
                + doubleValue(this.getScratchIop())
                + doubleValue(this.getEngineIop())
                + doubleValue(this.getSpontaneousLossIop()));
    }

    @Transient
    public int getQuotedFieldsNum() {
        return (null == this.getInsurancePackage()) ? 0 : this.getInsurancePackage().countQuotedFields();
    }

    public Double calculateRebateablePremium() {
        return DoubleUtils.displayDoubleValue(getTotalPremium() - doubleValue(this.autoTax));
    }

    public Double calculatePaidAmount(Double reduceFee) {
        return reduceFee >= getTotalPremium() ? 0 : new BigDecimal(getTotalPremium() - reduceFee).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public Double calculatePremium() {
        this.setPremium(this.calculatePremiumNoSetPremium());
        return this.getPremium();
    }

    public Boolean apiQuote() {
        return API_QUOTE_SOURCES.contains(this.type);
    }

    public Double calculatePremiumNoSetPremium() {
        Double iopTotal = this.iopTotal;
        if (iopTotal == null || iopTotal <= 0) {
            iopTotal = doubleValue(this.getDamageIop())
                + doubleValue(this.getDriverIop())
                + doubleValue(this.getPassengerIop())
                + doubleValue(this.getThirdPartyIop())
                + doubleValue(this.getTheftIop())
                + doubleValue(this.getScratchIop())
                + doubleValue(this.getEngineIop()
                + doubleValue(this.getSpontaneousLossIop()));
        }
        return DoubleUtils.displayDoubleValue(doubleValue(this.getDamagePremium())
            + doubleValue(this.getDriverPremium())
            + doubleValue(this.getPassengerPremium())
            + doubleValue(this.getThirdPartyPremium())
            + doubleValue(this.getTheftPremium())
            + doubleValue(this.getScratchPremium())
            + doubleValue(this.getSpontaneousLossPremium())
            + doubleValue(this.getGlassPremium())
            + doubleValue(this.getEnginePremium())
            + doubleValue(this.getUnableFindThirdPartyPremium())
            + doubleValue(this.getDesignatedRepairShopPremium())
            + doubleValue(iopTotal));
    }

    private double doubleValue(Double doubleObj) {
        return DoubleUtils.doubleValue(doubleObj);
    }


    public void formatEmptyPremium() {
        if (null == this.getPremium()) {
            this.setPremium(0.0);               // 商业险保费
        }
        if (null == this.getCompulsoryPremium()) {
            this.setCompulsoryPremium(0.0);     // 交通强制险
        }
        if (null == this.getAutoTax()) {
            this.setAutoTax(0.0);               // 车船使用税
        }
        if (null == this.getThirdPartyPremium()) {
            this.setThirdPartyPremium(0.0);     // 三者险保费
        }
        if (null == this.getThirdPartyAmount())
            this.setThirdPartyAmount(0.0);      // 三者险保额
        if (null == this.getThirdPartyIop())
            this.setThirdPartyIop(0.0);         // 三者不计免赔

        if (null == this.getDamagePremium()) {
            this.setDamagePremium(0.0);         // 车损险保费
        }
        if (null == this.getDamageAmount())
            this.setDamageAmount(0.0);          // 车损险保额
        if (null == this.getDamageIop())
            this.setDamageIop(0.0);             // 车损不计免赔

        if (null == this.getTheftPremium())
            this.setTheftPremium(0.0);          // 盗抢险保费
        if (null == this.getTheftAmount())
            this.setTheftAmount(0.0);           // 盗抢险保额
        if (null == this.getTheftIop())
            this.setTheftIop(0.0);              // 盗抢不计免赔

        if (null == this.getEnginePremium())
            this.setEnginePremium(0.0);         // 发动机特别险保费
        if (null == this.getEngineIop())
            this.setEngineIop(0.0);             // 发动机特别险不计免赔

        if (null == this.getDriverPremium())
            this.setDriverPremium(0.0);         // 车上人员（司机）保费
        if (null == this.getDriverAmount())
            this.setDriverAmount(0.0);          // 车上人员（司机）保额
        if (null == this.getDriverIop())
            this.setDriverIop(0.0);             // 车上人员（司机）不计免赔

        if (null == this.getPassengerPremium())
            this.setPassengerPremium(0.0);      // 车上人员（乘客）保费
        if (null == this.getPassengerAmount())
            this.setPassengerAmount(0.0);       // 车上人员（乘客）保额
        if (null == this.getPassengerIop())
            this.setPassengerIop(0.0);          // 车上人员（乘客）不计免赔

        if (null == this.getSpontaneousLossPremium())
            this.setSpontaneousLossPremium(0.0);    // 自燃损失险保费
        if (null == this.getSpontaneousLossAmount())
            this.setSpontaneousLossAmount(0.0);     // 自燃损失险保额

        if (null == this.getGlassPremium())
            this.setGlassPremium(0.0);          // 玻璃单独破碎险保费

        if (null == this.getScratchPremium())
            this.setScratchPremium(0.0);        // 划痕险保额
        if (null == this.getScratchAmount())
            this.setScratchAmount(0.0);         // 划痕险保费
        if (null == this.getScratchIop())
            this.setScratchIop(0.0);            // 划痕险不计免赔保费

        if(null == this.getUnableFindThirdPartyPremium())
            this.setUnableFindThirdPartyPremium(0.0);


        if(null == this.getDesignatedRepairShopPremium())
            this.setDesignatedRepairShopPremium(0.0); // 指定专修厂险

        if(null == this.getSpontaneousLossIop())
            this.setSpontaneousLossIop(0.0);

        if(null == this.getPassengerCount()){
            this.setPassengerCount(0);          // 车上人员（乘客）数量
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public QuoteRecord clone() {
        QuoteRecord target = new QuoteRecord();
        BeanUtils.copyProperties(this, target, "id");

        if (null != this.getAuto()) {  //TODO need better way
            Auto targetAuto = new Auto();
            BeanUtils.copyProperties(this.getAuto(), targetAuto, "id");
            target.setAuto(targetAuto);
            if (null != this.getAuto().getAutoType()) {
                AutoType targetAutoType = new AutoType();
                BeanUtils.copyProperties(this.getAuto().getAutoType(), targetAutoType, "id");
                targetAuto.setAutoType(targetAutoType);
            }
        }

        if (null != this.getInsurancePackage()) {
            target.setInsurancePackage(this.getInsurancePackage().clone());
        }

        return target;

    }

    public static QuoteRecord copyProperties(QuoteRecord source, QuoteRecord target) {
        if (null == source) return null;

        target.setInsuranceCompany(source.getInsuranceCompany());
        target.setInsurancePackage(source.getInsurancePackage());
        target.setApplicant(source.getApplicant());
        target.setArea(source.getArea());
        target.setAuto(source.getAuto());
        target.setChannel(source.getChannel());
        target.setQuoteFlowType(source.getQuoteFlowType());

        target.setType(source.getType());
        target.setChangedCar(source.isChangedCar());

        String[] contains = new String[]{"id", "discount", "compulsoryPremium", "autoTax", "thirdPartyPremium", "thirdPartyAmount", "damagePremium", "damageAmount",

            "theftPremium", "theftAmount", "enginePremium", "driverPremium", "driverAmount", "passengerPremium", "passengerAmount", "spontaneousLossPremium",
            "spontaneousLossAmount", "glassPremium", "scratchPremium", "scratchAmount", "iopTotal", "premium","unableFindThirdPartyPremium", "designatedRepairShopPremium", "effectiveDate", "expireDate", "compulsoryEffectiveDate", "compulsoryExpireDate"};

        BeanUtil.copyPropertiesContain(source, target, contains);

        target.setUpdateTime(source.getUpdateTime());
        target.setCreateTime(source.getCreateTime());

        target.formatEmptyPremium();

        return target;
    }

    @Transient
    private Map total;

    @Transient
    public Map getTotal() {
        return total;
    }

    public void setTotal(Map total) {
        this.total = total;
    }

    public boolean autoTaxFree() {
        return (this.getAutoTax() == 0.00) && this.getInsurancePackage() != null && this.getInsurancePackage().isAutoTax();
    }

    @Transient
    public String getQuoteRecordKey() {
        return quoteRecordKey;
    }

    public void setQuoteRecordKey(String quoteRecordKey) {
        this.quoteRecordKey = quoteRecordKey;
    }

    @Transient
    public Map getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Map annotations) {
        this.annotations = annotations;
    }

    @Transient
    public List<Marketing> getMarketingList() {
        return marketingList;
    }

    public void setMarketingList(List<Marketing> marketingList) {
        this.marketingList = marketingList;
    }

    @PrePersist
    @PreUpdate
    void checkChannel() {
        if (null == channel) {
            channel = WAP_8;
        }
    }

}

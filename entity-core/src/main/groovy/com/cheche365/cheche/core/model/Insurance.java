package com.cheche365.cheche.core.model;

import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.core.repository.BaseEntity;
import com.cheche365.cheche.core.serializer.FormattedDoubleSerializer;
import com.cheche365.cheche.core.service.listener.EntityChangeListener;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * 商业险保单
 */
@Entity
@EntityListeners(EntityChangeListener.class)
@JsonIgnoreProperties(value = {"id", "quoteRecord", "insuranceAgent", "internalUser", "operator"}, ignoreUnknown = true)
public class Insurance extends BaseEntity {

    private static final long serialVersionUID = -3651916575234508704L;
    private User applicant;
    private Auto auto;
    private QuoteRecord quoteRecord;//报价记录
    private String proposalNo;//投保单号
    private String policyNo;//保单号
    private InsuranceCompany insuranceCompany;//保险公司
    private InsuranceAgent insuranceAgent;//保险代理机构
    private InsurancePackage insurancePackage;//保险套餐
    private Date effectiveDate;//生效日期
    private Date expireDate;//失效日期
    private Double premium;//商业险保费
    private String originalPolicyNo;//原保单号（续保时用）
    private OrderStatus insuranceStatus;//商业险状态

    private Double thirdPartyPremium;//三者险保费
    private Double thirdPartyAmount;//三者险保额
    private Double damagePremium;//车损险保费
    private Double damageAmount;// 车损险保额
    private Double theftPremium;//盗抢险保费
    private Double theftAmount;//盗抢险保额
    private Double enginePremium;//发动机特别险保费
    private Double engineAmount;//发动机特别险保额
    private Double driverPremium;//车上人员（司机）保费
    private Double driverAmount;//车上人员（司机）保额
    private Double passengerPremium;//车上人员（乘客）保费
    private Double passengerAmount;//车上人员（乘客）保额
    private Double passengerCount;//车上人员（乘客）数量
    private Double spontaneousLossPremium;//自燃损失险保费
    private Double spontaneousLossAmount;//自燃损失险保额
    private Double glassPremium;//玻璃单独破碎险保费
    private Double glassAmount;//玻璃单独破碎险保额
    private Double scratchAmount; //划痕险保额
    private Double scratchPremium; //划痕险保费
    private Double damageIop;//车损不计免赔
    private Double thirdPartyIop;//三者不计免赔
    private Double theftIop;//盗抢不计免赔
    private Double engineIop;//发动机特别险不计免赔
    private Double driverIop;//车上人员（司机）不计免赔
    private Double passengerIop;//车上人员（乘客）不计免赔
    private Double scratchIop; //划痕险不计免赔

    private String insuranceImage;//保单扫描文件地址
    private InternalUser operator;//操作员
    private Integer effectiveHour; //生效小时
    private Integer expireHour; //过期小时
    private String insuredName;//被保险人姓名	String	否	被保险人姓名（汉字）
    private Double discount;

    private String applicantName;//投保人姓名	String	否	投保人姓名（汉字）
    private String applicantIdNo;//投保人证件号码	String	否	身份证号
    private IdentityType applicantIdentityType;
    private String applicantMobile;//投保人手机	String	否
    private String applicantEmail;//投保人邮箱	String	否

    private String insuredIdNo;//被保险人证件号码	String	否	身份证号
    private IdentityType insuredIdentityType;
    private String insuredMobile;//被保险人手机	String	否
    private String insuredEmail;//被保险人邮箱	String	否

    private Institution institution;//出单机构

    private Double iopTotal = 0.0;//不计免赔总额

    private Double spontaneousLossIop = 0.0;   //自燃险不计免赔
    private Double unableFindThirdPartyPremium = 0.0;   //无法找到第三方特约险保费

    private Double proportion; //停驶返钱比例
    private Double designatedRepairShopPremium;//指定专修厂险
    private String specialAgreement;

    private static final String HAS_EXPIRED = "已过期";
    private static final String HAS_EFFECTIVE = "已生效";
    private static final String NOT_EFFECTIVE = "未生效";

    private Map annotations;


    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getIopTotal() {
        return iopTotal;
    }

    public void setIopTotal(Double iopTotal) {
        this.iopTotal = iopTotal;
    }

    @ManyToOne
    @JoinColumn(name = "applicant", foreignKey = @ForeignKey(name = "FK_INSURANCE_REF_USER", foreignKeyDefinition = "FOREIGN KEY (applicant) REFERENCES user(id)"))
    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    @ManyToOne
    @JoinColumn(name = "auto", foreignKey = @ForeignKey(name = "FK_INSURANCE_REF_AUTO", foreignKeyDefinition = "FOREIGN KEY (auto) REFERENCES auto(id)"))
    public Auto getAuto() {
        return auto;
    }

    public void setAuto(Auto auto) {
        this.auto = auto;
    }

    @ManyToOne
    @JoinColumn(name = "quoteRecord", foreignKey = @ForeignKey(name = "FK_INSURANCE_REF_QUOTE_RECORD", foreignKeyDefinition = "FOREIGN KEY (quote_record) REFERENCES quote_record(id)"))
    public QuoteRecord getQuoteRecord() {
        return quoteRecord;
    }

    public void setQuoteRecord(QuoteRecord quoteRecord) {
        this.quoteRecord = quoteRecord;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getProposalNo() {
        return proposalNo;
    }

    public void setProposalNo(String proposalNo) {
        this.proposalNo = proposalNo;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    @ManyToOne
    @JoinColumn(name = "insuranceCompany", foreignKey = @ForeignKey(name = "FK_INSURANCE_REF_INSURANCE_COMPANY", foreignKeyDefinition = "FOREIGN KEY (insurance_company) REFERENCES insurance_company(id)"))
    public InsuranceCompany getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(InsuranceCompany insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    @ManyToOne
    @JoinColumn(name = "insuranceAgent", foreignKey = @ForeignKey(name = "FK_INSURANCE_REF_INSURANCE_AGENT", foreignKeyDefinition = "FOREIGN KEY (insurance_agent) REFERENCES insurance_agent(id)"))
    public InsuranceAgent getInsuranceAgent() {
        return insuranceAgent;
    }

    public void setInsuranceAgent(InsuranceAgent insuranceAgent) {
        this.insuranceAgent = insuranceAgent;
    }

    @ManyToOne
    @JoinColumn(name = "insurancePackage", foreignKey = @ForeignKey(name = "FK_INSURANCE_REF_INSURANCE_PACKAGE", foreignKeyDefinition = "FOREIGN KEY (insurance_package) REFERENCES insurance_package(id)"))
    public InsurancePackage getInsurancePackage() {
        return insurancePackage;
    }

    public void setInsurancePackage(InsurancePackage insurancePackage) {
        this.insurancePackage = insurancePackage;
    }

    @Column(columnDefinition = "DATE")
    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @Column(columnDefinition = "DATE")
    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getPremium() {
        return premium;
    }

    public void setPremium(Double premium) {
        this.premium = premium;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getOriginalPolicyNo() {
        return originalPolicyNo;
    }

    public void setOriginalPolicyNo(String originalPolicyNo) {
        this.originalPolicyNo = originalPolicyNo;
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


    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getEngineAmount() {
        return engineAmount;
    }

    public void setEngineAmount(Double engineAmount) {
        this.engineAmount = engineAmount;
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

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(Double passengerCount) {
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

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getGlassAmount() {
        return glassAmount;
    }

    public void setGlassAmount(Double glassAmount) {
        this.glassAmount = glassAmount;
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

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getScratchIop() {
        return scratchIop;
    }

    public void setScratchIop(Double scratchIop) {
        this.scratchIop = scratchIop;
    }

    @Column(columnDefinition = "VARCHAR(200)")
    public String getInsuranceImage() {
        return insuranceImage;
    }

    public void setInsuranceImage(String insuranceImage) {
        this.insuranceImage = insuranceImage;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey = @ForeignKey(name = "FK_INSURANCE_REF_INTERNAL_USER", foreignKeyDefinition = "FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    public void setEffectiveHour(Integer effectiveHour) {
        this.effectiveHour = effectiveHour;
    }

    @Column(columnDefinition = "tinyint(3)")
    public Integer getEffectiveHour() {
        return effectiveHour;
    }

    @Column(columnDefinition = "tinyint(3)")
    public Integer getExpireHour() {
        return expireHour;
    }

    public void setExpireHour(Integer expireHour) {
        this.expireHour = expireHour;
    }

    public void setInsuredName(String insuredName) {
        this.insuredName = insuredName;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getInsuredName() {
        return insuredName;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,6)")
    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getApplicantIdNo() {
        return applicantIdNo;
    }

    public void setApplicantIdNo(String applicantIdNo) {
        this.applicantIdNo = applicantIdNo;
    }

    @ManyToOne
    @JoinColumn(name="applicantIdentityType", foreignKey = @ForeignKey(name="FK_INSURANCE_APPLICANT_REF_IDENTITY_TYPE", foreignKeyDefinition="FOREIGN KEY (applicant_identity_type) REFERENCES identity_type(id)"))
    public IdentityType getApplicantIdentityType() {
        return applicantIdentityType;
    }

    public void setApplicantIdentityType(IdentityType applicantIdentityType) {
        this.applicantIdentityType = applicantIdentityType;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getApplicantMobile() {
        return applicantMobile;
    }

    public void setApplicantMobile(String applicantMobile) {
        this.applicantMobile = applicantMobile;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getApplicantEmail() {
        return applicantEmail;
    }

    public void setApplicantEmail(String applicantEmail) {
        this.applicantEmail = applicantEmail;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getInsuredIdNo() {
        return insuredIdNo;
    }

    public void setInsuredIdNo(String insuredIdNo) {
        this.insuredIdNo = insuredIdNo;
    }

    @ManyToOne
    @JoinColumn(name="insuredIdentityType", foreignKey = @ForeignKey(name="FK_INSURANCE_INSURED_REF_IDENTITY_TYPE", foreignKeyDefinition="FOREIGN KEY (insured_identity_type) REFERENCES identity_type(id)"))
    public IdentityType getInsuredIdentityType() {
        return insuredIdentityType;
    }

    public void setInsuredIdentityType(IdentityType insuredIdentityType) {
        this.insuredIdentityType = insuredIdentityType;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getInsuredMobile() {
        return insuredMobile;
    }

    public void setInsuredMobile(String insuredMobile) {
        this.insuredMobile = insuredMobile;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getInsuredEmail() {
        return insuredEmail;
    }

    public void setInsuredEmail(String insuredEmail) {
        this.insuredEmail = insuredEmail;
    }

    @ManyToOne
    @JoinColumn(name = "institution", foreignKey = @ForeignKey(name = "FK_INSURANCE_REF_INSTITUTION", foreignKeyDefinition = "FOREIGN KEY (institution) REFERENCES institution(id)"))
    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }


    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getSpontaneousLossIop() {
        return spontaneousLossIop;
    }

    public void setSpontaneousLossIop(Double spontaneousLossIop) {
        this.spontaneousLossIop = spontaneousLossIop;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getUnableFindThirdPartyPremium() {
        return unableFindThirdPartyPremium;
    }

    public void setUnableFindThirdPartyPremium(Double unableFindThirdPartyPremium) {
        this.unableFindThirdPartyPremium = unableFindThirdPartyPremium;
    }

    @Column(columnDefinition = "DECIMAL(5,2)")
    public Double getProportion() {
        return proportion;
    }

    public void setProportion(Double proportion) {
        this.proportion = proportion;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getDesignatedRepairShopPremium() { return designatedRepairShopPremium; }

    public void setDesignatedRepairShopPremium(Double designatedRepairShopPremium) { this.designatedRepairShopPremium = designatedRepairShopPremium; }

    @ManyToOne
    @JoinColumn(name = "status", foreignKey = @ForeignKey(name = "FK_INSURANCE_REF_ORDER_STATUS", foreignKeyDefinition = "FOREIGN KEY (status) REFERENCES order_status(id)"))
    public OrderStatus getInsuranceStatus() {
        return insuranceStatus;
    }

    public void setInsuranceStatus(OrderStatus insuranceStatus) {
        this.insuranceStatus = insuranceStatus;
    }

    @Column(columnDefinition = "VARCHAR(1000)")
    public String getSpecialAgreement(){
        return specialAgreement;
    }

    public void setSpecialAgreement(String specialAgreement){
        this.specialAgreement = specialAgreement;
    }


    @Transient
    public boolean getValid() {
        boolean validity = false;
        if (effectiveDate != null && expireDate != null) {
            validity = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH).compareTo(expireDate) <= 0;
        }
        return validity;
    }

    @Transient
    public String getStatus() {
        return validDate(effectiveDate, expireDate);
    }

    @Transient
    public Map getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Map annotations) {
        this.annotations = annotations;
    }

    private String validDate(Date effectiveDate, Date expireDate) {
        String result = NOT_EFFECTIVE;
        if (effectiveDate != null && expireDate != null) {
            Date currentDate = new Date();
            Calendar startCalendar = Calendar.getInstance();
            Calendar endCalendar = Calendar.getInstance();
            Calendar currentCalendar = Calendar.getInstance();
            startCalendar.setTime(effectiveDate);
            endCalendar.setTime(expireDate);
            currentCalendar.setTime(currentDate);
            if (currentCalendar.compareTo(startCalendar) >= 0 && currentCalendar.compareTo(endCalendar) <= 0) {
                result = HAS_EFFECTIVE;
            } else if (currentCalendar.compareTo(endCalendar) > 0) {
                result = HAS_EXPIRED;
            } else if (currentCalendar.compareTo(startCalendar) < 0) {
                result = new SimpleDateFormat("yyyy-MM-dd生效").format(effectiveDate).toString();
            }
        }
        return result;
    }

    public static void setInsuranceReferences(PurchaseOrder purchaseOrder, QuoteRecord quoteRecord,
                                              InternalUser internalUser, Insurance insurance) {
        insurance.setApplicant(purchaseOrder.getApplicant());
        insurance.setAuto(purchaseOrder.getAuto());
        insurance.setQuoteRecord(quoteRecord);
        insurance.setInsurancePackage(quoteRecord.getInsurancePackage());
        insurance.setInsuranceCompany(quoteRecord.getInsuranceCompany());
        insurance.setOperator(internalUser);
    }

    public boolean finished() {
        return StringUtils.isNotBlank(this.getPolicyNo()) && this.getEffectiveDate() != null && this.getExpireDate() != null;
    }

    /**
     * 计算商业险总保额
     */
    public static Double calculateTotalAmount(Insurance insurance) {
        if (insurance == null) {
            return 0.0;
        }

        Double totalAmount = 0.0;
        //1.机动车第三者责任保险
        if (insurance.getThirdPartyPremium() != null && insurance.getThirdPartyPremium().compareTo(0.0) > 0) {
            totalAmount += DoubleUtils.doubleValue(insurance.getThirdPartyAmount());
        }
        //2.机动车损失险
        if (insurance.getDamagePremium() != null && insurance.getDamagePremium().compareTo(0.0) > 0) {
            totalAmount += DoubleUtils.doubleValue(insurance.getDamageAmount());
        }
        //3.玻璃单独破碎险
        if (insurance.getGlassPremium() != null && insurance.getGlassPremium().compareTo(0.0) > 0) {
            totalAmount += DoubleUtils.doubleValue(0.0);
        }
        //4.车身划痕损失险
        if (insurance.getScratchPremium() != null && insurance.getScratchPremium().compareTo(0.0) > 0) {
            totalAmount += DoubleUtils.doubleValue(insurance.getScratchAmount());
        }
        //5.车上人员责任险(司机)
        if (insurance.getDriverPremium() != null && insurance.getDriverPremium().compareTo(0.0) > 0) {
            totalAmount += DoubleUtils.doubleValue(insurance.getDriverAmount());
        }
        //6.车上人员责任险(乘客)
        if (insurance.getPassengerPremium() != null && insurance.getPassengerPremium().compareTo(0.0) > 0) {
            totalAmount += DoubleUtils.doubleValue(insurance.getPassengerAmount());
        }
        //7.机动车盗抢险
        if (insurance.getTheftPremium() != null && insurance.getTheftPremium().compareTo(0.0) > 0) {
            totalAmount += DoubleUtils.doubleValue(insurance.getTheftAmount());
        }
        //8.自燃损失险
        if (insurance.getSpontaneousLossPremium() != null && insurance.getSpontaneousLossPremium().compareTo(0.0) > 0) {
            totalAmount += DoubleUtils.doubleValue(insurance.getSpontaneousLossAmount());
        }
        //9.发动机特别损失险
        if (insurance.getEnginePremium() != null && insurance.getEnginePremium().compareTo(0.0) > 0) {
            totalAmount += DoubleUtils.doubleValue(0.0);
        }
        return DoubleUtils.displayDoubleValue(totalAmount);
    }

}

package com.cheche365.cheche.core.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ReflectionToStringBuilder
import org.springframework.beans.BeanUtils

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ForeignKey
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.PrePersist
import javax.persistence.PreUpdate
import javax.persistence.Transient
import java.beans.PropertyDescriptor
import java.lang.reflect.Method

import static com.cheche365.cheche.core.model.GlassType.Enum.DOMESTIC_1
import static com.cheche365.cheche.core.model.GlassType.Enum.IMPORT_2
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
class InsurancePackage implements Serializable {
    private static final long serialVersionUID = 1L

    private Long id;
    private boolean compulsory;//是否购买交通强制险
    private boolean autoTax;//是否购买车船使用税
    private Double thirdPartyAmount;//三者险金额：空表示不投保
    private boolean thirdPartyIop;//是否购买三者不计免赔
    private boolean damage;//是否购买车损险
    private boolean damageIop;//是否购买车损不计免赔
    private boolean theft;//是否购买盗抢险
    private boolean theftIop;//是否购买盗抢不计免赔
    private boolean engine;//是否购买发动机特别损失险
    private boolean engineIop;//是否购买发动机特别险不计免赔
    private boolean glass;//是否购买玻璃险
    private GlassType glassType;//玻璃类型:1:国产;2:进口
    private Double driverAmount;//车上人员（司机）责任险保额：null：不投保
    private boolean driverIop;//是否购买车上人员（司机）不计免赔
    private Double passengerAmount;//车上人员（乘客）责任险,null表示不投保
    private boolean passengerIop;//是否购买车上人员（乘客）不计免赔
    private boolean spontaneousLoss;//是否购买自燃险
    private Double scratchAmount;//划痕险保额,null表示不投保
    private boolean scratchIop;//是否购买划痕险不计免赔
    private boolean spontaneousLossIop;//自燃险不计免赔
    private boolean unableFindThirdParty;//无法找到第三方特约险
    private boolean designatedRepairShop//指定专修厂险
    private boolean iopTotal;//不计免赔radio
    private Long glassTypeId;


    public static PropertyDescriptor[] PROPERTIES = BeanUtils.getPropertyDescriptors(InsurancePackage.class);

    /**
     * 14 位boolean + 3位（三者险保额，单位万）
     * ＋2（车上人员司机责任险保额 单位万）＋2（车上人员乘客责任险保额单位万）
     * ＋2（划痕险保额， 单位千）＋1位玻璃类型（0:不投保，1:国产玻璃， 2：进口玻璃）
     * 14+3+2+2+2+1 = 24
     */
    private String uniqueString;

    @Transient
    Long getGlassTypeId() {
        return this.glassType == null ? 0 : this.glassType.getId();
    }

    void setGlassTypeId(Long glassTypeId) {
        this.glassTypeId = glassTypeId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "tinyint(1)")
    boolean isCompulsory() {
        return compulsory;
    }

    void setCompulsory(boolean compulsory) {
        this.compulsory = compulsory;
    }

    @Column(columnDefinition = "tinyint(1)")
    boolean isAutoTax() {
        return autoTax;
    }

    void setAutoTax(boolean autoTax) {
        this.autoTax = autoTax;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double getThirdPartyAmount() {
        return thirdPartyAmount;
    }

    void setThirdPartyAmount(Double thirdPartyAmount) {
        this.thirdPartyAmount = thirdPartyAmount;
    }

    @Column(columnDefinition = "tinyint(1)")
    boolean isThirdPartyIop() {
        return thirdPartyIop;
    }

    void setThirdPartyIop(boolean thirdPartyIop) {
        this.thirdPartyIop = thirdPartyIop;
    }

    @Column(columnDefinition = "tinyint(1)")
    boolean isDamage() {
        return damage;
    }

    void setDamage(boolean damage) {
        this.damage = damage;
    }

    @Column(columnDefinition = "tinyint(1)")
    boolean isDamageIop() {
        return damageIop;
    }

    void setDamageIop(boolean damageIop) {
        this.damageIop = damageIop;
    }

    @Column(columnDefinition = "tinyint(1)")
    boolean isTheft() {
        return theft;
    }

    void setTheft(boolean theft) {
        this.theft = theft;
    }

    @Column(columnDefinition = "tinyint(1)")
    boolean isTheftIop() {
        return theftIop;
    }

    void setTheftIop(boolean theftIop) {
        this.theftIop = theftIop;
    }

    @Column(columnDefinition = "tinyint(1)")
    boolean isEngine() {
        return engine;
    }

    void setEngine(boolean engine) {
        this.engine = engine;
    }

    @Column(columnDefinition = "tinyint(1)")
    boolean isEngineIop() {
        return engineIop;
    }

    void setEngineIop(boolean engineIop) {
        this.engineIop = engineIop;
    }

    @Column(columnDefinition = "tinyint(1)")
    boolean isGlass() {
        return glass;
    }

    void setGlass(boolean glass) {
        this.glass = glass;
    }

    @ManyToOne
    @JoinColumn(name="glassType", foreignKey=@ForeignKey(name="FK_INSURANCE_PACKAGE_REF_GLASS_TYPE", foreignKeyDefinition="FOREIGN KEY (glass_type) REFERENCES glass_type(id)"))
    GlassType getGlassType() {
        return glassType;
    }

    void setGlassType(GlassType glassType) {
        this.glassType = glassType;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double getDriverAmount() {
        return driverAmount;
    }

    void setDriverAmount(Double driverAmount) {
        this.driverAmount = driverAmount;
    }

    @Column(columnDefinition = "tinyint(1)")
    boolean isDriverIop() {
        return driverIop;
    }

    void setDriverIop(boolean driverIop) {
        this.driverIop = driverIop;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double getPassengerAmount() {
        return passengerAmount;
    }

    void setPassengerAmount(Double passengerAmount) {
        this.passengerAmount = passengerAmount;
    }

    @Column(columnDefinition = "tinyint(1)")
    boolean isPassengerIop() {
        return passengerIop;
    }

    void setPassengerIop(boolean passengerIop) {
        this.passengerIop = passengerIop;
    }

    @Column(columnDefinition = "tinyint(1)")
    boolean isSpontaneousLoss() {
        return spontaneousLoss;
    }

    void setSpontaneousLoss(boolean spontaneousLoss) {
        this.spontaneousLoss = spontaneousLoss;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double getScratchAmount() {
        return scratchAmount;
    }

    void setScratchAmount(Double scratchAmount) {
        this.scratchAmount = scratchAmount;
    }

    @Column(columnDefinition = "tinyint(1)")
    boolean isScratchIop() {
        return scratchIop;
    }

    void setScratchIop(boolean scratchIop) {
        this.scratchIop = scratchIop;
    }

    @Column(columnDefinition = "VARCHAR(30)")
    String getUniqueString() {
        return uniqueString;
    }

    void setUniqueString(String uniqueString) {
        this.uniqueString = uniqueString;
    }

    @Column(columnDefinition = "tinyint(1)")
    boolean isSpontaneousLossIop() {
        return spontaneousLossIop;
    }

    void setSpontaneousLossIop(boolean spontaneousLossIop) {
        this.spontaneousLossIop = spontaneousLossIop;
    }
    @Column(columnDefinition = "tinyint(1)")
    boolean isUnableFindThirdParty() {
        return unableFindThirdParty;
    }

    void setUnableFindThirdParty(boolean unableFindThirdParty) {
        this.unableFindThirdParty = unableFindThirdParty;
    }
    @Column(columnDefinition = "tinyint(1)")
    boolean isDesignatedRepairShop() {
        return designatedRepairShop
    }

    void setDesignatedRepairShop(boolean designatedRepairShop) {
        this.designatedRepairShop = designatedRepairShop
    }

    @Transient
    boolean isIopTotal() {
        toIopTotal()
        return iopTotal
    }

    void setIopTotal(boolean iopTotal) {
        this.iopTotal = iopTotal;
    }

    private BOOLEAN_TO_STRING = { booleanValue ->
        booleanValue ? '1' : '0'
    }

    private AMOUNT_TO_STRING_BASE = { amount, format = '%02d', amountValue ->
        String.format(format, ((amountValue ?: 0).longValue() / amount).intValue())
    }

    private AMOUNT_TO_STRING_THOUSAND = AMOUNT_TO_STRING_BASE.curry 1000
    private AMOUNT_TO_STRING_TEN_THOUSAND = AMOUNT_TO_STRING_BASE.curry 10000

    private GLASS_TYPE_TO_STRING = { glassType ->
        glassType?.id == DOMESTIC_1.id ? '1' : glassType?.id == IMPORT_2.id ? '2' : '0'
    }

    private STRING_TO_BOOLEAN = { str ->
        str == '1'
    }

    private STRING_TO_AMOUNT_BASE = { amountStr, str ->
        Double.valueOf(str + amountStr)
    }

    private STRING_TO_AMOUNT_THOUSAND = STRING_TO_AMOUNT_BASE.curry '000'
    private STRING_TO_AMOUNT_TEN_THOUSAND = STRING_TO_AMOUNT_BASE.curry '0000'

    private STRING_TO_GLASS_TYPE = { str ->
        str == '1' ? DOMESTIC_1 : str == '2' ? IMPORT_2 : null
    }

    private INSURANCE_CALCULATE_MAPPING = [
        compulsory          : [0, 1, BOOLEAN_TO_STRING, STRING_TO_BOOLEAN],
        autoTax             : [1, 1, BOOLEAN_TO_STRING, STRING_TO_BOOLEAN],
        thirdPartyAmount    : [2, 3, AMOUNT_TO_STRING_TEN_THOUSAND.curry('%03d'), STRING_TO_AMOUNT_TEN_THOUSAND],
        damage              : [5, 1, BOOLEAN_TO_STRING, STRING_TO_BOOLEAN],
        theft               : [6, 1, BOOLEAN_TO_STRING, STRING_TO_BOOLEAN],
        driverAmount        : [7, 2, AMOUNT_TO_STRING_TEN_THOUSAND, STRING_TO_AMOUNT_TEN_THOUSAND],
        passengerAmount     : [9, 2, AMOUNT_TO_STRING_TEN_THOUSAND, STRING_TO_AMOUNT_TEN_THOUSAND],
        engine              : [11, 1, BOOLEAN_TO_STRING, STRING_TO_BOOLEAN],
        scratchAmount       : [12, 2, AMOUNT_TO_STRING_THOUSAND, STRING_TO_AMOUNT_THOUSAND],
        spontaneousLoss     : [14, 1, BOOLEAN_TO_STRING, STRING_TO_BOOLEAN],
        glass               : [15, 1, BOOLEAN_TO_STRING, STRING_TO_BOOLEAN],
        glassType           : [16, 1, GLASS_TYPE_TO_STRING, STRING_TO_GLASS_TYPE],
        thirdPartyIop       : [17, 1, BOOLEAN_TO_STRING, STRING_TO_BOOLEAN],
        damageIop           : [18, 1, BOOLEAN_TO_STRING, STRING_TO_BOOLEAN],
        theftIop            : [19, 1, BOOLEAN_TO_STRING, STRING_TO_BOOLEAN],
        driverIop           : [20, 1, BOOLEAN_TO_STRING, STRING_TO_BOOLEAN],
        passengerIop        : [21, 1, BOOLEAN_TO_STRING, STRING_TO_BOOLEAN],
        engineIop           : [22, 1, BOOLEAN_TO_STRING, STRING_TO_BOOLEAN],
        scratchIop          : [23, 1, BOOLEAN_TO_STRING, STRING_TO_BOOLEAN],
        spontaneousLossIop  : [24, 1, BOOLEAN_TO_STRING, STRING_TO_BOOLEAN],
        unableFindThirdParty: [25, 1, BOOLEAN_TO_STRING, STRING_TO_BOOLEAN],
        designatedRepairShop: [26, 1, BOOLEAN_TO_STRING, STRING_TO_BOOLEAN]

    ]

    void calculateUniqueString() {
        // Don't modify this method except you are confident it will not cause any issue.

        this.uniqueString = generatedUniqueString()
    }

    /**
     * 根据险种生成uniqueString
     *
     * 16位 boolean + 3位（三者险保额，单位万）
     * ＋2（车上人员司机责任险保额 单位万）＋2（车上人员乘客责任险保额单位万）
     * ＋2（划痕险保额， 单位千）＋1位玻璃类型（0:不投保，1:国产玻璃， 2：进口玻璃）
     * 16+3+2+2+2+1 = 26
     */
    private def generatedUniqueString() {
        // Don't modify this method except you are confident it will not cause any issue.

        INSURANCE_CALCULATE_MAPPING.inject(new StringBuilder()) { sb, key,value->
            def (_0,_1,calculate,_3) = value
            sb.append calculate(this[key])
        }.toString()
    }

    /**
     * 根据uniqueString还原险种
     *
     * 16位 boolean + 3位（三者险保额，单位万）
     * ＋2（车上人员司机责任险保额 单位万）＋2（车上人员乘客责任险保额单位万）
     * ＋2（划痕险保额， 单位千）＋1位玻璃类型（0:不投保，1:国产玻璃， 2：进口玻璃）
     * 16+3+2+2+2+1 = 26
     */
    private void reducingInsurance() {
        log.info 'InsurancePackage修正险种信息：uniqueString：{}，错误险种信息：{}', this.uniqueString, generatedUniqueString()
        INSURANCE_CALCULATE_MAPPING.each { key, value ->
            def (index, length, _2, reducing) = value
            this[key] = reducing this.uniqueString.substring(index, index + length)
        }
    }

    int countQuotedFields(){
        int totalNum = 0;
        boolean iopCounted = false;

        for(PropertyDescriptor property : PROPERTIES) {
            Method readMethod = property.getReadMethod();
            try {
                Object value = readMethod.invoke(this);
                if(readMethod.getReturnType()==Double.class && (null!=value && ((Double)value)>0)){
                    totalNum++;
                } else if(readMethod.getReturnType()==boolean.class && (null!=value && (Boolean)value)){

                    if(readMethod.getName().toLowerCase().contains("iop")) {   //所有不计免赔算一个险种
                        if(!iopCounted){
                            totalNum++;
                            iopCounted = true;
                        }

                    } else {
                        totalNum++;
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return totalNum;

    }

    @Override
    String toString() {
        return new ReflectionToStringBuilder(this, SHORT_PREFIX_STYLE).setExcludeFieldNames("BOOLEAN_TO_STRING", "AMOUNT_TO_STRING_BASE", "AMOUNT_TO_STRING_THOUSAND", "AMOUNT_TO_STRING_TEN_THOUSAND", "GLASS_TYPE_TO_STRING", "STRING_TO_BOOLEAN", "STRING_TO_AMOUNT_BASE", "STRING_TO_AMOUNT_THOUSAND", "STRING_TO_AMOUNT_TEN_THOUSAND", "STRING_TO_GLASS_TYPE", "INSURANCE_CALCULATE_MAPPING").toString()
    }


    /**
     * this
     * @return
     */
    InsurancePackage clone(){
        InsurancePackage insurancePackage = new InsurancePackage();

        insurancePackage.setCompulsory(this.isCompulsory());
        insurancePackage.setAutoTax(this.isAutoTax());

        insurancePackage.setDamage(this.isDamage());
        insurancePackage.setDamageIop(this.isDamageIop());

        insurancePackage.setDriverAmount(this.getDriverAmount());
        insurancePackage.setDriverIop(this.isDriverIop());

        insurancePackage.setPassengerAmount(this.getPassengerAmount());
        insurancePackage.setPassengerIop(this.isPassengerIop());

        insurancePackage.setScratchAmount(this.getScratchAmount());
        insurancePackage.setScratchIop(this.isScratchIop());

        insurancePackage.setGlass(this.isGlass());
        insurancePackage.setGlassType(this.getGlassType());

        insurancePackage.setThirdPartyAmount(this.getThirdPartyAmount());
        insurancePackage.setThirdPartyIop(this.isThirdPartyIop());

        insurancePackage.setEngine(this.isEngine());
        insurancePackage.setEngineIop(this.isEngineIop());

        insurancePackage.setSpontaneousLoss(this.isSpontaneousLoss());

        insurancePackage.setTheft(this.isTheft());
        insurancePackage.setTheftIop(this.isTheftIop());

        insurancePackage.setSpontaneousLossIop(this.isSpontaneousLossIop());
        insurancePackage.setUnableFindThirdParty(this.isUnableFindThirdParty());
        insurancePackage.setDesignatedRepairShop(this.isDesignatedRepairShop());

        insurancePackage.calculateUniqueString();

        return insurancePackage;
    }

    void toIop(){
        toIopTotal();

        if(damage) {//是否购买车损险
            setDamageIop(iopTotal);
        }
        if(null!=driverAmount&&!driverAmount.equals(new Double(0.0))) {//车上人员（司机）
            setDriverIop(iopTotal);
        }
        if(engine) {//是否购买发动机特别损失险
            setEngineIop(iopTotal);
        }
        if(null!=passengerAmount&&!passengerAmount.equals(new Double(0.0))) {//车上人员（乘客）责任险,null表示不投保
            setPassengerIop(iopTotal);
        }
        if(null!=scratchAmount&&!scratchAmount.equals(new Double(0.0))) {//划痕险保额,null表示不投保
            setScratchIop(iopTotal);
        }
        if(spontaneousLoss) {//是否购买自燃险
            setSpontaneousLossIop(iopTotal);
        }
        if(theft) {//是否购买盗抢险
            setTheftIop(iopTotal);
        }
        if(null!=thirdPartyAmount&&!thirdPartyAmount.equals(new Double(0.0))) {//三者险金额：空表示不投保
            setThirdPartyIop(iopTotal);
        }
        setAutoTax(isCompulsory());

        if (GlassType.Enum.DOMESTIC_1.getId().equals(glassTypeId)) {
            setGlass(true);
            setGlassType(GlassType.Enum.DOMESTIC_1);
        } else if (GlassType.Enum.IMPORT_2.getId().equals(glassTypeId)) {
            setGlass(true);
            setGlassType(GlassType.Enum.IMPORT_2);
        } else if (this.getGlassType() == null || this.getGlassType().getId() == null || 0L == glassTypeId) {
            setGlass(false);
            setGlassType(null);
        }
    }

    void toIopTotal(){
        setIopTotal(damageIop||engineIop||passengerIop||scratchIop||spontaneousLossIop||theftIop||thirdPartyIop||iopTotal);
    }

    /**
     * 主险未选中，不计免赔不可选
     */
    void formatEmptyAmount() {
        this.setThirdPartyAmount(this.getThirdPartyAmount() == null ? 0.0 : this.getThirdPartyAmount())
        this.setThirdPartyIop(!this.getThirdPartyAmount().equals(0.0) && this.isThirdPartyIop())
        this.setDriverAmount(this.getDriverAmount() == null ? 0.0 : this.getDriverAmount())
        this.setDriverIop(!this.getDriverAmount().equals(0.0) && this.isDriverIop())
        this.setPassengerAmount(this.getPassengerAmount() == null ? 0.0 : this.getPassengerAmount())
        this.setPassengerIop(!this.getPassengerAmount().equals(0.0) && this.isPassengerIop())
        this.setScratchAmount(this.getScratchAmount() == null ? 0.0 : this.getScratchAmount())
        this.setScratchIop(!this.getScratchAmount().equals(0.0) && this.isScratchIop())
        this.setEngineIop(this.isEngine() && this.isEngineIop())
        this.setDamageIop(this.isDamage() && this.isDamageIop())
        this.setTheftIop(this.isTheft() && this.isTheftIop())
    }

//    新增修理厂险，更新uniqueString报错，注释掉uniqueString纠正功能
//    @PrePersist
//    @PreUpdate
    void checkUniqueString() {
        if (!this.uniqueString) {
            calculateUniqueString()
        } else if (generatedUniqueString() != this.uniqueString) {
            reducingInsurance()
        }
    }

    static List defaultPackages(){
        [
            new InsurancePackage(
                compulsory: true,
                autoTax: true,
                thirdPartyAmount: 500000d,
                thirdPartyIop: true,
                damage: true,
                damageIop: true),
            new InsurancePackage(),
            new InsurancePackage()
        ]
    }

    @Override
    boolean equals(Object o) {
        return o instanceof InsurancePackage && EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}

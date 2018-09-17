package com.cheche365.cheche.core.model;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.annotation.EqualsField;
import com.cheche365.cheche.core.annotation.EqualsFiledHandler;
import com.cheche365.cheche.core.repository.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotNull;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Auto extends BaseEntity {

    private static final long serialVersionUID = -4216678799169704678L;
    private long kilometerPerYear; //每年行驶里程
    @EqualsField
    private AutoType autoType;     //车型
    @NotNull
    @EqualsField
    private String licensePlateNo; //车牌号
    @EqualsField
    private String engineNo; //发动机号
    @EqualsField
    private String owner;//车主姓名
    @EqualsField
    private String vinNo;//车架号
    @EqualsField
    private Date enrollDate;//车辆注册日期
    @EqualsField
    private Area area;//汽车所在区域
    private String licenseType;
    @EqualsField
    private String licenseColorCode;
    @EqualsField
    private String identity; //证件id
    @EqualsField
    private IdentityType identityType;//证件类型
    @EqualsField
    private FuelType fuelType;//燃料类型
    @EqualsField
    private UseCharacter useCharacter;//车辆使用性质
    @EqualsField
    private String insuredIdNo; //去年被保险人证件号码
    private boolean disable;
    private boolean billRelated;

    @Transient
    private Set<UserAuto> userAutos;
    @Transient
    private String mobile; //workaround, just for signosig only

    public static PropertyDescriptor[] PROPERTIES = BeanUtils.getPropertyDescriptors(Auto.class);
    public static String NEW_CAR_PLATE_NO = "新车未上牌";

    @Transient
    @JsonIgnore
    public Integer getSeats() {
        return autoType != null ? autoType.getSeats() : null;
    }

    @Column
    public long getKilometerPerYear() {
        return kilometerPerYear;
    }

    public void setKilometerPerYear(long kilometerPerYear) {
        this.kilometerPerYear = kilometerPerYear;
    }

    @ManyToOne   //(cascade= CascadeType.PERSIST)
    @JoinColumn(name = "autoType", foreignKey = @ForeignKey(name = "FK_AUTO_REF_AUTO_TYPE", foreignKeyDefinition = "FOREIGN KEY (auto_type) REFERENCES auto_type(id)"))
    public AutoType getAutoType() {
        return autoType;
    }

    public void setAutoType(AutoType autoType) {
        this.autoType = autoType;
    }

    /**
     * 新车价格
     *
     * @return
     */
    @Transient
    @JsonIgnore
    public double getNewPrice() {
        return autoType == null ? 0.0 : autoType.getNewPrice();
    }

    /**
     * 当前价格（折旧价格）
     *
     * @return
     */
    @Transient
    @JsonIgnore
    public double getCurrentPrice() {
        return autoType == null ? 0.0 : autoType.getCurrentPrice();
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getLicensePlateNo() {
        return licensePlateNo;
    }

    public Auto setLicensePlateNo(String licensePlateNo) {
        this.licensePlateNo = licensePlateNo;
        return this;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getEngineNo() {
        return engineNo;
    }

    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getOwner() {
        return owner;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getIdentity() {
        return identity;
    }

    public Auto setIdentity(String identity) {
        this.identity = identity;
        return this;
    }

    @ManyToOne
    @JoinColumn(name = "identityType", foreignKey = @ForeignKey(name = "FK_AUTO_REF_IDENTITY_TYPE", foreignKeyDefinition = "FOREIGN KEY (identity_type) REFERENCES identity_type(id)"))
    public IdentityType getIdentityType() {
        return identityType;
    }

    public void setIdentityType(IdentityType identityType) {
        this.identityType = identityType;
    }

    @ManyToOne
    @JoinColumn(name = "fuel_type", foreignKey = @ForeignKey(name = "FK_AUTO_REF_FUEL_TYPE", foreignKeyDefinition = "FOREIGN KEY (`fuel_type`) REFERENCES `fuel_type` (`id`)"))
    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    @ManyToOne
    @JoinColumn(name = "use_character", foreignKey = @ForeignKey(name = "FK_AUTO_REF_USE_CHARACTER", foreignKeyDefinition = "FOREIGN KEY (`use_character`) REFERENCES `use_character` (`id`)"))
    public UseCharacter getUseCharacter() {
        return useCharacter;
    }

    public void setUseCharacter(UseCharacter useCharacter) {
        this.useCharacter = useCharacter;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getInsuredIdNo() {
        return insuredIdNo;
    }

    public void setInsuredIdNo(String insuredIdNo) {
        this.insuredIdNo = insuredIdNo;
    }


    public Auto setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getVinNo() {
        return vinNo;
    }

    public void setVinNo(String vinNo) {
        this.vinNo = vinNo;
    }

    @Column(columnDefinition = "DATE")
    public Date getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(Date enrollDate) {
        this.enrollDate = enrollDate;
    }

    @Transient
    @JsonIgnore
    public long getDrivingYears() {
        return DateUtils.getDrivingYears(this.enrollDate);
    }

    @ManyToOne
    @JoinColumn(name = "area", foreignKey = @ForeignKey(name = "FK_AUTO_REF_AREA", foreignKeyDefinition = "FOREIGN KEY (area) REFERENCES area(id)"))
    public Area getArea() {
        if (area != null && area.getShortCode() != null && area.getShortCode().contains(",") && licensePlateNo != null) {
            area = area.clone();
            area.setShortCode(licensePlateNo.substring(0, 2));
        }
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    @Column(columnDefinition = "VARCHAR(10)")
    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseColorCode(String licenseColorCode) {
        this.licenseColorCode = licenseColorCode;
    }

    @Column(columnDefinition = "VARCHAR(10)")
    public String getLicenseColorCode() {
        return licenseColorCode;
    }

    @Transient
    @OneToMany(mappedBy = "auto", fetch = FetchType.LAZY)
    public Set<UserAuto> getUserAutos() {
        return userAutos;
    }

    @Transient
    public void setUserAutos(Set<UserAuto> userAutos) {
        this.userAutos = userAutos;
    }

    @Column(columnDefinition = "tinyint(1)")
    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    @Transient
    public String getMobile() {
        return mobile;
    }

    @Transient
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Column(columnDefinition = "tinyint(1)")
    public boolean isBillRelated() {
        return billRelated;
    }

    public void setBillRelated(boolean billRelated) {
        this.billRelated = billRelated;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public List<Auto> generateAutoCopies(int size) {
        List<Auto> autos = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Auto target = this.clone();
            autos.add(target);
        }
        return autos;
    }

    public Auto clone() {
        Auto target = new Auto();
        BeanUtils.copyProperties(this, target);
        if (null != this.getAutoType()) {
            AutoType targetAutoType = new AutoType();
            BeanUtils.copyProperties(this.getAutoType(), targetAutoType);
            if (null != this.getAutoType().getSupplementInfo()) {
                SupplementInfo targetSupplementInfo = new SupplementInfo();
                BeanUtils.copyProperties(this.getAutoType().getSupplementInfo(), targetSupplementInfo);
                this.getAutoType().setSupplementInfo(targetSupplementInfo);
            }
            target.setAutoType(targetAutoType);
        }
        return target;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }

        if (obj == null && !getClass().equals(obj.getClass())) {
            return false;
        }

        return EqualsFiledHandler.objectEquals(obj, this);
    }
}

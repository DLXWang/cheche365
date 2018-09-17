package com.cheche365.cheche.core.model;

import com.cheche365.cheche.common.math.NumberUtils;
import com.cheche365.cheche.core.exception.BusinessException
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;

@Entity
@JsonIgnoreProperties(ignoreUnknown = false, value = ["createTime", "updateTime", "address"])
class Address extends DescribableEntity {
    private static final long serialVersionUID = 1L

    private User applicant;
    private Area area;
    private String street;
    private String district;
    private String districtName;
    private String city;
    private String cityName;
    private String province;
    private String provinceName;
    private String name;
    private String telephone;
    private String mobile;
    private String postalcode;

    private boolean disable;
    private boolean defaultAddress;

    @ManyToOne
    @JoinColumn(name = "applicant", foreignKey=@ForeignKey(name="FK_ADDRESS_REF_USER", foreignKeyDefinition="FOREIGN KEY (APPLICANT) REFERENCES USER(ID)"))
    User getApplicant() {
        return applicant;
    }

    void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    @ManyToOne
    @JoinColumn(name = "area", foreignKey=@ForeignKey(name="FK_ADDRESS_REF_AREA", foreignKeyDefinition="FOREIGN KEY (AREA) REFERENCES AREA(ID)"))
    Area getArea() {
        return area;
    }

    void setArea(Area area) {
        this.area = area;
    }

    @Column(columnDefinition = "VARCHAR(400)")
    String getStreet() {
        return street;
    }

    void setStreet(String street) {
        this.street = street;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    String getDistrict() {
        return district;
    }

    void setDistrict(String district) {
        this.district = district;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    String getCity() {
        return city;
    }

    void setCity(String city) {
        this.city = city;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    String getProvince() {
        return province;
    }

    void setProvince(String province) {
        this.province = province;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    String getTelephone() {
        return telephone;
    }

    void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    String getMobile() {
        return mobile;
    }

    void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Column(columnDefinition = "VARCHAR(6)")
    String getPostalcode() {
        return postalcode;
    }

    void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    @Column(columnDefinition = "tinyint(1)")
    boolean isDisable() {
        return disable;
    }

    void setDisable(boolean disable) {
        this.disable = disable;
    }

    @Column(columnDefinition = "tinyint(1)")
    boolean isDefaultAddress() {
        return defaultAddress;
    }
    void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    @Transient
    String getDistrictName() {
        if(NumberUtils.isDigits(this.getDistrict())){
            Area district = Area.Enum.getValueByCode(Long.valueOf(this.getDistrict()));
            return null==district ? this.getDistrict() : district.getName();
        }
        return this.getDistrict();
    }

    void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    @Transient
    String getCityName() {
        if (this.getCity() == null || !NumberUtils.isDigits(this.getCity())) {
            return this.getCity();
        }

        Area area = Area.Enum.getValueByCode(Long.valueOf(this.getCity()));

        if (area == null) {
            return this.getCity();
        }

        return area.getName();
    }

    void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Transient
    String getProvinceName() {
        if (this.getProvince() == null || !NumberUtils.isDigits(this.getProvince())) {
            return this.getProvince();
        }

        Area area = Area.Enum.getValueByCode(Long.valueOf(this.getProvince()));

        if (area == null) {
            return this.getProvince();
        }

        return area.getName();
    }

    void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    @Transient
    String getAddress(){
        StringBuilder address = new StringBuilder()
        ['provinceName', 'cityName', 'districtName', 'street'].each{this[it] ? address.append(this[it].trim()) : ""}
        address
    }

}

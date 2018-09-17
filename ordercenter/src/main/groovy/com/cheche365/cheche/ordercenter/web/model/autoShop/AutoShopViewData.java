package com.cheche365.cheche.ordercenter.web.model.autoShop;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by wangfei on 2015/6/3.
 */
public class AutoShopViewData {
    private Long id;//autoShop id
    @NotBlank
    private String name;//4s店名
    private String contactPerson;//联系人
    private String contactPersonMobile;//联系人手机
    private String contactPersonPhone_zone;//联系人电话区号
    private String contactPersonPhone_local;//联系人电话本地号
    private String contactPersonPhone;//联系人固话 由区号+本地号组合而来
    private String province;//省
    private String city;//市
    private String address;//详细地址
    private String fullAddress;//全地址 由省+市+详细地址组合而来
    @NotNull
    private Double longitude;//经度
    @NotNull
    private Double latitude;//纬度
    private List<AutoShopPictureViewData> pictures;//4s店照片列表
    private String insuranceCompanyIds;//支持保险公司列表
    private List<AutoShopServiceItemViewData> serviceItems;//服务列表
    private List<AutohomeBrandViewData> brands;//品牌列表
    private String comments;//备注
    private String createTime;//创建时间
    private String updateTime;//更新时间
    private String operator;//操作人

    private String deleteImgIds;//编辑操作时删除图片id集合
    private String strBrands;//品牌字符串

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AutohomeBrandViewData> getBrands() {
        return brands;
    }

    public void setBrands(List<AutohomeBrandViewData> brands) {
        this.brands = brands;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPersonMobile() {
        return contactPersonMobile;
    }

    public void setContactPersonMobile(String contactPersonMobile) {
        this.contactPersonMobile = contactPersonMobile;
    }

    public String getContactPersonPhone_zone() {
        return contactPersonPhone_zone;
    }

    public void setContactPersonPhone_zone(String contactPersonPhone_zone) {
        this.contactPersonPhone_zone = contactPersonPhone_zone;
    }

    public String getContactPersonPhone_local() {
        return contactPersonPhone_local;
    }

    public void setContactPersonPhone_local(String contactPersonPhone_local) {
        this.contactPersonPhone_local = contactPersonPhone_local;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public List<AutoShopPictureViewData> getPictures() {
        return pictures;
    }

    public void setPictures(List<AutoShopPictureViewData> pictures) {
        this.pictures = pictures;
    }

    public String getInsuranceCompanyIds() {
        return insuranceCompanyIds;
    }

    public void setInsuranceCompanyIds(String insuranceCompanyIds) {
        this.insuranceCompanyIds = insuranceCompanyIds;
    }

    public List<AutoShopServiceItemViewData> getServiceItems() {
        return serviceItems;
    }

    public void setServiceItems(List<AutoShopServiceItemViewData> serviceItems) {
        this.serviceItems = serviceItems;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getContactPersonPhone() {
        return contactPersonPhone;
    }

    public void setContactPersonPhone(String contactPersonPhone) {
        this.contactPersonPhone = contactPersonPhone;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getDeleteImgIds() {
        return deleteImgIds;
    }

    public void setDeleteImgIds(String deleteImgIds) {
        this.deleteImgIds = deleteImgIds;
    }

    public String getStrBrands() {
        return strBrands;
    }

    public void setStrBrands(String strBrands) {
        this.strBrands = strBrands;
    }

    public static String getFormatPhone(String contactPersonPhone_zone, String contactPersonPhone_local) {
        String formatPhone;
        if (StringUtils.isBlank(contactPersonPhone_zone) && StringUtils.isBlank(contactPersonPhone_local)) {
            formatPhone = "";
        } else if (StringUtils.isBlank(contactPersonPhone_zone) && !StringUtils.isBlank(contactPersonPhone_local)) {
            formatPhone = StringUtils.trimToEmpty(contactPersonPhone_local);
        } else if (!StringUtils.isBlank(contactPersonPhone_zone) && StringUtils.isBlank(contactPersonPhone_local)){
            formatPhone = StringUtils.trimToEmpty(contactPersonPhone_zone);
        } else {
            formatPhone = StringUtils.trimToEmpty(contactPersonPhone_zone) + "-" +
                StringUtils.trimToEmpty(contactPersonPhone_local);
        }
        return formatPhone;
    }

    public static String getDetailAddress(String province, String city, String address) {
        StringBuffer addressDetail = new StringBuffer("");

        if (StringUtils.isNotBlank(province) && !"请选择".equals(province))
            addressDetail.append(StringUtils.trimToEmpty(province));

        if (StringUtils.isNotBlank(city) && !"请选择".equals(city))
            addressDetail.append(StringUtils.trimToEmpty(city));

        if (StringUtils.isNotBlank(address))
            addressDetail.append(StringUtils.trimToEmpty(address));

        return addressDetail.toString();
    }


}

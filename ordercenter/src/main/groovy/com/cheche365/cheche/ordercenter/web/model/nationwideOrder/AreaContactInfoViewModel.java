package com.cheche365.cheche.ordercenter.web.model.nationwideOrder;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.AreaContactInfo;
import com.cheche365.cheche.core.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by xu.yelong on 2015/11/13.
 */
public class AreaContactInfoViewModel {
    private Long id;
    private String areaName;//城市
    private Long area;
    private String name;//负责人姓名
    private String mobile;//负责人手机号
    private String email;//负责人邮箱
    private String qq;//负责人QQ
    private String provinceName;//省份
    private Long province;
    private String cityName;//城市
    private Long city;
    private String districtName;//区县
    private Long district;
    private String street;//街道

    private String comment;//备注
    private String createTime;//创建时间
    private String updateTime;//修改时间
    private String operator;//操作人

    private String detailAddress;//详细地址 拼接

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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Long getArea() {
        return area;
    }

    public void setArea(Long area) {
        this.area = area;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public Long getProvince() {
        return province;
    }

    public void setProvince(Long province) {
        this.province = province;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Long getCity() {
        return city;
    }

    public void setCity(Long city) {
        this.city = city;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public Long getDistrict() {
        return district;
    }

    public void setDistrict(Long district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public String getDetailAddress() {
        return StringUtils.trimToEmpty(this.provinceName) +
            StringUtils.trimToEmpty(this.cityName) +
            StringUtils.trimToEmpty(this.districtName) +
            StringUtils.trimToEmpty(this.street);
    }

    public static AreaContactInfoViewModel createViewModel(AreaContactInfo areaContactInfo) {
        if (null == areaContactInfo) {
            return null;
        }
        AreaContactInfoViewModel model = new AreaContactInfoViewModel();
        String[] contains = new String[]{"id", "name", "email", "mobile", "qq", "street",
            "comment"};
        BeanUtil.copyPropertiesContain(areaContactInfo, model, contains);
        model.setAreaName(areaContactInfo.getArea() != null ? areaContactInfo.getArea().getName() : null);
        model.setArea(areaContactInfo.getArea() != null ? areaContactInfo.getArea().getId() : null);
        model.setProvinceName(areaContactInfo.getProvince() != null ? areaContactInfo.getProvince().getName() : null);
        model.setProvince(areaContactInfo.getProvince() != null ? areaContactInfo.getProvince().getId() : null);
        model.setCityName(areaContactInfo.getCity() != null ? areaContactInfo.getCity().getName() : null);
        model.setCity(areaContactInfo.getCity() != null ? areaContactInfo.getCity().getId() : null);
        model.setDistrictName(areaContactInfo.getDistrict() != null ? areaContactInfo.getDistrict().getName() : null);
        model.setDistrict(areaContactInfo.getDistrict() != null ? areaContactInfo.getDistrict().getId() : null);
        model.setOperator(areaContactInfo.getOperator() != null ? areaContactInfo.getOperator().getName() : null);
        model.setUpdateTime(areaContactInfo.getUpdateTime() != null ? DateUtils.getDateString(areaContactInfo.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN) : "");
        return model;
    }
}

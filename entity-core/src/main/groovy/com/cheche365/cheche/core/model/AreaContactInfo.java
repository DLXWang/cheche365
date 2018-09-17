package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.Date;

/**
 * 车车地区联系信息表
 * Created by sunhuazhong on 2015/11/13.
 */

@Entity
public class AreaContactInfo {
    private Long id;
    private Area area;//城市
    private String name;//负责人姓名
    private String mobile;//负责人手机号
    private String email;//负责人邮箱
    private String qq;//负责人QQ
    private Area province;//省份
    private Area city;//城市
    private Area district;//区县
    private String street;//街道
    private String comment;//备注
    private Date createTime;//创建时间
    private Date updateTime;//修改时间
    private InternalUser operator;//操作人

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "area", foreignKey=@ForeignKey(name="FK_AREA_CONTACT_INFO_REF_AREA", foreignKeyDefinition="FOREIGN KEY (area) REFERENCES area(id)"))
    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    @Column(columnDefinition = "varchar(20)")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(columnDefinition = "varchar(20)")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Column(columnDefinition = "varchar(45)")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(columnDefinition = "varchar(20)")
    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    @ManyToOne
    @JoinColumn(name = "province", foreignKey=@ForeignKey(name="FK_AREA_CONTACT_INFO_REF_PROVINCE", foreignKeyDefinition="FOREIGN KEY (province) REFERENCES area(id)"))
    public Area getProvince() {
        return province;
    }

    public void setProvince(Area province) {
        this.province = province;
    }

    @ManyToOne
    @JoinColumn(name = "city", foreignKey=@ForeignKey(name="FK_AREA_CONTACT_INFO_REF_CITY", foreignKeyDefinition="FOREIGN KEY (city) REFERENCES area(id)"))
    public Area getCity() {
        return city;
    }

    public void setCity(Area city) {
        this.city = city;
    }

    @ManyToOne
    @JoinColumn(name = "district", foreignKey=@ForeignKey(name="FK_AREA_CONTACT_INFO_REF_DISTRICT", foreignKeyDefinition="FOREIGN KEY (district) REFERENCES area(id)"))
    public Area getDistrict() {
        return district;
    }

    public void setDistrict(Area district) {
        this.district = district;
    }

    @Column(columnDefinition = "varchar(100)")
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Column(columnDefinition = "varchar(200)")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Column(columnDefinition = "datetime")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(columnDefinition = "datetime")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey=@ForeignKey(name="FK_AREA_CONTACT_INFO_REF_OPERATOR", foreignKeyDefinition="FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }
}

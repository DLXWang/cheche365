package com.cheche365.cheche.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by taguangyao on 2015/12/29.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = false, value = {"createTime", "updateTime"})
public class VehicleContact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "VARCHAR(50)")
    private String mobile;

    @ManyToOne
    @JoinColumn(name = "vehicleLicense", foreignKey=@ForeignKey(name="FK_VEHICLE_CONTACT_REF_VEHICLE_LICENSE", foreignKeyDefinition="FOREIGN KEY (vehicle_license) REFERENCES vehicle_license(id)"))
    private VehicleLicense vehicleLicense;

    @Column(columnDefinition = "DATETIME")
    private Date createTime;

    @Column(columnDefinition = "DATETIME")
    private Date updateTime;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VehicleLicense getVehicleLicense() {
        return vehicleLicense;
    }

    public void setVehicleLicense(VehicleLicense vehicleLicense) {
        this.vehicleLicense = vehicleLicense;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}

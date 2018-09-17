package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Device {

    private Long id;
    private User user;
    private String deviceUniqueId;//设备唯一标识
    private Long deviceType;//设备类型
    private String deviceInfo;//设备信息
    private String osInfo;//操作系统信息
    private String osVersion;//操作系统版本
    private String appInfo;//app信息
    private String appVersion;//app版本
    private String uuid;//IOS_4 UUID
    private Date createTime;
    private Date updateTime;
    private boolean disable;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "user", foreignKey = @ForeignKey(name = "FK_DEVICE_REF_USER", foreignKeyDefinition = "FOREIGN KEY (user) REFERENCES user(id)"))
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getDeviceUniqueId() {
        return deviceUniqueId;
    }

    public void setDeviceUniqueId(String deviceUniqueId) {
        this.deviceUniqueId = deviceUniqueId;
    }

    @Column(columnDefinition = "BIGINT(20)")
    public Long getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Long deviceType) {
        this.deviceType = deviceType;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getOsInfo() {
        return osInfo;
    }

    public void setOsInfo(String osInfo) {
        this.osInfo = osInfo;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(String appInfo) {
        this.appInfo = appInfo;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Column(columnDefinition = "TINYINT(1)")
    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

}

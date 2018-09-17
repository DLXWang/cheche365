package com.cheche365.cheche.core.model;

import javax.persistence.*;

/**
 * Created by chenxiaozhe on 15-10-10.
 */
@Entity
public class AppidMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "VARCHAR(1024)")
    private String appid;

    @Column(columnDefinition = "VARCHAR(1024)")
    private String mchid;

    @Column(columnDefinition = "VARCHAR(1024)")
    private String iosCerFile;

    @Column(columnDefinition = "VARCHAR(1024)")
    private String iosCerPwd;

    @Column(columnDefinition = "VARCHAR(512)")
    private String userAppid;

    @Column(columnDefinition = "VARCHAR(512)")
    private String devIosCerFile;

    @Column(columnDefinition = "VARCHAR(512)")
    private String devIosCerPwd;

    public String getIosCerPwd() {
        return iosCerPwd;
    }

    public void setIosCerPwd(String iosCerPwd) {
        this.iosCerPwd = iosCerPwd;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMchid() {
        return mchid;
    }

    public void setMchid(String mchid) {
        this.mchid = mchid;
    }

    public String getIosCerFile() {
        return iosCerFile;
    }

    public void setIosCerFile(String iosCerFile) {
        this.iosCerFile = iosCerFile;
    }

    public String getUserAppid() {
        return userAppid;
    }

    public void setUserAppid(String userAppid) {
        this.userAppid = userAppid;
    }

    public String getDevIosCerPwd() {
        return devIosCerPwd;
    }

    public void setDevIosCerPwd(String devIosCerPwd) {
        this.devIosCerPwd = devIosCerPwd;
    }

    public String getDevIosCerFile() {
        return devIosCerFile;
    }

    public void setDevIosCerFile(String devIosCerFile) {
        this.devIosCerFile = devIosCerFile;
    }
}

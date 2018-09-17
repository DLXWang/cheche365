package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhengwei on 6/10/15.
 */

@Entity
public class UUIDMapping {

    private String id;
    private String uuid;
    private String sessionId;
    private User user;
    private boolean logged;
    private Date updateDate;
    private Date loginDate;
    private Date logoutDate;
    private String clientType;
    private String version;
    private String appid;



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getUuid() {
        return uuid;
    }

    public UUIDMapping setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getSessionId() {
        return sessionId;
    }

    public UUIDMapping setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    @ManyToOne
    @JoinColumn(name="user", foreignKey=@ForeignKey(name="FK_UUID_MAPPING_REF_USER", foreignKeyDefinition="FOREIGN KEY (user) REFERENCES user(id)"))
    public User getUser() {
        return user;
    }

    public UUIDMapping setUser(User user) {
        this.user = user;
        return this;
    }

    @Column(columnDefinition = "tinyint(1)")
    public boolean isLogged() {
        return logged;
    }

    public UUIDMapping setLogged(boolean logged) {
        this.logged = logged;
        return this;
    }

    @Column(columnDefinition = "DATE")
    public Date getUpdateDate() {
        return updateDate;
    }

    public UUIDMapping setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
        return this;
    }

    @Column(columnDefinition = "DATE")
    public Date getLoginDate() {
        return loginDate;
    }

    public UUIDMapping setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
        return this;
    }

    @Column(columnDefinition = "DATE")
    public Date getLogoutDate() {
        return logoutDate;
    }

    public UUIDMapping setLogoutDate(Date logoutDate) {
        this.logoutDate = logoutDate;
        return this;
    }

    @Column(columnDefinition = "BIGINT(20)")
    public String getClientType() {
        return clientType;
    }

    public UUIDMapping setClientType(String clientType) {
        this.clientType = clientType;
        return this;
    }
    @Column(columnDefinition = "VARCHAR(512)")
    public String getVersion() {
        return version;
    }

    public UUIDMapping setVersion(String version) {
        this.version = version;
        return this;
    }

    @Column(columnDefinition = "VARCHAR(512)")
    public String getAppid() {
        return appid;
    }

    public UUIDMapping setAppid(String appid) {
        this.appid = appid;
        return this;
    }
}

package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by xu.yelong on 2016-05-16.
 */
@Entity
public class InternalUserLoginLog {
    private Long id;
    private Date LoginTime;
    private String ip;
    private InternalUser internalUser;
    private String platform;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "datetime")
    public Date getLoginTime() {
        return LoginTime;
    }

    public void setLoginTime(Date loginTime) {
        LoginTime = loginTime;
    }

    @Column(columnDefinition = "VARCHAR(20)")
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    @ManyToOne
    @JoinColumn(name = "internalUser", foreignKey=@ForeignKey(name="FK_INTERNAL_USER_LOGIN_LOG_REF_INTERNAL_USER", foreignKeyDefinition="FOREIGN KEY (internalUser) REFERENCES internal_user(id)"))
    public InternalUser getInternalUser() {
        return internalUser;
    }

    public void setInternalUser(InternalUser internalUser) {
        this.internalUser = internalUser;
    }

    @Column(columnDefinition = "VARCHAR(20)")
    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public static class PLATEFORM {
        public static final String ORDER_CENTER="orderCenter";
        public static final String ADMINISTER="administer";
        public static final String OPERATION_CENTER="operationCenter";
    }
}

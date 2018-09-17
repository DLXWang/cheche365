package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by wangfei on 2015/9/11.
 */
@Entity
public class UserLoginInfo {
    private Long id;
    private User user;
    private Date lastLoginTime;
    private String lastLoginIp;
    private Area area;
    private Channel channel;
    private MobileSourceType mobileSourceType;//如果车mobileType

    @ManyToOne
    @JoinColumn(name = "area", foreignKey=@ForeignKey(name="FK_USER_LOGIN_INFO_REF_AREA", foreignKeyDefinition="FOREIGN KEY (area) REFERENCES area(id)"))
    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "user", foreignKey=@ForeignKey(name="FK_USER_LOGIN_INFO_REF_USER", foreignKeyDefinition="FOREIGN KEY (user) REFERENCES user(id)"))
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    @ManyToOne
    @JoinColumn(name = "channel", foreignKey=@ForeignKey(name="FK_USER_LOGIN_INFO_REF_CHANNEL", foreignKeyDefinition="FOREIGN KEY (channel) REFERENCES channel(id)"))
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @ManyToOne
    public MobileSourceType getMobileSourceType() {
        return mobileSourceType;
    }

    public void setMobileSourceType(MobileSourceType mobileSourceType) {
        this.mobileSourceType = mobileSourceType;
    }
}

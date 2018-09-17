package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by chenxiaozhe on 15-10-10.
 */
@Entity
public class UserImg implements Serializable {


    private static final long serialVersionUID = 3651577125080568337L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "user", foreignKey = @ForeignKey(name = "FK_USER_IMG_REF_USER", foreignKeyDefinition = "FOREIGN KEY (user) REFERENCES user(id)"))
    private User user;

    @Column(columnDefinition = "DATETIME")
    private Date createTime;

    @Column(columnDefinition = "VARCHAR(2000)")
    private String comment;

    @Column(columnDefinition = "VARCHAR(100)")
    private String drivingLicensePath;//行驶证图片URL

    @Column(columnDefinition = "VARCHAR(100)")
    private String ownerIdentityPath;//车主身份证图片URL

    @Column(columnDefinition = "DATETIME")
    private Date updateTime;

    @ManyToOne
    @JoinColumn(name = "quote_entrance", foreignKey=@ForeignKey(name="FK_USER_IMG_REF_QUOTE_ENTRANCE", foreignKeyDefinition="FOREIGN KEY (quote_entrance) REFERENCES quote_entrance(id)"))
    private QuoteEntrance quoteEntrance;

    @ManyToOne
    @JoinColumn(name = "source_channel", foreignKey=@ForeignKey(name="FK_USER_IMG_REF_CHANNEL_IDX", foreignKeyDefinition="FOREIGN KEY (source_channel) REFERENCES channel(id)"))
    private Channel sourceChannel; //来源渠道，如微信，IOS_4，第三方

    public QuoteEntrance getQuoteEntrance() {
        return quoteEntrance;
    }

    public void setQuoteEntrance(QuoteEntrance quoteEntrance) {
        this.quoteEntrance = quoteEntrance;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getDrivingLicensePath() {
        return drivingLicensePath;
    }

    public void setDrivingLicensePath(String drivingLicensePath) {
        this.drivingLicensePath = drivingLicensePath;
    }

    public String getOwnerIdentityPath() {
        return ownerIdentityPath;
    }

    public void setOwnerIdentityPath(String ownerIdentityPath) {
        this.ownerIdentityPath = ownerIdentityPath;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Channel getSourceChannel() {
        return sourceChannel;
    }

    public void setSourceChannel(Channel sourceChannel) {
        this.sourceChannel = sourceChannel;
    }
}

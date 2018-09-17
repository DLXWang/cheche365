package com.cheche365.cheche.core.model;

import com.cheche365.cheche.common.util.DateUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by chenxiaozhe on 15-12-11.
 */
@Entity
public class PartnerUser extends DescribableEntity {

    private ApiPartner partner;
    private User user;
    private String partnerId;
    private String partnerMobile;
    private String notifyInfo;
    private String state;

    public PartnerUser() {

    }

    public PartnerUser(ApiPartner partner, User user, String partnerId) {
        this.partner = partner;
        this.user = user;
        this.partnerId = partnerId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getPartnerMobile() {
        return partnerMobile;
    }

    public void setPartnerMobile(String partnerMobile) {
        this.partnerMobile = partnerMobile;
    }

    @ManyToOne
    @JoinColumn(name = "partner", nullable = true, foreignKey = @ForeignKey(name = "fk_partner", foreignKeyDefinition = "FOREIGN KEY (partner) REFERENCES partner_third(id)"))
    public ApiPartner getPartner() {
        return partner;
    }


    public void setPartner(ApiPartner partner) {
        this.partner = partner;
    }

    @ManyToOne
    @JoinColumn(name = "user", nullable = true, foreignKey = @ForeignKey(name = "fk_user", foreignKeyDefinition = "FOREIGN KEY (user) REFERENCES user(id)"))
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public void appendDescription(String description) {
        setDescription(new StringBuilder(null == getDescription() ? "" : getDescription()).append(">> ").append(DateUtils.getCurrentDateString(DateUtils.DATE_LONGTIME24_PATTERN)).append(" ").append(description).toString());
    }
    @Column(name = "notify_info",columnDefinition = "VARCHAR(1024)")
    public String getNotifyInfo() {
        return notifyInfo;
    }

    public void setNotifyInfo(String notifyInfo) {
        this.notifyInfo = notifyInfo;
    }

    @Column(name = "state",columnDefinition = "VARCHAR(1024)")
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}

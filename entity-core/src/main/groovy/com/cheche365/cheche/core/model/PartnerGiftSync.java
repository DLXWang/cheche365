package com.cheche365.cheche.core.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by zhaozhong on 2016/3/23.
 */
@Entity
public class PartnerGiftSync extends DescribableEntity {

    private Gift gift;
    private Channel channel;
    private String serialNumber;
    private PartnerGiftStatus partnerGiftStatus;
    private String errorMsg;

    @ManyToOne
    @JoinColumn(columnDefinition = "gift", referencedColumnName = "id")
    public Gift getGift() {
        return gift;
    }

    public void setGift(Gift gift) {
        this.gift = gift;
    }

    @ManyToOne
    @JoinColumn(columnDefinition = "channel", referencedColumnName = "id")
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @ManyToOne
    @JoinColumn(columnDefinition = "partner_gift_status", referencedColumnName = "id")
    public PartnerGiftStatus getPartnerGiftStatus() {
        return partnerGiftStatus;
    }

    public void setPartnerGiftStatus(PartnerGiftStatus partnerGiftStatus) {
        this.partnerGiftStatus = partnerGiftStatus;
    }


    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "PartnerGiftSync{" +
            "gift=" + gift +
            ", channel=" + channel +
            ", serialNumber='" + serialNumber + '\'' +
            ", partnerGiftStatus=" + partnerGiftStatus +
            ", createTime=" + getCreateTime() +
            ", updateTime=" + getUpdateTime() +
            ", errorMsg='" + errorMsg + '\'' +
            '}';
    }
}

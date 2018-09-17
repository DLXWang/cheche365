package com.cheche365.cheche.scheduletask.model

import com.cheche365.cheche.core.model.MarketingSuccess
import com.cheche365.cheche.core.model.PartnerUser

/**
 * Created by zhangtc on 2018/1/29.
 */
class DongchediMarketingReportModel extends AttachmentData {

    private String parterId
    private String mobile
    private String licensePlateNo
    private String owner
    private String identity
    private String message
    private String status

    String getParterId() {
        return parterId
    }

    void setParterId(String parterId) {
        this.parterId = parterId
    }

    String getMobile() {
        return mobile
    }

    void setMobile(String mobile) {
        this.mobile = mobile
    }

    String getLicensePlateNo() {
        return licensePlateNo
    }

    void setLicensePlateNo(String licensePlateNo) {
        this.licensePlateNo = licensePlateNo
    }

    String getOwner() {
        return owner
    }

    void setOwner(String owner) {
        this.owner = owner
    }

    String getIdentity() {
        return identity
    }

    void setIdentity(String identity) {
        this.identity = identity
    }

    String getMessage() {
        return message
    }

    void setMessage(String message) {
        this.message = message
    }

    String getStatus() {
        return status
    }

    void setStatus(String status) {
        this.status = status
    }

    static DongchediMarketingReportModel create(MarketingSuccess marketingSuccess, PartnerUser partnerUser, Map map) {
        DongchediMarketingReportModel model = new DongchediMarketingReportModel()
        model.setParterId(partnerUser.getPartnerId())
        model.setMobile(marketingSuccess.getMobile())
        model.setLicensePlateNo(marketingSuccess.getLicensePlateNo())
        model.setOwner(marketingSuccess.getOwner())
        model.setIdentity(marketingSuccess.getIdentity())
        model.setStatus(String.valueOf(map.get('status')))
        model.setMessage(String.valueOf(map.get('message')))
        return model
    }
}

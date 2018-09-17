package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.util.RuntimeUtil
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.Canonical

import javax.persistence.Entity

import static com.cheche365.cheche.core.constants.TagConstants.PARTNER_TAGS

@Entity
@JsonIgnoreProperties(ignoreUnknown = true, value = ["appId", "appSecret"])
@Canonical
class ApiPartner extends DescribableEntity implements Serializable{
    private static final long serialVersionUID = 1L

    String code
    String appId
    String appSecret
    Long tag

    Boolean noParameter() {
        this.tag && (this.tag & PARTNER_TAGS.NO_PARAMETER.mask)
    }

    Boolean withUser() {
        this.tag && (this.tag & PARTNER_TAGS.WITH_USER.mask)
    }

    Boolean withAuto() {
        this.tag && (this.tag & PARTNER_TAGS.WITH_AUTO.mask)
    }

    Boolean withState() {
        this.tag && (this.tag & PARTNER_TAGS.WITH_STATE.mask)
    }

    Boolean singleCompany() {
        this.tag && (this.tag & PARTNER_TAGS.SINGLE_COMPANY.mask)
    }

    Boolean needDecrypt() {
        this.tag && (this.tag & PARTNER_TAGS.NEED_DECRYPT.mask)
    }

    Boolean needSyncOrder() {
        this.tag && (this.tag & PARTNER_TAGS.NEED_SYNC_ORDER.mask)
    }

    Boolean needEmailSync() {
        this.tag && (this.tag & PARTNER_TAGS.NEED_EMAIL_SYNC.mask)
    }

    Boolean supportAmend() {
        this.tag && (this.tag & PARTNER_TAGS.SUPPORT_AMEND.mask)
    }

    Boolean needSyncRebate() {
        this.tag && (this.tag & PARTNER_TAGS.NEED_SYNC_REBATE.mask)
    }

    Boolean toPhotoPage() {
        this.tag && (this.tag & PARTNER_TAGS.TO_PHOTO_PAGE.mask)
    }

    Boolean redirectWithUid() {
        this.tag && (this.tag & PARTNER_TAGS.REDIRECT_WITH_UID.mask)
    }

    Boolean createPartnerUser() {
        this.tag && (this.tag & PARTNER_TAGS.CREATE_PARTNER_USER.mask)
    }

    Boolean syncBodyNeedEncrypt() {
        this.tag && (this.tag & PARTNER_TAGS.SYNC_BODY_NEED_ENCRYPT.mask)
    }

    static explainTag(Long tag) {
        PARTNER_TAGS.collectEntries {
            [it.value.desc, (tag & it.value.mask) as boolean]
        }
    }

    static ApiPartner toApiPartner(Channel channel) {
        return channel.apiPartner
    }

    static ApiPartner findByCode(String code) {
        return Channel.allPartners().collect { it.apiPartner }.find { it.code == code }
    }

    static class Enum {

        public static ApiPartner BAIDU_PARTNER_2
        public static ApiPartner XIAOMI_PARTNER_12
        public static ApiPartner RRYP_PARTNER_14
        public static ApiPartner HAODAI_PARTNER_20
        public static ApiPartner DATEBAO_PARTNER_22
        public static ApiPartner EQIAO_PARTNER_32
        public static ApiPartner KUAIQIAN_PARTNER_33
        public static ApiPartner CEBBANK_PARTNER_38
        public static ApiPartner FFAN_PARTNER_40
        public static ApiPartner BDINSUR_PARTNER_50

        static {
            RuntimeUtil.loadEnum('apiPartnerRepository', ApiPartner, Enum)
        }
    }

    @Override
    boolean equals(Object obj) {
        if (null == obj) return false
        if (!(obj instanceof ApiPartner)) return false
        return this.id == ((ApiPartner) obj).id
    }

    @Override
    int hashCode() {
        return id.hashCode()
    }

}

package com.cheche365.cheche.core.model

import com.cheche365.cheche.common.util.DoubleUtils

import javax.persistence.*

/**
 * Created by yinJianBin on 2017/6/12.
 */
@Entity
class ChannelRebate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @ManyToOne
    @JoinColumn(name = "channel", foreignKey = @ForeignKey(name = "FK_CHANNEL_REBATE_REF_CHANNEL", foreignKeyDefinition = "FOREIGN KEY (channel) REFERENCES channel(id)"))
    Channel channel

    @ManyToOne
    @JoinColumn(name = "area", foreignKey = @ForeignKey(name = "FK_CHANNEL_REBATE_REF_AREA", foreignKeyDefinition = "FOREIGN KEY (`area`) REFERENCES `area` (`id`)"))
    Area area

    @ManyToOne
    @JoinColumn(name = "insuranceCompany", foreignKey = @ForeignKey(name = "FK_CHANNEL_REBATE_REF_INSURANCE_COMPANY", foreignKeyDefinition = "FOREIGN KEY (insurance_company) REFERENCES insurance_company(id)"))
    InsuranceCompany insuranceCompany

    @Column(columnDefinition = "DATETIME")
    Date effectiveDate

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double onlyCommercialRebate

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double onlyCompulsoryRebate

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double commercialRebate

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double compulsoryRebate

    @Column(columnDefinition = "DATETIME")
    Date readyEffectiveDate

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double onlyReadyCommercialRebate

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double onlyReadyCompulsoryRebate

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double readyCommercialRebate

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double readyCompulsoryRebate

    @Column(columnDefinition = "TINYINT(4)")
    Integer status

    @Column(columnDefinition = "DATETIME")
    Date createTime

    @Column(columnDefinition = "DATETIME")
    Date updateTime

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey = @ForeignKey(name = "FK_CHANNEL_REBATE_REF_INTERNAL_USER", foreignKeyDefinition = "FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    InternalUser operator

    @Column(columnDefinition = "text")
    String description

    static class Enum {
        public static Integer NOT_EFFECTIVE_0 = 0
        public static Integer EFFECTIVED_1 = 1
        public static Integer EXPIRED_2 = 2
        public static Map<Integer, String> STATUS_MAPPING = [:]
        static {
            STATUS_MAPPING.put(NOT_EFFECTIVE_0, '未生效')
            STATUS_MAPPING.put(EFFECTIVED_1, '生效中')
            STATUS_MAPPING.put(EXPIRED_2, '已失效')
        }
    }

    @Transient
    Double discountAmount(QuoteRecord quoteRecord) {
        Double discountAmount = 0d
        if (DoubleUtils.isNotZero(this.commercialRebate)) {
            discountAmount += quoteRecord.premium * this.commercialRebate / 100
        }
        if (DoubleUtils.isNotZero(this.compulsoryRebate)) {
            discountAmount += quoteRecord.compulsoryPremium * this.compulsoryRebate / 100
        }
        DoubleUtils.displayDoubleValue(discountAmount)
    }
}

package com.cheche365.cheche.core.model.agent

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.AutoType
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.InsurancePackage
import com.cheche365.cheche.core.model.SupplementInfo
import com.cheche365.cheche.core.model.User
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.Canonical

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ForeignKey
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
@JsonIgnoreProperties(value = ['id', 'channel', 'user', 'compulsoryStartDate', 'commercialStartDate'])
@Canonical(excludes = ['id'])
class QuoteHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @ManyToOne
    @JoinColumn(name = "user", foreignKey = @ForeignKey(name = "FK_QUOTE_HIS_REF_USER", foreignKeyDefinition = "FOREIGN KEY (`user`) REFERENCES `user` (`id`)"))
    User user

    @ManyToOne
    @JoinColumn(name = "auto", foreignKey = @ForeignKey(name = "FK_QUOTE_HIS_REF_AUTO", foreignKeyDefinition = "FOREIGN KEY (`auto`) REFERENCES `auto` (`id`)"))
    Auto auto

    @ManyToOne
    @JoinColumn(name = "area", foreignKey = @ForeignKey(name = "FK_QUOTE_HIS_REF_AREA", foreignKeyDefinition = "FOREIGN KEY (area) REFERENCES area(id)"))
    Area area

    @ManyToOne
    @JoinColumn(name = "channel", foreignKey = @ForeignKey(name = "FK_QUOTE_HIS_REF_CHANNEL", foreignKeyDefinition = "FOREIGN KEY (`channel`) REFERENCES `channel` (`id`)"))
    Channel channel

    @ManyToOne
    @JoinColumn(name = "insurancePackage", foreignKey = @ForeignKey(name = "FK_QUOTE_HIS_REF_PKG", foreignKeyDefinition = "FOREIGN KEY (`insurance_package`) REFERENCES `insurance_package` (`id`)"))
    InsurancePackage insurancePackage

    // 交强险生效日期
    @Column(name = "compulsoryStartDate",columnDefinition = "DATE")
    Date compulsoryStartDate

    // 商业险生效日期
    @Column(name= "commercialStartDate",columnDefinition = "DATE")
    Date commercialStartDate

    @Column(columnDefinition = "DATETIME")
    Date createTime

    @Column(columnDefinition = "DATETIME")
    Date updateTime

    Auto getAuto() {
        if (compulsoryStartDate || commercialStartDate) {
            auto.autoType = auto.autoType ?: new AutoType()
            auto.autoType.supplementInfo = auto.autoType.supplementInfo ?: new SupplementInfo()
            auto.autoType.supplementInfo.compulsoryStartDate = compulsoryStartDate
            auto.autoType.supplementInfo.commercialStartDate = commercialStartDate
        }
        return auto
    }
}

package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.serializer.FormattedDoubleSerializer
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonSerialize

import javax.persistence.*

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
class PingPlusAppSupport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @ManyToOne
    @JoinColumn(name = "ping_id", foreignKey=@ForeignKey(name="FK_AUTO_REF_PING_PLUS_APP", foreignKeyDefinition="FOREIGN KEY (ping_id) REFERENCES ping_plus_app(id)"))
    PingPlusApp pingPlusApp

    @ManyToOne
    @JoinColumn(name = "area", foreignKey=@ForeignKey(name="FK_AUTO_REF_AREA", foreignKeyDefinition="FOREIGN KEY (area) REFERENCES area(id)"))
    Area area

    @ManyToOne
    @JoinColumn(name = "insuranceCompany", foreignKey = @ForeignKey(name = "FK_QUOTE_RECORD_REF_INSURANCE_COMPANY", foreignKeyDefinition = "FOREIGN KEY (insurance_company) REFERENCES insurance_company(id)"))
    InsuranceCompany insuranceCompany

    @Column(columnDefinition = "VARCHAR(50)")
    String royaltyMode

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    Double royaltyValue

    @Column(columnDefinition = "VARCHAR(50)")
    String refundMode

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    Double refundValue
}

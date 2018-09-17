package com.cheche365.cheche.core.model.abao;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

@Entity
@Table(name = "INSURANCE_QUOTE_FIELD")
public class InsuranceQuoteField {

    private Long id; // 主键
    private InsuranceQuote insuranceQuote; // 报价id
    private InsuranceField insuranceField;// 险种id
    private BigDecimal amount;// 险种保额
    private BigDecimal premium;// 险种保费

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "INSURANCE_QUOTE", foreignKey = @ForeignKey(name = "FK_QUOTE_FIELD_REF_INSURANCE_QUOTE", foreignKeyDefinition = "FOREIGN KEY (`insurance_quote`) REFERENCES `insurance_quote` (`id`)"))
    public InsuranceQuote getInsuranceQuote() {
        return this.insuranceQuote;
    }

    public void setInsuranceQuote(InsuranceQuote insuranceQuote) {
        this.insuranceQuote = insuranceQuote;
    }

    @ManyToOne
    @JoinColumn(name = "INSURANCE_FIELD", foreignKey = @ForeignKey(name = "FK_QUOTE_FIELD_REF_INSURANCE_FIELD", foreignKeyDefinition = "FOREIGN KEY (`insurance_field`) REFERENCES `insurance_field` (`id`)"))
    public InsuranceField getInsuranceField() {
        return this.insuranceField;
    }

    public void setInsuranceField(InsuranceField insuranceField) {
        this.insuranceField = insuranceField;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public BigDecimal getPremium() {
        return this.premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}

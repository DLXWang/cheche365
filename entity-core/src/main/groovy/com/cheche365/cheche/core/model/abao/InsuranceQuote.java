package com.cheche365.cheche.core.model.abao;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.model.abao.InsuranceProduct;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;


@Entity
@Table(name = "INSURANCE_QUOTE")
public class InsuranceQuote extends DescribableEntity {

    private InsuranceProduct insuranceProduct; // 保险产品id
    private InsuranceCompany insuranceCompany; // 保险公司id
    private User user; // 用户id
    private BigDecimal premium; // 保费
    private BigDecimal paidPremium;// 实付保费
    private Channel channel;
    private QuoteSource type;// 报价类型

    //Transient
    private String quoteKey;

    @ManyToOne
    @JoinColumn(name = "INSURANCE_PRODUCT", foreignKey = @ForeignKey(name = "FK_INSURANCE_QUOTE_REF_INSURANCE_PRODUCT", foreignKeyDefinition = "FOREIGN KEY (`insurance_product`) REFERENCES `insurance_product` (`id`)"))
    public InsuranceProduct getInsuranceProduct() {
        return this.insuranceProduct;
    }

    public void setInsuranceProduct(InsuranceProduct insuranceProduct) {
        this.insuranceProduct = insuranceProduct;
    }

    @ManyToOne
    @JoinColumn(name = "INSURANCE_COMPANY", foreignKey = @ForeignKey(name = "FK_INSURANCE_QUOTE_REF_INSURANCE_COMPANY", foreignKeyDefinition = "FOREIGN KEY (insurance_company) REFERENCES insurance_company(id)"))
    public InsuranceCompany getInsuranceCompany() {
        return this.insuranceCompany;
    }

    public void setInsuranceCompany(InsuranceCompany insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    @ManyToOne
    @JoinColumn(name = "USER", foreignKey = @ForeignKey(name = "FK_INSURANCE_QUOTE_REF_USER", foreignKeyDefinition = "FOREIGN KEY (`user`) REFERENCES `user` (`id`)"))
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public BigDecimal getPremium() {
        return this.premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public BigDecimal getPaidPremium() {
        return this.paidPremium;
    }

    public void setPaidPremium(BigDecimal paidPremium) {
        this.paidPremium = paidPremium;
    }

    @ManyToOne
    @JoinColumn(name = "CHANNEL", foreignKey = @ForeignKey(name = "FK_INSURANCE_QUOTE_REF_CHANNEL", foreignKeyDefinition = "FOREIGN KEY (channel) REFERENCES channel(id)"))
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @ManyToOne
    @JoinColumn(name = "TYPE", foreignKey = @ForeignKey(name = "FK_INSURANCE_QUOTE_REF_QUOTE_SOURCE", foreignKeyDefinition = "FOREIGN KEY (type) REFERENCES quote_source(id)"))
    public QuoteSource getType() {
        return this.type;
    }

    public void setType(QuoteSource type) {
        this.type = type;
    }

    @Transient
    public String getQuoteKey() {
        return quoteKey;
    }

    public void setQuoteKey(String quoteKey) {
        this.quoteKey = quoteKey;
    }

    private List<InsuranceQuoteField> insuranceQuoteFields = new ArrayList<>();

    @Fetch(FetchMode.SELECT)
    @OneToMany(mappedBy = "insuranceQuote", fetch = FetchType.EAGER)
    public List<InsuranceQuoteField> getInsuranceQuoteFields() {
        return insuranceQuoteFields;
    }

    public void setInsuranceQuoteFields(List<InsuranceQuoteField> insuranceQuoteFields) {
        this.insuranceQuoteFields = insuranceQuoteFields;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, SHORT_PREFIX_STYLE);
    }

}

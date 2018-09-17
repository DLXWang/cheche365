package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.context.ApplicationContextHolder

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ForeignKey
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

import static com.cheche365.cheche.core.model.InsuranceCompany.allCompanies

/**
 * Created by mahong on 2016/1/25.
 * 配置优惠券/兑换码支持的保险公司
 * 缺省值：默认支持所有的保险公司
 */
@Entity
public class GiftInsuranceCompany {
    private Long id;
    private InsuranceCompany insuranceCompany;
    private SourceType sourceType;
    private Long source;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "insurance_company", foreignKey = @ForeignKey(name = "FK_GIFT_INSURANCE_COMPANY_REF_INSURANCE_COMPANY", foreignKeyDefinition = "FOREIGN KEY (insurance_company) REFERENCES insurance_company(id)"))
    public InsuranceCompany getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(InsuranceCompany insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    @ManyToOne
    @JoinColumn(name = "source_type", foreignKey = @ForeignKey(name = "FK_GIFT_INSURANCE_COMPANY_REF_SOURCE_TYPE", foreignKeyDefinition = "FOREIGN KEY (source_type) REFERENCES source_type(id)"))
    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    @Column(columnDefinition = "BIGINT(20)")
    public Long getSource() {
        return source;
    }

    public void setSource(Long source) {
        this.source = source;
    }

    static class Enum {
        public static Map<String, List> GIFT_INSURANCE_COMPANY_MAP

        static  {
            GIFT_INSURANCE_COMPANY_MAP = ApplicationContextHolder.getApplicationContext().getBean('giftInsuranceCompanyRepository')
                .findAll()
                .groupBy {GiftChannel.cacheKey(it.sourceType, it.source)}
                .collectEntries {[(it.key): it.value.insuranceCompany.id]}
        }

        static String giftInsuranceCompanyKey(SourceType sourceType, Long source) {
            "$sourceType.id:$source"
        }

        static boolean containsCompany(SourceType sourceType=SourceType.Enum.WECHATRED_2, Long source, InsuranceCompany company){
            findBySource(sourceType, source).contains(company.id)
        }

        static List<Long> findBySource(SourceType sourceType=SourceType.Enum.WECHATRED_2, Long source){
            GIFT_INSURANCE_COMPANY_MAP.get(GiftChannel.cacheKey(sourceType, source)) ?: allCompanies().id
        }

    }

}

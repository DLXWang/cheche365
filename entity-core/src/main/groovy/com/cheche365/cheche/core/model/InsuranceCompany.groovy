package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.service.IHttpServletRequestService
import com.cheche365.cheche.core.service.IResourceService
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.Canonical
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle

import javax.persistence.*

import static com.cheche365.cheche.core.constants.TagConstants.COMPANY_TAGS
import static com.cheche365.cheche.core.context.ApplicationContextHolder.getApplicationContext
import static com.cheche365.cheche.core.util.RuntimeUtil.loadEnum
import static java.nio.file.Files.exists
import static java.nio.file.Paths.get

@Entity
@Canonical
@JsonIgnoreProperties(ignoreUnknown = true, value = ['parent'])
class InsuranceCompany implements Serializable {
    private static final long serialVersionUID = 1L

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id
    @Column(columnDefinition = "VARCHAR(10)")
    String code
    @Column(columnDefinition = "VARCHAR(45)")
    String name
    @ManyToOne
    InsuranceCompany parent
    @Column(columnDefinition = "VARCHAR(50)")
    String logo
    @Column(columnDefinition = "VARCHAR(100)")
    String slogan
    @Column(columnDefinition = "VARCHAR(100)")
    String websiteUrl//保险公司官网url
    @Column(columnDefinition = "VARCHAR(45)")
    String mobile//保险公司电话
    @Column(columnDefinition = "BIGINT(20)")
    Long tag
    @Column(columnDefinition = "tinyint(10)")
    Integer rank //保险公司排列顺序
    @Column(columnDefinition = "VARCHAR(4500)")
    String description//保险公司描述
    @Column(columnDefinition = "VARCHAR(100)")
    String recommendReason

    @Transient
    Boolean recommend
    @Transient
    Boolean renewSupport
    @Transient
    String logoUrl
    @Transient
    List inforArrayList

    String getLogoUrl() {
        try {
            def resourceService = applicationContext.getBean(IResourceService.class)
            def request = applicationContext.containsBean('httpServletRequestService')
            def channel = null,version = null
            if(request){
                channel = applicationContext.getBean(IHttpServletRequestService.class)?.getChannel()
                version = applicationContext.getBean(IHttpServletRequestService.class)?.getVersion()
            }
            def logoRootPath = resourceService.getResourceAbsolutePath resourceService.properties.insuranceCompanyLogoPath
            def channelLogoPath = channel ? logoRootPath + channel.id + '_' + this.logo : null
            def logoPath = logoRootPath + this.logo
            if(channelLogoPath && exists(get(channelLogoPath))){
                logoPath = channelLogoPath
            }else if (this.logo && channel.isStandardAgent()&& version && version >'v1.8'){
                def nameSuffix = this.logo.split("\\.")
                logoPath = logoRootPath + nameSuffix[0] + "_a." + nameSuffix[1]
            }

            resourceService.absoluteUrl(channelLogoPath && exists(get(channelLogoPath)) ? channelLogoPath : logoPath, "")
        } catch (Exception e) {
            // do nothing
        }
    }

    List getInforArrayList() {
        if (description) {
            return Arrays.asList(description.split(","))
        }
        return null
    }

    Boolean getRecommend() {
        return this.tag && (this.tag & COMPANY_TAGS.RECOMMEND.mask)
    }

    Boolean getRenewSupport() {
        return this.tag && (this.tag & COMPANY_TAGS.RENEW_SUPPORT.mask)
    }

    Boolean disable() {
        return this.tag && (this.tag & COMPANY_TAGS.DISABLED.mask)
    }

    Boolean quote() {
        return this.tag && (this.tag & COMPANY_TAGS.QUOTE.mask)
    }

    Boolean display() {
        return this.tag && (this.tag & COMPANY_TAGS.DISPLAY.mask)
    }

    Boolean apiQuote() {
        return this.tag && (this.tag & COMPANY_TAGS.API_QUOTE.mask)
    }

    Boolean manualQuote() {
        return this.tag && (this.tag & COMPANY_TAGS.OC_MANUAL_SUPPORT.mask)
    }

    Boolean ocQuote() {
        quote() || display() || manualQuote()
    }

    Boolean fanhua() {
        return this.tag && (this.tag & COMPANY_TAGS.FANHUA_SUPPORT.mask)
    }

    Boolean reInsureSupport() {
        return this.tag && (this.tag & COMPANY_TAGS.RE_INSURE_SUPPORT.mask)
    }

    Boolean referenceBase() {
        return this.tag && (this.tag & COMPANY_TAGS.REFERENCE_BASE.mask)
    }

    Boolean circ() {//保监会官网
        return this.tag && (this.tag & COMPANY_TAGS.CIRC.mask)
    }

    Boolean useChecheCashier() {
        return this.tag && (this.tag & COMPANY_TAGS.USE_CASHIER.mask)
    }

    static explainTag(Long tag) {
        COMPANY_TAGS.collectEntries {
            [it.value.desc, (tag & it.value.mask) as boolean]
        }
    }

    static InsuranceCompany toInsuranceCompany(Long id) {
        allCompanies().find { it.id == id }
    }

    static List<InsuranceCompany> allCompanies() {
        ApplicationContextHolder.getApplicationContext().getBean('insuranceCompanyRepository').findAll()
                .findAll { !it.disable() }
    }

    static List<InsuranceCompany> referenceBaseCompanies() {
        allCompanies().findAll { it.referenceBase() }
    }

    static List<InsuranceCompany> apiQuoteCompanies() {
        allCompanies().findAll { it.apiQuote() }
    }

    static List<InsuranceCompany> quoteAndDisplayCompanies() {
        allCompanies().findAll { it.quote() && it.display() }
    }

    static List<InsuranceCompany> ocQuoteAndDisplayCompanies() {
        allCompanies().findAll { it.ocQuote() }
    }

    static List<InsuranceCompany> nonAutoCompanies() {
        findByTag(COMPANY_TAGS.NON_AUTO_SUPPORT.mask)
    }

    static List<InsuranceCompany> cricCompanies() {
        allCompanies().findAll { it.circ() }
    }


    static findByTag(mask) {
        ApplicationContextHolder.getApplicationContext().getBean('insuranceCompanyRepository').findAll()
                .findAll { it.tag && (it.tag & mask) }
    }

    static InsuranceCompany findByName(String name) {
        allCompanies().find { it.name == name }
    }

    static class Enum {
        //人保
        public static InsuranceCompany PICC_10000
        //阳光
        public static InsuranceCompany SINOSIG_15000
        //平安
        public static InsuranceCompany PINGAN_20000
        //太平洋
        public static InsuranceCompany CPIC_25000
        //人寿财险
        public static InsuranceCompany CHINALIFE_40000
        //中华联合
        public static InsuranceCompany CIC_45000
        //众安保险
        public static InsuranceCompany ZHONGAN_50000
        //安盛天平
        public static InsuranceCompany AXATP_55000
        //富德
        public static InsuranceCompany FUNDINS_60000
        //安心停驶返钱
        public static InsuranceCompany ANSWERN_65000
        //人保UK
        public static InsuranceCompany PICCUK_10500
        //平安UK
        public static InsuranceCompany PINGANUK_20500
        //中国太平
        public static InsuranceCompany TAIPING_30000
        //华安
        public static InsuranceCompany SINOSAFE_205000
        //亚太
        public static InsuranceCompany MINANINS_85000
        //永诚
        public static InsuranceCompany ALLTRUST_95000
        //紫金
        public static InsuranceCompany ZKING_165000
        //大地
        public static InsuranceCompany CCIC_240000
        //英大泰和财产保险
        public static InsuranceCompany YDTH_220000
        //泰康在线
        public static InsuranceCompany TK_80000
        //华农保险
        public static InsuranceCompany HN_150000
        //天安
        public static InsuranceCompany TIAN_100000
        //英大
        public static InsuranceCompany YDPIC_155000

        static {
            loadEnum('insuranceCompanyRepository', InsuranceCompany, Enum, { field -> [code: field.name.split('_')[0..-2].join('_')] }).findAll { ic ->
                !ic.disable()
            }
        }

    }

    @JsonIgnore
    Boolean isTaikang(){
        this == Enum.TK_80000
    }

    @JsonIgnore
    Boolean isZhongAn(){
        this == Enum.ZHONGAN_50000
    }

    @JsonIgnore
    Boolean isAnXin(){
        this == Enum.ANSWERN_65000
    }

    @JsonIgnore
    Boolean isHuaAn(){
        this == Enum.SINOSAFE_205000
    }

    @Override
    boolean equals(o) {
        o instanceof InsuranceCompany && new EqualsBuilder().append(id, o.id).append(code, o.code).isEquals()
    }

    @Override
    String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
    }

    @Override
    int hashCode() {
        new HashCodeBuilder().append(id).append(code).toHashCode()
    }


}

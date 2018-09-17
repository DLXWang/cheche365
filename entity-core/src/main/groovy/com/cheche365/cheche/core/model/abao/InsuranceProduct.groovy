package com.cheche365.cheche.core.model.abao

import com.cheche365.cheche.core.model.InsuranceCompany
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.springframework.beans.BeanUtils

import javax.persistence.*
import java.beans.PropertyDescriptor

/**
 * Created by wangjiahuan on 2016/11/16 0016.
 */
@Entity
class InsuranceProduct {

    private Long id;
    private String name;
    private InsuranceProductType productType;
    private InsuranceCompany insuranceCompany;
    private String webUrl;
    private String wapUrl;
    private String premium;
    private InsuranceProductStatus status;
    private Boolean hotSale;

    public static final PropertyDescriptor[] PROPERTIES = BeanUtils.getPropertyDescriptors(InsuranceProduct.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    @Column(columnDefinition = "VARCHAR(100)")
    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    @ManyToOne
    InsuranceProductType getProductType() {
        return productType
    }

    void setProductType(InsuranceProductType productType) {
        this.productType = productType
    }

    @ManyToOne
    @JoinColumn(name = "insuranceCompany", foreignKey = @ForeignKey(name = "FK_INSURANCE_PRODUCT_REF_INSURANCE_COMPANY", foreignKeyDefinition = "FOREIGN KEY (insurance_company) REFERENCES insurance_company(id)"))
    public InsuranceCompany getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(InsuranceCompany insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    @Column(columnDefinition = "VARCHAR(500)")
    String getWebUrl() {
        return webUrl
    }

    void setWebUrl(String webUrl) {
        this.webUrl = webUrl
    }

    @Column(columnDefinition = "VARCHAR(500)")
    String getWapUrl() {
        return wapUrl
    }

    void setWapUrl(String wapUrl) {
        this.wapUrl = wapUrl
    }

    @Column(columnDefinition = "VARCHAR(100)")
    String getPremium() {
        return premium
    }

    void setPremium(String premium) {
        this.premium = premium
    }

    @ManyToOne
    InsuranceProductStatus getStatus() {
        return status
    }

    void setStatus(InsuranceProductStatus status) {
        this.status = status
    }

    @Column(columnDefinition = "TINYINT")
    Boolean getHotSale() {
        return hotSale
    }

    void setHotSale(Boolean hotSale) {
        this.hotSale = hotSale
    }

    private List<InsuranceProductTag> insuranceProductTags = new ArrayList<InsuranceProductTag>();

    @Fetch(FetchMode.SELECT)
    @OneToMany(mappedBy = "insuranceProduct", fetch = FetchType.EAGER)
    List<InsuranceProductTag> getInsuranceProductTags() {
        return insuranceProductTags
    }

    void setInsuranceProductTags(List<InsuranceProductTag> insuranceProductTags) {
        this.insuranceProductTags = insuranceProductTags
    }

    private List<InsuranceProductDetail> insuranceProductDetails = new ArrayList<InsuranceProductDetail>();

    @Fetch(FetchMode.SELECT)
    @OneToMany(mappedBy = "insuranceProduct", fetch = FetchType.EAGER)
    List<InsuranceProductDetail> getInsuranceProductDetails() {
        return insuranceProductDetails.findAll { InsuranceProductDetail.Enum.allowDisplayDetail(it) }
    }

    void setInsuranceProductDetails(List<InsuranceProductDetail> insuranceProductDetails) {
        this.insuranceProductDetails = insuranceProductDetails
    }

}

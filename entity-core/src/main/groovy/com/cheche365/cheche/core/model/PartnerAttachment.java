package com.cheche365.cheche.core.model;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;

/**
 * 合作商附件
 * Created by sunhuazhong on 2015/8/25.
 */
@Entity
public class PartnerAttachment {
    private Long id;
    private Partner	partner;//合作商
    private String contractUrl;//合同文件URL
    private String contractName;//合同原始文件名称
    private String technicalDocumentUrl;//技术文档文件URL
    private String technicalDocumentName;//技术文档原始文件名称


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "partner", foreignKey=@ForeignKey(name="FK_PPARTNER_ATTACHMENT_REF_PARTNER", foreignKeyDefinition="FOREIGN KEY (partner) REFERENCES partner(id)"))
    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    @Column(columnDefinition = "VARCHAR(300)")
    public String getContractUrl() {
        return contractUrl;
    }

    public void setContractUrl(String contractUrl) {
        this.contractUrl = contractUrl;
    }

    @Column(columnDefinition = "VARCHAR(300)")
    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    @Column(columnDefinition = "VARCHAR(300)")
    public String getTechnicalDocumentUrl() {
        return technicalDocumentUrl;
    }

    public void setTechnicalDocumentUrl(String technicalDocumentUrl) {
        this.technicalDocumentUrl = technicalDocumentUrl;
    }

    @Column(columnDefinition = "VARCHAR(300)")
    public String getTechnicalDocumentName() {
        return technicalDocumentName;
    }

    public void setTechnicalDocumentName(String technicalDocumentName) {
        this.technicalDocumentName = technicalDocumentName;
    }

    public static boolean isEmptyEntity(PartnerAttachment attachment) {
        return (null == attachment) || (StringUtils.isBlank(attachment.getContractName()) && StringUtils.isBlank(attachment.getContractUrl())
            && StringUtils.isBlank(attachment.getTechnicalDocumentName()) && StringUtils.isBlank(attachment.getTechnicalDocumentUrl())
            && (attachment.getPartner() == null));
    }
}

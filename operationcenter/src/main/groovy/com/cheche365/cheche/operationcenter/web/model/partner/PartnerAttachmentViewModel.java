package com.cheche365.cheche.operationcenter.web.model.partner;

/**
 * 合作商上传文件对象
 * Created by sunhuazhong on 2015/8/26.
 */
public class PartnerAttachmentViewModel {
    private Long id;
    private Long partnerId;//合作商id
    private String contractUrl;//合同文件URL
    private String contractName;//合同原始文件名称
    private String technicalDocumentUrl;//技术文档文件URL
    private String technicalDocumentName;//技术文档原始文件名称

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public String getContractUrl() {
        return contractUrl;
    }

    public void setContractUrl(String contractUrl) {
        this.contractUrl = contractUrl;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getTechnicalDocumentUrl() {
        return technicalDocumentUrl;
    }

    public void setTechnicalDocumentUrl(String technicalDocumentUrl) {
        this.technicalDocumentUrl = technicalDocumentUrl;
    }

    public String getTechnicalDocumentName() {
        return technicalDocumentName;
    }

    public void setTechnicalDocumentName(String technicalDocumentName) {
        this.technicalDocumentName = technicalDocumentName;
    }
}

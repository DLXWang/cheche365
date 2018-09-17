package com.cheche365.cheche.scheduletask.model;

/**
 * Created by guoweifu on 2015/9/21.
 */
public class EmailConfig {
    private String title;
    private String template;
    private String[] tos;
    private String[] production_tos;
    private String[] ccs;
    private String[] production_ccs;
    private String attachment_template;
    private ExcelAttachmentConfig excelAttachmentConfig;
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String[] getTos() {
        return tos;
    }

    public void setTos(String[] tos) {
        this.tos = tos;
    }

    public String[] getProduction_tos() {
        return production_tos;
    }

    public void setProduction_tos(String[] production_tos) {
        this.production_tos = production_tos;
    }

    public String getAttachment_template() {
        return attachment_template;
    }

    public ExcelAttachmentConfig getExcelAttachmentConfig() {
        return excelAttachmentConfig;
    }

    public void setExcelAttachmentConfig(ExcelAttachmentConfig excelAttachmentConfig) {
        this.excelAttachmentConfig = excelAttachmentConfig;
    }

    public void setAttachment_template(String attachment_template) {
        this.attachment_template = attachment_template;
    }

    public String[] getCcs() {
        return ccs;
    }

    public void setCcs(String[] ccs) {
        this.ccs = ccs;
    }

    public String[] getProduction_ccs() {
        return production_ccs;
    }

    public void setProduction_ccs(String[] production_ccs) {
        this.production_ccs = production_ccs;
    }
}

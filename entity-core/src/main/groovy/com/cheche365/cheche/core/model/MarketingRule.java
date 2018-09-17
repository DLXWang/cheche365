package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.serializer.FormattedDateDeserializer;
import com.cheche365.cheche.core.serializer.FormattedDateSerializer;
import com.cheche365.cheche.core.util.RuntimeUtil;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.io.File;
import java.util.Date;

/**
 * Created by mahong on 2016/7/28.
 */
@Entity
public class MarketingRule {
    private Long id;
    private Marketing marketing;
    private String title;
    private String subTitle;
    private String description;
    private Area area;
    private Channel channel;
    private InsuranceCompany insuranceCompany;
    private ActivityType activityType;
    private MarketingRuleStatus status;
    private Date createTime;
    private Date update_time;
    private Date effectiveDate;
    private Date expireDate;
    private String topImage;
    private Integer version;
    private InternalUser operator;
    private String url;


    @Transient
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "marketing", foreignKey = @ForeignKey(name = "FK_MARKETING_RULE_REF_MARKETING", foreignKeyDefinition = "FOREIGN KEY (`marketing`) REFERENCES `marketing` (`id`)"))
    public Marketing getMarketing() {
        return marketing;
    }

    public void setMarketing(Marketing marketing) {
        this.marketing = marketing;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    @Column(columnDefinition = "VARCHAR(2000)")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne
    @JoinColumn(name = "area", foreignKey = @ForeignKey(name = "FK_MARKETING_RULE_REF_AREA", foreignKeyDefinition = "FOREIGN KEY (`area`) REFERENCES `area` (`id`)"))
    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    @ManyToOne
    @JoinColumn(name = "channel", foreignKey = @ForeignKey(name = "FK_MARKETING_RULE_REF_CHANNEL", foreignKeyDefinition = "FOREIGN KEY (`channel`) REFERENCES `channel` (`id`)"))
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @ManyToOne
    @JoinColumn(name = "insuranceCompany", foreignKey = @ForeignKey(name = "FK_MARKETING_RULE_REF_COMPANY", foreignKeyDefinition = "FOREIGN KEY (`insurance_company`) REFERENCES `insurance_company` (`id`)"))
    public InsuranceCompany getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(InsuranceCompany insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    @ManyToOne
    @JoinColumn(name = "activityType", foreignKey = @ForeignKey(name = "FK_MARKETING_RULE_REF_ACTIVITY_TYPE", foreignKeyDefinition = "FOREIGN KEY (`activity_type`) REFERENCES `activity_type` (`id`)"))
    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    @ManyToOne
    @JoinColumn(name = "status", foreignKey = @ForeignKey(name = "FK_MARKETING_RULE_REF_STATUS", foreignKeyDefinition = "FOREIGN KEY (`status`) REFERENCES `marketing_rule_status` (`id`)"))
    public MarketingRuleStatus getStatus() {
        return status;
    }

    public void setStatus(MarketingRuleStatus status) {
        this.status = status;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    @JsonSerialize(using = FormattedDateSerializer.class)
    @Column(columnDefinition = "DATE")
    public Date getEffectiveDate() {
        return effectiveDate;
    }

    @JsonDeserialize(using = FormattedDateDeserializer.class)
    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @JsonSerialize(using = FormattedDateSerializer.class)
    @Column(columnDefinition = "DATE")
    public Date getExpireDate() {
        return expireDate;
    }

    @JsonDeserialize(using = FormattedDateDeserializer.class)
    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    @Column(columnDefinition = "VARCHAR(200)")
    public String getTopImage() {
        return topImage;
    }

    public void setTopImage(String topImage) {
        this.topImage = topImage;
    }

    @Column(columnDefinition = "INT(11)")
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey = @ForeignKey(name = "FK_MARKETING_RULE_REF_INTERNAL_USER", foreignKeyDefinition = "FOREIGN KEY (`operator`) REFERENCES `internal_user` (`id`)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    @Override
    public boolean equals(Object obj) {

        MarketingRule marketingRule = (MarketingRule) obj;
        if (this.getMarketing().getCode().equals(marketingRule.getMarketing().getCode())
            && this.getInsuranceCompany().getId().equals(marketingRule.getInsuranceCompany().getId())
            && this.getArea().getId().equals(marketingRule.getArea().getId())
            && this.getChannel().getId().equals(marketingRule.getChannel().getId())) {
            return true;
        }
        return false;
    }

    public String genUniqueBannerUrl(String url, String target) {
        StringBuffer replacementUrl = new StringBuffer();
        replacementUrl.append(WebConstants.IMAGE_BANNER_PATH).append(this.getArea().getId()).append(File.separator)
            .append(this.getMarketing().getCode()).append("_").append(this.getArea().getId()).append("_")
            .append(this.getChannel().getId()).append("_").append(this.getInsuranceCompany().getId()).append("_")
            .append(this.getVersion()).append(".jpg");
        return url.replace(target, replacementUrl.toString());
    }

    public String genUniqueLinkUrl(String url) {
        StringBuffer linkUrl = new StringBuffer(url);
        linkUrl.append("?companyId=").append(this.getInsuranceCompany().getId())
            .append("&channel=").append(this.getChannel().getId())
            .append("&areaId=").append(this.getArea().getId())
            .append("&activityVersion=").append(this.getVersion())
            .append("&activity=").append(this.getMarketing().getCode())
            .append("&env=").append(RuntimeUtil.getEvnProfile());
        return linkUrl.toString();
    }

}

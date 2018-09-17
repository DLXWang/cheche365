package com.cheche365.cheche.manage.common.model;


import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.model.BusinessActivity;
import com.cheche365.cheche.core.model.InternalUser;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by yellow on 2017/6/6.
 * 运营活动监控链接
 */
@Entity
public class ActivityMonitorUrl {
    private Long id;
    private BusinessActivity businessActivity;
    private String scope;//岗位
    private String source;//渠道
    private String plan;//计划
    private String unit;//单元
    private String keyword;//关键词
    private String url; //生成链接
    private String tinyUrl;//短链接
    private InternalUser operator;//操作人
    private Date createTime;//创建时间
    private Boolean enable;//启用状态
    private Boolean quote;//是否报价

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "businessActivity", foreignKey=@ForeignKey(name="FK_ACTIVITY_MONITOR_URL_REF_BUSINESS_ACTIVITY", foreignKeyDefinition="FOREIGN KEY (business_activity) REFERENCES business_activity(id)"))
    public BusinessActivity getBusinessActivity() {
        return businessActivity;
    }

    public void setBusinessActivity(BusinessActivity businessActivity) {
        this.businessActivity = businessActivity;
    }

    @Column(columnDefinition = "VARCHAR(20)")
    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Column(columnDefinition = "VARCHAR(20)")
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Column(columnDefinition = "VARCHAR(20)")
    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    @Column(columnDefinition = "VARCHAR(20)")
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Column(columnDefinition = "VARCHAR(20)")
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Column(columnDefinition = "VARCHAR(255)")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Column(columnDefinition = "VARCHAR(255)")
    public String getTinyUrl() {
        return tinyUrl;
    }

    public void setTinyUrl(String tinyUrl) {
        this.tinyUrl = tinyUrl;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey=@ForeignKey(name="FK_ACTIVITY_MONITOR_URL_REF_INTERNAL_USER", foreignKeyDefinition="FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Boolean getQuote() {
        return quote;
    }

    public void setQuote(Boolean quote) {
        this.quote = quote;
    }
}

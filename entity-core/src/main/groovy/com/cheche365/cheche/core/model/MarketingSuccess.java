package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.serializer.FormattedDoubleSerializer;
import com.cheche365.cheche.core.service.listener.EntityChangeListener;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.util.Date;

@Entity
@EntityListeners(EntityChangeListener.class)
@JsonIgnoreProperties(ignoreUnknown = false)

public class MarketingSuccess extends DescribableEntity {

    private static final long serialVersionUID = 8389180294239158213L;
    private Marketing marketing;//marketing的id
    private Long userId;//参加活动的登陆用户,未登陆为空.
    private String mobile;//参加活动的手机号
    private Date effectDate;//开始生效时间，领券的时间，参加活动时间
    private Date failureDate;//失效时间，根据marketing的failureDateClass和failureDateClassParam计算出来的
    private Double amount;//计算出来的，根据amountClass和amountClassParam计算出来的
    private String owner;
    private String licensePlateNo;
    private String sourceChannel;//渠道来源，m(M站)，IOSAPP，wx(微信)，。。。
    private boolean synced;
    private Channel channel; //渠道
    private String detailTableName;//外部关联表
    private Long detail;//外部关联主键
    private Integer status;
    private BusinessActivity businessActivity;
    private String identity;
    private Area area;

    @Column(columnDefinition = "VARCHAR(45)")
    public String getSourceChannel() {
        return sourceChannel;
    }

    public void setSourceChannel(String sourceChannel) {
        this.sourceChannel = sourceChannel;
    }




    @ManyToOne
    @JoinColumn(name = "marketing_id", foreignKey = @ForeignKey(name = "FK_MARKETING_SUCCESS_REF_MARKETING", foreignKeyDefinition = "FOREIGN KEY (marketing_id) REFERENCES marketing(id)"))
    public Marketing getMarketing() {return marketing;}

    public void setMarketing(Marketing marketing) {this.marketing = marketing; }

    @Column(columnDefinition = "int(10)")
    public Long getUserId() {return userId;}
    public void setUserId(Long userId) {this.userId = userId;}

    @Column(columnDefinition = "VARCHAR(45)")
    public String getMobile() {return mobile;}
    public void setMobile(String mobile) {this.mobile = mobile;}

    @Column(columnDefinition = "DATETIME")
    public Date getEffectDate() {return effectDate;}
    public void setEffectDate(Date effectDate) {this.effectDate = effectDate;}

    @Column(columnDefinition = "DATETIME")
    public Date getFailureDate() {
        return failureDate;
    }

    public void setFailureDate(Date failureDate) {
        this.failureDate = failureDate;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "decimal(18,2)")
    public Double getAmount() {return amount;}
    public void setAmount(Double amount) {this.amount = amount;}

    @Column(columnDefinition = "tinyint(1)")
    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    @ManyToOne
    @JoinColumn(name = "channel", foreignKey = @ForeignKey(name = "FK_MARKETING_SUCCESS_REF_CHANNEL", foreignKeyDefinition = "FOREIGN KEY (channel) REFERENCES channel(id)"))
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getDetailTableName() {
        return detailTableName;
    }

    public void setDetailTableName(String detailTableName) {
        this.detailTableName = detailTableName;
    }

    @Column(columnDefinition = "BIGINT(20)")
    public Long getDetail() {
        return detail;
    }


    public void setDetail(Long detail) {
        this.detail = detail;
    }

    @Column(columnDefinition = "tinyint(4)")
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getLicensePlateNo() {
        return licensePlateNo;
    }

    public void setLicensePlateNo(String licensePlateNo) {
        this.licensePlateNo = licensePlateNo;
    }

    @ManyToOne
    @JoinColumn(name = "business_activity", foreignKey = @ForeignKey(name = "FK_MARKETING_SUCCESS_REF_BUSINESS_ACTIVITY", foreignKeyDefinition = "FOREIGN KEY (business_activity) REFERENCES business_activity(id)"))
    public BusinessActivity getBusinessActivity() {
        return businessActivity;
    }

    public void setBusinessActivity(BusinessActivity businessActivity) {
        this.businessActivity = businessActivity;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    @ManyToOne
    @JoinColumn(name = "area", foreignKey = @ForeignKey(name = "FK_MARKETING_SUCCESS_REF_AREA", foreignKeyDefinition = "FOREIGN KEY (area) REFERENCES arae(id)"))
    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }
}

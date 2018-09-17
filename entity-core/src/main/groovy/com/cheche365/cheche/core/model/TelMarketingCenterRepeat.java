package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.TelMarketingCenterSource;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.service.listener.EntityChangeListener;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@EntityListeners(EntityChangeListener.class)
public class TelMarketingCenterRepeat implements Serializable {

    private static final long serialVersionUID = -2005749400743645430L;
    private Long id;//主键
    private String mobile;//号码
    private Date createTime;//创建时间
    private User user;//用户
    private String userName;//用户名称
    private TelMarketingCenterSource source;//来源
    private Date sourceCreateTime;//来源创建时间
    private Long sourceId;//来源id
    private Long activeUrlId;   //如果来源是活动的话，如果有广告来源，则记录该广告来源
    private String activeUrlSource;     //广告来源所属的渠道，即：activity_monitor_url表中的source字段
    /**
     * 来源表名
     * 商业险保单：insurance
     * 交强险保单：compulsory_insurance
     * 注册但无行为用户：user
     * 新年礼包：gift
     * 主动预约：appointment_insurance
     * 拍照预约：quote_photo
     * 带优惠券活动：gift
     * 不带优惠券活动：marketing_success
     * 未支付订单:purchase_order
     */
    private String sourceTable;
    private Channel channel;
    private String renewalDate;//续保日期

    @Transient
    public String getActiveUrlSource() {
        return activeUrlSource;
    }

    public void setActiveUrlSource(String activeUrlSource) {
        this.activeUrlSource = activeUrlSource;
    }

    @Transient
    public Long getActiveUrlId() {
        return activeUrlId;
    }

    public void setActiveUrlId(Long activeUrlId) {
        this.activeUrlId = activeUrlId;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getSourceCreateTime() {
        return sourceCreateTime;
    }

    public void setSourceCreateTime(Date sourceCreateTime) {
        this.sourceCreateTime = sourceCreateTime;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(50)")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @ManyToOne
    @JoinColumn(name = "user", foreignKey = @ForeignKey(name = "FK_TEL_MARKETING_CENTER_REPEAT_REF_USER", foreignKeyDefinition = "FOREIGN KEY (user) REFERENCES user(id)"))
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @ManyToOne
    @JoinColumn(name = "source", foreignKey = @ForeignKey(name = "FK_TEL_MARKETING_CENTER_REPEAT_REF_SOURCE", foreignKeyDefinition = "FOREIGN KEY (source) REFERENCES tel_marketing_center_source(id)"))
    public TelMarketingCenterSource getSource() {
        return source;
    }

    public void setSource(TelMarketingCenterSource source) {
        this.source = source;
    }

    @Column(columnDefinition = "BIGINT(20)")
    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getSourceTable() {
        return sourceTable;
    }

    public void setSourceTable(String sourceTable) {
        this.sourceTable = sourceTable;
    }

    @ManyToOne
    @JoinColumn(name = "channel", foreignKey = @ForeignKey(name = "FK_TEL_MARKETING_CENTER_REPEAT_REF_CHANNEL", foreignKeyDefinition = "FOREIGN KEY (channel) REFERENCES channel(id)"))
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getRenewalDate() { return renewalDate; }

    public void setRenewalDate(String renewalDate) { this.renewalDate = renewalDate; }

    @Component
    public static class Enum {
        public static String INSURANCE = "insurance";
        public static String COMPULSORY_INSURANCE = "compulsory_insurance";
        public static String USER = "user";
        public static String GIFT = "gift";
        public static String APPOINTMENT_INSURANCE = "appointment_insurance";
        public static String QUOTE_PHOTO = "quote_photo";
        public static String MARKETING_SUCCESS = "marketing_success";
        public static String PURCHASE_ORDER = "purchase_order";
        public static String PURCHASE_ORDER_AMEND = "purchase_order_amend";
        public static String APPLICATION_LOG = "application_log";
    }
}

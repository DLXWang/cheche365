package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.service.listener.EntityChangeListener;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 预约
 * Created by sunhuazhong on 2015/7/24.
 */
@Entity
@EntityListeners(EntityChangeListener.class)
@JsonIgnoreProperties({ "user"})
public class AppointmentInsurance implements Serializable {

    private static final long serialVersionUID = -350628352561695136L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user", foreignKey=@ForeignKey(name="FK_APPOINTMENT_INSURANCE_REF_USER", foreignKeyDefinition="FOREIGN KEY (user) REFERENCES user(id)"))
    private User user;//预约用户

    @NotNull
    @Column(columnDefinition = "VARCHAR(45)")
    private String licensePlateNo;//车牌号

    @NotNull
    @Column(columnDefinition = "DATETIME")
    private Date expireBefore;//车辆到期日期

    @NotNull
    @Column(columnDefinition = "VARCHAR(45)")
    private String contact;//联系人

    @Column (columnDefinition = "DATETIME")
    private Date createTime;
    @Column (columnDefinition = "DATETIME")
    private Date updateTime;

    @Column(columnDefinition = "tinyint(1)")
    private Integer status = 1;//处理状态，1-未处理，2-已处理

    @Column(columnDefinition = "VARCHAR(2000)")
    private String comment;//备注
    @Column(columnDefinition = "tinyint(1)")
    private Integer source;

    @ManyToOne
    @JoinColumn(name = "source_channel", foreignKey=@ForeignKey(name="FK_APPOINTMENT_INSURANCE_REF_CHANNEL_IDX", foreignKeyDefinition="FOREIGN KEY (source_channel) REFERENCES channel(id)"))
    private Channel sourceChannel; //来源渠道，如微信，IOS_4，第三方

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLicensePlateNo() {
        return licensePlateNo;
    }

    public void setLicensePlateNo(String licensePlateNo) {
        this.licensePlateNo = licensePlateNo;
    }

    public Date getExpireBefore() {
        return expireBefore;
    }

    public void setExpireBefore(Date expireBefore) {
        this.expireBefore = expireBefore;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Channel getSourceChannel() {
        return sourceChannel;
    }

    public void setSourceChannel(Channel sourceChannel) {
        this.sourceChannel = sourceChannel;
    }

    public static class Enum {
        public static final Integer SOURCE_QUOTE_APPOINTMENT = 1; //来源：报价预约
        public static final Integer SOURCE_INSURANCE_BY_DAY_APPOINTMENT = 2;//来源：按天买车险预约
    }
}

package com.cheche365.cheche.core.model;


import javax.persistence.*;
import java.util.Date;

/**
 * 合作渠道
 * Created by sunhuazhong on 2015/5/22.
 */
@Entity
@Table(name = "cps_channel")
public class CPSChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "VARCHAR(45)")
    private String name;//渠道名称

    @Column(columnDefinition = "VARCHAR(45)")
    private String channelNo;//渠道编号

    @Column(columnDefinition = "DECIMAL(18,2)")
    private Double rebate;//返点

    @Column(columnDefinition = "VARCHAR(45)")
    private String wapUrl;//wapURL

    @Column(columnDefinition = "DATE")
    private Date startDate;//合作开始日期

    @Column(columnDefinition = "DATE")
    private Date endDate;//合作结束日期

    @Column(columnDefinition = "VARCHAR(45)")
    private String linkMan;//联系人

    @Column(columnDefinition = "VARCHAR(45)")
    private String mobile;//联系人手机号

    @Column(columnDefinition = "VARCHAR(45)")
    private String email;//联系人邮箱

    @Column(columnDefinition = "tinyint(1)")
    private Integer frequency;//发送频率，1-每周；2-每月

    @Column(columnDefinition = "DATETIME")
    private Date createTime;

    @Column(columnDefinition = "DATETIME")
    private Date updateTime;

    @Column(columnDefinition = "tinyint(1)")
    private boolean enable;//是否使用优惠券,true-使用，false-不使用

    @Column(columnDefinition = "tinyint(1)")
    private boolean display;//是否显示"回到首页"、"我的"等按钮 true-显示  false-不显示

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey=@ForeignKey(name="FK_CPS_CHANNEL_REF_INTERNAL_USER", foreignKeyDefinition="FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    private InternalUser operator;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(String channelNo) {
        this.channelNo = channelNo;
    }

    public Double getRebate() {
        return rebate;
    }

    public void setRebate(Double rebate) {
        this.rebate = rebate;
    }

    public String getWapUrl() {
        return wapUrl;
    }

    public void setWapUrl(String wapUrl) {
        this.wapUrl = wapUrl;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getLinkMan() {
        return linkMan;
    }

    public void setLinkMan(String linkMan) {
        this.linkMan = linkMan;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
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

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }
}

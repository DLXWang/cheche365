package com.cheche365.cheche.manage.common.web.model;

import java.util.Date;

/**
 * Created by sunhuazhong on 2015/9/14.
 */
public class MarketingViewModel {
    private Long id;
    private String name;
    private String code;
    private Date beginDate;//活动开始时间
    private Date endDate;//活动结束时间
    private String channel;//渠道，当前只有IOS
    private String amountClass;//金额计算类
    private String amountClassParam;//金额计算类的参数
    private String failureDateClass;//结束时间计算类
    private String failureDateClassParam;//结束时间计算类的参数
    private String describle;//描述
    private String fullLimitParam;//gift做满减条件计算参数
    private String giftClass;//写gift数据的处理类

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getAmountClass() {
        return amountClass;
    }

    public void setAmountClass(String amountClass) {
        this.amountClass = amountClass;
    }

    public String getAmountClassParam() {
        return amountClassParam;
    }

    public void setAmountClassParam(String amountClassParam) {
        this.amountClassParam = amountClassParam;
    }

    public String getFailureDateClass() {
        return failureDateClass;
    }

    public void setFailureDateClass(String failureDateClass) {
        this.failureDateClass = failureDateClass;
    }

    public String getFailureDateClassParam() {
        return failureDateClassParam;
    }

    public void setFailureDateClassParam(String failureDateClassParam) {
        this.failureDateClassParam = failureDateClassParam;
    }

    public String getDescrible() {
        return describle;
    }

    public void setDescrible(String describle) {
        this.describle = describle;
    }

    public String getFullLimitParam() {
        return fullLimitParam;
    }

    public void setFullLimitParam(String fullLimitParam) {
        this.fullLimitParam = fullLimitParam;
    }

    public String getGiftClass() {
        return giftClass;
    }

    public void setGiftClass(String giftClass) {
        this.giftClass = giftClass;
    }
}

package com.cheche365.cheche.ordercenter.model;

import com.cheche365.cheche.manage.common.model.*;
import com.cheche365.cheche.manage.common.model.PublicQuery;

/**
 * Created by wangshaobin on 2017/5/4.
 */
public class WalletQuery extends PublicQuery {

    private Long walletId;
    private String startTime;   //开始时间
    private String endTime;     //结束时间
    private String type;       //类型
    private String[] sources;   //来源，多选
    private String[] statuses;  //状态，多选
    private String[] platforms; //平台，多选
    private Integer isGroupBy;  //是否分组 1/0 : 分组/不分组
    private Long channel;       //渠道

    public Integer getIsGroupBy() {
        return isGroupBy;
    }

    public void setIsGroupBy(Integer isGroupBy) {
        this.isGroupBy = isGroupBy;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getSources() {
        return sources;
    }

    public void setSources(String[] sources) {
        this.sources = sources;
    }

    public String[] getStatuses() {
        return statuses;
    }

    public void setStatuses(String[] statuses) {
        this.statuses = statuses;
    }

    public String[] getPlatforms() {
        return platforms;
    }

    public void setPlatforms(String[] platforms) {
        this.platforms = platforms;
    }

    public Long getChannel() {
        return channel;
    }

    public void setChannel(Long channel) {
        this.channel = channel;
    }
}

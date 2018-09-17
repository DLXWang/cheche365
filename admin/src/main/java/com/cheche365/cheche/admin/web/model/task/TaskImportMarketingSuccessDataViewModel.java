package com.cheche365.cheche.admin.web.model.task;

import com.cheche365.cheche.core.model.SourceType;
import com.cheche365.cheche.manage.common.model.TaskImportMarketingSuccessData;
import com.cheche365.cheche.core.model.TelMarketingCenterSource;

/**
 * Created by xu.yelong on 2016-03-31.
 */
public class TaskImportMarketingSuccessDataViewModel {
    private Long id;
    private String cacheKey;
    private String marketingCode;
    private String marketingName;
    private TelMarketingCenterSource source;
    private SourceType sourceType;
    private Boolean enable = true;
    private Integer priority;
    private Long channelId;
    private String channelName;

    public TaskImportMarketingSuccessDataViewModel(TaskImportMarketingSuccessData taskImportMarketingSuccessData){
        this.id=taskImportMarketingSuccessData.getId();
        this.cacheKey=taskImportMarketingSuccessData.getCacheKey();
        this.marketingCode=taskImportMarketingSuccessData.getMarketing().getCode();
        this.marketingName=taskImportMarketingSuccessData.getMarketing().getName();
        this.source=taskImportMarketingSuccessData.getSource();
        this.enable=taskImportMarketingSuccessData.getEnable();
        this.priority=taskImportMarketingSuccessData.getPriority();
        this.channelId = taskImportMarketingSuccessData.getChannel().getId();
        this.channelName = taskImportMarketingSuccessData.getChannel().getDescription();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    public String getMarketingCode() {
        return marketingCode;
    }

    public void setMarketingCode(String marketingCode) {
        this.marketingCode = marketingCode;
    }

    public TelMarketingCenterSource getSource() {
        return source;
    }

    public void setSource(TelMarketingCenterSource source) {
        this.source = source;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getMarketingName() {
        return marketingName;
    }

    public void setMarketingName(String marketingName) {
        this.marketingName = marketingName;
    }

    public Long getChannelId() { return channelId; }

    public void setChannelId(Long channelId) {  this.channelId = channelId;  }

    public String getChannelName() {  return channelName;  }

    public void setChannelName(String channelName) { this.channelName = channelName;  }

}

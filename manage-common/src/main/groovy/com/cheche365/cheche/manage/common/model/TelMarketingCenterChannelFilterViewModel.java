package com.cheche365.cheche.manage.common.model;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.util.BeanUtil;

/**
 * Created by wangshaobin on 2016/8/29.
 */
public class TelMarketingCenterChannelFilterViewModel {
    private Long id;
    private String excludeChannels;
    private String taskType;
    private String createTime;
    private String updateTime;
    private String operator;

    public void setId(Long id) {
        this.id = id;
    }

    public void setExcludeChannels(String excludeChannels) {
        this.excludeChannels = excludeChannels;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Long getId() {
        return id;
    }

    public String getExcludeChannels() {
        return excludeChannels;
    }

    public String getTaskType() {
        return taskType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getOperator() {
        return operator;
    }

    public static TelMarketingCenterChannelFilterViewModel createViewModel(TelMarketingCenterChannelFilter setting) {
        if (setting == null)
            return null;
        TelMarketingCenterChannelFilterViewModel viewModel = new TelMarketingCenterChannelFilterViewModel();
        String[] properties = new String[]{"id", "excludeChannels"};
        BeanUtil.copyPropertiesContain(setting, viewModel, properties);
        viewModel.setCreateTime(DateUtils.getDateString(setting.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setUpdateTime(DateUtils.getDateString(setting.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setOperator(setting.getOperator() == null ? "" : setting.getOperator().getName());
        viewModel.setTaskType(TelMarketingCenterTaskType.Enum.findById(setting.getTaskType()).getName());
        return viewModel;
    }
}

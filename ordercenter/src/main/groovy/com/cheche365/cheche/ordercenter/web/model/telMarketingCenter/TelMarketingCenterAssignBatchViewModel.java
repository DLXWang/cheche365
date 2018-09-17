package com.cheche365.cheche.ordercenter.web.model.telMarketingCenter;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterAssignBatch;

public class TelMarketingCenterAssignBatchViewModel {

    private Long id;//主键
    private String createTime;//分配时间
    private Long assignNum;//分配数量
    private String sourceName;//来源
    private String targetName;//指定人
    private Long parentId;//父ID
    private String channel;//渠道
    private String sourceType;//类型
    private String area;//地区

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Long getAssignNum() {
        return assignNum;
    }

    public void setAssignNum(Long assignNum) {
        this.assignNum = assignNum;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public static TelMarketingCenterAssignBatchViewModel createViewData(TelMarketingCenterAssignBatch telMarketingCenterAssignBatch) {
        TelMarketingCenterAssignBatchViewModel viewModel = new TelMarketingCenterAssignBatchViewModel();
        viewModel.setId(telMarketingCenterAssignBatch.getId());
        viewModel.setCreateTime(DateUtils.getDateString(
            telMarketingCenterAssignBatch.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewModel.setAssignNum(telMarketingCenterAssignBatch.getAssignNum());
        if (telMarketingCenterAssignBatch.getSourceAssigner() != null) {
            viewModel.setSourceName(telMarketingCenterAssignBatch.getSourceAssigner().getName());
        }
        viewModel.setTargetName(telMarketingCenterAssignBatch.getTargetAssigner().getName());
        if (telMarketingCenterAssignBatch.getParent() != null) {
            viewModel.setParentId(telMarketingCenterAssignBatch.getParent().getId());
        }
        if (telMarketingCenterAssignBatch.getChannel() != null) {
            viewModel.setChannel(telMarketingCenterAssignBatch.getChannel());
        }
        if (telMarketingCenterAssignBatch.getSourceType() != null) {
            viewModel.setSourceType(telMarketingCenterAssignBatch.getSourceType());
        }
        if (telMarketingCenterAssignBatch.getArea() != null) {
            viewModel.setArea(telMarketingCenterAssignBatch.getArea());
        }
        return viewModel;
    }
}

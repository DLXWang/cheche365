package com.cheche365.cheche.operationcenter.web.model.partner;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 合作商
 * Created by sunhuazhong on 2015/8/26.
 */
public class PartnerViewModel {
    private Long id;
    @NotNull
    private String name;//合作商名称
    @NotNull
    private String cooperationMode;//合作方式，以逗号分割，接收请求用
    private List<CooperationModeViewModel> cooperationModes;//合作方式集合 返回数据用
    private PartnerTypeViewModel partnerType;//合作商类型
    @NotNull
    private String cooperationTime;//预计首次合作时间，类型为yyyy-MM-dd
    private boolean enable = false;//是否启用，0-禁用，1-启用

    private String createTime;//创建时间
    private String updateTime;//修改时间
    private String operator;//操作人
    private String comment;//备注

    private PartnerAttachmentViewModel partnerAttachment;//合作商上传文件

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

    public String getCooperationMode() {
        return cooperationMode;
    }

    public void setCooperationMode(String cooperationMode) {
        this.cooperationMode = cooperationMode;
    }

    public PartnerTypeViewModel getPartnerType() {
        return partnerType;
    }

    public void setPartnerType(PartnerTypeViewModel partnerType) {
        this.partnerType = partnerType;
    }

    public String getCooperationTime() {
        return cooperationTime;
    }

    public void setCooperationTime(String cooperationTime) {
        this.cooperationTime = cooperationTime;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public PartnerAttachmentViewModel getPartnerAttachment() {
        return partnerAttachment;
    }

    public void setPartnerAttachment(PartnerAttachmentViewModel partnerAttachment) {
        this.partnerAttachment = partnerAttachment;
    }

    public List<CooperationModeViewModel> getCooperationModes() {
        return cooperationModes;
    }

    public void setCooperationModes(List<CooperationModeViewModel> cooperationModes) {
        this.cooperationModes = cooperationModes;
    }
}

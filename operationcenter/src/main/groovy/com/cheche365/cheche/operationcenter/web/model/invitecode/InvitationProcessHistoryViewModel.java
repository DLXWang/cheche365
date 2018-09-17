package com.cheche365.cheche.operationcenter.web.model.invitecode;

public class InvitationProcessHistoryViewModel {
    private Long id;//主键
    private Long prizeSendId;//奖品发放id
    private String comment ;//备注
    private String createTime;//创建时间
    private String operatorName;//操作人

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrizeSendId() {
        return prizeSendId;
    }

    public void setPrizeSendId(Long prizeSendId) {
        this.prizeSendId = prizeSendId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }
}

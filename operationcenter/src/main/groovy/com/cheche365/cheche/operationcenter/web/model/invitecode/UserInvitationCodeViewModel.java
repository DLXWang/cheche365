package com.cheche365.cheche.operationcenter.web.model.invitecode;

/**
 * Created by zhangshitao on 2015/11/2.
 */
public class UserInvitationCodeViewModel {

    private String id;
    private Long userId;//用户id
    private String phoneNo;//手机号码
    private String inviteCode;//邀请码
    private String createTime;//创建时间
    private Integer inviteCount;//总邀请次数
    private Integer currentCount;//当前邀请次数
    private String userName;//
    private String inviteTime;//邀请时间
    private String invitedId;
    private String invitedPhoneNo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getInviteCount() {
        return inviteCount;
    }

    public void setInviteCount(Integer inviteCount) {
        this.inviteCount = inviteCount;
    }

    public Integer getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(Integer currentCount) {
        this.currentCount = currentCount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getInviteTime() {
        return inviteTime;
    }

    public void setInviteTime(String inviteTime) {
        this.inviteTime = inviteTime;
    }

    public String getInvitedId() {
        return invitedId;
    }

    public void setInvitedId(String invitedId) {
        this.invitedId = invitedId;
    }

    public String getInvitedPhoneNo() {
        return invitedPhoneNo;
    }

    public void setInvitedPhoneNo(String invitedPhoneNo) {
        this.invitedPhoneNo = invitedPhoneNo;
    }
}

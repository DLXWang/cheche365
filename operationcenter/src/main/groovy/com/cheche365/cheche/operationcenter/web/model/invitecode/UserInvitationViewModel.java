package com.cheche365.cheche.operationcenter.web.model.invitecode;

/**
 * Created by zhangshitao on 2015/11/2.
 */
public class UserInvitationViewModel {

    private Long id;
    private Long userId;//被邀请用户id
    private String phoneNo;//被邀请用户手机号码
    private String registTime;//注册时间
    private String createTime;//使用邀请码时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getRegistTime() {
        return registTime;
    }

    public void setRegistTime(String registTime) {
        this.registTime = registTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}

package com.cheche365.cheche.rest.processor.login

import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.model.agent.ApproveStatus
import com.cheche365.cheche.core.model.agent.ChannelAgent

/**
 * Author:   shanxf
 * Date:     2018/9/11 15:35
 */
class UserInfo {

    private boolean needCompletionUser
    private ApproveStatus approveStatus
    private Map agent
    private Map user

    Map getAgent() {
        return agent
    }

    void setAgent(ChannelAgent channelAgent) {
        this.agent = [
            "ethnic": channelAgent.ethnic
        ]
    }

    Map getUser() {
        return user
    }

    void setUser(User user) {
        this.user = [
            "name"    : user.name,
            "identity": user.identity
        ]
    }

    boolean getNeedCompletionUser() {
        return needCompletionUser
    }

    void setNeedCompletionUser(boolean needCompletionUser) {
        this.needCompletionUser = needCompletionUser
    }

    ApproveStatus getApproveStatus() {
        return approveStatus
    }

    void setApproveStatus(ApproveStatus approveStatus) {
        this.approveStatus = approveStatus
    }
}

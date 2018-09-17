package com.cheche365.cheche.core.model.mongo;

import com.cheche365.cheche.core.model.User;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by wangshaobin on 2017/8/22.
 */
public class MongoUser implements Serializable {

    private static final long serialVersionUID = -2205301834484157208L;
    private Long id;
    private String mobile;
    private Date createTime;
    private Long registerChannel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getRegisterChannel() {
        return registerChannel;
    }

    public void setRegisterChannel(Long registerChannel) {
        this.registerChannel = registerChannel;
    }

    public static MongoUser toMongoUser(User user){
        if(null == user){
            return null;
        }
        MongoUser mongoUser = new MongoUser();
        mongoUser.setId(user.getId());
        mongoUser.setMobile(user.getMobile());
        if (user.getRegisterChannel() != null){
            mongoUser.setRegisterChannel(user.getRegisterChannel().getId());
        }
        mongoUser.setCreateTime(user.getCreateTime());
        return mongoUser;
    }
}

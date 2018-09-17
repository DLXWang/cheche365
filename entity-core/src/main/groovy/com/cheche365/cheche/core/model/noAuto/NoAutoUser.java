package com.cheche365.cheche.core.model.noAuto;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.DescribableEntity;
import com.cheche365.cheche.core.model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

/**
 * Created by Shanxf on 2017/6/7.
 * 非车险用户跟车车用户关联关系
 */
@Entity
@Table(name = "no_auto_user")
public class NoAutoUser extends DescribableEntity {

    private Channel channel;
    private User user;
    private String uid;
    private String originalMobile;

    @ManyToOne
    @JoinColumn(name="channel_id",foreignKey = @ForeignKey(name = "channel_id_fk",foreignKeyDefinition ="FOREIGN KEY (channel_id) REFERENCES channel (id)"))
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @ManyToOne
    @JoinColumn(name = "user_id",foreignKey = @ForeignKey(name = "user_id_fk",foreignKeyDefinition ="FOREIGN KEY (user_id) REFERENCES user (id)"))
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getUid() {
        return uid;
    }


    public void setUid(String uid) {
        this.uid = uid;
    }

    @Column(name = "original_mobile",columnDefinition = "VARCHAR(10)")
    public String getOriginalMobile() {
        return originalMobile;
    }

    public void setOriginalMobile(String originalMobile) {
        this.originalMobile = originalMobile;
    }

}

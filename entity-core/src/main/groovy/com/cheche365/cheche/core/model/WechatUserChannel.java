package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.model.Channel;

import javax.persistence.*;

/**
 * Created by Chenqc on 2016/10/27.
 */

@Entity
public class WechatUserChannel extends DescribableEntity {

    private String openId;
    private Channel channel;
    private WechatUserInfo wechatUserInfo;

    private long subscribe;
    private Long qrcode;
    private Long qrcodeChannel;

    private long subscribe_time;
    private boolean unsubscribed;

    @Column(name="open_id",columnDefinition = "varchar(100)")
    public String getOpenId() {
        return openId;
    }

    public WechatUserChannel setOpenId(String openId) {
        this.openId = openId;
        return this;
    }

    @ManyToOne
    @JoinColumn(foreignKey=@ForeignKey(name="FK_WECHAT_OPRN_ID_REF_CHANNEL", foreignKeyDefinition="FOREIGN KEY (channel) REFERENCES channel(id)"))
    public Channel getChannel() {
        return channel;
    }

    public WechatUserChannel setChannel(Channel channel) {
        this.channel = channel;
        return this;
    }

    @ManyToOne
    @JoinColumn(name = "wechat_user_info")
    public WechatUserInfo getWechatUserInfo() {
        return wechatUserInfo;
    }

    public WechatUserChannel setWechatUserInfo(WechatUserInfo wechatUserInfo) {
        this.wechatUserInfo = wechatUserInfo;
        return this;
    }

    @Column
    public long getSubscribe() {
        return subscribe;
    }

    public WechatUserChannel setSubscribe(long subscribe) {
        this.subscribe = subscribe;
        return this;
    }

    @Column
    public Long getQrcode() {
        return qrcode;
    }

    public WechatUserChannel setQrcode(Long qrcode) {
        this.qrcode = qrcode;
        return this;
    }

    @Column(columnDefinition = "bigint(20)")
    public Long getQrcodeChannel() {
        return qrcodeChannel;
    }

    public void setQrcodeChannel(Long qrcodeChannel) {
        this.qrcodeChannel = qrcodeChannel;
    }

    @Column
    public boolean isUnsubscribed() {
        return unsubscribed;
    }

    public WechatUserChannel setUnsubscribed(boolean unsubscribed) {
        this.unsubscribed = unsubscribed;
        return this;
    }

    @Column(name="subscribe_time")
    public Long getSubscribe_time() {
        return subscribe_time;
    }

    public WechatUserChannel setSubscribe_time(Long subscribe_time) {
        this.subscribe_time = subscribe_time;
        return this;
    }

    @Override
    public String toString() {
        return "WechatUserChannel{" +
            "openId='" + openId + '\'' +
            ", channel=" + channel +
            ", subscribe=" + subscribe +
            ", qrcode=" + qrcode +
            ", qrcodeChannel=" + qrcodeChannel +
            ", subscribe_time=" + subscribe_time +
            ", unsubscribed=" + unsubscribed +
            '}';
    }
}

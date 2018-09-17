package com.cheche365.cheche.wechat.message;

import com.cheche365.cheche.wechat.util.XStreamFactory;
import com.thoughtworks.xstream.XStream;

/**
 * Created by liqiang on 5/6/15.
 */
public class TransferSupportResponse {
    private String FromUserName;
    private String ToUserName;
    private long CreateTime;
    private String MsgType;

    public String getFromUserName() {
        return FromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.FromUserName = fromUserName;
    }

    public String getToUserName() {
        return ToUserName;
    }

    public void setToUserName(String toUserName) {
        this.ToUserName = toUserName;
    }

    public long getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(long createTime) {
        this.CreateTime = createTime;
    }

    public String getMsgType() {
        return MsgType;
    }

    public void setMsgType(String msgType) {
        this.MsgType = msgType;
    }

    public String toXmlString() {
        XStream xs = XStreamFactory.init(true);
        xs.alias("xml", this.getClass());
        return xs.toXML(this);
    }
}

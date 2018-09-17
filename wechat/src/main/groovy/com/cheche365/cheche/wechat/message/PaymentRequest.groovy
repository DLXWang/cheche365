package com.cheche365.cheche.wechat.message

import com.cheche365.cheche.core.WechatConstant
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.wechat.util.Signature
import com.cheche365.cheche.wechat.util.XStreamFactory
import com.thoughtworks.xstream.XStream
import org.apache.commons.lang3.RandomStringUtils

/**
 * Created by liqiang on 4/9/15.
 */
public class PaymentRequest {
    private String appid;
    private String mch_id;
    private String nonce_str;
    private String sign;

    public PaymentRequest(){

    }

    public PaymentRequest(Channel channel){

        appid = WechatConstant.findAppId(channel)
        mch_id = WechatConstant.findMchId(channel)

        nonce_str  = RandomStringUtils.randomAlphanumeric(32).toUpperCase();
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String toXmlString() {

        this.setSign(null);
        String sign = Signature.getSign(this);
        setSign(sign);

        XStream xs = XStreamFactory.init(true);
        xs.alias("xml", this.getClass());
        return xs.toXML(this);
    }
}

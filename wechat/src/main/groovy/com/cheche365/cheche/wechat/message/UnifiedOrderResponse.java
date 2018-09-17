package com.cheche365.cheche.wechat.message;

import com.cheche365.cheche.wechat.util.XStreamUtil;

/**
 * Created by liqiang on 4/7/15.
 */
public class UnifiedOrderResponse extends PaymentResponse {

    private String trade_type;
    private String prepay_id;
    private String code_url;
    private String mweb_url;

    public String getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }

    public String getPrepay_id() {
        return prepay_id;
    }

    public void setPrepay_id(String prepay_id) {
        this.prepay_id = prepay_id;
    }

    public String getCode_url() {
        return code_url;
    }

    public void setCode_url(String code_url) {
        this.code_url = code_url;
    }

    public String getMweb_url() {
        return mweb_url;
    }

    public void setMweb_url(String mweb_url) {
        this.mweb_url = mweb_url;
    }

    public static UnifiedOrderResponse parseMessage(String message){
        return XStreamUtil.parse(message, UnifiedOrderResponse.class);
    }
}

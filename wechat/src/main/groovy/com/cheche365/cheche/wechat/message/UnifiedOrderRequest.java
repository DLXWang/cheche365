package com.cheche365.cheche.wechat.message;

import com.cheche365.cheche.core.model.Channel;

/**
 * Created by liqiang on 4/7/15.
 */
public class UnifiedOrderRequest extends PaymentRequest {

    private String device_info;
    private String body;
    private String attach;
    private String out_trade_no;
    private long total_fee;
    private String spbill_create_ip;
    private String time_start;
    private String time_expire;
    private String goods_tag;
    private String notify_url;
    private String trade_type;
    private String openid;
    private String product_id;

    public UnifiedOrderRequest() {
    }

    public UnifiedOrderRequest(Channel channel){
        super(channel);
    }

    public String getDevice_info() {
        return device_info;
    }

    public UnifiedOrderRequest setDevice_info(String device_info) {
        this.device_info = device_info;
        return this;
    }

    public String getBody() {
        return body;
    }

    public UnifiedOrderRequest setBody(String body) {
        this.body = body;
        return this;
    }

    public String getAttach() {
        return attach;
    }

    public UnifiedOrderRequest setAttach(String attach) {
        this.attach = attach;
        return this;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public UnifiedOrderRequest setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
        return this;
    }

    public long getTotal_fee() {
        return total_fee;
    }

    public UnifiedOrderRequest setTotal_fee(long total_fee) {
        this.total_fee = total_fee;
        return this;
    }

    public String getSpbill_create_ip() {
        return spbill_create_ip;
    }

    public UnifiedOrderRequest setSpbill_create_ip(String spbill_create_ip) {
        this.spbill_create_ip = spbill_create_ip;
        return this;
    }

    public String getTime_start() {
        return time_start;
    }

    public UnifiedOrderRequest setTime_start(String time_start) {
        this.time_start = time_start;
        return this;
    }

    public String getTime_expire() {
        return time_expire;
    }

    public UnifiedOrderRequest setTime_expire(String time_expire) {
        this.time_expire = time_expire;
        return this;
    }

    public String getGoods_tag() {
        return goods_tag;
    }

    public UnifiedOrderRequest setGoods_tag(String goods_tag) {
        this.goods_tag = goods_tag;
        return this;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public UnifiedOrderRequest setNotify_url(String notify_url) {
        this.notify_url = notify_url;
        return this;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public UnifiedOrderRequest setTrade_type(String trade_type) {
        this.trade_type = trade_type;
        return this;
    }

    public String getOpenid() {
        return openid;
    }

    public UnifiedOrderRequest setOpenid(String openid) {
        this.openid = openid;
        return this;
    }

    public String getProduct_id() {
        return product_id;
    }

    public UnifiedOrderRequest setProduct_id(String product_id) {
        this.product_id = product_id;
        return this;
    }

}

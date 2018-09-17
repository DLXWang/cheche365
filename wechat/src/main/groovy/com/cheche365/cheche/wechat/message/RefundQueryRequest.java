package com.cheche365.cheche.wechat.message;

import com.cheche365.cheche.core.model.Channel;

/**
 * Created by liqiang on 4/9/15.
 */
public class RefundQueryRequest extends  PaymentRequest{
    private String device_info;
    private String transaction_id;
    private String out_trade_no;
    private String out_refund_no;
    private String refund_id;

    public RefundQueryRequest(){

    }

    public RefundQueryRequest(Channel channel) {
        super(channel);
    }

    public String getDevice_info() {
        return device_info;
    }

    public void setDevice_info(String device_info) {
        this.device_info = device_info;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getOut_refund_no() {
        return out_refund_no;
    }

    public void setOut_refund_no(String out_refund_no) {
        this.out_refund_no = out_refund_no;
    }

    public String getRefund_id() {
        return refund_id;
    }

    public void setRefund_id(String refund_id) {
        this.refund_id = refund_id;
    }
}

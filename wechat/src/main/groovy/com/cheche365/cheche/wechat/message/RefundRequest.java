package com.cheche365.cheche.wechat.message;

import com.cheche365.cheche.core.model.Channel;

/**
 * Created by liqiang on 4/9/15.
 */
public class RefundRequest extends PaymentRequest{
    private String device_info;
    private String transaction_id;
    private String out_trade_no;
    private long total_fee;
    private String out_refund_no;
    private long refund_fee;
    private String op_user_id;
    private String refund_account;

    public RefundRequest(Channel channel) {
        super(channel);
    }
    public RefundRequest() {

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

    public long getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(long total_fee) {
        this.total_fee = total_fee;
    }

    public String getOut_refund_no() {
        return out_refund_no;
    }

    public void setOut_refund_no(String out_refund_no) {
        this.out_refund_no = out_refund_no;
    }

    public long getRefund_fee() {
        return refund_fee;
    }

    public void setRefund_fee(long refund_fee) {
        this.refund_fee = refund_fee;
    }

    public String getOp_user_id() {
        return op_user_id;
    }

    public void setOp_user_id(String op_user_id) {
        this.op_user_id = op_user_id;
    }

    public String getRefund_account() {
        return refund_account;
    }

    public void setRefund_account(String refund_account) {
        this.refund_account = refund_account;
    }
}

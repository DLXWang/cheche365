package com.cheche365.cheche.wechat.message;

import java.util.List;

/**
 * Created by liqiang on 4/9/15.
 */
public class RefundQueryResponse extends PaymentResponse {
    private String transaction_id;
    private String out_trade_no;
    private long refundCount;
    private List<RefundInfo> refundInfos;

    private class RefundInfo {
        private String out_refund_no;
        private String refund_id;
        private String refund_channel;
        private long refund_fee;
        private long coupon_refund_fee;
        private String refund_status;

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

        public String getRefund_channel() {
            return refund_channel;
        }

        public void setRefund_channel(String refund_channel) {
            this.refund_channel = refund_channel;
        }

        public long getRefund_fee() {
            return refund_fee;
        }

        public void setRefund_fee(long refund_fee) {
            this.refund_fee = refund_fee;
        }

        public long getCoupon_refund_fee() {
            return coupon_refund_fee;
        }

        public void setCoupon_refund_fee(long coupon_refund_fee) {
            this.coupon_refund_fee = coupon_refund_fee;
        }

        public String getRefund_status() {
            return refund_status;
        }

        public void setRefund_status(String refund_status) {
            this.refund_status = refund_status;
        }
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

    public long getRefundCount() {
        return refundCount;
    }

    public void setRefundCount(long refundCount) {
        this.refundCount = refundCount;
    }

    public List<RefundInfo> getRefundInfos() {
        return refundInfos;
    }

    public void setRefundInfos(List<RefundInfo> refundInfos) {
        this.refundInfos = refundInfos;
    }
}

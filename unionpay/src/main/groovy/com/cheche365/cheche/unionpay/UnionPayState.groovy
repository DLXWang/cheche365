package com.cheche365.cheche.unionpay
/**
 * Created by wangfei on 2015/7/13.
 */
enum UnionPayState {
    SUCCESS("00"),//成功
    FLAWED_SUCCESS("A6"),//有缺陷成功
    UNKNOWN_03("03"),//交易通讯超时，请发起查询交易
    UNKNOWN_04("04"),//交易状态未明，请查询对账结果
    UNKNOWN_05("05");//交易已受理，请稍后查询结果

    private final String value;

    UnionPayState(String value){
        this.value = value;
    }

    String getValue() {
        return value;
    }

    static List<UnionPayState> paySuccess() {
        [SUCCESS, FLAWED_SUCCESS]
    }

    static List<UnionPayState> unknownTrade() {
        [UNKNOWN_03, UNKNOWN_04, UNKNOWN_05]
    }

    static boolean isPaySuccess(String respCode) {
        for (UnionPayState state : paySuccess()) {
            if (state.value == respCode) {
                return true;
            }
        }
        return false;
    }

    static boolean isUnknown(String respCode) {
        for (UnionPayState state : unknownTrade()) {
            if (state.value == respCode) {
                return true;
            }
        }
        return false;
    }
}

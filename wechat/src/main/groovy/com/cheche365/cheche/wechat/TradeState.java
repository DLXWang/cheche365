package com.cheche365.cheche.wechat;

import java.util.Arrays;
import java.util.List;

/**
 * Created by liqiang on 4/7/15.
 */
public enum TradeState {
    SUCCESS, //支付成功
    REFUND,  //转入退款
    NOTPAY,  //未支付
    CLOSED,  //已关闭
    REVOKED, //已撤销
    USERPAYING, //用户支付中
    NOPAY, //未支付(输入密码或确认支付超时)
    PAYERROR; //支付失败(其他原因，如银行返回失败)

    public static List<TradeState> processFinishedStates() {
         return Arrays.asList(new TradeState[]{SUCCESS, REFUND, NOTPAY, CLOSED, REVOKED, PAYERROR});
    }

    public static List<TradeState> inProcessingStates() {
        return Arrays.asList(new TradeState[]{NOTPAY, USERPAYING});
    }

    public static boolean contains(String targetTradeState){
        for(TradeState tradeState : TradeState.values()){
            if(tradeState.name().equals(targetTradeState)){
                return true;
            }
        }

        return false;
    }


}

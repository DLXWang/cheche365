package com.cheche365.cheche.wallet.model;

/**
 * Created by mjg on 2017/6/7.
 */
public class ResultDataModel {
    private String retStat;
    private String retMsg;
    private Object retData;

    public ResultDataModel() {
        this("0", "成功");
    }

    public ResultDataModel(String retStat, String retMsg) {
        this.retStat = retStat;
        this.retMsg = retMsg;
    }

    public ResultDataModel(String retStat, String retMsg, Object retData) {
        this.retStat = retStat;
        this.retMsg = retMsg;
        this.retData = retData;
    }

    public String isRetStat() {
        return retStat;
    }

    public void setRetStat(String retStat) {
        this.retStat = retStat;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }

    public Object getRetData() {
        return retData;
    }

    public void setRetData(Object retData) {
        this.retData = retData;
    }

}

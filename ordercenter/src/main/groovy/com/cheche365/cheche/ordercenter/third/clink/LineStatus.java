package com.cheche365.cheche.ordercenter.third.clink;


/**
 * Created by yellow on 2017/9/25.
 */
public class LineStatus {
    protected String result;
    protected Integer code;
    protected String msg;

    public LineStatus(){}

    public LineStatus(String result,Integer code,String msg){
        this.result=result;
        this.code=code;
        this.msg=msg;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    class OnlineStatus extends  LineStatus{
    }

    class OffLineStatus extends  LineStatus{

    }

    class QuitStatus extends  LineStatus{

    }


}

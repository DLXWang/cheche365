package com.cheche365.cheche.ordercenter.third.clink;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yellow on 2017/9/25.
 */
public class CallStatus {
    private Integer res;
    private String uniqueId;

    public CallStatus(){}

    public CallStatus(Integer res){
        this.res=res;
    }
    public Integer getRes() {
        return res;
    }

    public void setRes(Integer res) {
        this.res = res;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public static Map STATUS_MAP;

    static{
        STATUS_MAP = new HashMap(){{
            put(0,"呼叫成功");
            put(1,"呼叫座席失败");
            put(2,"参数不正确");
            put(3,"用户验证没有通过");
            put(4,"账号被停用");
            put(5,"资费不足");
            put(6,"指定的业务尚未开通");
            put(7,"电话号码不正确");
            put(8,"座席工号（cno）不存在");
            put(9,"座席状态不为空闲，可能未登录或忙");
            put(10,"其他错误");
            put(11,"电话号码为黑名单");
            put(12,"座席不在线");
            put(13,"座席正在通话/呼叫中");
            put(14,"外显号码不正确");
            put(33,"在外呼中或者座席振铃、通话中等");
            put(40,"外呼失败，外呼号码次数超过限制");
            put(41,"企业状态错误");
            put(100,"呼叫失败,请添加天润账号");
        }};
    }
}

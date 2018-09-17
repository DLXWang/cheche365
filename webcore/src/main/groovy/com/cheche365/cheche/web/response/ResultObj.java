package com.cheche365.cheche.web.response;

/**
 * Created by gaochengchun on 2015/5/4.
 */

import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 带操作成功／失败信息的对象
 **/
public class ResultObj {
    Object obj;//结果对象
    boolean result;//操作结果
    String memoCode;//描述的编号
    String memo;//描述
    Map<String, Object> extInfo = new HashedMap();//扩展信息

    public String getMemoCode() {
        return memoCode;
    }

    public void setMemoCode(String memoCode) {
        this.memoCode = memoCode;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Map<String, Object> getExtInfo() {
        return extInfo;
    }

    public void appendExtInfo(String key, Object value) {
        getExtInfo().put(key, value);
    }

    @Component
    public static class EnumMemo {
        //手机号已存在
        public static String MOBILEEXISTS = "mobileExists";
    }
}

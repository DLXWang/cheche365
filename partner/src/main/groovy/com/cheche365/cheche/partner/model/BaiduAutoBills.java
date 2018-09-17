package com.cheche365.cheche.partner.model;

import com.cheche365.cheche.partner.utils.BaiduEncryptUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zhengwei on 2/23/16.
 */
public class BaiduAutoBills extends BaiduCommon {
    public static final List<String> AUTO_SYNC_SIGN_FIELDS = Arrays.asList(new String[]{"partner_id", "userinfos"});  //同步车辆需要签名的字段集合


    public void setUserinfos(String userinfos){
        this.putSingle("userinfos", null==userinfos ? null : BaiduEncryptUtil.encrypt(userinfos));
    }

    @Override
    public List<String> getSignFields() {
        return AUTO_SYNC_SIGN_FIELDS;
    }
}

package com.cheche365.cheche.wechat.message;

import com.cheche365.cheche.core.WechatConstant;
import com.cheche365.cheche.wechat.util.XStreamFactory;
import com.thoughtworks.xstream.XStream;

/**
 * Created by liqiang on 4/7/15.
 */
public class PaymentResponse {

    private String return_code;
    private String return_msg;

    private String result_code;
    private String err_code;
    private String err_code_desc;

    private String appid;
    private String mch_id;
    private String device_info;
    private String nonce_str;
    private String sign;

    public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public String getReturn_msg() {
        return return_msg;
    }

    public void setReturn_msg(String return_msg) {
        this.return_msg = return_msg;
    }

    public String getErr_code() {
        return err_code;
    }

    public void setErr_code(String err_code) {
        this.err_code = err_code;
    }

    public String getErr_code_desc() {
        return err_code_desc;
    }

    public void setErr_code_desc(String err_code_desc) {
        this.err_code_desc = err_code_desc;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getDevice_info() {
        return device_info;
    }

    public void setDevice_info(String device_info) {
        this.device_info = device_info;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public boolean returnSuccess(){
        return "SUCCESS".equals(return_code);
    }

    public boolean resultSuccess(){
        return "SUCCESS".equals(result_code);
    }

    public String getResult_code() {
        return result_code;
    }

    public void setResult_code(String result_code) {
        this.result_code = result_code;
    }

    public String toXmlString() {
        XStream xs = XStreamFactory.init(false);
        xs.alias("xml", this.getClass());
        return xs.toXML(this);
    }

    public String getResult(){
        if((null==err_code||"".equals(err_code))&& WechatConstant.WECHAT_SUCCESS.equals(result_code)){
            return WechatConstant.WECHAT_SUCCESS;
        }else{
            return err_code;
        }
    }
}

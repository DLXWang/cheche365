package com.cheche365.cheche.core.service.sms
/**
 * Created by yinJianBin on 2017/7/27.
 */
class SmsInfo {
    String mobile
    String content
    String smsChannel
    String smsType
    String verifyCode
    Long scheduleMessageLogId


    static class Enum {
        public static String SMS_TYPE_VERIFY_CODE = 'verifyCode'    //验证码
        public static String SMS_TYPE_MESSAGE = 'message'   //普通短信
    }

    @Override
    public String toString() {
        return "{mobile: $mobile , smsChannel: $smsChannel, smsType: $smsType , verifyCode: $verifyCode , content: $content , scheduleMessageLogId: $scheduleMessageLogId }"
    }
}

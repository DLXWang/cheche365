package com.cheche365.cheche.core.util;

import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.message.AnswernNotifyEmailMessage;
import com.cheche365.cheche.core.message.BotpyNotifyEmailMessage;
import com.cheche365.cheche.core.message.CrocodileNotifyEmailMessage;
import com.cheche365.cheche.core.message.NotifyEmailMessage;
import com.cheche365.cheche.core.message.QueueMessage;

public class NotifyMessageUtils {


    public static QueueMessage getNotifyMessage(String notifyMsg, String code){

        switch (code){
            case WebConstants.ANSWERN_NOTIFY_EMAIL_CODE:
                return new AnswernNotifyEmailMessage(notifyMsg,code);
            case WebConstants.BOTPY_NOTIFY_EMAIL_CODE:
                return new BotpyNotifyEmailMessage(notifyMsg,code);
            case WebConstants.CROCODILE_NOTIFY_EMAIL_CODE:
                return new CrocodileNotifyEmailMessage(notifyMsg,code);
            default:
                return new NotifyEmailMessage(notifyMsg,code);
        }
    }


}

package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.repository.LogTypeRepository
import com.cheche365.cheche.core.util.RuntimeUtil

import javax.persistence.Entity;

/**
 * Created by zhengwei on 5/5/15.
 */
@Entity
class LogType extends AutoLoadEnum {
    private static final long serialVersionUID = 1L

    static class Enum {

        public static LogType INSURE_FAILURE_1, ORDER_RELATED_3, ORDER_INSURANCE_COMPLETE_TIME_6,
                              RESET_EXPIRED_ORDER_STATUS_9, DELETE_INTERNAL_USER_10, UPDATE_WECHAT_QRCODE_CHANNEL_NAME_18, UPDATE_WECHAT_QRCODE_CHANNEL_REBATE_19,
                              CHANGE_ORDER_PAY_CHANNEL_20, APPOINTMENT_STATUS_CHANGE_22, INSURANCE_STATUS_TRANSITION_25, COMMENT_CHANGE_27,
                              INSURANCE_HISTORY_28, Quote_Cache_Record_31, YIQIJIA_33, CCINT_34, BAOXIAN_35,CONCURRENT_API_CALL_36,
                              EXCEPTION_STACK_51, MARKETING_FREE_INSURANCE_52, BIHU_53,BOTPY_56,TaiKang_57,TIDE_58,PARTNER_SYNC_59,HuaNong_60,AIBAO_61

        static {
            RuntimeUtil.loadEnum(LogTypeRepository, LogType, Enum)
        }


    }
}

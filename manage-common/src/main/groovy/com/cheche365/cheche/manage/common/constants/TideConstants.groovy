package com.cheche365.cheche.manage.common.constants

import com.cheche365.cheche.core.model.LogType

/**
 * 潮汐系统实体的状态
 * Created by yinJianBin on 2018/4/21.
 */
class TideConstants {

    final static Integer STATUS_CREATE = 0 //未生效
    final static Integer STATUS_EFFECTIVE_ING = 1 //生效中(合约期内)
    final static Integer STATUS_DRAFT = 2 //草稿
    final static Integer STATUS_EXPIRED = 5 //已失效  (已过期)

    static Map<Integer, String> STATUS_MAP = [:]
    static Map<String, String> INSURANCETYPE_MAP = [:]
    static Map<String, String> CARTYPE_MAP = [:]
    static Map<Integer, String> REBATESTATUS_MAP = [:]
    static Map<Integer, String> AUTOTAXRETURNTYPE_MAP = [:]

    static LogType a = new LogType(id: 1L)

    final static Integer OPERATION_TYPE_UPDATE = 1
    final static Integer OPERATION_TYPE_RENEWAL = 2


    static {
        STATUS_MAP.put(STATUS_CREATE, "未生效")
        STATUS_MAP.put(STATUS_EFFECTIVE_ING, "有效期内")
        STATUS_MAP.put(STATUS_EXPIRED, "已过期")

        INSURANCETYPE_MAP.put("1", "单商业")
        INSURANCETYPE_MAP.put("2", "单交强")
        INSURANCETYPE_MAP.put("3", "交商同保")

        CARTYPE_MAP.put("1", "非营业客车")
        CARTYPE_MAP.put("2", "营业客车")
        CARTYPE_MAP.put("3", "非营业货车")
        CARTYPE_MAP.put("4", "营业货车")

        REBATESTATUS_MAP.put(STATUS_CREATE, "未生效")
        REBATESTATUS_MAP.put(STATUS_EFFECTIVE_ING, "有效期内")
        REBATESTATUS_MAP.put(STATUS_EXPIRED, "已过期")
        REBATESTATUS_MAP.put(STATUS_DRAFT, "草稿")

        AUTOTAXRETURNTYPE_MAP.put(1, "比例")
        AUTOTAXRETURNTYPE_MAP.put(2, "绝对值")
    }
}

package com.cheche365.cheche.idcredit.flow

import static com.cheche365.cheche.parser.Constants._DATE_FORMAT2

/**
 * 绿湾流程步骤所需的常量
 */
class Constants {

    static final _STATUS_CODE_IDCREDIT_API_UPPER_LIMIT_ERROR    = -10001L
    static final _STATUS_CODE_IDCREDIT_VEHICLE_INFO_FAILURE     = _STATUS_CODE_IDCREDIT_API_UPPER_LIMIT_ERROR - 1

    static final _VEHICLE_INFO_EXTRACTOR = { context ->
        def vehicleInfo = context.vehicleInfo
        [
            vinNo      : '-1' != vehicleInfo.'clsbdh**' ? vehicleInfo.'clsbdh**' : null,
            engineNo   : '-1' != vehicleInfo.'fdjh**' ? vehicleInfo.'fdjh**' : null,
            enrollDate : '-1' != vehicleInfo.'zcrq**' ? _DATE_FORMAT2.parse(vehicleInfo.'zcrq**') : null,
            brandCode  : '-1' != vehicleInfo.'ppxh**' ? vehicleInfo.'ppxh**' : null
        ].with {
            // 如果值都是null，就返回null
            values().any(Closure.IDENTITY) ? it : null
        }
    }

}

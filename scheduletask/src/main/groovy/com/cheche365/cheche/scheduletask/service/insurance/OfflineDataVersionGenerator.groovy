package com.cheche365.cheche.scheduletask.service.insurance

import groovy.util.logging.Slf4j
import org.apache.commons.codec.digest.DigestUtils
/**
 * Created by yinjianbin on 2018/1/19.
 */
@Slf4j
class OfflineDataVersionGenerator {
    static final double DOUBLE_TO_STRING_NUM = 1000;

    static String generate(OfflineDataVersionModel versionModel) {
        def dataVersionString = new StringBuilder("")
        dataVersionString.append(versionModel.policyNo)
        dataVersionString.append(versionModel.licensePlateNo ?: "")
        dataVersionString.append(versionModel.code ?: "")
        dataVersionString.append(versionModel.totalPremium ? (versionModel.totalPremium * DOUBLE_TO_STRING_NUM).toString() : "")
        dataVersionString.append(versionModel.identity ?: "")
        dataVersionString.append(versionModel.downCommercialAmount ? (versionModel.downCommercialAmount * DOUBLE_TO_STRING_NUM).toString() : "")
        dataVersionString.append(versionModel.downCompulsoryAmount ? (versionModel.downCompulsoryAmount * DOUBLE_TO_STRING_NUM).toString() : "")

        String md5 = DigestUtils.md5Hex(dataVersionString.toString())
        log.debugEnabled && log.debug("generate offline data version by dataInfo:( {} ),result:( {} )", dataVersionString.toString(), md5)
        return md5
    }
}

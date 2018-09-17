package com.cheche365.cheche.aibao.flow.step

import com.cheche365.cheche.aibao.util.BusinessUtils
import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import java.time.LocalDate

import static com.cheche365.cheche.aibao.util.BusinessUtils.getUserInfo
import static com.cheche365.cheche.common.Constants.get_DATETIME_FORMAT3
import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.Constants.get_SUPPLEMENT_INFO_VERIFICATION_CODE_TEMPLATE_INSURING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSupplementInfoFSRV



/**
 * 校验验证码
 * Created by LIU xuechl on 2018/9/7.
 */
@Slf4j
class IssueCodeTransfer implements IStep {

    private static final interfaceID = '100080'

    @Override
    def run(context) {
        log.info('开始执行验证码校验接口')
        def verificationCode = context.additionalParameters.supplementInfo?.verificationCode

        // TODO 测试写的循环正式删掉
//        while (!verificationCode && context.waitIdentityCaptcha) {
//            FileInputStream fs = new FileInputStream("C:\\Users\\admin\\Desktop\\新建文本文档.txt");
//            BufferedReader b = new BufferedReader(new InputStreamReader(fs));
//            String b1;
//            println "wait==============================="
//            Thread.sleep(2000)
//            while ((b1 = b.readLine()) != null) {
//                println(b1);
//                verificationCode = b1;
//            }
//        }
//        File f = new File("C:\\Users\\admin\\Desktop\\新建文本文档.txt");
//        FileWriter fw = new FileWriter(f);
//        fw.write("");
//        fw.close();
        if (verificationCode && context.waitIdentityCaptcha) {
            // 保存验证码共前端支付使用
            log.info '验证码校验成功'
            context.verificationCode = verificationCode
            getContinueFSRV(verificationCode)
        } else {
            def result = BusinessUtils.sendParamsAndReceive(context, getRequestParams(context, verificationCode), log, interfaceID)
            if ('0000' == result.head.errorCode) {
                log.info '需身份验证码核实信息'
                // 更新保单状态
                context.waitIdentityCaptcha = true
                return getSupplementInfoFSRV([mergeMaps(_SUPPLEMENT_INFO_VERIFICATION_CODE_TEMPLATE_INSURING, [meta: [orderNo: address]])])
            } else {
                log.error '验证码校验失败 resultMessage : {}', result.head.errorMsg
                getFatalErrorFSRV result.head.errorMsg ?: '验证码校验失败'
            }
        }
    }

    private static getRequestParams(context, verificationCode) {
        // TODO 信息暂时写死
        def now = LocalDate.now()
        def (nation, address, issuer, certiStartDat, certiEndDate) =
        ['汉族', context.area.name, '签发机构', _DATETIME_FORMAT3.format(now), _DATETIME_FORMAT3.format(now.plusYears(10))]
        [
            applicantInfo: getUserInfo(context, true) << [
                applicantNation       : nation,                                  // 民族
                applicantAddress      : address,                                // 地址
                applicantIssuer       : issuer,                                 // 签发机关
                applicantCertiStartDat: certiStartDat,                          // 发证日期
                applicantCertiEndDate : certiEndDate                            // 有效日期
            ],
            insuredInfo  : getUserInfo(context, false) << [
                insuredNation       : nation,                                    // 民族
                insuredAddress      : address,                                   // 地址
                insuredIssuer       : issuer,                                   // 签发机关
                insuredCertiStartDat: certiStartDat,                            // 发证日期
                insuredCertiEndDate : certiEndDate,                             // 有效日期
            ]
        ]
    }

}

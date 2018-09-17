package com.cheche365.cheche.parser.ms.controller

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.service.IThirdPartyCheckAccountService
import com.cheche365.cheche.parser.dto.ResponseObjectForList
import com.cheche365.cheche.parser.dto.ResponseObjectForMap
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController


/**
 * 第三方保险检查账户REST接口
 */
@RestController
@RequestMapping('/insurancePlatform')
@Slf4j
class ThirdPartyCheckAccountResource {

    @Autowired(required = false)
    private IThirdPartyCheckAccountService service


    @PostMapping('/getFailedAccounts')
    @ResponseBody
    def getFailedAccounts() {

        log.info '检查账号状态'

        try {
            def failedAccounts = service.getFailedAccounts()
            log.info '检查账号状态完成，返回值：{}', failedAccounts
            new ResponseObjectForList(
                code: 0,
                resultInfos: failedAccounts
            ).with {
                log.info '账号异常信息{}', it.resultInfos
                it
            }

        } catch (ex) {
            if (ex instanceof BusinessException) {
                log.warn '检查账号状态返回业务异常：', ex
                new ResponseObjectForMap(
                    code: ex.code.codeValue,
                    errorData: ex.errorObject,
                    message: ex.message
                )
            } else {
                log.error '检查账号状态返回非业务异常：', ex
                new ResponseObjectForMap(
                    code: -1,
                    message: ex.message
                )
            }
        }
    }

}

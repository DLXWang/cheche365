package com.cheche365.cheche.parser.client.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.service.ISuitability
import com.cheche365.cheche.core.service.IThirdPartyCheckAccountService
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.core.exception.BusinessException.Code.INTERNAL_SERVICE_ERROR
import static com.cheche365.cheche.parser.client.utils.BusinessUtils.handlerExceptionResult



/**
 * 检查账号状态接口客户端
 */
@Slf4j
class ThirdPartyCheckAccountService implements ISuitability<Map>, IThirdPartyCheckAccountService {

    private IThirdPartyCheckAccountFeignClient client
    private Closure checkSuitability

    ThirdPartyCheckAccountService(IThirdPartyCheckAccountFeignClient client, Closure checkSuitability) {
        this.client = client
        this.checkSuitability = checkSuitability
    }

    @Override
    List<Map> getFailedAccounts() {
        log.info '客户端请求检查账户状态查询'
        try {
            client.getFailedAccounts().with { result ->
                log.debug "客户端检查账户状态返回值：{}", result
                if (0 == result.code) {
                    result.resultInfos
                } else {
                    log.info '检查账户状态失败'
                    handlerExceptionResult result
                }
            }
        } catch (ex) {
            if (ex instanceof BusinessException) {
                throw ex
            } else {
                log.error '远端服务调用异常', ex
                throw new BusinessException(INTERNAL_SERVICE_ERROR, '远端服务调用异常')
            }
        }
    }

    @Override
    boolean isSuitable(Map conditions) {
        checkSuitability?.call conditions
    }

}

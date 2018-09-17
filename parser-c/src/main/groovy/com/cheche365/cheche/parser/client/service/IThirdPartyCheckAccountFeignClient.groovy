package com.cheche365.cheche.parser.client.service

import com.cheche365.cheche.parser.dto.ResponseObjectForList



/**
 * 第三方保险平台客户端检查账号状态接口
 */
interface IThirdPartyCheckAccountFeignClient {

    ResponseObjectForList getFailedAccounts()

}

package com.cheche365.cheche.parser.client.service

import com.cheche365.cheche.parser.dto.InsuringRequestObject
import com.cheche365.cheche.parser.dto.InsuringResponseObject
import com.cheche365.cheche.parser.dto.QuotingRequestObject
import com.cheche365.cheche.parser.dto.QuotingResponseObject


/**
 * 第三方保险平台客户端接口
 * @author Huabin Zhang
 */
interface IThirdPartyHandlerFeignClient {

    QuotingResponseObject quote(QuotingRequestObject body)

    InsuringResponseObject insure(InsuringRequestObject body)

}

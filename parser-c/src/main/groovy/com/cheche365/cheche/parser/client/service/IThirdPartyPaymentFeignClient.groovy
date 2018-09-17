package com.cheche365.cheche.parser.client.service

import com.cheche365.cheche.parser.dto.RequestObjectForList
import com.cheche365.cheche.parser.dto.RequestObjectForMap
import com.cheche365.cheche.parser.dto.ResponseObjectForList
import com.cheche365.cheche.parser.dto.ResponseObjectForMap
import org.springframework.web.bind.annotation.RequestBody



/**
 * 第三方保险平台客户端支付接口
 */
interface IThirdPartyPaymentFeignClient {

    ResponseObjectForMap getPaymentChannels(@RequestBody RequestObjectForMap body)

    ResponseObjectForMap getPaymentInfo(@RequestBody RequestObjectForMap body)

    ResponseObjectForList checkPaymentState(@RequestBody RequestObjectForList body)

    ResponseObjectForMap cancelPayment(@RequestBody RequestObjectForMap body)

}

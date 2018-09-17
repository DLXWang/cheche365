package com.cheche365.cheche.web.service

import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.PaymentChannel
import com.cheche365.cheche.core.model.PingPlusChannelName
import com.cheche365.cheche.core.repository.PingPlusCnRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @Author shanxf
 * @Date 2018/1/17  9:53
 */
@Service
class PingPlusCnService {

    @Autowired
    private PingPlusCnRepository pingPlusCnRepository

    String getPpChannelName(Long paymentChannelId, Channel channel, Boolean inWechat){
        PaymentChannel pc = PaymentChannel.Enum.toPaymentChannel(paymentChannelId)
        if(inWechat && PaymentChannel.Enum.PING_PLUS_WX_23 == pc){
            return "wx_pub"
        }
        PingPlusChannelName pingPlusChannelName = pingPlusCnRepository.findByPaymentChannelAndChannel(pc,channel)

        (pingPlusChannelName)? pingPlusChannelName.name : PaymentChannel.Enum.DEFAULT_NAME.get(paymentChannelId)
    }

}

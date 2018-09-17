package com.cheche365.cheche.partner.api

import com.cheche365.cheche.core.model.ApiPartner

import static com.cheche365.cheche.core.model.ApiPartnerProperties.Key.SYNC_AGENT_URL
import static com.cheche365.cheche.core.model.ApiPartnerProperties.findByPartnerAndKey
import static com.cheche365.cheche.core.model.LogType.Enum.PARTNER_SYNC_59

/**
 * Created by liheng on 2018/7/12 0012.
 */
abstract class SyncAgentApi extends PartnerApi {

    @Override
    ApiPartner apiPartner(apiInput) {
        apiInput.channelAgent.channel.apiPartner
    }

    @Override
    String apiUrl(apiInput) {
        findByPartnerAndKey(apiPartner(apiInput), SYNC_AGENT_URL)?.value
    }

    @Override
    def successCall(responseBody) {
        0 == responseBody?.code
    }

    @Override
    Map toLogParam(apiInput) {
        [
            prefix  : "${apiInput.channelAgent.channel.id} ",
            logType : PARTNER_SYNC_59,
            objId   : apiInput.channelAgent.id,
            objTable: 'channel_agent'
        ]
    }
}

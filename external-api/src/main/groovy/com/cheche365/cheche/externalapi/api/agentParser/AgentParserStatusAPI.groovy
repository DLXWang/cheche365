package com.cheche365.cheche.externalapi.api.agentParser

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.util.RuntimeUtil
import com.cheche365.cheche.externalapi.ExternalAPI
import org.springframework.stereotype.Service


@Service
class AgentParserStatusAPI extends ExternalAPI {


    @Override
    String method() {
        'POST'
    }

    @Override
    String host() {
        RuntimeUtil.isDevEnv() ? WebConstants.getDomainURL(false) : WebConstants.getDomainURL()
    }

    @Override
    String path() {
        '/agentParser/status'
    }
}

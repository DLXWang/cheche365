package com.cheche365.cheche.mock.api.botpy

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.service.spi.IHTTPContext
import com.cheche365.cheche.externalapi.ExternalAPI
import com.cheche365.cheche.mock.api.MockToken
import com.cheche365.cheche.mock.util.MockSessionUtil

/**
 * 模拟金斗云支付状态回调
 */
class MockBotpyCallBackAPI extends ExternalAPI{

    def call(def body, IHTTPContext httpContext) {
        String sessionId = new MockToken().call([mock_base_url:host()]).data.token
        def result = super.call(
            [
                body: body
            ]
        )
        MockSessionUtil.removeMockUrl(sessionId, httpContext)
        result
    }

    @Override
    String method() {
        'POST'
    }

    @Override
    String host() {
        WebConstants.getDomainURL(false)
    }

    @Override
    String path() {
        'api/callback/botpy'
    }
}

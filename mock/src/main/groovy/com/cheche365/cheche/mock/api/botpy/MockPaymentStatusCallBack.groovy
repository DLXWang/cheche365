package com.cheche365.cheche.mock.api.botpy

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.externalapi.ExternalAPI
import com.cheche365.cheche.mock.api.MockToken

import static com.cheche365.cheche.mock.util.DataFileParserUtil.model

/**
 * 模拟金斗云支付状态回调
 */
class MockPaymentStatusCallBack extends ExternalAPI{

    def call(Map setters) {
        new MockToken().call([mock_base_url:host()])
        def bodyData = model('pay_status_callback')
        setters.payStatus?.call(bodyData)
        super.call(
            [
                body: bodyData
            ]
        )
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

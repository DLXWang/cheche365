package com.cheche365.cheche.mock.api.botpy

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.externalapi.ExternalAPI
import com.cheche365.cheche.mock.api.MockToken

import static com.cheche365.cheche.mock.util.DataFileParserUtil.model

class MockStatusChangeCallback extends ExternalAPI {

    def call(Map setters){
        new MockToken().call([mock_base_url:host()])
        def bodyData = model('status_change_callback')
        setters.changeStauts?.call(bodyData)
        bodyData.data.records[0].old_proposal_status = 'WAIT_PAY'
        bodyData.data.records[0].proposal_status = 'PAID'
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

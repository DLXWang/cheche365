package com.cheche365.cheche.pinganuk.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

/**
 * 删除询价单号
 * Created by wangmz on 2016.10.28
 */
@Component
@Slf4j
class DeleteQuotePolicy extends ADeletePolicy {

    @Override
    protected getRequestParams(context) {
        [
            voucherNo : context.voucherNo
        ]
    }

    @Override
    protected getApiPath() {
        '/icore_pnbs/do/app/workbench/deleteQuotePolicy'
    }

}

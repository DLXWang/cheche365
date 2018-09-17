package com.cheche365.cheche.pinganuk.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

/**
 * 删除投保单号(待缴费状态)
 * Created by wangmz on 2016.10.31
 */
@Component
@Slf4j
class RepAndDelApplied extends ADeletePolicy {

    @Override
    protected getRequestParams(context) {
        [
            applyPolicyNo : context.applyPolicyList.first().applyPolicyNo
        ]
    }

    @Override
    protected getApiPath() {
        '/icore_pnbs/do/app/workbench/repAndDelApplied'
    }

}

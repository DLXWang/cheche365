package com.cheche365.cheche.piccuk.flow.step

import groovy.util.logging.Slf4j

/**
 * 取消投保单
 * Created by liheng on 2016.11.01
 */
@Slf4j
class EditCancelUnderwrite extends ACancelProposal {

    @Override
    protected getApiPath(context) {
        '/prpall/business/editCancelUndwrt.do'
    }

}

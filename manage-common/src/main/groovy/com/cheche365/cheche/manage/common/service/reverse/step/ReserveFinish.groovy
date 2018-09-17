package com.cheche365.cheche.manage.common.service.reverse.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.common.flow.Constants.get_ROUTE_FLAG_DONE
import static com.cheche365.cheche.common.flow.Constants.get_STATUS_CODE_OK

/**
 * Created by yellow on 2017/11/30.
 */
@Service
@Slf4j
class ReserveFinish implements TPlaceInsuranceStep{

    @Transactional
    @Override
    Object run(Object context) {
        log.debug("------流程完成------")
        [_ROUTE_FLAG_DONE, _STATUS_CODE_OK, true, null]
    }
}

package com.cheche365.cheche.partner.service.index

import com.cheche365.cheche.core.model.ApiPartner
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.ApiPartner.Enum.FFAN_PARTNER_40

/**
 * Created by shanxf on 2017/7/20
 */
@Service
class FFanService extends KuaiQianService {

    protected final Logger logger = LoggerFactory.getLogger(getClass())

    @Override
    ApiPartner apiPartner() {
        FFAN_PARTNER_40
    }
}

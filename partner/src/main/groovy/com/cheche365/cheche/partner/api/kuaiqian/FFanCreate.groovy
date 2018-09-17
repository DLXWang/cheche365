package com.cheche365.cheche.partner.api.kuaiqian

import com.cheche365.cheche.core.model.ApiPartner
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.ApiPartner.Enum.FFAN_PARTNER_40

/**
 * Created by shanxf on 2017/7/20
 */
@Service
class FFanCreate extends KuaiQianCreate {

    @Override
    ApiPartner apiPartner() {
        FFAN_PARTNER_40
    }
}

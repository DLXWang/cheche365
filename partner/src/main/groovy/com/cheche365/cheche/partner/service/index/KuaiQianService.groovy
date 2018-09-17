package com.cheche365.cheche.partner.service.index

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.ApiPartner
import com.cheche365.cheche.partner.api.kuaiqian.DesEncryptUtil
import com.cheche365.cheche.partner.handler.index.PartnerIndexParams
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Created by shanxf on 2017/7/20
 */
@Service
class KuaiQianService extends PartnerService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    ApiPartner apiPartner() {
        return ApiPartner.Enum.KUAIQIAN_PARTNER_33
    }

    @Override
    void decryptParam(ApiPartner partner, PartnerIndexParams param) {
        try {
            String encodeMobile = param.mobile
            logger.info("kuai qian encode mobile:{}", encodeMobile)
            String decodeMobile = DesEncryptUtil.decryptPost(encodeMobile)
            logger.info("kuai qian decode mobile:{}", decodeMobile)
            param.put('mobile', decodeMobile)
        } catch (Exception e) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "parse json fail! ");
        }
    }

}

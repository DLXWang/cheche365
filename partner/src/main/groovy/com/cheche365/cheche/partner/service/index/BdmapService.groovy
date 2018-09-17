package com.cheche365.cheche.partner.service.index

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.ApiPartner
import com.cheche365.cheche.core.util.ValidationUtil
import com.cheche365.cheche.partner.config.app.Constant
import com.cheche365.cheche.partner.handler.index.PartnerIndexParams
import com.cheche365.cheche.partner.utils.BaiduEncryptUtil
import org.springframework.stereotype.Service

/**
 * Created by chenxiaozhe on 16-2-19.
 */
@Service
class BdmapService extends PartnerService {

    private static final String LOG_PREFIX = "bdmap log, "

    @Override
    ApiPartner apiPartner() {
        return ApiPartner.Enum.BAIDU_PARTNER_2
    }

    @Override
    void decryptParam(ApiPartner partner, PartnerIndexParams param) {
        String originalMobile = param.mobile?.replaceAll(' ', '+')
        param.put('mobile', BaiduEncryptUtil.deCrypt(originalMobile))
        if (!Constant.BAIDU_NO_MOBILE == param.mobile && !ValidationUtil.validMobile(param.mobile)) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "手机号格式验证失败 " + param.mobile)
        }
        logger.debug("{}解密百度mobile，密文: {}，明文: {}", LOG_PREFIX, originalMobile, param.mobile)
    }

    @Override
    void preHandle(ApiPartner partner, PartnerIndexParams param) {
        super.preHandle(partner, param)

        if (Constant.BAIDU_NO_MOBILE == param.mobile) {
            param.mobile = null
        }
    }
}

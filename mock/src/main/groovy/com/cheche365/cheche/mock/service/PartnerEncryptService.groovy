package com.cheche365.cheche.mock.service

import com.cheche365.cheche.core.model.ApiPartner
import com.cheche365.cheche.partner.api.kuaiqian.DesEncryptUtil
import com.cheche365.cheche.partner.utils.BaiduEncryptUtil

import static com.cheche365.cheche.core.model.ApiPartner.Enum.BAIDU_PARTNER_2
import static com.cheche365.cheche.core.model.ApiPartner.Enum.FFAN_PARTNER_40
import static com.cheche365.cheche.core.model.ApiPartner.Enum.KUAIQIAN_PARTNER_33

class PartnerEncryptService {

    static encrypt(ApiPartner apiPartner,Map params){
        if (KUAIQIAN_PARTNER_33 == apiPartner || FFAN_PARTNER_40 == apiPartner) {
            kuaiQianEncrypt(params)
        } else if (BAIDU_PARTNER_2 == apiPartner){
            bdMapEncrypt(params)
        }
    }

    private static kuaiQianEncrypt(params){
        params.mobile = URLDecoder.decode(DesEncryptUtil.encode(params.mobile as String), "UTF-8")
    }

    private static bdMapEncrypt(params){
        params.bduid = URLDecoder.decode(BaiduEncryptUtil.encrypt(params.uid as String), "UTF-8")
        params.mobile = URLDecoder.decode(BaiduEncryptUtil.encrypt(params.mobile as String), "UTF-8")
    }

}

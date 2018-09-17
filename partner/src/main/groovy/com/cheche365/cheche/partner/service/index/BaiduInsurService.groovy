package com.cheche365.cheche.partner.service.index

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.ApiPartner
import com.cheche365.cheche.partner.handler.index.PartnerIndexParams
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.apache.commons.codec.binary.Base64
import org.springframework.stereotype.Service

import static com.baidu.tool.RsaUtil.decryptByPrivateKey
import static com.cheche365.cheche.core.exception.BusinessException.Code.INPUT_FIELD_NOT_VALID
import static com.cheche365.cheche.core.model.ApiPartner.Enum.BDINSUR_PARTNER_50
import static com.cheche365.cheche.core.model.ApiPartnerProperties.findByPartnerAndKey
import static com.cheche365.cheche.partner.handler.index.PartnerIndexParams.INDEX_PARAMS
import static groovy.json.StringEscapeUtils.unescapeJava

/**
 * 百度保险
 * Created by liheng on 2018/3/14 014.
 */
@Service
class BaiduInsurService extends PartnerService {

    public static String PARTNER_PUBLIC_KEY = 'partner.public.key'
    public static String CHECHE_PRIVATE_KEY = 'cheche.private.key'
    public static String CHECHE_PUBLIC_KEY = 'cheche.public.key'
    public static String PARTNER_SP_NO = 'partner.sp.no'

    @Override
    ApiPartner apiPartner() {
        BDINSUR_PARTNER_50
    }

    @Override
    void decryptParam(ApiPartner partner, PartnerIndexParams param) {
        try {
            def plaintext = unescapeJava decryptByPrivateKey(new String(Base64.decodeBase64(param.param), 'UTF-8'), findByPartnerAndKey(apiPartner(), CHECHE_PRIVATE_KEY).value)
            logger.debug("解密百度请求参数，密文: {}，明文: {}", param.param, plaintext)
            param << new JsonSlurper().parseText(plaintext).with { params ->
                [
                    (INDEX_PARAMS.UID)   : params.userId,
                    (INDEX_PARAMS.MOBILE): params.mobile,
                    (INDEX_PARAMS.STATE) : new JsonBuilder(params.subMap(['applyNo', 'productCode', 'channelId', 'payType', 'promoCode'])).toString()
                ] + (
                    params.subMap(['certType', 'certNo', 'name']).every { it.value } && 'b' == params.certType ?
                        [(INDEX_PARAMS.AUTO): new JsonBuilder([licensePlateNo: null, owner: params.name, identity: params.certNo]).toString()] : [:]
                )
            }
        } catch (e) {
            throw new BusinessException(INPUT_FIELD_NOT_VALID, '解密百度请求参数失败！')
        }
    }
}

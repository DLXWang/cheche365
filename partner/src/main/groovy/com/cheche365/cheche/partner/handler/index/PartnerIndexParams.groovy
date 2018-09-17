package com.cheche365.cheche.partner.handler.index

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.ApiPartner
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.core.util.URLUtils
import groovy.json.JsonSlurper
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.core.constants.TagConstants.PARTNER_TAGS
import static com.cheche365.cheche.core.constants.WebConstants.SESSION_KEY_PARTNER_STATE
import static com.cheche365.cheche.core.exception.BusinessException.Code.INPUT_FIELD_NOT_VALID
import static com.cheche365.cheche.core.exception.BusinessException.Code.INTERNAL_SERVICE_ERROR
import static com.cheche365.cheche.core.model.Channel.Enum.PARTNER_JD
import static com.cheche365.cheche.core.model.Channel.findByApiPartner
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.PICC_10000
import static com.cheche365.cheche.core.service.ChannelService.generatedBaseUrl
import static com.cheche365.cheche.web.service.system.SystemURL.PAYMENT_URL_COMPANY_KEY
import static java.net.URLEncoder.encode

/**
 * Created by zhengwei on 09/10/2017.
 */
class PartnerIndexParams {

    private static final Logger logger = LoggerFactory.getLogger(getClass())

    public static final CHECK_PARAM_CODE_SUCCESS = 1
    public static final CHECK_PARAM_CODE_CONTINUE = CHECK_PARAM_CODE_SUCCESS + 1
    public static final CHECK_PARAM_CODE_ERROR = CHECK_PARAM_CODE_CONTINUE + 1

    public static final String BD_MAP_UID = "bduid"
    public static final String CEB_BANK_UID = "CUST_NBR"

    static final INDEX_PARAMS = [
        UID      : 'uid',
        MOBILE   : 'mobile',
        AUTO     : 'auto',
        COMPANYID: 'companyId',
        STATE    : 'state',
    ]

    //参数别名，处理partner参数名和车车不一致情况，数据结构：Map(key: apiPartner.id, value: Map(partner参数名1: cheche参数名1, partner参数名2: cheche参数名2))
    static final PARAMETER_ALIAS = [
        2L : [(BD_MAP_UID): INDEX_PARAMS.UID],
        38L: [(CEB_BANK_UID): INDEX_PARAMS.UID]
    ]

    // ApiPartner tag关系映射，用于检查tag配置逻辑是否正确 interaction：互斥tag、dependence：依赖tag、correlationParams：相关参数
    private static final API_PARTNER_TAG_RELEVANCE_MAPPING = [
        (PARTNER_TAGS.NO_PARAMETER)       : [
            interaction: [PARTNER_TAGS.WITH_USER, PARTNER_TAGS.WITH_AUTO, PARTNER_TAGS.SINGLE_COMPANY, PARTNER_TAGS.NEED_DECRYPT, PARTNER_TAGS.WITH_STATE, PARTNER_TAGS.REDIRECT_WITH_UID]
        ],
        (PARTNER_TAGS.WITH_USER)          : [
            interaction      : [PARTNER_TAGS.NO_PARAMETER, PARTNER_TAGS.CREATE_PARTNER_USER],
            correlationParams: [INDEX_PARAMS.UID, INDEX_PARAMS.MOBILE]
        ],
        (PARTNER_TAGS.WITH_AUTO)          : [
            interaction      : [PARTNER_TAGS.NO_PARAMETER],
            correlationParams: [INDEX_PARAMS.AUTO]
        ],
        (PARTNER_TAGS.SINGLE_COMPANY)     : [
            interaction      : [PARTNER_TAGS.NO_PARAMETER],
            correlationParams: [INDEX_PARAMS.COMPANYID]
        ],
        (PARTNER_TAGS.NEED_DECRYPT)       : [
            interaction      : [PARTNER_TAGS.NO_PARAMETER],
            dependence       : [PARTNER_TAGS.WITH_USER],
            correlationParams: [INDEX_PARAMS.UID, INDEX_PARAMS.MOBILE]
        ],
        (PARTNER_TAGS.WITH_STATE)         : [
            interaction      : [PARTNER_TAGS.NO_PARAMETER],
            correlationParams: [INDEX_PARAMS.STATE]
        ],
        (PARTNER_TAGS.REDIRECT_WITH_UID)  : [
            interaction      : [PARTNER_TAGS.NO_PARAMETER],
            dependence       : [PARTNER_TAGS.WITH_USER],
            correlationParams: [INDEX_PARAMS.UID]
        ],
        (PARTNER_TAGS.CREATE_PARTNER_USER): [
            interaction: [PARTNER_TAGS.WITH_USER]
        ],
        default                           : [
            interaction      : [],
            dependence       : [],
            correlationParams: []
        ]
    ]

    // 检查首页所需参数是否为空，如果为空跳过后续处理
    static final DEFAULT_CHECK_PARAM = { partnerTag, param ->
        checkPartnerIndexParams(partnerTag, param) ? CHECK_PARAM_CODE_SUCCESS : CHECK_PARAM_CODE_CONTINUE
    }

    // 首页参数不做校验
    static final NON_CHECK_PARAM = { partnerTag, param ->
        CHECK_PARAM_CODE_SUCCESS
    }

    static final DEFAULT_PARAMS_PROCESSOR_CLOSURE = { logger, partner, session, partnerService, param ->
        logger.debug 'partner: {} 无需处理参数，直接跳转', partner.code
    }
    static final NEED_DECRYPT_TAG_PARAMS_PROCESSOR_CLOSURE = { logger, partner, session, partnerService, param ->
        partnerService.decryptParam partner, param
    }
    static final WITH_USER_TAG_PARAMS_PROCESSOR_CLOSURE = { logger, partner, session, partnerService, param ->
        param.with {
            logger.debug ' uid contains +, will replace with white space, before {}', it.uid
            it.uid = it.uid?.trim()?.contains('+') ? it.uid.replace('+', ' ') : it.uid
            it
        }
        def user = partnerService.createOrUpdateUser partner, param
        if (user != null) {
            CacheUtil.cacheUser session, user
            partnerService.cacheChannelAgent user, partner, session
            session.setAttribute WebConstants.SESSION_KEY_PARTNER_UID, param.uid
            logger.debug ' {} log : [用户登陆结束,用户id:[{}],手机号:[{}]] ', partner.code, user.id, user.mobile
        } else {
            logger.debug ' {} log : [用户登陆结束, no login] ', partner.code
        }
    }

    static final WITH_AUTO_TAG_PARAMS_PROCESSOR_CLOSURE = { logger, partner, session, partnerService, param ->
        param.with {
            it << new JsonSlurper().parseText(it.auto)
        }
        def user = partnerService.createOrUpdateUser partner, param.uid, param.mobile
        partnerService.fillUpAuto param, user
        param.auto = encode(param.auto, "UTF-8")
    }
    static final SINGLE_COMPANY_TAG_PARAMS_PROCESSOR_CLOSURE = { logger, partner, session, partnerService, param ->
        if (partner.singleCompany()) {
            session.setAttribute PAYMENT_URL_COMPANY_KEY, param.companyId
            logger.debug " e's log : [{}] companyId:{}", partner.code, param.companyId
            param.companyId = param.companyId ? Long.valueOf(param.companyId) : PICC_10000.id
        }
    }
    static final WITH_STATE_TAG_PARAMS_PROCESSOR_CLOSURE = { logger, partner, session, partnerService, param ->
        session.setAttribute SESSION_KEY_PARTNER_STATE, param.state
    }

    static final DEFAULT_URI_BUILDER = { queryStrPart, param, builder -> builder }
    static final QUERY_STRING_PART_BUILDER = { paramName, queryStrPart, param, builder ->
        queryStrPart && param[paramName] ? builder.queryParam(paramName, param[paramName]) : builder
    }
    static final HASH_PART_BUILDER = { fragment, queryStrPart, param, builder ->
        builder.fragment fragment
    }

    // 包含3个闭包：参数校验、参数相关处理、url构建
    static final PARTNER_INDEX_CHECK_HANDLES = [
        [PARTNER_TAGS.NEED_DECRYPT, NON_CHECK_PARAM, NEED_DECRYPT_TAG_PARAMS_PROCESSOR_CLOSURE, [DEFAULT_URI_BUILDER]],
        [PARTNER_TAGS.WITH_USER, NON_CHECK_PARAM, WITH_USER_TAG_PARAMS_PROCESSOR_CLOSURE, [DEFAULT_URI_BUILDER]],
        [PARTNER_TAGS.WITH_AUTO, DEFAULT_CHECK_PARAM, WITH_AUTO_TAG_PARAMS_PROCESSOR_CLOSURE, [QUERY_STRING_PART_BUILDER.curry(INDEX_PARAMS.AUTO), DEFAULT_URI_BUILDER]],
        [PARTNER_TAGS.SINGLE_COMPANY, NON_CHECK_PARAM, SINGLE_COMPANY_TAG_PARAMS_PROCESSOR_CLOSURE, [QUERY_STRING_PART_BUILDER.curry(INDEX_PARAMS.COMPANYID)]],
        [PARTNER_TAGS.REDIRECT_WITH_UID, DEFAULT_CHECK_PARAM, DEFAULT_PARAMS_PROCESSOR_CLOSURE, [DEFAULT_URI_BUILDER]],
        [PARTNER_TAGS.WITH_STATE, DEFAULT_CHECK_PARAM, WITH_STATE_TAG_PARAMS_PROCESSOR_CLOSURE, [DEFAULT_URI_BUILDER]],
    ]

    @Delegate
    Map unifiedParams

    static final getApiPartnerTagRelevance(partnerTag) {
        API_PARTNER_TAG_RELEVANCE_MAPPING[partnerTag].with {
            mergeMaps it, API_PARTNER_TAG_RELEVANCE_MAPPING.default
        }
    }

    PartnerIndexParams(ApiPartner partner, String queryString) {
        unifiedParams = URLUtils.splitQuery(queryString).collectEntries {
            if (PARAMETER_ALIAS.get(partner.id)?.get(it.key)) {
                [PARAMETER_ALIAS.get(partner.id).get(it.key), it.value]
            } else {
                it
            }
        }
    }

    /**
     * 检查partner tag配置是否正确
     * @param partnerTag
     * @param partner
     * @return
     */
    static final checkPartnerTag(partnerTag, partner) {
        partner.tag && (partner.tag & partnerTag.mask) && getApiPartnerTagRelevance(partnerTag).with {
            it.interaction.every { tag -> !(partner.tag & tag.mask) } && it.dependence.every { tag -> partner.tag & tag.mask }
        }.with {
            if (!it) {
                throw new BusinessException(INTERNAL_SERVICE_ERROR, "第三方合作渠道[${partner.code}][${partnerTag.desc}]配置有误")
            }
            it
        }
    }

    /**
     * 检查tag依赖参数是否为空
     * @param partnerTag
     * @param params
     * @return
     */
    static final checkPartnerIndexParams(partnerTag, params) {
        getApiPartnerTagRelevance(partnerTag).correlationParams.any { paramName -> params?.get(paramName) }
    }

    def generateUrl(partner, queryStrPart, fragment, uriBuilder, partnerService, session, params = this) {
        PARTNER_INDEX_CHECK_HANDLES.inject(uriBuilder) { builder, checkClosureHandles ->
            def (partnerTag, checkParamsClosure, paramsProcessorClosure, uriBuilderClosure) = checkClosureHandles
            if (CHECK_PARAM_CODE_SUCCESS == checkParamsClosure(partnerTag, params)) {
                paramsProcessorClosure logger, partner, session, partnerService, params
            } else if (CHECK_PARAM_CODE_ERROR == checkParamsClosure(partnerTag, params)) {
                throw new BusinessException(INPUT_FIELD_NOT_VALID, "第三方合作渠道[${partner.code}][${params}]参数有误")
            }
            uriBuilderClosure.inject(builder) { b, bc ->
                bc queryStrPart, params, b
            }
        }.with {
            if (partner.redirectWithUid()) {
                QUERY_STRING_PART_BUILDER(INDEX_PARAMS.UID, queryStrPart, params, it)
            }
            if ('ershouche' == partner.code) {
                QUERY_STRING_PART_BUILDER('status', queryStrPart, params, it)
                session.setAttribute SESSION_KEY_PARTNER_STATE, params.status
            }
            if (PARTNER_JD == findByApiPartner(partner)) {
                it.queryParam 'shareLink', encode(generatedBaseUrl(findByApiPartner(partner)) + (params.state ? '?state=' + params.state : ''), 'UTF-8')
            }
            if (partner.withAuto()) {
                it.fragment 'base'
            }
            if (partner.toPhotoPage()) {
                it.fragment 'photo'
            }
            it.fragment(fragment ?: it.fragment)
        }.build().toString()
    }

}

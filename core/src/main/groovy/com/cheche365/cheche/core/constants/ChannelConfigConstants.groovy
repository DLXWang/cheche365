package com.cheche365.cheche.core.constants

import static com.cheche365.cheche.core.constants.TagConstants.CHANNEL_TAGS
import static com.cheche365.cheche.core.constants.TagConstants.PARTNER_TAGS

/**
 * Created by liheng on 2018/4/14 0014.
 */
class ChannelConfigConstants {

    final static String PARTNER_ = 'PARTNER_'
    final static String ORDER_CENTER_ = 'ORDER_CENTER_'

    //<editor-fold defaultstate="collapsed" desc="渠道配置参数">
    static final String CHANNEL_CODE = 'channelCode'                   // String 第三方渠道英文简称
    static final String CHANNEL_NAME = 'channelName'                   // String 第三方渠道名称
    static final String AGENT = 'agent'                                // Boolean 是否toA
    static final String HAS_ORDER_CENTER = 'hasOrderCenter'            // Boolean 出单中心是否可下单
    static final String SINGLE_COMPANY = 'singleCompany'               // Boolean 是否直投
    static final String NEED_SYNC_ORDER = 'needSyncOrder'              // Boolean 是否支持订单同步
    static final String SYNC_ORDER_URL = 'syncOrderUrl'                // String 第三方订单同步url
    static final String NEED_EMAIL_SYNC = 'needEmailSync'              // Boolean 是否支持邮件同步
    static final String SYNC_ORDER_EMAILS = 'syncOrderEmails'          // List 第三方订单同步邮箱
    static final String SUPPORT_AMEND = 'supportAmend'                 // Boolean 是否支持增补

    // ToA
    static final String LEVEL_AGENT = 'levelAgent'                     // Boolean 是否支持三级管理
    static final String REBATE_INTO_WALLET = 'rebateIntoWallet'        // Boolean 返点进钱包

    static final String DISABLED_CHANNEL = 'disabledChannel'           // Boolean 渠道禁用
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="渠道tag配置">
    static final hasTag(model, tag) {
        model.tag & tag.mask
    }

    static final modifyTag(param, model, tag) {
        param ? (model.tag |= tag.mask) : (model.tag -= (hasTag(model, tag) ? tag.mask : 0))
    }

    static final CHANNEL_TYPE_CONFIG_HANDLE = { param, channel ->
        if (param) {
            channel.tag |= CHANNEL_TAGS.AGENT.mask
            channel.tag |= CHANNEL_TAGS.STANDARD_AGENT.mask
        }
    }

    static final CHANNEL_TAG_CONFIG_HANDLE = { tag, param, channel ->
        modifyTag param, channel, tag
    }

    static final PARTNER_TAG_CONFIG_HANDLE = { tag, param, channel ->
        modifyTag param, channel.apiPartner, tag
    }

    static final CHANNEL_CHECK_TAG_HANDLE = { tag, channel ->
        (channel.tag & tag.mask) as Boolean
    }

    static final PARTNER_CHECK_TAG_HANDLE = { tag, channel ->
        (channel.apiPartner.tag & tag.mask) as Boolean
    }

    static final CHECK_PRECONDITION_DEFAULT = { channel, params -> true }

    static final CHECK_PRECONDITION_TOA = { channel, params -> hasTag channel, CHANNEL_TAGS.AGENT }

    static final CHECK_PRECONDITION_TOC = { channel, params -> !hasTag (channel, CHANNEL_TAGS.AGENT) }

    /**
     * tag相关参数处理
     * value：渠道参数生成处理器、参数配置前置校验、参数配置处理器
     */
    static final PARAMS_UNCHANGEABLE_TAG_HANDLE_MAPPING = [
        (AGENT)             : [CHANNEL_CHECK_TAG_HANDLE.curry(CHANNEL_TAGS.AGENT), CHECK_PRECONDITION_DEFAULT, CHANNEL_TYPE_CONFIG_HANDLE],
        (SINGLE_COMPANY)    : [PARTNER_CHECK_TAG_HANDLE.curry(PARTNER_TAGS.SINGLE_COMPANY), CHECK_PRECONDITION_DEFAULT, PARTNER_TAG_CONFIG_HANDLE.curry(PARTNER_TAGS.SINGLE_COMPANY)],
        (LEVEL_AGENT)       : [CHANNEL_CHECK_TAG_HANDLE.curry(CHANNEL_TAGS.LEVEL_AGENT), CHECK_PRECONDITION_TOA, CHANNEL_TAG_CONFIG_HANDLE.curry(CHANNEL_TAGS.LEVEL_AGENT)],
        (REBATE_INTO_WALLET): [CHANNEL_CHECK_TAG_HANDLE.curry(CHANNEL_TAGS.REBATE_INTO_WALLET), CHECK_PRECONDITION_TOA, CHANNEL_TAG_CONFIG_HANDLE.curry(CHANNEL_TAGS.REBATE_INTO_WALLET)],
    ]

    static final PARAMS_CHANGEABLE_TAG_HANDLE_MAPPING = [
        (NEED_SYNC_ORDER) : [PARTNER_CHECK_TAG_HANDLE.curry(PARTNER_TAGS.NEED_SYNC_ORDER), CHECK_PRECONDITION_DEFAULT, PARTNER_TAG_CONFIG_HANDLE.curry(PARTNER_TAGS.NEED_SYNC_ORDER)],
        (NEED_EMAIL_SYNC) : [PARTNER_CHECK_TAG_HANDLE.curry(PARTNER_TAGS.NEED_EMAIL_SYNC), CHECK_PRECONDITION_DEFAULT, PARTNER_TAG_CONFIG_HANDLE.curry(PARTNER_TAGS.NEED_EMAIL_SYNC)],
        (DISABLED_CHANNEL): [CHANNEL_CHECK_TAG_HANDLE.curry(CHANNEL_TAGS.DISABLED_CHANNEL), CHECK_PRECONDITION_DEFAULT, CHANNEL_TAG_CONFIG_HANDLE.curry(CHANNEL_TAGS.DISABLED_CHANNEL)],
        (SUPPORT_AMEND)   : [PARTNER_CHECK_TAG_HANDLE.curry(PARTNER_TAGS.SUPPORT_AMEND), CHECK_PRECONDITION_TOC, PARTNER_TAG_CONFIG_HANDLE.curry(PARTNER_TAGS.SUPPORT_AMEND)],
    ]

    static final PARAMS_TAG_HANDLE_MAPPING = PARAMS_UNCHANGEABLE_TAG_HANDLE_MAPPING + PARAMS_CHANGEABLE_TAG_HANDLE_MAPPING

    static final ORDER_CENTER_CHANNEL_SYNCHRONOUS_CHANGE_PARAMS = [DISABLED_CHANNEL]

    static configTag(configMapping, params, channel, orderCenterChannel = null) {
        configMapping.each { key, value ->
            def (_0, checkPrecondition, handle) = value
            if (checkPrecondition(channel, params) && params.keySet().contains(key)) {
                handle params[key], channel
                if (orderCenterChannel && ORDER_CENTER_CHANNEL_SYNCHRONOUS_CHANGE_PARAMS.contains(key)) {
                    handle params[key], orderCenterChannel
                }
            }
        }
        if (hasTag(channel, CHANNEL_TAGS.LEVEL_AGENT)) {
            modifyTag true, channel, CHANNEL_TAGS.REBATE_INTO_WALLET
        }
    }

    static getTagParams(channel) {
        PARAMS_TAG_HANDLE_MAPPING.collectEntries { key, value ->
            [(key): value.first()(channel)]
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="渠道参数检验">
    static final CHECK_CONFIG_MAPPING_BASE = [
        ({ params, channel, apiPartnerRepository -> params[NEED_SYNC_ORDER] && !params[SYNC_ORDER_URL]
        }): [code: SYNC_ORDER_URL, msg: '配置订单同步必须配置订单同步url！'],
        ({ params, channel, apiPartnerRepository ->
            params[NEED_SYNC_ORDER] && params[SYNC_ORDER_URL] && !(params[SYNC_ORDER_URL] =~ /^(https?):\/\/[\w\-]+(\.[\w\-]+)+([\w\-\\.,@?^=%&:\/~\+#]*[\w\-\@?^=%&\/~\+#])?$/).matches()
        }): [code: SYNC_ORDER_URL, msg: '第三方订单同步url格式不正确！'],
        ({ params, channel, apiPartnerRepository -> params[NEED_EMAIL_SYNC] && !params[SYNC_ORDER_EMAILS]
        }): [code: SYNC_ORDER_EMAILS, msg: '配置邮件同步必须配置同步邮箱！'],
        ({ params, channel, apiPartnerRepository ->
            params[NEED_EMAIL_SYNC] && params[SYNC_ORDER_EMAILS] && params[SYNC_ORDER_EMAILS].any { syncOrderEmail ->
                !(syncOrderEmail =~ /^[\w\-]+@[\w\-]+(\.[\w\-]+)+$/).matches()
            }
        }): [code: SYNC_ORDER_EMAILS, msg: '第三方订单同步邮箱格式不正确！']
    ]

    static final CHECK_CONFIG_MAPPING_CREATE = [
        ({ params, channel, apiPartnerRepository -> !params[CHANNEL_CODE] }): [code: CHANNEL_CODE, msg: '第三方渠道英文简称不能为空！'],
        ({ params, channel, apiPartnerRepository ->
            !(params[CHANNEL_CODE] =~ /[a-z0-9]{1,30}/).matches()
        })                                                                  : [code: CHANNEL_CODE, msg: '第三方渠道英文简称只能输入英文字母(小写)和数字，最多输入30字符！'],
        ({ params, channel, apiPartnerRepository -> !params[CHANNEL_NAME] }): [code: CHANNEL_NAME, msg: '第三方渠道名称不能为空！'],
        ({ params, channel, apiPartnerRepository ->
            !(params[CHANNEL_NAME] =~ /.{1,200}/).matches()
        })                                                                  : [code: CHANNEL_NAME, msg: '第三方渠道名称最多输入200字符！'],
        ({ params, channel, apiPartnerRepository ->
            apiPartnerRepository.findFirstByCode params[CHANNEL_CODE]
        })                                                                  : [code: CHANNEL_CODE, msg: '此第三方渠道英文简称已存在，请重新填写'],
        ({ params, channel, apiPartnerRepository ->
            apiPartnerRepository.findFirstByDescription params[CHANNEL_NAME]
        })                                                                  : [code: CHANNEL_NAME, msg: '第三方渠道名称已存在，请重新填写']
    ] + CHECK_CONFIG_MAPPING_BASE

    static final CHECK_CONFIG_MAPPING_MODIFY = [
        ({ params, channel, apiPartnerRepository -> !channel.isPartnerChannel() }): [code: 'channel', msg: '只能修改H5渠道！']
    ] + CHECK_CONFIG_MAPPING_BASE

    static checkConfig(checkMapping, params, channel, apiPartnerRepository) {
        checkMapping.find { key, value -> key params, channel, apiPartnerRepository }?.value
    }
    //</editor-fold>
}

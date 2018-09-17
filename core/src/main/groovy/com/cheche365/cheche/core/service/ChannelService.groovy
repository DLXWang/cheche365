package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.model.ApiPartner
import com.cheche365.cheche.core.model.ApiPartnerProperties
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.Partner
import com.cheche365.cheche.core.repository.ApiPartnerPropertiesRepository
import com.cheche365.cheche.core.repository.ApiPartnerRepository
import com.cheche365.cheche.core.repository.ChannelRepository
import com.cheche365.cheche.core.repository.QuoteFlowConfigRepository
import com.cheche365.cheche.core.util.RuntimeUtil
import groovy.text.GStringTemplateEngine
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.constants.ChannelConfigConstants.CHANNEL_CODE
import static com.cheche365.cheche.core.constants.ChannelConfigConstants.CHANNEL_NAME
import static com.cheche365.cheche.core.constants.ChannelConfigConstants.CHECK_CONFIG_MAPPING_CREATE
import static com.cheche365.cheche.core.constants.ChannelConfigConstants.CHECK_CONFIG_MAPPING_MODIFY
import static com.cheche365.cheche.core.constants.ChannelConfigConstants.HAS_ORDER_CENTER
import static com.cheche365.cheche.core.constants.ChannelConfigConstants.ORDER_CENTER_
import static com.cheche365.cheche.core.constants.ChannelConfigConstants.PARAMS_CHANGEABLE_TAG_HANDLE_MAPPING
import static com.cheche365.cheche.core.constants.ChannelConfigConstants.PARAMS_TAG_HANDLE_MAPPING
import static com.cheche365.cheche.core.constants.ChannelConfigConstants.PARTNER_
import static com.cheche365.cheche.core.constants.ChannelConfigConstants.SYNC_ORDER_EMAILS as PARAMS_SYNC_ORDER_EMAILS
import static com.cheche365.cheche.core.constants.ChannelConfigConstants.SYNC_ORDER_URL as PARAMS_SYNC_ORDER_URL
import static com.cheche365.cheche.core.constants.ChannelConfigConstants.checkConfig
import static com.cheche365.cheche.core.constants.ChannelConfigConstants.configTag
import static com.cheche365.cheche.core.constants.ChannelConfigConstants.getTagParams
import static com.cheche365.cheche.core.constants.TagConstants.CHANNEL_TAGS
import static com.cheche365.cheche.core.constants.WebConstants.domainURL
import static com.cheche365.cheche.core.constants.WebConstants.getDomain
import static com.cheche365.cheche.core.model.ApiPartnerProperties.Key.SYNC_APP_ID
import static com.cheche365.cheche.core.model.ApiPartnerProperties.Key.SYNC_APP_SECRET
import static com.cheche365.cheche.core.model.ApiPartnerProperties.Key.SYNC_ORDER_EMAIL
import static com.cheche365.cheche.core.model.ApiPartnerProperties.Key.SYNC_ORDER_URL
import static com.cheche365.cheche.core.model.ApiPartnerProperties.Key.SYNC_SIGN_METHOD
import static com.cheche365.cheche.core.model.ApiPartnerProperties.findByPartnerAndKey
import static com.cheche365.cheche.core.model.Channel.toOrderCenterChannel
import static java.nio.file.Paths.get
import static java.util.UUID.randomUUID
import static javax.ws.rs.core.UriBuilder.fromUri

/**
 * Created by liheng on 2018/4/14 0014.
 */
@Service
@Slf4j
class ChannelService {

    @Autowired
    private ApiPartnerRepository apiPartnerRepository

    @Autowired
    private ChannelRepository channelRepository

    @Autowired
    private ApiPartnerPropertiesRepository apiPartnerPropertiesRepository

    @Autowired
    private ResourceService resourceService

    @Autowired
    private QuoteFlowConfigRepository quoteFlowConfigRepository

    /**
     * 配置第三方渠道
     * @param partner
     * @param params
     *          [
     *              channelCode:'test10',
     *              channelName:'渠道10',
     *              agent:false,
     *              hasOrderCenter:true,
     *              singleCompany:true,
     *              needSyncOrder:true,
     *              syncOrderUrl:'https://www.cheche365.com',
     *              needEmailSync:true,
     *              syncOrderEmails:['test1@cheche365.com','test2@cheche365.com'],
     *              supportAmend:true,
     *              levelAgent:true,
     *              rebateIntoWallet:true
     *          ]
     * @param channel 修改配置时必传，可修改参数：needSyncOrder、syncOrderUrl、needEmailSync、syncOrderEmails、disabledChannel（渠道禁用）、supportAmend
     * @return
     */
    Tuple config(Partner partner, Map params, Channel channel = null) {
        log.info '合作商：{} {}渠道{} 配置内容：{}', partner.name, channel ? '修改' : '添加', channel ? channel.description : '', params
        if (channel) {
            def msg = checkConfig CHECK_CONFIG_MAPPING_MODIFY, params, channel, apiPartnerRepository
            if (msg) {
                log.error '渠道修改失败：{}', msg
                return new Tuple([false, msg])
            }
            def orderCenterChannel = toOrderCenterChannel(channel.id)
            configTag PARAMS_CHANGEABLE_TAG_HANDLE_MAPPING, params, channel, orderCenterChannel
            channelRepository.save channel
            apiPartnerRepository.save channel.apiPartner
            if (orderCenterChannel) {
                channelRepository.save orderCenterChannel
            }

            configPartnerProperties channel.apiPartner, params
        } else {
            def channelCode = params[CHANNEL_CODE]
            def msg = checkConfig CHECK_CONFIG_MAPPING_CREATE, params, channel, apiPartnerRepository
            if (msg) {
                log.error '渠道添加失败：{}', msg
                return new Tuple([false, msg])
            }
            def apiPartner = new ApiPartner(
                code: channelCode,
                description: params[CHANNEL_NAME],
                tag: 0
            )
            channel = new Channel(
                name: PARTNER_ + channelCode.toUpperCase(),
                tag: CHANNEL_TAGS.PARTNER_H5.mask,
                icon: channelCode + '.png',
                description: params[CHANNEL_NAME],
                apiPartner: apiPartner,
                partner: partner
            )
            configTag PARAMS_TAG_HANDLE_MAPPING, params, channel
            apiPartnerRepository.save channel.apiPartner
            channel.parent = channel
            channelRepository.save channel

            if (params[HAS_ORDER_CENTER]) {
                channelRepository.save new Channel(
                    name: ORDER_CENTER_ + channelCode.toUpperCase(),
                    tag: channel.tag + CHANNEL_TAGS.ORDER_CENTER.mask - CHANNEL_TAGS.PARTNER_H5.mask,
                    icon: channel.icon,
                    description: params[CHANNEL_NAME] + '-出单中心',
                    apiPartner: apiPartner,
                    parent: channel,
                    partner: partner
                )
            }

            configPartnerProperties channel.apiPartner, params
        }
        log.info '{}配置成功', channel.description
        new Tuple([true, [msg: '渠道配置成功！', channel: channel]])
    }

    static Map getConfig(Channel channel) {
        if (channel.isPartnerChannel()) {
            [
                (CHANNEL_CODE)            : channel.apiPartner.code,
                (CHANNEL_NAME)            : channel.apiPartner.description,
                (HAS_ORDER_CENTER)        : toOrderCenterChannel(channel.id) as boolean,
                (PARAMS_SYNC_ORDER_URL)   : channel.apiPartner.needSyncOrder() ? findByPartnerAndKey(channel.apiPartner, SYNC_ORDER_URL)?.value : null,
                (PARAMS_SYNC_ORDER_EMAILS): channel.apiPartner.needEmailSync() ? findByPartnerAndKey(channel.apiPartner, SYNC_ORDER_EMAIL)?.value?.split(',')?.toList() : null
            ] + getTagParams(channel)
        } else {
            [:]
        }
    }

    /**
     * 生成渠道基本报价链接
     * @param channel
     * @return
     */
    static String generatedBaseUrl(Channel channel) {
        fromUri('https://' + getDomain()).path('partner').path(channel.apiPartner.code).path('index').build().toString()
    }

    /**
     * 生成渠道配置文档
     * @param channel
     * @return 文档下载链接
     */
    String generatedConfigFile(Channel channel) {
        def rootPath = get(resourceService.properties.rootPath, 'channel', 'config')
        def fileName = channel.apiPartner.code + '_config.txt'
        def filePath = get(rootPath.toString(), fileName).toString()
        new File(filePath).newPrintWriter().with {
            it << new GStringTemplateEngine().createTemplate(new File(get(rootPath.toString(), 'template', 'channelTemplate.txt').toString())).make([
                channel           : channel,
                baseUrl           : generatedBaseUrl(channel),
                syncSignMethod    : findByPartnerAndKey(channel.apiPartner, SYNC_SIGN_METHOD),
                syncAppId         : findByPartnerAndKey(channel.apiPartner, SYNC_APP_ID),
                syncAppSecret     : findByPartnerAndKey(channel.apiPartner, SYNC_APP_SECRET),
                syncOrderUrl      : findByPartnerAndKey(channel.apiPartner, SYNC_ORDER_URL),
                syncOrderEmail    : findByPartnerAndKey(channel.apiPartner, SYNC_ORDER_EMAIL),
                insuranceCompanies: quoteFlowConfigRepository.findInsuranceCompanyByChannel(channel)
            ])
            it.flush()
            it.close()
        }
        filePath
    }

    private configPartnerProperties(ApiPartner partner, Map params) {
        if (partner.needSyncOrder()) {
            def syncOrderUrl = findByPartnerAndKey(partner, SYNC_ORDER_URL)
            if (syncOrderUrl) {
                apiPartnerPropertiesRepository.save syncOrderUrl.with {
                    it.value = params[PARAMS_SYNC_ORDER_URL]
                    it
                }
            } else {
                def (appId, appSecret) = randomUUID().toString().split('-', 2)
                [
                    (getPropertyKey(SYNC_SIGN_METHOD)): 'HMAC-SHA1',
                    (getPropertyKey(SYNC_APP_ID))     : appId,
                    (getPropertyKey(SYNC_APP_SECRET)) : appSecret,
                    (getPropertyKey(SYNC_ORDER_URL))  : params[PARAMS_SYNC_ORDER_URL]
                ].each { key, value ->
                    apiPartnerPropertiesRepository.save new ApiPartnerProperties(partner: partner, key: key, value: value)
                }
            }
        }
        if (partner.needEmailSync()) {
            apiPartnerPropertiesRepository.save findByPartnerAndKey(partner, SYNC_ORDER_EMAIL)?.with {
                it.value = params[PARAMS_SYNC_ORDER_EMAILS].join(',')
                it
            } ?: new ApiPartnerProperties(partner: partner, key: SYNC_ORDER_EMAIL, value: params[PARAMS_SYNC_ORDER_EMAILS].join(','))
        }
    }

    private static getPropertyKey(String key) {
        RuntimeUtil.isProductionEnv() ? 'production.' + key : key
    }

    List<Channel> selectAll() {
        return channelRepository.findAll()
    }
}

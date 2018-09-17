package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.mongodb.repository.MoDisplayMessageRepository
import com.cheche365.cheche.core.repository.*
import com.cheche365.cheche.core.service.image.ImgAssembleSrcService
import com.cheche365.cheche.core.util.FileUtil
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

import java.util.stream.Collectors

/**
 * Created by Jason on 05/05/2016.
 */
@Service
class ModuleService {

    private Logger logger = LoggerFactory.getLogger(getClass())

    @Autowired
    DisplayMessageService displayMessageService
    @Autowired
    IResourceService resourceService
    @Autowired
    GiftAreaRepository giftAreaRepository
    @Autowired
    MarketingRepository marketingRepository
    @Autowired
    AreaRepository areaRepository
    @Autowired
    InsuranceCompanyRepository insuranceCompanyRepository
    @Autowired
    MarketingRuleRepository marketingRuleRepository
    @Autowired
    DisplayMessageRepository displayMessageRepository
    @Autowired
    QuoteFlowConfigRepository quoteFlowConfigRepository
    @Autowired
    MoDisplayMessageRepository mdmRepository

    @Autowired
    MessageTypeRepository messageTypeRepository

    @Autowired
    private SystemCountService systemCountService

    @Cacheable(value = "moDisplayMessage",keyGenerator = "cacheKeyGenerator")
    Object homeMessages(String keyName, BusinessActivity businessActivity, Channel channel) {

        MessageType messageType = messageTypeRepository.findFirstByType(keyName)
        if (null == messageType) {
            logger.debug("can't find keyName message, keyName={}", keyName)
            return null
        }

        if(messageType.id == 32l){
            //临时解决方案,因为车主福利以后还要用
            logger.info("车主福利暂且搁置")
            return null
        }

        MoDisplayMessage moDisplayMessage = mdmRepository.findByMessageType(messageType.id)
        if (!moDisplayMessage) {
            return null
        }

        List<Map> list = moDisplayMessage.message

        if (MessageType.Enum.COMPANY == messageType) {
            list = byChannelFilterInsuranceCompany(list, channel)
        }

        if (MessageType.Enum.A_HOME_TOP_IMAGE == messageType) {
            if (!Channel.agentLevelChannels().contains(channel)) {
                list = list?.findAll {
                    it.channel != "67"
                }
            } else {
                list = list?.findAll {
                    it.channel != "partner_toa"
                }
            }
        }

        processMessage(list, channel, moDisplayMessage.version)
        ImgAssembleSrcService.handleChannelType(list, businessActivity, channel)

        list = list.sort(false) {
            a, b -> Integer.valueOf(b.weight) <=> Integer.valueOf(a.weight)
        }
        return list
    }
    /**
     *  为了兼容车保易老版app 强升之后 此段可删
     *  TODO
     * @param messageList
     * @param version
     * @return
     */
    Object filterToaBanner(Object messageList,version ){
        messageList.findAll{
            it.version == version
        }
    }
    private List<Map> byChannelFilterInsuranceCompany(List<Map> list, Channel channel) {
        logger.info("handle old company channel:{},display message list :{}", channel, list)
        List<QuoteFlowConfig> quoteFlowConfigs = quoteFlowConfigRepository.findByChannel(channel.getParent())
        List<Long> insuranceCompanyIds = []
        quoteFlowConfigs.each { it -> insuranceCompanyIds.add(it.insuranceCompany.id) }
        list = list.findAll {
            insuranceCompanyIds.stream().distinct().collect(Collectors.toList()).contains(it.id)
        }
        return list
    }

    private void processMessage(List<Map> list, Channel channel, String version) {
        list.each {
            if (it.iconUrl) {
                it.iconUrl    = getAbsoluteUrl(it.iconUrl,version)
            }
            if(it.icon){
                it.icon = getAbsoluteUrl(it.icon,version)
            }
            if (it.url&&!it.url.toString().startsWith("http")) {
                it.url = displayMessageService.genLinkUrl(it.url.toString())
            }
        }
    }

    private String getAbsoluteUrl(String  url,String version) {
        return resourceService.absoluteUrl(
            resourceService.getResourceAbsolutePath(
                resourceService.getProperties().getIosPath()
            ), url + "?version=" + version
        )
    }

    def findBaseBanner(String companyId, Channel channel) {
        List<String> banners = []
        String bannerPath = resourceService.getResourceAbsolutePath(resourceService.getProperties().getBannerPath())
        def baseBannerResource = [
            "channel": channel,
            "marketingCode": null,
            "action": "NONE",
            "banner": null,
            "popImg": null,
            "resourceUrl": null
        ]

        InsuranceCompany insuranceCompany = StringUtils.isEmpty(companyId) ? null : insuranceCompanyRepository.findOne(Long.valueOf(companyId))

        if (insuranceCompany) {
            banners.add(bannerPath + insuranceCompany.id + '_new.jpg')
        }
        banners.add(bannerPath + 'banner_' + channel.id + '.jpg')
        banners.add(bannerPath + 'base-info-banner3.jpg')
        
        def bannerUrl = banners.find { FileUtil.isExist(it) }
        if (bannerUrl) {
            MoDisplayMessage moDisplayMessage = mdmRepository.findByMessageType(MessageType.Enum.COMPANY.id)
            baseBannerResource.banner = resourceService.absoluteUrl(bannerUrl, '')+"?version=" + moDisplayMessage.version
            return baseBannerResource
        }
        return null
    }

}

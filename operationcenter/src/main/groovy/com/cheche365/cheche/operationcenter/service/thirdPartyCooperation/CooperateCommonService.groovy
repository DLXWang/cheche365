package com.cheche365.cheche.operationcenter.service.thirdPartyCooperation

import com.cheche365.cheche.common.util.CollectionUtils
import com.cheche365.cheche.common.util.StringUtil
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.MoChannelPageConfig
import com.cheche365.cheche.core.model.Partner
import com.cheche365.cheche.core.mongodb.repository.MoChannelPageConfigRepository
import com.cheche365.cheche.core.repository.PartnerRepository
import com.cheche365.cheche.core.repository.QuoteFlowConfigRepository
import com.cheche365.cheche.core.service.ChannelService
import com.cheche365.cheche.core.service.ResourceService
import com.cheche365.cheche.manage.common.web.model.ResultModel
import com.cheche365.cheche.operationcenter.model.PartnerQuery
import com.cheche365.cheche.operationcenter.web.model.thirdParty.ChannelManagerViewModel
import org.apache.commons.lang.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service

import javax.persistence.EntityManager
import javax.persistence.Query

@Service
class CooperateCommonService {

    private Logger logger = LoggerFactory.getLogger(this.getClass())
    @Autowired
    private EntityManager entityManager
    @Autowired
    private MongoTemplate mongoTemplate
    @Autowired
    private PartnerRepository partnerRepository
    @Autowired
    private ChannelService channelService
    @Autowired
    private MoChannelPageConfigRepository moChannelPageConfigRepository;
    @Autowired
    private ChannelFilterService channelFilterService;
    @Autowired
    private QuoteFlowConfigRepository quoteFlowConfigRepository
    @Autowired
    private ResourceService resourceService



    ResultModel createChannel(PartnerQuery query) {
        query.setAdd(true)
        Channel channel
        Tuple result = createChannelServiceMap(query)
        Object res = result.get(0)
        if (res[0].equals(true)) {
            Map tupleInfo = res[1]
            channel = tupleInfo.get('channel')
        } else {
            Map tupleInfo = res[1]
            System.out.print(tupleInfo.get("msg"))
            return new ResultModel(false, tupleInfo.get("msg").toString())
        }
        MoChannelPageConfig config = new MoChannelPageConfig()
        config.setChannelId(channel.getId())
        Map infoMap
        if (query.getPartnerType().equals(PartnerQuery.PartnerType.TOA)) {
            infoMap = createToAMongoData(query)
        } else {
            infoMap = createToCMongoData(query)
        }

        config.setConfig(infoMap)
        channelFilterService.filterChannelAll(channel, query.getIsOrderCenter())
        mongoTemplate.insert(config, "channel_page_config")
        return new ResultModel(true, channel.getId().toString())
    }

    Page<ChannelManagerViewModel> findChannelBySpecAndPaginate(PartnerQuery partnerQuery) {
        String sql = buildQuery(partnerQuery)
        Query query = entityManager.createNativeQuery(sql)
        int totals = query.getResultList().size()
        List<ChannelManagerViewModel> viewList = new ArrayList<>()
        List<Object[]> currentView = query.setFirstResult((partnerQuery.getCurrentPage() - 1) * partnerQuery.getPageSize())
            .setMaxResults(partnerQuery.getPageSize()).getResultList()
        currentView.each {
            Object[] obj ->
                viewList.add(createViewData(obj))
        }
        Page<ChannelManagerViewModel> page = new PageImpl<ChannelManagerViewModel>(viewList, new PageRequest(partnerQuery.getCurrentPage() - 1, partnerQuery.getPageSize()), totals)
        return page;
    }

    def createMongoViewData(ChannelManagerViewModel model) {
        Channel channel = Channel.toChannel(model.getId())
        MoChannelPageConfig config = moChannelPageConfigRepository.findByChannelId(model.getId())
        if (config != null) {
            Map channelPageConfig = config.getConfig()
            //渠道配置部分参数
            Map paramsMap = channelService.getConfig(channel)
            if (null != paramsMap) {
                if (channelPageConfig.get('home')) {
                    model.setLandingPage("首页")
                } else {
                    model.setLandingPage("基本信息页")
                }
                model.setStatus(!paramsMap.get("disabledChannel"))
                model.setQuoteWay(channelPageConfig.get("singleCompany")? "直投" : "比价")
            }
        }
        return model
    }

    private ChannelManagerViewModel createViewData(Object[] obj) {
        ChannelManagerViewModel model = new ChannelManagerViewModel()
        model.setId(Long.parseLong(obj[0].toString()))
        model.setEnName(obj[1].toString())
        model.setName(obj[2].toString())
        model.setChannelName(obj[3].toString())
        model = createMongoViewData(model)
        return model;
    }


    private String buildQuery(PartnerQuery query) {
        String toA_condition = " AND c.tag & 1 << 3 ";//TOA
        String toC_condition = " AND NOT c.tag & 1 << 3 ";//TOC
        String single_company = " AND api.tag & 1 << 4 ";//直投
        String mullti_company = " AND NOT api.tag & 1 << 4 ";//比价
        String enable = " AND NOT c.tag & 1 << 4 ";//启用
        String disable = " AND c.tag & 1 << 4  ";//禁用
        StringBuffer sql = new StringBuffer("SELECT c.id, api.`code`, p.`name`, c.description " +
            " FROM  channel c " +
            " JOIN api_partner api ON api.id = c.api_partner  " +
            " JOIN partner p ON p.id = c.partner " +
            " WHERE " +
            " c.tag & 1 << 1 " +
            (query.getPartnerType().equals(PartnerQuery.PartnerType.TOA) ? toA_condition : toC_condition) +
            (StringUtil.isNull(query.getPartner()) ? "" : " AND c.partner = " + query.getPartner()) +
            (StringUtil.isNull(query.getChannel()) ? "" : " AND c.id = " + query.getChannel()) +
            (StringUtil.isNull(query.getApiPartner()) ? "" : " AND c.api_partner = " + query.getApiPartner()) +
            (query.getPartnerId() == null ? "" : " AND c.partner = " + query.getPartnerId()))
        if (!StringUtil.isNull(query.getQuoteWay())) {
            if (PartnerQuery.QuoteType.MULTI.getIndex().equals(Integer.parseInt(query.getQuoteWay()))) {
                sql.append(mullti_company)
            } else {
                sql.append(single_company)
            }
        }
        if (!StringUtil.isNull(query.getStatus())) {
            if (PartnerQuery.State.ENABLE.getIndex().equals(Integer.parseInt(query.getStatus()))) {
                sql.append(enable)
            } else {
                sql.append(disable)
            }
        }
        sql.append(" order by c.id desc ")
        return sql.toString()
    }

    ResultModel updateChannel(PartnerQuery query) {
        Channel channel = Channel.toChannel(query.getId())
        Tuple result = createChannelServiceMap(query)
        Object res = result.get(0)
        if (res[0].equals(true)) {
            Map tupleInfo = res[1]
            channel = tupleInfo.get('channel')
        } else {
            Map tupleInfo = res[1]
            System.out.print(tupleInfo.get("msg"))
            return new ResultModel(false, tupleInfo.get("msg").toString())
        }
        MoChannelPageConfig channelPageConfig = moChannelPageConfigRepository.findByChannelId(channel.getId())
        MoChannelPageConfig config = new MoChannelPageConfig()
        config.setChannelId(channel.getId())
        if (channelPageConfig != null) {
            config.setId(channelPageConfig.getId())
        }
        Map infoMap
        if (StringUtils.equals(query.getPartnerType().getName(), "ToA")) {
            infoMap = createToAMongoData(query)
        } else {
            infoMap = createToCMongoData(query)
        }
        MoChannelPageConfig getFrmMongo = moChannelPageConfigRepository.findByChannelId(query.getId())?:new MoChannelPageConfig()
        Map getFrmMongoMap = getFrmMongo.getConfig()
        config.setConfig( mergeFromChannelService(getFrmMongoMap,infoMap))
        moChannelPageConfigRepository.save(config)
        return new ResultModel(true, channel.getId().toString())
    }

    //公共方法
    Map createToCMongoData(PartnerQuery query) {
        return new HashMap(
            "reserve": query.reserve,
            "supportPhoto": query.supportPhoto,
            "showCustomService": query.showCustomService,
            "serviceTel": query.serviceTel,
            "home": query.home,
            "homeFixBottom": query.homeFixBottom,
            "showPartner": query.showPartner,
            "baseLogin": query.baseLogin,
            "baseCustomAndPhoto": query.baseCustomAndPhoto,
            "baseBanner": query.baseBanner,
            "baseOrder": query.baseOrder,
            "baseMine": query.baseMine,
            "hasWallet": query.hasWallet,
            "cheWallet": query.cheWallet,
            "orderGift": query.orderGift,
            "orderInsuredCar": query.orderInsuredCar,
            "successOrder": query.successOrder,
            "orderUrl": query.orderUrl,
            "homeUrl": query.homeUrl,
            "googleTrackId": query.googleTrackId,
            "themeColor": query.themeColor
        )
    }

    //修改时从channelService里取
    Map mergeFromChannelService(Map before,Map after) {
        return CollectionUtils.mergeMaps(before, after.findAll { null != it.value })
    }
    //公共方法
    Map createToAMongoData(PartnerQuery query) {
        Map map = new HashMap(
            "hasWallet": query.wallet,//我的钱包
            "showAgentLicense": query.elecAgreement,//电子协议
            "showAgentReward": query.award,//奖励显示
            "home": true,//落地页
            "support3m": query.thirdPartyManage,//落地页
            "supportPhoto": query.supportPhoto,
            "showCustomService": query.showCustomService,
            "serviceTel": query.serviceTel,
            "googleTrackId": query.googleTrackId,
            "themeColor": query.themeColor
        )
        if (query.getPartnerType().equals(PartnerQuery.PartnerType.TOA)) {
            map << ["singleCompany": true]
        }

        return map
    }

    //公共方法
    Tuple createChannelServiceMap(PartnerQuery query) {
        Channel channel = null
        Partner partner = null
        if (!query.getAdd()) {
            channel = Channel.toChannel(query.getId())
            partner = partnerRepository.findOne(channel.getPartner().getId())
        } else {
            partner = partnerRepository.findOne(Long.parseLong(query.getPartner()))
        }
        Map map = new HashMap(
            "channelCode": query.apiPartner,
            "channelName": query.channel,
            "agent": query.partnerType.equals(PartnerQuery.PartnerType.TOA) ? true : false,
            "hasOrderCenter": query.getIsOrder(),//出单中心是否可下单
            "needSyncOrder": query.synchro,
            "syncOrderUrl": StringUtil.trim(query.address),
            "levelAgent": query.thirdPartyManage,
            "rebateIntoWallet": query.wallet,
            "supportAmend": query.supplement //是否支持增补
        )
        if (query.getPartnerType().equals(PartnerQuery.PartnerType.TOA)) {
            map << ["singleCompany": true]
        }
        return channelService.config(partner, map, channel)
    }

    boolean chgAble(PartnerQuery query) {
        Channel channel = Channel.toChannel(Long.parseLong(query.getChannel()))
        Boolean disabledChannel = (query.getStatus().equals("0")) ? true : false
        Map map = new HashMap()
        map.put('disabledChannel', disabledChannel)
        Tuple result = channelService.config(channel.getPartner(), map, channel)
        return result[0]
    }
}

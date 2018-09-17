package com.cheche365.cheche.web.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.ChannelRebate
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.agent.*
import com.cheche365.cheche.core.repository.*
import com.cheche365.cheche.core.repository.agent.ChannelAgentOrderRebateRepository
import com.cheche365.cheche.core.repository.agent.ChannelAgentRepository
import com.cheche365.cheche.web.util.ChannelAgentUtil
import groovy.util.logging.Slf4j
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

import java.text.DecimalFormat

import static com.cheche365.cheche.core.constants.RebateCalculateConstants.CalculateType
import static com.cheche365.cheche.core.model.agent.AgentLevel.Enum.nextLevel
import static com.cheche365.cheche.web.util.ClientTypeUtil.getChannel

/**
 * Author:   shanxf
 * Date:     2018/3/10 14:29
 *
 */

@Service
@Slf4j
class ChannelAgentInfoService {

    private static final DecimalFormat DF = new DecimalFormat("0.00")

    ChannelAgentOrderRebateRepository channelAgentOrderRebateRepository

    ChannelAgentRepository channelAgentRepository

    PurchaseOrderRepository purchaseOrderRepository

    ChannelAgentRebateRepository channelAgentRebateRepository

    ChannelRebateRepository channelRebateRepository

    QuoteFlowConfigRepository quoteFlowConfigRepository

    ChecheAgentInviteCodeRepository checheAgentInviteCodeRepository;

    ChannelAgentInfoService(ChannelAgentOrderRebateRepository channelAgentOrderRebateRepository,
                            ChannelAgentRepository channelAgentRepository,
                            PurchaseOrderRepository purchaseOrderRepository,
                            ChannelAgentRebateRepository channelAgentRebateRepository,
                            ChannelRebateRepository channelRebateRepository,
                            QuoteFlowConfigRepository quoteFlowConfigRepository,
                            ChecheAgentInviteCodeRepository checheAgentInviteCodeRepository) {
        this.channelAgentOrderRebateRepository = channelAgentOrderRebateRepository
        this.channelAgentRepository = channelAgentRepository
        this.purchaseOrderRepository = purchaseOrderRepository
        this.channelAgentRebateRepository = channelAgentRebateRepository
        this.channelRebateRepository = channelRebateRepository
        this.quoteFlowConfigRepository = quoteFlowConfigRepository
        this.checheAgentInviteCodeRepository = checheAgentInviteCodeRepository
    }

    /**
     *
     * @param channelAgent
     * @retur
     * 根据当前代理人的的 所有收益 保费金额 出单量等相关信息，对应前端团队业务页面
     */

    Map agentInfo(ChannelAgent channelAgent) {

        List<ChannelAgentPurchaseOrderRebate> allOrders = channelAgentOrderRebateRepository.findAllOrderedByChannelAgent(channelAgent)

        return [
            totalIncome: ChannelAgentUtil.totalIncome(allOrders),
            totalOrder : ChannelAgentUtil.totalOrder(allOrders)
        ] + assembleTeam(channelAgent)
    }

    Map assembleTeam(ChannelAgent channelAgent) {

        List<ChannelAgent> agentList = channelAgentRepository.findByAgentCodeLike(AgentLevel.agentCodeLike(channelAgent.id))
        return [
            team      : [
                teamCount       : agentList.size(),
                finishOrderCount: agentList.size() ? channelAgentRepository.findAgentOrder(agentList*.id) : 0,
                details         : nodeInfo(agentList, nextLevel(channelAgent.agentLevel))
            ],
            inviteCode: channelAgent.inviteCode
        ]
    }

    Map nodeInfo(List<ChannelAgent> channelAgentList, AgentLevel agentLevel) {

        if (!agentLevel) {
            return null
        }
        def nextIds = channelAgentList?.findAll { channelAgent ->
            channelAgent.agentLevel == agentLevel
        }?.collect { it -> it.id }?.toList()

        return [
            level: agentLevel,
            data : [
                teamCount : nextIds.size(),
                orderCount: nextIds.size() > 0 ? purchaseOrderRepository.findAgentOrder(nextIds)?.size() : 0,
                premium   : nextIds.size() > 0 ? ChannelAgentUtil.totalPremium(purchaseOrderRepository.findAgentOrder(nextIds)) : 0d,
            ],
            child: nodeInfo(channelAgentList, nextLevel(agentLevel))
        ]

    }

    Page<List> agentList(Pageable pageable, ChannelAgent channelAgent, String keyword, AgentLevel agentLevel) {

        def list = []
        List<ChannelAgent> channelAgentList = channelAgentRepository.findByAgentCodeLikeAndLevel(AgentLevel.agentCodeLike(channelAgent.id), agentLevel.id)

        channelAgentList?.findAll { it ->
            it.user.name?.contains(keyword) || it.user.mobile.contains(keyword)
        }?.each { it ->
            List<Object[]> premiumOrders = channelAgentOrderRebateRepository.findUserMonthAgentOrder(it.id)
            def map = [
                id          : it.id,
                name        : it.user.name,
                mobile      : (agentLevel.id - channelAgent.agentLevel.id > 1) ? it.user.mobile[0..1] + '*******' + it.user.mobile[-2..-1] : it.user.mobile,
                monthPremium: premiumOrders.get(0)[0] ?: 0,
                monthOrder  : premiumOrders.get(0)[1]
            ]
            if (!agentLevel.isLeaf) {
                map.saleCount = channelAgentRepository.findByAgentCodeLike(AgentLevel.agentCodeLike(it.id))?.size() ?: 0
            }
            if (it.parent.agentLevel == channelAgent.agentLevel) {
                map.rebateSet = Boolean.TRUE
            }
            list.add(map)
        }
        list?.sort {
            a, b ->
                b.monthPremium <=> a.monthPremium
        }
        new Page<List>(pageable.pageNumber, pageable.pageSize, list?.size() ?: 0, list)
    }


    List<Map> channelAgentRebateList(ChannelAgent channelAgent, Long channelAgentId, Long areaId) {

        log.info("find next agent rebate info by channelAgentId:{},areaId:{}", channelAgentId, areaId)
        ChannelAgent childChannelAgent = channelAgentRepository.findOne(channelAgentId)
        Area area = Area.Enum.getValueByCode(areaId)
        List<InsuranceCompany> insuranceCompanies = quoteFlowConfigRepository.findInsuranceCompanyByAreaAndChannel(area, getChannel())

        def channelAgentRebateView = []
        insuranceCompanies.each {
            ChannelRebate channelRebate = channelRebateRepository.findFirstByChannelAndAreaAndInsuranceCompanyAndStatus(channelAgent.channel.parent, area, it, ChannelRebate.Enum.EFFECTIVED_1)
            channelAgentRebateView << [
                areaId                    : area.id,
                totalCommercialRebate     : childChannelAgent.commercialRebate(channelRebate, CalculateType.totalRebate),
                detainCommercialRebate    : childChannelAgent.commercialRebate(channelRebate, CalculateType.detainRebate),
                totalCompulsoryRebate     : childChannelAgent.compulsoryRebate(channelRebate, CalculateType.totalRebate),
                detainCompulsoryRebate    : childChannelAgent.compulsoryRebate(channelRebate, CalculateType.detainRebate),
                totalOnlyCommercialRebate : childChannelAgent.onlyCommercialRebate(channelRebate, CalculateType.totalRebate),
                detainOnlyCommercialRebate: childChannelAgent.onlyCommercialRebate(channelRebate, CalculateType.detainRebate),
                totalOnlyCompulsoryRebate : childChannelAgent.onlyCompulsoryRebate(channelRebate, CalculateType.totalRebate),
                detainOnlyCompulsoryRebate: childChannelAgent.onlyCompulsoryRebate(channelRebate, CalculateType.detainRebate),
                insuranceCompany          : InsuranceCompany.toInsuranceCompany(it.id),
                channelAgentId            : channelAgent.id,
                childChannelAgentId       : childChannelAgent.id
            ]


        }
        channelAgentRebateView

    }

    def findChannelAgentRebate(List<InsuranceCompany> insuranceCompanies, ChannelAgent channelAgent, ChannelAgent childChannelAgent, Area area) {

    }

    /**
     * 算出channelAgent下单实际可以拿到多少点位
     * @param channelRebate
     * @param channelAgent
     * @param calculateMap @value RebateCalculateConstants 传入具体查询类型
     * @return
     */
    Double actualRebate(ChannelRebate channelRebate, ChannelAgent channelAgent, Map calculateMap) {
        if (!channelRebate || !(channelRebate."$calculateMap.insuranceType")) {
            return 0
        }
        Double total = channelRebate."$calculateMap.insuranceType"
        channelAgent.absoluteAgentCode()?.split('\\.')?.drop(1)?.each {
            ChannelAgentRebate rebate = channelAgentRebateRepository.findByAreaAndInsuranceCompanyAndChannelAgent(
                channelRebate.area, channelRebate.insuranceCompany, channelAgentRepository.findOne(it as Long)
            )
            if (!rebate || total == 0 || calculateMap.parentDetainRebate.call(rebate) == null || calculateMap.parentDetainRebate.call(rebate) > total) {
                //默认扣所有 下级点位置0
                total = 0
            } else {
                total = total - calculateMap.parentDetainRebate.call(rebate)
            }
        }
        return total
    }

    /**
     * 计算出 channelAgent 的上级扣了多少点位
     * 如果没有配置默认扣所有
     * 1级代理人直接返回 0
     * @param channelRebate
     * @param channelAgent
     * @param calculateMap
     * @return
     */
    Double detainRebate(ChannelRebate channelRebate, ChannelAgent channelAgent, Map calculateMap) {
        if (!channelRebate || !channelAgent?.parent) {
            return 0
        }
        ChannelAgentRebate rebate = channelAgentRebateRepository.findByAreaAndInsuranceCompanyAndChannelAgent(channelRebate.area, channelRebate.insuranceCompany, channelAgent)

        if (rebate && calculateMap.parentDetainRebate.call(rebate) != null) {
            return calculateMap.parentDetainRebate.call(rebate)
        }
        //如果上级没给下级配置点位 默认上级扣所有
        return actualRebate(channelRebate, channelAgent.parent, calculateMap)
    }

    /**
     * 计算出channelAgent最多可以拥有多少点位 即配置的点位的最大值 即等于直接上级实际拥有多少点位
     * 如果是一级代理人则直接读取渠道点位
     * 如果非一级代理人即等于直接上级实际拥有多少点位
     * @param channelRebate
     * @param channelAgent
     * @param calculateMap
     * @return
     */
    Double totalRebate(ChannelRebate channelRebate, ChannelAgent channelAgent, Map calculateMap) {
        if (!channelRebate) {
            return 0
        }
        if (!channelAgent?.parent) {
            return channelRebate."$calculateMap.insuranceType"
        }
        return actualRebate(channelRebate, channelAgent.parent, calculateMap)
    }

    void modifyRebates(ChannelAgent channelAgent, rebateConfigs) {
        if (rebateConfigs && rebateConfigs.areaId && rebateConfigs.childChannelAgentId) {
            Area area = Area.Enum.getValueByCode(rebateConfigs.areaId)
            ChannelAgent childChannelAgent = channelAgentRepository.findOne(rebateConfigs.childChannelAgentId.longValue())

            preSafeCheck(channelAgent, childChannelAgent, rebateConfigs, area)

            rebateConfigs.content?.each {
                InsuranceCompany insuranceCompany = InsuranceCompany.toInsuranceCompany(it.insuranceCompanyId)
                ChannelAgentRebate channelAgentRebate = channelAgentRebateRepository.findByAreaAndInsuranceCompanyAndChannelAgent(
                    area,
                    insuranceCompany,
                    childChannelAgent
                )
                if (!channelAgentRebate) {
                    channelAgentRebate = new ChannelAgentRebate().with {
                        it.area = area
                        it.insuranceCompany = insuranceCompany
                        it.channelAgent = childChannelAgent
                        it
                    }
                }
                if (it.detainCommercialRebate >= 0) {
                    channelAgentRebate.setParentDetainCommercialRebate(it.detainCommercialRebate)
                }
                if (it.detainCompulsoryRebate >= 0) {
                    channelAgentRebate.setParentDetainCompulsoryRebate(it.detainCompulsoryRebate)
                }
                if (it.detainOnlyCommercialRebate >= 0) {
                    channelAgentRebate.setOnlyCommercialRebate(it.detainOnlyCommercialRebate)
                }
                if (it.detainOnlyCompulsoryRebate >= 0) {
                    channelAgentRebate.setOnlyCompulsoryRebate(it.detainOnlyCompulsoryRebate)
                }
                channelAgentRebateRepository.save(channelAgentRebate)
            }
        }
    }

    /**
     * 检查配置点位参数合法性
     * @param channelAgent
     * @param rebateConfigs
     * @param area
     */
    void preSafeCheck(ChannelAgent channelAgent, ChannelAgent childChannelAgent, Map rebateConfigs, Area area) {

        if (channelAgent.id != childChannelAgent?.parent?.id) {
            log.info("当前代理人为：{},待配置下级代理人为：{}", channelAgent.id, childChannelAgent?.id)
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "参数不合法")
        }

        rebateConfigs.content?.each {
            ChannelRebate channelRebate = channelRebateRepository.findFirstByChannelAndAreaAndInsuranceCompanyAndStatus(
                channelAgent.channel,
                area,
                InsuranceCompany.toInsuranceCompany(it.insuranceCompanyId),
                ChannelRebate.Enum.EFFECTIVED_1
            )
            if (
            (it.detainCommercialRebate && it.detainCommercialRebate < 0) ||
                (it.detainCompulsoryRebate && it.detainCompulsoryRebate < 0) ||
                (it.detainOnlyCommercialRebate && it.detainOnlyCommercialRebate < 0) ||
                (it.detainOnlyCompulsoryRebate && it.detainOnlyCompulsoryRebate < 0)
            ) {
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "参数不合法")
            }

            if (it.detainCommercialRebate > 0 && channelAgent.commercialRebate(channelRebate, CalculateType.totalRebate) < it.detainCommercialRebate) {

                log.info("商业险扣取点位不合法,总的点位为：{},扣取点位为：{}", channelAgent.commercialRebate(channelRebate, CalculateType.totalRebate), it.detainCommercialRebate)
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "参数不合法")
            }
            if (it.detainCompulsoryRebate > 0 && channelAgent.compulsoryRebate(channelRebate, CalculateType.totalRebate) < it.detainCompulsoryRebate) {

                log.info("交强险扣取点位不合法,总的点位为：{},扣取点位为：{}", channelAgent.compulsoryRebate(channelRebate, CalculateType.totalRebate), it.detainCompulsoryRebate)
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "参数不合法")
            }
            if (it.detainOnlyCommercialRebate > 0 && channelAgent.onlyCommercialRebate(channelRebate, CalculateType.totalRebate) < it.detainOnlyCommercialRebate) {

                log.info("单商业险扣取点位不合法,总的点位为：{},扣取点位为：{}", channelAgent.onlyCommercialRebate(channelRebate, CalculateType.totalRebate), it.detainOnlyCommercialRebate)
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "参数不合法")
            }
            if (it.detainOnlyCompulsoryRebate > 0 && channelAgent.onlyCompulsoryRebate(channelRebate, CalculateType.totalRebate) < it.detainOnlyCompulsoryRebate) {

                log.info("单交强险扣取点位不合法,总的点位为：{},扣取点位为：{}", channelAgent.onlyCompulsoryRebate(channelRebate, CalculateType.totalRebate), it.detainOnlyCompulsoryRebate)
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "参数不合法")
            }
        }
    }

    ChannelAgentRebate initChannelAgentRebate(area, insuranceCompany, commercialRebate, channelAgent) {

        channelAgentRebateRepository.save(new ChannelAgentRebate().with {
            it.channelAgent = channelAgent
            it.insuranceCompany = insuranceCompany
            it.area = area
            it.parentDetainCommercialRebate = commercialRebate
            it
        })

    }
    /**
     * 查看代理人点位
     * @param channelAgent
     * @param areaId
     * @return
     */
    List<Map> rebateRate(ChannelAgent channelAgent, Long areaId) {
        def rebateRateList = []
        List<InsuranceCompany> insuranceCompanies = quoteFlowConfigRepository.findInsuranceCompanyByAreaAndChannel(Area.Enum.getValueByCode(areaId), getChannel())

        insuranceCompanies.each {
            it ->
                ChannelRebate channelRebate = channelRebateRepository.findFirstByChannelAndAreaAndInsuranceCompanyAndStatus(channelAgent.channel.parent, Area.Enum.getValueByCode(areaId), it, ChannelRebate.Enum.EFFECTIVED_1)

                rebateRateList << [
                    insuranceCompany         : it,
                    totalCommercialRebate    : channelAgent.commercialRebate(channelRebate, CalculateType.actualRebate),
                    totalCompulsoryRebate    : channelAgent.compulsoryRebate(channelRebate, CalculateType.actualRebate),
                    totalOnlyCommercialRebate: channelAgent.onlyCommercialRebate(channelRebate, CalculateType.actualRebate),
                    totalOnlyCompulsoryRebate: channelAgent.onlyCompulsoryRebate(channelRebate, CalculateType.actualRebate)
                ]

        }
        rebateRateList
    }

    ChecheAgentInviteCode findChecheInviteCode(ChannelAgent channelAgent) {
        ChannelAgent channelAgentTop
        if (channelAgent.agentLevel == AgentLevel.Enum.SALE_DIRECTOR_1) {
            channelAgentTop = channelAgent
        } else {
            channelAgentTop = channelAgentRepository.findOne(Long.valueOf(channelAgent.agentCode.split("\\.").find()))
        }
        checheAgentInviteCodeRepository.findByChannelAgentId(channelAgentTop.id)
    }
}

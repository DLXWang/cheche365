package com.cheche365.cheche.manage.common.service.reverse

import com.cheche365.cheche.common.flow.FlowBuilder
import com.cheche365.cheche.core.repository.*
import com.cheche365.cheche.core.service.*
import com.cheche365.cheche.core.service.agent.ChannelRebateService
import com.cheche365.cheche.manage.common.service.InsurancePurchaseOrderRebateManageService
import com.cheche365.cheche.manage.common.service.InternalUserManageService
import com.cheche365.cheche.manage.common.service.OrderInsurancePackageService
import com.cheche365.cheche.manage.common.service.gift.OrderCenterGiftService
import com.cheche365.cheche.manage.common.service.reverse.step.TPlaceInsuranceStep
import com.cheche365.cheche.wallet.repository.WalletTradeRepository
import com.cheche365.cheche.wallet.service.WalletTradeService
import com.cheche365.cheche.web.service.InsurancePurchaseOrderRebateService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by yellow on 2017/11/4.
 */
@Service
@Slf4j
class InsuranceReverseProcess implements InitializingBean {

    private _STEP_NAME_CLAZZ_MAPPINGS

    @Autowired
    private InsuranceRepository insuranceRepository

    @Autowired
    private CompulsoryInsuranceRepository compulsoryInsuranceRepository

    @Autowired
    private AgentRepository agentRepository

    @Autowired
    private InsuranceCompanyRepository insuranceCompanyRepository

    @Autowired
    private QuoteRecordRepository quoteRecordRepository

    @Autowired
    private AddressRepository addressRepository

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository

    @Autowired
    private PaymentRepository paymentRepository

    @Autowired
    private AreaRepository areaRepository

    @Autowired
    private OrderOperationInfoRepository orderOperationInfoRepository

    @Autowired
    private IInternalUserService internalUserService

    @Autowired
    private PurchaseOrderIdService purchaseOrderIdService

    @Autowired
    private AutoService autoService

    @Autowired
    private OrderOperationInfoService orderOperationInfoService

    @Autowired
    private InternalUserManageService internalUserManageService

    @Autowired
    private ResourceService resourceService

    @Autowired
    private DeliveryInfoRepository deliveryInfoRepository

    @Autowired
    private OrderAgentService orderAgentService

    @Autowired
    private InsurancePurchaseOrderRebateRepository insurancePurchaseOrderRebateRepository

    @Autowired
    private InsurancePurchaseOrderRebateManageService insurancePurchaseOrderRebateManageService

    @Autowired
    private InsurancePurchaseOrderRebateService insurancePurchaseOrderRebateService

    @Autowired
    private AgentRebateHistoryService agentRebateHistoryService

    @Autowired
    private OrderInsurancePackageService orderInsurancePackageService

    @Autowired
    private ChannelRepository channelRepository

    @Autowired
    private ChannelRebateService channelRebateService

    @Autowired
    private InstitutionRebateHistoryService institutionRebateHistoryService

    @Autowired
    private OrderCenterGiftService orderCenterGiftService

    @Autowired
    private WalletTradeService walletTradeService

    @Autowired
    private WalletTradeRepository walletTradeRepository

    @Autowired
    private InstitutionRepository institutionRepository

    @Autowired
    private GiftService giftService

    @Autowired
    private PurchaseOrderGiftService purchaseOrderGiftService

    @Autowired
    private UserRepository userRepository

    @Autowired
    Map<String, TPlaceInsuranceStep> steps


    private USER_INSURANCE_REVERSE_FLOW

    private TOA_INSURANCE_REVERSE_FLOW

    private AGENT_INSURANCE_REVERSE_FLOW

    private OFFLINE_IMPORT_FLOW

    private FAN_HUA_SYNC_FLOW

    @Override
    void afterPropertiesSet() {
        _STEP_NAME_CLAZZ_MAPPINGS = [
            "生成用户"    : steps.generateUser,
            "生成代理人"   : steps.generateAgent,
            "生成出单机构"  : steps.generateInstitution,
            "生成车辆"    : steps.generateAuto,
            "生成报价"    : steps.generateQuoteRecord,
            "生成订单"    : steps.generatePurchaseOrder,
            "生成支付"    : steps.generatePayment,
            "生成保单"    : steps.generateInsurance,
            "生成出单"    : steps.generateOrderOperationInfo,
            "上游代理佣金计算": steps.calculateAgentRebate,
            "上游渠道佣金计算": steps.calculateChannelRebate,
            "下游佣金计算"  : steps.calculateInstitutionRebate,
            "钱包计算"    : steps.walletAmountIn,
            "生成礼品"    : steps.generateGift,
            "流程结束"    : steps.reserveFinish
        ]
        def userFlowBuilder = new FlowBuilder(nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS)
        USER_INSURANCE_REVERSE_FLOW = userFlowBuilder {
            生成车辆 >> 生成报价 >> 生成订单 >> 生成保单 >> 生成出单 >> 下游佣金计算 >> 生成礼品 >> 生成支付 >> 流程结束
        }

        def toAFlowBuilder = new FlowBuilder(nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS)
        TOA_INSURANCE_REVERSE_FLOW = toAFlowBuilder {
            生成车辆 >> 生成报价 >> 生成订单 >> 生成支付 >> 生成保单 >> 生成出单 >> 上游渠道佣金计算 >> 下游佣金计算 >> 钱包计算 >> 流程结束
        }

        def agentFlowBuilder = new FlowBuilder(nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS)
        AGENT_INSURANCE_REVERSE_FLOW = agentFlowBuilder {
            生成车辆 >> 生成报价 >> 生成订单 >> 生成保单 >> 生成出单 >> 上游代理佣金计算 >> 下游佣金计算 >> 生成礼品 >> 生成支付 >> 流程结束
        }

        def offLineFlowBuilder = new FlowBuilder(nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS)
        OFFLINE_IMPORT_FLOW = offLineFlowBuilder {
            生成用户 >> 生成代理人 >> 生成出单机构 >> 生成车辆 >> 生成报价 >> 生成订单 >> 生成支付 >> 生成保单 >> 生成出单 >> 上游代理佣金计算 >> 下游佣金计算 >> 流程结束
        }

        def fanHuaFlowBuilder = new FlowBuilder(nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS)
        FAN_HUA_SYNC_FLOW = fanHuaFlowBuilder {
            生成用户 >> 生成代理人 >> 生成出单机构 >> 生成车辆 >> 生成报价 >> 生成订单 >> 生成支付 >> 生成保单 >> 生成出单 >> 上游代理佣金计算 >> 下游佣金计算 >> 流程结束
        }
    }

    @Transactional
    def doService(OrderReverse orderReverse) {
        def context = [

            insuranceRepository                      : insuranceRepository,
            compulsoryInsuranceRepository            : compulsoryInsuranceRepository,
            agentRepository                          : agentRepository,
            insuranceCompanyRepository               : insuranceCompanyRepository,
            quoteRecordRepository                    : quoteRecordRepository,
            addressRepository                        : addressRepository,
            purchaseOrderRepository                  : purchaseOrderRepository,
            paymentRepository                        : paymentRepository,
            areaRepository                           : areaRepository,
            orderOperationInfoRepository             : orderOperationInfoRepository,
            internalUserService                      : internalUserService,
            purchaseOrderIdService                   : purchaseOrderIdService,
            autoService                              : autoService,
            orderOperationInfoService                : orderOperationInfoService,
            internalUserManageService                : internalUserManageService,
            resourceService                          : resourceService,
            deliveryInfoRepository                   : deliveryInfoRepository,
            orderAgentService                        : orderAgentService,
            insurancePurchaseOrderRebateRepository   : insurancePurchaseOrderRebateRepository,
            insurancePurchaseOrderRebateManageService: insurancePurchaseOrderRebateManageService,
            agentRebateHistoryService                : agentRebateHistoryService,
            orderInsurancePackageService             : orderInsurancePackageService,
            channelRepository                        : channelRepository,
            insurancePurchaseOrderRebateService      : insurancePurchaseOrderRebateService,
            channelRebateService                     : channelRebateService,
            institutionRebateHistoryService          : institutionRebateHistoryService,
            orderCenterGiftService                   : orderCenterGiftService,
            giftService                              : giftService,
            purchaseOrderGiftService                 : purchaseOrderGiftService,
            walletTradeService                       : walletTradeService,
            walletTradeRepository                    : walletTradeRepository,
            userRepository                           : userRepository,
            institutionRepository                    : institutionRepository,
            model                                    : orderReverse,
            user                                     : null,
            auto                                     : null,
            quoteRecord                              : null,
            purchaseOrderExtend                      : null
        ]

        def (flag, code, result, msg) = findFlow(orderReverse).run(context)
        result
    }

    private findFlow(OrderReverse model) {
        OrderReverse.ReverseSource reverseSource = model.getReverseSource()
        if (reverseSource == OrderReverse.ReverseSource.FAN_HUA_SYNC) {
            log.debug("泛华保单同步流程开始----------")
            return FAN_HUA_SYNC_FLOW
        } else if (reverseSource == OrderReverse.ReverseSource.OFFLINE_IMPORT) {
            log.debug("线下保单导入流程开始----------")
            return OFFLINE_IMPORT_FLOW
        } else if (reverseSource == OrderReverse.ReverseSource.TOA_INPUT) {
            log.debug("TOA保单回录流程开始----------")
            return TOA_INSURANCE_REVERSE_FLOW
        } else if (reverseSource == OrderReverse.ReverseSource.USER_INPUT) {
            log.debug("普通用户保单回录流程开始----------")
            return USER_INSURANCE_REVERSE_FLOW
        } else {
            log.debug("代理人保单回录流程开始----------")
            return AGENT_INSURANCE_REVERSE_FLOW
        }
    }

}

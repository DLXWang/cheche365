package com.cheche365.cheche.rest.processor.order

import com.cheche365.cheche.common.flow.FlowBuilder
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.message.RedisPublisher
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteSource
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository
import com.cheche365.cheche.core.repository.*
import com.cheche365.cheche.core.repository.agent.CustomerAutoRepository
import com.cheche365.cheche.core.repository.agent.CustomerRepository
import com.cheche365.cheche.core.service.*
import com.cheche365.cheche.core.service.agent.CustomerService
import com.cheche365.cheche.core.service.sms.ConditionTriggerHandler
import com.cheche365.cheche.rest.InsuranceServiceFinder
import com.cheche365.cheche.rest.processor.order.step.TPlaceOrderStep
import com.cheche365.cheche.web.service.PaymentCallbackURLHandler
import com.cheche365.cheche.web.service.PaymentChannelService
import com.cheche365.cheche.web.service.order.PurchaseOrderLockService
import com.cheche365.cheche.web.service.order.discount.strategy.DiscountCalculator
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

import javax.servlet.http.HttpServletRequest

import static com.cheche365.cheche.core.exception.BusinessException.toCode
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.SINOSAFE_205000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.HN_150000
import static com.cheche365.cheche.rest.util.WebFlowUtil.get_STATUS_OK

/**
 * Created by zhengwwei on 7/16/15.
 * 订单处理器。处理下单所需要的相关操作。
 * 为了保证线程安全，类中没有属性，purchase_order,insurance_package等都作为函数参数在各个步骤之间传递。
 */

@Service
@Slf4j
class OrderProcessor implements InitializingBean {

    @Autowired
    private InsurancePackageRepository insurancePackageRepository
    @Autowired
    private QuoteRecordCacheService quoteRecordCacheService
    @Autowired
    private PartnerOrderRepository partnerOrderRepository
    @Autowired
    private PartnerUserRepository partnerUserRepository
    @Autowired
    private OrderAgentService orderAgentService
    @Autowired
    private PaymentRepository paymentRepository
    @Autowired
    private RedisPublisher redisPublisher
    @Autowired
    private QuoteService quoteService
    @Autowired
    private PurchaseOrderService orderService
    @Autowired
    private PurchaseOrderIdService orderIdService
    @Autowired
    private PaymentChannelService paymentChannelService
    @Autowired
    private QuoteRecordCacheService cacheService
    @Autowired
    private OrderOperationInfoService orderOperationInfoService
    @Autowired
    private InsuranceRepository insuranceRepository
    @Autowired
    private DiscountCalculator paymentGenerator
    @Autowired
    private CompulsoryInsuranceRepository compulsoryInsuranceRepository
    @Autowired
    private MoApplicationLogRepository logRepository
    @Autowired
    private CustomerRepository customerRepository
    @Autowired
    private CustomerAutoRepository customerAutoRepository
    @Autowired
    protected ConditionTriggerHandler conditionTriggerHandler
    @Autowired
    InsuranceServiceFinder serviceFinder
    @Autowired
    Map<String, TPlaceOrderStep> steps
    @Autowired
    HttpServletRequest request
    @Autowired
    private QuoteSupplementInfoRepository quoteSupplementInfoRepository
    @Autowired
    private PurchaseOrderImageService poiService
    @Autowired
    private QuoteConfigService quoteConfigService
    @Autowired
    private QuoteFlowConfigRepository quoteFlowConfigRepository
    @Autowired
    private StringRedisTemplate stringRedisTemplate
    @Autowired
    private PaymentCallbackURLHandler urlHandler
    @Autowired
    private PurchaseOrderLockService lockService
    @Autowired
    private CustomerService customerService


    private _STEP_NAME_CLAZZ_MAPPINGS
    private _NAME_FLOW_MAPPINGS
    private PLACE_ORDER_FLOW

    @Override
    public void afterPropertiesSet() {
        _STEP_NAME_CLAZZ_MAPPINGS = [
            流程初始化       :  steps.initFlow,
            锁定订单        :  steps.addLock,
            初始化参数      :  steps.initParam,
            恢复核保参数   :   steps.restoreInsureParam,
            预保存         :  steps.persistObjects,
            生成保单       :  steps.generateBills,
            恢复缓存状态    :  steps.restoreCachedStates,
            持久化预保存    : steps.prePersist,
            核保必要性检查  :  steps.insureChecker,
            核保           :  steps.doInsure,
            核保失败前处理  :   steps.preInsureFail,
            泛华核保失败处理 :   steps.baoXianInsureFail,
            华安核保失败处理 :  steps.sinoSafeInsureFail,
            华农核保失败处理 : steps.huanongInsureFail,
            金斗云核保失败处理 : steps.botpyInsureFail,
            核保失败后处理 :    steps.postInsureFail,
            合并套餐          :   steps.mergeInsurancePackage,
            计算折扣       :   steps.applyDiscount,
            持久化         :   steps.persistObjects,
            发短信         :   steps.sendSms,
            同步出单中心    :  steps.syncOrderCenter,
            持久化合作渠道订单 : steps.handleApiPartner,
            生成响应       :  steps.generateResponse

        ]

        _NAME_FLOW_MAPPINGS = [
            核保片段 : new FlowBuilder(nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS).call {
                锁定订单 >> 恢复缓存状态 >> 持久化预保存 >> 核保必要性检查 >> [
                    (true): {
                        核保 >> [
                            (false): {
                                核保失败前处理 >> [
                                    (QuoteSource.Enum.PLANTFORM_BX_6) : {泛华核保失败处理},
                                    (SINOSAFE_205000) : {华安核保失败处理},
                                    (QuoteSource.Enum.PLATFORM_BOTPY_11) : {金斗云核保失败处理},
                                    (HN_150000):{华农核保失败处理}
                                ] >> 核保失败后处理
                            }
                        ]
                    }
                ]
            },
            核保后片段 : new FlowBuilder(nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS).call {
                持久化合作渠道订单 >> 持久化 >> 发短信 >> 同步出单中心 >> 生成响应
            }
        ]

        def commonFlowBuilder = new FlowBuilder(
            nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS,
            nameFlowMappings: _NAME_FLOW_MAPPINGS

        )

        PLACE_ORDER_FLOW = commonFlowBuilder {
            流程初始化 >> [
                common: {
                    初始化参数 >> 预保存 >> 生成保单 >> 核保片段 >> 合并套餐 >> 计算折扣 >> 核保后片段
                },
                reInsure: {
                    恢复核保参数 >> 核保片段 >> 核保后片段
                }
            ]
        }


    }

    def doService(String objId, PurchaseOrder orderFromWeb, Map additionalParameters) {


        def context = [
            request : request,
            orderFromWeb: orderFromWeb,
            objId: objId,
            quoteService : quoteService,
            quoteRecordCacheService : quoteRecordCacheService,
            orderService : orderService,
            orderIdService : orderIdService,
            orderAgentService : orderAgentService,
            paymentRepository : paymentRepository,
            paymentChannelService : paymentChannelService,
            paymentGenerator : paymentGenerator,
            insuranceRepository : insuranceRepository,
            insurancePackageRepository : insurancePackageRepository,
            additionalParameters : additionalParameters,
            compulsoryInsuranceRepository : compulsoryInsuranceRepository,
            cacheService : cacheService,
            redisPublisher : redisPublisher,
            logRepository : logRepository,
            orderOperationInfoService : orderOperationInfoService,
            conditionTriggerHandler : conditionTriggerHandler,
            partnerOrderRepository : partnerOrderRepository,
            partnerUserRepository : partnerUserRepository,
            customerRepository : customerRepository,
            customerAutoRepository : customerAutoRepository,
            serviceFinder : serviceFinder,
            quoteSupplementInfoRepository : quoteSupplementInfoRepository,
            poiService : poiService,
            quoteConfigService:quoteConfigService,
            quoteFlowConfigRepository:quoteFlowConfigRepository,
            toBePersistObjects : [],
            stringRedisTemplate : stringRedisTemplate,
            urlHandler : urlHandler,
            lockService: lockService,
            customerService: customerService
        ]

        def (flag, code, payload, msg) = PLACE_ORDER_FLOW.run(context)

        try{
            if(context._STATUS__LAZY_END_FLOW_ERROR) {

                throw (BusinessException)context._STATUS__LAZY_END_FLOW_ERROR

            } else if(_STATUS_OK == code) {
                payload
            } else {
                throw new BusinessException(toCode(code), msg)
            }
        } finally {
            log.debug('{} 开始释放订单锁', context.order?.orderNo)
            lockService.unLock(context.order?.orderNo)
        }

    }


}

package com.cheche365.cheche.scheduletask.service.insurance

import com.cheche365.cheche.common.util.DoubleUtils
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.*
import com.cheche365.cheche.core.service.AutoService
import com.cheche365.cheche.core.service.InternalUserService
import com.cheche365.cheche.core.service.PurchaseOrderIdService
import com.cheche365.cheche.core.util.BeanUtil
import com.cheche365.cheche.manage.common.exception.FileUploadException
import com.cheche365.cheche.manage.common.model.InsuranceOfflineDataModel
import com.cheche365.cheche.manage.common.model.OfflineDataHistory
import com.cheche365.cheche.manage.common.model.OfflineOrderImportHistory
import com.cheche365.cheche.manage.common.repository.OfflineDataHistoryRepository
import com.cheche365.cheche.manage.common.repository.OfflineOrderImportHistoryRepository
import com.cheche365.cheche.manage.common.service.offlinedata.IOfflineDataService
import groovy.util.logging.Slf4j
import org.apache.commons.collections.CollectionUtils
import org.apache.poi.ss.usermodel.Row
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate

import javax.transaction.Transactional
/**
 * #
 * Created by yinJianBin on 2017/1/20
 */
@Slf4j
abstract class AbstractFanhuaDataHandler {

    @Autowired
    private PaymentRepository paymentRepository
    @Autowired
    private InsurancePackageRepository insurancePackageRepository
    @Autowired
    private IOfflineDataService offlineDataService;
    @Autowired
    UserRepository userRepository
    @Autowired
    AutoRepository autoRepository
    @Autowired
    UserAutoRepository userAutoRepository
    @Autowired
    InsuranceRepository insuranceRepository
    @Autowired
    CompulsoryInsuranceRepository compulsoryInsuranceRepository
    @Autowired
    QuoteRecordRepository quoteRecordRepository
    @Autowired
    PurchaseOrderRepository purchaseOrderRepository
    @Autowired
    OrderOperationInfoRepository orderOperationInfoRepository
    @Autowired
    InsurancePurchaseOrderRebateRepository insurancePurchaseOrderRebateRepository
    @Autowired
    PurchaseOrderIdService purchaseOrderIdService
    @Autowired
    AutoService autoService
    @Autowired
    AddressRepository addressRepository
    @Autowired
    InstitutionRepository institutionRepository
    @Autowired
    AgentRepository agentRepository
    @Autowired
    InternalUserService internalUserService
    @Autowired
    OfflineOrderImportHistoryRepository historyRepository
    @Autowired
    StringRedisTemplate stringRedisTemplate
    @Autowired
    OfflineDataHistoryRepository offlineDataHistoryRepository
    @Autowired
    AutoTypeRepository autoTypeRepository
    @Autowired
    PurchaseOrderAmendRepository purchaseOrderAmendRepository

    @Transactional
    void singleSave(map, history, admin) {
        InsuranceOfflineDataModel model = buildRawData(map)
        def insuranceType = model.insuranceType
        if (InsuranceOfflineDataModel.INSURANCE_TYPE_COMMERCIAL.equals(insuranceType)) {
            Insurance byPolicyNo = insuranceRepository.findLastByPolicyNo(model.policyNo);
            model.insurance = byPolicyNo
            premiumCheck(model, model.totalPremiumDouble, byPolicyNo)
        } else if (InsuranceOfflineDataModel.INSURANCE_TYPE_COMPULSORY.equals(insuranceType)) {
            CompulsoryInsurance byPolicyNo = compulsoryInsuranceRepository.findLastByPolicyNo(model.policyNo);
            model.compulsoryInsurance = byPolicyNo
            premiumCheck(model, model.totalPremiumDouble, byPolicyNo)
        }

        PurchaseOrder purchaseOrderPo = model.purchaseOrder
        def isFullRefund = model.fullRefund

        InsuranceCompany insuranceCompanyPo = buildInsuranceCompany(map)
        Auto autoVO = buildAuto(map)
        PurchaseOrder purchaseOrderVo = buildPurchaseOrder(map)
        Insurance insuranceVo = buildInsurance(map)
        CompulsoryInsurance compulsoryInsuranceVo = buildCompulsoryInsurance(map)
        InsurancePurchaseOrderRebate insurancePurchaseOrderRebateVo = buildInsurancePurchaseOrderRebate(map)
        Institution institutionVo = buildInstitution(map)
        Agent agentVo = buildAgent(map)

        def createTime = purchaseOrderVo.getCreateTime()
        def beforeCreateTime = addDays(createTime, -(new Random().nextInt(100)))
        def currentDate = new Date()
        def sourceId = history.getId() * 100000 + (model.order as Long)
        def licensePlateNo = autoVO.licensePlateNo.trim()
        def isNewCar = "新车" == licensePlateNo || autoVO.engineNo.endsWith(licensePlateNo)
        def description = '线下数据导入'

        User userPo = saveUser(purchaseOrderPo, model, history, beforeCreateTime, currentDate)
        AutoType autoType = saveAutoType(purchaseOrderPo, model)
        Auto autoPO = saveAuto(purchaseOrderPo, autoVO, history, beforeCreateTime, currentDate, autoType, userPo, model, isNewCar)
        InsurancePackage insurancePackagePO = createInsurancePackage(insuranceVo, compulsoryInsuranceVo)
        QuoteRecord quoteRecordPo = saveQuoteRecord(purchaseOrderPo, insuranceType, insuranceVo, compulsoryInsuranceVo, history, autoPO, userPo, insuranceCompanyPo, insurancePackagePO, createTime, currentDate, model)
        Institution institutionPo = saveInstitution(institutionVo, beforeCreateTime, currentDate, admin, description)
        if (model.insuranceType == InsuranceOfflineDataModel.INSURANCE_TYPE_COMMERCIAL) {
            saveInsurance(purchaseOrderPo, insuranceVo, userPo, autoPO, createTime, quoteRecordPo, institutionPo, insuranceCompanyPo, insurancePackagePO, model, currentDate)
        } else if (model.insuranceType == InsuranceOfflineDataModel.INSURANCE_TYPE_COMPULSORY) {
            saveCompulsoryInsurance(purchaseOrderPo, compulsoryInsuranceVo, userPo, autoPO, createTime, quoteRecordPo, institutionPo, insuranceCompanyPo, insurancePackagePO, model, currentDate)
        }
        PurchaseOrder purchaseOrder = savePurchaseOrder(purchaseOrderPo, purchaseOrderVo, history, autoPO, userPo, quoteRecordPo, sourceId, description, createTime, currentDate, model, isNewCar, admin, isFullRefund)
        OrderOperationInfo orderOperationInfo = saveOrderOperationInfo(purchaseOrderPo, purchaseOrder, createTime, currentDate, admin, isFullRefund)
        savePayment(purchaseOrderPo, purchaseOrder, description, currentDate, isFullRefund, orderOperationInfo, quoteRecordPo)
        Agent agentPo = saveAgent(agentVo, beforeCreateTime, currentDate, model, userPo, description, admin)
        InsurancePurchaseOrderRebate insurancePurchaseOrderRebate = saveInsurancePurchaseOrderRebate(purchaseOrderPo, insurancePurchaseOrderRebateVo, purchaseOrder, agentPo, institutionPo, createTime, currentDate, isFullRefund)
        saveOfflineDataHistory(model, history, purchaseOrder, insurancePurchaseOrderRebate, isFullRefund)
    }

    User saveUser(PurchaseOrder purchaseOrderPo, model, history, beforeCreateTime, currentDate) {
        User user
        if (purchaseOrderPo) {
            user = purchaseOrderPo.getApplicant()
        } else {
            user = userRepository.findFirstByIdentity(model.getAgentIdentity() as String)
        }
        if (user == null) {
            user = new User()
            user.setIdentity(model.getAgentIdentity())
            user.setArea(history.area)
            user.setIdentityType(IdentityType.Enum.IDENTITYCARD)
            user.setCreateTime(beforeCreateTime)
            user.setUpdateTime(currentDate)
            if (model.agentName) user.setName(model.agentName)
            user.setUserType(UserType.Enum.Agent)
            user.setSourceType(OrderSourceType.Enum.OFFLINE_6)
            user = userRepository.save(user)
        } else {
            user.setUserType(UserType.Enum.Agent)
            if (user.area == null) user.setArea(history.area)
            if (model.agentName) {
                if (user.name == null) {
                    user.setName(model.agentName)
                } else if (user.name != model.agentName) {
                    throw new FileUploadException("代理人姓名与数据库不一致，数据库名称:${user.name}")
                }
            }
            if (user.sourceType == null) user.setSourceType(OrderSourceType.Enum.OFFLINE_6)
            user.setUpdateTime(currentDate)
            user = userRepository.save(user)
        }
        return user
    }

    AutoType saveAutoType(purchaseOrderPo, InsuranceOfflineDataModel model) {
        AutoType autoType
        if (purchaseOrderPo) {
            autoType = purchaseOrderPo.auto?.autoType
        }
        if (autoType) {
            if (!autoType.seats || (model.seats && autoType.seats != model.seats)) autoType.setSeats(model.seats)
            if (!autoType.newPrice || (model.newPrice && autoType.newPrice != model.newPrice)) autoType.setNewPrice(model.newPrice)
            if (!autoType.code || (model.code && autoType.code != model.code)) autoType.setCode(model.code)
        } else {
            autoType = new AutoType()
            if (model.code) autoType.setCode(model.code)
            if (model.seats) autoType.setSeats(model.seats)
            if (model.newPrice) autoType.setNewPrice(model.newPrice)
        }
        autoTypeRepository.save(autoType)
        return autoType
    }

    Auto saveAuto(PurchaseOrder purchaseOrderPo, autoVo, history, beforeCreateTime, currentDate, autoType, user, model, isNewCar) {
        Auto auto
        if (purchaseOrderPo) {
            //更新
            auto = purchaseOrderPo.getAuto()
            if (isNewCar) {//当前车辆是新车
                //do nothing for licensePlateNo
            } else if ("新车" == auto.licensePlateNo || auto.engineNo.endsWith(auto.licensePlateNo)) {//已经导入的车辆是新车并且当前不是新车
                auto.setLicensePlateNo(model.licenseNo)
            } else if (model.licenseNo != auto.licensePlateNo) {
                throw new FileUploadException("车牌号与数据库中已经存在的数据不一致!")
            }
            if (model.owner) auto.setOwner(model.owner)
            if (model.kilometerPerYear) auto.setKilometerPerYear(model.kilometerPerYear)
            if (model.enrollDate) auto.setEnrollDate(model.enrollDate)
            if (history.area) auto.setArea(history.area)
            autoRepository.save(auto)
        } else {
            //新建
            auto = autoVo
            if (model.owner) auto.setOwner(model.owner)
            if (model.kilometerPerYear) auto.setKilometerPerYear(model.kilometerPerYear)
            if (model.enrollDate) auto.setEnrollDate(model.enrollDate)
            auto.setAutoType(autoType)
            auto.setCreateTime(beforeCreateTime)
            auto.setUpdateTime(currentDate)
            if (history.area) auto.setArea(history.area)
            auto = autoService.saveOrMerge(auto, user, false, new StringBuilder(), false)
        }
        return auto
    }

    QuoteRecord saveQuoteRecord(PurchaseOrder purchaseOrderPo, insuranceType, insuranceVo, compulsoryInsuranceVo, history, autoPO, user, insuranceCompanyPo, insurancePackagePO, createTime, currentDate, model) {
        QuoteRecord quoteRecord
        if (purchaseOrderPo) {
            quoteRecord = quoteRecordRepository.findByOrderId(purchaseOrderPo.getId())
        } else {
            quoteRecord = buildQuoteRecord(insuranceType, insuranceVo, compulsoryInsuranceVo)
            quoteRecord.setCreateTime(createTime)
            quoteRecord.setAuto(autoPO)
            quoteRecord.setApplicant(user)
            quoteRecord.setQuoteFlowType(QuoteFlowType.Enum.GENERAL)
            quoteRecord.setArea(history.getArea())
            quoteRecord.setInsuranceCompany(insuranceCompanyPo)
        }
        if (model.effectiveDate) {
            if (insuranceType == InsuranceOfflineDataModel.INSURANCE_TYPE_COMMERCIAL) {
                quoteRecord.setEffectiveDate(model.effectiveDate)
            } else {
                quoteRecord.setCompulsoryEffectiveDate(model.effectiveDate)
            }
        }
        quoteRecord.setInsurancePackage(insurancePackagePO)
        quoteRecord.setUpdateTime(currentDate)
        quoteRecordRepository.save(quoteRecord)
        return quoteRecord
    }

    Institution saveInstitution(institutionVo, beforeCreateTime, currentDate, admin, description) {
        def institutionPo = institutionRepository.findFirstByName(institutionVo.getName())
        if (institutionPo == null) {
            institutionVo.setInstitutionType(Institution.Enum.INSTITUTION_TYPE_OFFLINE_IMPORT)
            institutionVo.setCreateTime(beforeCreateTime)
            institutionVo.setUpdateTime(currentDate)
            institutionVo.setOperator(admin)
            institutionVo.setComment(description)
            institutionPo = institutionRepository.save(institutionVo)
        }
        return institutionPo
    }


    Insurance saveInsurance(PurchaseOrder purchaseOrderPo, insurance, user, autoPO, createTime, quoteRecordPo, institutionPo, insuranceCompanyPo, insurancePackagePO, model, currentDate) {
        if (purchaseOrderPo) {
            insurance = model.insurance
        } else {
            insurance.setCreateTime(createTime)
            insurance.setDiscount(1.000000d)
            insurance.setApplicant(user)
            insurance.setAuto(autoPO)
            insurance.setInsuranceCompany(insuranceCompanyPo)
            insurance.setQuoteRecord(quoteRecordPo)
        }
        insurance.setInstitution(institutionPo)
        insurance.setInsurancePackage(insurancePackagePO)
        if (model.owner) insurance.setInsuredName(model.owner)
        if (model.applicantName) insurance.setApplicantName(model.applicantName)
        if (model.effectiveDate) insurance.setEffectiveDate(model.effectiveDate)
        insurance.setUpdateTime(currentDate)
        insuranceRepository.save(insurance)
    }

    CompulsoryInsurance saveCompulsoryInsurance(PurchaseOrder purchaseOrderPo, compulsoryInsurance, user, autoPO, createTime, quoteRecordPo, institutionPo, insuranceCompanyPo, insurancePackagePO, model, currentDate) {
        if (purchaseOrderPo) {
            compulsoryInsurance = model.compulsoryInsurance
        } else {
            compulsoryInsurance.setCreateTime(createTime)
            compulsoryInsurance.setDiscount(1.000000d)
            compulsoryInsurance.setApplicant(user)
            compulsoryInsurance.setAuto(autoPO)
            compulsoryInsurance.setQuoteRecord(quoteRecordPo)
            compulsoryInsurance.setInsuranceCompany(insuranceCompanyPo)
            compulsoryInsurance.setQuoteRecord(quoteRecordPo)
        }
        compulsoryInsurance.setInstitution(institutionPo)
        compulsoryInsurance.setInsurancePackage(insurancePackagePO)
        if (model.owner) compulsoryInsurance.setInsuredName(model.owner)
        if (model.applicantName) compulsoryInsurance.setApplicantName(model.applicantName)
        if (model.effectiveDate) compulsoryInsurance.setEffectiveDate(model.effectiveDate)
        compulsoryInsurance.setUpdateTime(currentDate)
        compulsoryInsuranceRepository.save(compulsoryInsurance)
        return compulsoryInsurance
    }

    PurchaseOrder savePurchaseOrder(PurchaseOrder purchaseOrderPo, purchaseOrder, history, autoPO, user, quoteRecordPo, sourceId, description, createTime, currentDate, model, isNewCar, admin, isFullRefund) {
        description = isNewCar ? description + ",新车未上牌" : description
        if (purchaseOrderPo) {
            purchaseOrder = purchaseOrderPo
            purchaseOrder.appendDescription(description + ",oldSourceId:" + purchaseOrder.orderSourceId)
            if (model.applicantName) purchaseOrder.setApplicantName(model.applicantName)
            if (isFullRefund) {
                purchaseOrder.setStatus(OrderStatus.Enum.REFUNDED_9)
                purchaseOrder.setStatusDisplay(null)
            }
        } else {
            purchaseOrder.setArea(history.area)
            purchaseOrder.setAuto(autoPO)
            purchaseOrder.setApplicant(user)
            purchaseOrder.setObjId(quoteRecordPo.getId())
            purchaseOrder.setType(OrderType.Enum.INSURANCE)
            purchaseOrder.setOrderSourceType(OrderSourceType.Enum.OFFLINE_6)
            purchaseOrder.setStatus(OrderStatus.Enum.FINISHED_5)
            purchaseOrder.setDeliveryAddress(createAddress(user, history.area, addressRepository))
            purchaseOrder.setOrderNo(purchaseOrderIdService.getNextByTime(OrderType.Enum.INSURANCE, createTime, 1))
            purchaseOrder.setChannel(PaymentChannel.Enum.OFFLINE_PAY_19)
            purchaseOrder.setSourceChannel(Channel.Enum.WAP_8)
            purchaseOrder.setDescription(description)
            purchaseOrder.setSendDate(createTime)
            purchaseOrder.setOperator(admin)
            if (model.applicantName) purchaseOrder.setApplicantName(model.applicantName)
        }
        purchaseOrder.setUpdateTime(currentDate)
        purchaseOrder.setOrderSourceId(sourceId as String)
        purchaseOrderRepository.save(purchaseOrder)
        return purchaseOrder
    }

    OrderOperationInfo saveOrderOperationInfo(PurchaseOrder purchaseOrderPo, PurchaseOrder purchaseOrder, Date createTime, Date currentDate, admin, isFullRefund) {
        OrderOperationInfo orderOperationInfo
        if (purchaseOrderPo) {
            orderOperationInfo = orderOperationInfoRepository.findFirstByPurchaseOrder(purchaseOrderPo)
            if (isFullRefund) {
                orderOperationInfo.setOriginalStatus(orderOperationInfo.getCurrentStatus())
                orderOperationInfo.setCurrentStatus(OrderTransmissionStatus.Enum.REFUNDED)
            }
        } else {
            orderOperationInfo = new OrderOperationInfo()
            orderOperationInfo.setPurchaseOrder(purchaseOrder)
            orderOperationInfo.setCurrentStatus(OrderTransmissionStatus.Enum.ORDER_INPUTED)
            orderOperationInfo.setCreateTime(createTime)
            orderOperationInfo.setConfirmOrderDate(createTime)
            orderOperationInfo.setAssigner(admin)
            orderOperationInfo.setOwner(admin)
            orderOperationInfo.setOperator(admin)
        }
        orderOperationInfo.setUpdateTime(currentDate)
        orderOperationInfoRepository.save(orderOperationInfo)
        return orderOperationInfo
    }

    Payment savePayment(purchaseOrderPo, PurchaseOrder purchaseOrder, String description, Date currentDate, isFullRefund, orderOperationInfo, quoteRecord) {
        Payment payment
        if (purchaseOrderPo && !isFullRefund) {//补充信息
            payment = paymentRepository.findFirstByPurchaseOrderAndPaymentTypeOrderByIdDesc(purchaseOrderPo, PaymentType.Enum.INITIALPAYMENT_1)
        } else {
            payment = new Payment()
            if (isFullRefund) {
                payment.setComments("该订单是由excel文档导入的订单订单，状态置退款成功")
                payment.setPaymentType(PaymentType.Enum.FULLREFUND_4)
                savePurchaseOrderAmend(purchaseOrderPo, orderOperationInfo, quoteRecord)
            } else {
                payment.setComments("该订单是由excel文档导入的订单订单，状态置为已支付")
                payment.setPaymentType(PaymentType.Enum.INITIALPAYMENT_1)
            }
            payment.setStatus(PaymentStatus.Enum.PAYMENTSUCCESS_2)
            payment.setPurchaseOrder(purchaseOrder)
            payment.setChannel(PaymentChannel.Enum.OFFLINE_PAY_19)
            payment.setClientType(Channel.Enum.WAP_8)

            payment.setCreateTime(purchaseOrder.getCreateTime())
        }

        payment.setUser(purchaseOrder.getApplicant())
        payment.setAmount(purchaseOrder.getPaidAmount())
        payment.setOperator(purchaseOrder.getOperator())

        payment.setDescription(description)
        payment.setUpdateTime(currentDate)
        paymentRepository.save(payment)
        return payment
    }

    PurchaseOrderAmend savePurchaseOrderAmend(purchaseOrderPo, orderOperationInfo, quoteRecord) {
        PurchaseOrderAmend purchaseOrderAmend = new PurchaseOrderAmend()
        purchaseOrderAmend.setPurchaseOrder(purchaseOrderPo)
        purchaseOrderAmend.setOrderOperationInfo(orderOperationInfo)
        purchaseOrderAmend.setNewQuoteRecord(quoteRecord)
        purchaseOrderAmend.setOriginalQuoteRecord(quoteRecord)
        purchaseOrderAmend.setPaymentType(PaymentType.Enum.FULLREFUND_4)
        purchaseOrderAmend.setPurchaseOrderAmendStatus(PurchaseOrderAmendStatus.Enum.FINISHED)
        purchaseOrderAmend.setCreateTime(purchaseOrderPo.createTime)
        purchaseOrderAmend.setDescription("该订单是由excel文档导入的订单订单，状态置退款成功")
        purchaseOrderAmend.setPurchaseOrderHistory(null)
        purchaseOrderAmendRepository.save(purchaseOrderAmend)
    }

    Agent saveAgent(agentVo, beforeCreateTime, currentDate, model, user, description, admin) {
        Agent agent = agentRepository.findByIdentity(agentVo.getIdentity())
        if (agent == null) {
            agentVo.setAgentType(Agent.Enum.AGENT_TYPE_OFFLINE_IMPORT)
            agentVo.setEnable(true)
            agentVo.setCreateTime(beforeCreateTime)
            agentVo.setUpdateTime(currentDate)
            agentVo.setName(model.getAgentName())
            agentVo.setMobile(user.getMobile())
            agentVo.setUser(user)
            agentVo.setComment(description)
            agentVo.setIdentityType(IdentityType.Enum.IDENTITYCARD)
            agentVo.setOperator(admin)
            if (model.cardNumber) agentVo.setCardNumber(model.cardNumber)
            agent = agentRepository.save(agentVo)
        } else {
            if (model.agentName) agent.setName(model.agentName)
            if (model.cardNumber) agentVo.setCardNumber(model.cardNumber)
            agentRepository.save(agent)
        }
        return agent
    }

    InsurancePurchaseOrderRebate saveInsurancePurchaseOrderRebate(PurchaseOrder purchaseOrderPo, InsurancePurchaseOrderRebate insurancePurchaseOrderRebateVo, PurchaseOrder purchaseOrder, Agent agent, Institution institution, Date createTime, Date currentDate, isFullRefund) {
        if (purchaseOrderPo) {
            InsurancePurchaseOrderRebate insurancePurchaseOrderRebate = insurancePurchaseOrderRebateRepository.findFirstByPurchaseOrder(purchaseOrderPo)
            if (!insurancePurchaseOrderRebate.upCommercialAmount && insurancePurchaseOrderRebateVo.upCommercialAmount) {
                insurancePurchaseOrderRebate.setUpCommercialAmount(Math.abs(insurancePurchaseOrderRebateVo.upCommercialAmount))
            }
            if (!insurancePurchaseOrderRebate.upCompulsoryAmount && insurancePurchaseOrderRebateVo.upCompulsoryAmount) {
                insurancePurchaseOrderRebate.setUpCompulsoryAmount(Math.abs(insurancePurchaseOrderRebateVo.upCompulsoryAmount))
            }
            if (!insurancePurchaseOrderRebate.upCommercialRebate && insurancePurchaseOrderRebateVo.upCommercialRebate) {
                insurancePurchaseOrderRebate.setUpCommercialRebate(Math.abs(insurancePurchaseOrderRebateVo.upCommercialRebate))
            }
            if (!insurancePurchaseOrderRebate.upCompulsoryRebate && insurancePurchaseOrderRebateVo.upCompulsoryRebate) {
                insurancePurchaseOrderRebate.setUpCompulsoryRebate(Math.abs(insurancePurchaseOrderRebateVo.upCompulsoryRebate))
            }
            if (!insurancePurchaseOrderRebate.downCommercialAmount && insurancePurchaseOrderRebateVo.downCommercialAmount) {
                insurancePurchaseOrderRebate.setDownCommercialAmount(Math.abs(insurancePurchaseOrderRebateVo.downCommercialAmount))
            }
            if (!insurancePurchaseOrderRebate.downCompulsoryAmount && insurancePurchaseOrderRebateVo.downCompulsoryAmount) {
                insurancePurchaseOrderRebate.setDownCompulsoryAmount(Math.abs(insurancePurchaseOrderRebateVo.downCompulsoryAmount))
            }
            if (!insurancePurchaseOrderRebate.downCommercialRebate && insurancePurchaseOrderRebateVo.downCommercialRebate) {
                insurancePurchaseOrderRebate.setDownCommercialRebate(Math.abs(insurancePurchaseOrderRebateVo.downCommercialRebate))
            }
            if (!insurancePurchaseOrderRebate.downCompulsoryRebate && insurancePurchaseOrderRebateVo.downCompulsoryRebate) {
                insurancePurchaseOrderRebate.setDownCompulsoryRebate(Math.abs(insurancePurchaseOrderRebateVo.downCompulsoryRebate))
            }
            return insurancePurchaseOrderRebateRepository.save(insurancePurchaseOrderRebate)
        } else {
            insurancePurchaseOrderRebateVo.setPurchaseOrder(purchaseOrder)
            insurancePurchaseOrderRebateVo.setUpChannelId(agent.getId())
            insurancePurchaseOrderRebateVo.setUpRebateChannel(RebateChannel.Enum.REBATE_CHANNEL_AGENT)
            insurancePurchaseOrderRebateVo.setDownChannelId(institution.getId())
            insurancePurchaseOrderRebateVo.setDownRebateChannel(RebateChannel.Enum.REBATE_CHANNEL_INSTITUTION)
            insurancePurchaseOrderRebateVo.setCreateTime(createTime)
            insurancePurchaseOrderRebateVo.setUpdateTime(currentDate)
            return insurancePurchaseOrderRebateRepository.save(insurancePurchaseOrderRebateVo)
        }
    }

    OfflineDataHistory saveOfflineDataHistory(model, history, purchaseOrder, insurancePurchaseOrderRebate, isFullRefund) {
        OfflineDataVersionModel versionModel = new OfflineDataVersionModel()
        versionModel.setPolicyNo(model.policyNo)
        versionModel.setLicensePlateNo(purchaseOrder.auto.licensePlateNo)
        versionModel.setCode(purchaseOrder.auto.autoType?.code)
        versionModel.setTotalPremium(purchaseOrder.payableAmount)
        versionModel.setIdentity(purchaseOrder.applicant.identity)
        versionModel.setDownCompulsoryAmount(insurancePurchaseOrderRebate.downCompulsoryAmount)
        versionModel.setDownCommercialAmount(insurancePurchaseOrderRebate.downCommercialAmount)

        OfflineDataHistory offlineDataHistory = model.offlineDataHistory
        if (offlineDataHistory == null) {
            offlineDataHistory = new OfflineDataHistory()
            offlineDataHistory.setPurchaseOrderId(purchaseOrder.id)
            offlineDataHistory.setPolicyNo(model.policyNo)
        }
        offlineDataHistory.setHistoryId(history.id)
        offlineDataHistory.setSourceId(purchaseOrder.orderSourceId)
        offlineDataHistory.setDataSource(history.type)
        offlineDataHistory.setDataVersion(OfflineDataVersionGenerator.generate(versionModel))
        if (isFullRefund) offlineDataHistory.setComment("全额退款")
        offlineDataHistoryRepository.save(offlineDataHistory)
    }


    static InsuranceOfflineDataModel buildRawData(Map map) {
        map['rawData'] as InsuranceOfflineDataModel
    }

    static Agent buildAgent(Map map) {
        map['agent'] as Agent
    }

    static Institution buildInstitution(Map map) {
        map['institution'] as Institution
    }

    static Auto buildAuto(def map) {
        map['auto'] as Auto
    }

    static InsuranceCompany buildInsuranceCompany(Map map) {
        map['insuranceCompany'] as InsuranceCompany
    }

    static Insurance buildInsurance(Map map) {
        map['insurance'] as Insurance
    }

    static CompulsoryInsurance buildCompulsoryInsurance(Map map) {
        map['compulsoryInsurance'] as CompulsoryInsurance
    }

    static QuoteRecord buildQuoteRecord(String insuranceType, Insurance insurance, CompulsoryInsurance compulsoryInsurance) {
        QuoteRecord quoteRecord = new QuoteRecord()
        if (insuranceType == InsuranceOfflineDataModel.INSURANCE_TYPE_COMMERCIAL) {
            quoteRecord.setEffectiveDate(insurance.effectiveDate)
            quoteRecord.setExpireDate(insurance.expireDate)
            quoteRecord.setPremium(insurance.getPremium())
            quoteRecord.setType(QuoteSource.Enum.TELEMARKETING_3)
            quoteRecord.setChannel(Channel.Enum.ORDER_CENTER_11)
            String[] contains = ['thirdPartyPremium', 'thirdPartyAmount', 'damagePremium', 'damageAmount', 'theftPremium', 'theftAmount', 'enginePremium', 'engineAmount',
                                 'driverPremium', 'driverAmount', 'passengerPremium', 'passengerAmount', 'passengerCount', 'spontaneousLossPremium',
                                 'spontaneousLossAmount', 'glassPremium', 'glassAmount', 'scratchAmount', 'scratchPremium', 'damageIop', 'thirdPartyIop',
                                 'theftIop', 'engineIop', 'driverIop', 'passengerIop', 'scratchIop']
            BeanUtil.copyPropertiesContain(insurance, quoteRecord, contains)
        } else if (insuranceType == InsuranceOfflineDataModel.INSURANCE_TYPE_COMPULSORY) {
            quoteRecord.setCompulsoryEffectiveDate(compulsoryInsurance.effectiveDate)
            quoteRecord.setCompulsoryExpireDate(compulsoryInsurance.expireDate)
            quoteRecord.setCompulsoryPremium(compulsoryInsurance.getCompulsoryPremium())
            quoteRecord.setAutoTax(compulsoryInsurance.getAutoTax())
        }
        quoteRecord
    }

    static PurchaseOrder buildPurchaseOrder(Map map) {
        map['purchaseOrder'] as PurchaseOrder
    }

    static InsurancePurchaseOrderRebate buildInsurancePurchaseOrderRebate(Map map) {
        map['rebate'] as InsurancePurchaseOrderRebate
    }


    InsurancePackage createInsurancePackage(Insurance insurance, CompulsoryInsurance compulsoryInsurance) {
        InsurancePackage insurancePackage = new InsurancePackage()

        if (compulsoryInsurance && compulsoryInsurance.getCompulsoryPremium() > 0) {
            insurancePackage.setCompulsory(true)
            insurancePackage.setAutoTax(compulsoryInsurance.getAutoTax() > 0)
        } else {
            insurancePackage.setCompulsory(false)
            insurancePackage.setAutoTax(false)
        }
        if (insurance && insurance.getThirdPartyPremium() > 0) {
            insurancePackage.setThirdPartyAmount(insurance.getThirdPartyAmount())
            insurancePackage.setThirdPartyIop(insurance.getThirdPartyIop() > 0 || insurance.getIopTotal() > 0)
        } else {
            insurancePackage.setThirdPartyAmount(null)
            insurancePackage.setThirdPartyIop(false)
        }
        if (insurance && insurance.getDamagePremium() > 0) {
            insurancePackage.setDamage(true)
            insurancePackage.setDamageIop(insurance.getDamageIop() > 0 || insurance.getIopTotal() > 0)
        } else {
            insurancePackage.setDamage(false)
            insurancePackage.setDamageIop(false)
        }
        if (insurance && insurance && insurance.getTheftPremium() > 0) {
            insurancePackage.setTheft(true)
            insurancePackage.setTheftIop(insurance.getTheftIop() > 0 || insurance.getIopTotal() > 0)
        } else {
            insurancePackage.setTheft(false)
            insurancePackage.setTheftIop(false)
        }
        if (insurance && insurance.getEnginePremium() > 0) {
            insurancePackage.setEngine(true)
            insurancePackage.setEngineIop(insurance.getEngineIop() > 0 || insurance.getIopTotal() > 0)
        } else {
            insurancePackage.setEngine(false)
            insurancePackage.setEngineIop(false)
        }
        if (insurance && insurance.getGlassPremium() > 0) {
            insurancePackage.setGlass(true)
            insurancePackage.setGlassType(GlassType.Enum.DOMESTIC_1)
        } else {
            insurancePackage.setGlass(false)
            insurancePackage.setGlassType(null)
        }
        if (insurance && insurance.getDriverPremium() > 0) {
            insurancePackage.setDriverAmount(insurance.getDriverAmount())
            insurancePackage.setDriverIop(insurance.getDriverIop() > 0 || insurance.getIopTotal() > 0)
        } else {
            insurancePackage.setDriverAmount(null)
            insurancePackage.setDriverIop(false)
        }
        if (insurance && insurance.getPassengerPremium() > 0) {
            insurancePackage.setPassengerAmount(insurance.getPassengerAmount())
            insurancePackage.setPassengerIop(insurance.getPassengerIop() > 0 || insurance.getIopTotal() > 0)
        } else {
            insurancePackage.setPassengerAmount(null)
            insurancePackage.setPassengerIop(false)
        }
        if (insurance && insurance.getSpontaneousLossPremium() > 0) {
            insurancePackage.setSpontaneousLoss(true)
            insurancePackage.setSpontaneousLossIop(insurance.getSpontaneousLossIop() > 0 || insurance.getIopTotal() > 0)
        }
        if (insurance && insurance.getScratchPremium() > 0) {
            insurancePackage.setScratchAmount(insurance.getScratchAmount())
            insurancePackage.setScratchIop(insurance.getScratchIop() > 0 || insurance.getIopTotal() > 0)
        } else {
            insurancePackage.setScratchAmount(null)
            insurancePackage.setScratchIop(false)
        }
        insurancePackage.setUnableFindThirdParty(insurance != null && insurance.getUnableFindThirdPartyPremium() > 0)
        insurancePackage.calculateUniqueString()

        InsurancePackage old = insurancePackageRepository.findFirstByUniqueString(insurancePackage.getUniqueString())
        if (null != old) {
            return old
        }
        insurancePackageRepository.save(insurancePackage)
    }


    static def createAddress(User user, Area area, AddressRepository addressRepository) {
        List<Address> addressList = addressRepository.findByApplicant(user)
        if (CollectionUtils.isNotEmpty(addressList)) {
            return addressList.get(0)
        } else {
            Address address = new Address()
            address.setApplicant(user)
            address.setStreet("北苑路北")
            address.setArea(area)
            address.setCity(area.getId().toString())
            address.setCreateTime(user.createTime)
            addressRepository.save(address)
        }
    }

    Boolean premiumCheck(model, totalPremiumDouble, byPolicyNo) {
        if (byPolicyNo) {
            def purchaseOrder = purchaseOrderRepository.findFirstByObjIdOrderByCreateTimeDesc(byPolicyNo.quoteRecord.id)
            model.setPurchaseOrder(purchaseOrder)
            if (totalPremiumDouble < 0) {
                if (purchaseOrder.status == OrderStatus.Enum.REFUNDED_9) {
                    throw new FileUploadException("该订单已经退款!")
                } else if (DoubleUtils.equalsDouble(-totalPremiumDouble, purchaseOrder.payableAmount)) {
                    model.fullRefund = true
                    return true
                } else {
                    throw new FileUploadException("暂不支持部分退款!")
                }
            } else if (!DoubleUtils.equalsDouble(totalPremiumDouble, purchaseOrder.payableAmount)) {
                throw new FileUploadException("总保费与数据库金额不一致!")
            } else {
                return true
            }
        } else {
            if (totalPremiumDouble && totalPremiumDouble < 0) throw new FileUploadException("请先导入该保单数据的支付记录!")
        }
        return false;
    }


    abstract InsuranceOfflineDataModel readExcelRow(Row cells)

    abstract InsuranceOfflineDataModel checkRow(InsuranceOfflineDataModel insuranceOfflineDataModel, Set<String> strings, OfflineOrderImportHistory history)

}

package com.cheche365.cheche.fanhua.service

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.model.Agent
import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.Institution
import com.cheche365.cheche.core.model.MoFanhuaSyncMessage
import com.cheche365.cheche.core.model.OrderSourceType
import com.cheche365.cheche.core.model.UserType
import com.cheche365.cheche.core.mongodb.repository.MoFanhuaSyncMessageRepository
import com.cheche365.cheche.core.repository.AreaRepository
import com.cheche365.cheche.core.util.AutoUtils
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.fanhua.model.FanhuaSuite
import com.cheche365.cheche.fanhua.repository.FanhuaInstitutionRepository
import com.cheche365.cheche.fanhua.repository.FanhuaSuiteRepository
import com.cheche365.cheche.fanhua.util.IdentityTypeMappings
import com.cheche365.cheche.manage.common.service.reverse.InsuranceReverseProcess
import com.cheche365.cheche.manage.common.service.reverse.OrderReverse
import com.cheche365.cheche.manage.common.web.model.InsurancePurchaseOrderRebateViewModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

import java.text.SimpleDateFormat

import static com.cheche365.cheche.core.model.MoFanhuaSyncMessage.Enum.FAILED
import static com.cheche365.cheche.core.model.MoFanhuaSyncMessage.Enum.NEW
import static com.cheche365.cheche.core.model.MoFanhuaSyncMessage.Enum.SUCCESS
import static com.cheche365.cheche.fanhua.model.FanhuaSuite.DAMAGE
import static com.cheche365.cheche.fanhua.model.FanhuaSuite.DAMAGE_IOP
import static com.cheche365.cheche.fanhua.model.FanhuaSuite.DRIVER
import static com.cheche365.cheche.fanhua.model.FanhuaSuite.DRIVER_IOP
import static com.cheche365.cheche.fanhua.model.FanhuaSuite.ENGINE
import static com.cheche365.cheche.fanhua.model.FanhuaSuite.ENGINE_IOP
import static com.cheche365.cheche.fanhua.model.FanhuaSuite.GLASS
import static com.cheche365.cheche.fanhua.model.FanhuaSuite.PASSENGER
import static com.cheche365.cheche.fanhua.model.FanhuaSuite.PASSENGER_IOP
import static com.cheche365.cheche.fanhua.model.FanhuaSuite.SCRATCH
import static com.cheche365.cheche.fanhua.model.FanhuaSuite.SCRATCH_IOP
import static com.cheche365.cheche.fanhua.model.FanhuaSuite.SPONTANEOUS_LOSS
import static com.cheche365.cheche.fanhua.model.FanhuaSuite.SPONTANEOUS_LOSS_IOP
import static com.cheche365.cheche.fanhua.model.FanhuaSuite.THEFT
import static com.cheche365.cheche.fanhua.model.FanhuaSuite.THEFT_IOP
import static com.cheche365.cheche.fanhua.model.FanhuaSuite.THIRD_PARTY
import static com.cheche365.cheche.fanhua.model.FanhuaSuite.THIRD_PARTY_IOP
import static com.cheche365.cheche.fanhua.model.FanhuaSuite.UNABLE_FIND_THIRD_PARTY

/**
 * TODO:失败数据处理
 * Created by zhangtc on 2017/12/4.
 */
@Service
class SyncService {

    private Logger logger = LoggerFactory.getLogger(this.getClass())

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    @Autowired
    MoFanhuaSyncMessageRepository moFanhuaSyncMessageRepository
    @Autowired
    FanhuaSuiteRepository fanhuaSuiteRepository
    @Autowired
    FanhuaInstitutionRepository fanhuaInstitutionRepository
    @Autowired
    AreaRepository areaRepository
    @Autowired
    InsuranceReverseProcess flows

    /**
     * 上传报文暂存到MongoDB
     */
    def saveRequest(String body) {
        moFanhuaSyncMessageRepository.save(new MoFanhuaSyncMessage(
            messageType: NEW,
            createTime: new Date(),
            content: body
        ))
    }

    /**
     * 报文数据持久化
     */
    def List saveInsurance() {
        List<Map> failedMessages = new ArrayList()
        Date date = DateUtils.getCustomDate(new Date(), 0, 0, 0, 0)
        boolean hasNew = true
        while (hasNew) {
            Page<MoFanhuaSyncMessage> messages = moFanhuaSyncMessageRepository.findByMessageTypeAndCreateTime(NEW, date, buildPageable(1, 100))
            logger.info("泛华保单入库开始，剩余记录数量:{}", messages.getTotalElements())
            for (MoFanhuaSyncMessage moFanhuaSyncMessage : messages.getContent()) {
                boolean saveOrderReverse = true
                try {
                    Map recordInsuranceCover = CacheUtil.doJacksonDeserialize moFanhuaSyncMessage.getContent(), Map.class
                    for (Map recordInsurance : recordInsuranceCover.res) {
                        OrderReverse orderReverse = toOrderReverse recordInsurance
                        def result = flows.doService(orderReverse)
                        saveOrderReverse = result != null
                    }
                    moFanhuaSyncMessage.messageType = saveOrderReverse ? SUCCESS : FAILED
                } catch (Exception e) {
                    logger.error("泛华保单入库失败 id:{}", moFanhuaSyncMessage.id, e)
                    failedMessages.add(moFanhuaSyncMessage.failedMap(e.toString()))
                    saveOrderReverse = false
                } finally {
                    moFanhuaSyncMessage.messageType = saveOrderReverse ? SUCCESS : FAILED
                    moFanhuaSyncMessageRepository.save moFanhuaSyncMessage
                }
            }
            hasNew = messages.getContent().size() > 0
        }
        failedMessages
    }

    OrderReverse toOrderReverse(Map ri) {
        Map risks = getRiskMap(ri.suiteCode)
        OrderReverse or = new OrderReverse()
        // 车辆

        if (ri.carInfo.plateNum.length() < 2) {
            or.isNewCar = true
            or.licensePlateNo = ''
        } else {
            or.isNewCar = false
            or.licensePlateNo = ri.carInfo.plateNum             //车牌号
        }
        or.owner = ri.carInfo.owner                         //车主姓名
//        or.identity = ri.insuredPersonInfo.certNumber       //车主证件号
        or.vinNo = ri.carInfo.vin                           //车辆车架号
        or.engineNo = ri.carInfo.engineNum                  //车辆发动机号
        or.enrollDate = ri.carInfo.firstRegDate             //车辆登记日期
        or.brand = ri.carInfo.carModelName      //车辆品牌
        or.insuredIdNo = ri.insuredPersonInfo.certNumber    //被保人证件号
        or.insuredIdType = IdentityTypeMappings.getLocal(ri.insuredPersonInfo.certKind)//被保人证件类型
        or.insuredName = ri.insuredPersonInfo.fullName    //被保人姓名
        //代理人
        or.agentType = Agent.Enum.AGENT_TYPE_OFFLINE_IMPORT
        or.identityType = IdentityTypeMappings.getLocal('')
        or.agentName = ri.agentInfo.agentName
        or.agentIdentity = ri.agentInfo.agentCode   //代理人身份证 目前没提供先用工号
        or.comment = ri.agentInfo.agentCode
        or.userType = UserType.Enum.Agent
        or.orderSourceType = OrderSourceType.Enum.INTF_SYNC_7
        //机构
        Map institution = getInstitution(ri.agencyOrgList)
        or.insuranceCompany = risks.ic                                          //保险公司ID
        or.institutionType = Institution.Enum.INSTITUTION_TYPE_OFFLINE_IMPORT   // 出单机构类型
        or.area = getCity(ri.carInfo.plateNum, institution.comCode, or.isNewCar)//城市
        or.institutionName = institution.fullName                               //出单机构
        //保单
        or.originalPremium = Double.valueOf(ri.charge)      //应付金额
        or.rebateExceptPremium = Double.valueOf(ri.charge)  //实付金额
        or.applicantDate = sdf.parse(ri.effectiveDate)                   //投保日期
        or.confirmOrderDate = ri.effectiveDate                           //确认出单日期
        if ('2' == risks.insuranceType) {
            or.quoteCommercialPolicyNo = ri.policyNum                           //报价商业险保单号
            or.commercialPolicyNo = ri.policyNum                           //商业险保单号
            or.commercialPremium = Double.valueOf ri.charge                           //商业险保费
            or.commercialEffectiveDate = ri.effectiveDate                           //商业险生效日期
            or.commercialEffectiveHour = 0                           //商业险生效小时 12/24
            or.commercialExpireDate = ri.expiryDate                           //商业险终保日期
            or.commercialExpireHour = 24                           //商业险终保小时 12/24

            ri.suite.collect() { unit ->
                switch (risks.get(unit.ecode)) {
                    case DAMAGE:
                        or.damageAmount = Double.valueOf unit.amount                      //车损险保额
                        or.damagePremium = Double.valueOf unit.charge                           //车损险保费
                        break;
                    case THIRD_PARTY:
                        or.thirdPartyAmount = Double.valueOf unit.amount                           //三者险保额
                        or.thirdPartyPremium = Double.valueOf unit.charge                           //三者险保费
                        break;
                    case DRIVER:
                        or.driverAmount = Double.valueOf unit.amount                           //车上人员责任险(司机)保额
                        or.driverPremium = Double.valueOf unit.charge                           //车上人员责任险(司机)保费
                        break;
                    case PASSENGER:
                        or.passengerAmount = Double.valueOf unit.amount                           //车上人员责任险(乘客)保额
                        or.passengerPremium = Double.valueOf unit.charge                           //车上人员责任险(乘客)保费
                        or.passengerCount = Integer.valueOf(ri.carInfo.seatCnt) - 1                           //乘客人数
                        break;
                    case THEFT:
                        or.theftAmount = Double.valueOf unit.amount                           //盗抢险保额
                        or.theftPremium = Double.valueOf unit.charge                           //盗抢险保费
                        break;
                    case SCRATCH:
                        or.scratchAmount = Double.valueOf unit.amount                           //划痕险保额
                        or.scratchPremium = Double.valueOf unit.charge                           //划痕险保费
                        break;
                    case SPONTANEOUS_LOSS:
                        or.spontaneousLossAmount = Double.valueOf unit.amount                           //自燃险保额
                        or.spontaneousLossPremium = Double.valueOf unit.charge                           //自燃险保费
                        break;
                    case GLASS:
                        or.glassType = null                           //玻璃险类型 TODO：待确认 GlassType.Enum.findById
                        or.glassTypeName = null                           //玻璃类型名称
                        or.glassPremium = Double.valueOf unit.charge                           //玻璃险保费
                        break;
                    case ENGINE:
                        or.engineAmount = Double.valueOf unit.amount                           //发动机险保额
                        or.enginePremium = Double.valueOf unit.charge                           //发动机特别险保费
                        break;
                    case UNABLE_FIND_THIRD_PARTY:
                        or.unableFindThirdPartyPremium = Double.valueOf unit.charge                           //机动车损失保险无法找到第三方特约险保费
                        break;
                    case DAMAGE_IOP:
                        or.damageIop = Double.valueOf unit.charge                           //车损险不计免赔
                        break;
                    case THIRD_PARTY_IOP:
                        or.thirdPartyIop = Double.valueOf unit.charge                           //三者险不计免赔
                        break;
                    case THEFT_IOP:
                        or.theftIop = Double.valueOf unit.charge                           //盗抢险不计免赔
                        break;
                    case SPONTANEOUS_LOSS_IOP:
                        or.spontaneousLossIop = Double.valueOf unit.charge                           //自燃险不计免赔
                        break;
                    case ENGINE_IOP:
                        or.engineIop = Double.valueOf unit.charge                           //发动机险不计免赔
                        break;
                    case DRIVER_IOP:
                        or.driverIop = Double.valueOf unit.charge                           //车上人员责任险(司机)不计免赔
                        break;
                    case PASSENGER_IOP:
                        or.passengerIop = Double.valueOf unit.charge                           //车上人员责任险(乘客)不计免赔
                        break;
                    case SCRATCH_IOP:
                        or.scratchIop = Double.valueOf unit.charge                           //划痕险不计免赔
                        break;
                }
            }
            or.iop = null                           //不计免赔总额
            or.compulsoryPremium = 0
        } else {
            //交强险
            or.compulsoryPolicyNo = ri.policyNum                           //保单号
            or.compulsoryPremium = Double.valueOf ri.charge                           //保费
            or.autoTax = Double.valueOf ri.taxCharge                 //车船税
            or.compulsoryEffectiveDate = ri.effectiveDate               //生效日期
            or.compulsoryEffectiveHour = 0                           //生效小时
            or.compulsoryExpireDate = ri.expiryDate                  //终保日期
            or.compulsoryExpireHour = 24                           //终保小时
            or.quoteCompulsoryPolicyNo = ri.policyNum                           //报价交强险保单号
        }

//        or.applicantName = ri.insuredPersonInfo.fullName                                          //投保人姓名 TODO=没有投保人信息
//        or.applicantIdType = IdentityTypeMappings.getLocal(ri.insuredPersonInfo.certKind)       //投保人证件类型 TODO=没有投保人信息
//        or.applicantIdNo = ri.insuredPersonInfo.certNumber                                        //投保人证件号 TODO=没有投保人信息

        or.channel = Channel.Enum.WAP_8.id                           //保单回录、支付有问题时，需要选择渠道
        or.reverseSource = OrderReverse.ReverseSource.FAN_HUA_SYNC
        or.insurancePurchaseOrderRebateViewModel = toInsurancePurchaseOrderRebateViewModel(ri, risks)

        return or
    }

    static InsurancePurchaseOrderRebateViewModel toInsurancePurchaseOrderRebateViewModel(Map ri, Map risks) {
        InsurancePurchaseOrderRebateViewModel viewModel = new InsurancePurchaseOrderRebateViewModel()
        viewModel.setCreateTime(ri.effectiveDate)
        viewModel.setUpdateTime(ri.effectiveDate)
        if ('2' == risks.insuranceType) {
            viewModel.setCommercialPremium(Double.valueOf(ri.charge))
        } else {
            viewModel.setCompulsoryPremium(Double.valueOf(ri.charge))
        }
        return viewModel
    }

    static Map getInstitution(List<Map> agencyOrgList) {
        String orgType = ''
        Map thisMap = null
        agencyOrgList.collect() { map ->
            if (map.orgType > orgType) {
                orgType = map.orgType
                thisMap = map
            }
        }
        return thisMap
    }

    def Map getRiskMap(String suiteCode) {
        Map map = new HashMap()
        List<FanhuaSuite> list = fanhuaSuiteRepository.findByRiskcode(suiteCode)
        list.collect() { FanhuaSuite suite ->
            map.get('ic') ? null : map.put('ic', suite.insuranceCompany.id)
            map.get('insuranceType') ? null : map.put('insuranceType', suite.insuranceType)
            map.put(suite.riskkindcode, suite.riskkindType)
        }
        return map

    }

    private Pageable buildPageable(int currentPage, int pageSize) {
        Sort sort = new Sort(Sort.Direction.ASC, "createTime")
        return new PageRequest(currentPage - 1, pageSize, sort)
    }

    private Long getCity(String licensePlateNo, String comCode, boolean isNewCar) {

        Area area = AutoUtils.getAreaOfAuto(licensePlateNo)
        if (isNewCar || area == null) {
            return fanhuaInstitutionRepository.findOne(Long.valueOf(comCode)).area.id
        } else {
            return area.getId()
        }

    }

    private static String getBrand(String carModelName) {
        return carModelName?.split("\\w")[0]
    }

}


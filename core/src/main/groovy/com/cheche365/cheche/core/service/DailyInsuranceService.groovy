package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Bank
import com.cheche365.cheche.core.model.BankCard
import com.cheche365.cheche.core.model.DailyInsurance
import com.cheche365.cheche.core.model.DailyInsuranceDetail
import com.cheche365.cheche.core.model.DailyInsuranceStatus
import com.cheche365.cheche.core.model.DailyRestartInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.InsurancePackage
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.repository.BankCardRepository
import com.cheche365.cheche.core.repository.BankRepository
import com.cheche365.cheche.core.repository.DailyInsuranceDetailRepository
import com.cheche365.cheche.core.repository.DailyInsuranceRepository
import com.cheche365.cheche.core.repository.DailyRestartInsuranceRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import com.cheche365.cheche.core.service.BankService
import com.cheche365.cheche.core.service.IContextService
import com.cheche365.cheche.core.service.InsurancePackageService
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.time.DateFormatUtils
import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.ws.rs.core.UriBuilder
import java.beans.PropertyDescriptor
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

import static com.cheche365.cheche.common.util.ContactUtils.getGenderByIdentity
import static com.cheche365.cheche.common.util.DoubleUtils.displayDoubleValue
import static com.cheche365.cheche.core.model.DailyInsuranceStatus.Enum.RESTART_APPLY
import static com.cheche365.cheche.core.model.DailyInsuranceStatus.getOrderDailyDisplayStatus
import static com.cheche365.cheche.core.model.DailyInsuranceStatus.mapStatus
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.ANSWERN_65000
import static com.cheche365.cheche.core.serializer.SerializerUtil.toMapKeepFields
import static java.util.Calendar.DAY_OF_MONTH
import static java.util.Calendar.instance
import static java.util.concurrent.TimeUnit.SECONDS
import static org.apache.commons.collections.CollectionUtils.isEmpty
import static org.apache.commons.collections.CollectionUtils.isNotEmpty
import static org.apache.commons.lang3.time.DateUtils.addDays
import static org.apache.commons.lang3.time.DateUtils.truncate
import static org.apache.commons.lang3.time.DateUtils.truncatedCompareTo

/**
 * Created by mahong on 2016/12/2.
 */
@Service
@Slf4j
class DailyInsuranceService {

    @Autowired
    private DailyInsuranceRepository dailyInsuranceRepository

    @Autowired
    private DailyInsuranceDetailRepository dailyInsuranceDetailRepository

    @Autowired
    private InsurancePackageService insurancePackageService

    @Autowired
    private DailyRestartInsuranceRepository restartInsuranceRepository

    @Autowired
    private WebPurchaseOrderService orderService

    @Autowired
    private InsuranceRepository insuranceRepository

    @Autowired
    private BankRepository bankRepository

    @Autowired
    private BankCardRepository bankCardRepository

    @Autowired
    private BankService bankService

    @Autowired
    private RedisTemplate redisTemplate

    private redisBindService

    @Autowired
    DailyInsuranceService(@Qualifier('redisContextService') IContextService redisContextService,
                          StringRedisTemplate stringRedisTemplate) {
        this.redisBindService = redisContextService.getContext redis: stringRedisTemplate, category: 'daily-insure'
    }

    DailyInsurance extractStopParam(Map<String, Object> param) {
        DailyInsurance dailyInsurance = new DailyInsurance()
        dailyInsurance.setOrderNo(String.valueOf(param.get("orderNo")))
        dailyInsurance.setBeginDate(DateUtils.parseDate(String.valueOf(param.get("beginDate")), "yyyy-MM-dd"))
        dailyInsurance.setEndDate(DateUtils.parseDate(String.valueOf(param.get("endDate")), "yyyy-MM-dd"))
        dailyInsurance
    }

    PurchaseOrder checkBeforeStop(DailyInsurance dailyInsurance, User user) {
        PurchaseOrder order = orderService.checkOrder(dailyInsurance.orderNo, user)

        if (!order.dailyInsuranceOperationAllowed()) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "当前订单不支持停驶操作")
        }

        Insurance insurance = insuranceRepository.findByQuoteRecordId(order.getObjId())
        if (!insurance?.finished()) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "投保单号、生效日期、失效日期均不能为空")
        }

        if (insurance.insuranceCompany != ANSWERN_65000) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "保单对应保险公司错误")
        }

        List<DailyInsurance> dailyInsurances = dailyInsuranceRepository.findAllByPurchaseOrderOrderByIdDesc(order)
        def lastDailyInsurance = dailyInsurances ? dailyInsurances.first() : null
        def (stopedBeginDate, stopedEndDate) = getStopedDates(lastDailyInsurance)
        def (effectiveBeginDate, effectiveEndDate) = getStopDates(insurance,lastDailyInsurance)
        if (stopedBeginDate || !effectiveBeginDate) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "当前状态不允许申请停驶");
        }

        if (truncatedCompareTo(dailyInsurance.getBeginDate(), effectiveBeginDate, DAY_OF_MONTH) < 0
            || truncatedCompareTo(dailyInsurance.getEndDate(), effectiveEndDate, DAY_OF_MONTH) > 0) {
            log.error("请求停驶开始日期 [ {} ], 请求停驶结束日期 ：[ {} ], 有效停驶开始日期 [ {} ], 有效停驶结束日期 ：[ {} ]",
                DateFormatUtils.format(dailyInsurance.getBeginDate(), "yyyy-MM-dd"), DateFormatUtils.format(dailyInsurance.getEndDate(), "yyyy-MM-dd"),
                DateFormatUtils.format(effectiveBeginDate, "yyyy-MM-dd"), DateFormatUtils.format(effectiveEndDate, "yyyy-MM-dd"))
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "输入停驶开始日期、结束日期错误")
        }

        order
    }

    void initStopParams(Map<String, Object> param, DailyInsurance dailyInsurance, PurchaseOrder order, Insurance insurance) {
        dailyInsurance.setPurchaseOrder(order)
        dailyInsurance.setPolicyNo(insurance.getPolicyNo())
        if (param.get("dailyInsuranceDetails") == null) {
            dailyInsurance.setInsurancePackage(insurance.getInsurancePackage())
        } else {
            dailyInsurance.setInsurancePackage(insurancePackageService.generateInsurancePackage(insurance.getInsurancePackage(), (String[]) param.get("dailyInsuranceDetails")))
        }

        dailyInsurance.setStatus(DailyInsuranceStatus.Enum.STOP_CALCULATE)
        dailyInsurance.appendDescription(dailyInsurance.status.description)
    }

    DailyInsurance checkBeforeStopConfirm(Map<String, Object> param, User user) {
        DailyInsurance dailyInsuranceDB
        if (param.get("id") == null || (dailyInsuranceDB = dailyInsuranceRepository.findOne(Long.valueOf(param.get("id")))) == null) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "停驶试算标识输入错误")
        }
        if (!dailyInsuranceDB.purchaseOrder.dailyInsuranceOperationAllowed()) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "当前订单不支持停驶操作")
        }
        if (DailyInsuranceStatus.Enum.STOP_CALCULATE != dailyInsuranceDB.status) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "停驶失败，当前状态为：" + dailyInsuranceDB.getStatus().getStatus())
        }
        if (param.get("bankCard")) {
            BankCard bankCard = bankCardRepository.findFirstByIdAndUserAndDisable(Long.valueOf(String.valueOf(param.get("bankCard"))), user, false)
            if (!bankCard) {
                throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "银行卡不存在")
            }
            dailyInsuranceDB.setBankCard(bankCard)
            return dailyInsuranceDB
        }
        if (!param.get("name") || !param.get("bank") || !param.get("bankNo")) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "银行、银行卡号、姓名均不能为空")
        }
        Bank bank = bankRepository.findOne(Long.valueOf(String.valueOf(param.get("bank"))))
        BankCard bankCard = new BankCard(user: user, bankNo: String.valueOf(param.get("bankNo")), name: String.valueOf(param.get("name")), bank: bank)
        dailyInsuranceDB.bankCard = bankService.findOrCreate(bankCard)
        dailyInsuranceDB
    }

    void initStopConfirmParams(Map<String, Object> dailyInsuranceParam, DailyInsurance dailyInsuranceDB) {
        if (dailyInsuranceParam.get("dailyInsuranceDetails") == null) {
            dailyInsuranceDB.setInsurancePackage(dailyInsuranceDB.insurancePackage)
        } else {
            dailyInsuranceDB.setInsurancePackage(insurancePackageService.generateInsurancePackage(dailyInsuranceDB.getInsurancePackage(), (String[]) dailyInsuranceParam.get("dailyInsuranceDetails")))
        }
        List<DailyInsuranceDetail> dailyInsuranceDetails = dailyInsuranceDB.getDailyInsuranceDetails().findAll { dailyInsuranceDetail ->
            PropertyDescriptor descriptor = InsurancePackage.PROPERTIES.find {
                it.getName() == dailyInsuranceDetail.getCode()
            }
            descriptor && descriptor.getReadMethod().invoke(dailyInsuranceDB.getInsurancePackage())
        }
        dailyInsuranceDB.setDailyInsuranceDetails(dailyInsuranceDetails)

        Double totalRefundAmount = 0.0
        dailyInsuranceDB.getDailyInsuranceDetails().each { totalRefundAmount += it.getRefundPremium() }
        dailyInsuranceDB.setTotalRefundAmount(displayDoubleValue(totalRefundAmount))
        dailyInsuranceDB.setStatus(DailyInsuranceStatus.Enum.STOP_APPLY)
        dailyInsuranceDB.appendDescription(dailyInsuranceDB.status.description)
    }

    DailyInsurance checkBeforeRestart(DailyInsurance dailyInsurance) {
        DailyInsurance dailyInsuranceDB
        if (dailyInsurance.getId() == null || (dailyInsuranceDB = dailyInsuranceRepository.findOne(dailyInsurance.getId())) == null) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "停驶试算标识输入错误")
        }
        if (!dailyInsuranceDB.purchaseOrder.dailyInsuranceOperationAllowed()) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "当前订单不支持复驶操作")
        }
        def (stopedBeginDate, stopedEndDate) = getStopedDates(dailyInsurance)
        if (truncatedCompareTo(dailyInsurance.getRestartDate(), instance.time, DAY_OF_MONTH) == 0 ||
            stopedBeginDate && truncatedCompareTo(dailyInsurance.getRestartDate(), stopedBeginDate, DAY_OF_MONTH) < 0 ||
            stopedEndDate && truncatedCompareTo(dailyInsurance.getRestartDate(), stopedEndDate, DAY_OF_MONTH) > 0) {
            log.error("复驶日期[ {} ]有误, 停驶开始日期 [ {} ], 停驶结束日期 ：[ {} ]",
                DateFormatUtils.format(dailyInsurance.getRestartDate(), "yyyy-MM-dd"),
                DateFormatUtils.format(dailyInsuranceDB.getBeginDate(), "yyyy-MM-dd"),
                DateFormatUtils.format(dailyInsuranceDB.getEndDate(), "yyyy-MM-dd"))
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "复驶日期输入错误")
        }
        if (![DailyInsuranceStatus.Enum.STOP_APPLY, DailyInsuranceStatus.Enum.STOPPED].contains(dailyInsuranceDB.getStatus())) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "复驶失败，当前状态为：" + dailyInsuranceDB.getStatus().getStatus())
        }
        if (dailyInsuranceDB.getRestartDate() != null) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "当次停驶已经复驶成功")
        }
        dailyInsuranceDB
    }

    DailyRestartInsurance initRestartParams(DailyInsurance dailyInsuranceDB, DailyInsurance dailyInsurance) {
        DailyRestartInsurance restartInsurance = restartInsuranceRepository.findFirstByDailyInsuranceAndStatusOrderByIdDesc(dailyInsuranceDB, DailyInsuranceStatus.Enum.RESTART_APPLY)
        if (restartInsurance == null) {
            restartInsurance = new DailyRestartInsurance()
            restartInsurance.setDailyInsurance(dailyInsuranceDB)
        }
        restartInsurance.setBeginDate(dailyInsurance.getRestartDate())
        restartInsurance.setEndDate(dailyInsuranceDB.getActualEndDate())
        restartInsurance.setRestartInsuranceDetails(new ArrayList<>())
        restartInsurance.setStatus(DailyInsuranceStatus.Enum.RESTART_APPLY)
        restartInsurance.appendDescription(restartInsurance.status.description)
        restartInsurance
    }

    @Transactional
    @CacheEvict(value = 'dailyInsuranceData', allEntries = true, beforeInvocation = true, condition = '#dailyInsurance.status.id==2')
    DailyInsurance saveDailyInsurance(DailyInsurance dailyInsurance) {
        dailyInsuranceRepository.save(dailyInsurance)
    }

    @Transactional
    @CacheEvict(value = 'dailyInsuranceData', allEntries = true, beforeInvocation = true, condition = '#dailyInsurance.status.id==2')
    DailyInsurance saveDailyInsuranceCascade(DailyInsurance dailyInsurance, List<DailyInsuranceDetail> details) {

        saveDailyInsurance(dailyInsurance)

        dailyInsuranceDetailRepository.deleteByDailyInsurance(dailyInsurance)  //一次停驶只保存最新险种记录
        dailyInsurance.dailyInsuranceDetails = null
        details.each { it.dailyInsurance = dailyInsurance }
        dailyInsurance.dailyInsuranceDetails = dailyInsuranceDetailRepository.save(details)
        dailyInsurance
    }

    Map<String, Object> decorateResult(PurchaseOrder order, Object homeMarketing) {
        if (!order.dailyInsuranceOperationAllowed()) {
            return null
        }
        def sdf = new SimpleDateFormat('yyyy-MM-dd')
        def newDailyBeginDate = sdf.parse '2017-05-25'   // TODO 安心不支持旧的停驶记录申请提前复驶
        Insurance insurance = insuranceRepository.findByQuoteRecordId(order.getObjId())
        List<DailyInsurance> dailyInsurances = dailyInsuranceRepository.findAllByPurchaseOrderOrderByIdDesc(order)
        def lastDailyInsurance = dailyInsurances ? dailyInsurances.first() : null
        def dailyRestartInsurance = restartInsuranceRepository.findFirstByDailyInsuranceOrderByIdDesc(lastDailyInsurance)
        def (stopedBeginDate, stopedEndDate) = getStopedDates(lastDailyInsurance)
        def (stopBeginDate, stopEndDate) = getStopDates(insurance,lastDailyInsurance)
        def result = [
            orderNo           : order.orderNo,
            effectiveBeginDate: stopBeginDate,
            effectiveEndDate  : stopEndDate,
            licensePlateNo    : order.auto.licensePlateNo,
            owner             : order.auto.owner,
            applicantName     : insurance.applicantName,
            status            : getOrderDailyDisplayStatus(insurance, dailyInsurances),
            premium           : insurance.premium,
            dailyInsurances   : dailyInsurances ?: null,
            allowStop         : stopedBeginDate ? false : stopBeginDate ? true : false,
            allowRestart      : stopedBeginDate && !lastDailyInsurance.restartDate &&
                (!dailyRestartInsurance || RESTART_APPLY == dailyRestartInsurance.status) &&
                truncatedCompareTo(instance.time, stopedEndDate, DAY_OF_MONTH) != 0 &&
                truncatedCompareTo(lastDailyInsurance.createTime, newDailyBeginDate, DAY_OF_MONTH) > 0
        ]

        def oldHomeMarketing = homeMarketing?.find { it.old }
//        def newHomeMarketing = homeMarketing?.find { !it.old }
//        def hasBills = isEmpty(orderService.findRenewalOrderByLicensePlateNo(order.auto.licensePlateNo)) && checkOrderHasBills(order)
        result << [
//            url    : newHomeMarketing?.flag ? newHomeMarketing?.url : hasBills ? UriBuilder.fromPath(WebConstants.getDomainURL()).path('marketing').path('suspendBill').path('index.html').queryParam('orderNo', order.orderNo).build().toString() : '',
//              iconUrl: !newHomeMarketing?.flag && !hasBills ? oldHomeMarketing?.iconUrl : newHomeMarketing?.iconUrl
              url    : null,
              iconUrl: oldHomeMarketing?.iconUrl
        ]

        Long totalDays = 0L
        Double totalDiscountAmount = 0.0
        dailyInsurances.each {
            it.toNetData(restartInsuranceRepository.findAllByDailyInsurance(it))
            it.status = mapStatus it.status
            totalDays += it.days
            totalDiscountAmount += it.discountAmount
        }
        result["totalDays"] = totalDays
        result["totalDiscountAmount"] = displayDoubleValue totalDiscountAmount

        return result
    }

    /**
     * 获取当前有效的停驶日期区间
     * @param dailyInsurance        最后一次成功停驶
     * @return  Tuple2(stopedBeginDate, stopedEndDate)  有效的停驶区间
     */
    def getStopedDates(DailyInsurance dailyInsurance) {
        def (stopedBeginDate, stopedEndDate) = []
        DailyRestartInsurance dailyRestartInsurance
        if (dailyInsurance){
            def dailyRestartInsurances = restartInsuranceRepository.findAllByDailyInsurance(dailyInsurance)
            dailyRestartInsurance = dailyRestartInsurances ? dailyRestartInsurances.first() : null
            if (dailyRestartInsurance) {
                if (truncatedCompareTo(dailyRestartInsurance.beginDate, dailyInsurance.beginDate, DAY_OF_MONTH) > 0) {
                    stopedBeginDate = dailyInsurance.beginDate
                    stopedEndDate = addDays dailyRestartInsurance.beginDate, -1
                }
            } else {
                stopedBeginDate = dailyInsurance.beginDate
                stopedEndDate = dailyInsurance.endDate
            }
            if (stopedEndDate && truncatedCompareTo(instance.time, stopedEndDate, DAY_OF_MONTH) > 0) {
                stopedBeginDate = null
                stopedEndDate = null
            } else if (stopedBeginDate && truncatedCompareTo(instance.time, stopedBeginDate, DAY_OF_MONTH) > 0) {
                stopedBeginDate = instance.time
            }
        }
        new Tuple2(stopedBeginDate, stopedEndDate)
    }

    /**
     * 获取当前可停驶区间
     * 开始时间规则：max( 保单生效日期, 当前日期+1 )
     * 结束时间规则：min( 保单结束日期-1, 当前日期+90 )
     *
     * @param insurance 保单
     * @param dailyInsuranceDB 如果没有停驶记录，传null TODO 安心目前不支持停驶上次停驶之前的时间区间
     * @return  Tuple2(stopBeginDate, stopEndDate)  可停驶区间
     */
    def getStopDates(Insurance insurance, DailyInsurance dailyInsuranceDB) {
        def (stopBeginDate, stopEndDate) = []
        def effectiveDate = dailyInsuranceDB == null ? insurance.getEffectiveDate() : dailyInsuranceDB.beginDate
        def expireDate = addDays insurance.expireDate, -1
        def currentDateNextDay = addDays truncate(instance.time, DAY_OF_MONTH), 1
        stopBeginDate = truncatedCompareTo(effectiveDate, currentDateNextDay, DAY_OF_MONTH) > 0 ? effectiveDate : currentDateNextDay
        stopBeginDate = truncatedCompareTo(stopBeginDate, expireDate, DAY_OF_MONTH) > 0 ? null : stopBeginDate

        def maxEffectiveEndDate = addDays truncate(instance.time, DAY_OF_MONTH), 90

        stopEndDate = truncatedCompareTo(maxEffectiveEndDate, expireDate, DAY_OF_MONTH) > 0 ? expireDate : maxEffectiveEndDate
        stopEndDate = stopBeginDate ? stopEndDate : null
        new Tuple2(stopBeginDate, stopEndDate)
    }

    /**
     * 生成停驶账单
     * maxDaysProportion：最大停驶天数超越用户比例
     * totalAmountProportion：返现金额超越用户比例
     * @param order
     * @return
     */
    Map<String, Object> createBills(PurchaseOrder order, Map dailyInsuranceData) {
        def result = dailyInsuranceData[order.area.id.toString().with {
            it.startsWith('11') ? '110000' : it.startsWith('4403') ? '440300' : it.startsWith('44') ? '440000' : it
        }]?.with { diDataByArea ->
            diDataByArea.list[order.orderNo]?.with { di ->
                di.area = di.area == '110000' ? '北京' : di.area == '440300' ? '深圳' : di.area == '440000' ? '广东' : '本'
                di.paidAmount = order.paidAmount
                di.createTime = order.createTime
                di.sex = order.auto.identityType.id == 1 ? getGenderByIdentity(order.auto.identity) : 0
                di.nickName = '尊敬的' + (order.auto.identityType.id == 1 ? (order.auto.owner[0] + (di.sex == 1 ? '先生' : '女士')) : '用户')
                di.identityType = order.auto.identityType
                def maxDaysProportion = Math.round(diDataByArea.maxDaysSort.with {
                    (it.lastIndexOf(di.maxDays) + 1) / it.size()
                } * 100)
                def totalAmountProportion = Math.round(diDataByArea.totalAmountSort.with {
                    (it.lastIndexOf(di.totalAmount) + 1) / it.size()
                } * 100)
                di << [
                    maxDaysProportion    : Math.min(Math.max(maxDaysProportion, 18), 88) + '%',
                    totalAmountProportion: Math.min(Math.max(totalAmountProportion, 18), 88) + '%'
                ]
            } ?: [:]
        }

        result << dailyInsuranceRepository.findAllByPurchaseOrderOrderByIdDesc(order).collect {
            it.toNetData restartInsuranceRepository.findAllByDailyInsurance(it)
            it.discountAmount = displayDoubleValue it.discountAmount
            toMapKeepFields it, 'days,discountAmount,createTime', true
        }.with { di ->
            di ? [firstDailyInsurance: di.min { it.createTime }, maxDailyInsurance: di.max { it.days }] : [:]
        }
    }

    /**
     * 获取截止当前时间一年内的停驶返钱数据，并按地区分组
     * maxDaysSort：最大停驶天数排序
     * totalAmountSort：停驶总金额排序
     * @return
     */
    @Cacheable(value = 'dailyInsuranceData', keyGenerator = "cacheKeyGenerator")
    Map getOneYearDailyInsuranceData() {
        def dailyInsuranceData = dailyInsuranceRepository.countOneYearDailyInsurance().collect {
            def (area, orderNo, totalAmount, countDays, maxDays) = it
            [area: area as String, orderNo: orderNo, totalAmount: displayDoubleValue(totalAmount), countDays: countDays as Long, maxDays: maxDays as Long]
        }
        dailyInsuranceData.groupBy { it.area }.collectEntries { key, value ->
            [(key): [list           : value.collectEntries { [(it.orderNo): it] },
                     maxDaysSort    : dailyInsuranceData.maxDays.sort(),
                     totalAmountSort: value.totalAmount.sort()]
            ]
        }
    }

    Boolean checkOrderHasBills(order) {
        def insurance = insuranceRepository.findByQuoteRecordId(order.getObjId())
        ANSWERN_65000 == insurance.insuranceCompany && insurance.expireDate >= new Date().clearTime() && insurance.expireDate < addDays(new Date().clearTime(), 90) && isNotEmpty(dailyInsuranceRepository.findAllByPurchaseOrderOrderByIdDesc(order))
    }

    boolean checkOrderBinding(PurchaseOrder order) {
        redisBindService.exists getBindingKey(order)
    }

    boolean bindPurchaseOrder(PurchaseOrder order, long ttl, TimeUnit timeUnit = SECONDS) {
        redisBindService.bindIfAbsentWithTTL getBindingKey(order), true, ttl, timeUnit
    }

    void unbindPurchaseOrder(PurchaseOrder order) {
        redisBindService.unbind getBindingKey(order)
    }

    private static String getBindingKey(PurchaseOrder order) {
        order.orderNo
    }
}

package com.cheche365.cheche.core.service

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.constants.DisplayConstants
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.core.repository.*
import com.cheche365.cheche.core.repository.agent.ChannelAgentOrderRebateRepository
import com.cheche365.cheche.core.repository.agent.ChannelAgentRepository
import com.cheche365.cheche.core.service.AutoService
import com.cheche365.cheche.core.service.PurchaseOrderService
import com.cheche365.cheche.core.util.CalendarUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.text.DecimalFormat
import java.text.SimpleDateFormat

import static com.cheche365.cheche.core.model.PaymentStatus.Enum.NOTPAYMENT_1
import static com.cheche365.cheche.core.model.PaymentType.Enum.DAILY_RESTART_PAY_7

/**
 * Created by zhengwei on 3/1/16.
 * Web项目用的订单相关服务，和出单中心分开
 */

@Service
@Transactional
class WebPurchaseOrderService {

    private Logger logger = LoggerFactory.getLogger(PurchaseOrderService.class);

    public static final  SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd")

    @Autowired
    private WebPurchaseOrderRepository webOrderRepository;

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InsuranceRepository insuranceRepository

    @Autowired
    private CompulsoryInsuranceRepository ciRepository


    @Autowired
    private QuoteFlowConfigRepository quoteFlowConfigRepository

    @Autowired
    private AutoService autoService

    @Autowired
    private ChannelAgentRepository channelAgentRepository

    @Autowired
    private ChannelAgentOrderRebateRepository channelAgentOrderRebateRepository

    @Transactional
    Payment saveDailyRestartPayment(PurchaseOrder purchaseOrder, DailyRestartInsurance restartInsurance) {
        Payment payment = (restartInsurance.payment) ? restartInsurance.payment : Payment.getPaymentTemplate(purchaseOrder)
        List<Payment> ptList = paymentRepository.findByPurchaseOrderAndPaymentTypeAndStatus(purchaseOrder.id, PaymentType.Enum.DAILY_RESTART_PAY_7, NOTPAYMENT_1);
        ptList.each {
            if(it.getId() != payment.getId()){
                it.setStatus(PaymentStatus.Enum.CANCEL_4);
                paymentRepository.save(it);
            }
        }
        payment.setAmount(restartInsurance.getPaidAmount());
        payment.setPaymentType(DAILY_RESTART_PAY_7);
        return paymentRepository.save(payment);
    }


    List<PurchaseOrder> findFinishedOrdersByApplicantAndCompany(User user, InsuranceCompany insuranceCompany) {
        return webOrderRepository.findFinishedOrdersByApplicantAndCompany(user, insuranceCompany);
    }

    List<PurchaseOrder> findRenewalOrderByLicensePlateNo(String licensePlateNo) {
        webOrderRepository.findRenewalOrderByLicensePlateNo licensePlateNo
    }

    PurchaseOrder findFirstByOrderNo(String orderNo, User user) {
        List<OrderType> orderTypes = [OrderType.Enum.INSURANCE, OrderType.Enum.ACTIVITY];
        return webOrderRepository.findFirstByOrderNoAndOrderTypesAndApplicant(orderNo, orderTypes, user);
    }


    PurchaseOrder checkOrder(String orderNo, User user) {
        PurchaseOrder order = findFirstByOrderNo(orderNo, user)
        if (!order) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "输入的订单号错误")
        }
        order
    }

    Page<PurchaseOrder> findByUserAndChannelsAndStatus(User applicant, Channel channel, List<OrderStatus> status,
                                                       final Pageable pageable) {
        List<Channel> channels = Channel.findChannels(channel)
        webOrderRepository.findByUserAndChannelsAndStatus(applicant, channels, status, pageable)
    }

    Page<SimpleOrderResult> findSimplifiedOrders(User applicant, Channel channel, List<OrderStatus> status,
                                                 final Pageable pageable) {
        List<Channel> channels = Channel.findChannels(channel)
        webOrderRepository.findSimplifiedOrders(applicant, channels, status, pageable)
    }

    void updateChannelAndClientType(PurchaseOrder purchaseOrder, Payment payment, Channel channel, PaymentChannel paymentChannel) {
        logger.debug("update purchase order's PaymentChannel channel for {}, the original one is {}, new one is {}", purchaseOrder.getOrderNo(), purchaseOrder.getChannel().getId(), paymentChannel.getId());

        if (payment) {
            payment.setClientType(channel);
            payment.setChannel(paymentChannel);
            payment.setComments(paymentChannel.getDescription());
            paymentRepository.save(payment);
        }

        purchaseOrder.setChannel(paymentChannel);
        this.webOrderRepository.save(purchaseOrder);
    }


    PurchaseOrder getFirstPurchaseOrderByNo(String orderNo){
        return this.webOrderRepository.findFirstByOrderNo(orderNo);
    }

    Map getOrderSummaryInfo(User user, Channel channel) {
        ChannelAgent channelAgent = channelAgentRepository.findByUserAndChannel(user,Channel.findAgentChannel(channel))
        List<Channel> channels = Channel.allChannels().findAll { channel.id == it.parent.id }.collect()
        if(channel.isLevelAgent() && channelAgent){

           List<Object[]> year  = channelAgentOrderRebateRepository.findAgentOrdersYear(channelAgent.id)
           List<Object[]> month = channelAgentOrderRebateRepository.findAgentOrdersMonth(channelAgent.id)
           List<Object[]> day   = channelAgentOrderRebateRepository.findAgentOrdersDay(channelAgent.id)

            return [
                totalAmount : new DecimalFormat("#.##").with { it.format(year.get(0)[0] ?: 0) },
                totalCount  : year.get(0)[1],
                monthAmount : new DecimalFormat("#.##").with { it.format(month.get(0)[0] ?: 0) },
                monthCount  : month.get(0)[1],
                dayAmount   : new DecimalFormat("#.##").with { it.format(day.get(0)[0] ?: 0) },
                dayCount    : day.get(0)[1],
                pendingCount: webOrderRepository.sumByChannelAndApplicantAndStatus(channels, user, OrderStatus.Enum.PENDING_PAYMENT_1)[0][0]
            ]
        }else {
            def totalResult = webOrderRepository.sumPaidOrdersByChannelAndApplicant(channels, user)
            def firstDayOfMonth = GregorianCalendar.instance.with {
                set(it[Calendar.YEAR], it[Calendar.MONTH], 1, 0, 0, 0)
                it.time
            }
            def lastDayOfMonth = GregorianCalendar.instance.with {
                set(it[Calendar.YEAR], it[Calendar.MONTH], it.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
                it.time
            }
            def monthResult = webOrderRepository.sumPaidOrdersByCreateTime(channels, user, firstDayOfMonth, lastDayOfMonth)

            def beginOfDay = GregorianCalendar.instance.with {
                set(it[Calendar.YEAR], it[Calendar.MONTH], it.getAt(Calendar.DATE), 0, 0, 0)
                it.time
            }
            def endOfDay = GregorianCalendar.instance.with {
                set(it[Calendar.YEAR], it[Calendar.MONTH], it.getAt(Calendar.DATE), 23, 59, 59)
                it.time
            }
            def dayResult = webOrderRepository.sumPaidOrdersByCreateTime(channels, user, beginOfDay, endOfDay)

            return [
                totalAmount : new DecimalFormat("#.##").with { it.format(totalResult[0][1] ? totalResult[0][1] : 0) },
                totalCount  : totalResult[0][0],
                monthAmount : new DecimalFormat("#.##").with { it.format(monthResult[0][1] ? monthResult[0][1] : 0) },
                monthCount  : monthResult[0][0],
                dayAmount   : new DecimalFormat("#.##").with { it.format(dayResult[0][1] ? dayResult[0][1] : 0) },
                dayCount    : dayResult[0][0],
                pendingCount: webOrderRepository.sumByChannelAndApplicantAndStatus(channels, user, OrderStatus.Enum.PENDING_PAYMENT_1)[0][0]
            ]
        }


    }

    List<Map> renewalOrder(User user,Channel channel){
        List<Map> renewalMap = []
        if(channel.isAgentChannel()){
            return renewalMap
        }
        Date beforeRenewalDate = CalendarUtil.appointDate(90)
        Date afterRenewalDate = CalendarUtil.appointDate(-30)
        List<PurchaseOrder> purchaseOrders =webOrderRepository.findAllByApplicantAndStatusAndChannel(user,OrderStatus.Enum.FINISHED_5,Channel.findChannels(channel),beforeRenewalDate,afterRenewalDate)
        purchaseOrders?.findAll{it->
            quoteFlowConfigRepository.findByAreaAndInsuranceCompanyAndChannel(it.area,quoteRecordRepository.findOne(it.objId).insuranceCompany , channel.parent) != null
        }.each {it->
            def (insurance, compulsoryInsurance) = renewalbeFilter(
                insuranceRepository.findByQuoteRecordId(it.objId),
                ciRepository.findByQuoteRecordId(it.objId)
            )
            if(renewalInsurance(insurance, it)){
                renewalMap.add(assembleRenewalMap(it.orderNo,insurance.expireDate,it.auto?.licensePlateNo))
            }else {
                if(renewalCompulsoryInsurance(compulsoryInsurance, it)){
                    renewalMap.add(assembleRenewalMap(it.orderNo,compulsoryInsurance.expireDate,it.auto?.licensePlateNo))
                }
            }
        }
        renewalMap?.sort {a,b -> SDF.parse(a.expireTime) <=> SDF.parse(b.expireTime)}
        return renewalMap
    }

    Map assembleRenewalMap(orderNo,expireTime,licensePlateNo){
        return [
            "orderNo":orderNo,
            "expireTime":DateUtils.getDateString(expireTime,DateUtils.DATE_SHORTDATE_PATTERN),
            "licensePlateNo":licensePlateNo,
            "type":DisplayConstants.getALTER_TYPE()
        ]
    }

    def renewalbeFilter(insuranceDB, ciDB) {
        Date beforeRenewalDate = CalendarUtil.appointDate(90)
        Date afterRenewalDate = CalendarUtil.appointDate(-30)
        Date now = new Date()

        def validDate = { Date expireDate ->
            (expireDate?.compareTo(beforeRenewalDate) <= 0 && expireDate?.compareTo(now) > 0) ||
                (expireDate?.compareTo(afterRenewalDate) >= 0 && expireDate?.compareTo(now) < 0)
        }
        Insurance insurance = validDate(insuranceDB?.expireDate) ? insuranceDB : null
        CompulsoryInsurance ci = validDate(ciDB?.expireDate) ? ciDB : null

        [insurance, ci]
    }

    boolean renewalbe(purchaseOrderDB, insuranceDB, ciDB) {
        def (insurance, ci) = renewalbeFilter(insuranceDB, ciDB)
        renewalInsurance(insurance, purchaseOrderDB) || renewalCompulsoryInsurance(ci, purchaseOrderDB)
    }

    //TODO MAHONG
    boolean renewalCompulsoryInsurance(CompulsoryInsurance compulsoryInsurance, PurchaseOrder it) {
        if(!compulsoryInsurance){
            return false
        }
        List<Auto> autoList = autoService.findRenewalAuto(it.applicant.id,it.auto.licensePlateNo)
        if(!autoList){
            return false
        }
        return webOrderRepository.findRenewaledCiOrder(it.applicant,autoList, compulsoryInsurance.expireDate).size() == 0
    }

    boolean renewalInsurance(Insurance insurance, PurchaseOrder it) {
        if(!insurance){
            return false
        }
        List<Auto> autoList = autoService.findRenewalAuto(it.applicant.id,it.auto.licensePlateNo)
        if(!autoList){
            return false
        }
        return webOrderRepository.findRenewaledInsuranceOrder(it.applicant,autoList, insurance.expireDate).size() == 0
    }
}




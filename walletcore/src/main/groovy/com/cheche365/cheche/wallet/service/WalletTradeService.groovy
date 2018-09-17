package com.cheche365.cheche.wallet.service

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.core.model.agent.ChannelAgentPurchaseOrderRebate
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.repository.agent.ChannelAgentOrderRebateRepository
import com.cheche365.cheche.core.repository.agent.ChannelAgentRepository
import com.cheche365.cheche.core.service.BankService
import com.cheche365.cheche.core.service.IWalletTradeService
import com.cheche365.cheche.core.service.agent.AgentRewardCacheHandler
import com.cheche365.cheche.core.service.agent.ChannelRebateService
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.soopay.payment.withdraw.SoopayWithdrawHandler
import com.cheche365.cheche.wallet.model.*
import com.cheche365.cheche.wallet.repository.WalletRemitRepository
import com.cheche365.cheche.wallet.repository.WalletRepository
import com.cheche365.cheche.wallet.repository.WalletTradeRepository
import com.cheche365.cheche.wallet.utils.RandomUitl
import com.cheche365.cheche.web.service.InsurancePurchaseOrderRebateService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

import static com.cheche365.cheche.core.service.agent.AgentRewardCacheHandler.*

import static com.cheche365.cheche.wallet.model.WalletTradeSource.Enum.*
import static com.cheche365.cheche.web.util.ClientTypeUtil.getChannel
import static com.cheche365.cheche.common.util.DateUtils.DATE_LONGTIME24_PATTERN

/**
 * Created by mjg on 2017/6/6.
 */
@Service("walletTradeService")
@Transactional
public class WalletTradeService implements IWalletTradeService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private WalletTradeRepository walletTradeRepository;
    @Autowired
    private WalletRemitRepository walletRemitRepository;
    @Autowired
    private BankService bankService;
    @Autowired
    private SoopayWithdrawHandler withdrawHandler;
    @Autowired
    private WalletService walletService;
    @Autowired
    private ChannelRebateService channelRebateService
    @Autowired
    private ChannelAgentOrderRebateRepository channelAgentOrderRebateRepository
    @Autowired
    private InsurancePurchaseOrderRebateService insurancePurchaseOrderRebateService
    @Autowired
    private ChannelAgentRepository channelAgentRepository
    @Autowired
    private AgentRewardCacheHandler agentRewardCacheHandler
    @Autowired
    PurchaseOrderRepository purchaseOrderRepository

    @Override
    def createAgentWalletTrade(QuoteRecord quoteRecord, PurchaseOrder order) {

        logger.info("qr id:{},po.id:{}", quoteRecord.id, order.id)
        if (order.sourceChannel.isLevelAgent()) {
            createLevelAgentWalletTrade(order, quoteRecord)
        } else {
            Wallet wallet = walletService.queryOrCreateWallet(order.getApplicant(), order.sourceChannel)
            Double rebate = channelRebateService.getChannelRebate(quoteRecord, order)?.discountAmount(quoteRecord)
            WalletTrade trade = new WalletTrade(
                tradeFlag: 1, amount: rebate, userId: order.applicant.id, tradeNo: RandomUitl.buildOrderNo("T"),
                tradeType: REBATE_TOA_4, tradeSourceId: order.id, status: WalletTradeStatus.Enum.FINISHED_2, channel: order.sourceChannel.id
            )
            createWalletTrade(trade, wallet)
        }

        /* 需求 #10728 车保易关闭扣税功能 后期可能放开
        Double totalRebate = trades.findAll { it.tradeType == REBATE_TOA_4 }.amount.sum()?.doubleValue()
        Double payableTaxFee = channelRebateService.calculateTaxFee((totalRebate ? totalRebate : 0) + rebate)
        Double paidTaxFee = trades.findAll { it.tradeType == TAX_FEE_5 }.amount.sum()?.doubleValue()
        Double taxFee = DoubleUtils.displayDoubleValue(payableTaxFee - (paidTaxFee ? paidTaxFee : 0))
        if (taxFee <= 0) {
            return trade
        }
        WalletTrade taxFeeTrade = new WalletTrade(
            tradeFlag: 0, amount: taxFee, userId: order.applicant.id, tradeNo: RandomUitl.buildOrderNo("T"),
            tradeType: TAX_FEE_5, tradeSourceId: trade.id, status: WalletTradeStatus.Enum.FINISHED_2, channel: order.sourceChannel.id
        )
        logger.debug("订单完成,代理人返点进钱包,orderNO:{},walletId:{},currentRebate:{},payableTaxFee:{},taxFee:{}", order.orderNo, wallet.id, rebate, payableTaxFee, taxFee)
        createWalletTrade(taxFeeTrade, wallet)
        */

    }

    private void createLevelAgentWalletTrade(PurchaseOrder order, QuoteRecord quoteRecord) {
        List<ChannelAgentPurchaseOrderRebate> purchaseOrderRebates = channelAgentOrderRebateRepository.findAllByPurchaseOrder(order)
        logger.info("order channel:{},user:{},capor size:{}", order.sourceChannel.id, order.applicant.id, purchaseOrderRebates.size())

        purchaseOrderRebates?.each {
            logger.info("capor id:{}", it.id)
            Wallet wallet = walletService.queryOrCreateWallet(it.channelAgent.user, order.sourceChannel)
            Double rebate = insurancePurchaseOrderRebateService.settleRebate(it, quoteRecord)
            if (rebate > 0) {
                if (walletTradeRepository.findByUserIdAndTradeSourceIdAndTradeType(it.channelAgent.user.id, order.id, ORDER_REBATE_6)) {
                    logger.info("三级代理返点walletTrade已经存在,orderNo:{}", order.orderNo)
                    throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "该三级代理订单已经存入钱包")
                }
                WalletTrade trade = new WalletTrade(
                    tradeFlag: 1, amount: rebate, userId: it.channelAgent.user.id, tradeNo: RandomUitl.buildOrderNo("T"),
                    tradeType: ORDER_REBATE_6, tradeSourceId: order.id, status: WalletTradeStatus.Enum.FINISHED_2, channel: order.sourceChannel.id,
                    description: setPrefix(order, it.channelAgent)
                )
                createWalletTrade(trade, wallet)
            }
        }

        firstOrderReward(order)
    }

    private void firstOrderReward(PurchaseOrder order) {

        if (isFirstOrder(order)) {
            ChannelAgent channelAgent = channelAgentRepository.findByUserAndChannel(order.applicant, Channel.findAgentChannel(order.sourceChannel))
            log.info("新注册代理人首次出单奖励channelAgent:{}，orderNo:{}", channelAgent.id, order.orderNo)
            Wallet wallet = walletService.queryOrCreateWallet(order.applicant, order.sourceChannel)
            BigDecimal firstOrderRebate = new BigDecimal(agentRewardCacheHandler.getRebateFromCache(_FIRST_ORDER_REBATE))
            WalletTrade trade = new WalletTrade(
                tradeFlag: 1, amount: firstOrderRebate, userId: order.applicant.id, tradeNo: RandomUitl.buildOrderNo("T"),
                tradeType: CA_FIRST_ORDER_REBATE_9, tradeSourceId: order.id, status: WalletTradeStatus.Enum.FINISHED_2,
                channel: order.sourceChannel.id
            )
            createWalletTrade(trade, wallet)

            if (channelAgent.parent && agentRewardCacheHandler.inviteFirstOrderLimit(channelAgent.parent)) {
                ChannelAgent parentCa = channelAgent.parent

                log.info("下级代理人首次出单，上级代理人奖励 parent channelAgent：{}", parentCa.id)
                Wallet parentWallet = walletService.queryOrCreateWallet(parentCa.user, parentCa.channel)
                BigDecimal nextOrderRebate = new BigDecimal(agentRewardCacheHandler.getRebateFromCache(_INVITE_FIRST_ORDER_REBATE))
                WalletTrade parentTrade = new WalletTrade(
                    tradeFlag: 1, amount: nextOrderRebate, userId: parentCa.user.id, tradeNo: RandomUitl.buildOrderNo("T"),
                    tradeType: NEXT__LEVEL_FIRST_ORDER_REBATE_10, tradeSourceId: order.id, status: WalletTradeStatus.Enum.FINISHED_2,
                    channel: order.sourceChannel.id
                )
                createWalletTrade(parentTrade, parentWallet)
                agentRewardCacheHandler.countClosure.call(_AGENT_COUNT_HASH_KEY, _GET_HASH_KEY.call(_INVITE_CA_REGISTER_PRE, channelAgent.parent.id))
            }
        }
    }

    private boolean isFirstOrder(PurchaseOrder order) {
        agentRewardCacheHandler.activityValid() &&
            Channel.agentLevelChannels().contains(order.sourceChannel) &&
            !walletTradeRepository.findByUserIdAndTradeType(order.applicant.id, CA_FIRST_ORDER_REBATE_9) &&
            !purchaseOrderRepository.findBeforeActivityOrder(order.applicant, Channel.agentLevelChannels(), agentRewardCacheHandler.getActivityTime(_BEGIN_TIME))
    }

    WalletTrade createWalletTrade(WalletTrade trade, Wallet wallet) {

        if (trade == null || wallet == null) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "交易参数不能为空！");
        }
        if (trade.getBalance() == null && wallet != null) {
            if (trade.getTradeFlag() == 0) {
                trade.setBalance(wallet.getBalance().subtract(trade.getAmount()));
            } else if (trade.getTradeFlag() == 1) {
                trade.setBalance(wallet.getBalance().add(trade.getAmount()));
            }
        }
        trade.setWalletId(wallet.getId());
        trade.setCreateTime(new Date());
        trade.setTradeFee(new BigDecimal(0));
        trade.setTradeNo(RandomUitl.buildOrderNo("T"));
        Date now = new Date()
        trade.setTradeDate(now)
        trade.setUpdateTime(now)
        WalletTrade wat = walletTradeRepository.save(trade);
        wallet.setBalance(wat.getBalance());
        walletRepository.save(wallet);
        trade
    }

    public WalletRemitRecord withdraw(WalletTrade trade, Wallet wallet, Channel channel, User user) {
        BankCard bc = bankService.findOne(trade.getBankcardId(), user);
        WalletRemitRecord wrr = saveRemitInfo(trade, bc, channel);
        trade.setTradeSourceId(wrr.getId());
        trade.setPartnerRequestno(wrr.getRequestNo());
        if (wallet.getUnbalance() != null) {
            wallet.setUnbalance(wallet.getUnbalance().add(trade.getAmount()));
        } else {
            wallet.setUnbalance(trade.getAmount());
        }
        createWalletTrade(trade, wallet);
        return wrr;
    }

    public WalletRemitRecord saveRemitInfo(WalletTrade trade, BankCard bc, Channel channel) {
        WalletRemitRecord remitInfo = new WalletRemitRecord();
        remitInfo.setBankId(bc.getBank());
        remitInfo.setRequestNo(RandomUitl.buildOrderNo("R"));
        remitInfo.setRemitDate(new Date());
        remitInfo.setTradeSource(1);
        remitInfo.setAccountType(0);
        remitInfo.setAccountNo(bc.getBankNo());
        remitInfo.setAccountName(bc.getName());
        remitInfo.setBankName("");
        remitInfo.setChannel(channel)
        remitInfo.setTradeAmt(trade.getAmount());
        remitInfo.setTradeFee(new BigDecimal(0));
        remitInfo.setStatus(channel.isAgentChannel() ? WalletTradeStatus.Enum.CREATE_1 : WalletTradeStatus.Enum.PROCESSING_5)
        Date now = new Date()
        remitInfo.setCreateTime(now)
        remitInfo.setUpdateTime(now)
        return walletRemitRepository.save(remitInfo);
    }


    public WalletTrade queryTradeByTradeNo(String tradeNo, User user) {
        WalletTrade trade = null;
        try {
            trade = walletTradeRepository.findByTradeNo(tradeNo, user);
            return trade;
        } catch (Exception e) {
            logger.error("query wallet trade error", e);
            return trade;
        }
    }

    public WalletRemitRecord queryRemitByRequestNo(String requestNo, User user) {
        WalletRemitRecord remitRecord = null;
        try {
            remitRecord = walletRemitRepository.findByRequestNo(requestNo, user.id);
            return remitRecord;
        } catch (Exception e) {
            logger.error("query wallet remit error", e);
            return remitRecord;
        }
    }

    public List<WalletTrade> queryTradeByTradeNo(String beiginDate, String endDate) {
        List<WalletTrade> trades = null;
        try {
            trades = walletTradeRepository.findByDate(beiginDate, endDate);
            return trades;
        } catch (Exception e) {
            logger.error("query wallet trade error", e);
            return trades;
        }
    }

    public WalletTrade queryTradeByRequestNo(String requestNo) {
        WalletTrade trade = null;
        try {
            trade = walletTradeRepository.findByRequestNo(requestNo);
            return trade;
        } catch (Exception e) {
            logger.error("query wallet trade error", e);
            return trade;
        }
    }

    public Map findByStatusAndUser(final List<WalletTradeStatus> status, final Wallet wallet, final Pageable pageable) {
        Page<WalletTrade> page = walletTradeRepository.findAll(new Specification<WalletTrade>() {
            @Override
            public Predicate toPredicate(Root<WalletTrade> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<WalletTrade> criteriaQuery = cb.createQuery(WalletTrade.class);
                List<Predicate> predicateList = new ArrayList<>();
                predicateList.add(root.get("status").in(status));
                predicateList.add(cb.equal(root.get("walletId"), wallet.getId()));

                criteriaQuery.orderBy(cb.desc(root.get("id")));
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);

        Map ordersMap = CacheUtil.doJacksonDeserialize(CacheUtil.doJacksonSerialize(page, true), Map)
        ordersMap?.content?.each {
            if (it.description) {
                it.tradeType.source = it.description + it.tradeType.source
                it.tradeType.description = it.description + it.tradeType.description
            }
        }
        return ordersMap
    }

    Map walletTradeDetailInfo(Long tradeId, Long tradeTypeId) {

        logger.info("find  detail info wallet trade :{} ,trade type :{} ", tradeId, tradeTypeId)
        if (tradeTypeId == WalletTradeSource.Enum.ORDER_REBATE_6.id) {
            WalletTrade walletTrade = walletTradeRepository.findOne(tradeId)
            ChannelAgent orderChannelAgent = channelAgentRepository.findByChannelAndOrderId(Channel.findAgentChannel(getChannel()).id, walletTrade.tradeSourceId)
            def placeOrderName = orderChannelAgent.user.name
            if (orderChannelAgent.user.id != walletTrade.userId) {
                ChannelAgent currentChannelAgent = channelAgentRepository.findChannelAgent(walletTrade.userId, Channel.findAgentChannel(Channel.toChannel(walletTrade.channel)).id)
                if (orderChannelAgent.agentLevel.id - currentChannelAgent.agentLevel.id > 1) {
                    placeOrderName = placeOrderName[0] + '*' * (placeOrderName.length() - 1)
                }
            }
            return [
                title         : walletTrade.description ? (walletTrade.description + walletTrade.tradeType.source) : walletTrade.tradeType.source,
                status        : walletTrade.status.status,
                amount        : walletTrade.amount,
                tradeDate     : DateUtils.getDateString(walletTrade.tradeDate, DATE_LONGTIME24_PATTERN),
                placeOrderName: placeOrderName
            ]

        } else if (tradeTypeId == WalletTradeSource.Enum.WITHDRAW_3.id) {
            List<Object[]> walletTradeInfo = walletTradeRepository.findWalletTradeDetail(tradeId)
            return [
                status   : walletTradeInfo[0][0],
                amount   : walletTradeInfo[0][1],
                tradeDate: DateUtils.getDateString(walletTradeInfo[0][2], DATE_LONGTIME24_PATTERN),
                identity : walletTradeInfo[0][3],
                bandName : walletTradeInfo[0][4],
                bandNo   : walletTradeInfo[0][5],
                remark   : walletTradeInfo[0][6],
                title    : walletTradeInfo[0][7]
            ]
        } else {
            return null
        }

    }

    public Map<String, String> doRemitPay(String requestNo, String tradeNo, User user) {
        WalletRemitRecord remitRecord = this.queryRemitByRequestNo(requestNo, user);
        WalletTrade trade = this.queryTradeByTradeNo(tradeNo, user);
        if (trade == null) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "未找到对应的交易记录！");
        }
        if (remitRecord == null) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "未找到相应订单号的订单记录！");
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("order_id", requestNo);
        map.put("amount", remitRecord.getTradeAmt().multiply(new BigDecimal(100)).setScale(0).toString());
        map.put("recv_account_type", "00");
        map.put("recv_bank_acc_pro", String.valueOf(remitRecord.getAccountType()));
        map.put("recv_account", remitRecord.getAccountNo());
        map.put("recv_user_name", remitRecord.getAccountName());
        map.put("recv_gate_id", remitRecord.getBankId().getShortName());
        map.put("purpose", "钱包提现");
//        map.put("bank_brhname", remitRecord.getBankName());

        return withdrawHandler.withdraw(map, user);

    }

    public void updateTradeStatus(Map<String, String> resMap, String tradeNo, String requestNo, User user) {
        WalletTrade trade = this.queryTradeByTradeNo(tradeNo, user);
        WalletRemitRecord remitRecord = this.queryRemitByRequestNo(requestNo, user);
        if (resMap != null && trade.getStatus().getId().equals(WalletTradeStatus.Enum.PROCESSING_5.getId())) {
            if (resMap.get("ret_code").equals("0000") || resMap.get("ret_code").equals("00180021")) {
                trade.setStatus(WalletTradeStatus.Enum.PROCESSING_5);
                trade.setTradeFee(new BigDecimal(resMap.getOrDefault("fee", "100")).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_DOWN));
                trade.setUpdateTime(new Date());
                walletTradeRepository.save(trade);
                this.updateRemitStatus(resMap, remitRecord, WalletTradeStatus.Enum.PROCESSING_5, user);
            } else {
                trade.setStatus(WalletTradeStatus.Enum.FAIL_3);
                trade.setUpdateTime(new Date());
                walletTradeRepository.save(trade);
                this.updateRemitStatus(resMap, remitRecord, WalletTradeStatus.Enum.FAIL_3, user);
                //恢复钱包余额
                Wallet wallet = walletRepository.findOne(trade.walletId);
                wallet.setBalance(wallet.getBalance().add(trade.getAmount()));
                wallet.setUnbalance(wallet.getUnbalance().subtract(trade.getAmount()));
                wallet.setUpdateTime(new Date());
                walletRepository.save(wallet);
            }
        }
    }

    void updateRemitStatus(Map<String, String> resMap, WalletRemitRecord remitRecord, WalletTradeStatus status, User user) {

        if (status.getId().equals(WalletTradeStatus.Enum.FINISHED_2.getId())) {
            remitRecord.setTradeFee(new BigDecimal(resMap.getOrDefault("fee", "100")).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_DOWN));
        }
        remitRecord.setResponseCode(resMap.get("ret_code"));
        remitRecord.setResponseMsg(resMap.get("ret_msg"));
        remitRecord.setResponseNo(resMap.get("trade_no"));
        remitRecord.setResponseTime(DateUtils.getDate(resMap.get("mer_date"), "yyyyMMdd"));
        remitRecord.setUpdateTime(new Date());
        remitRecord.setStatus(status);
        walletRemitRepository.save(remitRecord);
    }

    void updateNoticeStatus(Map<String, String> resMap, WalletTrade trade, WalletTradeStatus status, User user) {
        WalletRemitRecord remitRecord = this.queryRemitByRequestNo(resMap.get("order_id"), user);
        remitRecord.setResponseCode(resMap.get("ret_code"));
        remitRecord.setResponseMsg(resMap.get("ret_msg"));
        remitRecord.setResponseNo(resMap.get("trade_no"));
        remitRecord.setResponseTime(DateUtils.getDate(resMap.get("mer_date"), "yyyyMMdd"));

        Wallet wallet = walletRepository.findOne(trade.getWalletId());
        if (status.getId() == (WalletTradeStatus.Enum.FINISHED_2.getId())) {
            trade.setTradeFee(new BigDecimal(resMap.getOrDefault("fee", "100")).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_DOWN));
            remitRecord.setTradeFee(trade.getTradeFee());
            if (trade.getStatus().getId() == (WalletTradeStatus.Enum.PROCESSING_5.getId())) {
                wallet.setUnbalance(wallet.getUnbalance().subtract(trade.getAmount()));
            } else {
                wallet.setBalance(wallet.getBalance().subtract(trade.getAmount()));
            }
        } else {
            wallet.setBalance(wallet.getBalance().add(trade.getAmount()));
            wallet.setUnbalance(wallet.getUnbalance().subtract(trade.getAmount()));
        }
        remitRecord.setUpdateTime(new Date());
        remitRecord.setStatus(status);
        trade.setStatus(status);
        trade.setUpdateTime(new Date());
        wallet.setUpdateTime(new Date());
        walletRemitRepository.save(remitRecord);
        walletTradeRepository.save(trade);
        walletRepository.save(wallet);

    }
    /**
     *  设置钱包描述前缀,先找到当前订单的ChannelAgent,即orderCa
     *  channelAgent 参数该订单父级ChannelAgent,入钱包时，记录该下级的agentLevel描述
     * @param purchaseOrder
     * @param channelAgent
     * @return
     */
    def setPrefix(PurchaseOrder purchaseOrder, ChannelAgent channelAgent) {

        ChannelAgent orderCa = channelAgentRepository.findByUserAndChannel(purchaseOrder.applicant, Channel.findAgentChannel(purchaseOrder.sourceChannel.parent))
        if (!orderCa) {
            logger.info("by user id:{},channel id:{}", purchaseOrder.applicant, purchaseOrder.sourceChannel.id)
            throw new BusinessException(BusinessException.Code.BAD_QUOTE_PARAMETER, "未找到该订单得channelAgent用户")
        }
        return orderCa.agentLevel != channelAgent.agentLevel ? orderCa.agentLevel.description : ""
    }

    void registerRebate(ChannelAgent channelAgent, BigDecimal rebate, WalletTradeSource tradeType) {
        Wallet wallet = walletService.queryOrCreateWallet(channelAgent.user, channelAgent.channel)
        WalletTrade trade = new WalletTrade(
            tradeFlag: 1, amount: rebate, userId: channelAgent.user.id, tradeNo: RandomUitl.buildOrderNo("T"),
            tradeType: tradeType, tradeSourceId: channelAgent.id, status: WalletTradeStatus.Enum.FINISHED_2, channel: channelAgent.channel.id
        )
        createWalletTrade(trade, wallet)
    }

    void registerReward(ChannelAgent channelAgent) {

        if (agentRewardCacheHandler.activityValid() && Channel.agentLevelChannels().contains(channelAgent.channel)) {
            if (agentRewardCacheHandler.registerLimit.call()) {
                logger.info("first register channel agent >>> {},parent >>> {}", channelAgent.id, channelAgent.parent?.id)
                registerRebate(channelAgent, new BigDecimal(agentRewardCacheHandler.getRebateFromCache(_REGISTER_REBATE)), REGISTER_REBATE_7)
                agentRewardCacheHandler.countClosure.call(_ACTIVITY_HASH_KEY, _REGISTERS)
            }

            if (channelAgent.parent && agentRewardCacheHandler.inviteRegisterLimit.call(channelAgent.parent)) {
                logger.info("结算邀请人 >>> {}的奖励金", channelAgent.parent.id)
                registerRebate(channelAgent.parent, new BigDecimal(agentRewardCacheHandler.getRebateFromCache(_INVITE_REBATE)), INVITE_CA_REBATE_8)
                agentRewardCacheHandler.countClosure.call(_AGENT_COUNT_HASH_KEY, _GET_HASH_KEY.call(_INVITE_REGISTER_PRE, channelAgent.parent.id))
                agentRewardCacheHandler.countClosure.call(_ACTIVITY_HASH_KEY, _INVITES)
            }
        }
    }
}

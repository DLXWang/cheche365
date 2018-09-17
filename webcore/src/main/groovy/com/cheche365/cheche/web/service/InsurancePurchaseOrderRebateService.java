package com.cheche365.cheche.web.service;

import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.model.agent.ChannelAgent;
import com.cheche365.cheche.core.model.agent.ChannelAgentPurchaseOrderRebate;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.core.repository.agent.ChannelAgentOrderRebateRepository;
import com.cheche365.cheche.core.service.agent.ChannelRebateService;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.web.model.ChannelAgentRebateInfo;
import com.cheche365.cheche.web.util.ChannelAgentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.cheche365.cheche.core.model.RebateChannel.Enum.REBATE_CHANNEL_AGENT;
import static com.cheche365.cheche.core.model.RebateChannel.Enum.REBATE_CHANNEL_TOA;

/**
 * Created by mahong on 2016/5/31.
 */
@Service
@Transactional
public class InsurancePurchaseOrderRebateService {

    private Logger logger = LoggerFactory.getLogger(InsurancePurchaseOrderRebateService.class);

    public final String upType = "up";
    final String downType = "down";

    @Autowired
    private InsurancePurchaseOrderRebateRepository purchaseOrderRebateRepository;

    @Autowired
    private AgentRebateRepository agentRebateRepository;

    @Autowired
    private InsuranceRepository insuranceRepository;

    @Autowired
    private CompulsoryInsuranceRepository compulsoryInsuranceRepository;

    @Autowired
    private InsurancePurchaseOrderRebateRepository insurancePurchaseOrderRebateRepository;

    @Autowired
    public ChannelRebateService channelRebateService;

    @Autowired
    private InsurancePurchaseOrderRebateRepository orderRebateRepository;

    @Autowired
    private ChannelAgentService channelAgentService;

    @Autowired
    private ChannelAgentRebateRepository channelAgentRebateRepository;

    @Autowired
    ChannelAgentOrderRebateRepository channelAgentOrderRebateRepository;

    @Autowired
    ChannelAgentInfoService channelAgentInfoService;

    @Autowired
    ChannelRebateRepository channelRebateRepository;

    public void savePurchaseOrderRebate(QuoteRecord quoteRecord, PurchaseOrder order, Agent agent) {
        AgentRebate agentRebate = agentRebateRepository.findFirstByAgentAndAreaAndInsuranceCompany(agent, quoteRecord.getArea(), quoteRecord.getInsuranceCompany());
        Double upCompulsoryRebate, upCommercialRebate;
        if (agentRebate != null) {
            upCompulsoryRebate = agentRebate.getCompulsoryRebate();
            upCommercialRebate = agentRebate.getCommercialRebate();
        } else {
            upCompulsoryRebate = agent.getRebate();
            upCommercialRebate = agent.getRebate();
        }
        InsurancePurchaseOrderRebate purchaseOrderRebate = new InsurancePurchaseOrderRebate();
        purchaseOrderRebate.setPurchaseOrder(order);
        purchaseOrderRebate.setUpRebateChannel(RebateChannel.Enum.REBATE_CHANNEL_AGENT);
        purchaseOrderRebate.setUpChannelId(agent.getId());
        purchaseOrderRebate.setUpCommercialRebate(DoubleUtils.doubleValue(upCommercialRebate));
        purchaseOrderRebate.setUpCompulsoryRebate(DoubleUtils.doubleValue(upCompulsoryRebate));
        purchaseOrderRebate.setCreateTime(new Date());
        purchaseOrderRebate.setUpdateTime(purchaseOrderRebate.getCreateTime());
        purchaseOrderRebateRepository.save(purchaseOrderRebate);
    }

    public InsurancePurchaseOrderRebate updateChannelRebate(String type, Double compulsoryRebate, Double commercialRebate, PurchaseOrder purchaseOrder) {
        InsurancePurchaseOrderRebate purchaseOrderRebate = purchaseOrderRebateRepository.findFirstByPurchaseOrder(purchaseOrder);
        Insurance insurance = insuranceRepository.findByQuoteRecordId(purchaseOrder.getObjId());
        CompulsoryInsurance compulsoryInsurance = compulsoryInsuranceRepository.findByQuoteRecordId(purchaseOrder.getObjId());
        Double insurancePremium = insurance != null ? insurance.getPremium() : 0;
        Double compulsoryPremium = compulsoryInsurance != null ? compulsoryInsurance.getCompulsoryPremium() : 0;
        if (upType.equals(type)) {
            purchaseOrderRebate.setUpCommercialRebate(DoubleUtils.doubleValue(commercialRebate));
            purchaseOrderRebate.setUpCompulsoryRebate(DoubleUtils.doubleValue(compulsoryRebate));
            purchaseOrderRebate.setUpCommercialAmount(DoubleUtils.displayDoubleValue(DoubleUtils.mul(insurancePremium, commercialRebate, BigDecimal.ROUND_HALF_UP) / 100));
            purchaseOrderRebate.setUpCompulsoryAmount(DoubleUtils.displayDoubleValue(DoubleUtils.mul(compulsoryPremium, compulsoryRebate, BigDecimal.ROUND_HALF_UP) / 100));
        } else if (downType.equals(type)) {
            purchaseOrderRebate.setDownCommercialRebate(commercialRebate);
            purchaseOrderRebate.setDownCompulsoryRebate(compulsoryRebate);
            purchaseOrderRebate.setDownCommercialAmount(DoubleUtils.displayDoubleValue(DoubleUtils.mul(insurancePremium, commercialRebate, BigDecimal.ROUND_HALF_UP) / 100));
            purchaseOrderRebate.setDownCompulsoryAmount(DoubleUtils.displayDoubleValue(DoubleUtils.mul(compulsoryPremium, compulsoryRebate, BigDecimal.ROUND_HALF_UP) / 100));
        }
        purchaseOrderRebate.setUpdateTime(new Date());
        return purchaseOrderRebateRepository.save(purchaseOrderRebate);
    }

    public InsurancePurchaseOrderRebate findByPurchaseOrder(PurchaseOrder purchaseOrder) {
        return insurancePurchaseOrderRebateRepository.findFirstByPurchaseOrder(purchaseOrder);
    }


    public Double discountAmount(QuoteRecord quoteRecord, InsurancePurchaseOrderRebate rebate) {
        Double discountAmount = 0d;
        if (rebate == null) {
            return discountAmount;
        }
        if (DoubleUtils.isNotZero(rebate.getUpCommercialRebate())) {
            discountAmount += quoteRecord.getPremium() * rebate.getUpCommercialRebate() / 100;
        }
        if (DoubleUtils.isNotZero(rebate.getUpCompulsoryRebate())) {
            discountAmount += quoteRecord.getCompulsoryPremium() * rebate.getUpCompulsoryRebate() / 100;
        }
        return DoubleUtils.displayDoubleValue(discountAmount);
    }

    public void applyRebate(QuoteRecord quoteRecord, PurchaseOrder order) {

        ChannelAgent channelAgent = channelAgentService.getCurrentChannelAgent(quoteRecord);

        if (channelAgent != null) {
            fillCapor(channelAgent, quoteRecord, order);
        } else {
            fillInsurancePurchaseOrderRebate(quoteRecord, order);
        }
    }

    private void fillInsurancePurchaseOrderRebate(QuoteRecord quoteRecord, PurchaseOrder order) {
        ChannelRebate rebate = channelRebateService.getChannelRebate(quoteRecord, order);
        if (rebate == null) {
            return;
        }
        InsurancePurchaseOrderRebate orderRebate = orderRebateRepository.findFirstByPurchaseOrder(order);
        if (orderRebate == null) {
            orderRebate = new InsurancePurchaseOrderRebate();
            orderRebate.setPurchaseOrder(order);
            orderRebate.setCreateTime(new Date());
            orderRebate.setUpRebateChannel(quoteRecord.getChannel().isAgentChannel() ? REBATE_CHANNEL_TOA : REBATE_CHANNEL_AGENT);
            orderRebate.setUpChannelId(quoteRecord.getChannel().getId());
            orderRebate.setUpCommercialRebate(rebate != null ? rebate.getCommercialRebate() : 0);
            orderRebate.setUpCompulsoryRebate(rebate != null ? rebate.getCompulsoryRebate() : 0);
        }
        orderRebate.setUpCommercialAmount(DoubleUtils.displayDoubleValue(DoubleUtils.mul(DoubleUtils.doubleValue(quoteRecord.getPremium()), orderRebate.getUpCommercialRebate(), BigDecimal.ROUND_HALF_UP) / 100));
        orderRebate.setUpCompulsoryAmount(DoubleUtils.displayDoubleValue(DoubleUtils.mul(DoubleUtils.doubleValue(quoteRecord.getCompulsoryPremium()), orderRebate.getUpCompulsoryRebate(), BigDecimal.ROUND_HALF_UP) / 100));
        orderRebate.setUpdateTime(new Date());
        orderRebateRepository.save(orderRebate);
    }

    /**
     * description capor 即ChannelAgentPurchaseOrderRebate 缩写
     *
     * @param channelAgent  当前下单代理人channelAgent
     * @param quoteRecord
     * @param purchaseOrder 通过channelAgent找出所有上级代理人和每个涉及到的代理人的 ChannelAgentRebate 将每个代理人此时的点位
     *                      和订单关联 存入ChannelAgentPurchaseOrderRebate
     */
    public void fillCapor(ChannelAgent channelAgent, QuoteRecord quoteRecord, PurchaseOrder purchaseOrder) {

        ChannelRebate channelRebate = channelRebateRepository.findFirstByChannelAndAreaAndInsuranceCompanyAndStatus(
            Channel.findAgentChannel(quoteRecord.getChannel().getParent()),
            quoteRecord.getArea(),
            quoteRecord.getInsuranceCompany(),
            ChannelRebate.Enum.EFFECTIVED_1
        );
        logger.info("代理人下单分配订单点位,已知信息订单归属代理人>>>>{},所以上级人员>>>>{},订单号>>>>{}",channelAgent.getId(),channelAgent.getAgentCode(),purchaseOrder.getOrderNo());
        ChannelAgentRebateInfo channelAgentRebateInfo = new ChannelAgentRebateInfo();
        Map calculateRebate = ChannelAgentUtil.calculateRebate(channelRebate,channelAgent,quoteRecord);
        channelAgentRebateInfo.setChannelAgent(channelAgent);
        channelAgentRebateInfo.setChannelRebate(channelRebate);
        channelAgentRebateInfo.setPurchaseOrder(purchaseOrder);
        channelAgentRebateInfo.setQuoteRecord(quoteRecord);
        channelAgentRebateInfo.setCommercialRebate((Double)calculateRebate.get("commercialRebate"));
        channelAgentRebateInfo.setChildCommercialRebate(0d);
        channelAgentRebateInfo.setCompulsoryRebate((Double)calculateRebate.get("compulsoryRebate"));
        channelAgentRebateInfo.setChildCompulsoryRebate(0d);
        assembleRebate(channelAgentRebateInfo);
    }

    private void assembleRebate(ChannelAgentRebateInfo channelAgentRebateInfo) {

        saveCapor(channelAgentRebateInfo.getChannelAgent(),
            channelAgentRebateInfo.getPurchaseOrder(),
            (channelAgentRebateInfo.getCommercialRebate()-channelAgentRebateInfo.getChildCommercialRebate()),
            (channelAgentRebateInfo.getCompulsoryRebate()-channelAgentRebateInfo.getChildCompulsoryRebate()));

        if (channelAgentRebateInfo.getChannelAgent().getParent() != null) {
            Map calculateRebate = ChannelAgentUtil.calculateRebate(channelAgentRebateInfo.getChannelRebate(),channelAgentRebateInfo.getChannelAgent().getParent(),channelAgentRebateInfo.getQuoteRecord());
            channelAgentRebateInfo.setChannelAgent(channelAgentRebateInfo.getChannelAgent().getParent());
            channelAgentRebateInfo.setChildCommercialRebate(channelAgentRebateInfo.getCommercialRebate());
            channelAgentRebateInfo.setChildCompulsoryRebate(channelAgentRebateInfo.getCompulsoryRebate());
            channelAgentRebateInfo.setCommercialRebate((Double)calculateRebate.get("commercialRebate"));
            channelAgentRebateInfo.setCompulsoryRebate((Double)calculateRebate.get("compulsoryRebate"));
            assembleRebate(channelAgentRebateInfo);
        }
    }

    public void saveCapor(ChannelAgent channelAgent, PurchaseOrder purchaseOrder, Double commercialRebate, Double compulsoryRebate) {

        ChannelAgentPurchaseOrderRebate capor = channelAgentOrderRebateRepository.findByChannelAgentAndPurchaseOrder(channelAgent, purchaseOrder);
        if (capor == null) {
            capor = new ChannelAgentPurchaseOrderRebate();
            capor.setPurchaseOrder(purchaseOrder);
            capor.setChannelAgent(channelAgent);
        }
        capor.setCommercialRebate(commercialRebate);
        capor.setCompulsoryRebate(compulsoryRebate);
        capor.setCreateTime(new Date());
        capor.setCreateTime(new Date());

        logger.info("代理人点位订单持久化信息,代理人>>>>{},商业险点位>>>>{},交强险点位>>>>{}",channelAgent.getId(),commercialRebate,compulsoryRebate);
        channelAgentOrderRebateRepository.save(capor);
    }

    public Double settleRebate(ChannelAgentPurchaseOrderRebate channelAgentPurchaseOrderRebate, QuoteRecord quoteRecord) {
        Double commercialAmount = 0d;
        Double compulsoryAmount = 0d;
        if (DoubleUtils.isNotZero(channelAgentPurchaseOrderRebate.getCommercialRebate())) {
            commercialAmount = quoteRecord.getPremium() * channelAgentPurchaseOrderRebate.getCommercialRebate() / 100;
        }

        if (DoubleUtils.isNotZero(channelAgentPurchaseOrderRebate.getCompulsoryRebate())) {
            compulsoryAmount = quoteRecord.getCompulsoryPremium() * channelAgentPurchaseOrderRebate.getCompulsoryRebate() / 100;
        }

        channelAgentPurchaseOrderRebate.setCommercialAmount(commercialAmount);
        channelAgentPurchaseOrderRebate.setCompulsoryAmount(compulsoryAmount);
        channelAgentPurchaseOrderRebate.setUpdateTime(new Date());
        channelAgentOrderRebateRepository.save(channelAgentPurchaseOrderRebate);
        logger.info("代理人点位结算 ChannelAgentPurchaseOrderRebate id: {},商业险佣金为:{},交强险佣金为:{}", channelAgentPurchaseOrderRebate.getId(), commercialAmount, compulsoryAmount);
        return (double) Math.round((commercialAmount + compulsoryAmount) * 100) / 100;
    }

}

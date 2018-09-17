package com.cheche365.cheche.core.service;

import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.AgentRebateRepository;
import com.cheche365.cheche.core.repository.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/***************************************************************************/
/*                              AgentService.java                 */
/*   文   件 名: AgentService.java                                  */
/*   模  块： 代理人系统                                              */
/*   功  能:  代理人服务类                           */
/*   初始创建:2015/4/8                                            */
/*   版本更新:V1.0                                                         */
/*   版权所有:北京车与车科技有限公司                                       */
/***************************************************************************/

@Service("AgentService")
@Transactional
public class AgentService {

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private AgentRebateRepository agentRebateRepository;


    public long getTotalCount() {
        return agentRepository.count();
    }

    public Agent findOne(long id) {
        return agentRepository.findOne(id);
    }

    public List findAvgAndMaxAndMinRebate() {
        return agentRepository.findAvgAndMaxAndMinRebate();
    }

    public Double calculateRebateAmount(QuoteRecord quoteRecord, Agent oldAgent) {
        Double totalRebateAmount = 0.0D;
        Channel channel = quoteRecord.getChannel();
        if (channel == null || Channel.isInGroup(channel.getId(), Channel.Enum.ALIPAY_21) || channel.isThirdPartnerChannel()) {
            return totalRebateAmount;
        }
        Agent agent = agentRepository.findFirstByUser(quoteRecord.getApplicant());
        if (agent != null && agent.getEnable()) {
            Area area = quoteRecord.getArea();
            InsuranceCompany company = quoteRecord.getInsuranceCompany();
            AgentRebate agentRebate = agentRebateRepository.findFirstByAgentAndAreaAndInsuranceCompany(agent, area, company);
            if (agentRebate != null) {
                Double premium = quoteRecord.getPremium();
                Double compulsoryPremium = quoteRecord.getCompulsoryPremium();
                Double commercialRebate = agentRebate.getCommercialRebate();
                Double compulsoryRebate = agentRebate.getCompulsoryRebate();
                Double commercialRebateAmount = 0.0D;
                Double compulsoryRebateAmount = 0.0D;
                if (DoubleUtils.isNotZero(premium) && commercialRebate != null) {
                    commercialRebateAmount = getFormatAmount(premium, commercialRebate);
                }

                if (DoubleUtils.isNotZero(compulsoryPremium) && compulsoryRebate != null) {
                    compulsoryRebateAmount = getFormatAmount(compulsoryPremium, compulsoryRebate);
                }
                totalRebateAmount = DoubleUtils.displayDoubleValue(commercialRebateAmount + compulsoryRebateAmount);
            } else {
                totalRebateAmount = oldAgent.caculatRebateAmount(quoteRecord);
            }
        }
        return totalRebateAmount;
    }

    private Double getFormatAmount(Double premium, Double Rebate) {
        return new BigDecimal(DoubleUtils.displayDoubleValue(DoubleUtils.doubleValue(premium)) * Rebate / 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public Boolean checkAgent(User user) {
        if (null != user && null != user.getUserType() && user.getUserType().getId() == UserType.Enum.Agent.getId()) {
            Agent agent = agentRepository.findFirstByUser(user);
            return agent != null && agent.getEnable();
        }
        return false;
    }

}

package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.AgentRepository;
import com.cheche365.cheche.core.repository.OrderAgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xu.yelong on 2016/3/10.
 */
@Service
public class OrderAgentService {
    @Autowired
    private OrderAgentRepository orderAgentRepository;

    @Autowired
    private AgentRepository agentRepository;

    public OrderAgent findByPurchaseOrder(PurchaseOrder purchaseOrder){
        return orderAgentRepository.findByPurchaseOrder(purchaseOrder);
    }


    public void checkAgent(PurchaseOrder purchaseOrder){
        if(purchaseOrder==null){
            return;
        }
        User user = purchaseOrder.getApplicant();
        if(user.getUserType() != null && (user.getUserType().getId() == UserType.Enum.Agent.getId())){
            Agent agent=agentRepository.findFirstByUser(user);
            if(agent==null||!agent.getEnable()){
                return;
            }
            OrderAgent orderAgent=findByPurchaseOrder(purchaseOrder);
            if(orderAgent!=null){
                if(orderAgent.getAgent().getId().equals(agent.getId())){
                    return;
                }
            }else{
                orderAgent=new OrderAgent();
            }
            orderAgent.setAgent(agent);
            orderAgent.setPurchaseOrder(purchaseOrder);
            orderAgentRepository.save(orderAgent);
        }
    }
}

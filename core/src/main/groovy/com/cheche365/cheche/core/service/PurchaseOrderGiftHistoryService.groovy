package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.model.PurchaseOrderGift
import com.cheche365.cheche.core.model.PurchaseOrderGiftHistory
import com.cheche365.cheche.core.model.PurchaseOrderHistory
import com.cheche365.cheche.core.repository.PurchaseOrderGiftHistoryRepository
import com.cheche365.cheche.core.repository.PurchaseOrderGiftRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by mahong on 2016/9/22.
 */
@Service
public class PurchaseOrderGiftHistoryService {

    @Autowired
    private PurchaseOrderGiftHistoryRepository orderGiftHistoryRepository;

    @Autowired
    private PurchaseOrderGiftRepository orderGiftRepository;

    @Transactional
    public List<PurchaseOrderGiftHistory> saveOrderGiftHistories(PurchaseOrderHistory orderHistory, List<PurchaseOrderGift> orderGifts) {

        if (orderGifts == null || orderGifts.isEmpty()) {
            return null
        }

        List<PurchaseOrderGiftHistory> orderGiftHistories = new ArrayList<>();
        Date createTime = new Date()
        orderGifts.each {
            PurchaseOrderGiftHistory orderGiftHis = new PurchaseOrderGiftHistory()
            orderGiftHis.setGift(it.gift)
            orderGiftHis.setGivenAfterOrder(it.givenAfterOrder)
            orderGiftHis.setPurchaseOrderHistory(orderHistory)
            orderGiftHis.setCreateTime(createTime)
            orderGiftHistories.add(orderGiftHis)
        }
        orderGiftHistoryRepository.save(orderGiftHistories)
    }

    public List<PurchaseOrderGiftHistory> saveOrderGiftHistories(PurchaseOrderHistory orderHistory) {
        saveOrderGiftHistories(orderHistory, orderGiftRepository.findByPurchaseOrder(orderHistory.getPurchaseOrder()))
    }

    public List<PurchaseOrderGiftHistory> findByPurchaseOrderHistory(PurchaseOrderHistory orderHistory) {
        orderGiftHistoryRepository.findByPurchaseOrderHistory(orderHistory)
    }
}

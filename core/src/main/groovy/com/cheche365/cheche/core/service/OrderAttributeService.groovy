package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.model.AttributeType
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.PurchaseOrderAttribute
import com.cheche365.cheche.core.repository.PurchaseOrderAttributeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by wen on 2018/6/15.
 */
@Service
class OrderAttributeService {

    @Autowired
    PurchaseOrderAttributeRepository purchaseOrderAttributeRepo

    def savePurchaseOrderAttribute(PurchaseOrder purchaseOrder,AttributeType type,String value){
        PurchaseOrderAttribute purchaseOrderAttribute = purchaseOrderAttributeRepo.findByPurchaseOrderAndType(purchaseOrder,type)
        if(!purchaseOrderAttribute){
            purchaseOrderAttribute = new PurchaseOrderAttribute(purchaseOrder : purchaseOrder,
               type : type)
        }

        purchaseOrderAttribute.value = value
        purchaseOrderAttributeRepo.save(purchaseOrderAttribute)
    }
}

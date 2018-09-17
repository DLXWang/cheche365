package com.cheche365.cheche.ordercenter.service.nationwideOrder;

import com.cheche365.cheche.core.model.DeliveryInfo;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.repository.DeliveryInfoRepository;
import com.cheche365.cheche.core.service.DeliveryInfoService;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.ordercenter.service.user.IInternalUserManageService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by wangfei on 2015/11/21.
 */
@Service
@Transactional
public class DeliveryInfoExpandService extends DeliveryInfoService {
    private Logger logger = LoggerFactory.getLogger(DeliveryInfoExpandService.class);

    @Autowired
    private IInternalUserManageService internalUserManageService;

    @Autowired
    private DeliveryInfoRepository deliveryInfoRepository;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private InstitutionQuoteRecordService institutionQuoteRecordService;

    public DeliveryInfo mergeDeliveryInfo (PurchaseOrder purchaseOrder, DeliveryInfo deliveryInfo) {
        DeliveryInfo model;
        if (null == purchaseOrder.getDeliveryInfo()) {
            logger.info("deliveryInfo of purchaseOrder is null, need to new one and update purchaseOrder.");
            model = createDeliveryInfo(deliveryInfo);
            purchaseOrderService.updateOrderDeliveryInfo(purchaseOrder, model);
        } else {
            logger.info("deliveryInfo of purchaseOrder is not null, need to update deliveryInfo of purchaseOrder.");
            DeliveryInfo oldDeliveryInfo = purchaseOrder.getDeliveryInfo();
            if (StringUtils.isNotBlank(deliveryInfo.getExpressCompany())) {
                oldDeliveryInfo.setExpressCompany(deliveryInfo.getExpressCompany());
            }
            if (StringUtils.isNotBlank(deliveryInfo.getTrackingNo())) {
                oldDeliveryInfo.setTrackingNo(deliveryInfo.getTrackingNo());
            }
            model = updateDeliveryInfo(oldDeliveryInfo);
        }
        return model;
    }

    public DeliveryInfo createDeliveryInfo (DeliveryInfo deliveryInfo) {
        deliveryInfo.setCreateTime(new Date());
        return updateDeliveryInfo(deliveryInfo);
    }

    public DeliveryInfo updateDeliveryInfo (DeliveryInfo deliveryInfo) {
        deliveryInfo.setOperator(internalUserManageService.getCurrentInternalUser());
        deliveryInfo.setUpdateTime(new Date());
        return deliveryInfoRepository.save(deliveryInfo);
    }

    @Transactional
    public DeliveryInfo updateInsuranceInfo(String commercialPolicyNo, String compulsoryPolicyNo, PurchaseOrder purchaseOrder,
                                            DeliveryInfo deliveryInfo) {
        if (StringUtils.isNotBlank(commercialPolicyNo) || StringUtils.isNotBlank(compulsoryPolicyNo)) {
            logger.info("policyNo is not empty, require to update policyNo of institutionQuoteRecord.");
            institutionQuoteRecordService.updateInsuranceInfo(commercialPolicyNo, compulsoryPolicyNo, purchaseOrder);
        }
        return mergeDeliveryInfo(purchaseOrder, deliveryInfo);
    }

}

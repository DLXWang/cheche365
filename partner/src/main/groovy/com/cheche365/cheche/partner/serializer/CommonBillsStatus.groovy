package com.cheche365.cheche.partner.serializer

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.model.PartnerOrder
import com.cheche365.cheche.core.serializer.ModelFieldsCopier

/**
 * Created by zhengwei on 7/13/16.
 */
class CommonBillsStatus extends ModelFieldsCopier {

    static final FIELDS_MAPPING = [
        [
            'sourcePath': [''],
            'targetPath': '',
            'fields'    : ['partnerUser.partnerId->uid', 'purchaseOrder.orderNo->orderNo', 'purchaseOrder.status->status', 'state']
        ]
    ]

    CommonBillsStatus generatePartnerOrder(PartnerOrder partnerOrder) {
        copyFields(partnerOrder)
        this.put("operateTime", partnerOrder.purchaseOrder.updateTime?.format(DateUtils.DATE_LONGTIME24_PATTERN))
        return this
    }

    @Override
    fieldsMapping() {
        return FIELDS_MAPPING
    }
}

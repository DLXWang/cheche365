package com.cheche365.cheche.soopay.payment

import com.cheche365.cheche.core.model.LogType
import com.cheche365.cheche.core.model.MoApplicationLog
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.model.mongo.MongoUser
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.DoubleDBService
import com.umpay.api.exception.VerifyException
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static com.umpay.api.paygate.v40.Plat2Mer_v40.getPlatNotifyData

/**
 * Created by mjg on 2017/6/19.
 */
@Component
class SoopayProcessor {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private DoubleDBService mongoDBService;

    void saveSoopayWithdrawLog(String objId, String orderNo, String msg, User user) {

        mongoDBService.saveApplicationLog( new MoApplicationLog(
            createTime: Calendar.getInstance().getTime(),
            instanceNo: orderNo,
            objId: objId,
            objTable: 'WALLET_REMIT_RECORD',
            user: MongoUser.toMongoUser(user),
            logType: LogType.Enum.ORDER_RELATED_3,
            logId: msg
        ))
    }

    static boolean isSoopayRefundTrade(String txnType) {
        return StringUtils.isNotBlank(txnType) & ISoopayHandler.SOOPAY_TXN_TYPE_03.equals(txnType);
    }

    static boolean isCorrect(respMap){
        def isCorrect
        try{
            isCorrect = getPlatNotifyData(respMap)
        } catch (VerifyException e) {
            isCorrect = false
        }
        isCorrect
    }
}

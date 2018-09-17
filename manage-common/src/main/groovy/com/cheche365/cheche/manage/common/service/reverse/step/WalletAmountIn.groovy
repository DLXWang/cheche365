package com.cheche365.cheche.manage.common.service.reverse.step

import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.repository.QuoteRecordRepository
import com.cheche365.cheche.wallet.model.WalletTrade
import com.cheche365.cheche.wallet.model.WalletTradeSource
import com.cheche365.cheche.wallet.repository.WalletTradeRepository
import com.cheche365.cheche.wallet.service.WalletTradeService
import groovy.util.logging.Slf4j
import org.apache.commons.collections.CollectionUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * Created by yellow on 2017/11/6.
 */
@Service
@Slf4j
class WalletAmountIn implements TPlaceInsuranceStep{
    @Override
    @Transactional
    Object run(Object context) {
        log.debug("------钱包计算------")
        WalletTradeService walletTradeService=context.walletTradeService
        PurchaseOrder purchaseOrder=context.purchaseOrder
        WalletTradeRepository walletTradeRepository=context.walletTradeRepository
        QuoteRecordRepository quoteRecordRepository=context.quoteRecordRepository
        List<WalletTrade> walletTradeList=walletTradeRepository.findByTradeSourceIdAndTradeType(purchaseOrder.getId(),WalletTradeSource.Enum.REBATE_TOA_4)
        //返录保单TOA渠道计算佣金到钱包，如进行返录的修改，则不再做进入钱包的修改
        if(Channel.rebateToWallets().contains(purchaseOrder.getSourceChannel()) && CollectionUtils.isEmpty(walletTradeList)) {
            walletTradeService.createAgentWalletTrade(quoteRecordRepository.findOne(purchaseOrder.getObjId()), purchaseOrder)
        }
        getContinueFSRV true
    }
}

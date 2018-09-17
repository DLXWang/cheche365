package com.cheche365.cheche.mock.controller

import com.cheche365.cheche.common.util.HashUtils
import com.cheche365.cheche.core.model.Bank
import com.cheche365.cheche.core.model.BankCard
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.repository.BankCardRepository
import com.cheche365.cheche.wallet.model.Wallet
import com.cheche365.cheche.wallet.model.WalletRemitRecord
import com.cheche365.cheche.wallet.model.WalletTrade
import com.cheche365.cheche.wallet.model.WalletTradeSource
import com.cheche365.cheche.wallet.model.WalletTradeStatus
import com.cheche365.cheche.wallet.repository.WalletRemitRepository
import com.cheche365.cheche.wallet.repository.WalletRepository
import com.cheche365.cheche.wallet.repository.WalletTradeRepository
import com.cheche365.cheche.wallet.service.WalletService
import com.cheche365.cheche.wallet.service.WalletTradeService
import com.cheche365.cheche.wallet.utils.RandomUitl
import com.cheche365.cheche.web.ContextResource
import com.cheche365.cheche.web.counter.annotation.NonProduction
import com.cheche365.cheche.web.util.ClientTypeUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletRequest

/**
 * Created by taichangwei on 2017/9/28.
 */
@RestController
@RequestMapping("/v1.6/mock/wallet")
@Slf4j
class MockWalletResource extends  ContextResource{
    @Autowired
    WalletService walletService;

    @Autowired
    private WalletTradeService tradeService;

    @Autowired
    BankCardRepository bankCardRepository

    @Autowired
    WalletRepository walletRepository
    @Autowired
    WalletTradeRepository walletTradeRepository
    @Autowired
    WalletRemitRepository walletRemitRepository


    /**
     * 模拟钱包提现逻辑创建数据库记录，主要是为提现回调测试做准备
     * @param request
     * @param param
     * @return
     */
    @NonProduction
    @RequestMapping(value = 'withdraw', method = RequestMethod.POST)
    def withdraw(HttpServletRequest request, @RequestBody Map<String, Object> param){

        String balance = String.valueOf(param.get("balance"))
        String unbalance = String.valueOf(param.get("unbalance"))
        String amount = String.valueOf(param.get("amount"))

        User user = currentUser()

        BankCard bankCard = bankCardRepository.findByUser(user)[0] ?: bankCardRepository.save(new BankCard(user: user, bank: new Bank(id: 1), bankNo: '6217000010000000000', name: '提现测试', disable: false))

        Channel channel = ClientTypeUtil.getChannel(request)
        Wallet wallet = walletService.queryOrCreateWallet(user, channel)
        wallet.setPaymentPwd(HashUtils.MD5('123456'))
        wallet.setBalance(new BigDecimal(balance))
        wallet.setUnbalance(new BigDecimal(unbalance))

        WalletTrade trade = new WalletTrade()
        trade.setTradeFlag(0)
        trade.setAmount(new BigDecimal(amount))
        trade.setBalance(wallet.getBalance().subtract(trade.getAmount()))
        trade.setBankcardId(Long.valueOf(bankCard.id))
        trade.setUserId(user.getId())
        trade.setTradeNo(RandomUitl.buildOrderNo("T"))
        trade.setTradeType(WalletTradeSource.Enum.WITHDRAW_3)
        trade.setStatus(WalletTradeStatus.Enum.PROCESSING_5)
        trade.setChannel(channel.getId())

        WalletRemitRecord remitRecord = tradeService.withdraw(trade, wallet, channel, user)

        [wallet: wallet, trade: trade, remitRecord: remitRecord]

    }

    @NonProduction
    @RequestMapping(value = 'walletInfo', method = RequestMethod.GET)
    def queryWalletInfo(@RequestParam Long walletId, @RequestParam Long tradeId, @RequestParam Long remitRecordId){
        Wallet wallet = walletRepository.findOne(walletId)
        WalletTrade walletTrade = walletTradeRepository.findOne(tradeId)
        WalletRemitRecord walletRemitRecord = walletRemitRepository.findOne(remitRecordId)

        [wallet: wallet, trade: walletTrade, remitRecord: walletRemitRecord]

    }

}

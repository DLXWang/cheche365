package com.cheche365.cheche.ordercenter.web.model.wallet

import groovy.transform.ToString

/**
 * Created by yinJianBin on 2018/4/5.
 */
@ToString
class WalletRemitResultModel {
    String merchantSeqNo
    String paymentMode //打款方式,暂时只有银行卡
    String billType //交易类型
    String accountNo
    String identity
    String accountName
    String transferAmount
    String orderStatus
    String bankSeqNo

}

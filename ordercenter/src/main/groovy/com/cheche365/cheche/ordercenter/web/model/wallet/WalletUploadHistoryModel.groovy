package com.cheche365.cheche.ordercenter.web.model.wallet

import groovy.transform.ToString

/**
 * Created by yinJianBin on 2018/4/8.
 */
@ToString
class WalletUploadHistoryModel {
    Long id
    String createTime
    String operator
    String fileName
    String status
}

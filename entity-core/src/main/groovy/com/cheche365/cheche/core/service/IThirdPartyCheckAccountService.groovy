package com.cheche365.cheche.core.service



/**
 * 第三方检查账号是否有效
 */
interface IThirdPartyCheckAccountService {

    /**
     * 检查账号是否可登录.
     * @return 返回登录异常的账号信息组成的List
     */
    List<Map> getFailedAccounts()

}

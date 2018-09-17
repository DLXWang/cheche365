package com.cheche365.cheche.core.service



/**
 * 第三方保险获取报价单信息
 */
public interface IThirdPartyQuoteRecordService {

    /**
     * 获取报价单状态
     * @param numbers 投保单号（交强险的、商业险的）、报价单号、订单号（车车的），格式[
     * [commercial:123,compulsory:6789,orderNo:12312,quoteNo:171881040747],
     * [commercial:123,compulsory:6789,orderNo:12312,quoteNo:171881040747]]
     * @param additionalParameters 附加参数
     * @return
     */
    def getQuoteRecordState(List numbers, Map<String, Object> additionalParameters)

}

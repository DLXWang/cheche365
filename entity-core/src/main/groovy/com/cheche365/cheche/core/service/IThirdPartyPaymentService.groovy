package com.cheche365.cheche.core.service



/**
 * 第三方保险获取支付信息业务
 */
public interface IThirdPartyPaymentService {

    /**
     * 获取支持的支付方式
     * @param applyPolicyNos 投保单号，格式[commercial: 12345, compulsory: 67890]
     * @param additionalParameters 附加参数
     * @return channels 格式 [channelType : 01, channels : [alipay,wx]]
     */
    def getPaymentChannels(Map applyPolicyNos, Map<String, Object> additionalParameters);


    /**
     * 获取支付链接或二维码
     * @param applyPolicyNos 投保单号，格式[commercial: 12345, compulsory: 67890]
     * @param additionalParameters 附加参数
     * @return paymentInfo 格式 [paymentURL: qrCodeBase64,metaInfo : [paymentURLFormat : base64,paymentNo : 12345]]
     */
    def getPaymentInfo(Map applyPolicyNos, Map<String, Object> additionalParameters);

    /**
     * 检查支付状态
     * @param paymentInfo 支付信息，格式[
     * [commercial:123,compulsory:6789,orderNo:12312,paymentNo:171881040747], 投保单号
     * [commercial:123,compulsory:6789,orderNo:12312,paymentNo:171881040747]]
     * @param additionalParameters 附加参数
     * @return
     */
    def checkPaymentState(List paymentInfos, Map<String, Object> additionalParameters)

    /**
     * 如果过期未支付
     * 取消支付，废除生成的支付单
     * @param applyPolicyNos 保单号，格式[commercial: 12345, compulsory: 67890]
     * @param additionalParameters 附加参数
     * @return
     */
    def cancelPay(Map applyPolicyNos, Map<String, Object> additionalParameters)


}

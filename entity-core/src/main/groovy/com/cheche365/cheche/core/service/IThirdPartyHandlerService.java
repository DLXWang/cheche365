package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.CompulsoryInsurance;
import com.cheche365.cheche.core.model.Insurance;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.QuoteRecord;

import java.util.Map;

/**
 * 第三方保险业务
 */
public interface IThirdPartyHandlerService {

    /**
     * 报价
     * @param quoteRecord 报价记录
     * @param additionalParameters 附加参数
     */
    void quote(QuoteRecord quoteRecord, Map<String, Object> additionalParameters);

    /**
     * 核保，如果是基于商务接口的，则通常能够得到投保单号，这时需要将投保单号置入相应insurance实例中
     * @param order 订单
     * @param insurance 商业险保单
     * @param compulsoryInsurance 交强险保单
     * @param additionalParameters 附加参数
     */
    void insure(PurchaseOrder order, Insurance insurance, CompulsoryInsurance compulsoryInsurance, Map<String, Object> additionalParameters);

    /**
     * 承保
     * @param order 订单
     * @param insurance 商业险保单
     * @param compulsoryInsurance 交强险保单
     * @param additionalParameters 附加参数
     */
    void order(PurchaseOrder order, Insurance insurance, CompulsoryInsurance compulsoryInsurance, Map<String, Object> additionalParameters);

}

package com.cheche365.cheche.unionpay.payment

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.unionpay.UnionPayConstant
import com.unionpay.acp.sdk.CertUtil
import com.unionpay.acp.sdk.SDKUtil
import com.unionpay.acp.sdk.SecureUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by wangfei on 2015/7/21.
 */
@Component
class UnionPaySignature {
    private static Logger logger = LoggerFactory.getLogger(UnionPaySignature.class);

    static Map<String, String> signData(Map<String, String> contentData) {
        Map<String, String> submitFromMap = contentData.findAll {it.value}
        String merchantId = contentData.get("merId");
        //todo 根据商户号找到对应的私钥证书加密传输数据
        try {
            switch (merchantId) {
                case IUnionPayHandler.UNION_PAY_MERCHANT_ID_PC:
                    SDKUtil.signByCertInfo(submitFromMap, IUnionPayHandler.UNION_PAY_ENCODING,
                        UnionPayConstant.UNION_PAY_SIGN_CERT_PATH_PC, UnionPayConstant.UNION_PAY_SIGN_CERT_PASSWORD);
                    break;
                case IUnionPayHandler.UNION_PAY_MERCHANT_ID_MOBILE:
                    SDKUtil.signByCertInfo(submitFromMap, IUnionPayHandler.UNION_PAY_ENCODING,
                        UnionPayConstant.UNION_PAY_SIGN_CERT_PATH_MOBILE, UnionPayConstant.UNION_PAY_SIGN_CERT_PASSWORD);
                    break;
                case IUnionPayHandler.UNION_PAY_MERCHANT_ID_PC_KQ:
                    SDKUtil.signByCertInfo(submitFromMap, IUnionPayHandler.UNION_PAY_ENCODING,
                        UnionPayConstant.UNION_PAY_SIGN_CERT_PATH_PC_KQ, UnionPayConstant.UNION_PAY_SIGN_CERT_PASSWORD_KQ);
                    break;
                case IUnionPayHandler.UNION_PAY_MERCHANT_ID_MOBILE_KQ:
                    SDKUtil.signByCertInfo(submitFromMap, IUnionPayHandler.UNION_PAY_ENCODING,
                        UnionPayConstant.UNION_PAY_SIGN_CERT_PATH_MOBILE_KQ, UnionPayConstant.UNION_PAY_SIGN_CERT_PASSWORD_KQ);
                    break;
                case IUnionPayHandler.UNION_PAY_MERCHANT_ID_APPLEPAY:
                    SDKUtil.signByCertInfo(submitFromMap, IUnionPayHandler.UNION_PAY_ENCODING,
                        UnionPayConstant.UNION_PAY_SIGN_CERT_PATH_APPLEPAY, UnionPayConstant.UNION_PAY_SIGN_CERT_PASSWORD_APPLEPAY);
                    break;
                default:
                    throw new RuntimeException("银联非法商户号->" + merchantId);
            }
        } catch (Throwable e) {
            logger.error("银联支付签名失败", e);
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "银联支付签名失败");
        }

        return submitFromMap;
    }

    /**
     * 对控件支付成功返回的结果信息中data域进行验签（控件端获取的应答信息）
     * @param jsonData json格式数据，例如：{"sign" : "J6rPLClQ64szrdXCOtV1ccOMzUmpiOKllp9cseBuRqJ71pBKPPkZ1FallzW18gyP7CvKh1RxfNNJ66AyXNMFJi1OSOsteAAFjF5GZp0Xsfm3LeHaN3j/N7p86k3B1GrSPvSnSw1LqnYuIBmebBkC1OD0Qi7qaYUJosyA1E8Ld8oGRZT5RR2gLGBoiAVraDiz9sci5zwQcLtmfpT5KFk/eTy4+W9SsC0M/2sVj43R9ePENlEvF8UpmZBqakyg5FO8+JMBz3kZ4fwnutI5pWPdYIWdVrloBpOa+N4pzhVRKD4eWJ0CoiD+joMS7+C0aPIEymYFLBNYQCjM0KV7N726LA==",  "data" : "pay_result=success&tn=201602141008032671528&cert_id=68759585097"}
     * @return 是否成功
     */
    public static boolean validateAppResponse(String jsonData, String encoding) {
        if (SDKUtil.isEmpty(encoding)) {
            encoding = "UTF-8";
        }

        Pattern p = Pattern.compile("\\s*\"sign\"\\s*:\\s*\"([^\"]*)\"\\s*");
        Matcher m = p.matcher(jsonData);
        if(!m.find()) return false;
        String sign = m.group(1);

        p = Pattern.compile("\\s*\"data\"\\s*:\\s*\"([^\"]*)\"\\s*");
        m = p.matcher(jsonData);
        if(!m.find()) return false;
        String data = m.group(1);

        p = Pattern.compile("cert_id=(\\d*)");
        m = p.matcher(jsonData);
        if(!m.find()) return false;
        String certId = m.group(1);

        try {
            // 验证签名需要用银联发给商户的公钥证书.
            return SecureUtil.validateSignBySoft(CertUtil
                .getValidateKey(certId), SecureUtil.base64Decode(sign
                .getBytes(encoding)), SecureUtil.sha1X16(data,
                encoding));
        } catch (Exception ex) {
            logger.error("验证控件支付成功返回结果信息签名异常", ex);
        }

        return false;
    }
}

package com.cheche365.cheche.soopay.payment.withdraw

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.util.RuntimeUtil
import com.cheche365.cheche.externalapi.api.soopay.SoopayQueryBalanceAPI
import com.cheche365.cheche.externalapi.api.soopay.SoopayWithdrawAPI
import com.cheche365.cheche.soopay.SoopayConstant
import com.cheche365.cheche.soopay.payment.ISoopayHandler
import com.cheche365.cheche.soopay.payment.SoopayProcessor
import com.cheche365.cheche.soopay.util.LogUtil
import com.umpay.api.paygate.v40.Mer2Plat_v40
import com.umpay.api.paygate.v40.Plat2Mer_v40
import com.umpay.api.util.DataUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by mjg on 2017/6/19.
 */
@Component
@Slf4j
class SoopayWithdrawHandler implements ISoopayHandler {

    @Autowired
    private SoopayProcessor soopayProcessor;

    @Autowired
    private SoopayWithdrawAPI withdrawAPI

    @Autowired
    private SoopayQueryBalanceAPI queryBalanceAPI


    Map<String, Object> withdraw(Map<String, String> params, User user) {
        Map<String, String> request = this.createReqMap(params);

        String result;
        def resMap;
        try {
            result = withdrawAPI.call(request)
            resMap = Plat2Mer_v40.getResData(result);
            callFront(resMap, user)
        } catch (Exception ex) {
            log.error("联动优势U付支付接口调用失败", ex);
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "联动优势提现异常，请稍候重试");
        }
        resMap
    }

    Map<String,String> callBack(HttpServletRequest request) {
        Map<String,String> param
        if (RuntimeUtil.isProductionEnv()){
            param = Plat2Mer_v40.getPlatNotifyData(request)
        } else {
            param = DataUtil.getData(request)
            log.debug("支付结果通知请求数据为:" + param)
            log.info("非生产环境签名验证")
        }
        soopayProcessor.saveSoopayWithdrawLog(param.get("order_id"), param.get("trade_no"),
            "联动优势支付交易，后台通知数据：" + LogUtil.getStrLog(param), null);
        param;
    }

    void callFront(Map<String, String> respMap, User user) {
        soopayProcessor.saveSoopayWithdrawLog(respMap.get("order_id"), respMap.get("trade_no"),
            "联动优势支付交易，同步通知数据：" + LogUtil.getStrLog(respMap), user);
    }

    Map<String, String> createReqMap(Map<String, String> busiParam) {
        Map<String, String> dataMap = new HashMap<>();
        this.createBasicData(dataMap);
        dataMap.putAll(busiParam);
        return dataMap
    }

    protected void createBasicData(Map<String, String> dataMap) {
        // 接口名称
        dataMap.put("service", SOOPAY_TXN_TYPE_05);
        // 版本号
        dataMap.put("version", SOOPAY_VERSION);
        // 字符集编码 默认"UTF-8"
        dataMap.put("charset", SOOPAY_ENCODING);
        // 签名方法 01 RSA
        dataMap.put("sign_type", SOOPAY_SIGN_METHOD);
        // 商户号码，请改成自己的商户号
        dataMap.put("mer_id", SOOPAY_MERCHANT_ID);
        // 订单发送时间，取系统时间
        dataMap.put("mer_date", dateFormat.format(new Date()));
        // 交易币种
        dataMap.put("amt_type", SOOPAY_AMT_TYPE);

        dataMap.put("notify_url", SoopayConstant.getSoopayWithdrawCallbackUrl());
    }

    void notice2Soopay(Map<String, String> dataMap, HttpServletResponse response){
        Map responseMap = new HashMap();
        def resString;
        try {
            responseMap.put("mer_id", dataMap.get("mer_id"));
            responseMap.put("version", dataMap.get("version"));
            responseMap.put("sign_type", dataMap.get("sign_type"));
            responseMap.put("order_id",dataMap.get("order_id"));
            responseMap.put("mer_date",dataMap.get("mer_date"));
            responseMap.put("ret_code","0000");

            resString = Mer2Plat_v40.merNotifyResData(responseMap);
        } catch (Exception ex) {
            log.error("联动优势异步通知返回调用失败", ex);
            responseMap.put("ret_code","1111");
            resString = Mer2Plat_v40.merNotifyResData(responseMap);
        }
        PrintWriter write = response.getWriter();
        resString = "<html><META NAME=\"MobilePayPlatform\" CONTENT=\"" + resString + "\" /></html>" ;
        write.print(resString);
        write.flush();

    }

    String queryMerBalance(){
        Map dataMap = new HashMap();
        // 接口名称
        dataMap.put("service", "query_account_balance");
        // 版本号
        dataMap.put("version", SOOPAY_VERSION);
        // 字符集编码 默认"UTF-8"
        dataMap.put("charset", SOOPAY_ENCODING);
        // 签名方法 01 RSA
        dataMap.put("sign_type", SOOPAY_SIGN_METHOD);
        // 商户号码，请改成自己的商户号
        dataMap.put("mer_id", SOOPAY_MERCHANT_ID);
        def result;
        try {
            result = queryBalanceAPI.call(dataMap)
        } catch (Exception ex) {
            log.error("联动优势支付app推送订单信息接口调用失败", ex);
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "联动优势提现异常，请稍候重试");
        }
        result
    }
}

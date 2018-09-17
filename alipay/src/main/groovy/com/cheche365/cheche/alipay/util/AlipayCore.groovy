package com.cheche365.cheche.alipay.util

import com.cheche365.cheche.alipay.dto.PayRequestDto
import com.cheche365.cheche.alipay.util.sign.MD5
import com.cheche365.cheche.alipay.util.sign.RSA
import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.model.LogType
import com.cheche365.cheche.core.model.MoApplicationLog
import com.cheche365.cheche.core.service.DoubleDBService
import com.cheche365.cheche.core.service.PaymentSerialNumberGenerator
import org.apache.commons.lang.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpServletRequest

@Service
@Transactional
class AlipayCore {

    @Autowired
    private DoubleDBService mongoDBService;

    @Autowired
    private PaymentSerialNumberGenerator paymentSerialNumberGenerator;

    private static final Logger logger = LoggerFactory.getLogger(AlipayCore.class);


    Map<String, String> paraFilter(Map<String, String> sArray) {

        Map<String, String> result = new HashMap<String, String>();

        if (sArray == null || sArray.size() <= 0) {
            return result;
        }

        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign") || key.equalsIgnoreCase("sign_type")) {
                continue;
            }
            result.put(key, value);
        }

        return result;
    }


    String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }

        return prestr;
    }


    void logResult(String outTradeNo, String sWord) {
        String debugWord = AliPayConstant.LOG_TAG + "[" + outTradeNo + "][" + DateUtils.getDateString(new Date(), DateUtils.DATE_LONGTIME24_PATTERN) + "][" + System.currentTimeMillis() + "]" + " contents{" + sWord + "}";
        logger.debug(debugWord);
        //保存日志到数据库
        payLogging(outTradeNo, debugWord);

    }



    Map<String, String> convertRequestParamToMap(HttpServletRequest request, String charSet, boolean isConvertChartSet) {
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            try {
                if (isConvertChartSet) {
                    //如果mysign和sign不相等也可以使用这段代码转化
                    valueStr = new String(valueStr.getBytes("ISO-8859-1"), charSet);
                }
                params.put(name, valueStr);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return params;
    }





    String buildWapPayRequest(Map<String, String> sParaTemp, String strMethod, String strButtonName) {
        //待请求参数数组
        Map<String, String> sPara = buildRequestPara(sParaTemp, AliPayConstant.SIGN_RSA);
        List<String> keys = new ArrayList<String>(sPara.keySet());
        StringBuffer sbHtml = new StringBuffer();
        sbHtml.append("<form id=\"alipaysubmit\" name=\"alipaysubmit\" action=\"" + AliPayConstant.ALIPAY_GATEWAY_URL
            + "_input_charset=" + AliPayConstant.INPUT_CHARSET + "\" method=\"" + strMethod
            + "\">");
        for (int i = 0; i < keys.size(); i++) {
            String name = (String) keys.get(i);
            String value = (String) sPara.get(name);
            sbHtml.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
        }
        if (StringUtils.isNotBlank(strButtonName)) {
            sbHtml.append("<input type=\"submit\" value=\"" + strButtonName + "\" style=\"display:none;\"></form>");
        }
        return sbHtml.toString();
    }


    String buildMobilePayRequest(PayRequestDto payRequestDto) {

        StringBuffer payInfoBuf = new StringBuffer();
        // 签约合作者身份ID
        payInfoBuf.append("partner=").append("\"").append(AliPayConstant.PARTNER).append("\"");
        // 签约卖家支付宝账号
        payInfoBuf.append("&seller_id=").append("\"").append(AliPayConstant.SELLER_EMAIL).append("\"");
        // 商户网站唯一订单号
        payInfoBuf.append("&out_trade_no=").append("\"").append(payRequestDto.getOutTradeNo()).append("\"");
        // 商品名称
        payInfoBuf.append("&subject=").append("\"").append(payRequestDto.getOutTradeNo()).append("\"");
        // 商品详情
        payInfoBuf.append("&body=").append("\"").append(payRequestDto.getBody()).append("\"");
        // 商品金额
        payInfoBuf.append("&total_fee=").append("\"").append(payRequestDto.getTotalFee()).append("\"");
        // 服务器异步通知页面路径
        payInfoBuf.append("&notify_url=").append("\"").append(AliPayConstant.getIosPayNotifyUrl()).append("\"");
        // 服务接口名称
        payInfoBuf.append("&service=").append("\"").append(AliPayConstant.MOBILE_SERVICE).append("\"");
        // 支付类型
        payInfoBuf.append("&payment_type=").append("\"").append(AliPayConstant.PAYMENT_TYPE).append("\"");
        // 参数编码
        payInfoBuf.append("&_input_charset=").append("\"").append(AliPayConstant.INPUT_CHARSET).append("\"");
        //支付时间
        payInfoBuf.append("&it_b_pay=").append("\"").append(AliPayConstant.PAY_TIME).append("\"");

        String paramBySign = payInfoBuf.toString();
        String sign = RSA.sign(paramBySign, AliPayConstant.RSA_PRI_KEY, AliPayConstant.INPUT_CHARSET);
        try {
            sign = URLEncoder.encode(sign, AliPayConstant.INPUT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        payInfoBuf.append("&sign=\"").append(sign).append("\"").append("&sign_type=").append("\"").append(AliPayConstant.SIGN_RSA).append("\"");
        return payInfoBuf.toString();
    }


    Map<String, String> buildRequestPara(Map<String, String> sParaTemp, String signType) {
        //除去数组中的空值和签名参数
        Map<String, String> sPara = paraFilter(sParaTemp);
        //生成签名结果
        String mysign = buildRequestMysign(sPara, signType);

        //签名结果与签名方式加入请求提交参数组中
        sPara.put("sign", mysign);
        sPara.put("sign_type", signType);

        return sPara;
    }


    String buildRequestMysign(Map<String, String> sPara, String signType) {
        String prestr = createLinkString(sPara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        String mysign = "";
        if (signType.equals(AliPayConstant.SIGN_MD5)) {
            mysign = MD5.sign(prestr, AliPayConstant.KEY, AliPayConstant.INPUT_CHARSET);
        } else if (signType.equals(AliPayConstant.SIGN_RSA)) {
            mysign = RSA.sign(prestr, AliPayConstant.RSA_PRI_KEY, AliPayConstant.INPUT_CHARSET);
        }
        //请求时记录日志
        String outTradeNo = sPara.get("out_trade_no");
        String sWord = "发出支付请求信息\n outTradeNo=" + outTradeNo + "\n requestTxt=" + prestr + "\n mysign=" + mysign;
        logResult(outTradeNo, sWord);

        return mysign;
    }


    String getParameter(HttpServletRequest request, String key) {
        String value = "";
        value = request.getParameter(key);
        value = null==value?"":value;
        try {
            value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return value;
    }


    PayRequestDto getPayRequestDto(Map prePayParam) {
        PayRequestDto payRequestDto = new PayRequestDto();
        payRequestDto.body = prePayParam.orderNo
        payRequestDto.outTradeNo = prePayParam.serialNumber
        payRequestDto.totalFee = prePayParam.amount as String
        return payRequestDto;
    }

    /**
     * 将日志保存数据库
     *
     * @param outTradeNo
     * @param logString
     */
    private void payLogging(String outTradeNo, String logString) {
        MoApplicationLog log = new MoApplicationLog();
        log.setCreateTime(Calendar.getInstance().getTime());
        log.setInstanceNo(outTradeNo);
        log.setObjId(outTradeNo);
        log.setLogMessage(logString);
        log.setObjTable("purchase_order");
        log.setLogType(LogType.Enum.ORDER_RELATED_3);
        mongoDBService.saveApplicationLog(log);
    }
}

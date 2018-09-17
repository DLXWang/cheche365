import com.cheche365.cheche.alipay.app.config.AliPayConfig
import com.cheche365.cheche.alipay.util.AliPayConstant
import com.cheche365.cheche.alipay.util.AlipayCore
import com.cheche365.cheche.core.app.config.CoreConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification
import java.awt.Desktop

import java.text.SimpleDateFormat

@WebAppConfiguration
@ContextConfiguration( classes = [ CoreConfig, AliPayConfig ] )
class RefundFT extends Specification {

    @Autowired
    protected AlipayCore alipayCore;

    /**
     * 支付宝退款测试
     * @return
     */
    def "alipay refund test"() {


        given: "订单保单数据"

        //目标用于验证和结果是否相等
        String expectedVal = "<form id=\"alipaysubmit\" name=\"alipaysubmit\" action=\"https://mapi.alipay.com/gateway.do?_input_charset=utf-8\" method=\"get\"><input type=\"hidden\" name=\"seller_email\" value=\"zfb@cheche365.com\"/><input type=\"hidden\" name=\"seller_user_id\" value=\"2088911040184910\"/><input type=\"hidden\" name=\"batch_no\" value=\"201608310001\"/><input type=\"hidden\" name=\"partner\" value=\"2088911040184910\"/><input type=\"hidden\" name=\"service\" value=\"refund_fastpay_by_platform_pwd\"/><input type=\"hidden\" name=\"_input_charset\" value=\"utf-8\"/><input type=\"hidden\" name=\"sign\" value=\"gTN3FVth8mOXEDvKyqCJFFnlc72KOjERqHwePnvVgIjsUlFNrO7HZ4NK7JDG7N6aYlpGg1zmL4L/7jfiXvbVoHDbiu+8lqIMi8mObPVFjs9id9WhckImLcjDnLzUfvGZ0jmgu+BgTfAii9Ey1MzT0/kI6xDyTxGy5DsiracVNBU=\"/><input type=\"hidden\" name=\"notify_url\" value=\"http://106.2.164.178:8085/web/alipay/wappay/notify\"/><input type=\"hidden\" name=\"batch_num\" value=\"1\"/><input type=\"hidden\" name=\"sign_type\" value=\"RSA\"/><input type=\"hidden\" name=\"refund_date\" value=\"2016-08-31 14:34:51\"/><input type=\"hidden\" name=\"detail_data\" value=\"2016083121001004640251263152^0.01^退款\"/><input type=\"submit\" value=\"confirm\" style=\"display:none;\"></form><script>document.forms['alipaysubmit'].submit();</script>";

        when: "转换数据格式"
        def result = alipayCore.buildWapPayRequest(getRequestMap(true), "get", "confirm");
        result = result+"<script>document.forms['alipaysubmit'].submit();</script>"
        def File file= new File("d:/test.html");//目标文件用于把html页面打印到该文件后面会打开该文件
        if(!file.exists()){//是否存在该文件没有则穿件(提示如果本地有该文件请确认之前的文件是否用用)
            file.mkdirs();
        }
        PrintStream ps = new PrintStream(new FileOutputStream(file));//输出流
        ps.println("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><title>支付宝即时到账批量退款有密接口</title></head>");// 往文件里写入字符串
        ps.append("<body></body>");
        def outResult = alipayCore.buildWapPayRequest(getRequestMap(false), "get", "confirm");
        outResult = outResult+"<script>document.forms['alipaysubmit'].submit();</script>"
        ps.append(outResult);
        Desktop.getDesktop().browse(file.toURI());//打开文件

        then: "校验格式"
        expectedVal.equals(result);//判断目标和结果是否相等


    }

    /**
     * 状态值 用于判断和预期值比较的特定字符串的时间还是调用结构的当前时间
     * @param bol
     * @return
     */
    private Map<String, String> getRequestMap(def bol) {
        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sim1 = new SimpleDateFormat("yyyyMMdd");//批次号使用
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("service", "refund_fastpay_by_platform_pwd");//接口名 退款接口名称
        sParaTemp.put("partner", AliPayConstant.PARTNER);//合作者身份id
        sParaTemp.put("_input_charset", AliPayConstant.INPUT_CHARSET);//编码
        sParaTemp.put("notify_url", AliPayConstant.WAPPAY_NOTIFY_URL);//回调地址
        /*
        邮件和卖家id二选1，以卖家id为准
         */
        sParaTemp.put("seller_email", "zfb@cheche365.com");//邮件
        sParaTemp.put("seller_user_id", AliPayConstant.PARTNER);//卖家id
        //退款请求时间
        if(bol) {
            sParaTemp.put("refund_date", "2016-08-31 14:34:51");
        }else {
            sParaTemp.put("refund_date", sim.format(new Date()));
        }
        /*
        批次号生成规则为时间如(20160101)加上(3~24位流水号)
         */
        sParaTemp.put("batch_no", sim1.format(new Date())+"0001");//批次号
        sParaTemp.put("batch_num", "1");//批次号里面的退款的数量 退款的数据集被#号分割的数量)
        sParaTemp.put("detail_data", "2016083121001004640251263152^0.01^退款");//退款的数据集(批次号^退款金额^备注#批次号^退款金额^备注#.....)
        return sParaTemp;
    }
}

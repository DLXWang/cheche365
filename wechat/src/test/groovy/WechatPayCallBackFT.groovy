import com.cheche365.cheche.core.app.config.CoreConfig
import com.cheche365.cheche.wechat.app.config.WechatConfig
import com.cheche365.cheche.wechat.payment.OrderPaymentManager
import com.cheche365.cheche.wechat.util.XStreamUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

@WebAppConfiguration
@ContextConfiguration(classes = [CoreConfig, WechatConfig])
class WechatPayCallBackFT extends Specification {

    @Autowired
    private OrderPaymentManager orderPaymentManager;

    def "微信支付回调测试"() {

        given:

        String requestBody = '''<xml><appid><![CDATA[wxcf02994504264506]]></appid><bank_type><![CDATA[CCB_DEBIT]]></bank_type><cash_fee><![CDATA[9915]]></cash_fee><coupon_count><![CDATA[1]]></coupon_count><coupon_fee>55</coupon_fee><coupon_fee_0><![CDATA[55]]></coupon_fee_0><coupon_id_0><![CDATA[2000000000185503040]]></coupon_id_0><fee_type><![CDATA[CNY]]></fee_type><is_subscribe><![CDATA[N]]></is_subscribe><mch_id><![CDATA[1232068902]]></mch_id><nonce_str><![CDATA[HKYXWZVTDYSQMQLJWNPFWU7ABTZ0GXZZ]]></nonce_str><openid><![CDATA[ogvRMs1eeupTfvYR-eeFUhUPOFx0]]></openid><out_trade_no><![CDATA[I20170531001287Z0011]]></out_trade_no><result_code><![CDATA[SUCCESS]]></result_code><return_code><![CDATA[SUCCESS]]></return_code><sign><![CDATA[D4B701E11854CF43EDF55C3B422D4DF3]]></sign><time_end><![CDATA[20170531144526]]></time_end><total_fee>9970</total_fee><trade_type><![CDATA[JSAPI]]></trade_type><transaction_id><![CDATA[4008102001201705313510015456]]></transaction_id></xml>'''

        when:

        Map<String, Object> response = XStreamUtil.parseToMap(requestBody);

        println(response)

        orderPaymentManager.processOrderQueryResponse(response);

        then:

        true
    }
}

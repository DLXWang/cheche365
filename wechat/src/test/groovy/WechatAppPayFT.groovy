import com.cheche365.cheche.core.app.config.CoreConfig
import com.cheche365.cheche.wechat.MessageSender
import com.cheche365.cheche.wechat.TradeType
import com.cheche365.cheche.wechat.app.config.WechatConfig
import com.cheche365.cheche.wechat.message.UnifiedOrderRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

/**
 * Created by chenqc on 2016/12/13.
 */

@WebAppConfiguration
@ContextConfiguration( classes = [CoreConfig,WechatConfig] )
class WechatAppPayFT extends Specification {

    @Autowired
    MessageSender sender;

    def "小程序统一支付接口参数测试"() {

        given:
        UnifiedOrderRequest request = new UnifiedOrderRequest('out_trade_no':'T201609270000041Z00113',
            'openid':'onRHq0CXbppJgt7cIeV8ztmPyBsw', 'body':'车险服务订单', 'attach':null, 'total_fee':707405,
            'trade_type':TradeType.JSAPI, 'appid':'wxd1da98ee5dfe2dc3','mch_id':'1404828902','nonce_str':'NSJJODMPFF1I2RHZGUFH35M83NZHUZCZ',
            'spbill_create_ip':'106.39.200.47','time_start':'20161212172843',
            'notify_url':'http://dev1.cheche365.com/web/wechat/payment/callback','product_id':'T201609270000041Z00113',
        );
        when:
        def message = sender.postPayMessage("pay/unifiedorder", new HashMap<String, Object>(), request)
        then:
        println message
        true
    }
}

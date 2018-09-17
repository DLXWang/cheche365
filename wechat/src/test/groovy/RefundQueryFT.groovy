import com.cheche365.cheche.core.app.config.CoreConfig
import com.cheche365.cheche.wechat.MessageSender
import com.cheche365.cheche.wechat.app.config.WechatConfig
import com.cheche365.cheche.wechat.message.RefundQueryRequest
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

/**
 * Created by zhengwei on 12/9/16.
 */

@WebAppConfiguration
@ContextConfiguration( classes = [CoreConfig,WechatConfig] )
class RefundQueryFT extends Specification {

    @Autowired
    MessageSender sender;

    def "微信退款查询测试"() {

        given:
        def nonceStr = RandomStringUtils.randomAlphanumeric(32).toUpperCase();
        //从payment表中可以找到appid mch_id和out_refund_no三个参数值，其中out_refund_no就是payment中的out_trade_no
        def request = new RefundQueryRequest('appid': 'wx8f215a7f9024eafe', 'mch_id':'1243723002', 'nonce_str' : nonceStr, 'out_refund_no':'I20161125007655T003')
        when:
        sender.postPayMessage('pay/refundquery', null, request)
        then:
        true
    }
}

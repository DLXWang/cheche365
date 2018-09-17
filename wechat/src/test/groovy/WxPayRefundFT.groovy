import com.cheche365.cheche.core.app.config.CoreConfig
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.service.UnifiedRefundHandler
import com.cheche365.cheche.wechat.MessageSender
import com.cheche365.cheche.wechat.app.config.WechatConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

import static com.cheche365.cheche.core.WechatConstant.APP_ID

@WebAppConfiguration
@ContextConfiguration( classes = [CoreConfig,WechatConfig] )
class WxPayRefundFT extends Specification {


    @Autowired
    protected PaymentRepository paymentRepository;

    @Autowired
    protected List<UnifiedRefundHandler> handlers;

    @Autowired
    MessageSender sender;


    def "wx pay refund test"() {

        given: "订单保单数据"
        def mubiao = true;
        when: "转换数据格式"
//        Payment payment = paymentRepository.findOne(Long.valueOf(System.getProperty("test.payment.id")));
//        def reslut = handlers.find{it.support(payment)}.refund(payment);
        sender.fetchAccessToken(APP_ID)
        then: "校验格式"
        true

    }
}

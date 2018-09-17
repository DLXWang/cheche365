import com.cheche365.cheche.alipay.app.config.AliPayConfig
import com.cheche365.cheche.core.app.config.CoreConfig
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.service.UnifiedRefundHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

/**
 * Created by Administrator on 2016/10/17 0017.
 */

@WebAppConfiguration
@ContextConfiguration( classes = [ CoreConfig, AliPayConfig ] )
class RefundNoPassFT extends Specification{

    @Autowired
    private UnifiedRefundHandler refundHandler;

    @Autowired
    private PaymentRepository paymentRepository;

    def "alipay nopass refund test"() {
        given: "订单保单数据"

        when: "转换数据格式"
        boolean bol = refundHandler.refund(paymentRepository.findOne(Long.valueOf(System.getProperty("test.payment.id"))))

        then: "校验格式"
        bol==true

    }
}

package com.cheche365.cheche.soopay.prePay

import com.cheche365.cheche.core.app.config.CoreConfig
import com.cheche365.cheche.soopay.app.config.SoopayConfig
import com.cheche365.cheche.soopay.payment.withdraw.SoopayWithdrawHandler
import com.cheche365.cheche.test.common.ASpockSpecification
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

/**
 * Created by wangxin on 2017/6/20.
 */
@ContextConfiguration(classes = [SoopayConfig, CoreConfig])
@Slf4j
class SoopayWithdrawFT extends ASpockSpecification {

    @Autowired
    private SoopayWithdrawHandler withdrawHandler;


    def '测试soopay的支付请求接口'() {
        given:

        when:
            Map map = new HashMap()
            map.put("order_id", "R17062310154482409")
            map.put("amount",1)
            map.put("recv_account_type","00")
            map.put("recv_bank_acc_pro","0")
            map.put("recv_account", "6228480402564890018")
            map.put("recv_user_name","吴同册")
            map.put("recv_gate_id","CCB")
            map.put("purpose","车车钱包提现")
            map.put("bank_brhname","")

            def result = withdrawHandler.prePay(map)


        then:
            result

    }


}

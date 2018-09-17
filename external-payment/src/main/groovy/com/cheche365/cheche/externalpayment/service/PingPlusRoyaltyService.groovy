package com.cheche365.cheche.externalpayment.service

import com.cheche365.cheche.core.model.PingPlusAppSupport
import com.cheche365.cheche.core.repository.PingPlusAppSupportRepository
import com.cheche365.cheche.externalpayment.constants.PingPlusConstant
import com.cheche365.cheche.externalpayment.pingplus.RefundModeType
import com.cheche365.cheche.externalpayment.pingplus.RoyaltyModeType
import com.pingplusplus.model.RoyaltySettlement
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@Slf4j
class PingPlusRoyaltyService {

    @Autowired
    PingPlusAppSupportRepository pingPlusAppSupportRepository
    /**
     * 分润结算
     * @param params
     * @return
     */
    def royalty(Map<String, Object> params){
        /*try{
            Map<String,Object> createParams = [
                payer_app : PingPlusConstant.APP_ID,
                method : "unionpay",//TODO 日志报错，不应该写死银联支付
                source_no : params.outTradeNo
            ]
            log.info("create royalty settlement params:{}",createParams)
            RoyaltySettlement.create(createParams)
        }catch (Exception e){
            log.error("create royalty settlement error : {} ",e.message)
        }*/
    }

    /**
     * 根据配置计算分润信息
     * @param params
     * @param isRoyalty
     * @return
     */
    def calculateRoyalty(Map<String, Object> params,boolean isRoyalty){

        String amount = isRoyalty?"amount":"amount_refunded"
        List<Map> royalty_users =new ArrayList<>()
        /*PingPlusAppSupport pingPlusAppSupports = pingPlusAppSupportRepository.findFirstByArea(params.area)
        if(pingPlusAppSupports){
            Map royalty_user = new HashMap<>()
            royalty_user.put("user",pingPlusAppSupports.pingPlusApp.user)
            royalty_user.put(amount,calculate(pingPlusAppSupports,params.amount,isRoyalty))
            royalty_users.add(royalty_user)
            log.info("royalty users result:{},isRoyalty:{}",royalty_users,isRoyalty)
        }*/
        royalty_users?.size()>0 ? royalty_users : null
    }

    private int calculate(PingPlusAppSupport support,double amount,boolean isRoyalty){
        int result = 0
        if(isRoyalty){
            result = support.royaltyMode.equalsIgnoreCase(RoyaltyModeType.RATE.toString())?(support.royaltyValue*(amount*100)).intValue():(support.royaltyValue*100).intValue()
        }else{
            if(support.refundMode?.equalsIgnoreCase(RefundModeType.PROPORTIONAL.toString())){
                result = (support.refundValue*(amount*100)).intValue()
            }else if(support.refundMode?.equalsIgnoreCase(RefundModeType.FULL_REFUND.toString())){
                result = (amount*100).intValue()
            }else{
                result = 0
            }
        }
        result
    }
}

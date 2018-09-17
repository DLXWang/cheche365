package com.cheche365.cheche.web

import com.cheche365.cheche.core.app.config.CoreConfig
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.repository.PartnerUserRepository
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.WebPurchaseOrderRepository
import com.cheche365.cheche.core.service.PurchaseOrderIdService
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.web.app.config.WebcoreConfig
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

/**
 * Created by zhengwei on 4/22/16.
 */

@WebAppConfiguration
@ContextConfiguration( classes = [ CoreConfig, WebcoreConfig ] )
@Slf4j
@Newify([StringBuffer, ConfigSlurper, JsonSlurper])
abstract class WebFT extends Specification {

    @Autowired
    PurchaseOrderIdService idService;

    @Autowired
    WebPurchaseOrderRepository purchaseOrderRepository

    @Autowired
    PaymentRepository paymentRepository

    def randomGenerator = new Random();

    @Autowired
    PartnerUserRepository partnerUserRepository;

    def setupSpec() {
        (this.class.simpleName =~ /([A-Z|\d]+)/).with { m ->
            def sb = StringBuffer()
            m.each { appendReplacement(sb, /_${it[0].toLowerCase()}/) }
            appendTail sb
            def cf = "${sb[1..-1]}.json"
            def mainCfURL = this.class.getResource cf
            def mainCf = mainCfURL ? JsonSlurper().parse(mainCfURL) : null
            if(mainCf){
                mainCf.each {
                    it.input = parseJsonFile(it.input.file, it.input.clazz)
                    it.output = parseJsonFile(it.output.file, it.output?.clazz)
                }
            }


            doSetupSpec mainCf
        }
    }

    def parseJsonFile(fileName, clazz){
        def fileUrl = this.class.getResource(fileName)
        clazz ?
            CacheUtil.doJacksonDeserialize(fileUrl.text, Class.forName(clazz)) :
            fileUrl.text
    }

    protected void doSetupSpec(config) {}

    def PurchaseOrder nextOrder(){

        def order = getRandomPO()
        order
    }

    def order(orderNo) {
        return purchaseOrderRepository.findFirstByOrderNo(orderNo)
    }

    def payment(PurchaseOrder order){
        return paymentRepository.findByPurchaseOrder(order)
    }

    def PurchaseOrder getRandomPO(){

        def List<PurchaseOrder> pos = purchaseOrderRepository.getTop10ByChannel(13)
        if(pos.empty) {
            throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "purchase_order表中无可用数据，请配置")
        }
        pos.get(randomGenerator.nextInt(pos.size()))
    }

    def toJSON(Object obj) {
        return CacheUtil.doJacksonSerialize(obj)
    }

    def toJSON(JsonSerializer serializer, Object obj){

        return CacheUtil.doJacksonSerialize(obj, serializer)
    }

    def compareJSON(String result, String expected){
        ObjectMapper mapper = new ObjectMapper()

        def resultObj = mapper.readTree(result)
        def expectedObj = mapper.readTree(expected)

        return resultObj.equals(expectedObj)
    }

    def compareJSON(String result, String expected, Closure preHandle){
        ObjectMapper mapper = new ObjectMapper()

        def resultObj = mapper.readTree(result)
        def expectedObj = mapper.readTree(expected)

        preHandle(resultObj)
        preHandle(expectedObj)

        return resultObj.equals(expectedObj)
    }

}

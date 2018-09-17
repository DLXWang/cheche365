package com.cheche365.cheche.web

import com.cheche365.cheche.core.serializer.ArrayQRSerializer
import spock.lang.Shared

/**
 * Created by zhengwei on 4/27/16.
 */
class QrSerializerFT extends WebFT {

    @Shared
    private testData

    void doSetupSpec(config){
        testData = config
    }


    def "serialize quote record test"() {

        when: "转换数据格式"
        println('='.multiply(20) + desc + '='.multiply(20))
        def result = toJSON(new ArrayQRSerializer(), quoteRecord)
        println("expected:\n" + expected + "\nresult:\n" + result)

        then: "校验格式"
        compareJSON(result, expected) {it.insuranceCompany.logoUrl=null}

        where :
        [desc, quoteRecord, expected] << testData.collect{[it.desc, it.input, it.output]}

    }

}

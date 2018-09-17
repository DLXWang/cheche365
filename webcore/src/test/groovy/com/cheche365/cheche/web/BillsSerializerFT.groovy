package com.cheche365.cheche.web

import com.cheche365.cheche.core.serializer.ArrayBillsSerializer
import spock.lang.Shared

/**
 * Created by zhengwei on 4/28/16.
 */

class BillsSerializerFT extends WebFT {

    @Shared
    private testData

    void doSetupSpec(config){
        testData = config
    }

    def "serialize bills test"(){

        when: "转换格式"
        println('='.multiply(20) + desc + '='.multiply(20))
        def result = toJSON(new ArrayBillsSerializer(), bills)
        println("expected:\n" + expected + "\nresult:\n" + result)


        then: "校验格式"
        compareJSON(result, expected)

        where :
        [desc, bills, expected] << testData.collect{[it.desc, it.input, it.output]}

    }

}

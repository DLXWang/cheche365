package com.cheche365.cheche.test.core

import com.cheche365.cheche.core.exception.handler.LackOfSupplementInfoHandler
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.AutoType
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.QuoteSupplementInfo
import com.cheche365.cheche.core.model.SupplementInfo
import com.cheche365.cheche.core.serializer.SerializerUtil
import com.cheche365.cheche.test.core.common.QuoteQuery
import spock.lang.Specification
import spock.lang.Unroll

import java.text.SimpleDateFormat

/**
 * Created by zhengwei on 2/17/17.
 */
class LackOfSupplementInfoHandlerFT extends Specification {

    @Unroll
    def "write补充信息格式转换测试"() {

        given:
        def errorObject = [
            [
                "fieldType" : "date",
                "fieldLabel" : "车辆注册日期",
                "validationType" : "enroll-date",
                "fieldPath" : "supplementInfo.enrollDate",
                "phase" : "quoting"
            ]

        ]

        expect:
        def result = LackOfSupplementInfoHandler.writeResponse(errorObject, checker)
        result[0].fieldPath == expectedPath

        where:
                checker   |  expectedPath
                 null     |  'additionalParameters.supplementInfo.enrollDate'
    }



    @Unroll    //Unroll 会当成2个方法在运行
    def "uniqueString解析测试"(){
        given:

        expect:

        def result =SerializerUtil.resolveUniqueString(uniqueString)
            result.get('thirdPartyAmount')==res

        where:
             uniqueString                               |    res
             '11050100101010012110110100'           |   500000
             '00010000002000000000000000'           |   100000

    }






    def "read补充信息格式转换测试"(){
        given:

        def inputData = new QuoteQuery()
        inputData.additionalParameters = [supplementInfo : [:]]
        inputData.additionalParameters.supplementInfo.autoModel = 'DZAAI0020'
        inputData.additionalParameters.supplementInfo.enrollDate = '2012-01-09'

        def oldInputData = new QuoteQuery()
        oldInputData.auto = new Auto()
        oldInputData.auto.autoType = new AutoType()
        oldInputData.auto.autoType.supplementInfo = new SupplementInfo()
        oldInputData.auto.autoType.supplementInfo.autoModel = 'DZAAI0020'

        when:
        LackOfSupplementInfoHandler.readRequest(inputData)
        LackOfSupplementInfoHandler.readRequest(oldInputData)

        then:
        inputData.auto.enrollDate == new SimpleDateFormat('yyyy-MM-dd').parse('2012-01-09')
        inputData.supplementInfo.enrollDate == new SimpleDateFormat('yyyy-MM-dd').parse('2012-01-09')
        inputData.supplementInfo.autoModel == 'DZAAI0020'
        oldInputData.supplementInfo.autoModel == 'DZAAI0020'
    }

    def "C端格式转换测试"() {
        given:
        def inputData = new QuoteQuery()
        inputData.additionalParameters = [supplementInfo : [:]]
        inputData.additionalParameters.supplementInfo.autoModel = 'DZAAI0020'
        inputData.additionalParameters.supplementInfo.enrollDate = '2012-01-09'
        inputData.auto = new Auto()
        inputData.auto.owner = '常洁'

        when:
        def result = LackOfSupplementInfoHandler.transformFormat(inputData, Channel.Enum.WAP_8)

        then:
        result instanceof List
    }

    def "出单中心补充信息格式测试"() {
        given:

        def info = [
            new QuoteSupplementInfo(id:1, fieldPath: 'additionalParameters.supplementInfo.enrollDate', value: '2010-01-09'),
            new QuoteSupplementInfo(id:2, fieldPath: 'additionalParameters.supplementInfo.autoModel', valueName: 'DZAAI0020'),

        ]

        when:
        def result = LackOfSupplementInfoHandler.toOrderCenterFormat(info)

        then:
        result.size() == 2
        result[0].label == '车辆注册日期'
        result[0].value == '2010-01-09'
        result[0].fieldPath == 'additionalParameters.supplementInfo.enrollDate'
        result[1].label == '车型列表'
        result[1].value == 'DZAAI0020'
        result[1].fieldPath == 'additionalParameters.supplementInfo.autoModel'
    }






}

package com.cheche365.cheche.externalpayment

import groovy.json.JsonSlurper
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by zhengwei on 4/15/17.
 */
class InputFileSpec extends Specification{

    @Shared
    def input
    final setupSpec() {
        input =  new JsonSlurper().parseText(InputFileSpec.class.getResource("${getClass().simpleName}.json").text)
    }
}

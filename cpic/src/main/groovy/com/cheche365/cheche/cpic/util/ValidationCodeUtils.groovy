package com.cheche365.cheche.cpic.util

import groovy.util.logging.Slf4j

/**
 * Created by yuhao on 2015/10/21.
 */
@Slf4j
class ValidationCodeUtils {
    static final OPERATOR_LIST = ['+', '-', '*', '/', '加', '减', '乘', '除']
    static final OPERATOR_MAP = [
    '+' : '+', 加 : '+',
    '-' : '-', 减 : '-',
    '*' : '*', 乘 : '*',
    '/' : '/', 除 : '/'
    ]
    static final OPERAND_MAP = [
    '零': 0, '0': 0,
    '一': 1, '壹': 1, '1': 1,
    '二': 2, '贰': 2, '2': 2,
    '三': 3, '叁': 3, '3': 3,
    '四': 4, '肆': 4, '4': 4,
    '五': 5, '伍': 5, '5': 5,
    '六': 6, '陆': 6, '6': 6,
    '七': 7, '柒': 7, '7': 7,
    '八': 8, '捌': 8, '8': 8,
    '九': 9, '玖': 9, '9': 9,
    '十': 10, '拾': 10, '10': 10]

    static decodeValidationCode(expression) {
        try{
            def question = ''
            def answer = ''
            def y = expression =~ /(.*)(?:等于|=)(?:.*请选择：)(.*)/
            if (y.matches()) {
                question = y[0][1]
                answer = y[0][2]
            }
            def questionSnippet = question.toCharArray() as List
            def operatorCharacter = OPERATOR_LIST.intersect(questionSnippet)[0] as String
            def operator = OPERATOR_MAP[operatorCharacter]
            def (operand1, operand2) = question.tokenize(operatorCharacter).with { left, right ->
                [convertOperand(left), convertOperand(right)]
            }
            def options = answer.tokenize(';').collectEntries { singleAnswer ->
                singleAnswer.tokenize().with { option, value ->
                    [(value) : option]
                }
            }
            def gs = new GroovyShell()
            def result = gs.evaluate("$operand1 $operator $operand2") as String
            def validationCode = options[result]
            log.info("成功破解验证码：" + expression + "答案是：" + validationCode)
            validationCode
        }
        catch(Exception e){
            log.error('暂时还未支持的验证码表达式为：' + expression)
            'A'
        }
    }

    static convertOperand(operand){
        StringBuffer sb = new StringBuffer()
        for(char operandSnippet : operand.toCharArray()){
            sb.append OPERAND_MAP[operandSnippet as String]
        }
        sb.toString()
    }
}

package com.cheche365.cheche.chinalife.flow.step

import com.cheche365.cheche.common.flow.IStep

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static java.lang.Character.isDigit

/**
 * 根据国寿财前端逻辑，判断车牌能否投保
 * 1 判断出租车能不能投保
 * 2 判断外地车能不能投保
 */
class CheckLicensePlateNo implements IStep {

    @Override
    run(Object context) {
        def licensePlateNo = context.auto.licensePlateNo
        def params = context.carVerify
        def ecdemicCar = params.UIecdemicCar //外地车不允许投保
        def ecdemicCarFirstStep = params.UIecdemicCarFirstStep //外地车第一步转电销
        def newCarWhetherChoiceFlag = params.UInewCarWhetherChoiceFlag
        /**
         * 可能出现的值：
         * 渝||渝BT,渝CT,渝DT,渝ET,渝FT,渝GT,渝HT,渝IT,渝JT,渝KT,渝LT,渝MT,渝NT,渝OT,渝PT,渝QT,渝RT,渝ST,渝TT,渝UT,渝VT,渝WT,渝XT,渝YT,渝ZT
         * 苏A,苏L,苏K,苏J,苏H,苏G,苏F,苏E,苏D,苏C,苏B,苏N,苏M
         */
        def newCarWhetherChoiceFlagMessage = params.UInewCarWhetherChoiceFlagMessage?.tokenize('||')?.first()?.tokenize(',')

        def taxiJudge = params.UITaxiJudge //是否校验出租车
        def taxiJudgeType = params.UITaxiJudgeType
        def taxiJudgeTypeMessage = params.UITaxiJudgeTypeMessage //出租车开头牌号

        def taxiRejected, ecdemicRejected
        if ('1' == taxiJudge && taxiJudgeType in ['1', '2']) {
            /**
             * 出租车车牌模式有如下可能：
             * 赣*X****
             * 皖A8*****,皖AT*#***,皖Q8*****,皖A**T***
             *
             * 目前根据js规则的分析：模式有八位，#代表数字，*代表任意字符，安位匹配即可 (2016-09-07)
             */
//            taxiRejected = taxiJudgeTypeMessage?.tokenize(',')?.collect { lpn ->
//                def lpnPattern = lpn.tokenize('#*')
//                1 == lpnPattern.size() ? lpnPattern[0] : lpn.replace('#', '*')
//            }?.any {
//                if (it.contains('*')) {
//                    def same = true
//                    it.eachWithIndex { item, i ->
//                        if (licensePlateNo.size() > i && licensePlateNo[i] != item && '*' != item) {
//                            same = false
//                            DONE
//                        }
//                    }
//                    same
//                } else {
//                    licensePlateNo.contains(it)
//                }
//            }
            taxiRejected = taxiJudgeTypeMessage?.tokenize(',')?.any {
                def same = true
                it.eachWithIndex { item, i ->
                    if (same && licensePlateNo.size() > i && '*' != item) { // * 任意字符
                        if (('#' == item && !isDigit(licensePlateNo[i] as char))  // # 数字
                            || ('#' != item && licensePlateNo[i] != item)) { // 字符相等
                            same = false
                        }
                    }
                }
                same
            }

            if ('0' == ecdemicCar || '1' == ecdemicCarFirstStep) {
                if ('2' == newCarWhetherChoiceFlag) {
                    ecdemicRejected = !newCarWhetherChoiceFlagMessage?.any {
                        licensePlateNo.contains(it)
                    }
                }
            }

            if (taxiRejected || ecdemicRejected) {
                getFatalErrorFSRV '此车牌不能投保'
            } else {
                getContinueFSRV true
            }
        } else {
            getContinueFSRV true
        }

    }
}

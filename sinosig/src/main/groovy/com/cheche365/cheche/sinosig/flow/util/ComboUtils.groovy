package com.cheche365.cheche.sinosig.flow.util

import groovy.util.logging.Slf4j

import static com.cheche365.cheche.parser.Constants._DAMAGE
import static com.cheche365.cheche.parser.Constants._DRIVER_AMOUNT
import static com.cheche365.cheche.parser.Constants._ENGINE
import static com.cheche365.cheche.parser.Constants._GLASS
import static com.cheche365.cheche.parser.Constants._INSURANCE_MAPPINGS
import static com.cheche365.cheche.parser.Constants._PASSENGER_AMOUNT
import static com.cheche365.cheche.parser.Constants._SCRATCH_AMOUNT
import static com.cheche365.cheche.parser.Constants._SPONTANEOUS_LOSS
import static com.cheche365.cheche.parser.Constants._THEFT
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_AMOUNT
import static com.cheche365.cheche.parser.Constants._UNABLE_FIND_THIRDPARTY
import static com.cheche365.cheche.parser.util.BusinessUtils.adjustInsureAmount

/**
 * 险种套餐工具类
 */
@Slf4j
class ComboUtils {

    /**
     *  kindFlag里面的第一个数字代表主险（1）还是附加险（2），第二个数字代表IOP上（1）还是不上（0）
     */
    static final _KIND_ITEM_CONFIG = [
        (_DAMAGE)            : [
            kindCode   : 'A',
            kindName   : '车辆损失险',
            newKindName: '机动车损失保险',
            kindFlag   : [withIOP : ' 1  1 ', withoutIOP : ' 1  0 '],
        ],
        (_THIRD_PARTY_AMOUNT): [
            kindCode   : 'B',
            kindName   : '商业第三者责任险',
            newKindName: '商业第三者责任险',
            kindFlag   : [withIOP : ' 1  1 ', withoutIOP : ' 1  0 '],
        ],

        (_THEFT)             : [
            kindCode   : 'G1',
            kindName   : '全车盗抢险',
            newKindName: '全车盗抢保险',
            kindFlag   : [withIOP : ' 1  1 ', withoutIOP : ' 1  0 '],
        ],

        (_DRIVER_AMOUNT)     : [
            kindCode   : 'D3',
            kindName   : '司机座位责任险',
            newKindName: '车上人员责任保险(驾驶员)',
            kindFlag   : [withIOP : ' 1  1 ', withoutIOP : ' 1  0 '],
        ],

        (_PASSENGER_AMOUNT)  : [
            kindCode   : 'D4',
            kindName   : '乘客座位责任险',
            newKindName: '车上人员责任保险(乘客)',
            kindFlag   : [withIOP : ' 1  1 ', withoutIOP : ' 1  0 '],
        ],

        (_SPONTANEOUS_LOSS)  : [
            kindCode   : 'Z',
            kindName   : '自燃损失险',
            newKindName: '自燃损失险',
            kindFlag   : [withIOP : ' 2  1 ', withoutIOP : ' 2  0 '],
        ],

        (_GLASS)             : [
            kindCode   : 'F',
            kindName   : '玻璃单独破碎险',
            newKindName: '玻璃单独破碎险',
            kindFlag   : [withIOP : ' 2  0 ', withoutIOP : ' 2  0 '],
        ],

        (_SCRATCH_AMOUNT)    : [
            kindCode   : 'L',
            kindName   : '车身划痕损失险',
            newKindName: '车身划痕损失险',
            kindFlag   : [withIOP : ' 2  1 ', withoutIOP : ' 2  0 '],
        ],

        (_ENGINE)            : [
            kindCode   : 'X1',
            kindName   : '发动机特别损失险',
            newKindName: '发动机涉水损失险',
            kindFlag   : [withIOP : ' 2  1 ', withoutIOP : ' 2  0 '],
        ],
        (_UNABLE_FIND_THIRDPARTY)            : [
            kindCode   : 'Z3',
            kindName   : '无法找到第三方特约险',
            newKindName: '无法找到第三方特约险',
            kindFlag   : [withIOP : '', withoutIOP : ''],
        ]
    ]

    static getKindItemFromList(kindItemList, key) {
        kindItemList.find {
            it.kindCode == _KIND_ITEM_CONFIG[key]?.kindCode
        }
    }

    static getAmountRangeMappingsFromList(kindItemList) {
        _KIND_ITEM_CONFIG.collectEntries { key, _2 ->
            def kindItem = getKindItemFromList(kindItemList, key )
            [(key) : kindItem?.mapValue.collect { kindKey, _3 ->
                kindKey.toDouble()
            }]
        } - null
    }

    static void adjustAccurateInsurancePackageAmount(accurateInsurancePackage, amountRangeMapping) {
        [_THIRD_PARTY_AMOUNT, _DRIVER_AMOUNT, _PASSENGER_AMOUNT, _SCRATCH_AMOUNT].each { itemName ->
            def originalValue = accurateInsurancePackage[itemName]
            if (originalValue && amountRangeMapping[itemName]) {
                accurateInsurancePackage[itemName] = adjustInsureAmount(originalValue, amountRangeMapping[itemName].collect {
                    it as double
                })
            }
        }
    }

    //用cheche的保额修正阳光官网的保额，取交集，只针对有保额列表的
    static adjustAmountRangeMapping(amountRangeMappings ) {
        amountRangeMappings + _INSURANCE_MAPPINGS.findAll { itemName, itemMap ->
            itemMap.amountList
         }.collectEntries { itemName, itemMap ->
            def amountValueList = amountRangeMappings[itemName]
            [(itemName) : amountValueList.intersect(itemMap.amountList) ]
        }
    }

}

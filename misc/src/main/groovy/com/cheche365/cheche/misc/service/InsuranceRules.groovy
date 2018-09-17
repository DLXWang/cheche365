package com.cheche365.cheche.misc.service

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.GlassType

import static com.cheche365.cheche.common.util.DateUtils.getYearsUntil
import static com.cheche365.cheche.common.util.DateUtils.getYearsUntil

/**
 * @author Huabin
 */
final class InsuranceRules {

    private static final _INSURANCE_TYPE_NAME_THIRD_PARTY   = '第三者责任保险'
    private static final _INSURANCE_TYPE_NAME_PERSON        = '车上人员责任险'
    private static final _INSURANCE_TYPE_NAME_DAMAGE        = '机动车辆损失险'
    private static final _INSURANCE_TYPE_NAME_GLASS         = '玻璃单独破碎险'
    private static final _INSURANCE_TYPE_NAME_SCRATCH         = '划痕险'
    private static final _INSURANCE_TYPE_NAME_SPONTANEOUS_LOSS = '自燃险'
    private static final _INSURANCE_TYPE_NAME_THEFT         =  '盗抢险'

    // 如果key等于item则返回value，否则返回null的finder
    private static final _FINDER_BY_KEY         = { item, entry -> entry.key == item ? entry.value : null }
    // 以key做checker，以item做参数，如果checker返回true则返回value，否则返回null的finder
    private static final _FINDER_BY_KEY_CHECKER = { item, entry -> entry.key(item) ? entry.value : null }

    // 车座数
    private static final _CHECKER_SEAT_6    = { it < 6 }
    private static final _CHECKER_SEAT_6_10 = { 6 <= it && it < 10 }
    private static final _CHECKER_SEAT_10   = { 10 <= it }

    // 100万以上保额时三者险的计算公式，见151附件
    private static final _FORMULA_THIRD_PARTY_1M = { double amount, double A, double B ->
        A + 0.9 * ((amount - 1_000_000.0) / 500_000.0) * (A - B)
    }

    private static final _CHECKER_AUTO_AGE_1 = { it < 1 }
    private static final _CHECKER_AUTO_AGE_1_2 = { 1 <= it && it < 2 }
    private static final _CHECKER_AUTO_AGE_0_2 = { 0 <= it && it < 2 }
    private static final _CHECKER_AUTO_AGE_2 = { 2 <= it }
    private static final _CHECKER_AUTO_AGE_2_6 = { 2 <= it && it < 6 }
    private static final _CHECKER_AUTO_AGE_6 = { 6 <= it }


    private static final _CHECKER_AUTO_GLASS_TYPE_1 = { it == 1 }
    private static final _CHECKER_AUTO_GLASS_TYPE_2 = { it == 2 }


    private static final _CHECKER_AUTO_NEW_PRICE_30 = { it < 300000 }
    private static final _CHECKER_AUTO_NEW_PRICE_30_50 = { 300000 <=it && it < 500000 }
    private static final _CHECKER_AUTO_NEW_PRICE_50 = { 500000 <= it }

    private static final _CHEAT_SHEET = [
        '北京市': [
            // 机动车辆损失险
            (_INSURANCE_TYPE_NAME_DAMAGE): [
                (_CHECKER_AUTO_AGE_1)   : [
                    (_CHECKER_SEAT_6)       : [459, 1.088/100],
                    (_CHECKER_SEAT_6_10)    : [550, 1.088/100],
                    (_CHECKER_SEAT_10)      : [550, 1.088/100]
                ],
                (_CHECKER_AUTO_AGE_1_2) : [
                    (_CHECKER_SEAT_6)       : [437, 1.037/100],
                    (_CHECKER_SEAT_6_10)    : [524, 1.037/100],
                    (_CHECKER_SEAT_10)      : [524, 1.037/100]
                ],
                (_CHECKER_AUTO_AGE_2_6)   : [
                    (_CHECKER_SEAT_6)       : [432, 1.029/100],
                    (_CHECKER_SEAT_6_10)    : [518, 1.029/100],
                    (_CHECKER_SEAT_10)      : [518, 1.029/100]
                ],
                (_CHECKER_AUTO_AGE_6)     : [
                    (_CHECKER_SEAT_6)       : [445, 1.054/100],
                    (_CHECKER_SEAT_6_10)    : [534, 1.054/100],
                    (_CHECKER_SEAT_10)      : [534, 1.054/100]
                ]
            ],
            // 第三者责任保险
            (_INSURANCE_TYPE_NAME_THIRD_PARTY) : [
                (_CHECKER_SEAT_6)       : [50_000.0: 516, 100_000.0: 746, 150_000.0: 850, 200_000.0: 924, 300_000.0: 1043, 500_000.0: 1252, 1_000_000.0: 1630],
                (_CHECKER_SEAT_6_10)    : [50_000.0: 478, 100_000.0: 674, 150_000.0: 761, 200_000.0: 821, 300_000.0: 919,  500_000.0: 1094, 1_000_000.0: 1425],
                (_CHECKER_SEAT_10)      : [50_000.0: 478, 100_000.0: 674, 150_000.0: 761, 200_000.0: 821, 300_000.0: 919,  500_000.0: 1094, 1_000_000.0: 1425]
            ],
            // 车上人员责任险
            (_INSURANCE_TYPE_NAME_PERSON): [
                (_CHECKER_SEAT_6)       : [0.349/100, 0.221/100],
                (_CHECKER_SEAT_6_10)    : [0.332/100, 0.213/100],
                (_CHECKER_SEAT_10)      : [0.332/100, 0.213/100]
            ],
            //玻璃单独破碎险
            (_INSURANCE_TYPE_NAME_GLASS): [
                (_CHECKER_AUTO_GLASS_TYPE_1) : [
                    (_CHECKER_SEAT_6)       : [0.162/100],
                    (_CHECKER_SEAT_6_10)    : [0.179/100],
                    (_CHECKER_SEAT_10)      : [0.196/100]
                ],
                (_CHECKER_AUTO_GLASS_TYPE_2) : [
                    (_CHECKER_SEAT_6)       : [0.264/100],
                    (_CHECKER_SEAT_6_10)    : [0.272/100],
                    (_CHECKER_SEAT_10)      : [0.323/100]
                ]
            ],
            //划痕险
            (_INSURANCE_TYPE_NAME_SCRATCH): [
                (_CHECKER_AUTO_AGE_0_2) : [
                    (_CHECKER_AUTO_NEW_PRICE_30) : [2000.0: 340, 5000.0: 485, 10000.0: 646, 20000.0: 969],
                    (_CHECKER_AUTO_NEW_PRICE_30_50) : [2000.0: 498, 5000.0: 765, 10000.0: 995, 20000.0: 1513],
                    (_CHECKER_AUTO_NEW_PRICE_50) : [2000.0: 723, 5000.0: 935, 10000.0: 1275, 20000.0: 1913]
                ],
                (_CHECKER_AUTO_AGE_2) : [
                    (_CHECKER_AUTO_NEW_PRICE_30) : [2000.0: 519, 5000.0: 723, 10000.0: 1105, 20000.0: 1615],
                    (_CHECKER_AUTO_NEW_PRICE_30_50) : [2000.0: 765, 5000.0: 1148, 10000.0: 1530, 20000.0: 2210],
                    (_CHECKER_AUTO_NEW_PRICE_50) : [2000.0: 935, 5000.0: 1275, 10000.0: 1700, 20000.0: 2550]
                ]
            ],
            //自燃险
            (_INSURANCE_TYPE_NAME_SPONTANEOUS_LOSS): [
                (_CHECKER_AUTO_AGE_1) : [0.128/100],
                (_CHECKER_AUTO_AGE_1_2) : [0.153/100],
                (_CHECKER_AUTO_AGE_2_6) : [0.170/100],
                (_CHECKER_AUTO_AGE_6) : [0.196/100]
            ],

            (_INSURANCE_TYPE_NAME_THEFT): [
                (_CHECKER_SEAT_6)       : [102, 0.451/100],
                (_CHECKER_SEAT_6_10)    : [119, 0.374/100],
                (_CHECKER_SEAT_10)      : [119, 0.374/100]
            ]

        ],
        '郑州市': [
            // 机动车辆损失险
            (_INSURANCE_TYPE_NAME_DAMAGE): [
                (_CHECKER_AUTO_AGE_1)   : [
                    (_CHECKER_SEAT_6)       : [459, 1.088/100],
                    (_CHECKER_SEAT_6_10)    : [550, 1.088/100],
                    (_CHECKER_SEAT_10)      : [550, 1.088/100]
                ],
                (_CHECKER_AUTO_AGE_1_2) : [
                    (_CHECKER_SEAT_6)       : [437, 1.037/100],
                    (_CHECKER_SEAT_6_10)    : [524, 1.037/100],
                    (_CHECKER_SEAT_10)      : [524, 1.037/100]
                ],
                (_CHECKER_AUTO_AGE_2_6)   : [
                    (_CHECKER_SEAT_6)       : [432, 1.029/100],
                    (_CHECKER_SEAT_6_10)    : [518, 1.029/100],
                    (_CHECKER_SEAT_10)      : [518, 1.029/100]
                ],
                (_CHECKER_AUTO_AGE_6)     : [
                    (_CHECKER_SEAT_6)       : [445, 1.054/100],
                    (_CHECKER_SEAT_6_10)    : [534, 1.054/100],
                    (_CHECKER_SEAT_10)      : [534, 1.054/100]
                ]
            ],
            // 第三者责任保险
            (_INSURANCE_TYPE_NAME_THIRD_PARTY) : [
                (_CHECKER_SEAT_6)       : [50_000.0: 516, 100_000.0: 746, 150_000.0: 850, 200_000.0: 924, 300_000.0: 1043, 500_000.0: 1252, 1_000_000.0: 1630],
                (_CHECKER_SEAT_6_10)    : [50_000.0: 478, 100_000.0: 674, 150_000.0: 761, 200_000.0: 821, 300_000.0: 919,  500_000.0: 1094, 1_000_000.0: 1425],
                (_CHECKER_SEAT_10)      : [50_000.0: 478, 100_000.0: 674, 150_000.0: 761, 200_000.0: 821, 300_000.0: 919,  500_000.0: 1094, 1_000_000.0: 1425]
            ],
            // 车上人员责任险
            (_INSURANCE_TYPE_NAME_PERSON): [
                (_CHECKER_SEAT_6)       : [0.349/100, 0.221/100],
                (_CHECKER_SEAT_6_10)    : [0.332/100, 0.213/100],
                (_CHECKER_SEAT_10)      : [0.332/100, 0.213/100]
            ],
            //玻璃单独破碎险
            (_INSURANCE_TYPE_NAME_GLASS): [
                (_CHECKER_AUTO_GLASS_TYPE_1) : [
                    (_CHECKER_SEAT_6)       : [0.162/100],
                    (_CHECKER_SEAT_6_10)    : [0.179/100],
                    (_CHECKER_SEAT_10)      : [0.196/100]
                ],
                (_CHECKER_AUTO_GLASS_TYPE_2) : [
                    (_CHECKER_SEAT_6)       : [0.264/100],
                    (_CHECKER_SEAT_6_10)    : [0.272/100],
                    (_CHECKER_SEAT_10)      : [0.323/100]
                ]
            ],
            //划痕险
            (_INSURANCE_TYPE_NAME_SCRATCH): [
                (_CHECKER_AUTO_AGE_0_2) : [
                    (_CHECKER_AUTO_NEW_PRICE_30) : [2000.0: 340, 5000.0: 485, 10000.0: 646, 20000.0: 969],
                    (_CHECKER_AUTO_NEW_PRICE_30_50) : [2000.0: 498, 5000.0: 765, 10000.0: 995, 20000.0: 1513],
                    (_CHECKER_AUTO_NEW_PRICE_50) : [2000.0: 723, 5000.0: 935, 10000.0: 1275, 20000.0: 1913]
                ],
                (_CHECKER_AUTO_AGE_2) : [
                    (_CHECKER_AUTO_NEW_PRICE_30) : [2000.0: 519, 5000.0: 723, 10000.0: 1105, 20000.0: 1615],
                    (_CHECKER_AUTO_NEW_PRICE_30_50) : [2000.0: 765, 5000.0: 1148, 10000.0: 1530, 20000.0: 2210],
                    (_CHECKER_AUTO_NEW_PRICE_50) : [2000.0: 935, 5000.0: 1275, 10000.0: 1700, 20000.0: 2550]
                ]
            ],
            //自燃险
            (_INSURANCE_TYPE_NAME_SPONTANEOUS_LOSS): [
                (_CHECKER_AUTO_AGE_1) : [0.128/100],
                (_CHECKER_AUTO_AGE_1_2) : [0.153/100],
                (_CHECKER_AUTO_AGE_2_6) : [0.170/100],
                (_CHECKER_AUTO_AGE_6) : [0.196/100]
            ],

            (_INSURANCE_TYPE_NAME_THEFT): [
                (_CHECKER_SEAT_6)       : [102, 0.451/100],
                (_CHECKER_SEAT_6_10)    : [119, 0.374/100],
                (_CHECKER_SEAT_10)      : [119, 0.374/100]
            ]

        ],
        '深圳市': [
            // 机动车辆损失险
            (_INSURANCE_TYPE_NAME_DAMAGE): [
                (_CHECKER_AUTO_AGE_1)   : [
                    (_CHECKER_SEAT_6)       : [459, 1.088/100],
                    (_CHECKER_SEAT_6_10)    : [550, 1.088/100],
                    (_CHECKER_SEAT_10)      : [550, 1.088/100]
                ],
                (_CHECKER_AUTO_AGE_1_2) : [
                    (_CHECKER_SEAT_6)       : [437, 1.037/100],
                    (_CHECKER_SEAT_6_10)    : [524, 1.037/100],
                    (_CHECKER_SEAT_10)      : [524, 1.037/100]
                ],
                (_CHECKER_AUTO_AGE_2_6)   : [
                    (_CHECKER_SEAT_6)       : [432, 1.029/100],
                    (_CHECKER_SEAT_6_10)    : [518, 1.029/100],
                    (_CHECKER_SEAT_10)      : [518, 1.029/100]
                ],
                (_CHECKER_AUTO_AGE_6)     : [
                    (_CHECKER_SEAT_6)       : [445, 1.054/100],
                    (_CHECKER_SEAT_6_10)    : [534, 1.054/100],
                    (_CHECKER_SEAT_10)      : [534, 1.054/100]
                ]
            ],
            // 第三者责任保险
            (_INSURANCE_TYPE_NAME_THIRD_PARTY) : [
                (_CHECKER_SEAT_6)       : [50_000.0: 516, 100_000.0: 746, 150_000.0: 850, 200_000.0: 924, 300_000.0: 1043, 500_000.0: 1252, 1_000_000.0: 1630],
                (_CHECKER_SEAT_6_10)    : [50_000.0: 478, 100_000.0: 674, 150_000.0: 761, 200_000.0: 821, 300_000.0: 919,  500_000.0: 1094, 1_000_000.0: 1425],
                (_CHECKER_SEAT_10)      : [50_000.0: 478, 100_000.0: 674, 150_000.0: 761, 200_000.0: 821, 300_000.0: 919,  500_000.0: 1094, 1_000_000.0: 1425]
            ],
            // 车上人员责任险
            (_INSURANCE_TYPE_NAME_PERSON): [
                (_CHECKER_SEAT_6)       : [0.349/100, 0.221/100],
                (_CHECKER_SEAT_6_10)    : [0.332/100, 0.213/100],
                (_CHECKER_SEAT_10)      : [0.332/100, 0.213/100]
            ],
            //玻璃单独破碎险
            (_INSURANCE_TYPE_NAME_GLASS): [
                (_CHECKER_AUTO_GLASS_TYPE_1) : [
                    (_CHECKER_SEAT_6)       : [0.162/100],
                    (_CHECKER_SEAT_6_10)    : [0.179/100],
                    (_CHECKER_SEAT_10)      : [0.196/100]
                ],
                (_CHECKER_AUTO_GLASS_TYPE_2) : [
                    (_CHECKER_SEAT_6)       : [0.264/100],
                    (_CHECKER_SEAT_6_10)    : [0.272/100],
                    (_CHECKER_SEAT_10)      : [0.323/100]
                ]
            ],
            //划痕险
            (_INSURANCE_TYPE_NAME_SCRATCH): [
                (_CHECKER_AUTO_AGE_0_2) : [
                    (_CHECKER_AUTO_NEW_PRICE_30) : [2000.0: 340, 5000.0: 485, 10000.0: 646, 20000.0: 969],
                    (_CHECKER_AUTO_NEW_PRICE_30_50) : [2000.0: 498, 5000.0: 765, 10000.0: 995, 20000.0: 1513],
                    (_CHECKER_AUTO_NEW_PRICE_50) : [2000.0: 723, 5000.0: 935, 10000.0: 1275, 20000.0: 1913]
                ],
                (_CHECKER_AUTO_AGE_2) : [
                    (_CHECKER_AUTO_NEW_PRICE_30) : [2000.0: 519, 5000.0: 723, 10000.0: 1105, 20000.0: 1615],
                    (_CHECKER_AUTO_NEW_PRICE_30_50) : [2000.0: 765, 5000.0: 1148, 10000.0: 1530, 20000.0: 2210],
                    (_CHECKER_AUTO_NEW_PRICE_50) : [2000.0: 935, 5000.0: 1275, 10000.0: 1700, 20000.0: 2550]
                ]
            ],
            //自燃险
            (_INSURANCE_TYPE_NAME_SPONTANEOUS_LOSS): [
                (_CHECKER_AUTO_AGE_1) : [0.128/100],
                (_CHECKER_AUTO_AGE_1_2) : [0.153/100],
                (_CHECKER_AUTO_AGE_2_6) : [0.170/100],
                (_CHECKER_AUTO_AGE_6) : [0.196/100]
            ],

            (_INSURANCE_TYPE_NAME_THEFT): [
                (_CHECKER_SEAT_6)       : [102, 0.451/100],
                (_CHECKER_SEAT_6_10)    : [119, 0.374/100],
                (_CHECKER_SEAT_10)      : [119, 0.374/100]
            ]

        ],
        '苏州市': [
            // 机动车辆损失险
            (_INSURANCE_TYPE_NAME_DAMAGE): [
                (_CHECKER_AUTO_AGE_1)   : [
                    (_CHECKER_SEAT_6)       : [459, 1.088/100],
                    (_CHECKER_SEAT_6_10)    : [550, 1.088/100],
                    (_CHECKER_SEAT_10)      : [550, 1.088/100]
                ],
                (_CHECKER_AUTO_AGE_1_2) : [
                    (_CHECKER_SEAT_6)       : [437, 1.037/100],
                    (_CHECKER_SEAT_6_10)    : [524, 1.037/100],
                    (_CHECKER_SEAT_10)      : [524, 1.037/100]
                ],
                (_CHECKER_AUTO_AGE_2_6)   : [
                    (_CHECKER_SEAT_6)       : [432, 1.029/100],
                    (_CHECKER_SEAT_6_10)    : [518, 1.029/100],
                    (_CHECKER_SEAT_10)      : [518, 1.029/100]
                ],
                (_CHECKER_AUTO_AGE_6)     : [
                    (_CHECKER_SEAT_6)       : [445, 1.054/100],
                    (_CHECKER_SEAT_6_10)    : [534, 1.054/100],
                    (_CHECKER_SEAT_10)      : [534, 1.054/100]
                ]
            ],
            // 第三者责任保险
            (_INSURANCE_TYPE_NAME_THIRD_PARTY) : [
                (_CHECKER_SEAT_6)       : [50_000.0: 516, 100_000.0: 746, 150_000.0: 850, 200_000.0: 924, 300_000.0: 1043, 500_000.0: 1252, 1_000_000.0: 1630],
                (_CHECKER_SEAT_6_10)    : [50_000.0: 478, 100_000.0: 674, 150_000.0: 761, 200_000.0: 821, 300_000.0: 919,  500_000.0: 1094, 1_000_000.0: 1425],
                (_CHECKER_SEAT_10)      : [50_000.0: 478, 100_000.0: 674, 150_000.0: 761, 200_000.0: 821, 300_000.0: 919,  500_000.0: 1094, 1_000_000.0: 1425]
            ],
            // 车上人员责任险
            (_INSURANCE_TYPE_NAME_PERSON): [
                (_CHECKER_SEAT_6)       : [0.349/100, 0.221/100],
                (_CHECKER_SEAT_6_10)    : [0.332/100, 0.213/100],
                (_CHECKER_SEAT_10)      : [0.332/100, 0.213/100]
            ],
            //玻璃单独破碎险
            (_INSURANCE_TYPE_NAME_GLASS): [
                (_CHECKER_AUTO_GLASS_TYPE_1) : [
                    (_CHECKER_SEAT_6)       : [0.162/100],
                    (_CHECKER_SEAT_6_10)    : [0.179/100],
                    (_CHECKER_SEAT_10)      : [0.196/100]
                ],
                (_CHECKER_AUTO_GLASS_TYPE_2) : [
                    (_CHECKER_SEAT_6)       : [0.264/100],
                    (_CHECKER_SEAT_6_10)    : [0.272/100],
                    (_CHECKER_SEAT_10)      : [0.323/100]
                ]
            ],
            //划痕险
            (_INSURANCE_TYPE_NAME_SCRATCH): [
                (_CHECKER_AUTO_AGE_0_2) : [
                    (_CHECKER_AUTO_NEW_PRICE_30) : [2000.0: 340, 5000.0: 485, 10000.0: 646, 20000.0: 969],
                    (_CHECKER_AUTO_NEW_PRICE_30_50) : [2000.0: 498, 5000.0: 765, 10000.0: 995, 20000.0: 1513],
                    (_CHECKER_AUTO_NEW_PRICE_50) : [2000.0: 723, 5000.0: 935, 10000.0: 1275, 20000.0: 1913]
                ],
                (_CHECKER_AUTO_AGE_2) : [
                    (_CHECKER_AUTO_NEW_PRICE_30) : [2000.0: 519, 5000.0: 723, 10000.0: 1105, 20000.0: 1615],
                    (_CHECKER_AUTO_NEW_PRICE_30_50) : [2000.0: 765, 5000.0: 1148, 10000.0: 1530, 20000.0: 2210],
                    (_CHECKER_AUTO_NEW_PRICE_50) : [2000.0: 935, 5000.0: 1275, 10000.0: 1700, 20000.0: 2550]
                ]
            ],
            //自燃险
            (_INSURANCE_TYPE_NAME_SPONTANEOUS_LOSS): [
                (_CHECKER_AUTO_AGE_1) : [0.128/100],
                (_CHECKER_AUTO_AGE_1_2) : [0.153/100],
                (_CHECKER_AUTO_AGE_2_6) : [0.170/100],
                (_CHECKER_AUTO_AGE_6) : [0.196/100]
            ],

            (_INSURANCE_TYPE_NAME_THEFT): [
                (_CHECKER_SEAT_6)       : [102, 0.451/100],
                (_CHECKER_SEAT_6_10)    : [119, 0.374/100],
                (_CHECKER_SEAT_10)      : [119, 0.374/100]
            ]

        ],
        '上海市': [
            // 机动车辆损失险
            (_INSURANCE_TYPE_NAME_DAMAGE): [
                (_CHECKER_AUTO_AGE_1)   : [
                    (_CHECKER_SEAT_6)       : [459, 1.088/100],
                    (_CHECKER_SEAT_6_10)    : [550, 1.088/100],
                    (_CHECKER_SEAT_10)      : [550, 1.088/100]
                ],
                (_CHECKER_AUTO_AGE_1_2) : [
                    (_CHECKER_SEAT_6)       : [437, 1.037/100],
                    (_CHECKER_SEAT_6_10)    : [524, 1.037/100],
                    (_CHECKER_SEAT_10)      : [524, 1.037/100]
                ],
                (_CHECKER_AUTO_AGE_2_6)   : [
                    (_CHECKER_SEAT_6)       : [432, 1.029/100],
                    (_CHECKER_SEAT_6_10)    : [518, 1.029/100],
                    (_CHECKER_SEAT_10)      : [518, 1.029/100]
                ],
                (_CHECKER_AUTO_AGE_6)     : [
                    (_CHECKER_SEAT_6)       : [445, 1.054/100],
                    (_CHECKER_SEAT_6_10)    : [534, 1.054/100],
                    (_CHECKER_SEAT_10)      : [534, 1.054/100]
                ]
            ],
            // 第三者责任保险
            (_INSURANCE_TYPE_NAME_THIRD_PARTY) : [
                (_CHECKER_SEAT_6)       : [50_000.0: 516, 100_000.0: 746, 150_000.0: 850, 200_000.0: 924, 300_000.0: 1043, 500_000.0: 1252, 1_000_000.0: 1630],
                (_CHECKER_SEAT_6_10)    : [50_000.0: 478, 100_000.0: 674, 150_000.0: 761, 200_000.0: 821, 300_000.0: 919,  500_000.0: 1094, 1_000_000.0: 1425],
                (_CHECKER_SEAT_10)      : [50_000.0: 478, 100_000.0: 674, 150_000.0: 761, 200_000.0: 821, 300_000.0: 919,  500_000.0: 1094, 1_000_000.0: 1425]
            ],
            // 车上人员责任险
            (_INSURANCE_TYPE_NAME_PERSON): [
                (_CHECKER_SEAT_6)       : [0.349/100, 0.221/100],
                (_CHECKER_SEAT_6_10)    : [0.332/100, 0.213/100],
                (_CHECKER_SEAT_10)      : [0.332/100, 0.213/100]
            ],
            //玻璃单独破碎险
            (_INSURANCE_TYPE_NAME_GLASS): [
                (_CHECKER_AUTO_GLASS_TYPE_1) : [
                    (_CHECKER_SEAT_6)       : [0.162/100],
                    (_CHECKER_SEAT_6_10)    : [0.179/100],
                    (_CHECKER_SEAT_10)      : [0.196/100]
                ],
                (_CHECKER_AUTO_GLASS_TYPE_2) : [
                    (_CHECKER_SEAT_6)       : [0.264/100],
                    (_CHECKER_SEAT_6_10)    : [0.272/100],
                    (_CHECKER_SEAT_10)      : [0.323/100]
                ]
            ],
            //划痕险
            (_INSURANCE_TYPE_NAME_SCRATCH): [
                (_CHECKER_AUTO_AGE_0_2) : [
                    (_CHECKER_AUTO_NEW_PRICE_30) : [2000.0: 340, 5000.0: 485, 10000.0: 646, 20000.0: 969],
                    (_CHECKER_AUTO_NEW_PRICE_30_50) : [2000.0: 498, 5000.0: 765, 10000.0: 995, 20000.0: 1513],
                    (_CHECKER_AUTO_NEW_PRICE_50) : [2000.0: 723, 5000.0: 935, 10000.0: 1275, 20000.0: 1913]
                ],
                (_CHECKER_AUTO_AGE_2) : [
                    (_CHECKER_AUTO_NEW_PRICE_30) : [2000.0: 519, 5000.0: 723, 10000.0: 1105, 20000.0: 1615],
                    (_CHECKER_AUTO_NEW_PRICE_30_50) : [2000.0: 765, 5000.0: 1148, 10000.0: 1530, 20000.0: 2210],
                    (_CHECKER_AUTO_NEW_PRICE_50) : [2000.0: 935, 5000.0: 1275, 10000.0: 1700, 20000.0: 2550]
                ]
            ],
            //自燃险
            (_INSURANCE_TYPE_NAME_SPONTANEOUS_LOSS): [
                (_CHECKER_AUTO_AGE_1) : [0.128/100],
                (_CHECKER_AUTO_AGE_1_2) : [0.153/100],
                (_CHECKER_AUTO_AGE_2_6) : [0.170/100],
                (_CHECKER_AUTO_AGE_6) : [0.196/100]
            ],

            (_INSURANCE_TYPE_NAME_THEFT): [
                (_CHECKER_SEAT_6)       : [102, 0.451/100],
                (_CHECKER_SEAT_6_10)    : [119, 0.374/100],
                (_CHECKER_SEAT_10)      : [119, 0.374/100]
            ]

        ]
    ]
    private static final _THIRD_PARTY_AMOUNT_LIST   = [50000.0, 100000.0, 200000.0, 300000.0, 500000.0, 1000000.0, 1500000.0, 2000000.0, 2500000.0, 3000000.0, 3500000.0];
    private static final _DRIVER_AMOUNT_LIST        = [10000.0, 20000.0, 30000.0, 40000.0, 50000.0, 80000.0, 100000.0, 150000.0, 200000.0];
    private static final _PASSENGER_AMOUNT_LIST     = [10000.0, 20000.0, 30000.0, 40000.0, 50000.0, 80000.0, 100000.0, 150000.0, 200000.0];


    // 计算折扣率（0~1之间的小数）
    static double discount(double premium, ruleFunc, Area area, Auto auto, double amount) {
        0 != premium ? premium / eval(ruleFunc, area, auto, amount) : 0
    }

    // 机动车辆损失险
    static double damagePremium(Area area, Auto auto, double amount) {
        def autoAge = getYearsUntil auto.enrollDate
        def autoSeats = 5
        def cs = cheatSheet([
            _FINDER_BY_KEY.curry('北京市'),
            _FINDER_BY_KEY.curry(_INSURANCE_TYPE_NAME_DAMAGE),
            _FINDER_BY_KEY_CHECKER.curry(autoAge),
            _FINDER_BY_KEY_CHECKER.curry(autoSeats)
        ])
        cs[0] + amount * cs[1]
    }

    // 第三者责任保险
    static double thirdPartyPremium(Area area, Auto auto, double amount) {
        if (0 == amount) {
            return 0
        }
        def autoSeats = auto.seats ?: 5
        def cs = cheatSheet([
            _FINDER_BY_KEY.curry('北京市'),
            _FINDER_BY_KEY.curry(_INSURANCE_TYPE_NAME_THIRD_PARTY),
            _FINDER_BY_KEY_CHECKER.curry(autoSeats)
        ])
        amount <= 1_000_000.0 ? cs[amount as BigDecimal] : _FORMULA_THIRD_PARTY_1M(amount, cs[1_000_000.0], cs[500_000.0])
    }

    // 车上人员责任险-司机
    static double driverPremium(Area area, Auto auto, double amount) {
        def cs = personPremiumCheatSheet area, auto
        amount * cs[0]
    }

    // 车上人员责任险-乘客（以座位数-1计算）
    static double passengerPremium(Area area, Auto auto, double amount) {
        def cs = personPremiumCheatSheet area, auto
        amount * cs[1] * ((auto.seats ?: 5) - 1)
    }

    static double glassPremium(Area area, Auto auto, GlassType glassType, Double newPrice){
        def autoAge = getYearsUntil auto.enrollDate
        def cs = cheatSheet([
            _FINDER_BY_KEY.curry('北京市'),
            _FINDER_BY_KEY.curry(_INSURANCE_TYPE_NAME_GLASS),
            _FINDER_BY_KEY_CHECKER.curry(glassType.id),
            _FINDER_BY_KEY_CHECKER.curry(autoAge)]);
        newPrice * cs[0]
    }

    static double scratchPremium(Area area, Auto auto, Double newPrice, Double amount){
        def autoAge = getYearsUntil auto.enrollDate
        def cs = cheatSheet([
            _FINDER_BY_KEY.curry('北京市'),
            _FINDER_BY_KEY.curry(_INSURANCE_TYPE_NAME_SCRATCH),
            _FINDER_BY_KEY_CHECKER.curry(autoAge),
            _FINDER_BY_KEY_CHECKER.curry(newPrice as BigDecimal)]);
        cs[amount as BigDecimal]
    }

    static Double spontaneousLossPremium(Area area, Auto auto, Double currentPrice) {
        def autoAge = getYearsUntil auto.enrollDate
        def cs = cheatSheet([
            _FINDER_BY_KEY.curry('北京市'),
            _FINDER_BY_KEY.curry(_INSURANCE_TYPE_NAME_SPONTANEOUS_LOSS),
            _FINDER_BY_KEY_CHECKER.curry(autoAge)]);
        cs[0] * currentPrice
    }

    static Double theftPremium(Area area, Auto auto, Double currentPrice) {
        def autoSeats = auto.seats ?: 5
        def cs = cheatSheet([
            _FINDER_BY_KEY.curry('北京市'),
            _FINDER_BY_KEY.curry(_INSURANCE_TYPE_NAME_THEFT),
            _FINDER_BY_KEY_CHECKER.curry(autoSeats)
        ])
        cs[0] + cs[1] * currentPrice;
    }


    static Double enginePremium(Area area, Auto auto, Double damagePremium) {
        return 5.0/100 * damagePremium
    }

    static double thirdPartyIOPPremium(Area area, Auto auto, Double thirdPartyPremium){
        return 15.0/100 * thirdPartyPremium;
    }

    static double damageIOPPremium(Area area, Auto auto, Double damagePremium){
        return 15.0/100 * damagePremium;
    }

    static double driverIOPPremium(Area area, Auto auto, Double driverPremium){
        return 15.0/100 * driverPremium;
    }

    static double passengerIOPPremium(Area area, Auto auto, Double passengerPremium){
        return 15.0/100 * passengerPremium;
    }

    static double scratchIOPPremium(Area area, Auto auto, Double scratchPremium){
        return 15.0/100 * scratchPremium;
    }

    static double theftIOPPremium(Area area, Auto auto, Double theftPremium){
        return 20.0/100 * theftPremium;
    }

    static double engineIOPPremium(Area area, Auto auto, Double enginePremium){
        return 20.0/100 * enginePremium;
    }

    /**
     * 车龄>3年禁止承保划痕险
     座位数>9座，禁止承保划痕险
     * @param area
     * @param auto
     * @param newPrice
     * @param claims
     * @return
     */
    static boolean couldApplyScratch(Area area, Auto auto, Double newPrice, Integer claims){
        def autoAge = getYearsUntil auto.enrollDate
        def autoSeats = auto.seats ?: 5
        if (autoAge > 3){
            return false;
        }
        if (autoSeats > 9){
            return false;
        }
    }

    /**
     * 车龄≥2年且上年理赔次数≥5次，禁止承保划痕险的不计免赔
     * @param are
     * @param auto
     * @param newPrice
     * @param claims
     * @return
     */
    static boolean couldApplyScratchIOP(Area area, Auto auto, Double newPrice, Integer claims){
        def autoAge = getYearsUntil auto.enrollDate
        return couldApplyScratch(area,auto,newPrice,claims) && (! ((autoAge > 2) && (claims > 5)));
    }

    /**
     * "以下4种情况之一的，划痕险最高限2万元：
     ①上年商业险出险次数≤1次的
     ②上年商业险赔款总额不超过本年商业险标准保费*A*B*54/85的
     ③高端车型（详见车型列表）
     ④车龄≤1年的"
     新车购置价＜15万，车身划痕最高保额2000元
     新车购置价≥15万且＜25万的，车身划痕最高保额5000元
     新车购置价≥25万且＜50万的，车身划痕最高保额1万元
     新车购置价≥50万的，车身划痕最高保额2万元
     车龄≥2年且上年理赔次数≥5次，车身划痕最高保额2000元
     this method doesn't implement all rules above
     * @param Area
     * @param area
     * @param auto
     * @param newPrice
     * @param claims
     * @return
     */
    static applicableScratchAmount(Area area, Auto auto, Double newPrice, Integer claims){
        def autoAge = getYearsUntil auto.enrollDate
        if (!couldApplyScratch(area,auto,newPrice,claims)){
            return [];
        }
        if (autoAge >2 && claims > 5){
            return [2000.0];
        }
        if (newPrice < 150000.0){
            return [2000.0];
        }
        if (newPrice < 250000.0){
            return [2000.0,5000.0]
        }
        if (newPrice < 500000.0){
            return [2000.0,5000.0, 10000.0];
        }

        return [2000.0,5000.0,10000.0,20000.0];
    }

    static applicableThirdPartyAmount(Area area, Auto auto){
        return _THIRD_PARTY_AMOUNT_LIST;
    }

    static applicableDriverAmount(Area area, Auto auto) {
        return _DRIVER_AMOUNT_LIST;
    }

    static applicablePassengerAmount(Area area, Auto auto) {
        return _PASSENGER_AMOUNT_LIST;
    }

    static double currentPrice(double purchasePrice, Date enrollDate){

    }


    // 车上人员责任险速查表
    private static personPremiumCheatSheet(Area area, Auto auto) {
        def autoSeats = auto.seats ?: 5
        cheatSheet([
            _FINDER_BY_KEY.curry('北京市'),
            _FINDER_BY_KEY.curry(_INSURANCE_TYPE_NAME_PERSON),
            _FINDER_BY_KEY_CHECKER.curry(autoSeats)
        ])
    }

    // 费率速查表
    private static cheatSheet(finders) {
        finders.inject _CHEAT_SHEET, { value, finder ->
            value.findResult finder
        }
    }

    /**
     * 公式求值
     *
     * @param ruleFunc    规则函数指针
     * @param area
     * @param auto
     * @param amount
     * @return
     */
    private static double eval(ruleFunc, Area area, Auto auto, double amount) {
        0 != amount ? ruleFunc(area, auto, amount) : 0
    }



}

package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.InsurancePackage
import com.cheche365.cheche.core.repository.InsurancePackageRepository
import org.apache.commons.lang3.SerializationUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

import java.beans.PropertyDescriptor

import static com.cheche365.cheche.core.model.GlassType.Enum.DOMESTIC_1

@Service
@Transactional
class InsurancePackageService {

    static final List ALL_FIELDS = [
        [
            'insuranceList': [
                [
                    'showTitle'   : true,
                    'hint'        : '适合2年内新车、5年内新手、经常开车、常去异地的车主',
                    'name'        : '机动车损失险',
                    'recommend'   : true,
                    'id'          : 'damage',
                    'code'        : 'damage',
                    'shortName'   : '车损险',
                    'type'        : 'radio',
                    'hot'         : '95%',
                    'desc'        : ['用于赔付自己车辆损失'],
                    'defaultValue': false
                ],
                [
                    'showTitle'   : true,
                    'hint'        : '适合新手，新车，经常开车的车主',
                    'name'        : '机动车第三者责任保险',
                    'options'     : [
                        ['text' : '5万',
                         'value': '50000'],
                        ['text' : '10万',
                         'value': '100000'],
                        ['text' : '20万',
                         'value': '200000'],
                        ['text' : '30万',
                         'value': '300000'],
                        ['text' : '50万',
                         'value': '500000'],
                        ['text' : '100万',
                         'value': '1000000'],
                        ['text' : '150万',
                         'value': '1500000'],
                        ['text' : '200万',
                         'value': '2000000'],
                        ['text' : '250万',
                         'value': '2500000'],
                        ['text' : '300万',
                         'value': '3000000']
                    ],
                    'recommend'   : true,
                    'id'          : 'thirdPartyAmount',
                    'code'        : 'thirdParty',
                    'shortName'   : '三者险',
                    'type'        : 'select',
                    'hot'         : '99%',
                    'desc'        : [
                        '用于赔付对他人造成的财产损失'
                    ],
                    'defaultValue': 0.0
                ],
                [
                    'showTitle'   : true,
                    'hint'        : '适合新手或者经常开车的车主',
                    'name'        : '车上人员责任险(司机)',
                    'options'     : [
                        ['text' : '1万',
                         'value': '10000'],
                        ['text' : '2万',
                         'value': '20000'],
                        ['text' : '5万',
                         'value': '50000'],
                        ['text' : '10万',
                         'value': '100000'],
                        ['text' : '20万',
                         'value': '200000']
                    ],
                    'recommend'   : false,
                    'id'          : 'driverAmount',
                    'code'        : 'driver',
                    'shortName'   : '司机险',
                    'type'        : 'select',
                    'hot'         : '72%',
                    'desc'        : [
                        '用于对司机造成的人身伤害赔偿'
                    ],
                    'defaultValue': 0.0
                ],
                [
                    'showTitle'   : true,
                    'hint'        : '适合经常载人的车主',
                    'name'        : '车上人员责任险(乘客)',
                    'options'     : [
                        ['text' : '1万/座',
                         'value': '10000'],
                        ['text' : '2万/座',
                         'value': '20000'],
                        ['text' : '5万/座',
                         'value': '50000'],
                        ['text' : '10万/座',
                         'value': '100000'],
                        ['text' : '20万/座',
                         'value': '200000']
                    ],
                    'recommend'   : false,
                    'id'          : 'passengerAmount',
                    'code'        : 'passenger',
                    'shortName'   : '乘客险',
                    'type'        : 'select',
                    'hot'         : '63%',
                    'desc'        : [
                        '车内本车乘客(非驾驶员)的人身伤亡赔偿'
                    ],
                    'defaultValue': 0.0
                ],
                [
                    'showTitle'   : true,
                    'hint'        : '适合常走高速、上下班开车的车主',
                    'name'        : '玻璃单独破碎险',
                    'options'     : [
                        ['text' : '国产',
                         'value': '1'],
                        ['text' : '进口',
                         'value': '2']
                    ],
                    'recommend'   : false,
                    'id'          : 'glassTypeId',
                    'code'        : 'glass',
                    'shortName'   : '玻璃险',
                    'type'        : 'select',
                    'hot'         : '35%',
                    'desc'        : [
                        '用于赔付非抗力对玻璃造成的损失'
                    ],
                    'defaultValue': 0
                ],
                [
                    'showTitle'   : true,
                    'hint'        : '适合无固定停车位、3年以下新车、豪车；户外活动多的车主',
                    'name'        : '机动车盗抢险',
                    'recommend'   : false,
                    'id'          : 'theft',
                    'code'        : 'theft',
                    'shortName'   : '盗抢险',
                    'type'        : 'radio',
                    'hot'         : '52%',
                    'desc'        : [
                        '用于赔付车辆被盗抢损失'
                    ],
                    'defaultValue': false
                ],
                [
                    'showTitle'   : true,
                    'hint'        : '适合无固定停车位、新车、豪车；3年以上车龄的车主',
                    'name'        : '自燃损失险',
                    'recommend'   : false,
                    'id'          : 'spontaneousLoss',
                    'code'        : 'spontaneousLoss',
                    'shortName'   : '自燃损失险',
                    'type'        : 'radio',
                    'hot'         : '30%',
                    'desc'        : [
                        '车的自身原因起火造成车辆本身的损失赔偿'
                    ],
                    'defaultValue': false
                ],
                [
                    'showTitle'   : true,
                    'hint'        : '适合无固定停车位，新车，偶尔开车，上下班开车的车主',
                    'name'        : '车身划痕损失险',
                    'options'     : [
                        ['text' : '2千',
                         'value': '2000'],
                        ['text' : '5千',
                         'value': '5000'],
                        ['text' : '1万',
                         'value': '10000'],
                        ['text' : '2万',
                         'value': '20000']
                    ],
                    'recommend'   : false,
                    'id'          : 'scratchAmount',
                    'code'        : 'scratch',
                    'shortName'   : '划痕险',
                    'type'        : 'select',
                    'hot'         : '8%',
                    'desc'        : [
                        '他人恶意行为造成的车辆车身人为划痕赔偿'
                    ],
                    'defaultValue': 0.0
                ],
                [
                    'showTitle'   : true,
                    'hint'        : '适合易积水地区（如北京）；户外活动多的车主',
                    'name'        : '发动机特别损失险(涉水险)',
                    'recommend'   : false,
                    'id'          : 'engine',
                    'code'        : 'engine',
                    'shortName'   : '涉水险',
                    'type'        : 'radio',
                    'hot'         : '40%',
                    'desc'        : [
                        '因水淹或涉水行驶造成发动机损坏的费用赔偿'
                    ],
                    'defaultValue': false
                ],
                [
                    'showTitle'   : true,
                    'hint'        : '建议全部购买 适合所有车主',
                    'name'        : '不计免赔险',
                    'recommend'   : true,
                    'id'          : 'iopTotal',
                    'code'        : 'iopTotal',
                    'shortName'   : '不计免赔险',
                    'type'        : 'radio',
                    'hot'         : '90%',
                    'desc'        : [
                        '事故发生后自己不再承担损失'
                    ],
                    'defaultValue': false
                ],
                [
                    'showTitle'   : true,
                    'hint'        : '适合经常把车辆停在公共停车场的车主',
                    'name'        : '无法找到第三方特约险',
                    'recommend'   : true,
                    'id'          : 'unableFindThirdParty',
                    'code'        : 'unableFindThirdParty',
                    'shortName'   : '无法找到第三方',
                    'type'        : 'radio',
                    'hot'         : '65%',
                    'desc'        : [
                        '发生事故后如果找不到另一责任方，未投保此险种需自行承担30%绝对免赔额，投保后则由保险公司全部承担'
                    ],
                    'defaultValue': false
                ]
            ],

            'name'         : '商业险',
            'id'           : 'base'
        ],
        [
            'insuranceList': [
                [
                    'showTitle'   : false,
                    'name'        : '交强险+车船税',
                    'recommend'   : false,
                    'id'          : 'compulsory',
                    'code'        : 'compulsoryPremium_autoTax',
                    'shortName'   : '交强险+车船税',
                    'type'        : 'radio',
                    'desc'        : ['交强险:国家规定的强制保险', '车船税:以排量为收取标准的国家税收'],
                    'defaultValue': false
                ]
            ],
            'name'         : '交强险+车船税',
            'id'           : 'compulsory'
        ]
    ]

    static final List ALL_FIELDS_REVERSE = ALL_FIELDS.reverse()

    @Autowired
    private InsurancePackageRepository insurancePackageRepository

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    InsurancePackage findByUniqueString(String uniqueString) {
        insurancePackageRepository.findFirstByUniqueString uniqueString
    }

    InsurancePackage saveInsurancePackage(InsurancePackage insurancePackage) {
        insurancePackageRepository.save insurancePackage
    }

    InsurancePackage mergeInsurancePackage(InsurancePackage insurancePackage) {
        insurancePackage.toIop()
        InsurancePackage newInsurancePackage = insurancePackage;
        newInsurancePackage.id = null
        newInsurancePackage.calculateUniqueString()

        InsurancePackage existInsurancePackage = insurancePackageRepository.findFirstByUniqueString(newInsurancePackage.uniqueString);
        if (existInsurancePackage) {
            return existInsurancePackage;
        }

        return this.insurancePackageRepository.save(newInsurancePackage);
    }

    /**
     * 将原始险种套餐根据传入的指定险种过滤，生成新的险种套餐
     */
    InsurancePackage generateInsurancePackage(InsurancePackage originalInsurancePackage, String[] properties) {
        if (!properties) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "险种列表不能为空")
        }

        InsurancePackage insurancePackage = new InsurancePackage()
        properties.each { property ->
            List<PropertyDescriptor> descriptors = InsurancePackage.PROPERTIES.findAll {
                it.getName().contains(property.replace("Amount", ""))
            }
            descriptors.each {
                it.getWriteMethod().invoke(insurancePackage, it.getReadMethod().invoke(originalInsurancePackage));
            }
        }
        mergeInsurancePackage(insurancePackage)
    }

    // 全险种
    static final InsurancePackage getAllPackage() {
        new InsurancePackage(
            compulsory: true,
            autoTax: true,
            thirdPartyAmount: 100_0000d,
            damage: true,
            theft: true,
            driverAmount: 10_0000d,
            passengerAmount: 10_0000d,
            engine: true,
            scratchAmount: 2000d,
            spontaneousLoss: true,
            glass: true,
            glassType: DOMESTIC_1,
            unableFindThirdParty: true,
            iopTotal: true
        ).with {
            it.toIop()
            it
        }
    }

    //最实惠
    static final InsurancePackage getDefaultPackage() {
        InsurancePackage insurancePackage = new InsurancePackage();
        insurancePackage.setAutoTax(true);
        insurancePackage.setCompulsory(true);

        insurancePackage.setThirdPartyAmount(500000d);
        insurancePackage.setThirdPartyIop(true);

        insurancePackage.setDamage(true);
        insurancePackage.setDamageIop(true);


        return insurancePackage;
    }



    final static List getInsurancePackage(Channel channel, InsurancePackage insurancePackage = null) {

        def source = channel.standardAgent ? ALL_FIELDS_REVERSE : ALL_FIELDS
        def cloned = SerializationUtils.clone(source)

        cloned.collect {
            it.insuranceList = it.insuranceList.collect { f ->
                def p = insurancePackage ?: 'ershouche' == channel?.apiPartner?.code ? allPackage : defaultPackage
                f << [defaultValue: p[f.id] ?: f.defaultValue]
            }
            it
        }
    }

}

package com.cheche365.cheche.fanhua.model

import com.cheche365.cheche.core.model.InsuranceCompany

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

/**
 * Created by zhangtc on 2017/12/6.
 */
@Entity
class FanhuaSuite {

    @Id
    Long id
    String inscomcode
    String inscomname
    String riskcode
    String riskname
    String riskkindcode
    String riskkindname
    String insuranceType //1交强险|2商业险
    Integer riskkindType //商业险险种
    @ManyToOne
    InsuranceCompany insuranceCompany

    /**
     * 商业险险种 riskkindType
     */
    public static final Integer OTHER = 0//其他商业险
    public static final Integer DAMAGE = 1//车辆损失险
    public static final Integer THIRD_PARTY = 2//第三者责任险
    public static final Integer DRIVER = 3//司机责任险
    public static final Integer PASSENGER = 4//乘客责任险
    public static final Integer THEFT = 5//全车盗抢险
    public static final Integer SCRATCH = 6//车身划痕险
    public static final Integer SPONTANEOUS_LOSS = 7//自燃损失险
    public static final Integer GLASS = 8//玻璃单独破碎险
    public static final Integer ENGINE = 9//涉水损失险
    public static final Integer UNABLE_FIND_THIRD_PARTY = 10//机动车损失保险无法找到第三方特约险
    public static final Integer DAMAGE_IOP = 11//附加车辆损失险不计免赔
    public static final Integer THIRD_PARTY_IOP = 12//附加第三者责任险不计免赔
    public static final Integer THEFT_IOP = 13//附加全车盗抢险不计免赔
    public static final Integer SPONTANEOUS_LOSS_IOP = 14//附加自燃损失险不计免赔
    public static final Integer ENGINE_IOP = 15//附加涉水损失险不计免赔
    public static final Integer DRIVER_IOP = 16//附加司机责任险不计免赔
    public static final Integer PASSENGER_IOP = 17//附加乘客责任险不计免赔
    public static final Integer SCRATCH_IOP = 18//附加车身划痕险不计免赔


}

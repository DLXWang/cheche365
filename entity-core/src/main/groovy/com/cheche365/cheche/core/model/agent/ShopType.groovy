package com.cheche365.cheche.core.model.agent

import com.cheche365.cheche.core.repository.ShopTypeRepository
import com.cheche365.cheche.core.util.RuntimeUtil
import groovy.transform.Canonical

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * 代理人所在渠道，1:个人、2:洗车店、3:维修中心
 */
@Entity
@Canonical(excludes = ['id'])
class ShopType implements Serializable {
    private static final long serialVersionUID = 1L

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Column(columnDefinition = "VARCHAR(100)")
    String name

    @Column(columnDefinition = "VARCHAR(100)")
    String description


    static class Enum{
        public static ShopType INDIVIDUAL_1 //个人
        public static ShopType CAR_WASH_2 //洗车店
        public static ShopType REPAIR_CENTRE_3 //维修中心

        static {
            RuntimeUtil.loadEnum(ShopTypeRepository, ShopType, Enum)
        }
    }

}

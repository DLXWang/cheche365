package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.context.ApplicationContextHolder

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

import static com.cheche365.cheche.core.util.RuntimeUtil.loadEnum
import static javax.persistence.GenerationType.IDENTITY


@Entity
class VehicleDataSource {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    long id
    String code
    String name
    Integer priority

    static List<InsuranceCompany> allDataSources() {
        ApplicationContextHolder.getApplicationContext().getBean('vehicleDataSourceRepository').findAll()
    }

    static class Enum {

        public static VehicleDataSource BIHU_1
        public static VehicleDataSource CHECHE_2

        static List<VehicleDataSource> ALL

        static {
            ALL = loadEnum('vehicleDataSourceRepository', VehicleDataSource, Enum, { field -> [code: field.name.split('_')[0..-2].join('_')]})
        }

        static findVehicleDataSource(code) {
            ALL.find {
                it.code == code
            }
        }
    }
}

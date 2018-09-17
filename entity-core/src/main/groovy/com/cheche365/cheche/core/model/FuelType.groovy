package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.repository.FuelTypeRepository
import com.cheche365.cheche.core.util.RuntimeUtil

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class FuelType implements Serializable {
    private static final long serialVersionUID = 1L

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Column(columnDefinition = "VARCHAR(45)")
    String name

    @Column(columnDefinition = "VARCHAR(2000)")
    String description

    static class Enum {

        public static FuelType GASOLINE_1, ELECTRICITY_2, DIESEL_3, NATURAL_GAS_4
        public static List<FuelType> ALL

        static {
            ALL = RuntimeUtil.loadEnum(FuelTypeRepository, FuelType, Enum)
        }

    }
}

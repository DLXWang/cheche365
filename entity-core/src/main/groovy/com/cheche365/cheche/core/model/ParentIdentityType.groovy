package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.repository.ParentIdentityTypeRepository
import com.cheche365.cheche.core.util.RuntimeUtil

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class ParentIdentityType implements Serializable {
    private static final long serialVersionUID = 1L

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Column(columnDefinition = "VARCHAR(45)")
    String name

    @Column(columnDefinition = "VARCHAR(2000)")
    String description

    static class Enum {

        public static ParentIdentityType INDIVIDUAL_1, GROUP_2
        public static List<ParentIdentityType> ALL

        static {
            ALL = RuntimeUtil.loadEnum(ParentIdentityTypeRepository, ParentIdentityType, Enum)
        }

    }

}

package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.repository.UseCharacterRepository
import org.springframework.stereotype.Component

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

import static javax.persistence.GenerationType.IDENTITY

/**
 * 行驶证-使用性质
 *
 * GA/T 16.3—2012
 *
 * 代码参考 GA 24.3-2005
 *
 * Created by liheng on 2017/3/24 024.
 */
@Entity
class UseCharacter implements Serializable {
    private static final long serialVersionUID = 1L

    @Id
    @GeneratedValue(strategy = IDENTITY)
    long id
    String code
    String description

    @Component
    static class Enum {

        public static UseCharacter FAMILY_21, BUSINESS_22, ORGANIZATION_23, OTHER_20
        public static List<UseCharacter> USE_CHARACTERS_TOA
        public static List<UseCharacter> ALL

        Enum(UseCharacterRepository useCharacterRepository) {
            FAMILY_21 = useCharacterRepository.findOne(21L)
            BUSINESS_22 = useCharacterRepository.findOne(22L)
            ORGANIZATION_23 = useCharacterRepository.findOne(23L)
            OTHER_20 = useCharacterRepository.findOne(20L)
            USE_CHARACTERS_TOA = [FAMILY_21, BUSINESS_22, ORGANIZATION_23]
            ALL = useCharacterRepository.findAll()
        }

        static findUseCharacter(description) {
            ALL.find {
                it.description == description
            }
        }
    }
}

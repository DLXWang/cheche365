package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.UseCharacter
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Created by liheng on 2017/3/27 027.
 */
@Repository
interface UseCharacterRepository extends CrudRepository<UseCharacter, Long> {

    UseCharacter findFirstByDescription(description)

    List<UseCharacter> findAll()
}

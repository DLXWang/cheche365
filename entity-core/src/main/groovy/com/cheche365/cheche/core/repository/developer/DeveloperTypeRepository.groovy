package com.cheche365.cheche.core.repository.developer

import com.cheche365.cheche.core.model.developer.DeveloperType
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Created by zhengwei on 08/03/2018.
 */

@Repository
interface DeveloperTypeRepository extends CrudRepository<DeveloperType, Long> {
}

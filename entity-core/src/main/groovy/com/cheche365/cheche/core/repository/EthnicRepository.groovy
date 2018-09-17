package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.agent.Ethnic
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Author:   shanxf
 * Date:     2018/9/10 11:43
 */
@Repository
interface EthnicRepository extends CrudRepository<Ethnic,Long> {

}

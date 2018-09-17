package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.agent.ApproveStatus
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Author:   shanxf
 * Date:     2018/9/10 14:14
 */
@Repository
interface ApproveStatusRepository extends CrudRepository<ApproveStatus,Long> {

}

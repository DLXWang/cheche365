package com.cheche365.cheche.core.model.agent

import com.cheche365.cheche.core.model.AutoLoadEnum
import com.cheche365.cheche.core.repository.ApproveStatusRepository
import com.cheche365.cheche.core.util.RuntimeUtil

import javax.persistence.Entity

/**
 * Author:   shanxf
 * Date:     2018/9/11 17:01
 */
@Entity
class ApproveStatus extends AutoLoadEnum implements Serializable{


    static class Enum {
        public static ApproveStatus NOT_APPROVE_1, TO_BE_APPROVE_2, APPROVING_3, SUCCESS_APPROVE_4, FAIL_APPROVE_5
        static {
            RuntimeUtil.loadEnum(ApproveStatusRepository.class, ApproveStatus.class, Enum.class)
        }
    }
}

package com.cheche365.cheche.fanhua.repository

import com.cheche365.cheche.fanhua.model.FanhuaInstitution
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created by zhangtc on 2017/12/15.
 */
@Repository
interface FanhuaInstitutionRepository  extends JpaRepository<FanhuaInstitution,Long> {

}

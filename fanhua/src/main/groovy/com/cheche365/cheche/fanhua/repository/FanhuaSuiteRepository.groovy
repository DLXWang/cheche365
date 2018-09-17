package com.cheche365.cheche.fanhua.repository

import com.cheche365.cheche.fanhua.model.FanhuaSuite
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created by zhangtc on 2017/12/6.
 */
@Repository
interface FanhuaSuiteRepository extends JpaRepository<FanhuaSuite,Long> {

    List<FanhuaSuite> findByRiskcode(String riskcode)
}

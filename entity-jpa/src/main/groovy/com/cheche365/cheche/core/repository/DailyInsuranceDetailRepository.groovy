package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.DailyInsurance
import com.cheche365.cheche.core.model.DailyInsuranceDetail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Created by mahong on 2016/12/2.
 */
@Repository
public interface DailyInsuranceDetailRepository extends JpaRepository<DailyInsuranceDetail, Long> {

    void deleteByDailyInsurance(DailyInsurance dailyInsurance)

    @Query(value = "select * from daily_insurance_detail where daily_insurance=?1", nativeQuery = true)
    List<DailyInsuranceDetail> queryByDailyInsurance(Long dailyInsurance)
}

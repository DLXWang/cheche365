package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.DailyRestartInsurance
import com.cheche365.cheche.core.model.DailyRestartInsuranceDetail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
public interface DailyRestartInsuranceDetailRepository extends JpaRepository<DailyRestartInsuranceDetail, Long> {

    void deleteByDailyRestartInsurance(DailyRestartInsurance restartInsurance)

}

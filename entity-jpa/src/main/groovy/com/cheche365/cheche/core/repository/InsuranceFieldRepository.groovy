package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.abao.InsuranceField
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created by mahong on 2017/1/18.
 */
@Repository
public interface InsuranceFieldRepository extends JpaRepository<InsuranceField, Long> {
    InsuranceField findFirstByCode(String code)
}

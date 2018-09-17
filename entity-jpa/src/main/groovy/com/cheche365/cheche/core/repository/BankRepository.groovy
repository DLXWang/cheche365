package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.Bank
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created by wangjiahuan on 2016/12/22 0022.
 */
@Repository
interface BankRepository extends JpaRepository<Bank, Long> {

    List<Bank> findAll();
}

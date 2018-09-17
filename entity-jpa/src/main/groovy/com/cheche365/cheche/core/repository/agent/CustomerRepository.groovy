package com.cheche365.cheche.core.repository.agent

import com.cheche365.cheche.core.model.agent.Customer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findFirstByNameAndMobile(String name, String mobile)

}

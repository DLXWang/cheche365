package com.cheche365.cheche.core.repository.agent

import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.model.agent.Customer
import com.cheche365.cheche.core.model.agent.CustomerAuto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CustomerAutoRepository extends JpaRepository<CustomerAuto, Long> {

    CustomerAuto findFirstByCustomerAndAuto(Customer customer, Auto auto)

    CustomerAuto findFirstByAutoOrderByIdDesc(Auto auto)

    @Query(value = ''' select c.id,c.name,a.license_plate_no licensePlateNo from user_customer uc 
    left join customer_auto ca on uc.customer = ca.customer left join customer c on c.id = uc.customer
    left join auto a on a.id = ca.auto where uc.user = ?1 group by uc.customer ''', nativeQuery = true)
    List<Object[]> findCustomerAutos(User user)

}

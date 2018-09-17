package com.cheche365.cheche.core.repository.agent

import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.model.agent.Customer
import com.cheche365.cheche.core.model.agent.UserCustomer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserCustomerRepository extends JpaRepository<UserCustomer, Long> {

    UserCustomer findFirstByUserAndCustomer(User user, Customer customer)

    @Query(value = ''' select uc.* from purchase_order po,user_customer uc,customer_auto ca where po.applicant = uc.user 
    and po.auto = ca.auto and uc.customer = ca.customer and po.id = ?1 limit 1 ''', nativeQuery = true)
    UserCustomer findByPurchaseOrder(PurchaseOrder purchaseOrder)

}

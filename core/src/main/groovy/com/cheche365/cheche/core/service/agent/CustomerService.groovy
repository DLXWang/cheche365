package com.cheche365.cheche.core.service.agent

import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.InsuranceBills
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.model.agent.Customer
import com.cheche365.cheche.core.model.agent.CustomerAuto
import com.cheche365.cheche.core.model.agent.UserCustomer
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import com.cheche365.cheche.core.repository.Page
import com.cheche365.cheche.core.repository.WebPurchaseOrderRepository
import com.cheche365.cheche.core.repository.agent.CustomerAutoRepository
import com.cheche365.cheche.core.repository.agent.CustomerRepository
import com.cheche365.cheche.core.repository.agent.UserCustomerRepository
import com.cheche365.cheche.core.service.AutoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by mahong on 16/06/2017.
 */
@Service
@Transactional
class CustomerService {

    @Autowired
    CustomerRepository customerRepository

    @Autowired
    UserCustomerRepository userCustomerRepository

    @Autowired
    CustomerAutoRepository customerAutoRepository

    @Autowired
    AutoService autoService

    @Autowired
    InsuranceRepository insuranceRepository

    @Autowired
    CompulsoryInsuranceRepository ciRepository

    @Autowired
    WebPurchaseOrderRepository orderRepository

    Customer createIfNotExists(User user, Customer customer) {

        Customer existed = customerRepository.findFirstByNameAndMobile(customer.name, customer.mobile)
        if (!existed) {
            existed = customerRepository.save(customer)
        }

        UserCustomer existedUC = userCustomerRepository.findFirstByUserAndCustomer(user, existed)
        if (!existedUC) {
            userCustomerRepository.save(new UserCustomer(user: user, customer: existed))
        }
        existed
    }

    CustomerAuto createCustomerAuto(User user, Long customerId, Auto auto) {
        Auto autoSaved = autoService.saveOrMerge(auto, user, false, new StringBuilder())
        customerAutoRepository.save(new CustomerAuto(customer: new Customer(id: customerId), auto: autoSaved))
    }

    Page<Object> findCustomerAutos(User user, Pageable pageable) {
        customerAutoRepository.findCustomerAutos(user).collect {
            [id: it[0], name: it[1], licensePlateNo: it[2]]
        }.with { customerAutos ->
            new Page<>(pageable.pageNumber, pageable.pageSize, (customerAutos?.size() ?: 0) as Long, customerAutos);
        }
    }

    Map findBillsByCustomer(User user, Long customerId) {
        List<PurchaseOrder> orders = orderRepository.findOrdersByCustomer(user, customerId)
        def bills = orders.collect {
            new InsuranceBills(orderNo: it.orderNo, insurance: insuranceRepository.findByQuoteRecordId(it.objId), ci: ciRepository.findByQuoteRecordId(it.objId))
        }
        [customer: customerRepository.findOne(customerId), bills: bills]
    }
}

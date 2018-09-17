package com.cheche365.cheche.bihu.model

import com.cheche365.cheche.bihu.repository.BihuCustKeyRepository
import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.util.RuntimeUtil
import org.springframework.context.ApplicationContext

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class BihuCustKey {

    private Long id
    private String custKey

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    @Column(name = 'cust_key', columnDefinition = "VARCHAR(32)")
    String getCustKey() {
        return custKey
    }

    void setCustKey(String custKey) {
        this.custKey = custKey
    }

    static class Enum {

        public static List<BihuCustKey> ALL

        static {
            if (RuntimeUtil.isDevEnv()) {
                ALL = [
                    new BihuCustKey(custKey: 'JtfKOghfjkeriN60gyXB51tXALx9FGif'),
                    new BihuCustKey(custKey: 'T6mFGmIYDRD1MLef6hBWtWeGlO1dAPFy'),
                    new BihuCustKey(custKey: 'D0wFV37LTHebRaMLC2xtcHDNQzWcjLxA'),
                    new BihuCustKey(custKey: 'lzAPFLcvd8tf9U3XiKP1toIjhAaN9huJ'),
                    new BihuCustKey(custKey: 'jpslpEKLu2e39vXrKJ23RuMgIXr8Xl2w')
                ]
            } else {
                ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext()
                if (applicationContext != null) {
                    BihuCustKeyRepository bihuCustKeyRepository = applicationContext.getBean(BihuCustKeyRepository)
                    ALL = bihuCustKeyRepository.findAll() as List<BihuCustKey>
                } else {
                    throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "bihu cust key 初始化失败")
                }
            }

        }

    }


}

package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.repository.PaymentStatusRepository
import com.cheche365.cheche.core.util.RuntimeUtil
import groovy.transform.AutoClone
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

import javax.persistence.*

@Entity
@AutoClone
public class PaymentStatus implements Serializable{
    private static final long serialVersionUID = 1L

    private Long id;
    private String status;//支付状态：1.未支付,2.支付成功,3.支付失败
    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(45)", updatable= false)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(columnDefinition = "VARCHAR(2000)", updatable= false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PaymentStatus && EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public static class Enum {
        public static PaymentStatus NOTPAYMENT_1, PAYMENTSUCCESS_2, PAYMENTFAILED_3, CANCEL_4;
        static {
            RuntimeUtil.loadEnum(PaymentStatusRepository, PaymentStatus, Enum)

        }
    }
}

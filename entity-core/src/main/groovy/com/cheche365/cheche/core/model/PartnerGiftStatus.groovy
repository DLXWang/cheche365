package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.context.ApplicationContextHolder
import org.springframework.context.ApplicationContext

import javax.persistence.Entity
import javax.persistence.Id

/**
 * Created by zhaozhong on 2016/3/23.
 */
@Entity
public class PartnerGiftStatus {

    private Long id;
    private String name;
    private String description;

    @Id
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PartnerGiftStatus that = (PartnerGiftStatus) o;

        if (!name.equals(that.name)) return false;
        return description != null ? description.equals(that.description) : that.description == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PartnerCouponStatus{" +
            "name='" + name + '\'' +
            ", description='" + description + '\'' +
            '}';
    }

    public static class Enum {
        public static final PartnerGiftStatus UNUSED;
        public static final PartnerGiftStatus USED;
        public static final PartnerGiftStatus EXPIRE;

        static {
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            def repository = applicationContext.getBean('partnerGiftStatusRepository');
            UNUSED = repository.findByName("UNUSED");
            USED = repository.findByName("USED");
            EXPIRE = repository.findByName("EXPIRE");
        }

        public static PartnerGiftStatus fromGiftStatus(GiftStatus giftStatus) {
            PartnerGiftStatus partnerGiftStatus;
            if (GiftStatus.Enum.CREATED_1.getId().equals(giftStatus.getId())) {
                partnerGiftStatus = UNUSED;
            } else if (GiftStatus.Enum.EXCEEDED_4.getId().equals(giftStatus.getId())) {
                partnerGiftStatus = EXPIRE;
            } else if (GiftStatus.Enum.USED_3.getId().equals(giftStatus.getId())) {
                partnerGiftStatus = USED;
            } else {
                partnerGiftStatus = null;
            }
            return partnerGiftStatus;
        }
    }
}

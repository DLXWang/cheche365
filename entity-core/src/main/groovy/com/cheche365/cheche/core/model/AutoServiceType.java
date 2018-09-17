package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.repository.AutoServiceTypeRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderImageSceneRepository;
import org.springframework.context.ApplicationContext;

import javax.persistence.*;

/**
 * Created by Shanxf on 2016/10/8.
 */
@Entity
public class AutoServiceType {
    private Long id;
    private String name;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    public static class Enum{
        public  static  AutoServiceType VEHICLE_LICENSE;
        static {
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            if (applicationContext != null) {
                AutoServiceTypeRepository autoServiceTypeRepository = applicationContext.getBean(AutoServiceTypeRepository.class);
                VEHICLE_LICENSE = autoServiceTypeRepository.findOne(1L);

            } else {
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "auto service type 初始化失败");
            }
        }
    }
}


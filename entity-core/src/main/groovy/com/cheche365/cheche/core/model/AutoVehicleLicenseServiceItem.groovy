package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.util.RuntimeUtil
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import org.springframework.context.ApplicationContext

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id;

/**
 * Created by Shanxf on 2016/10/8.
 */
@Entity
public class AutoVehicleLicenseServiceItem {
    private Long id;
    private String serviceName;
    private Integer priority;
    private boolean disable;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "service_name")
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Column(columnDefinition = "SMALLINT(1)")
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Column(columnDefinition = "TINYINT(1)")
    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public static class Enum {
        public static AutoVehicleLicenseServiceItem IDCREDIT_1;
        public static AutoVehicleLicenseServiceItem YIQIJIA_3;
        public static AutoVehicleLicenseServiceItem CCINT_4;
        public static AutoVehicleLicenseServiceItem BIHU_5;

        static {
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            if (applicationContext != null) {
                RuntimeUtil.loadEnum('autoVehicleLicenseServiceItemRepository', AutoVehicleLicenseServiceItem, Enum)
            } else {
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "auto vehicle license service item 初始化失败");
            }
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}


package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.repository.DailyInsuranceStatusRepository
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.time.DateUtils
import org.springframework.context.ApplicationContext

import javax.persistence.*

/**
 * Created by mahong on 2016/11/29.
 * 按天买车险-停驶/复驶 状态表
 */
@Entity
class DailyInsuranceStatus {
    private Long id;
    private String status;
    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public DailyInsuranceStatus setId(Long id) {
        this.id = id;
        return this;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getStatus() {
        return status;
    }

    public DailyInsuranceStatus setStatus(String status) {
        this.status = status;
        return this;
    }

    @Column(columnDefinition = "VARCHAR(2000)")
    public String getDescription() {
        return description;
    }

    public DailyInsuranceStatus setDescription(String description) {
        this.description = description;
        return this;
    }

    public static class Enum {
        public static DailyInsuranceStatus UNPROTECTED//未起保
        public static DailyInsuranceStatus PROTECTED//保障中

        public static DailyInsuranceStatus STOP_CALCULATE;//停驶试算
        public static DailyInsuranceStatus STOP_APPLY;//已申请停驶
        public static DailyInsuranceStatus STOPPED;//停驶中
        public static DailyInsuranceStatus RESTART_APPLY;//申请复驶
        public static DailyInsuranceStatus RESTART_INSURED;//复驶已承保
        public static DailyInsuranceStatus RESTART_INSURED_FAILURE;//复驶承保失败
        public static DailyInsuranceStatus RESTARTED;//已复驶
        public static List<DailyInsuranceStatus> ALLOW_STOP_STATUS_LIST //允许申请停驶状态列表

        public static Iterable<DailyInsuranceStatus> ALL;
        public static ALLOW_RESTART ;

        static {
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            if (applicationContext != null) {

                UNPROTECTED = new DailyInsuranceStatus(id: 1, status: "未起保", description: "未起保")
                PROTECTED = new DailyInsuranceStatus(id: 1, status: "保障中", description: "保障中")

                DailyInsuranceStatusRepository statusRepository = applicationContext.getBean(DailyInsuranceStatusRepository.class);
                ALL = statusRepository.findAll();
                STOP_CALCULATE = ALL.find { status -> 1L == status.getId() };
                STOP_APPLY = ALL.find { status -> 2L == status.getId() };
                STOPPED = ALL.find { status -> 3L == status.getId() };
                RESTART_APPLY = ALL.find { status -> 4L == status.getId() };
                RESTART_INSURED = ALL.find { status -> 5L == status.getId() };
                RESTART_INSURED_FAILURE = ALL.find { status -> 6L == status.getId() };
                RESTARTED = ALL.find { status -> 7L == status.getId() };

                ALLOW_RESTART = [STOP_APPLY, STOPPED]
                ALLOW_STOP_STATUS_LIST = [STOP_CALCULATE, RESTARTED]

            } else {
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "DailyInsuranceStatus初始化失败");
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DailyInsuranceStatus && EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    static DailyInsuranceStatus mapStatus(DailyInsuranceStatus dailyInsuranceStatus) {
        Enum.ALLOW_STOP_STATUS_LIST.contains(dailyInsuranceStatus) ? Enum.PROTECTED : dailyInsuranceStatus
    }

    static DailyInsuranceStatus getOrderDailyDisplayStatus(Insurance insurance, List<DailyInsurance> dailyInsurances) {
        (DateUtils.truncatedCompareTo(insurance.getEffectiveDate(), new Date(), Calendar.DAY_OF_MONTH) > 0) ? Enum.UNPROTECTED :
            CollectionUtils.isEmpty(dailyInsurances) ? DailyInsuranceStatus.Enum.PROTECTED : DailyInsuranceStatus.mapStatus(dailyInsurances.get(0).getStatus())
    }
}

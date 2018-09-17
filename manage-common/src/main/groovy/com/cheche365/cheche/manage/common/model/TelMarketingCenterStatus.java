package com.cheche365.cheche.manage.common.model;


import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterStatusRepository;
import org.springframework.context.ApplicationContext;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
public class TelMarketingCenterStatus {
    private Long id;
    private String name;
    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(columnDefinition = "VARCHAR(2000)")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public static class Enum {
        public static final TelMarketingCenterStatus UNTREATED;//未处理

        public static final TelMarketingCenterStatus ALREADY_ORDER;//已成单
        public static final TelMarketingCenterStatus ALREADY_OFFER;//已报价
        public static final TelMarketingCenterStatus ALREADY_NOTIFY;//已告知
        public static final TelMarketingCenterStatus OVERDUE_CONTACT;//到期联系
        public static final TelMarketingCenterStatus NO_OPEN_CITY;//其他城市
        public static final TelMarketingCenterStatus ORDER;//成单
        public static final TelMarketingCenterStatus REFUSE;//拒绝
        public static final TelMarketingCenterStatus NOT_OWNER;//非车主
        public static final TelMarketingCenterStatus CANNOT_CONNECT;//无法接通
        public static final TelMarketingCenterStatus HANG_UP;//挂断
        public static final TelMarketingCenterStatus OTHER_STATUS;//其他状态
        public static final TelMarketingCenterStatus VACANT_NUMBER;//空号
        public static final TelMarketingCenterStatus NO_ANSWER;//无人接听
        public static final TelMarketingCenterStatus NO_EXPIRE;//车险未到期
        public static final TelMarketingCenterStatus ORDER_PAID;//已支付
        public static final TelMarketingCenterStatus ORDER_CANCEL;//已取消
        public static final TelMarketingCenterStatus RE_ORDER;//重新下单
        public static final TelMarketingCenterStatus PURCHASED_BY_OTHER_CHANNEL;//已在其他渠道购买
        public static final TelMarketingCenterStatus REFUND_CONFIRM;//确认退款
        public static final TelMarketingCenterStatus REFUND_CANCEL;//取消退款
        public static final TelMarketingCenterStatus PAYMENT_WAIT;//等待付款
        public static List<TelMarketingCenterStatus> ALLSTATUS;
        public static List<TelMarketingCenterStatus> APPOINTMENT_STATUS;
        static {
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            if (applicationContext != null) {
                TelMarketingCenterStatusRepository telMarketingCenterStatusRepository = applicationContext.getBean(TelMarketingCenterStatusRepository.class);
                UNTREATED = telMarketingCenterStatusRepository.findOne(Integer.toUnsignedLong(1));
                VACANT_NUMBER = telMarketingCenterStatusRepository.findOne(Integer.toUnsignedLong(2));
                NO_ANSWER = telMarketingCenterStatusRepository.findOne(Integer.toUnsignedLong(3));
                NO_EXPIRE = telMarketingCenterStatusRepository.findOne(Integer.toUnsignedLong(4));
                ORDER_PAID = telMarketingCenterStatusRepository.findOne(Integer.toUnsignedLong(5));
                ORDER_CANCEL = telMarketingCenterStatusRepository.findOne(Integer.toUnsignedLong(6));
                RE_ORDER = telMarketingCenterStatusRepository.findOne(Integer.toUnsignedLong(7));
                ALREADY_ORDER = telMarketingCenterStatusRepository.findOne(Integer.toUnsignedLong(10));
                ALREADY_OFFER = telMarketingCenterStatusRepository.findOne(Integer.toUnsignedLong(20));
                ALREADY_NOTIFY = telMarketingCenterStatusRepository.findOne(Integer.toUnsignedLong(30));
                OVERDUE_CONTACT = telMarketingCenterStatusRepository.findOne(Integer.toUnsignedLong(40));
                NO_OPEN_CITY = telMarketingCenterStatusRepository.findOne(Integer.toUnsignedLong(50));
                ORDER = telMarketingCenterStatusRepository.findOne(Integer.toUnsignedLong(60));
                REFUSE = telMarketingCenterStatusRepository.findOne(Integer.toUnsignedLong(70));
                NOT_OWNER = telMarketingCenterStatusRepository.findOne(Integer.toUnsignedLong(80));
                PURCHASED_BY_OTHER_CHANNEL=telMarketingCenterStatusRepository.findOne(Integer.toUnsignedLong(81));
                CANNOT_CONNECT = telMarketingCenterStatusRepository.findOne(Integer.toUnsignedLong(90));
                HANG_UP = telMarketingCenterStatusRepository.findOne(Integer.toUnsignedLong(91));
                OTHER_STATUS = telMarketingCenterStatusRepository.findOne(Integer.toUnsignedLong(92));
                REFUND_CONFIRM = telMarketingCenterStatusRepository.findOne(Integer.toUnsignedLong(93));
                REFUND_CANCEL = telMarketingCenterStatusRepository.findOne(Integer.toUnsignedLong(94));
                PAYMENT_WAIT = telMarketingCenterStatusRepository.findOne(Integer.toUnsignedLong(95));
                ALLSTATUS = Arrays.asList(new TelMarketingCenterStatus[]{UNTREATED,VACANT_NUMBER,NO_ANSWER,NO_EXPIRE,
                    ORDER_PAID, ORDER_CANCEL, RE_ORDER, ALREADY_ORDER, ALREADY_OFFER,
                    ALREADY_NOTIFY, OVERDUE_CONTACT, NO_OPEN_CITY, ORDER, REFUSE, NOT_OWNER, CANNOT_CONNECT,
                    HANG_UP, OTHER_STATUS,PURCHASED_BY_OTHER_CHANNEL,REFUND_CONFIRM,REFUND_CANCEL,PAYMENT_WAIT});
                APPOINTMENT_STATUS = Arrays.asList(TelMarketingCenterStatus.Enum.UNTREATED,
                    TelMarketingCenterStatus.Enum.NO_ANSWER,TelMarketingCenterStatus.Enum.NO_EXPIRE,
                    TelMarketingCenterStatus.Enum.ALREADY_OFFER,TelMarketingCenterStatus.Enum.ALREADY_NOTIFY,
                    TelMarketingCenterStatus.Enum.OVERDUE_CONTACT,TelMarketingCenterStatus.Enum.REFUSE,
                    TelMarketingCenterStatus.Enum.CANNOT_CONNECT,TelMarketingCenterStatus.Enum.HANG_UP);
            }else{
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "TelMarketingCenterStatus 初始化失败");
            }
        }

    }

}

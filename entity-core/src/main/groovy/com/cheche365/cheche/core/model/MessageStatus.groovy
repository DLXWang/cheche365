package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.repository.MessageStatusRepository;
import com.cheche365.cheche.core.repository.OrderStatusRepository;
import org.springframework.context.ApplicationContext;

import javax.persistence.*;

@Entity
public class MessageStatus {

    private Long id;
    private String status;
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
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(columnDefinition = "VARCHAR(200)")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class Enum {
        //等待审核
        public static MessageStatus WAIT_REVIEW;
        //审核失败
        public static MessageStatus REVIEW_FAIL;
        //等待发送
        public static MessageStatus WAIT_SEND;
        //发送成功
        public static MessageStatus SEND_SUCCESS;
        //发送失败
        public static MessageStatus SEND_FAIL;

        public static Iterable<MessageStatus> ALL;

        static {
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            if (applicationContext != null) {
                MessageStatusRepository messageStatusRepository = applicationContext.getBean(MessageStatusRepository.class);
                ALL = messageStatusRepository.findAll();
                WAIT_REVIEW = ALL.find{channel -> 1L == channel.getId()};
                REVIEW_FAIL =  ALL.find{channel -> 2L == channel.getId()};
                WAIT_SEND = ALL.find{channel -> 3L == channel.getId()};
                SEND_SUCCESS = ALL.find{channel -> 4L == channel.getId()};
                SEND_FAIL = ALL.find{channel -> 5L == channel.getId()};
            }else{
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "MessageStatus初始化失败");
            }
        }
    }
}

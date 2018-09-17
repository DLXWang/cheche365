package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.repository.UserSourceRepository;
import org.springframework.context.ApplicationContext;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 用户来源
 * Created by sunhuazhong on 2015/10/27.
 */
@Entity
public class UserSource implements Serializable {

    private static final long serialVersionUID = -4880235583657476377L;
    private Long id;
    private String name;//电话报价
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

    @Column(columnDefinition = "VARCHAR(200)")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class Enum {
        // 电话报价
        public static final UserSource QUOTE_PHONE;
        public static final UserSource WECHAT_FOLLOW;
        public static final UserSource ALIPAY_WALLET;

        static {
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            if (applicationContext != null) {
                UserSourceRepository userSourceRepository = applicationContext.getBean(UserSourceRepository.class);
                QUOTE_PHONE = userSourceRepository.findFirstByName("电话报价");
                WECHAT_FOLLOW = userSourceRepository.findFirstByName("微信关注");
                ALIPAY_WALLET = userSourceRepository.findFirstByName("支付宝钱包");
            }else{
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "User Source 初始化失败");
            }
        }
    }
}

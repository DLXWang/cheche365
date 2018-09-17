package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.repository.TelMarketingCenterSourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class TelMarketingCenterSource implements Serializable {

    private static final long serialVersionUID = 4778597380957983265L;
    private Long id;
    private String name;
    private String description;
    private boolean enable = true;//来源是否可显示，默认为true
    /**
     * 来源类型，默认值为0
     * 0-无
     * 1-预约，包括主动预约和拍照预约
     * 2-活动，包括车船税活动和满减活动等各种活动
     * 3-客服转报价
     * 4-注册无行为
     * 5-续保，包括商业险即将到期和交强险即将到期
     * 6-未支付订单
     * 7-人工导入
     * 8-上年未成单订单
     */
    private int type = 0;

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

    @Column(columnDefinition = "tinyint(1)")
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Column(columnDefinition = "tinyint(1)")
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static class Enum {
        // 主动预约
        public static TelMarketingCenterSource APPOINTMENT;
        // 注册但无行为用户
        public static TelMarketingCenterSource NO_BUY_RECOURD;
        public static TelMarketingCenterSource PHOTO_APPOINTMENT;
        // 有订单未支付用户
        public static TelMarketingCenterSource ORDERS_UNPAY;
        // 支付宝加油服务主动预约
        public static TelMarketingCenterSource ALIPAY_REFUELING_SERVICE_APPOINTMENT;
        // 支付宝服务窗主动预约
        public static TelMarketingCenterSource ALIPAY_SERVICE_WINDOW_APPOINTMENT;
        // 人工导入
        public static TelMarketingCenterSource ARTIFICIAL_IMPORT;
        // 支付宝加油服务拍照预约
        public static TelMarketingCenterSource ALIPAY_REFUELING_SERVICE_PHOTO_APPOINTMENT;
        // 商业险即将到期
        public static TelMarketingCenterSource INSURANCE_EXPIRE_DATE;
        // 交强险即将到期
        public static TelMarketingCenterSource COMPULSORY_INSURANCE_EXPIRE_DATE;
        // 汽车之家主动预约
        public static TelMarketingCenterSource AUTOHOME_APPOINTMENT;
        // 百度地图主动预约
        public static TelMarketingCenterSource BAIDU_APPOINTMENT;
        // 百度地图拍照预约
        public static TelMarketingCenterSource BAIDU_PHOTO_APPOINTMENT;
        // 途虎养车主动预约
        public static TelMarketingCenterSource TUHU_APPOINTMENT;
        // 途虎养车拍照预约
        public static TelMarketingCenterSource TUHU_PHOTO_APPOINTMENT;

        // 车享网主动预约
        public static TelMarketingCenterSource CHE_XIANG_APPOINTMENT;
        // 车享网拍照预约
        public static TelMarketingCenterSource CHE_XIANG_PHOTO_APPOINTMENT;
        // 客服转报价
        public static TelMarketingCenterSource CUSTOMER_TO_QUOTE;

        //上年未成单订单
        public static TelMarketingCenterSource ORDERS_LAST_YEAR_UNPAY;

        //购车险送油卡
        public static List<TelMarketingCenterSource> SOURCE_LIST;

        // 申请退款用户
        public static TelMarketingCenterSource ORDERS_REFUND;

        // 报价用户
        public static TelMarketingCenterSource QUOTE_RECORD;

        //登录用户
        public static TelMarketingCenterSource USER_LOGIN_INFO;

        //续保用户
        public static TelMarketingCenterSource RENEWAL_INSURANCE;

        //续保用户首次提醒
        public static TelMarketingCenterSource RENEWAL_ONCE;


        public static Map<Channel,TelMarketingCenterSource> SOURCE_CHANNEL_MAP;

        static {
            Logger logger= LoggerFactory.getLogger(Enum.class);
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            if (applicationContext != null) {
                TelMarketingCenterSourceRepository telMarketingCenterSourceRepository = applicationContext.getBean(TelMarketingCenterSourceRepository.class);
                telMarketingCenterSourceRepository.findAll().forEach(telMarketingCenterSource -> {
                    try {
                        Enum.class.getDeclaredField(telMarketingCenterSource.getName()).set(Enum.class.newInstance(),telMarketingCenterSource);
                    } catch (IllegalAccessException  | InstantiationException | NoSuchFieldException e) {
                        logger.info("初始化枚举类TelMarketingCenterSource, 数据库中存在id {}, 常量不存在, 忽略初始化该常量",telMarketingCenterSource.getId());
                    }
                });
                SOURCE_LIST = telMarketingCenterSourceRepository.findByEnable(true);
                initSourceChannelMap();
            } else {
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "TelMarketingCenterSource 初始化失败");
            }
        }
        public static TelMarketingCenterSource getById(Long id) {
            for (TelMarketingCenterSource telMarketingCenterSource: SOURCE_LIST) {
                if (telMarketingCenterSource.getId().equals(id)) {
                    return telMarketingCenterSource;
                }
            }
            return null;
        }

        public static void initSourceChannelMap(){
            SOURCE_CHANNEL_MAP = new HashMap<Channel,TelMarketingCenterSource>() {
                {
                    put(Channel.Enum.ALIPAY_21,ALIPAY_SERVICE_WINDOW_APPOINTMENT);
                    put(Channel.Enum.PARTNER_BAIDU_15,BAIDU_APPOINTMENT);
                    put(Channel.Enum.PARTNER_TUHU_203,TUHU_APPOINTMENT);
                }
            };
        }

        public static boolean fromMarketing(Long id) {
            return fromMarketing(getById(id));
        }

        public static boolean fromMarketing(TelMarketingCenterSource souece){
           return  souece.getName().matches("MARKETING_.*");
        }

        public static String getMarketingId(Long id) {
            if (fromMarketing(id)) {
                return getById(id).getName().split("MARKETING_")[1];
            }
            return "";
        }
    }




}

package com.cheche365.cheche.ordercenter.constants;

import org.apache.commons.collections.map.HashedMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 电销系统筛选类型
 * Created by sunhuazhong on 2016/4/22.
 */
public class TelMarketingCenterType {

    private Integer id;
    private String name;

    private TelMarketingCenterType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static class Enum {

        public static TelMarketingCenterType APPOINTMENT;

        public static TelMarketingCenterType ACTIVITY;

        public static TelMarketingCenterType CUSTOMER_TO_QUOTE;

        public static TelMarketingCenterType REGISTER_NO_OPERATION;

        public static TelMarketingCenterType RENEWAL;

        public static TelMarketingCenterType UN_PAY_ORDER;

        public static TelMarketingCenterType MANUAL_IMPORT;

        public static TelMarketingCenterType UN_ORDER_ORDER;

        public static TelMarketingCenterType REFUND;

        public static TelMarketingCenterType QUOTE_RECORD;

        public static TelMarketingCenterType LOGIN;

        public static List<Integer> DATA_A_TYPES_ID = new ArrayList();
        public static List<Integer> DATA_B_TYPES_ID = new ArrayList();
        public static List<Integer> DATA_C_TYPES_ID = new ArrayList();
        public static List<Integer> DATA_D_TYPES_ID = new ArrayList();
        public static Map<String,List<Integer>> DATA_TYPE_MAP = new HashedMap();
        public static Map<Integer,TelMarketingCenterType> All_TYPE_MAP = new HashedMap();

        static {
            APPOINTMENT = new TelMarketingCenterType(1, "预约");
            ACTIVITY = new TelMarketingCenterType(2, "活动");
            CUSTOMER_TO_QUOTE = new TelMarketingCenterType(3, "客服转报价");
            REGISTER_NO_OPERATION = new TelMarketingCenterType(4, "注册无行为");
            RENEWAL = new TelMarketingCenterType(5, "续保");
            UN_PAY_ORDER = new TelMarketingCenterType(6, "未支付订单");
            MANUAL_IMPORT = new TelMarketingCenterType(7, "人工导入");
            UN_ORDER_ORDER = new TelMarketingCenterType(8, "未成单订单");
            REFUND = new TelMarketingCenterType(9, "退款订单");
            QUOTE_RECORD = new TelMarketingCenterType(10, "报价");
            LOGIN = new TelMarketingCenterType(11, "登录用户");

            DATA_A_TYPES_ID.add(UN_PAY_ORDER.getId());//未支付订单
            DATA_A_TYPES_ID.add(CUSTOMER_TO_QUOTE.getId());//客服转报价
            DATA_A_TYPES_ID.add(RENEWAL.getId());//续保
            DATA_B_TYPES_ID.add(REFUND.getId());//退款订单
            DATA_B_TYPES_ID.add(QUOTE_RECORD.getId());//报价
            DATA_B_TYPES_ID.add(APPOINTMENT.getId());//预约
            DATA_C_TYPES_ID.add(ACTIVITY.getId());//活动
            DATA_C_TYPES_ID.add(REGISTER_NO_OPERATION.getId());//注册无行为
            DATA_C_TYPES_ID.add(UN_ORDER_ORDER.getId());//未成单订单
            DATA_D_TYPES_ID.add(LOGIN.getId());//登录用户
            DATA_D_TYPES_ID.add(MANUAL_IMPORT.getId());//人工导入

            DATA_TYPE_MAP.put("A",DATA_A_TYPES_ID);
            DATA_TYPE_MAP.put("B",DATA_B_TYPES_ID);
            DATA_TYPE_MAP.put("C",DATA_C_TYPES_ID);
            DATA_TYPE_MAP.put("D",DATA_D_TYPES_ID);

            All_TYPE_MAP.put(1,APPOINTMENT);
            All_TYPE_MAP.put(2,ACTIVITY);
            All_TYPE_MAP.put(3,CUSTOMER_TO_QUOTE);
            All_TYPE_MAP.put(4,REGISTER_NO_OPERATION);
            All_TYPE_MAP.put(5,RENEWAL);
            All_TYPE_MAP.put(6,UN_PAY_ORDER);
            All_TYPE_MAP.put(7,MANUAL_IMPORT);
            All_TYPE_MAP.put(8,UN_ORDER_ORDER);
            All_TYPE_MAP.put(9,REFUND);
            All_TYPE_MAP.put(10,QUOTE_RECORD);
            All_TYPE_MAP.put(11,LOGIN);


        }

        public static List<TelMarketingCenterType> getAllType() {
            return Arrays.asList(
                APPOINTMENT, ACTIVITY, CUSTOMER_TO_QUOTE,
                REGISTER_NO_OPERATION, RENEWAL, UN_PAY_ORDER, MANUAL_IMPORT, UN_ORDER_ORDER, REFUND, QUOTE_RECORD,LOGIN);
        }

    }
}

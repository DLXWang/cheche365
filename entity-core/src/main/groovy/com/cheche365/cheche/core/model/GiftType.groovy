package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.util.RuntimeUtil

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ForeignKey
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Transient

@Entity
class GiftType extends AutoLoadEnum{
    private GiftTypeUseType useType;
    private Integer category; //1：代金券 2：抵扣券 3：折扣券 4：礼包 5：兑换码 6：实物礼品
    private String categoryName;
    private List<String> names;
    private Boolean disable;
    private InternalUser operator;
    private Date createTime; // 创建时间
    private Date updateTime; // 更新时间
    private Boolean deliveryFlag;//是否配送


    @ManyToOne
    @JoinColumn(name = "use_type", foreignKey = @ForeignKey(name = "FK_GIFT_TYPE_REF_USE_TYPE", foreignKeyDefinition = "FOREIGN KEY (gift_type_use_type) REFERENCES gift_type_use_type(id)"))
    public GiftTypeUseType getUseType() {
        return useType;
    }

    public void setUseType(GiftTypeUseType useType) {
        this.useType = useType;
    }

    @Column(columnDefinition = "TINYINT(3)")
    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    @Column(columnDefinition = "VARCHAR(50)")
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Transient
    public List<String> getNames() {
        if (this.description != null && this.description.contains("&")) {
            return Arrays.asList(this.description.split("&"));
        }
        return Arrays.asList(this.name);
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    @Column(columnDefinition = "TINYINT(4)")
    public Boolean getDisable() {
        return disable;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey = @ForeignKey(name = "gift_type_ref_internal_user", foreignKeyDefinition = "FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Column(columnDefinition = "TINYINT(4)")
    public Boolean getDeliveryFlag() {
        return deliveryFlag;
    }

    public void setDeliveryFlag(Boolean deliveryFlag) {
        this.deliveryFlag = deliveryFlag;
    }

    public static class Enum {
        public static GiftType FUELCARD_1, WECHATRED_2, COUPON_3, GIFT_CARD_4, WASH_CAR_COUPON_5,
                               OIL_COUPON_6, MAINTENANCE_COUPON_7, TIRE_DISCOUNT_COUPON_8, CAR_CLEANER_9,
                               DRIVING_RECORDER_10, LETV_11, IWATCH_12, CARTOOLBOX_14, SAFETY_HAMMER_15, FRESH_GIFT_PACK_16,
                               REFRESH_GIFT_PACK_17, ICE_GIFT_PACK_18, COOL_GIFT_PACK_19, AUTO_TAX_20, FUEL_CARD_PACK_21,
                               MOBILE_POWER_22, MOBILE_HARD_DISK_23, FULL_SEND_GIFT_PACK_25, PERFECT_DRIVER_CERTIFICATE_26,
                               TEL_MARKETING_DISCOUNT_27, FULL_FUELCARD_28, INSURE_GIVE_GIFT_PACK_29, JINGDONG_CARD_30, XIAOMI_MOBILE_POWER_31,
                               HAIER_AIR_CLEANER_32, LE_DRIVING_RECORDER_33, CASH_34, JINGDONG_CARD_PACK_35,CASH_36,CASH_37;

        public static List<Long> BILLABLE_GIFTS;  //可抵扣礼物类型
        public static List<GiftType> BILLABLE_GIFTS_TYPE;  //可抵扣礼物类型
        public static List<GiftType> ALL;

        public static Map<Integer, String> CATEGORY_MAPPING = new HashMap<>();

        static  {
            RuntimeUtil.loadEnum('giftTypeRepository', GiftType, Enum)
            BILLABLE_GIFTS_TYPE = [WECHATRED_2, COUPON_3, GIFT_CARD_4, FRESH_GIFT_PACK_16, REFRESH_GIFT_PACK_17, ICE_GIFT_PACK_18, COOL_GIFT_PACK_19, AUTO_TAX_20, FUEL_CARD_PACK_21, FULL_SEND_GIFT_PACK_25, PERFECT_DRIVER_CERTIFICATE_26, FULL_FUELCARD_28, INSURE_GIVE_GIFT_PACK_29]
            BILLABLE_GIFTS = BILLABLE_GIFTS_TYPE.collect {it.id}

            CATEGORY_MAPPING = [
                    1: "代金券",
                    2: "抵扣券",
                    3: "折扣券",
                    4: "优惠券礼包",
                    5: "兑换码",
                    6: "实物礼品"
            ]
        }
    }

    public static String getGiftTypeCategory(Integer category) {
        return Enum.CATEGORY_MAPPING.get(category) == null ? "" : Enum.CATEGORY_MAPPING.get(category);
    }

}

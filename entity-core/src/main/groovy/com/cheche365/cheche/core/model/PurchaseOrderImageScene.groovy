package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.util.RuntimeUtil
import groovy.transform.Canonical
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

import javax.persistence.*

@Entity
@Canonical
class PurchaseOrderImageScene {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id

    @Column(columnDefinition = "VARCHAR(50)")
    private String name

    @Column(columnDefinition = "tinyint(1)")
    private boolean csIgnore //车车客服忽略图片，不用审核，cs means customer service

    static class Enum {
        //订单支付成功图片上传-过户车
        public static PurchaseOrderImageScene TRANSFER_OWNERSHIP_2
        //订单支付成功图片上传-外地车
        public static PurchaseOrderImageScene OTHER_PROVINCE_1
        //订单支付成功图片上传-脱保车
        public static PurchaseOrderImageScene OVERDUE_3
        //订单支付成功图片上传-北京车
        public static PurchaseOrderImageScene BEI_JING_4
        //安心按天买车险核保图片上传
        public static PurchaseOrderImageScene DAILY_INSURE_5
        //安心按天买车险复驶图片上传
        public static PurchaseOrderImageScene DAILY_RESTART_6
        //华安图片上传
        public static PurchaseOrderImageScene SINOSAFE_INSURE_7
        //泛华图片上传
        public static PurchaseOrderImageScene BAOXIAN_INSURE_8
        //自定义图片上传
        public static PurchaseOrderImageScene API_CUSTOM_9
        //小鳄鱼图片上传
        public static PurchaseOrderImageScene AGENT_PARSER_INSURE_10
        //验车图片上传
        public static PurchaseOrderImageScene VEHICLE_EXAMINATIOS_11
        //出单图片上传
        public static PurchaseOrderImageScene ORDER_FINISHED_12

        public static List<PurchaseOrderImageScene> THIRD_PARTY_IMAGE_SCENES

        static {
            RuntimeUtil.loadEnum('purchaseOrderImageSceneRepository', PurchaseOrderImageScene, Enum)
            THIRD_PARTY_IMAGE_SCENES = [DAILY_INSURE_5, DAILY_RESTART_6, SINOSAFE_INSURE_7, BAOXIAN_INSURE_8, API_CUSTOM_9, AGENT_PARSER_INSURE_10, VEHICLE_EXAMINATIOS_11, ORDER_FINISHED_12]
        }
    }

    public Long getId() {
        return id;
    }

    @Override
    boolean equals(Object o) {
        return o instanceof PurchaseOrderImageScene && EqualsBuilder.reflectionEquals(this, o)
    }

    @Override
    int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this)
    }

}

package com.cheche365.cheche.operationcenter.web.model.marketing;

import com.cheche365.cheche.core.model.GiftType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yinJianBin on 2017/4/24.
 */
public class GiftTypeViewModel {
    private String id;                  //编号
    private String giftName;            //礼品名称
    private String giftType;            //礼品类型
    private String giftStatus;          //礼品状态
    private String operator;            //操作人
    private String description;         //描述
    private String useType;             //使用类型
    private String deliveryFlag;        //是否配送

    private static Map<Integer, String> CATEGORY_MAPPING_SHOWED = new HashMap<>();

    static {
        CATEGORY_MAPPING_SHOWED.put(4, "买送礼品");
        CATEGORY_MAPPING_SHOWED.put(6, "再送礼品");
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public String getGiftType() {
        return giftType;
    }

    public void setGiftType(String giftType) {
        this.giftType = giftType;
    }

    public String getGiftStatus() {
        return giftStatus;
    }

    public void setGiftStatus(String giftStatus) {
        this.giftStatus = giftStatus;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUseType() {
        return useType;
    }

    public void setUseType(String useType) {
        this.useType = useType;
    }

    public String getDeliveryFlag() {
        return deliveryFlag;
    }

    public void setDeliveryFlag(String deliveryFlag) {
        this.deliveryFlag = deliveryFlag;
    }

    public static Map<Integer, String> getCategoryMappingShowed() {
        return CATEGORY_MAPPING_SHOWED;
    }

    public static void setCategoryMappingShowed(Map<Integer, String> categoryMappingShowed) {
        CATEGORY_MAPPING_SHOWED = categoryMappingShowed;
    }

    public static GiftTypeViewModel createViewModel(GiftType giftType) {
        GiftTypeViewModel giftTypeViewModel = new GiftTypeViewModel();
        giftTypeViewModel.setId(giftType.getId() + "");
        giftTypeViewModel.setGiftName(giftType.getName());
        giftTypeViewModel.setDescription(giftType.getDescription());
        giftTypeViewModel.setUseType(giftType.getUseType() == null ? "" : giftType.getUseType().getName());
        giftTypeViewModel.setGiftType(CATEGORY_MAPPING_SHOWED.get(giftType.getCategory()) == null ? GiftType.getGiftTypeCategory(giftType.getCategory()) : CATEGORY_MAPPING_SHOWED.get(giftType.getCategory()));
        giftTypeViewModel.setOperator(giftType.getOperator() == null ? "" : giftType.getOperator().getName());
        giftTypeViewModel.setGiftStatus(giftType.getDisable() ? "已禁用" : "已启用");
        giftTypeViewModel.setDeliveryFlag(giftType.getDeliveryFlag() ? "是" : "否");
        return giftTypeViewModel;
    }
}

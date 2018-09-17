package com.cheche365.cheche.partner.model;

import com.cheche365.cheche.core.serializer.FormattedDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

/**
 * Created by mahong on 2016/2/17.
 */
public class BaiduPolicyCar {
    private String city; //城市代码（国标，如北京市、110000）(必填)
    private String plate_num; //车牌号车牌号(必填)
    private String engine_num; //发动机号(非必填)
    private String frame_num; //车架号(非必填)
    private Date policy_end_date; //保险到期时间(非必填)
    private Date buy_date; //车辆购买时间(非必填)

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPlate_num() {
        return plate_num;
    }

    public void setPlate_num(String plate_num) {
        this.plate_num = plate_num;
    }

    public String getEngine_num() {
        return engine_num;
    }

    public void setEngine_num(String engine_num) {
        this.engine_num = engine_num;
    }

    public String getFrame_num() {
        return frame_num;
    }

    public void setFrame_num(String frame_num) {
        this.frame_num = frame_num;
    }

    @JsonSerialize(using = FormattedDateSerializer.class)
    public Date getPolicy_end_date() {
        return policy_end_date;
    }

    public void setPolicy_end_date(Date policy_end_date) {
        this.policy_end_date = policy_end_date;
    }

    @JsonSerialize(using = FormattedDateSerializer.class)
    public Date getBuy_date() {
        return buy_date;
    }

    public void setBuy_date(Date buy_date) {
        this.buy_date = buy_date;
    }
}

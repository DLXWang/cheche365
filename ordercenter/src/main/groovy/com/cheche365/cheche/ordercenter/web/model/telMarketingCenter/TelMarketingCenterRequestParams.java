package com.cheche365.cheche.ordercenter.web.model.telMarketingCenter;

import com.cheche365.cheche.manage.common.model.TelMarketingCenterStatus;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sunhuazhong on 2016/4/19.
 */
public class TelMarketingCenterRequestParams extends com.cheche365.cheche.manage.common.model.PublicQuery{
    private String startTime;//开始时间
    private String endTime;//结束时间
    private Integer timeSlot;//时间区间
    private Long userId;//操作人id
    private Integer type;//查询类型，1-呼出记录；2-短信数量；3-报价记录；4-成单记录；5-整体情况；6-预约数据；7-数据量
    private Long status;//处理状态id
    private Long source;//数据来源id
    private String mobileNo;//查询手机号

    private Long channelId;//电销渠道
    private Integer telType;//电销类型
    private Integer areaType;//用户登录区域
    private String expireTime;//车险到期日止
    private String[] channelIds;//电销渠道，多选
    private String[] telTypes;//电销类型，多选
    private Long[] orderStatus;//出单状态
    private Long[] areaId;//开通城市
    private Integer handleMode;//处理方式，1按用户查询；2按行为查询
    private Integer isHandled;//是否已处理，0为未处理；1为已处理

    private Long id;
    private Integer draw;
    private String dataLevel;//数据级别
    private String[] operatorIds;//操作人ids
    private String[] operatorAssignNums;//每个操作人被分配的数量
    private String renewalDate;//续保年份

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDataLevel() {
        return dataLevel;
    }

    public void setDataLevel(String dataLevel) {
        this.dataLevel = dataLevel;
    }

    public Integer getHandleMode() {
        return handleMode;
    }

    public void setHandleMode(Integer handleMode) {
        this.handleMode = handleMode;
    }

    public Integer getIsHandled() {
        return isHandled;
    }

    public void setIsHandled(Integer isHandled) {
        this.isHandled = isHandled;
    }

    public Long[] getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Long[] orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getDraw() {
        return draw;
    }

    public void setDraw(Integer draw) {
        this.draw = draw;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(Integer timeSlot) {
        this.timeSlot = timeSlot;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getSource() {
        return source;
    }

    public void setSource(Long source) {
        this.source = source;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Integer getTelType() {
        return telType;
    }

    public void setTelType(Integer telType) {
        this.telType = telType;
    }

    public Integer getAreaType() {
        return areaType;
    }

    public void setAreaType(Integer areaType) {
        this.areaType = areaType;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String[] getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(String[] channelIds) {
        this.channelIds = channelIds;
    }

    public String[] getTelTypes() {
        return telTypes;
    }

    public void setTelTypes(String[] telTypes) {
        this.telTypes = telTypes;
    }

    public Long[] getAreaId() {
        return areaId;
    }

    public void setAreaId(Long[] areaId) {
        this.areaId = areaId;
    }

    public String[] getOperatorIds() {

        return operatorIds;
    }

    public void setOperatorIds(String[] operatorIds) {
        this.operatorIds = operatorIds;
    }

    public String[] getOperatorAssignNums() {
        return operatorAssignNums;
    }

    public void setOperatorAssignNums(String[] operatorAssignNums) {
        this.operatorAssignNums = operatorAssignNums;
    }

    public String getRenewalDate() { return renewalDate; }

    public void setRenewalDate(String renewalDate) { this.renewalDate = renewalDate; }

    public void clearDataForMobile() {
        this.setStartTime(null);
        this.setEndTime(null);
        this.setTimeSlot(null);
        this.setUserId(null);
        this.setType(null);
        this.setStatus(null);
        this.setChannelIds(null);
        this.setTelTypes(null);
        this.setAreaId(null);
//        this.setOperatorIds(null);
    }

    public List<Integer> getTypeList() {
        if (this.type != null) {
            if (this.type == 1) {
                return Arrays.asList(1, 4);
            } else {
                return Arrays.asList(this.type);
            }
        }
        return null;
    }

    public Long getOperationStatusId() {
        if (this.type != null && this.type == 4) {
            return TelMarketingCenterStatus.Enum.ORDER.getId();
        }
        return null;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("currentPage:").append(getCurrentPage()).append(";");
        sb.append("pageSize:").append(getPageSize()).append(";");
        sb.append("startTime:").append(this.startTime).append(";");
        sb.append("endTime:").append(this.endTime).append(";");
        sb.append("timeSlot:").append(this.timeSlot).append(";");
        sb.append("userId:").append(this.userId).append(";");
        sb.append("type:").append(this.type).append(";");
        sb.append("status:").append(this.status).append(";");
        sb.append("source:").append(this.source).append(";");
        sb.append("mobileNo:").append(this.mobileNo).append(";");
        sb.append("areaType:").append(this.areaType).append(";");
        sb.append("expireTime:").append(this.expireTime).append(";");
        sb.append("areaId").append(this.areaId).append(";");
        sb.append("dataLevel:").append(this.dataLevel).append(";");
        if (ArrayUtils.isEmpty(this.channelIds)) {
            sb.append("channelIds:").append("").append(";");
        } else {
            sb.append("channelIds:");
            for (String channelIdStr : this.channelIds) {
                sb.append(channelIdStr).append(",");
            }
            sb.append(";");
        }
        if (ArrayUtils.isEmpty(this.telTypes)) {
            sb.append("telTypes:").append("").append(";");
        } else {
            sb.append("telTypes:");
            for (String telType : this.telTypes) {
                sb.append(telType).append(",");
            }
            sb.append(";");
        }
        if (ArrayUtils.isEmpty(this.areaId)) {
            sb.append("areaIds:").append("").append(";");
        } else {
            sb.append("areaIds:");
            for (Long areaId : this.areaId) {
                sb.append(areaId).append(",");
            }
            sb.append(";");
        }
        sb.append("renewalDate:").append(this.renewalDate).append(";");
        return sb.toString();
    }

}

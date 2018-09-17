package com.cheche365.cheche.ordercenter.web.model.order;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.DailyInsurance;
import com.cheche365.cheche.core.model.DailyInsuranceDetail;
import com.cheche365.cheche.core.model.DailyRestartInsurance;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wangxq on 2016/12/9 0002.
 */
public class DailyInsuranceViewModel {

    private String createTime;               //创建时间
    private String beginDate;                    // 停驶时间
    private String endDate;                 // 停驶结束时间
    private Long stopDays;              // 停驶天数
    private Double refundAmount;             // 返还金额
    private String insurancePackage;        //险种详情

    private String status;                          // 状态
    private Long totalStopDays;             // 累计停驶天数
    private Long totalRestartDays;         // 累计复驶天数
    private Double totalRefundAmount;             // 累计返还金额
    private Double totalPaidAmount;               // 累计再次支付金额
    private Double premium;                       // 商业险保费

    private List<DailyRestartViewModel> dailyRestartViewModelList;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Long getStopDays() {
        return stopDays;
    }

    public void setStopDays(Long stopDays) {
        this.stopDays = stopDays;
    }

    public Double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(Double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public Double getPremium() {
        return premium;
    }

    public void setPremium(Double premium) {
        this.premium = premium;
    }

    public String getInsurancePackage() {
        return insurancePackage;
    }

    public void setInsurancePackage(String insurancePackage) {
        this.insurancePackage = insurancePackage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTotalStopDays() {
        return totalStopDays;
    }

    public void setTotalStopDays(Long totalStopDays) {
        this.totalStopDays = totalStopDays;
    }

    public Long getTotalRestartDays() {
        return totalRestartDays;
    }

    public void setTotalRestartDays(Long totalRestartDays) {
        this.totalRestartDays = totalRestartDays;
    }

    public Double getTotalRefundAmount() {
        return totalRefundAmount;
    }

    public void setTotalRefundAmount(Double totalRefundAmount) {
        this.totalRefundAmount = totalRefundAmount;
    }

    public Double getTotalPaidAmount() {
        return totalPaidAmount;
    }

    public void setTotalPaidAmount(Double totalPaidAmount) {
        this.totalPaidAmount = totalPaidAmount;
    }

    public List<DailyRestartViewModel> getDailyRestartViewModelList() {
        return dailyRestartViewModelList;
    }

    public void setDailyRestartViewModelList(List<DailyRestartViewModel> dailyRestartViewModelList) {
        this.dailyRestartViewModelList = dailyRestartViewModelList;
    }

    public static List<DailyInsuranceViewModel> createViewModel(List<DailyInsurance> dailyInsurances, Map<Long,List<DailyRestartInsurance>> dailyRestartMap) {

        List<DailyInsuranceViewModel> list = new ArrayList<>();
        for (DailyInsurance dailyInsurance : dailyInsurances) {
            DailyInsuranceViewModel model = new DailyInsuranceViewModel();
            model.setCreateTime(DateUtils.getDateString(dailyInsurance.getCreateTime(),DateUtils.DATE_LONGTIME24_PATTERN));
            model.setBeginDate(DateUtils.getDateString(dailyInsurance.getBeginDate(),DateUtils.DATE_SHORTDATE_PATTERN));
            model.setEndDate(DateUtils.getDateString(dailyInsurance.getEndDate(),DateUtils.DATE_SHORTDATE_PATTERN));
            model.setStopDays(DateUtils.getDaysBetween(dailyInsurance.getEndDate(), dailyInsurance.getBeginDate()) + 1);
            model.setRefundAmount(dailyInsurance.getTotalRefundAmount());
            List<DailyRestartInsurance> dailyRestartInsuranceList=dailyRestartMap.get(dailyInsurance.getId());
            List<DailyRestartViewModel> dailyRestartViewModelList=new ArrayList<>();
            if(CollectionUtils.isNotEmpty(dailyRestartInsuranceList)){
                for(DailyRestartInsurance dailyRestartInsurance:dailyRestartInsuranceList){
                    DailyRestartViewModel dailyRestartViewModel=model.new DailyRestartViewModel();
                    dailyRestartViewModel.setCreateTime(DateUtils.getDateString(dailyRestartInsurance.getCreateTime(),DateUtils.DATE_LONGTIME24_PATTERN));
                    dailyRestartViewModel.setBeginDate(DateUtils.getDateString(dailyRestartInsurance.getBeginDate(),DateUtils.DATE_SHORTDATE_PATTERN));
                    dailyRestartViewModel.setEndDate(DateUtils.getDateString(dailyRestartInsurance.getEndDate(),DateUtils.DATE_SHORTDATE_PATTERN));
                    dailyRestartViewModel.setPaidAmount(dailyRestartInsurance.getPaidAmount());
                    dailyRestartViewModel.setRestartDays(DateUtils.getDaysBetween(dailyRestartInsurance.getEndDate(), dailyRestartInsurance.getBeginDate()) + 1);
                    dailyRestartViewModelList.add(dailyRestartViewModel);
                }
            }
            model.setDailyRestartViewModelList(dailyRestartViewModelList);
            List<DailyInsuranceDetail> dailyInsuranceDetails = dailyInsurance.getDailyInsuranceDetails();
            StringBuffer detailStr = new StringBuffer();
            for (DailyInsuranceDetail dailyInsuranceDetail : dailyInsuranceDetails) {
                detailStr.append(dailyInsuranceDetail.getName()).append(",");
            }
            model.setInsurancePackage(objFormatString(detailStr));
            list.add(model);
        }
        return list;
    }


    private static String objFormatString(StringBuffer stb) {
        //stb可能为null
        return stb.toString().endsWith(",") ? stb.toString().substring(0, stb.lastIndexOf(",")) : stb.toString();
    }

    class DailyRestartViewModel{
        private String createTime;
        private String beginDate;
        private String endDate;
        private Double paidAmount;
        private Double premium;
        private Long restartDays;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getBeginDate() {
            return beginDate;
        }

        public void setBeginDate(String beginDate) {
            this.beginDate = beginDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public Double getPaidAmount() {
            return paidAmount;
        }

        public void setPaidAmount(Double paidAmount) {
            this.paidAmount = paidAmount;
        }

        public Double getPremium() {
            return premium;
        }

        public void setPremium(Double premium) {
            this.premium = premium;
        }

        public Long getRestartDays() {
            return restartDays;
        }

        public void setRestartDays(Long restartDays) {
            this.restartDays = restartDays;
        }
    }
}

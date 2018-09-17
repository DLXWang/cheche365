package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.BusinessActivity;
import com.cheche365.cheche.core.model.CooperationMode;
import com.cheche365.cheche.core.repository.BusinessActivityRepository;
import com.cheche365.cheche.core.service.AgentService;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by guoweifu on 2016/1/13.
 */
@Service
public class SettlementReportService {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private BusinessActivityRepository businessActivityRepository;


    public Map<String, Object> getContentParam() {
        // 结算日期
        Date settlementDate = getSettlementDate();
        String settlementDateStr = DateUtils.getDateString(settlementDate, "yyyy年MM月dd日");

        // 系统提醒邮件内容参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("settlementDate", settlementDateStr);//结算日期

        /**
         * 总报表
         */
        setSumReportData(paramMap);

        /**
         * 普通客户报表
         */
        setCustomerReportData(paramMap);

        /**
         * 代理人报表
         */
        setAgentReportData(paramMap);


        /**
         * CPS渠道报表
         */
        setCPSChannelReportData(paramMap);
        return paramMap;
    }

    private Date getSettlementDate() {
        Calendar calendar = Calendar.getInstance();//得到日历
        calendar.setTime(new Date());//把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -1);//设置为前一天
        return calendar.getTime();//得到前一天的时间
    }

    private void setCPSChannelReportData(Map<String, Object> paramMap) {
        // 累计保单总金额（含线上、线下，优惠后金额）
        Double sumPaidAmount = purchaseOrderService.getSumPaidAmount(4);
        // 累计到账总金额（线上，优惠后金额）
        Double onLineSumPaidAmount = purchaseOrderService.getOnLineSumPaidAmount(4);
        // 累计保单数量（支付成功，含订单完成）
        Long sumPaidOrderCount = purchaseOrderService.getSumPaidOrderCount(4);

        // 昨天累计保单总金额（含线上、线下，优惠后金额）
        Double lastSumPaidAmount = purchaseOrderService.getYesterdaySumPaidAmount(4);
        // 昨天累计到账总金额（线上，优惠后金额）
        Double lastOnLineSumPaidAmount = purchaseOrderService.getYesterdayOnLineSumPaidAmount(4);
        // 昨天累计保单数量（支付成功，含订单完成）
        Long lastSumPaidOrderCount = purchaseOrderService.getYesterdaySumPaidOrderCount(4);

        // 渠道合作统计：渠道数量
        Long channelCount = new Long(0);
        List<BusinessActivity> businessActivityList = businessActivityRepository.findByCooperationMode(CooperationMode.Enum.CPS);
        if(!CollectionUtils.isEmpty(businessActivityList)) {
            channelCount = Long.valueOf(businessActivityList.size() + "");
        }

        List rebateList = businessActivityRepository.findAvgAndMaxAndMinRebate(CooperationMode.Enum.CPS.getId());
        // 渠道合作统计：平均抽成
        Double channelAvgRebate = rebateList == null || rebateList.isEmpty() || ((Object[])rebateList.get(0))[0] == null?
            new Double(0.00) : ((BigDecimal)(((Object[])rebateList.get(0))[0])).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        // 渠道合作统计：最高抽成
        Double channelMaxRebate = rebateList == null || rebateList.isEmpty() || ((Object[])rebateList.get(0))[1] == null?
            new Double(0.00) : ((BigDecimal)(((Object[])rebateList.get(0))[1])).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        // 渠道合作统计：最低抽成
        Double channelMinRebate = rebateList == null || rebateList.isEmpty() || ((Object[])rebateList.get(0))[2] == null?
            new Double(0.00) : ((BigDecimal)(((Object[])rebateList.get(0))[2])).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        paramMap.put("channelSumPaidAmount", getMoney(sumPaidAmount));//累计保单总金额（含线上、线下，优惠后金额）
        paramMap.put("channelOnLineSumPaidAmount", getMoney(onLineSumPaidAmount));//累计到账总金额（线上，优惠后金额）
        paramMap.put("channelSumPaidOrderCount", sumPaidOrderCount.toString());//累计保单数量（支付成功，含订单完成）
        paramMap.put("channelLastSumPaidAmount", getMoney(lastSumPaidAmount));//昨天累计保单总金额（含线上、线下，优惠后金额）
        paramMap.put("channelLastOnLineSumPaidAmount", getMoney(lastOnLineSumPaidAmount));//昨天累计到账总金额（线上，优惠后金额）
        paramMap.put("channelLastSumPaidOrderCount", lastSumPaidOrderCount.toString());//昨天累计保单数量（支付成功，含订单完成）
        paramMap.put("channelCount", channelCount.toString());//渠道数量
        paramMap.put("channelAvgRebate", getRebate(channelAvgRebate));//平均抽成
        paramMap.put("channelMaxRebate", getRebate(channelMaxRebate));//最高抽成
        paramMap.put("channelMinRebate", getRebate(channelMinRebate));//最低抽成
    }


    private void setAgentReportData(Map<String, Object> paramMap) {
        // 累计保单总金额（含线上、线下，优惠后金额）
        Double sumPaidAmount = purchaseOrderService.getSumPaidAmount(2);
        // 累计到账总金额（线上，优惠后金额）
        Double onLineSumPaidAmount = purchaseOrderService.getOnLineSumPaidAmount(2);
        // 累计保单数量（支付成功，含订单完成）
        Long sumPaidOrderCount = purchaseOrderService.getSumPaidOrderCount(2);

        // 昨天累计保单总金额（含线上、线下，优惠后金额）
        Double lastSumPaidAmount = purchaseOrderService.getYesterdaySumPaidAmount(2);
        // 昨天累计到账总金额（线上，优惠后金额）
        Double lastOnLineSumPaidAmount = purchaseOrderService.getYesterdayOnLineSumPaidAmount(2);
        // 昨天累计保单数量（支付成功，含订单完成）
        Long lastSumPaidOrderCount = purchaseOrderService.getYesterdaySumPaidOrderCount(2);

        // 代理人统计：代理人数量
        Long agentCount = agentService.getTotalCount();
        List rebateList = agentService.findAvgAndMaxAndMinRebate();
        // 代理人统计：平均返点
        Double agentAvgRebate = rebateList == null || rebateList.isEmpty() || ((Object[])rebateList.get(0))[0] == null?
            new Double(0.00) : ((BigDecimal)(((Object[])rebateList.get(0))[0])).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        // 代理人统计：最高返点
        Double agentMaxRebate = rebateList == null || rebateList.isEmpty() || ((Object[])rebateList.get(0))[1] == null?
            new Double(0.00) : ((BigDecimal)(((Object[])rebateList.get(0))[1])).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        // 代理人统计：最低返点
        Double agentMinRebate = rebateList == null || rebateList.isEmpty() || ((Object[])rebateList.get(0))[2] == null?
            new Double(0.00) : ((BigDecimal)(((Object[])rebateList.get(0))[2])).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        paramMap.put("agentSumPaidAmount", getMoney(sumPaidAmount));//累计保单总金额（含线上、线下，优惠后金额）
        paramMap.put("agentOnLineSumPaidAmount", getMoney(onLineSumPaidAmount));//累计到账总金额（线上，优惠后金额）
        paramMap.put("agentSumPaidOrderCount", sumPaidOrderCount.toString());//累计保单数量（支付成功，含订单完成）
        paramMap.put("agentLastSumPaidAmount", getMoney(lastSumPaidAmount));//昨天累计保单总金额（含线上、线下，优惠后金额）
        paramMap.put("agentLastOnLineSumPaidAmount", getMoney(lastOnLineSumPaidAmount));//昨天累计到账总金额（线上，优惠后金额）
        paramMap.put("agentLastSumPaidOrderCount", lastSumPaidOrderCount.toString());//昨天累计保单数量（支付成功，含订单完成）
        paramMap.put("agentCount", agentCount.toString());//代理人数量
        paramMap.put("agentAvgRebate", getRebate(agentAvgRebate));//平均返点
        paramMap.put("agentMaxRebate", getRebate(agentMaxRebate));//最高返点
        paramMap.put("agentMinRebate", getRebate(agentMinRebate));//最低返点
    }

    private void setCustomerReportData(Map<String, Object> paramMap) {
        // 累计保单总金额（含线上、线下，优惠后金额）
        Double sumPaidAmount = purchaseOrderService.getSumPaidAmount(1);
        // 累计到账总金额（线上，优惠后金额）
        Double onLineSumPaidAmount = purchaseOrderService.getOnLineSumPaidAmount(1);
        // 累计保单数量（支付成功，含订单完成）
        Long sumPaidOrderCount = purchaseOrderService.getSumPaidOrderCount(1);

        // 昨天累计保单总金额（含线上、线下，优惠后金额）
        Double lastSumPaidAmount = purchaseOrderService.getYesterdaySumPaidAmount(1);
        // 昨天累计到账总金额（线上，优惠后金额）
        Double lastOnLineSumPaidAmount = purchaseOrderService.getYesterdayOnLineSumPaidAmount(1);
        // 昨天累计保单数量（支付成功，含订单完成）
        Long lastSumPaidOrderCount = purchaseOrderService.getYesterdaySumPaidOrderCount(1);

        paramMap.put("customerSumPaidAmount", getMoney(sumPaidAmount));//累计保单总金额（含线上、线下，优惠后金额）
        paramMap.put("customerOnLineSumPaidAmount", getMoney(onLineSumPaidAmount));//累计到账总金额（线上，优惠后金额）
        paramMap.put("customerSumPaidOrderCount", sumPaidOrderCount.toString());//累计保单数量（支付成功，含订单完成）
        paramMap.put("customerLastSumPaidAmount", getMoney(lastSumPaidAmount));//昨天累计保单总金额（含线上、线下，优惠后金额）
        paramMap.put("customerLastOnLineSumPaidAmount", getMoney(lastOnLineSumPaidAmount));//昨天累计到账总金额（线上，优惠后金额）
        paramMap.put("customerLastSumPaidOrderCount", lastSumPaidOrderCount.toString());//昨天累计保单数量（支付成功，含订单完成）
    }

    private void setSumReportData(Map<String, Object> paramMap) {
        // 累计保单总金额（含线上、线下，优惠后金额）
        Double sumPaidAmount = purchaseOrderService.getSumPaidAmount(5);
        // 累计到账总金额（线上，优惠后金额）
        Double onLineSumPaidAmount = purchaseOrderService.getOnLineSumPaidAmount(5);
        // 累计保单数量（支付成功，含订单完成）
        Long sumPaidOrderCount = purchaseOrderService.getSumPaidOrderCount(5);

        // 昨天累计保单总金额（含线上、线下，优惠后金额）
        Double lastSumPaidAmount = purchaseOrderService.getYesterdaySumPaidAmount(5);
        // 昨天累计到账总金额（线上，优惠后金额）
        Double lastOnLineSumPaidAmount = purchaseOrderService.getYesterdayOnLineSumPaidAmount(5);
        // 昨天累计保单数量（支付成功，含订单完成）
        Long lastSumPaidOrderCount = purchaseOrderService.getYesterdaySumPaidOrderCount(5);

        paramMap.put("sumPaidAmount", getMoney(sumPaidAmount));//累计保单总金额（含线上、线下，优惠后金额）
        paramMap.put("onLineSumPaidAmount", getMoney(onLineSumPaidAmount));//累计到账总金额（线上，优惠后金额）
        paramMap.put("sumPaidOrderCount", sumPaidOrderCount.toString());//累计保单数量（支付成功，含订单完成）
        paramMap.put("lastSumPaidAmount", getMoney(lastSumPaidAmount));//昨天累计保单总金额（含线上、线下，优惠后金额）
        paramMap.put("lastOnLineSumPaidAmount", getMoney(lastOnLineSumPaidAmount));//昨天累计到账总金额（线上，优惠后金额）
        paramMap.put("lastSumPaidOrderCount", lastSumPaidOrderCount.toString());//昨天累计保单数量（支付成功，含订单完成）
    }

    private String getRebate(Double rebate) {
        if(rebate == null) {
            return "0.00";
        }
        DecimalFormat format = new DecimalFormat("#.##");
        return format.format(rebate) + "%";
    }

    private String getMoney(Double amount) {
        if(amount == null) {
            return "0.00";
        }
        Double money = amount / 10000;
        DecimalFormat format = new DecimalFormat("#,###.####");
        format.setRoundingMode(RoundingMode.HALF_UP);
        return format.format(money) + "万元";
    }
}

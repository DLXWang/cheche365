package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.manage.common.model.TelMarketingCenter;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterHistory;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterStatus;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterHistoryRepository;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterRepository;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by wangshaobin on 2016/7/19.
 */
@Service
public class TelMarketingCenterGenerateOrderReportService {
    Logger logger = LoggerFactory.getLogger(TelMarketingCenterGenerateOrderReportService.class);

    @Autowired
    private OrderOperationInfoRepository orderOperationInfoRepository;

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;

    @Autowired
    private PurchaseOrderGiftRepository purchaseOrderGiftRepository;

    @Autowired
    private TelMarketingCenterHistoryRepository telMarketingCenterHistoryRepository;

    @Autowired
    private TelMarketingCenterRepository telMarketingCenterRepository;

    /**
     * 获取出单信息
     * **/
    public Map<String,List<PurchaseOrderInfo>> getGenerateOrderInfos(Map<String, Date> timeMap){
        //从订单中获取id、objId、userId作为后面数据的查询参数
        Map paramFromOrder = new HashMap();
        //1、查询出单时间在当天0点到当前时间的订单的信息
        List<OrderOperationInfo> infos = findConfirmOrderByTimePeriod(timeMap);
        if(!CollectionUtils.isEmpty(infos)){
            logger.debug("可以导出到出单报表的条目数量为{}",infos.size());
            //1.1 拼装查询到的订单信息及查询参数信息
            Map<Long, OrderOperationInfo> orderInfoMap = assembleConfirmOrder(infos, paramFromOrder);
            //2、通过订单中的applicant查询tel_marketing_center
            //因为center表中的user有可能为空，而手机号不会为空，经沟通，现修改为通过手机号来查询center表中的数据
            List<TelMarketingCenter> centers = findTelMarketingCenterByMobiles((List) paramFromOrder.get("mobiles"));
            //参数：centerIDS
            List centerIds = new ArrayList();
            //2.1 拼装center数据
            Map<String, TelMarketingCenter> centerInfoMap = assembleTelMarketingCenter(centers,centerIds);
            //3、通过tel_marketing_center查询tel_marketing_center_history
            List<TelMarketingCenterHistory> histories = findTelMarketingCenterHistoryByCenterIds(centerIds);
            //3.1拼装history数据
            Map<Long, TelMarketingCenterHistory> historyMap = assembleTelMarketingCenterHistory(histories);
            //4、根据订单ID查询优惠金额
            Map giftMap = findGiftAmountByOrderIds((List) paramFromOrder.get("orderIds"));
            //5、根据订单的ObjID查询QuoteRecord
            List<QuoteRecord> records = findQuoteRecordByIds((List) paramFromOrder.get("objIds"));
            Map<Long, QuoteRecord> recordMap = assembleQuoteRecord(records);
            //6、将各个数据拼装成邮件附件所需要的格式
            return getIssueExcelSheetInfo(orderInfoMap,centerInfoMap,historyMap,giftMap,recordMap);
        }
        logger.debug("状态在确认出单、已付款，出单完成、录单完成的订单数量为0");
        return null;
    }

    private List<OrderOperationInfo> findConfirmOrderByTimePeriod(Map<String, Date> timeMap){
        //订单状态在“确认出单、已付款，出单完成、录单完成”中的
        List orderStatus = Arrays.asList(OrderTransmissionStatus.Enum.CONFIRM_TO_ORDER,
            OrderTransmissionStatus.Enum.PAID_AND_FINISH_ORDER,
            OrderTransmissionStatus.Enum.ORDER_INPUTED);
        return orderOperationInfoRepository.findConfirmOrderByTimePeriod(timeMap.get("startTime"), timeMap.get("endTime"),orderStatus);
    }

    private Map<Long, OrderOperationInfo> assembleConfirmOrder(List<OrderOperationInfo> infos,Map paramFromOrder){
        Map<Long, OrderOperationInfo> reMap = new HashMap<Long, OrderOperationInfo>();
        if(!CollectionUtils.isEmpty(infos)){
            List orderIds = new ArrayList();
            List objIds = new ArrayList();
            List mobiles = new ArrayList();
            for(OrderOperationInfo info : infos){
                PurchaseOrder order = info.getPurchaseOrder();
                reMap.put(order.getId(),info);
                orderIds.add(order.getId());
                objIds.add(order.getObjId());
                if(order.getApplicant()!=null)
                    mobiles.add(order.getApplicant().getMobile());
            }
            paramFromOrder.put("orderIds",orderIds);
            paramFromOrder.put("objIds",objIds);
            paramFromOrder.put("mobiles",mobiles);
        }
        return reMap;
    }

    private List<TelMarketingCenter> findTelMarketingCenterByMobiles(List mobiles){
        if(!CollectionUtils.isEmpty(mobiles))
            return telMarketingCenterRepository.findTelMarketingCenterByMobiles(mobiles);
        logger.debug("查询tel_marketing_center时，传入的参数为空");
        return null;
    }

    private Map<String, TelMarketingCenter> assembleTelMarketingCenter(List<TelMarketingCenter> centers,List centerIds){
        Map<String, TelMarketingCenter> reMap = new HashMap<String, TelMarketingCenter>();
        if(!CollectionUtils.isEmpty(centers)){
            for(TelMarketingCenter center : centers){
                reMap.put(center.getMobile(),center);
                centerIds.add(center.getId());
            }
        }
        return reMap;
    }

    private List<TelMarketingCenterHistory> findTelMarketingCenterHistoryByCenterIds(List centerIds){
        List<TelMarketingCenterHistory> histories = null;
        if(!CollectionUtils.isEmpty(centerIds)){
            /**
             *  过滤掉无效状态：未处理、其他、挂断、无人接听、无法接通、空号、非车主、非开通城市
             * **/
            List invalidStatus = Arrays.asList(TelMarketingCenterStatus.Enum.UNTREATED.getId(),TelMarketingCenterStatus.Enum.OTHER_STATUS.getId(),
                TelMarketingCenterStatus.Enum.HANG_UP.getId(),TelMarketingCenterStatus.Enum.NO_ANSWER.getId(),
                TelMarketingCenterStatus.Enum.CANNOT_CONNECT.getId(),TelMarketingCenterStatus.Enum.VACANT_NUMBER.getId(),
                TelMarketingCenterStatus.Enum.NOT_OWNER.getId(),TelMarketingCenterStatus.Enum.NO_OPEN_CITY.getId());
            histories = telMarketingCenterHistoryRepository.findTelMarketingCenterHistoryByCenterIds(centerIds, invalidStatus);
        }
        return histories;
    }

    private Map<Long, TelMarketingCenterHistory> assembleTelMarketingCenterHistory(List<TelMarketingCenterHistory> histories){
        Map<Long, TelMarketingCenterHistory> reMap = new HashMap<Long, TelMarketingCenterHistory>();
        if(!CollectionUtils.isEmpty(histories)){
            for(TelMarketingCenterHistory history : histories){
                reMap.put(history.getTelMarketingCenter().getId(),history);
            }
        }
        return reMap;
    }

    private Map<Long, String> findGiftAmountByOrderIds(List orderIds){
        Map<Long, String> reMap = new HashMap<Long, String>();
        List<PurchaseOrderGift> orderGifts =purchaseOrderGiftRepository.findByOrderIds(orderIds);
        if (orderGifts != null) {
            orderGifts.forEach(orderGift -> {
                reMap.put(orderGift.getPurchaseOrder().getId(),String.valueOf(orderGift.getGift()==null?"":orderGift.getGift().getGiftAmount()));
            });
        }
        return reMap;
    }

    private List<QuoteRecord> findQuoteRecordByIds(List ids){
        return quoteRecordRepository.findQuoteRecordByIds(ids);
    }

    private Map<Long, QuoteRecord> assembleQuoteRecord(List<QuoteRecord> records){
        Map<Long, QuoteRecord> reMap = new HashMap<Long, QuoteRecord>();
        if(!CollectionUtils.isEmpty(records)){
            for(QuoteRecord record:records){
                reMap.put(record.getId(),record);
            }
        }
        return reMap;
    }

    private Map<String,List<PurchaseOrderInfo>> getIssueExcelSheetInfo(Map<Long, OrderOperationInfo> orderInfoMap,Map<String, TelMarketingCenter> centerInfoMap,
                                                                       Map<Long, TelMarketingCenterHistory> historyMap,Map<Long, String> giftMap,Map<Long, QuoteRecord> recordMap){
        List<PurchaseOrderInfo> telMarketingCenterDataSource = new ArrayList<PurchaseOrderInfo>();

        /**新需求：
         *  1)判断订单确认出单时间和电销那边处理时间的，确认出单时间晚于电销处理时间才进入报表**/
        for(Map.Entry<Long, OrderOperationInfo> en : orderInfoMap.entrySet()){
            PurchaseOrderInfo purchaseOrderInfo = new PurchaseOrderInfo();
            Long orderId = en.getKey();
            OrderOperationInfo operationInfo = en.getValue();

            Date issueTime = operationInfo.getConfirmOrderDate();
            PurchaseOrder order = operationInfo.getPurchaseOrder();
            if(order.getApplicant()!=null ){
                String mobile = order.getApplicant().getMobile();
                TelMarketingCenter center = centerInfoMap.get(mobile);
                if(center == null) {//在电销主表中，找不到对应记录的(也就是没有经过电销人员沟通的)，不计入电销人员业绩，不进入到出单报表中
                    logger.debug("手机号为{}对应的center为空!",mobile);
                    continue;
                }
                TelMarketingCenterHistory history = historyMap.get(center.getId());
                if(history == null||(history != null && DateUtils.compareDate(history.getCreateTime(), issueTime))) {//只有当电销第一次处理时间小于确认出单时间的，才能进到出单报表
                    logger.debug("centerId为{}的history条目有效操作记录为空或者history创建时间{}大于确认出单时间{}",center.getId(),history==null?"NULL":history.getCreateTime(),issueTime);
                    continue;
                }
            }

            //1、封装出单时间在当天0点到当前时间的订单的信息
            purchaseOrderInfo.setChannel(String.valueOf(order.getSourceChannel()==null?"":order.getSourceChannel().getDescription()));
            purchaseOrderInfo.setOrderTime(DateUtils.getDateString(order.getCreateTime(),DateUtils.DATE_SHORTDATE_PATTERN));
            purchaseOrderInfo.setLicenseNo(String.valueOf(order.getAuto()==null?"":order.getAuto().getLicensePlateNo()));
            purchaseOrderInfo.setOrderNo(String.valueOf(order.getOrderNo()));
            purchaseOrderInfo.setArea(String.valueOf(order.getArea()==null?"":order.getArea().getName()));
            purchaseOrderInfo.setPayableAmount(String.valueOf(order.getPayableAmount()));
            purchaseOrderInfo.setPaidAmount(String.valueOf(order.getPaidAmount()));
            purchaseOrderInfo.setIssueTime(DateUtils.getDateString(operationInfo.getConfirmOrderDate(),DateUtils.DATE_SHORTDATE_PATTERN));
            if(order.getApplicant()!=null ){
                //2、封装订单关联的tel_marketing_center
                String mobile = order.getApplicant().getMobile();
                TelMarketingCenter center = centerInfoMap.get(mobile);
                if(center != null){
                    purchaseOrderInfo.setSource(String.valueOf(center.getSource()==null?"":center.getSource().getDescription()));
                    purchaseOrderInfo.setMobile(String.valueOf(center.getMobile()));
                    /**新需求：重新指派人之后，出单报表中员工姓名应该是当前指定人的**/
                    purchaseOrderInfo.setLinkMan(String.valueOf(center.getOperator()==null?"":center.getOperator().getName()));
                    //3、封装center对应的history信息
                    TelMarketingCenterHistory history = historyMap.get(center.getId());
                    if(history != null){
                        purchaseOrderInfo.setOperateTime(DateUtils.getDateString(history.getCreateTime(),DateUtils.DATE_SHORTDATE_PATTERN));
                    }
                }
            }
            //4、根据orderID封装优惠金额
            String giftAmount = giftMap.get(Long.valueOf(orderId))==null?"":String.valueOf(giftMap.get(Long.valueOf(orderId)));
            purchaseOrderInfo.setPaymentDiscountAmount(giftAmount);
            //5、根据objId封装quoteRecord中的数据
            Long objId = order.getObjId();
            QuoteRecord quoteRecord = recordMap.get(objId);
            if(quoteRecord != null){
                purchaseOrderInfo.setInsuranceCompany(String.valueOf(quoteRecord.getInsuranceCompany()==null?"":quoteRecord.getInsuranceCompany().getName()));
                purchaseOrderInfo.setCompulsoryPremium(String.valueOf(quoteRecord.getCompulsoryPremium()));
                purchaseOrderInfo.setAutoTax(String.valueOf(quoteRecord.getAutoTax()));
                purchaseOrderInfo.setCommecialPremium(String.valueOf(quoteRecord.getPremium()));
            }
            telMarketingCenterDataSource.add(purchaseOrderInfo);
        }
        Map<String,List<PurchaseOrderInfo>> purchaseOrderInfoListMap = new HashMap<>();
        purchaseOrderInfoListMap.put("dataSource",telMarketingCenterDataSource);
        logger.debug("电销拨打数据——报表数据拼装成Excel，完成;数据条数为{}",telMarketingCenterDataSource.size());
        return purchaseOrderInfoListMap;
    }
}

package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.core.service.UserService;
import com.cheche365.cheche.manage.common.model.TelMarketingCenter;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterHistoryRepository;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterRepository;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.*;

/**
 * Created by wangshaobin on 2016/7/14.
 */
@Service
public class TelMarketingCenterCallRecordReportService {
    Logger logger = LoggerFactory.getLogger(TelMarketingCenterCallRecordReportService.class);
    @Autowired
    private TelMarketingCenterHistoryRepository telMarketingCenterHistoryRepository;

    @Autowired
    private TelMarketingCenterRepository telMarketingCenterRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    /**
     * 获取数据源信息
     * @return
     */
    public Map<String,List<PurchaseOrderInfo>> getCallRecordInfos(){
        List centerIds = new ArrayList();
        //获取各个活动当天的进库量
        List<Object[]> sourceInputAmount = getSourceInputAmount();
        //从tel_marketing_center_history表中获取首次拨打日期、联系人、联系结果、备注
        Map historyMap = findHistoryData(centerIds);
        if(!CollectionUtils.isEmpty(centerIds)){
            //查询centerId、用户Id、手机号、活动
            List<TelMarketingCenter> centers = findUserSourceByIds(centerIds);
            List<User> users = new ArrayList<User>();
            //拼装手机号、活动等信息
            Map<Long, TelMarketingCenter> centerMap = assembleUserSource(centers,users);
            //根据用户ID查询渠道
            Map userChannelMap = findUserChannel(users);
            return getDataSourceExcelSheetInfo(historyMap,centerMap,userChannelMap,sourceInputAmount);
        }
        logger.debug("未查到数据源报表——history数据");
        return null;
    }

    private List<Object[]> getSourceInputAmount(){
        //当天23点
        Date endTime = DateUtils.getDayEndTime(new Date());
        //当天0点
        Date startTime = DateUtils.getDayStartTime(new Date());
        return telMarketingCenterRepository.findSourceInputAmount(startTime, endTime);
    }

    private List<TelMarketingCenter> findUserSourceByIds(List centerIds){
        return telMarketingCenterRepository.findUserSourceByIds(centerIds);
    }

    private Map<Long, TelMarketingCenter> assembleUserSource(List<TelMarketingCenter> centers,List<User> users){
        Map<Long, TelMarketingCenter> centerMap = new HashMap<Long, TelMarketingCenter>();
        if(!CollectionUtils.isEmpty(centers)) {
            for(int i=0;i<centers.size();i++){
                TelMarketingCenter center = centers.get(i);
                addTelMarketingCenterUser(center,users);//center用户有可能为空，需要进行非空处理
                centerMap.put(center.getId(),center);
            }
        }
        return centerMap;
    }

    private void addTelMarketingCenterUser(TelMarketingCenter center,List<User> users){
        User user = null;
        if(center.getUser()==null)
            user = userService.getUserByMobile(center.getMobile());
        else
            user = center.getUser();
        if(user != null)
            users.add(user);
    }

    private Map findUserChannel(List<User> users){
        Map userChannelMap = new HashMap();
        if(!CollectionUtils.isEmpty(users)){
            for(int i=0;i<users.size();i++){
                User user = users.get(i);
                userChannelMap.put(user.getId(),user.getRegisterChannel()==null?"未知":user.getRegisterChannel().getDescription());
            }
        }
        return userChannelMap;
    }

    private Map findHistoryData(List centerIds){
        Map historiesMap = new HashMap();
        //当天18点
        Date endTime = DateUtils.getDate(DateUtils.getCustomDate(new Date(),0,18,0,0),DateUtils.DATE_LONGTIME24_PATTERN);
        //前一天18点
        Date startTime = DateUtils.getDate(DateUtils.getCustomDate(new Date(),-1,18,0,0),DateUtils.DATE_LONGTIME24_PATTERN);
        //从tel_marketing_center_history表中获取首次拨打日期、联系人、联系结果、备注
        List<Object[]> histories = telMarketingCenterHistoryRepository.findHistoryDataByCreateTimeBetween(startTime,endTime);
        logger.debug("从{}到{}范围内查询的电销操作记录条数为{}",
            DateUtils.getDateString(startTime,DateUtils.DATE_LONGTIME24_PATTERN),
            DateUtils.getDateString(endTime,DateUtils.DATE_LONGTIME24_PATTERN),
            CollectionUtils.isEmpty(histories)?0:histories.size());
        if(!CollectionUtils.isEmpty(histories)) {
            for(int i=0;i<histories.size();i++){
                Object[] objs = histories.get(i);
                Map historyInfo = new HashMap();
                historyInfo.put("operator", objs[1]);
                historyInfo.put("result",objs[2]);
                historyInfo.put("comment", objs[3]);
                historyInfo.put("createTime", String.valueOf(objs[4]));
                historiesMap.put(objs[0],historyInfo);
                centerIds.add(objs[0]);
            }
        }
        return historiesMap;
    }

    private Map<String,List<PurchaseOrderInfo>> getDataSourceExcelSheetInfo(Map<BigInteger, Map> historiesMap, Map<Long, TelMarketingCenter> centersMap, Map<BigInteger, String> userChannelMap, List<Object[]> sourceInputAmount){
        List<PurchaseOrderInfo> dataInputAmount = new ArrayList<PurchaseOrderInfo>();
        if(!CollectionUtils.isEmpty(sourceInputAmount)){
            for(Object[] obj : sourceInputAmount){
                PurchaseOrderInfo purchaseOrderInfo = new PurchaseOrderInfo();
                purchaseOrderInfo.setSource(String.valueOf(obj[0]));
                purchaseOrderInfo.setAmount(String.valueOf(obj[1]));
                dataInputAmount.add(purchaseOrderInfo);
            }
        }
        List<PurchaseOrderInfo> telMarketingCenterDataSource = new ArrayList<PurchaseOrderInfo>();
        if(!CollectionUtils.isEmpty(historiesMap)){
            for(Map.Entry<BigInteger, Map> en: historiesMap.entrySet()){
                PurchaseOrderInfo purchaseOrderInfo = new PurchaseOrderInfo();
                BigInteger centerId = en.getKey();
                Map value = en.getValue();
                purchaseOrderInfo.setLinkMan((String)value.get("operator"));
                String comment = value.get("comment")==null?"":String.valueOf(value.get("comment"));
                String trimComment = comment.replaceAll(" ", "");
                if(trimComment.length() > 83)//经实际测试，当汉字字数大于83时，拼装Excel时，报错；现截取前82个，并拼装一个“…”
                    trimComment = trimComment.substring(0,82)+"…";
                purchaseOrderInfo.setComment(trimComment);
                String createTime = String.valueOf(value.get("createTime"));
                purchaseOrderInfo.setOperateTime(createTime==""?"":createTime.substring(0,createTime.indexOf(" ")));
                TelMarketingCenter center = centersMap.get(centerId.longValue());
                if(center != null){
                    purchaseOrderInfo.setResult(center.getStatus()!= null?center.getStatus().getName():"");
                    purchaseOrderInfo.setSource(center.getSource()==null?"":center.getSource().getDescription());
                    purchaseOrderInfo.setMobile(center.getMobile());
                    purchaseOrderInfo.setExpireTime(DateUtils.getDateString(center.getExpireTime(),DateUtils.DATE_SHORTDATE_PATTERN));
                    purchaseOrderInfo.setSubmitTime(DateUtils.getDateString(center.getSourceCreateTime(),DateUtils.DATE_SHORTDATE_PATTERN));
                    if(center.getUser()!=null){
                        Long userId = center.getUser().getId();
                        purchaseOrderInfo.setChannel(userChannelMap.get(userId));
                    }
                }
                telMarketingCenterDataSource.add(purchaseOrderInfo);
            }
        }
        Map<String,List<PurchaseOrderInfo>> purchaseOrderInfoListMap = new HashMap<>();
        purchaseOrderInfoListMap.put("dataInputAmount",dataInputAmount);

        purchaseOrderInfoListMap.put("dataSource",telMarketingCenterDataSource);
        logger.debug("将电销拨打数据——数据源数据拼装成Excel，完成！数量为{}",telMarketingCenterDataSource.size());
        return purchaseOrderInfoListMap;
    }
}

package com.cheche365.cheche.scheduletask.service.task;

import com.alibaba.fastjson.JSON;
import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository;
import com.cheche365.cheche.core.repository.PartnerUserRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.repository.UserLoginInfoRepository;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 百度数据同步邮件定时任务
 * Created by wangshaobin on 2017/1/5.
 */
@Service
public class BaiduDataSyncEmailService {
    Logger logger = LoggerFactory.getLogger(BaiduDataSyncEmailService.class);

    private static final String ACCOUNT = "account";                            //百度账号
    private static final String NAME = "name";                                  //客户姓名
    private static final String MOBILE = "mobile";                              //手机号
    private static final String CREATE_TIME = "createTime";                     //创建时间
    private static final String AUTO = "auto";                                  //汽车信息
    private static final String EXPIRE_TIME = "expireTime";                     //车险到期日
    private static final String INSURANCE_COMPANY = "insuranceCompany";         //保险公司
    private static final String COMMECIAL_PREMIUM = "commecialPremium";         //商业险保费
    private static final String COMPULSORY_PREMIUM = "compulsoryPremium";       //交强险保费

    private static final String USER_LOGIN_INFO = "userLoginInfo";              //登录用户数据
    private static final String APPLICATION_LOG_INFO = "applicationLogInfo";    //报价用户数据
    private static final String PURCHASE_ORDER_INFO = "purchaseOrderInfo";      //下单用户数据

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserLoginInfoRepository userLoginInfoRepository;

    @Autowired
    private MoApplicationLogRepository applicationLogMongoRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PartnerUserRepository partnerUserRepository;


    public Map<String,List<PurchaseOrderInfo>> getSyncBaiduData(){
        Date startTime = null;
        Date endTime = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
        String previousTimeStr = stringRedisTemplate.opsForValue().get(TaskConstants.BAIDU_DATA_SYNC_TIME);
        logger.debug("从redis中获取百度数据同步邮件的定时任务上次执行时间为{}", previousTimeStr);
        if(!StringUtils.isEmpty(previousTimeStr))
            startTime = DateUtils.getDate(previousTimeStr,DateUtils.DATE_LONGTIME24_PATTERN);
        else //如果定时任务第一次执行，redis中没有上次执行时间，默认取昨天零点
            startTime = DateUtils.getDate(DateUtils.getCustomDate(endTime,-1,0,0,0),DateUtils.DATE_LONGTIME24_PATTERN);

        //1) 获取登录用户数据
        List<Object[]> userLoginInfo = getUserLoginInfo(startTime, endTime);
        //2) 获取报价用户数据
        //   同一个用户，一天内可能报多次价，因此报价信息放在list中
        Map<Long,List<Map>> applicationLogInfo = getApplicationLogInfo(startTime, endTime);
        //3) 获取下单用户数据
        List<Object[]> purchaseOrderInfo = getPurchaseOrderinInfo(startTime, endTime);
        //拼装各个数据到excel
        Map<String,List<PurchaseOrderInfo>> map = getBaiduDataExcelSheetInfo(userLoginInfo, applicationLogInfo, purchaseOrderInfo);
        //设置本次定时任务的执行时间到Redis缓存中
        stringRedisTemplate.opsForValue().set(TaskConstants.BAIDU_DATA_SYNC_TIME, DateUtils.getDateString(endTime, DateUtils.DATE_LONGTIME24_PATTERN));
        return map;
    }

    /**
     * 获取登录用户数据
     */
    private List<Object[]> getUserLoginInfo(Date startDate, Date endDate){
        List<Object[]> list = new ArrayList<Object[]>();
        Integer startIndex = 0;
        Integer pageSize = TaskConstants.PAGE_SIZE;
        List<Object[]> infoList = userLoginInfoRepository.findByLastLoginTimeAndChannel(startDate, endDate, Arrays.asList(Channel.Enum.PARTNER_BAIDU_15, Channel.Enum.ORDER_CENTER_BAIDU_16), ApiPartner.Enum.BAIDU_PARTNER_2, pageSize, startIndex);
        logger.debug("获取时间点大于{}，下标从{}开始的登录用户数据为{}条", DateUtils.getDateString(startDate, DateUtils.DATE_LONGTIME24_PATTERN), startIndex, infoList.size());
        while (CollectionUtils.isNotEmpty(infoList)){
            list.addAll(infoList);
            if(infoList.size() < pageSize)
                break;
            startIndex += infoList.size();
            infoList = userLoginInfoRepository.findByLastLoginTimeAndChannel(startDate, endDate, Arrays.asList(Channel.Enum.PARTNER_BAIDU_15, Channel.Enum.ORDER_CENTER_BAIDU_16), ApiPartner.Enum.BAIDU_PARTNER_2, pageSize, startIndex);
            logger.debug("获取时间点大于{}，下标从{}开始的登录用户数据为{}条", DateUtils.getDateString(startDate, DateUtils.DATE_LONGTIME24_PATTERN), startIndex, infoList.size());
        }
        logger.debug("经过过滤后，获取时间点大于{}的百度用户的登录用户最终数据为{}条",DateUtils.getDateString(startDate, DateUtils.DATE_LONGTIME24_PATTERN), list.size());
        return list;
    }


    /******************   MongoDB逻辑    start  ******************/
    /**
     * 获取报价用户数据
     */
    private Map<Long,List<Map>> getApplicationLogInfo(Date startDate, Date endDate){
        Map<Long,List<Map>> map = new HashMap<Long,List<Map>>();

        /**
         * 每天的内容数一百多条，且将数据在Map中的拼接也是在内存中，分页没有意义，干掉
         *  根据每天的日志数据递增情况，每天有将近一万的增幅；目前百度用户人数将近30万
         *  而Mongodb的效率，要远比mysql效率高，因此，先查询每天的报价日志，再通过日志的用户去用户表查询第三方账号
         *  基于查询出的用户信息，只处理第三方为百度的日志记录
         * **/
        List<MoApplicationLog> logList = applicationLogMongoRepository.findByLogTypeAndCreateTimeBetweenAndUserNotNull(LogType.Enum.Quote_Cache_Record_31.getId(), startDate, endDate);
        Set<Long> userSet = new HashSet<Long>();
        for (MoApplicationLog log : logList){
            userSet.add(log.getUser().getId());
        }
        //如果对应的用户，表示没有符合条件的用户信息，直接返回空数据
        if(CollectionUtils.isEmpty(userSet))
            return map;
        List<PartnerUser> partnerUsers = partnerUserRepository.findByUsers(new ArrayList<Long>(userSet));
        /**
         * 存储百度用户ID和第三方账号的Map，用于在处理日志数据时，检索百度的数据
         * **/
        Map<Long, String> baiduUserMap = new HashMap<Long, String>();
        for (PartnerUser partnerUser : partnerUsers){
            if (partnerUser.getPartner().getId() == ApiPartner.Enum.BAIDU_PARTNER_2.getId())
                baiduUserMap.put(partnerUser.getUser().getId(), partnerUser.getPartnerId());
        }

        logger.debug("获取时间点大于{}报价用户数据为{}条", DateUtils.getDateString(startDate, DateUtils.DATE_LONGTIME24_PATTERN), logList.size());
        if (!baiduUserMap.isEmpty() && CollectionUtils.isNotEmpty(logList)){
            List<Map> infoList = null;
            for(MoApplicationLog log : logList){
                try {
                    if (log.getUser() != null){
                        Long logUser = log.getUser().getId();
                        if (logUser != null && baiduUserMap.containsKey(logUser)){
                            QuoteRecord quoteRecord = CacheUtil.doJacksonDeserialize(JSON.toJSONString(log.getLogMessage()),QuoteRecord.class);
                            if(!Arrays.asList(Channel.Enum.PARTNER_BAIDU_15, Channel.Enum.ORDER_CENTER_BAIDU_16).contains(quoteRecord.getChannel()))
                                continue;
                            User user = quoteRecord.getApplicant();
                            if(map.containsKey(user.getId()))
                                infoList = map.get(user.getId());
                            else
                                infoList = new ArrayList<Map>();
                            Map infoMap = new HashMap<>();
                            infoMap.put(NAME, defaultNullStr(user.getName()));
                            infoMap.put(MOBILE, defaultNullStr(user.getMobile()));
                            infoMap.put(AUTO, quoteRecord.getAuto());
                            infoMap.put(CREATE_TIME,DateUtils.getDateString(log.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
                            infoMap.put(COMMECIAL_PREMIUM, quoteRecord.getPremium());
                            infoMap.put(COMPULSORY_PREMIUM, quoteRecord.getCompulsoryPremium());
                            infoMap.put(EXPIRE_TIME, DateUtils.getDateString(quoteRecord.getExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN));
                            infoMap.put(INSURANCE_COMPANY, quoteRecord.getInsuranceCompany()==null?"":quoteRecord.getInsuranceCompany().getName());
                            infoMap.put(ACCOUNT, defaultNullStr(baiduUserMap.get(logUser)));
                            infoList.add(infoMap);
                            map.put(user.getId(),infoList);
                        }
                    }
                } catch (Exception e) {
                    logger.error("序列化成quoteRecord对象异常，不是标准的数据格式,->{}",String.valueOf(log.getLogMessage()), e);
                }
            }
        }
        logger.debug("经过过滤后，获取时间点大于{}的百度用户的报价用户最终数据为{}条",DateUtils.getDateString(startDate, DateUtils.DATE_LONGTIME24_PATTERN), map.size());
        return map;
    }
    /******************   MongoDB逻辑    end  ******************/


    /**
     * 获取下单用户数据
     */
    private List getPurchaseOrderinInfo(Date startDate, Date endDate){
        List<Object[]> list = new ArrayList<Object[]>();
        Integer startIndex = 0;
        Integer pageSize = TaskConstants.PAGE_SIZE;
        List<Object[]> orderList = purchaseOrderRepository.findByCreateTimeAndChannelAndPartner(startDate, endDate, Arrays.asList(Channel.Enum.PARTNER_BAIDU_15, Channel.Enum.ORDER_CENTER_BAIDU_16), ApiPartner.Enum.BAIDU_PARTNER_2, pageSize, startIndex);
        logger.debug("获取时间点大于{}，下标从{}开始的下单用户数据为{}条", DateUtils.getDateString(startDate, DateUtils.DATE_LONGTIME24_PATTERN), startIndex, orderList.size());
        while (CollectionUtils.isNotEmpty(orderList)){
            list.addAll(orderList);
            if(orderList.size() < pageSize)
                break;
            startIndex += orderList.size();
            orderList = purchaseOrderRepository.findByCreateTimeAndChannelAndPartner(startDate, endDate, Arrays.asList(Channel.Enum.PARTNER_BAIDU_15, Channel.Enum.ORDER_CENTER_BAIDU_16), ApiPartner.Enum.BAIDU_PARTNER_2, pageSize, startIndex);
            logger.debug("获取时间点大于{}，下标从{}开始的下单用户数据为{}条", DateUtils.getDateString(startDate, DateUtils.DATE_LONGTIME24_PATTERN), startIndex, orderList.size());
        }
        logger.debug("经过过滤后，获取时间点大于{}的百度用户的下单用户最终数据为{}条",DateUtils.getDateString(startDate, DateUtils.DATE_LONGTIME24_PATTERN), list.size());
        return list;
    }

    private Map<String,List<PurchaseOrderInfo>> getBaiduDataExcelSheetInfo(List<Object[]> userLoginInfo, Map<Long,List<Map>> applicationLogInfo, List<Object[]> orderList){
        //拼装登录用户数据
        List<PurchaseOrderInfo> userLoginInfoList = makeUserLoginInfoExcel(userLoginInfo);
        logger.debug("百度数据同步——登录用户数据拼装成Excel，完成！数量为{}", userLoginInfoList.size());
        //拼装报价用户数据
        List<PurchaseOrderInfo> applicationLogInfoList = makeExcelFromListInfo(applicationLogInfo);
        logger.debug("百度数据同步——报价用户数据拼装成Excel，完成！数量为{}", applicationLogInfoList.size());
        //拼装下单用户数据
        List<PurchaseOrderInfo> purchaseOrderfoList = makeExcelFromPurchaseOrderfo(orderList);
        logger.debug("百度数据同步——下单用户数据拼装成Excel，完成！数量为{}", purchaseOrderfoList.size());

        Map<String,List<PurchaseOrderInfo>> purchaseOrderInfoListMap = new HashMap<>();
        purchaseOrderInfoListMap.put(USER_LOGIN_INFO, userLoginInfoList);
        purchaseOrderInfoListMap.put(APPLICATION_LOG_INFO, applicationLogInfoList);
        purchaseOrderInfoListMap.put(PURCHASE_ORDER_INFO, purchaseOrderfoList);
        return purchaseOrderInfoListMap;
    }

    private List<PurchaseOrderInfo> makeExcelFromListInfo(Map<Long,List<Map>> applicationLogInfo){
        List<PurchaseOrderInfo> applicationLogInfoList = new ArrayList<PurchaseOrderInfo>();
        if(!applicationLogInfo.isEmpty()){
            for(Map.Entry<Long, List<Map>> entry : applicationLogInfo.entrySet()){
                List<Map> infoList = entry.getValue();
                for (Map infoMap : infoList)
                    putDataToExcelInfo(infoMap, applicationLogInfoList);
            }
        }
        return applicationLogInfoList;
    }

    private List<PurchaseOrderInfo> makeUserLoginInfoExcel(List<Object[]> userLoginInfo){
        List<PurchaseOrderInfo> userLoginInfoList = new ArrayList<PurchaseOrderInfo>();
        if(CollectionUtils.isNotEmpty(userLoginInfo)){
            for (Object[] obj : userLoginInfo){
                PurchaseOrderInfo info = new PurchaseOrderInfo();
                info.setAccount(defaultNullStr(obj[1]));
                info.setInsuredName(defaultNullStr(obj[2]));
                info.setLinkPhone(defaultNullStr(obj[3]));
                info.setSubmitTime(formatTimeToString(obj[4]));
                userLoginInfoList.add(info);
            }
        }
        return userLoginInfoList;
    }

    private void putDataToExcelInfo(Map infoMap, List<PurchaseOrderInfo> excelInfoList){
        PurchaseOrderInfo info = new PurchaseOrderInfo();
        info.setInsuranceCompany(defaultNullStr(infoMap.get(INSURANCE_COMPANY)));
        info.setInsuredName(defaultNullStr(infoMap.get(NAME)));
        info.setAccount(defaultNullStr(infoMap.get(ACCOUNT)));
        info.setLinkPhone(defaultNullStr(infoMap.get(MOBILE)));
        info.setCommecialPremium(defaultNullStr(infoMap.get(COMMECIAL_PREMIUM)));
        info.setCompulsoryPremium(defaultNullStr(infoMap.get(COMPULSORY_PREMIUM)));
        info.setSubmitTime(defaultNullStr(infoMap.get(CREATE_TIME)));
        info.setOfflineCashBackSum("");
        info.setOfflineCashBackBase("");
        info.setIsCashBack("");
        if(infoMap.get(AUTO) != null){
            Auto auto = (Auto) infoMap.get(AUTO);
            info.setLicenseNo(auto.getLicensePlateNo());
            info.setAutoType(auto.getAutoType()==null?"":auto.getAutoType().getModel());
            info.setVinNo(auto.getVinNo());
            info.setEngineNo(auto.getEngineNo());
            info.setEnrollDate(DateUtils.getDateString(auto.getEnrollDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        }
        info.setExpireTime(defaultNullStr(infoMap.get(EXPIRE_TIME)));
        excelInfoList.add(info);
    }

    private List<PurchaseOrderInfo> makeExcelFromPurchaseOrderfo(List<Object[]> purchaseOrderInfo){
        List<PurchaseOrderInfo> purchaseOrderfoList = new ArrayList<PurchaseOrderInfo>();
        if(CollectionUtils.isNotEmpty(purchaseOrderInfo)){
            for (Object[] obj : purchaseOrderInfo){
                PurchaseOrderInfo info = new PurchaseOrderInfo();
                info.setInsuranceCompany(defaultNullStr(obj[0]));
                info.setInsuredName(defaultNullStr(obj[1]));
                info.setAccount(defaultNullStr(obj[2]));
                info.setLicenseNo(defaultNullStr(obj[3]));
                info.setLinkPhone(defaultNullStr(obj[4]));
                info.setCommecialPremium(defaultNullStr(obj[5]));
                info.setCompulsoryPremium(defaultNullStr(obj[6]));
                info.setSubmitTime(formatTimeToString(obj[7]));
                info.setAutoType(defaultNullStr(obj[8]));
                info.setVinNo(defaultNullStr(obj[9]));
                info.setEngineNo(defaultNullStr(obj[10]));
                info.setEnrollDate(defaultNullStr(obj[11]));
                info.setExpireTime(defaultNullStr(obj[12]));
                purchaseOrderfoList.add(info);
            }
        }
        return purchaseOrderfoList;
    }

    private String defaultNullStr(Object obj){
        return obj == null?"" : obj.toString();
    }

    private static String formatTimeToString(Object obj){
        return obj == null ? "" : obj.toString().substring(0,19);
    }
}

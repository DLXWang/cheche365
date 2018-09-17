package com.cheche365.cheche.ordercenter.third.clink;

import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.TelMarketer;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.core.util.MD5;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.ordercenter.constants.OrderCenterConstants;
import com.cheche365.cheche.ordercenter.util.HttpClientUtils;
import com.cheche365.cheche.wallet.utils.RandomUitl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yellow on 2017/9/25.
 * 天润呼叫对接
 */
@Service
public class ClinkService {
    private static Logger logger = LoggerFactory.getLogger(ClinkService.class);
    public static final Integer STATUS = 1;
    public static final Integer TYPE = 1;

    @Autowired
    private InternalUserManageService internalUserManageService;

    public void onLine(){
        InternalUser internalUser=internalUserManageService.getCurrentInternalUser();
        TelMarketer marketer = internalUser.getTelMarketer();
        if(marketer == null){
            return;
        }
        String randomStr=randomStr();
        Map<String, String> paramsMap = new HashMap(){{
            put("enterpriseId",OrderCenterConstants.CLINK_ENTERPRISE_ID);
            put("userName",OrderCenterConstants.CLINK_USER);
            put("pwd", MD5.MD5Encode(OrderCenterConstants.CLINK_PWD + randomStr));
            put("seed",randomStr);
            put("cno",marketer.getCno());
            put("status",STATUS);
            put("bindTel",marketer.getBindTel());
            put("type",TYPE);
        }};
        logger.debug("clink online request json : {}", CacheUtil.doJacksonSerialize(paramsMap));
        try{
            String response = HttpClientUtils.doGetWithHeader(null, OrderCenterConstants.CLINK_ON_LINE_URL, paramsMap, null);
            LineStatus status= CacheUtil.doJacksonDeserialize(response,LineStatus.class);
            logger.debug("clink online response json : {}", response);
        }catch(Exception e){
            logger.error("clink online error:" ,e.getMessage());
        }
    }

    public void offLine(){
        InternalUser internalUser=internalUserManageService.getCurrentInternalUser();
        TelMarketer marketer = internalUser.getTelMarketer();
        if(marketer == null){
            return;
        }
        String randomStr=randomStr();
        Map<String, String> paramsMap = new HashMap(){{
            put("enterpriseId",OrderCenterConstants.CLINK_ENTERPRISE_ID);
            put("userName",OrderCenterConstants.CLINK_USER);
            put("pwd",MD5.MD5Encode(OrderCenterConstants.CLINK_PWD + randomStr));
            put("seed",randomStr);
            put("cno",marketer.getCno());
        }};
        logger.debug("clink offLine request json : {}", CacheUtil.doJacksonSerialize(paramsMap));
        String response = HttpClientUtils.doGetWithHeader(null, OrderCenterConstants.CLINK_OFF_LINE_URL, paramsMap, null);
        LineStatus status= CacheUtil.doJacksonDeserialize(response,LineStatus.class);
        logger.debug("clink offline return json : {}", response);

    }

    /**
     *
     * @param customerNumber 呼叫号码
     * @param tag 自定义字段，传入电销数据的来源信息，rebate表中最新的telMarketingCenter-name  和sourceId
     * @return
     */
    public CallStatus call(String customerNumber,String tag){
        InternalUser internalUser=internalUserManageService.getCurrentInternalUser();

        TelMarketer marketer = internalUser.getTelMarketer();
        if(marketer == null){
            return new CallStatus(100);
        }
        Map<String, String> paramsMap = new HashMap(){{
            put("enterpriseId",OrderCenterConstants.CLINK_ENTERPRISE_ID);
            put("cno",marketer.getCno());
            put("pwd",OrderCenterConstants.CLINK_PWD);
            put("customerNumber",customerNumber);
            put("sync","1");
            put("userField",tag);
        }};
        logger.debug("clink call request json : {}", CacheUtil.doJacksonSerialize(paramsMap));
        String response = HttpClientUtils.doGetWithHeader(null, OrderCenterConstants.CLINK_CALL_URL, paramsMap, null);
        CallStatus status= CacheUtil.doJacksonDeserialize(response,CallStatus.class);
        logger.debug("clink call return json : {}", response);
        return status;
    }

    private String randomStr(){
        return  RandomUitl.generateString(8);
    }

}

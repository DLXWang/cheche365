package com.cheche365.cheche.ordercenter.service.telMarketingCenter;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.ChannelRepository;
import com.cheche365.cheche.core.repository.InternalUserRepository;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterHistoryRepository;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterRepository;
import com.cheche365.cheche.manage.common.service.TelMarketingCenterService;
import com.cheche365.cheche.core.service.UserService;
import com.cheche365.cheche.manage.common.model.TelMarketingCenter;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterHistory;
import com.cheche365.cheche.core.model.TelMarketingCenterSource;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterStatus;
import com.cheche365.cheche.ordercenter.service.user.OrderCenterInternalUserManageService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by xu.yelong on 2016-05-03.
 */
@Service
public class NewTelCustomerService {
    @Autowired
    private TelMarketingCenterRepository telMarketingCenterRepository;

    @Autowired
    private TelMarketingCenterService telMarketingCenterService;

    @Autowired
    private TelMarketingCenterHistoryRepository telMarketingCenterHistoryRepository;

    @Autowired
    private OrderCenterInternalUserManageService orderCenterInternalUserManageService;

    @Autowired
    private InternalUserRepository internalUserRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ChannelRepository channelRepository;

    @Transactional
    public void save(Long operatorId,Long channelId,String mobiles,String comment){
        String[] mobileArray=mobiles.split(",");
        for(String mobile:mobileArray){
            Channel channel=channelRepository.findById(channelId);
            List<TelMarketingCenter> telMarketingCenterList=this.saveTelMarketingCenter(operatorId,mobile);
            for(TelMarketingCenter telMarketingCenter:telMarketingCenterList){
                telMarketingCenterService.saveTelMarketingCenterRepeat(telMarketingCenter, TelMarketingCenterSource.Enum.CUSTOMER_TO_QUOTE,new Date(),null,null,channel,null);
                this.saveHistory(telMarketingCenter,comment);
            }
        }
    }

    public List<TelMarketingCenter> saveTelMarketingCenter(Long operatorId,String mobile){
        InternalUser internalUser=null;
        if(operatorId!=null){
            internalUser=internalUserRepository.findOne(operatorId);
        }

        List<TelMarketingCenter> telMarketingCenterList=telMarketingCenterService.findByMobile(mobile);
        if(CollectionUtils.isEmpty(telMarketingCenterList)){
            User user=userService.getBindingUser(mobile);
            TelMarketingCenter telMarketingCenter=new TelMarketingCenter();
            if(user!=null){
                telMarketingCenter.setUser(user);
                telMarketingCenter.setUserName(user.getName());
            }
            if(internalUser!=null){
                telMarketingCenter.setOperator(internalUser);
                telMarketingCenter.setDisplay(true);
            }
            telMarketingCenter.setSource(TelMarketingCenterSource.Enum.CUSTOMER_TO_QUOTE);
            telMarketingCenter.setPriority(1);
            telMarketingCenter.setProcessedNumber(0L);
            telMarketingCenter.setMobile(mobile);
            telMarketingCenter.setCreateTime(new Date());
            telMarketingCenter.setUpdateTime(new Date());
            telMarketingCenter.setStatus(TelMarketingCenterStatus.Enum.UNTREATED);
            telMarketingCenter.setSourceCreateTime(new Date());
            telMarketingCenterList.add(telMarketingCenterRepository.save(telMarketingCenter));
        }else{
            for(TelMarketingCenter telMarketingCenter:telMarketingCenterList){
                if(telMarketingCenter.getOperator()==null&&internalUser!=null){
                    telMarketingCenter.setOperator(internalUser);
                }
                telMarketingCenter.setDisplay(true);
                telMarketingCenter.setSourceCreateTime(new Date());
                telMarketingCenter.setSource(TelMarketingCenterSource.Enum.CUSTOMER_TO_QUOTE);
            }
            telMarketingCenterRepository.save(telMarketingCenterList);
        }
        return telMarketingCenterList;
    }

    public void saveHistory(TelMarketingCenter telMarketingCenter,String comment){
        TelMarketingCenterHistory telMarketingCenterHistory=new TelMarketingCenterHistory();
        telMarketingCenterHistory.setOperator(orderCenterInternalUserManageService.getCurrentInternalUser());
        telMarketingCenterHistory.setTelMarketingCenter(telMarketingCenter);
        telMarketingCenterHistory.setDealResult("客服转报价");
        telMarketingCenterHistory.setCreateTime(new Date());
        telMarketingCenterHistory.setComment(comment);
        telMarketingCenterHistory.setType(5);
        telMarketingCenterHistoryRepository.save(telMarketingCenterHistory);
    }

    public String check(String mobiles){
        String[] mobileArray=mobiles.split(",");
        StringBuffer operators=new StringBuffer();
        for(String mobile:mobileArray){
            List<TelMarketingCenter> telMarketingCenterList=telMarketingCenterRepository.findByMobile(mobile);
            if(CollectionUtils.isEmpty(telMarketingCenterList)){
                continue;
            }

            for(TelMarketingCenter telMarketingCenter:telMarketingCenterList){
                if(telMarketingCenter.getOperator()!=null){
                    operators.append(telMarketingCenter.getOperator().getName()).append(" ");
                }
            }
        }
        return operators.toString();
    }
}

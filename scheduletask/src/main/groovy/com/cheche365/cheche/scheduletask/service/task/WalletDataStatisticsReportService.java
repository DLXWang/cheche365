package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.scheduletask.model.WalletDataInfo;
import com.cheche365.cheche.wallet.repository.WalletRepository;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by wangshaobin on 2017/7/28.
 */
@Service
public class WalletDataStatisticsReportService {
    Logger logger = LoggerFactory.getLogger(WalletDataStatisticsReportService.class);

    @Autowired
    private WalletRepository walletRepository;

    /**
     * 重要说三遍：
     *   所有的数据渠道都取ToC的，即：排除ToA的
     *   所有的数据渠道都取ToC的，即：排除ToA的
     *   所有的数据渠道都取ToC的，即：排除ToA的
     * **/
    public Map<String,List<WalletDataInfo>> getWalletExcelInfo(){
        List<Channel> toAChannels = Channel.agents();
        Date yesterdayStart = DateUtils.getCustomDate(new Date(), -1, 0, 0, 0);
        Date yesterdayEnd = DateUtils.getCustomDate(new Date(), -1, 23, 59, 59);
        Map<String,List<WalletDataInfo>> excelInfo = new HashMap<String,List<WalletDataInfo>>();
        WalletDataInfo info = new WalletDataInfo();
        //获取钱包总余额、钱包总数
        List<Object[]> walletCountAndBalance = walletRepository.findWalletCountAndBalance(toAChannels);
        //获取提现总金额、提现总次数、提现用户总数
        List<Object[]> outNumberAndAmountAndUserCount = walletRepository.findOutNumberAndAmountAndUserCount(toAChannels);
        //钱包活跃数
        Long activeWalletCount = walletRepository.findActiveWalletCount(toAChannels);
        //绑定银行卡总数、绑定银行卡的用户数、删除银行卡的用户数
        List<Object[]> bindCountAndBindUserAndDelUserCount = walletRepository.findbindCountAndBindUserAndDelUserCount(toAChannels);
        //查询昨日提现情况：昨日提现金额、昨日提现次数、昨日提现用户数、昨日提现失败数、昨日提现成功数、昨日提现中数
        List<Object[]> yesterdayOutInfo = walletRepository.findYesterdayOutInfo(yesterdayStart, yesterdayEnd, toAChannels);

        Object[] data = null;
        if(CollectionUtils.isNotEmpty(walletCountAndBalance)){
            data = walletCountAndBalance.get(0);
            info.setWalletCount(StringUtil.defaultNullStr(data[0]));
            info.setWalletTotalBalance(StringUtil.defaultNullStr(data[1]));
        }
        if(CollectionUtils.isNotEmpty(outNumberAndAmountAndUserCount)){
            data = outNumberAndAmountAndUserCount.get(0);
            info.setOutTotalNumber(StringUtil.defaultNullStr(data[0]));
            info.setOutTotalAmount(StringUtil.defaultNullStr(data[1]));
            info.setOutUserCount(StringUtil.defaultNullStr(data[2]));
        }
        info.setActiveWalletCount(StringUtil.defaultNullStr(activeWalletCount));
        if(CollectionUtils.isNotEmpty(bindCountAndBindUserAndDelUserCount)){
            data = bindCountAndBindUserAndDelUserCount.get(0);
            info.setBindingBankCardCount(StringUtil.defaultNullStr(data[0]));
            info.setBindingBankCardUserCount(StringUtil.defaultNullStr(data[1]));
            info.setDelBankCardCount(StringUtil.defaultNullStr(data[2]));
        }
        if(CollectionUtils.isNotEmpty(yesterdayOutInfo)){
            data = yesterdayOutInfo.get(0);
            info.setYesterdayOutAmount(StringUtil.defaultNullStr(data[0]));
            info.setYesterdayOutNumber(StringUtil.defaultNullStr(data[1]));
            info.setYesterdayOutUserCount(StringUtil.defaultNullStr(data[2]));
            info.setYesterdayOutSuccessCount(StringUtil.defaultNullStr(data[3]));
            info.setYesterdayOutFailCount(StringUtil.defaultNullStr(data[4]));
            info.setYesterdayOutingCount(StringUtil.defaultNullStr(data[5]));
        }

        List<WalletDataInfo> list = Arrays.asList(info);
        excelInfo.put("walletData",list);
        return excelInfo;
    }
}

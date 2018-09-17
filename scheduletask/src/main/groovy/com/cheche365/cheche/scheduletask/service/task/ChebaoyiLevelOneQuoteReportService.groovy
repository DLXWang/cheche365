package com.cheche365.cheche.scheduletask.service.task

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.model.LogType
import com.cheche365.cheche.core.model.MoApplicationLog
import com.cheche365.cheche.core.model.agent.AgentLevel
import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository
import com.cheche365.cheche.core.repository.UserRepository
import com.cheche365.cheche.core.repository.agent.ChannelAgentRepository
import com.cheche365.cheche.manage.common.service.BaseService
import com.cheche365.cheche.scheduletask.model.ChebaoyiLevelOneQuoteDataReportModel
import grails.async.Promises
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit

/**
 * 获取指定车保易level one用户${mobileList}名下的level two用户指定时间范围[${startTime}-${endTime}]内的报价数据
 * Created by yinJianBin on 2018/8/1.
 */
@Service
@Slf4j
class ChebaoyiLevelOneQuoteReportService extends BaseService {

    @Autowired
    MoApplicationLogRepository moApplicationLogRepository
    @Autowired
    ChannelAgentRepository channelAgentRepository
    @Autowired
    UserRepository userRepository

    List<ChebaoyiLevelOneQuoteDataReportModel> getQuoteData(Date startTime, Date endTime, List<String> mobileList) {
        List<ChebaoyiLevelOneQuoteDataReportModel> modelList = new CopyOnWriteArrayList<>()
        Promises.tasks(
                mobileList.collect() { mobile ->
                    return { getDataListByMobile(mobile, startTime, endTime, modelList) }
                }
        ).get(3L, TimeUnit.HOURS)
        modelList
    }

    def getDataListByMobile = { String levelOneMobile, Date startTime, Date endTime, modelList ->
        ChannelAgent channelAgent = channelAgentRepository.findFirstByUserMobileOrderByIdDesc(levelOneMobile)
        List<String> subLevelMobileList = channelAgentRepository.findSubLevelMobileByMobile(levelOneMobile, AgentLevel.Enum.SALE_MANAGER_2.id)
        List<MoApplicationLog> logList = moApplicationLogRepository.findAllByCreateTimeAndObjTableAndMobileList(startTime, endTime, subLevelMobileList, LogType.Enum.Quote_Cache_Record_31.id)
        def hashSet = new HashSet(logList.size())
        def results = new ArrayList(logList.size())
        logList.each() {
            Map quoteRecordMap = it.logMessage
            def agentName = userRepository.findOne(it.user.id)?.name
            def licensePlateNo = quoteRecordMap.get('auto')?.get('licensePlateNo')
            if (hashSet.add(agentName + licensePlateNo)) {
                def model = new ChebaoyiLevelOneQuoteDataReportModel(
                        mobile: it.getUser()?.getMobile(),
                        agentName: agentName,
                        sellDirectorName: channelAgent?.getUser()?.getName(),
                        ownerName: quoteRecordMap.get('auto')?.get('owner'),
                        licensePlateNo: licensePlateNo,
                        insuranceCompanyName: quoteRecordMap.get('insuranceCompany')?.get('name'),
                        areaName: quoteRecordMap.get('area')?.get('name'),
                        quoteTime: DateUtils.getDateString(it.createTime, DateUtils.DATE_LONGTIME24_PATTERN),
                )
                results << model
            }

        }
        log.debug("获取level one mobile:{}下的level two报价数据线程执行完毕,获取到数据:{}条", levelOneMobile, results.size())
        results && modelList.addAll(results)
    }

}




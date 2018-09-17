package com.cheche365.cheche.scheduletask.service.task

import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.repository.AgentRepository
import com.cheche365.cheche.manage.common.model.UserRemitTradeHistory
import com.cheche365.cheche.manage.common.repository.UserRemitTradeHistoryRepository
import com.cheche365.cheche.scheduletask.model.ChebaoyiWithdrawalInfoModel
import com.cheche365.cheche.wallet.model.WalletTradeStatus
import com.cheche365.cheche.wallet.repository.WalletRemitRepository
import groovy.util.logging.Slf4j
import org.joda.time.LocalDate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

/**
 * Created by yinJianBin on 2018/4/3.
 */
@Service
@Slf4j
class ChebaoyiWithdrawalReportService {
    private def final MERCHANT_SEQ_NO_PREFIX = "A1"
    private def final MERCHANT_SEQ_NO_REDIS_KEY_PREFIX = "merchantSeqNo:"
    private def final LONG_TO_STRING_FORMAT_PATTERN = "00000"

    @Autowired
    private AgentRepository agentRepository
    @Autowired
    private WalletRemitRepository walletRemitRepository
    @Autowired
    private UserRemitTradeHistoryRepository userRemitTradeHistoryRepository
    @Autowired
    private StringRedisTemplate stringRedisTemplate

    @Transactional
    Map<String, List<ChebaoyiWithdrawalInfoModel>> getWithdrawalInfo(startTime, endTime) {

        updateAgentInternal()
        System.out.println(Channel.levelAgents()*.id)
        def collectInfoObject = walletRemitRepository.getChebaoyiWithdrawalCollectInfo(startTime, endTime, Channel.levelAgents()*.id)
        def detailInfoObjects = walletRemitRepository.getChebaoyiWithDrawalDetailInfo(startTime, endTime, Channel.levelAgents()*.id)
        def excelReportMap = [
                "collectInfo": getCollectInfo(collectInfoObject),
                "detailInfo" : getDetailInfo(detailInfoObjects, startTime, endTime)
        ]
        excelReportMap
    }

    static def getCollectInfo(Object[] collectInfoObject) {
        [
                new ChebaoyiWithdrawalInfoModel(
                        sumCount: collectInfoObject[0],
                        sumAmount: collectInfoObject[1]
                )
        ]
    }

    def getDetailInfo(List<Object[]> detailInfoObjects, startTime, endTime) {
        ChebaoyiWithdrawalInfoModel model
        List<ChebaoyiWithdrawalInfoModel> modelList = detailInfoObjects.collect {
            Object[] object ->
                model = new ChebaoyiWithdrawalInfoModel(
                        merchantSeqNo: object[3],
                        accountNo: object[0],
                        accountName: object[1],
                        identity: object[2] ?: '',
                        userId: object[3],
                        walletTradeId: object[4],
                        transferAmount: object[5],
                        requestNo: object[6]
                )
        }

        def resultList = []
        modelList.groupBy {
            it.userId
        }.each { userId, userDataList ->
            log.debug("userId:{$userId}对应的数据${userDataList.size()}条,包含的商户号:${userDataList*.accountNo.join(',')} ")
            userDataList.groupBy { userModel ->
                userModel.accountNo
            }.each { accountNo, userAccountDataList ->
                def merchantSeqNo = getMerchantSeqNo(LocalDate.fromDateFields(endTime).toString("yyyyMMdd"))
                log.debug("userId:{$userId},account:${accountNo}对应的数据${userDataList.size()}条,生成商户号:${merchantSeqNo}")
                userAccountDataList.each { withdrawalInfo ->
                    def remitRecord = walletRemitRepository.findByRequestNo(withdrawalInfo.requestNo, userId as Long)
                    remitRecord.setStatus(WalletTradeStatus.Enum.PROCESSING_5)
                    remitRecord.setUpdateTime(new Date())
                    walletRemitRepository.save(remitRecord)
                    def userRemitHistory = new UserRemitTradeHistory(
                            userId: userId as Long,
                            merchantSeqNo: merchantSeqNo,
                            walletRemitTradeId: withdrawalInfo.walletTradeId as Long,
                            requestNo: withdrawalInfo.requestNo,
                            startTime: startTime,
                            endTime: endTime
                    )
                    userRemitTradeHistoryRepository.save(userRemitHistory)
                }

                def resultModel = new ChebaoyiWithdrawalInfoModel(
                        merchantSeqNo: merchantSeqNo,
                        accountNo: accountNo,
                        accountName: userAccountDataList[0].accountName,
                        identity: userAccountDataList[0].identity,
                        transferAmount: userAccountDataList.inject(0.00d) { previous, entry -> previous + (entry.transferAmount as Double) }
                )
                resultList << resultModel
            }
        }
        return resultList
    }

    String getMerchantSeqNo(String dateString) {
        def baseNo = MERCHANT_SEQ_NO_PREFIX + dateString
        String key = MERCHANT_SEQ_NO_REDIS_KEY_PREFIX + baseNo
        def startIndex = 1
        long index = stringRedisTemplate.opsForValue().increment(key, startIndex)
        if (index == startIndex) stringRedisTemplate.expire(key, 1, TimeUnit.DAYS) //一天后过期
        def seqNo = new DecimalFormat(LONG_TO_STRING_FORMAT_PATTERN).format(index)
        return baseNo + seqNo
    }

    @Transactional(readOnly = false)
    void updateAgentInternal(){
        agentRepository.updateAgentInternal()
    }
}

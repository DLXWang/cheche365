package com.cheche365.cheche.manage.common.offlinedata

import com.cheche365.cheche.bihu.app.config.BihuConfig
import com.cheche365.cheche.core.app.config.CoreConfig
import com.cheche365.cheche.manage.common.app.config.ManageCommonConfig
import com.cheche365.cheche.manage.common.model.InsuranceOfflineDataModel
import com.cheche365.cheche.manage.common.service.offlinedata.OfflineDataService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import java.text.SimpleDateFormat

@Transactional
@ContextConfiguration(classes = [CoreConfig, ManageCommonConfig, BihuConfig])
class OfflineDataServiceFT extends Specification {

    @Autowired
    private OfflineDataService service

    @Rollback(false)
    def 'offline-check'() {
        given:
        def file = OfflineDataServiceFT.getResource('data.csv').file as File
        def lines = file.readLines()[11..13].collect {
            it.split(',')
        }.collect {
            println it
            new InsuranceOfflineDataModel([
                order               : it[0],
                createTime          : it[2],
                institution         : it[3],
                insuranceCompanyName: it[5],
                agentName           : it[6],
                agentIdentity       : it[7],
                owner               : it[8],
                licenseNo           : it[9],
                totalPremium        : it[10],
                compulsory          : it[11],
                autoTax             : it[12],
                premium             : it[13],
                upCompulsoryRebate  : it[14],
                upCommercialRebate  : it[15],
                upCommercialAmount  : it[16],
                upCompulsoryAmount  : it[16],
                policyNo            : it[17],
                engineNo            : it[18],
                vinNo               : it[19],
                downCommercialAmount: it[22],
                downCompulsoryAmount: it[23],
                insuranceType       : it[13] ? '商业险' : '交强险',
            ])
        }
        when: ''


        then: '调用服务'
        def df = new SimpleDateFormat('yyyy-MM-dd')
        service.exportData lines, [chunkSize: 2, startInsuredDate: df.parse('2017-03-01'), endInsuredDate: df.parse('2019-05-01')]
        where: ''
    }

//    @Rollback(false)
//    def 'offline'() {
//        given:
//        def lines = [
//            new InsuranceOfflineDataModel('2393', '金联安', '人保', '2017-03-09', 'PDAA201632050000443262', '黄朱顺', '043707D', '苏EV98K1', 'LGBF5DE0XDR041418', '5511.67', '950.00', '300.00', '"4,261.67"', '0.26', '0.04', '1146.03', '4365.64', '26+4', '转支付宝/转公户', '金联安'),
//        ]
//
//        when: ''
//
//
//        then: '调用服务'
//        def df = new SimpleDateFormat('yyyy-MM-dd')
//        service.exportData lines, [chunkSize: 500, startInsuredDate : df.parse('2017-03-01'), endInsuredDate : df.parse('2017-05-01')]
//
//        where: ''
//    }
}

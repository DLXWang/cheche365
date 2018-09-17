package com.cheche365.cheche.operationcenter.web.model.tide

import com.cheche365.cheche.core.model.tide.TideContractHistory
import com.fasterxml.jackson.annotation.JsonFormat
import groovy.transform.ToString

/**
 * Created by yinJianBin on 2017/6/13.
 */
@ToString
class ContractHistoryViewModel {
    Long historyId
    Long contractId
    Long platformId
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    Date effectiveDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    Date expireDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    def createTime
    def operator
    Integer operationType


    Integer currentPage
    Integer pageSize
    Integer draw

    static ContractHistoryViewModel buildViewData(TideContractHistory history) {
        def model = new ContractHistoryViewModel(
                historyId: history.id,
                contractId: history.tideContract.id,
                effectiveDate: history.effectiveDate,
                expireDate: history.expireDate,
                operator: history.operator?.name,
                createTime: history.createTime,
        )
        model
    }

}

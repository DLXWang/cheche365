package com.cheche365.cheche.operationcenter.web.model.tide

import com.cheche365.cheche.core.model.tide.TideContractSupportArea
import com.fasterxml.jackson.annotation.JsonFormat

/**
 * Created by yinJianBin on 2017/6/13.
 */
class ContractAreaViewModel {
    def contractAreaId
    String areaName
    Boolean disable

    Integer currentPage
    Integer pageSize
    Integer draw

    static ContractAreaViewModel buildViewData(TideContractSupportArea contractSupportArea) {
        def model = new ContractAreaViewModel(
                contractAreaId: contractSupportArea.id,
                areaName: contractSupportArea.supportArea?.name,
                disable: contractSupportArea.disable
        )
        model
    }

}

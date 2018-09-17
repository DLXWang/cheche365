package com.cheche365.cheche.operationcenter.web.model.tide

import com.cheche365.cheche.core.model.tide.TideInstitution
import com.cheche365.cheche.manage.common.constants.TideConstants
import com.fasterxml.jackson.annotation.JsonFormat

/**
 * Created by yinJianBin on 2017/6/13.
 */
class InstitutionViewModel {
    Long id
    Long branchId
    Long platformId
    String institutionName
    String description
    def status
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date createTime
    String operator

    Integer currentPage
    Integer pageSize
    Integer draw

    static InstitutionViewModel buildViewData(TideInstitution institution) {
        def model = new InstitutionViewModel(
                id: institution.id,
                institutionName: institution.institutionName,
                status: TideConstants.STATUS_MAP.get(institution.status),
                createTime: institution.createTime,
                operator: institution.operator?.name
        )
        model
    }

}

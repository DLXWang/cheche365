package com.cheche365.cheche.operationcenter.web.model.tide

import com.cheche365.cheche.core.model.tide.UploadFile
import com.fasterxml.jackson.annotation.JsonFormat

/**
 * Created by yinJianBin on 2017/6/13.
 */
class FileViewModel {
    def sourceId
    def fileId
    String fileName
    @JsonFormat(timezone = "GMT+8", pattern = 'yyyy-MM-dd hh:mm:ss')
    def createTime
    def operator
    def fileUrl

    static FileViewModel buildViewData(UploadFile uploadFile) {
        def model = new FileViewModel(
                sourceId: uploadFile.sourceId,
                fileId: uploadFile.id,
                createTime: uploadFile.createTime,
                operator: uploadFile.operator?.name
        )
        model
    }

}

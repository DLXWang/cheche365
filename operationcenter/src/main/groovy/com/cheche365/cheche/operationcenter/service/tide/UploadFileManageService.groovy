package com.cheche365.cheche.operationcenter.service.tide

import com.cheche365.cheche.core.model.tide.UploadFile
import com.cheche365.cheche.core.repository.tide.UploadFileRepository
import com.cheche365.cheche.core.service.ResourceService
import com.cheche365.cheche.operationcenter.web.model.tide.FileViewModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by yinJianBin on 2018/4/21.
 */
@Service
class UploadFileManageService {

    @Autowired
    UploadFileRepository uploadFileRepository
    @Autowired
    ResourceService resourceService;

    def save(UploadFile uploadFile) {
        uploadFileRepository.save(uploadFile)
    }

    def save(filePath, fileName, sourceType, sourceId, status, operator) {
        new UploadFile(
                filePath: filePath,
                fileName: fileName,
                sourceType: sourceType,
                sourceId: sourceId,
                status: status,
                operator: operator
        ).with {
            uploadFileRepository.save(it)
        }
    }

    def getBySourceTypeAndSourceId(Integer sourceType, Long sourceId) {
        uploadFileRepository.findAllBySourceTypeAndSourceIdAndStatus(sourceType, sourceId, UploadFile.Enum.STATUS_ACTIVE)
    }

    def getViewModelBySourceTypeAndSourceId(Integer sourceType, Long sourceId) {
        def fileList = getBySourceTypeAndSourceId(sourceType, sourceId)
        fileList.collect() {
            new FileViewModel(
                    sourceId: sourceId,
                    fileId: it.id,
                    fileName: it.fileName,
                    operator: it.operator?.name,
                    createTime: it.createTime,
                    fileUrl: resourceService.absoluteUrl(resourceService.getResourceUrl(it.filePath))
            )
        }
    }

    def deleteFile(long fileId) {
        def uploadFile = uploadFileRepository.findOne(fileId)
        uploadFile.setStatus(UploadFile.Enum.STATUS_DISABLE)
        uploadFileRepository.save(uploadFile)
    }
}

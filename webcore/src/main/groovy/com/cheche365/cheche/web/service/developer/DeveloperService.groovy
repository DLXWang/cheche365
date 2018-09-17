package com.cheche365.cheche.web.service.developer

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.developer.DeveloperInfo
import com.cheche365.cheche.core.model.developer.DeveloperType
import com.cheche365.cheche.core.repository.developer.DeveloperBusinessTypeRepository
import com.cheche365.cheche.core.repository.developer.DeveloperInfoRepository
import com.cheche365.cheche.core.repository.developer.DeveloperTypeRepository
import com.cheche365.cheche.core.util.ValidationUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by zhengwei on 08/03/2018.
 */

@Service
class DeveloperService {

    @Autowired
    DeveloperTypeRepository typeRepository
    @Autowired
    DeveloperBusinessTypeRepository businessTypeRepository
    @Autowired
    DeveloperInfoRepository infoRepository

    void add(DeveloperInfo devInfo){
        formatDevInfo(devInfo)
        infoRepository.save(devInfo)
    }

    void formatDevInfo(DeveloperInfo devInfo){
        if(!devInfo.developerTypeId){
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, '请选择对接类型')
        }
        if(DeveloperType.Enum.COMPANY_1.id == devInfo.developerTypeId && !devInfo.developerBusinessTypeId){
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, '请选择所属行业')
        }
        if(!ValidationUtil.validMobile(devInfo.mobile)) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, '手机号格式校验错误')
        }

        devInfo.developerType = typeRepository.findAll().find {it.id == devInfo.developerTypeId}
        devInfo.developerBusinessType = businessTypeRepository.findAll().find {it.id == devInfo.developerBusinessTypeId}

    }
}

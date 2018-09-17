package com.cheche365.cheche.scheduletask.service.task

import com.cheche365.cheche.core.model.developer.DeveloperInfo
import com.cheche365.cheche.core.repository.developer.DeveloperInfoRepository
import com.cheche365.cheche.scheduletask.constants.TaskConstants
import com.cheche365.cheche.scheduletask.model.OpenSignupReportModel
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.math.NumberUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

import static com.cheche365.cheche.scheduletask.constants.TaskConstants.DEVELOPER_INFO_ID_CACHE

/**
 * Created by zhangtc on 2018/3/13.
 */
@Service
class OpenSignupReportService {

    Logger logger = LoggerFactory.getLogger(OpenSignupReportService.class)
    @Autowired
    private StringRedisTemplate stringRedisTemplate

    @Autowired
    DeveloperInfoRepository developerInfoRepository

    List<OpenSignupReportModel> getDeveloperInfoList() {
        List<OpenSignupReportModel> reportModelList = new ArrayList<>()

        String developerInfoIdStr = stringRedisTemplate.opsForValue().get(DEVELOPER_INFO_ID_CACHE)
        if (StringUtils.isEmpty(developerInfoIdStr)) {
            developerInfoIdStr = "0"
        }
        logger.debug("schedule task starting-->  get developer info data,greater than id --> [{}]", developerInfoIdStr)

        List<DeveloperInfo> list = developerInfoRepository.findByIdGreaterThan(NumberUtils.toLong(developerInfoIdStr))

        if (CollectionUtils.isEmpty(list)) {
            return reportModelList
        }
        logger.debug("id大于{}范围内的申请数量为{}", developerInfoIdStr, list.size())
        list.collect() {
            map ->
                OpenSignupReportModel model = new OpenSignupReportModel()
                model.setDeveloperType(map.developerType?.description)
                model.setDeveloperBusinessType(map.developerBusinessType?.description)
                model.setCompany(map.company)
                model.setMobile(map.mobile)
                model.setContactName(map.contactName)
                model.setEmail(map.email)
                model.setAddress(map.address)
                reportModelList.add(model)
        }
        developerInfoIdStr = list.get(list.size() - 1).getId().toString()
        stringRedisTemplate.opsForValue().set(DEVELOPER_INFO_ID_CACHE, developerInfoIdStr)
        return reportModelList
    }
}

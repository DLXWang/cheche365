package com.cheche365.cheche.operationcenter.service.thirdPartyCooperation

import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.manage.common.model.TelMarketingCenterChannelFilter
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterChannelFilterRepository
import com.cheche365.cheche.manage.common.service.InternalUserManageService
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 渠道过滤配置
 ** 进电销配置及查询
 * Created by zhangtc on 2018/4/26.
 */
@Service
class ChannelFilterService {

    private Logger logger = LoggerFactory.getLogger(this.getClass())
    @Autowired
    InternalUserManageService internalUserManageService
    @Autowired
    TelMarketingCenterChannelFilterRepository telMarketingCenterChannelFilterRepository

    /**
     * 是否进电销
     ** false-禁止全部行为
     * @param channel
     * @return
     */
    public Boolean isEntry(Channel channel) {
        boolean result = false
        if (channel == null) {
            return result
        }
        List<TelMarketingCenterChannelFilter> filters = telMarketingCenterChannelFilterRepository.findAll()
        for (TelMarketingCenterChannelFilter filter : filters) {
            if (!getTarget(filter.getExcludeChannels(), channel.id.toString())) {
                result = true
                break
            }
        }
        return result
    }

    /**
     * 设置是否进入电销
     * @param channel
     * @param entry t
     * @return
     */
    @Transactional
    public Boolean filterChannelAll(Channel channel, boolean entry) {
        if (channel == null) {
            return false
        }
        boolean result = true
        Iterable<TelMarketingCenterChannelFilter> filters = telMarketingCenterChannelFilterRepository.findAll()
        filters.collect() { filter ->
            String excludeChannels = setTarget(filter.getExcludeChannels(), channel.id as Integer, !entry)
            filter.setExcludeChannels(excludeChannels)
            result = result && save(filter)
        }
        return result
    }


    private boolean save(TelMarketingCenterChannelFilter filter) {
        if (filter == null) {
            return false
        }
        filter.setOperator(internalUserManageService.getCurrentInternalUser())
        filter.setUpdateTime(new Date())
        telMarketingCenterChannelFilterRepository.save(filter)
        return true
    }

    private static boolean getTarget(String arr, String targetValue) {
        arr = arr == null ? '' : arr
        Set<String> set = new HashSet<String>(Arrays.asList(arr.split(',')))
        return set.contains(targetValue)
    }

    private static String setTarget(String arr, Integer targetValue, boolean add) {
        arr = arr == null ? '' : arr
        if (targetValue == null) {
            return arr
        }
        String result = ''
        Object[] os = arr.split(",")
        Set<Integer> set = new TreeSet<Integer>()
        os.each { o ->
            if (StringUtils.isNotBlank(o)) {
                set.add(Integer.valueOf(o))
            }
        }
        add ? set.add(targetValue) : set.remove(targetValue)
        set.each { c ->
            result += c + ','
        }
        result = result.length() > 0 ? result.substring(0, result.length() - 1) : result
        result
    }
}

package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.UserLoginInfoRepository;
import com.cheche365.cheche.manage.common.service.TelMarketingCenterChannelFilterService;
import com.cheche365.cheche.manage.common.service.TelMarketingCenterService;
import com.cheche365.cheche.core.model.TelMarketingCenterSource;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterTaskType;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by xu.yelong on 2016/11/17.
 */
@Service
public class TmcUserLoginInfoImportService {

    private Logger logger = LoggerFactory.getLogger(TmcUserLoginInfoImportService.class);

    @Autowired
    private TelMarketingCenterService telMarketingCenterService;

    @Autowired
    private UserLoginInfoRepository userLoginInfoRepository;

    @Autowired
    private TelMarketingCenterChannelFilterService taskExcludeChannelSettingService;

    public void importData() {
        Date startTime = DateUtils.getCustomDate(new Date(), -1, 0, 0, 0);
        Date endTime = DateUtils.getCustomDate(new Date(), -1, 23, 59, 59);
        int pageNum = TaskConstants.PAGE_NUMBER;
        List excludeChannelList= taskExcludeChannelSettingService.findExcludeChannelsByTaskType(TelMarketingCenterTaskType.Enum.LOGIN);
        List<UserLoginInfo> userLoginInfoList = userLoginInfoRepository.findByLastLoginTimeBetween(startTime, endTime,excludeChannelList, pageNum, TaskConstants.PAGE_SIZE);
        int sum = 0;
        while (CollectionUtils.isNotEmpty(userLoginInfoList)) {
            sum += userLoginInfoList.size();
            for (UserLoginInfo userLoginInfo : userLoginInfoList) {
                Channel channel = userLoginInfo.getChannel() == null ? Channel.Enum.WAP_8 : userLoginInfo.getChannel();
                telMarketingCenterService.save(userLoginInfo.getUser(), userLoginInfo.getUser().getMobile(), TelMarketingCenterSource.Enum.USER_LOGIN_INFO, null, userLoginInfo.getLastLoginTime(), userLoginInfo.getId(), "user_login_info", channel);
            }
            if (userLoginInfoList.size() < 1000) {
                break;
            }
            pageNum += 1000;
            userLoginInfoList = userLoginInfoRepository.findByLastLoginTimeBetween(startTime, endTime,excludeChannelList, pageNum, TaskConstants.PAGE_SIZE);
        }
        logger.info("import user login info date to tel marketing center,sum data size:->{}", sum);
    }
}

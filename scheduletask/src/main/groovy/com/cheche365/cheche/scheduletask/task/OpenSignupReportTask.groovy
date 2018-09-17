package com.cheche365.cheche.scheduletask.task

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.model.developer.DeveloperInfo
import com.cheche365.cheche.core.repository.developer.DeveloperInfoRepository
import com.cheche365.cheche.email.model.EmailInfo
import com.cheche365.cheche.scheduletask.model.MessageInfo
import com.cheche365.cheche.scheduletask.model.OpenSignupReportModel
import com.cheche365.cheche.scheduletask.service.task.OpenSignupReportService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * 凌云用户合作申请
 * Created by zhangtc on 2018/3/9.
 */
@Service
class OpenSignupReportTask extends BaseTask {

    private String emailconfigPath = "/emailconfig/open_signup_report.yml"

    @Autowired
    OpenSignupReportService openSignupReportService

    @Override
    protected void doProcess() throws Exception {
        getMessageInfo()
    }

    private void getMessageInfo() throws IOException {
        List<OpenSignupReportModel> reportModelList = openSignupReportService.getDeveloperInfoList()
        if (reportModelList != null && reportModelList.size() > 0) {
            EmailInfo emailInfo = assembleEmailInfo(this.emailconfigPath, new HashMap() {

                {
                    put("currentDateTime", DateUtils.getDateString(new Date(), "yyyy年MM月dd日HH时mm分ss秒"))
                }
            })
            addSimpleAttachment(emailInfo, this.emailconfigPath, new HashMap() {

                {
                    put("currentDateTime", DateUtils.getDateString(new Date(), "yyyy年MM月dd日HH时mm分ss秒"))
                }
            }, reportModelList)
            messageInfoList.add(MessageInfo.createMessageInfo(emailInfo))
        }
    }
}

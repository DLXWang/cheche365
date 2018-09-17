package com.cheche365.cheche.scheduletask.task

import com.alibaba.fastjson.JSON
import com.cheche365.cheche.common.util.StringUtil
import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.model.ApiPartner
import com.cheche365.cheche.core.repository.ApiPartnerRepository
import com.cheche365.cheche.core.repository.AreaRepository
import com.cheche365.cheche.core.repository.InsuranceCompanyRepository
import com.cheche365.cheche.scheduletask.service.HttpClientUtil
import com.cheche365.cheche.scheduletask.service.task.AlarmAccount4BotpyService
import net.sf.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service



/**
 * 金斗云报价账号状态报警
 * Created by zhangtc on 2018/7/31.
 */
@Service
class AlarmAccount4BotpyTask extends BaseTask {
    Logger logger = LoggerFactory.getLogger(AlarmAccount4BotpyTask.class);


    private final String emailConfigPath = "/emailconfig/alarm_account_4_botpy.yml"

    private final String emailConfigPath1 = "/emailconfig/alarm_account_4_botpy_error.yml"


    @Autowired
    AlarmAccount4BotpyService account4BotpyService

    @Autowired
    AreaRepository areaRepository;

    @Autowired
    InsuranceCompanyRepository insuranceCompanyRepository;

    @Autowired
    ApiPartnerRepository apiPartnerRepository;



    @Override
    protected void doProcess() throws Exception {
        String[] spUrl = WebConstants.getDomainURL().split(":")
        String header = spUrl[0] + ":" + spUrl[1]
        String errorData;
        JSONObject jsonObject;

        String errorMessage = HttpClientUtil.doGetWithHeaderToSendEmail(null, header + "/v1.8/system/status/botpy/account", null, null);


        try {
            jsonObject = JSONObject.fromObject(errorMessage);
        } catch (Exception e) {
            sendErrorMessage("botpy接口出现错误 错误信息为:"+errorMessage,emailConfigPath1);
            return;
        }



        try {
            errorData = jsonObject.get("data");
        } catch (Exception e) {
            return;
        }
        if (StringUtil.isNull(errorData) || errorData == "[]") {
            return;
        }
        List<Map<String, String>> listw = new ArrayList<Map<String, String>>();
        List<Object> list = JSON.parseArray(errorData);
        for (Object object : list) {

            Map<String, String> ret = (Map<String, String>) object;

            listw.add(ret);
        }
        List<String> resultList = new ArrayList<>();
        HashSet hs = new HashSet();
        String result;
        for (Map<String, String> flag : listw) {
            String a = flag.get("city")
            hs.add(a)
            String b = flag.get("insuranceCompany")
            String c = flag.get("message")
            String area = areaRepository.findById(Long.parseLong(a)).getName()
            String inc = insuranceCompanyRepository.findById(Long.parseLong(b)).getName()
            ApiPartner e=apiPartnerRepository.findFirstByCode("jinshibx")
            if(flag.get("channel") != null && flag.get("channel")!=""){
                String d = e.getDescription()
                result = "系统检测到您的帐户变更或是密码变更，所属地区：" + area + "，所属保险公司：" + inc + "，所属渠道：" + d + "，详细信息：" + c + "，报价方式：【金斗云】为确保系统正常使用，请尽快确认账号和密码的正确性，避免产生不必要的损失。"
            }else {
                result = "系统检测到您的帐户变更或是密码变更，所属地区：" + area + "，所属保险公司：" + inc + "，详细信息：" + c + "，报价方式：【金斗云】为确保系统正常使用，请尽快确认账号和密码的正确性，避免产生不必要的损失。"
            }
            hs.add(a)
            resultList.add(result);


        }
        for (String flag : hs) {
            String res = "尊敬的用户：";
            for (String a : resultList) {
                if (a.contains(areaRepository.findById(Long.parseLong(flag)).getName())) res = res + "<br>" + a;
            }
            sendErrorMessage(res, emailConfigPath);
        }


    }
}

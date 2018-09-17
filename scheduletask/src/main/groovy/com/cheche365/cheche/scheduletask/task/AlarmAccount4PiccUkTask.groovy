package com.cheche365.cheche.scheduletask.task

import com.alibaba.fastjson.JSON
import com.cheche365.cheche.common.util.StringUtil
import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.repository.ApiPartnerRepository
import com.cheche365.cheche.core.repository.AreaRepository
import com.cheche365.cheche.core.repository.InsuranceCompanyRepository
import com.cheche365.cheche.scheduletask.service.HttpClientUtil
import net.sf.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * 人保小鳄鱼报价账号状态报警
 * Created by zhangpc on 2018/8/15.
 */
@Service
class AlarmAccount4PiccUkTask  extends BaseTask {
    Logger logger = LoggerFactory.getLogger(AlarmAccount4PiccUkTask.class);
    private final String emailConfigPath = "/emailconfig/alarm_account_4_piccuk.yml"

    private final String emailConfigPath1 = "/emailconfig/alarm_account_4_piccuk_error.yml"

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

        String errorMessage = HttpClientUtil.doGetWithHeaderToSendEmail(null, header + "/v1.8/system/status/piccuk/account", null, null);
        try {
            jsonObject = JSONObject.fromObject(errorMessage);
        } catch (Exception e) {
            sendErrorMessage("piccuk接口出现错误 错误信息为:"+errorMessage,emailConfigPath1);
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
            String c = flag.get("username")
            String d = flag.get("password")

            String area = areaRepository.findById(Long.parseLong(a)).getName()
            String inc = insuranceCompanyRepository.findById(Long.parseLong(b)).getName()
            if(flag.get("channel")!=null && flag.get("channel")!=""){
                String e =apiPartnerRepository.findFirstByCode(flag.get("channel")).getDescription()
                result = "系统检测到您的帐户变更或是密码变更，原账号：" + c + "，密码：" + d + "，所属地区：" + area + "，所属保险公司：" + inc + "，所属渠道：" + e + "，报价方式：【小鳄鱼】为确保系统正常使用，请尽快确认账号和密码的正确性，避免产生不必要的损失。"
            }else {
                result = "系统检测到您的帐户变更或是密码变更，原账号：" + c + "，密码：" + d + "，所属地区：" + area + "，所属保险公司：" + inc + "，报价方式：【小鳄鱼】为确保系统正常使用，请尽快确认账号和密码的正确性，避免产生不必要的损失。"
            }
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

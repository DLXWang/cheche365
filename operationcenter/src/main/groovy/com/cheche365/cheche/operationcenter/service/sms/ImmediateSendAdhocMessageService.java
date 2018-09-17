package com.cheche365.cheche.operationcenter.service.sms;

import com.cheche365.cheche.core.constants.SmsConstants;
import com.cheche365.cheche.core.model.AdhocMessage;
import com.cheche365.cheche.core.model.FilterUser;
import com.cheche365.cheche.core.model.MessageStatus;
import com.cheche365.cheche.core.repository.AdhocMessageRepository;
import com.cheche365.cheche.core.repository.Page;
import com.cheche365.cheche.core.repository.UserRepository;
import com.cheche365.cheche.core.service.sms.SmsInfo;
import com.cheche365.cheche.manage.common.constants.SMSMessageConstants;
import com.cheche365.cheche.manage.common.service.sms.FilterUserService;
import com.cheche365.cheche.manage.common.service.sms.SendMessageErrorHandler;
import com.cheche365.cheche.manage.common.service.sms.SendMessageService;
import com.cheche365.cheche.manage.common.util.SMSMessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 主动发送短信处理类
 * 异步通过一个线程发送短信
 * Created by sunhuazhong on 2015/10/16.
 */
@Component
@Transactional
public class ImmediateSendAdhocMessageService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String MESSAGE_PATTERN = "\\$\\{((\\w*)\\.?(\\w*))}";

    @Autowired
    private SendMessageErrorHandler sendMessageErrorHandler;

    @Autowired
    private SendMessageService sendMessageService;

    @Autowired
    private AdhocMessageRepository adhocMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FilterUserService filterUserService;

    public int sendSms(AdhocMessage adhocMessage) {
        int result;

        try {
            if (StringUtils.isNotEmpty(adhocMessage.getMobile())) {// 单一用户立即发送
                result = sendSingleSms(adhocMessage);
                adhocMessage.setTotalCount(1);
                adhocMessage.setSentCount(result == 0 ? 1 : 0);
            } else {        //群组立即发送
                result = sendGroupSms(adhocMessage);
            }
        } catch (Exception e) {
            result = -99;
            logger.info("主动发送短信异常,mobile->({}),smsTemplateId->({}) ", adhocMessage.getMobile(), adhocMessage.getSmsTemplate().getId(), e);
        }

        if (result != 0) {
            adhocMessage.setStatus(MessageStatus.Enum.SEND_FAIL);
            sendMessageErrorHandler.adhocHandler(result, adhocMessage);// 发送短信失败，需要向负责人发送邮件
        } else {
            adhocMessage.setStatus(MessageStatus.Enum.SEND_SUCCESS);
        }

        adhocMessageRepository.save(adhocMessage);
        return result;
    }

    public int sendSingleSms(AdhocMessage adhocMessage) {
        SmsInfo smsInfo = getSmsInfo(adhocMessage);
        int result = sendMessageService.sendMessage(smsInfo);
        return result;
    }

    public int sendGroupSms(AdhocMessage adhocMessage) {
        return 0;
    }

    private SmsInfo getSmsInfo(AdhocMessage adhocMessage) {
        SmsInfo smsInfo = new SmsInfo();

        Pattern pattern = Pattern.compile(MESSAGE_PATTERN);
        String message = adhocMessage.getSmsTemplate().getContent();
        String paramter[] = adhocMessage.getParameter().split(",");
        Matcher matcher = pattern.matcher(message);
        for (int i = 0; matcher.find(); i++) {
            String value = paramter[i];
            String variableName = matcher.group(0);
            message = message.replace(variableName, value);
        }

        smsInfo.setMobile(adhocMessage.getMobile());
        smsInfo.setContent(message);
        smsInfo.setSmsType(SmsInfo.Enum.SMS_TYPE_MESSAGE);
        smsInfo.setSmsChannel(SmsConstants._SMS_VENDOR_ZUCP);

        return smsInfo;
    }


    //////////////////====================================///////////////////////////////////////////////////////

    // 新建一个线程，用于发送短信
    public int sendMessage(AdhocMessage adhocMessage) {
        // 获取短信内容
        StringBuffer content = getMessageContent(adhocMessage);
        logger.debug("主动发送短信id:{} 的短信内容:{}", adhocMessage.getId(), content.toString());
        // 短信发送结果
        int result = 0;
        // 用户群发送短信失败手机集合
        List<String> sendErrorMobileList = new ArrayList<>();
        // 单一用户立即发送
        if (StringUtils.isNotEmpty(adhocMessage.getMobile())) {
//            result = sendMessageService.sendMessage(adhocMessage.getMobile(), content.toString());
            adhocMessage.setTotalCount(1);
            adhocMessage.setSentCount(result == 0 ? 1 : 0);
        }
        // 用户群立即发送
        else {
            result = getFilterUserResult(adhocMessage, content.toString(), sendErrorMobileList);
        }

        adhocMessage.setStatus(result == 0 ? MessageStatus.Enum.SEND_SUCCESS : MessageStatus.Enum.SEND_FAIL);
        logger.debug("主动发送短信id:{} 的发送结果:{}", adhocMessage.getId(), result);
        adhocMessageRepository.save(adhocMessage);

        // 发送失败的用户手机号及短信内容需要记录下来
//        if(!CollectionUtils.isEmpty(sendErrorMobileList)) {
//            stringRedisTemplate.opsForHash().put(
//                SMSMessageUtil.getFilterUserFailKey(),
//                SMSMessageUtil.getFilterUserFailContentHashKey(adhocMessage.getId()),
//                content);
//            stringRedisTemplate.opsForHash().put(
//                SMSMessageUtil.getFilterUserFailKey(),
//                SMSMessageUtil.getFilterUserFailMobileHashKey(adhocMessage.getId()),
//                CacheUtil.doJacksonSerialize(sendErrorMobileList));
//        }

        // 发送短信失败，需要想负责人发送邮件
        if (result != 0) {
            sendMessageErrorHandler.adhocHandler(result, adhocMessage);
        }
        return result;
    }


    private int getFilterUserResult(AdhocMessage adhocMessage, String content, List<String> sendErrorMobileList) {
        int result = 0, pageCount = 0;
        FilterUser filterUser = adhocMessage.getFilterUser();
        String sqlContent = filterUser.getSqlTemplate().getContent().replaceAll(SMSMessageConstants.MESSAGE_PATTERN, "?");
        String[] sqlParameters = StringUtils.isEmpty(filterUser.getParameter()) ? null : filterUser.getParameter().split("&");
        Integer allFilterUserMobileCount = filterUserService.getFilterUserCount(filterUser.getId());
        if (allFilterUserMobileCount == null || allFilterUserMobileCount == 0) {
            logger.debug("主动发送短信id：{} 的用户群：{} 没有数据，不用发送短信", adhocMessage.getId(), filterUser.getName());
            adhocMessage.setTotalCount(0);
            adhocMessage.setSentCount(0);
            return 0;
        }
        logger.debug("主动发送短信id：{} 的用户群：{} 用户数量：{}", adhocMessage.getId(), filterUser.getName(), allFilterUserMobileCount);
        Page<String> page = new Page(pageCount, SMSMessageUtil.getPerSendMobileCount());
        page.setTotalElements(allFilterUserMobileCount);
        Integer sentFilterUserMobileCount = new Integer(0);//已发送过滤用户的总数
        Page<String> mobilePage = userRepository.findUserMobileList(page, sqlContent, sqlParameters);
        while (mobilePage.getContent() != null) {
//            result = sendMessageService.sendBatchMessage(mobilePage.getContent(), content);
            logger.debug("主动发送短信id：{} 分批发送短信，每次{}条，这是第{}次，返回结果:{}",
                    adhocMessage.getId(), SMSMessageUtil.getPerSendMobileCount(), page.getNumber() + 1, result);
            if (result != 0) {
                break;
            }
            sentFilterUserMobileCount = sentFilterUserMobileCount + mobilePage.getContent().size();
            //if(result != 0) {
            //    Map<Integer, String> smsResultExplainMappings = (Map<Integer, String>)Constants.get_SMS_RESULT_EXPLAIN_MAPPINGS();
            //    logger.error("主动发送短信id：{} 分批发送短信失败，返回结果：{}，失败原因：{}",
            //        adhocMessage.getId(), result, smsResultExplainMappings.get(result));
            //    // 发送失败的用户手机号和短信内容需要记录下来
            //    sendErrorMobileList.addAll(mobilePage.getContent());
            //}
            if (sentFilterUserMobileCount == allFilterUserMobileCount) {
                break;
            }
            page.setNumber(++pageCount);
            mobilePage = userRepository.findUserMobileList(page, sqlContent, sqlParameters);
        }
        adhocMessage.setTotalCount(allFilterUserMobileCount);
        adhocMessage.setSentCount(sentFilterUserMobileCount);
        return result;
    }


    private StringBuffer getMessageContent(AdhocMessage adhocMessage) {
        String messageParameters = adhocMessage.getParameter();
        StringBuffer content = new StringBuffer();
        content.append(adhocMessage.getSmsTemplate().getZucpCode());
        if (StringUtils.isNotEmpty(messageParameters)) {
            for (String value : messageParameters.split(",")) {
                content.append("|");
                content.append(value);
            }
        }
        return content;
    }
}

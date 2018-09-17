package com.cheche365.cheche.fanhua.service

import com.cheche365.cheche.email.model.EmailInfo
import com.cheche365.cheche.email.service.IEmailService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

import java.text.SimpleDateFormat

/**
 * 保单同步任务
 * Created by zhangtc on 2018/2/5.
 */
@Service
class SyncTask {

    private Logger logger = LoggerFactory.getLogger(this.getClass())

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    @Autowired
    IEmailService emailService
    @Autowired
    Environment env
    @Autowired
    SyncService syncService

    @Scheduled(cron = "0 0 5 * * ? ")
    def saveInsurance() {
        List failedMessages = syncService.saveInsurance()
        buildAndSendEmail(failedMessages)
        logger.info("泛华保单入库完成")
    }

    private buildAndSendEmail(List failedMessages) {
        def report = getReport failedMessages
        def email = new EmailInfo(
            from: 'insurance-policy@cheche365.com',
            to: env.getProperty("mailreceivers")?.split(','),
            subject: sdf.format(new Date()) + "泛华保单同步${System.getProperty('spring.profiles.active')}环境失败记录",
            content: "失败记录总数${failedMessages.size()}</br>" + report
        )
        emailService.sender(email)
    }

    private getReport(failedMessages) {
        def newline = '</br>'
        new StringWriter().with { writer ->
            writer.println(newline)
            writer.println "| ID | 创建时间$newline"
            failedMessages?.collect() { unit ->
                writer.println "| ${unit.id} | ${unit.createTime}$newline${unit.content}$newline"
            }
            writer.println(newline)
            writer.println(newline)
            writer.println(newline)
            writer
        }.toString()
    }
}

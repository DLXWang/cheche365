INSERT INTO `schedule_condition` (`name`, `description`) VALUES ('CHANNEL_AGENT_REGISTER', '发送代理人注册成功短信');
INSERT INTO `sms_template` (`name`, `zucp_code`, `yxt_code`, `disable`, `content`, `comment`, `create_time`, `update_time`, `operator`) VALUES
('发送代理人注册成功短信', 'PENDING_263', '', '0', '尊敬的车保易用户您好，团队成员${agentName}已注册成功，手机号为${agentMobile}，身份级别为${agentLevel}。为防止出单无佣金，请尽快给成员配置点位，感谢您对车车科技的支持', NULL, NOW(), NULL, '40');
INSERT INTO `schedule_message` (`sms_template`, `schedule_condition`, `disable`, `comment`, `create_time`, `update_time`, `operator`) VALUES
((SELECT id FROM sms_template WHERE `name` = '发送代理人注册成功短信'),
 (SELECT id FROM schedule_condition WHERE `name` = 'CHANNEL_AGENT_REGISTER'),
 '0', NULL, NOW(), NULL, '40'
);

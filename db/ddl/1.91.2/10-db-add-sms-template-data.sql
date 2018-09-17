
INSERT INTO `sms_template` (`name`, `zucp_code`, `yxt_code`, `disable`, `content`, `comment`, `create_time`, `update_time`, `operator`) VALUES
('京东请求验证码', 'PENDING_255', '', '0', '{"smsTemplateId":"594","smsParam":"${VerifyCode}"}', NULL, NOW(), NULL, '40'),
('京东订单提交', 'PENDING_256', '', '0', '{"smsTemplateId":"721","smsParam":"${InsuranceCompany.Name}|${Amount}| ${MOrderDetail} "}', NULL, NOW(), NULL, '40'),
('京东支付成功', 'PENDING_257', '', '0', '{"smsTemplateId":"712","smsParam":"${InsuranceCompany.Name}"}', NULL, NOW(), NULL, '40'),
('京东订单取消', 'PENDING_258', '', '0', '{"smsTemplateId":"713","smsParam":"${InsuranceCompany.Name}"}', NULL, NOW(), NULL, '40'),
('京东提交订单30分钟未支付提醒', 'PENDING_259', '', '0', '{"smsTemplateId":"700","smsParam":" ${MOrderDetail} "}', NULL, NOW(), NULL, '40'),
('京东增补追加付款', 'PENDING_260', '', '0', '{"smsTemplateId":"701","smsParam":" ${MOrderDetail} "}', NULL, NOW(), NULL, '40'),
('京东订单图片上传短信提醒', 'PENDING_261', '', '0', '{"smsTemplateId":"709","smsParam":"${InsuranceCompany.Name}| ${MOrderDetail} "}', NULL, NOW(), NULL, '40'),
('京东模糊报价订单提交', 'PENDING_262', '', '0', '{"smsTemplateId":"709","smsParam":"${InsuranceCompany.Name}|${Amount}"}', NULL, NOW(), NULL, '40');


INSERT INTO `schedule_condition` (`name`, `description`) VALUES
('JD_REQUEST_VERIFY_CODE', '京东请求验证码'),
('JD_ORDER_COMMIT', '京东订单提交'),
('JD_PAYMENT_SUCCESS', '京东支付成功'),
('JD_ORDER_CANCEL', '京东订单取消'),
('JD_NO_PAYMENT_REMIND', '京东提交订单30分钟未支付提醒'),
('JD_AMEND_QUOTE_ORDER', '京东增补追加付款'),
('JD_RECOMMENDED_ORDER_IMAGE_UPLOAD', '京东订单图片上传短信提醒'),
('JD_ORDER_COMMIT_NOT_ALLOW_PAY', '京东模糊报价订单提交');


INSERT INTO `schedule_message` (`sms_template`, `schedule_condition`, `disable`, `comment`, `create_time`, `update_time`, `operator`) VALUES
((SELECT id FROM sms_template WHERE `name` = '京东请求验证码'),
 (SELECT id FROM schedule_condition WHERE `name` = 'JD_REQUEST_VERIFY_CODE'),
 '0', NULL, NOW(), NULL, '40'
),
((SELECT id FROM sms_template WHERE `name` = '京东订单提交'),
 (SELECT id FROM schedule_condition WHERE `name` = 'JD_ORDER_COMMIT'),
 '0', NULL, NOW(), NULL, '40'
),
((SELECT id FROM sms_template WHERE `name` = '京东支付成功'),
 (SELECT id FROM schedule_condition WHERE `name` = 'JD_PAYMENT_SUCCESS'),
 '0', NULL, NOW(), NULL, '40'
),
((SELECT id FROM sms_template WHERE `name` = '京东订单取消'),
 (SELECT id FROM schedule_condition WHERE `name` = 'JD_ORDER_CANCEL'),
 '0', NULL, NOW(), NULL, '40'
),
((SELECT id FROM sms_template WHERE `name` = '京东提交订单30分钟未支付提醒'),
 (SELECT id FROM schedule_condition WHERE `name` = 'JD_NO_PAYMENT_REMIND'),
 '0', NULL, NOW(), NULL, '40'
),
((SELECT id FROM sms_template WHERE `name` = '京东增补追加付款'),
 (SELECT id FROM schedule_condition WHERE `name` = 'JD_AMEND_QUOTE_ORDER'),
 '0', NULL, NOW(), NULL, '40'
),
((SELECT id FROM sms_template WHERE `name` = '京东订单图片上传短信提醒'),
 (SELECT id FROM schedule_condition WHERE `name` = 'JD_RECOMMENDED_ORDER_IMAGE_UPLOAD'),
 '0', NULL, NOW(), NULL, '40'
),
((SELECT id FROM sms_template WHERE `name` = '京东模糊报价订单提交'),
 (SELECT id FROM schedule_condition WHERE `name` = 'JD_ORDER_COMMIT_NOT_ALLOW_PAY'),
 '0', NULL, NOW(), NULL, '40'
);

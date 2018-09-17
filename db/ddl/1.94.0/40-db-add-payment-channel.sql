INSERT INTO `payment_channel` (`id`, `name`, `customer_pay`, `description`, `parent_id`, `logo_url`) VALUES (61, 'agent_parser', 1, '小鳄鱼', NULL, NULL);
INSERT INTO `payment_channel` (`id`, `name`, `customer_pay`, `description`, `parent_id`, `logo_url`) VALUES (62, 'alipay', 1, '支付宝', 61, 'paymentChannel/alipay.png');
INSERT INTO `payment_channel` (`id`, `name`, `customer_pay`, `description`, `parent_id`, `logo_url`) VALUES (63, 'wechats', 2, '微信扫码', 61, 'paymentChannel/wechat.png');

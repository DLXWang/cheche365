ALTER TABLE `gift_code_exchange_way` ADD COLUMN `rule_param` VARCHAR(200) DEFAULT NULL ;

INSERT INTO `gift_code_exchange_way` (`id`,`name`,`exchange_class`,`common_exchange`,`amount`,`operator`,`create_time`,`update_time`,`description`,`effective_date`,`expire_date`,`amount_param`,`full_limit_param`,`rule_param`)
VALUES (31,'车险代金券','com.cheche365.cheche.core.service.giftcode.CommonGiftCodeExchange',1,300.00,NULL,now(),NULL,' 每个优惠码兑换一张300元优惠券','2018-07-15','2019-12-31','300','300_1000','commercial');

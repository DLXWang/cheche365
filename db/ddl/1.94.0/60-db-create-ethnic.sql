create TABLE `ethnic`(
			`id` BIGINT(20) not null AUTO_INCREMENT,
			`name` VARCHAR(20) not null,
			`disable` TINYINT(1)  DEFAULT '0',
			PRIMARY KEY(id)

)ENGINE=INNODB DEFAULT CHARSET=utf8;

INSERT into ethnic(id,name) VALUES(1,'汉族');
INSERT into ethnic(id,name) VALUES(2,'蒙古族');
INSERT into ethnic(id,name) VALUES(3,'回族');
INSERT into ethnic(id,name) VALUES(4,'藏族');
INSERT into ethnic(id,name) VALUES(5,'维吾尔族');
INSERT into ethnic(id,name) VALUES(6,'苗族');
INSERT into ethnic(id,name) VALUES(7,'彝族');
INSERT into ethnic(id,name) VALUES(8,'壮族');
INSERT into ethnic(id,name) VALUES(9,'布依族');
INSERT into ethnic(id,name) VALUES(10,'朝鲜族');
INSERT into ethnic(id,name) VALUES(11,'满族');
INSERT into ethnic(id,name) VALUES(12,'侗族');
INSERT into ethnic(id,name) VALUES(13,'瑶族');
INSERT into ethnic(id,name) VALUES(14,'白族');
INSERT into ethnic(id,name) VALUES(15,'土家族');
INSERT into ethnic(id,name) VALUES(16,'哈尼族');
INSERT into ethnic(id,name) VALUES(17,'哈萨克族');
INSERT into ethnic(id,name) VALUES(18,'傣族');
INSERT into ethnic(id,name) VALUES(19,'黎族');
INSERT into ethnic(id,name) VALUES(20,'傈僳族');
INSERT into ethnic(id,name) VALUES(21,'佤族');
INSERT into ethnic(id,name) VALUES(22,'畲族');
INSERT into ethnic(id,name) VALUES(23,'高山族');
INSERT into ethnic(id,name) VALUES(24,'拉祜族');
INSERT into ethnic(id,name) VALUES(25,'水族');
INSERT into ethnic(id,name) VALUES(26,'东乡族');
INSERT into ethnic(id,name) VALUES(27,'纳西族');
INSERT into ethnic(id,name) VALUES(28,'景颇族');
INSERT into ethnic(id,name) VALUES(29,'柯尔克孜族');
INSERT into ethnic(id,name) VALUES(30,'土族');
INSERT into ethnic(id,name) VALUES(31,'达斡尔族');
INSERT into ethnic(id,name) VALUES(32,'仫佬族');
INSERT into ethnic(id,name) VALUES(33,'羌族');
INSERT into ethnic(id,name) VALUES(34,'布朗族');
INSERT into ethnic(id,name) VALUES(35,'撒拉族');
INSERT into ethnic(id,name) VALUES(36,'毛难族');
INSERT into ethnic(id,name) VALUES(37,'仡佬族');
INSERT into ethnic(id,name) VALUES(38,'锡伯族');
INSERT into ethnic(id,name) VALUES(39,'阿昌族');
INSERT into ethnic(id,name) VALUES(40,'普米族');
INSERT into ethnic(id,name) VALUES(41,'塔吉克族');
INSERT into ethnic(id,name) VALUES(42,'怒族');
INSERT into ethnic(id,name) VALUES(43,'乌孜别克族');
INSERT into ethnic(id,name) VALUES(44,'俄罗斯族');
INSERT into ethnic(id,name) VALUES(45,'鄂温克族');
INSERT into ethnic(id,name) VALUES(46,'崩龙族');
INSERT into ethnic(id,name) VALUES(47,'保安族');
INSERT into ethnic(id,name) VALUES(48,'裕固族');
INSERT into ethnic(id,name) VALUES(49,'京族');
INSERT into ethnic(id,name) VALUES(50,'塔塔尔族');
INSERT into ethnic(id,name) VALUES(51,'独龙族');
INSERT into ethnic(id,name) VALUES(52,'鄂伦春族');
INSERT into ethnic(id,name) VALUES(53,'赫哲族');
INSERT into ethnic(id,name) VALUES(54,'门巴族');
INSERT into ethnic(id,name) VALUES(55,'珞巴族');
INSERT into ethnic(id,name) VALUES(56,'基诺族');
INSERT into ethnic(id,name) VALUES(97,'其他');
INSERT into ethnic(id,name) VALUES(98,'外国血统');

create table `approve_status`(
		`id` BIGINT(20) not null AUTO_INCREMENT,
		`description` VARCHAR(20),
		`name` VARCHAR(20),
		PRIMARY KEY(id)
)ENGINE=INNODB DEFAULT CHARSET=utf8;

INSERT into approve_status(id,description,name) VALUES(1,'未认证','未认证');
INSERT into approve_status(id,description,name) VALUES(2,'待认证','待认证');
INSERT into approve_status(id,description,name) VALUES(3,'认证中','认证中');
INSERT into approve_status(id,description,name) VALUES(4,'认证成功','认证成功');
INSERT into approve_status(id,description,name) VALUES(5,'认证失败','认证失败');

create TABLE `profession_approve`(
			`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
			`channel_agent`  BIGINT(20) NOT NULL ,
			`approve_status` BIGINT(20) NOT NUll,
			`create_time` datetime DEFAULT NULL,
			`update_time` datetime DEFAULT NULL,
			PRIMARY KEY(id),
			UNIQUE KEY `PROFESSION_APPROVE_CHANNEL_AGENT` (`channel_agent`),
			KEY `PROFESSION_APPROVE_STATUS` (`approve_status`),
			CONSTRAINT `FK_PROFESSION_APPROVE_CHANNEL_AGENT` FOREIGN KEY (`channel_agent`) REFERENCES `channel_agent` (`id`),
			CONSTRAINT `FK_PROFESSION_APPROVE_STATUS` FOREIGN KEY (`approve_status`) REFERENCES `approve_status` (`id`)
)ENGINE=INNODB DEFAULT CHARSET = utf8;

ALTER TABLE `channel_agent`
ADD COLUMN `ethnic`  bigint(20) NULL AFTER `shop_type`;

ALTER TABLE `channel_agent` ADD CONSTRAINT `FK_CHANNEL_AGENT_ETHNIC` FOREIGN KEY (`ethnic`) REFERENCES `ethnic` (`id`);

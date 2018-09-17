CREATE TABLE `purchase_order_auditing` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `purchase_order_image` bigint(20) NOT NULL,
  `create_time` datetime DEFAULT NULL ,
  `hint` varchar(255) DEFAULT NULL,
  `status` smallint(2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_PURCHASE_ORDER_AUDITING_REF_PURCHASE_ORDER_IMAGE` (`purchase_order_image`),
  CONSTRAINT `FK_PURCHASE_ORDER_AUDITING_REF_PURCHASE_ORDER_IMAGE` FOREIGN KEY (`purchase_order_image`) REFERENCES `purchase_order_image` (`id`)
);

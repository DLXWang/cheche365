CREATE TABLE `purchase_order_attribute` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`purchase_order` BIGINT NOT NULL DEFAULT '0',
	`type` BIGINT NOT NULL DEFAULT '0',
	`create_time` DATETIME  NULL ,
	`update_time` DATETIME  NULL,
	`value` VARCHAR(500) NULL,
	PRIMARY KEY (`id`)
)
COLLATE='utf8_bin'
ENGINE=InnoDB;


ALTER TABLE `purchase_order_attribute`
	ADD CONSTRAINT `FK1_PURCHASE_ORDER_ATTRIBUTE` FOREIGN KEY (`purchase_order`) REFERENCES `purchase_order` (`id`),
	ADD CONSTRAINT `FK2_ATTRIBUTE_TYPE` FOREIGN KEY (`type`) REFERENCES `attribute_type` (`id`);
ALTER TABLE `purchase_order_attribute`
	ADD UNIQUE INDEX `FK1_PURCHASE_ORDER_ATTRIBUTE_TYPE` (`purchase_order`, `type`);

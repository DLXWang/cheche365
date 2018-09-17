ALTER TABLE `purchase_order_attribute`
CHANGE COLUMN `value` `value` VARCHAR(500) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL AFTER `type`;

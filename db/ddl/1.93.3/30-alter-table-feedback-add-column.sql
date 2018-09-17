ALTER TABLE `feedback`
CHANGE COLUMN `channel_id` `channel`  bigint(20) NULL DEFAULT NULL AFTER `user_id`;

ALTER TABLE `feedback` ADD CONSTRAINT `FK_FEEDBACK_REF_CHANNEL` FOREIGN KEY (`channel`) REFERENCES `channel` (`id`);


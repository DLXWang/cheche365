ALTER TABLE `channel_agent`
ADD COLUMN `invite_qr_code` VARCHAR(100) NULL AFTER `invite_code`;

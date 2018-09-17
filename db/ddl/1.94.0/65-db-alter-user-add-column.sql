ALTER TABLE `user`
ADD COLUMN `use_default_email`  TINYINT(1) DEFAULT FALSE AFTER `email`;

ALTER TABLE `quote_history`
ADD COLUMN `compulsory_start_date` DATE DEFAULT NULL AFTER `insurance_package`,
ADD COLUMN `commercial_start_date` DATE DEFAULT NULL AFTER `compulsory_start_date`;

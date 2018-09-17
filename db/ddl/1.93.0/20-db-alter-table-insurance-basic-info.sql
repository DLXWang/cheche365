ALTER TABLE `insurance_basic_info`
ADD COLUMN `applicant_name` varchar(45) DEFAULT NULL,
ADD COLUMN `applicant_id_no` varchar(45) DEFAULT NULL,
ADD COLUMN `applicant_identity_type` bigint(20) DEFAULT '1',
ADD COLUMN `applicant_mobile` varchar(45) DEFAULT NULL,
ADD COLUMN `insured_name` varchar(45) DEFAULT NULL,
ADD COLUMN `insured_id_no` varchar(45) DEFAULT NULL,
ADD COLUMN `insured_identity_type` bigint(20) DEFAULT '1',
ADD COLUMN `insured_mobile` varchar(45) DEFAULT NULL,
ADD KEY `FK_INSURANCE_BASIC_INFO_REF_APPLICANT_IDENTITY_TYPE` (`applicant_identity_type`) USING BTREE,
ADD KEY `FK_INSURANCE_BASIC_INFO_REF_INSURED_IDENTITY_TYPE` (`insured_identity_type`) USING BTREE,
ADD CONSTRAINT `insurance_basic_info_ibfk_2` FOREIGN KEY (`applicant_identity_type`) REFERENCES `identity_type` (`id`),
ADD CONSTRAINT `insurance_basic_info_ibfk_3` FOREIGN KEY (`insured_identity_type`) REFERENCES `identity_type` (`id`);

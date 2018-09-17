ALTER TABLE `insurance_basic_info`
ADD COLUMN `insurance_company` bigint(20) DEFAULT NULL,
ADD KEY `FK_IBI_REF_INSURANCE_COMPANY` (`insurance_company`) USING BTREE,
ADD CONSTRAINT `insurance_basic_info_ibfk_4`
FOREIGN KEY (insurance_company) REFERENCES insurance_company(id);

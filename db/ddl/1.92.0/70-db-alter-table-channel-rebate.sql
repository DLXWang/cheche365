alter table channel_rebate add column `only_commercial_rebate` decimal(18,2) comment '单商业险费率';
alter table channel_rebate add column `only_compulsory_rebate` decimal(18,2) comment '单交强险费率';
alter table channel_rebate add column `only_ready_commercial_rebate` decimal(18,2) comment '预生效单商业险';
alter table channel_rebate add column `only_ready_compulsory_rebate` decimal(18,2) comment '预生效单交强险';

update channel_rebate set
  only_commercial_rebate = commercial_rebate,
  only_compulsory_rebate = compulsory_rebate,
  only_ready_commercial_rebate = ready_commercial_rebate,
  only_ready_compulsory_rebate = ready_compulsory_rebate;
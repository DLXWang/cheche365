alter table  internal_user add column `lock` tinyint(1) DEFAULT '0';
alter table  internal_user add column  `change_password_time` datetime;
update internal_user set change_password_time = now();

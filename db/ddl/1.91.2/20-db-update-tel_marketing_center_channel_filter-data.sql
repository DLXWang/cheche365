UPDATE tel_marketing_center_channel_filter SET exclude_channels = CONCAT(exclude_channels, ',', (SELECT id FROM channel WHERE `name` = 'PARTNER_JD')) WHERE task_type = 13;

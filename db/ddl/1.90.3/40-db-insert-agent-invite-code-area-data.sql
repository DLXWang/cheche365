-- 18311339522 18510250561  18500384137 绑定地区改为全国
insert into agent_invite_code_area(area,cheche_agent_invite_code)  (select null , (select id from cheche_agent_invite_code where invite_code = '32455681'));
insert into agent_invite_code_area(area,cheche_agent_invite_code)  (select null , (select id from cheche_agent_invite_code where invite_code = '73147453'));
insert into agent_invite_code_area(area,cheche_agent_invite_code)  (select null , (select id from cheche_agent_invite_code where invite_code = '89780140'));

-- 18311339522   绑定地区改为全国
update agent_invite_code_area aica,cheche_agent_invite_code caic,channel_agent ca ,user u
set aica.area = null
where ca.user = u.id and caic.channel_agent = ca.id and caic.id = aica.cheche_agent_invite_code and u.mobile = '18311339522' and ca.channel = 67;

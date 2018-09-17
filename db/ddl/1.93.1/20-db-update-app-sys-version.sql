update sys_version set update_advice = 'required' where version='1.4.0' and channel=222;
INSERT INTO `sys_version` (`channel`, `version`, `update_advice`, `reason`) VALUES ('221', '2.0.0', 'required', '有新版本上线啦！为了您投保更快捷、安全，请去更新最新版本。');


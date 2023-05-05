CREATE TABLE `sys_login_info` (
`info_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '访问ID',
`login_name` varchar(50) DEFAULT '' COMMENT '登录账号',
`ipaddr` varchar(128) DEFAULT '' COMMENT '登录IP地址',
`login_location` varchar(255) DEFAULT '' COMMENT '登录地点',
`browser` varchar(50) DEFAULT '' COMMENT '浏览器类型',
`os` varchar(50) DEFAULT '' COMMENT '操作系统',
`status` char(1) DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
`msg` varchar(255) DEFAULT '' COMMENT '提示消息',
`login_time` datetime DEFAULT NULL COMMENT '访问时间',
PRIMARY KEY (`info_id`),
KEY `idx_sys_logininfor_s` (`status`),
KEY `idx_sys_logininfor_lt` (`login_time`)
) ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=utf8mb4 COMMENT='系统访问记录';


CREATE TABLE `sys_oper_log` (
`oper_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
`title` varchar(50) DEFAULT '' COMMENT '模块标题',
`business_type` int(2) DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
`method` varchar(100) DEFAULT '' COMMENT '方法名称',
`request_method` varchar(10) DEFAULT '' COMMENT '请求方式',
`operator_type` int(1) DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
`oper_name` varchar(50) DEFAULT '' COMMENT '操作人员',
`oper_url` varchar(255) DEFAULT '' COMMENT '请求URL',
`oper_ip` varchar(128) DEFAULT '' COMMENT '主机地址',
`oper_location` varchar(255) DEFAULT '' COMMENT '操作地点',
`oper_param` varchar(2000) DEFAULT '' COMMENT '请求参数',
`json_result` varchar(2000) DEFAULT '' COMMENT '返回参数',
`status` int(1) DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
`error_msg` varchar(2000) DEFAULT '' COMMENT '错误消息',
`oper_time` datetime DEFAULT NULL COMMENT '操作时间',
`cost_time` bigint(20) DEFAULT '0' COMMENT '消耗时间',
PRIMARY KEY (`oper_id`),
KEY `idx_sys_oper_log_bt` (`business_type`),
KEY `idx_sys_oper_log_s` (`status`),
KEY `idx_sys_oper_log_ot` (`oper_time`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='操作日志记录';
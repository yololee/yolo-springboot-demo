CREATE TABLE `orm_role` (
`role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
`role_name` varchar(30) NOT NULL COMMENT '角色名称',
`role_sort` int(4) NOT NULL COMMENT '显示顺序',
`status` char(1) NOT NULL COMMENT '角色状态（0正常 1停用）',
`remark` varchar(500) DEFAULT NULL COMMENT '备注',
PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';

INSERT INTO `test`.`orm_role` (`role_id`, `role_name`, `role_sort`, `status`, `remark`) VALUES (6, '管理员', 1, '0', 'test');
INSERT INTO `test`.`orm_role` (`role_id`, `role_name`, `role_sort`, `status`, `remark`) VALUES (9, '运维', 2, '0', 'test2222');
INSERT INTO `test`.`orm_role` (`role_id`, `role_name`, `role_sort`, `status`, `remark`) VALUES (10, '运维管理员', 2, '0', 'test1111');
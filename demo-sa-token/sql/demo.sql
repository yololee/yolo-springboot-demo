
create database test;

CREATE TABLE `user` (
                        `id` varchar(40) COLLATE utf8mb4_german2_ci NOT NULL,
                        `username` varchar(30) CHARACTER SET utf8 NOT NULL COMMENT '用户名',
                        `cnname` varchar(30) CHARACTER SET utf8 NOT NULL COMMENT '中文名',
                        `mobile` varchar(20) CHARACTER SET utf8 DEFAULT NULL COMMENT '手机号码',
                        `password` varchar(100) CHARACTER SET utf8 NOT NULL COMMENT '密码',
                        `email` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '邮箱',
                        `head_url` varchar(1000) CHARACTER SET utf8 DEFAULT NULL COMMENT '头像地址',
                        `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                        `last_login_time` datetime DEFAULT NULL COMMENT '上次登录时间',
                        `customer_id` bigint(11) DEFAULT NULL COMMENT '客户id',
                        `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_german2_ci;

INSERT INTO `test`.`user` (`id`, `username`, `cnname`, `mobile`, `password`, `email`, `head_url`, `create_time`, `last_login_time`, `customer_id`, `update_time`) VALUES ('1664079392906809344', 'admin', '管理员', '17612340004', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', NULL, NULL, '2023-06-01 09:20:19', NULL, NULL, NULL);


CREATE TABLE `role` (
                        `id` varchar(40) CHARACTER SET utf8 NOT NULL,
                        `name` varchar(30) CHARACTER SET utf8 NOT NULL COMMENT '角色名称',
                        `display_name` varchar(30) CHARACTER SET utf8 NOT NULL COMMENT '别名',
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_german2_ci;


INSERT INTO `test`.`role` (`id`, `name`, `display_name`) VALUES ('156228362616832', 'admin', '管理员');
INSERT INTO `test`.`role` (`id`, `name`, `display_name`) VALUES ('156228363202560', 'normal', '普通用户');


CREATE TABLE `user_role` (
                             `user_id` varchar(40) CHARACTER SET utf8 NOT NULL,
                             `role_id` varchar(40) CHARACTER SET utf8 NOT NULL,
                             PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_german2_ci;

INSERT INTO `test`.`user_role` (`user_id`, `role_id`) VALUES ('1664079392906809344', '156228362616832');
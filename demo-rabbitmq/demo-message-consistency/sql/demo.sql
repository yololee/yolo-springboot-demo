CREATE TABLE `msg_log` (
                           `id` varchar(255) NOT NULL DEFAULT '' COMMENT '消息唯一标识',
                           `msg` text COMMENT '消息体, json格式化',
                           `exchange` varchar(255) NOT NULL DEFAULT '' COMMENT '交换机',
                           `routing_key` varchar(255) NOT NULL DEFAULT '' COMMENT '路由键',
                           `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态: 0投递中 1投递成功 2投递失败 3已消费',
                           `try_count` int(11) NOT NULL DEFAULT '0' COMMENT '重试次数',
                           `next_try_time` bigint(20) DEFAULT NULL COMMENT '下一次重试时间',
                           `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
                           `update_time` bigint(20) DEFAULT NULL COMMENT '更新时间',
                           PRIMARY KEY (`id`) USING BTREE,
                           UNIQUE KEY `unq_msg_id` (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='消息投递日志';
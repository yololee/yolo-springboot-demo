CREATE TABLE `company` (
                           `id` bigint(20) NOT NULL COMMENT 'id',
                           `name` varchar(255) DEFAULT NULL COMMENT '名称',
                           `contact` varchar(50) DEFAULT NULL COMMENT '联系人',
                           `contact_type` varchar(50) DEFAULT NULL COMMENT '联系方式',
                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                           `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                           `removed` int(2) DEFAULT NULL COMMENT '是否删除(0:存在，-1:删除)',
                           `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公司单位';


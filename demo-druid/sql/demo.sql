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


INSERT INTO `test`.`company` (`id`, `name`, `contact`, `contact_type`, `create_time`, `update_time`, `removed`, `delete_time`) VALUES (1656854581440679937, '阿里', '张三222', '17683723698', '2023-05-12 10:51:30', '2023-05-12 11:03:16', 0, NULL);
INSERT INTO `test`.`company` (`id`, `name`, `contact`, `contact_type`, `create_time`, `update_time`, `removed`, `delete_time`) VALUES (1656856229496020993, '阿里', '张三', '17683720001', '2023-05-12 10:58:03', NULL, -1, NULL);
INSERT INTO `test`.`company` (`id`, `name`, `contact`, `contact_type`, `create_time`, `update_time`, `removed`, `delete_time`) VALUES (1656899435763879938, '华为', '李四111', '17683720005', NULL, '2023-05-12 13:50:55', 0, NULL);
INSERT INTO `test`.`company` (`id`, `name`, `contact`, `contact_type`, `create_time`, `update_time`, `removed`, `delete_time`) VALUES (1656900129375956993, '腾讯', '李四1113423', '17683720005', '2023-05-12 13:52:29', '2023-05-12 13:53:16', 0, NULL);
INSERT INTO `test`.`company` (`id`, `name`, `contact`, `contact_type`, `create_time`, `update_time`, `removed`, `delete_time`) VALUES (1659369371304284161, '腾讯1111', '李四', '17683720003', '2023-05-19 09:24:22', NULL, 0, NULL);
INSERT INTO `test`.`company` (`id`, `name`, `contact`, `contact_type`, `create_time`, `update_time`, `removed`, `delete_time`) VALUES (1659370240775098370, '腾讯1111', '李四', '17683720003', '2023-05-19 09:27:50', NULL, 0, NULL);
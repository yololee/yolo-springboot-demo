/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 50710 (5.7.10)
 Source Host           : localhost:3306
 Source Schema         : test

 Target Server Type    : MySQL
 Target Server Version : 50710 (5.7.10)
 File Encoding         : 65001

 Date: 03/07/2023 15:14:39
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_permission`;
CREATE TABLE `t_sys_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `url` varchar(50) DEFAULT NULL COMMENT 'url',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='系统管理-权限资源表 ';

-- ----------------------------
-- Records of t_sys_permission
-- ----------------------------
BEGIN;
INSERT INTO `t_sys_permission` (`id`, `url`, `create_time`, `update_time`) VALUES (1, '/admin', '2019-03-28 18:51:08', '2019-03-28 18:51:10');
INSERT INTO `t_sys_permission` (`id`, `url`, `create_time`, `update_time`) VALUES (2, '/test', '2019-03-28 18:52:13', '2019-08-31 21:26:57');
INSERT INTO `t_sys_permission` (`id`, `url`, `create_time`, `update_time`) VALUES (3, '/', '2019-03-28 18:52:13', '2019-03-28 18:52:13');
COMMIT;

-- ----------------------------
-- Table structure for t_sys_role
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_role`;
CREATE TABLE `t_sys_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code` varchar(50) DEFAULT NULL COMMENT '角色编码',
  `name` varchar(50) DEFAULT NULL COMMENT '角色名称',
  `remarks` varchar(100) DEFAULT NULL COMMENT '角色描述',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='系统管理-角色表 ';

-- ----------------------------
-- Records of t_sys_role
-- ----------------------------
BEGIN;
INSERT INTO `t_sys_role` (`id`, `code`, `name`, `remarks`, `create_time`, `update_time`) VALUES (1, 'admin', '系统管理员', '系统管理员', '2019-03-28 15:51:56', '2019-03-28 15:51:59');
INSERT INTO `t_sys_role` (`id`, `code`, `name`, `remarks`, `create_time`, `update_time`) VALUES (2, 'visitor', '访客', '访客', '2019-03-28 20:17:04', '2019-09-09 16:32:15');
COMMIT;

-- ----------------------------
-- Table structure for t_sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_role_permission`;
CREATE TABLE `t_sys_role_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id` int(11) DEFAULT NULL COMMENT '角色ID',
  `permission_id` int(11) DEFAULT NULL COMMENT '权限ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='系统管理 - 角色-权限资源关联表 ';

-- ----------------------------
-- Records of t_sys_role_permission
-- ----------------------------
BEGIN;
INSERT INTO `t_sys_role_permission` (`id`, `role_id`, `permission_id`, `create_time`, `update_time`) VALUES (1, 1, 1, '2019-09-18 21:06:26', '2019-09-18 21:06:26');
INSERT INTO `t_sys_role_permission` (`id`, `role_id`, `permission_id`, `create_time`, `update_time`) VALUES (2, 1, 2, '2019-09-18 21:06:27', '2019-09-18 21:06:27');
INSERT INTO `t_sys_role_permission` (`id`, `role_id`, `permission_id`, `create_time`, `update_time`) VALUES (3, 1, 3, '2019-09-18 21:06:27', '2019-09-18 21:06:27');
INSERT INTO `t_sys_role_permission` (`id`, `role_id`, `permission_id`, `create_time`, `update_time`) VALUES (4, 2, 1, '2019-09-18 21:26:43', '2019-09-18 21:26:43');
INSERT INTO `t_sys_role_permission` (`id`, `role_id`, `permission_id`, `create_time`, `update_time`) VALUES (5, 3, 1, '2019-09-18 21:26:43', '2019-09-18 21:26:43');
COMMIT;

-- ----------------------------
-- Table structure for t_sys_user
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_user`;
CREATE TABLE `t_sys_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(100) DEFAULT NULL COMMENT '账号',
  `password` varchar(100) DEFAULT NULL COMMENT '登录密码',
  `nick_name` varchar(50) DEFAULT NULL COMMENT '昵称',
  `salt` varchar(50) DEFAULT NULL COMMENT '盐值',
  `token` varchar(100) DEFAULT NULL COMMENT 'token',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='系统管理-用户基础信息表';

-- ----------------------------
-- Records of t_sys_user
-- ----------------------------
BEGIN;
INSERT INTO `t_sys_user` (`id`, `username`, `password`, `nick_name`, `salt`, `token`, `create_time`, `update_time`) VALUES (1, 'admin', 'd376637c4e39d5e3314bc4433ec420afbd60ba85', '张三', 'yolo', '3e81e64fd2c1549e526d41665818e45c60dda4f0', '2019-05-05 16:09:06', '2023-07-03 14:57:31');
INSERT INTO `t_sys_user` (`id`, `username`, `password`, `nick_name`, `salt`, `token`, `create_time`, `update_time`) VALUES (2, 'test', 'd376637c4e39d5e3314bc4433ec420afbd60ba85', '测试号', 'yolo', 'a4bf084f250aebc8f0bc806bdf9bca205c7706c9', '2019-05-05 16:15:06', '2019-09-19 01:47:19');
COMMIT;

-- ----------------------------
-- Table structure for t_sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_user_role`;
CREATE TABLE `t_sys_user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id` int(11) DEFAULT NULL COMMENT '角色ID',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='系统管理 - 用户角色关联表 ';

-- ----------------------------
-- Records of t_sys_user_role
-- ----------------------------
BEGIN;
INSERT INTO `t_sys_user_role` (`id`, `role_id`, `user_id`, `create_time`, `update_time`) VALUES (1, 1, 1, '2019-08-21 10:49:41', '2019-08-21 10:49:41');
INSERT INTO `t_sys_user_role` (`id`, `role_id`, `user_id`, `create_time`, `update_time`) VALUES (2, 1, 2, '2019-09-18 21:26:32', '2019-09-18 21:26:32');
INSERT INTO `t_sys_user_role` (`id`, `role_id`, `user_id`, `create_time`, `update_time`) VALUES (3, 2, 2, '2019-09-18 21:26:32', '2019-09-18 21:26:32');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;

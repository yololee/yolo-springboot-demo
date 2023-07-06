/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 50710 (5.7.10)
 Source Host           : localhost:3306
 Source Schema         : test2

 Target Server Type    : MySQL
 Target Server Version : 50710 (5.7.10)
 File Encoding         : 65001

 Date: 06/07/2023 17:37:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `pid` int(4) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `permission_name` varchar(20) DEFAULT NULL COMMENT '资源名称',
  `str` varchar(20) DEFAULT NULL COMMENT '资源标识符',
  PRIMARY KEY (`pid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of permission
-- ----------------------------
BEGIN;
INSERT INTO `permission` (`pid`, `permission_name`, `str`) VALUES (1, '用户删除', 'sys:user:delete');
COMMIT;

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `rid` int(4) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `role_name` varchar(20) DEFAULT NULL COMMENT '角色名称',
  PRIMARY KEY (`rid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of role
-- ----------------------------
BEGIN;
INSERT INTO `role` (`rid`, `role_name`) VALUES (1, 'ROLE_admin');
INSERT INTO `role` (`rid`, `role_name`) VALUES (2, 'ROLE_root');
INSERT INTO `role` (`rid`, `role_name`) VALUES (3, 'ROLE_test');
COMMIT;

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `rid` int(4) DEFAULT NULL COMMENT '角色id',
  `pid` int(4) DEFAULT NULL COMMENT '权限id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `fk_rid2` (`rid`) USING BTREE,
  KEY `fk_pid2` (`pid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of role_permission
-- ----------------------------
BEGIN;
INSERT INTO `role_permission` (`id`, `rid`, `pid`) VALUES (1, 1, 1);
COMMIT;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `uid` int(4) NOT NULL AUTO_INCREMENT COMMENT '账户id',
  `user_name` varchar(20) DEFAULT NULL COMMENT '姓名',
  `password` varchar(200) DEFAULT NULL COMMENT '密码',
  `lock` int(1) DEFAULT '0' COMMENT '是否可用 1可用 0不可用',
  `salt` varchar(255) DEFAULT NULL COMMENT '盐',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` (`uid`, `user_name`, `password`, `lock`, `salt`) VALUES (1, 'admin', 'd376637c4e39d5e3314bc4433ec420afbd60ba85', 0, 'yolo');
INSERT INTO `user` (`uid`, `user_name`, `password`, `lock`, `salt`) VALUES (2, 'test', 'd376637c4e39d5e3314bc4433ec420afbd60ba85', 0, 'yolo');
COMMIT;

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uid` int(4) DEFAULT NULL COMMENT '用户id',
  `rid` int(4) DEFAULT NULL COMMENT '角色id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `fk_uid` (`uid`) USING BTREE,
  KEY `fk_rid` (`rid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of user_role
-- ----------------------------
BEGIN;
INSERT INTO `user_role` (`id`, `uid`, `rid`) VALUES (1, 1, 1);
INSERT INTO `user_role` (`id`, `uid`, `rid`) VALUES (2, 1, 2);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;

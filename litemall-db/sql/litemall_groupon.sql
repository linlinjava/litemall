/*
 Navicat MySQL Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 80011
 Source Host           : localhost:3306
 Source Schema         : litemall2

 Target Server Type    : MySQL
 Target Server Version : 80011
 File Encoding         : 65001

 Date: 06/08/2018 20:16:26
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for litemall_groupon
-- ----------------------------
DROP TABLE IF EXISTS `litemall_groupon`;
CREATE TABLE `litemall_groupon`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL COMMENT '关联的订单ID',
  `groupon_id` int(11) NULL DEFAULT 0 COMMENT '参与的团购ID，仅当user_type不是1',
  `rules_id` int(11) NOT NULL COMMENT '团购规则ID，关联litemall_groupon_rules表ID字段',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `creator_user_id` int(11) NOT NULL COMMENT '创建者ID',
  `add_time` datetime(0) NOT NULL COMMENT '创建时间',
  `share_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '团购分享图片地址',
  `payed` tinyint(1) NOT NULL COMMENT '是否已经支付',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  `version` int(11) NULL DEFAULT 0 COMMENT '乐观锁字段',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 44 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

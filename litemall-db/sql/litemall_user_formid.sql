/*
 Navicat MySQL Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 80011
 Source Host           : localhost:3306
 Source Schema         : litemall

 Target Server Type    : MySQL
 Target Server Version : 80011
 File Encoding         : 65001

 Date: 24/07/2018 12:41:27
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for litemall_user_formid
-- ----------------------------
DROP TABLE IF EXISTS `litemall_user_formid`;
CREATE TABLE `litemall_user_formid`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `formId` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '缓存的FormId',
  `isprepay` tinyint(1) NOT NULL COMMENT '是FormId还是prepayId',
  `useAmount` int(2) NOT NULL COMMENT '可用次数，fromId为1，prepay为3，用1次减1',
  `expire_time` datetime(0) NOT NULL COMMENT '过期时间，腾讯规定为7天',
  `openId` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '微信登录openid',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

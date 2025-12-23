/*
 Navicat MySQL Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 50744
 Source Host           : localhost:3306
 Source Schema         : pet_game

 Target Server Type    : MySQL
 Target Server Version : 50744
 File Encoding         : 65001

 Date: 23/12/2025 22:44:29
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for explore_rewards
-- ----------------------------
DROP TABLE IF EXISTS `explore_rewards`;
CREATE TABLE `explore_rewards`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '奖励ID',
  `location_id` int(11) NOT NULL COMMENT '对应地点ID',
  `type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '奖励类型：coin/pet/item',
  `item_id` int(11) NULL DEFAULT NULL COMMENT '物品或宠物ID',
  `amount` int(11) NULL DEFAULT 1 COMMENT '数量（金币/道具数量）',
  `weight` int(11) NULL DEFAULT 1 COMMENT '概率权重（数字越大概率越高）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `location_id`(`location_id`) USING BTREE,
  CONSTRAINT `explore_rewards_ibfk_1` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 63 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '探索掉落表（每个地点对应的奖励池）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of explore_rewards
-- ----------------------------
INSERT INTO `explore_rewards` VALUES (1, 1, 'coin', NULL, 20, 30);
INSERT INTO `explore_rewards` VALUES (2, 1, 'coin', NULL, 50, 15);
INSERT INTO `explore_rewards` VALUES (3, 1, 'item', 1, 1, 10);
INSERT INTO `explore_rewards` VALUES (4, 1, 'item', 2, 1, 10);
INSERT INTO `explore_rewards` VALUES (5, 1, 'item', 3, 1, 10);
INSERT INTO `explore_rewards` VALUES (6, 1, 'item', 4, 1, 10);
INSERT INTO `explore_rewards` VALUES (7, 1, 'item', 5, 1, 10);
INSERT INTO `explore_rewards` VALUES (8, 1, 'pet', 1, 1, 3);
INSERT INTO `explore_rewards` VALUES (9, 1, 'pet', 2, 1, 3);
INSERT INTO `explore_rewards` VALUES (10, 1, 'pet', 3, 1, 3);
INSERT INTO `explore_rewards` VALUES (11, 1, 'pet', 4, 1, 3);
INSERT INTO `explore_rewards` VALUES (12, 1, 'pet', 5, 1, 3);
INSERT INTO `explore_rewards` VALUES (13, 2, 'coin', NULL, 80, 30);
INSERT INTO `explore_rewards` VALUES (14, 2, 'coin', NULL, 150, 15);
INSERT INTO `explore_rewards` VALUES (15, 2, 'item', 6, 1, 10);
INSERT INTO `explore_rewards` VALUES (16, 2, 'item', 7, 1, 10);
INSERT INTO `explore_rewards` VALUES (17, 2, 'item', 8, 1, 10);
INSERT INTO `explore_rewards` VALUES (18, 2, 'item', 9, 1, 10);
INSERT INTO `explore_rewards` VALUES (19, 2, 'item', 10, 1, 10);
INSERT INTO `explore_rewards` VALUES (20, 2, 'pet', 6, 1, 3);
INSERT INTO `explore_rewards` VALUES (21, 2, 'pet', 7, 1, 3);
INSERT INTO `explore_rewards` VALUES (22, 2, 'pet', 8, 1, 3);
INSERT INTO `explore_rewards` VALUES (23, 2, 'pet', 9, 1, 3);
INSERT INTO `explore_rewards` VALUES (24, 2, 'pet', 10, 1, 3);
INSERT INTO `explore_rewards` VALUES (25, 3, 'coin', NULL, 200, 30);
INSERT INTO `explore_rewards` VALUES (26, 3, 'coin', NULL, 500, 15);
INSERT INTO `explore_rewards` VALUES (27, 3, 'item', 11, 1, 10);
INSERT INTO `explore_rewards` VALUES (28, 3, 'item', 12, 1, 10);
INSERT INTO `explore_rewards` VALUES (29, 3, 'item', 13, 1, 10);
INSERT INTO `explore_rewards` VALUES (30, 3, 'item', 14, 1, 10);
INSERT INTO `explore_rewards` VALUES (31, 3, 'item', 15, 1, 10);
INSERT INTO `explore_rewards` VALUES (32, 3, 'pet', 11, 1, 3);
INSERT INTO `explore_rewards` VALUES (33, 3, 'pet', 12, 1, 3);
INSERT INTO `explore_rewards` VALUES (34, 3, 'pet', 13, 1, 3);
INSERT INTO `explore_rewards` VALUES (35, 3, 'pet', 14, 1, 3);
INSERT INTO `explore_rewards` VALUES (36, 3, 'pet', 15, 1, 3);

-- ----------------------------
-- Table structure for food_base
-- ----------------------------
DROP TABLE IF EXISTS `food_base`;
CREATE TABLE `food_base`  (
  `id` int(11) NOT NULL COMMENT '食物ID（与 shop_items.id 对应）',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '食物名称',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '食物说明',
  `is_universal` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否通用食物（1=所有宠物可吃）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '食物图鉴表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of food_base
-- ----------------------------
INSERT INTO `food_base` VALUES (1, '鱼干', '适合小猫食用', 0);
INSERT INTO `food_base` VALUES (2, '骨头', '适合小狗食用', 0);
INSERT INTO `food_base` VALUES (3, '种子', '适合麻雀食用', 0);
INSERT INTO `food_base` VALUES (4, '坚果', '适合松鼠食用', 0);
INSERT INTO `food_base` VALUES (5, '胡萝卜', '适合白兔食用', 0);
INSERT INTO `food_base` VALUES (6, '小鱼虾', '适合蓝鳍鱼食用', 0);
INSERT INTO `food_base` VALUES (7, '电能饵', '适合电鳗食用', 0);
INSERT INTO `food_base` VALUES (8, '水晶藻', '适合水晶鲤食用', 0);
INSERT INTO `food_base` VALUES (9, '深湖肉块', '适合深湖妖鱼食用', 0);
INSERT INTO `food_base` VALUES (10, '荧光浮游生物', '适合发光水母食用', 0);
INSERT INTO `food_base` VALUES (11, '魔能矿石', '适合石像守卫食用', 0);
INSERT INTO `food_base` VALUES (12, '灵魂碎片', '适合遗迹灵魂食用', 0);
INSERT INTO `food_base` VALUES (13, '巨鸟果实', '适合石羽巨鸟食用', 0);
INSERT INTO `food_base` VALUES (14, '金属能量块', '适合黄金魔像食用', 0);
INSERT INTO `food_base` VALUES (15, '古代核心', '适合遗迹之王食用', 0);

-- ----------------------------
-- Table structure for gacha_logs
-- ----------------------------
DROP TABLE IF EXISTS `gacha_logs`;
CREATE TABLE `gacha_logs`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `gacha_type` enum('normal','advanced') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `reward_type` enum('coin','pet','food') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `reward_id` int(11) NULL DEFAULT NULL,
  `reward_amount` int(11) NULL DEFAULT NULL,
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP,
  `reward_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '奖励名称快照',
  `rarity` enum('normal','rare','epic') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '稀有度',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `gacha_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 53 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gacha_logs
-- ----------------------------
INSERT INTO `gacha_logs` VALUES (42, 3, 'normal', 'coin', 121, NULL, '2025-12-23 21:54:32', '121 金币', 'normal');
INSERT INTO `gacha_logs` VALUES (43, 3, 'normal', 'food', 5, NULL, '2025-12-23 21:54:34', '胡萝卜', 'normal');
INSERT INTO `gacha_logs` VALUES (44, 3, 'normal', 'coin', 173, NULL, '2025-12-23 21:54:35', '173 金币', 'normal');
INSERT INTO `gacha_logs` VALUES (45, 3, 'normal', 'coin', 188, NULL, '2025-12-23 21:54:36', '188 金币', 'normal');
INSERT INTO `gacha_logs` VALUES (46, 3, 'normal', 'pet', 4, NULL, '2025-12-23 21:54:37', '松鼠', 'normal');
INSERT INTO `gacha_logs` VALUES (47, 3, 'normal', 'coin', 181, NULL, '2025-12-23 21:54:42', '181 金币', 'normal');
INSERT INTO `gacha_logs` VALUES (48, 3, 'normal', 'coin', 70, NULL, '2025-12-23 21:54:42', '70 金币', 'normal');
INSERT INTO `gacha_logs` VALUES (49, 3, 'normal', 'coin', 68, NULL, '2025-12-23 21:56:54', '68 金币', 'normal');
INSERT INTO `gacha_logs` VALUES (50, 3, 'normal', 'pet', 4, NULL, '2025-12-23 21:56:55', '松鼠', 'normal');
INSERT INTO `gacha_logs` VALUES (51, 3, 'normal', 'food', 2, NULL, '2025-12-23 21:56:55', '骨头', 'normal');
INSERT INTO `gacha_logs` VALUES (52, 3, 'normal', 'food', 2, NULL, '2025-12-23 21:56:55', '骨头', 'normal');

-- ----------------------------
-- Table structure for gacha_pool
-- ----------------------------
DROP TABLE IF EXISTS `gacha_pool`;
CREATE TABLE `gacha_pool`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `gacha_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `reward_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `min_id` int(11) NOT NULL,
  `max_id` int(11) NOT NULL,
  `weight` int(11) NOT NULL,
  `rarity` enum('normal','rare','epic') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '奖励稀有度',
  `reward_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '奖励显示名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gacha_pool
-- ----------------------------
INSERT INTO `gacha_pool` VALUES (1, 'normal', 'coin', 1, 200, 5, 'normal', '金币');
INSERT INTO `gacha_pool` VALUES (2, 'normal', 'pet', 1, 5, 2, 'normal', '普通宠物');
INSERT INTO `gacha_pool` VALUES (3, 'normal', 'food', 1, 5, 3, 'normal', '普通食物');
INSERT INTO `gacha_pool` VALUES (4, 'advanced', 'coin', 300, 1000, 4, 'normal', '金币');
INSERT INTO `gacha_pool` VALUES (5, 'advanced', 'pet', 6, 10, 2, 'rare', '稀有宠物');
INSERT INTO `gacha_pool` VALUES (6, 'advanced', 'food', 6, 10, 2, 'rare', '高级食物');
INSERT INTO `gacha_pool` VALUES (7, 'advanced', 'pet', 11, 15, 1, 'epic', '史诗宠物');
INSERT INTO `gacha_pool` VALUES (8, 'advanced', 'food', 11, 15, 1, 'epic', '史诗食物');

-- ----------------------------
-- Table structure for locations
-- ----------------------------
DROP TABLE IF EXISTS `locations`;
CREATE TABLE `locations`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '地点ID',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '地点名称',
  `description` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '地点介绍',
  `icon` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图标URL',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '探索地点表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of locations
-- ----------------------------
INSERT INTO `locations` VALUES (1, '公园', '适合新手的休闲场所，可获得普通宠物与基础奖励', NULL);
INSERT INTO `locations` VALUES (2, '神秘湖泊', '宁静又危险的湖区，可以钓上稀有生物', NULL);
INSERT INTO `locations` VALUES (3, '遗迹', '充满未知能量的古老遗迹，可挑战强力怪物', NULL);

-- ----------------------------
-- Table structure for pets_base
-- ----------------------------
DROP TABLE IF EXISTS `pets_base`;
CREATE TABLE `pets_base`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '宠物ID',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '宠物名称',
  `rarity` int(11) NOT NULL COMMENT '稀有度（1-3）',
  `description` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '宠物描述',
  `icon` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '宠物图标URL',
  `food_item_id` int(11) NULL DEFAULT NULL COMMENT '可食用的低级食物ID（shop_items.id）',
  `fatigue_max` int(11) NULL DEFAULT NULL COMMENT '疲劳上限',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '宠物图鉴表（系统定义的全部宠物信息）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pets_base
-- ----------------------------
INSERT INTO `pets_base` VALUES (1, '小猫', 1, '温顺的猫咪，适合陪伴新手训练师', NULL, 1, 10);
INSERT INTO `pets_base` VALUES (2, '小狗', 1, '活泼可爱的小狗，喜欢跑来跑去', NULL, 2, 10);
INSERT INTO `pets_base` VALUES (3, '麻雀', 1, '随处可见的小鸟，但精力充沛', NULL, 3, 10);
INSERT INTO `pets_base` VALUES (4, '松鼠', 1, '喜欢囤积食物', NULL, 4, 10);
INSERT INTO `pets_base` VALUES (5, '白兔', 1, '机灵的小兔子，经常四处乱跳', NULL, 5, 10);
INSERT INTO `pets_base` VALUES (6, '蓝鳍鱼', 2, '闪亮鳍片的蓝色小鱼', NULL, 6, 30);
INSERT INTO `pets_base` VALUES (7, '电鳗', 2, '身体带电的奇怪生物', NULL, 7, 30);
INSERT INTO `pets_base` VALUES (8, '水晶鲤', 2, '据说能带来好运的稀有鱼类', NULL, 8, 30);
INSERT INTO `pets_base` VALUES (9, '深湖妖鱼', 2, '湖泊深处出现的神秘生物', NULL, 9, 30);
INSERT INTO `pets_base` VALUES (10, '发光水母', 2, '拥有发光触手的灵性水母', NULL, 10, 30);
INSERT INTO `pets_base` VALUES (11, '石像守卫', 3, '古代遗迹的守门者，体型巨大', NULL, 11, 50);
INSERT INTO `pets_base` VALUES (12, '遗迹灵魂', 3, '遗迹中残存的能量实体', NULL, 12, 50);
INSERT INTO `pets_base` VALUES (13, '石羽巨鸟', 3, '传说中俯瞰遗迹的巨鸟', NULL, 13, 50);
INSERT INTO `pets_base` VALUES (14, '黄金魔像', 3, '由宝石与金属构成的古代造物', NULL, 14, 50);
INSERT INTO `pets_base` VALUES (15, '遗迹之王', 3, '掌握古代能量的神秘王者', NULL, 15, 50);

-- ----------------------------
-- Table structure for shop_items
-- ----------------------------
DROP TABLE IF EXISTS `shop_items`;
CREATE TABLE `shop_items`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '道具ID',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '道具名称',
  `price` int(11) NOT NULL COMMENT '道具价格（金币）',
  `description` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '说明',
  `icon` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '道具图标URL',
  `is_food` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否为食物',
  `food_power` int(11) NULL DEFAULT 0 COMMENT '恢复疲劳值',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商店物品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shop_items
-- ----------------------------
INSERT INTO `shop_items` VALUES (1, '鱼干', 20, '适合小猫食用', '/shop/鱼干.png', 1, 2);
INSERT INTO `shop_items` VALUES (2, '骨头', 20, '适合小狗食用', '/shop/骨头.png', 1, 2);
INSERT INTO `shop_items` VALUES (3, '种子', 20, '适合麻雀食用', '/shop/种子.png', 1, 2);
INSERT INTO `shop_items` VALUES (4, '坚果', 20, '适合松鼠食用', '/shop/坚果.png', 1, 2);
INSERT INTO `shop_items` VALUES (5, '胡萝卜', 20, '适合白兔食用', '/shop/胡萝卜.png', 1, 2);
INSERT INTO `shop_items` VALUES (6, '高级食物', 300, '所有宠物都能吃，降低更多疲劳值', '/shop/高级食物.png', 1, 50);

-- ----------------------------
-- Table structure for user_items
-- ----------------------------
DROP TABLE IF EXISTS `user_items`;
CREATE TABLE `user_items`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '玩家道具记录ID',
  `user_id` int(11) NULL DEFAULT NULL COMMENT '玩家ID',
  `item_id` int(11) NULL DEFAULT NULL COMMENT '道具ID',
  `amount` int(11) NULL DEFAULT 1 COMMENT '数量',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `item_id`(`item_id`) USING BTREE,
  CONSTRAINT `user_items_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `user_items_ibfk_2` FOREIGN KEY (`item_id`) REFERENCES `shop_items` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '玩家背包表（记录玩家拥有的道具）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_items
-- ----------------------------
INSERT INTO `user_items` VALUES (1, 1, 1, 9);
INSERT INTO `user_items` VALUES (2, 1, 3, 2);
INSERT INTO `user_items` VALUES (3, 1, 2, 3);
INSERT INTO `user_items` VALUES (4, 1, 5, 2);
INSERT INTO `user_items` VALUES (5, 1, 4, 1);
INSERT INTO `user_items` VALUES (6, 2, 1, 6);
INSERT INTO `user_items` VALUES (7, 2, 6, 1);
INSERT INTO `user_items` VALUES (8, 2, 3, 1);
INSERT INTO `user_items` VALUES (10, 2, 3, 1);
INSERT INTO `user_items` VALUES (11, 2, 2, 1);
INSERT INTO `user_items` VALUES (12, 2, 1, 1);
INSERT INTO `user_items` VALUES (13, 2, 3, 1);
INSERT INTO `user_items` VALUES (14, 2, 2, 1);
INSERT INTO `user_items` VALUES (15, 3, 1, 5);
INSERT INTO `user_items` VALUES (16, 3, 6, 1);
INSERT INTO `user_items` VALUES (17, 3, 4, 1);
INSERT INTO `user_items` VALUES (18, 3, 3, 1);
INSERT INTO `user_items` VALUES (19, 3, 5, 1);
INSERT INTO `user_items` VALUES (20, 3, 5, 1);
INSERT INTO `user_items` VALUES (21, 3, 2, 1);
INSERT INTO `user_items` VALUES (22, 3, 2, 1);

-- ----------------------------
-- Table structure for user_pets
-- ----------------------------
DROP TABLE IF EXISTS `user_pets`;
CREATE TABLE `user_pets`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '玩家宠物唯一ID',
  `user_id` int(11) NOT NULL COMMENT '所属玩家ID',
  `pet_id` int(11) NOT NULL COMMENT '对应宠物图鉴ID（pets_base）',
  `nickname` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '玩家给宠物的昵称',
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '获得时间',
  `fatigue` int(11) NOT NULL DEFAULT 0 COMMENT '当前疲劳值',
  `fatigue_max` int(11) NOT NULL DEFAULT 10 COMMENT '疲劳上限',
  `is_active` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否当前携带（1=携带中）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `pet_id`(`pet_id`) USING BTREE,
  CONSTRAINT `user_pets_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `user_pets_ibfk_2` FOREIGN KEY (`pet_id`) REFERENCES `pets_base` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '玩家宠物表（记录玩家实际拥有的宠物）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_pets
-- ----------------------------
INSERT INTO `user_pets` VALUES (1, 1, 1, '小猫', '2025-12-06 23:10:45', 0, 10, 0);
INSERT INTO `user_pets` VALUES (2, 1, 2, '小狗', '2025-12-06 23:10:45', 0, 10, 0);
INSERT INTO `user_pets` VALUES (3, 1, 6, '蓝鳍鱼', '2025-12-18 23:14:37', 0, 30, 0);
INSERT INTO `user_pets` VALUES (4, 2, 1, '我的小猫', '2025-12-23 20:54:22', 0, 10, 1);
INSERT INTO `user_pets` VALUES (5, 2, 4, NULL, '2025-12-23 21:34:01', 0, 10, 0);
INSERT INTO `user_pets` VALUES (6, 2, 1, NULL, '2025-12-23 21:34:01', 0, 10, 0);
INSERT INTO `user_pets` VALUES (7, 2, 5, NULL, '2025-12-23 21:34:12', 0, 10, 0);
INSERT INTO `user_pets` VALUES (8, 2, 5, NULL, '2025-12-23 21:34:13', 0, 10, 0);
INSERT INTO `user_pets` VALUES (9, 2, 8, NULL, '2025-12-23 21:35:01', 0, 10, 0);
INSERT INTO `user_pets` VALUES (10, 2, 3, NULL, '2025-12-23 21:40:22', 0, 10, 0);
INSERT INTO `user_pets` VALUES (11, 3, 1, '我的小猫', '2025-12-23 21:41:17', 0, 10, 1);
INSERT INTO `user_pets` VALUES (12, 3, 7, NULL, '2025-12-23 21:41:35', 0, 10, 0);
INSERT INTO `user_pets` VALUES (13, 3, 5, NULL, '2025-12-23 21:42:55', 0, 10, 0);
INSERT INTO `user_pets` VALUES (14, 3, 1, NULL, '2025-12-23 21:42:55', 0, 10, 0);
INSERT INTO `user_pets` VALUES (15, 3, 4, NULL, '2025-12-23 21:54:37', 0, 10, 0);
INSERT INTO `user_pets` VALUES (16, 3, 4, NULL, '2025-12-23 21:56:55', 0, 10, 0);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID（主键）',
  `account` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '登录账号',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '玩家昵称',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '登录密码（建议加密）',
  `avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像URL',
  `coins` int(11) NULL DEFAULT 0 COMMENT '玩家持有金币',
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '用户信息更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `account`(`account`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户表（存储玩家账号、属性等基础资料）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', '管理员', 'admin', NULL, 1111119083, '2025-12-06 23:10:34', '2025-12-23 22:05:25');
INSERT INTO `users` VALUES (2, 'cst', 'cst', '123456', '/avatars/txone.jpg', 174, '2025-12-23 20:54:22', '2025-12-23 22:05:27');
INSERT INTO `users` VALUES (3, 'xmm', 'xmm', '123456', '/avatars/txone.jpg', 20, '2025-12-23 21:41:17', '2025-12-23 21:56:55');

SET FOREIGN_KEY_CHECKS = 1;

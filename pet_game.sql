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

 Date: 24/12/2025 03:02:50
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
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '探索掉落表（每个地点对应的奖励池）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of explore_rewards
-- ----------------------------
INSERT INTO `explore_rewards` VALUES (1, 1, 'coin', NULL, 50, 1);
INSERT INTO `explore_rewards` VALUES (2, 1, 'item_range', 1, 1, 1);
INSERT INTO `explore_rewards` VALUES (3, 1, 'pet_range', 1, 1, 1);
INSERT INTO `explore_rewards` VALUES (4, 2, 'coin', NULL, 150, 1);
INSERT INTO `explore_rewards` VALUES (5, 2, 'item_range', 6, 1, 1);
INSERT INTO `explore_rewards` VALUES (6, 2, 'pet_range', 6, 1, 1);
INSERT INTO `explore_rewards` VALUES (7, 3, 'coin', NULL, 500, 1);
INSERT INTO `explore_rewards` VALUES (8, 3, 'item_range', 11, 1, 1);
INSERT INTO `explore_rewards` VALUES (9, 3, 'pet_range', 11, 1, 1);

-- ----------------------------
-- Table structure for food_base
-- ----------------------------
DROP TABLE IF EXISTS `food_base`;
CREATE TABLE `food_base`  (
  `id` int(11) NOT NULL COMMENT '食物ID（与 shop_items.id 对应）',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '食物名称',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '食物说明',
  `is_universal` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否通用食物（1=所有宠物可吃）',
  `icon` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '食物图标URL',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '食物图鉴表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of food_base
-- ----------------------------
INSERT INTO `food_base` VALUES (1, '鱼干', '适合小猫食用', 0, '/shop/鱼干.png');
INSERT INTO `food_base` VALUES (2, '骨头', '适合小狗食用', 0, '/shop/骨头.png');
INSERT INTO `food_base` VALUES (3, '种子', '适合麻雀食用', 0, '/shop/种子.png');
INSERT INTO `food_base` VALUES (4, '坚果', '适合松鼠食用', 0, '/shop/坚果.png');
INSERT INTO `food_base` VALUES (5, '胡萝卜', '适合白兔食用', 0, '/shop/胡萝卜.png');
INSERT INTO `food_base` VALUES (6, '小鱼虾', '适合蓝鳍鱼食用', 0, '/shop/小鱼虾.png');
INSERT INTO `food_base` VALUES (7, '电能饵', '适合电鳗食用', 0, '/shop/电能饵.png');
INSERT INTO `food_base` VALUES (8, '水晶藻', '适合水晶鲤食用', 0, '/shop/水晶藻.png');
INSERT INTO `food_base` VALUES (9, '深湖肉块', '适合深湖妖鱼食用', 0, '/shop/深湖肉块.png');
INSERT INTO `food_base` VALUES (10, '荧光浮游生物', '适合发光水母食用', 0, '/shop/荧光浮游生物.png');
INSERT INTO `food_base` VALUES (11, '魔能矿石', '适合石像守卫食用', 0, '/shop/魔能矿石.png');
INSERT INTO `food_base` VALUES (12, '灵魂碎片', '适合遗迹灵魂食用', 0, '/shop/灵魂碎片.png');
INSERT INTO `food_base` VALUES (13, '巨鸟果实', '适合石羽巨鸟食用', 0, '/shop/巨鸟果实.png');
INSERT INTO `food_base` VALUES (14, '金属能量块', '适合黄金魔像食用', 0, '/shop/金属能量块.png');
INSERT INTO `food_base` VALUES (15, '古代核心', '适合遗迹之王食用', 0, '/shop/古代核心.png');

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
) ENGINE = InnoDB AUTO_INCREMENT = 63 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

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
INSERT INTO `pets_base` VALUES (1, '小猫', 1, '温顺的猫咪，适合陪伴新手训练师', '/icons/cat.png', 1, 10);
INSERT INTO `pets_base` VALUES (2, '小狗', 1, '活泼可爱的小狗，喜欢跑来跑去', '/icons/dog.png', 2, 10);
INSERT INTO `pets_base` VALUES (3, '麻雀', 1, '随处可见的小鸟，但精力充沛', '/icons/maque.png', 3, 10);
INSERT INTO `pets_base` VALUES (4, '松鼠', 1, '喜欢囤积食物', '/icons/songshu.png', 4, 10);
INSERT INTO `pets_base` VALUES (5, '白兔', 1, '机灵的小兔子，经常四处乱跳', '/icons/baitu.png', 5, 10);
INSERT INTO `pets_base` VALUES (6, '蓝鳍鱼', 2, '闪亮鳍片的蓝色小鱼', '/icons/bluefish.png', 6, 30);
INSERT INTO `pets_base` VALUES (7, '电鳗', 2, '身体带电的奇怪生物', '/icons/dianmang.png', 7, 30);
INSERT INTO `pets_base` VALUES (8, '水晶鲤', 2, '据说能带来好运的稀有鱼类', '/icons/li.png', 8, 30);
INSERT INTO `pets_base` VALUES (9, '深湖妖鱼', 2, '湖泊深处出现的神秘生物', '/icons/deepfish.png', 9, 30);
INSERT INTO `pets_base` VALUES (10, '发光水母', 2, '拥有发光触手的灵性水母', '/icons/bulingbuling.png', 10, 30);
INSERT INTO `pets_base` VALUES (11, '石像守卫', 3, '古代遗迹的守门者，体型巨大', '/icons/stone.png', 11, 50);
INSERT INTO `pets_base` VALUES (12, '遗迹灵魂', 3, '遗迹中残存的能量实体', '/icons/ghost.png', 12, 50);
INSERT INTO `pets_base` VALUES (13, '石羽巨鸟', 3, '传说中俯瞰遗迹的巨鸟', '/icons/bigbird.png', 13, 50);
INSERT INTO `pets_base` VALUES (14, '黄金魔像', 3, '由宝石与金属构成的古代造物', '/icons/golden.png', 14, 50);
INSERT INTO `pets_base` VALUES (15, '遗迹之王', 3, '掌握古代能量的神秘王者', '/icons/king.png', 15, 50);

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
  CONSTRAINT `user_items_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 56 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '玩家背包表（记录玩家拥有的道具）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_items
-- ----------------------------
INSERT INTO `user_items` VALUES (1, 1, 1, 9);
INSERT INTO `user_items` VALUES (2, 1, 3, 2);
INSERT INTO `user_items` VALUES (3, 1, 2, 3);
INSERT INTO `user_items` VALUES (4, 1, 5, 2);
INSERT INTO `user_items` VALUES (5, 1, 4, 1);

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
) ENGINE = InnoDB AUTO_INCREMENT = 63 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '玩家宠物表（记录玩家实际拥有的宠物）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_pets
-- ----------------------------
INSERT INTO `user_pets` VALUES (1, 1, 1, '小猫', '2025-12-06 23:10:45', 10, 10, 0);
INSERT INTO `user_pets` VALUES (2, 1, 2, '小狗', '2025-12-06 23:10:45', 0, 10, 0);
INSERT INTO `user_pets` VALUES (3, 1, 6, '蓝鳍鱼', '2025-12-18 23:14:37', 0, 30, 0);

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
INSERT INTO `users` VALUES (1, 'admin', '管理员', 'admin', '/avatars/default.jpg', 1111119083, '2025-12-06 23:10:34', '2025-12-23 22:05:25');

SET FOREIGN_KEY_CHECKS = 1;

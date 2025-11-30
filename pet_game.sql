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

 Date: 30/11/2025 18:54:49
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
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '探索掉落表（每个地点对应的奖励池）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of explore_rewards
-- ----------------------------
INSERT INTO `explore_rewards` VALUES (1, 1, 'coin', NULL, 20, 40);
INSERT INTO `explore_rewards` VALUES (2, 1, 'coin', NULL, 50, 20);
INSERT INTO `explore_rewards` VALUES (3, 1, 'item', 1, 1, 35);
INSERT INTO `explore_rewards` VALUES (4, 1, 'pet', 1, 1, 4);
INSERT INTO `explore_rewards` VALUES (5, 1, 'pet', 2, 1, 4);
INSERT INTO `explore_rewards` VALUES (6, 1, 'pet', 3, 1, 4);
INSERT INTO `explore_rewards` VALUES (7, 1, 'pet', 4, 1, 2);
INSERT INTO `explore_rewards` VALUES (8, 1, 'pet', 5, 1, 2);
INSERT INTO `explore_rewards` VALUES (9, 2, 'coin', NULL, 80, 30);
INSERT INTO `explore_rewards` VALUES (10, 2, 'coin', NULL, 150, 20);
INSERT INTO `explore_rewards` VALUES (11, 2, 'item', 2, 1, 25);
INSERT INTO `explore_rewards` VALUES (12, 2, 'item', 3, 1, 10);
INSERT INTO `explore_rewards` VALUES (13, 2, 'pet', 6, 1, 4);
INSERT INTO `explore_rewards` VALUES (14, 2, 'pet', 7, 1, 3);
INSERT INTO `explore_rewards` VALUES (15, 2, 'pet', 8, 1, 3);
INSERT INTO `explore_rewards` VALUES (16, 2, 'pet', 9, 1, 2);
INSERT INTO `explore_rewards` VALUES (17, 2, 'pet', 10, 1, 2);
INSERT INTO `explore_rewards` VALUES (18, 3, 'coin', NULL, 200, 25);
INSERT INTO `explore_rewards` VALUES (19, 3, 'coin', NULL, 500, 15);
INSERT INTO `explore_rewards` VALUES (20, 3, 'item', 4, 1, 20);
INSERT INTO `explore_rewards` VALUES (21, 3, 'item', 5, 1, 10);
INSERT INTO `explore_rewards` VALUES (22, 3, 'pet', 11, 1, 4);
INSERT INTO `explore_rewards` VALUES (23, 3, 'pet', 12, 1, 3);
INSERT INTO `explore_rewards` VALUES (24, 3, 'pet', 13, 1, 3);
INSERT INTO `explore_rewards` VALUES (25, 3, 'pet', 14, 1, 2);
INSERT INTO `explore_rewards` VALUES (26, 3, 'pet', 15, 1, 2);

-- ----------------------------
-- Table structure for gacha_logs
-- ----------------------------
DROP TABLE IF EXISTS `gacha_logs`;
CREATE TABLE `gacha_logs`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` int(11) NULL DEFAULT NULL COMMENT '玩家ID',
  `pet_id` int(11) NULL DEFAULT NULL COMMENT '抽到的宠物ID',
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '抽奖时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `pet_id`(`pet_id`) USING BTREE,
  CONSTRAINT `gacha_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `gacha_logs_ibfk_2` FOREIGN KEY (`pet_id`) REFERENCES `pets_base` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '抽奖日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for gacha_pool
-- ----------------------------
DROP TABLE IF EXISTS `gacha_pool`;
CREATE TABLE `gacha_pool`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '抽奖池条目ID',
  `pet_id` int(11) NOT NULL COMMENT '对应宠物ID',
  `weight` int(11) NULL DEFAULT 1 COMMENT '掉落权重',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `pet_id`(`pet_id`) USING BTREE,
  CONSTRAINT `gacha_pool_ibfk_1` FOREIGN KEY (`pet_id`) REFERENCES `pets_base` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '扭蛋抽奖池（用于抽奖获得宠物）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gacha_pool
-- ----------------------------
INSERT INTO `gacha_pool` VALUES (1, 1, 20);
INSERT INTO `gacha_pool` VALUES (2, 2, 20);
INSERT INTO `gacha_pool` VALUES (3, 3, 20);
INSERT INTO `gacha_pool` VALUES (4, 4, 15);
INSERT INTO `gacha_pool` VALUES (5, 5, 15);
INSERT INTO `gacha_pool` VALUES (6, 6, 5);
INSERT INTO `gacha_pool` VALUES (7, 7, 4);
INSERT INTO `gacha_pool` VALUES (8, 8, 3);
INSERT INTO `gacha_pool` VALUES (9, 9, 2);
INSERT INTO `gacha_pool` VALUES (10, 10, 1);
INSERT INTO `gacha_pool` VALUES (11, 6, 10);
INSERT INTO `gacha_pool` VALUES (12, 7, 10);
INSERT INTO `gacha_pool` VALUES (13, 8, 10);
INSERT INTO `gacha_pool` VALUES (14, 9, 10);
INSERT INTO `gacha_pool` VALUES (15, 10, 10);
INSERT INTO `gacha_pool` VALUES (16, 11, 8);
INSERT INTO `gacha_pool` VALUES (17, 12, 8);
INSERT INTO `gacha_pool` VALUES (18, 13, 8);
INSERT INTO `gacha_pool` VALUES (19, 14, 8);
INSERT INTO `gacha_pool` VALUES (20, 15, 8);

-- ----------------------------
-- Table structure for levels
-- ----------------------------
DROP TABLE IF EXISTS `levels`;
CREATE TABLE `levels`  (
  `level` int(11) NOT NULL COMMENT '等级',
  `exp_required` int(11) NOT NULL COMMENT '升级到下一级所需经验',
  PRIMARY KEY (`level`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '玩家升级经验配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of levels
-- ----------------------------
INSERT INTO `levels` VALUES (1, 0);
INSERT INTO `levels` VALUES (2, 100);
INSERT INTO `levels` VALUES (3, 250);
INSERT INTO `levels` VALUES (4, 450);
INSERT INTO `levels` VALUES (5, 700);
INSERT INTO `levels` VALUES (6, 1000);
INSERT INTO `levels` VALUES (7, 1400);
INSERT INTO `levels` VALUES (8, 1850);
INSERT INTO `levels` VALUES (9, 2350);
INSERT INTO `levels` VALUES (10, 2900);
INSERT INTO `levels` VALUES (11, 3500);
INSERT INTO `levels` VALUES (12, 4150);
INSERT INTO `levels` VALUES (13, 4850);
INSERT INTO `levels` VALUES (14, 5600);
INSERT INTO `levels` VALUES (15, 6400);
INSERT INTO `levels` VALUES (16, 7250);
INSERT INTO `levels` VALUES (17, 8150);
INSERT INTO `levels` VALUES (18, 9100);
INSERT INTO `levels` VALUES (19, 10100);
INSERT INTO `levels` VALUES (20, 11200);

-- ----------------------------
-- Table structure for locations
-- ----------------------------
DROP TABLE IF EXISTS `locations`;
CREATE TABLE `locations`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '地点ID',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '地点名称',
  `min_level` int(11) NULL DEFAULT 1 COMMENT '进入地点需要的最低玩家等级',
  `description` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '地点介绍',
  `icon` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图标URL',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '探索地点表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of locations
-- ----------------------------
INSERT INTO `locations` VALUES (1, '公园', 1, '适合新手的休闲场所，可获得普通宠物与基础奖励', NULL);
INSERT INTO `locations` VALUES (2, '神秘湖泊', 5, '宁静又危险的湖区，可以钓上稀有生物', NULL);
INSERT INTO `locations` VALUES (3, '遗迹', 10, '充满未知能量的古老遗迹，可挑战强力怪物', NULL);

-- ----------------------------
-- Table structure for pets_base
-- ----------------------------
DROP TABLE IF EXISTS `pets_base`;
CREATE TABLE `pets_base`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '宠物ID',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '宠物名称',
  `rarity` int(11) NOT NULL COMMENT '稀有度（1-5）',
  `description` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '宠物描述',
  `icon` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '宠物图标URL',
  `obtain_level` int(11) NULL DEFAULT 1 COMMENT '可获得的最低玩家等级',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '宠物图鉴表（系统定义的全部宠物信息）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pets_base
-- ----------------------------
INSERT INTO `pets_base` VALUES (1, '家猫', 1, '温顺的猫咪，适合陪伴新手训练师', NULL, 1);
INSERT INTO `pets_base` VALUES (2, '小狗', 1, '活泼可爱的小狗，喜欢跑来跑去', NULL, 1);
INSERT INTO `pets_base` VALUES (3, '麻雀', 1, '随处可见的小鸟，但精力充沛', NULL, 1);
INSERT INTO `pets_base` VALUES (4, '乌龟', 1, '行动缓慢但很可靠', NULL, 1);
INSERT INTO `pets_base` VALUES (5, '白兔', 1, '机灵的小兔子，经常四处乱跳', NULL, 1);
INSERT INTO `pets_base` VALUES (6, '蓝鳍鱼', 2, '闪亮鳍片的蓝色小鱼', NULL, 5);
INSERT INTO `pets_base` VALUES (7, '电鳗', 2, '身体带电的奇怪生物', NULL, 5);
INSERT INTO `pets_base` VALUES (8, '水晶鲤', 2, '据说能带来好运的稀有鱼类', NULL, 5);
INSERT INTO `pets_base` VALUES (9, '深湖妖鱼', 2, '湖泊深处出现的神秘生物', NULL, 5);
INSERT INTO `pets_base` VALUES (10, '水母精灵', 2, '拥有发光触手的灵性水母', NULL, 5);
INSERT INTO `pets_base` VALUES (11, '石像守卫', 3, '古代遗迹的守门者，体型巨大', NULL, 10);
INSERT INTO `pets_base` VALUES (12, '遗迹灵魂', 3, '遗迹中残存的能量实体', NULL, 10);
INSERT INTO `pets_base` VALUES (13, '石羽巨鸟', 3, '传说中俯瞰遗迹的巨鸟', NULL, 10);
INSERT INTO `pets_base` VALUES (14, '黄金魔像', 3, '由宝石与金属构成的古代造物', NULL, 10);
INSERT INTO `pets_base` VALUES (15, '遗迹之王', 3, '掌握古代能量的神秘王者', NULL, 10);

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
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商店物品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shop_items
-- ----------------------------
INSERT INTO `shop_items` VALUES (1, '普通食物', 50, '降低宠物疲劳10点', NULL);
INSERT INTO `shop_items` VALUES (2, '稀有食物', 150, '降低疲劳20点', NULL);
INSERT INTO `shop_items` VALUES (3, '普通抽奖券', 100, '可进行一次普通抽奖', NULL);
INSERT INTO `shop_items` VALUES (4, '高级食物', 300, '所有宠物都能吃，恢复更多疲劳', NULL);
INSERT INTO `shop_items` VALUES (5, '高级抽奖券', 500, '可进行一次高级抽奖', NULL);

-- ----------------------------
-- Table structure for tasks
-- ----------------------------
DROP TABLE IF EXISTS `tasks`;
CREATE TABLE `tasks`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `title` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '任务标题',
  `description` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '任务说明',
  `reward_coins` int(11) NULL DEFAULT 0 COMMENT '奖励金币',
  `reward_exp` int(11) NULL DEFAULT 0 COMMENT '奖励经验',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '任务列表（系统预设任务）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tasks
-- ----------------------------
INSERT INTO `tasks` VALUES (1, '探索任意地点3次', '完成3次探索任务', 100, 50);
INSERT INTO `tasks` VALUES (2, '喂食宠物一次', '给你的宠物喂一次食物', 50, 30);
INSERT INTO `tasks` VALUES (3, '进行1次抽奖', '参与一次抽奖活动', 80, 40);
INSERT INTO `tasks` VALUES (4, '收集5种宠物', '收集满5种不同的宠物', 150, 100);
INSERT INTO `tasks` VALUES (5, '收集10种宠物', '收集满10种不同的宠物', 200, 150);
INSERT INTO `tasks` VALUES (6, '收集15种宠物', '集齐全部宠物图鉴', 500, 300);
INSERT INTO `tasks` VALUES (7, '首次探索公园', '第一次探索公园地点', 50, 20);
INSERT INTO `tasks` VALUES (8, '首次探索神秘湖泊', '第一次探索神秘湖泊地点', 100, 40);
INSERT INTO `tasks` VALUES (9, '首次探索遗迹', '第一次探索遗迹地点', 200, 80);
INSERT INTO `tasks` VALUES (10, '获得一只普通宠物', '首次获取普通稀有度宠物', 50, 20);
INSERT INTO `tasks` VALUES (11, '获得一只稀有宠物', '首次获取稀有稀有度宠物', 150, 60);
INSERT INTO `tasks` VALUES (12, '获得一只史诗宠物', '首次获取史诗稀有度宠物', 300, 120);

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '玩家背包表（记录玩家拥有的道具）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_pets
-- ----------------------------
DROP TABLE IF EXISTS `user_pets`;
CREATE TABLE `user_pets`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '玩家宠物唯一ID',
  `user_id` int(11) NOT NULL COMMENT '所属玩家ID',
  `pet_id` int(11) NOT NULL COMMENT '对应宠物图鉴ID（pets_base）',
  `nickname` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '玩家给宠物的昵称',
  `level` int(11) NULL DEFAULT 1 COMMENT '宠物当前等级',
  `exp` int(11) NULL DEFAULT 0 COMMENT '宠物当前经验',
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '获得时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `pet_id`(`pet_id`) USING BTREE,
  CONSTRAINT `user_pets_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `user_pets_ibfk_2` FOREIGN KEY (`pet_id`) REFERENCES `pets_base` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '玩家宠物表（记录玩家实际拥有的宠物）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_tasks
-- ----------------------------
DROP TABLE IF EXISTS `user_tasks`;
CREATE TABLE `user_tasks`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户任务ID',
  `user_id` int(11) NULL DEFAULT NULL COMMENT '玩家ID',
  `task_id` int(11) NULL DEFAULT NULL COMMENT '任务ID',
  `status` int(11) NULL DEFAULT 0 COMMENT '0未完成 1完成',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `task_id`(`task_id`) USING BTREE,
  CONSTRAINT `user_tasks_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `user_tasks_ibfk_2` FOREIGN KEY (`task_id`) REFERENCES `tasks` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '玩家任务状态表' ROW_FORMAT = Dynamic;

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
  `level` int(11) NULL DEFAULT 1 COMMENT '玩家等级',
  `coins` int(11) NULL DEFAULT 0 COMMENT '玩家持有金币',
  `exp` int(11) NULL DEFAULT 0 COMMENT '玩家当前经验',
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '用户信息更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `account`(`account`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户表（存储玩家账号、属性等基础资料）' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

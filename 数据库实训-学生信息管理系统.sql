/*
 Navicat Premium Dump SQL

 Source Server         : 202335010347向志豪
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : 数据库实训-学生信息管理系统

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 11/06/2025 22:06:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for 健康信息
-- ----------------------------
DROP TABLE IF EXISTS `健康信息`;
CREATE TABLE `健康信息`  (
  `学号` char(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `血型` enum('A型','B型','AB型','O型','其他') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `身高` smallint NOT NULL,
  `体重` smallint NULL DEFAULT NULL,
  `疾病史` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`学号`) USING BTREE,
  CONSTRAINT `健康信息_基本信息_学号_fk` FOREIGN KEY (`学号`) REFERENCES `基本信息` (`学号`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of 健康信息
-- ----------------------------
INSERT INTO `健康信息` VALUES ('202301001', 'A型', 175, 65, '青霉素过敏');
INSERT INTO `健康信息` VALUES ('202301002', 'B型', 168, 55, NULL);
INSERT INTO `健康信息` VALUES ('202301003', 'O型', 180, 70, '哮喘病史');
INSERT INTO `健康信息` VALUES ('202301005', 'A型', 165, 60, NULL);

-- ----------------------------
-- Table structure for 参与组织
-- ----------------------------
DROP TABLE IF EXISTS `参与组织`;
CREATE TABLE `参与组织`  (
  `学号` char(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `组织编号` char(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `组织名称` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  INDEX `参与组织_基本信息_学号_fk`(`学号` ASC) USING BTREE,
  CONSTRAINT `参与组织_基本信息_学号_fk` FOREIGN KEY (`学号`) REFERENCES `基本信息` (`学号`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of 参与组织
-- ----------------------------
INSERT INTO `参与组织` VALUES ('202301001', 'STU001', '学生会');
INSERT INTO `参与组织` VALUES ('202301001', 'TECH001', '计算机协会');
INSERT INTO `参与组织` VALUES ('202301002', 'ECON001', '金融研究社');
INSERT INTO `参与组织` VALUES ('202301003', 'SPO001', '篮球社');

-- ----------------------------
-- Table structure for 基本信息
-- ----------------------------
DROP TABLE IF EXISTS `基本信息`;
CREATE TABLE `基本信息`  (
  `学号` char(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `姓名` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `班级` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `出生时间` date NOT NULL,
  `出生地` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `毕业学校` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `原籍住址` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `宿舍号` int NOT NULL,
  `手机号` char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `处分/奖励史` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `担任班委` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `专业编号` char(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `专业` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`学号`) USING BTREE,
  INDEX `基本信息_学院专业表_专业编号_fk`(`专业编号` ASC) USING BTREE,
  CONSTRAINT `基本信息_学院专业表_专业编号_fk` FOREIGN KEY (`专业编号`) REFERENCES `学院专业表` (`专业编号`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of 基本信息
-- ----------------------------
INSERT INTO `基本信息` VALUES ('202301001', '张三', '01', '2000-01-01', '北京', '北京一中', '北京市海淀区', 101, '13800138001', '校级三好学生', '劳动委员', '100', '计算机科学与技术');
INSERT INTO `基本信息` VALUES ('202301002', '李四', '02', '2001-05-15', '上海', '上海实验中学', '上海市浦东新区', 202, '13800138002', '无', '学习委员', '101', '金融学');
INSERT INTO `基本信息` VALUES ('202301003', '王五', '03', '2002-03-20', '广州', '广州中学', '广州市天河区', 303, '13800138003', '优秀学生干部', NULL, '100', '计算机科学与技术');
INSERT INTO `基本信息` VALUES ('202301005', '小红', '01', '2001-01-01', '北京', '北京八中', '北京', 303, '11111111111', '', '', '102', '汉语言文学艺术');

-- ----------------------------
-- Table structure for 学院专业表
-- ----------------------------
DROP TABLE IF EXISTS `学院专业表`;
CREATE TABLE `学院专业表`  (
  `专业编号` char(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `专业名称` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `学院编号` tinyint NULL DEFAULT NULL,
  `学院名称` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`专业编号`) USING BTREE,
  INDEX `学院专业表_学院编号_index`(`学院编号` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of 学院专业表
-- ----------------------------
INSERT INTO `学院专业表` VALUES ('100', '计算机科学与技术', 1, '计算机学院');
INSERT INTO `学院专业表` VALUES ('101', '金融学', 2, '经济管理学院');
INSERT INTO `学院专业表` VALUES ('102', '汉语言文学艺术', 3, '汉语言文学院');
INSERT INTO `学院专业表` VALUES ('待分配', '未分配专业', NULL, '新生院');

-- ----------------------------
-- Table structure for 成绩表
-- ----------------------------
DROP TABLE IF EXISTS `成绩表`;
CREATE TABLE `成绩表`  (
  `学号` char(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `课程编号` int NOT NULL,
  `成绩` tinyint NULL DEFAULT NULL,
  `是否补考` tinyint(1) NOT NULL DEFAULT 0,
  `补考成绩` int NULL DEFAULT NULL,
  `是否重修` tinyint(1) NOT NULL DEFAULT 0,
  `重修成绩` int NULL DEFAULT NULL,
  `学分` float NOT NULL,
  `绩点` float NULL DEFAULT NULL,
  PRIMARY KEY (`学号`, `课程编号`) USING BTREE,
  INDEX `成绩表_课程表_课程编号_fk`(`课程编号` ASC) USING BTREE,
  CONSTRAINT `成绩表_基本信息_学号_fk` FOREIGN KEY (`学号`) REFERENCES `基本信息` (`学号`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `成绩表_课程表_课程编号_fk` FOREIGN KEY (`课程编号`) REFERENCES `课程表` (`课程编号`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of 成绩表
-- ----------------------------
INSERT INTO `成绩表` VALUES ('202301001', 1001, 90, 0, NULL, 0, NULL, 4, 4);
INSERT INTO `成绩表` VALUES ('202301001', 1002, 85, 0, NULL, 0, NULL, 3.5, 3.7);
INSERT INTO `成绩表` VALUES ('202301001', 2001, 92, 0, NULL, 0, NULL, 4.2, 4.2);
INSERT INTO `成绩表` VALUES ('202301002', 1001, 78, 1, 82, 0, NULL, 3.2, 3);
INSERT INTO `成绩表` VALUES ('202301002', 2002, 95, 0, NULL, 0, NULL, 4.5, 4.5);
INSERT INTO `成绩表` VALUES ('202301003', 1001, 58, 1, 65, 0, NULL, 1.5, 1.5);

-- ----------------------------
-- Table structure for 毕业设计
-- ----------------------------
DROP TABLE IF EXISTS `毕业设计`;
CREATE TABLE `毕业设计`  (
  `学号` char(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `课题` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `指导老师` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `成绩` tinyint NOT NULL,
  PRIMARY KEY (`学号`) USING BTREE,
  CONSTRAINT `毕业设计_基本信息_学号_fk` FOREIGN KEY (`学号`) REFERENCES `基本信息` (`学号`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of 毕业设计
-- ----------------------------
INSERT INTO `毕业设计` VALUES ('202301001', '人工智能在金融风控中的应用', '刘教授', 92);
INSERT INTO `毕业设计` VALUES ('202301002', '金融市场波动性研究', '陈教授', 88);

-- ----------------------------
-- Table structure for 课程表
-- ----------------------------
DROP TABLE IF EXISTS `课程表`;
CREATE TABLE `课程表`  (
  `课程编号` int NOT NULL,
  `课程名称` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `任课教师` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`课程编号`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of 课程表
-- ----------------------------
INSERT INTO `课程表` VALUES (1001, '高等数学', '王教授');
INSERT INTO `课程表` VALUES (1002, '大学英语', '李老师');
INSERT INTO `课程表` VALUES (2001, '计算机基础', '张教授');
INSERT INTO `课程表` VALUES (2002, '微观经济学', '赵博士');

-- ----------------------------
-- Triggers structure for table 成绩表
-- ----------------------------
DROP TRIGGER IF EXISTS `calculate_credit_before_insert`;
delimiter ;;
CREATE TRIGGER `calculate_credit_before_insert` BEFORE INSERT ON `成绩表` FOR EACH ROW BEGIN
    DECLARE max_score INT;

    -- 在成绩和补考成绩之间取较大值
    SET max_score = GREATEST(IFNULL(NEW.成绩, 0), IFNULL(NEW.补考成绩, 0));

    -- 根据规则计算学分
    IF max_score >= 60 THEN
        SET NEW.学分 = (max_score / 10) - 5;
    ELSE
        SET NEW.学分 = 0;
    END IF;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table 成绩表
-- ----------------------------
DROP TRIGGER IF EXISTS `calculate_credit_before_update`;
delimiter ;;
CREATE TRIGGER `calculate_credit_before_update` BEFORE UPDATE ON `成绩表` FOR EACH ROW BEGIN
    DECLARE max_score INT;

    -- 检查是否尝试直接修改学分字段
    IF NEW.学分 != OLD.学分 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '不允许直接修改学分字段，学分由系统根据成绩自动计算';
    END IF;

    -- 在成绩和补考成绩之间取较大值
    SET max_score = GREATEST(IFNULL(NEW.成绩, 0), IFNULL(NEW.补考成绩, 0));

    -- 根据规则计算学分
    IF max_score >= 60 THEN
        SET NEW.学分 = (max_score / 10) - 5;
    ELSE
        SET NEW.学分 = 0;
    END IF;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;

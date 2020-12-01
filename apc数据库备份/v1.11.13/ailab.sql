/*
 Navicat Premium Data Transfer

 Source Server         : 192,168,156,27
 Source Server Type    : MySQL
 Source Server Version : 100411
 Source Host           : 192.168.156.27:3306
 Source Schema         : ailab

 Target Server Type    : MySQL
 Target Server Version : 100411
 File Encoding         : 65001

 Date: 27/11/2020 09:37:39
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for algorithmfilter
-- ----------------------------
DROP TABLE IF EXISTS `algorithmfilter`;
CREATE TABLE `algorithmfilter`  (
  `filterid` int(11) NOT NULL AUTO_INCREMENT,
  `filtername` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '滤波方法：mvav(移动平均)和fodl(一阶滤波)',
  `filteralphe` double NULL DEFAULT NULL COMMENT '一阶滤波参数\r\n',
  `filtertime` int(11) NULL DEFAULT NULL COMMENT '移动平均所用参数\r\n',
  `backtodcstag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '滤波反写的opc位号\r\n',
  `resource` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'opc位号来源',
  `referencepropertyid` int(11) NULL DEFAULT NULL COMMENT '引用的算法属性id',
  PRIMARY KEY (`filterid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of algorithmfilter
-- ----------------------------
INSERT INTO `algorithmfilter` VALUES (1, 'fodl', 0.1, NULL, 'DCS.APC.SPSB.SPSB_1', 'opc192.168.156.27', 3);
INSERT INTO `algorithmfilter` VALUES (2, 'fodl', 0.1, NULL, 'DCS.APC.SPSB.SPSB_2', 'opc192.168.156.27', 4);

-- ----------------------------
-- Table structure for algorithmmodle
-- ----------------------------
DROP TABLE IF EXISTS `algorithmmodle`;
CREATE TABLE `algorithmmodle`  (
  `modleid` int(11) NOT NULL AUTO_INCREMENT,
  `algorithmName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '算法名称',
  `updatetime` timestamp(0) NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`modleid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '算法模型(视频和大数据模型)' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of algorithmmodle
-- ----------------------------
INSERT INTO `algorithmmodle` VALUES (1, '1#生料皮带', '2020-09-10 14:22:29');
INSERT INTO `algorithmmodle` VALUES (2, '饱磨识别数据反写', '2020-11-27 08:00:02');

-- ----------------------------
-- Table structure for algorithmproperties
-- ----------------------------
DROP TABLE IF EXISTS `algorithmproperties`;
CREATE TABLE `algorithmproperties`  (
  `propertyid` int(11) NOT NULL AUTO_INCREMENT,
  `propertyName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '属性名称(ch-zh)',
  `property` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '属性',
  `refrencealgorithmid` int(11) NULL DEFAULT NULL COMMENT '引用的算法id',
  `resource` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'opc数据源',
  `opctag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '反写的opc位号',
  `datatype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '属性类型，value/image',
  PRIMARY KEY (`propertyid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '算法结果属性表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of algorithmproperties
-- ----------------------------
INSERT INTO `algorithmproperties` VALUES (1, '原始图片', 'origion', 1, '', '', 'image');
INSERT INTO `algorithmproperties` VALUES (2, '处理后', 'result', 1, '', '', 'image');
INSERT INTO `algorithmproperties` VALUES (3, '数量', 'stoneNum', 1, '', '', 'value');
INSERT INTO `algorithmproperties` VALUES (4, '面积', 'stoneArea', 1, '', '', 'value');
INSERT INTO `algorithmproperties` VALUES (5, '是否包磨', 'isfull', 2, 'opc192.168.156.27', 'DCS.APC.SPSB.SPSB_1', 'value');

-- ----------------------------
-- Table structure for company
-- ----------------------------
DROP TABLE IF EXISTS `company`;
CREATE TABLE `company`  (
  `companyId` int(11) NOT NULL AUTO_INCREMENT,
  `commenName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `companyOrder` int(11) NULL DEFAULT NULL,
  `ff` int(11) NULL DEFAULT 8,
  `mv` int(11) NULL DEFAULT 8,
  `pv` int(11) NULL DEFAULT 8,
  PRIMARY KEY (`companyId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of company
-- ----------------------------
INSERT INTO `company` VALUES (1, '兰州红狮', 1, 8, 8, 8);

-- ----------------------------
-- Table structure for company_copy1
-- ----------------------------
DROP TABLE IF EXISTS `company_copy1`;
CREATE TABLE `company_copy1`  (
  `companyId` int(11) NOT NULL AUTO_INCREMENT,
  `commenName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `companyOrder` int(11) NULL DEFAULT NULL,
  `ff` int(11) NULL DEFAULT 8,
  `mv` int(11) NULL DEFAULT 8,
  `pv` int(11) NULL DEFAULT 8,
  PRIMARY KEY (`companyId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of company_copy1
-- ----------------------------
INSERT INTO `company_copy1` VALUES (1, '兰州红狮', 1, 8, 8, 8);

-- ----------------------------
-- Table structure for filter
-- ----------------------------
DROP TABLE IF EXISTS `filter`;
CREATE TABLE `filter`  (
  `pk_filterid` int(11) NOT NULL AUTO_INCREMENT COMMENT '滤波器主键',
  `filtername` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '滤波方法：mvav(移动平均)和fodl(一阶滤波),nofilt无滤波',
  `filter_alphe` double NULL DEFAULT NULL COMMENT '一阶滤波系数',
  `filter_time` int(11) NULL DEFAULT NULL COMMENT '移动平均滤波时间',
  `pk_pinid` int(11) NULL DEFAULT NULL COMMENT '滤波器的引脚id主键',
  `backToDCSTag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '反写进dcs的位号',
  `opcresource` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`pk_filterid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1319 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of filter
-- ----------------------------
INSERT INTO `filter` VALUES (30, 'mvav', NULL, 60, 659, 'DCS.APC.YTP.A_YTP_PV', 'opc192.168.156.27');
INSERT INTO `filter` VALUES (341, 'fodl', 0.8, NULL, 2014, '', '');
INSERT INTO `filter` VALUES (342, 'fodl', 0.95, NULL, 2016, '', '');
INSERT INTO `filter` VALUES (343, 'fodl', 0.3, NULL, 2018, '', '');
INSERT INTO `filter` VALUES (399, 'fodl', 0.85, NULL, 2325, '', '');
INSERT INTO `filter` VALUES (478, 'fodl', 0.7, NULL, 2931, '', '');
INSERT INTO `filter` VALUES (890, 'fodl', 0.9, NULL, 4411, '', '');
INSERT INTO `filter` VALUES (891, 'fodl', 0.9, NULL, 4413, '', '');
INSERT INTO `filter` VALUES (1113, 'fodl', 0.85, NULL, 5981, '', '');
INSERT INTO `filter` VALUES (1114, 'fodl', 0.5, NULL, 5983, '', '');
INSERT INTO `filter` VALUES (1115, 'fodl', 0.3, NULL, 5985, '', '');
INSERT INTO `filter` VALUES (1116, 'fodl', 0.9, NULL, 5988, '', '');
INSERT INTO `filter` VALUES (1117, 'fodl', 0.9, NULL, 5992, '', '');
INSERT INTO `filter` VALUES (1118, 'fodl', 0.9, NULL, 5999, '', '');
INSERT INTO `filter` VALUES (1212, 'fodl', 0.9, NULL, 6942, '', '');
INSERT INTO `filter` VALUES (1213, 'fodl', 0.9, NULL, 6945, '', '');
INSERT INTO `filter` VALUES (1214, 'fodl', 0.9, NULL, 6956, '', '');
INSERT INTO `filter` VALUES (1230, 'fodl', 0.6, NULL, 7073, '', '');
INSERT INTO `filter` VALUES (1231, 'fodl', 0.4, NULL, 7076, '', '');
INSERT INTO `filter` VALUES (1252, 'fodl', 0.35, NULL, 7137, '', '');
INSERT INTO `filter` VALUES (1253, 'fodl', 0.6, NULL, 7139, '', '');
INSERT INTO `filter` VALUES (1254, 'fodl', 0.1, NULL, 7141, 'DCS.APC.SLM2.A_SLM2_PV6', 'opc192.168.156.27');
INSERT INTO `filter` VALUES (1255, 'fodl', 0.7, NULL, 7144, '', '');
INSERT INTO `filter` VALUES (1256, 'fodl', 0.6, NULL, 7147, '', '');
INSERT INTO `filter` VALUES (1257, 'fodl', 0.5, NULL, 7150, '', '');
INSERT INTO `filter` VALUES (1258, 'fodl', 0.5, NULL, 7153, 'DCS.APC.SLM2.SLM2_MV1', 'opc192.168.156.27');
INSERT INTO `filter` VALUES (1259, 'fodl', 0.6, NULL, 7163, '', '');
INSERT INTO `filter` VALUES (1278, 'fodl', 0.8, NULL, 7233, '', '');
INSERT INTO `filter` VALUES (1279, 'fodl', 0.9, NULL, 7235, '', '');
INSERT INTO `filter` VALUES (1280, 'fodl', 0.7, NULL, 7237, '', '');
INSERT INTO `filter` VALUES (1281, 'fodl', 0.6, NULL, 7244, '', '');
INSERT INTO `filter` VALUES (1282, 'fodl', 0.9, NULL, 7246, '', '');
INSERT INTO `filter` VALUES (1283, 'fodl', 0.65, NULL, 7248, '', '');
INSERT INTO `filter` VALUES (1284, 'fodl', 0.3, NULL, 7255, '', '');
INSERT INTO `filter` VALUES (1285, 'fodl', 0.7, NULL, 7257, '', '');
INSERT INTO `filter` VALUES (1286, 'fodl', 0.1, NULL, 7259, 'DCS.APC.SLM1.A_SLM1_PV6', 'opc192.168.156.27');
INSERT INTO `filter` VALUES (1287, 'fodl', 0.2, NULL, 7261, '', '');
INSERT INTO `filter` VALUES (1288, 'fodl', 0.8, NULL, 7264, '', '');
INSERT INTO `filter` VALUES (1289, 'fodl', 0.3, NULL, 7267, '', '');
INSERT INTO `filter` VALUES (1290, 'fodl', 0.2, NULL, 7270, '', '');
INSERT INTO `filter` VALUES (1291, 'fodl', 0.3, NULL, 7274, '', '');
INSERT INTO `filter` VALUES (1292, 'fodl', 0.7, NULL, 7276, '', '');
INSERT INTO `filter` VALUES (1293, 'fodl', 0.1, NULL, 7278, 'DCS.APC.SLM1.A_SLM1_PV6', 'opc192.168.156.27');
INSERT INTO `filter` VALUES (1294, 'fodl', 0.2, NULL, 7280, '', '');
INSERT INTO `filter` VALUES (1295, 'fodl', 0.8, NULL, 7283, '', '');
INSERT INTO `filter` VALUES (1296, 'fodl', 0.3, NULL, 7286, '', '');
INSERT INTO `filter` VALUES (1297, 'fodl', 0.2, NULL, 7289, '', '');
INSERT INTO `filter` VALUES (1298, 'fodl', 0.3, NULL, 7293, '', '');
INSERT INTO `filter` VALUES (1299, 'fodl', 0.7, NULL, 7295, '', '');
INSERT INTO `filter` VALUES (1300, 'fodl', 0.1, NULL, 7297, 'DCS.APC.SLM1.A_SLM1_PV6', 'opc192.168.156.27');
INSERT INTO `filter` VALUES (1301, 'fodl', 0.2, NULL, 7299, '', '');
INSERT INTO `filter` VALUES (1302, 'fodl', 0.8, NULL, 7302, '', '');
INSERT INTO `filter` VALUES (1303, 'fodl', 0.3, NULL, 7305, '', '');
INSERT INTO `filter` VALUES (1304, 'fodl', 0.2, NULL, 7308, '', '');
INSERT INTO `filter` VALUES (1305, 'fodl', 0.3, NULL, 7312, '', '');
INSERT INTO `filter` VALUES (1306, 'fodl', 0.7, NULL, 7314, '', '');
INSERT INTO `filter` VALUES (1307, 'fodl', 0.1, NULL, 7316, 'DCS.APC.SLM1.A_SLM1_PV6', 'opc192.168.156.27');
INSERT INTO `filter` VALUES (1308, 'fodl', 0.2, NULL, 7318, '', '');
INSERT INTO `filter` VALUES (1309, 'fodl', 0.8, NULL, 7321, '', '');
INSERT INTO `filter` VALUES (1310, 'fodl', 0.3, NULL, 7324, '', '');
INSERT INTO `filter` VALUES (1311, 'fodl', 0.2, NULL, 7327, '', '');
INSERT INTO `filter` VALUES (1312, 'fodl', 0.3, NULL, 7331, '', '');
INSERT INTO `filter` VALUES (1313, 'fodl', 0.7, NULL, 7333, '', '');
INSERT INTO `filter` VALUES (1314, 'fodl', 0.1, NULL, 7335, 'DCS.APC.SLM1.A_SLM1_PV6', 'opc192.168.156.27');
INSERT INTO `filter` VALUES (1315, 'fodl', 0.2, NULL, 7337, '', '');
INSERT INTO `filter` VALUES (1316, 'fodl', 0.8, NULL, 7340, '', '');
INSERT INTO `filter` VALUES (1317, 'fodl', 0.3, NULL, 7343, '', '');
INSERT INTO `filter` VALUES (1318, 'fodl', 0.2, NULL, 7346, '', '');

-- ----------------------------
-- Table structure for filter_copy1
-- ----------------------------
DROP TABLE IF EXISTS `filter_copy1`;
CREATE TABLE `filter_copy1`  (
  `pk_filterid` int(11) NOT NULL AUTO_INCREMENT COMMENT '滤波器主键',
  `filtername` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '滤波方法：mvav(移动平均)和fodl(一阶滤波),nofilt无滤波',
  `filter_alphe` double NULL DEFAULT NULL COMMENT '一阶滤波系数',
  `filter_time` int(11) NULL DEFAULT NULL COMMENT '移动平均滤波时间',
  `pk_pinid` int(11) NULL DEFAULT NULL COMMENT '滤波器的引脚id主键',
  `backToDCSTag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '反写进dcs的位号',
  `opcresource` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`pk_filterid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 154 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of filter_copy1
-- ----------------------------
INSERT INTO `filter_copy1` VALUES (30, 'mvav', NULL, 60, 659, 'DCS.APC.YTP.A_YTP_PV', 'opc192.168.156.27');
INSERT INTO `filter_copy1` VALUES (127, 'fodl', 0.7, NULL, 1134, '', '');
INSERT INTO `filter_copy1` VALUES (128, 'fodl', 0.95, NULL, 1136, '', '');
INSERT INTO `filter_copy1` VALUES (129, 'fodl', 0.3, NULL, 1138, '', '');
INSERT INTO `filter_copy1` VALUES (150, 'fodl', 0.7, NULL, 1255, '', '');
INSERT INTO `filter_copy1` VALUES (151, 'fodl', 0.4, NULL, 1257, '', '');
INSERT INTO `filter_copy1` VALUES (152, 'fodl', 0.3, NULL, 1259, '', '');
INSERT INTO `filter_copy1` VALUES (153, 'fodl', 0.9, NULL, 1273, '', '');

-- ----------------------------
-- Table structure for filter_copy2
-- ----------------------------
DROP TABLE IF EXISTS `filter_copy2`;
CREATE TABLE `filter_copy2`  (
  `pk_filterid` int(11) NOT NULL AUTO_INCREMENT COMMENT '滤波器主键',
  `filtername` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '滤波方法：mvav(移动平均)和fodl(一阶滤波),nofilt无滤波',
  `filter_alphe` double NULL DEFAULT NULL COMMENT '一阶滤波系数',
  `filter_time` int(11) NULL DEFAULT NULL COMMENT '移动平均滤波时间',
  `pk_pinid` int(11) NULL DEFAULT NULL COMMENT '滤波器的引脚id主键',
  `backToDCSTag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '反写进dcs的位号',
  `opcresource` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`pk_filterid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 995 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of filter_copy2
-- ----------------------------
INSERT INTO `filter_copy2` VALUES (30, 'mvav', NULL, 60, 659, 'DCS.APC.YTP.A_YTP_PV', 'opc192.168.156.27');
INSERT INTO `filter_copy2` VALUES (341, 'fodl', 0.8, NULL, 2014, '', '');
INSERT INTO `filter_copy2` VALUES (342, 'fodl', 0.95, NULL, 2016, '', '');
INSERT INTO `filter_copy2` VALUES (343, 'fodl', 0.3, NULL, 2018, '', '');
INSERT INTO `filter_copy2` VALUES (356, 'fodl', 0.85, NULL, 2069, '', '');
INSERT INTO `filter_copy2` VALUES (357, 'fodl', 0.5, NULL, 2071, '', '');
INSERT INTO `filter_copy2` VALUES (358, 'fodl', 0.3, NULL, 2073, '', '');
INSERT INTO `filter_copy2` VALUES (359, 'fodl', 0.9, NULL, 2076, '', '');
INSERT INTO `filter_copy2` VALUES (360, 'fodl', 0.9, NULL, 2080, '', '');
INSERT INTO `filter_copy2` VALUES (361, 'fodl', 0.9, NULL, 2087, '', '');
INSERT INTO `filter_copy2` VALUES (399, 'fodl', 0.85, NULL, 2325, '', '');
INSERT INTO `filter_copy2` VALUES (478, 'fodl', 0.7, NULL, 2931, '', '');
INSERT INTO `filter_copy2` VALUES (618, 'fodl', 0.6, NULL, 3408, '', '');
INSERT INTO `filter_copy2` VALUES (737, 'fodl', 0.4, NULL, 3752, '', '');
INSERT INTO `filter_copy2` VALUES (807, 'fodl', 0.3, NULL, 3949, '', 'opc192.168.156.27');
INSERT INTO `filter_copy2` VALUES (808, 'fodl', 0.7, NULL, 3951, '', '');
INSERT INTO `filter_copy2` VALUES (809, 'fodl', 0.1, NULL, 3953, '', '');
INSERT INTO `filter_copy2` VALUES (810, 'fodl', 0.2, NULL, 3955, '', '');
INSERT INTO `filter_copy2` VALUES (811, 'fodl', 0.8, NULL, 3958, '', '');
INSERT INTO `filter_copy2` VALUES (812, 'fodl', 0.3, NULL, 3961, '', '');
INSERT INTO `filter_copy2` VALUES (813, 'fodl', 0.2, NULL, 3964, '', '');
INSERT INTO `filter_copy2` VALUES (851, 'fodl', 0.8, NULL, 4179, '', '');
INSERT INTO `filter_copy2` VALUES (852, 'fodl', 0.9, NULL, 4181, '', '');
INSERT INTO `filter_copy2` VALUES (853, 'fodl', 0.7, NULL, 4183, '', '');
INSERT INTO `filter_copy2` VALUES (890, 'fodl', 0.9, NULL, 4411, '', '');
INSERT INTO `filter_copy2` VALUES (891, 'fodl', 0.9, NULL, 4413, '', '');
INSERT INTO `filter_copy2` VALUES (897, 'fodl', 0.6, NULL, 4456, '', '');
INSERT INTO `filter_copy2` VALUES (898, 'fodl', 0.9, NULL, 4458, '', '');
INSERT INTO `filter_copy2` VALUES (899, 'fodl', 0.65, NULL, 4460, '', '');
INSERT INTO `filter_copy2` VALUES (946, 'fodl', 0.35, NULL, 4824, '', '');
INSERT INTO `filter_copy2` VALUES (947, 'fodl', 0.6, NULL, 4826, '', '');
INSERT INTO `filter_copy2` VALUES (948, 'fodl', 0.15, NULL, 4828, '', '');
INSERT INTO `filter_copy2` VALUES (949, 'fodl', 0.7, NULL, 4831, '', '');
INSERT INTO `filter_copy2` VALUES (950, 'fodl', 0.6, NULL, 4834, '', '');
INSERT INTO `filter_copy2` VALUES (951, 'fodl', 0.5, NULL, 4837, '', '');
INSERT INTO `filter_copy2` VALUES (992, 'fodl', 0.9, NULL, 5137, '', '');
INSERT INTO `filter_copy2` VALUES (993, 'fodl', 0.9, NULL, 5140, '', '');
INSERT INTO `filter_copy2` VALUES (994, 'fodl', 0.9, NULL, 5155, '', '');

-- ----------------------------
-- Table structure for modle
-- ----------------------------
DROP TABLE IF EXISTS `modle`;
CREATE TABLE `modle`  (
  `modleId` int(11) NOT NULL AUTO_INCREMENT,
  `modleName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `controlAPCOutCycle` int(11) NULL DEFAULT NULL,
  `predicttime_P` int(11) NULL DEFAULT NULL,
  `timeserise_N` int(11) NULL DEFAULT NULL,
  `controltime_M` int(11) NULL DEFAULT NULL,
  `modleEnable` int(11) NULL DEFAULT NULL,
  `runstyle` int(11) NULL DEFAULT 0,
  PRIMARY KEY (`modleId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of modle
-- ----------------------------
INSERT INTO `modle` VALUES (1, '脱销控制模块', 25, 80, 90, 2, 1, 0);
INSERT INTO `modle` VALUES (2, '头排转速控制', 10, 65, 80, 2, 1, 0);
INSERT INTO `modle` VALUES (3, '余热锅炉水位', 20, 80, 120, 2, 1, 0);
INSERT INTO `modle` VALUES (4, '余热锅炉给水', 20, 60, 100, 2, 1, 0);
INSERT INTO `modle` VALUES (5, '生料磨1#产量', 10, 80, 120, 2, 0, 0);
INSERT INTO `modle` VALUES (6, '生料磨1#温度', 10, 70, 90, 2, 1, 0);
INSERT INTO `modle` VALUES (7, '生料磨1#风量', 20, 40, 60, 2, 0, 0);
INSERT INTO `modle` VALUES (8, '生料磨2#产量', 10, 80, 120, 2, 0, 0);
INSERT INTO `modle` VALUES (9, '生料磨2#温度', 15, 70, 90, 2, 0, 0);
INSERT INTO `modle` VALUES (10, '生料磨2#风量', 15, 70, 80, 2, 0, 0);
INSERT INTO `modle` VALUES (11, '煤磨1产量控制', 15, 80, 100, 2, 0, 0);
INSERT INTO `modle` VALUES (12, '煤磨温度控制', 12, 70, 90, 2, 1, 0);
INSERT INTO `modle` VALUES (13, '煤磨2产量控制', 10, 80, 100, 2, 0, 0);
INSERT INTO `modle` VALUES (15, '煤磨引风机控制', 12, 70, 90, 2, 0, 0);

-- ----------------------------
-- Table structure for modle_copy1
-- ----------------------------
DROP TABLE IF EXISTS `modle_copy1`;
CREATE TABLE `modle_copy1`  (
  `modleId` int(11) NOT NULL AUTO_INCREMENT,
  `modleName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `controlAPCOutCycle` int(11) NULL DEFAULT NULL,
  `predicttime_P` int(11) NULL DEFAULT NULL,
  `timeserise_N` int(11) NULL DEFAULT NULL,
  `controltime_M` int(11) NULL DEFAULT NULL,
  `modleEnable` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`modleId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of modle_copy1
-- ----------------------------
INSERT INTO `modle_copy1` VALUES (1, '脱销控制模块', 25, 80, 90, 2, 0);
INSERT INTO `modle_copy1` VALUES (2, '头排转速控制', 10, 65, 80, 2, 0);
INSERT INTO `modle_copy1` VALUES (3, '余热锅炉水位', 20, 80, 120, 2, 1);
INSERT INTO `modle_copy1` VALUES (4, '余热锅炉给水', 20, 60, 100, 2, 1);

-- ----------------------------
-- Table structure for modle_copy2
-- ----------------------------
DROP TABLE IF EXISTS `modle_copy2`;
CREATE TABLE `modle_copy2`  (
  `modleId` int(11) NOT NULL AUTO_INCREMENT,
  `modleName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `controlAPCOutCycle` int(11) NULL DEFAULT NULL,
  `predicttime_P` int(11) NULL DEFAULT NULL,
  `timeserise_N` int(11) NULL DEFAULT NULL,
  `controltime_M` int(11) NULL DEFAULT NULL,
  `modleEnable` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`modleId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of modle_copy2
-- ----------------------------
INSERT INTO `modle_copy2` VALUES (1, '脱销控制模块', 25, 80, 90, 2, 0);
INSERT INTO `modle_copy2` VALUES (2, '头排转速控制', 10, 65, 80, 2, 1);
INSERT INTO `modle_copy2` VALUES (3, '余热锅炉水位', 20, 80, 120, 2, 0);
INSERT INTO `modle_copy2` VALUES (4, '余热锅炉给水', 20, 60, 100, 2, 1);
INSERT INTO `modle_copy2` VALUES (5, '生料磨1#产量', 10, 80, 120, 2, 1);
INSERT INTO `modle_copy2` VALUES (6, '生料磨1#温度', 10, 70, 90, 2, 1);
INSERT INTO `modle_copy2` VALUES (7, '生料磨1#风量', 20, 40, 60, 2, 0);
INSERT INTO `modle_copy2` VALUES (8, '生料磨2#产量', 10, 80, 120, 2, 0);
INSERT INTO `modle_copy2` VALUES (9, '生料磨2#温度', 10, 70, 90, 2, 0);
INSERT INTO `modle_copy2` VALUES (10, '生料磨2#风量', 15, 70, 80, 2, 0);
INSERT INTO `modle_copy2` VALUES (11, '煤磨1产量控制', 15, 80, 100, 2, 1);
INSERT INTO `modle_copy2` VALUES (12, '煤磨温度控制', 12, 70, 90, 2, 0);
INSERT INTO `modle_copy2` VALUES (13, '煤磨2产量控制', 10, 80, 100, 2, 0);

-- ----------------------------
-- Table structure for modle_copy3
-- ----------------------------
DROP TABLE IF EXISTS `modle_copy3`;
CREATE TABLE `modle_copy3`  (
  `modleId` int(11) NOT NULL AUTO_INCREMENT,
  `modleName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `controlAPCOutCycle` int(11) NULL DEFAULT NULL,
  `predicttime_P` int(11) NULL DEFAULT NULL,
  `timeserise_N` int(11) NULL DEFAULT NULL,
  `controltime_M` int(11) NULL DEFAULT NULL,
  `modleEnable` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`modleId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of modle_copy3
-- ----------------------------
INSERT INTO `modle_copy3` VALUES (1, '脱销控制模块', 25, 80, 90, 2, 1);
INSERT INTO `modle_copy3` VALUES (2, '头排转速控制', 10, 65, 80, 2, 1);
INSERT INTO `modle_copy3` VALUES (3, '余热锅炉水位', 20, 80, 120, 2, 1);
INSERT INTO `modle_copy3` VALUES (4, '余热锅炉给水', 20, 60, 100, 2, 1);
INSERT INTO `modle_copy3` VALUES (5, '生料磨1#产量', 10, 80, 120, 2, 1);
INSERT INTO `modle_copy3` VALUES (6, '生料磨1#温度', 10, 70, 90, 2, 0);
INSERT INTO `modle_copy3` VALUES (7, '生料磨1#风量', 20, 40, 60, 2, 0);
INSERT INTO `modle_copy3` VALUES (8, '生料磨2#产量', 10, 80, 120, 2, 0);
INSERT INTO `modle_copy3` VALUES (9, '生料磨2#温度', 10, 70, 90, 2, 0);
INSERT INTO `modle_copy3` VALUES (10, '生料磨2#风量', 15, 70, 80, 2, 0);
INSERT INTO `modle_copy3` VALUES (11, '煤磨1产量控制', 15, 80, 100, 2, 1);
INSERT INTO `modle_copy3` VALUES (12, '煤磨温度控制', 12, 70, 90, 2, 1);
INSERT INTO `modle_copy3` VALUES (13, '煤磨2产量控制', 10, 80, 100, 2, 1);

-- ----------------------------
-- Table structure for modlepins
-- ----------------------------
DROP TABLE IF EXISTS `modlepins`;
CREATE TABLE `modlepins`  (
  `modlepinsId` int(11) NOT NULL AUTO_INCREMENT,
  `reference_modleId` int(11) NULL DEFAULT NULL,
  `modleOpcTag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '\'NULL\'',
  `filterMethod` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `modlePinName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '\'NULL\'',
  `opcTagName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'NULL',
  `resource` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Q` double NULL DEFAULT NULL,
  `dmvHigh` double NULL DEFAULT NULL,
  `deadZone` double NULL DEFAULT NULL,
  `funelinitValue` double NULL DEFAULT NULL,
  `R` double NULL DEFAULT NULL,
  `dmvLow` double NULL DEFAULT NULL,
  `referTrajectoryCoef` double NULL DEFAULT NULL,
  `funneltype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `pinEnable` int(11) NULL DEFAULT 1,
  `updateTime` timestamp(0) NULL DEFAULT current_timestamp(),
  `pintype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `tracoefmethod` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`modlepinsId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7349 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of modlepins
-- ----------------------------
INSERT INTO `modlepins` VALUES (658, 2, 'DCS.APC.YTP.A_YTP_AUTO', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43', 'auto', NULL);
INSERT INTO `modlepins` VALUES (659, 2, 'DCS.APC.YTP.PT5701P01', NULL, 'pv1', NULL, 'opc192.168.156.27', 0.005, NULL, 5, 5, NULL, NULL, 0.8, 'fullfunnel', 1, '2020-08-18 08:09:43', 'pv', 'before');
INSERT INTO `modlepins` VALUES (660, 2, 'DCS.APC.YTP.A_YTP_SP', NULL, 'sp1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43', 'sp', NULL);
INSERT INTO `modlepins` VALUES (661, 2, 'DCS.APC.YTP.A_YTP_MV', NULL, 'mv1', NULL, 'opc192.168.156.27', NULL, 2, NULL, NULL, 10, 0.01, NULL, NULL, 1, '2020-08-18 08:09:43', 'mv', NULL);
INSERT INTO `modlepins` VALUES (662, 2, 'DCS.APC.YTP.A_YTP_MV', NULL, 'mvfb1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43', 'mvfb', NULL);
INSERT INTO `modlepins` VALUES (663, 2, 'DCS.APC.YTP.A_YTP_HI', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43', 'mvup', NULL);
INSERT INTO `modlepins` VALUES (664, 2, 'DCS.APC.YTP.A_YTP_LO', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43', 'mvdown', NULL);
INSERT INTO `modlepins` VALUES (2013, 4, 'DCS.YR.A_GSB_AM', NULL, 'auto', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43', 'auto', NULL);
INSERT INTO `modlepins` VALUES (2014, 4, 'DCS.YR.A_GSB_PV1', NULL, 'pv1', NULL, 'opc128.128.2.140', 0.001, NULL, 10, 10, NULL, NULL, 0.7, 'fullfunnel', 1, '2020-08-18 08:09:43', 'pv', 'before');
INSERT INTO `modlepins` VALUES (2015, 4, 'DCS.YR.A_GSB_SV1', NULL, 'sp1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43', 'sp', NULL);
INSERT INTO `modlepins` VALUES (2016, 4, 'DCS.YR.A_GSB_PV2', NULL, 'pv2', NULL, 'opc128.128.2.140', 0.002, NULL, 1, 1, NULL, NULL, 0.7, 'fullfunnel', 1, '2020-08-18 08:09:43', 'pv', 'before');
INSERT INTO `modlepins` VALUES (2017, 4, 'DCS.YR.A_GSB_SV2', NULL, 'sp2', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43', 'sp', NULL);
INSERT INTO `modlepins` VALUES (2018, 4, 'DCS.YR.A_SP_PV', NULL, 'pv3', NULL, 'opc128.128.2.140', 0.0015, NULL, 25, 15, NULL, NULL, 0.8, 'fullfunnel', 1, '2020-08-18 08:09:43', 'pv', 'before');
INSERT INTO `modlepins` VALUES (2019, 4, 'DCS.YR.A_SP_SV', NULL, 'sp3', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43', 'sp', NULL);
INSERT INTO `modlepins` VALUES (2020, 4, 'DCS.YR.A_GSB_MV', NULL, 'mv1', NULL, 'opc128.128.2.140', NULL, 0.2, NULL, NULL, 25, 0.01, NULL, NULL, 1, '2020-08-18 08:09:43', 'mv', NULL);
INSERT INTO `modlepins` VALUES (2021, 4, 'DCS.YR.A_GSB_MV', NULL, 'mvfb1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43', 'mvfb', NULL);
INSERT INTO `modlepins` VALUES (2022, 4, 'DCS.YR.A_GSB_HI', NULL, 'mvup1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43', 'mvup', NULL);
INSERT INTO `modlepins` VALUES (2023, 4, 'DCS.YR.A_GSB_LO', NULL, 'mvdown1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43', 'mvdown', NULL);
INSERT INTO `modlepins` VALUES (2324, 7, 'DCS.APC.SLM1.A_SLM1_AM3', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43', 'auto', NULL);
INSERT INTO `modlepins` VALUES (2325, 7, 'DCS.APC.SLM1.A_SLM1_PV5', NULL, 'pv1', NULL, 'opc192.168.156.27', 0.001, NULL, 0.2, 0.4, NULL, NULL, 0.8, 'fullfunnel', 1, '2020-08-18 08:09:43', 'pv', 'before');
INSERT INTO `modlepins` VALUES (2326, 7, 'DCS.APC.SLM1.A_SLM1_SV5', NULL, 'sp1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43', 'sp', NULL);
INSERT INTO `modlepins` VALUES (2327, 7, 'DCS.APC.SLM1.A_SLM1_MV3', NULL, 'mv1', NULL, 'opc192.168.156.27', NULL, 2, NULL, NULL, 30, 0.5, NULL, NULL, 1, '2020-08-18 08:09:43', 'mv', NULL);
INSERT INTO `modlepins` VALUES (2328, 7, 'DCS.APC.SLM1.A_SLM1_MV3', NULL, 'mvfb1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43', 'mvfb', NULL);
INSERT INTO `modlepins` VALUES (2329, 7, 'DCS.APC.SLM1.A_SLM1_HI3', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43', 'mvup', NULL);
INSERT INTO `modlepins` VALUES (2330, 7, 'DCS.APC.SLM1.A_SLM1_LO3', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43', 'mvdown', NULL);
INSERT INTO `modlepins` VALUES (2930, 10, 'DCS.APC.SLM2.A_SLM2_AM3', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43', 'auto', NULL);
INSERT INTO `modlepins` VALUES (2931, 10, 'DCS.APC.SLM2.A_SLM2_PV5', NULL, 'pv1', NULL, 'opc192.168.156.27', 0.01, NULL, 0.5, 0.5, NULL, NULL, 0.7, 'fullfunnel', 1, '2020-08-18 08:09:43', 'pv', 'before');
INSERT INTO `modlepins` VALUES (2932, 10, 'DCS.APC.SLM2.A_SLM2_SV5', NULL, 'sp1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43', 'sp', NULL);
INSERT INTO `modlepins` VALUES (2933, 10, 'DCS.APC.SLM2.A_SLM2_MV3', NULL, 'mv1', NULL, 'opc192.168.156.27', NULL, 2, NULL, NULL, 5, 0.2, NULL, NULL, 1, '2020-08-18 08:09:43', 'mv', NULL);
INSERT INTO `modlepins` VALUES (2934, 10, 'DCS.APC.SLM2.A_SLM2_MV3', NULL, 'mvfb1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43', 'mvfb', NULL);
INSERT INTO `modlepins` VALUES (2935, 10, 'DCS.APC.SLM2.A_SLM2_HI3', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43', 'mvup', NULL);
INSERT INTO `modlepins` VALUES (2936, 10, 'DCS.APC.SLM2.A_SLM2_LO3', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43', 'mvdown', NULL);
INSERT INTO `modlepins` VALUES (3967, 1, 'DCS.APC.TX.A_TX_AUTO', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43', 'auto', NULL);
INSERT INTO `modlepins` VALUES (3968, 1, 'DCS.APC.TX.NOX_LB', NULL, 'pv1', 'NOX', 'opc192.168.156.27', 0.0003, NULL, 2, 2, NULL, NULL, 0.8, 'fullfunnel', 1, '2020-08-18 08:09:43', 'pv', 'before');
INSERT INTO `modlepins` VALUES (3969, 1, 'DCS.APC.TX.NOX_SV', NULL, 'sp1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43', 'sp', NULL);
INSERT INTO `modlepins` VALUES (3970, 1, 'DCS.APC.TX.A_TX_MV', NULL, 'mv1', '氨水流量', 'opc192.168.156.27', NULL, 0.1, NULL, NULL, 60, 0.005, NULL, NULL, 1, '2020-08-18 08:09:43', 'mv', NULL);
INSERT INTO `modlepins` VALUES (3971, 1, 'DCS.APC.TX.SNCR_LI', NULL, 'mvfb1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43', 'mvfb', NULL);
INSERT INTO `modlepins` VALUES (3972, 1, 'DCS.APC.TX.A_TX_HI', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43', 'mvup', NULL);
INSERT INTO `modlepins` VALUES (3973, 1, 'DCS.APC.TX.A_TX_LO', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43', 'mvdown', NULL);
INSERT INTO `modlepins` VALUES (5980, 3, 'DCS.YR.A_AUTO', NULL, 'auto', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33', 'auto', NULL);
INSERT INTO `modlepins` VALUES (5981, 3, 'DCS.YR.A_AQCH_PV', NULL, 'pv1', '', 'opc128.128.2.140', 0.008, NULL, 3, 8, NULL, NULL, 0.1, 'fullfunnel', 1, '2020-08-20 08:47:33', 'pv', 'before');
INSERT INTO `modlepins` VALUES (5982, 3, 'DCS.YR.A_AQCH_SV', NULL, 'sp1', '', 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33', 'sp', NULL);
INSERT INTO `modlepins` VALUES (5983, 3, 'DCS.YR.A_AQCL_PV', NULL, 'pv2', '', 'opc128.128.2.140', 0.0008, NULL, 5, 7, NULL, NULL, 0.72, 'fullfunnel', 1, '2020-08-20 08:47:33', 'pv', 'before');
INSERT INTO `modlepins` VALUES (5984, 3, 'DCS.YR.A_AQCL_SV', NULL, 'sp2', '', 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33', 'sp', NULL);
INSERT INTO `modlepins` VALUES (5985, 3, 'DCS.YR.A_SP_PV', NULL, 'pv3', '', 'opc128.128.2.140', 0.00003, NULL, 25, 25, NULL, NULL, 0.85, 'fullfunnel', 1, '2020-08-20 08:47:33', 'pv', 'before');
INSERT INTO `modlepins` VALUES (5986, 3, 'DCS.YR.A_SP_SV', NULL, 'sp3', '', 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33', 'sp', NULL);
INSERT INTO `modlepins` VALUES (5987, 3, 'DCS.YR.A_AQCH_MV', NULL, 'mv1', '', 'opc128.128.2.140', NULL, 2, NULL, NULL, 40, 0.05, NULL, NULL, 1, '2020-08-20 08:47:33', 'mv', NULL);
INSERT INTO `modlepins` VALUES (5988, 3, 'DCS.YR.A_AQCH_MV', NULL, 'mvfb1', '', 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33', 'mvfb', NULL);
INSERT INTO `modlepins` VALUES (5989, 3, 'DCS.YR.A_AQCH_HI', NULL, 'mvup1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33', 'mvup', NULL);
INSERT INTO `modlepins` VALUES (5990, 3, 'DCS.YR.A_AQCH_LO', NULL, 'mvdown1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33', 'mvdown', NULL);
INSERT INTO `modlepins` VALUES (5991, 3, 'DCS.YR.A_AQCL_MV', NULL, 'mv2', '', 'opc128.128.2.140', NULL, 1, NULL, NULL, 50, 0.05, NULL, NULL, 1, '2020-08-20 08:47:33', 'mv', NULL);
INSERT INTO `modlepins` VALUES (5992, 3, 'DCS.YR.A_AQCL_MV', NULL, 'mvfb2', '', 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33', 'mvfb', NULL);
INSERT INTO `modlepins` VALUES (5993, 3, 'DCS.YR.A_AQCL_HI', NULL, 'mvup2', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33', 'mvup', NULL);
INSERT INTO `modlepins` VALUES (5994, 3, 'DCS.YR.A_AQCL_LO', NULL, 'mvdown2', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33', 'mvdown', NULL);
INSERT INTO `modlepins` VALUES (5995, 3, 'DCS.YR.A_SP_MV', NULL, 'mv3', '', 'opc128.128.2.140', NULL, 1, NULL, NULL, 50, 0.1, NULL, NULL, 1, '2020-08-20 08:47:33', 'mv', NULL);
INSERT INTO `modlepins` VALUES (5996, 3, 'DCS.YR.A_SP_MV', NULL, 'mvfb3', '', 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33', 'mvfb', NULL);
INSERT INTO `modlepins` VALUES (5997, 3, 'DCS.YR.A_SP_HI', NULL, 'mvup3', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33', 'mvup', NULL);
INSERT INTO `modlepins` VALUES (5998, 3, 'DCS.YR.A_SP_LO', NULL, 'mvdown3', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33', 'mvdown', NULL);
INSERT INTO `modlepins` VALUES (5999, 3, 'DCS.YR.GAPT06', NULL, 'ff1', '', 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33', 'ff', NULL);
INSERT INTO `modlepins` VALUES (6000, 3, '0.77', NULL, 'ffup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33', 'ffup', NULL);
INSERT INTO `modlepins` VALUES (6001, 3, '0.63', NULL, 'ffdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33', 'ffdown', NULL);
INSERT INTO `modlepins` VALUES (6941, 12, '1', NULL, 'auto', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:31:21', 'auto', NULL);
INSERT INTO `modlepins` VALUES (6942, 12, 'DCS.APC.MM.A_MM1_PV2', NULL, 'pv1', '1#出磨温度', 'opc192.168.156.27', 1.2, NULL, 0.1, 0.3, NULL, NULL, 0.5, 'fullfunnel', 0, '2020-08-29 16:31:21', 'pv', 'before');
INSERT INTO `modlepins` VALUES (6943, 12, 'DCS.APC.MM.A_MM1_SV2', NULL, 'sp1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:31:21', 'sp', NULL);
INSERT INTO `modlepins` VALUES (6944, 12, 'DCS.APC.MM.A_MM1_AM2', NULL, 'pvenable1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:31:21', 'pvenable', NULL);
INSERT INTO `modlepins` VALUES (6945, 12, 'DCS.APC.MM.A_MM2_PV2', NULL, 'pv2', '2#出磨温度', 'opc192.168.156.27', 1.2, NULL, 0.1, 0.3, NULL, NULL, 0.5, 'fullfunnel', 0, '2020-08-29 16:31:21', 'pv', 'before');
INSERT INTO `modlepins` VALUES (6946, 12, 'DCS.APC.MM.A_MM2_SV2', NULL, 'sp2', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:31:21', 'sp', NULL);
INSERT INTO `modlepins` VALUES (6947, 12, 'DCS.APC.MM.A_MM2_AM2', NULL, 'pvenable2', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:31:21', 'pvenable', NULL);
INSERT INTO `modlepins` VALUES (6948, 12, 'DCS.APC.MM.A_MM1_MV2', NULL, 'mv1', '', 'opc192.168.156.27', NULL, 2, NULL, NULL, 4, 0.2, NULL, NULL, 1, '2020-08-29 16:31:21', 'mv', NULL);
INSERT INTO `modlepins` VALUES (6949, 12, 'DCS.APC.MM.A_MM1_MV2', NULL, 'mvfb1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:31:21', 'mvfb', NULL);
INSERT INTO `modlepins` VALUES (6950, 12, 'DCS.APC.MM.A_MM1_HI2', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:31:21', 'mvup', NULL);
INSERT INTO `modlepins` VALUES (6951, 12, 'DCS.APC.MM.A_MM1_LO2', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:31:21', 'mvdown', NULL);
INSERT INTO `modlepins` VALUES (6952, 12, 'DCS.APC.MM.A_MM2_MV2', NULL, 'mv2', '', 'opc192.168.156.27', NULL, 2, NULL, NULL, 4, 0.2, NULL, NULL, 1, '2020-08-29 16:31:21', 'mv', NULL);
INSERT INTO `modlepins` VALUES (6953, 12, 'DCS.APC.MM.A_MM2_MV2', NULL, 'mvfb2', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:31:21', 'mvfb', NULL);
INSERT INTO `modlepins` VALUES (6954, 12, 'DCS.APC.MM.A_MM2_HI2', NULL, 'mvup2', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:31:21', 'mvup', NULL);
INSERT INTO `modlepins` VALUES (6955, 12, 'DCS.APC.MM.A_MM2_LO2', NULL, 'mvdown2', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:31:21', 'mvdown', NULL);
INSERT INTO `modlepins` VALUES (6956, 12, 'DCS.APC.MM.A_MM_MV', NULL, 'ff1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:31:21', 'ff', NULL);
INSERT INTO `modlepins` VALUES (6957, 12, '25', NULL, 'ffup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:31:21', 'ffup', NULL);
INSERT INTO `modlepins` VALUES (6958, 12, '5', NULL, 'ffdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:31:21', 'ffdown', NULL);
INSERT INTO `modlepins` VALUES (6982, 15, 'DCS.APC.MM.A_MM_AM', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:51:09', 'auto', NULL);
INSERT INTO `modlepins` VALUES (6983, 15, 'DCS.APC.MM.A_MM1_PV2', NULL, 'pv1', '', 'opc192.168.156.27', 2.5, NULL, 0.6, 0.8, NULL, NULL, 0.6, 'fullfunnel', 0, '2020-08-29 16:51:09', 'pv', 'before');
INSERT INTO `modlepins` VALUES (6984, 15, 'DCS.APC.MM.A_MM1_SV2', NULL, 'sp1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:51:09', 'sp', NULL);
INSERT INTO `modlepins` VALUES (6985, 15, 'DCS.APC.MM.A_MM1_AM2', NULL, 'pvenable1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:51:09', 'pvenable', NULL);
INSERT INTO `modlepins` VALUES (6986, 15, 'DCS.APC.MM.A_MM2_PV2', NULL, 'pv2', '', 'opc192.168.156.27', 2.5, NULL, 0.6, 0.8, NULL, NULL, 0.6, 'fullfunnel', 0, '2020-08-29 16:51:09', 'pv', 'before');
INSERT INTO `modlepins` VALUES (6987, 15, 'DCS.APC.MM.A_MM2_SV2', NULL, 'sp2', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:51:09', 'sp', NULL);
INSERT INTO `modlepins` VALUES (6988, 15, 'DCS.APC.MM.A_MM2_AM2', NULL, 'pvenable2', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:51:09', 'pvenable', NULL);
INSERT INTO `modlepins` VALUES (6989, 15, 'DCS.APC.MM.A_MM1_MV2', NULL, 'pv3', '', 'opc192.168.156.27', 0.8, NULL, 1, 1, NULL, NULL, 0.5, 'upfunnel', 0, '2020-08-29 16:51:09', 'pv', 'before');
INSERT INTO `modlepins` VALUES (6990, 15, '17', NULL, 'sp3', '', 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:51:09', 'sp', NULL);
INSERT INTO `modlepins` VALUES (6991, 15, 'DCS.APC.MM.A_MM1_AM2', NULL, 'pvenable3', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:51:09', 'pvenable', NULL);
INSERT INTO `modlepins` VALUES (6992, 15, 'DCS.APC.MM.A_MM2_MV2', NULL, 'pv4', '', 'opc192.168.156.27', 0.8, NULL, 1, 1, NULL, NULL, 0.5, 'upfunnel', 0, '2020-08-29 16:51:09', 'pv', 'before');
INSERT INTO `modlepins` VALUES (6993, 15, '17', NULL, 'sp4', '', 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:51:09', 'sp', NULL);
INSERT INTO `modlepins` VALUES (6994, 15, 'DCS.APC.MM.A_MM2_AM2', NULL, 'pvenable4', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:51:09', 'pvenable', NULL);
INSERT INTO `modlepins` VALUES (6995, 15, 'DCS.APC.MM.A_MM1_MV2', NULL, 'pv5', '', 'opc192.168.156.27', 0.5, NULL, 1, 1, NULL, NULL, 0.5, 'downfunnel', 0, '2020-08-29 16:51:09', 'pv', 'before');
INSERT INTO `modlepins` VALUES (6996, 15, '3', NULL, 'sp5', '', 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:51:09', 'sp', NULL);
INSERT INTO `modlepins` VALUES (6997, 15, 'DCS.APC.MM.A_MM1_AM2', NULL, 'pvenable5', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:51:09', 'pvenable', NULL);
INSERT INTO `modlepins` VALUES (6998, 15, 'DCS.APC.MM.A_MM2_MV2', NULL, 'pv6', '', 'opc192.168.156.27', 0.5, NULL, 1, 1, NULL, NULL, 0.5, 'downfunnel', 0, '2020-08-29 16:51:09', 'pv', 'before');
INSERT INTO `modlepins` VALUES (6999, 15, '3', NULL, 'sp6', '', 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:51:09', 'sp', NULL);
INSERT INTO `modlepins` VALUES (7000, 15, 'DCS.APC.MM.A_MM2_AM2', NULL, 'pvenable6', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:51:09', 'pvenable', NULL);
INSERT INTO `modlepins` VALUES (7001, 15, 'DCS.APC.MM.A_MM_MV', NULL, 'mv1', '', 'opc192.168.156.27', NULL, 0.15, NULL, NULL, 30, 0.05, NULL, NULL, 1, '2020-08-29 16:51:09', 'mv', NULL);
INSERT INTO `modlepins` VALUES (7002, 15, 'DCS.APC.MM.A_MM_MV', NULL, 'mvfb1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:51:09', 'mvfb', NULL);
INSERT INTO `modlepins` VALUES (7003, 15, 'DCS.APC.MM.A_MM_HI', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:51:09', 'mvup', NULL);
INSERT INTO `modlepins` VALUES (7004, 15, 'DCS.APC.MM.A_MM_LO', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-29 16:51:09', 'mvdown', NULL);
INSERT INTO `modlepins` VALUES (7069, 9, 'DCS.APC.SLM2.A_SLM2_AM2', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-31 23:50:33', 'auto', NULL);
INSERT INTO `modlepins` VALUES (7070, 9, 'DCS.APC.SLM2.A_SLM2_PV3', NULL, 'pv1', '', 'opc192.168.156.27', 0.045, NULL, 0.3, 0.1, NULL, NULL, 0.6, 'fullfunnel', 1, '2020-08-31 23:50:33', 'pv', 'before');
INSERT INTO `modlepins` VALUES (7071, 9, 'DCS.APC.SLM2.A_SLM2_SV3', NULL, 'sp1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-31 23:50:33', 'sp', NULL);
INSERT INTO `modlepins` VALUES (7072, 9, 'DCS.APC.SLM2.A_SLM2_MV2', NULL, 'mv1', '', 'opc192.168.156.27', NULL, 0.2, NULL, NULL, 18, 0.01, NULL, NULL, 1, '2020-08-31 23:50:33', 'mv', NULL);
INSERT INTO `modlepins` VALUES (7073, 9, 'DCS.APC.SLM2.A_SLM2_MV2FB', NULL, 'mvfb1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-31 23:50:33', 'mvfb', NULL);
INSERT INTO `modlepins` VALUES (7074, 9, 'DCS.APC.SLM2.A_SLM2_HI2', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-31 23:50:33', 'mvup', NULL);
INSERT INTO `modlepins` VALUES (7075, 9, 'DCS.APC.SLM2.A_SLM2_LO2', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-31 23:50:33', 'mvdown', NULL);
INSERT INTO `modlepins` VALUES (7076, 9, 'DCS.APC.SLM2.F3518', NULL, 'ff1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-31 23:50:33', 'ff', NULL);
INSERT INTO `modlepins` VALUES (7077, 9, '50', NULL, 'ffup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-31 23:50:33', 'ffup', NULL);
INSERT INTO `modlepins` VALUES (7078, 9, '20', NULL, 'ffdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-31 23:50:33', 'ffdown', NULL);
INSERT INTO `modlepins` VALUES (7136, 8, 'DCS.APC.SLM2.A_SLM2_AM1', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-02 15:15:19', 'auto', NULL);
INSERT INTO `modlepins` VALUES (7137, 8, 'DCS.APC.SLM2.A_SLM2_PV1', NULL, 'pv1', '', 'opc192.168.156.27', 0.00005, NULL, 10, 40, NULL, NULL, 0.6, 'fullfunnel', 1, '2020-09-02 15:15:19', 'pv', 'before');
INSERT INTO `modlepins` VALUES (7138, 8, 'DCS.APC.SLM2.A_SLM2_SV1', NULL, 'sp1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-02 15:15:19', 'sp', NULL);
INSERT INTO `modlepins` VALUES (7139, 8, 'DCS.APC.SLM2.A_SLM2_PV2', NULL, 'pv2', '', 'opc192.168.156.27', 0.55, NULL, 2, 0.6, NULL, NULL, 0.8, 'upfunnel', 1, '2020-09-02 15:15:19', 'pv', 'before');
INSERT INTO `modlepins` VALUES (7140, 8, 'DCS.APC.SLM2.A_SLM2_SV2', NULL, 'sp2', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-02 15:15:19', 'sp', NULL);
INSERT INTO `modlepins` VALUES (7141, 8, 'DCS.APC.SLM2.M4112Y01', NULL, 'pv3', '', 'opc192.168.156.27', 0.24, NULL, 4, 1, NULL, NULL, 0.85, 'fullfunnel', 1, '2020-09-02 15:15:19', 'pv', 'before');
INSERT INTO `modlepins` VALUES (7142, 8, 'DCS.APC.SLM2.A_SLM2_SV6', NULL, 'sp3', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-02 15:15:19', 'sp', NULL);
INSERT INTO `modlepins` VALUES (7143, 8, 'DCS.APC.SLM2.A_SLM2_MV1', NULL, 'mv1', '', 'opc192.168.156.27', NULL, 3, NULL, NULL, 30, 0.1, NULL, NULL, 1, '2020-09-02 15:15:19', 'mv', NULL);
INSERT INTO `modlepins` VALUES (7144, 8, 'DCS.APC.SLM2.A_SLM2_MV1', NULL, 'mvfb1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-02 15:15:19', 'mvfb', NULL);
INSERT INTO `modlepins` VALUES (7145, 8, 'DCS.APC.SLM2.A_SLM2_HI1', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-02 15:15:19', 'mvup', NULL);
INSERT INTO `modlepins` VALUES (7146, 8, 'DCS.APC.SLM2.A_SLM2_LO1', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-02 15:15:19', 'mvdown', NULL);
INSERT INTO `modlepins` VALUES (7147, 8, 'DCS.APC.SLM2.A_SLM2_PV2', NULL, 'ff1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-02 15:15:19', 'ff', NULL);
INSERT INTO `modlepins` VALUES (7148, 8, '43', NULL, 'ffup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-02 15:15:19', 'ffup', NULL);
INSERT INTO `modlepins` VALUES (7149, 8, '39', NULL, 'ffdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-02 15:15:19', 'ffdown', NULL);
INSERT INTO `modlepins` VALUES (7150, 8, 'DCS.APC.SLM2.A_SLM2_PV5', NULL, 'ff2', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-02 15:15:19', 'ff', NULL);
INSERT INTO `modlepins` VALUES (7151, 8, '180', NULL, 'ffup2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-02 15:15:19', 'ffup', NULL);
INSERT INTO `modlepins` VALUES (7152, 8, '155', NULL, 'ffdown2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-02 15:15:19', 'ffdown', NULL);
INSERT INTO `modlepins` VALUES (7153, 8, 'DCS.APC.SLM2.A_SLM2_MV1', NULL, 'ff3', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-02 15:15:19', 'ff', NULL);
INSERT INTO `modlepins` VALUES (7154, 8, '500', NULL, 'ffup3', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-02 15:15:19', 'ffup', NULL);
INSERT INTO `modlepins` VALUES (7155, 8, '350', NULL, 'ffdown3', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-02 15:15:19', 'ffdown', NULL);
INSERT INTO `modlepins` VALUES (7156, 6, 'DCS.APC.SLM1.A_SLM1_AM2', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-09 07:55:05', 'auto', NULL);
INSERT INTO `modlepins` VALUES (7157, 6, 'DCS.APC.SLM1.A_SLM1_PV3', NULL, 'pv1', '', 'opc192.168.156.27', 0.5, NULL, 0.1, 0.1, NULL, NULL, 0.5, 'fullfunnel', 1, '2020-09-09 07:55:05', 'pv', 'before');
INSERT INTO `modlepins` VALUES (7158, 6, 'DCS.APC.SLM1.A_SLM1_SV3', NULL, 'sp1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-09 07:55:05', 'sp', NULL);
INSERT INTO `modlepins` VALUES (7159, 6, 'DCS.APC.SLM1.A_SLM1_MV2', NULL, 'mv1', '', 'opc192.168.156.27', NULL, 2, NULL, NULL, 18, 0.01, NULL, NULL, 1, '2020-09-09 07:55:05', 'mv', NULL);
INSERT INTO `modlepins` VALUES (7160, 6, 'DCS.APC.SLM1.A_SLM1_MV2', NULL, 'mvfb1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-09 07:55:05', 'mvfb', NULL);
INSERT INTO `modlepins` VALUES (7161, 6, 'DCS.APC.SLM1.A_SLM1_HI2', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-09 07:55:05', 'mvup', NULL);
INSERT INTO `modlepins` VALUES (7162, 6, 'DCS.APC.SLM1.A_SLM1_LO2', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-09 07:55:05', 'mvdown', NULL);
INSERT INTO `modlepins` VALUES (7163, 6, 'DCS.APC.SLM1.FT4111', NULL, 'ff1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-09 07:55:05', 'ff', NULL);
INSERT INTO `modlepins` VALUES (7164, 6, '8', NULL, 'ffup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-09 07:55:05', 'ffup', NULL);
INSERT INTO `modlepins` VALUES (7165, 6, '2.5', NULL, 'ffdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-09 07:55:05', 'ffdown', NULL);
INSERT INTO `modlepins` VALUES (7232, 13, 'DCS.APC.MM.A_MM2_AM1', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-10 13:51:43', 'auto', NULL);
INSERT INTO `modlepins` VALUES (7233, 13, 'DCS.APC.MM.A_MM2_PV1', NULL, 'pv1', '主电机电流', 'opc192.168.156.27', 0.01, NULL, 2, 2, NULL, NULL, 0.8, 'fullfunnel', 1, '2020-09-10 13:51:43', 'pv', 'before');
INSERT INTO `modlepins` VALUES (7234, 13, 'DCS.APC.MM.A_MM2_SV1', NULL, 'sp1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-10 13:51:43', 'sp', NULL);
INSERT INTO `modlepins` VALUES (7235, 13, 'DCS.APC.MM.A_MM2_PV2', NULL, 'pv2', '出磨温度', 'opc192.168.156.27', 0.1, NULL, 0.2, 0.2, NULL, NULL, 0.8, 'fullfunnel', 1, '2020-09-10 13:51:43', 'pv', NULL);
INSERT INTO `modlepins` VALUES (7236, 13, 'DCS.APC.MM.A_MM2_SV2', NULL, 'sp2', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-10 13:51:43', 'sp', NULL);
INSERT INTO `modlepins` VALUES (7237, 13, 'DCS.APC.MM.A_MM2_PV3', NULL, 'pv3', '压差', 'opc192.168.156.27', 0.0002, NULL, 40, 40, NULL, NULL, 0.8, 'fullfunnel', 1, '2020-09-10 13:51:43', 'pv', 'before');
INSERT INTO `modlepins` VALUES (7238, 13, 'DCS.APC.MM.A_MM2_SV3', NULL, 'sp3', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-10 13:51:43', 'sp', NULL);
INSERT INTO `modlepins` VALUES (7239, 13, 'DCS.APC.MM.A_MM2_MV1', NULL, 'mv1', '', 'opc192.168.156.27', NULL, 2, NULL, NULL, 30, 0.2, NULL, NULL, 1, '2020-09-10 13:51:43', 'mv', NULL);
INSERT INTO `modlepins` VALUES (7240, 13, 'DCS.APC.MM.A_MM1_MV1', NULL, 'mvfb1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-10 13:51:43', 'mvfb', NULL);
INSERT INTO `modlepins` VALUES (7241, 13, 'DCS.APC.MM.A_MM2_HI1', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-10 13:51:43', 'mvup', NULL);
INSERT INTO `modlepins` VALUES (7242, 13, 'DCS.APC.MM.A_MM2_LO1', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-10 13:51:43', 'mvdown', NULL);
INSERT INTO `modlepins` VALUES (7243, 11, 'DCS.APC.MM.A_MM1_AM1', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-10 13:51:53', 'auto', NULL);
INSERT INTO `modlepins` VALUES (7244, 11, 'DCS.APC.MM.A_MM1_PV1', NULL, 'pv1', '主电机电流', 'opc192.168.156.27', 0.002, NULL, 2, 1, NULL, NULL, 0.8, 'fullfunnel', 1, '2020-09-10 13:51:53', 'pv', 'before');
INSERT INTO `modlepins` VALUES (7245, 11, 'DCS.APC.MM.A_MM1_SV1', NULL, 'sp1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-10 13:51:53', 'sp', NULL);
INSERT INTO `modlepins` VALUES (7246, 11, 'DCS.APC.MM.TT73A05T02', NULL, 'pv2', '出磨温度', 'opc192.168.156.27', 0.08, NULL, 0.2, 0.2, NULL, NULL, 0.85, 'fullfunnel', 1, '2020-09-10 13:51:53', 'pv', 'before');
INSERT INTO `modlepins` VALUES (7247, 11, 'DCS.APC.MM.A_MM1_SV2', NULL, 'sp2', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-10 13:51:53', 'sp', NULL);
INSERT INTO `modlepins` VALUES (7248, 11, 'DCS.APC.MM.A_MM1_PV3', NULL, 'pv3', '压差', 'opc192.168.156.27', 0.0005, NULL, 40, 40, NULL, NULL, 0.75, 'fullfunnel', 1, '2020-09-10 13:51:53', 'pv', 'before');
INSERT INTO `modlepins` VALUES (7249, 11, 'DCS.APC.MM.A_MM1_SV3', NULL, 'sp3', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-10 13:51:53', 'sp', NULL);
INSERT INTO `modlepins` VALUES (7250, 11, 'DCS.APC.MM.A_MM1_MV1', NULL, 'mv1', '', 'opc192.168.156.27', NULL, 1, NULL, NULL, 30, 0.1, NULL, NULL, 1, '2020-09-10 13:51:53', 'mv', NULL);
INSERT INTO `modlepins` VALUES (7251, 11, 'DCS.APC.MM.A_MM1_MV1', NULL, 'mvfb1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-10 13:51:53', 'mvfb', NULL);
INSERT INTO `modlepins` VALUES (7252, 11, 'DCS.APC.MM.A_MM1_HI1', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-10 13:51:53', 'mvup', NULL);
INSERT INTO `modlepins` VALUES (7253, 11, 'DCS.APC.MM.A_MM1_LO1', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-09-10 13:51:53', 'mvdown', NULL);
INSERT INTO `modlepins` VALUES (7330, 5, 'DCS.APC.SLM1.A_SLM1_AM1', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-19 14:21:23', 'auto', 'before');
INSERT INTO `modlepins` VALUES (7331, 5, 'DCS.APC.SLM1.A_SLM1_PV1', NULL, 'pv1', '', 'opc192.168.156.27', 0.0002, NULL, 20, 40, NULL, NULL, 0.6, 'fullfunnel', 1, '2020-11-19 13:21:26', 'pv', 'before');
INSERT INTO `modlepins` VALUES (7332, 5, 'DCS.APC.SLM1.A_SLM1_SV1', NULL, 'sp1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-19 13:21:26', 'sp', NULL);
INSERT INTO `modlepins` VALUES (7333, 5, 'DCS.APC.SLM1.A_SLM1_PV2', NULL, 'pv2', '', 'opc192.168.156.27', 0.5, NULL, 2, 0.5, NULL, NULL, 0.8, 'upfunnel', 1, '2020-11-19 13:21:26', 'pv', 'before');
INSERT INTO `modlepins` VALUES (7334, 5, 'DCS.APC.SLM1.A_SLM1_SV2', NULL, 'sp2', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-19 13:21:26', 'sp', NULL);
INSERT INTO `modlepins` VALUES (7335, 5, 'DCS.APC.SLM1.M4111Y01', NULL, 'pv3', '', 'opc192.168.156.27', 0.045, NULL, 3, 3, NULL, NULL, 0.85, 'fullfunnel', 1, '2020-11-19 13:54:16', 'pv', 'before');
INSERT INTO `modlepins` VALUES (7336, 5, 'DCS.APC.SLM1.A_SLM1_SV6', NULL, 'sp3', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-19 13:54:16', 'sp', 'before');
INSERT INTO `modlepins` VALUES (7337, 5, 'DCS.APC.SLM1.A_SLM1_PV4', NULL, 'pv4', '', 'opc192.168.156.27', 0.15, NULL, 4, 4, NULL, NULL, 0.8, 'upfunnel', 1, '2020-11-19 13:21:26', 'pv', 'before');
INSERT INTO `modlepins` VALUES (7338, 5, 'DCS.APC.SLM1.A_SLM1_SV4', NULL, 'sp4', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-19 13:21:26', 'sp', NULL);
INSERT INTO `modlepins` VALUES (7339, 5, 'DCS.APC.SLM1.A_SLM1_MV1', NULL, 'mv1', '', 'opc192.168.156.27', NULL, 3, NULL, NULL, 20, 0.1, NULL, NULL, 1, '2020-11-19 14:06:48', 'mv', 'before');
INSERT INTO `modlepins` VALUES (7340, 5, 'DCS.APC.SLM1.A_SLM1_MV1', NULL, 'mvfb1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-19 14:06:48', 'mvfb', 'before');
INSERT INTO `modlepins` VALUES (7341, 5, 'DCS.APC.SLM1.A_SLM1_HI1', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-19 14:06:48', 'mvup', 'before');
INSERT INTO `modlepins` VALUES (7342, 5, 'DCS.APC.SLM1.A_SLM1_LO1', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-19 14:06:48', 'mvdown', 'before');
INSERT INTO `modlepins` VALUES (7343, 5, 'DCS.APC.SLM1.A_SLM1_PV2', NULL, 'ff1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-19 13:21:26', 'ff', NULL);
INSERT INTO `modlepins` VALUES (7344, 5, '60', NULL, 'ffup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-19 13:21:26', 'ffup', NULL);
INSERT INTO `modlepins` VALUES (7345, 5, '40', NULL, 'ffdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-19 13:21:26', 'ffdown', NULL);
INSERT INTO `modlepins` VALUES (7346, 5, 'DCS.APC.SLM1.A_SLM1_PV5', NULL, 'ff2', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-19 13:21:26', 'ff', NULL);
INSERT INTO `modlepins` VALUES (7347, 5, '176', NULL, 'ffup2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-19 13:21:26', 'ffup', NULL);
INSERT INTO `modlepins` VALUES (7348, 5, '155', NULL, 'ffdown2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-19 13:21:26', 'ffdown', NULL);

-- ----------------------------
-- Table structure for modlepins_copy1
-- ----------------------------
DROP TABLE IF EXISTS `modlepins_copy1`;
CREATE TABLE `modlepins_copy1`  (
  `modlepinsId` int(11) NOT NULL AUTO_INCREMENT,
  `reference_modleId` int(11) NULL DEFAULT NULL,
  `modleOpcTag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '\'NULL\'',
  `filterMethod` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `modlePinName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '\'NULL\'',
  `opcTagName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'NULL',
  `resource` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Q` double NULL DEFAULT NULL,
  `dmvHigh` double NULL DEFAULT NULL,
  `deadZone` double NULL DEFAULT NULL,
  `funelinitValue` double NULL DEFAULT NULL,
  `R` double NULL DEFAULT NULL,
  `dmvLow` double NULL DEFAULT NULL,
  `referTrajectoryCoef` double NULL DEFAULT NULL,
  `funneltype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`modlepinsId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1276 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of modlepins_copy1
-- ----------------------------
INSERT INTO `modlepins_copy1` VALUES (569, 1, 'DCS.APC.TX.A_TX_AUTO', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (570, 1, 'DCS.APC.TX.NOX_LB', NULL, 'pv1', NULL, 'opc192.168.156.27', 0.0003, NULL, 2, 2, NULL, NULL, 0.8, 'fullfunnel');
INSERT INTO `modlepins_copy1` VALUES (571, 1, 'DCS.APC.TX.NOX_SV', NULL, 'sp1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (572, 1, 'DCS.APC.TX.A_TX_MV', NULL, 'mv1', NULL, 'opc192.168.156.27', NULL, 0.1, NULL, NULL, 60, 0.005, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (573, 1, 'DCS.APC.TX.SNCR_LI', NULL, 'mvfb1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (574, 1, 'DCS.APC.TX.A_TX_HI', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (575, 1, 'DCS.APC.TX.A_TX_LO', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (658, 2, 'DCS.APC.YTP.A_YTP_AUTO', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (659, 2, 'DCS.APC.YTP.PT5701P01', NULL, 'pv1', NULL, 'opc192.168.156.27', 0.005, NULL, 5, 5, NULL, NULL, 0.8, 'fullfunnel');
INSERT INTO `modlepins_copy1` VALUES (660, 2, 'DCS.APC.YTP.A_YTP_SP', NULL, 'sp1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (661, 2, 'DCS.APC.YTP.A_YTP_MV', NULL, 'mv1', NULL, 'opc192.168.156.27', NULL, 2, NULL, NULL, 10, 0.01, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (662, 2, 'DCS.APC.YTP.A_YTP_MV', NULL, 'mvfb1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (663, 2, 'DCS.APC.YTP.A_YTP_HI', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (664, 2, 'DCS.APC.YTP.A_YTP_LO', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (1133, 4, 'DCS.YR.A_GSB_AM', NULL, 'auto', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (1134, 4, 'DCS.YR.A_GSB_PV1', NULL, 'pv1', NULL, 'opc128.128.2.140', 0.0005, NULL, 2, 3, NULL, NULL, 0.8, 'fullfunnel');
INSERT INTO `modlepins_copy1` VALUES (1135, 4, 'DCS.YR.A_GSB_SV1', NULL, 'sp1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (1136, 4, 'DCS.YR.A_GSB_PV2', NULL, 'pv2', NULL, 'opc128.128.2.140', 0.0009, NULL, 1, 1, NULL, NULL, 0.8, 'fullfunnel');
INSERT INTO `modlepins_copy1` VALUES (1137, 4, 'DCS.YR.A_GSB_SV2', NULL, 'sp2', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (1138, 4, 'DCS.YR.A_SP_PV', NULL, 'pv3', NULL, 'opc128.128.2.140', 0.00003, NULL, 10, 10, NULL, NULL, 0.9, 'fullfunnel');
INSERT INTO `modlepins_copy1` VALUES (1139, 4, 'DCS.YR.A_SP_SV', NULL, 'sp3', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (1140, 4, 'DCS.YR.A_GSB_MV', NULL, 'mv1', NULL, 'opc128.128.2.140', NULL, 0.2, NULL, NULL, 68, 0.02, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (1141, 4, 'DCS.YR.A_GSB_MV', NULL, 'mvfb1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (1142, 4, 'DCS.YR.A_GSB_HI', NULL, 'mvup1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (1143, 4, 'DCS.YR.A_GSB_LO', NULL, 'mvdown1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (1254, 3, 'DCS.YR.A_AUTO', NULL, 'auto', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (1255, 3, 'DCS.YR.A_AQCH_PV', NULL, 'pv1', NULL, 'opc128.128.2.140', 0.004, NULL, 5, 5, NULL, NULL, 0.8, 'fullfunnel');
INSERT INTO `modlepins_copy1` VALUES (1256, 3, 'DCS.YR.A_AQCH_SV', NULL, 'sp1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (1257, 3, 'DCS.YR.A_AQCL_PV', NULL, 'pv2', NULL, 'opc128.128.2.140', 0.0002, NULL, 5, 5, NULL, NULL, 0.8, 'fullfunnel');
INSERT INTO `modlepins_copy1` VALUES (1258, 3, 'DCS.YR.A_AQCL_SV', NULL, 'sp2', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (1259, 3, 'DCS.YR.A_SP_PV', NULL, 'pv3', NULL, 'opc128.128.2.140', 0.00003, NULL, 5, 5, NULL, NULL, 0.8, 'fullfunnel');
INSERT INTO `modlepins_copy1` VALUES (1260, 3, 'DCS.YR.A_SP_SV', NULL, 'sp3', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (1261, 3, 'DCS.YR.A_AQCH_MV', NULL, 'mv1', NULL, 'opc128.128.2.140', NULL, 2, NULL, NULL, 40, 0.1, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (1262, 3, 'DCS.YR.A_AQCH_MVFB', NULL, 'mvfb1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (1263, 3, 'DCS.YR.A_AQCH_HI', NULL, 'mvup1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (1264, 3, 'DCS.YR.A_AQCH_LO', NULL, 'mvdown1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (1265, 3, 'DCS.YR.A_AQCL_MV', NULL, 'mv2', NULL, 'opc128.128.2.140', NULL, 1, NULL, NULL, 50, 0.1, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (1266, 3, 'DCS.YR.A_AQCL_MVFB', NULL, 'mvfb2', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (1267, 3, 'DCS.YR.A_AQCL_HI', NULL, 'mvup2', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (1268, 3, 'DCS.YR.A_AQCL_LO', NULL, 'mvdown2', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (1269, 3, 'DCS.YR.A_SP_MV', NULL, 'mv3', NULL, 'opc128.128.2.140', NULL, 1, NULL, NULL, 50, 0.1, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (1270, 3, 'DCS.YR.A_SP_MV', NULL, 'mvfb3', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (1271, 3, 'DCS.YR.A_SP_HI', NULL, 'mvup3', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (1272, 3, 'DCS.YR.A_SP_LO', NULL, 'mvdown3', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (1273, 3, 'DCS.YR.GAPT06', NULL, 'ff1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (1274, 3, '0.77', NULL, 'ffup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `modlepins_copy1` VALUES (1275, 3, '0.63', NULL, 'ffdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for modlepins_copy2
-- ----------------------------
DROP TABLE IF EXISTS `modlepins_copy2`;
CREATE TABLE `modlepins_copy2`  (
  `modlepinsId` int(11) NOT NULL AUTO_INCREMENT,
  `reference_modleId` int(11) NULL DEFAULT NULL,
  `modleOpcTag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '\'NULL\'',
  `filterMethod` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `modlePinName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '\'NULL\'',
  `opcTagName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'NULL',
  `resource` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Q` double NULL DEFAULT NULL,
  `dmvHigh` double NULL DEFAULT NULL,
  `deadZone` double NULL DEFAULT NULL,
  `funelinitValue` double NULL DEFAULT NULL,
  `R` double NULL DEFAULT NULL,
  `dmvLow` double NULL DEFAULT NULL,
  `referTrajectoryCoef` double NULL DEFAULT NULL,
  `funneltype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `pinEnable` int(11) NULL DEFAULT 1,
  `updateTime` timestamp(0) NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`modlepinsId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5158 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of modlepins_copy2
-- ----------------------------
INSERT INTO `modlepins_copy2` VALUES (658, 2, 'DCS.APC.YTP.A_YTP_AUTO', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (659, 2, 'DCS.APC.YTP.PT5701P01', NULL, 'pv1', NULL, 'opc192.168.156.27', 0.005, NULL, 5, 5, NULL, NULL, 0.8, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (660, 2, 'DCS.APC.YTP.A_YTP_SP', NULL, 'sp1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (661, 2, 'DCS.APC.YTP.A_YTP_MV', NULL, 'mv1', NULL, 'opc192.168.156.27', NULL, 2, NULL, NULL, 10, 0.01, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (662, 2, 'DCS.APC.YTP.A_YTP_MV', NULL, 'mvfb1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (663, 2, 'DCS.APC.YTP.A_YTP_HI', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (664, 2, 'DCS.APC.YTP.A_YTP_LO', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2013, 4, 'DCS.YR.A_GSB_AM', NULL, 'auto', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2014, 4, 'DCS.YR.A_GSB_PV1', NULL, 'pv1', NULL, 'opc128.128.2.140', 0.001, NULL, 10, 10, NULL, NULL, 0.7, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2015, 4, 'DCS.YR.A_GSB_SV1', NULL, 'sp1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2016, 4, 'DCS.YR.A_GSB_PV2', NULL, 'pv2', NULL, 'opc128.128.2.140', 0.002, NULL, 1, 1, NULL, NULL, 0.7, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2017, 4, 'DCS.YR.A_GSB_SV2', NULL, 'sp2', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2018, 4, 'DCS.YR.A_SP_PV', NULL, 'pv3', NULL, 'opc128.128.2.140', 0.0015, NULL, 25, 15, NULL, NULL, 0.8, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2019, 4, 'DCS.YR.A_SP_SV', NULL, 'sp3', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2020, 4, 'DCS.YR.A_GSB_MV', NULL, 'mv1', NULL, 'opc128.128.2.140', NULL, 0.2, NULL, NULL, 25, 0.01, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2021, 4, 'DCS.YR.A_GSB_MV', NULL, 'mvfb1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2022, 4, 'DCS.YR.A_GSB_HI', NULL, 'mvup1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2023, 4, 'DCS.YR.A_GSB_LO', NULL, 'mvdown1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2068, 3, 'DCS.YR.A_AUTO', NULL, 'auto', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2069, 3, 'DCS.YR.A_AQCH_PV', NULL, 'pv1', NULL, 'opc128.128.2.140', 0.008, NULL, 3, 8, NULL, NULL, 0.6, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2070, 3, 'DCS.YR.A_AQCH_SV', NULL, 'sp1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2071, 3, 'DCS.YR.A_AQCL_PV', NULL, 'pv2', NULL, 'opc128.128.2.140', 0.0008, NULL, 5, 7, NULL, NULL, 0.72, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2072, 3, 'DCS.YR.A_AQCL_SV', NULL, 'sp2', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2073, 3, 'DCS.YR.A_SP_PV', NULL, 'pv3', NULL, 'opc128.128.2.140', 0.00003, NULL, 25, 25, NULL, NULL, 0.85, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2074, 3, 'DCS.YR.A_SP_SV', NULL, 'sp3', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2075, 3, 'DCS.YR.A_AQCH_MV', NULL, 'mv1', NULL, 'opc128.128.2.140', NULL, 2, NULL, NULL, 40, 0.05, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2076, 3, 'DCS.YR.A_AQCH_MV', NULL, 'mvfb1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2077, 3, 'DCS.YR.A_AQCH_HI', NULL, 'mvup1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2078, 3, 'DCS.YR.A_AQCH_LO', NULL, 'mvdown1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2079, 3, 'DCS.YR.A_AQCL_MV', NULL, 'mv2', NULL, 'opc128.128.2.140', NULL, 1, NULL, NULL, 50, 0.05, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2080, 3, 'DCS.YR.A_AQCL_MV', NULL, 'mvfb2', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2081, 3, 'DCS.YR.A_AQCL_HI', NULL, 'mvup2', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2082, 3, 'DCS.YR.A_AQCL_LO', NULL, 'mvdown2', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2083, 3, 'DCS.YR.A_SP_MV', NULL, 'mv3', NULL, 'opc128.128.2.140', NULL, 1, NULL, NULL, 50, 0.1, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2084, 3, 'DCS.YR.A_SP_MV', NULL, 'mvfb3', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2085, 3, 'DCS.YR.A_SP_HI', NULL, 'mvup3', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2086, 3, 'DCS.YR.A_SP_LO', NULL, 'mvdown3', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2087, 3, 'DCS.YR.GAPT06', NULL, 'ff1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2088, 3, '0.77', NULL, 'ffup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2089, 3, '0.63', NULL, 'ffdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2324, 7, 'DCS.APC.SLM1.A_SLM1_AM3', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2325, 7, 'DCS.APC.SLM1.A_SLM1_PV5', NULL, 'pv1', NULL, 'opc192.168.156.27', 0.001, NULL, 0.2, 0.4, NULL, NULL, 0.8, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2326, 7, 'DCS.APC.SLM1.A_SLM1_SV5', NULL, 'sp1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2327, 7, 'DCS.APC.SLM1.A_SLM1_MV3', NULL, 'mv1', NULL, 'opc192.168.156.27', NULL, 2, NULL, NULL, 30, 0.5, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2328, 7, 'DCS.APC.SLM1.A_SLM1_MV3', NULL, 'mvfb1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2329, 7, 'DCS.APC.SLM1.A_SLM1_HI3', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2330, 7, 'DCS.APC.SLM1.A_SLM1_LO3', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2930, 10, 'DCS.APC.SLM2.A_SLM2_AM3', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2931, 10, 'DCS.APC.SLM2.A_SLM2_PV5', NULL, 'pv1', NULL, 'opc192.168.156.27', 0.01, NULL, 0.5, 0.5, NULL, NULL, 0.7, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2932, 10, 'DCS.APC.SLM2.A_SLM2_SV5', NULL, 'sp1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2933, 10, 'DCS.APC.SLM2.A_SLM2_MV3', NULL, 'mv1', NULL, 'opc192.168.156.27', NULL, 2, NULL, NULL, 5, 0.2, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2934, 10, 'DCS.APC.SLM2.A_SLM2_MV3', NULL, 'mvfb1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2935, 10, 'DCS.APC.SLM2.A_SLM2_HI3', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (2936, 10, 'DCS.APC.SLM2.A_SLM2_LO3', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3401, 6, 'DCS.APC.SLM1.A_SLM1_AM2', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3402, 6, 'DCS.APC.SLM1.A_SLM1_PV3', NULL, 'pv1', NULL, 'opc192.168.156.27', 0.3, NULL, 0.1, 0.1, NULL, NULL, 0.5, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3403, 6, 'DCS.APC.SLM1.A_SLM1_SV3', NULL, 'sp1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3404, 6, 'DCS.APC.SLM1.A_SLM1_MV2', NULL, 'mv1', NULL, 'opc192.168.156.27', NULL, 2, NULL, NULL, 18, 0.01, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3405, 6, 'DCS.APC.SLM1.A_SLM1_MV2', NULL, 'mvfb1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3406, 6, 'DCS.APC.SLM1.A_SLM1_HI2', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3407, 6, 'DCS.APC.SLM1.A_SLM1_LO2', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3408, 6, 'DCS.APC.SLM1.FT4111', NULL, 'ff1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3409, 6, '8', NULL, 'ffup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3410, 6, '2.5', NULL, 'ffdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3745, 9, 'DCS.APC.SLM2.A_SLM2_AM2', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3746, 9, 'DCS.APC.SLM2.A_SLM2_PV3', NULL, 'pv1', NULL, 'opc192.168.156.27', 0.015, NULL, 0.3, 0.1, NULL, NULL, 0.6, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3747, 9, 'DCS.APC.SLM2.A_SLM2_SV3', NULL, 'sp1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3748, 9, 'DCS.APC.SLM2.A_SLM2_MV2', NULL, 'mv1', NULL, 'opc192.168.156.27', NULL, 0.2, NULL, NULL, 18, 0.01, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3749, 9, 'DCS.APC.SLM2.A_SLM2_MV2FB', NULL, 'mvfb1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3750, 9, 'DCS.APC.SLM2.A_SLM2_HI2', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3751, 9, 'DCS.APC.SLM2.A_SLM2_LO2', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3752, 9, 'DCS.APC.SLM2.F3518', NULL, 'ff1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3753, 9, '50', NULL, 'ffup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3754, 9, '20', NULL, 'ffdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3948, 5, 'DCS.APC.SLM1.A_SLM1_AM1', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3949, 5, 'DCS.APC.SLM1.A_SLM1_PV1', NULL, 'pv1', NULL, 'opc192.168.156.27', 0.0002, NULL, 20, 40, NULL, NULL, 0.6, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3950, 5, 'DCS.APC.SLM1.A_SLM1_SV1', NULL, 'sp1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3951, 5, 'DCS.APC.SLM1.A_SLM1_PV2', NULL, 'pv2', NULL, 'opc192.168.156.27', 0.3, NULL, 2, 0.5, NULL, NULL, 0.8, 'upfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3952, 5, 'DCS.APC.SLM1.A_SLM1_SV2', NULL, 'sp2', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3953, 5, 'DCS.APC.SLM1.M4111Y01', NULL, 'pv3', NULL, 'opc192.168.156.27', 0.04, NULL, 3, 3, NULL, NULL, 0.85, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3954, 5, '94', NULL, 'sp3', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3955, 5, 'DCS.APC.SLM1.A_SLM1_PV4', NULL, 'pv4', NULL, 'opc192.168.156.27', 0.15, NULL, 4, 4, NULL, NULL, 0.8, 'upfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3956, 5, 'DCS.APC.SLM1.A_SLM1_SV4', NULL, 'sp4', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3957, 5, 'DCS.APC.SLM1.A_SLM1_MV1', NULL, 'mv1', NULL, 'opc192.168.156.27', NULL, 2, NULL, NULL, 20, 0.1, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3958, 5, 'DCS.APC.SLM1.A_SLM1_MV1', NULL, 'mvfb1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3959, 5, 'DCS.APC.SLM1.A_SLM1_HI1', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3960, 5, 'DCS.APC.SLM1.A_SLM1_LO1', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3961, 5, 'DCS.APC.SLM1.A_SLM1_PV2', NULL, 'ff1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3962, 5, '60', NULL, 'ffup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3963, 5, '40', NULL, 'ffdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3964, 5, 'DCS.APC.SLM1.A_SLM1_PV5', NULL, 'ff2', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3965, 5, '176', NULL, 'ffup2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3966, 5, '155', NULL, 'ffdown2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3967, 1, 'DCS.APC.TX.A_TX_AUTO', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3968, 1, 'DCS.APC.TX.NOX_LB', NULL, 'pv1', 'NOX', 'opc192.168.156.27', 0.0003, NULL, 2, 2, NULL, NULL, 0.8, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3969, 1, 'DCS.APC.TX.NOX_SV', NULL, 'sp1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3970, 1, 'DCS.APC.TX.A_TX_MV', NULL, 'mv1', '氨水流量', 'opc192.168.156.27', NULL, 0.1, NULL, NULL, 60, 0.005, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3971, 1, 'DCS.APC.TX.SNCR_LI', NULL, 'mvfb1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3972, 1, 'DCS.APC.TX.A_TX_HI', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (3973, 1, 'DCS.APC.TX.A_TX_LO', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4178, 13, 'DCS.APC.MM.A_MM2_AM1', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4179, 13, 'DCS.APC.MM.A_MM2_PV1', NULL, 'pv1', '主电机电流', 'opc192.168.156.27', 0.0001, NULL, 2, 2, NULL, NULL, 0.8, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4180, 13, 'DCS.APC.MM.A_MM2_SV1', NULL, 'sp1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4181, 13, 'DCS.APC.MM.A_MM2_PV2', NULL, 'pv2', '出磨温度', 'opc192.168.156.27', 0.001, NULL, 0.5, 0.5, NULL, NULL, 0.8, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4182, 13, 'DCS.APC.MM.A_MM2_SV2', NULL, 'sp2', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4183, 13, 'DCS.APC.MM.A_MM2_PV3', NULL, 'pv3', '压差', 'opc192.168.156.27', 0.00001, NULL, 20, 20, NULL, NULL, 0.8, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4184, 13, 'DCS.APC.MM.A_MM2_SV3', NULL, 'sp3', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4185, 13, 'DCS.APC.MM.A_MM2_MV1', NULL, 'mv1', '', 'opc192.168.156.27', NULL, 2, NULL, NULL, 30, 0.2, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4186, 13, 'DCS.APC.MM.A_MM1_MV1', NULL, 'mvfb1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4187, 13, 'DCS.APC.MM.A_MM1_HI1', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4188, 13, 'DCS.APC.MM.A_MM1_LO1', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4455, 11, 'DCS.APC.MM.A_MM1_AM1', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4456, 11, 'DCS.APC.MM.A_MM1_PV1', NULL, 'pv1', '主电机电流', 'opc192.168.156.27', 0.01, NULL, 2, 1, NULL, NULL, 0.8, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4457, 11, 'DCS.APC.MM.A_MM1_SV1', NULL, 'sp1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4458, 11, 'DCS.APC.MM.TT73A05T02', NULL, 'pv2', '出磨温度', 'opc192.168.156.27', 0.5, NULL, 0.1, 0.2, NULL, NULL, 0.8, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4459, 11, 'DCS.APC.MM.A_MM1_SV2', NULL, 'sp2', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4460, 11, 'DCS.APC.MM.A_MM1_PV3', NULL, 'pv3', '压差', 'opc192.168.156.27', 0.001, NULL, 20, 20, NULL, NULL, 0.75, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4461, 11, 'DCS.APC.MM.A_MM1_SV3', NULL, 'sp3', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4462, 11, 'DCS.APC.MM.A_MM1_MV1', NULL, 'mv1', '', 'opc192.168.156.27', NULL, 1, NULL, NULL, 30, 0.1, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4463, 11, 'DCS.APC.MM.A_MM1_MV1', NULL, 'mvfb1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4464, 11, 'DCS.APC.MM.A_MM1_HI1', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4465, 11, 'DCS.APC.MM.A_MM1_LO1', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4823, 8, 'DCS.APC.SLM2.A_SLM2_AM1', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4824, 8, 'DCS.APC.SLM2.A_SLM2_PV1', NULL, 'pv1', '', 'opc192.168.156.27', 0.00005, NULL, 10, 40, NULL, NULL, 0.6, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4825, 8, 'DCS.APC.SLM2.A_SLM2_SV1', NULL, 'sp1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4826, 8, 'DCS.APC.SLM2.A_SLM2_PV2', NULL, 'pv2', '', 'opc192.168.156.27', 0.8, NULL, 2.5, 0.5, NULL, NULL, 0.8, 'upfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4827, 8, 'DCS.APC.SLM2.A_SLM2_SV2', NULL, 'sp2', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4828, 8, 'DCS.APC.SLM2.M4112Y01', NULL, 'pv3', '', 'opc192.168.156.27', 0.18, NULL, 4, 1, NULL, NULL, 0.85, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4829, 8, '46', NULL, 'sp3', '', 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4830, 8, 'DCS.APC.SLM2.A_SLM2_MV1', NULL, 'mv1', '', 'opc192.168.156.27', NULL, 2, NULL, NULL, 30, 0.1, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4831, 8, 'DCS.APC.SLM2.A_SLM2_MV1', NULL, 'mvfb1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4832, 8, 'DCS.APC.SLM2.A_SLM2_HI1', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4833, 8, 'DCS.APC.SLM2.A_SLM2_LO1', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4834, 8, 'DCS.APC.SLM2.A_SLM2_PV2', NULL, 'ff1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4835, 8, '43', NULL, 'ffup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4836, 8, '39', NULL, 'ffdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4837, 8, 'DCS.APC.SLM2.A_SLM2_PV5', NULL, 'ff2', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4838, 8, '180', NULL, 'ffup2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (4839, 8, '155', NULL, 'ffdown2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy2` VALUES (5136, 12, 'DCS.APC.MM.A_MM1_AM2', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:11:52');
INSERT INTO `modlepins_copy2` VALUES (5137, 12, 'DCS.APC.MM.A_MM1_PV2', NULL, 'pv1', '1#出磨温度', 'opc192.168.156.27', 1.5, NULL, 0, 0.3, NULL, NULL, 0.5, 'fullfunnel', 0, '2020-08-18 08:11:52');
INSERT INTO `modlepins_copy2` VALUES (5138, 12, 'DCS.APC.MM.A_MM1_SV2', NULL, 'sp1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:11:52');
INSERT INTO `modlepins_copy2` VALUES (5139, 12, 'DCS.APC.MM.A_MM1_AM2', NULL, 'pvenable1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:11:52');
INSERT INTO `modlepins_copy2` VALUES (5140, 12, 'DCS.APC.MM.A_MM2_PV2', NULL, 'pv2', '2#出磨温度', 'opc192.168.156.27', 1.5, NULL, 0, 0.3, NULL, NULL, 0.5, 'fullfunnel', 0, '2020-08-18 08:11:52');
INSERT INTO `modlepins_copy2` VALUES (5141, 12, 'DCS.APC.MM.A_MM2_SV2', NULL, 'sp2', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:11:52');
INSERT INTO `modlepins_copy2` VALUES (5142, 12, 'DCS.APC.MM.A_MM2_AM2', NULL, 'pvenable2', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:11:52');
INSERT INTO `modlepins_copy2` VALUES (5143, 12, 'DCS.APC.MM.A_MM1_MV2', NULL, 'mv1', '', 'opc192.168.156.27', NULL, 2, NULL, NULL, 10, 0.2, NULL, NULL, 1, '2020-08-18 08:11:52');
INSERT INTO `modlepins_copy2` VALUES (5144, 12, 'DCS.APC.MM.A_MM1_MV2', NULL, 'mvfb1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:11:52');
INSERT INTO `modlepins_copy2` VALUES (5145, 12, 'DCS.APC.MM.A_MM1_HI2', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:11:52');
INSERT INTO `modlepins_copy2` VALUES (5146, 12, 'DCS.APC.MM.A_MM1_LO2', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:11:52');
INSERT INTO `modlepins_copy2` VALUES (5147, 12, 'DCS.APC.MM.A_MM2_MV2', NULL, 'mv2', '', 'opc192.168.156.27', NULL, 2, NULL, NULL, 10, 0.2, NULL, NULL, 1, '2020-08-18 08:11:52');
INSERT INTO `modlepins_copy2` VALUES (5148, 12, 'DCS.APC.MM.A_MM2_MV2', NULL, 'mvfb2', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:11:52');
INSERT INTO `modlepins_copy2` VALUES (5149, 12, 'DCS.APC.MM.A_MM2_HI2', NULL, 'mvup2', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:11:52');
INSERT INTO `modlepins_copy2` VALUES (5150, 12, 'DCS.APC.MM.A_MM2_LO2', NULL, 'mvdown2', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:11:52');
INSERT INTO `modlepins_copy2` VALUES (5151, 12, 'DCS.APC.MM.A_MM_MV', NULL, 'mv3', '', 'opc192.168.156.27', NULL, 0.2, NULL, NULL, 350, 0.02, NULL, NULL, 1, '2020-08-18 08:11:52');
INSERT INTO `modlepins_copy2` VALUES (5152, 12, 'DCS.APC.MM.A_MM_MV', NULL, 'mvfb3', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:11:52');
INSERT INTO `modlepins_copy2` VALUES (5153, 12, 'DCS.APC.MM.A_MM_HI', NULL, 'mvup3', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:11:52');
INSERT INTO `modlepins_copy2` VALUES (5154, 12, 'DCS.APC.MM.A_MM_LO', NULL, 'mvdown3', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:11:52');
INSERT INTO `modlepins_copy2` VALUES (5155, 12, 'DCS.APC.MM.A_MM_MV', NULL, 'ff1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:11:52');
INSERT INTO `modlepins_copy2` VALUES (5156, 12, '25', NULL, 'ffup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:11:52');
INSERT INTO `modlepins_copy2` VALUES (5157, 12, '5', NULL, 'ffdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:11:52');

-- ----------------------------
-- Table structure for modlepins_copy3
-- ----------------------------
DROP TABLE IF EXISTS `modlepins_copy3`;
CREATE TABLE `modlepins_copy3`  (
  `modlepinsId` int(11) NOT NULL AUTO_INCREMENT,
  `reference_modleId` int(11) NULL DEFAULT NULL,
  `modleOpcTag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '\'NULL\'',
  `filterMethod` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `modlePinName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '\'NULL\'',
  `opcTagName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'NULL',
  `resource` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Q` double NULL DEFAULT NULL,
  `dmvHigh` double NULL DEFAULT NULL,
  `deadZone` double NULL DEFAULT NULL,
  `funelinitValue` double NULL DEFAULT NULL,
  `R` double NULL DEFAULT NULL,
  `dmvLow` double NULL DEFAULT NULL,
  `referTrajectoryCoef` double NULL DEFAULT NULL,
  `funneltype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `pinEnable` int(11) NULL DEFAULT 1,
  `updateTime` timestamp(0) NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`modlepinsId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6064 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of modlepins_copy3
-- ----------------------------
INSERT INTO `modlepins_copy3` VALUES (658, 2, 'DCS.APC.YTP.A_YTP_AUTO', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (659, 2, 'DCS.APC.YTP.PT5701P01', NULL, 'pv1', NULL, 'opc192.168.156.27', 0.005, NULL, 5, 5, NULL, NULL, 0.8, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (660, 2, 'DCS.APC.YTP.A_YTP_SP', NULL, 'sp1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (661, 2, 'DCS.APC.YTP.A_YTP_MV', NULL, 'mv1', NULL, 'opc192.168.156.27', NULL, 2, NULL, NULL, 10, 0.01, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (662, 2, 'DCS.APC.YTP.A_YTP_MV', NULL, 'mvfb1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (663, 2, 'DCS.APC.YTP.A_YTP_HI', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (664, 2, 'DCS.APC.YTP.A_YTP_LO', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (2013, 4, 'DCS.YR.A_GSB_AM', NULL, 'auto', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (2014, 4, 'DCS.YR.A_GSB_PV1', NULL, 'pv1', NULL, 'opc128.128.2.140', 0.001, NULL, 10, 10, NULL, NULL, 0.7, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (2015, 4, 'DCS.YR.A_GSB_SV1', NULL, 'sp1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (2016, 4, 'DCS.YR.A_GSB_PV2', NULL, 'pv2', NULL, 'opc128.128.2.140', 0.002, NULL, 1, 1, NULL, NULL, 0.7, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (2017, 4, 'DCS.YR.A_GSB_SV2', NULL, 'sp2', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (2018, 4, 'DCS.YR.A_SP_PV', NULL, 'pv3', NULL, 'opc128.128.2.140', 0.0015, NULL, 25, 15, NULL, NULL, 0.8, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (2019, 4, 'DCS.YR.A_SP_SV', NULL, 'sp3', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (2020, 4, 'DCS.YR.A_GSB_MV', NULL, 'mv1', NULL, 'opc128.128.2.140', NULL, 0.2, NULL, NULL, 25, 0.01, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (2021, 4, 'DCS.YR.A_GSB_MV', NULL, 'mvfb1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (2022, 4, 'DCS.YR.A_GSB_HI', NULL, 'mvup1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (2023, 4, 'DCS.YR.A_GSB_LO', NULL, 'mvdown1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (2324, 7, 'DCS.APC.SLM1.A_SLM1_AM3', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (2325, 7, 'DCS.APC.SLM1.A_SLM1_PV5', NULL, 'pv1', NULL, 'opc192.168.156.27', 0.001, NULL, 0.2, 0.4, NULL, NULL, 0.8, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (2326, 7, 'DCS.APC.SLM1.A_SLM1_SV5', NULL, 'sp1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (2327, 7, 'DCS.APC.SLM1.A_SLM1_MV3', NULL, 'mv1', NULL, 'opc192.168.156.27', NULL, 2, NULL, NULL, 30, 0.5, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (2328, 7, 'DCS.APC.SLM1.A_SLM1_MV3', NULL, 'mvfb1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (2329, 7, 'DCS.APC.SLM1.A_SLM1_HI3', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (2330, 7, 'DCS.APC.SLM1.A_SLM1_LO3', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (2930, 10, 'DCS.APC.SLM2.A_SLM2_AM3', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (2931, 10, 'DCS.APC.SLM2.A_SLM2_PV5', NULL, 'pv1', NULL, 'opc192.168.156.27', 0.01, NULL, 0.5, 0.5, NULL, NULL, 0.7, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (2932, 10, 'DCS.APC.SLM2.A_SLM2_SV5', NULL, 'sp1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (2933, 10, 'DCS.APC.SLM2.A_SLM2_MV3', NULL, 'mv1', NULL, 'opc192.168.156.27', NULL, 2, NULL, NULL, 5, 0.2, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (2934, 10, 'DCS.APC.SLM2.A_SLM2_MV3', NULL, 'mvfb1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (2935, 10, 'DCS.APC.SLM2.A_SLM2_HI3', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (2936, 10, 'DCS.APC.SLM2.A_SLM2_LO3', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3401, 6, 'DCS.APC.SLM1.A_SLM1_AM2', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3402, 6, 'DCS.APC.SLM1.A_SLM1_PV3', NULL, 'pv1', NULL, 'opc192.168.156.27', 0.3, NULL, 0.1, 0.1, NULL, NULL, 0.5, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3403, 6, 'DCS.APC.SLM1.A_SLM1_SV3', NULL, 'sp1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3404, 6, 'DCS.APC.SLM1.A_SLM1_MV2', NULL, 'mv1', NULL, 'opc192.168.156.27', NULL, 2, NULL, NULL, 18, 0.01, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3405, 6, 'DCS.APC.SLM1.A_SLM1_MV2', NULL, 'mvfb1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3406, 6, 'DCS.APC.SLM1.A_SLM1_HI2', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3407, 6, 'DCS.APC.SLM1.A_SLM1_LO2', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3408, 6, 'DCS.APC.SLM1.FT4111', NULL, 'ff1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3409, 6, '8', NULL, 'ffup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3410, 6, '2.5', NULL, 'ffdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3948, 5, 'DCS.APC.SLM1.A_SLM1_AM1', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3949, 5, 'DCS.APC.SLM1.A_SLM1_PV1', NULL, 'pv1', NULL, 'opc192.168.156.27', 0.0002, NULL, 20, 40, NULL, NULL, 0.6, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3950, 5, 'DCS.APC.SLM1.A_SLM1_SV1', NULL, 'sp1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3951, 5, 'DCS.APC.SLM1.A_SLM1_PV2', NULL, 'pv2', NULL, 'opc192.168.156.27', 0.3, NULL, 2, 0.5, NULL, NULL, 0.8, 'upfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3952, 5, 'DCS.APC.SLM1.A_SLM1_SV2', NULL, 'sp2', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3953, 5, 'DCS.APC.SLM1.M4111Y01', NULL, 'pv3', NULL, 'opc192.168.156.27', 0.04, NULL, 3, 3, NULL, NULL, 0.85, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3954, 5, '94', NULL, 'sp3', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3955, 5, 'DCS.APC.SLM1.A_SLM1_PV4', NULL, 'pv4', NULL, 'opc192.168.156.27', 0.15, NULL, 4, 4, NULL, NULL, 0.8, 'upfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3956, 5, 'DCS.APC.SLM1.A_SLM1_SV4', NULL, 'sp4', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3957, 5, 'DCS.APC.SLM1.A_SLM1_MV1', NULL, 'mv1', NULL, 'opc192.168.156.27', NULL, 2, NULL, NULL, 20, 0.1, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3958, 5, 'DCS.APC.SLM1.A_SLM1_MV1', NULL, 'mvfb1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3959, 5, 'DCS.APC.SLM1.A_SLM1_HI1', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3960, 5, 'DCS.APC.SLM1.A_SLM1_LO1', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3961, 5, 'DCS.APC.SLM1.A_SLM1_PV2', NULL, 'ff1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3962, 5, '60', NULL, 'ffup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3963, 5, '40', NULL, 'ffdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3964, 5, 'DCS.APC.SLM1.A_SLM1_PV5', NULL, 'ff2', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3965, 5, '176', NULL, 'ffup2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3966, 5, '155', NULL, 'ffdown2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3967, 1, 'DCS.APC.TX.A_TX_AUTO', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3968, 1, 'DCS.APC.TX.NOX_LB', NULL, 'pv1', 'NOX', 'opc192.168.156.27', 0.0003, NULL, 2, 2, NULL, NULL, 0.8, 'fullfunnel', 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3969, 1, 'DCS.APC.TX.NOX_SV', NULL, 'sp1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3970, 1, 'DCS.APC.TX.A_TX_MV', NULL, 'mv1', '氨水流量', 'opc192.168.156.27', NULL, 0.1, NULL, NULL, 60, 0.005, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3971, 1, 'DCS.APC.TX.SNCR_LI', NULL, 'mvfb1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3972, 1, 'DCS.APC.TX.A_TX_HI', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (3973, 1, 'DCS.APC.TX.A_TX_LO', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-18 08:09:43');
INSERT INTO `modlepins_copy3` VALUES (5920, 9, 'DCS.APC.SLM2.A_SLM2_AM2', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 14:47:59');
INSERT INTO `modlepins_copy3` VALUES (5921, 9, 'DCS.APC.SLM2.A_SLM2_PV3', NULL, 'pv1', '', 'opc192.168.156.27', 0.015, NULL, 0.3, 0.1, NULL, NULL, 0.6, 'fullfunnel', 1, '2020-08-19 14:47:59');
INSERT INTO `modlepins_copy3` VALUES (5922, 9, 'DCS.APC.SLM2.A_SLM2_SV3', NULL, 'sp1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 14:47:59');
INSERT INTO `modlepins_copy3` VALUES (5923, 9, 'DCS.APC.SLM2.A_SLM2_MV2', NULL, 'mv1', '', 'opc192.168.156.27', NULL, 0.2, NULL, NULL, 18, 0.01, NULL, NULL, 1, '2020-08-19 14:47:59');
INSERT INTO `modlepins_copy3` VALUES (5924, 9, 'DCS.APC.SLM2.A_SLM2_MV2FB', NULL, 'mvfb1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 14:47:59');
INSERT INTO `modlepins_copy3` VALUES (5925, 9, 'DCS.APC.SLM2.A_SLM2_HI2', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 14:47:59');
INSERT INTO `modlepins_copy3` VALUES (5926, 9, 'DCS.APC.SLM2.A_SLM2_LO2', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 14:47:59');
INSERT INTO `modlepins_copy3` VALUES (5927, 9, 'DCS.APC.SLM2.F3518', NULL, 'ff1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 14:47:59');
INSERT INTO `modlepins_copy3` VALUES (5928, 9, '50', NULL, 'ffup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 14:47:59');
INSERT INTO `modlepins_copy3` VALUES (5929, 9, '20', NULL, 'ffdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 14:47:59');
INSERT INTO `modlepins_copy3` VALUES (5930, 8, 'DCS.APC.SLM2.A_SLM2_AM1', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 15:27:20');
INSERT INTO `modlepins_copy3` VALUES (5931, 8, 'DCS.APC.SLM2.A_SLM2_PV1', NULL, 'pv1', '', 'opc192.168.156.27', 0.00005, NULL, 10, 40, NULL, NULL, 0.6, 'fullfunnel', 1, '2020-08-19 15:27:20');
INSERT INTO `modlepins_copy3` VALUES (5932, 8, 'DCS.APC.SLM2.A_SLM2_SV1', NULL, 'sp1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 15:27:20');
INSERT INTO `modlepins_copy3` VALUES (5933, 8, 'DCS.APC.SLM2.A_SLM2_PV2', NULL, 'pv2', '', 'opc192.168.156.27', 0.99, NULL, 2.5, 0.5, NULL, NULL, 0.8, 'upfunnel', 1, '2020-08-19 15:27:20');
INSERT INTO `modlepins_copy3` VALUES (5934, 8, 'DCS.APC.SLM2.A_SLM2_SV2', NULL, 'sp2', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 15:27:20');
INSERT INTO `modlepins_copy3` VALUES (5935, 8, 'DCS.APC.SLM2.M4112Y01', NULL, 'pv3', '', 'opc192.168.156.27', 0.18, NULL, 4, 1, NULL, NULL, 0.85, 'fullfunnel', 1, '2020-08-19 15:27:20');
INSERT INTO `modlepins_copy3` VALUES (5936, 8, '46', NULL, 'sp3', '', 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 15:27:20');
INSERT INTO `modlepins_copy3` VALUES (5937, 8, 'DCS.APC.SLM2.A_SLM2_MV1', NULL, 'mv1', '', 'opc192.168.156.27', NULL, 2, NULL, NULL, 30, 0.1, NULL, NULL, 1, '2020-08-19 15:27:20');
INSERT INTO `modlepins_copy3` VALUES (5938, 8, 'DCS.APC.SLM2.A_SLM2_MV1', NULL, 'mvfb1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 15:27:20');
INSERT INTO `modlepins_copy3` VALUES (5939, 8, 'DCS.APC.SLM2.A_SLM2_HI1', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 15:27:20');
INSERT INTO `modlepins_copy3` VALUES (5940, 8, 'DCS.APC.SLM2.A_SLM2_LO1', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 15:27:20');
INSERT INTO `modlepins_copy3` VALUES (5941, 8, 'DCS.APC.SLM2.A_SLM2_PV2', NULL, 'ff1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 15:27:20');
INSERT INTO `modlepins_copy3` VALUES (5942, 8, '43', NULL, 'ffup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 15:27:20');
INSERT INTO `modlepins_copy3` VALUES (5943, 8, '39', NULL, 'ffdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 15:27:20');
INSERT INTO `modlepins_copy3` VALUES (5944, 8, 'DCS.APC.SLM2.A_SLM2_PV5', NULL, 'ff2', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 15:27:20');
INSERT INTO `modlepins_copy3` VALUES (5945, 8, '180', NULL, 'ffup2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 15:27:20');
INSERT INTO `modlepins_copy3` VALUES (5946, 8, '155', NULL, 'ffdown2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 15:27:20');
INSERT INTO `modlepins_copy3` VALUES (5958, 13, 'DCS.APC.MM.A_MM2_AM1', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 16:57:30');
INSERT INTO `modlepins_copy3` VALUES (5959, 13, 'DCS.APC.MM.A_MM2_PV1', NULL, 'pv1', '主电机电流', 'opc192.168.156.27', 0.01, NULL, 2, 2, NULL, NULL, 0.8, 'fullfunnel', 1, '2020-08-19 16:57:30');
INSERT INTO `modlepins_copy3` VALUES (5960, 13, 'DCS.APC.MM.A_MM2_SV1', NULL, 'sp1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 16:57:30');
INSERT INTO `modlepins_copy3` VALUES (5961, 13, 'DCS.APC.MM.A_MM2_PV2', NULL, 'pv2', '出磨温度', 'opc192.168.156.27', 0.5, NULL, 0.2, 0.2, NULL, NULL, 0.8, 'fullfunnel', 1, '2020-08-19 16:57:30');
INSERT INTO `modlepins_copy3` VALUES (5962, 13, 'DCS.APC.MM.A_MM2_SV2', NULL, 'sp2', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 16:57:30');
INSERT INTO `modlepins_copy3` VALUES (5963, 13, 'DCS.APC.MM.A_MM2_PV3', NULL, 'pv3', '压差', 'opc192.168.156.27', 0.001, NULL, 20, 20, NULL, NULL, 0.8, 'upfunnel', 1, '2020-08-19 16:57:30');
INSERT INTO `modlepins_copy3` VALUES (5964, 13, 'DCS.APC.MM.A_MM2_SV3', NULL, 'sp3', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 16:57:30');
INSERT INTO `modlepins_copy3` VALUES (5965, 13, 'DCS.APC.MM.A_MM2_MV1', NULL, 'mv1', '', 'opc192.168.156.27', NULL, 2, NULL, NULL, 30, 0.2, NULL, NULL, 1, '2020-08-19 16:57:30');
INSERT INTO `modlepins_copy3` VALUES (5966, 13, 'DCS.APC.MM.A_MM1_MV1', NULL, 'mvfb1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 16:57:30');
INSERT INTO `modlepins_copy3` VALUES (5967, 13, 'DCS.APC.MM.A_MM2_HI1', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 16:57:30');
INSERT INTO `modlepins_copy3` VALUES (5968, 13, 'DCS.APC.MM.A_MM2_LO1', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 16:57:30');
INSERT INTO `modlepins_copy3` VALUES (5969, 11, 'DCS.APC.MM.A_MM1_AM1', NULL, 'auto', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 16:57:34');
INSERT INTO `modlepins_copy3` VALUES (5970, 11, 'DCS.APC.MM.A_MM1_PV1', NULL, 'pv1', '主电机电流', 'opc192.168.156.27', 0.01, NULL, 2, 1, NULL, NULL, 0.8, 'fullfunnel', 1, '2020-08-19 16:57:34');
INSERT INTO `modlepins_copy3` VALUES (5971, 11, 'DCS.APC.MM.A_MM1_SV1', NULL, 'sp1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 16:57:34');
INSERT INTO `modlepins_copy3` VALUES (5972, 11, 'DCS.APC.MM.TT73A05T02', NULL, 'pv2', '出磨温度', 'opc192.168.156.27', 0.5, NULL, 0.2, 0.2, NULL, NULL, 0.8, 'fullfunnel', 1, '2020-08-19 16:57:34');
INSERT INTO `modlepins_copy3` VALUES (5973, 11, 'DCS.APC.MM.A_MM1_SV2', NULL, 'sp2', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 16:57:34');
INSERT INTO `modlepins_copy3` VALUES (5974, 11, 'DCS.APC.MM.A_MM1_PV3', NULL, 'pv3', '压差', 'opc192.168.156.27', 0.001, NULL, 50, 50, NULL, NULL, 0.75, 'upfunnel', 1, '2020-08-19 16:57:34');
INSERT INTO `modlepins_copy3` VALUES (5975, 11, 'DCS.APC.MM.A_MM1_SV3', NULL, 'sp3', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 16:57:34');
INSERT INTO `modlepins_copy3` VALUES (5976, 11, 'DCS.APC.MM.A_MM1_MV1', NULL, 'mv1', '', 'opc192.168.156.27', NULL, 1, NULL, NULL, 30, 0.1, NULL, NULL, 1, '2020-08-19 16:57:34');
INSERT INTO `modlepins_copy3` VALUES (5977, 11, 'DCS.APC.MM.A_MM1_MV1', NULL, 'mvfb1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 16:57:34');
INSERT INTO `modlepins_copy3` VALUES (5978, 11, 'DCS.APC.MM.A_MM1_HI1', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 16:57:34');
INSERT INTO `modlepins_copy3` VALUES (5979, 11, 'DCS.APC.MM.A_MM1_LO1', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-19 16:57:34');
INSERT INTO `modlepins_copy3` VALUES (5980, 3, 'DCS.YR.A_AUTO', NULL, 'auto', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33');
INSERT INTO `modlepins_copy3` VALUES (5981, 3, 'DCS.YR.A_AQCH_PV', NULL, 'pv1', '', 'opc128.128.2.140', 0.008, NULL, 3, 8, NULL, NULL, 0.1, 'fullfunnel', 1, '2020-08-20 08:47:33');
INSERT INTO `modlepins_copy3` VALUES (5982, 3, 'DCS.YR.A_AQCH_SV', NULL, 'sp1', '', 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33');
INSERT INTO `modlepins_copy3` VALUES (5983, 3, 'DCS.YR.A_AQCL_PV', NULL, 'pv2', '', 'opc128.128.2.140', 0.0008, NULL, 5, 7, NULL, NULL, 0.72, 'fullfunnel', 1, '2020-08-20 08:47:33');
INSERT INTO `modlepins_copy3` VALUES (5984, 3, 'DCS.YR.A_AQCL_SV', NULL, 'sp2', '', 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33');
INSERT INTO `modlepins_copy3` VALUES (5985, 3, 'DCS.YR.A_SP_PV', NULL, 'pv3', '', 'opc128.128.2.140', 0.00003, NULL, 25, 25, NULL, NULL, 0.85, 'fullfunnel', 1, '2020-08-20 08:47:33');
INSERT INTO `modlepins_copy3` VALUES (5986, 3, 'DCS.YR.A_SP_SV', NULL, 'sp3', '', 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33');
INSERT INTO `modlepins_copy3` VALUES (5987, 3, 'DCS.YR.A_AQCH_MV', NULL, 'mv1', '', 'opc128.128.2.140', NULL, 2, NULL, NULL, 40, 0.05, NULL, NULL, 1, '2020-08-20 08:47:33');
INSERT INTO `modlepins_copy3` VALUES (5988, 3, 'DCS.YR.A_AQCH_MV', NULL, 'mvfb1', '', 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33');
INSERT INTO `modlepins_copy3` VALUES (5989, 3, 'DCS.YR.A_AQCH_HI', NULL, 'mvup1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33');
INSERT INTO `modlepins_copy3` VALUES (5990, 3, 'DCS.YR.A_AQCH_LO', NULL, 'mvdown1', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33');
INSERT INTO `modlepins_copy3` VALUES (5991, 3, 'DCS.YR.A_AQCL_MV', NULL, 'mv2', '', 'opc128.128.2.140', NULL, 1, NULL, NULL, 50, 0.05, NULL, NULL, 1, '2020-08-20 08:47:33');
INSERT INTO `modlepins_copy3` VALUES (5992, 3, 'DCS.YR.A_AQCL_MV', NULL, 'mvfb2', '', 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33');
INSERT INTO `modlepins_copy3` VALUES (5993, 3, 'DCS.YR.A_AQCL_HI', NULL, 'mvup2', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33');
INSERT INTO `modlepins_copy3` VALUES (5994, 3, 'DCS.YR.A_AQCL_LO', NULL, 'mvdown2', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33');
INSERT INTO `modlepins_copy3` VALUES (5995, 3, 'DCS.YR.A_SP_MV', NULL, 'mv3', '', 'opc128.128.2.140', NULL, 1, NULL, NULL, 50, 0.1, NULL, NULL, 1, '2020-08-20 08:47:33');
INSERT INTO `modlepins_copy3` VALUES (5996, 3, 'DCS.YR.A_SP_MV', NULL, 'mvfb3', '', 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33');
INSERT INTO `modlepins_copy3` VALUES (5997, 3, 'DCS.YR.A_SP_HI', NULL, 'mvup3', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33');
INSERT INTO `modlepins_copy3` VALUES (5998, 3, 'DCS.YR.A_SP_LO', NULL, 'mvdown3', NULL, 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33');
INSERT INTO `modlepins_copy3` VALUES (5999, 3, 'DCS.YR.GAPT06', NULL, 'ff1', '', 'opc128.128.2.140', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33');
INSERT INTO `modlepins_copy3` VALUES (6000, 3, '0.77', NULL, 'ffup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33');
INSERT INTO `modlepins_copy3` VALUES (6001, 3, '0.63', NULL, 'ffdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 08:47:33');
INSERT INTO `modlepins_copy3` VALUES (6033, 12, '1', NULL, 'auto', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6034, 12, 'DCS.APC.MM.A_MM1_PV2', NULL, 'pv1', '1#出磨温度', 'opc192.168.156.27', 4, NULL, 0.1, 0.3, NULL, NULL, 0.5, 'fullfunnel', 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6035, 12, 'DCS.APC.MM.A_MM1_SV2', NULL, 'sp1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6036, 12, 'DCS.APC.MM.A_MM1_AM2', NULL, 'pvenable1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6037, 12, 'DCS.APC.MM.A_MM2_PV2', NULL, 'pv2', '2#出磨温度', 'opc192.168.156.27', 4, NULL, 0.1, 0.3, NULL, NULL, 0.5, 'fullfunnel', 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6038, 12, 'DCS.APC.MM.A_MM2_SV2', NULL, 'sp2', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6039, 12, 'DCS.APC.MM.A_MM2_AM2', NULL, 'pvenable2', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6040, 12, 'DCS.APC.MM.A_MM_MV', NULL, 'pv3', '', 'opc192.168.156.27', 0.1, NULL, 1, 0, NULL, NULL, 0.7, 'fullfunnel', 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6041, 12, '15', NULL, 'sp3', '', 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6042, 12, 'DCS.APC.MM.A_MM_AM', NULL, 'pvenable3', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6043, 12, 'DCS.APC.MM.A_MM1_MV2', NULL, 'pv4', '', 'opc192.168.156.27', 0.06, NULL, 3, 0, NULL, NULL, 0.85, 'upfunnel', 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6044, 12, '17', NULL, 'sp4', '', 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6045, 12, 'DCS.APC.MM.A_MM_AM', NULL, 'pvenable4', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6046, 12, 'DCS.APC.MM.A_MM2_MV2', NULL, 'pv5', '', 'opc192.168.156.27', 0.06, NULL, 3, 0, NULL, NULL, 0.85, 'upfunnel', 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6047, 12, '17', NULL, 'sp5', '', 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6048, 12, 'DCS.APC.MM.A_MM_AM', NULL, 'pvenable5', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6049, 12, 'DCS.APC.MM.A_MM1_MV2', NULL, 'mv1', '', 'opc192.168.156.27', NULL, 2, NULL, NULL, 4, 0.2, NULL, NULL, 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6050, 12, 'DCS.APC.MM.A_MM1_MV2', NULL, 'mvfb1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6051, 12, 'DCS.APC.MM.A_MM1_HI2', NULL, 'mvup1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6052, 12, 'DCS.APC.MM.A_MM1_LO2', NULL, 'mvdown1', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6053, 12, 'DCS.APC.MM.A_MM2_MV2', NULL, 'mv2', '', 'opc192.168.156.27', NULL, 2, NULL, NULL, 4, 0.2, NULL, NULL, 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6054, 12, 'DCS.APC.MM.A_MM2_MV2', NULL, 'mvfb2', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6055, 12, 'DCS.APC.MM.A_MM2_HI2', NULL, 'mvup2', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6056, 12, 'DCS.APC.MM.A_MM2_LO2', NULL, 'mvdown2', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6057, 12, 'DCS.APC.MM.A_MM_MV', NULL, 'mv3', '', 'opc192.168.156.27', NULL, 0.2, NULL, NULL, 400, 0.02, NULL, NULL, 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6058, 12, 'DCS.APC.MM.A_MM_MV', NULL, 'mvfb3', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6059, 12, 'DCS.APC.MM.A_MM_HI', NULL, 'mvup3', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6060, 12, 'DCS.APC.MM.A_MM_LO', NULL, 'mvdown3', NULL, 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6061, 12, 'DCS.APC.MM.A_MM_MV', NULL, 'ff1', '', 'opc192.168.156.27', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6062, 12, '25', NULL, 'ffup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 17:03:53');
INSERT INTO `modlepins_copy3` VALUES (6063, 12, '5', NULL, 'ffdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-08-20 17:03:53');

-- ----------------------------
-- Table structure for modlerespon
-- ----------------------------
DROP TABLE IF EXISTS `modlerespon`;
CREATE TABLE `modlerespon`  (
  `modletagId` int(11) NOT NULL AUTO_INCREMENT,
  `refrencemodleId` int(11) NULL DEFAULT NULL,
  `stepRespJson` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `inputPins` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `outputPins` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `effectRatio` double NULL DEFAULT 1,
  PRIMARY KEY (`modletagId`) USING BTREE,
  INDEX `refrencemodleId`(`refrencemodleId`) USING BTREE,
  CONSTRAINT `modlerespon_ibfk_1` FOREIGN KEY (`refrencemodleId`) REFERENCES `modle` (`modleId`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1789 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of modlerespon
-- ----------------------------
INSERT INTO `modlerespon` VALUES (95, 2, '{k:-7,t:160,tao:12}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (396, 4, '{k:-62,t:380,tao:100}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (397, 4, '{k:-20,t:550,tao:200}', 'mv1', 'pv2', 1);
INSERT INTO `modlerespon` VALUES (398, 4, '{k:-45,t:620,tao:100}', 'mv1', 'pv3', 1);
INSERT INTO `modlerespon` VALUES (448, 7, '{k:0.7,t:25,tao:5}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (560, 10, '{k:0.4,t:30,tao:5}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (878, 1, '{k:-820,t:200,tao:60}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (1441, 3, '{k:-24,t:650,tao:60}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (1442, 3, '{k:-24,t:800,tao:60}', 'mv2', 'pv2', 1);
INSERT INTO `modlerespon` VALUES (1443, 3, '{k:-30,t:620,tao:100}', 'mv3', 'pv3', 1);
INSERT INTO `modlerespon` VALUES (1444, 3, '{k:4000,t:130,tao:90}', 'ff1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (1696, 12, '{k:-0.3,t:280,tao:60}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (1697, 12, '{k:-0.3,t:280,tao:60}', 'mv2', 'pv2', 1);
INSERT INTO `modlerespon` VALUES (1698, 12, '{k:1.1,t:220,tao:30}', 'ff1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (1699, 12, '{k:1.1,t:220,tao:30}', 'ff1', 'pv2', 1);
INSERT INTO `modlerespon` VALUES (1706, 15, '{k:1.5,t:260,tao:60}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (1707, 15, '{k:1.5,t:260,tao:60}', 'mv1', 'pv2', 1);
INSERT INTO `modlerespon` VALUES (1708, 15, '{k:4,t:350,tao:60}', 'mv1', 'pv3', 1);
INSERT INTO `modlerespon` VALUES (1709, 15, '{k:4,t:350,tao:60}', 'mv1', 'pv4', 1);
INSERT INTO `modlerespon` VALUES (1710, 15, '{k:6,t:350,tao:60}', 'mv1', 'pv5', 1);
INSERT INTO `modlerespon` VALUES (1711, 15, '{k:6,t:350,tao:60}', 'mv1', 'pv6', 1);
INSERT INTO `modlerespon` VALUES (1730, 9, '{k:-1.5,t:255,tao:30}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (1731, 9, '{k:-0.001,t:100,tao:80}', 'ff1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (1750, 8, '{k:50,t:260,tao:60}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (1751, 8, '{k:0.3,t:220,tao:60}', 'mv1', 'pv2', 1);
INSERT INTO `modlerespon` VALUES (1752, 8, '{k:3.5,t:280,tao:60}', 'mv1', 'pv3', 1);
INSERT INTO `modlerespon` VALUES (1753, 8, '{k:70,t:100,tao:30}', 'ff1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (1754, 8, '{k:-50,t:150,tao:5}', 'ff2', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (1755, 8, '{k:-2,t:150,tao:5}', 'ff2', 'pv3', 1);
INSERT INTO `modlerespon` VALUES (1756, 6, '{k:-0.2,t:200,tao:40}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (1757, 6, '{k:-4,t:180,tao:30}', 'ff1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (1776, 13, '{k:1,t:240,tao:60}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (1777, 13, '{k:-0.8,t:240,tao:60}', 'mv1', 'pv2', 1);
INSERT INTO `modlerespon` VALUES (1778, 13, '{k:10,t:240,tao:60}', 'mv1', 'pv3', 1);
INSERT INTO `modlerespon` VALUES (1779, 11, '{k:1,t:250,tao:60}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (1780, 11, '{k:-0.8,t:250,tao:60}', 'mv1', 'pv2', 1);
INSERT INTO `modlerespon` VALUES (1781, 11, '{k:10,t:250,tao:60}', 'mv1', 'pv3', 1);
INSERT INTO `modlerespon` VALUES (1782, 5, '{k:42,t:240,tao:60}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (1783, 5, '{k:0.5,t:210,tao:60}', 'mv1', 'pv2', 1);
INSERT INTO `modlerespon` VALUES (1784, 5, '{k:2.7,t:240,tao:60}', 'mv1', 'pv3', 1);
INSERT INTO `modlerespon` VALUES (1785, 5, '{k:1.0,t:200,tao:50}', 'mv1', 'pv4', 1);
INSERT INTO `modlerespon` VALUES (1786, 5, '{k:30,t:100,tao:30}', 'ff1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (1787, 5, '{k:-50,t:150,tao:5}', 'ff2', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (1788, 5, '{k:-2,t:150,tao:5}', 'ff2', 'pv3', 1);

-- ----------------------------
-- Table structure for modlerespon_copy1
-- ----------------------------
DROP TABLE IF EXISTS `modlerespon_copy1`;
CREATE TABLE `modlerespon_copy1`  (
  `modletagId` int(11) NOT NULL AUTO_INCREMENT,
  `refrencemodleId` int(11) NULL DEFAULT NULL,
  `stepRespJson` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `inputPins` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `outputPins` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`modletagId`) USING BTREE,
  INDEX `refrencemodleId`(`refrencemodleId`) USING BTREE,
  CONSTRAINT `modlerespon_copy1_ibfk_1` FOREIGN KEY (`refrencemodleId`) REFERENCES `modle` (`modleId`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 235 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of modlerespon_copy1
-- ----------------------------
INSERT INTO `modlerespon_copy1` VALUES (82, 1, '{k:-820,t:200,tao:60}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy1` VALUES (95, 2, '{k:-7,t:160,tao:12}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy1` VALUES (203, 4, '{k:82,t:270,tao:180}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy1` VALUES (204, 4, '{k:-24,t:650,tao:240}', 'mv1', 'pv2');
INSERT INTO `modlerespon_copy1` VALUES (205, 4, '{k:45,t:240,tao:180}', 'mv1', 'pv3');
INSERT INTO `modlerespon_copy1` VALUES (231, 3, '{k:-24,t:620,tao:100}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy1` VALUES (232, 3, '{k:2,t:500,tao:100}', 'mv2', 'pv2');
INSERT INTO `modlerespon_copy1` VALUES (233, 3, '{k:-24,t:620,tao:100}', 'mv3', 'pv3');
INSERT INTO `modlerespon_copy1` VALUES (234, 3, '{k:2200,t:150,tao:100}', 'ff1', 'pv1');

-- ----------------------------
-- Table structure for modlerespon_copy2
-- ----------------------------
DROP TABLE IF EXISTS `modlerespon_copy2`;
CREATE TABLE `modlerespon_copy2`  (
  `modletagId` int(11) NOT NULL AUTO_INCREMENT,
  `refrencemodleId` int(11) NULL DEFAULT NULL,
  `stepRespJson` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `inputPins` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `outputPins` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`modletagId`) USING BTREE,
  INDEX `refrencemodleId`(`refrencemodleId`) USING BTREE,
  CONSTRAINT `modlerespon_copy2_ibfk_1` FOREIGN KEY (`refrencemodleId`) REFERENCES `modle` (`modleId`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1190 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of modlerespon_copy2
-- ----------------------------
INSERT INTO `modlerespon_copy2` VALUES (95, 2, '{k:-7,t:160,tao:12}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy2` VALUES (396, 4, '{k:-62,t:380,tao:100}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy2` VALUES (397, 4, '{k:-20,t:550,tao:200}', 'mv1', 'pv2');
INSERT INTO `modlerespon_copy2` VALUES (398, 4, '{k:-45,t:620,tao:100}', 'mv1', 'pv3');
INSERT INTO `modlerespon_copy2` VALUES (407, 3, '{k:-24,t:650,tao:60}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy2` VALUES (408, 3, '{k:-24,t:800,tao:60}', 'mv2', 'pv2');
INSERT INTO `modlerespon_copy2` VALUES (409, 3, '{k:-30,t:620,tao:100}', 'mv3', 'pv3');
INSERT INTO `modlerespon_copy2` VALUES (410, 3, '{k:4000,t:130,tao:90}', 'ff1', 'pv1');
INSERT INTO `modlerespon_copy2` VALUES (448, 7, '{k:0.7,t:25,tao:5}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy2` VALUES (560, 10, '{k:0.4,t:30,tao:5}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy2` VALUES (682, 6, '{k:-0.2,t:200,tao:40}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy2` VALUES (683, 6, '{k:-4,t:180,tao:30}', 'ff1', 'pv1');
INSERT INTO `modlerespon_copy2` VALUES (800, 9, '{k:-1.3,t:215,tao:40}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy2` VALUES (801, 9, '{k:-0.001,t:100,tao:80}', 'ff1', 'pv1');
INSERT INTO `modlerespon_copy2` VALUES (871, 5, '{k:42,t:240,tao:60}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy2` VALUES (872, 5, '{k:0.5,t:210,tao:60}', 'mv1', 'pv2');
INSERT INTO `modlerespon_copy2` VALUES (873, 5, '{k:2.7,t:240,tao:60}', 'mv1', 'pv3');
INSERT INTO `modlerespon_copy2` VALUES (874, 5, '{k:1.0,t:200,tao:50}', 'mv1', 'pv4');
INSERT INTO `modlerespon_copy2` VALUES (875, 5, '{k:30,t:100,tao:30}', 'ff1', 'pv1');
INSERT INTO `modlerespon_copy2` VALUES (876, 5, '{k:-50,t:150,tao:5}', 'ff2', 'pv1');
INSERT INTO `modlerespon_copy2` VALUES (877, 5, '{k:-2,t:150,tao:5}', 'ff2', 'pv3');
INSERT INTO `modlerespon_copy2` VALUES (878, 1, '{k:-820,t:200,tao:60}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy2` VALUES (932, 13, '{k:1,t:180,tao:60}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy2` VALUES (933, 13, '{k:-0.5,t:180,tao:60}', 'mv1', 'pv2');
INSERT INTO `modlerespon_copy2` VALUES (934, 13, '{k:10,t:180,tao:60}', 'mv1', 'pv3');
INSERT INTO `modlerespon_copy2` VALUES (1010, 11, '{k:1,t:180,tao:60}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy2` VALUES (1011, 11, '{k:-0.5,t:180,tao:60}', 'mv1', 'pv2');
INSERT INTO `modlerespon_copy2` VALUES (1012, 11, '{k:10,t:180,tao:60}', 'mv1', 'pv3');
INSERT INTO `modlerespon_copy2` VALUES (1098, 8, '{k:50,t:260,tao:60}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy2` VALUES (1099, 8, '{k:0.3,t:220,tao:60}', 'mv1', 'pv2');
INSERT INTO `modlerespon_copy2` VALUES (1100, 8, '{k:3.5,t:280,tao:60}', 'mv1', 'pv3');
INSERT INTO `modlerespon_copy2` VALUES (1101, 8, '{k:70,t:100,tao:30}', 'ff1', 'pv1');
INSERT INTO `modlerespon_copy2` VALUES (1102, 8, '{k:-50,t:150,tao:5}', 'ff2', 'pv1');
INSERT INTO `modlerespon_copy2` VALUES (1103, 8, '{k:-2,t:150,tao:5}', 'ff2', 'pv3');
INSERT INTO `modlerespon_copy2` VALUES (1184, 12, '{k:-0.2,t:280,tao:60}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy2` VALUES (1185, 12, '{k:-0.2,t:280,tao:60}', 'mv2', 'pv2');
INSERT INTO `modlerespon_copy2` VALUES (1186, 12, '{k:1,t:240,tao:60}', 'mv3', 'pv1');
INSERT INTO `modlerespon_copy2` VALUES (1187, 12, '{k:1,t:240,tao:60}', 'mv3', 'pv2');
INSERT INTO `modlerespon_copy2` VALUES (1188, 12, '{k:1,t:220,tao:30}', 'ff1', 'pv1');
INSERT INTO `modlerespon_copy2` VALUES (1189, 12, '{k:1,t:220,tao:30}', 'ff1', 'pv2');

-- ----------------------------
-- Table structure for modlerespon_copy3
-- ----------------------------
DROP TABLE IF EXISTS `modlerespon_copy3`;
CREATE TABLE `modlerespon_copy3`  (
  `modletagId` int(11) NOT NULL AUTO_INCREMENT,
  `refrencemodleId` int(11) NULL DEFAULT NULL,
  `stepRespJson` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `inputPins` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `outputPins` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`modletagId`) USING BTREE,
  INDEX `refrencemodleId`(`refrencemodleId`) USING BTREE,
  CONSTRAINT `modlerespon_copy3_ibfk_1` FOREIGN KEY (`refrencemodleId`) REFERENCES `modle` (`modleId`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1470 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of modlerespon_copy3
-- ----------------------------
INSERT INTO `modlerespon_copy3` VALUES (95, 2, '{k:-7,t:160,tao:12}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy3` VALUES (396, 4, '{k:-62,t:380,tao:100}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy3` VALUES (397, 4, '{k:-20,t:550,tao:200}', 'mv1', 'pv2');
INSERT INTO `modlerespon_copy3` VALUES (398, 4, '{k:-45,t:620,tao:100}', 'mv1', 'pv3');
INSERT INTO `modlerespon_copy3` VALUES (448, 7, '{k:0.7,t:25,tao:5}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy3` VALUES (560, 10, '{k:0.4,t:30,tao:5}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy3` VALUES (682, 6, '{k:-0.2,t:200,tao:40}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy3` VALUES (683, 6, '{k:-4,t:180,tao:30}', 'ff1', 'pv1');
INSERT INTO `modlerespon_copy3` VALUES (871, 5, '{k:42,t:240,tao:60}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy3` VALUES (872, 5, '{k:0.5,t:210,tao:60}', 'mv1', 'pv2');
INSERT INTO `modlerespon_copy3` VALUES (873, 5, '{k:2.7,t:240,tao:60}', 'mv1', 'pv3');
INSERT INTO `modlerespon_copy3` VALUES (874, 5, '{k:1.0,t:200,tao:50}', 'mv1', 'pv4');
INSERT INTO `modlerespon_copy3` VALUES (875, 5, '{k:30,t:100,tao:30}', 'ff1', 'pv1');
INSERT INTO `modlerespon_copy3` VALUES (876, 5, '{k:-50,t:150,tao:5}', 'ff2', 'pv1');
INSERT INTO `modlerespon_copy3` VALUES (877, 5, '{k:-2,t:150,tao:5}', 'ff2', 'pv3');
INSERT INTO `modlerespon_copy3` VALUES (878, 1, '{k:-820,t:200,tao:60}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy3` VALUES (1424, 9, '{k:-1.5,t:235,tao:40}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy3` VALUES (1425, 9, '{k:-0.001,t:100,tao:80}', 'ff1', 'pv1');
INSERT INTO `modlerespon_copy3` VALUES (1426, 8, '{k:50,t:260,tao:60}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy3` VALUES (1427, 8, '{k:0.2,t:220,tao:60}', 'mv1', 'pv2');
INSERT INTO `modlerespon_copy3` VALUES (1428, 8, '{k:3.5,t:280,tao:60}', 'mv1', 'pv3');
INSERT INTO `modlerespon_copy3` VALUES (1429, 8, '{k:70,t:100,tao:30}', 'ff1', 'pv1');
INSERT INTO `modlerespon_copy3` VALUES (1430, 8, '{k:-50,t:150,tao:5}', 'ff2', 'pv1');
INSERT INTO `modlerespon_copy3` VALUES (1431, 8, '{k:-2,t:150,tao:5}', 'ff2', 'pv3');
INSERT INTO `modlerespon_copy3` VALUES (1435, 13, '{k:1,t:240,tao:60}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy3` VALUES (1436, 13, '{k:-0.8,t:240,tao:60}', 'mv1', 'pv2');
INSERT INTO `modlerespon_copy3` VALUES (1437, 13, '{k:10,t:240,tao:60}', 'mv1', 'pv3');
INSERT INTO `modlerespon_copy3` VALUES (1438, 11, '{k:1,t:250,tao:60}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy3` VALUES (1439, 11, '{k:-0.8,t:250,tao:60}', 'mv1', 'pv2');
INSERT INTO `modlerespon_copy3` VALUES (1440, 11, '{k:10,t:250,tao:60}', 'mv1', 'pv3');
INSERT INTO `modlerespon_copy3` VALUES (1441, 3, '{k:-24,t:650,tao:60}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy3` VALUES (1442, 3, '{k:-24,t:800,tao:60}', 'mv2', 'pv2');
INSERT INTO `modlerespon_copy3` VALUES (1443, 3, '{k:-30,t:620,tao:100}', 'mv3', 'pv3');
INSERT INTO `modlerespon_copy3` VALUES (1444, 3, '{k:4000,t:130,tao:90}', 'ff1', 'pv1');
INSERT INTO `modlerespon_copy3` VALUES (1454, 12, '{k:-0.2,t:280,tao:60}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy3` VALUES (1455, 12, '{k:-0.2,t:280,tao:60}', 'mv2', 'pv2');
INSERT INTO `modlerespon_copy3` VALUES (1456, 12, '{k:1.5,t:260,tao:60}', 'mv3', 'pv1');
INSERT INTO `modlerespon_copy3` VALUES (1457, 12, '{k:1.5,t:260,tao:60}', 'mv3', 'pv2');
INSERT INTO `modlerespon_copy3` VALUES (1458, 12, '{k:1,t:350,tao:30}', 'mv3', 'pv3');
INSERT INTO `modlerespon_copy3` VALUES (1459, 12, '{k:3,t:400,tao:30}', 'mv3', 'pv4');
INSERT INTO `modlerespon_copy3` VALUES (1460, 12, '{k:3,t:400,tao:30}', 'mv3', 'pv5');
INSERT INTO `modlerespon_copy3` VALUES (1461, 12, '{k:1,t:220,tao:30}', 'ff1', 'pv1');
INSERT INTO `modlerespon_copy3` VALUES (1462, 12, '{k:1,t:220,tao:30}', 'ff1', 'pv2');
INSERT INTO `modlerespon_copy3` VALUES (1463, 5, '{k:42,t:240,tao:60}', 'mv1', 'pv1');
INSERT INTO `modlerespon_copy3` VALUES (1464, 5, '{k:0.5,t:210,tao:60}', 'mv1', 'pv2');
INSERT INTO `modlerespon_copy3` VALUES (1465, 5, '{k:2.7,t:240,tao:60}', 'mv1', 'pv3');
INSERT INTO `modlerespon_copy3` VALUES (1466, 5, '{k:1.0,t:200,tao:50}', 'mv1', 'pv4');
INSERT INTO `modlerespon_copy3` VALUES (1467, 5, '{k:30,t:100,tao:30}', 'ff1', 'pv1');
INSERT INTO `modlerespon_copy3` VALUES (1468, 5, '{k:-50,t:150,tao:5}', 'ff2', 'pv1');
INSERT INTO `modlerespon_copy3` VALUES (1469, 5, '{k:-2,t:150,tao:5}', 'ff2', 'pv3');

-- ----------------------------
-- Table structure for opcserveinfo
-- ----------------------------
DROP TABLE IF EXISTS `opcserveinfo`;
CREATE TABLE `opcserveinfo`  (
  `opcserveid` int(11) NOT NULL AUTO_INCREMENT,
  `opcuser` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `opcpassword` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `opcip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `opcclsid` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`opcserveid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'opcserver的信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of opcserveinfo
-- ----------------------------
INSERT INTO `opcserveinfo` VALUES (1, 'administrator', 'supcondcs', '192.168.156.27', '6E6170F0-FF2D-11D2-8087-00105AA8F840');
INSERT INTO `opcserveinfo` VALUES (2, 'administrator', 'supcondcs', '128.128.2.140', 'B3AF0BF6-4C0C-4804-A122-6F3B160F4397');

-- ----------------------------
-- Table structure for opcserveinfo_copy1
-- ----------------------------
DROP TABLE IF EXISTS `opcserveinfo_copy1`;
CREATE TABLE `opcserveinfo_copy1`  (
  `opcserveid` int(11) NOT NULL AUTO_INCREMENT,
  `opcuser` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `opcpassword` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `opcip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `opcclsid` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`opcserveid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'opcserver的信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of opcserveinfo_copy1
-- ----------------------------
INSERT INTO `opcserveinfo_copy1` VALUES (1, 'administrator', 'supcondcs', '192.168.156.27', '6E6170F0-FF2D-11D2-8087-00105AA8F840');
INSERT INTO `opcserveinfo_copy1` VALUES (2, 'administrator', 'supcondcs', '128.128.2.140', 'B3AF0BF6-4C0C-4804-A122-6F3B160F4397');

-- ----------------------------
-- Table structure for opcserveinfo_copy2
-- ----------------------------
DROP TABLE IF EXISTS `opcserveinfo_copy2`;
CREATE TABLE `opcserveinfo_copy2`  (
  `opcserveid` int(11) NOT NULL AUTO_INCREMENT,
  `opcuser` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `opcpassword` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `opcip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `opcclsid` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`opcserveid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'opcserver的信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of opcserveinfo_copy2
-- ----------------------------
INSERT INTO `opcserveinfo_copy2` VALUES (1, 'administrator', 'supcondcs', '192.168.156.27', '6E6170F0-FF2D-11D2-8087-00105AA8F840');
INSERT INTO `opcserveinfo_copy2` VALUES (2, 'administrator', 'supcondcs', '128.128.2.140', 'B3AF0BF6-4C0C-4804-A122-6F3B160F4397');

-- ----------------------------
-- Table structure for opcverification
-- ----------------------------
DROP TABLE IF EXISTS `opcverification`;
CREATE TABLE `opcverification`  (
  `tagid` int(11) NOT NULL AUTO_INCREMENT,
  `tagName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `tag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'opc验证位号',
  `opcserveid` int(11) NULL DEFAULT NULL COMMENT 'opc serve的id',
  PRIMARY KEY (`tagid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'opc验证位号表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of opcverification
-- ----------------------------
INSERT INTO `opcverification` VALUES (1, NULL, 'DCS.APC.TX.APC_TXJY2', 1);
INSERT INTO `opcverification` VALUES (2, NULL, 'DCS.YR.A_TXJY', 2);

-- ----------------------------
-- Table structure for opcverification_copy1
-- ----------------------------
DROP TABLE IF EXISTS `opcverification_copy1`;
CREATE TABLE `opcverification_copy1`  (
  `tagid` int(11) NOT NULL AUTO_INCREMENT,
  `tagName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `tag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'opc验证位号',
  `opcserveid` int(11) NULL DEFAULT NULL COMMENT 'opc serve的id',
  PRIMARY KEY (`tagid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'opc验证位号表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of opcverification_copy1
-- ----------------------------
INSERT INTO `opcverification_copy1` VALUES (1, NULL, 'DCS.APC.TX.APC_TXJY2', 1);
INSERT INTO `opcverification_copy1` VALUES (2, NULL, 'DCS.YR.A_TXJY', 2);

-- ----------------------------
-- Table structure for opcverification_copy2
-- ----------------------------
DROP TABLE IF EXISTS `opcverification_copy2`;
CREATE TABLE `opcverification_copy2`  (
  `tagid` int(11) NOT NULL AUTO_INCREMENT,
  `tagName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `tag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'opc验证位号',
  `opcserveid` int(11) NULL DEFAULT NULL COMMENT 'opc serve的id',
  PRIMARY KEY (`tagid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'opc验证位号表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of opcverification_copy2
-- ----------------------------
INSERT INTO `opcverification_copy2` VALUES (1, NULL, 'DCS.APC.TX.APC_TXJY2', 1);
INSERT INTO `opcverification_copy2` VALUES (2, NULL, 'DCS.YR.A_TXJY', 2);

-- ----------------------------
-- Table structure for shockdetect
-- ----------------------------
DROP TABLE IF EXISTS `shockdetect`;
CREATE TABLE `shockdetect`  (
  `pk_shockdetectid` int(11) NOT NULL AUTO_INCREMENT,
  `pk_pinid` int(11) NULL DEFAULT NULL COMMENT '引用的过滤器主键id',
  `backToDCSTag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '有效幅值计算结果反写位号',
  `opcresource` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '有效幅值opc反写位号源',
  `dampcoeff` double NULL DEFAULT NULL COMMENT '动态阻尼系数\r\n',
  `windowstime` int(11) NULL DEFAULT NULL COMMENT '窗口时间',
  `filtercoeff` double NULL DEFAULT NULL COMMENT '一阶滤波系数',
  `enable` int(11) NULL DEFAULT NULL COMMENT '是否启用',
  `filterbacktodcstag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '滤波后数据反写位号',
  `filteropcresource` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '滤波数据反写位号opc源',
  PRIMARY KEY (`pk_shockdetectid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '震荡检测' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for shockdetect_copy1
-- ----------------------------
DROP TABLE IF EXISTS `shockdetect_copy1`;
CREATE TABLE `shockdetect_copy1`  (
  `pk_shockdetectid` int(11) NOT NULL AUTO_INCREMENT,
  `pk_pinid` int(11) NULL DEFAULT NULL COMMENT '引用的过滤器主键id',
  `backToDCSTag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '有效幅值计算结果反写位号',
  `opcresource` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '有效幅值opc反写位号源',
  `dampcoeff` double NULL DEFAULT NULL COMMENT '动态阻尼系数\r\n',
  `windowstime` int(11) NULL DEFAULT NULL COMMENT '窗口时间',
  `filtercoeff` double NULL DEFAULT NULL COMMENT '一阶滤波系数',
  `enable` int(11) NULL DEFAULT NULL COMMENT '是否启用',
  `filterbacktodcstag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '滤波后数据反写位号',
  `filteropcresource` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '滤波数据反写位号opc源',
  PRIMARY KEY (`pk_shockdetectid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '震荡检测' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

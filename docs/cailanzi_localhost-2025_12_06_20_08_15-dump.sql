-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: cailanzi
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `company`
--

DROP TABLE IF EXISTS `company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `company` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '企业名称',
  `established_date` date DEFAULT NULL COMMENT '企业设立时间',
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `legal_person_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `legal_person_phone` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `freight_pass_contact_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `freight_pass_contact_phone` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `company_type` enum('国有','民营','其他') COLLATE utf8mb4_unicode_ci DEFAULT '民营' COMMENT '企业性质',
  `registered_capital` decimal(12,2) DEFAULT NULL COMMENT '注册资本（万元）',
  `revenue_2022` decimal(14,2) DEFAULT NULL COMMENT '2022年营业收入',
  `revenue_2023` decimal(14,2) DEFAULT NULL COMMENT '2023年营业收入',
  `revenue_2024` decimal(14,2) DEFAULT NULL COMMENT '2024年营业收入',
  `business_scope` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_company_name` (`name`),
  KEY `idx_company_type` (`company_type`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='企业基本信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company`
--

LOCK TABLES `company` WRITE;
/*!40000 ALTER TABLE `company` DISABLE KEYS */;
INSERT INTO `company` VALUES (7,'上海清美绿色食品（集团）有限公司',NULL,'','时培亮','18916160282',NULL,NULL,'民营',NULL,NULL,NULL,NULL,'食品生产、食品经营、道路货物运输等','2025-12-04 07:17:12','2025-12-05 09:56:51'),(14,'上海功德林素食工业有限公司',NULL,'','江浩','13661902000',NULL,NULL,'民营',NULL,NULL,NULL,NULL,'食品制造业','2025-12-04 11:55:22','2025-12-04 11:55:22'),(15,'上海富伊清真食品配送有限公司',NULL,'','李港','18621881869',NULL,NULL,'民营',NULL,NULL,NULL,NULL,'清真牛羊肉及其副品','2025-12-04 11:55:47','2025-12-04 11:55:47'),(16,'上海强丰实业有限公司',NULL,'','刘雪雷','13564112761',NULL,NULL,'民营',NULL,NULL,NULL,NULL,'全品类食堂配送\n','2025-12-04 11:56:07','2025-12-04 11:56:07'),(17,'上海市糖业烟酒（集团）有限公司',NULL,'','顾巍骅','13917600287',NULL,NULL,'民营',NULL,NULL,NULL,NULL,'商业零售、食糖生产及销售、酒类销售等\n','2025-12-04 11:58:16','2025-12-04 11:58:16'),(18,'上海中盐国际物流有限公司',NULL,'','刘永红','13921022558',NULL,NULL,'民营',NULL,NULL,NULL,NULL,'盐和盐化工产品\n','2025-12-04 11:58:34','2025-12-04 11:58:34'),(19,'上海海烟物流发展有限公司',NULL,'','赵佳麟','13918186464',NULL,NULL,'民营',NULL,NULL,NULL,NULL,'卷烟，非烟配送\n','2025-12-04 12:03:25','2025-12-04 12:03:25');
/*!40000 ALTER TABLE `company` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `compliance`
--

DROP TABLE IF EXISTS `compliance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `compliance` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `vehicle_id` bigint NOT NULL,
  `report_year` int NOT NULL COMMENT '报告年份',
  `report_month` int NOT NULL COMMENT '报告月份',
  `illegal_modification` tinyint(1) DEFAULT '0' COMMENT '是否存在违规改装',
  `traffic_violations` int DEFAULT '0' COMMENT '交通违法次数',
  `traffic_accidents` int DEFAULT '0' COMMENT '交通事故次数',
  `violation_details` text COLLATE utf8mb4_unicode_ci,
  `accident_details` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_vehicle_ym` (`vehicle_id`,`report_year`,`report_month`),
  UNIQUE KEY `UKcv96lxlf9ho2rwa9xf3ds038i` (`vehicle_id`,`report_year`,`report_month`),
  KEY `idx_report_period` (`report_year`,`report_month`),
  CONSTRAINT `compliance_ibfk_1` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_compliance_month` CHECK (((`report_month` >= 1) and (`report_month` <= 12)))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='合规运营记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `compliance`
--

LOCK TABLES `compliance` WRITE;
/*!40000 ALTER TABLE `compliance` DISABLE KEYS */;
/*!40000 ALTER TABLE `compliance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `delivery_point`
--

DROP TABLE IF EXISTS `delivery_point`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `delivery_point` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `company_id` bigint NOT NULL,
  `vehicle_id` bigint DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '网点名称',
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `point_type` enum('批发市场','配送中心','超市卖场便利店','餐饮店','商场','菜场','其他') COLLATE utf8mb4_unicode_ci DEFAULT '其他',
  `contact_person` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contact_phone` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_point_type` (`point_type`),
  KEY `idx_company_type` (`company_id`,`point_type`),
  KEY `idx_vehicle` (`vehicle_id`),
  CONSTRAINT `delivery_point_ibfk_1` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`) ON DELETE CASCADE,
  CONSTRAINT `delivery_point_ibfk_2` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='配送网点信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `delivery_point`
--

LOCK TABLES `delivery_point` WRITE;
/*!40000 ALTER TABLE `delivery_point` DISABLE KEYS */;
/*!40000 ALTER TABLE `delivery_point` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feedback`
--

DROP TABLE IF EXISTS `feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedback` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `company_id` bigint NOT NULL,
  `vehicle_id` bigint DEFAULT NULL,
  `report_year` int NOT NULL,
  `report_month` int NOT NULL,
  `main_operational_difficulties` text COLLATE utf8mb4_unicode_ci,
  `policy_suggestions` text COLLATE utf8mb4_unicode_ci,
  `contact_person` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contact_phone` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('待处理','处理中','已处理','已关闭') COLLATE utf8mb4_unicode_ci DEFAULT '待处理',
  `response` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `vehicle_id` (`vehicle_id`),
  KEY `idx_company_period` (`company_id`,`report_year`,`report_month`),
  KEY `idx_status` (`status`),
  CONSTRAINT `feedback_ibfk_1` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`) ON DELETE CASCADE,
  CONSTRAINT `feedback_ibfk_2` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='需求与问题反馈';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedback`
--

LOCK TABLES `feedback` WRITE;
/*!40000 ALTER TABLE `feedback` DISABLE KEYS */;
INSERT INTO `feedback` VALUES (4,14,NULL,2025,12,'无车牌号无法添加\n','','','','待处理','','2025-12-05 17:15:45','2025-12-05 17:16:02'),(5,16,NULL,2025,12,'未填车辆数据\n','','','','待处理','','2025-12-05 17:16:24','2025-12-05 17:16:24'),(6,15,NULL,2025,12,'统一车辆外观、标识及编码，明确经营范畴，杜绝 “山寨” 车。\n建立多部门联动机制，禁止不合理收费，打击无资质流动售卖。\n搭建产销数据平台，精准规划运输线路与备货品类。\n纳入溯源体系，记录产地、轨迹等信息，实现全程可追溯。','','','','待处理','','2025-12-05 17:35:51','2025-12-05 17:37:55'),(7,17,NULL,2025,12,'增加车型选择，方便各企业结合实际使用情况作出选择。\n','','','','待处理','','2025-12-05 17:38:19','2025-12-05 17:38:19'),(8,7,NULL,2025,12,'多给点菜篮子额度\n','','','','待处理','','2025-12-05 17:38:45','2025-12-05 17:38:45');
/*!40000 ALTER TABLE `feedback` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `maintenance`
--

DROP TABLE IF EXISTS `maintenance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `maintenance` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `vehicle_id` bigint NOT NULL,
  `maint_date` date NOT NULL COMMENT '维护日期',
  `maint_type` enum('日常保养','定期维护','故障维修','年度检测','其他') COLLATE utf8mb4_unicode_ci DEFAULT '日常保养',
  `cost` decimal(12,2) DEFAULT '0.00' COMMENT '费用',
  `description` text COLLATE utf8mb4_unicode_ci,
  `service_provider` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '服务提供商',
  `next_maint_date` date DEFAULT NULL COMMENT '下次维护日期',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_maint_date` (`maint_date`),
  KEY `idx_vehicle_date` (`vehicle_id`,`maint_date`),
  CONSTRAINT `maintenance_ibfk_1` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车辆维护保养记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `maintenance`
--

LOCK TABLES `maintenance` WRITE;
/*!40000 ALTER TABLE `maintenance` DISABLE KEYS */;
/*!40000 ALTER TABLE `maintenance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_type`
--

DROP TABLE IF EXISTS `product_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_type` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type_code` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `display_order` int DEFAULT '0' COMMENT '显示顺序',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_type_code` (`type_code`),
  KEY `idx_display_order` (`display_order`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品类型字典';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_type`
--

LOCK TABLES `product_type` WRITE;
/*!40000 ALTER TABLE `product_type` DISABLE KEYS */;
INSERT INTO `product_type` VALUES (1,'vegetable','蔬菜',1,1,'2025-11-08 13:35:38',NULL),(2,'meat','肉类',2,1,'2025-11-08 13:35:38',NULL),(3,'poultry_egg','禽蛋',3,1,'2025-11-08 13:35:38',NULL),(4,'aquatic','水产',4,1,'2025-11-08 13:35:38',NULL),(5,'fruit','水果',5,1,'2025-11-08 13:35:38',NULL),(6,'beverage','饮料',6,1,'2025-11-08 13:35:38',NULL),(7,'nuts','炒货',7,1,'2025-11-08 13:35:38',NULL),(8,'other','其他',8,1,'2025-11-08 13:35:38',NULL);
/*!40000 ALTER TABLE `product_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report_submission`
--

DROP TABLE IF EXISTS `report_submission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `report_submission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `company_id` bigint NOT NULL,
  `submitted_by` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `source_platform` enum('微信小程序','后台导入','管理员录入') COLLATE utf8mb4_unicode_ci DEFAULT '微信小程序',
  `submit_type` enum('企业信息','车辆信息','运输统计','维护记录','合规记录','反馈信息') COLLATE utf8mb4_unicode_ci DEFAULT '运输统计',
  `reference_id` bigint DEFAULT NULL COMMENT '关联记录ID',
  `submit_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `raw_payload` json DEFAULT NULL COMMENT '原始提交数据',
  `ip_address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_agent` text COLLATE utf8mb4_unicode_ci,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_submit_time` (`submit_time`),
  KEY `idx_company_type` (`company_id`,`submit_type`),
  KEY `idx_platform` (`source_platform`),
  CONSTRAINT `report_submission_ibfk_1` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据填报记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report_submission`
--

LOCK TABLES `report_submission` WRITE;
/*!40000 ALTER TABLE `report_submission` DISABLE KEYS */;
/*!40000 ALTER TABLE `report_submission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `route_point`
--

DROP TABLE IF EXISTS `route_point`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `route_point` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `vehicle_id` bigint NOT NULL,
  `route_date` date NOT NULL COMMENT '路线日期',
  `route_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `seq` int NOT NULL COMMENT '到达顺序',
  `address` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `point_type` enum('批发市场','配送中心','超市卖场便利店','餐饮店','商场','菜场','其他') COLLATE utf8mb4_unicode_ci DEFAULT '其他',
  `arrive_time` datetime DEFAULT NULL COMMENT '抵达时间',
  `description` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_route_seq` (`route_id`,`seq`),
  UNIQUE KEY `UKk30903n5exwvlmqj0d2rxr3mx` (`route_id`,`seq`),
  KEY `idx_vehicle_date` (`vehicle_id`,`route_date`),
  KEY `idx_route` (`route_id`),
  CONSTRAINT `route_point_ibfk_1` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车辆路线信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `route_point`
--

LOCK TABLES `route_point` WRITE;
/*!40000 ALTER TABLE `route_point` DISABLE KEYS */;
/*!40000 ALTER TABLE `route_point` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_config`
--

DROP TABLE IF EXISTS `system_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `config_key` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `config_value` text COLLATE utf8mb4_unicode_ci,
  `config_group` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '配置描述',
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_config_key` (`config_key`),
  KEY `idx_config_group` (`config_group`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_config`
--

LOCK TABLES `system_config` WRITE;
/*!40000 ALTER TABLE `system_config` DISABLE KEYS */;
INSERT INTO `system_config` VALUES (1,'system.name','菜篮子工程车填报和展示系统','general','系统名称',1,'2025-11-08 13:35:38','2025-11-08 13:35:38'),(2,'report.period','monthly','report','报表周期',1,'2025-11-08 13:35:38','2025-11-08 13:35:38'),(3,'vehicle.emission_standards','国三,国四,国五,国六,新能源','vehicle','排放标准选项',1,'2025-11-08 13:35:38','2025-11-08 13:35:38'),(4,'max_file_size','10485760','upload','最大文件上传大小(字节)',1,'2025-11-08 13:35:38','2025-11-08 13:35:38'),(5,'default_report_year','2025','report','默认统计年份',1,'2025-11-08 13:35:38','2025-11-08 13:35:38');
/*!40000 ALTER TABLE `system_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transport_stats`
--

DROP TABLE IF EXISTS `transport_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transport_stats` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `vehicle_id` bigint NOT NULL,
  `stat_year` int NOT NULL COMMENT '统计年份',
  `stat_month` int NOT NULL COMMENT '统计月份',
  `daily_delivery_times` decimal(5,2) DEFAULT '0.00' COMMENT '日配送次数（次/天）',
  `month_product_ton` decimal(10,2) DEFAULT '0.00' COMMENT '月运输产品数量（吨/月）',
  `month_kilometers` decimal(12,2) DEFAULT '0.00' COMMENT '月运输里程数（公里/月）',
  `month_delivery_cost` decimal(12,2) DEFAULT NULL COMMENT '平均每月配送费用（元/月）',
  `daily_delivery_points` int DEFAULT NULL COMMENT '平均每日配送网点数（个/天）',
  `peak_season_daily_increase_times` int DEFAULT NULL COMMENT '旺季每日增加配送次数（次/天）',
  `product_types` json DEFAULT NULL COMMENT '运输产品品种，JSON数组',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_vehicle_ym` (`vehicle_id`,`stat_year`,`stat_month`),
  UNIQUE KEY `UKgmkhreu35h8vq9kuaegp4xsg6` (`vehicle_id`,`stat_year`,`stat_month`),
  KEY `idx_year_month` (`stat_year`,`stat_month`),
  CONSTRAINT `transport_stats_ibfk_1` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_month_range` CHECK (((`stat_month` >= 1) and (`stat_month` <= 12)))
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='运输状况统计';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transport_stats`
--

LOCK TABLES `transport_stats` WRITE;
/*!40000 ALTER TABLE `transport_stats` DISABLE KEYS */;
INSERT INTO `transport_stats` VALUES (4,110,2025,12,1.00,40.00,2000.00,10000.00,3,5,'[]','2025-12-05 18:05:05','2025-12-05 18:05:05'),(5,99,2025,12,3.00,120.00,3500.00,NULL,NULL,NULL,'[]','2025-12-05 18:07:45','2025-12-05 18:07:45'),(6,100,2025,12,3.00,120.00,3500.00,NULL,NULL,NULL,'[]','2025-12-05 18:08:35','2025-12-05 18:08:50'),(7,101,2025,12,3.00,130.00,3500.00,NULL,NULL,NULL,'[]','2025-12-05 18:09:48','2025-12-05 18:09:48'),(8,102,2025,12,2.66,120.00,3200.00,NULL,NULL,NULL,'[]','2025-12-05 18:11:06','2025-12-05 18:11:06'),(9,103,2025,12,3.00,120.00,3500.00,NULL,NULL,NULL,'[]','2025-12-05 18:11:31','2025-12-05 18:11:31'),(10,104,2025,12,3.00,120.00,3000.00,NULL,NULL,NULL,'[]','2025-12-05 18:12:00','2025-12-05 18:12:00'),(11,105,2025,12,3.33,150.00,3800.00,NULL,NULL,NULL,'[]','2025-12-05 18:12:24','2025-12-05 18:12:24'),(12,106,2025,12,3.33,150.00,3800.00,NULL,NULL,NULL,'[]','2025-12-05 18:12:54','2025-12-05 18:12:54'),(13,66,2025,12,1.00,80.00,2800.00,0.00,16,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(14,67,2025,12,1.00,80.00,2800.00,0.00,18,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(15,68,2025,12,1.00,80.00,4800.00,1600.00,26,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(16,69,2025,12,1.00,80.00,2800.00,0.00,20,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(17,70,2025,12,1.00,80.00,2800.00,0.00,22,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(18,71,2025,12,1.00,80.00,2500.00,0.00,21,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(19,72,2025,12,1.00,80.00,4500.00,1200.00,24,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(20,73,2025,12,1.00,80.00,2500.00,0.00,21,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(21,74,2025,12,1.00,80.00,2600.00,0.00,20,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(22,75,2025,12,1.00,26.00,3000.00,0.00,19,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(23,76,2025,12,1.00,26.00,4300.00,2100.00,16,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(24,77,2025,12,1.00,80.00,3000.00,1000.00,2,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(25,78,2025,12,1.00,80.00,6000.00,0.00,18,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(26,79,2025,12,1.00,80.00,9400.00,0.00,11,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(27,80,2025,12,1.00,80.00,4200.00,0.00,12,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(28,81,2025,12,1.00,80.00,4500.00,7600.00,10,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(29,82,2025,12,1.00,80.00,6200.00,0.00,12,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(30,83,2025,12,1.00,80.00,4000.00,0.00,4,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(31,84,2025,12,1.00,80.00,3200.00,0.00,13,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(32,85,2025,12,1.00,80.00,5200.00,0.00,10,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(33,86,2025,12,1.00,80.00,4200.00,0.00,12,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(34,87,2025,12,1.00,200.00,3500.00,1500.00,1,2,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(35,88,2025,12,1.00,200.00,3500.00,5000.00,1,2,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(36,89,2025,12,1.00,80.00,3500.00,0.00,24,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(37,90,2025,12,1.00,80.00,3000.00,0.00,20,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(38,91,2025,12,1.00,80.00,3100.00,0.00,21,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(39,92,2025,12,1.00,80.00,3400.00,0.00,20,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(40,93,2025,12,1.00,80.00,3000.00,0.00,22,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(41,94,2025,12,1.00,80.00,5800.00,1000.00,23,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(42,95,2025,12,1.00,80.00,4200.00,0.00,20,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(43,96,2025,12,1.00,80.00,3500.00,0.00,20,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(44,97,2025,12,1.00,80.00,3400.00,0.00,18,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(45,98,2025,12,1.00,80.00,3400.00,0.00,21,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02');
/*!40000 ALTER TABLE `transport_stats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `nickname` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` enum('ADMIN','COMPANY','DRIVER') COLLATE utf8mb4_unicode_ci DEFAULT 'COMPANY' COMMENT '角色：管理员、企业用户、司机用户',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `company_id` bigint DEFAULT NULL,
  `vehicle_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`),
  KEY `idx_company` (`company_id`),
  KEY `idx_vehicle` (`vehicle_id`),
  CONSTRAINT `users_ibfk_1` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`) ON DELETE SET NULL,
  CONSTRAINT `users_ibfk_2` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle` (`id`) ON DELETE SET NULL,
  CONSTRAINT `users_ibfk_3` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`) ON DELETE SET NULL,
  CONSTRAINT `users_ibfk_4` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,NULL,'2025-11-15 01:33:24.531671','',_binary '','管理员','96cae35ce8a9b0244178bf28e4966c2ce1b8385723a96a6b838858cdd6ca0a1e','ADMIN','admin','',NULL,NULL),(4,'2025-11-15 01:18:44.801016','2025-11-15 01:32:29.008871','d1@qq.com',_binary '','d1','96cae35ce8a9b0244178bf28e4966c2ce1b8385723a96a6b838858cdd6ca0a1e','DRIVER','D1','12345678909',NULL,NULL),(5,'2025-11-15 01:33:52.035621','2025-11-15 01:33:52.035621','',_binary '','','96cae35ce8a9b0244178bf28e4966c2ce1b8385723a96a6b838858cdd6ca0a1e','COMPANY','C1','',NULL,NULL),(6,'2025-12-05 17:56:25.015739','2025-12-05 17:56:25.015739','',_binary '','','96cae35ce8a9b0244178bf28e4966c2ce1b8385723a96a6b838858cdd6ca0a1e','COMPANY','qm','',7,NULL),(7,'2025-12-06 01:36:21.213283','2025-12-06 01:36:21.213283','',_binary '','','96cae35ce8a9b0244178bf28e4966c2ce1b8385723a96a6b838858cdd6ca0a1e','COMPANY','fy','',15,NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `v_company_vehicle_summary`
--

DROP TABLE IF EXISTS `v_company_vehicle_summary`;
/*!50001 DROP VIEW IF EXISTS `v_company_vehicle_summary`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_company_vehicle_summary` AS SELECT 
 1 AS `company_id`,
 1 AS `company_name`,
 1 AS `company_type`,
 1 AS `total_vehicles`,
 1 AS `regular_basket_count`,
 1 AS `cold_basket_count`,
 1 AS `non_basket_count`,
 1 AS `emission_standard_count`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_compliance_report`
--

DROP TABLE IF EXISTS `v_compliance_report`;
/*!50001 DROP VIEW IF EXISTS `v_compliance_report`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_compliance_report` AS SELECT 
 1 AS `company_name`,
 1 AS `plate_number`,
 1 AS `report_year`,
 1 AS `report_month`,
 1 AS `traffic_violations`,
 1 AS `traffic_accidents`,
 1 AS `illegal_modification`,
 1 AS `maintenance_count`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_vehicle_transport_stats`
--

DROP TABLE IF EXISTS `v_vehicle_transport_stats`;
/*!50001 DROP VIEW IF EXISTS `v_vehicle_transport_stats`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_vehicle_transport_stats` AS SELECT 
 1 AS `vehicle_id`,
 1 AS `plate_number`,
 1 AS `company_name`,
 1 AS `stat_year`,
 1 AS `stat_month`,
 1 AS `daily_delivery_times`,
 1 AS `month_product_ton`,
 1 AS `month_kilometers`,
 1 AS `delivery_points_count`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `vehicle`
--

DROP TABLE IF EXISTS `vehicle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vehicle` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `company_id` bigint NOT NULL,
  `plate_number` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `vehicle_category` enum('菜篮子工程车','非菜篮子工程车') COLLATE utf8mb4_unicode_ci DEFAULT '菜篮子工程车',
  `vehicle_type` enum('普通','冷藏') COLLATE utf8mb4_unicode_ci DEFAULT '普通',
  `color_plate` enum('蓝牌','黄牌') COLLATE utf8mb4_unicode_ci DEFAULT '蓝牌',
  `emission_standard` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `approved_load` decimal(8,2) DEFAULT NULL COMMENT '核定载质量（吨）',
  `purchase_date` date DEFAULT NULL COMMENT '购置时间',
  `vin` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `engine_no` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `driving_license_data` json DEFAULT NULL COMMENT '行驶证识别结果',
  `data_version` int DEFAULT '1' COMMENT '数据版本，用于追踪变更',
  `last_updated_reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后更新原因',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否有效',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_plate_company` (`company_id`,`plate_number`),
  UNIQUE KEY `UKshlu51oawoorbqfp42cvkbkp9` (`company_id`,`plate_number`),
  KEY `idx_vehicle_category` (`vehicle_category`),
  KEY `idx_vehicle_type` (`vehicle_type`),
  KEY `idx_emission` (`emission_standard`),
  KEY `idx_active` (`is_active`),
  CONSTRAINT `vehicle_ibfk_1` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=126 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车辆基本信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vehicle`
--

LOCK TABLES `vehicle` WRITE;
/*!40000 ALTER TABLE `vehicle` DISABLE KEYS */;
INSERT INTO `vehicle` VALUES (66,7,'沪DP8755','菜篮子工程车','冷藏','黄牌','国5',0.00,NULL,'','',NULL,4,NULL,1,'2025-12-04 12:17:33','2025-12-05 16:49:44'),(67,7,'沪DP8780','菜篮子工程车','冷藏','黄牌','国5',0.00,NULL,'','',NULL,3,NULL,1,'2025-12-04 12:19:52','2025-12-05 16:49:40'),(68,7,'沪DP8825','菜篮子工程车','冷藏','黄牌','国5',0.00,NULL,'','',NULL,3,NULL,1,'2025-12-04 12:20:19','2025-12-05 16:49:36'),(69,7,'沪DP8827','菜篮子工程车','冷藏','黄牌','国5',0.00,NULL,'','',NULL,2,NULL,1,'2025-12-05 16:25:08','2025-12-05 16:49:24'),(70,7,'沪DP8850','菜篮子工程车','冷藏','黄牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(71,7,'沪DP9005','菜篮子工程车','冷藏','黄牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(72,7,'沪DP9007','菜篮子工程车','冷藏','黄牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(73,7,'沪DP9016','菜篮子工程车','冷藏','黄牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(74,7,'沪DP9030','菜篮子工程车','冷藏','黄牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(75,7,'沪BQN083','菜篮子工程车','冷藏','蓝牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(76,7,'沪BRK938','菜篮子工程车','冷藏','蓝牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(77,7,'沪GE7208','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(78,7,'沪GF2261','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(79,7,'沪GF2956','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(80,7,'沪GF8625','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(81,7,'沪GE1817','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(82,7,'沪GE6202','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(83,7,'沪GF0596','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(84,7,'沪DS4719','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(85,7,'沪GE8783','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(86,7,'沪GF0809','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(87,7,'沪GG5366','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(88,7,'沪GG7816','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(89,7,'沪EP4993','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(90,7,'沪GG6051','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(91,7,'沪GG6577','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(92,7,'沪GG7635','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(93,7,'沪GG7813','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(94,7,'沪GH8722','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(95,7,'沪GG9537','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(96,7,'沪GG7682','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(97,7,'沪GG5922','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(98,7,'沪GG2078','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(99,18,'沪NH5592','菜篮子工程车','普通','蓝牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 17:00:43','2025-12-05 17:00:43'),(100,18,'沪DP3041','菜篮子工程车','普通','黄牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 17:00:43','2025-12-05 17:00:43'),(101,18,'沪BKE877','菜篮子工程车','普通','蓝牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 17:00:43','2025-12-05 17:00:43'),(102,18,'沪FUC567','菜篮子工程车','普通','蓝牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 17:00:43','2025-12-05 17:00:43'),(103,18,'沪DP3466','菜篮子工程车','普通','黄牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 17:00:43','2025-12-05 17:00:43'),(104,18,'沪BKE865','菜篮子工程车','普通','蓝牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 17:00:43','2025-12-05 17:00:43'),(105,18,'沪BWV839','菜篮子工程车','普通','蓝牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 17:00:43','2025-12-05 17:00:43'),(106,18,'沪BZX981','菜篮子工程车','普通','蓝牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 17:00:43','2025-12-05 17:00:43'),(107,17,'沪DS7376','菜篮子工程车','普通','黄牌',NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 17:06:54','2025-12-05 17:06:54'),(108,17,'沪EJ0517','菜篮子工程车','普通','黄牌',NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 17:06:54','2025-12-05 17:06:54'),(109,17,'沪BZB836','菜篮子工程车','冷藏','蓝牌',NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 17:06:54','2025-12-05 17:06:54'),(110,15,'沪EQG088','菜篮子工程车','冷藏','蓝牌','国6A',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 17:12:09','2025-12-05 17:12:09'),(112,19,'沪BBE556','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-10-31','LSH14JTC8FA235768','R915A017670',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27'),(113,19,'沪BBE305','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-10-30','LSH14JTCXFA235772','R915A017675',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27'),(114,19,'沪BBE693','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-10-31','LSH14JTC5FA235761','R915A017667',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27'),(115,19,'沪BBE678','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-10-30','LSH14JTC3FA235774','R915A017674',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27'),(116,19,'沪BBE233','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-10-30','LSH14JTC7FA235776','R915A017658',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27'),(117,19,'沪BBE728','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-10-31','LSH14JTC7FA235762','R915A017657',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27'),(118,19,'沪BBE201','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-10-31','LSH14JTCXFA235769','R915A017666',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27'),(119,19,'沪BCC507','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-10-30','LSH14JTC1FA235773','R915A017661',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27'),(120,19,'沪BBE085','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-10-30','LSH14JTC6FA235770','R915A017665',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27'),(121,19,'沪BBE187','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-10-30','LSH14JTC5FA235775','R915A017652',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27'),(122,19,'沪BBE623','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-10-31','LSH14JTC6FA235767','R915A017672',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27'),(123,19,'沪BBE367','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-10-30','LSH14JTC4FA235766','R915A017671',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27'),(124,19,'沪BBH005','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-11-16','LSH14JTCXFA235755','R915A017664',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27'),(125,19,'沪AZP650','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-11-12','LSH14JTC3FA235757','R915A017659',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27');
/*!40000 ALTER TABLE `vehicle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vehicle_statistics`
--

DROP TABLE IF EXISTS `vehicle_statistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vehicle_statistics` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `company_id` bigint NOT NULL,
  `stat_date` date NOT NULL COMMENT '统计日期',
  `regular_basket_blue` int DEFAULT '0' COMMENT '普通菜篮子工程车-蓝牌',
  `regular_basket_yellow` int DEFAULT '0' COMMENT '普通菜篮子工程车-黄牌',
  `cold_basket_blue` int DEFAULT '0' COMMENT '冷藏菜篮子工程车-蓝牌',
  `cold_basket_yellow` int DEFAULT '0' COMMENT '冷藏菜篮子工程车-黄牌',
  `basket_emission_standard` int DEFAULT '0' COMMENT '菜篮子工程车国五以上数量',
  `regular_freight_blue` int DEFAULT '0' COMMENT '普通货运车-蓝牌',
  `regular_freight_yellow` int DEFAULT '0' COMMENT '普通货运车-黄牌',
  `cold_freight_blue` int DEFAULT '0' COMMENT '冷藏货运车-蓝牌',
  `cold_freight_yellow` int DEFAULT '0' COMMENT '冷藏货运车-黄牌',
  `freight_emission_standard` int DEFAULT '0' COMMENT '非菜篮子工程车国五以上数量',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_company_date` (`company_id`,`stat_date`),
  UNIQUE KEY `UKeon6y1kbu7n9x4g594tfrpu2y` (`company_id`,`stat_date`),
  KEY `idx_stat_date` (`stat_date`),
  CONSTRAINT `vehicle_statistics_ibfk_1` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车辆数量统计';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vehicle_statistics`
--

LOCK TABLES `vehicle_statistics` WRITE;
/*!40000 ALTER TABLE `vehicle_statistics` DISABLE KEYS */;
INSERT INTO `vehicle_statistics` VALUES (18,7,'2025-12-06',0,0,2,31,0,0,0,0,0,0,'2025-12-05 17:07:31','2025-12-05 17:07:31'),(19,18,'2025-12-06',6,2,0,0,0,0,0,0,0,0,'2025-12-05 17:07:58','2025-12-05 17:07:58');
/*!40000 ALTER TABLE `vehicle_statistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Final view structure for view `v_company_vehicle_summary`
--

/*!50001 DROP VIEW IF EXISTS `v_company_vehicle_summary`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_company_vehicle_summary` AS select `c`.`id` AS `company_id`,`c`.`name` AS `company_name`,`c`.`company_type` AS `company_type`,count(`v`.`id`) AS `total_vehicles`,sum((case when ((`v`.`vehicle_category` = '菜篮子工程车') and (`v`.`vehicle_type` = '普通')) then 1 else 0 end)) AS `regular_basket_count`,sum((case when ((`v`.`vehicle_category` = '菜篮子工程车') and (`v`.`vehicle_type` = '冷藏')) then 1 else 0 end)) AS `cold_basket_count`,sum((case when (`v`.`vehicle_category` = '非菜篮子工程车') then 1 else 0 end)) AS `non_basket_count`,sum((case when (`v`.`emission_standard` in ('国五','国六','新能源')) then 1 else 0 end)) AS `emission_standard_count` from (`company` `c` left join `vehicle` `v` on(((`v`.`company_id` = `c`.`id`) and (`v`.`is_active` = true)))) group by `c`.`id`,`c`.`name`,`c`.`company_type` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_compliance_report`
--

/*!50001 DROP VIEW IF EXISTS `v_compliance_report`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_compliance_report` AS select `c`.`name` AS `company_name`,`v`.`plate_number` AS `plate_number`,`comp`.`report_year` AS `report_year`,`comp`.`report_month` AS `report_month`,`comp`.`traffic_violations` AS `traffic_violations`,`comp`.`traffic_accidents` AS `traffic_accidents`,`comp`.`illegal_modification` AS `illegal_modification`,(select count(`m`.`id`) from `maintenance` `m` where ((`m`.`vehicle_id` = `v`.`id`) and (year(`m`.`maint_date`) = `comp`.`report_year`) and (month(`m`.`maint_date`) = `comp`.`report_month`))) AS `maintenance_count` from ((`compliance` `comp` join `vehicle` `v` on((`comp`.`vehicle_id` = `v`.`id`))) join `company` `c` on((`v`.`company_id` = `c`.`id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_vehicle_transport_stats`
--

/*!50001 DROP VIEW IF EXISTS `v_vehicle_transport_stats`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_vehicle_transport_stats` AS select `v`.`id` AS `vehicle_id`,`v`.`plate_number` AS `plate_number`,`c`.`name` AS `company_name`,`ts`.`stat_year` AS `stat_year`,`ts`.`stat_month` AS `stat_month`,`ts`.`daily_delivery_times` AS `daily_delivery_times`,`ts`.`month_product_ton` AS `month_product_ton`,`ts`.`month_kilometers` AS `month_kilometers`,(select count(distinct `dp`.`id`) from `delivery_point` `dp` where ((`dp`.`vehicle_id` = `v`.`id`) and (`dp`.`is_active` = true))) AS `delivery_points_count` from ((`vehicle` `v` join `company` `c` on((`v`.`company_id` = `c`.`id`))) left join `transport_stats` `ts` on((`ts`.`vehicle_id` = `v`.`id`))) where (`v`.`is_active` = true) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-06 20:08:16

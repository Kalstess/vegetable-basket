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
-- Table structure for table `audit_log`
--

DROP TABLE IF EXISTS `audit_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `action` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `detail` text COLLATE utf8mb4_unicode_ci,
  `operator_role` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `operator_username` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `target_id` bigint DEFAULT NULL,
  `target_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_log`
--

LOCK TABLES `audit_log` WRITE;
/*!40000 ALTER TABLE `audit_log` DISABLE KEYS */;
INSERT INTO `audit_log` VALUES (1,'2025-12-16 21:43:47.024929','2025-12-16 21:43:47.024929','CREATE','创建用户，username=shang1, role=BUSINESS_COMMISSION','ADMIN','admin',8,'USER'),(2,'2025-12-16 22:40:41.100918','2025-12-16 22:40:41.100918','UPDATE','更新用户信息，username=shang1','ADMIN','admin',8,'USER'),(3,'2025-12-16 22:49:36.957906','2025-12-16 22:49:36.957906','UPDATE','更新用户信息，username=C1','ADMIN','admin',5,'USER'),(4,'2025-12-16 22:50:03.511598','2025-12-16 22:50:03.511598','CREATE','创建用户，username=qm1, role=COMPANY_USER','COMPANY_ADMIN','C1',9,'USER'),(5,'2025-12-16 22:50:38.594701','2025-12-16 22:50:38.594701','UPDATE','更新用户信息，username=qm1','COMPANY','qm',9,'USER'),(6,'2025-12-16 23:17:23.830064','2025-12-16 23:17:23.830064','DELETE','删除用户','ADMIN','admin',4,'USER'),(7,'2025-12-16 23:17:25.859570','2025-12-16 23:17:25.859570','DELETE','删除用户','ADMIN','admin',5,'USER'),(8,'2025-12-16 23:17:27.464609','2025-12-16 23:17:27.464609','DELETE','删除用户','ADMIN','admin',6,'USER'),(9,'2025-12-16 23:17:28.904636','2025-12-16 23:17:28.904636','DELETE','删除用户','ADMIN','admin',7,'USER'),(10,'2025-12-16 23:17:30.332980','2025-12-16 23:17:30.332980','DELETE','删除用户','ADMIN','admin',8,'USER'),(11,'2025-12-16 23:17:31.917619','2025-12-16 23:17:31.917619','DELETE','删除用户','ADMIN','admin',9,'USER'),(12,'2025-12-16 23:18:03.532859','2025-12-16 23:18:03.532859','CREATE','创建用户，username=admin2, role=ADMIN','ADMIN','admin',10,'USER'),(13,'2025-12-16 23:19:07.301605','2025-12-16 23:19:07.301605','DELETE','删除用户','ADMIN','admin2',10,'USER'),(14,'2025-12-16 23:20:35.370335','2025-12-16 23:20:35.370335','CREATE','创建用户，username=admin2, role=ADMIN','ADMIN','admin',11,'USER'),(15,'2025-12-16 23:21:11.851891','2025-12-16 23:21:11.851891','CREATE','创建用户，username=business_commission1, role=BUSINESS_COMMISSION','ADMIN','admin',12,'USER'),(16,'2025-12-16 23:36:18.904234','2025-12-16 23:36:18.904234','DELETE','删除用户','ADMIN','admin2',11,'USER'),(17,'2025-12-16 23:37:37.011610','2025-12-16 23:37:37.011610','CREATE','创建用户，username=admin2, role=ADMIN','ADMIN','admin',13,'USER'),(18,'2025-12-16 23:37:54.659426','2025-12-16 23:37:54.659426','DELETE','删除用户','ADMIN','admin2',13,'USER'),(19,'2025-12-16 23:38:45.581694','2025-12-16 23:38:45.581694','CREATE','创建用户，username=admin2, role=ADMIN','ADMIN','admin',14,'USER'),(20,'2025-12-16 23:41:51.477332','2025-12-16 23:41:51.477332','REGISTER','企业自助注册，名称=测试企业A',NULL,NULL,20,'COMPANY'),(21,'2025-12-16 23:42:33.988439','2025-12-16 23:42:33.988439','APPROVE','审批通过，备注=审核通过，企业资质齐全','BUSINESS_COMMISSION','business_commission1',20,'COMPANY'),(22,'2025-12-16 23:44:53.606493','2025-12-16 23:44:53.606493','UPDATE','更新用户信息，username=company_admin_a','ADMIN','admin',15,'USER'),(23,'2025-12-16 23:45:32.356049','2025-12-16 23:45:32.356049','REGISTER','企业自助注册，名称=1',NULL,NULL,21,'COMPANY'),(24,'2025-12-16 23:46:00.936816','2025-12-16 23:46:00.936816','APPROVE','审批通过，备注=','BUSINESS_COMMISSION','business_commission1',21,'COMPANY'),(25,'2025-12-16 23:46:36.965024','2025-12-16 23:46:36.965024','UPDATE','更新用户信息，username=1','ADMIN','admin',16,'USER'),(26,'2025-12-16 23:47:06.297822','2025-12-16 23:47:06.297822','DELETE','删除用户','ADMIN','admin',16,'USER'),(27,'2025-12-16 23:58:50.640380','2025-12-16 23:58:50.640380','REGISTER','企业自助注册，名称=1',NULL,NULL,22,'COMPANY'),(28,'2025-12-16 23:58:59.028574','2025-12-16 23:58:59.028574','APPROVE','审批通过，备注=','BUSINESS_COMMISSION','business_commission1',22,'COMPANY'),(29,'2025-12-17 00:00:05.955477','2025-12-17 00:00:05.955477','REGISTER','企业自助注册，名称=2',NULL,NULL,23,'COMPANY'),(30,'2025-12-17 00:05:52.975866','2025-12-17 00:05:52.975866','APPROVE','审批通过，备注=','BUSINESS_COMMISSION','business_commission1',23,'COMPANY'),(31,'2025-12-17 00:16:57.237226','2025-12-17 00:16:57.237226','REGISTER','企业自助注册，名称=3',NULL,NULL,24,'COMPANY'),(32,'2025-12-17 00:17:23.420665','2025-12-17 00:17:23.420665','REJECT','审批驳回，备注=no','BUSINESS_COMMISSION','business_commission1',24,'COMPANY'),(33,'2025-12-17 00:18:45.858962','2025-12-17 00:18:45.858962','DELETE','删除用户','ADMIN','admin',19,'USER'),(34,'2025-12-17 00:18:46.974730','2025-12-17 00:18:46.974730','DELETE','删除用户','ADMIN','admin',18,'USER'),(35,'2025-12-17 00:18:48.828572','2025-12-17 00:18:48.828572','DELETE','删除用户','ADMIN','admin',17,'USER'),(36,'2025-12-17 00:21:44.377568','2025-12-17 00:21:44.377568','CREATE','创建用户，username=company_user1, role=COMPANY_USER','COMPANY_ADMIN','company_admin_a',20,'USER'),(37,'2025-12-17 00:32:50.563812','2025-12-17 00:32:50.563812','REGISTER','企业自助注册，名称=1',NULL,NULL,25,'COMPANY'),(38,'2025-12-17 00:33:01.160416','2025-12-17 00:33:01.160416','APPROVE','审批通过，备注=','ADMIN','admin',25,'COMPANY'),(39,'2025-12-17 00:33:12.744050','2025-12-17 00:33:12.744050','REGISTER','企业自助注册，名称=2',NULL,NULL,26,'COMPANY'),(40,'2025-12-17 00:33:22.468717','2025-12-17 00:33:22.468717','REJECT','审批驳回，备注=','ADMIN','admin',26,'COMPANY'),(41,'2025-12-17 00:35:23.942914','2025-12-17 00:35:23.942914','DELETE','删除用户','ADMIN','admin',22,'USER'),(42,'2025-12-17 00:35:25.296554','2025-12-17 00:35:25.296554','DELETE','删除用户','ADMIN','admin',21,'USER'),(43,'2025-12-17 00:36:36.873163','2025-12-17 00:36:36.873163','REGISTER','企业自助注册，名称=2',NULL,NULL,27,'COMPANY'),(44,'2025-12-17 00:36:44.837678','2025-12-17 00:36:44.837678','REJECT','审批驳回，备注=','ADMIN','admin',27,'COMPANY'),(45,'2025-12-17 00:37:39.419013','2025-12-17 00:37:39.419013','DELETE','删除用户','ADMIN','admin',23,'USER'),(46,'2025-12-17 00:37:59.080097','2025-12-17 00:37:59.080097','REGISTER','企业自助注册，名称=1',NULL,NULL,28,'COMPANY'),(47,'2025-12-17 00:38:11.760150','2025-12-17 00:38:11.760150','REJECT','审批驳回并删除，企业名称=1，备注=','ADMIN','admin',28,'COMPANY'),(48,'2025-12-17 14:22:28.389518','2025-12-17 14:22:28.389518','CREATE','创建用户，username=driver1, role=DRIVER','COMPANY_ADMIN','company_admin_a',25,'USER'),(49,'2025-12-17 14:36:32.241591','2025-12-17 14:36:32.241591','REGISTER','企业自助注册，名称=string',NULL,NULL,29,'COMPANY'),(50,'2025-12-17 14:38:01.136987','2025-12-17 14:38:01.136987','REGISTER','企业自助注册，名称=API测试企业',NULL,NULL,30,'COMPANY'),(51,'2025-12-17 14:38:13.990586','2025-12-17 14:38:13.990586','APPROVE','审批通过，备注=','ADMIN','admin',30,'COMPANY'),(52,'2025-12-17 14:38:18.050301','2025-12-17 14:38:18.050301','REJECT','审批驳回并删除，企业名称=string，备注=','ADMIN','admin',29,'COMPANY'),(53,'2025-12-17 14:47:25.434023','2025-12-17 14:47:25.434023','REGISTER','企业自助注册，名称=1',NULL,NULL,31,'COMPANY'),(54,'2025-12-17 14:47:40.017381','2025-12-17 14:47:40.017381','APPROVE','审批通过，备注=','ADMIN','admin',31,'COMPANY'),(55,'2025-12-17 14:47:55.065736','2025-12-17 14:47:55.065736','DELETE','删除用户','ADMIN','admin',28,'USER'),(56,'2025-12-17 14:49:02.517019','2025-12-17 14:49:02.517019','REGISTER','企业自助注册，名称=string',NULL,NULL,32,'COMPANY'),(57,'2025-12-17 14:49:10.328518','2025-12-17 14:49:10.328518','APPROVE','审批通过，备注=','ADMIN','admin',32,'COMPANY'),(58,'2025-12-17 14:49:15.767286','2025-12-17 14:49:15.767286','DELETE','删除用户','ADMIN','admin',29,'USER'),(59,'2025-12-17 14:49:28.877232','2025-12-17 14:49:28.877232','REGISTER','企业自助注册，名称=1',NULL,NULL,33,'COMPANY'),(60,'2025-12-17 14:49:36.073369','2025-12-17 14:49:36.073369','APPROVE','审批通过，备注=','ADMIN','admin',33,'COMPANY'),(61,'2025-12-17 14:49:39.433502','2025-12-17 14:49:39.433502','DELETE','删除用户','ADMIN','admin',30,'USER'),(62,'2025-12-17 14:54:01.084985','2025-12-17 14:54:01.084985','REGISTER','企业自助注册，名称=string',NULL,NULL,34,'COMPANY'),(63,'2025-12-20 13:23:56.689704','2025-12-20 13:23:56.689704','REGISTER','企业自助注册，名称=1',NULL,NULL,35,'COMPANY'),(64,'2025-12-20 13:24:05.356671','2025-12-20 13:24:05.356671','REGISTER','企业自助注册，名称=2',NULL,NULL,36,'COMPANY'),(65,'2025-12-20 13:24:13.975316','2025-12-20 13:24:13.975316','REGISTER','企业自助注册，名称=3',NULL,NULL,37,'COMPANY'),(66,'2025-12-20 13:24:21.489023','2025-12-20 13:24:21.489023','REGISTER','企业自助注册，名称=4',NULL,NULL,38,'COMPANY'),(67,'2025-12-20 13:24:35.822509','2025-12-20 13:24:35.822509','APPROVE','审批通过，备注=批量审批通过','ADMIN','admin',34,'COMPANY'),(68,'2025-12-20 13:24:35.822509','2025-12-20 13:24:35.822509','APPROVE','审批通过，备注=批量审批通过','ADMIN','admin',35,'COMPANY'),(69,'2025-12-20 13:24:42.138904','2025-12-20 13:24:42.138904','REJECT','审批驳回并删除，企业名称=3，备注=批量审批驳回','ADMIN','admin',37,'COMPANY'),(70,'2025-12-20 13:24:42.138904','2025-12-20 13:24:42.138904','REJECT','审批驳回并删除，企业名称=2，备注=批量审批驳回','ADMIN','admin',36,'COMPANY'),(71,'2025-12-20 13:24:57.635660','2025-12-20 13:24:57.635660','REJECT','审批驳回并删除，企业名称=4，备注=批量审批驳回','ADMIN','admin',38,'COMPANY'),(72,'2025-12-20 13:28:32.773157','2025-12-20 13:28:32.773157','DELETE','删除用户','ADMIN','admin',31,'USER'),(73,'2025-12-20 13:28:34.682153','2025-12-20 13:28:34.682153','DELETE','删除用户','ADMIN','admin',32,'USER');
/*!40000 ALTER TABLE `audit_log` ENABLE KEYS */;
UNLOCK TABLES;

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
  `audit_remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `audited_at` datetime(6) DEFAULT NULL,
  `status` enum('APPROVED','DISABLED','PENDING','REJECTED') COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_company_name` (`name`),
  KEY `idx_company_type` (`company_type`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='企业基本信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company`
--

LOCK TABLES `company` WRITE;
/*!40000 ALTER TABLE `company` DISABLE KEYS */;
INSERT INTO `company` VALUES (7,'上海清美绿色食品（集团）有限公司',NULL,'','时培亮','18916160282',NULL,NULL,'民营',NULL,NULL,NULL,NULL,'食品生产、食品经营、道路货物运输等','2025-12-04 07:17:12','2025-12-05 09:56:51',NULL,NULL,'APPROVED'),(14,'上海功德林素食工业有限公司',NULL,'','江浩','13661902000',NULL,NULL,'国有',NULL,NULL,NULL,NULL,'食品制造业','2025-12-04 11:55:22','2025-12-16 02:19:41',NULL,NULL,'APPROVED'),(15,'上海富伊清真食品配送有限公司',NULL,'','李港','18621881869',NULL,NULL,'民营',NULL,NULL,NULL,NULL,'清真牛羊肉及其副品','2025-12-04 11:55:47','2025-12-04 11:55:47',NULL,NULL,'APPROVED'),(16,'上海强丰实业有限公司',NULL,'','刘雪雷','13564112761',NULL,NULL,'民营',NULL,NULL,NULL,NULL,'全品类食堂配送\n','2025-12-04 11:56:07','2025-12-04 11:56:07',NULL,NULL,'APPROVED'),(17,'上海市糖业烟酒（集团）有限公司',NULL,'','顾巍骅','13917600287',NULL,NULL,'国有',NULL,NULL,NULL,NULL,'商业零售、食糖生产及销售、酒类销售等\n','2025-12-04 11:58:16','2025-12-16 02:15:07',NULL,NULL,'APPROVED'),(18,'上海中盐国际物流有限公司',NULL,'','刘永红','13921022558',NULL,NULL,'国有',NULL,NULL,NULL,NULL,'盐和盐化工产品\n','2025-12-04 11:58:34','2025-12-16 02:15:45',NULL,NULL,'APPROVED'),(19,'上海海烟物流发展有限公司',NULL,'','赵佳麟','13918186464',NULL,NULL,'国有',NULL,NULL,NULL,NULL,'卷烟，非烟配送\n','2025-12-04 12:03:25','2025-12-16 02:18:22',NULL,NULL,'APPROVED'),(20,'测试企业A',NULL,'北京市朝阳区测试路123号','张三','13800138001','李四','13800138002','民营',NULL,NULL,NULL,NULL,'蔬菜、水果、肉类配送','2025-12-16 15:41:51','2025-12-16 15:42:34','审核通过，企业资质齐全','2025-12-16 23:42:33.918186','APPROVED'),(30,'API测试企业',NULL,'测试地址','法人','13800138000','联系人','13800138001','民营',NULL,NULL,NULL,NULL,'测试经营范围','2025-12-17 06:38:01','2025-12-17 06:38:14','','2025-12-17 14:38:13.969685','APPROVED');
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='合规运营记录';
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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='需求与问题反馈';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedback`
--

LOCK TABLES `feedback` WRITE;
/*!40000 ALTER TABLE `feedback` DISABLE KEYS */;
INSERT INTO `feedback` VALUES (6,15,NULL,2025,12,'统一车辆外观、标识及编码，明确经营范畴，杜绝 “山寨” 车。\n建立多部门联动机制，禁止不合理收费，打击无资质流动售卖。\n搭建产销数据平台，精准规划运输线路与备货品类。\n纳入溯源体系，记录产地、轨迹等信息，实现全程可追溯。','','','','待处理','','2025-12-05 17:35:51','2025-12-05 17:37:55'),(7,17,NULL,2025,12,'增加车型选择，方便各企业结合实际使用情况作出选择。\n','','','','待处理','','2025-12-05 17:38:19','2025-12-05 17:38:19'),(8,7,NULL,2025,12,'多给点菜篮子额度','','','','待处理','','2025-12-05 17:38:45','2025-12-16 08:22:52'),(13,20,NULL,2025,12,'','','','','待处理','','2025-12-20 05:50:27','2025-12-20 05:50:27');
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车辆维护保养记录';
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
  `depart_time` datetime DEFAULT NULL COMMENT '出发时间',
  `description` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_route_seq` (`route_id`,`seq`),
  UNIQUE KEY `UKk30903n5exwvlmqj0d2rxr3mx` (`route_id`,`seq`),
  KEY `idx_vehicle_date` (`vehicle_id`,`route_date`),
  KEY `idx_route` (`route_id`),
  CONSTRAINT `route_point_ibfk_1` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车辆路线信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `route_point`
--

LOCK TABLES `route_point` WRITE;
/*!40000 ALTER TABLE `route_point` DISABLE KEYS */;
INSERT INTO `route_point` VALUES (7,127,'2025-12-20','ROUTE_20251220_1766208403039',1,'1','批发市场','2025-12-20 13:26:00',NULL,'','2025-12-20 05:26:43','2025-12-20 13:26:43.072170'),(8,127,'2025-12-20','ROUTE_20251220_1766208403039',2,'2','配送中心','2025-12-20 13:26:00',NULL,'','2025-12-20 05:26:43','2025-12-20 13:26:43.078893'),(9,127,'2025-12-20','ROUTE_20251220_1766208458635',1,'1','批发市场','2025-12-20 13:27:00',NULL,'','2025-12-20 05:27:39','2025-12-20 13:27:38.642206'),(10,127,'2025-12-20','ROUTE_20251220_1766208458635',2,'2','配送中心','2025-12-20 13:29:00',NULL,'','2025-12-20 05:27:39','2025-12-20 13:27:38.648361'),(13,127,'2025-12-20','ROUTE_20251220_1766210582979',1,'1','配送中心','2025-12-20 14:02:00',NULL,'','2025-12-20 06:03:03','2025-12-20 14:03:02.989563'),(14,127,'2025-12-20','ROUTE_20251220_1766210582979',2,'2','配送中心','2025-12-20 14:08:00',NULL,'','2025-12-20 06:03:03','2025-12-20 14:03:02.996378'),(15,127,'2025-12-20','ROUTE_20251220_1766210953400',1,'1.1','配送中心','2025-12-20 14:08:00','2025-12-20 14:08:00','','2025-12-20 06:09:14','2025-12-20 14:09:13.556228'),(16,127,'2025-12-20','ROUTE_20251220_1766210953400',2,'1.2','商场','2025-12-20 14:09:00','2025-12-20 14:09:00','','2025-12-20 06:09:14','2025-12-20 14:09:13.604700');
/*!40000 ALTER TABLE `route_point` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `survey_delivery_customers`
--

DROP TABLE IF EXISTS `survey_delivery_customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `survey_delivery_customers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `customer_type` enum('个人团购自提点等','其他','批发市场','电商平台','连锁商超','食品加工与生产企业','食堂学校机关企业','餐饮及中央厨房') COLLATE utf8mb4_unicode_ci NOT NULL,
  `survey_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK2jtkqmkcf9e1vfa2t1580eq10` (`survey_id`,`customer_type`),
  CONSTRAINT `FKrhwnnbevj9mi6ddw38tbaj4d5` FOREIGN KEY (`survey_id`) REFERENCES `survey_questionnaire` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `survey_delivery_customers`
--

LOCK TABLES `survey_delivery_customers` WRITE;
/*!40000 ALTER TABLE `survey_delivery_customers` DISABLE KEYS */;
INSERT INTO `survey_delivery_customers` VALUES (1,'2025-12-07 00:55:34.163675','2025-12-07 00:55:34.163675','连锁商超',1),(2,'2025-12-07 00:55:34.167943','2025-12-07 00:55:34.167943','批发市场',1),(3,'2025-12-07 00:55:34.170686','2025-12-07 00:55:34.170686','食堂学校机关企业',1),(5,'2025-12-07 01:27:49.053540','2025-12-07 01:27:49.053540','电商平台',2),(6,'2025-12-07 01:27:49.058244','2025-12-07 01:27:49.058244','连锁商超',2),(7,'2025-12-07 01:27:49.059560','2025-12-07 01:27:49.059560','个人团购自提点等',2),(8,'2025-12-07 01:36:46.278133','2025-12-07 01:36:46.278133','连锁商超',3),(9,'2025-12-07 01:36:46.279132','2025-12-07 01:36:46.279132','食品加工与生产企业',3),(10,'2025-12-07 01:36:46.279132','2025-12-07 01:36:46.279132','批发市场',3),(11,'2025-12-07 01:40:32.513532','2025-12-07 01:40:32.513532','连锁商超',4),(12,'2025-12-07 01:40:32.513532','2025-12-07 01:40:32.513532','餐饮及中央厨房',4),(13,'2025-12-07 01:40:32.513532','2025-12-07 01:40:32.513532','食品加工与生产企业',4),(14,'2025-12-07 01:43:00.825428','2025-12-07 01:43:00.825428','电商平台',5),(15,'2025-12-07 01:43:00.827428','2025-12-07 01:43:00.827428','连锁商超',5),(16,'2025-12-07 01:43:00.828427','2025-12-07 01:43:00.828427','批发市场',5),(17,'2025-12-07 01:45:51.341623','2025-12-07 01:45:51.341623','餐饮及中央厨房',6),(18,'2025-12-07 01:45:51.343651','2025-12-07 01:45:51.343651','食品加工与生产企业',6),(19,'2025-12-07 01:45:51.344579','2025-12-07 01:45:51.344579','食堂学校机关企业',6),(20,'2025-12-07 01:47:36.252930','2025-12-07 01:47:36.252930','电商平台',7),(21,'2025-12-07 01:47:36.254885','2025-12-07 01:47:36.254885','连锁商超',7),(22,'2025-12-07 01:47:36.256951','2025-12-07 01:47:36.256951','个人团购自提点等',7),(23,'2025-12-20 13:29:44.002024','2025-12-20 13:29:44.002024','电商平台',10),(24,'2025-12-20 13:29:44.009654','2025-12-20 13:29:44.009654','连锁商超',10);
/*!40000 ALTER TABLE `survey_delivery_customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `survey_gps_platforms`
--

DROP TABLE IF EXISTS `survey_gps_platforms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `survey_gps_platforms` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `other_platform` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `platform_type` enum('上海主副食品市场运行调控系统','上海交通局执法支队监管平台','企业自建或第三方车队管理系统TMS','全国道路货运车辆公共监管与服务平台简称全国货运平台','其他','未接入任何平台','甲方货主或物流平台的运力协同系统') COLLATE utf8mb4_unicode_ci NOT NULL,
  `survey_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKlrv43jnevmfvtt3g1q9bvl7x` (`survey_id`,`platform_type`),
  CONSTRAINT `FK5ixk761hdeiq9hci2kqhghsa9` FOREIGN KEY (`survey_id`) REFERENCES `survey_questionnaire` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `survey_gps_platforms`
--

LOCK TABLES `survey_gps_platforms` WRITE;
/*!40000 ALTER TABLE `survey_gps_platforms` DISABLE KEYS */;
INSERT INTO `survey_gps_platforms` VALUES (1,'2025-12-07 00:55:34.187028','2025-12-07 00:55:34.187028',NULL,'上海主副食品市场运行调控系统',1),(2,'2025-12-07 01:36:46.285034','2025-12-07 01:36:46.285034',NULL,'全国道路货运车辆公共监管与服务平台简称全国货运平台',3),(3,'2025-12-07 01:40:32.524790','2025-12-07 01:40:32.524790',NULL,'甲方货主或物流平台的运力协同系统',4),(4,'2025-12-07 01:43:00.832428','2025-12-07 01:43:00.832428',NULL,'全国道路货运车辆公共监管与服务平台简称全国货运平台',5),(5,'2025-12-07 01:45:51.347619','2025-12-07 01:45:51.347619',NULL,'全国道路货运车辆公共监管与服务平台简称全国货运平台',6),(6,'2025-12-07 01:47:36.261903','2025-12-07 01:47:36.261903',NULL,'企业自建或第三方车队管理系统TMS',7),(7,'2025-12-20 13:29:44.009654','2025-12-20 13:29:44.009654',NULL,'上海主副食品市场运行调控系统',10);
/*!40000 ALTER TABLE `survey_gps_platforms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `survey_operational_problems`
--

DROP TABLE IF EXISTS `survey_operational_problems`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `survey_operational_problems` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `other_description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `problem_type` enum('交警城管处罚频繁','企业经营下滑无法重新更换新车','其他','冷藏车购置维护成本高','卸货车位被社会车占用','司机短缺工资上涨','回程空驶率高','客户账期长现金流压力','新能源充电桩少','新能源续航不足冬季掉电','无法进行尾板箱体分割等改装','社区商超地下停车场限高禁入','限行区外缺少合法卸货点','高峰时段限行无法进城') COLLATE utf8mb4_unicode_ci NOT NULL,
  `sort_order` int NOT NULL,
  `survey_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKg1byvmy9rc822iminsmh3qmyf` (`survey_id`,`problem_type`),
  UNIQUE KEY `UK8dcenn18bm6of2yrlqa80mf4o` (`survey_id`,`sort_order`),
  CONSTRAINT `FKr07odi203mjl0ak3ogn4gdlgc` FOREIGN KEY (`survey_id`) REFERENCES `survey_questionnaire` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=141 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `survey_operational_problems`
--

LOCK TABLES `survey_operational_problems` WRITE;
/*!40000 ALTER TABLE `survey_operational_problems` DISABLE KEYS */;
INSERT INTO `survey_operational_problems` VALUES (1,'2025-12-07 00:55:34.196547','2025-12-07 00:55:34.196547',NULL,'高峰时段限行无法进城',1,1),(2,'2025-12-07 00:55:34.200969','2025-12-07 00:55:34.200969',NULL,'限行区外缺少合法卸货点',2,1),(3,'2025-12-07 00:55:34.202971','2025-12-07 00:55:34.202971',NULL,'企业经营下滑无法重新更换新车',3,1),(4,'2025-12-07 00:55:34.204967','2025-12-07 00:55:34.204967',NULL,'社区商超地下停车场限高禁入',4,1),(5,'2025-12-07 00:55:34.206968','2025-12-07 00:55:34.206968',NULL,'卸货车位被社会车占用',5,1),(6,'2025-12-07 00:55:34.209478','2025-12-07 00:55:34.209478',NULL,'交警城管处罚频繁',6,1),(7,'2025-12-07 00:55:34.211490','2025-12-07 00:55:34.211490',NULL,'新能源续航不足冬季掉电',7,1),(8,'2025-12-07 00:55:34.213490','2025-12-07 00:55:34.213490',NULL,'新能源充电桩少',8,1),(9,'2025-12-07 00:55:34.216413','2025-12-07 00:55:34.216413',NULL,'冷藏车购置维护成本高',9,1),(10,'2025-12-07 00:55:34.219426','2025-12-07 00:55:34.219426',NULL,'司机短缺工资上涨',10,1),(11,'2025-12-07 00:55:34.221151','2025-12-07 00:55:34.221151',NULL,'客户账期长现金流压力',11,1),(12,'2025-12-07 00:55:34.223163','2025-12-07 00:55:34.223163',NULL,'回程空驶率高',12,1),(13,'2025-12-07 00:55:34.226268','2025-12-07 00:55:34.226268',NULL,'无法进行尾板箱体分割等改装',13,1),(14,'2025-12-07 00:55:34.228267','2025-12-07 00:55:34.228267',NULL,'其他',14,1),(15,'2025-12-07 01:27:49.066564','2025-12-07 01:27:49.066564',NULL,'企业经营下滑无法重新更换新车',1,2),(16,'2025-12-07 01:27:49.066564','2025-12-07 01:27:49.066564',NULL,'高峰时段限行无法进城',2,2),(17,'2025-12-07 01:27:49.066564','2025-12-07 01:27:49.066564',NULL,'限行区外缺少合法卸货点',3,2),(18,'2025-12-07 01:27:49.073398','2025-12-07 01:27:49.073398',NULL,'社区商超地下停车场限高禁入',4,2),(19,'2025-12-07 01:27:49.073398','2025-12-07 01:27:49.073398',NULL,'卸货车位被社会车占用',5,2),(20,'2025-12-07 01:27:49.073398','2025-12-07 01:27:49.073398',NULL,'交警城管处罚频繁',6,2),(21,'2025-12-07 01:27:49.077690','2025-12-07 01:27:49.077690',NULL,'新能源续航不足冬季掉电',7,2),(22,'2025-12-07 01:27:49.079756','2025-12-07 01:27:49.079756',NULL,'新能源充电桩少',8,2),(23,'2025-12-07 01:27:49.079756','2025-12-07 01:27:49.079756',NULL,'冷藏车购置维护成本高',9,2),(24,'2025-12-07 01:27:49.079756','2025-12-07 01:27:49.079756',NULL,'司机短缺工资上涨',10,2),(25,'2025-12-07 01:27:49.079756','2025-12-07 01:27:49.079756',NULL,'客户账期长现金流压力',11,2),(26,'2025-12-07 01:27:49.079756','2025-12-07 01:27:49.079756',NULL,'回程空驶率高',12,2),(27,'2025-12-07 01:27:49.087148','2025-12-07 01:27:49.087148',NULL,'无法进行尾板箱体分割等改装',13,2),(28,'2025-12-07 01:27:49.087148','2025-12-07 01:27:49.087148',NULL,'其他',14,2),(29,'2025-12-07 01:36:46.291854','2025-12-07 01:36:46.291854',NULL,'企业经营下滑无法重新更换新车',1,3),(30,'2025-12-07 01:36:46.291854','2025-12-07 01:36:46.291854',NULL,'社区商超地下停车场限高禁入',2,3),(31,'2025-12-07 01:36:46.291854','2025-12-07 01:36:46.291854',NULL,'限行区外缺少合法卸货点',3,3),(32,'2025-12-07 01:36:46.296363','2025-12-07 01:36:46.296363',NULL,'高峰时段限行无法进城',4,3),(33,'2025-12-07 01:36:46.298092','2025-12-07 01:36:46.298092',NULL,'卸货车位被社会车占用',5,3),(34,'2025-12-07 01:36:46.298975','2025-12-07 01:36:46.298975',NULL,'交警城管处罚频繁',6,3),(35,'2025-12-07 01:36:46.300322','2025-12-07 01:36:46.300322',NULL,'新能源续航不足冬季掉电',7,3),(36,'2025-12-07 01:36:46.300322','2025-12-07 01:36:46.300322',NULL,'新能源充电桩少',8,3),(37,'2025-12-07 01:36:46.300322','2025-12-07 01:36:46.300322',NULL,'冷藏车购置维护成本高',9,3),(38,'2025-12-07 01:36:46.304948','2025-12-07 01:36:46.304948',NULL,'司机短缺工资上涨',10,3),(39,'2025-12-07 01:36:46.305930','2025-12-07 01:36:46.305930',NULL,'客户账期长现金流压力',11,3),(40,'2025-12-07 01:36:46.305930','2025-12-07 01:36:46.305930',NULL,'回程空驶率高',12,3),(41,'2025-12-07 01:36:46.305930','2025-12-07 01:36:46.305930',NULL,'无法进行尾板箱体分割等改装',13,3),(42,'2025-12-07 01:36:46.305930','2025-12-07 01:36:46.305930',NULL,'其他',14,3),(43,'2025-12-07 01:40:32.528757','2025-12-07 01:40:32.528757',NULL,'企业经营下滑无法重新更换新车',1,4),(44,'2025-12-07 01:40:32.528757','2025-12-07 01:40:32.528757',NULL,'限行区外缺少合法卸货点',2,4),(45,'2025-12-07 01:40:32.528757','2025-12-07 01:40:32.528757',NULL,'社区商超地下停车场限高禁入',3,4),(46,'2025-12-07 01:40:32.528757','2025-12-07 01:40:32.528757',NULL,'高峰时段限行无法进城',4,4),(47,'2025-12-07 01:40:32.533347','2025-12-07 01:40:32.533347',NULL,'卸货车位被社会车占用',5,4),(48,'2025-12-07 01:40:32.534286','2025-12-07 01:40:32.534286',NULL,'交警城管处罚频繁',6,4),(49,'2025-12-07 01:40:32.534790','2025-12-07 01:40:32.534790',NULL,'新能源续航不足冬季掉电',7,4),(50,'2025-12-07 01:40:32.534790','2025-12-07 01:40:32.534790',NULL,'新能源充电桩少',8,4),(51,'2025-12-07 01:40:32.534790','2025-12-07 01:40:32.534790',NULL,'冷藏车购置维护成本高',9,4),(52,'2025-12-07 01:40:32.534790','2025-12-07 01:40:32.534790',NULL,'司机短缺工资上涨',10,4),(53,'2025-12-07 01:40:32.534790','2025-12-07 01:40:32.534790',NULL,'客户账期长现金流压力',11,4),(54,'2025-12-07 01:40:32.541050','2025-12-07 01:40:32.541050',NULL,'回程空驶率高',12,4),(55,'2025-12-07 01:40:32.541050','2025-12-07 01:40:32.541050',NULL,'无法进行尾板箱体分割等改装',13,4),(56,'2025-12-07 01:40:32.541050','2025-12-07 01:40:32.541050',NULL,'其他',14,4),(57,'2025-12-07 01:43:00.839559','2025-12-07 01:43:00.839559',NULL,'高峰时段限行无法进城',1,5),(58,'2025-12-07 01:43:00.839559','2025-12-07 01:43:00.839559',NULL,'企业经营下滑无法重新更换新车',2,5),(59,'2025-12-07 01:43:00.839559','2025-12-07 01:43:00.839559',NULL,'限行区外缺少合法卸货点',3,5),(60,'2025-12-07 01:43:00.839559','2025-12-07 01:43:00.839559',NULL,'社区商超地下停车场限高禁入',4,5),(61,'2025-12-07 01:43:00.845289','2025-12-07 01:43:00.845289',NULL,'卸货车位被社会车占用',5,5),(62,'2025-12-07 01:43:00.847611','2025-12-07 01:43:00.847611',NULL,'交警城管处罚频繁',6,5),(63,'2025-12-07 01:43:00.847611','2025-12-07 01:43:00.847611',NULL,'新能源续航不足冬季掉电',7,5),(64,'2025-12-07 01:43:00.847611','2025-12-07 01:43:00.847611',NULL,'新能源充电桩少',8,5),(65,'2025-12-07 01:43:00.854018','2025-12-07 01:43:00.854018',NULL,'冷藏车购置维护成本高',9,5),(66,'2025-12-07 01:43:00.855089','2025-12-07 01:43:00.855089',NULL,'司机短缺工资上涨',10,5),(67,'2025-12-07 01:43:00.855089','2025-12-07 01:43:00.855089',NULL,'客户账期长现金流压力',11,5),(68,'2025-12-07 01:43:00.855089','2025-12-07 01:43:00.855089',NULL,'回程空驶率高',12,5),(69,'2025-12-07 01:43:00.859146','2025-12-07 01:43:00.859146',NULL,'无法进行尾板箱体分割等改装',13,5),(70,'2025-12-07 01:43:00.860400','2025-12-07 01:43:00.860400',NULL,'其他',14,5),(71,'2025-12-07 01:45:51.354593','2025-12-07 01:45:51.354593',NULL,'高峰时段限行无法进城',1,6),(72,'2025-12-07 01:45:51.355122','2025-12-07 01:45:51.355122',NULL,'冷藏车购置维护成本高',2,6),(73,'2025-12-07 01:45:51.355122','2025-12-07 01:45:51.355122',NULL,'回程空驶率高',3,6),(74,'2025-12-07 01:45:51.355122','2025-12-07 01:45:51.355122',NULL,'企业经营下滑无法重新更换新车',4,6),(75,'2025-12-07 01:45:51.359442','2025-12-07 01:45:51.359442',NULL,'限行区外缺少合法卸货点',5,6),(76,'2025-12-07 01:45:51.360120','2025-12-07 01:45:51.360120',NULL,'社区商超地下停车场限高禁入',6,6),(77,'2025-12-07 01:45:51.360120','2025-12-07 01:45:51.360120',NULL,'卸货车位被社会车占用',7,6),(78,'2025-12-07 01:45:51.360120','2025-12-07 01:45:51.360120',NULL,'交警城管处罚频繁',8,6),(79,'2025-12-07 01:45:51.360120','2025-12-07 01:45:51.360120',NULL,'新能源续航不足冬季掉电',9,6),(80,'2025-12-07 01:45:51.360120','2025-12-07 01:45:51.360120',NULL,'新能源充电桩少',10,6),(81,'2025-12-07 01:45:51.366383','2025-12-07 01:45:51.366383',NULL,'司机短缺工资上涨',11,6),(82,'2025-12-07 01:45:51.366383','2025-12-07 01:45:51.366383',NULL,'客户账期长现金流压力',12,6),(83,'2025-12-07 01:45:51.366383','2025-12-07 01:45:51.366383',NULL,'无法进行尾板箱体分割等改装',13,6),(84,'2025-12-07 01:45:51.366383','2025-12-07 01:45:51.366383',NULL,'其他',14,6),(85,'2025-12-07 01:47:36.261903','2025-12-07 01:47:36.261903',NULL,'企业经营下滑无法重新更换新车',1,7),(86,'2025-12-07 01:47:36.267775','2025-12-07 01:47:36.267775',NULL,'高峰时段限行无法进城',2,7),(87,'2025-12-07 01:47:36.268681','2025-12-07 01:47:36.268681',NULL,'限行区外缺少合法卸货点',3,7),(88,'2025-12-07 01:47:36.270693','2025-12-07 01:47:36.270693',NULL,'社区商超地下停车场限高禁入',4,7),(89,'2025-12-07 01:47:36.270693','2025-12-07 01:47:36.270693',NULL,'卸货车位被社会车占用',5,7),(90,'2025-12-07 01:47:36.270693','2025-12-07 01:47:36.270693',NULL,'交警城管处罚频繁',6,7),(91,'2025-12-07 01:47:36.275169','2025-12-07 01:47:36.275169',NULL,'新能源续航不足冬季掉电',7,7),(92,'2025-12-07 01:47:36.276268','2025-12-07 01:47:36.276268',NULL,'新能源充电桩少',8,7),(93,'2025-12-07 01:47:36.276268','2025-12-07 01:47:36.276268',NULL,'冷藏车购置维护成本高',9,7),(94,'2025-12-07 01:47:36.276268','2025-12-07 01:47:36.276268',NULL,'司机短缺工资上涨',10,7),(95,'2025-12-07 01:47:36.276268','2025-12-07 01:47:36.276268',NULL,'客户账期长现金流压力',11,7),(96,'2025-12-07 01:47:36.276268','2025-12-07 01:47:36.276268',NULL,'回程空驶率高',12,7),(97,'2025-12-07 01:47:36.281678','2025-12-07 01:47:36.281678',NULL,'无法进行尾板箱体分割等改装',13,7),(98,'2025-12-07 01:47:36.282573','2025-12-07 01:47:36.282573',NULL,'其他',14,7),(127,'2025-12-20 13:29:44.015562','2025-12-20 13:29:44.015562',NULL,'高峰时段限行无法进城',1,10),(128,'2025-12-20 13:29:44.015562','2025-12-20 13:29:44.015562',NULL,'企业经营下滑无法重新更换新车',2,10),(129,'2025-12-20 13:29:44.015562','2025-12-20 13:29:44.015562',NULL,'社区商超地下停车场限高禁入',3,10),(130,'2025-12-20 13:29:44.021908','2025-12-20 13:29:44.021908',NULL,'卸货车位被社会车占用',4,10),(131,'2025-12-20 13:29:44.022837','2025-12-20 13:29:44.022837',NULL,'限行区外缺少合法卸货点',5,10),(132,'2025-12-20 13:29:44.022837','2025-12-20 13:29:44.022837',NULL,'新能源续航不足冬季掉电',6,10),(133,'2025-12-20 13:29:44.022837','2025-12-20 13:29:44.022837',NULL,'交警城管处罚频繁',7,10),(134,'2025-12-20 13:29:44.022837','2025-12-20 13:29:44.022837',NULL,'新能源充电桩少',8,10),(135,'2025-12-20 13:29:44.022837','2025-12-20 13:29:44.022837',NULL,'冷藏车购置维护成本高',9,10),(136,'2025-12-20 13:29:44.022837','2025-12-20 13:29:44.022837',NULL,'司机短缺工资上涨',10,10),(137,'2025-12-20 13:29:44.022837','2025-12-20 13:29:44.022837',NULL,'客户账期长现金流压力',11,10),(138,'2025-12-20 13:29:44.022837','2025-12-20 13:29:44.022837',NULL,'回程空驶率高',12,10),(139,'2025-12-20 13:29:44.022837','2025-12-20 13:29:44.022837',NULL,'无法进行尾板箱体分割等改装',13,10),(140,'2025-12-20 13:29:44.022837','2025-12-20 13:29:44.022837',NULL,'其他',14,10);
/*!40000 ALTER TABLE `survey_operational_problems` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `survey_questionnaire`
--

DROP TABLE IF EXISTS `survey_questionnaire`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `survey_questionnaire` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `annual_transport_2022` decimal(12,2) DEFAULT NULL,
  `annual_transport_2023` decimal(12,2) DEFAULT NULL,
  `annual_transport_2024` decimal(12,2) DEFAULT NULL,
  `annual_transport_2025` decimal(12,2) DEFAULT NULL,
  `avg_loading_rate` enum('七十一到九十','九十一以上','五十一到七十','小于等于50') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `basket_trip_comparison` enum('低','差不多','非常低','非常高','高') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `basket_vehicle_importance` enum('其他','车身统一菜篮子工程标识带来的企业公益形象提升','进市区通行的便利性','高速隧道桥通行费的减免') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cold_basket_vehicles_2025` int DEFAULT NULL,
  `cold_other_vehicles_2025` int DEFAULT NULL,
  `cold_vehicle_multi_temp` bit(1) DEFAULT NULL,
  `future_vehicle_plan` enum('不变','减少','增加') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `gps_system_type` enum('GPS单模','北斗GPSGLONASSGalileo多星融合','北斗GPS双模','北斗单模','暂未安装任何卫星定位系统') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `has_cold_basket_vehicle` bit(1) DEFAULT NULL,
  `main_product_type` enum('A生鲜蔬菜类','B肉类及其制品','C水产品类','D豆制品类','E蛋奶及其制品','F水果类','G粮食及其制品','H综合配送类') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `other_importance` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `regular_basket_vehicles_2025` int DEFAULT NULL,
  `regular_other_vehicles_2025` int DEFAULT NULL,
  `revenue_2025` decimal(14,2) DEFAULT NULL,
  `review_comment` text COLLATE utf8mb4_unicode_ci,
  `reviewed_at` datetime(6) DEFAULT NULL,
  `reviewed_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `secondary_product_type` enum('A生鲜蔬菜类','B肉类及其制品','C水产品类','D豆制品类','E蛋奶及其制品','F水果类','G粮食及其制品','H综合配送类') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `submit_status` enum('已审核','已提交','已驳回','草稿') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `submit_time` datetime(6) DEFAULT NULL,
  `submitted_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `survey_year` int NOT NULL,
  `temp_recording_device` enum('其他','只有驾驶区有温度显示无记录','实时云端监控异常报警平台','无温控记录设备','车载温度记录仪单机无网络型','车载温度记录仪无线网络传输型') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `company_id` bigint NOT NULL,
  `submitted_user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKroonn1byk8pteqovisjn9c7oy` (`company_id`,`survey_year`),
  KEY `FK5lh586cblem6g4x0q0ph9y6c4` (`submitted_user_id`),
  CONSTRAINT `FK5lh586cblem6g4x0q0ph9y6c4` FOREIGN KEY (`submitted_user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKiweh4gjhp8eyii9u32sgwe3rm` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `survey_questionnaire`
--

LOCK TABLES `survey_questionnaire` WRITE;
/*!40000 ALTER TABLE `survey_questionnaire` DISABLE KEYS */;
INSERT INTO `survey_questionnaire` VALUES (1,'2025-12-07 00:55:34.082751','2025-12-07 00:55:39.486862',550000.00,570000.00,730000.00,700000.00,'七十一到九十','高','进市区通行的便利性',33,408,_binary '','增加','北斗GPS双模',_binary '','D豆制品类',NULL,0,2,100000.00,NULL,NULL,NULL,'A生鲜蔬菜类','已提交','2025-12-07 00:55:39.465532','管理员',2025,'车载温度记录仪无线网络传输型',7,1),(2,'2025-12-07 01:27:49.040332','2025-12-07 01:27:51.640220',1176.00,1751.00,2175.00,2544.00,NULL,NULL,NULL,0,0,NULL,'减少',NULL,_binary '\0',NULL,NULL,0,15,877000.00,NULL,NULL,NULL,NULL,'已提交','2025-12-07 01:27:51.627302','管理员',2025,NULL,19,1),(3,'2025-12-07 01:36:46.270690','2025-12-07 01:36:48.777113',12800.00,12000.00,10000.00,7000.00,'七十一到九十','低','进市区通行的便利性',0,0,NULL,'减少','北斗GPS双模',_binary '\0','G粮食及其制品',NULL,8,6,830.00,NULL,NULL,NULL,'G粮食及其制品','已提交','2025-12-07 01:36:48.765691','管理员',2025,NULL,18,1),(4,'2025-12-07 01:40:32.510540','2025-12-07 01:40:35.140102',280.00,3002623.00,160.00,80.00,'五十一到七十','高','进市区通行的便利性',1,0,_binary '\0','减少','北斗单模',_binary '','H综合配送类',NULL,3,0,23000000000.00,NULL,NULL,NULL,'G粮食及其制品','已提交','2025-12-07 01:40:35.125221','管理员',2025,'车载温度记录仪无线网络传输型',17,1),(5,'2025-12-07 01:43:00.820143','2025-12-07 01:43:02.605544',1509.00,1437.00,1728.00,1004.00,'五十一到七十','高','进市区通行的便利性',2,3,_binary '\0','增加','GPS单模',_binary '','A生鲜蔬菜类',NULL,1,3,5000.00,NULL,NULL,NULL,'A生鲜蔬菜类','已提交','2025-12-07 01:43:02.589972','管理员',2025,'车载温度记录仪单机无网络型',16,1),(6,'2025-12-07 01:45:51.331828','2025-12-07 01:45:53.112592',350.00,315.00,370.00,400.00,'五十一到七十','非常高','进市区通行的便利性',1,0,_binary '\0','增加','北斗GPS双模',_binary '','B肉类及其制品',NULL,0,0,2000.00,NULL,NULL,NULL,'B肉类及其制品','已提交','2025-12-07 01:45:53.101430','管理员',2025,'只有驾驶区有温度显示无记录',15,1),(7,'2025-12-07 01:47:36.248923','2025-12-07 01:47:38.572102',230.00,250.00,260.00,160.00,'小于等于50','差不多','进市区通行的便利性',1,0,_binary '\0','减少','GPS单模',_binary '','H综合配送类',NULL,1,0,2800.00,NULL,NULL,NULL,'H综合配送类','已提交','2025-12-07 01:47:38.561269','管理员',2025,'只有驾驶区有温度显示无记录',14,1),(10,'2025-12-20 13:29:43.989233','2025-12-20 14:16:18.548127',1.00,2.00,3.00,4.00,'五十一到七十','非常低','进市区通行的便利性',1,1,_binary '','增加','北斗GPS双模',_binary '','C水产品类',NULL,10,1,1.00,NULL,NULL,NULL,'D豆制品类','已提交','2025-12-20 14:16:18.543170','王五',2025,'只有驾驶区有温度显示无记录',20,15);
/*!40000 ALTER TABLE `survey_questionnaire` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `survey_standard_equipment`
--

DROP TABLE IF EXISTS `survey_standard_equipment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `survey_standard_equipment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `equipment_type` enum('其他','标准周转筐','标准周转箱','标准托盘','标准笼车','没有使用标准化载具') COLLATE utf8mb4_unicode_ci NOT NULL,
  `other_description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `survey_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKby8llj5j2ksmfvcshphr2tjyw` (`survey_id`,`equipment_type`),
  CONSTRAINT `FKhtrv0tnbk6igvvjie5vivfatm` FOREIGN KEY (`survey_id`) REFERENCES `survey_questionnaire` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `survey_standard_equipment`
--

LOCK TABLES `survey_standard_equipment` WRITE;
/*!40000 ALTER TABLE `survey_standard_equipment` DISABLE KEYS */;
INSERT INTO `survey_standard_equipment` VALUES (1,'2025-12-07 00:55:34.177699','2025-12-07 00:55:34.177699','标准周转筐',NULL,1),(2,'2025-12-07 01:36:46.285034','2025-12-07 01:36:46.285034','标准托盘',NULL,3),(3,'2025-12-07 01:40:32.521769','2025-12-07 01:40:32.521769','没有使用标准化载具',NULL,4),(4,'2025-12-07 01:43:00.832428','2025-12-07 01:43:00.832428','标准周转筐',NULL,5),(5,'2025-12-07 01:45:51.347619','2025-12-07 01:45:51.347619','没有使用标准化载具',NULL,6),(6,'2025-12-07 01:47:36.260088','2025-12-07 01:47:36.260088','标准周转箱',NULL,7),(7,'2025-12-20 13:29:44.009654','2025-12-20 13:29:44.009654','标准笼车',NULL,10);
/*!40000 ALTER TABLE `survey_standard_equipment` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='运输状况统计';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transport_stats`
--

LOCK TABLES `transport_stats` WRITE;
/*!40000 ALTER TABLE `transport_stats` DISABLE KEYS */;
INSERT INTO `transport_stats` VALUES (4,110,2025,12,1.00,40.00,2000.00,10000.00,3,5,'[]','2025-12-05 18:05:05','2025-12-16 10:59:26'),(5,99,2025,12,3.00,120.00,3500.00,NULL,NULL,NULL,'[]','2025-12-05 18:07:45','2025-12-16 10:59:29'),(6,100,2025,12,3.00,120.00,3500.00,NULL,NULL,NULL,'[\"炒货\"]','2025-12-05 18:08:35','2025-12-16 10:59:06'),(7,101,2025,12,3.00,130.00,3500.00,NULL,NULL,NULL,'[]','2025-12-05 18:09:48','2025-12-05 18:09:48'),(8,102,2025,12,2.66,120.00,3200.00,NULL,NULL,NULL,'[]','2025-12-05 18:11:06','2025-12-05 18:11:06'),(9,103,2025,12,3.00,120.00,3500.00,NULL,NULL,NULL,'[]','2025-12-05 18:11:31','2025-12-05 18:11:31'),(10,104,2025,12,3.00,120.00,3000.00,NULL,NULL,NULL,'[]','2025-12-05 18:12:00','2025-12-05 18:12:00'),(11,105,2025,12,3.33,150.00,3800.00,NULL,NULL,NULL,'[]','2025-12-05 18:12:24','2025-12-05 18:12:24'),(12,106,2025,12,3.33,150.00,3800.00,NULL,NULL,NULL,'[]','2025-12-05 18:12:54','2025-12-05 18:12:54'),(13,66,2025,12,1.00,80.00,2800.00,0.00,16,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(14,67,2025,12,1.00,80.00,2800.00,0.00,18,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(15,68,2025,12,1.00,80.00,4800.00,1600.00,26,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(16,69,2025,12,1.00,80.00,2800.00,0.00,20,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(17,70,2025,12,1.00,80.00,2800.00,0.00,22,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(18,71,2025,12,1.00,80.00,2500.00,0.00,21,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(19,72,2025,12,1.00,80.00,4500.00,1200.00,24,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(20,73,2025,12,1.00,80.00,2500.00,0.00,21,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(21,74,2025,12,1.00,80.00,2600.00,0.00,20,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(22,75,2025,12,1.00,26.00,3000.00,0.00,19,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(23,76,2025,12,1.00,26.00,4300.00,2100.00,16,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(24,77,2025,12,1.00,80.00,3000.00,1000.00,2,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(25,78,2025,12,1.00,80.00,6000.00,0.00,18,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(26,79,2025,12,1.00,80.00,9400.00,0.00,11,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(27,80,2025,12,1.00,80.00,4200.00,0.00,12,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(28,81,2025,12,1.00,80.00,4500.00,7600.00,10,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(29,82,2025,12,1.00,80.00,6200.00,0.00,12,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(30,83,2025,12,1.00,80.00,4000.00,0.00,4,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(31,84,2025,12,1.00,80.00,3200.00,0.00,13,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(32,85,2025,12,1.00,80.00,5200.00,0.00,10,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(33,86,2025,12,1.00,80.00,4200.00,0.00,12,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(34,87,2025,12,1.00,200.00,3500.00,1500.00,1,2,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(35,88,2025,12,1.00,200.00,3500.00,5000.00,1,2,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(36,89,2025,12,1.00,80.00,3500.00,0.00,24,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(37,90,2025,12,1.00,80.00,3000.00,0.00,20,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(38,91,2025,12,1.00,80.00,3100.00,0.00,21,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(39,92,2025,12,1.00,80.00,3400.00,0.00,20,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(40,93,2025,12,1.00,80.00,3000.00,0.00,22,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(41,94,2025,12,1.00,80.00,5800.00,1000.00,23,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(42,95,2025,12,1.00,80.00,4200.00,0.00,20,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(43,96,2025,12,1.00,80.00,3500.00,0.00,20,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(44,97,2025,12,1.00,80.00,3400.00,0.00,18,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(45,98,2025,12,1.00,80.00,3400.00,0.00,21,1,'[]','2025-12-05 18:29:02','2025-12-05 18:29:02'),(47,127,2025,1,5.50,100.50,5000.00,50000.00,10,2,'[\"蔬菜\", \"肉类\", \"水果\"]','2025-12-17 17:19:53','2025-12-17 17:19:53');
/*!40000 ALTER TABLE `transport_stats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_company_rel`
--

DROP TABLE IF EXISTS `user_company_rel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_company_rel` (
  `user_id` bigint NOT NULL,
  `company_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`company_id`),
  KEY `FKbl7wqgabq5y6di7sesivt7ic9` (`company_id`),
  CONSTRAINT `FKbl7wqgabq5y6di7sesivt7ic9` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`),
  CONSTRAINT `FKtb7hc1bejj9mg2a5p2f4i78ln` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_company_rel`
--

LOCK TABLES `user_company_rel` WRITE;
/*!40000 ALTER TABLE `user_company_rel` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_company_rel` ENABLE KEYS */;
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
  `role` enum('ADMIN','BUSINESS_COMMISSION','COMPANY','COMPANY_ADMIN','COMPANY_USER','DRIVER') COLLATE utf8mb4_unicode_ci NOT NULL,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `company_id` bigint DEFAULT NULL,
  `vehicle_id` bigint DEFAULT NULL,
  `last_login_at` datetime(6) DEFAULT NULL,
  `last_login_ip` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `locked_until` datetime(6) DEFAULT NULL,
  `login_fail_count` int DEFAULT NULL,
  `wechat_open_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `wechat_union_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`),
  KEY `idx_company` (`company_id`),
  KEY `idx_vehicle` (`vehicle_id`),
  CONSTRAINT `users_ibfk_1` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`) ON DELETE SET NULL,
  CONSTRAINT `users_ibfk_2` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle` (`id`) ON DELETE SET NULL,
  CONSTRAINT `users_ibfk_3` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`) ON DELETE SET NULL,
  CONSTRAINT `users_ibfk_4` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,NULL,'2025-12-20 14:10:04.927691','',_binary '','管理员','$2a$10$vGrOCugTdhrUWkr0erPQw.6ZcJXPCWfyg9QPKwCnOHLTH7D2Y9AaW','ADMIN','admin','',NULL,NULL,'2025-12-20 14:10:04.924691',NULL,NULL,0,NULL,NULL),(12,'2025-12-16 23:21:11.847886','2025-12-17 15:53:43.920015','business@example.com',_binary '','商务委用户1','$2a$10$QAd1sODpCczoFmWBXlG64e0WyU.GDUz9TlDVrPsP5qMqpTd0RWpLK','BUSINESS_COMMISSION','business_commission1','13900139000',NULL,NULL,'2025-12-17 15:53:43.920015',NULL,NULL,0,NULL,NULL),(14,'2025-12-16 23:38:45.577185','2025-12-16 23:43:51.731852','admin@example.com',_binary '','管理员2','$2a$10$Yjijd.Im2UbVp9Wb.0Y0lOPrupIGCS.A6I6VmckeKLI9ecCv8EAou','ADMIN','admin2','13800138000',NULL,NULL,'2025-12-16 23:43:51.730086',NULL,NULL,0,NULL,NULL),(15,'2025-12-16 23:41:51.458384','2025-12-20 14:14:53.392476','admin_a@test.com',_binary '','王五','$2a$10$lXz7HBVd2LC5dlGDtR.ojOdUeS6G4YGSV1xyp/d3lEKHRbK8iM/R2','COMPANY_ADMIN','company_admin_a','13800138003',20,NULL,'2025-12-20 14:14:53.391477',NULL,NULL,0,NULL,NULL),(20,'2025-12-17 00:21:44.373068','2025-12-20 14:14:34.833988','user1@test.com',_binary '','企业普通用户1','$2a$10$peE4KgCicc56v96PeimbguBiojl5P7Ik5SKaNcvPs7Rfp5CnpzyW.','COMPANY_USER','company_user1','13800138004',20,NULL,'2025-12-20 14:14:34.832988',NULL,NULL,0,NULL,NULL),(25,'2025-12-17 14:22:28.380021','2025-12-17 14:23:10.860829','driver1@test.com',_binary '','司机1','$2a$10$m0wlqsQvdVN.mUuctLBbQeauvlEjoC06WTEiD8v8GUJeWCGU1TSiO','DRIVER','driver1','13800138005',20,127,'2025-12-17 14:23:10.860829',NULL,NULL,0,NULL,NULL),(27,'2025-12-17 14:38:01.136987','2025-12-17 14:38:13.983606','api@test.com',_binary '','API测试管理员','$2a$10$TiunOYBEjNnlnnf1X1wllO5gi9PA5Mx.uYBMHigvJURmLjL4r.VCK','COMPANY_USER','api_test_admin','13800138002',30,NULL,NULL,NULL,NULL,0,NULL,NULL);
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
  `color_plate` enum('蓝牌','黄牌','绿牌') COLLATE utf8mb4_unicode_ci DEFAULT '蓝牌',
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
) ENGINE=InnoDB AUTO_INCREMENT=130 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车辆基本信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vehicle`
--

LOCK TABLES `vehicle` WRITE;
/*!40000 ALTER TABLE `vehicle` DISABLE KEYS */;
INSERT INTO `vehicle` VALUES (66,7,'沪DP8755','菜篮子工程车','冷藏','黄牌','国5',0.00,NULL,'','',NULL,4,NULL,1,'2025-12-04 12:17:33','2025-12-05 16:49:44'),(67,7,'沪DP8780','菜篮子工程车','冷藏','黄牌','国5',0.00,NULL,'','',NULL,3,NULL,1,'2025-12-04 12:19:52','2025-12-05 16:49:40'),(68,7,'沪DP8825','菜篮子工程车','冷藏','黄牌','国5',0.00,NULL,'','',NULL,3,NULL,1,'2025-12-04 12:20:19','2025-12-05 16:49:36'),(69,7,'沪DP8827','菜篮子工程车','冷藏','黄牌','国5',0.00,NULL,'','',NULL,2,NULL,1,'2025-12-05 16:25:08','2025-12-05 16:49:24'),(70,7,'沪DP8850','菜篮子工程车','冷藏','黄牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(71,7,'沪DP9005','菜篮子工程车','冷藏','黄牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(72,7,'沪DP9007','菜篮子工程车','冷藏','黄牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(73,7,'沪DP9016','菜篮子工程车','冷藏','黄牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(74,7,'沪DP9030','菜篮子工程车','冷藏','黄牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(75,7,'沪BQN083','菜篮子工程车','冷藏','蓝牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(76,7,'沪BRK938','菜篮子工程车','冷藏','蓝牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(77,7,'沪GE7208','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(78,7,'沪GF2261','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(79,7,'沪GF2956','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(80,7,'沪GF8625','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(81,7,'沪GE1817','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(82,7,'沪GE6202','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(83,7,'沪GF0596','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(84,7,'沪DS4719','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(85,7,'沪GE8783','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(86,7,'沪GF0809','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(87,7,'沪GG5366','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(88,7,'沪GG7816','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(89,7,'沪EP4993','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(90,7,'沪GG6051','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(91,7,'沪GG6577','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(92,7,'沪GG7635','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(93,7,'沪GG7813','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(94,7,'沪GH8722','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(95,7,'沪GG9537','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(96,7,'沪GG7682','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(97,7,'沪GG5922','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(98,7,'沪GG2078','菜篮子工程车','冷藏','黄牌','国6B',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 16:48:26','2025-12-05 16:48:26'),(99,18,'沪NH5592','菜篮子工程车','普通','蓝牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 17:00:43','2025-12-05 17:00:43'),(100,18,'沪DP3041','菜篮子工程车','普通','黄牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 17:00:43','2025-12-05 17:00:43'),(101,18,'沪BKE877','菜篮子工程车','普通','蓝牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 17:00:43','2025-12-05 17:00:43'),(102,18,'沪FUC567','菜篮子工程车','普通','蓝牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 17:00:43','2025-12-05 17:00:43'),(103,18,'沪DP3466','菜篮子工程车','普通','黄牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 17:00:43','2025-12-05 17:00:43'),(104,18,'沪BKE865','菜篮子工程车','普通','蓝牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 17:00:43','2025-12-05 17:00:43'),(105,18,'沪BWV839','菜篮子工程车','普通','蓝牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 17:00:43','2025-12-05 17:00:43'),(106,18,'沪BZX981','菜篮子工程车','普通','蓝牌','国5',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 17:00:43','2025-12-05 17:00:43'),(107,17,'沪DS7376','菜篮子工程车','普通','黄牌',NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 17:06:54','2025-12-05 17:06:54'),(108,17,'沪EJ0517','菜篮子工程车','普通','黄牌',NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 17:06:54','2025-12-05 17:06:54'),(109,17,'沪BZB836','菜篮子工程车','冷藏','蓝牌',NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 17:06:54','2025-12-05 17:06:54'),(110,15,'沪EQG088','菜篮子工程车','冷藏','蓝牌','国6A',NULL,NULL,NULL,NULL,NULL,1,NULL,1,'2025-12-05 17:12:09','2025-12-05 17:12:09'),(112,19,'沪BBE556','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-10-31','LSH14JTC8FA235768','R915A017670',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27'),(113,19,'沪BBE305','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-10-30','LSH14JTCXFA235772','R915A017675',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27'),(114,19,'沪BBE693','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-10-31','LSH14JTC5FA235761','R915A017667',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27'),(115,19,'沪BBE678','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-10-30','LSH14JTC3FA235774','R915A017674',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27'),(116,19,'沪BBE233','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-10-30','LSH14JTC7FA235776','R915A017658',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27'),(117,19,'沪BBE728','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-10-31','LSH14JTC7FA235762','R915A017657',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27'),(118,19,'沪BBE201','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-10-31','LSH14JTCXFA235769','R915A017666',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27'),(119,19,'沪BCC507','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-10-30','LSH14JTC1FA235773','R915A017661',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27'),(120,19,'沪BBE085','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-10-30','LSH14JTC6FA235770','R915A017665',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27'),(121,19,'沪BBE187','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-10-30','LSH14JTC5FA235775','R915A017652',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27'),(122,19,'沪BBE623','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-10-31','LSH14JTC6FA235767','R915A017672',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27'),(123,19,'沪BBE367','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-10-30','LSH14JTC4FA235766','R915A017671',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27'),(124,19,'沪BBH005','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-11-16','LSH14JTCXFA235755','R915A017664',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27'),(125,19,'沪AZP650','菜篮子工程车','普通','蓝牌','国5',1.61,'2015-11-12','LSH14JTC3FA235757','R915A017659',NULL,1,NULL,1,'2025-12-05 17:22:27','2025-12-05 17:22:27'),(127,20,'京A12345','菜篮子工程车','普通','蓝牌','',0.00,NULL,'','',NULL,1,NULL,1,'2025-12-17 06:21:53','2025-12-17 06:21:53');
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
  `cold_basket_green` int DEFAULT NULL,
  `cold_freight_green` int DEFAULT NULL,
  `regular_basket_green` int DEFAULT NULL,
  `regular_freight_green` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_company_date` (`company_id`,`stat_date`),
  UNIQUE KEY `UKeon6y1kbu7n9x4g594tfrpu2y` (`company_id`,`stat_date`),
  KEY `idx_stat_date` (`stat_date`),
  CONSTRAINT `vehicle_statistics_ibfk_1` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车辆数量统计';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vehicle_statistics`
--

LOCK TABLES `vehicle_statistics` WRITE;
/*!40000 ALTER TABLE `vehicle_statistics` DISABLE KEYS */;
INSERT INTO `vehicle_statistics` VALUES (19,18,'2025-12-06',6,2,0,0,0,0,0,0,0,0,'2025-12-05 17:07:58','2025-12-05 17:07:58',NULL,NULL,NULL,NULL),(21,7,'2025-12-16',0,0,2,31,33,0,0,0,0,0,'2025-12-16 15:57:05','2025-12-16 15:57:05',0,0,0,0);
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

-- Dump completed on 2025-12-20 15:05:24

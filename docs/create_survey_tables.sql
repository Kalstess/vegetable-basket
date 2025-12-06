-- 问卷调查表结构
-- 根据 docs/字段整理.txt 中的问卷内容设计

-- 1. 问卷调查主表
CREATE TABLE IF NOT EXISTS `survey_questionnaire` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `company_id` BIGINT NOT NULL COMMENT '企业ID',
  `survey_year` INT NOT NULL COMMENT '调查年份',
  `submit_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `submit_status` ENUM('草稿','已提交','已审核','已驳回') DEFAULT '草稿' COMMENT '提交状态',
  `submitted_by` VARCHAR(255) COMMENT '提交人',
  `submitted_user_id` BIGINT COMMENT '提交用户ID',
  `reviewed_by` VARCHAR(255) COMMENT '审核人',
  `reviewed_at` DATETIME COMMENT '审核时间',
  `review_comment` TEXT COMMENT '审核意见',
  
  -- 基本信息部分
  `annual_transport_2022` DECIMAL(12,2) COMMENT '2022年运输量(吨)',
  `annual_transport_2023` DECIMAL(12,2) COMMENT '2023年运输量(吨)',
  `annual_transport_2024` DECIMAL(12,2) COMMENT '2024年运输量(吨)',
  `annual_transport_2025` DECIMAL(12,2) COMMENT '2025年运输量(吨)',
  `future_vehicle_plan` ENUM('增加','减少','不变') COMMENT '未来三年自有货运车辆预计',
  `revenue_2025` DECIMAL(14,2) COMMENT '2025年营业收入(万元)',
  
  -- 车辆数量部分
  `regular_basket_vehicles_2025` INT DEFAULT 0 COMMENT '2025年普通菜篮子工程车(辆)',
  `cold_basket_vehicles_2025` INT DEFAULT 0 COMMENT '2025年冷藏菜篮子工程车(辆)',
  `regular_other_vehicles_2025` INT DEFAULT 0 COMMENT '2025年普通其他货运车(辆)',
  `cold_other_vehicles_2025` INT DEFAULT 0 COMMENT '2025年冷藏其他货运车(辆)',
  
  -- 出车次数比较
  `basket_trip_comparison` ENUM('非常低','低','差不多','高','非常高') COMMENT '菜篮子车出车次数比较',
  
  -- 运输产品种类
  `main_product_type` ENUM(
    'A.生鲜蔬菜类',
    'B.肉类及其制品',
    'C.水产品类',
    'D.豆制品类',
    'E.蛋奶及其制品',
    'F.水果类',
    'G.粮食及其制品',
    'H.综合配送类'
  ) COMMENT '最主要产品种类',
  
  `secondary_product_type` ENUM(
    'A.生鲜蔬菜类',
    'B.肉类及其制品',
    'C.水产品类',
    'D.豆制品类',
    'E.蛋奶及其制品',
    'F.水果类',
    'G.粮食及其制品',
    'H.综合配送类'
  ) COMMENT '次要产品种类',
  
  -- 卫星定位系统
  `gps_system_type` ENUM(
    'GPS单模',
    '北斗单模',
    '北斗GPS双模',
    '北斗GPSGLONASSGalileo多星融合',
    '暂未安装任何卫星定位系统'
  ) COMMENT '卫星定位系统类型',
  
  -- 冷藏车相关
  `has_cold_basket_vehicle` BOOLEAN DEFAULT FALSE COMMENT '是否有冷藏菜篮子工程车',
  `cold_vehicle_multi_temp` BOOLEAN COMMENT '冷藏车是否有多温区设计',
  `temp_recording_device` ENUM(
    '只有驾驶区有温度显示无记录',
    '车载温度记录仪单机无网络型',
    '车载温度记录仪无线网络传输型',
    '实时云端监控异常报警平台',
    '无温控记录设备',
    '其他'
  ) COMMENT '温度记录设备类型',
  
  -- 装载率
  `avg_loading_rate` ENUM('小于等于50','五十一到七十','七十一到九十','九十一以上') COMMENT '平均装载率',
  
  -- 使用菜篮子车最看重的因素
  `basket_vehicle_importance` ENUM(
    '进市区通行的便利性',
    '高速隧道桥通行费的减免',
    '车身统一菜篮子工程标识带来的企业公益形象提升',
    '其他'
  ) COMMENT '使用菜篮子车最看重的因素',
  
  `other_importance` VARCHAR(500) COMMENT '其他看重因素说明',
  
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_company_survey_year` (`company_id`, `survey_year`),
  KEY `idx_survey_year` (`survey_year`),
  KEY `idx_submit_status` (`submit_status`),
  CONSTRAINT `fk_survey_company` FOREIGN KEY (`company_id`) 
    REFERENCES `company` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_survey_user` FOREIGN KEY (`submitted_user_id`) 
    REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问卷调查表';

-- 2.1 配送客户类型多选表
CREATE TABLE IF NOT EXISTS `survey_delivery_customers` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `survey_id` BIGINT NOT NULL,
  `customer_type` ENUM(
    '电商平台',
    '连锁商超',
    '餐饮及中央厨房',
    '批发市场',
    '食品加工与生产企业',
    '食堂（学校/机关/企业）',
    '个人（团购自提点等）',
    '其他'
  ) NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_survey_customer` (`survey_id`, `customer_type`),
  CONSTRAINT `fk_delivery_customers_survey` FOREIGN KEY (`survey_id`) 
    REFERENCES `survey_questionnaire` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='配送客户类型多选答案';

-- 2.2 标准化载具多选表
CREATE TABLE IF NOT EXISTS `survey_standard_equipment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `survey_id` BIGINT NOT NULL,
  `equipment_type` ENUM(
    '标准周转箱',
    '标准周转筐',
    '标准托盘',
    '标准笼车',
    '没有使用标准化载具',
    '其他'
  ) NOT NULL,
  `other_description` VARCHAR(500) COMMENT '其他载具说明',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_survey_equipment` (`survey_id`, `equipment_type`),
  CONSTRAINT `fk_standard_equipment_survey` FOREIGN KEY (`survey_id`) 
    REFERENCES `survey_questionnaire` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标准化载具多选答案';

-- 2.3 卫星定位平台多选表
CREATE TABLE IF NOT EXISTS `survey_gps_platforms` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `survey_id` BIGINT NOT NULL,
  `platform_type` ENUM(
    '全国道路货运车辆公共监管与服务平台简称全国货运平台',
    '上海主副食品市场运行调控系统',
    '企业自建或第三方车队管理系统TMS',
    '甲方货主或物流平台的运力协同系统',
    '上海交通局执法支队监管平台',
    '未接入任何平台',
    '其他'
  ) NOT NULL,
  `other_platform` VARCHAR(500) COMMENT '其他平台说明',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_survey_platform` (`survey_id`, `platform_type`),
  CONSTRAINT `fk_gps_platforms_survey` FOREIGN KEY (`survey_id`) 
    REFERENCES `survey_questionnaire` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='卫星定位平台多选答案';

-- 2.4 运营问题排序表
CREATE TABLE IF NOT EXISTS `survey_operational_problems` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `survey_id` BIGINT NOT NULL,
  `problem_type` ENUM(
    '企业经营下滑无法重新更换新车',
    '高峰时段限行无法进城',
    '限行区外缺少合法卸货点',
    '社区商超地下停车场限高禁入',
    '卸货车位被社会车占用',
    '交警城管处罚频繁',
    '新能源续航不足冬季掉电',
    '新能源充电桩少',
    '冷藏车购置维护成本高',
    '司机短缺工资上涨',
    '客户账期长现金流压力',
    '回程空驶率高',
    '无法进行尾板箱体分割等改装',
    '其他'
  ) NOT NULL,
  `sort_order` INT NOT NULL COMMENT '排序序号（1,2,3...）',
  `other_description` VARCHAR(500) COMMENT '其他问题说明',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_survey_problem_order` (`survey_id`, `problem_type`),
  UNIQUE KEY `ux_survey_order` (`survey_id`, `sort_order`),
  CONSTRAINT `fk_operational_problems_survey` FOREIGN KEY (`survey_id`) 
    REFERENCES `survey_questionnaire` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='运营问题排序答案';


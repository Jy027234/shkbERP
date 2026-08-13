-- SHKB 看板核心 schema。
--
-- SHKB 业务代码最初提交时没有同步提交建表 SQL，导致从标准 tenant dump
-- 初始化的新环境缺少看板依赖的表。本迁移只补齐已经能由实体和查询语句
-- 完整证明的核心结构，不替代其余 SHKB 子模块后续需要补录的 schema。

CREATE TABLE IF NOT EXISTS `base_data_machine_type` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `code` varchar(20) NOT NULL COMMENT '编号',
  `name` varchar(100) NOT NULL COMMENT '名称',
  `available` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态',
  `description` varchar(200) NOT NULL DEFAULT '' COMMENT '备注',
  `create_by` varchar(32) NOT NULL COMMENT '创建人',
  `create_by_id` varchar(32) NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(32) NOT NULL COMMENT '修改人',
  `update_by_id` varchar(32) NOT NULL COMMENT '修改人ID',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_machine_type_code` (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='机型';

-- 兼容已经由人工脚本添加过商品扩展列的环境。
SET @part_number_column_ddl = IF(
  EXISTS(
    SELECT 1
      FROM information_schema.columns
     WHERE table_schema = DATABASE()
       AND table_name = 'base_data_product'
       AND column_name = 'part_number_id'
  ),
  'SELECT 1',
  'ALTER TABLE `base_data_product` ADD COLUMN `part_number_id` varchar(32) DEFAULT NULL COMMENT ''关联件号ID'' AFTER `brand_id`'
);
PREPARE part_number_column_stmt FROM @part_number_column_ddl;
EXECUTE part_number_column_stmt;
DEALLOCATE PREPARE part_number_column_stmt;

SET @machine_type_column_ddl = IF(
  EXISTS(
    SELECT 1
      FROM information_schema.columns
     WHERE table_schema = DATABASE()
       AND table_name = 'base_data_product'
       AND column_name = 'machine_type_id'
  ),
  'SELECT 1',
  'ALTER TABLE `base_data_product` ADD COLUMN `machine_type_id` varchar(32) DEFAULT NULL COMMENT ''机型ID'' AFTER `part_number_id`'
);
PREPARE machine_type_column_stmt FROM @machine_type_column_ddl;
EXECUTE machine_type_column_stmt;
DEALLOCATE PREPARE machine_type_column_stmt;

CREATE TABLE IF NOT EXISTS `shkb_contract` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `code` varchar(50) NOT NULL COMMENT '合同编号',
  `name` varchar(100) NOT NULL COMMENT '合同名称',
  `available` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态',
  `description` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(32) NOT NULL COMMENT '创建人',
  `create_by_id` varchar(32) NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(32) NOT NULL COMMENT '修改人',
  `update_by_id` varchar(32) NOT NULL COMMENT '修改人ID',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `part_number_id` varchar(32) NOT NULL COMMENT '件号ID',
  `contract_time` datetime NOT NULL COMMENT '合同时间',
  `customer_id` varchar(32) NOT NULL COMMENT '客户ID',
  `serial_number` varchar(100) DEFAULT NULL COMMENT '产品序号',
  `other_repair_requirements` varchar(1000) DEFAULT NULL COMMENT '其他维修需求',
  `storage_time` datetime DEFAULT NULL COMMENT '入库时间',
  `planned_completion_time` datetime DEFAULT NULL COMMENT '计划完工时间',
  `delivery_time` datetime DEFAULT NULL COMMENT '发货时间',
  `contract_price` decimal(24,2) DEFAULT NULL COMMENT '合同报价',
  `replacement_part_price` decimal(24,2) DEFAULT NULL COMMENT '更换件价格',
  `contract_type` tinyint NOT NULL COMMENT '合同类型',
  `contract_status` tinyint NOT NULL DEFAULT 0 COMMENT '合同进度',
  `actual_completion_time` datetime DEFAULT NULL COMMENT '实际完工时间',
  `from_contract_task_id` varchar(32) DEFAULT NULL COMMENT '来源合同任务ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_shkb_contract_code` (`code`) USING BTREE,
  KEY `idx_shkb_contract_type_time` (`contract_type`, `create_time`) USING BTREE,
  KEY `idx_shkb_contract_part_number` (`part_number_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='维修合同';

CREATE TABLE IF NOT EXISTS `shkb_contract_task` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `contract_id` varchar(32) NOT NULL COMMENT '合同ID',
  `task_status` varchar(32) DEFAULT NULL COMMENT '任务状态',
  `repair_status` varchar(32) DEFAULT NULL COMMENT '维修状态',
  `material_status` varchar(32) DEFAULT NULL COMMENT '航材状态',
  `create_by` varchar(32) NOT NULL COMMENT '创建人',
  `create_by_id` varchar(32) NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(32) NOT NULL COMMENT '修改人',
  `update_by_id` varchar(32) NOT NULL COMMENT '修改人ID',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `dispatch` varchar(100) DEFAULT NULL COMMENT '派发至',
  `task_user_id` varchar(32) DEFAULT NULL COMMENT '任务人员ID',
  `description` varchar(500) DEFAULT NULL COMMENT '合同任务备注',
  `repair_description` varchar(500) DEFAULT NULL COMMENT '维修备注',
  `task_type` varchar(32) DEFAULT NULL COMMENT '任务类型',
  `sh_contract_id` varchar(32) DEFAULT NULL COMMENT 'SH单位合同ID',
  `approval_file_number` varchar(100) DEFAULT NULL COMMENT '放行文件编号',
  `other_work_card_number` varchar(100) DEFAULT NULL COMMENT '其他工卡',
  `is_material_issued` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已发料',
  `return_repair_reason` varchar(500) DEFAULT NULL COMMENT '退修原因',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_contract_task_contract_id` (`contract_id`) USING BTREE,
  KEY `idx_contract_task_create_time` (`create_time`) USING BTREE,
  KEY `idx_contract_task_user_id` (`task_user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='合同任务';

CREATE TABLE IF NOT EXISTS `shkb_tool` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `code` varchar(50) NOT NULL COMMENT '管理编号',
  `name` varchar(100) NOT NULL COMMENT '工具名称',
  `description` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(32) NOT NULL COMMENT '创建人',
  `create_by_id` varchar(32) NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(32) NOT NULL COMMENT '修改人',
  `update_by_id` varchar(32) NOT NULL COMMENT '修改人ID',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `management_area` varchar(100) NOT NULL COMMENT '管理区域',
  `available` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态',
  `last_maintenance_time` date DEFAULT NULL COMMENT '上次计量日期',
  `next_maintenance_time` date DEFAULT NULL COMMENT '下次计量日期',
  `expiration_time` date DEFAULT NULL COMMENT '维保到期日期',
  `certificate_number` varchar(100) DEFAULT NULL COMMENT '证书编号',
  `model` varchar(100) DEFAULT NULL COMMENT '型号',
  `specification` varchar(100) DEFAULT NULL COMMENT '规格',
  `standard` varchar(100) DEFAULT NULL COMMENT '计量标准',
  `precision` varchar(100) DEFAULT NULL COMMENT '精度',
  `storage_location` varchar(200) DEFAULT NULL COMMENT '存放位置',
  `calibration_period` int DEFAULT NULL COMMENT '计量周期',
  `last_maintenance_unit` varchar(100) DEFAULT NULL COMMENT '上次维保单位',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_shkb_tool_code` (`code`) USING BTREE,
  KEY `idx_shkb_tool_expiration` (`available`, `expiration_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='工具管理';

CREATE TABLE IF NOT EXISTS `shkb_device` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `code` varchar(50) NOT NULL COMMENT '设备编号',
  `name` varchar(100) NOT NULL COMMENT '名称',
  `description` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(32) NOT NULL COMMENT '创建人',
  `create_by_id` varchar(32) NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(32) NOT NULL COMMENT '修改人',
  `update_by_id` varchar(32) NOT NULL COMMENT '修改人ID',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `management_area` varchar(100) NOT NULL COMMENT '管理区域',
  `available` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态',
  `maintenance_project` varchar(200) DEFAULT NULL COMMENT '维保项目',
  `maintenance_interval` int DEFAULT NULL COMMENT '维保间隔（月）',
  `maintenance_card` varchar(200) DEFAULT NULL COMMENT '维保工卡',
  `last_maintenance_time` date DEFAULT NULL COMMENT '上次维保时间',
  `next_maintenance_time` date DEFAULT NULL COMMENT '下次维保时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_shkb_device_code` (`code`) USING BTREE,
  KEY `idx_shkb_device_maintenance` (`available`, `next_maintenance_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='设备管理';

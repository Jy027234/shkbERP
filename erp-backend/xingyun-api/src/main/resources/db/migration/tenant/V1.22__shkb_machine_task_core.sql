-- SHKB 自动化设备、拧紧机任务和磁粉机任务核心 schema。
--
-- 这些接口和页面已存在于业务代码，但原项目没有提交对应建表 SQL。
-- 本迁移只补齐代码能够证明的字段与索引，不写入设备 IP 或业务任务数据。

CREATE TABLE IF NOT EXISTS `shkb_machine_info` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `machine_id` varchar(64) NOT NULL COMMENT '设备标识',
  `machine_type` tinyint NOT NULL COMMENT '设备类型：1拧紧机、2磁粉机',
  `machine_name` varchar(100) NOT NULL COMMENT '设备名称',
  `visit_time` datetime DEFAULT NULL COMMENT '最近访问时间',
  `ip_address` varchar(255) DEFAULT NULL COMMENT '设备 IP 或主机地址',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_machine_info_machine_id` (`machine_id`) USING BTREE,
  KEY `idx_machine_info_type` (`machine_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='自动化设备';

CREATE TABLE IF NOT EXISTS `shkb_machine_task_tightening` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `task_id` varchar(64) DEFAULT NULL COMMENT '平台任务ID；线下任务为空',
  `machine_task_status` tinyint NOT NULL DEFAULT 0 COMMENT '任务状态：0待装配、1已完成',
  `task_type` tinyint NOT NULL DEFAULT 0 COMMENT '任务类型：0平台任务、1线下任务',
  `contract_no` varchar(100) DEFAULT NULL COMMENT '合同号',
  `part_no` varchar(100) DEFAULT NULL COMMENT '件号',
  `serial_no` varchar(100) DEFAULT NULL COMMENT '序列号',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `report_time` datetime DEFAULT NULL COMMENT '上报时间',
  `report_data` longtext COMMENT '上报 JSON 数据',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_machine_task_tightening_task` (`task_id`) USING BTREE,
  KEY `idx_machine_task_tightening_status_time` (`machine_task_status`, `create_time`) USING BTREE,
  KEY `idx_machine_task_tightening_contract` (`contract_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='拧紧机任务';

CREATE TABLE IF NOT EXISTS `shkb_machine_task_magnetic_powder` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `task_id` varchar(64) NOT NULL COMMENT '平台任务ID',
  `contract_no` varchar(100) DEFAULT NULL COMMENT '合同号',
  `part_no` varchar(100) DEFAULT NULL COMMENT '件号',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `serial_no` varchar(100) DEFAULT NULL COMMENT '序列号',
  `machine_task_status` tinyint NOT NULL DEFAULT 0 COMMENT '任务状态：0待下发、1已下发',
  `send_time` datetime DEFAULT NULL COMMENT '下发时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_machine_task_magnetic_task` (`task_id`) USING BTREE,
  KEY `idx_machine_task_magnetic_status_time` (`machine_task_status`, `create_time`) USING BTREE,
  KEY `idx_machine_task_magnetic_contract` (`contract_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='磁粉机任务';

-- 兼容已经由人工脚本创建过表、但缺少任务唯一约束的环境。
-- 如果存量库已有重复 task_id，ALTER 会明确失败，必须先在恢复副本中人工对账，
-- 不允许迁移静默删除或合并业务任务。
SET @v122_tightening_unique_ddl = IF(
  EXISTS(
    SELECT 1 FROM information_schema.STATISTICS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'shkb_machine_task_tightening'
       AND INDEX_NAME = 'uk_machine_task_tightening_task'
  ),
  'SELECT 1',
  'ALTER TABLE `shkb_machine_task_tightening` ADD UNIQUE KEY `uk_machine_task_tightening_task` (`task_id`) USING BTREE'
);
PREPARE v122_tightening_unique_stmt FROM @v122_tightening_unique_ddl;
EXECUTE v122_tightening_unique_stmt;
DEALLOCATE PREPARE v122_tightening_unique_stmt;

SET @v122_magnetic_unique_ddl = IF(
  EXISTS(
    SELECT 1 FROM information_schema.STATISTICS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'shkb_machine_task_magnetic_powder'
       AND INDEX_NAME = 'uk_machine_task_magnetic_task'
  ),
  'SELECT 1',
  'ALTER TABLE `shkb_machine_task_magnetic_powder` ADD UNIQUE KEY `uk_machine_task_magnetic_task` (`task_id`) USING BTREE'
);
PREPARE v122_magnetic_unique_stmt FROM @v122_magnetic_unique_ddl;
EXECUTE v122_magnetic_unique_stmt;
DEALLOCATE PREPARE v122_magnetic_unique_stmt;

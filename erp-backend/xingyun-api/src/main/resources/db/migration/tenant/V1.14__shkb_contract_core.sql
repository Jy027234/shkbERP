-- SHKB 合同核心 schema。
--
-- 原项目提交了维修类型、合同维修类型关联与合同附件代码，但没有版本化
-- 对应的建表 SQL。本迁移只补齐合同录入、查询、详情及附件列表明确依赖的结构。

CREATE TABLE IF NOT EXISTS `base_data_repair_type` (
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
  UNIQUE KEY `uk_repair_type_code` (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='维修类型';

CREATE TABLE IF NOT EXISTS `shkb_contract_repair` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `repair_type_id` varchar(32) NOT NULL COMMENT '维修类型ID',
  `contract_id` varchar(32) NOT NULL COMMENT '合同ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_contract_repair` (`contract_id`, `repair_type_id`) USING BTREE,
  KEY `idx_contract_repair_type` (`repair_type_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='合同维修类型关联';

CREATE TABLE IF NOT EXISTS `shkb_contract_file` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `url` varchar(1000) NOT NULL COMMENT '文件地址',
  `contract_id` varchar(32) NOT NULL COMMENT '合同ID',
  `create_time` datetime NOT NULL COMMENT '上传时间',
  `file_name` varchar(255) DEFAULT NULL COMMENT '文件名称',
  `content_type` varchar(255) DEFAULT NULL COMMENT 'Content-Type',
  `file_suffix` varchar(32) DEFAULT NULL COMMENT '文件后缀',
  `file_size` varchar(32) DEFAULT NULL COMMENT '可读文件大小',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_contract_file_contract_time` (`contract_id`, `create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='合同附件';

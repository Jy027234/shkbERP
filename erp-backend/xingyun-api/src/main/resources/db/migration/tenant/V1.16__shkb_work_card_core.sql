-- SHKB 维修工卡核心 schema。
--
-- 补齐工卡主数据、附件、必换件，以及合同任务使用工卡和保存必换件
-- 数量快照及其领料审批守卫所需的结构。只新增缺失表，不修改已有业务数据。

CREATE TABLE IF NOT EXISTS `shkb_work_card` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `code` varchar(50) NOT NULL COMMENT '工卡号',
  `name` varchar(200) NOT NULL COMMENT '工卡名称',
  `available` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态',
  `description` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(32) NOT NULL COMMENT '创建人',
  `create_by_id` varchar(32) NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(32) NOT NULL COMMENT '修改人',
  `update_by_id` varchar(32) NOT NULL COMMENT '修改人ID',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `repair_type_id` varchar(32) DEFAULT NULL COMMENT '维修类型ID',
  `part_number_id` varchar(32) NOT NULL COMMENT '工卡件号ID',
  `approval_date` datetime DEFAULT NULL COMMENT '批准日期',
  `customer_id` varchar(32) DEFAULT NULL COMMENT '客户ID',
  `version` varchar(50) DEFAULT NULL COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_work_card_code` (`code`) USING BTREE,
  KEY `idx_work_card_part_number` (`part_number_id`) USING BTREE,
  KEY `idx_work_card_repair_type` (`repair_type_id`) USING BTREE,
  KEY `idx_work_card_customer` (`customer_id`) USING BTREE,
  KEY `idx_work_card_available_time` (`available`, `create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='维修工卡';

CREATE TABLE IF NOT EXISTS `shkb_work_card_file` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `url` varchar(1000) NOT NULL COMMENT '文件地址',
  `work_card_id` varchar(32) NOT NULL COMMENT '工卡ID',
  `create_time` datetime NOT NULL COMMENT '上传时间',
  `file_name` varchar(255) DEFAULT NULL COMMENT '文件名称',
  `content_type` varchar(255) DEFAULT NULL COMMENT 'Content-Type',
  `file_suffix` varchar(32) DEFAULT NULL COMMENT '文件后缀',
  `file_size` varchar(32) DEFAULT NULL COMMENT '可读文件大小',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_work_card_file_card_time` (`work_card_id`, `create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='工卡附件';

CREATE TABLE IF NOT EXISTS `shkb_work_card_product` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `product_id` varchar(32) NOT NULL COMMENT '必换商品ID',
  `work_card_id` varchar(32) NOT NULL COMMENT '工卡ID',
  `quantity` int NOT NULL DEFAULT 1 COMMENT '数量',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_work_card_product` (`work_card_id`, `product_id`) USING BTREE,
  KEY `idx_work_card_product_product` (`product_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='工卡必换件';

CREATE TABLE IF NOT EXISTS `shkb_contract_task_work_card` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `task_id` varchar(32) NOT NULL COMMENT '合同任务ID',
  `work_card_id` varchar(32) NOT NULL COMMENT '工卡ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_task_work_card` (`task_id`, `work_card_id`) USING BTREE,
  KEY `idx_task_work_card_card` (`work_card_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='合同任务工卡';

CREATE TABLE IF NOT EXISTS `shkb_contract_task_work_card_product` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `task_id` varchar(32) NOT NULL COMMENT '合同任务ID',
  `product_id` varchar(32) NOT NULL COMMENT '必换商品ID',
  `quantity` int NOT NULL DEFAULT 1 COMMENT '任务确认数量',
  `work_card_id` varchar(32) NOT NULL COMMENT '来源工卡ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_task_work_card_product` (`task_id`, `work_card_id`, `product_id`) USING BTREE,
  KEY `idx_task_work_card_product_card` (`work_card_id`) USING BTREE,
  KEY `idx_task_work_card_product_product` (`product_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='合同任务必换件数量快照';

CREATE TABLE IF NOT EXISTS `shkb_contract_task_material_apply` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `task_id` varchar(32) NOT NULL COMMENT '合同任务ID',
  `apply_code` varchar(50) NOT NULL COMMENT '领料申请编号',
  `create_time` datetime NOT NULL COMMENT '申请时间',
  `approval_status` tinyint NOT NULL DEFAULT 0 COMMENT '审批状态：0待审、1通过、2拒绝',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_material_apply_task` (`task_id`) USING BTREE,
  UNIQUE KEY `uk_material_apply_code` (`apply_code`) USING BTREE,
  KEY `idx_material_apply_status_time` (`approval_status`, `create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='合同任务领料申请';

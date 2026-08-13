-- SHKB 领料申请、发料单与发料出库核心 schema。
--
-- 只新增缺失结构，不删除或重建已有业务表。部署前仍需备份目标租户库，
-- 并按版本顺序单独执行；应用启动不会自动执行本目录迁移。

CREATE TABLE IF NOT EXISTS `shkb_contract_task_non_part_product` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `task_id` varchar(32) NOT NULL COMMENT '合同任务ID',
  `product_id` varchar(32) NOT NULL COMMENT '非必换商品ID',
  `quantity` int NOT NULL DEFAULT 1 COMMENT '数量',
  `reason` varchar(500) DEFAULT NULL COMMENT '原因说明',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_task_non_part_task` (`task_id`) USING BTREE,
  KEY `idx_task_non_part_product` (`product_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='合同任务非必换件';

CREATE TABLE IF NOT EXISTS `shkb_contract_task_non_part_file` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `task_id` varchar(32) NOT NULL COMMENT '合同任务ID',
  `non_part_id` varchar(32) NOT NULL COMMENT '非必换件记录ID',
  `url` varchar(1000) NOT NULL COMMENT '文件地址',
  `file_suffix` varchar(32) DEFAULT NULL COMMENT '文件后缀',
  `file_size` varchar(32) DEFAULT NULL COMMENT '可读文件大小',
  `file_name` varchar(255) DEFAULT NULL COMMENT '文件名称',
  `content_type` varchar(255) DEFAULT NULL COMMENT 'Content-Type',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_task_non_part_file_record` (`non_part_id`) USING BTREE,
  KEY `idx_task_non_part_file_task` (`task_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='合同任务非必换件附件';

CREATE TABLE IF NOT EXISTS `shkb_material_order` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `code` varchar(50) NOT NULL COMMENT '发料单号',
  `sc_id` varchar(32) NOT NULL COMMENT '仓库ID',
  `total_num` int NOT NULL DEFAULT 0 COMMENT '应发数量',
  `total_out_num` int NOT NULL DEFAULT 0 COMMENT '已发数量',
  `total_amount` decimal(24,2) NOT NULL DEFAULT 0.00 COMMENT '发料金额',
  `description` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(32) NOT NULL COMMENT '创建人',
  `create_by_id` varchar(32) NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `material_apply_id` varchar(32) DEFAULT NULL COMMENT '领料申请ID',
  `is_out_finish` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否全部出库',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_material_order_code` (`code`) USING BTREE,
  UNIQUE KEY `uk_material_order_apply` (`material_apply_id`) USING BTREE,
  KEY `idx_material_order_sc_time` (`sc_id`, `create_time`) USING BTREE,
  KEY `idx_material_order_finish_time` (`is_out_finish`, `create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='发料单';

CREATE TABLE IF NOT EXISTS `shkb_material_order_detail` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `order_id` varchar(32) NOT NULL COMMENT '发料单ID',
  `product_id` varchar(32) NOT NULL COMMENT '商品ID',
  `tax_price` decimal(24,6) NOT NULL DEFAULT 0.000000 COMMENT '含税单价',
  `tax_amount` decimal(24,2) NOT NULL DEFAULT 0.00 COMMENT '含税金额',
  `description` varchar(500) DEFAULT NULL COMMENT '备注',
  `out_num` int NOT NULL DEFAULT 0 COMMENT '已出库数量',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '应发数量',
  `ori_bundle_detail_id` varchar(32) DEFAULT NULL COMMENT '组合商品原始明细ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_material_order_detail_order` (`order_id`) USING BTREE,
  KEY `idx_material_order_detail_product` (`product_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='发料单明细';

CREATE TABLE IF NOT EXISTS `tbl_product_stock_batch` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `sc_id` varchar(32) NOT NULL COMMENT '仓库ID',
  `product_id` varchar(32) NOT NULL COMMENT '商品ID',
  `quantity` int NOT NULL DEFAULT 0 COMMENT '库存数量',
  `batch_number` varchar(100) NOT NULL COMMENT '批次号',
  `shelf_location` varchar(100) DEFAULT NULL COMMENT '架位',
  `production_date` date DEFAULT NULL COMMENT '生产日期',
  `expiry_date` date DEFAULT NULL COMMENT '失效日期',
  `supplier_id` varchar(32) DEFAULT NULL COMMENT '供应商ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_product_stock_batch` (`sc_id`, `product_id`, `batch_number`) USING BTREE,
  KEY `idx_product_stock_batch_product` (`product_id`) USING BTREE,
  KEY `idx_product_stock_batch_expiry` (`expiry_date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='商品批次库存';

CREATE TABLE IF NOT EXISTS `tbl_product_stock_serial` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `product_id` varchar(32) NOT NULL COMMENT '商品ID',
  `serial_number` varchar(100) NOT NULL COMMENT '唯一序列号',
  `stock_status` tinyint NOT NULL DEFAULT 1 COMMENT '在库状态：1在库、0出库',
  `batch_id` varchar(32) NOT NULL COMMENT '批次库存ID',
  `production_date` date DEFAULT NULL COMMENT '生产日期',
  `expiry_date` date DEFAULT NULL COMMENT '失效日期',
  `shelf_location` varchar(100) DEFAULT NULL COMMENT '架位',
  `supplier_id` varchar(32) DEFAULT NULL COMMENT '供应商ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_product_stock_serial` (`serial_number`) USING BTREE,
  KEY `idx_product_stock_serial_batch` (`batch_id`) USING BTREE,
  KEY `idx_product_stock_serial_product_status` (`product_id`, `stock_status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='商品序列号库存';

CREATE TABLE IF NOT EXISTS `tbl_material_out_sheet` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `code` varchar(50) NOT NULL COMMENT '发料出库单号',
  `sc_id` varchar(32) NOT NULL COMMENT '仓库ID',
  `supplier_id` varchar(32) DEFAULT NULL COMMENT '供应商ID',
  `material_user_id` varchar(32) DEFAULT NULL COMMENT '发料员ID',
  `material_date` date DEFAULT NULL COMMENT '发料日期',
  `material_order_id` varchar(32) DEFAULT NULL COMMENT '发料单ID',
  `total_num` int NOT NULL DEFAULT 0 COMMENT '本次出库数量',
  `total_amount` decimal(24,2) NOT NULL DEFAULT 0.00 COMMENT '本次出库金额',
  `description` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(32) NOT NULL COMMENT '创建人',
  `create_by_id` varchar(32) NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(32) NOT NULL COMMENT '修改人',
  `update_by_id` varchar(32) NOT NULL COMMENT '修改人ID',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `approve_by` varchar(32) DEFAULT NULL COMMENT '发料审核人ID',
  `approve_time` datetime DEFAULT NULL COMMENT '发料时间',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0备料中、1已发料、2可领料',
  `refuse_reason` varchar(500) DEFAULT NULL COMMENT '可领料备注',
  `tx_id` varchar(64) DEFAULT NULL COMMENT '事务ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_material_out_sheet_code` (`code`) USING BTREE,
  KEY `idx_material_out_sheet_order` (`material_order_id`) USING BTREE,
  KEY `idx_material_out_sheet_sc_time` (`sc_id`, `create_time`) USING BTREE,
  KEY `idx_material_out_sheet_status_time` (`status`, `create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='发料出库单';

CREATE TABLE IF NOT EXISTS `tbl_material_out_sheet_detail` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `sheet_id` varchar(32) NOT NULL COMMENT '发料出库单ID',
  `product_id` varchar(32) NOT NULL COMMENT '商品ID',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '发料单应发数量',
  `ori_price` decimal(24,6) DEFAULT NULL COMMENT '原价',
  `tax_price` decimal(24,6) NOT NULL DEFAULT 0.000000 COMMENT '含税单价',
  `tax_rate` decimal(5,2) DEFAULT NULL COMMENT '税率',
  `description` varchar(500) DEFAULT NULL COMMENT '备注',
  `order_no` int NOT NULL DEFAULT 1 COMMENT '排序号',
  `out_num` int NOT NULL DEFAULT 0 COMMENT '本次出库数量',
  `stock_batch_id` varchar(32) DEFAULT NULL COMMENT '批次库存ID',
  `serial_numbers` varchar(2000) DEFAULT NULL COMMENT '序列号展示文本',
  `tax_amount` decimal(24,2) NOT NULL DEFAULT 0.00 COMMENT '含税金额',
  `material_order_detail_id` varchar(32) DEFAULT NULL COMMENT '发料单明细ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_material_out_detail_sheet_order` (`sheet_id`, `order_no`) USING BTREE,
  KEY `idx_material_out_detail_product` (`product_id`) USING BTREE,
  KEY `idx_material_out_detail_order_detail` (`material_order_detail_id`) USING BTREE,
  KEY `idx_material_out_detail_batch` (`stock_batch_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='发料出库单明细';

CREATE TABLE IF NOT EXISTS `tbl_material_out_sheet_detail_serial` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `sheet_id` varchar(32) NOT NULL COMMENT '发料出库单ID',
  `product_id` varchar(32) NOT NULL COMMENT '商品ID',
  `stock_serial_id` varchar(32) NOT NULL COMMENT '序列号库存ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_material_out_detail_serial` (`sheet_id`, `stock_serial_id`) USING BTREE,
  KEY `idx_material_out_serial_product` (`product_id`) USING BTREE,
  KEY `idx_material_out_serial_stock` (`stock_serial_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='发料出库单序列号明细';

-- 批次出库日志会写入 stock_batch_id；旧库存日志表没有该列时幂等补齐。
SET @v117_add_stock_batch_id = IF(
  EXISTS(
    SELECT 1
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'tbl_product_stock_log'
      AND COLUMN_NAME = 'stock_batch_id'
  ),
  'SELECT 1',
  'ALTER TABLE `tbl_product_stock_log` ADD COLUMN `stock_batch_id` varchar(32) DEFAULT NULL COMMENT ''批次库存ID'' AFTER `biz_type`, ADD KEY `idx_product_stock_log_batch` (`stock_batch_id`) USING BTREE'
);
PREPARE v117_stmt FROM @v117_add_stock_batch_id;
EXECUTE v117_stmt;
DEALLOCATE PREPARE v117_stmt;

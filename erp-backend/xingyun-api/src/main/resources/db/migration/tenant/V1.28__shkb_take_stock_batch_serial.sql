-- SHKB take-stock batch/serial detail contract (business-confirmed 2026-08-17/18).
-- Rules versioned here:
--   * 盘点明细必须逐批次、逐序列号录入；一条序列号对应一条明细。
--   * 批次实盘数量允许调整（盘盈/盘亏按实盘数量与系统数量差异处理）。
--   * 盘点单提交后允许修改或撤销（沿用既有盘点单状态机，明细可整体替换）。
--   * 审核通过并生成差异后，差异处理立即按批次/序列号变更库存。
--   * 同一批次/序列号在同一盘点单内重复提交将被拒绝（应用校验 + 唯一键兜底）。
--   * 已锁定/出库/调拨/报废状态的批次、序列号仍允许录入盘点明细，差异处理按
--     实盘状态与系统状态逐条判定（见 TakeStockPlanServiceImpl.handleDiff）。
-- Only CREATE TABLE IF NOT EXISTS; no data mutation.

SET NAMES utf8mb4;
START TRANSACTION;

CREATE TABLE IF NOT EXISTS `tbl_take_stock_sheet_detail_batch` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `sheet_id` varchar(32) NOT NULL COMMENT '盘点单ID',
  `sheet_detail_id` varchar(32) NOT NULL COMMENT '盘点单明细ID',
  `product_id` varchar(32) NOT NULL COMMENT '商品ID',
  `batch_number` varchar(100) NOT NULL COMMENT '批次号',
  `stock_num` int NOT NULL DEFAULT 0 COMMENT '系统批次库存数量（录入时快照）',
  `take_num` int NOT NULL DEFAULT 0 COMMENT '实盘数量',
  `description` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_take_stock_sheet_batch` (`sheet_detail_id`, `batch_number`) USING BTREE,
  KEY `idx_take_stock_sheet_batch_sheet` (`sheet_id`) USING BTREE,
  KEY `idx_take_stock_sheet_batch_product` (`product_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='盘点单批次明细';

CREATE TABLE IF NOT EXISTS `tbl_take_stock_sheet_detail_serial` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `sheet_id` varchar(32) NOT NULL COMMENT '盘点单ID',
  `sheet_detail_id` varchar(32) NOT NULL COMMENT '盘点单明细ID',
  `product_id` varchar(32) NOT NULL COMMENT '商品ID',
  `serial_number` varchar(100) NOT NULL COMMENT '序列号',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批次号（盘盈序列号归属批次）',
  `take_status` tinyint NOT NULL DEFAULT 1 COMMENT '实盘状态：1实盘在库、0实盘缺失',
  `description` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_take_stock_sheet_serial` (`sheet_detail_id`, `serial_number`) USING BTREE,
  KEY `idx_take_stock_sheet_serial_sheet` (`sheet_id`) USING BTREE,
  KEY `idx_take_stock_sheet_serial_product` (`product_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='盘点单序列号明细（一条序列号一条明细）';

COMMIT;

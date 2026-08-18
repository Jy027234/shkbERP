-- SHKB stock-adjust batch/serial detail contract (business-confirmed 2026-08-17/18).
-- Rules versioned here:
--   * 调整单入库、出库均允许批次/序列号明细；调整数量必须等于明细数量之和。
--   * 序列号不允许从一种状态直接跳到另一种状态：调整入库仅允许“新序列号入库”或
--     “已出库序列号重新入库”(0->1)；调整出库仅允许“在库序列号出库”(1->0)；
--     同一序列号重复入库/重复出库被拒绝。
--   * 审核通过后立即变更库存；重复提交、重复审核与并发审核由既有单据行锁与
--     条件更新拒绝。
--   * 审核驳回后允许修改并重新提交（沿用既有状态机，追溯明细整体替换）。
--   * 负库存与重复序列号不允许存在（批次出库条件更新守卫充足库存，
--     序列号状态条件更新拒绝并发冲突）。
--   * 跨仓库引用不属于本单业务，跨仓场景应走仓库调拨流程。
-- Only CREATE TABLE IF NOT EXISTS; no data mutation.

SET NAMES utf8mb4;
START TRANSACTION;

CREATE TABLE IF NOT EXISTS `tbl_stock_adjust_sheet_detail_batch` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `sheet_id` varchar(32) NOT NULL COMMENT '库存调整单ID',
  `sheet_detail_id` varchar(32) NOT NULL COMMENT '库存调整单明细ID',
  `product_id` varchar(32) NOT NULL COMMENT '商品ID',
  `batch_number` varchar(100) NOT NULL COMMENT '批次号',
  `stock_num` int NOT NULL DEFAULT 0 COMMENT '调整数量（入库/出库均为正数，方向由调整单业务类型决定）',
  `description` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_stock_adjust_sheet_batch` (`sheet_detail_id`, `batch_number`) USING BTREE,
  KEY `idx_stock_adjust_sheet_batch_sheet` (`sheet_id`) USING BTREE,
  KEY `idx_stock_adjust_sheet_batch_product` (`product_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='库存调整单批次明细';

CREATE TABLE IF NOT EXISTS `tbl_stock_adjust_sheet_detail_serial` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `sheet_id` varchar(32) NOT NULL COMMENT '库存调整单ID',
  `sheet_detail_id` varchar(32) NOT NULL COMMENT '库存调整单明细ID',
  `product_id` varchar(32) NOT NULL COMMENT '商品ID',
  `serial_number` varchar(100) NOT NULL COMMENT '序列号',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批次号（入库序列号归属批次）',
  `description` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_stock_adjust_sheet_serial` (`sheet_detail_id`, `serial_number`) USING BTREE,
  KEY `idx_stock_adjust_sheet_serial_sheet` (`sheet_id`) USING BTREE,
  KEY `idx_stock_adjust_sheet_serial_product` (`product_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='库存调整单序列号明细（一条序列号一条明细）';

COMMIT;

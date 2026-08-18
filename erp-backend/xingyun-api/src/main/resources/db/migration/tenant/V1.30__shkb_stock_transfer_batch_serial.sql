-- SHKB stock-transfer batch/serial detail contract (business-confirmed 2026-08-17/18).
-- Rules versioned here:
--   * 调拨单必须逐批次、逐序列号指定库存；转出单据审核通过后立即扣减转出仓库存，
--     收货时才增加转入仓库存（两阶段语义）。
--   * 部分收货允许：批次按已收数量累计，序列号逐条收货；
--     最终收货条件为全部批次已收齐且全部序列号已收货。
--   * 在途库存单独记录：批次在途 = transfer_num - received_num，
--     序列号在途 = transfer_status = 1 的行。
--   * 收货明细与调拨明细不一致（序列号/批次不属于本单或超出未收数量）时退回，
--     拒绝本次收货，不允许部分写入。
--   * 重复收货、并发收货由行锁与条件更新拒绝；一张调拨单只有一进一出。
--   * 批次/序列号允许跨仓、跨库位调拨：序列号收货时切换到转入仓对应批次。
-- Only CREATE TABLE IF NOT EXISTS; no data mutation.

SET NAMES utf8mb4;
START TRANSACTION;

CREATE TABLE IF NOT EXISTS `tbl_sc_transfer_order_detail_batch` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `order_id` varchar(32) NOT NULL COMMENT '仓库调拨单ID',
  `order_detail_id` varchar(32) NOT NULL COMMENT '仓库调拨单明细ID',
  `product_id` varchar(32) NOT NULL COMMENT '商品ID',
  `batch_number` varchar(100) NOT NULL COMMENT '批次号',
  `transfer_num` int NOT NULL DEFAULT 0 COMMENT '调拨数量',
  `received_num` int NOT NULL DEFAULT 0 COMMENT '已收货数量（在途=transfer_num-received_num）',
  `description` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_sc_transfer_order_batch` (`order_detail_id`, `batch_number`) USING BTREE,
  KEY `idx_sc_transfer_order_batch_order` (`order_id`) USING BTREE,
  KEY `idx_sc_transfer_order_batch_product` (`product_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='仓库调拨单批次明细（在途库存按未收数量单独记录）';

CREATE TABLE IF NOT EXISTS `tbl_sc_transfer_order_detail_serial` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `order_id` varchar(32) NOT NULL COMMENT '仓库调拨单ID',
  `order_detail_id` varchar(32) NOT NULL COMMENT '仓库调拨单明细ID',
  `product_id` varchar(32) NOT NULL COMMENT '商品ID',
  `serial_number` varchar(100) NOT NULL COMMENT '序列号',
  `transfer_status` tinyint NOT NULL DEFAULT 1 COMMENT '调拨状态：1在途、2已收货',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批次号（收货时在转入仓归属批次）',
  `description` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_sc_transfer_order_serial` (`order_detail_id`, `serial_number`) USING BTREE,
  KEY `idx_sc_transfer_order_serial_order` (`order_id`) USING BTREE,
  KEY `idx_sc_transfer_order_serial_product` (`product_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='仓库调拨单序列号明细（一条序列号一条明细，在途状态单独记录）';

COMMIT;

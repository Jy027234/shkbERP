-- 采购收货明细的批次/序列号追溯字段。
-- Java 实体、Mapper 和收货审批流程已依赖这些列，历史初始化脚本与增量迁移遗漏了它们。
-- 每列独立判断，兼容某些环境已由人工补过部分列的情况。

SET @receive_batch_ddl = IF(
  EXISTS(SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'tbl_receive_sheet_detail'
      AND column_name = 'batch_number'),
  'SELECT 1',
  'ALTER TABLE `tbl_receive_sheet_detail` ADD COLUMN `batch_number` varchar(100) NOT NULL DEFAULT ''DEFAULT'' COMMENT ''批次号'' AFTER `return_num`'
);
PREPARE receive_batch_stmt FROM @receive_batch_ddl;
EXECUTE receive_batch_stmt;
DEALLOCATE PREPARE receive_batch_stmt;

SET @receive_serial_ddl = IF(
  EXISTS(SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'tbl_receive_sheet_detail'
      AND column_name = 'serial_number_list'),
  'SELECT 1',
  'ALTER TABLE `tbl_receive_sheet_detail` ADD COLUMN `serial_number_list` text NULL COMMENT ''唯一序列号列表，多个序列号用逗号分隔'' AFTER `batch_number`'
);
PREPARE receive_serial_stmt FROM @receive_serial_ddl;
EXECUTE receive_serial_stmt;
DEALLOCATE PREPARE receive_serial_stmt;

SET @receive_production_date_ddl = IF(
  EXISTS(SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'tbl_receive_sheet_detail'
      AND column_name = 'production_date'),
  'SELECT 1',
  'ALTER TABLE `tbl_receive_sheet_detail` ADD COLUMN `production_date` date NULL COMMENT ''生产日期'' AFTER `serial_number_list`'
);
PREPARE receive_production_date_stmt FROM @receive_production_date_ddl;
EXECUTE receive_production_date_stmt;
DEALLOCATE PREPARE receive_production_date_stmt;

SET @receive_expiry_date_ddl = IF(
  EXISTS(SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'tbl_receive_sheet_detail'
      AND column_name = 'expiry_date'),
  'SELECT 1',
  'ALTER TABLE `tbl_receive_sheet_detail` ADD COLUMN `expiry_date` date NULL COMMENT ''失效日期'' AFTER `production_date`'
);
PREPARE receive_expiry_date_stmt FROM @receive_expiry_date_ddl;
EXECUTE receive_expiry_date_stmt;
DEALLOCATE PREPARE receive_expiry_date_stmt;

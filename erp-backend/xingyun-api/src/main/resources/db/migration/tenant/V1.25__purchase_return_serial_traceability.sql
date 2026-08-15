-- 采购退货序列号追溯。
--
-- 仅为退货明细补充已选择的序列号快照；不删除或重建任何现有业务表。
-- 应用本迁移前仍需备份目标租户库，并按版本顺序单独执行。

SET @v125_add_return_serials = IF(
  EXISTS(
    SELECT 1
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'tbl_purchase_return_detail'
      AND COLUMN_NAME = 'serial_number_list'
  ),
  'SELECT 1',
  'ALTER TABLE `tbl_purchase_return_detail` ADD COLUMN `serial_number_list` text NULL COMMENT ''本次退货序列号，多个序列号用逗号分隔'' AFTER `receive_sheet_detail_id`'
);
PREPARE v125_stmt FROM @v125_add_return_serials;
EXECUTE v125_stmt;
DEALLOCATE PREPARE v125_stmt;

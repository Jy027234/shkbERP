-- 二期代码（批次/序列号管理）依赖的商品表列，原 dump 与既有增量 SQL 均未包含
ALTER TABLE `base_data_product`
  ADD COLUMN `is_batch` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否批次管理' AFTER `available`;
ALTER TABLE `base_data_product`
  ADD COLUMN `is_serial` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否序列号管理' AFTER `is_batch`;

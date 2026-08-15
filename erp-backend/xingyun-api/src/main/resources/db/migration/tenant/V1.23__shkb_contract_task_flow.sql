-- SHKB 合同任务流程补充 schema。
--
-- 合同任务创建会在同一事务中写入初始维修状态记录。此前该实体和接口已有，
-- 但部署迁移没有对应表，导致从版本化迁移建立的环境无法生成合同任务。

CREATE TABLE IF NOT EXISTS `shkb_contract_task_repair_status_record` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `task_id` varchar(32) NOT NULL COMMENT '合同任务ID',
  `repair_status` varchar(32) NOT NULL COMMENT '维修状态',
  `description` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(32) NOT NULL COMMENT '创建人',
  `create_by_id` varchar(32) NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_task_repair_status_task_time` (`task_id`, `create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='合同任务维修状态记录';

-- 一个合同在当前业务模型中只允许生成一个任务。若存量数据违反该约束，迁移应
-- 明确失败并先人工对账，不自动删除或合并业务数据。
SET @contract_task_unique_ddl = IF(
  EXISTS(
    SELECT index_name
      FROM information_schema.statistics
     WHERE table_schema = DATABASE()
       AND table_name = 'shkb_contract_task'
       AND index_name = 'uk_contract_task_contract'
     GROUP BY index_name
    HAVING MIN(non_unique) = 0
       AND COUNT(*) = 1
       AND MAX(CASE WHEN seq_in_index = 1 AND column_name = 'contract_id' THEN 1 ELSE 0 END) = 1
  ),
  'SELECT 1',
  'ALTER TABLE `shkb_contract_task` ADD UNIQUE KEY `uk_contract_task_contract` (`contract_id`) USING BTREE'
);
PREPARE contract_task_unique_stmt FROM @contract_task_unique_ddl;
EXECUTE contract_task_unique_stmt;
DEALLOCATE PREPARE contract_task_unique_stmt;

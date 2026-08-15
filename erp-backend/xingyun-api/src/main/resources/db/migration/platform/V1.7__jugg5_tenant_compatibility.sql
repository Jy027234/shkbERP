-- Compatibility bridge for databases that predate platform/V1.6.
-- MySQL does not provide portable ALTER TABLE ... ADD COLUMN IF NOT EXISTS,
-- so each information_schema guard emits either the required DDL or SELECT 1.
-- This preserves idempotency without replaying the historical V1.6 file.
SET @shkb_v17_schema := DATABASE();

SET @shkb_v17_server_name_ddl := (
  SELECT IF(
    EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE table_schema = @shkb_v17_schema
        AND table_name = 'tenant'
        AND column_name = 'server_name'
    ),
    'SELECT 1',
    'ALTER TABLE `tenant` ADD COLUMN `server_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT ''域名'' AFTER `name`'
  )
);
PREPARE shkb_v17_server_name FROM @shkb_v17_server_name_ddl;
EXECUTE shkb_v17_server_name;
DEALLOCATE PREPARE shkb_v17_server_name;

SET @shkb_v17_is_platform_ddl := (
  SELECT IF(
    EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE table_schema = @shkb_v17_schema
        AND table_name = 'tenant'
        AND column_name = 'is_platform'
    ),
    'SELECT 1',
    'ALTER TABLE `tenant` ADD COLUMN `is_platform` tinyint(1) NOT NULL DEFAULT 0 COMMENT ''是否为平台管理租户'' AFTER `jdbc_password`'
  )
);
PREPARE shkb_v17_is_platform FROM @shkb_v17_is_platform_ddl;
EXECUTE shkb_v17_is_platform;
DEALLOCATE PREPARE shkb_v17_is_platform;

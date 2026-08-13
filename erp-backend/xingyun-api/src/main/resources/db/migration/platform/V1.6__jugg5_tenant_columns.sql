-- jugg 5 的 Tenant 实体新增两列，与上游 xingyun platform.sql 保持一致
ALTER TABLE `tenant`
  ADD COLUMN `server_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '域名' AFTER `name`;
ALTER TABLE `tenant`
  ADD COLUMN `is_platform` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否为平台管理租户' AFTER `jdbc_password`;

-- 与上游一致：默认租户标记为平台管理租户
UPDATE `tenant` SET `is_platform` = 1 WHERE `id` = 1000;

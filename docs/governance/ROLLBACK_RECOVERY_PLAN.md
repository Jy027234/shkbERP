# 本地回退与恢复方案（隔离演练，非生产运行书）

> 2026-08-18 起草。本文件把数据库、后端、前端和附件的可执行回退步骤固化为**本地隔离环境**演练流程；
> 未经用户对具体候选版本和变更窗口的明确批准，不得对生产执行。生产回退需在此文档基础上补充业务确认和变更窗口。

## 适用边界

- 只针对 `shkbERP` 本地候选（当前 `main` 未提交 HR 改动 + V1.26 + verify-hr-flow）。
- 演练目标：`xingyun-smoke-mysql`、`kberp-api` 容器、`erp-frontend` Vite/构建产物。
- 每次演练前先记录当前镜像、jar、迁移哈希和数据库逻辑导出 SHA-256。

## 1. 数据库回退

前提：只对本地隔离库执行；已有数据的库严禁执行 `V1.0__init.sql`（DROP/CREATE 全量）。

回退步骤（本地演练）：

```powershell
# 1) 记录当前状态
docker exec xingyun-smoke-mysql mysqldump -uroot -p335577 --single-transaction --no-tablespaces shkb_platform > before.sql
Get-FileHash before.sql

# 2) 按迁移目录回退 HR 相关增量（V1.26 只含 CREATE TABLE IF NOT EXISTS，回退 = 手动 DROP 13 张 HR 表）
#    仅本地演练：DROP 顺序必须子表优先
docker exec -i xingyun-smoke-mysql mysql -uroot -p335577 shkb_platform <<'SQL'
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS shkb_person_authorization_file;
DROP TABLE IF EXISTS shkb_person_authorization_project;
DROP TABLE IF EXISTS shkb_person_authorization;
DROP TABLE IF EXISTS shkb_authorization_required_course;
DROP TABLE IF EXISTS shkb_authorization_project;
DROP TABLE IF EXISTS shkb_training_participant;
DROP TABLE IF EXISTS shkb_training_implementation;
DROP TABLE IF EXISTS shkb_training_course_file;
DROP TABLE IF EXISTS shkb_training_course;
DROP TABLE IF EXISTS shkb_employee_training;
DROP TABLE IF EXISTS shkb_employee_certificate;
DROP TABLE IF EXISTS shkb_employee_file;
DROP TABLE IF EXISTS shkb_employee;
SET FOREIGN_KEY_CHECKS=1;
SQL

# 3) 恢复（如需要回到迁移后状态）
docker exec -i xingyun-smoke-mysql mysql -uroot -p335577 shkb_platform < erp-backend/xingyun-api/src/main/resources/db/migration/tenant/V1.26__shkb_hr_core.sql
# 4) 校验表齐全
docker exec xingyun-smoke-mysql mysql -uroot -p335577 -N -B shkb_platform -e "SHOW TABLES LIKE 'shkb_%';"
```

更完整的回退是**基于逻辑备份恢复**：`scripts/verify-backup-restore.ps1` 已实现导出→恢复随机克隆库→比较 SHA-256→清理，只允许对本地 `xingyun-smoke-mysql` 执行。

## 2. 后端回退

```powershell
# 记录当前镜像与标签
docker images kberp-api:local-hr-flow-20260817
# 回退到上一镜像（如 local-stock-transfer-v1-20260815）
docker rm -f kberp-api
docker run -d --name kberp-api --restart unless-stopped --network smoke_default -p 8088:8088   -v kberp-api-data:/opt/data -v kberp-api-logs:/opt/logs \
  -e ...（环境变量同 verify-hr-flow 部署段） \
  kberp-api:local-stock-transfer-v1-20260815
# 校验
Invoke-WebRequest -UseBasicParsing http://127.0.0.1:8088/livez
```

注意：回退后端到旧镜像时，若旧镜像缺少 V1.26 HR 表对应的代码，不会报错（表多无妨）；
若回退的镜像早于某次迁移，需先回退数据库到对应点。

## 3. 前端回退

```powershell
# dev：直接重启 Vite（复用现有 5173）
# 生产构建产物：保留上一版 dist 目录，切换 Nginx/静态目录指向即回退
# 依赖锁定：pnpm-lock.yaml 是回滚点，不要删除
```

## 4. 附件回退

- 附件物理文件位于 `/opt/data/upload`（jugg.upload.location），数据库只存 URL。
- 回退数据库后，物理文件与 DB 行可能不一致：本地演练中 `verify-hr-flow.ps1` 的 finally 清理和
  `Remove-SmokeUploadUrl` 会精确删除测试上传文件；生产恢复需按备份策略决定附件归档。
- 附件生命周期缺口（P1）：删除接口已补物理文件删除（ShkbUploadFileUtil），但合同/工具/设备/工卡等
  模块的删除仍只删 DB 行（物理删除被注释）。统一生命周期需业务确认存储方案后另立专题。

## 5. 演练记录要求

每次演练记录：日期、起始/结束镜像与哈希、数据库逻辑导出 SHA-256、执行的迁移、残留检查结果、
以及是否涉及生产（默认否）。演练通过不代表生产可回退；生产回退需候选版本 + 变更窗口批准。

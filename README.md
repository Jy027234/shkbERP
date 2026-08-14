# SHKB ERP

凯奔 ERP 的统一代码仓库。当前代码已迁移到可持续维护的 Java 25 / Spring Boot 3.5 与 Node.js 24 / Vue 3 基线。

## 目录

- `erp-backend/`：Maven 多模块后端，详细升级与验证说明见 `erp-backend/UPGRADE.md`。
- `erp-frontend/`：Vue 3 前端，详细升级与验证说明见 `erp-frontend/UPGRADE.md`。
- `scripts/verify-all.ps1`：依次执行前后端标准门禁。

## 本地工具链

- JDK 25，`JAVA_HOME` 指向 JDK 25。
- Maven 3.9.x。
- Node.js 24。
- pnpm 固定为 9.15.9，建议直接使用仓库脚本，避免全局版本漂移。

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\verify-all.ps1
```

发布前运行完整后端测试和打包：

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\verify-all.ps1 -Full
```

## 配置安全

生产环境的数据库、Redis、RabbitMQ、上传目录和应用密钥均通过环境变量注入。变量清单见 `erp-backend/.env.example`；该文件只有占位符，不得把真实凭据写回仓库。

MySQL 定时备份默认关闭。只有在运行镜像具备备份工具、备份产物可外部持久化且完成恢复演练后，才设置 `MYSQL_BACKUP_ENABLED=true`。

本地冒烟库恢复演练：

```powershell
cd erp-backend
powershell -ExecutionPolicy Bypass -File .\scripts\verify-backup-restore.ps1
```

该脚本只接受 `xingyun-smoke-mysql` 容器，创建带随机后缀的临时数据库，完成压缩备份、恢复和内容哈希比对后自动删除临时库。

## 运行健康检查

后端只公开不含组件详情的健康端点：

- `/livez`：仅表示应用自身是否需要重启，不依赖外部服务。
- `/readyz`：确认应用已可接收流量，并检查数据库与 Redis。
- `/actuator/health`：综合健康状态，响应只包含 `status` 与非敏感的探针组名称。

本地运行时验证：

```powershell
cd erp-backend
powershell -ExecutionPolicy Bypass -File .\scripts\verify-health.ps1
```

除 `health` 外的 Actuator 端点不对外暴露。运行镜像使用 `/livez` 作为 Docker `HEALTHCHECK`，并为约一分钟的应用启动过程预留 90 秒启动窗口。

## 数据库警告

`erp-backend/xingyun-api/src/main/resources/db/migration/tenant/V1.0__init.sql` 是全量初始化脚本，会 DROP/CREATE 表。它只允许用于新建空库，禁止对已有业务数据的数据库执行。

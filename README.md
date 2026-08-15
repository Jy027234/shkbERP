# SHKB ERP

凯奔 ERP 的统一代码仓库。当前代码已迁移到可持续维护的 Java 25 / Spring Boot 3.5 与 Node.js 24 / Vue 3 基线。

## 目录

- `erp-backend/`：Maven 多模块后端，详细升级与验证说明见 `erp-backend/UPGRADE.md`。
- `erp-frontend/`：Vue 3 前端，详细升级与验证说明见 `erp-frontend/UPGRADE.md`。
- `scripts/verify-all.ps1`：依次执行前后端标准门禁。
- `docs/governance/`：唯一源码、部署基线、业务模块覆盖和发布治理记录。

## 本地工具链

- JDK 25，`JAVA_HOME` 指向 JDK 25。
- Maven 3.9.x。
- Node.js 24。
- pnpm 固定为 9.15.9，建议直接使用仓库脚本，避免全局版本漂移。

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\verify-all.ps1
```

该命令首先校验当前目录确实是 GitHub 单体仓库，防止从旧独立仓库或混合工作区构建。治理与对账状态见 [源码治理说明](docs/governance/SOURCE_OF_TRUTH.md)。

发布前运行完整后端测试和打包：

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\verify-all.ps1 -Full
```

正式发布还必须显式增加 `-Release`。该模式要求工作树干净、HEAD 有版本标签，并且治理基线已经解除部署锁：

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\verify-all.ps1 -Full -Release
```

当前处于旧仓库双向差异对账阶段，生产部署锁保持关闭。云端现网只作为冻结参照，治理脚本不会连接或修改云服务器。

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
- `/readyz`：确认应用已可接收流量，并检查数据库、Redis 与 RabbitMQ。
- `/actuator/health`：综合健康状态，响应只包含 `status` 与非敏感的探针组名称。

本地运行时验证：

```powershell
cd erp-backend
powershell -ExecutionPolicy Bypass -File .\scripts\verify-health.ps1
```

除 `health` 外的 Actuator 端点不对外暴露。运行镜像使用 `/livez` 作为 Docker `HEALTHCHECK`，并为约一分钟的应用启动过程预留 90 秒启动窗口。

## 核心业务回归

后端完整门禁会运行物料出库状态、发料明细归属、累计数量和超发守卫等 JUnit 测试。涉及真实事务、库存扣减、批次或序列号并发时，还必须在本地隔离冒烟库运行：

```powershell
cd erp-backend
powershell -ExecutionPolicy Bypass -File .\scripts\verify-material-flow-write.ps1
powershell -ExecutionPolicy Bypass -File .\scripts\verify-material-concurrency.ps1 -Iterations 5
powershell -ExecutionPolicy Bypass -File .\scripts\verify-purchase-flow.ps1
powershell -ExecutionPolicy Bypass -File .\scripts\verify-purchase-flow.ps1 -BaseUrl http://127.0.0.1:5173/api
```

这些脚本会直接造数，只允许连接本机隔离环境，并在结束时精确清理测试数据。采购流程脚本同时验证批次扣减、序列号部分退货和并发状态变化时的事务回滚。

## RabbitMQ 失败恢复

消费者异常会按 1 秒、2 秒退避最多尝试 3 次；仍然失败的原始消息和异常诊断会经发布确认写入持久化 `shkb.failed` 队列，避免无限重新入队。生产者连接瞬时异常同样执行有限重试，不可路由消息必须返回生产端。

系统通知、邮件和站内信三个队列只由项目侧 Listener 消费；兼容处理器会移除 jugg 5 自动配置导入的同队列重复 Listener，同时保留 jugg 独有的导出任务 Listener，防止不同 DTO/服务栈竞争同一条消息。

`shkb.failed` 出现消息时应触发运维告警；必须先修复根因并确认目标操作具备幂等性，再人工重放，禁止自动循环回灌。RabbitMQ 同时纳入 `/readyz`，连接不可用时实例停止接收新流量。

本地 RabbitMQ 与隔离库运行时，可验证真实失败恢复链路：

```powershell
cd erp-backend
powershell -ExecutionPolicy Bypass -File .\scripts\verify-rabbitmq-recovery.ps1
```

脚本会向独立的图表消费队列注入一条因空金额必然回滚的订单事件，确认重试退避、失败队列、原交换机和异常诊断字段后自动清理测试消息，不写入业务数据。

## 数据库警告

`erp-backend/xingyun-api/src/main/resources/db/migration/tenant/V1.0__init.sql` 是全量初始化脚本，会 DROP/CREATE 表。它只允许用于新建空库，禁止对已有业务数据的数据库执行。

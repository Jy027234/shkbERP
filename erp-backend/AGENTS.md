# 后端协作约束

本文件适用于整个 `erp-backend` 仓库。

## 技术基线

- 使用 Java 25 LTS、Spring Boot 3.5.x、jugg 5.x 和 `jakarta.*` API。
- 不得重新引入 Java 8、Spring Boot 2、`javax.*` servlet/persistence/validation 依赖或旧版 Springfox 运行时。
- 根 reactor 当前包含 10 个项目；`cloud/xingyun-cloud-api` 与 `cloud/xingyun-cloud-gateway` 尚未纳入 reactor，不能把主 reactor 通过解释为 cloud 已验证。
- 迁移背景和残余风险见 `UPGRADE.md` 与 `.github/modernize/java-upgrade/20260812043920/progress.md`。

## 修改规则

- 开始前执行 `git status --short`。仓库已有大规模迁移改动，禁止清理、回退或批量重写无关文件。
- 不编辑 `target/`、`logs/`、`upload/` 或升级记录中的生成日志/产物。
- 数据库结构变更只能新增有序增量 SQL；不得改写已部署迁移。严禁在已有数据库执行 `migration/tenant/V1.0__init.sql`。
- 产品后续按单租户部署。保留现有租户上下文与登录租户字段以兼容 jugg 和存量数据，但不主动扩展跨租户能力或测试矩阵；权限和数据范围语义仍必须保持。涉及认证、打印、导出、消息或定时任务时，编译通过不等于完成，必须补相应运行时验证。
- 业务范围以根目录 `docs/governance/PRODUCTION_FUNCTION_BASELINE.md` 为准：人事管理是已确认的现网模块；销售相关通用代码和“成品出入库”属于冻结/非发布范围，未经用户明确启用不得补后端、schema 或测试。
- Swagger 2 注解和 jugg inner 双栈目前是兼容层，除非任务专门治理该风险，不要顺手删除。

## 发布恢复与迁移治理

- migration-catalog.json 是全部版本化 SQL 的哈希和安全分类基线。现有数据的库只能考虑 catalog 中 existingDatabasePlan 的 SQL；V1.0 和历史重建 SQL 永远不得补跑。
- 涉及迁移目录、发布或 schema 时，先运行根目录 verify-migration-catalog.ps1；再在本地 xingyun-smoke-mysql 运行 verify-release-preflight.ps1。两个脚本不会连接云端。
- verify-release-restore.ps1 只会把本地源库恢复到受限名称的随机克隆库，对候选迁移连续执行两轮并在 finally 中清理。它通过不代表生产恢复副本通过。
- V1.21 会更新租户名称并移除三个租户模块关系；即使技术预检通过，生产前也必须获得业务负责人确认。

## 验证

- 标准门禁：`powershell -ExecutionPolicy Bypass -File .\scripts\verify.ps1`。
- 停止运行中的后端 JVM 后，可增加 `-Full` 执行包含测试与打包的 Maven `verify`；发布前应使用此模式。
- 脚本会定位 Java 25 并执行整个 Maven reactor 的 compile；不得用 Java 17/8 的结果代替。
- `xingyun-core` 已建立全局异常 HTTP 映射的首批 JUnit 测试；其他模块自动化覆盖仍少。框架、配置、SQL、接口或跨模块改动还需使用 `.github/modernize/java-upgrade/20260812043920/smoke/README.md` 的环境做针对性冒烟。
- 修改异常处理、认证、权限或响应包装时，运行 `scripts/verify-error-contract.ps1`，并增加 `-BaseUrl http://127.0.0.1:5173/api` 验证前端代理；错误体契约为 `code`、`msg`、`traceId`。
- 修改登录、角色、菜单权限、Token 生命周期或账号状态时，在本地隔离库运行 `scripts/verify-auth-permission.ps1`，并对 Vite `/api` 再运行一次；脚本会创建受限用户和角色并在 `finally` 中精确清理，禁止指向真实业务库。
- 修改菜单、租户模块或业务角色基线时，运行 `scripts/verify-menu-baseline.ps1`，并对 Vite `/api` 再运行一次；默认租户为上海凯奔航空技术有限公司。
- 修改自动化设备、线束检测机（遗留路由名为 `tightening`）或磁粉机任务时，运行 `scripts/verify-machine-task.ps1`；涉及设备上报、重复下发或状态流转时，仅在本地隔离环境运行 `scripts/verify-machine-task-flow.ps1`。
- 修改合同新增、修改、附件或任务生成时，运行 `scripts/verify-contract.ps1`；涉及写入和状态流转时，仅在本地隔离环境运行 `scripts/verify-contract-flow.ps1`，并对 Vite `/api` 再运行一次。
- 修改工具、设备、计量/维保记录或其附件时，运行 `scripts/verify-equipment.ps1`；涉及写入时，仅在本地隔离环境运行 `scripts/verify-equipment-flow.ps1`，并对 Vite `/api` 再运行一次。
- 修改盘点任务、盘点单或差异库存处理时，仅在本地隔离环境运行 `scripts/verify-stocktake-flow.ps1`，并对 Vite `/api` 再运行一次；批次/序列号盘点尚无明细级调整契约，不能移除现有安全阻断后只验证总库存。
- 修改库存调整单、调整原因或调整审批时，仅在本地隔离环境运行 `scripts/verify-stock-adjust-flow.ps1`，并对 Vite `/api` 再运行一次；该脚本覆盖普通库存、输入/引用守卫、重复审批和追溯型航材安全阻断。
- 修改仓库调拨单、调拨审批、收货或调拨库存动作时，仅在本地隔离环境运行 `scripts/verify-stock-transfer-flow.ps1`，并对 Vite `/api` 再运行一次；该脚本覆盖两阶段库存、负数/引用守卫、部分收货、重复并发收货和追溯型航材安全阻断。
- 修改员工、证书、培训记录、培训课程、培训实施、授权项目或人员授权时，仅在本地隔离环境运行 `scripts/verify-hr-flow.ps1`，并对 Vite `/api` 再运行一次；该脚本覆盖七项 HR 读写、401/403/409 契约、培训完成事务、导出和附件安全边界，并精确清理夹具。
- 涉及物料出库审批、库存、批次、序列号或单据状态流转时，在本地隔离冒烟库运行 `powershell -ExecutionPolicy Bypass -File .\scripts\verify-material-concurrency.ps1 -Iterations 5`；该脚本会直接造数，禁止指向真实业务库。
- Windows 上重新打包 `xingyun-api` 前先停止正在运行的 JVM，否则胖 jar 可能因文件锁没有更新。

## 升级策略

- 先稳定 3.5.x，再处理兼容层，最后才评估下一代大版本；未经明确任务不得直接迁移 Spring Boot 4。
- 每次升级只处理一个依赖族或一个兼容主题，并在 `UPGRADE.md` 记录版本、验证证据和回滚点。

# 后端升级运行手册

## 已建立的基线

| 项目 | 当前值 | 说明 |
| --- | --- | --- |
| Java | 25 LTS | 编译、VS Code 和运行镜像必须一致 |
| Spring Boot | 3.5.0 | 当前迁移落点；后续先做 3.5.x 补丁升级 |
| jugg | 5.0.1 | 已迁移到 Jakarta，并保留少量兼容层 |
| Maven | 3.9.x | 本机已验证 3.9.12 |
| 运行镜像 | Eclipse Temurin 25 JRE | 三个源码 Dockerfile 使用同一主版本 |

2026-08-12 已用 Java 25 编译通过主 reactor 的 10 个项目，并完成登录、租户、查询、打印、导出和前端代理冒烟。完整证据位于 `.github/modernize/java-upgrade/20260812043920/`。

同日已用 `xingyun-api/target/xingyun-api.jar` 实际构建本地镜像 `kberp-api:codex-baseline-20260812`；镜像内 `/opt/app.jar` 存在，Temurin 25.0.3 与 `java -server` 启动参数验证通过。当前基础镜像摘要为 `sha256:35f47084a4c1e34636fc8842780d5ca1e85b1b74de139723d1a541137932ddf2`（移动标签后续可能更新）。

同日补齐发料出库航材搜索 `GET /material/out/sheet/product/search` 与合同任务修改 `PUT /shkb/contract-task`，并扩充合同任务详情的机型/件号 ID。修改后已再次通过 Java 25 主 reactor 10 模块编译。

2026-08-13 航材搜索已改为单次批量库存查询，并以新打包的胖 jar 完成后端直连及 Vite `/api` 代理冒烟。

同日确认原仓库及 Git 历史均没有 SHKB 业务建表 SQL：相关 Java 代码提交后，数据库结构未被版本化。新增 `V1.13__shkb_dashboard_core.sql`，以增量方式补齐看板已明确依赖的机型、合同、合同任务、工具、设备表及商品 `part_number_id`、`machine_type_id`。在隔离冒烟库应用后，三个维修类型、库存、工具设备共五个看板接口均返回 200。可在后端运行 `powershell -ExecutionPolicy Bypass -File .\scripts\verify-dashboard.ps1` 重复验证；该迁移是看板核心基线，不代表其余 SHKB 子模块的 schema 已全部补齐。

随后新增 `V1.14__shkb_contract_core.sql`，补齐维修类型、合同维修类型关联与合同附件表，使合同列表、详情、录入关联和附件列表具备可部署的 schema 基线。可运行 `powershell -ExecutionPolicy Bypass -File .\scripts\verify-contract.ps1` 验证合同查询、维修类型查询和附件列表。

继续新增 `V1.15__shkb_equipment_records.sql`，在 `V1.13` 工具/设备主表基础上补齐工具附件、工具计量记录及其附件、设备附件和设备维保记录。可运行 `powershell -ExecutionPolicy Bypass -File .\scripts\verify-equipment.ps1` 验证主列表、附件列表和从属记录分页接口。

本轮新增 `V1.16__shkb_work_card_core.sql`，补齐维修工卡、工卡附件、必换件、任务工卡、任务必换件数量快照和领料审批守卫共 6 张表。同时修复件号筛选非法 SQL、工卡件号与必换商品混用、默认数量漏传，以及嵌套数量未级联校验的问题。`scripts/verify-work-card.ps1` 提供只读空数据冒烟；仅在本地隔离环境运行 `scripts/verify-work-card-flow.ps1`，可重复验证工卡新增、件号筛选、必换件数量正/负校验、附件元数据、任务关联、默认数量与任务快照两条分支，并在 `finally` 中清理精确测试数据。

随后新增 `V1.17__shkb_material_flow.sql`，以幂等增量方式补齐非必换件、发料单、出库单、批次/序列号库存共 9 张表，并为库存日志补充 `stock_batch_id`。同时对齐发料单/出库单 Mapper、合同号与日期查询参数、JSON 导出、供应商/领料人持久化、出库数量正数校验及任务完料状态。`scripts/verify-material-flow.ps1` 是可对后端直连或 Vite `/api` 代理执行的只读探针；`scripts/verify-material-flow-write.ps1` 仅允许本机隔离环境运行，会重复验证“申请→审批→发料单→出库审批→库存 10 扣至 8→任务完料”以及零数量、重复制单守卫，并在 `finally` 中清理测试数据。该迁移已在隔离库连续应用两次，第二次无错误。

随后完成 V1.18 物料事务与并发加固（本阶段没有新增 DDL）：出库审批、修改、可领料和删除统一使用单据行锁；同一发料单以主单锁串行累计进度，并按主键固定顺序锁定所引用明细；发料明细、批次库存和序列号状态均使用条件更新阻止重复扣减或超卖；序列号增加商品、批次和仓库归属校验。真实并发基线曾稳定复现两张出库单均成功但主单 `total_out_num` 仅增加一次，修复后运行 `scripts/verify-material-concurrency.ps1 -Iterations 5` 已覆盖主单累计、重复审批、库存失败全回滚、同批次超卖、同序列号竞争、跨仓序列号、审批/可领料竞争及审批/删除竞争；脚本仅允许本机隔离环境运行，并在 `finally` 中精确清理测试数据。

随后完成 V1.19 全局错误契约治理（本阶段没有新增 DDL）：不再把所有异常强制为 HTTP 500，参数校验使用 400、认证使用 401、权限使用 403、可恢复业务拒绝使用 409，未知系统异常仍为 500；响应体继续保留既有 `code`、`msg`、`traceId`，避免破坏已有调用方。修正 `Throwable` 和校验器配置错误被误报为用户输入错误的问题，并在 `xingyun-core` 建立首批 4 个 JUnit 用例。可对后端直连和 Vite 代理分别运行 `scripts/verify-error-contract.ps1`，只读验证 200/400/401/409/405 及错误追踪字段。

随后完成 V1.20 单租户认证与权限可靠性加固（本阶段没有新增 DDL）：按产品后续仅单租户的定位，不建设跨租户矩阵，也不移除 jugg 依赖的租户上下文。新增 `scripts/verify-auth-permission.ps1`，在本地隔离库临时创建仅有仓库查询权限的用户和角色，验证管理员访问、最小权限菜单与查询、用户管理 403、伪造及注销 Token 401、锁定/停用账号拒绝登录，并在 `finally` 中清理账号、角色、关系和操作日志。真实基线发现锁定账号被重复的 `isAccountNonLocked` 分支误报为“账户已过期”，现已修为“账户已锁定”，并在 `xingyun-sys` 新增 4 个登录资格 JUnit 用例。

## 每次改动的固定流程

1. 执行 `git status --short`，确认并保护现有改动。
2. 执行 `powershell -ExecutionPolicy Bypass -File .\scripts\verify.ps1`，记录改动前基线。
3. 一次只升级一个依赖族；不要同时升级 Java、Spring Boot、jugg 和数据库驱动。
4. 再次运行验证脚本。涉及运行时行为时启动 smoke 环境并验证受影响接口。
   看板相关改动同时运行 `scripts/verify-dashboard.ps1`。
   合同相关改动同时运行 `scripts/verify-contract.ps1`。
   工具/设备相关改动同时运行 `scripts/verify-equipment.ps1`。
   工卡相关改动同时运行 `scripts/verify-work-card.ps1`；本地隔离库增加 `scripts/verify-work-card-flow.ps1`。
   物料相关改动同时运行 `scripts/verify-material-flow.ps1`；本地隔离库增加 `scripts/verify-material-flow-write.ps1`。涉及审批、库存、批次、序列号或状态流转时，再运行 `scripts/verify-material-concurrency.ps1 -Iterations 5`。
   修改全局异常、认证、权限或响应包装时，同时对 8088 直连和 5173 `/api` 代理运行 `scripts/verify-error-contract.ps1`。
   修改登录、角色、菜单权限、Token 或账号状态时，在本地隔离库同时对 8088 和 5173 `/api` 运行 `scripts/verify-auth-permission.ps1`。
5. 发布候选版本先停止运行中的 JVM，再用 `scripts/verify.ps1 -Full` 执行测试与打包。
6. 记录旧版本、新版本、失败现象、修复方式、验证结果和可回滚提交。

## 推荐演进顺序

1. 在现有 Spring Boot 3.5 分支内做补丁升级，并回归登录、租户、打印、导出和数据库迁移。
2. 为核心业务补最小自动化测试，优先覆盖金额/库存、单租户权限、Token 生命周期和迁移脚本。
3. 分专题移除 Swagger 2 注解兼容层、验证 jugg inner 双栈、补 RabbitMQ/定时任务/WebSocket 测试。
4. 将两个 cloud 模块单独编译和运行验证后，才决定是否纳入主 reactor。
5. 只有在上述门禁稳定后，另开任务评估 Spring Boot 下一大版本。

## 已知非绿色区域

- 导出兼容层默认上限为 10000。
- Swagger 2 注解仍由 `swagger-annotations 1.6.14` 提供编译兼容。
- jugg 独有 inner Bean 与项目实现双栈共存，深度一致性尚未验证。
- RabbitMQ、定时任务、WebSocket 和 cloud 模块尚无完整端到端覆盖。
- Lombok 在 Java 25 下仍可能输出 `sun.misc.Unsafe` 警告。
- 原项目未版本化完整 SHKB 业务 schema；目前 `V1.13` 覆盖看板核心、`V1.14` 覆盖合同核心、`V1.15` 覆盖工具/设备从属记录、`V1.16` 覆盖维修工卡及任务关联核心、`V1.17` 覆盖领料申请后的发料与出库库存闭环。其余 SHKB 功能仍需按接口逐步补录增量迁移与回归样例。

## 数据安全

`migration/tenant/V1.0__init.sql` 是全量 DROP/CREATE 脚本，只能用于新建空库。已有数据库只允许按环境确认后的增量迁移，执行前必须备份并核对目标 schema。

当前 `xingyun-api/pom.xml` 明确从运行 jar 排除 `db/**`，工程也没有 Flyway 运行时依赖。因此 `db/migration` 是纳入版本控制的部署前迁移源文件，并不会因重新打包或重启应用而自动执行。发布时必须先备份目标库，按版本顺序单独应用尚未执行的增量 SQL，再运行对应 `verify-*.ps1` 接口冒烟；禁止把“应用已启动”当作“迁移已完成”。

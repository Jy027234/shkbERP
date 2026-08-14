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

随后完成 V1.21 发布与恢复门禁：生产凭据全部改为环境变量，MySQL 定时备份改为默认关闭；运行镜像安装 MariaDB 10.11 客户端并验证提供兼容的 `mysqldump`。备份任务移除仅 MySQL 8 客户端支持的 `--column-statistics` 参数，增加单事务导出、可配置执行文件和 JDBC URL 单元测试。`scripts/verify-backup-restore.ps1` 只允许对本地 `xingyun-smoke-mysql` 执行，会生成 gzip 备份、恢复至随机临时库、比较两次逻辑导出的 SHA-256，并在 `finally` 中删除临时库和容器内文件。完成该演练并为 `/opt/data/backup/mysql` 配置容器外持久化之前，不得开启生产备份开关。

随后完成 V1.22 运行健康门禁：引入 Spring Boot Actuator，但只通过 HTTP 暴露无详情的 `health` 端点；启用 `/actuator/health/liveness`、`/actuator/health/readiness` 以及主端口上的 `/livez`、`/readyz`。Liveness 不依赖外部系统，避免共享基础设施故障引发重启风暴；Readiness 纳入数据库与 Redis，核心依赖不可用时停止接收流量。应用启用 30 秒优雅停机，运行镜像修正为暴露真实的 8088 端口，并使用 `/livez` 作为 Docker `HEALTHCHECK`。`scripts/verify-health.ps1` 会无认证验证五个端点均为 UP，且响应不泄露组件和环境细节。

随后完成 V1.23 核心物料写操作回归基线（本阶段没有新增 DDL）：将发料出库单的修改、审批、可领料和删除状态守卫，以及发料明细归属、同明细多行数量汇总、剩余数量和主单累计进度规则集中到无数据库依赖的规则类。`xingyun-shkb` 新增首批 10 个 JUnit 用例，覆盖合法状态、重复审批提示、未知状态、跨发料单或跨商品明细、零数量、同明细合计超发、部分出库、完成出库和累计超发；这些测试会随 Maven `verify` 在 CI 中执行。真实事务、库存扣减、回滚和并发仍由本地隔离库的 `verify-material-flow-write.ps1` 与 `verify-material-concurrency.ps1` 覆盖，二者职责互补。

随后完成 V1.24 RabbitMQ 消费失败恢复基线（本阶段没有新增 DDL）：Direct Listener 从异常后默认无限重入队改为进程内最多 3 次、1 秒起始且 2 倍退避的有限重试；耗尽后使用发布确认把原消息、原交换机/路由及异常诊断转存至持久化 `shkb.failed` 队列。RabbitTemplate 同时启用不可路由返回和连接瞬时故障有限重试，RabbitMQ 纳入 readiness，连接不可用时实例停止接收新流量。`xingyun-api` 新增 3 个配置/拓扑 JUnit 用例；`scripts/verify-rabbitmq-recovery.ps1` 仅允许本机隔离环境，会向独立图表队列注入因空金额必然回滚的订单事件并验证退避、失败转存和诊断头后清理消息。失败队列只允许在根因修复且确认幂等后人工重放。该门禁解决毒消息循环与静默丢弃风险，但数据库事务提交后到消息发布之间的极小丢失窗口仍需后续 Outbox 专题处理。

随后完成 V1.25 jugg RabbitMQ Listener 去重（本阶段没有新增 DDL）：确认 `rabbitmq-starter 5.0.1` 自动配置直接导入的系统通知、邮件、站内信 Listener 与项目实现使用相同队列但不同 DTO/服务类型，启动后会形成竞争消费者。扩展既有 `JuggInnerBeanConflictResolver`，仅在项目侧存在同名替代 Bean 时移除 jugg 的重复 Listener；jugg 独有的导出任务 Listener 与既有 inner 服务依赖闭包继续保留。`xingyun-api` 新增 3 个注册表单元测试，覆盖三个项目替代 Listener、jugg 独有 Listener 和必须保留的 inner 服务。此项消除了三个消息队列的双栈竞争，但 jugg 其余 inner 控制器/服务的深度一致性仍需按专题验证。

随后完成 V1.26 核心事务消息 Outbox：新增部署迁移 `tenant/V1.18__mq_outbox.sql`，为单租户业务库建立 `sys_mq_outbox` 与 `sys_mq_inbox`。入库、出库和订单审批事件改为业务事务提交前写 Outbox；后台中继按租约领取，等待 RabbitMQ `CORRELATED` publisher confirm 与不可路由返回后才标记成功，失败按 5 秒起始的指数退避最多尝试 10 次。盘点统计和订单图表消费者在同一业务事务内写 Inbox 去重，因此即使发生“Broker 已确认、SENT 尚未落库”宕机窗口，重复投递也不会重复累计。产品仍按单租户部署；中继枚举现有可用租户仅用于兼容动态数据源，不扩展跨租户能力或测试矩阵。

部署顺序必须是：先备份并对业务库应用 `V1.18__mq_outbox.sql`，确认两张表存在，再发布新 jar；反向发布会使核心库存/审批事务因缺表回滚。`scripts/verify-outbox.ps1` 只允许本地隔离环境，已验证首次确认投递、强制重复中继、Inbox 只保留 1 条且图表业务记录只产生 1 条。Outbox 状态为 `0=待发送、1=发送中、2=已确认、3=永久失败`；状态 3 必须先修复根因并核对消费者幂等，才可人工重置为待发送。已确认记录默认保留 7 天，可通过 `app.rabbitmq.outbox.*` 调整批量、租约、重试、确认超时和保留期。

随后完成治理里程碑 V1.27（里程碑编号，不是数据库迁移号）：将 2026-08-14 对云端生产配置的只读核对结果合入唯一单体仓库，新增数据库迁移 `tenant/V1.21__shkb_menu_permission_baseline.sql`。该迁移补齐 67 条上海凯奔业务菜单、覆盖 21 条通用菜单的航材化配置，保留 10 个业务角色及 592 条有效角色—菜单关系，并将单租户名称固定为“上海凯奔航空技术有限公司”；不包含用户、用户角色、凭据或业务数据。`scripts/verify-menu-baseline.ps1` 验证登录后 14 个可见业务根菜单，并排除零售、开发、物流三个非产品模块。合入后已在随机临时克隆库连续两次执行 `V1.18__mq_outbox.sql` 与 `V1.21__shkb_menu_permission_baseline.sql`，两次均稳定得到 350 个菜单、11 个角色、592 条有效角色菜单关系、12 个租户模块和 2 张 Outbox/Inbox 表；临时库已删除。菜单探针已通过 8088 直连和单体前端 5174 `/api` 代理，Java 25 十模块编译、前端 6 个测试文件/27 个用例、类型检查和 9117 模块生产构建均通过。该证据仍不等于生产恢复副本验收，不能据此解除生产部署锁。

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
   修改菜单、租户模块或业务角色基线时，同时对 8088 和 5173 `/api` 运行 `scripts/verify-menu-baseline.ps1`。
5. 发布候选版本先停止运行中的 JVM，再用 `scripts/verify.ps1 -Full` 执行测试与打包。
6. 记录旧版本、新版本、失败现象、修复方式、验证结果和可回滚提交。

## 推荐演进顺序

1. 在现有 Spring Boot 3.5 分支内做补丁升级，并回归登录、租户、打印、导出和数据库迁移。
2. 为核心业务补最小自动化测试，优先覆盖金额/库存、单租户权限、Token 生命周期和迁移脚本。
3. 分专题移除 Swagger 2 注解兼容层、验证 jugg inner 双栈，并继续补 RabbitMQ Outbox、定时任务与 WebSocket 测试。
4. 将两个 cloud 模块单独编译和运行验证后，才决定是否纳入主 reactor。
5. 只有在上述门禁稳定后，另开任务评估 Spring Boot 下一大版本。

## 已知非绿色区域

- 导出兼容层默认上限为 10000。
- Swagger 2 注解仍由 `swagger-annotations 1.6.14` 提供编译兼容。
- jugg RabbitMQ 三个重复 Listener 已移除；其余独有 inner Bean 与项目实现仍双栈共存，深度一致性尚未验证。
- RabbitMQ 核心库存/审批事件已覆盖事务 Outbox、发布确认、消费失败重试、失败队列及非幂等消费者去重；其他直接消息生产点、定时任务、WebSocket 和 cloud 模块尚无完整端到端覆盖。
- Lombok 在 Java 25 下仍可能输出 `sun.misc.Unsafe` 警告。
- 原项目未版本化完整 SHKB 业务 schema；目前 `V1.13` 覆盖看板核心、`V1.14` 覆盖合同核心、`V1.15` 覆盖工具/设备从属记录、`V1.16` 覆盖维修工卡及任务关联核心、`V1.17` 覆盖领料申请后的发料与出库库存闭环。其余 SHKB 功能仍需按接口逐步补录增量迁移与回归样例。

## 数据安全

`migration/tenant/V1.0__init.sql` 是全量 DROP/CREATE 脚本，只能用于新建空库。已有数据库只允许按环境确认后的增量迁移，执行前必须备份并核对目标 schema。

当前 `xingyun-api/pom.xml` 明确从运行 jar 排除 `db/**`，工程也没有 Flyway 运行时依赖。因此 `db/migration` 是纳入版本控制的部署前迁移源文件，并不会因重新打包或重启应用而自动执行。发布时必须先备份目标库，按版本顺序单独应用尚未执行的增量 SQL，再运行对应 `verify-*.ps1` 接口冒烟；禁止把“应用已启动”当作“迁移已完成”。

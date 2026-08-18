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

随后完成治理里程碑 V1.27（里程碑编号，不是数据库迁移号）：将 2026-08-14 对云端生产配置的只读核对结果合入唯一单体仓库，新增数据库迁移 `tenant/V1.21__shkb_menu_permission_baseline.sql`。该迁移补齐 67 条上海凯奔业务菜单、覆盖 21 条通用菜单的航材化配置，保留 10 个业务角色及 592 条有效角色—菜单关系，并将单租户名称固定为“上海凯奔航空技术有限公司”；不包含用户、用户角色、凭据或业务数据。`scripts/verify-menu-baseline.ps1` 验证本地候选的 14 个预期业务根菜单，并排除零售、开发、物流三个非产品模块；该本地探针不是现网产品范围的最终依据。合入后已在随机临时克隆库连续两次执行 `V1.18__mq_outbox.sql` 与 `V1.21__shkb_menu_permission_baseline.sql`，两次均稳定得到 350 个菜单、11 个角色、592 条有效角色菜单关系、12 个租户模块和 2 张 Outbox/Inbox 表；临时库已删除。菜单探针已通过 8088 直连和单体前端 5174 `/api` 代理，Java 25 十模块编译、前端 6 个测试文件/27 个用例、类型检查和 9117 模块生产构建均通过。该证据仍不等于生产恢复副本验收，不能据此解除生产部署锁。

随后完成治理里程碑 V1.28（里程碑编号，不是数据库迁移号）：新增 `tenant/V1.22__shkb_machine_task_core.sql`，补齐自动化设备、线束检测机任务（遗留内部命名为 `tightening`）和磁粉机任务三张此前只有 Java/前端代码、没有版本化 DDL 的业务表，并为平台任务 ID 建立唯一约束。线束检测机上报现在只允许待处理任务完成一次：相同上报内容的设备重试按幂等成功处理，不同内容不得覆盖已完成记录；磁粉机下发在数据库事务内锁定任务行，拒绝已下发任务的重复操作，并保留“远端成功、数据库提交失败”这一跨系统极小窗口作为后续设备协议幂等专题。新增 5 个状态规则单元测试、`scripts/verify-machine-task.ps1` 管理端只读探针和仅限本机隔离库的 `scripts/verify-machine-task-flow.ps1` 写流程探针。V1.22 已在随机临时 MySQL 8 库连续执行两次，稳定得到 3 张表与 3 个关键唯一约束；新 Java 25 镜像已在本机健康启动，管理端探针通过 8088 直连和 5173 `/api` 代理，隔离写流程通过相同上报重试、冲突上报拒绝和重复下发拒绝，测试数据清理后为 0。不得据此推断云端存量表不存在重复任务，正式迁移前仍须在生产恢复副本核对唯一约束。

随后完成治理里程碑 V1.29（里程碑编号，不是数据库迁移号）：按真实业务范围将未纳入流程的“成品出入库”标记为冻结/非发布模块，不再为代码半成品自动补 schema。合同主流程新增 `tenant/V1.23__shkb_contract_task_flow.sql`，补齐任务创建必写但此前完全缺失的 `shkb_contract_task_repair_status_record`，并以 `contract_id` 唯一约束落实“一份合同一个任务”；若生产恢复副本存在重复任务，迁移会明确失败而不会自动删改业务数据。附件上传现在先校验所属合同，前端移除了没有后端接口、也没有“关闭前状态”数据支撑的恢复按钮，并将合同 API 类型与 Java 契约对齐。`scripts/verify-contract-flow.ps1` 仅允许本机隔离库，覆盖合同新增、修改、附件上传/列表/删除、无主附件拒绝、任务初始状态与重复生成 409，并精确清理数据库记录和本地测试文件。迁移连续执行两次无错误，Java 25 完整测试/打包、前端类型检查、27 个用例与 9117 模块生产构建通过；新镜像通过健康检查，写流程在 8088 直连和 5173 `/api` 代理均通过。合同恢复仍需业务明确恢复目标和审计要求后单独设计，不能推测为某个状态。

随后完成治理里程碑 V1.30（里程碑编号，不是数据库迁移号）：工具、计量记录、设备、维保记录及三类附件上传统一增加父记录存在性校验，并禁止修改记录时跨工具或跨设备转移归属，避免在无外键的既有表结构下形成孤儿数据或让主档汇总状态失真。设备维保间隔的实际前后端计算单位一直是“天”，本轮修正维保记录弹窗及 Java 契约中的误写“月”；已部署 `V1.13` 的历史列注释不回写，避免篡改既有迁移校验和。新增仅限本机隔离库的 `scripts/verify-equipment-flow.ps1`，覆盖无主记录/附件 409、工具及初始计量记录创建、最新计量证书和日期同步、归属变更拒绝、设备及维保记录写入、三类附件上传/列表/删除，以及数据库和本地测试文件精确清理。Java 25 十模块完整测试/打包、前端类型检查、27 个用例与 9117 模块生产构建通过；新镜像 `kberp-api:local-equipment-flow-20260815` 健康运行，读写探针在 8088 直连和 5173 `/api` 代理均通过，测试父记录和从属记录残留均为 0。该证据不包含附件物理文件通用生命周期、浏览器人工验收或生产恢复副本验收。

随后完成治理里程碑 V1.31（里程碑编号，不是数据库迁移号）：针对采购订单→收货→采购退货主链路，新增部署迁移 `tenant/V1.24__purchase_receive_traceability.sql`，以逐列幂等方式补齐 Java 实体和 Mapper 已依赖、但历史 schema 遗漏的收货明细批次号、序列号列表、生产日期和失效日期；迁移在本地已连续执行两次并稳定保留 4 列。收货和退货创建现在同时校验来源明细存在、属于所选主单且商品一致，底层累计服务也把缺失明细从空指针 500 改为明确业务拒绝。采购退货审批在同一事务中同步扣减总库存与来源批次库存，条件更新阻止批次并发超扣。新增仅限本机隔离库的 `scripts/verify-purchase-flow.ps1`，覆盖采购单审批、错误/跨单/跨商品明细拒绝、超量收货和退货拒绝、追溯字段往返、收货入库与退货扣库，并精确清理业务夹具、日志及 Outbox/Inbox；8088 直连和 5173 `/api` 代理均得到库存 `0→6→4`。Java 25 十模块编译、健康/错误契约/权限回归、前端类型检查、27 个测试与 9117 模块生产构建均通过；本地镜像 `kberp-api:local-purchase-flow-v2-20260815` 健康运行。盘点以及序列号商品的部分退货选择仍是独立后续专题；销售不属于上海凯奔产品范围，本证据不解除生产部署锁。

随后完成治理里程碑 V1.32（里程碑编号，不是数据库迁移号）：新增 `tenant/V1.25__purchase_return_serial_traceability.sql`，以幂等增量列保存采购退货明细的序列号快照。关联收货单的序列号商品现在只能从该收货明细仍在库的序列号中多选，草稿创建和审核均校验数量一致、无重复、商品/仓库/批次/来源归属正确；审核使用 `stock_status=1` 条件更新将具体序列号原子置为出库，任一序列号被并发占用时，单据状态、总库存与批次库存全部回滚。`scripts/verify-purchase-flow.ps1` 已扩展为 6 个序列号收货、指定 2 个部分退货，并覆盖数量不符、重复、非来源序列号、已出库重用、审核前状态竞争和事务回滚；8088 直连及 5173 `/api` 代理均通过，夹具清理后无残留。迁移在本地连续执行两次成功；本地运行镜像为 `kberp-api:local-purchase-serial-v2-20260815`。盘点、浏览器人工业务验收和生产恢复副本仍是独立后续项；销售退货不属于上海凯奔产品范围，本证据不解除生产部署锁，云服务器未作任何修改。

随后完成治理里程碑 V1.33（里程碑编号，不是数据库迁移号）：盘点任务的修改、生成差异、处理差异、取消和删除，以及盘点单的新增、修改、审批、拒绝、取消和删除，统一在事务内锁定盘点任务，避免盘点快照与单据状态并发漂移；任务创建后禁止换仓，录入时校验重复航材、负数、航材存在性/类型和非单品任务归属，单品盘点草稿删除的航材同步清理无引用任务明细。差异处理现在拒绝缺项、重复项和不存在航材，并在库存动作前完成完整校验。普通航材的总库存差异仍按既有流程原子调整；批次或序列号航材只要存在非零差异就明确拒绝，因为当前前后端没有批次/序列号盘点明细契约，禁止只改总库存造成追溯账不平。新增仅限本机隔离库的 `scripts/verify-stocktake-flow.ps1`，在 8088 直连与 5173 `/api` 代理均通过仓库不可变、输入守卫、单品残留清理、不完整差异回滚、普通库存 `5→3` 和批次库存拒绝后总量/批次量均保持 `4`；两轮夹具清理后残留为 0。本地运行镜像为 `kberp-api:local-stocktake-v1-20260815`。该证据是安全性与普通盘点闭环，不代表批次/序列号盘点功能已经实现，也不解除生产部署锁；云服务器未作任何修改。

随后完成治理里程碑 V1.34（里程碑编号，不是数据库迁移号）：库存调整单的修改、删除、审核通过和审核拒绝统一使用单据行锁，避免草稿明细更新与审批库存动作并发交错。“新增并直接审核”接口补上此前遗漏的正数校验，服务层同时集中校验仓库、启用中的调整原因、重复航材、航材存在性/类型和调整入库采购价，审批时会重新验证持久化明细，不能依赖前端或历史草稿可信。当前库存调整单没有批次/序列号明细字段，因此追溯型航材统一明确拒绝，禁止只改总库存。新增仅限本机隔离库的 `scripts/verify-stock-adjust-flow.ps1`，在 8088 直连和 5173 `/api` 代理均通过负数直接审核、重复/缺失引用拒绝、批次库存防失真、普通库存 `5→7→4` 和两次并发审核仅一次生效；夹具、日志与 Outbox 残留均为 0。本地运行镜像为 `kberp-api:local-stock-adjust-v1-20260815`。该证据不代表批次/序列号库存调整功能已经实现，也不解除生产部署锁；云服务器未作任何修改。

随后完成治理里程碑 V1.35（里程碑编号，不是数据库迁移号）：仓库调拨单的修改、删除、审核、拒绝和收货统一使用主单行锁，避免草稿、审批和重复收货并发交错；直接审核补齐正数校验，收货同时在服务层和条件 SQL 层拒绝零数/负数。创建、审核和收货都会重新核验转出/转入仓库、重复航材、航材存在性与类型、调拨数量、累计收货数量和调拨价格，未知收货航材不再触发空指针 500。普通航材继续保持“审核扣转出仓、收货加转入仓”的既有两阶段语义；当前契约没有批次/序列号调拨明细，因此追溯型航材明确阻断，禁止只搬总库存。新增仅限本机隔离库的 `scripts/verify-stock-transfer-flow.ps1`，在 8088 直连和 5173 `/api` 代理均通过输入/引用守卫、追溯型库存防失真、普通库存转出 `10→6`、部分及最终收货使目标库存 `1→5`，并验证两个并发最终收货只有一个成功；夹具、日志与 Outbox/Inbox 残留均为 0。本地运行镜像为 `kberp-api:local-stock-transfer-v1-20260815`。本阶段没有新增 DDL，不代表批次/序列号库存调拨已经实现，也不解除生产部署锁；云服务器未作任何修改。

## V1.36 安全发布候选与迁移治理

2026-08-15 完成迁移与候选发布治理，未修改云服务器。新增 migration-catalog.json，为版本化 SQL 固定 SHA-256、风险标记和执行分类：空库初始化、仅历史基线、已批准存量库增量。任何漏登记、哈希漂移、把 DROP/DELETE 类历史脚本混入存量库候选计划，或未标记受控数据修改的 SQL 都会被 verify-migration-catalog.ps1 阻断。现行候选计划以平台 `V1.7__jugg5_tenant_compatibility.sql` 开始，随后是租户 V1.13 至 V1.18 与 V1.21 至 V1.25，按清单顺序单独执行；V1.21 的菜单/模块数据影响仍需业务确认。

新增 verify-release-preflight.ps1 和 verify-release-restore.ps1，均硬限制在本地 xingyun-smoke-mysql。预检核对基础 schema、租户 1000、历史 Jugg 租户列、API 验收所需的 `admin` 列，以及 V1.22 设备任务唯一键与 V1.23 合同任务唯一键的重复数据；迁移后再核对 V1.7 补齐的 `server_name`、`is_platform`。恢复演练从本地业务库导出、恢复随机克隆库、比较逻辑导出 SHA-256，连续两轮应用 12 个候选 SQL 并校验 9 个关键输出，最后删除克隆库和容器临时文件。已通过的本地演练只证明候选过程可重复；不等于生产恢复副本验收。

根目录 new-release-candidate.ps1 在 main 的干净工作树运行完整前后端门禁、上述恢复演练和本地镜像构建，并创建包含源提交、jar、前端产物树、迁移计划、镜像和恢复证据哈希的注释候选标签。候选仍使生产 deploymentAllowed 保持 false。解除生产锁仍需要授权的生产备份恢复到隔离副本、schema/重复数据对账、V1.21 业务确认、核心业务验收、回退方案和明确发布批准。

同日已创建并推送首个候选 `v2026.08.15-rc.1`，指向 `edfd802562689a5b9521d53fd9533d370feeb6d0`。候选清单固定了后端 jar、前端文件树、迁移目录/计划、本地镜像和恢复演练证据的 SHA-256；该标签仅证明本地隔离候选可追溯，仍不构成生产恢复副本验收或生产授权。

2026-08-16 已在不修改云端的前提下，对授权导出的 `shkb_platform` 逻辑备份副本完成本地恢复验收。新增 `scripts/verify-production-backup-copy.ps1`：该脚本只接受本机 `.sql.gz` 与独立记录的 SHA-256，硬限制目标为 `xingyun-smoke-mysql`，导入随机受限源库、执行预检与二次恢复演练后精确清理本地数据库和容器临时文件，不包含 SSH、云端地址或生产凭据。首次验收通过 15 项预检、11 项候选迁移连续两轮和 7 项迁移后检查；补入非破坏性的 V1.7 Jugg 5 兼容桥后，最新重新验收通过 16 项预检、12 项候选迁移连续两轮和 9 项迁移后检查，恢复前后逻辑导出 SHA-256 一致。V1.21 在该副本上会更新租户名称，但三个目标模块关系均无需删除。该技术证据仍不替代菜单/角色业务确认、核心业务验收、回退方案或明确生产变更窗口批准，`deploymentAllowed` 保持 false。

同日开始恢复副本 API 验收时，候选镜像在本机默认 Jugg 密钥下无法解密历史租户的动态 JDBC 密码，并以 `BadPaddingException` 在启动阶段拒绝运行；同一候选镜像连接常规本机冒烟库可正常达到 readiness，说明问题是历史加密数据与密钥连续性，而不是 Java 25 镜像本身。新增 `scripts/verify-production-backup-api.ps1`：它只运行本地隔离 MySQL 和候选 Docker 镜像，要求由受控的当前进程环境注入历史 `JUGG_SECRET_KEY`，从不接受命令行密钥或持久化该值/指纹。脚本会先在内存验证历史密文可被解密，再只在生成的恢复库中将已启用租户的数据源 URL、用户名和本地 JDBC 密码重绑定；密码以同一历史密钥重加密，使 Jugg 的启动期租户数据源连接校验仍实际生效，候选绝不会连接云端。它还仅把本地冒烟夹具的 `admin` 密码哈希复制到生成副本中原有的启用且未锁定 admin，保留其 ID、状态和角色/菜单关系；不读取生产密码、不创建账号。脚本禁用 Outbox/Quartz/RabbitMQ 消费后台写入，并执行健康、菜单、看板、合同、工具设备、工卡、航材查询和设备任务的 API 探针。成功或失败后均精确删除生成的容器、数据库和容器文件。

2026-08-16 已通过受控会话完成授权生产备份副本的本地核心 API 验收：历史密文内存验证、候选镜像 readiness 和上述八组只读探针均通过，生成的本地资源已删除。该结果不包含业务负责人对 V1.21 菜单/角色影响、合同/采购/库存人工流程、数据库回退演练或生产变更窗口批准；`deploymentAllowed` 因此继续保持 false。

同日修复 Windows 中文工作目录下的门禁调度：`verify-all.ps1` 在当前 PowerShell 宿主内顺序执行治理、后端和前端脚本，避免 PowerShell 7 通过旧 `powershell.exe` 二次传参时发生路径转码；生产备份副本脚本仍保留子进程隔离，但以当前宿主通过 `Start-Process` 启动子脚本并精确清理临时日志。这样既保留失败退出与清理边界，也使 Codex 中的本地升级/验收命令能稳定运行。

同日完成上海凯奔人事模块的现网只读契约对齐：生产部署包和授权备份仅以只读方式核对员工、证书、培训记录、培训课程、培训实施、授权项目、人员授权的 Controller、DTO/VO、实体与表结构。本地补齐对应 HR Controller、DTO/VO、Mapper/Service 实现，并新增 `tenant/V1.26__shkb_hr_core.sql`。该迁移以前 12 张已核对表的结构为基础，并补建部署代码已引用、但该备份未见的 `shkb_person_authorization_file`；它只使用 `CREATE TABLE IF NOT EXISTS`，没有业务数据写入、删除或重建。`V1.26` 已在一次性本地 MySQL 8 首次和重复执行均通过，`verify-migration-catalog.ps1 -Plan ExistingDatabase`、Java 25 十模块编译、前端类型检查、27 项 Vitest 与生产构建均通过。人员授权附件下载另行限制在应用上传根目录内，并使用安全的 Content-Disposition 文件名编码。此处没有对云端执行 SQL、上传、部署或重启；HR 仍缺本地认证下的直连/Vite 写流程验收、菜单/角色业务配置确认和生产恢复副本验收。

随后完成治理里程碑 V1.37（里程碑编号，不是数据库迁移号）：新增仅限本机隔离库的 `scripts/verify-hr-flow.ps1`，对员工档案、证书、培训记录、培训课程、培训实施、授权项目和人员授权七项功能，在 8088 直连与 5173 `/api` 代理分别执行认证后关键读写、401/403/409 契约、导出、附件上传/列表/下载/删除与路径逃逸拒绝、培训实施完成事务（参与人结果与员工培训记录同事务落库）验收，并在 `finally` 中精确清理业务夹具、权限夹具、上传物理文件和残留断言为 0。验收过程中修复三处守卫缺口：员工附件与人员授权附件上传缺少父记录存在性校验（孤儿附件可写）、员工附件下载把绝对上传 URL 错误拼成文件路径且错误信息泄露真实路径、人员授权附件下载根目录与 `jugg.upload.location` 不一致；两个下载接口现统一从 URL 提取 `/oss/...` 路径映射到上传根并拒绝 `..` 逃逸。Java 25 十模块编译、前端类型检查、27 项 Vitest 与生产构建、`verify-migration-catalog.ps1 -Plan All` 均通过；本地运行镜像为 `kberp-api:local-hr-flow-20260817`。HR 模块从 L2 提升到 L3；HR 正式菜单/角色迁移仍是业务确认项（当前仅本地夹具权限验证），附件物理文件删除/恢复、浏览器人工验收和生产恢复副本验收仍未完成。云服务器未作任何修改。

随后完成治理里程碑 V1.38（里程碑编号，不是数据库迁移号）：在本地对账发现并修复 HR 菜单权限码漂移（`sys_menu` 105006 授权项目主菜单 permission 为遗留类名 `AuthorizationProject`，与前后端契约 `hr:authorization:query` 不一致，已仅对本地库对齐）；新增 `utils/ShkbUploadFileUtil` 从 `jugg.upload.location` 解析并安全删除附件物理文件（拒绝 `..` 逃逸与外部 `://` URL），并接入员工附件与人员授权附件的删除 Service——API 删除附件后容器内物理文件实测消失（探针验证 EXISTS→MISSING），HR 直连与 Vite `/api` 全流程回归通过。审计确认其余模块（合同/工具/设备/工卡等）的附件删除仍只删 DB 行（`UploadUtil.deleteFile` 调用被注释，且外部 web-starter 无该 API），统一附件生命周期仍属 P1 业务确认专题。新增 `docs/governance/ROLLBACK_RECOVERY_PLAN.md` 固化本地隔离回退演练步骤（数据库/后端/前端/附件），不构成生产回退批准。云服务器未作任何修改。

随后完成治理里程碑 V1.39（里程碑编号，不是数据库迁移号）：业务负责人确认 HR 菜单/角色正式配置——`/hr` 与七个子菜单全部启用；角色 010（人事质量，含员工档案）与 011（质量管理，不含员工档案）沿用 V1.21 生产快照授权，管理员 001 依赖 `admin` 权限豁免、不显式绑定菜单；正式权限码采用前后端代码现有 `hr:employee`/`hr:certificate`/`hr:training`/`hr:authorization` 前缀。新增部署迁移 `tenant/V1.27__shkb_hr_menu_permission_fix.sql`（受控 UPDATE、当前值守卫、可重复执行），把三处漂移版本化：授权项目主菜单遗留码 `AuthorizationProject`→`hr:authorization:query`、员工修改按钮 `hr:employee: update` 去掉多余空格、培训记录菜单及三个按钮整套 `hr:employee:*`→`hr:training:*`；同步把 `ShkbEmployeeTrainingController`（9 处）、`TrainingParticipantController`（`hr:training:add`→`hr:training:create`，2 处）与前端培训记录 index/add/modify 三文件权限码对齐，并在 migration-catalog 登记 `update-data` 受控数据变更。V1.27 在本地冒烟库连续执行两次，第二次为 no-op；`verify-migration-catalog.ps1 -Plan All`（34 项）、后端 Java 25 十模块编译、前端类型检查/6 文件 27 项 Vitest/生产构建、`verify-hr-flow.ps1`、`verify-menu-baseline.ps1`、`verify-auth-permission.ps1` 均通过 8088 直连与 5173 `/api` 代理，Playwright 5 用例（auth-menu + hr-menus）通过；本地镜像 `kberp-api:local-hr-permfix-20260818` 健康运行。该证据不代表生产恢复副本验收，不解除生产部署锁；云服务器未作任何修改。

随后完成治理里程碑 V1.40（里程碑编号，不是数据库迁移号）：按业务确认规则落地批次/序列号盘点明细契约。新增部署迁移 `tenant/V1.28__shkb_take_stock_batch_serial.sql`（仅 CREATE TABLE IF NOT EXISTS），建立盘点单批次明细表（逐批次录入、实盘数量允许调整、唯一键 `sheet_detail_id+batch_number` 兜底重复提交）与序列号明细表（一条序列号一条明细、实盘状态、唯一键 `sheet_detail_id+serial_number`）。盘点单新增/修改现在校验：批次管理航材必须逐批次录入且批次实盘合计等于盘点数量；序列号管理航材必须逐序列号录入且实盘在库数量等于盘点数量；重复批次/序列号与跨类型明细（批次商品录序列号等）明确拒绝；修改按“删除+重建”整体替换追溯明细，提交后允许修改/撤销。差异处理 `handleDiff` 按追溯明细执行：批次按实盘-系统逐批次盘盈/盘亏（新增 `ProductStockService.addStockBatch` 与批次条件加库，盘盈新批次自动建批次行），序列号逐条判定一致/盘亏（状态条件更新置出库）/盘盈（已出库序列号恢复在库或新序列号入批次），总库存按净差异统一调整保证批次合计与总账一致；系统无此序列号但实盘缺失的矛盾数据明确拒绝。`scripts/verify-stocktake-flow.ps1` 扩展为普通、批次（缺明细/重复/合计不符拒绝、盘亏+新批次盘盈 `4→5`）、序列号（缺明细/重复/数量不符拒绝、一致/盘亏/盘盈逐条、`stock_status` 与批次数量原子变化）与矛盾数据拒绝场景，8088 直连与 5173 `/api` 代理均通过，夹具清理后残留为 0；`verify-stock-adjust-flow.ps1` 与 `verify-stock-transfer-flow.ps1` 回归通过（普通调整/调拨与既有追溯型安全阻断不受影响）。Java 25 十模块编译、`verify-migration-catalog.ps1 -Plan All`（35 项）通过；V1.28 在本地冒烟库连续执行两次成功。前端盘点单新增/修改/详情页已支持批次/序列号明细录入与展示，前端类型检查/测试/构建门禁通过。该证据不代表批次/序列号库存调整与调拨已经实现，不解除生产部署锁；云服务器未作任何修改。

随后完成治理里程碑 V1.41（里程碑编号，不是数据库迁移号）：按业务确认规则落地批次/序列号库存调整明细契约。新增部署迁移 `tenant/V1.29__shkb_stock_adjust_batch_serial.sql`（仅 CREATE TABLE IF NOT EXISTS），建立调整单批次明细表（入库/出库均按批次指定、数量为正数、唯一键 `sheet_detail_id+batch_number`）与序列号明细表（一条序列号一条明细、唯一键 `sheet_detail_id+serial_number`）。调整单新增/修改校验：批次管理航材必须逐批次指定且批次合计等于调整数量；序列号管理航材必须逐序列号指定且明细数量等于调整数量；重复批次/序列号与跨类型明细拒绝；驳回后允许修改并重新提交（追溯明细整体替换）。审核通过按明细立即变更库存：批次入库用新增的 `ProductStockService.addStockBatch`（批次缺失自动建批次行），批次出库条件更新守卫充足库存拒绝负库存；序列号入库仅允许新序列号或已出库序列号重新入库（0→1），序列号出库仅允许在库序列号出库（1→0），不允许状态直跳，重复序列号由唯一键与状态条件更新拒绝；跨仓库序列号明确引导走调拨流程；总库存按原明细数量统一调整保证批次合计与总账一致。`scripts/verify-stock-adjust-flow.ps1` 扩展为普通（5→7→4、并发重复审核仅一次生效）、批次（缺明细/重复/合计不符/不存在批次出库拒绝、入库 `4→7` 与出库 `7→5` 逐批次生效）、序列号（缺明细/重复/数量不符/在库重复入库/已出库重复出库拒绝、新序列号入库与在库出库的 `stock_status` 与批次数量原子变化）场景，8088 直连与 5173 `/api` 代理均通过，夹具清理后残留为 0；`verify-stocktake-flow.ps1` 与 `verify-stock-transfer-flow.ps1` 回归通过。Java 25 十模块编译、`verify-migration-catalog.ps1 -Plan All`（36 项）通过；V1.29 在本地冒烟库连续执行两次成功。前端调整单新增/修改/详情页已支持批次/序列号明细录入与展示。该证据不代表批次/序列号仓库调拨已经实现，不解除生产部署锁；云服务器未作任何修改。

随后完成治理里程碑 V1.42（里程碑编号，不是数据库迁移号）：按业务确认规则落地批次/序列号仓库调拨明细契约与在途库存。新增部署迁移 `tenant/V1.30__shkb_stock_transfer_batch_serial.sql`（仅 CREATE TABLE IF NOT EXISTS），建立调拨单批次明细表（逐批次指定库存、received_num 累计收货、唯一键 `order_detail_id+batch_number`）与序列号明细表（一条序列号一条明细、transfer_status 1在途/2已收货、唯一键 `order_detail_id+serial_number`）。调拨单新增/修改校验：批次管理航材必须逐批次指定且合计等于调拨数量，序列号管理航材必须逐序列号指定且明细数量等于调拨数量；重复与跨类型明细拒绝；修改按“删除+重建”整体替换（撤审/修改/删除沿用 CREATED/APPROVE_REFUSE 状态机）。审核通过按明细立即扣减转出仓（批次条件更新拒绝负库存与不存在批次；序列号置出库并在途、批次数量同步扣减）；收货按明细加转入仓（批次在转入仓自动建批次行并条件累计已收；序列号仅当在途时条件置已收货、切到转入仓批次并在库），部分收货与最终收货按未收数量判定；收货明细与调拨明细不一致（不属于本单、超量、已收货）明确退回并整体回滚，重复/并发收货由行锁与条件更新拒绝；一张调拨单仅一进一出，跨仓/跨库位按批次号在转入仓归属。在途库存单独记录：批次在途=调拨数量-已收货数量，序列号在途=transfer_status=1 行。`scripts/verify-stock-transfer-flow.ps1` 扩展为普通（10→6 与 1→5、并发重复收货仅一次）、批次（缺明细/重复/合计不符拒绝、审核扣减、部分/最终收货与在途计数、幽灵批次退回）、序列号（重复拒绝、审核置在途、逐条收货切换到转入仓批次、不属于本单退回）场景，8088 直连与 5173 `/api` 代理均通过，夹具清理后残留为 0；`verify-stocktake-flow.ps1` 与 `verify-stock-adjust-flow.ps1` 回归通过。Java 25 十模块编译、`verify-migration-catalog.ps1 -Plan All`（37 项）通过；V1.30 在本地冒烟库连续执行两次成功。前端调拨单新增/修改/收货/详情页已支持批次/序列号明细录入、收货与展示。至此批次/序列号盘点、库存调整、仓库调拨三条追溯链路的明细契约均按业务确认规则落地并通过本地隔离验证。该证据不解除生产部署锁；云服务器未作任何修改。

随后完成治理里程碑 V1.43（里程碑编号，不是数据库迁移号）：统一附件物理生命周期。把 V1.38 引入的 `ShkbUploadFileUtil.deletePhysicalFile`（严格限制在上传根目录内、拒绝 `..` 逃逸与外部 `://`、删除失败不阻断数据库删除并记日志）接入其余五个只删 DB 行的附件删除路径：合同附件（`ContractFileServiceImpl`）、工具附件（`ShkbToolFileServiceImpl`）、设备附件（`ShkbDeviceFileServiceImpl`）、工具计量记录附件（`ToolRecordFileServiceImpl`）、工卡附件（`WorkCardFileServiceImpl`）；成品出入库附件属冻结范围不动。`verify-equipment-flow.ps1` 增加物理文件探针：工具/计量记录/设备附件上传后容器内文件 EXISTS，API 删除后 MISSING，8088 直连通过且夹具清理残留 0；`verify-contract-flow.ps1` 回归通过。新增 `tests/e2e/core-chains.spec.ts`（核心浏览器 E2E）：合同/维修任务/工卡/工具/采购/发料/库存调拨/盘点/调整共 12 个关键页面查询链路，与 auth-menu、hr-menus 共 6 个 Playwright 用例在本地冒烟通过。菜单/角色基线生产放行证据整理至 `docs/governance/RELEASE_CONFIRMATION_MENU_ROLE_BASELINE.md`（catalog 37 项、menu-baseline/auth-permission/hr-flow 双链路、`verify-release-preflight.ps1 -Stage AfterMigration` 通过并提示 V1.21 需业务确认）；放行需业务负责人按 D1–D5 逐项批准，生产执行前须在恢复副本演练并备份。Java 25 十模块编译、前端 type:check 与构建通过。生产存储方案与保留策略、浏览器人工验收与生产恢复副本验收仍未完成；不解除生产部署锁；云服务器未作任何修改。

## 每次改动的固定流程

1. 执行 `git status --short`，确认并保护现有改动。
2. 执行 `powershell -ExecutionPolicy Bypass -File .\scripts\verify.ps1`，记录改动前基线。
3. 一次只升级一个依赖族；不要同时升级 Java、Spring Boot、jugg 和数据库驱动。
4. 再次运行验证脚本。涉及运行时行为时启动 smoke 环境并验证受影响接口。
   看板相关改动同时运行 `scripts/verify-dashboard.ps1`。
   合同相关改动同时运行 `scripts/verify-contract.ps1`。
   合同新增、修改、附件或任务生成相关改动在本地隔离库增加 `scripts/verify-contract-flow.ps1`，并对 Vite `/api` 再运行一次。
   工具/设备相关改动同时运行 `scripts/verify-equipment.ps1`。
   工具、计量/维保记录或附件写入相关改动在本地隔离库增加 `scripts/verify-equipment-flow.ps1`，并对 Vite `/api` 再运行一次。
   工卡相关改动同时运行 `scripts/verify-work-card.ps1`；本地隔离库增加 `scripts/verify-work-card-flow.ps1`。
   物料相关改动同时运行 `scripts/verify-material-flow.ps1`；本地隔离库增加 `scripts/verify-material-flow-write.ps1`。涉及审批、库存、批次、序列号或状态流转时，再运行 `scripts/verify-material-concurrency.ps1 -Iterations 5`。
   修改全局异常、认证、权限或响应包装时，同时对 8088 直连和 5173 `/api` 代理运行 `scripts/verify-error-contract.ps1`。
   修改登录、角色、菜单权限、Token 或账号状态时，在本地隔离库同时对 8088 和 5173 `/api` 运行 `scripts/verify-auth-permission.ps1`。
   修改菜单、租户模块或业务角色基线时，同时对 8088 和 5173 `/api` 运行 `scripts/verify-menu-baseline.ps1`。
   修改自动化设备或设备任务时，运行 `scripts/verify-machine-task.ps1`；设备上报、下发和状态变化增加本地隔离库 `scripts/verify-machine-task-flow.ps1`。
   修改采购订单、收货、采购退货、批次库存或其迁移时，在本地隔离库运行 `scripts/verify-purchase-flow.ps1`，并对 Vite `/api` 再运行一次。
   修改盘点任务、盘点单或差异库存处理时，在本地隔离库运行 `scripts/verify-stocktake-flow.ps1`，并对 Vite `/api` 再运行一次。
   修改库存调整单、调整原因或审批库存动作时，在本地隔离库运行 `scripts/verify-stock-adjust-flow.ps1`，并对 Vite `/api` 再运行一次。
   修改仓库调拨单、调拨审批、收货或调拨库存动作时，在本地隔离库运行 `scripts/verify-stock-transfer-flow.ps1`，并对 Vite `/api` 再运行一次。
   修改人事、证书、培训或人员授权时，先运行 `scripts/verify-migration-catalog.ps1 -Plan ExistingDatabase`、后端和前端门禁；`V1.26` 只允许在本地隔离 MySQL 首次/重复执行。新增 HR 写流程验收前必须先明确菜单/角色配置，且同时覆盖直连与 Vite `/api`。
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
- 采购收货与采购退货已覆盖总库存、批次库存和指定序列号部分退货；销售不属于上海凯奔当前产品范围，不建立销售主链路回归。
- 普通航材盘点已覆盖关键写流程；批次/序列号盘点尚无明细级录入和调整契约，当前只做非零差异安全阻断，不得据此宣称追溯型盘点已实现。
- 普通航材库存调整已覆盖入库、出库和重复审批；批次/序列号库存调整尚无明细级契约，当前全部安全阻断。
- 普通航材库存调拨已覆盖两阶段出入库、部分收货和并发重复收货；批次/序列号库存调拨尚无明细级契约，当前全部安全阻断。

## 数据安全

`migration/tenant/V1.0__init.sql` 是全量 DROP/CREATE 脚本，只能用于新建空库。已有数据库只允许按环境确认后的增量迁移，执行前必须备份并核对目标 schema。

当前 `xingyun-api/pom.xml` 明确从运行 jar 排除 `db/**`，工程也没有 Flyway 运行时依赖。因此 `db/migration` 是纳入版本控制的部署前迁移源文件，并不会因重新打包或重启应用而自动执行。发布时必须先备份目标库，按版本顺序单独应用尚未执行的增量 SQL，再运行对应 `verify-*.ps1` 接口冒烟；禁止把“应用已启动”当作“迁移已完成”。

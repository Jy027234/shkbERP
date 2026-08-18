# 上海凯奔 ERP：下一轮 Codex 开发交接

> 状态快照：2026-08-16。本文用于新会话快速建立正确边界；它不是生产发布运行书。开始任何改动前，仍必须阅读仓库根目录 `AGENTS.md`、对应应用的 `UPGRADE.md` 和本目录中的专题文档。

> 2026-08-17 新增的执行型源码启停、优先级和完成定义见 `NEXT_AI_DEVELOPMENT_TODO.md`。交给其他 AI 开发时，应把该文件作为首要任务清单；产品范围仍以 `PRODUCTION_FUNCTION_BASELINE.md` 为准，可靠性等级仍以 `SHKB_MODULE_MATRIX.md` 为准。

## 一句话结论

只在本仓库开发：`shkbERP` 是唯一源码与发布入口。最新技术候选为 `v2026.08.16-rc.3`（源码提交 `9f9fbd68ccb528975741a90a9a2e8b99a6744789`）；本地恢复副本技术验收通过，但生产发布锁仍为 `false`，云端不得修改。

候选标签固定的是该提交的前端、后端、迁移和本地镜像证据。后续即使 `main` 因文档或开发提交继续前进，也不能把新的 `HEAD` 误称为 RC3；要发布新的代码，必须重新执行候选流程并创建新标签。

## 先做什么

在仓库根目录执行以下只读/验证步骤，再决定本轮只改后端、前端，还是同步 API 契约：

```powershell
git status --short
powershell -ExecutionPolicy Bypass -File .\scripts\verify-source-baseline.ps1
git log --oneline -8
```

如果工作树不是干净的，先识别和保护已有改动；不要覆盖、清理或格式化未知改动。修改前同步核对：

- 根目录 `AGENTS.md`：唯一源码、发布锁、单租户和验证规则；
- `docs/governance/NEXT_AI_DEVELOPMENT_TODO.md`：可用、条件可用、禁用源码和 P0/P1/P2 执行清单；
- `erp-backend/UPGRADE.md`：后端基线、迁移和模块级验证脚本；
- `erp-frontend/UPGRADE.md`：前端工具链、类型检查和浏览器验证；
- `docs/governance/SHKB_MODULE_MATRIX.md`：模块范围、可靠性等级和已知缺口。

## 源码、云端与范围边界

| 项目 | 当前规则 |
| --- | --- |
| 唯一开发与发布仓库 | 当前 `shkbERP` 单体仓库；工作区旁旧 `erp-backend`、`erp-frontend` 仅是只读对账输入，不能构建、部署或覆盖本仓库。 |
| 云端现网 | 冻结业务参照。它是旧 Java 8 / Spring Boot 2.2.x 运行物，且没有可核验提交号；现网菜单和真实流程决定产品业务范围，不能自动覆盖源码实现或发布版本。范围快照见 `PRODUCTION_FUNCTION_BASELINE.md`。除非用户逐次明确授权，禁止上传、执行 SQL、重启、改配置或写入云端。 |
| 产品定位 | 后续按单租户迭代。保留现有租户上下文和登录字段以兼容 jugg 与存量数据，但不建设跨租户功能或测试矩阵。 |
| 冻结模块 | 销售相关通用代码与“成品出入库”均未纳入上海凯奔真实流程，属于非发布范围。不得补 schema、扩展功能或纳入核心回归，除非用户先明确启用并确认业务范围。 |
| 运行时基线 | 后端 Java 25 / Spring Boot 3.5.x / jugg 5.x / Maven 3.9.x；前端 Node 24 / pnpm 9.15.9 / Vue 3 / Vite 4。 |

Windows 中文工作目录已被验证可用。自动化脚本必须沿用仓库脚本的当前 PowerShell 宿主调度方式，不要自行改回由旧 `powershell.exe` 二次转发参数的写法。

## 当前候选与已验证证据

| 层面 | 已完成的证据 | 不能推导出的结论 |
| --- | --- | --- |
| RC3 候选 | 完整前后端门禁、jar/前端产物/迁移目录/镜像哈希均已固定在带注释标签 `v2026.08.16-rc.3`。 | 标签不是生产授权。 |
| 迁移治理 | `migration-catalog.json` 管理 13 条存量库候选 SQL；预检和隔离恢复演练已连续执行两轮，迁移后 9 项检查通过；`V1.26` 的 HR schema 已在一次性 MySQL 首次/重复执行验证。 | SQL 不会随应用启动自动执行，也不可直接对生产执行。 |
| 授权备份副本 | 经授权的生产逻辑备份已只在本地隔离 MySQL 恢复和校验；恢复前后逻辑导出一致。 | 不代表业务负责人确认了菜单、流程或数据口径。 |
| 核心只读 API | RC3 候选镜像达到 readiness；历史 Jugg 密文连续性验证通过；健康、菜单、看板、合同、工具设备、工卡、航材查询、设备任务共 8 组探针通过；临时资源已清理。 | 没有验证合同、采购、库存、盘点、调拨等真实写流程，更不构成生产切换。 |

恢复副本 API 验收中的历史 Jugg 密钥仅被一次性受控会话注入当前进程，仓库没有保存密钥、指纹、密文、明文凭据或云端地址。以后若再次运行该验收，必须由授权人员走同样的密钥管理流程；不得通过重置密钥或改写生产租户 JDBC 配置绕过门禁。

原始生产备份副本是否归档或安全销毁，仍由数据所有者按保留策略决定；不要由开发任务自动删除。

## 模块可靠性：应如何理解

当前没有模块达到 L5（带标签候选在生产恢复副本完成业务验收）。主要可靠性已达到 L4 的本地隔离写流程包括：合同、工具设备、维修工卡、发料/出库、采购/收货/退货、普通航材盘点、普通航材库存调整和普通航材调拨。

仍应明确标注的缺口：

- 批次/序列号航材的盘点（V1.40/V1.28）、库存调整（V1.41/V1.29）与仓库调拨（V1.42/V1.30，含在途库存单独记录）明细契约均已按业务确认规则实现并双链路验证，三条追溯链路的安全阻断已解除；浏览器人工验收与生产恢复副本验收未完成；
- 合同恢复规则、附件物理存储生命周期、真实设备协议、定时任务、WebSocket、其余 RabbitMQ 生产点及两个 cloud 模块仍未完成端到端验证；
- 人事/培训/证书/授权已确认是现网正式功能，本地实现和 `V1.26` schema 已达到 L3：`verify-hr-flow.ps1` 已对七项功能在 8088 直连与 5173 `/api` 代理完成关键读写、事务（培训实施完成写员工培训记录）、401/403/409 契约、导出和附件安全（含 `..` 逃逸拒绝）验收，夹具残留为 0。HR 菜单/角色配置已于 2026-08-18 业务确认（1+7 菜单全部启用、角色 010+011、管理员 001 依赖 admin 豁免不显式绑定）并落地 `V1.27__shkb_hr_menu_permission_fix.sql`（三处权限码漂移版本化，8088/5173 双链路回归与 5 个 Playwright 用例通过，详见 UPGRADE 里程碑 V1.39）；仍缺附件物理文件生命周期、浏览器人工验收和生产恢复副本验收。

任何模块状态变化都要更新 `SHKB_MODULE_MATRIX.md`，并写明 schema、验证命令和证据日期；“能编译”或“页面存在”都不能提升可靠性等级。

## 数据库与发布红线

1. **严禁**对已有数据库执行 `erp-backend/xingyun-api/src/main/resources/db/migration/tenant/V1.0__init.sql`，它包含全量 DROP/CREATE。
2. `db/migration` 是部署前 SQL 源文件；运行 jar 排除了 `db/**`，项目没有 Flyway 运行时依赖。重启应用不会自动迁移数据库。
3. 只允许按 `docs/governance/migration-catalog.json` 中的 `existingDatabasePlan` 顺序评审增量 SQL。任何新 SQL 都要登记目录、哈希、风险和执行范围，先运行 `verify-migration-catalog.ps1`。
4. 只要 `docs/governance/source-baseline.json` 的 `release.deploymentAllowed` 不是 `true`，`scripts/verify-source-baseline.ps1 -Release` 必须失败，任何人不得绕过它。
5. 未获得一次具体、明确的用户授权时，不得对云端执行 SSH 写入、SQL、上传、容器替换、静态资源替换或重启。

生产发布仍缺四类人工条件：V1.21 菜单/模块影响的业务确认、合同/采购/库存等真实流程验收、可执行回退方案和明确变更窗口批准。它们不是下一个普通开发任务可以默认代替完成的事项。

## 建议的下一高价值开发专题

建议做**人事管理本地隔离验收与配置对账**。本地候选已补齐 HR 接口和 schema，下一步应证明员工、证书、培训和人员授权在本地认证链路中可用，而不是为不属于产品范围的销售代码补功能，也不是先做框架大版本升级或多租户改造。

建议的执行顺序：

1. 在隔离库恢复可用的本地认证、部门、用户、角色和菜单基线；HR 菜单/权限授予属于业务配置，先由负责人确认，禁止凭开发推断写入生产。
2. 为员工、证书、培训课程/实施、人员授权逐项建立直连 API 冒烟，再通过 Vite `/api` 代理执行同一组流程；所有新增、修改、删除、上传都只允许本地隔离库。
3. 覆盖人员授权附件上传、下载、删除与路径越界拒绝，以及培训完成写入参与人和培训记录的事务边界。
4. 把可重复的隔离流程沉淀为专题脚本和测试；保留 `V1.26` 首次/重复执行检查，绝不修改既有 SQL 或执行 `V1.0__init.sql`。
5. 运行相应专题脚本、后端和前端门禁，更新现网功能基线、模块矩阵与升级记录；以小而可回滚的提交进入评审。

若用户确认的下一业务优先级不是人事，则保留上述方法，改以被明确指定且已纳入现网范围的模块为主题；不要为了“继续优化”而启动销售或其他范围不明的半成品模块。

## 常用验证命令

```powershell
# 根目录：开发源码治理与前后端静态门禁
powershell -ExecutionPolicy Bypass -File .\scripts\verify-source-baseline.ps1
powershell -ExecutionPolicy Bypass -File .\scripts\verify-all.ps1

# 后端或前端发生对应改动时
Set-Location .\erp-backend
powershell -ExecutionPolicy Bypass -File .\scripts\verify.ps1

Set-Location ..\erp-frontend
powershell -ExecutionPolicy Bypass -File .\scripts\verify.ps1
```

业务写流程、恢复演练和生产备份副本验收脚本都只能指向本地隔离 Docker 环境；具体脚本与适用范围见两个 `UPGRADE.md` 及 `MIGRATION_SAFETY.md`。涉及 API、认证、数据库或代理时，必须同时验证后端直连和前端 `/api` 代理。

创建新发布候选只在 `main` 干净、全部改动已合并后进行：

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\new-release-candidate.ps1 -Version vYYYY.MM.DD-rc.N
```

候选流程不会解锁生产，也不应被用来替代业务验收。

## 可直接粘贴给下个会话的开场说明

```text
请在 shkbERP 单体仓库继续开发。先完整阅读 AGENTS.md、docs/governance/NEXT_AI_DEVELOPMENT_TODO.md、docs/governance/CODEX_HANDOFF.md、erp-backend/UPGRADE.md、erp-frontend/UPGRADE.md 和 SHKB_MODULE_MATRIX.md；检查 git status 并运行 verify-source-baseline.ps1。产品按单租户维护，云端只读且禁止部署。不要使用旁边旧 erp-backend/erp-frontend 仓库作为发布源，也不要执行 V1.0__init.sql。

本轮已完成：对已确认现网范围的“人事管理”完成只读接口/schema 对账，并在本地补齐员工、证书、培训记录、培训课程、培训实施、授权项目和人员授权的后端实现与 `V1.26` 增量 schema；前端、后端门禁及一次性 MySQL 首次/重复执行均通过。下一步是在已确认本地菜单/角色配置后，完成七项功能的隔离直连/Vite `/api` 读写验收。销售不属于上海凯奔产品范围，不得作为开发主题；禁止对云端写入或执行 SQL。
```

## 每次收尾清单

- 仅提交本任务相关文件，不覆盖用户改动；
- 更新模块矩阵、升级记录和本交接文档中受影响的状态；
- 记录实际运行的验证命令与结果，说明仍未覆盖的风险；
- 除非用户另行明确授权，不要把候选、测试通过或本地 Docker 状态表述为云端已部署。

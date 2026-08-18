# 菜单/角色基线（V1.21 + V1.27）生产放行确认清单

> 生成日期：2026-08-18。本文只整理放行所需的验证证据与业务决策项，**不是**生产执行授权。
> 生产执行（上传 jar、执行 SQL、重启、解锁 `deploymentAllowed`）必须由业务负责人在本清单逐项明确批准后进行。

## 1. 待放行内容

| 迁移 | 内容 | 类型 |
| --- | --- | --- |
| `tenant/V1.21__shkb_menu_permission_baseline.sql` | 单租户名称固定为“上海凯奔航空技术有限公司”；删除租户 1000 的模块关系（7,12,15）；写入 67 条业务菜单、21 条通用菜单覆盖、10 个业务角色（002–011，含 HR 的 010/011）与 592 条角色—菜单关系（含 HR 1+7 菜单） | 受控数据变更（`controlledDataMutation: true`） |
| `tenant/V1.27__shkb_hr_menu_permission_fix.sql` | HR 权限码三处修正：授权项目遗留码 `AuthorizationProject`→`hr:authorization:query`；员工修改按钮去掉多余空格；培训记录整套 `hr:employee:*`→`hr:training:*` | 受控 UPDATE（幂等） |

## 2. 验证证据（2026-08-18，全部本地隔离环境）

| 证据 | 结果 |
| --- | --- |
| `verify-migration-catalog.ps1 -Plan All` | ✅ 37 项（V1.21 哈希与登记一致；V1.26–V1.30 已登记） |
| V1.27 在本地冒烟库连续执行两次 | ✅ 第二次 no-op（幂等） |
| `verify-menu-baseline.ps1`（8088 + 5173） | ✅ 14 个业务根菜单 |
| `verify-auth-permission.ps1`（8088 + 5173） | ✅ 管理员/受限/403/401/锁定停用 |
| `verify-hr-flow.ps1`（8088 + 5173） | ✅ 七项 HR 读写/事务/权限/附件 |
| `verify-release-preflight.ps1 -Stage AfterMigration` | ✅ 通过；提示 V1.21 将更新租户名并移除 0 个（冒烟库已移除）模块关系，**需业务确认** |
| `verify-release-restore.ps1`（本地恢复副本演练） | ✅ 通过：源库 `shkb_platform` → 随机克隆库哈希一致，候选迁移连跑两轮，迁移后 9 项表/列检查通过，克隆库已清理（evidence: `evidence/release-restore-20260818.json`） |
| 前端 E2E（auth-menu + hr-menus + core-chains） | ✅ 5 + 1 用例（HR 七菜单导航、核心链路 12 页） |
| `git diff --check` / 提交 `b469b67` | ✅ 干净 |

## 3. 业务决策项（请逐项明确批准/否决）

- [ ] **D1 批准 V1.21 生产执行**：更新租户名称、移除租户 1000 的模块关系（7,12,15）、写入菜单/角色/角色菜单基线。说明：本地恢复副本演练（`verify-release-restore.ps1`）已通过（哈希一致、迁移两轮、9 项后置检查）；生产执行仍须先备份生产库并按部署流程单独应用增量 SQL。
- [ ] **D2 批准 V1.27 生产执行**：HR 权限码三处修正（幂等 UPDATE，仅 `sys_menu.permission`）。
- [ ] **D3 HR 正式启用配置复核**：`/hr` 与七个子菜单全部启用；可访问角色 010（人事质量，含员工档案）与 011（质量管理，不含员工档案）；管理员 001 依赖 `admin` 权限豁免、不显式绑定菜单；正式权限码采用 `hr:employee`/`hr:certificate`/`hr:training`/`hr:authorization` 前缀。**（该项已于 2026-08-18 确认，作为 D1/D2 的依据）**
- [ ] **D4 执行窗口与回滚责任人**：指定变更窗口与责任人；回滚步骤见 `docs/governance/ROLLBACK_RECOVERY_PLAN.md`。
- [ ] **D5 `deploymentAllowed` 解锁**：仅在批准本次发布动作时由你明确指令解锁 `source-baseline.json`；解锁不等于已发布。

## 4. 约束

- 严禁对已有数据的库执行 `V1.0__init.sql`；已登记迁移内容不得修改。
- 本清单形成期间对云端零写入；`source-baseline.json` 的 `release.deploymentAllowed` 仍为 `false`。

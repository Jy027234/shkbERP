# KBERP 单体仓库协作说明

本仓库包含两个应用目录：`erp-backend` 与 `erp-frontend`。它们现在共享一个 Git 基线，但构建和验证仍相互独立；跨接口、认证、数据库或代理配置的修改必须同时验证两端。

## 当前维护基线

- 后端：Java 25 LTS、Spring Boot 3.5.x、jugg 5.x、Jakarta API、Maven 3.9.x。
- 前端：Node.js 24 LTS、pnpm 9.15.9、Vue 3、Vite 4。
- 产品按单租户部署。保留现有租户上下文以兼容框架和存量数据，但不新增多租户能力或跨租户测试矩阵。

## 业务源码与发布治理

- 本仓库 `git@github.com:Jy027234/shkbERP.git` 是唯一发布入口。工作区旁的旧 `erp-backend`、`erp-frontend` 独立仓库只作为待对账输入，不得直接构建或部署。
- 当前治理状态见 `docs/governance/source-baseline.json`。只要 `release.deploymentAllowed` 不是 `true`，就禁止向生产环境发布。
- 云端现网是冻结的业务参照，不是可写的同步目标。除非用户明确批准一次具体发布，不得上传 jar、替换静态文件、执行迁移、重启服务或修改云端数据库。
- 发布必须来自本仓库的干净工作树和明确 Git 标签；前端、后端、数据库迁移必须属于同一提交。禁止把其他目录启动的 Vite、jar 或 Docker 镜像拼成发布版本。
- 修改业务模块前先更新或核对 `docs/governance/SHKB_MODULE_MATRIX.md`；“代码存在/编译通过”不能替代 schema、接口和业务流程验证。
- 开始开发和发布前运行 `scripts/verify-source-baseline.ps1`；正式发布增加 `-Release`。

## 开始任务前

1. 阅读目标目录内的 `AGENTS.md` 与 `UPGRADE.md`。
2. 执行 `git status --short`，保护用户已有改动。
3. 明确只改后端、只改前端，还是同步修改 API 契约。
4. 版本升级一次只改变一个主要依赖族，并保留可回滚提交。

## 验证

- 全部静态门禁：`powershell -ExecutionPolicy Bypass -File .\scripts\verify-all.ps1`。
- 后端：在 `erp-backend` 执行 `powershell -ExecutionPolicy Bypass -File .\scripts\verify.ps1`。
- 前端：在 `erp-frontend` 执行 `powershell -ExecutionPolicy Bypass -File .\scripts\verify.ps1`。
- 发布候选：全量门禁增加 `-Full`；涉及运行时行为时，按后端升级记录中的 smoke README 做端到端冒烟。
- 正式发布门禁：`powershell -ExecutionPolicy Bypass -File .\scripts\verify-all.ps1 -Full -Release`。治理状态未解锁、工作树不干净或 HEAD 没有版本标签时必须失败。

严禁对已有数据的数据库执行 `migration/tenant/V1.0__init.sql`，它包含 DROP/CREATE 全量初始化逻辑。生产凭据只能通过环境变量或密钥管理系统注入，不得提交真实 `.env` 文件。

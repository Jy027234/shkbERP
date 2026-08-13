# KBERP 单体仓库协作说明

本仓库包含两个应用目录：`erp-backend` 与 `erp-frontend`。它们现在共享一个 Git 基线，但构建和验证仍相互独立；跨接口、认证、数据库或代理配置的修改必须同时验证两端。

## 当前维护基线

- 后端：Java 25 LTS、Spring Boot 3.5.x、jugg 5.x、Jakarta API、Maven 3.9.x。
- 前端：Node.js 24 LTS、pnpm 9.15.9、Vue 3、Vite 4。
- 产品按单租户部署。保留现有租户上下文以兼容框架和存量数据，但不新增多租户能力或跨租户测试矩阵。

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

严禁对已有数据的数据库执行 `migration/tenant/V1.0__init.sql`，它包含 DROP/CREATE 全量初始化逻辑。生产凭据只能通过环境变量或密钥管理系统注入，不得提交真实 `.env` 文件。


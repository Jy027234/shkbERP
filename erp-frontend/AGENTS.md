# 前端协作约束

本文件适用于整个 `erp-frontend` 仓库。

## 技术基线

- 使用 Node.js 24 LTS 和 pnpm 9.15.9；以 `package.json`、`pnpm-lock.yaml`、`.nvmrc` 和 `.node-version` 为准。
- 安装依赖必须使用冻结锁文件：`npx --yes pnpm@9.15.9 install --frozen-lockfile`。
- 不使用 npm/yarn 生成第二份锁文件，不全局升级 pnpm，不删除或重建现有 `pnpm-lock.yaml`。

## 修改规则

- 开始前执行 `git status --short`。仓库已有未提交业务改动，只修改任务涉及的文件。
- 不编辑 `node_modules/`、`dist/` 或生成文件，不对全仓执行带 `--fix` 的 lint/format 命令。
- 保持 `/api` 开发代理与后端契约一致；涉及登录、租户、下载、WebSocket 或响应包装时必须做联调。
- 产品后续按单租户部署；保留当前租户输入和请求字段以兼容后端，除非用户明确要求，不新增多租户管理或跨租户交互。
- 后端统一响应字段为 `code`、`msg`、`data`、`traceId`；错误展示应复用公共 Axios 解析，不在业务页面复制状态码分支。
- 不用 `any`、`@ts-ignore` 或关闭严格检查掩盖类型问题；新增类型错误必须在同一批改动中修复。

## 验证

- 标准门禁：`powershell -ExecutionPolicy Bypass -File .\scripts\verify.ps1`。
- 标准门禁默认依次执行固定工具链校验、`type:check`、Vitest 回归测试和生产构建。
- 首次或锁文件变化后增加 `-Install`，使用冻结锁文件安装后再执行完整门禁。
- 必须阅读构建警告；2026-08-12 已清除既有缺失导出，新增 `is not exported by` 警告视为回归。
- 2026-08-13 `vue-tsc 2.0.29` 已清零历史类型错误；`type:check` 是绿色门禁，任何非零退出均视为回归。
- 修改 API 或公共工具时必须同步补充/更新相应 Vitest 用例，禁止通过删除断言维持绿色。
- 涉及登录、菜单、权限或路由时，在本地后端可用的环境执行 `scripts/verify-e2e.ps1`；E2E 失败不得用延时堆叠或脆弱 CSS 选择器掩盖。

## 升级策略

- 分别升级 Vite/Vue、Ant Design Vue 和 vxe-table；不要在一个任务中同时升级这些依赖族。
- 每次升级记录版本、构建结果、关键页面冒烟和回滚点。

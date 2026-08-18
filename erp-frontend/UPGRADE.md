# 前端升级运行手册

## 已建立的基线

| 项目 | 当前约束 | 说明 |
| --- | --- | --- |
| Node.js | 24.x LTS | `.nvmrc` / `.node-version` 固定主版本 |
| pnpm | 9.15.9 | 与 lockfile v9 一致，不依赖全局安装 |
| Vue | 3.4.38 | 暂不与构建工具同时做大版本升级 |
| Vite | 4.5.3 | 当前生产构建基线 |
| Ant Design Vue | 4.2.3 | 组件声明已完成对齐 |
| vxe-table | 4.7.68 | 渲染器与拦截器声明已完成对齐 |
| TypeScript | 锁文件解析为 5.5.4 | 依赖安装必须使用 frozen lockfile |
| vue-tsc | 2.0.29 | 与当前 TypeScript 兼容，可输出真实类型问题 |
| Vitest | 1.6.1 | 独立 Node 测试配置，不牵动应用 Vite 插件链 |
| Playwright | 1.62.1 | 独立 Chromium E2E，仅在本地后端可用时执行 |

2026-08-12 已在 Node 24 + pnpm 9.15.9 下完成开发服务器和经 `/api` 代理登录的端到端验证。

产品后续按单租户部署。当前登录页仍提交 `tenantName`，这是后端 jugg 租户上下文和存量数据的兼容要求；后续优化不主动扩展租户切换或租户管理界面。若要移除该输入，应单独评估后端默认租户解析和既有账号数据，而不是只隐藏前端字段。

## 当前生产构建警告

2026-08-12 已按真实后端契约修复原有 4 个缺失导出：发料出库改用 `/material/out/sheet/product/search` 返回库存与批次/序列号管理属性；合同任务补齐分页聚合 `queryAll` 和 `PUT /shkb/contract-task`。Vite 生产构建不再出现 `is not exported by` 警告。

当前非阻断警告只剩 Browserslist 数据陈旧和部分 chunk 超过 1500 kB。后续改动不得新增缺失导出；构建退出码 0 仍需结合告警和受影响页面冒烟判断。

## 每次改动的固定流程

1. 执行 `git status --short`，保护现有未提交改动。
2. 首次安装执行 `powershell -ExecutionPolicy Bypass -File .\scripts\verify.ps1 -Install`；日常执行不带参数的脚本。
3. 修改 API 时同时核对后端 DTO、请求编码、下载响应和代理路径。
4. 至少冒烟登录、菜单、改动页面和一个受影响的提交/查询流程。
5. 记录旧版本、新版本、构建结果、浏览器验证和回滚点。

## 类型检查门禁

旧 `vue-tsc 1.8.27` 会因 TypeScript 5.5.4 的内部结构变化直接崩溃。现已固定到可运行的 `vue-tsc 2.0.29`，它会报告既有类型错误，包括缺失类型、大小写不一致、错误的 `long`/`sring` 类型、UI 组件声明漂移以及部分本轮未提交业务代码错误。

2026-08-12 初始基线为 182 个 TypeScript 错误（退出码 2），第一批修复后剩余 134 个。2026-08-13 已完成第二批治理：修复 HR API 的 `requestOptions` 层级、公共 HTTP/状态/路由类型、动态插槽转发、Ant Design Vue 4.2 与 VXE 4.7 声明迁移，以及通知、打印、图表等公共组件问题。当前 `pnpm run type:check` 为 **0 个错误、退出码 0**。

`scripts/verify.ps1` 现已默认执行类型检查、Vitest 回归测试和生产构建；类型错误与测试失败不再属于允许债务。后续升级必须保持门禁为绿，发现回归应在同一批改动中修复，不重新建立非零基线。

后续类型治理规则：

1. API 改动继续以真实后端 DTO 和项目 HTTP 封装为准。
2. Ant Design Vue、vxe-table 等依赖升级时使用目标版本正式声明，不用批量断言压制漂移。
3. 新增公共组件必须为 props、emits、插槽和异步返回值提供明确契约。
4. VS Code 出现大量诊断时，先确认工作区 Java/TypeScript SDK 配置，再以仓库门禁结果区分环境问题与代码回归。

禁止通过全局 `skipLibCheck` 之外的宽泛降级、批量 `@ts-ignore` 或关闭严格选项伪造绿色结果。

## 自动化测试基线

2026-08-13 已建立 Vitest 最小回归基线：6 个测试文件、27 个用例，覆盖 `deepMerge` 的数组合并策略、登录/验证码/租户请求编码与 region、HR 培训课程查询/详情/导出/上传、维修工卡查询/录入/必换件/任务工卡关联、物料申请/发料单/出库创建审批/JSON 条件导出，以及 ERP、Spring fallback、嵌套网关和畸形响应的统一错误解析。

- 单次执行：`npx --yes pnpm@9.15.9 test`
- 监听执行：`npx --yes pnpm@9.15.9 test:watch`
- 测试配置：`vitest.config.mts`

新增或修改 API 时，至少断言 URL、请求方法、参数位置、`region`、下载响应类型等契约；公共纯函数需覆盖主要分支。组件交互和关键业务端到端测试仍需后续逐步补充。

## 错误响应契约

2026-08-13 已将前端 `Result` 类型从旧模板的 `message` 对齐为真实后端字段 `msg`。统一 Axios 错误处理优先显示 ERP `msg`，并兼容 Spring `status/error`、嵌套网关 `error.message` 和非 JSON 错误体；HTTP 400/401/403/409/500 均走同一入口，其中 401 保持自动退出处理。后端响应体继续使用 `code`、`msg`、`traceId`，不要在页面内重复发明另一套解析逻辑。

## 浏览器端到端基线

2026-08-13 已建立 Playwright 独立 E2E 门禁，使用语义化角色、placeholder 和可见文本定位，覆盖以下真实链路：

1. 打开登录页并填写租户、用户名和密码。
2. 等待 `/api/auth/login`，验证业务码为 200 且签发 token。
3. 验证进入仪表板并加载授权菜单。
4. 从用户菜单退出，确认回到 `#/login`。

同日物料域只读探针也已通过现有 Vite `/api` 代理，覆盖申请列表、带合同号/日期/完料状态过滤的发料单列表、出库单、批次/序列号库存和非必换件接口。

首次运行需安装隔离的 Chromium：

```powershell
npx --yes pnpm@9.15.9 exec playwright install chromium
```

本地后端 `127.0.0.1:8088` 可用后执行：

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\verify-e2e.ps1
```

默认冒烟账号为 `上海凯奔航空技术有限公司` / `admin` / `admin`（本地隔离库租户名，随 V1.21 更新；历史文档中的“测试租户”已过期），可使用 `E2E_TENANT`、`E2E_USERNAME`、`E2E_PASSWORD` 和 `E2E_BASE_URL` 覆盖。Playwright 会复用已有 5173 服务，服务未启动时自动启动 Vite；由于后端和数据库属于外部运行态，E2E 不并入纯代码标准门禁。

2026-08-18 已为人事管理新增 `tests/e2e/hr-menus.spec.ts`（4 个用例）：登录后遍历人事管理下员工档案、证书管理、培训记录、培训课程、培训实施、授权项目、人员授权七个菜单页并断言路由与页面无加载失败；员工档案页验证新增入口与工号查询；培训课程页验证新增课程入口；授权项目页验证列表加载。连同 `auth-menu.spec.ts` 共 5 个 Playwright 用例全部通过（`npx playwright test`），前端 `type:check` 保持 0 错误。

2026-08-18 培训记录页面权限码与后端契约对齐：`hr:employee:*`→`hr:training:*`（`views/hr/training-record/` 下 index/add/modify 三文件），与新增部署迁移 `V1.27__shkb_hr_menu_permission_fix.sql` 及后端 `ShkbEmployeeTrainingController`、`TrainingParticipantController`（`hr:training:add`→`hr:training:create`）一致；当日重跑 `verify-e2e.ps1`，auth-menu 与 hr-menus 共 5 个 Playwright 用例全部通过，`type:check` 保持 0 错误。

2026-08-18 新增 `tests/e2e/core-chains.spec.ts`（核心浏览器 E2E）：登录后按路由直达合同（民航维修合同/合同任务派发）、维修任务（民航维修执行）、工卡列表、计量工具管理、采购（订单/收货）、发料申请、仓库调拨、库存盘点（任务/单）、库存调整共 12 个关键页面，断言路由正确且列表查询无“加载数据失败”；与 auth-menu、hr-menus 共 6 个 Playwright 用例在本地冒烟通过。

## 推荐演进顺序

1. 为采购、出库和 HR 等关键提交/查询流程继续补端到端测试。
2. 分开升级 Vite/Vue 工具链、Ant Design Vue、vxe-table。
3. 每个依赖族升级后执行类型检查、自动化测试、生产构建与浏览器端到端冒烟。

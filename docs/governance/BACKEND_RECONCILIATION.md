# 后端双向差异对账记录

## 范围

2026-08-14 比较旧独立 `erp-backend` 工作区与 GitHub 单体仓库 `erp-backend`。逐文件 SHA-256 结果为 2319 个相同文件、33 个同路径不同内容文件；此外双方各有独有文件。

本次对账以“保留上海凯奔业务配置，同时不回退已合并的可靠性改进”为原则，禁止目录级复制。

## 采用旧独立工作区内容

下列 10 个脚本只有默认租户一行不同。单体仓库已改用“上海凯奔航空技术有限公司”，其余内容不变：

- `scripts/verify-auth-permission.ps1`
- `scripts/verify-contract.ps1`
- `scripts/verify-dashboard.ps1`
- `scripts/verify-equipment.ps1`
- `scripts/verify-error-contract.ps1`
- `scripts/verify-material-concurrency.ps1`
- `scripts/verify-material-flow-write.ps1`
- `scripts/verify-material-flow.ps1`
- `scripts/verify-work-card-flow.ps1`
- `scripts/verify-work-card.ps1`

旧独立工作区独有的两个有效业务文件也已按 SHA-256 核对后合入：

- `xingyun-api/src/main/resources/db/migration/tenant/V1.21__shkb_menu_permission_baseline.sql`
- `scripts/verify-menu-baseline.ps1`

`UPGRADE.md` 采用人工合并，同时保留双方已验证记录。

## 保留 GitHub 单体仓库内容

以下 22 个同路径文件属于单体仓库后续完成的凭据外置、备份恢复、健康检查、消息失败恢复、Listener 去重、事务 Outbox 与物料写规则，不得被旧工作区覆盖：

- `.env.example`
- `cloud/conf/db.yaml`
- `cloud/conf/mq.yaml`
- `cloud/xingyun-cloud-api/src/main/resources/project.yaml`
- `xingyun-api/Dockerfile`
- `xingyun-api/pom.xml`
- `xingyun-api/src/main/java/com/lframework/xingyun/api/JuggInnerBeanConflictResolver.java`
- `xingyun-api/src/main/java/com/lframework/xingyun/api/config/BackupProperties.java`
- `xingyun-api/src/main/java/com/lframework/xingyun/api/task/MysqlBackupTask.java`
- `xingyun-api/src/main/resources/application-dev.yml`
- `xingyun-api/src/main/resources/application-prod.yml`
- `xingyun-api/src/main/resources/application-test.yml`
- `xingyun-api/src/main/resources/application.yml`
- `xingyun-chart/src/main/java/com/lframework/xingyun/chart/listeners/mq/OrderDataToChartListener.java`
- `xingyun-core/src/main/java/com/lframework/xingyun/core/dto/order/ApprovePassOrderDto.java`
- `xingyun-core/src/main/java/com/lframework/xingyun/core/dto/stock/ProductStockChangeDto.java`
- `xingyun-sc/pom.xml`
- `xingyun-sc/src/main/java/com/lframework/xingyun/sc/listeners/app/OrderDataListener.java`
- `xingyun-sc/src/main/java/com/lframework/xingyun/sc/listeners/app/StockChangeToMqListener.java`
- `xingyun-sc/src/main/java/com/lframework/xingyun/sc/listeners/mq/TakeStockPlanStockChangeListener.java`
- `xingyun-shkb/pom.xml`
- `xingyun-shkb/src/main/java/com/lframework/xingyun/shkb/impl/MaterialOutSheetServiceImpl.java`

## 明确不合入

旧独立工作区独有的 `erl_crash.dump`、`.DS_Store` 属于本地产物；`xingyun-api/.../Test.java` 是未纳入治理的临时文件。三者均不进入唯一仓库。

前端唯一差异为 `package.json`：单体仓库保留 `cd .. && husky install erp-frontend/.husky`，确保从单体根目录安装 Husky；同时保留类型检查的 Node 内存上限。旧独立仓库的单仓库路径不能覆盖它。

## 组合验证结果

- 随机临时克隆库连续两次应用 `V1.18__mq_outbox.sql` 与 `V1.21__shkb_menu_permission_baseline.sql`，两次结果均为 350 个菜单、11 个角色、592 条有效角色菜单关系、12 个租户模块和 2 张 Outbox/Inbox 表；临时库已删除。
- 菜单探针通过本地 8088 直连和由单体仓库启动的 5174 Vite `/api` 代理，均返回 14 个预期业务根菜单。
- Java 25 后端十模块编译通过；前端类型检查、6 个测试文件/27 个用例和 9117 模块生产构建通过。
- 本批次作为独立干净治理提交；后续仍需建立带标签发布候选，在此之前 `release.deploymentAllowed` 保持 `false`。

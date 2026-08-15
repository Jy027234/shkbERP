# 数据库迁移安全治理

本仓库的 erp-backend/xingyun-api/src/main/resources/db/migration 是版本化的部署前 SQL 源文件，不是应用启动时的自动迁移队列。运行 jar 排除了 db/**，项目也没有 Flyway 运行时依赖；重启服务不会执行其中任何 SQL。

唯一的机器可读清单是 [migration-catalog.json](migration-catalog.json)。它固定每个 SQL 的 SHA-256、风险标记和可执行范围。`sha256-lf-bytes-v1` 只把 `CRLF` 或单独 `CR` 规范为 `LF` 后计算 SHA-256，其他每一个字节都纳入校验；因此 Windows/Linux 的文本检出差异不会误报，而任何实际 SQL 内容变化仍会被拦截。任何迁移文件、哈希、风险标记或既有库计划不一致时，下面的校验会失败：

~~~
powershell -ExecutionPolicy Bypass -File .\scripts\verify-migration-catalog.ps1
~~~

## 分类

| 分类 | 含义 | 运行规则 |
| --- | --- | --- |
| new-install-only | 空库初始化脚本 | 只能对新建空库执行。platform/V1.0 和 tenant/V1.0 均在此类；后者含全量 DROP/CREATE。 |
| historical-baseline-only | 历史版本来源 | 仅用于追溯原始版本演进，不能因为“版本号缺失”就在业务库补跑。它们可能重建表、删列、删菜单或修改数据。 |
| existing-database-delta | 已批准的存量库增量 | 只能在备份完成、隔离恢复副本预检通过后，以清单中顺序单独执行。 |

当前唯一允许进入“存量库候选计划”的 SQL 是：

1. tenant/V1.13__shkb_dashboard_core.sql
2. tenant/V1.14__shkb_contract_core.sql
3. tenant/V1.15__shkb_equipment_records.sql
4. tenant/V1.16__shkb_work_card_core.sql
5. tenant/V1.17__shkb_material_flow.sql
6. tenant/V1.18__mq_outbox.sql
7. tenant/V1.21__shkb_menu_permission_baseline.sql
8. tenant/V1.22__shkb_machine_task_core.sql
9. tenant/V1.23__shkb_contract_task_flow.sql
10. tenant/V1.24__purchase_receive_traceability.sql
11. tenant/V1.25__purchase_return_serial_traceability.sql

这不是“立即对生产执行”的指令。它仅定义了恢复副本通过后才可评审的有序候选集合。

## 自动阻断与预检

目录校验拒绝以下错误：

- 漏登记或多登记的 SQL 文件；
- 已部署迁移的内容哈希变化；
- 把 DROP TABLE、DROP COLUMN、TRUNCATE 等不可逆语句列入存量库计划；
- 有 DELETE 或 UPDATE 却未显式标为受控数据调整的存量库迁移；
- 把 new-install-only 或历史脚本混入存量库计划。

本地预检只连接名称固定的 Docker 冒烟 MySQL 容器，不接受云服务器地址：

~~~
cd erp-backend
powershell -ExecutionPolicy Bypass -File .\scripts\verify-release-preflight.ps1
~~~

它检查 V1.13、V1.17、V1.21、V1.24、V1.25 所需的既有表/列，检查租户 1000，并在已有表存在时检查下列唯一约束将要保护的重复值：

- shkb_machine_task_tightening.task_id
- shkb_machine_task_magnetic_powder.task_id
- shkb_contract_task.contract_id

重复记录会使预检失败，必须只在恢复副本中人工对账；迁移不得自动删改业务记录。

V1.21 是受控数据调整：它会更新租户名称，并删除租户 1000 对模块 7、12、15 的关系。预检会记录受影响关系数量，但不会替代业务负责人确认。

## 恢复副本演练

以下演练会从本地 shkb_platform 做逻辑备份，恢复到随机受限名称的临时库，比对恢复前后的逻辑导出 SHA-256，对临时库预检，并把 11 个候选 SQL 连续执行两轮以验证幂等性。结束时会精确删除临时库和容器内临时目录：

~~~
cd erp-backend
powershell -ExecutionPolicy Bypass -File .\scripts\verify-release-restore.ps1
~~~

这个脚本不会连接、上传、重启或修改云服务器；它也不会对 shkb_platform 执行迁移。它的通过结果只能证明本地隔离环境和候选 SQL 的可重复性。

## 生产恢复副本的外部前置条件

解除生产发布锁之前，仍必须由授权人员提供生产逻辑备份或数据库快照，并在与云端隔离的本地/专用恢复环境完成：

1. 校验备份来源、时间、大小和 SHA-256，并恢复到隔离库；
2. 以该恢复库作为本地预检与恢复演练的 -SourceDatabase；
3. 处理全部重复键和 schema 差异，不得在生产库猜测修复；
4. 由业务负责人确认 V1.21 的菜单/模块影响；
5. 在恢复副本上执行认证、菜单、合同、采购、库存等对应冒烟，并准备数据库回退备份；
6. 获得明确生产发布授权后，才可单独拟定云端变更窗口。

在这些条件完成前，source-baseline.json 中的 deploymentAllowed 必须保持 false。

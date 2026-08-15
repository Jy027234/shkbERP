# 上海凯奔 ERP 模块可靠性矩阵

## 等级定义

| 等级 | 含义 |
| --- | --- |
| L0 | 尚未确认代码或契约 |
| L1 | 已发现部分代码，但前后端或接口链路不完整 |
| L2 | 前后端/后端代码存在并可编译，缺少完整 schema 或专门回归 |
| L3 | 已有版本化 schema 与只读接口冒烟 |
| L4 | 已在本地隔离库验证关键写入流程、事务或并发 |
| L5 | 已用带标签发布候选在生产恢复副本完成业务验收 |

L5 才表示具备生产替换证据。当前没有任何升级模块达到 L5。

## 范围规则

源码中存在页面、接口或实体，不等于该模块属于上海凯奔当前产品范围。尚未纳入真实业务流程的半成品统一标记为“冻结/非发布范围”，不补 schema、不扩展功能、不纳入核心回归或发布；只有用户明确启用并重新完成范围、数据和流程验收后，才允许进入 L0-L5 可靠性升级。

## 业务模块

| 模块 | 前端代码 | 后端代码 | Schema/配置基线 | 自动化或冒烟 | 当前等级 | 主要缺口 |
| --- | --- | --- | --- | --- | --- | --- |
| 登录、单租户、菜单、权限 | 登录/动态菜单/权限路由 | 认证、用户、角色、菜单 | 认证加固与菜单 `V1.21` 已进入唯一仓库 | 组合迁移两次幂等；直连/单体 Vite 菜单探针通过 | L3 | 补带标签候选和生产恢复副本验收 |
| 维修数据看板 | `views/dashboard/maintenance-board` | `DashboardController` | `V1.13__shkb_dashboard_core.sql` | `verify-dashboard.ps1` | L3 | 补真实数据口径验收和前端交互 E2E |
| 合同管理 | `views/contract` | `ContractController`、`ContractTaskController` | `V1.14__shkb_contract_core.sql`、`V1.23__shkb_contract_task_flow.sql` | 只读探针；隔离写流程覆盖新增、修改、附件归属、任务初始状态和重复生成拒绝 | L4 | 明确合同恢复业务规则；补附件物理存储生命周期和生产恢复副本验收 |
| 工具与设备 | `views/equipment` | 工具、设备及记录 Controller | `V1.15__shkb_equipment_records.sql` | 只读探针；隔离写流程覆盖父记录守卫、计量/维保记录、日期/证书同步和附件上传删除 | L4 | 补附件物理存储生命周期、浏览器人工验收和生产恢复副本验收 |
| 维修工卡 | `views/work-card` | `WorkCardController` | `V1.16__shkb_work_card_core.sql` | 工卡只读与隔离写流程脚本 | L4 | 补浏览器端完整业务流和生产恢复副本验收 |
| 发料、出库与库存 | `views/material` | Material Order/Out Controller 与事务服务 | `V1.17__shkb_material_flow.sql`；Outbox `V1.18__mq_outbox.sql` | 写流程、并发、物料规则、Outbox 验证 | L4 | 对账菜单迁移后执行整套组合回归 |
| 采购、收货、退货与通用库存 | `views/sc/purchase` | 采购订单、收货单、采购退货与库存事务服务 | `V1.24__purchase_receive_traceability.sql` 补收货追溯列；`V1.25__purchase_return_serial_traceability.sql` 补退货序列号快照 | `verify-purchase-flow.ps1` 在直连与 Vite 代理覆盖主单/商品归属、超量守卫、批次扣减、序列号部分退货与并发回滚 | L4 | 销售仍需独立验收；补浏览器业务验收和生产恢复副本验收 |
| 库存盘点 | `views/sc/stock/take` | TakeStock Plan/Sheet Controller 与库存事务服务 | 沿用已版本化通用库存/盘点表；V1.33 无新增 DDL | `verify-stocktake-flow.ps1` 在直连与 Vite 代理覆盖仓库不可变、输入/归属守卫、单品残留清理、普通库存差异调整及追溯型库存安全阻断 | L4 | 批次/序列号尚无明细级盘点契约，只能阻断非零差异；补完整功能、浏览器验收和生产恢复副本验收 |
| 库存调整 | `views/sc/stock/adjust` | StockAdjust Sheet/Reason Controller 与库存事务服务 | 沿用已版本化库存调整表；V1.34 无新增 DDL | `verify-stock-adjust-flow.ps1` 在直连与 Vite 代理覆盖直接审核输入/引用守卫、普通库存入出、重复审批及追溯型库存安全阻断 | L4 | 批次/序列号尚无明细级调整契约；补完整功能、浏览器验收和生产恢复副本验收 |
| 拧紧机、磁粉机任务 | `views/machine-task` | MachineTask 与 MachineInfo Controller/Service | `V1.22__shkb_machine_task_core.sql` | 管理端只读探针；状态规则单测；隔离写流程覆盖相同上报重试、冲突上报和重复下发 | L3 | 补真实设备协议联调、下发成功后的断电窗口与生产恢复副本验收 |
| 成品出入库（冻结） | `views/product-storage` | `ProductStorageController` 及附件服务 | 不补迁移 | 不纳入核心回归 | 非发布范围 | 半成品且未纳入上海凯奔实际流程；除非用户明确启用并重新验收，否则不投入、不发布 |
| 人事、培训、证书、人员授权 | `views/hr`、`api/hr` | 已发现部分实体/Service/Mapper，未发现完整 HR Controller | 未发现专门增量迁移 | 仅部分前端 API 单测 | L1 | 先确认云端真实接口和表，再补后端入口、schema 与 E2E |
| 航材基础信息 | 基础资料和航材页面 | `xingyun-basedata` 及 SHKB 扩展 | 部分字段迁移 | 主要依赖编译和通用冒烟 | L2 | 建立机型、件号、批次、序列号的业务验收集 |
| 文件与业务附件 | 多模块上传组件 | `CommonFileController` 及各模块附件服务 | 部分模块已包含附件表 | 局部接口冒烟 | L2 | 验证存储持久化、权限、删除、恢复和大文件边界 |

## 运行支撑

| 能力 | 当前状态 | 等级/结论 |
| --- | --- | --- |
| Java 25 / Spring Boot 3.5 主 reactor | 10 个模块已编译和测试 | 本地门禁可用，不代表 cloud 模块 |
| 健康检查与优雅停机 | GitHub 单体仓库已实现并有验证脚本 | 本地已验证，待发布候选组合回归 |
| RabbitMQ 失败恢复与事务 Outbox | GitHub 单体仓库已有失败队列、确认、Inbox/Outbox | 核心物料事件覆盖较强，其他生产点未全覆盖 |
| 定时任务、WebSocket | 缺少完整端到端覆盖 | 非绿色 |
| cloud 两个模块 | 未纳入主 reactor | 未验证，不得从主构建成功推断其可用 |

每次补齐一个模块，必须同步更新本表的等级、迁移文件、验证命令和证据日期。

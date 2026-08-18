# 附件生命周期现状核对与业务确认清单

> 生成日期：2026-08-18。本文是“统一附件生命周期”专题的现状核对（六维：存储根/权限/MIME/文件名/大小/删除+备份恢复）与生产决策清单。
> 结论先行：**删除维度已统一并验证**（HR V1.38 + 合同/工具/设备/计量/工卡 V1.43）；其余维度现状核对如下，**生产存储方案与保留策略待业务确认**（见 S1–S6）。

## 1. 现状核对（2026-08-18，本地隔离环境）

### 1.1 存储根与 URL 形状
- 存储根：`jugg.upload.location`（生产容器 `-Djugg.upload.location=/opt/data/upload`；Spring Boot 上传临时目录另配 `spring.servlet.multipart.location`）。
- 上传 URL 形状：`{SHKB_UPLOAD_DOMAIN}/oss/1000/yyyy/MM/dd/{32位hex}.{扩展名}`，数据库仅保存 URL。
- 删除工具 `ShkbUploadFileUtil.deletePhysicalFile` 严格限制在上传根目录内（拒绝 `..` 逃逸与外部 `://`，支持 `/oss/...` 与 `/uploads/...` 两种前缀），删除失败不阻断数据库删除并记日志。

### 1.2 各模块附件接口、权限与删除现状

| 模块 | 附件表 | 上传/列表/删除接口 | 权限码 | 删除是否物理清理 |
| --- | --- | --- | --- | --- |
| 合同 | `shkb_contract_file` | `ContractController /contract/attachment/*` | `contract:aviation`/`factory-wb`/`factory-l` | ✅ V1.43（探针验证 EXISTS→MISSING） |
| 工具 | `shkb_tool_file` | `ShkbToolController /shkb/tool/attachment/*` | `equipment:tool` | ✅ V1.43（探针验证） |
| 设备 | `shkb_device_file` | `ShkbDeviceController /shkb/device/attachment/*` | `equipment:device` | ✅ V1.43（探针验证） |
| 工具计量记录 | `shkb_tool_record_file` | `ToolRecordController /shkb/tool/record/attachment/{id}` | `equipment:tool` | ✅ V1.43（探针验证） |
| 工卡 | `shkb_work_card_file` | `WorkCardController /work-card/attachment/*` | `work-card` | ✅ V1.43（`verify-work-card-flow.ps1` 回归通过） |
| 员工附件/照片 | `shkb_employee_file` | `ShkbEmployeeFileController` | `hr:employee:query/update` | ✅ V1.38 |
| 培训课程文档 | `shkb_training_course_file` | `TrainingCourseController` | `hr:training:*` | ✅ V1.38 |
| 人员授权附件 | `shkb_person_authorization_file` | `PersonAuthorizationController` | `hr:authorization:update` | ✅ V1.38 |
| 成品出入库附件 | `shkb_product_storage_file` | `ProductStorageController /product-storage/attachment/*` | `product:storage` | ⚠️ 冻结范围（不投入、不发布），删除仍只删 DB 行 |

### 1.3 横切限制现状
- 上传大小：Spring 配置 `spring.servlet.multipart.max-file-size=50MB`、`max-request-size=100MB`（application.yml）。
- MIME/文件名白名单：**无集中校验**——上传仅保存 `contentType` 与原文件名，未按扩展名/MIME 白名单过滤（下载侧 HR 已做安全 `Content-Disposition` 编码与 `/oss` 路径映射）。
- 下载鉴权：HR 下载走认证接口并映射 `/oss` 路径、拒绝路径逃逸；其余模块下载行为未做统一核对（见 S6）。
- 备份/恢复：仓库已有 `verify-backup-restore.ps1`、`verify-production-backup-api.ps1`、`verify-production-backup-copy.ps1`；本地冒烟 `backups/` 目录为回退演练产物；**生产恢复副本业务验收未完成**。

## 2. 验证证据（2026-08-18）
- `verify-equipment-flow.ps1`（8088 + 5173）：工具/计量/设备附件上传后容器内文件 EXISTS，API 删除后 MISSING，夹具清理残留 0。
- `verify-contract-flow.ps1`（8088 + 5173）：合同附件同上探针通过。
- `verify-work-card-flow.ps1`（8088）：工卡附件链路回归通过。
- `verify-hr-flow.ps1`（8088 + 5173）：HR 附件物理删除既有证据保持通过。
- 后端 Java 25 编译、前端 type:check/27 Vitest/生产构建通过。

## 3. 业务确认清单（请逐项拍板）

- [ ] **S1 生产存储方案**：沿用本地磁盘 + `jugg.upload.location`（现状），还是切换对象存储/共享存储？上传 URL 域名 `SHKB_UPLOAD_DOMAIN` 是否固定？
- [ ] **S2 备份策略**：上传目录的备份频率、保留份数、异地/离机副本位置（建议与数据库备份同窗口）。
- [ ] **S3 附件保留周期**：各类附件（合同、证书、工卡、培训、授权、计量/维保记录）在单据作废/人员离职/证书过期后的保留与归档规则。
- [ ] **S4 清理策略**：孤儿附件（无父记录）与过期附件的自动清理任务是否需要（当前仅删除时物理清理，无后台清扫）。
- [ ] **S5 上传限制**：50MB 上限是否合适；是否启用扩展名/MIME 白名单（如 pdf/jpg/png/xlsx/docx）以替换“仅保存不校验”。
- [ ] **S6 下载鉴权**：附件下载是否需要登录鉴权与父记录归属校验（HR 已实现），其余模块是否统一。

## 4. 结论
- 删除维度已统一（除冻结的成品出入库），并有双链路物理文件探针证据。
- 存储/备份/保留/清理/上传限制/下载鉴权六个方向待 S1–S6 业务确认后分别落地；其中 S5/S6 可并入后续“公共错误与安全”与“附件安全边界”回归。
- 本文形成期间对云端零写入；`deploymentAllowed` 仍为 `false`。
